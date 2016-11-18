package com.whyse.main;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bridj.BridJ;
import org.bridj.Pointer;

import com.whyse.lib.trader.CThostFtdcReqUserLoginField;
import com.whyse.lib.trader.CThostFtdcTraderApi;
import com.whyse.lib.trader.TraderLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.selfmodel.LoginBean;
import com.whyse.main.trader.BridjUtils;
import com.whyse.main.trader.server.TradeMngService;
import com.whyse.main.trader.server.TradeService;
import com.whyse.main.trader.server.impl.TradeServiceImpl;
import com.whyse.web.ctptest.MDTestController;




/*
 * CTP仿真前置一
fz101.ctp.gtjafutures.com
180.169.77.111
CTP仿真前置二
fz201.ctp.gtjafutures.com
112.65.128.129
CTP仿真前置三
fz102.ctp.gtjafutures.com
180.169.77.112
CTP仿真前置四
fz202.ctp.gtjafutures.com
112.65.128.130
交易端口：42205
行情端口：42213
BrokerID:2071
测试账号：10000282：888888
 */
public class MainTest {

	public static ExecutorService executorService = Executors.newFixedThreadPool(2);
	public static TradeServiceImpl tradeServiceImpl = null;
	/**
	 * @param args
	 * author:xumin 
	 * 2016-4-19 上午10:15:18
	 */
	public static void main(String[] args) {
		
		initNativeLibrary();
		//--------------------------------------------------
		final String localFilePath = "C:/ctpfile/";
		LoginBean.setLocalFilePath(localFilePath);
		
//		Pointer<CThostFtdcTraderApi > pTraderApi = CThostFtdcTraderApi.CreateFtdcTraderApi(BridjUtils.stringToBytePointer(localFilePath));
//		CThostFtdcTraderApi traderApi = pTraderApi.get();
		
		LoginBean loginBean = new LoginBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("9999");
		req.UserID().setCString("059266");
		req.Password().setCString("123456");
		req.UserProductInfo().setCString("ftdv1");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://112.65.128.129:42205");
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
		//===============================================
		tradeServiceImpl = new TradeServiceImpl(loginBean);
		tradeServiceImpl.init();
//		Pointer<CThostFtdcMdApi > pmdApi = CThostFtdcMdApi.CreateFtdcMdApi(BridjUtils.stringToBytePointer(localFilePath), true, true);
		//---------------------------
//		Runnable run = new Runnable() {
//			
//			public void run() {
//				
//				tradeServiceImpl.init();
//			}
//		};
//		executorService.submit(run);
		
		//--------------------------------------------------
		// 订阅私有流
		// TERT_RESTART:从本交易日开始重传
		// TERT_RESUME:从上次收到的续传
		// TERT_QUICK:只传送登录后私有流的内容
	}
	/**
	 * 加载动态链接库和环境
	 * 
	 * author:xumin 
	 * 2016-4-26 下午7:25:01
	 */
	public static void initNativeLibrary() {
		String os = System.getProperty("os.name").toLowerCase();//Windows 8.1
		String arch = System.getProperty("os.arch").toLowerCase();//amd64
		String sysTemPath = MDTestController.class.getResource("/").getPath();
		String path = null;
		if(os.contains("win"))
			path = sysTemPath+"lib/win64/thosttraderapi.dll";
		else
			path = sysTemPath+"lib/linux64/thosttraderapi.so";
		loadLibrary(path,"Trader");
	}
	public static void loadLibrary(String path, String name) {
		File f = new File(path);
		System.err.println(path);
		if(f.exists()){
//			BridJ.setNativeLibraryFile(name,f);
			try {
				System.err.println("ssssssssssssssss");
				BridJ.getNativeLibrary(name, f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		System.load(path);
	}
	public static TradeService testMoreAcc(TradeMngService tradeMngService) {
		//--------------------------------------------
		LoginBean loginBean = new LoginBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("9999");
		req.UserID().setCString("059267");//1000021  -- 1000030  10000282
		req.Password().setCString("123456");//1   888888
		req.UserProductInfo().setCString("ftdv111");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://180.168.146.187:10000");
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		tradeMngService.newTradeService(loginBean);//dfdf
		//-------------------------------------
		loginBean = new LoginBean();
		req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("9999");
		req.UserID().setCString("061735");//059269,061735
		req.Password().setCString("12345678");
		req.UserProductInfo().setCString("ftdv111");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://180.168.146.187:10000");
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		tradeMngService.newTradeService(loginBean);//dfdf
		
		//----------------------------------------------------
//		loginBean = new LoginBean();
//		req = new CThostFtdcReqUserLoginField();
//		//经纪公司代码
//		req.BrokerID().setCString("9999");
//		req.UserID().setCString("063377");//059269,061735
//		req.Password().setCString("11111111");
//		req.UserProductInfo().setCString("ftdv111");
//		loginBean.setReq(req);
//		
//		loginBean.setFrontUrl("tcp://180.168.146.187:10000");
//		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		
//		tradeMngService.newTradeService(loginBean);//dfdf
		
		//--------线上环境-------------------------------
//		loginBean = new LoginBean();
//		req = new CThostFtdcReqUserLoginField();
//		req.BrokerID().setCString("7090");
//		req.UserID().setCString("******");//1000021  -- 1000030  10000282
//		req.Password().setCString("538308");//1   888888
//		req.UserProductInfo().setCString("ftdv111");
//		loginBean.setReq(req);
//	
//		loginBean.setFrontUrl("tcp://180.169.75.19:41205");
//		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		int flag = tradeMngService.newTradeService(loginBean);//dfdf
		
		//------------------------------------------------------------------
		
//		loginBean = new LoginBean();
//		req = new CThostFtdcReqUserLoginField();
//		req.BrokerID().setCString("7090");
//		req.UserID().setCString("81002233");//1000021  -- 1000030  10000282
//		req.Password().setCString("175312");//1   888888
//		req.UserProductInfo().setCString("ftdv111");
//		loginBean.setReq(req);
//	
//		loginBean.setFrontUrl("tcp://180.169.75.19:41205");
//		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
//		tradeMngService.newTradeService(loginBean);//dfdf
		//---------------------------------------------------
		
		
		//80008686  - 80008700
		//密码 123456
		for(int i=80008686;i<=80008700;i++){
			loginBean = new LoginBean();
			req = new CThostFtdcReqUserLoginField();
			//经纪公司代码
			req.BrokerID().setCString("2071");
			req.UserID().setCString(String.valueOf(i));
			req.Password().setCString("123456");
			req.UserProductInfo().setCString("ftdv111");
			loginBean.setReq(req);
			
			loginBean.setFrontUrl("tcp://180.169.77.111:42205");
			loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
			loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
			
			tradeMngService.newTradeService(loginBean);//dfdf
		}
		String key = "207180008697";//9999059266,207180008697,7090******,709081002233
		return tradeMngService.getTradeServiceBykey(key);
	}

}
