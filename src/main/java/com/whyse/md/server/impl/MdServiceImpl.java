package com.whyse.md.server.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bridj.Pointer;

import com.whyse.lib.md.CThostFtdcDepthMarketDataField;
import com.whyse.lib.md.CThostFtdcMdApi;
import com.whyse.lib.md.CThostFtdcMdSpi;
import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.CThostFtdcRspInfoField;
import com.whyse.lib.md.CThostFtdcRspUserLoginField;
import com.whyse.lib.md.CThostFtdcUserLogoutField;
import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.main.trader.BridjUtils;
import com.whyse.md.MdSpiMyAdaptor;


public class MdServiceImpl {

//	private static MdServiceImpl instance = new MdServiceImpl();//饥汉单例
	private CThostFtdcMdApi mdApi;
	private CThostFtdcMdSpi mdSpi;
	private String brokerId;
	private String userId;
	private LoginMdBean loginMdBean;
	private String instrumentID="IF1612";
	private List<String> listSub;//这个账号订阅的期货品种
	protected static AtomicInteger seqId = new AtomicInteger();
	
	//===================================================
	protected MdServiceImpl(){
		
	}
	public void init(LoginMdBean loginMdBean, List<String> listSub) {
		if(loginMdBean==null || listSub==null){
			System.err.println("初始化参数不能为空！");
			return;
		}
		this.loginMdBean = loginMdBean;
		this.listSub = listSub;
		brokerId = loginMdBean.getReq().BrokerID().getCString();
		userId = loginMdBean.getReq().UserID().getCString();
		String mdPath = LoginMdBean.getLocalFilePath();//存放tradepai地方的地址+
		
		Pointer<CThostFtdcMdApi> pMdApi = CThostFtdcMdApi.CreateFtdcMdApi(BridjUtils.stringToBytePointer(mdPath),false, false);
		mdApi = pMdApi.get();
		
		mdSpi = new MdSpiMyAdaptor();
		MdSpiMyAdaptor msAd = (MdSpiMyAdaptor) mdSpi;
		msAd.mdService = this;
		mdApi.RegisterSpi(Pointer.getPointer(mdSpi));
		
		mdApi.RegisterFront(BridjUtils.stringToBytePointer(loginMdBean.getFrontUrl()));
		
		
		//======================================
		Runnable run = new Runnable() {
			
			public void run() {
				System.err.println("####################行情服务启动##################"); 
				mdApi.Init();
				//客户端认证做->用户登录->最少执行一次ReqQrySettlementInfoConfirm 才能交易
				mdApi.Join();
//				mdApi.Release();
				System.err.println("md线程释放");
			}
		};
		Thread thread = new Thread(run);
		String name = brokerId+userId+"_md";
		thread.setName(name);
		thread.start();
		
	}
	private int getNextSeq() {
		int tar = seqId.incrementAndGet();//(int) (aa%limitInt)
		return tar;
	}
	public void release() {
		Runnable run = new Runnable() {
			
			public void run() {
				mdApi.Release();
			}
		};
		Thread thread = new Thread(run);
		thread.start();
	}
	//---------------------------------------------
	public int ReqUserLogout() {
		CThostFtdcUserLogoutField req = new CThostFtdcUserLogoutField();
		req.BrokerID().setCString(brokerId);
		req.UserID().setCString(userId);
		Pointer<CThostFtdcUserLogoutField> pReq = Pointer.getPointer(req);
		int flag = mdApi.ReqUserLogout(pReq,getNextSeq());
		if(flag==0){
			System.err.println(userId+"登出成功！");
		}else{
			System.err.println(userId+"登出失败！");
		}
		return flag;
	}
	public int ReqUserLogin() {
		CThostFtdcReqUserLoginField req = loginMdBean.getReq();
		Pointer<CThostFtdcReqUserLoginField> pReq = Pointer.getPointer(req);
		int flag = mdApi.ReqUserLogin(pReq, getNextSeq());
		if(flag==0){
			System.err.println("------->登录成功！");
		}else{
			System.err.println("----->登录失败！");
		}
		return flag;
	}
	public void OnRspUserLogin(
			Pointer<CThostFtdcRspUserLoginField> pRspUserLogin,
			Pointer<CThostFtdcRspInfoField> pRspInfo, boolean bIsLast) {
		
//		listSub = new ArrayList<>(1);
//		listSub.add(instrumentID);
//		listSub.add("rb1701");
		
//		SubscribeForQuoteRsp(list);
//		UnSubscribeForQuoteRsp(list);
		SubscribeMarketData(listSub);//就这个管用
//		UnSubscribeMarketData(list);
		
		
	}
	public int SubscribeForQuoteRsp(List<String> list) {
		Pointer<Pointer<Byte>> ppInstrumentID = BridjUtils.getPPBylist(list);
		int flag = mdApi.SubscribeForQuoteRsp(ppInstrumentID, list.size());
		if(flag==0){
			System.err.println("------->SubscribeForQuoteRsp成功！");
		}else{
			System.err.println("----->SubscribeForQuoteRsp失败！");
		}
		return flag;
		
	}
	public int UnSubscribeForQuoteRsp(List<String> list) {
		Pointer<Pointer<Byte>> ppInstrumentID = BridjUtils.getPPBylist(list);
		int flag = mdApi.UnSubscribeForQuoteRsp(ppInstrumentID, list.size());
		if(flag==0){
			System.err.println("------->UnSubscribeForQuoteRsp成功！");
		}else{
			System.err.println("----->UnSubscribeForQuoteRsp失败！");
		}
		return flag;
		
	}
	/**
	 * 
	 * @param list
	 * @return
	 * author:xumin 
	 * 2016-9-29 下午2:44:50
	 */
	public int UnSubscribeMarketData(List<String> list) {
		Pointer<Pointer<Byte>> ppInstrumentID = BridjUtils.getPPBylist(list);
		int flag = mdApi.UnSubscribeMarketData(ppInstrumentID, list.size());
		if(flag==0){
			System.err.println("------->UnSubscribeMarketData成功！");
		}else{
			System.err.println("----->UnSubscribeMarketData失败！");
		}
		return flag;
	}
	/**
	 * 每次断线不执行这个是不会有行情的
	 * @param listInstrumentID
	 * @return
	 * author:xumin 
	 * 2016-9-29 下午2:44:19
	 */
	public int SubscribeMarketData(List<String> listInstrumentID) {
		Pointer<Pointer<Byte>> ppInstrumentID = BridjUtils.getPPBylist(listInstrumentID);
		int flag = mdApi.SubscribeMarketData(ppInstrumentID, listInstrumentID.size());
		if(flag==0){
			System.err.println("------->SubscribeMarketData成功！");
		}else{
			System.err.println("----->SubscribeMarketData失败！");
		}
		return flag;
	}
	//----------------------------------------------
	/**
	 * 这边处理将很重要，同步到这边应该异步接收数据
	 * @param pDepthMarketData
	 * author:xumin 
	 * 2016-9-30 下午2:52:12
	 */
	public void OnRtnDepthMarketData(
			Pointer<CThostFtdcDepthMarketDataField> pDepthMarketData) {
		// TODO Auto-generated method stub
	}
	
	


}
