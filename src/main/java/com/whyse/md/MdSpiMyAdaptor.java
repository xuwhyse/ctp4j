package com.whyse.md;

import org.bridj.Pointer;
import org.bridj.ann.Virtual;

import com.whyse.lib.md.CThostFtdcDepthMarketDataField;
import com.whyse.lib.md.CThostFtdcForQuoteRspField;
import com.whyse.lib.md.CThostFtdcMdSpi;
import com.whyse.lib.md.CThostFtdcRspInfoField;
import com.whyse.lib.md.CThostFtdcRspUserLoginField;
import com.whyse.lib.md.CThostFtdcSpecificInstrumentField;
import com.whyse.lib.md.CThostFtdcUserLogoutField;
import com.whyse.main.trader.BridjUtils;
import com.whyse.md.server.impl.MarketDataHelp;
import com.whyse.md.server.impl.MdServiceImpl;

public class MdSpiMyAdaptor extends CThostFtdcMdSpi{
	
	public MdServiceImpl mdService;
	
	/**
	 * Original signature : <code> void OnFrontConnected()</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:2</i>
	 */
	@Virtual(0) 
	public   void OnFrontConnected() {
		System.out.println("<-----OnFrontConnected");
		mdService.ReqUserLogin();
	}
	/**
	 * Original signature : <code> void OnFrontDisconnected(int)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:5</i>
	 */
	@Virtual(1) 
	public   void OnFrontDisconnected(int nReason) {
		System.out.println("<-----OnFrontDisconnected");
	}
	/**
	 * Original signature : <code> void OnHeartBeatWarning(int)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:8</i>
	 */
	@Virtual(2) 
	public   void OnHeartBeatWarning(int nTimeLapse) {
		System.out.println("<-----OnHeartBeatWarning");
	}
	/**
	 * Original signature : <code> void OnRspUserLogin(CThostFtdcRspUserLoginField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:11</i>
	 */
	@Virtual(3) 
	public  void OnRspUserLogin(Pointer<CThostFtdcRspUserLoginField > pRspUserLogin, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspUserLogin");
		BridjUtils.printObFields(pRspUserLogin);
		BridjUtils.printObFields(pRspInfo);
		mdService.OnRspUserLogin(pRspUserLogin,pRspInfo,bIsLast);
	}
	/**
	 * Original signature : <code> void OnRspUserLogout(CThostFtdcUserLogoutField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:14</i>
	 */
	@Virtual(4) 
	public  void OnRspUserLogout(Pointer<CThostFtdcUserLogoutField > pUserLogout, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspUserLogout");
	}
	/**
	 * Original signature : <code> void OnRspError(CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:17</i>
	 */
	@Virtual(5) 
	public  void OnRspError(Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspError");
	}
	/**
	 * Original signature : <code> void OnRspSubMarketData(CThostFtdcSpecificInstrumentField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:20</i>
	 */
	@Virtual(6) 
	public  void OnRspSubMarketData(Pointer<CThostFtdcSpecificInstrumentField > pSpecificInstrument, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspSubMarketData");
		BridjUtils.printObFields(pSpecificInstrument);
	}
	/**
	 * Original signature : <code> void OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:23</i>
	 */
	@Virtual(7) 
	public  void OnRspUnSubMarketData(Pointer<CThostFtdcSpecificInstrumentField > pSpecificInstrument, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspUnSubMarketData");
		BridjUtils.printObFields(pSpecificInstrument);
	}
	/**
	 * Original signature : <code> void OnRspSubForQuoteRsp(CThostFtdcSpecificInstrumentField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:26</i>
	 */
	@Virtual(8) 
	public  void OnRspSubForQuoteRsp(Pointer<CThostFtdcSpecificInstrumentField > pSpecificInstrument, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspSubForQuoteRsp");
		BridjUtils.printObFields(pSpecificInstrument);
		BridjUtils.printObFields(pRspInfo);
	}
	/**
	 * Original signature : <code> void OnRspUnSubForQuoteRsp(CThostFtdcSpecificInstrumentField*, CThostFtdcRspInfoField*, int, bool)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:29</i>
	 */
	@Virtual(9) 
	public  void OnRspUnSubForQuoteRsp(Pointer<CThostFtdcSpecificInstrumentField > pSpecificInstrument, Pointer<CThostFtdcRspInfoField > pRspInfo, int nRequestID, boolean bIsLast) {
		System.out.println("<-----OnRspUnSubForQuoteRsp");
		BridjUtils.printObFields(pSpecificInstrument);
	}
	/**
	 * 同步接口，N中品种都会从这边过来
	 */
	@Virtual(10) 
	public  void OnRtnDepthMarketData(Pointer<CThostFtdcDepthMarketDataField > pDepthMarketData) {
//		System.out.println("<-----OnRtnDepthMarketData");
//		mdService.OnRtnDepthMarketData(pDepthMarketData);
		//------------------------------------------------
		BridjUtils.printObFields(pDepthMarketData);
		/*讲指针中数据拷贝到一个结构体后，异步返回
		 * 结构体数据根据InstrumentID存到linkedBlockingQueue,消费者线程一检测到就工作
		 * 发现同样的InstrumentID，覆盖map中的值
		 * 上面轮到处理的InstrumentID，值从map中获取，这样可以拿到最新的
		 * 如果发现linkedBlockingQueue有同样的InstrumentID，丢弃。  不过值是放到最新的map中去
		 */
		int flag = MarketDataHelp.asyProduceDepthMarketData(pDepthMarketData);
		//------------------------------------------------
	}
	/**
	 * Original signature : <code> void OnRtnForQuoteRsp(CThostFtdcForQuoteRspField*)</code><br>
	 * <i> declaration : src\main\resources\lib\headfile\ThostFtdcMdApi.h:35</i>
	 */
	@Virtual(11) 
	public  void OnRtnForQuoteRsp(Pointer<CThostFtdcForQuoteRspField > pForQuoteRsp) {
		System.out.println("<-----OnRtnForQuoteRsp");
		BridjUtils.printObFields(pForQuoteRsp);
	}

}
