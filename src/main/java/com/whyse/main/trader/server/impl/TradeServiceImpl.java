package com.whyse.main.trader.server.impl;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.whyse.lib.trader.*;
import com.whyse.main.selfmodel.LoginBean;
import com.whyse.main.trader.BridjUtils;
import com.whyse.main.trader.TraderHelper;
import com.whyse.main.trader.TraderSpiMyAdaptor;
import com.whyse.main.trader.server.CTPHelper;
import com.whyse.main.trader.server.TradeService;
import com.whyse.main.util.BeanUtil;
import com.whyse.main.util.MsgCenterHelper;

import org.apache.commons.lang.StringUtils;
import org.bridj.Pointer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 封装TraderAPI逻辑
 * 负责注册多个远程服务端，登录
 * 等其他APT应该发起的请求
 *
 * author:xumin
 * 2016-4-21 下午7:07:01
 */
public class TradeServiceImpl implements TradeService{

//	private final static Logger log = LoggerFactory.getLogger(CtpTraderProxy.class);

	public  CThostFtdcTraderApi traderApi;
	private CThostFtdcTraderSpi traderSpi;
	public LoginBean  loginBean;
	/**
	 * -1: 还没有init成功  1:尝试登录  2:表示已经登录  -2:登录失败
	 */
	public int state = -1;
	private long timeWillReleas = 0;

	protected static AtomicInteger seqId = new AtomicInteger();

//	public ProfitUpdatePacket lastAccountPkt;//重新登录的时候可以快速返回，因为账号不变的因素返回
	public volatile boolean isAccountP=false;//是否要返回平仓标记，true就是要返回
//	public ProfitUpdatePacket lastPositionPkt;
	public boolean isReEnter = false;
	ReentrantLock releaseLock = new ReentrantLock();//将用于释放资源的锁，崩溃经常发生在此

	/**客户端发包序列号 <br>packet seq 为Key  ctp seq 为value*/
	private LoadingCache<Integer, Integer> seqMap ;

	public String userId;//投资者账户id
	public String brokerId;
	public String brokerName;
	/**
	 * fdtId+clientId
	 */
	public String appKey;

	public String bankId;			//银行代码
	public String bankBranchId;		//银行分支机构代码
	public String accountId;		//投资者银行账号
	public String bankAccount;		//银行账号
	public String bankName;			//银行名称

	public AtomicBoolean BANK_INIT_TAG = new AtomicBoolean(false);//银行参数初始标识
	//---------反射，阻塞队列，多线程----------------------------------------------
	private LinkedBlockingQueue<Map<String, Object>> blockingQueueMeth;
	ConcurrentHashMap<String, Boolean>  mapWaitingInvoke = new ConcurrentHashMap<>(10);
	private boolean isRealise = false;
	private static HashMap<String, Method> mapMethodS;
	static{
		Method[]  methods = CThostFtdcTraderApi.class.getMethods();
		mapMethodS = new HashMap<String, Method>(methods.length);
		for(Method item : methods){
			String name = item.getName();
			mapMethodS.put(name, item);
		}
	}
	//---交易的时候有用---------------------------------------------
	public int frontID;//2
	public int sessionID;
	public long maxOrderRef;
	public String systemName;//TradingHosting
	/**
	 * 今天是否已经确认结算单
	 */
	public volatile boolean isSettlementInfoConfirm = false;
	/**
	 * 下午四点5分，收集账户信息的标记位置到数据库
	 */
	public boolean tradingAccountWillGet = false;

	/**
	 * 登录后获取委托单，按照时间顺序逆序排序
	 * 成交记录跟持仓均价都要通过这个计算
	 * 最新的委托都插入到最前，动态变化的
	 */
	public LinkedList<Map<String, Object>> listOrder = new LinkedList<>();
	//这个是以OrderRef为key的冗余索引
	public Map<String, Map<String, Object>>  mapOrder = new HashMap<String, Map<String,Object>>(20);

    /*银期转账流水list*/
    public LinkedList<Map<String, Object>> transferSerialList = new LinkedList<>();
	public Map<String, Object> mapAuthenticateInfo = null;//一些客户端验证信息
	

	//==============================================================================
    public boolean isTimeOutReleas() {
    	long timeNow = System.currentTimeMillis();
    	if(timeWillReleas==0){
    		timeWillReleas = timeNow;
    		return false;
    	}
    	if(timeNow-timeWillReleas>=2500){
    		timeWillReleas = 0;
    		return true;
    	}
		return false;
	}
    @Override
	public void cleanStateForNextDay() {
    	isSettlementInfoConfirm = false;
//    	lastAccountPkt = null;
	}
	public static int getNextSeq() {
//		long aa = System.currentTimeMillis()/100;
		int tar = seqId.incrementAndGet();//(int) (aa%limitInt)
		return tar;
	}
	public TradeServiceImpl(LoginBean loginBean){
		this.loginBean = loginBean;
		brokerId = loginBean.getReq().BrokerID().getCString();
		userId = loginBean.getReq().UserID().getCString();
//		brokerName = CTPHelper.getBrokerName(brokerId);
//		mapAuthenticateInfo = CTPHelper.getMapTag(brokerId);
			
		initBlockingQueue();
        initSeqMap();
	}

	private void initSeqMap() {
		if (null == seqMap) {
			seqMap = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build(new CacheLoader<Integer, Integer>() {
				@Override
				public Integer load(Integer integer) throws Exception {
					return null;
				}
			});
		}

	}

    public Integer getSeq(Integer key) {
        try {
            return this.seqMap.get(key);
        } catch (Exception e) {
            System.err.println("无此key-value");
        }
        return null;
    }

    public void setSeq(Integer key, Integer value) {
        this.seqMap.put(key, value);
    }

	private void initBlockingQueue() {
		blockingQueueMeth = new LinkedBlockingQueue<Map<String, Object>>(100);
		Runnable run  = new Runnable() {

			@SuppressWarnings("static-access")
			@Override
			public void run() {
				while(!isRealise){
					String mName = null;
					Object[] args = null;
					try {
						Map<String, Object> item = blockingQueueMeth.take();
						if(item.isEmpty())
							return;
						mName = item.get("name").toString();
						mapWaitingInvoke.remove(mName);//消费了一个方法，同名方法触发可以入队
//						System.err.println("===消费:"+mName);
						args = (Object[]) item.get("args");
						Method meth =  mapMethodS.get(mName);
						int flag = (int) meth.invoke(traderApi, args);
						CTPHelper.printRoAndTurn(flag,mName);
						if(flag !=0){
							Thread.currentThread().sleep(300);
							flag = (int) meth.invoke(traderApi, args);
						}
						Thread.currentThread().sleep(1000);
					} catch (Exception e) {
						System.err.println("mName:"+mName+" args:"+args);
						e.printStackTrace();
					}
				}
			}
		};
		Thread threadCons = new Thread(run);
		String key = "ctp:"+brokerId+userId+"_cons";
		threadCons.setName(key);
		threadCons.start();
	}
	private Map<String, Object> getReqMap(String name,
			Object... args) {
		Map<String, Object> tar = new HashMap<String, Object>(2);
		tar.put("name", name);
		tar.put("args", args);
		return tar;
	}
	@Override
	public void release(final boolean isForce) {
		final String key = brokerId+userId;
		Runnable run = new Runnable() {

			@Override
			public void run() {
				releaseLock.lock();
				try{
					System.err.println("预release:"+key);
					if(isRealise){
						System.err.println("release同步异常，注意！！");
						return;
					}
					if(isForce || (traderApi!=null && state==2)){
						traderApi.RegisterSpi(null);
						traderApi.Release();
						traderApi =null;
						System.err.println("release_ing:"+key);
						
						isRealise = true;
						//释放阻塞消费线程
						blockingQueueMeth.offer(new HashMap<String, Object>(1));
						
						System.err.println("release_OK:"+key);
					}
				}finally{
					releaseLock.unlock();
				}
			}
		};
		new Thread(run).start();
//		TraderSpiMyAdaptor.executorService.submit(run);	
	}
	/**
	 * 释放驻留线程，去掉引用等待回收
	 * 注意：这边不调用c++的release
	 * author:xumin 
	 * 2016-11-2 下午3:28:28
	 */
	public void tradingAccountRelease() {
		if((traderApi!=null && state==2)){
			String key = brokerId+userId;
//			traderApi.RegisterSpi(null);
//			traderApi.Release();
			traderApi =null;
			System.err.println("release_ing:"+key);
			
			isRealise = true;
			//释放阻塞消费线程
			blockingQueueMeth.offer(new HashMap<String, Object>(1));
			
			System.err.println("tradingAccountRelease_OK:"+key);
		}
	}
	//
	/**
	 * 需要开线程去运行
	 * author:xumin
	 * 2016-4-22 下午4:36:43
	 */
	public void init() {
		//一个账户一个文件夹
		String localFilePath = LoginBean.getLocalFilePath()+brokerId+"/"+userId+"/";//后面这个符号很重要
		File localFiles = new File(localFilePath);
		if ( !localFiles.exists() ) {
			localFiles.mkdirs();
		}
//		monitorThreadLogin();
		// ------------------------------------------------------
		System.out.println("TraderApi.con路径为:"+localFilePath);
		//注意操作系统的文件句柄限制！ 多个用户的.con文件都是保持打开的
		Pointer<CThostFtdcTraderApi > pTraderApi = CThostFtdcTraderApi.CreateFtdcTraderApi(BridjUtils.stringToBytePointer(localFilePath));
		traderApi = pTraderApi.get();
		if(traderApi==null)
			return;
		traderSpi = new TraderSpiMyAdaptor(this);//TraderSpiMyAdaptor
		traderApi.RegisterSpi(Pointer.getPointer(traderSpi));
		//=============================
		RegisterFront();
	}
	/**
	 * 8秒后，如果发现登陆没有成功，就主动释放线程
	 * author:xumin 
	 * 2016-7-12 下午4:14:10
	 */
	private void monitorThreadLogin() {
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000*8);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(state!=2){
					System.err.println("客户端登陆超时，即将释放");
					release(true);
				}else{
					System.err.println("客户端启动成功");
				}
			}
		};
		Thread monitorThread = new Thread(run);
		String key = brokerId+userId;
		monitorThread.setName("ctp:"+key+"_monitor");
		monitorThread.start();
	}
	/**
	 * 注册多个前端机，让api去选择合适的
	 *
	 * author:xumin
	 * 2016-4-22 上午9:59:16
	 */
	private void RegisterFront() {
		traderApi.SubscribePrivateTopic(loginBean.getPrivateTopic());
		traderApi.SubscribePublicTopic(loginBean.getPublicTopic());
		//这边是配置在数据库的前端地址，对象化
		List<String>  listUrl = CTPHelper.getFrontUrl(brokerId);
		if(listUrl==null || listUrl.isEmpty()){
			return;
		}
		for(String url : listUrl){
			traderApi.RegisterFront(BridjUtils.stringToBytePointer(url));
		}
		traderApi.Init();
		//客户端认证做->用户登录->最少执行一次ReqQrySettlementInfoConfirm 才能交易
		traderApi.Join();
		System.err.println("Join  after realse:"+brokerId+userId);
	}
	/**
	 * 产品标识：FDTTrader
认证码  ：20160818FDTT0002
	 * 建立连接之后，登录之前
	 *
	 * author:xumin
	 * 2016-4-22 下午3:23:14
	 */
	public int doAuthenticate() {
		CThostFtdcReqAuthenticateField reqAuthenticateField = new CThostFtdcReqAuthenticateField();
		reqAuthenticateField.BrokerID().setCString(brokerId);
		reqAuthenticateField.UserID().setCString(userId);
//		reqAuthenticateField.UserProductInfo().setCString(loginBean.getReq().ProtocolInfo().getCString());
		reqAuthenticateField.UserProductInfo().setCString(mapAuthenticateInfo.get("name").toString());
		reqAuthenticateField.AuthCode().setCString(mapAuthenticateInfo.get("code").toString());

		Pointer<CThostFtdcReqAuthenticateField> pReqAuthenticateField = Pointer.getPointer(reqAuthenticateField);

		int flag = traderApi.ReqAuthenticate(pReqAuthenticateField, getNextSeq());
		System.err.println("doAuthenticate flag:"+flag);
		return flag;
	}
	/**
	 * 0，代表成功。
-1，表示网络连接失败；
-2，表示未处理请求超过许可数；
-3，表示每秒发送请求数超过许可数。
	 * author:xumin
	 * 2016-4-21 上午11:17:49
	 */
	public int doLogin() {
		CThostFtdcReqUserLoginField req = loginBean.getReq();
		Pointer<CThostFtdcReqUserLoginField> pReq = Pointer.getPointer(req);
		int seq = getNextSeq();
		int flag = traderApi.ReqUserLogin(pReq,seq);//getNextSeq()
		CTPHelper.printRoAndTurn(flag,"ReqUserLogin");
//		this.writeLogToDb(pReq,seq,"doLogin");
		return flag;
	}
	/**
	 * 先OnFrontDisconnected  再OnFrontConnected重新登录一遍
	 * 注意：目前，通过ReqUserLogout 登出系统的话，会先将现有的连接断开，再重新建立一个新的连接，重新
	登录后SessionID 会重置，因此MaxOrderRef 一般也会重新从0 计数。
	 * author:xumin
	 * 2016-4-22 下午5:19:50
	 */
	public int doLogout() {
		CThostFtdcUserLogoutField req = new CThostFtdcUserLogoutField();
		req.BrokerID().setCString(brokerId);
		req.UserID().setCString(userId);
		Pointer<CThostFtdcUserLogoutField> pReq = Pointer.getPointer(req);

		int seq = getNextSeq();
		int flag = traderApi.ReqUserLogout(pReq,seq);
		CTPHelper.printRoAndTurn(flag,"doLogout");

//		this.writeLogToDb(pReq,seq,"doLogout");
		return flag;
	}
	//------------------------
	/*
	 * 每秒最多只能查询一次。  在途查询只能有一个（如果没有响应，就不要再发查询）
	 * 针对所有ReqQry*** ；   除了报单，撤单，报价，询价操作
	 */
	//----------------------------
	/**
	 * *查询今天是否确认.
	 * 注意为了避免用户反复登录确认结算单。  建议在确认前先查询调用这个函数
	 * @return
	 * author:xumin
	 * 2016-4-28 上午11:00:27
	 */
	@Override
	public int doReqQrySettlementInfoConfirm() {
		CThostFtdcQrySettlementInfoConfirmField req = new CThostFtdcQrySettlementInfoConfirmField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);

		Pointer<CThostFtdcQrySettlementInfoConfirmField> pReq = Pointer.getPointer(req);

		blockQueueOffer("ReqQrySettlementInfoConfirm",pReq,getNextSeq());

		return 0;
	}
	private void blockQueueOffer(String name,
			Object... args) {

		if(mapWaitingInvoke.containsKey(name)){
			System.err.println("队列还有未执行，阻挡:"+name);
			return;
		}
		mapWaitingInvoke.put(name, true);//预先插入，用于阻挡后面的持续插入
		try {
			if(!blockingQueueMeth.offer((getReqMap(name,args)), 5, TimeUnit.SECONDS)){
				mapWaitingInvoke.remove(name);
				System.err.println("blockingQueueMeth插入超时:"+name);
			}else{
//				System.err.println("队列in:"+name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 请求查询结算单(当天调用一次)
	 * 这个接口也可以用来查询历史交易记录
	 * author:xumin
	 * 2016-4-22 下午8:05:30
	 */
	public int doReqQrySettlementInfo(String date) {
		CThostFtdcQrySettlementInfoField ob = new CThostFtdcQrySettlementInfoField();
		ob.BrokerID().setCString(brokerId);
		ob.InvestorID().setCString(userId);
		int nRequestID = getNextSeq();
		if(date!=null){
			//折证明是查询历史账单
			ob.TradingDay().setCString(date);//查询的应该是昨天或者之前的单子
			MsgCenterHelper.putOverThreadId(nRequestID);
		}
//		int flag = traderApi.ReqQrySettlementInfo(Pointer.getPointer(ob),nRequestID);
		blockQueueOffer("ReqQrySettlementInfo",Pointer.getPointer(ob),nRequestID);
		return 0;
	}
	/**
	 * 确认某天的结算单
	 * 只需填写公司代码和投资者代码
	 * author:xumin
	 * 2016-4-22 下午9:21:22
	 */
	public int doReqSettlementInfoConfirm() {
		CThostFtdcSettlementInfoConfirmField ob = new CThostFtdcSettlementInfoConfirmField();
		ob.BrokerID().setCString(brokerId);
		ob.InvestorID().setCString(userId);
//		ob.ConfirmDate().setCString(TimeUtil.getToday());//不填就是上一交易日
//		ob.ConfirmTime().setCString(TimeUtil.getTodayTime());
		int seq = getNextSeq();
		if(traderApi.ReqSettlementInfoConfirm(Pointer.getPointer(ob), seq)==0){
			System.err.println("----->doReqSettlementInfoConfirm");
		}
//		this.writeLogToDb(Pointer.getPointer(ob),seq,"doReqSettlementInfoConfirm");
		return 0;
	}

	/*
	 * FrontID + SessionID + OrderRef 第一步报单
	 * ExchangeID + TraderID + OrderLocalID   交易核心将报单提交到报盘
	 * ExchangeID + OrderSysID  交易所在接收了报单之后，会为该报单生成报单在交易所的编号OrderSysID
	 */
	@Override
	public int doReqOrderInsert(CThostFtdcInputOrderField req) {
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		/*
		//-----限价单---条件单+FAK（Fill and Kill）+FOK（Fill or Kill）多种组合----------------
		req.OrderPriceType((byte) TraderLibrary.THOST_FTDC_OPT_LimitPrice);//限价
		req.LimitPrice(12.88);//你出的价格
		req.TimeCondition((byte) TraderLibrary.THOST_FTDC_TC_GTC);//撤销前有效
		//------市价单---没有价格，按现在价格和方向成交----------------------------------------------------
		req.OrderPriceType((byte) TraderLibrary.THOST_FTDC_OPT_AnyPrice);//任何价
		req.LimitPrice(0);//没有限制
		req.TimeCondition((byte) TraderLibrary.THOST_FTDC_TC_IOC);//立即完成，否则撤销?
		//----------条件单-------------------------------------
		//当物品价格达到StopPrice价格时，立马，按照13.00的价格，限价买入。当日有效
		req.ContingentCondition((byte) TraderLibrary.THOST_FTDC_CC_Immediately);
		req.StopPrice(12.99);//条件标记价，止损止盈价
		req.OrderPriceType((byte) TraderLibrary.THOST_FTDC_OPT_LimitPrice);//限价
		req.LimitPrice(13.00);//你出的价格
		req.TimeCondition((byte) TraderLibrary.THOST_FTDC_TC_GFD);//当日有效
		//-------------------------------------------------------
		*/
		Pointer<CThostFtdcInputOrderField> pReq = Pointer.getPointer(req);
		int seq = getNextSeq();
		int flag = traderApi.ReqOrderInsert(pReq,seq);
		setLastAccountCacheNull();
		CTPHelper.printRoAndTurn(flag,"doReqOrderInsert");
//		this.writeLogToDb(pReq,seq,"doReqOrderInsert");
		return flag;
	}
	@Override
	public int doReqOrderAction(CThostFtdcInputOrderActionField req) {

		Pointer<CThostFtdcInputOrderActionField> pReq = Pointer.getPointer(req);
		//OnErrRtnOrderAction , OnRspOrderAction
		int seq = getNextSeq();
		int flag = traderApi.ReqOrderAction(pReq, seq);
		CTPHelper.printRoAndTurn(flag,"doReqOrderAction");
//		this.writeLogToDb(pReq,seq,"doReqOrderAction");
		return flag;
	}
	@Override
	public int doReqForQuoteInsert() {

		CThostFtdcInputForQuoteField req = new CThostFtdcInputForQuoteField();
		req.BrokerID().setCString(brokerId);
		req.UserID().setCString(userId);
//		req.InstrumentID().setCString(instrumentID);//合约代码?需要么
		req.ForQuoteRef().setCString(TraderHelper.getNextOrderRef());//询价的引用

		Pointer<CThostFtdcInputForQuoteField> pReq = Pointer.getPointer(req);

		int flag = traderApi.ReqForQuoteInsert(pReq,getNextSeq());
		if(flag==0){
			System.err.println("doReqForQuoteInsert成功！");
		}else{
			System.err.println("doReqForQuoteInsert失败！");
		}
		return flag;
	}
	@Override
	public int doReqQryInvestorPosition(String exchangeInstID) {
		CThostFtdcQryInvestorPositionField req = new CThostFtdcQryInvestorPositionField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		if(exchangeInstID!=null)
			req.InstrumentID().setCString(exchangeInstID);//合约代码。会返回多次

		Pointer<CThostFtdcQryInvestorPositionField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryInvestorPosition(pReq,getNextSeq());
		blockQueueOffer("ReqQryInvestorPosition",pReq,getNextSeq());
		return 0;
	}
	@Override
	public int doReqQryTradingAccount(Map<String, Object> mapReq) {
		CThostFtdcQryTradingAccountField req = new CThostFtdcQryTradingAccountField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		req.CurrencyID().setCString("CNY");
		int nRequestID = getNextSeq();
		if(mapReq!=null){
			MsgCenterHelper.putOverThreadId(nRequestID);
		}
		Pointer<CThostFtdcQryTradingAccountField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryTradingAccount(pReq,nRequestID);
		blockQueueOffer("ReqQryTradingAccount",pReq,nRequestID);
		return 0;
	}
	@Override
	public int doReqQryDepthMarketData() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * 有开始时间和结束时间，默认是当前交易日
	 * @param mapReq
	 * @return
	 * author:xumin 
	 * 2016-7-19 下午5:26:45
	 */
	@Override
	public int doReqQryTrade(Map<String, Object> mapReq) {
		CThostFtdcQryTradeField req = new CThostFtdcQryTradeField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);

		Pointer<CThostFtdcQryTradeField> pReq = Pointer.getPointer(req);
//		int flag = traderApi.ReqQryTrade(pReq, getNextSeq());
		blockQueueOffer("ReqQryTrade",pReq,getNextSeq());
		return 0;
	}
	/**
	 * 这个就能查询所有委托,登录后应该打回
	 * @param instrumentId
	 * @return
	 * author:xumin
	 * 2016-6-2 下午2:34:03
	 */
	@Override
	public int doReqQryOrder(String instrumentId) {
		CThostFtdcQryOrderField req = new CThostFtdcQryOrderField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		if(instrumentId!=null)
			req.InstrumentID().setCString(instrumentId);

		Pointer<CThostFtdcQryOrderField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryOrder(pReq, getNextSeq());
		blockQueueOffer("ReqQryOrder",pReq,getNextSeq());
		return 0;
	}
	/**
	 * 查今持仓明细
	 * @return
	 * author:xumin
	 * 2016-5-31 下午4:53:00
	 */
	@Override
	public int doReqQryInvestorPositionDetail(String instrumentID) {
		CThostFtdcQryInvestorPositionDetailField req = new CThostFtdcQryInvestorPositionDetailField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		if(instrumentID!=null)
			req.InstrumentID().setCString(instrumentID);

		Pointer<CThostFtdcQryInvestorPositionDetailField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryInvestorPositionDetail(pReq, getNextSeq());
		blockQueueOffer("ReqQryInvestorPositionDetail",pReq,getNextSeq());
		return 0;
	}
	@Override
	public Map<String, Object> doReqQryTransferBank() {
		CThostFtdcQryTransferBankField req = new CThostFtdcQryTransferBankField();
		req.BankBrchID().setCString(bankBranchId);
		req.BankID().setCString(bankId);

		Pointer<CThostFtdcQryTransferBankField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryTransferBank(pReq, getNextSeq());
		blockQueueOffer("ReqQryTransferBank",pReq,getNextSeq());
		return null;
	}
	@Override
	public Map<String, Object> doReqQryContractBank() {
		CThostFtdcQryContractBankField req = new CThostFtdcQryContractBankField();
		req.BankBrchID().setCString(bankBranchId);
		req.BankID().setCString(bankId);
		req.BrokerID().setCString(brokerId);

		Pointer<CThostFtdcQryContractBankField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryContractBank(pReq, getNextSeq());
		blockQueueOffer("ReqQryContractBank",pReq,getNextSeq());
		return null;
	}
	@Override
	public Map<String, Object> doReqQryAccountregister() {
		CThostFtdcQryAccountregisterField req = new CThostFtdcQryAccountregisterField();
		req.AccountID().setCString(userId);
		req.BrokerID().setCString(brokerId);
		Pointer<CThostFtdcQryAccountregisterField> pReq = Pointer.getPointer(req);

//		int flag = traderApi.ReqQryAccountregister(pReq, getNextSeq());
		blockQueueOffer("ReqQryAccountregister",pReq,getNextSeq());
		return null;
	}
	@Override
	public Map<String, Object> doReqQryTransferSerial() {
		CThostFtdcQryTransferSerialField req = new CThostFtdcQryTransferSerialField();
		req.AccountID().setCString(userId);
		req.BrokerID().setCString(brokerId);
		req.BankID().setCString(bankId);
		req.CurrencyID().setCString("CNY");
//		int flag = traderApi.ReqQryTransferSerial(Pointer.getPointer(req),getNextSeq());
		blockQueueOffer("ReqQryTransferSerial",Pointer.getPointer(req),getNextSeq());
		return null;
	}

	@Override
	public void doReqQryBindBank() {
//		CTPToBankFuturesTransferAdapter.onRspQryBindBank(bankName,bankAccount,this);
	}

	@Override
	public Map<String, Object> doReqQueryBankAccountMoneyByFuture(String password, String bankPassword) {
		CThostFtdcReqQueryAccountField req = new CThostFtdcReqQueryAccountField();
		req.TradeCode().setCString("204002");//业务功能码  期货发起查询银行余额
		req.BankBranchID().setCString(bankBranchId);   			//银行分支机构代码
		req.BankID().setCString(bankId);				//qv银行代码
		req.BrokerID().setCString(brokerId);		//期商代码

		req.Password().setCString(password);//期货密码
		req.BankPassWord().setCString(bankPassword);
		req.AccountID().setCString(userId);
		req.CurrencyID().setCString("CNY");   ////币种代码
//		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_NoCheck);
		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_BlankCheck);

		int flag = traderApi.ReqQueryBankAccountMoneyByFuture(Pointer.getPointer(req),getNextSeq());
		if(!CTPHelper.printRoAndTurn(flag,"ReqQueryBankAccountMoneyByFuture -----查询银行余额接口----- ")){
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			traderApi.ReqQueryBankAccountMoneyByFuture(Pointer.getPointer(req),getNextSeq());
		}
		return null;
	}
	@Override
	public Map<String, Object> doReqFromBankToFutureByFuture(String password,Double tradeAmount, String bankPassword) {
		CThostFtdcReqTransferField req = new CThostFtdcReqTransferField();

		req.TradeCode().setCString("202001");//202001 期货发起银行资金转期货
		req.BankBranchID().setCString(bankBranchId);
		req.BankID().setCString(bankId);
		req.BrokerID().setCString(brokerId);

		req.Password().setCString(password);  //期货密码
		req.AccountID().setCString(userId);
		req.CurrencyID().setCString("CNY");//币种代码
//		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_NoCheck);
		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_BlankCheck);
		req.BankPassWord().setCString(bankPassword);
		req.TradeAmount(tradeAmount);

		Pointer<CThostFtdcReqTransferField> pReq = Pointer.getPointer(req);
		int seq = getNextSeq();
		int flag = traderApi.ReqFromBankToFutureByFuture(pReq,seq);

		if(!CTPHelper.printRoAndTurn(flag,"ReqFromBankToFutureByFuture -----请求银行转期货----- ")){
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			seq = getNextSeq();
			traderApi.ReqFromBankToFutureByFuture(Pointer.getPointer(req),seq);
		}
//		this.writeLogToDb(Pointer.getPointer(req),seq,"doReqFromBankToFutureByFuture");
		return null;
	}
	@Override
	public Map<String, Object> doReqFromFutureToBankByFuture(String password,Double tradeAmount, String bankPassword) {
		CThostFtdcReqTransferField req = new CThostFtdcReqTransferField();

		req.TradeCode().setCString("202002");//202002 期货发起期货资金转银行
		req.BankBranchID().setCString(bankBranchId);
		req.BankID().setCString(bankId);
		req.BrokerID().setCString(brokerId);

		req.Password().setCString(password);  //期货密码
		req.BankPassWord().setCString(bankPassword);
		req.AccountID().setCString(userId);
		req.CurrencyID().setCString("CNY");//币种代码
//		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_NoCheck);
		req.SecuPwdFlag((byte) TraderLibrary.THOST_FTDC_BPWDF_BlankCheck);

		req.TradeAmount(tradeAmount);
		int seq = getNextSeq();
		int flag = traderApi.ReqFromFutureToBankByFuture(Pointer.getPointer(req),seq);

		if(!CTPHelper.printRoAndTurn(flag,"ReqFromFutureToBankByFuture -----请求期货转银行----- ")){
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			seq = getNextSeq();
			traderApi.ReqFromFutureToBankByFuture(Pointer.getPointer(req),seq);
		}
//		this.writeLogToDb(Pointer.getPointer(req),seq,"doReqFromFutureToBankByFuture");
		return null;
	}
	//===================下面是返回的一些自动处理===========================================
	/**
	 * 登录之后，保留相关全局返回数据
	 * @param pRspUserLogin
	 * @param pRspInfo
	 * @param nRequestID
	 * @param bIsLast
	 * author:xumin
	 * 2016-4-27 下午7:14:53
	 */
	public void OnRspUserLogin(
			Pointer<CThostFtdcRspUserLoginField> pRspUserLogin,
			Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID,
			boolean bIsLast) {

        CThostFtdcRspUserLoginField rspUserLogin = pRspUserLogin.get();
        CThostFtdcRspInfoField rspInfoField = pRspInfo.get();
		if(rspInfoField.ErrorID()==0){
			String tradingDay = rspUserLogin.TradingDay().getCString();
			String loginTime = rspUserLogin.LoginTime().getCString();
			systemName = rspUserLogin.SystemName().getCString();
			frontID = rspUserLogin.FrontID();
			sessionID = rspUserLogin.SessionID();
			maxOrderRef = rspUserLogin.MaxOrderRef().getLong();
			state = 2;
			System.out.println("<------frontID:"+frontID+" sessionID:"+sessionID+" systemName:"+systemName);

			//-----------下面是是否需要提醒结算单确认的逻辑--------------------
			OnAfterLoginReqQrySettlementInfoConfirm();
			threadRtnLoginInit();
		}else{
			//密码错误等，直接退出
			Map<String, Object>  map = BridjUtils.getMapByPoint(pRspInfo);
	        System.err.println(map);
			state = -2;
			release(true);
		}

//		this.writeLogToDb(pRspUserLogin,nRequestID,"OnRspUserLogin");
	}

	/**
	 * 调用查询转账银行接口,初始化相关银期转账参数
	 */
	public void threadRtnLoginInit() {
		/*初始化标记*/
		BANK_INIT_TAG.set(false);

		Runnable run = new Runnable() {

			public void run() {
				try {
					Thread.currentThread().sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				doReqQryAccountregister();
			}
		};
		TraderSpiMyAdaptor.executorService.submit(run);
	}
	/**
	 * 成功登录后执行的账单确认等，
	 * 包括客户端重连
	 *
	 * author:xumin
	 * 2016-5-25 上午10:32:30
	 */
	public void OnAfterLoginReqQrySettlementInfoConfirm() {
		//--1.获取当前交易日-------------------------------
//		tradingDay = traderApi.GetTradingDay().getCString();
		//!isSettlementInfoConfirm,true
		if(!isSettlementInfoConfirm){
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
//					doReqSettlementInfoConfirm();//测试，主动确认
					//---------------------
					doReqQrySettlementInfoConfirm();
				}
			};
			TraderSpiMyAdaptor.executorService.submit(runnable);
		}
		else{
			doGetPosAndAccount(true);
		}
	}
	/**
	 * 客户端确认后，获取持仓和账户
	 *
	 * author:xumin
	 * 2016-5-27 下午4:08:41
	 */
	public void doGetPosAndAccount(final boolean isGetOrder) {
		//isSettlementInfoConfirm
		if(isSettlementInfoConfirm){
			//-------------------------------------
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					if(!canReturnCache()){
						//------------------------------------------
						doReqQryTradingAccount(null);//发起账户信息请求
						doReqQryInvestorPosition(null);//持仓请求
					}else{
//						iClientNode.sendPacket(lastAccountPkt);
//						iClientNode.sendPacket(lastPositionPkt);
						System.err.println("使用缓存持仓");
					}
					//------------------------------------------
					if(isGetOrder){
						doReqQryOrder(null);//委托查询
						doReqQryTrade(null);//查询交易记录
					}
					//------------------------------------------
				}
			};
			TraderSpiMyAdaptor.executorService.submit(runnable);
			//-------------------------------------
		}else{
			OnAfterLoginReqQrySettlementInfoConfirm();
			System.err.println("还没有确认成功！");
		}
	}
	/**
	 * 是否是重新登录后直接返回持仓和账户
	 * @return
	 * author:xumin
	 * 2016-6-23 下午4:32:28
	 */
	protected boolean canReturnCache() {
		if(isReEnter ){
			isReEnter = false;
			return true;
		}
		return false;
	}
	/**
	 * 这个是确认成功
	 * @param pSettlementInfoConfirm
	 * @param pRspInfo
	 * @param nRequestID
	 * @param bIsLast
	 * author:xumin
	 * 2016-5-26 下午4:26:17
	 */
	public void OnRspQrySettlementInfoConfirm(
			Pointer<CThostFtdcSettlementInfoConfirmField> pSettlementInfoConfirm,
			Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID,
			boolean bIsLast) {
		if(pSettlementInfoConfirm==null && pRspInfo==null){
			//没有交易的结算单查询。 没有确认的也是这样
			isSettlementInfoConfirm = false;
			setLastAccountCacheNull();
			//-------------------------------------
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					//---------------------
					doReqQrySettlementInfo(null);//查询结算单
				}
			};
			TraderSpiMyAdaptor.executorService.submit(runnable);
		}
		if(pSettlementInfoConfirm!=null){
			isSettlementInfoConfirm = true;
			doGetPosAndAccount(true);
		}

	}
	/**
	 * 下单+没有确认的时候设置缓存失效
	 *	平凡的重连，所以设置缓存。只在重连的时候使用缓存.
	 *因为在下单的时候会制空，所以持久化数据不宜依靠这个！！
	 * author:xumin
	 * 2016-6-24 下午3:10:33
	 */
	private void setLastAccountCacheNull() {
//		lastAccountPkt = null;
//		lastPositionPkt = null;
	}
	/**
	 * 获取结算单，返回
	 * @param pSettlementInfo
	 * @param pRspInfo
	 * @param nRequestID
	 * @param bIsLast
	 * author:xumin
	 * 2016-5-27 下午3:58:48
	 */
	public void OnRspQrySettlementInfo(
			Pointer<CThostFtdcSettlementInfoField> pSettlementInfo,
			Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID,
			boolean bIsLast) {
	}
	//客户端已经确认，并返回成功
	public void OnRspSettlementInfoConfirm(
			Pointer<CThostFtdcSettlementInfoConfirmField> pSettlementInfoConfirm,
			Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID,
			boolean bIsLast) {
		isSettlementInfoConfirm = true;
		//====================================
		//------------------------------
		doGetPosAndAccount(true);
	}
	//初始化listOrder
	public void OnRspQryOrderInitList(Pointer<CThostFtdcOrderField> pOrder,
			Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID,
			boolean bIsLast) {
		if(pOrder!=null){
			Map<String, Object> map = BridjUtils.getMapByPoint(pOrder);
			MsgCenterHelper.putObjectTemp(nRequestID,map);
		}
		if(bIsLast){
			System.out.println("<-----OnRspQryOrder");
			//InsertTime排序
			orderMapClean();
			List<Map<String, Object>> list = (List<Map<String, Object>>) MsgCenterHelper.getAndCleanObjectTemp(nRequestID);
			if(list!=null){
				for(int i=0;i<list.size();i++){
					int maxIndex = i;
					String maxTime = list.get(maxIndex).get("InsertTime").toString();
					for(int j=i+1;j<list.size();j++){
						String thisTime = list.get(j).get("InsertTime").toString();
						if(thisTime.compareTo(maxTime)>0){
							maxIndex = j;
							maxTime = thisTime;
						}
					}
					//-------
					Map<String, Object>  item = list.get(maxIndex);
					addOrder(item);
					list.set(maxIndex, list.get(i));
				}
				//------------------
//				CTPToAppserverAdapter.OnRspQryOrder(this);
			}
		}
	}
	private void orderMapClean() {
		listOrder.clear();
		mapOrder.clear();
	}
	private void addOrder(Map<String, Object> item) {
		listOrder.addLast(item);
		mapOrder.put(item.get("OrderRef").toString(), item);
	}
	/**
	 * 委托等order的状态更新
	 * @param pOrder
	 * author:xumin
	 * 2016-6-6 下午5:15:51
	 */
	public void OnRtnOrder(Pointer<CThostFtdcOrderField> pOrder) {
		Map<String, Object> map = BridjUtils.getMapByPoint(pOrder);
		if(map.get("OrderRef")==null)
			return;
//		CTPToAppserverAdapter.OnRtnOrder(map,this);

//		this.writeLogToDb(pOrder,0,"OnRtnOrder");
	}

	/**
	 *  查询转帐银行 更新查询余额相关参数
	 * @param pTransferBank
     */
	public void onRspQryTransferBank(Pointer<CThostFtdcTransferBankField > pTransferBank){
		Map<String, Object> map = BridjUtils.getMapByPoint(pTransferBank);
		this.bankName = map.get("BankName") == null ? "" : map.get("BankName").toString();
		BANK_INIT_TAG.set(true);
	}


	/**
	 * 银期转账参数初始化一些 银行参数
	 * @param pAccountregister
     */
	public void onRspQryAccountregister(Pointer<CThostFtdcAccountregisterField > pAccountregister){
		Map<String, Object> map = BridjUtils.getMapByPoint(pAccountregister);
		if(map==null || map.get("BankID")==null){
			System.err.println("银期转账相关参数初始化失败");
			BANK_INIT_TAG.set(true);
			return;
		}
		this.bankId = map.get("BankID").toString();
		this.bankBranchId = map.get("BankBranchID")==null?"0000":map.get("BankBranchID").toString();

		/*获取签约银行名*/
		doReqQryTransferBank();

		this.bankAccount = map.get("BankAccount").toString();
		if(StringUtils.isBlank(this.bankAccount)){
			System.err.println("初始化银行账号失败");
			return;
		}else {
			String reg = "^(\\d{4})\\d+(\\d{4})$";
			this.bankAccount = this.bankAccount.replaceAll(reg,"$1 ****$2");
		}
	}

	/**
	 * 查询银行余额通知
	 * @param pNotifyQueryAccount
     */
	public void onRtnQueryBankBalanceByFuture(Pointer<CThostFtdcNotifyQueryAccountField> pNotifyQueryAccount){
	}

	/**
	 * 处理错误的信息在 pRspInfo中 ，比如 银行密码错误等信息
	 * @param pRspInfo
     */
	public void onRspQueryBankAccountMoneyByFuture(Pointer<CThostFtdcRspInfoField> pRspInfo){
	}

	/**
	 * 银期转账流水通知
	 * @param pTransferSerial
	 * @param bIsLast
     */
	public void OnRspQryTransferSerialList(Pointer<CThostFtdcTransferSerialField> pTransferSerial,int nRequestId, boolean bIsLast){
        if(pTransferSerial!=null){
            Map<String,Object> map = BridjUtils.getMapByPoint(pTransferSerial);
            MsgCenterHelper.putObjectTemp(nRequestId,map);
        }

        if(bIsLast){
            System.out.println("<-----OnRspQryTransferSerial");
            transferSerialClean();
            List<Map<String,Object>> list = (List<Map<String, Object>>) MsgCenterHelper.getAndCleanObjectTemp(nRequestId);

            if(list!=null){
                for(Map<String,Object> item:list){
                    addTransferSerialList(item);
                }
            }
        }

	}


    private void transferSerialClean(){
        transferSerialList.clear();
    }

    private void addTransferSerialList(Map<String, Object> item){
        transferSerialList.addLast(item);
    }


	public  void onRtnFromFutureToBankByFuture(Pointer<CThostFtdcRspTransferField> pRspTransfer){
		doReqQryTradingAccount(null);//发起账户信息请求
//		this.writeLogToDb(pRspTransfer,0,"onRtnFromFutureToBankByFuture");
	}

	public void onRtnFromBankToFutureByFuture(Pointer<CThostFtdcRspTransferField> pRspTransfer){
		doReqQryTradingAccount(null);//发起账户信息请求
//		this.writeLogToDb(pRspTransfer,0,"onRtnFromBankToFutureByFuture");
	}
//	@Override
//	public int writeLogToDb(final Pointer ob, final int seq, final String behavior) {
//		final Map<String, Object> map = BridjUtils.getMapByPoint(ob);
//
//		/*银期转账,登录绑定时,对密码做特殊处理*/
//		if(StringUtils.equals(behavior,"doReqFromFutureToBankByFuture")
//				||StringUtils.equals(behavior,"doReqFromBankToFutureByFuture")
//				||StringUtils.equals(behavior,"doLogin")){
//			if(map.containsKey("Password")){
//				map.put("Password","******");
//			}
//		}
//
//		Runnable run = new Runnable() {
//
//			@Override
//			public void run() {
//				try{
////					Map<String, Object> map = BridjUtils.getMapByPoint(ob);
//					String args = JsonUtil.ObToJson(map);
////					System.err.println(args);
//
//					CTPUserBehavior userBehavior = new CTPUserBehavior();
//					userBehavior.setFdtId(loginBean.fdtId);
//					userBehavior.setAccount(userId);
//					userBehavior.setBrokerId(brokerId);
//					userBehavior.setSeq(seq);
//					userBehavior.setArgs(args);
//					userBehavior.setBehavior(behavior);
//					userBehavior.setAppId(loginBean.getAppClientID().toString());
//					userBehavior.setCreateTime(System.currentTimeMillis());
//					CTPUserBehaviorDaoImpl.getCTPUserBehaviorDao().saveUserBehavior(userBehavior);
//				}catch(Exception e){
//					System.err.println("用户日志入队列异常"+e.getMessage());
//				}
//			}
//		};
//		TraderSpiMyAdaptor.executorService.submit(run);
//
//		return 0;
//	}
	/**
	 * 夜盘关闭时执行的事件，包括获取持仓和资金写入数据库
	 * 
	 * author:xumin 
	 * 2016-8-4 下午5:52:48
	 */
	public void doinitYPClosed() {
//		FadeLoginHelper.saveAccountAndPosition(this);
	}
	/**
	 * 持久化数据
	 * 
	 * author:xumin 
	 * 2016-8-22 上午11:54:30
	 */
	public void dopersisentAccAndPos() {
		doReqQryTradingAccount(null);//发起账户信息请求
		doReqQryInvestorPosition(null);//持仓请求
	}
	//-----------------------------------------------------------------------------------
	@Override
	public int doReqQryInstrumentMargin(String instrumentID) {
		CThostFtdcQryInstrumentMarginRateField  req = new CThostFtdcQryInstrumentMarginRateField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		req.InstrumentID().setCString(instrumentID);
		byte b = '1';
		req.HedgeFlag(b);//投机套保标志
		
		Pointer<CThostFtdcQryInstrumentMarginRateField> pReq = Pointer.getPointer(req);
//		int flag = traderApi.ReqQryInstrumentMarginRate(pReq, getNextSeq());
		blockQueueOffer("ReqQryInstrumentMarginRate",pReq,getNextSeq());
		return 0;
	}
	@Override
	public int doReqQueryMaxOrderVolume(String instrumentID, int direction,
			int offsetFlag) {
		CThostFtdcQueryMaxOrderVolumeField  req = new CThostFtdcQueryMaxOrderVolumeField();
		req.BrokerID().setCString(brokerId);
		req.InvestorID().setCString(userId);
		req.InstrumentID().setCString(instrumentID);
		byte b = '0';
		if(direction==1)
			b = '1';
		req.Direction(b);
		b = '0';
		if(offsetFlag==1)
			b = '1';
		if(offsetFlag==3)
			b = '3';
		if(offsetFlag==4)
			b = '4';
		req.OffsetFlag(b);
		req.HedgeFlag((byte)'1');
//		req.MaxVolume(1000);
		
		Pointer<CThostFtdcQueryMaxOrderVolumeField   > pReq = Pointer.getPointer(req);
		
		int flag = traderApi.ReqQueryMaxOrderVolume(pReq, getNextSeq());//这个查询不受限制
		if(flag==0){
			System.err.println("ReqQueryMaxOrderVolume成功！");
		}else{
			System.err.println("ReqQueryMaxOrderVolume失败！");
		}
		return flag;
	}


}
