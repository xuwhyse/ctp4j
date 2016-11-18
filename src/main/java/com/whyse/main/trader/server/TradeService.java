package com.whyse.main.trader.server;


import java.util.Map;

import org.bridj.Pointer;

import com.whyse.lib.trader.CThostFtdcInputOrderActionField;
import com.whyse.lib.trader.CThostFtdcInputOrderField;



public interface TradeService {
	void release(boolean isNow);
	int doAuthenticate();
	/**
	 * 需要开线程去运行traderApi.Init();
		traderApi.Join();
	 * @param localFilePath
	 * author:xumin 
	 * 2016-4-22 下午4:36:43
	 */
	void init();

	int doLogin();
	/**
	 * 先OnFrontDisconnected  再OnFrontConnected重新登录一遍
	 * 注意：目前，通过ReqUserLogout 登出系统的话，会先将现有的连接断开，再重新建立一个新的连接，重新
	登录后SessionID 会重置，因此MaxOrderRef 一般也会重新从0 计数。
	 * author:xumin 
	 * 2016-4-22 下午5:19:50
	 */
	int doLogout();
	/**
	 * 先执行，看用户是否有过结算 OnRspQrySettlementInfoConfirm
	 * @return
	 * author:xumin 
	 * 2016-4-26 下午8:03:50
	 */
	int doReqQrySettlementInfoConfirm();
	/**
	 * 请求查询结算单  OnRspQrySettlementInfo
	 * @param ob
	 * author:xumin 
	 * 2016-4-22 下午8:05:30
	 */
	int doReqQrySettlementInfo(String date);
	/**
	 * 结算单确认  OnRspSettlementInfoConfirm
	 * 之后才能交易
	 * @param ob
	 * author:xumin 
	 * 2016-4-22 下午9:21:22
	 */
	int doReqSettlementInfoConfirm();
	
	/**
	 * 请求报单，对应于上一节中的第1 步。 (端登成功,发出报单录入请求)
	 * 如果正确不会马上收到OnRspOrderInsert
	 * OnRspOrderInsert(ctp错误),OnRtnOrder,OnErrRtnOrderInsert（交易所错误）,OnRtnTrade
	 * @param req 
	 * @return
	 * author:xumin 
	 * 2016-4-26 下午8:11:00
	 */
	int doReqOrderInsert(CThostFtdcInputOrderField req);
	/**
	 * 注意：目前只支持撤单
	 * 客户端发出报单操作请求，包括报单的撤销、报单的挂起、报单的激活、报单的修改。
	 * 撤单操作需要对应可以定位该报单的序列号。上一节最后介绍的三组报单序列号都可以用来撤单。
	 * @param req 
	 * @return
	 * author:xumin 
	 * 2016-4-27 下午4:20:43
	 */
	int doReqOrderAction(CThostFtdcInputOrderActionField req);
	/**
	 * 投资者询价
	 * 传入询价的合约以及询价的引用即可
	 * OnRspForQuoteInsert 介意核心认为指令不合法时调用，返回错误信息。 合法就不会有返回
	 * @return
	 * author:xumin 
	 * 2016-4-28 下午3:11:02
	 */
	int doReqForQuoteInsert();
	/**
	 * 查询投资者持仓明细
	 * 需要传入持仓代码，所以是针对某个合约的
	 * @return
	 * author:xumin 
	 * 2016-4-28 下午7:35:09
	 */
	int doReqQryInvestorPosition(String exchangeInstID);
	/**
	 * 查询投资者最新的资金状况。如保证金，手续费，持仓盈利，可用资金等。
	 * 需要投资者代码+币种代码+经纪公司代码
	 * @return
	 * author:xumin 
	 * 2016-4-28 下午8:47:39
	 */
	int doReqQryTradingAccount(Map<String, Object> mapReq);
	/**
	 * 请求查询行情
	 * 需要合约代码
	 * @return
	 * author:xumin 
	 * 2016-4-29 上午10:54:54
	 */
	int doReqQryDepthMarketData();
	/**
	 * 请求查询报单
	 * @param instrumentId
	 * @return
	 * author:xumin 
	 * 2016-5-6 下午12:06:10
	 */
	int doReqQryOrder(String instrumentId);
	/**
	 * 
	 * @return
	 * author:xumin 
	 * 2016-5-6 下午2:25:05
	 */
	int doReqQryInvestorPositionDetail(String instrumentID);
	
	/**
	 * 请求查询转帐银行
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午10:57:54
	 */
	Map<String, Object> doReqQryTransferBank();
	/**
	 * 请求查询签约银行
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:41:56
	 */
	Map<String, Object> doReqQryContractBank();
	/**
	 * 请求查询银期签约关系
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:42:48
	 */
	Map<String, Object> doReqQryAccountregister();
	/**
	 * 请求查询转帐流水
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:43:32
	 */
	Map<String, Object> doReqQryTransferSerial();
	/**
	 * 期货发起查询银行余额请求
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:46:05
	 */
	Map<String, Object> doReqQueryBankAccountMoneyByFuture(String password, String bankPassword);
	/**
	 * 期货发起      银行资金转期货请求
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:46:59
	 */
	Map<String, Object> doReqFromBankToFutureByFuture(String password,Double tradeAmount, String bankPassword);
	/**
	 * 期货发起     期货资金转银行请求
	 * @return
	 * author:xumin 
	 * 2016-5-9 上午11:47:56
	 */
	Map<String, Object> doReqFromFutureToBankByFuture(String password,Double tradeAmount, String bankPassword);

	/**
	 * 查询银期绑定关系存管银行
	 */
	void doReqQryBindBank();
	/**
	 * 查询成交记录
	 * @return
	 * author:xumin 
	 * 2016-6-21 下午8:55:19
	 */
	int doReqQryTrade(Map<String, Object> mapReq);
	/**
	 * 每天夜盘开始清除相关状态
	 * 
	 * author:xumin 
	 * 2016-6-28 下午4:25:37
	 */
	void cleanStateForNextDay();
//	int writeLogToDb(Pointer ob,int seq,String behavior);
	/**
	 * 每手保证金查询
	 * @param instrumentID  商品代碼
	 * @return
	 * author:xumin 
	 * 2016-8-25 上午10:46:12
	 */
	int doReqQryInstrumentMargin(String instrumentID);
	/**
	 * 最大可买数量查询
	 * @param instrumentID  商品代碼
	 * @param direction  买卖方向0：买 1卖
	 * @param offsetFlag  开平标志0：开 1：平 3：平今 4：平昨
	 * @return
	 * author:xumin 
	 * 2016-8-25 上午10:46:37
	 */
	int doReqQueryMaxOrderVolume(String instrumentID, int direction,
			int offsetFlag);

}
