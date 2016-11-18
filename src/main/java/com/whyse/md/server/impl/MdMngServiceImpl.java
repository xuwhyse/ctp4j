package com.whyse.md.server.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whyse.main.selfmodel.LoginMdBean;
import com.whyse.md.TestMd;

public class MdMngServiceImpl {

	static Map<String, MdServiceImpl>  mapService = new HashMap<>(7);
	static {
		TestMd.initNativeLibrary();
		//生产者是行情订阅回调接口那边
		MarketDataHelp.newConsumer("consumer_MD_1");
//		MarketDataHelp.newConsumer("consumer_MD_2");
	}
	//===============================================================================
	private MdMngServiceImpl(){
		
	}
	/**
	 * @param args
	 * author:xumin 
	 * 2016-10-9 下午3:00:07
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 
	 * @param loginBean
	 * @param listSub  这个账号需要订阅的品种id,区分大小写
	 * @return
	 * author:xumin 
	 * 2016-10-9 下午3:19:25
	 */
	public static MdServiceImpl newMDService(LoginMdBean loginMdBean, List<String> listSub) {
		MdServiceImpl mdServiceImpl = new MdServiceImpl();
		
		String brokerId = loginMdBean.getReq().BrokerID().getCString();
		String userId = loginMdBean.getReq().UserID().getCString();
		String key = brokerId+userId;
		mapService.put(key, mdServiceImpl);
		mdServiceImpl.init(loginMdBean,listSub);
		
		return mdServiceImpl;
	}
	public static MdServiceImpl getMDService(String brokerId, String userId) {
		return mapService.get(brokerId+userId);
	}

}
