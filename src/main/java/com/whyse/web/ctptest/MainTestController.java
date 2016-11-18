//package com.whyse.web.ctptest;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.whyse.main.MainTest;
//import com.whyse.main.selfmodel.LoginBean;
//import com.whyse.main.trader.BridjUtils;
//import com.whyse.main.trader.TraderHelper;
//import com.whyse.main.trader.server.TradeService;
//import com.whyse.main.trader.server.impl.TradeMngServiceImpl;
//import com.whyse.main.trader.server.TradeMngService;;
//
//
//@Controller
//public class MainTestController {
//	
//	public static TradeService tradeService;
//	TradeMngService tradeMngService;
//	public static ExecutorService executorService = Executors.newFixedThreadPool(10);
//	
//	@PostConstruct
//	public void init(){
//		MainTest.initNativeLibrary();
//		
//		final String localFilePath = "c:/ctpfile/";
//		LoginBean.setLocalFilePath(localFilePath);
//		
//		tradeMngService = TradeMngServiceImpl.getInstance();
//		
//		System.err.println("============================================================");
//	}
//	@RequestMapping("/getTradingDay.do")
//	@ResponseBody
//	public Object getTradingDay(){
//		tradeService.getTradingDay();
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/release.do")
//	@ResponseBody
//	public int release(String userId){
//		tradeService.release();
//		return 0;
//	}
//	@RequestMapping("/start.do")
//	@ResponseBody
//	public int start(String userId){
//		tradeService = MainTest.testMoreAcc(tradeMngService);
//		return 0;
//	}
//	@RequestMapping("/exchangeId.do")
//	@ResponseBody
//	public int reinit(String userId){
//		//-----------------------------
//		String key = userId;
//		tradeService = tradeMngService.getTradeServiceBykey(key);
//		if(tradeService!=null)
//			return 222;
//		return 0;
//	}
//	//========合约保证金&手续费&查询&报单手续费=========================================
//	@RequestMapping("/doReqQueryMaxOrderVolume.do")
//	@ResponseBody
//	public Object doReqQueryMaxOrderVolume(String id){
//		//查询最大报单数量请求
//		tradeService.doReqQueryMaxOrderVolume(id);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryInstrument.do")
//	@ResponseBody
//	public Object doReqQryInstrument(String id){
//		//合约查询
//		tradeService.doReqQryInstrument(id);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryInstrumentMarginRate.do")
//	@ResponseBody
//	public Object doReqQryInstrumentMarginRate(String id){
//		//合约保证金率
//		tradeService.doReqQryInstrumentMarginRate(id);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryInstrumentCommissionRate.do")
//	@ResponseBody
//	public Object doReqQryInstrumentCommissionRate(String id){
//		//合约手续费率
//		tradeService.doReqQryInstrumentCommissionRate(id);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryInstrumentOrderCommRate.do")
//	@ResponseBody
//	public Object doReqQryInstrumentOrderCommRate(String id){
//		//报单手续费详情
//		tradeService.doReqQryInstrumentOrderCommRate(id);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	//=====================================================================
//	/**
//	 * 登录之前做客户端认证
//	 * 正式环境下需要
//	 * @return
//	 * author:xumin 
//	 * 2016-4-27 下午7:06:49
//	 */
//	@RequestMapping("/doAuthenticate.do")
//	@ResponseBody
//	public int doAuthenticate(){
//		return tradeService.doAuthenticate();
//	}
//	@RequestMapping("/doLogin.do")
//	@ResponseBody
//	public Object doLogin(){
//		int flag = tradeService.doLogin();
////		test();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	private void test() {
//		Runnable run = new Runnable() {
//			
//			@Override
//			public void run() {
//				doReqQryTradingAccount();
//			}
//		};
//		executorService.execute(run);
//		run = new Runnable() {
//			
//			@Override
//			public void run() {
//				doReqQryInvestorPosition();
//			}
//		};
//		executorService.execute(run);
//		//------------
//		run = new Runnable() {
//			
//			@Override
//			public void run() {
//				doReqQryOrder(null);
//			}
//		};
//		executorService.execute(run);
//	}
//	@RequestMapping("/doLogout.do")
//	@ResponseBody
//	public Object doLogout(){
//		tradeService.doLogout();
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryInvestorPosition.do")
//	@ResponseBody
//	public Object doReqQryInvestorPosition(){
//		//查询持仓
//		tradeService.doReqQryInvestorPosition();
//		try {
//			Thread.sleep(1500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.listMap;
//	}
//	@RequestMapping("/doReqQryInvestorPositionDetail.do")
//	@ResponseBody
//	public Object doReqQryInvestorPositionDetail(){
//		//查询持仓明细
//		tradeService.doReqQryInvestorPositionDetail();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	
//	@RequestMapping("/doReqQryTradingAccount.do")
//	@ResponseBody
//	public Object doReqQryTradingAccount(){
//		//查询投资者最新的资金状况。如保证金，手续费，持仓盈利，可用资金等。
//		tradeService.doReqQryTradingAccount();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	//-------------------------------
//	@RequestMapping("/doReqQrySettlementInfoConfirm.do")
//	@ResponseBody
//	public Object doReqQrySettlementInfoConfirm(){
//		int flag = tradeService.doReqQrySettlementInfoConfirm();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQrySettlementInfo.do")
//	@ResponseBody
//	public Object doReqQrySettlementInfo(String day){
//		tradeService.doReqQrySettlementInfo(day);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqSettlementInfoConfirm.do")
//	@ResponseBody
//	public Object doReqSettlementInfoConfirm(){
//		tradeService.doReqSettlementInfoConfirm();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	
//	//=====================下面时候报单=====================================
//	@RequestMapping("/doReqOrderInsert.do")
//	@ResponseBody
//	public Object doReqOrderInsert(String instrumentId){
//		int flag = tradeService.doReqOrderInsert(instrumentId);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryOrder.do")
//	@ResponseBody
//	public Object doReqQryOrder(String instrumentId){
//		int flag = tradeService.doReqQryOrder(instrumentId);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryTrade.do")
//	@ResponseBody
//	public Object doReqQryTrade(){
//		int flag = tradeService.doReqQryTrade();
//		try {
//			Thread.sleep(1600);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.listMap;
//	}
//	@RequestMapping("/doReqOrderAction.do")
//	@ResponseBody
//	public Object doReqOrderAction(String id){
//		Map<String, Object>  map = new HashMap<String, Object>(5);
//		if(id!=null)
//			map.put("id", id);
//		else
//			map.put("id", TraderHelper.getNextOrderRef());
//		int flag = tradeService.doReqOrderAction(map);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	//-----------------------------------------------
//	@RequestMapping("/doReqForQuoteInsert.do")
//	@ResponseBody
//	public Object doReqForQuoteInsert(){
//		int flag = tradeService.doReqForQuoteInsert();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryDepthMarketData.do")
//	@ResponseBody
//	public Object doReqQryDepthMarketData(){
//		int flag = tradeService.doReqQryDepthMarketData();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	
//	//==========银期转账相关=====================================
//	/*
//	 * 招商银行，获取name
//	 */
//	@RequestMapping("/doReqQryContractBank.do")
//	@ResponseBody
//	public Object doReqQryContractBank(){
//		Map<String, Object> map = tradeService.doReqQryContractBank();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	/*
//	 * 查询账号绑定的银行账号相关
//	 */
//	@RequestMapping("/doReqQryAccountregister.do")
//	@ResponseBody
//	public Object doReqQryAccountregister(){
//		Map<String, Object> map = tradeService.doReqQryAccountregister();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryTransferBank.do")
//	@ResponseBody
//	public Object doReqQryTransferBank(){
//		Map<String, Object> map = tradeService.doReqQryTransferBank();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqQryTransferSerial.do")
//	@ResponseBody
//	public Object doReqQryTransferSerial(){
//		Map<String, Object> map = tradeService.doReqQryTransferSerial();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	//查询余额
//	@RequestMapping("/doReqQueryBankAccountMoneyByFuture.do")
//	@ResponseBody
//	public Object doReqQueryBankAccountMoneyByFuture(){
//		Map<String, Object> map = tradeService.doReqQueryBankAccountMoneyByFuture();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqFromBankToFutureByFuture.do")
//	@ResponseBody
//	public Object doReqFromBankToFutureByFuture(){
//		Map<String, Object> map = tradeService.doReqFromBankToFutureByFuture();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	@RequestMapping("/doReqFromFutureToBankByFuture.do")
//	@ResponseBody
//	public Object doReqFromFutureToBankByFuture(){
//		Map<String, Object> map = tradeService.doReqFromFutureToBankByFuture();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		return BridjUtils.mapTar;
//	}
//	
//	
//}
