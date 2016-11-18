package com.whyse.main.util;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;



/**
 * 处理多个包断续发过来，组合成字符窜的问题
 * 
 * author:xumin 
 * 2016-5-9 上午9:54:48
 */
public class MsgCenterHelper {

	static ConcurrentHashMap<Integer, List>  mapBt2Str = new ConcurrentHashMap<>(500);
	/**
	 * 跨越线程的请求id查询存储
	 */
	static ConcurrentHashMap<Integer, Boolean>  mapReqId = new ConcurrentHashMap<Integer, Boolean>(100);
	static ReentrantLock reentrantLockMsg = new ReentrantLock();//
	static byte stopS = '\0';//C语言数组结束符
	/**
	 * @param args
	 * author:xumin 
	 * 2016-5-6 下午5:48:13
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	/**
	 * 同一个key，肯定都是线程线性的，
	 * @param bs
	 * @param key
	 * author:xumin 
	 * 2016-5-24 下午5:13:41
	 */
	public static void putMsgIn(byte[] bs,int key) {
		List list = mapBt2Str.get(key);
		if(list==null){
			list = new ArrayList<>(10);
			mapBt2Str.put(key, list);
		}
		list.add(bs);
	}
	/**
	 * 夸线程的缓存队列。常用于多个返回的合并暂存
	 * 注意：同一个key，必须同步插入
	 * @param key
	 * @param item
	 * author:xumin 
	 * 2016-5-24 下午4:58:24
	 */
	public static void putObjectTemp(int key, Object item) {
		List list = mapBt2Str.get(key);
		if(list==null){
			list = new ArrayList<>(10);
			mapBt2Str.put(key, list);
		}
		list.add(item);
	}
	public static List<?> getAndCleanObjectTemp(int key) {
		List<?> list = mapBt2Str.get(key);
		mapBt2Str.remove(key);
		return list;
	}
	/**
	 * 获取组合文字
	 * @param pSettlementInfo 
	 * @param nRequestID
	 * author:xumin 
	 * 2016-5-6 下午6:57:58
	 */
	public static String printQrySettlementInfo(int key) {
		String strTar=null;
		try{
			List<byte[] > list = (List<byte[] >)mapBt2Str.get(key);
			if(list==null)
				return null;
			int lengths = 0;
			for(byte[]  item : list){
				lengths = lengths+item.length;
			}
			List<Byte> listBt = new ArrayList<Byte>(lengths);
			for(byte[]  item : list){
				for(int i=0;i<item.length;i++){
					//最后一个是数组的结束符号， 
					if(item[i]!=stopS){
						listBt.add(item[i]);
					}else{
						break;
					}
				}
			}
			byte[] tar = new byte[listBt.size()];
			for(int i=0;i<listBt.size();i++){
				tar[i] = listBt.get(i);
			}
			strTar = new String(tar);
//			BridjUtils.mapTar = new HashMap<String, Object>(2);
//			BridjUtils.mapTar.put("content", strTar);
		}finally{
			mapBt2Str.remove(key);
		}
		return strTar;
	}
	public static void putOverThreadId(int nRequestID) {
		mapReqId.put(nRequestID, true);
	}
	public static boolean isOverThreadId(int nRequestID) {
		return mapReqId.containsKey(nRequestID);
	}
	public static void clearOverThreadId(int nRequestID) {
		mapReqId.remove(nRequestID);
	}


}
