package com.whyse.main.trader;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bridj.Pointer;
import org.bridj.ann.Ptr;
import org.bridj.ann.Virtual;

import com.whyse.lib.trader.*;
import com.whyse.main.trader.server.impl.TradeServiceImpl;
import com.whyse.myLiangHua.util.MyClient;



/**
 * 这个必须自己实现一个的
 * author:xumin
 * 2016-4-20 下午8:04:23
 */
public class TraderSpiMyAdaptor extends CThostFtdcTraderSpi {

    private TradeServiceImpl proxy;
    public static ExecutorService executorService = Executors.newFixedThreadPool(50);

    public TraderSpiMyAdaptor(TradeServiceImpl proxy) {
//		super();
        this.proxy = proxy;
    }

    /**
     * Original signature : <code>void OnFrontConnected()</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:2</i>
     */
    @Virtual(0)
    public void OnFrontConnected() {
        System.out.println("<-----OnFrontConnected");
        if(proxy.mapAuthenticateInfo!=null && proxy.mapAuthenticateInfo.containsKey("code"))
        	proxy.doAuthenticate();
        else
        	proxy.doLogin();
    }

    /**
     * （断线重连）断开连接时，主动调用这个函数
     * 5秒后交易接口自动尝试连接，不用我们操作
     */
    @Virtual(1)
    public void OnFrontDisconnected(int nReason) {
        System.out.println("<-----OnFrontDisconnected");
        if(proxy.state == 1){
        	proxy.release(true);
        }
        proxy.state = 1;
    }

    /**
     * Original signature : <code>void OnHeartBeatWarning(int)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:8</i>
     */
    @Virtual(2)
    public void OnHeartBeatWarning(int nTimeLapse) {
        System.out.println("<-----OnHeartBeatWarning");
        super.OnHeartBeatWarning(nTimeLapse);
    }

    /**
     * Original signature : <code>void OnRspAuthenticate(CThostFtdcRspAuthenticateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:11</i>
     */
    @Virtual(3)
    public void OnRspAuthenticate(Pointer<CThostFtdcRspAuthenticateField> pRspAuthenticateField, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
    	System.out.println("<-----OnRspAuthenticate");
    	proxy.doLogin();
    }

    @Virtual(3)
    protected void OnRspAuthenticate(@Ptr long pRspAuthenticateField, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * Original signature : <code>void OnRspUserLogin(CThostFtdcRspUserLoginField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:14</i>
     */
    @Virtual(4)
    public void OnRspUserLogin(Pointer<CThostFtdcRspUserLoginField> pRspUserLogin, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspUserLogin");
        proxy.OnRspUserLogin(pRspUserLogin, pRspInfo, nRequestID, bIsLast);
    }

    @Virtual(4)
    protected void OnRspUserLogin(@Ptr long pRspUserLogin, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * Original signature : <code>void OnRspUserLogout(CThostFtdcUserLogoutField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:17</i>
     */
    @Virtual(5)
    public void OnRspUserLogout(Pointer<CThostFtdcUserLogoutField> pUserLogout, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspUserLogout");
//        proxy.writeLogToDb(pUserLogout,nRequestID,"OnRspUserLogout");

        proxy.release(true);
    }

    @Virtual(5)
    protected void OnRspUserLogout(@Ptr long pUserLogout, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * Original signature : <code>void OnRspUserPasswordUpdate(CThostFtdcUserPasswordUpdateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:20</i>
     */
    @Virtual(6)
    public void OnRspUserPasswordUpdate(Pointer<CThostFtdcUserPasswordUpdateField> pUserPasswordUpdate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspUserPasswordUpdate");
    }

    @Virtual(6)
    protected void OnRspUserPasswordUpdate(@Ptr long pUserPasswordUpdate, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspTradingAccountPasswordUpdate(CThostFtdcTradingAccountPasswordUpdateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:23</i>
     */
    @Virtual(7)
    public void OnRspTradingAccountPasswordUpdate(Pointer<CThostFtdcTradingAccountPasswordUpdateField> pTradingAccountPasswordUpdate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspTradingAccountPasswordUpdate");
    }

    @Virtual(7)
    protected void OnRspTradingAccountPasswordUpdate(@Ptr long pTradingAccountPasswordUpdate, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * 报单录入请求响应
     * 综合交易平台交易核心返回的包含错误信息的报单响应，对应于上一节中的第7 步的第1 种情况。
     */
    @Virtual(8)
    public void OnRspOrderInsert(Pointer<CThostFtdcInputOrderField> pInputOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println(" <-----OnRspOrderInsert");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pInputOrder);
        CThostFtdcInputOrderField inputOrder = null;
        CThostFtdcRspInfoField rspInfo = null;
        if (pInputOrder != null){
            inputOrder = pInputOrder.get();
        }
        if (pRspInfo != null){
            rspInfo = pRspInfo.get();
//            this.proxy.writeLogToDb(pRspInfo,nRequestID,"OnRspOrderInsert");
        }

    }

    @Virtual(8)
    protected void OnRspOrderInsert(@Ptr long pInputOrder, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspParkedOrderInsert(CThostFtdcParkedOrderField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:29</i>
     */
    @Virtual(9)
    public void OnRspParkedOrderInsert(Pointer<CThostFtdcParkedOrderField> pParkedOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspParkedOrderInsert");
    }

    @Virtual(9)
    protected void OnRspParkedOrderInsert(@Ptr long pParkedOrder, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * Original signature : <code>void OnRspParkedOrderAction(CThostFtdcParkedOrderActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:32</i>
     */
    @Virtual(10)
    public void OnRspParkedOrderAction(Pointer<CThostFtdcParkedOrderActionField> pParkedOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspParkedOrderAction");
    }

    @Virtual(10)
    protected void OnRspParkedOrderAction(@Ptr long pParkedOrderAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * 报单操作请求响应
     * 撤单响应。交易核心返回的含有错误信息的撤单响应
     */
    @Virtual(11)
    public void OnRspOrderAction(Pointer<CThostFtdcInputOrderActionField> pInputOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
//		TraderHelper.printRes(pRspInfo);
        System.out.println("<-----OnRspOrderAction");
//        CTPToAppserverAdapter.OnRspOrderAction(pInputOrderAction, pRspInfo, nRequestID, bIsLast, proxy.iClientNode);
//        this.proxy.writeLogToDb(pInputOrderAction,nRequestID,"pInputOrderAction");
//        this.proxy.writeLogToDb(pRspInfo,nRequestID,"pInputOrderAction");
    }

    @Virtual(11)
    protected void OnRspOrderAction(@Ptr long pInputOrderAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * Original signature : <code>void OnRspQueryMaxOrderVolume(CThostFtdcQueryMaxOrderVolumeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:38</i>
     */
    @Virtual(12)
    public void OnRspQueryMaxOrderVolume(Pointer<CThostFtdcQueryMaxOrderVolumeField> pQueryMaxOrderVolume, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQueryMaxOrderVolume");
//        CTPToAppserverAdapter.OnRspQueryMaxOrderVolume(pQueryMaxOrderVolume, pRspInfo, nRequestID, bIsLast, proxy);
    }

    @Virtual(12)
    protected void OnRspQueryMaxOrderVolume(@Ptr long pQueryMaxOrderVolume, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {
    }

    /**
     * 请求确认结算单响应,每天都会有一次确认结算单（上个交易日）
     */
    @Virtual(13)
    public void OnRspSettlementInfoConfirm(Pointer<CThostFtdcSettlementInfoConfirmField> pSettlementInfoConfirm, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspSettlementInfoConfirm");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pSettlementInfoConfirm);
        //-----------------------------------------
        proxy.OnRspSettlementInfoConfirm(pSettlementInfoConfirm, pRspInfo, nRequestID, bIsLast);
    }

    /**
     * Original signature : <code>void OnRspRemoveParkedOrder(CThostFtdcRemoveParkedOrderField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:44</i>
     */
    @Virtual(14)
    public void OnRspRemoveParkedOrder(Pointer<CThostFtdcRemoveParkedOrderField> pRemoveParkedOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspRemoveParkedOrder");
    }

    @Virtual(14)
    protected void OnRspRemoveParkedOrder(@Ptr long pRemoveParkedOrder, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspRemoveParkedOrderAction(CThostFtdcRemoveParkedOrderActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:47</i>
     */
    @Virtual(15)
    public void OnRspRemoveParkedOrderAction(Pointer<CThostFtdcRemoveParkedOrderActionField> pRemoveParkedOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspRemoveParkedOrderAction");
    }

    @Virtual(15)
    protected void OnRspRemoveParkedOrderAction(@Ptr long pRemoveParkedOrderAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspExecOrderInsert(CThostFtdcInputExecOrderField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:50</i>
     */
    @Virtual(16)
    public void OnRspExecOrderInsert(Pointer<CThostFtdcInputExecOrderField> pInputExecOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspExecOrderInsert");
    }

    @Virtual(16)
    protected void OnRspExecOrderInsert(@Ptr long pInputExecOrder, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspExecOrderAction(CThostFtdcInputExecOrderActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:53</i>
     */
    @Virtual(17)
    public void OnRspExecOrderAction(Pointer<CThostFtdcInputExecOrderActionField> pInputExecOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspExecOrderAction");
    }

    @Virtual(17)
    protected void OnRspExecOrderAction(@Ptr long pInputExecOrderAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * 询价录入请求响应
     */
    @Virtual(18)
    public void OnRspForQuoteInsert(Pointer<CThostFtdcInputForQuoteField> pInputForQuote, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspForQuoteInsert");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pInputForQuote);
    }

    @Virtual(18)
    protected void OnRspForQuoteInsert(@Ptr long pInputForQuote, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspQuoteInsert(CThostFtdcInputQuoteField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:59</i>
     */
    @Virtual(19)
    public void OnRspQuoteInsert(Pointer<CThostFtdcInputQuoteField> pInputQuote, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspForQuoteInsert");
    }

    @Virtual(19)
    protected void OnRspQuoteInsert(@Ptr long pInputQuote, @Ptr long pRspInfo, int nRequestID, boolean bIsLast) {

    }

    /**
     * Original signature : <code>void OnRspQuoteAction(CThostFtdcInputQuoteActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:62</i>
     */
    @Virtual(20)
    public void OnRspQuoteAction(Pointer<CThostFtdcInputQuoteActionField> pInputQuoteAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQuoteAction");
    }

    @Virtual(20)
    protected native void OnRspQuoteAction(@Ptr long pInputQuoteAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspCombActionInsert(CThostFtdcInputCombActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:65</i>
     */
    @Virtual(21)
    public void OnRspCombActionInsert(Pointer<CThostFtdcInputCombActionField> pInputCombAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspCombActionInsert");
    }

    @Virtual(21)
    protected native void OnRspCombActionInsert(@Ptr long pInputCombAction, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * 请求查询报单响应
     */
    @Virtual(22)
    public void OnRspQryOrder(Pointer<CThostFtdcOrderField> pOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
//		System.out.println("<-----OnRspQryOrder");
        //		BridjUtils.printObFields(pRspInfo);
//		BridjUtils.printObFields(pOrder);
//		System.err.println("OrderRef:"+pOrder.get().OrderRef().getCString());
//		System.err.println("InsertTime:"+pOrder.get().InsertTime().getCString());
//		System.err.println("Direction:"+(char)pOrder.get().Direction()+"   CombOffsetFlag:"+(char)pOrder.get().CombOffsetFlag().getBytes()[0]);
//		System.err.println("OrderStatus:"+(char)pOrder.get().OrderStatus()+"   OrderSubmitStatus:"+(char)pOrder.get().OrderSubmitStatus());
        proxy.OnRspQryOrderInitList(pOrder, pRspInfo, nRequestID, bIsLast);
    }

    @Virtual(22)
    protected native void OnRspQryOrder(@Ptr long pOrder, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryTrade(CThostFtdcTradeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:71</i>
     */
    @Virtual(23)
    public void OnRspQryTrade(Pointer<CThostFtdcTradeField> pTrade, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
    	if(bIsLast)
    		System.out.println("<-----OnRspQryTrade");
//    	CTPToAppserverAdapter.OnRspQryTrade(pTrade, pRspInfo, nRequestID, bIsLast, proxy.iClientNode);
    }

    @Virtual(23)
    protected native void OnRspQryTrade(@Ptr long pTrade, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * 返回用户某个合约的持仓信息
     * 这个接口返回多个信息，必须同步
     */
    @Virtual(24)
    public void OnRspQryInvestorPosition(Pointer<CThostFtdcInvestorPositionField> pInvestorPosition, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        CThostFtdcInvestorPositionField investorPosition = null;
        CThostFtdcRspInfoField rspInfo = null;
        if (pInvestorPosition != null)
            investorPosition = pInvestorPosition.get();
        if (pRspInfo != null)
            rspInfo = pRspInfo.get();
//        CTPToAppserverAdapter.OnRspQryInvestorPosition(investorPosition, rspInfo, nRequestID, bIsLast, proxy);
        //--------------------------------
//		if(bIsLast){
//			proxy.reentrantLockReq.unlock();
//		}
        //--------------------------------

    }

    /**
     * 请求查询资金账户响应
     */
    @Virtual(25)
    public void OnRspQryTradingAccount(Pointer<CThostFtdcTradingAccountField> pTradingAccount, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryTradingAccount");
//        Map<String, Object> map = BridjUtils.getMapByPoint(pTradingAccount);
//        System.err.println(map);
        if(proxy.tradingAccountWillGet){
//        	proxy.writeLogToDb(pTradingAccount,-1,"OnRspQryTradingAccountForMy");
        	proxy.tradingAccountWillGet = false;//回复成原来的样子
        	return;
        }
        CThostFtdcTradingAccountField tradingAccount = null;
        CThostFtdcRspInfoField rspInfo = null;
        if (pTradingAccount != null)
            tradingAccount = pTradingAccount.get();
        if (pRspInfo != null)
            rspInfo = pRspInfo.get();
        final CThostFtdcRspInfoField frspInfo = rspInfo;
        final CThostFtdcTradingAccountField ftradingAccount = tradingAccount;
        final int fnRequestID = nRequestID;
//        CTPToAppserverAdapter.OnRspQryTradingAccount(frspInfo, fnRequestID, proxy, ftradingAccount);
    }

    /**
     * Original signature : <code>void OnRspQryInvestor(CThostFtdcInvestorField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:80</i>
     */
    @Virtual(26)
    public void OnRspQryInvestor(Pointer<CThostFtdcInvestorField> pInvestor, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInvestor");
    }

    @Virtual(26)
    protected native void OnRspQryInvestor(@Ptr long pInvestor, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryTradingCode(CThostFtdcTradingCodeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:83</i>
     */
    @Virtual(27)
    public void OnRspQryTradingCode(Pointer<CThostFtdcTradingCodeField> pTradingCode, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryTradingCode");
    }

    @Virtual(27)
    protected native void OnRspQryTradingCode(@Ptr long pTradingCode, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:86</i>
     */
    @Virtual(28)
    public void OnRspQryInstrumentMarginRate(Pointer<CThostFtdcInstrumentMarginRateField> pInstrumentMarginRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInstrumentMarginRate");
//        CTPToAppserverAdapter.OnRspQryInstrumentMarginRate(pInstrumentMarginRate, pRspInfo, nRequestID, bIsLast, proxy);
    }

    @Virtual(28)
    protected native void OnRspQryInstrumentMarginRate(@Ptr long pInstrumentMarginRate, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:89</i>
     */
    @Virtual(29)
    public void OnRspQryInstrumentCommissionRate(Pointer<CThostFtdcInstrumentCommissionRateField> pInstrumentCommissionRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInstrumentCommissionRate");
    }

    @Virtual(29)
    protected native void OnRspQryInstrumentCommissionRate(@Ptr long pInstrumentCommissionRate, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryExchange(CThostFtdcExchangeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:92</i>
     */
    @Virtual(30)
    public void OnRspQryExchange(Pointer<CThostFtdcExchangeField> pExchange, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryExchange");
    }

    @Virtual(30)
    protected native void OnRspQryExchange(@Ptr long pExchange, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryProduct(CThostFtdcProductField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:95</i>
     */
    @Virtual(31)
    public void OnRspQryProduct(Pointer<CThostFtdcProductField> pProduct, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryProduct");
    }

    @Virtual(31)
    protected native void OnRspQryProduct(@Ptr long pProduct, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryInstrument(CThostFtdcInstrumentField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:98</i>
     */
    @Virtual(32)
    public void OnRspQryInstrument(Pointer<CThostFtdcInstrumentField> pInstrument, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInstrument");
    }

    @Virtual(32)
    protected native void OnRspQryInstrument(@Ptr long pInstrument, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryDepthMarketData(CThostFtdcDepthMarketDataField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:101</i>
     */
    @Virtual(33)
    public void OnRspQryDepthMarketData(Pointer<CThostFtdcDepthMarketDataField> pDepthMarketData, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryDepthMarketData");
    }

    @Virtual(33)
    protected native void OnRspQryDepthMarketData(@Ptr long pDepthMarketData, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * 可用资金，持仓，保证金占用
     * 还能获取历史交易记录
     * 在获取结算单的时候必须同步！！
     */
    @Virtual(34)
    public void OnRspQrySettlementInfo(Pointer<CThostFtdcSettlementInfoField> pSettlementInfo, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
//		if(bIsLast)
//			proxy.reentrantLockReq.unlock();
        proxy.OnRspQrySettlementInfo(pSettlementInfo, pRspInfo, nRequestID, bIsLast);
    }

    /**
     * Original signature : <code>void OnRspQryTransferBank(CThostFtdcTransferBankField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:107</i>
     */
    @Virtual(35)
    public void OnRspQryTransferBank(Pointer<CThostFtdcTransferBankField> pTransferBank, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
    	if(bIsLast)
    		System.out.println("<-----OnRspQryTransferBank");
        this.proxy.onRspQryTransferBank(pTransferBank);
    }

    @Virtual(35)
    protected native void OnRspQryTransferBank(@Ptr long pTransferBank, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:110</i>
     */
    @Virtual(36)
    public void OnRspQryInvestorPositionDetail(Pointer<CThostFtdcInvestorPositionDetailField> pInvestorPositionDetail, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInvestorPositionDetail");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pInvestorPositionDetail);
//        CTPToAppserverAdapter.OnRspQryInvestorPositionDetail(pInvestorPositionDetail, pRspInfo, nRequestID, bIsLast, proxy.iClientNode);
    }

    /**
     * Original signature : <code>void OnRspQryNotice(CThostFtdcNoticeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:113</i>
     */
    @Virtual(37)
    public void OnRspQryNotice(Pointer<CThostFtdcNoticeField> pNotice, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryNotice");
    }

    @Virtual(37)
    protected native void OnRspQryNotice(@Ptr long pNotice, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * 查询结算单确认的日期
     */
    @Virtual(38)
    public void OnRspQrySettlementInfoConfirm(final Pointer<CThostFtdcSettlementInfoConfirmField> pSettlementInfoConfirm, final Pointer<CThostFtdcRspInfoField> pRspInfo, final int nRequestID, final boolean bIsLast) {
        System.out.println("<-----OnRspQrySettlementInfoConfirm");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pSettlementInfoConfirm);
        //--------------------------------
//		proxy.reentrantLockReq.unlock();
        proxy.OnRspQrySettlementInfoConfirm(pSettlementInfoConfirm, pRspInfo, nRequestID, bIsLast);

    }

    /**
     * Original signature : <code>void OnRspQryInvestorPositionCombineDetail(CThostFtdcInvestorPositionCombineDetailField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:119</i>
     */
    @Virtual(39)
    public void OnRspQryInvestorPositionCombineDetail(Pointer<CThostFtdcInvestorPositionCombineDetailField> pInvestorPositionCombineDetail, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInvestorPositionCombineDetail");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pInvestorPositionCombineDetail);
    }

    @Virtual(39)
    protected native void OnRspQryInvestorPositionCombineDetail(@Ptr long pInvestorPositionCombineDetail, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryCFMMCTradingAccountKey(CThostFtdcCFMMCTradingAccountKeyField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:122</i>
     */
    @Virtual(40)
    public void OnRspQryCFMMCTradingAccountKey(Pointer<CThostFtdcCFMMCTradingAccountKeyField> pCFMMCTradingAccountKey, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryCFMMCTradingAccountKey");
    }

    @Virtual(40)
    protected native void OnRspQryCFMMCTradingAccountKey(@Ptr long pCFMMCTradingAccountKey, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryEWarrantOffset(CThostFtdcEWarrantOffsetField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:125</i>
     */
    @Virtual(41)
    public void OnRspQryEWarrantOffset(Pointer<CThostFtdcEWarrantOffsetField> pEWarrantOffset, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryEWarrantOffset");
    }

    @Virtual(41)
    protected native void OnRspQryEWarrantOffset(@Ptr long pEWarrantOffset, @Ptr long pRspInfo, int nRequestID, boolean bIsLast);

    /**
     * Original signature : <code>void OnRspQryInvestorProductGroupMargin(CThostFtdcInvestorProductGroupMarginField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:128</i>
     */
    @Virtual(42)
    public void OnRspQryInvestorProductGroupMargin(Pointer<CThostFtdcInvestorProductGroupMarginField> pInvestorProductGroupMargin, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInvestorProductGroupMargin");
    }

    /**
     * Original signature : <code>void OnRspQryExchangeMarginRate(CThostFtdcExchangeMarginRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:131</i>
     */
    @Virtual(43)
    public void OnRspQryExchangeMarginRate(Pointer<CThostFtdcExchangeMarginRateField> pExchangeMarginRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryExchangeMarginRate");
    }

    /**
     * Original signature : <code>void OnRspQryExchangeMarginRateAdjust(CThostFtdcExchangeMarginRateAdjustField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:134</i>
     */
    @Virtual(44)
    public void OnRspQryExchangeMarginRateAdjust(Pointer<CThostFtdcExchangeMarginRateAdjustField> pExchangeMarginRateAdjust, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryExchangeMarginRateAdjust");
    }

    /**
     * Original signature : <code>void OnRspQryExchangeRate(CThostFtdcExchangeRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:137</i>
     */
    @Virtual(45)
    public void OnRspQryExchangeRate(Pointer<CThostFtdcExchangeRateField> pExchangeRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryExchangeRate");
    }

    /**
     * Original signature : <code>void OnRspQrySecAgentACIDMap(CThostFtdcSecAgentACIDMapField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:140</i>
     */
    @Virtual(46)
    public void OnRspQrySecAgentACIDMap(Pointer<CThostFtdcSecAgentACIDMapField> pSecAgentACIDMap, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQrySecAgentACIDMap");
    }

    /**
     * Original signature : <code>void OnRspQryProductGroup(CThostFtdcProductGroupField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:143</i>
     */
    @Virtual(47)
    public void OnRspQryProductGroup(Pointer<CThostFtdcProductGroupField> pProductGroup, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryProductGroup");
    }

    /**
     * Original signature : <code>void OnRspQryInstrumentOrderCommRate(CThostFtdcInstrumentOrderCommRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:146</i>
     */
    @Virtual(48)
    public void OnRspQryInstrumentOrderCommRate(Pointer<CThostFtdcInstrumentOrderCommRateField> pInstrumentOrderCommRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryInstrumentOrderCommRate");
    }

    /**
     * Original signature : <code>void OnRspQryOptionInstrTradeCost(CThostFtdcOptionInstrTradeCostField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:149</i>
     */
    @Virtual(49)
    public void OnRspQryOptionInstrTradeCost(Pointer<CThostFtdcOptionInstrTradeCostField> pOptionInstrTradeCost, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryOptionInstrTradeCost");
    }

    /**
     * Original signature : <code>void OnRspQryOptionInstrCommRate(CThostFtdcOptionInstrCommRateField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:152</i>
     */
    @Virtual(50)
    public void OnRspQryOptionInstrCommRate(Pointer<CThostFtdcOptionInstrCommRateField> pOptionInstrCommRate, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryOptionInstrCommRate");
    }

    /**
     * Original signature : <code>void OnRspQryExecOrder(CThostFtdcExecOrderField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:155</i>
     */
    @Virtual(51)
    public void OnRspQryExecOrder(Pointer<CThostFtdcExecOrderField> pExecOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryExecOrder");
    }

    /**
     * Original signature : <code>void OnRspQryForQuote(CThostFtdcForQuoteField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:158</i>
     */
    @Virtual(52)
    public void OnRspQryForQuote(Pointer<CThostFtdcForQuoteField> pForQuote, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryForQuote");
    }

    /**
     * Original signature : <code>void OnRspQryQuote(CThostFtdcQuoteField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:161</i>
     */
    @Virtual(53)
    public void OnRspQryQuote(Pointer<CThostFtdcQuoteField> pQuote, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryQuote");
    }

    /**
     * Original signature : <code>void OnRspQryCombInstrumentGuard(CThostFtdcCombInstrumentGuardField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:164</i>
     */
    @Virtual(54)
    public void OnRspQryCombInstrumentGuard(Pointer<CThostFtdcCombInstrumentGuardField> pCombInstrumentGuard, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryCombInstrumentGuard");
    }

    /**
     * Original signature : <code>void OnRspQryCombAction(CThostFtdcCombActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:167</i>
     */
    @Virtual(55)
    public void OnRspQryCombAction(Pointer<CThostFtdcCombActionField> pCombAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryCombAction");
    }

    /**
     * Original signature : <code>void OnRspQryTransferSerial(CThostFtdcTransferSerialField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:170</i>
     */
    @Virtual(56)
    public void OnRspQryTransferSerial(Pointer<CThostFtdcTransferSerialField> pTransferSerial, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
//        System.out.println("<-----OnRspQryTransferSerial");
        this.proxy.OnRspQryTransferSerialList(pTransferSerial,nRequestID , bIsLast);
    }

    /**
     * Original signature : <code>void OnRspQryAccountregister(CThostFtdcAccountregisterField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:173</i>
     */
    @Virtual(57)
    public void OnRspQryAccountregister(Pointer<CThostFtdcAccountregisterField> pAccountregister, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryAccountregister");
        this.proxy.onRspQryAccountregister(pAccountregister);
    }

    /**
     * Original signature : <code>void OnRspError(CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:176</i>
     */
    @Virtual(58)
    public void OnRspError(Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspError");
        BridjUtils.printObFields(pRspInfo);
    }

    /**
     * 报单回报主要作用是通知客户端该报单的最新状态，如已提交，已撤销，未触发，已成交deng
     * 每次报单状态有变化，该函数都会被调用一次。
     */
    @Virtual(59)
    public void OnRtnOrder(Pointer<CThostFtdcOrderField> pOrder) {
        System.out.println("<-----OnRtnOrder");
        System.out.println("OrderStatus:" + (char) pOrder.get().OrderStatus() + " OrderSubmitStatus:" + (char) pOrder.get().OrderSubmitStatus());
//		BridjUtils.printObFields(pOrder);
        proxy.OnRtnOrder(pOrder);
    }

    /**
     * 函数返回报单成交回报，每笔成交都会调用一次成交回报。成交回报中只包含合约，成交数量，价格等信息。
     * 成交回报只包含该笔成交相关的信息， 并不包含该笔成交之后投资者的持仓， 资金等信息。函数
     * ReqQryTradingAccount 用于查询投资者最新的资金状况。如保证金，手续费，持仓盈利，可用资金等
     */
    @Virtual(60)
    public void OnRtnTrade(Pointer<CThostFtdcTradeField> pTrade) {
        System.out.println("<-----OnRtnTrade");
        MyClient.OnRtnTrade(pTrade);
    }

    /**
     * 报盘将通过交易核心检查的报单发送到交易所前置，交易所会再次校验该报单。如果交易所认为该报单不合
     * 法，交易所会将该报单撤销，将错误信息返回给报盘，并返回更新后的该报单的状态。当客户端接收到该错
     * 误信息后，就会调用OnErrRtnOrderInsert 函数， 而更新后的报单状态会通过调用函数OnRtnOrder 发送到客
     * 户端。如果交易所认为该报单合法，则只返回该报单状态（此时的状态应为：“尚未触发”）
     */
    @Virtual(61)
    public void OnErrRtnOrderInsert(Pointer<CThostFtdcInputOrderField> pInputOrder, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnOrderInsert");

        CThostFtdcInputOrderField inputOrder = null;
        CThostFtdcRspInfoField rspInfo = null;
        if (pInputOrder != null)
            inputOrder = pInputOrder.get();
        if (pRspInfo != null)
            rspInfo = pRspInfo.get();
//		System.err.println(BridjUtils.getMapByPoint(pInputOrder));
//        CTPToAppserverAdapter.OnErrRtnOrderInsert(rspInfo, proxy.iClientNode, inputOrder);
//        proxy.writeLogToDb(pRspInfo,0,"OnErrRtnOrderInsert");
    }

    /**
     * Original signature : <code>void OnErrRtnOrderAction(CThostFtdcOrderActionField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:188</i>
     */
    @Virtual(62)
    public void OnErrRtnOrderAction(Pointer<CThostFtdcOrderActionField> pOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnOrderAction");
//        CTPToAppserverAdapter.OnErrRtnOrderAction(pOrderAction,pRspInfo,proxy);
//        proxy.writeLogToDb(pRspInfo,0,"OnErrRtnOrderAction");
    }

    @Virtual(62)
    protected native void OnErrRtnOrderAction(@Ptr long pOrderAction, @Ptr long pRspInfo);

    /**
     * 这边是下单，或者撤单，交易单的状态变化通知
     */
    @Virtual(63)
    public void OnRtnInstrumentStatus(Pointer<CThostFtdcInstrumentStatusField> pInstrumentStatus) {
        System.out.println("<-----OnRtnInstrumentStatus");
//        BridjUtils.printObFields(pInstrumentStatus, true);
    }

    @Virtual(63)
    protected native void OnRtnInstrumentStatus(@Ptr long pInstrumentStatus);

    /**
     * Original signature : <code>void OnRtnTradingNotice(CThostFtdcTradingNoticeInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:194</i>
     */
    @Virtual(64)
    public void OnRtnTradingNotice(Pointer<CThostFtdcTradingNoticeInfoField> pTradingNoticeInfo) {
        System.out.println("<-----OnRtnTradingNotice");
//        BridjUtils.printObFields(pTradingNoticeInfo, true);
    }

    @Virtual(64)
    protected native void OnRtnTradingNotice(@Ptr long pTradingNoticeInfo);

    /**
     * Original signature : <code>void OnRtnErrorConditionalOrder(CThostFtdcErrorConditionalOrderField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:197</i>
     */
    @Virtual(65)
    public void OnRtnErrorConditionalOrder(Pointer<CThostFtdcErrorConditionalOrderField> pErrorConditionalOrder) {
        System.out.println("<-----OnRtnErrorConditionalOrder");
    }

    @Virtual(65)
    protected native void OnRtnErrorConditionalOrder(@Ptr long pErrorConditionalOrder);

    /**
     * Original signature : <code>void OnRtnExecOrder(CThostFtdcExecOrderField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:200</i>
     */
    @Virtual(66)
    public void OnRtnExecOrder(Pointer<CThostFtdcExecOrderField> pExecOrder) {
        System.out.println("<-----OnRtnExecOrder");
    }

    /**
     * Original signature : <code>void OnErrRtnExecOrderInsert(CThostFtdcInputExecOrderField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:203</i>
     */
    @Virtual(67)
    public void OnErrRtnExecOrderInsert(Pointer<CThostFtdcInputExecOrderField> pInputExecOrder, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnExecOrderInsert");
    }

    /**
     * Original signature : <code>void OnErrRtnExecOrderAction(CThostFtdcExecOrderActionField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:206</i>
     */
    @Virtual(68)
    public void OnErrRtnExecOrderAction(Pointer<CThostFtdcExecOrderActionField> pExecOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnExecOrderAction");
    }

    /**
     * 询价录入错误回报
     */
    @Virtual(69)
    public void OnErrRtnForQuoteInsert(Pointer<CThostFtdcInputForQuoteField> pInputForQuote, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnForQuoteInsert");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pInputForQuote);
    }

    /**
     * Original signature : <code>void OnRtnQuote(CThostFtdcQuoteField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:212</i>
     */
    @Virtual(70)
    public void OnRtnQuote(Pointer<CThostFtdcQuoteField> pQuote) {
        System.out.println("<-----OnRtnQuote");
    }

    /**
     * Original signature : <code>void OnErrRtnQuoteInsert(CThostFtdcInputQuoteField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:215</i>
     */
    @Virtual(71)
    public void OnErrRtnQuoteInsert(Pointer<CThostFtdcInputQuoteField> pInputQuote, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnQuoteInsert");
    }

    /**
     * Original signature : <code>void OnErrRtnQuoteAction(CThostFtdcQuoteActionField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:218</i>
     */
    @Virtual(72)
    public void OnErrRtnQuoteAction(Pointer<CThostFtdcQuoteActionField> pQuoteAction, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnQuoteAction");
    }

    /**
     * Original signature : <code>void OnRtnForQuoteRsp(CThostFtdcForQuoteRspField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:221</i>
     */
    @Virtual(73)
    public void OnRtnForQuoteRsp(Pointer<CThostFtdcForQuoteRspField> pForQuoteRsp) {
        System.out.println("<-----OnRtnForQuoteRsp");
    }

    /**
     * Original signature : <code>void OnRtnCFMMCTradingAccountToken(CThostFtdcCFMMCTradingAccountTokenField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:224</i>
     */
    @Virtual(74)
    public void OnRtnCFMMCTradingAccountToken(Pointer<CThostFtdcCFMMCTradingAccountTokenField> pCFMMCTradingAccountToken) {
        System.out.println("<-----OnRtnCFMMCTradingAccountToken");
    }

    /**
     * Original signature : <code>void OnRtnCombAction(CThostFtdcCombActionField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:227</i>
     */
    @Virtual(75)
    public void OnRtnCombAction(Pointer<CThostFtdcCombActionField> pCombAction) {
        System.out.println("<-----OnRtnCombAction");
    }

    /**
     * Original signature : <code>void OnErrRtnCombActionInsert(CThostFtdcInputCombActionField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:230</i>
     */
    @Virtual(76)
    public void OnErrRtnCombActionInsert(Pointer<CThostFtdcInputCombActionField> pInputCombAction, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnCombActionInsert");
    }

    /**
     * Original signature : <code>void OnRspQryContractBank(CThostFtdcContractBankField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:233</i>
     */
    @Virtual(77)
    public void OnRspQryContractBank(Pointer<CThostFtdcContractBankField> pContractBank, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryContractBank");
    }

    /**
     * Original signature : <code>void OnRspQryParkedOrder(CThostFtdcParkedOrderField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:236</i>
     */
    @Virtual(78)
    public void OnRspQryParkedOrder(Pointer<CThostFtdcParkedOrderField> pParkedOrder, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryParkedOrder");
    }

    /**
     * Original signature : <code>void OnRspQryParkedOrderAction(CThostFtdcParkedOrderActionField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:239</i>
     */
    @Virtual(79)
    public void OnRspQryParkedOrderAction(Pointer<CThostFtdcParkedOrderActionField> pParkedOrderAction, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryParkedOrderAction");
    }

    /**
     * Original signature : <code>void OnRspQryTradingNotice(CThostFtdcTradingNoticeField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:242</i>
     */
    @Virtual(80)
    public void OnRspQryTradingNotice(Pointer<CThostFtdcTradingNoticeField> pTradingNotice, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryTradingNotice");
    }

    /**
     * Original signature : <code>void OnRspQryBrokerTradingParams(CThostFtdcBrokerTradingParamsField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:245</i>
     */
    @Virtual(81)
    public void OnRspQryBrokerTradingParams(Pointer<CThostFtdcBrokerTradingParamsField> pBrokerTradingParams, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryBrokerTradingParams");
    }

    /**
     * Original signature : <code>void OnRspQryBrokerTradingAlgos(CThostFtdcBrokerTradingAlgosField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:248</i>
     */
    @Virtual(82)
    public void OnRspQryBrokerTradingAlgos(Pointer<CThostFtdcBrokerTradingAlgosField> pBrokerTradingAlgos, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQryBrokerTradingAlgos");
    }

    /**
     * Original signature : <code>void OnRspQueryCFMMCTradingAccountToken(CThostFtdcQueryCFMMCTradingAccountTokenField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:251</i>
     */
    @Virtual(83)
    public void OnRspQueryCFMMCTradingAccountToken(Pointer<CThostFtdcQueryCFMMCTradingAccountTokenField> pQueryCFMMCTradingAccountToken, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQueryCFMMCTradingAccountToken");
    }

    /**
     * Original signature : <code>void OnRtnFromBankToFutureByBank(CThostFtdcRspTransferField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:254</i>
     */
    @Virtual(84)
    public void OnRtnFromBankToFutureByBank(Pointer<CThostFtdcRspTransferField> pRspTransfer) {
        System.out.println("<-----OnRtnFromBankToFutureByBank");
    }

    /**
     * Original signature : <code>void OnRtnFromFutureToBankByBank(CThostFtdcRspTransferField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:257</i>
     */
    @Virtual(85)
    public void OnRtnFromFutureToBankByBank(Pointer<CThostFtdcRspTransferField> pRspTransfer) {
        System.out.println("<-----OnRtnFromFutureToBankByBank");
    }

    /**
     * Original signature : <code>void OnRtnRepealFromBankToFutureByBank(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:260</i>
     */
    @Virtual(86)
    public void OnRtnRepealFromBankToFutureByBank(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromBankToFutureByBank");
    }

    /**
     * Original signature : <code>void OnRtnRepealFromFutureToBankByBank(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:263</i>
     */
    @Virtual(87)
    public void OnRtnRepealFromFutureToBankByBank(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromFutureToBankByBank");
    }

    /**
     * Original signature : <code>void OnRtnFromBankToFutureByFuture(CThostFtdcRspTransferField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:266</i>
     */
    @Virtual(88)
    public void OnRtnFromBankToFutureByFuture(Pointer<CThostFtdcRspTransferField> pRspTransfer) {
        System.out.println("<-----OnRtnFromBankToFutureByFuture");
        proxy.onRtnFromBankToFutureByFuture(pRspTransfer);
    }

    /**
     * Original signature : <code>void OnRtnFromFutureToBankByFuture(CThostFtdcRspTransferField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:269</i>
     */
    @Virtual(89)
    public void OnRtnFromFutureToBankByFuture(Pointer<CThostFtdcRspTransferField> pRspTransfer) {
        System.out.println("<-----OnRtnFromFutureToBankByFuture");
        proxy.onRtnFromFutureToBankByFuture(pRspTransfer);

    }

    /**
     * Original signature : <code>void OnRtnRepealFromBankToFutureByFutureManual(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:272</i>
     */
    @Virtual(90)
    public void OnRtnRepealFromBankToFutureByFutureManual(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromBankToFutureByFutureManual");
    }

    /**
     * Original signature : <code>void OnRtnRepealFromFutureToBankByFutureManual(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:275</i>
     */
    @Virtual(91)
    public void OnRtnRepealFromFutureToBankByFutureManual(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromFutureToBankByFutureManual");
    }

    /**
     * Original signature : <code>void OnRtnQueryBankBalanceByFuture(CThostFtdcNotifyQueryAccountField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:278</i>
     */
    @Virtual(92)
    public void OnRtnQueryBankBalanceByFuture(Pointer<CThostFtdcNotifyQueryAccountField> pNotifyQueryAccount) {
        System.out.println("<-----OnRtnQueryBankBalanceByFuture");
        BridjUtils.printObFields(pNotifyQueryAccount);
        this.proxy.onRtnQueryBankBalanceByFuture(pNotifyQueryAccount);
    }

    /**
     * Original signature : <code>void OnErrRtnBankToFutureByFuture(CThostFtdcReqTransferField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:281</i>
     */
    @Virtual(93)
    public void OnErrRtnBankToFutureByFuture(Pointer<CThostFtdcReqTransferField> pReqTransfer, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnBankToFutureByFuture");
    }

    /**
     * Original signature : <code>void OnErrRtnFutureToBankByFuture(CThostFtdcReqTransferField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:284</i>
     */
    @Virtual(94)
    public void OnErrRtnFutureToBankByFuture(Pointer<CThostFtdcReqTransferField> pReqTransfer, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnFutureToBankByFuture");
    }

    /**
     * Original signature : <code>void OnErrRtnRepealBankToFutureByFutureManual(CThostFtdcReqRepealField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:287</i>
     */
    @Virtual(95)
    public void OnErrRtnRepealBankToFutureByFutureManual(Pointer<CThostFtdcReqRepealField> pReqRepeal, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnRepealBankToFutureByFutureManual");
    }

    /**
     * Original signature : <code>void OnErrRtnRepealFutureToBankByFutureManual(CThostFtdcReqRepealField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:290</i>
     */
    @Virtual(96)
    public void OnErrRtnRepealFutureToBankByFutureManual(Pointer<CThostFtdcReqRepealField> pReqRepeal, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnRepealFutureToBankByFutureManual");
    }

    /**
     * Original signature : <code>void OnErrRtnQueryBankBalanceByFuture(CThostFtdcReqQueryAccountField*, CThostFtdcRspInfoField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:293</i>
     */
    @Virtual(97)
    public void OnErrRtnQueryBankBalanceByFuture(Pointer<CThostFtdcReqQueryAccountField> pReqQueryAccount, Pointer<CThostFtdcRspInfoField> pRspInfo) {
        System.out.println("<-----OnErrRtnQueryBankBalanceByFuture");
        BridjUtils.printObFields(pRspInfo);
        BridjUtils.printObFields(pReqQueryAccount);
    }

    /**
     * Original signature : <code>void OnRtnRepealFromBankToFutureByFuture(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:296</i>
     */
    @Virtual(98)
    public void OnRtnRepealFromBankToFutureByFuture(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromBankToFutureByFuture");
    }

    /**
     * Original signature : <code>void OnRtnRepealFromFutureToBankByFuture(CThostFtdcRspRepealField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:299</i>
     */
    @Virtual(99)
    public void OnRtnRepealFromFutureToBankByFuture(Pointer<CThostFtdcRspRepealField> pRspRepeal) {
        System.out.println("<-----OnRtnRepealFromFutureToBankByFuture");
    }

    /**
     * Original signature : <code>void OnRspFromBankToFutureByFuture(CThostFtdcReqTransferField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:302</i>
     */
    @Virtual(100)
    public void OnRspFromBankToFutureByFuture(Pointer<CThostFtdcReqTransferField> pReqTransfer, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspFromBankToFutureByFuture");
        if(pRspInfo==null)  {
            System.err.println("银期转账（OnRspFromBankToFutureByFuture）时错误信息（pRspInfo）为空");
            return;
        }
//        proxy.writeLogToDb(pReqTransfer,nRequestID,"OnRspFromBankToFutureByFuture");
//        proxy.writeLogToDb(pRspInfo,nRequestID,"OnRspFromBankToFutureByFuture");
    }

    /**
     * Original signature : <code>void OnRspFromFutureToBankByFuture(CThostFtdcReqTransferField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:305</i>
     */
    @Virtual(101)
    public void OnRspFromFutureToBankByFuture(Pointer<CThostFtdcReqTransferField> pReqTransfer, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspFromFutureToBankByFuture");
        if(pRspInfo==null)  {
            System.err.println("银期转账（OnRspFromFutureToBankByFuture）时错误信息（pRspInfo）为空");
            return;
        }
//        CTPToBankFuturesTransferAdapter.onRspFromFutureToBankByFuture(pRspInfo.get(),this.proxy.iClientNode);
//        proxy.writeLogToDb(pReqTransfer,nRequestID,"OnRspFromBankToFutureByFuture");
//        proxy.writeLogToDb(pRspInfo,nRequestID,"OnRspFromFutureToBankByFuture");
    }

    /**
     * Original signature : <code>void OnRspQueryBankAccountMoneyByFuture(CThostFtdcReqQueryAccountField*, CThostFtdcRspInfoField*, int, bool)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:308</i>
     */
    @Virtual(102)
    public void OnRspQueryBankAccountMoneyByFuture(Pointer<CThostFtdcReqQueryAccountField> pReqQueryAccount, Pointer<CThostFtdcRspInfoField> pRspInfo, int nRequestID, boolean bIsLast) {
        System.out.println("<-----OnRspQueryBankAccountMoneyByFuture");
        /*处理错误的信息在 pRspInfo中 ，比如 银行密码错误等信息*/
        this.proxy.onRspQueryBankAccountMoneyByFuture(pRspInfo);

    }

    /**
     * Original signature : <code>void OnRtnOpenAccountByBank(CThostFtdcOpenAccountField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:311</i>
     */
    @Virtual(103)
    public void OnRtnOpenAccountByBank(Pointer<CThostFtdcOpenAccountField> pOpenAccount) {
        System.out.println("<-----OnRtnOpenAccountByBank");
    }

    /**
     * Original signature : <code>void OnRtnCancelAccountByBank(CThostFtdcCancelAccountField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:314</i>
     */
    @Virtual(104)
    public void OnRtnCancelAccountByBank(Pointer<CThostFtdcCancelAccountField> pCancelAccount) {
        System.out.println("<-----OnRtnCancelAccountByBank");
    }

    /**
     * Original signature : <code>void OnRtnChangeAccountByBank(CThostFtdcChangeAccountField*)</code><br>
     * <i>native declaration : src\main\resources\lib\headfile\ThostFtdcTraderApi.h:317</i>
     */
    @Virtual(105)
    public void OnRtnChangeAccountByBank(Pointer<CThostFtdcChangeAccountField> pChangeAccount) {
        System.out.println("<-----OnRtnChangeAccountByBank");
    }

}
