package com.whyse.main.trader.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;


/**
 * ctp的相关解析
 * 
 * author:xumin 
 * 2016-5-25 下午3:31:47
 */
public class CTPHelper {

	static Map<String, List<String>>  mapUrl = new HashMap<String, List<String>>(10);
	/**
	 * 产品标识：FDTTrader
	        认证码  ：20160818FDTT0002
	 */
	public static Map<String, Map<String, Object>>  mapTag = new HashMap<String, Map<String, Object>>(10);
	/**
	 * brokerId为key
	 */
	public static Map<String, Map<String, Object>> MapBrokerInfo = new HashMap<String, Map<String,Object>>(10);
	static{
		
		List<String> list2071 = new ArrayList<String>(5);
		list2071.add("tcp://180.169.77.111:42205");
		mapUrl.put("2071", list2071);
		
		List<String> list9999 = new ArrayList<String>(5);
		list9999.add("tcp://180.168.146.187:10000");
		mapUrl.put("9999", list9999);
		
		List<String> list7090 = new ArrayList<String>(5);
		list7090.add("tcp://180.169.75.19:41205");
		list7090.add("tcp://180.169.75.21:41205");
		mapUrl.put("7090", list7090);
		
		//============================================
		Map<String, Object> item = new HashMap<String, Object>(3);
		item.put("code", "20160818FDTT0002");
		item.put("name", "FDTTrader");
		mapTag.put("2071", item);
	}
	/**
	 * xumin  2015-7-15 下午4:00:17
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Object> item = new HashMap<String, Object>(3);
		item.put("code", "20160818FDTT0002");
		item.put("name", "FDTTrader");
		System.err.println(JSON.toJSONString(item));
	}
	public static boolean printRoAndTurn(int flag, String string) {
		if(flag==0){
			System.err.println(string+"  成功");
			return true;
		}
		if(flag==-1){
			System.err.println(string+"  网络连接失败");
		}
		if(flag==-2){
			System.err.println(string+"  未处理请求超过许可数");
		}
		if(flag==-3){
			System.err.println(string+"  每秒发送请求数超过许可数");
		}
		return false;
	}

	public static List<String> getFrontUrl(String m_brokerID) {
		List<String> listurls = mapUrl.get(m_brokerID);
		if(listurls==null || listurls.isEmpty())
			System.err.println(m_brokerID+"暂不支持该券商,或者没有配置前端机！！");
		return listurls;
	}
	/**
	 * 根据券商id获取数据库中配置的支持的券商
	 * @param brokerId
	 * @return
	 * author:xumin 
	 * 2016-7-12 上午10:51:43
	 */
	public static String getBrokerName(String brokerId) {
		Map<String, Object> map = MapBrokerInfo.get(brokerId);
		if(map==null){
			return "未知券商"+brokerId;
		}
		String brokerName = map.get("brokerName").toString();
		return brokerName;
	}
	/**
	 * 获取该券商配置的认证信息
	 * @param brokerId
	 * @return
	 * author:xumin 
	 * 2016-8-18 下午2:06:50
	 */
	public static Map<String, Object> getMapTag(String brokerId) {
		Map<String, Object> map = mapTag.get(brokerId);
		return map;
	}

}
