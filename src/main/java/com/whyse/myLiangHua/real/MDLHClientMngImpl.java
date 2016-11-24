package com.whyse.myLiangHua.real;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.whyse.lib.md.CThostFtdcReqUserLoginField;
import com.whyse.lib.md.MdLibrary.THOST_TE_RESUME_TYPE;
import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.md.server.impl.MdMngServiceImpl;
import com.whyse.md.server.impl.MdServiceImpl;

public class MDLHClientMngImpl {

	public static List<String> listSym = new ArrayList<>(10);
	public static Map<String, MDLHClientImpl> mapClient = new HashMap<>(10);
	/**
	 * 量化多空，只做一个品种
	 */
//	public static String mainSym = "p1701";//棕榈
//	public static String mainSym = "JM1701";//焦煤
//	public static String mainSym = "RB1701";//螺纹
	static{
		listSym.add("p1701");//大商
		listSym.add("jm1701");//大商
		listSym.add("rb1701");//上期
//		listSym.add(mainSym);
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
		//---------------------------
		for(String sym :listSym){
			mapClient.put(sym, new MDLHClientImpl(sym));
		}
		//-------------------------------
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
		
		TimeWorkerReal.initCYTimeWorker1();
		TimeWorkerReal.savePerHour();
	}

	/**
	 * 只管存最新的5挡行情
	 * @param mapData
	 * author:xumin 
	 * 2016-11-16 下午4:20:55
	 * @param sym 
	 */
	@SuppressWarnings("static-access")
	public static void optData(Map<String, Object> mapData, String sym) {
		MDLHClientImpl item = mapClient.get(sym);
		item.LastPrice = (Double) mapData.get("LastPrice");
		item.LastMdMap = mapData;
		item.lastPriceInQueue(item.que5,item.LastPrice);//最新数据入队列
	}


}
