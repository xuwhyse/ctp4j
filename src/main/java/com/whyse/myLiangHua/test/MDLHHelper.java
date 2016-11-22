package com.whyse.myLiangHua.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.MdLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.md.server.impl.MdMngServiceImpl;
import com.whyse.md.server.impl.MdServiceImpl;
import com.whyse.myLiangHua.util.TimeWorker;

public class MDLHHelper {

	static List<String> listSym = new ArrayList<>(2);
	static volatile int count = 0;
	public static volatile double[] list5 = new double[4];
	public static volatile double[] list10 = new double[4];
	public static volatile double[] list20 = new double[4];
	public static volatile double[] list30 = new double[4];
	public static volatile Queue<Double> queMin34 = new LinkedBlockingQueue<>(34);
	/**
	 * 用来保存最近不同的报价数据,不同的报价才能被列为参数.
	 * 调整这个容量参数，可以达到扩容样本的目的
	 */
	static volatile Queue<Double> que5 = new LinkedBlockingQueue<>(5);//5显然太大
	static volatile Map<Double, Boolean>  mapQue = new HashMap<Double, Boolean>(10);
	public static LinkedBlockingQueue<Boolean> blockingQueue = new LinkedBlockingQueue<>(10);// 应该是600，看看会不会溢出任务
	/**
	 * 获取到的最新报价
	 */
	static volatile double LastPrice;
	static volatile double LastPriceOlder=0;
	public static volatile Map<String, Object> LastMdMap;
	/**
	 * 量化多空，只做一个品种
	 */
	public static String mainSym = "p1701";//棕榈
//	public static String mainSym = "JM1701";//焦煤
//	public static String mainSym = "RB1701";//螺纹
	static{
//		listSym.add("IF1612");
		listSym.add(mainSym);
	}
	//================================================================
	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 下午5:01:09
	 */
	public static void main(String[] args) {
		initMyMD();
//		long beg = System.currentTimeMillis();
//		for(int i=1;i<66;i++)
//			lastPriceInQueue(que34, i*1.0);
//		Double[]  temp = new Double[34];
//		temp = que34.toArray(temp);
//		updateJS(temp);
//		System.err.println(System.currentTimeMillis()-beg);
	}

	@SuppressWarnings("unused")
	public static void initMyMD() {
		LoginMdBean.setLocalFilePath("C:/ctpfile/md/");
		LoginMdBean loginBean = new LoginMdBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("7090");
		//****** + 825020   ;;;  81002445 +108652
		req.UserID().setCString("81002445");
		req.Password().setCString("108652");//行情：825020  交易、资金 538308
		req.UserProductInfo().setCString("ftdv1");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://180.169.75.19:41213");//实时行情的接口41213
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
//		List<String> list = new ArrayList<>(1);
//		list.add("IF1612");
//		list.add("rb1701");
		//==========================================================================
		MdServiceImpl mdServiceImpl = MdMngServiceImpl.newMDService(loginBean,listSym);
		newConsumerEventJS("计算均线"+mainSym);
		TimeWorker.initCYTimeWorker1();
		TimeWorker.savePerHour(queMin34);
		TimeWorker.tryReadQueMin(queMin34);
		count = queMin34.size();
//		mdServiceImpl = MdMngServiceImpl.getMDService("brokerId","userId");
		
	}

	//=====================================================================
	private static void newConsumerEventJS(String name) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						blockingQueue.take();
						//------这边开始计算均线是否OK-------------
						if(count>=34){
							Double[]  temp = new Double[34];
							temp = queMin34.toArray(temp);
							updateJS(temp);
							//------交易策略制定，等或者行动-------
							TraderLHOptImport.doOrNot();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread td = new Thread(run);
		td.setName(name);
		td.start();
	}
	//=================================================
	/**
	 * 因为乘法跟加法和一样，还是每次累加吧，计算速度基本一样
	 * 很快！！！,就这个就行
	 * author:xumin 
	 * 2016-11-17 下午4:09:45
	 * @param temp 
	 */
	protected static void updateJS(Double[] temp) {
		for(int i=33,j=0;i>29;i--,j++)
			list5[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4])/5;
		
		for(int i=33,j=0;i>29;i--,j++)
			list10[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9])/10;
		
		for(int i=33,j=0;i>29;i--,j++)
			list20[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9]
					+temp[i-10]+temp[i-11]+temp[i-12]+temp[i-13]+temp[i-14]+temp[i-15]+temp[i-16]+temp[i-17]+temp[i-18]+temp[i-19])/20;
		
		for(int i=33,j=0;i>29;i--,j++)
			list30[j] = (temp[i]+temp[i-1]+temp[i-2]+temp[i-3]+temp[i-4]+temp[i-5]+temp[i-6]+temp[i-7]+temp[i-8]+temp[i-9]
					+temp[i-10]+temp[i-11]+temp[i-12]+temp[i-13]+temp[i-14]+temp[i-15]+temp[i-16]+temp[i-17]+temp[i-18]+temp[i-19]
					+temp[i-20]+temp[i-21]+temp[i-22]+temp[i-23]+temp[i-24]+temp[i-25]+temp[i-26]+temp[i-27]+temp[i-28]+temp[i-29])/30;
	}

	/**
	 * 只管存最新的5挡行情
	 * @param mapData
	 * author:xumin 
	 * 2016-11-16 下午4:20:55
	 */
	public static void optData(Map<String, Object> mapData) {
		LastMdMap = mapData;
		LastPrice = (Double) LastMdMap.get("LastPrice");
		lastPriceInQueue(que5,LastPrice);//最新数据入队列
	}
	/**
	 * 每分钟被调用一次，记录过去一分钟的收盘价格
	 * author:xumin 
	 * 2016-11-22 下午7:59:59
	 */
	public static void miniteWriteEvent() {
		Double pjPrice = getPJPrice(que5,5);
		lastPriceInQueue(queMin34,pjPrice);//每分钟的最后均价
		count++;
		//==========行情更新不能阻塞=====================
		blockingQueue.add(true);//发送行情更新的消息
	}

	private static Double getPJPrice(Queue<Double> que, int size) {
		Double[]  temp = new Double[size];
		temp = que.toArray(temp);
		int s = temp.length;
		double total = 0;
		for(double dd : temp)
			total+=dd;
		return total/s;
	}

	/**
	 * 讲最新数据插入队列，如果队列满，则消费首位之后再入队列
	 * @param que
	 * @param price
	 * author:xumin 
	 * 2016-11-17 上午11:02:14
	 */
	public static void lastPriceInQueue(Queue<Double> que, Double price) {
		if(!que.offer(price)){
			//如果队列满，则取出队首再插入
			que.poll();//取出并丢弃
			que.add(price);
		}
	}
	

}
