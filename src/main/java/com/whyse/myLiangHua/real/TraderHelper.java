package com.whyse.myLiangHua.real;



import com.whyse.lib.trader.CThostFtdcInputOrderField;
import com.whyse.lib.trader.CThostFtdcReqUserLoginField;
import com.whyse.lib.trader.TraderLibrary;
import com.whyse.lib.trader.TraderLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.MainTest;
import com.whyse.main.selfmodel.LoginBean;
import com.whyse.main.trader.server.impl.TradeServiceImpl;

public class TraderHelper {

	static TradeServiceImpl tradeServiceImpl=null;
	static int optNum = 2;
	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 下午5:00:57
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initMyTrader();
	}

	public static void initMyTrader() {
		MainTest.initNativeLibrary();
		//--------------------------------------------------
		final String localFilePath = "C:/ctpfile/";
		LoginBean.setLocalFilePath(localFilePath);
		
		LoginBean loginBean = new LoginBean();
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		//经纪公司代码
		req.BrokerID().setCString("9999");
		req.UserID().setCString("059267");
		req.Password().setCString("123456");
		
//		req.BrokerID().setCString("2071");
//		req.UserID().setCString("80008696");
//		req.Password().setCString("123456");
		
		req.UserProductInfo().setCString("ftdv1");
		loginBean.setReq(req);
		
		loginBean.setPrivateTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		loginBean.setPublicTopic(THOST_TE_RESUME_TYPE.THOST_TERT_QUICK);
		
		//===============================================
		tradeServiceImpl = new TradeServiceImpl(loginBean);
		
		Thread td = new Thread(new Runnable() {
			
			@Override
			public void run() {
				tradeServiceImpl.init();
			}
		});
		td.setName("xumin_Trader");
		td.start();
		
	}
	/**
	 * 
	 * @param side 2:买开  1卖平  -1买平  -2卖开
	 * author:xumin 
	 * 2016-11-18 下午4:52:04
	 * @param item 
	 */
	public static void enterOrder(int side,double price, MDLHClientImpl item) {
		CThostFtdcInputOrderField req = new CThostFtdcInputOrderField();
        req.InstrumentID().setCString(item.mainSym);//合约代码IC1605.CF.FC
        if (side == 2 || side==-1)
            req.Direction((byte) TraderLibrary.THOST_FTDC_D_Buy);//买入
        else {
            req.Direction((byte) TraderLibrary.THOST_FTDC_D_Sell);//卖出
        }
        req.CombHedgeFlag().set(0, (byte) TraderLibrary.THOST_FTDC_HF_Speculation);//投机
//		req.MinVolume(1);//最小成交量
        
        req.VolumeCondition((byte) TraderLibrary.THOST_FTDC_VC_AV);//任何数量
        req.ContingentCondition((byte) TraderLibrary.THOST_FTDC_CC_Immediately);//立即
        req.ForceCloseReason((byte) TraderLibrary.THOST_FTDC_FCC_NotForceClose);//非强平
        req.IsAutoSuspend(0);//自动挂起标志,0:no ; 1:yes  ?w为什么会挂起，是因为什么条件不满足？
        req.UserForceClose(0);//0:no ; 1:yes 用户强评标志

//        req.OrderPriceType((byte) TraderLibrary.THOST_FTDC_OPT_AnyPrice);//市价
//        req.LimitPrice(0);//你出的价格
//        req.TimeCondition((byte) TraderLibrary.THOST_FTDC_TC_IOC);//立即完成，否则撤销
            // 2
        req.OrderPriceType((byte) TraderLibrary.THOST_FTDC_OPT_LimitPrice);//限价
        req.LimitPrice(price);//你出的价格
        req.TimeCondition((byte) TraderLibrary.THOST_FTDC_TC_GFD);//当日有效.(撤销前有效..)
        //=======================================================
        if(side==2 || side==-2){
        	 //直接是开仓
            req.CombOffsetFlag().set(0, (byte) TraderLibrary.THOST_FTDC_OF_Open);//开仓
            req.VolumeTotalOriginal(optNum);// 数量
            tradeServiceImpl.doReqOrderInsert(req);
        }else{
        	//平今仓,日内交易
        	 req.CombOffsetFlag().set(0, (byte) TraderLibrary.THOST_FTDC_OF_Close);
             req.VolumeTotalOriginal(optNum);// 数量
             tradeServiceImpl.doReqOrderInsert(req);
        }
	}

}
