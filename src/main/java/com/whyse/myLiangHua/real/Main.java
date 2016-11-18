package com.whyse.myLiangHua.real;

import com.whyse.lib.trader.CThostFtdcReqUserLoginField;
import com.whyse.lib.trader.TraderLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.MainTest;
import com.whyse.main.selfmodel.LoginBean;
import com.whyse.main.trader.server.impl.TradeServiceImpl;

public class Main {

	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 上午11:52:00
	 */
	public static void main(String[] args) {
		MainTest.initNativeLibrary();
		//--------------------------------------------------
		final String localFilePath = "C:/ctpfile/";
		LoginBean.setLocalFilePath(localFilePath);
		
		LoginBean loginBean = new LoginBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("7090");
		req.UserID().setCString("81002445");
		req.Password().setCString("108652");
		req.UserProductInfo().setCString("ftdv1");
		loginBean.setReq(req);
		
		loginBean.setFrontUrl("tcp://180.169.75.19:41205");
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
		//===============================================
		TradeServiceImpl tradeServiceImpl = new TradeServiceImpl(loginBean);
		tradeServiceImpl.init();
		
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tradeServiceImpl.doReqQryTradingAccount(null);
	}

}
