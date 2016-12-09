package com.whyse.md.server.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.bridj.Pointer;

import com.whyse.lib.md.CThostFtdcDepthMarketDataField;
import com.whyse.myLiangHua.real.MDLHClientMngImpl;
import com.whyse.myLiangHua.test.MDLHHelper;

public class MarketDataHelp {
	/**
	 * 以InstrumentID为key,里面存放的是最新的行情数据，不停覆盖 后续异步的所有线程往这边读数据，这边放入是同步的
	 */
	static public volatile Map<String, Map<String, Object>> mapMD = new HashMap<>(700);
//	public static ExecutorService executorService = Executors.newFixedThreadPool(50);
	// ---blockingQueueMeth跟map里面数据一样，为了效率不使用blockingQueueMeth.contain---------
	public static LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(
			10);// 应该是600，看看会不会溢出任务
	/**
	 * 任务队列里面需要处理的行情更新事件,queue的辅助  ConcHashMapurrentHashMap,HashMap
	 */
	public static volatile HashMap<String, Boolean> mapMDWorker = new HashMap<>(600);

	// -------------------------------------------------------------

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 为了增加效率，单独写一个获取数据的函数
	 * 
	 * @param pDepthMarketData
	 * @return author:xumin 2016-10-8 下午5:41:06
	 */
	public static Map<String, Object> getDepthMarketData(
			Pointer<CThostFtdcDepthMarketDataField> pDepthMarketData) {
		CThostFtdcDepthMarketDataField data = pDepthMarketData.get();
		Map<String, Object> map = new HashMap<>(10);
		Double LastPrice = data.LastPrice();// 最新价
		map.put("LastPrice", LastPrice);
		int Volume = data.Volume();// 数量
		map.put("Volume", Volume);
		String InstrumentID = data.InstrumentID().getCString();// 合约代码
		map.put("InstrumentID", InstrumentID);

		Double BidPrice1 = data.BidPrice1();// 申买价一
		map.put("BidPrice1", BidPrice1);
		Double BidPrice2 = data.BidPrice2();
		map.put("BidPrice2", BidPrice2);

		Double AskPrice1 = data.AskPrice1();// 申卖价一
		map.put("AskPrice1", AskPrice1);
		Double AskPrice2 = data.AskPrice2();
		map.put("AskPrice2", AskPrice2);

		mapMD.put(InstrumentID, map);// 更新行情数据
		return map;
	}
	/**
	 * 被多个账户的回调函数调用，多线程生产
	 * @param pDepthMarketData
	 * @return
	 * author:xumin 
	 * 2016-10-9 下午2:20:23
	 */
	public static int asyProduceDepthMarketData(
			Pointer<CThostFtdcDepthMarketDataField> pDepthMarketData) {

		Map<String, Object> mapRT = getDepthMarketData(pDepthMarketData);
		//不过每个线程产生的InstrumentID是循环有序的,不用锁也能保持同步
		String InstrumentID = mapRT.get("InstrumentID").toString();
		if (mapMDWorker.containsKey(InstrumentID)) {
//			System.err.println("队列还有未执行，阻挡:" + InstrumentID);
			return -1;
		} else {
			mapMDWorker.put(InstrumentID, true);
			if (!blockingQueue.offer(InstrumentID)) {
				System.err.println("blockingQueueMeth队列满:");
				mapMDWorker.remove(InstrumentID);// 防止溢出的消息永远保留在里面
			}
		}
		return 0;
	}

	/**
	 * 这个函数没调用多次就会出现多个行情消费者。
	 * 本次程序为了个人量化交易开发，只做一个品种，一个消费线程
	 * @param name
	 * author:xumin 
	 * 2016-11-16 下午4:13:21
	 */
	public static void newConsumer(String name) {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						String item = blockingQueue.take();
						Map<String, Object> mapData = mapMD.get(item);
						mapMDWorker.remove(item);// 从拿到最新数据开始，算作这次已经处理
						// TODO---拿到数据的线程需要做的事情----------
//						MDLHHelper.optData(mapData);
						MDLHClientMngImpl.optData(mapData,item);
//						System.out.println("消费数据:"+item);
//						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread td = new Thread(run);
		td.setName(name);
		td.start();
	}

}
