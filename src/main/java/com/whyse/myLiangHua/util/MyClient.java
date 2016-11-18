package com.whyse.myLiangHua.util;

import java.util.Map;

import org.bridj.Pointer;


import com.whyse.lib.trader.CThostFtdcTradeField;
import com.whyse.main.trader.BridjUtils;

public class MyClient {

	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-18 下午5:17:49
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void OnRtnTrade(Pointer<CThostFtdcTradeField> pTrade) {
		// TODO Auto-generated method stub
		Map<String, Object> map = BridjUtils.getMapByPoint(pTrade);
	}

}
