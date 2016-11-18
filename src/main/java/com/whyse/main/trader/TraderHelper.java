package com.whyse.main.trader;

import java.io.UnsupportedEncodingException;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraderHelper {
	private final static Logger log = LoggerFactory.getLogger(TraderHelper.class);	
	
	public static String toGBKString(byte[] bytes) {
		try {
			String msg = new String(bytes, "GB2312");
			return msg.trim();
		} catch (UnsupportedEncodingException e) {
			log.info(e.getMessage());
		}
		return "";
	}
	
	public static String toGBKString2(byte[] bytes) {
		try {
			return new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			log.info(e.getMessage());
		}
		return "";
	}
	
	public static String genClOrderId(int front, int session, String orderRef) {		
		return "" + front + ":" + session + ":" + orderRef;
	}
	
	public static String genExchangeOrderId(String exchangeId, String ordSysId) {
		return exchangeId + ":" + ordSysId;
	}
	
	public static <T extends StructObject> T getStructObject(Pointer<T> field) {
		return field == null ? null : field.get();
	}

	public static String getNextOrderRef() {
		long orderRef = System.currentTimeMillis()/10;
		System.err.println("orderRef:"+orderRef);
		return String.valueOf(orderRef);
	}

}
