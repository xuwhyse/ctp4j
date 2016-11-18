package com.whyse.main.trader.server.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.whyse.main.selfmodel.LoginBean;
import com.whyse.main.trader.server.TradeMngService;
import com.whyse.main.trader.server.TradeService;

/**
 * 单例绝对,因为要统管资源，多个就会泄露资源
 * 
 * author:xumin 
 * 2016-5-10 下午3:49:56
 */
public class TradeMngServiceImpl implements TradeMngService{

//	public static ExecutorService executorService = Executors.newFixedThreadPool(5);
	
	private static TradeMngServiceImpl tradeMngServiceImpl;
	private static ReentrantLock newTradeMngServiceLock = new ReentrantLock();//
	private static Map<String, TradeService> mapTradeService = new ConcurrentHashMap<String, TradeService>(1000);
	
	private ReentrantLock newTradeServiceLock = new ReentrantLock();//
	
	private TradeMngServiceImpl(){
	}
	/**
	 * 安全的单例
	 * @return
	 * author:xumin 
	 * 2016-5-10 下午4:02:46
	 */
	public static TradeMngService getInstance() {
		if(tradeMngServiceImpl!=null){
			return tradeMngServiceImpl;
		}else{
			newTradeMngServiceLock.lock();
			try{
				if(tradeMngServiceImpl!=null){
					return tradeMngServiceImpl;
				}else{
					tradeMngServiceImpl = new TradeMngServiceImpl();
					return tradeMngServiceImpl;
				}
			}finally{
				newTradeMngServiceLock.unlock();
			}
		}
	}
	
	@Override
	public int newTradeService(LoginBean loginBean) {
		if(loginBean==null)
			return -1;
		String brokerId = loginBean.getReq().BrokerID().getCString();
		String userId = loginBean.getReq().UserID().getCString();
		String key = brokerId+userId;
		//--------------------------
		newTradeServiceLock.lock();
		try{
			if(mapTradeService.containsKey(key)){
				TradeServiceImpl item = (TradeServiceImpl) mapTradeService.get(key);
				if(item.state>0){
					System.err.println("登录手速太快了！");
					return 0;
				}
				else{
					System.err.println("上次登录还没完成，请稍等");
					return -3;//
				}
			}
			new TradeServiceImpl(loginBean);
		}finally{
			newTradeServiceLock.unlock();
		}
		//--------------------------
		final TradeService tradeServiceTar = mapTradeService.get(key);
		if(tradeServiceTar==null){
			System.err.println("并发错误，未知错误");
			return -2;
		}
		Runnable run = new Runnable() {
			
			public void run() {
				tradeServiceTar.init();
			}
		};
		Thread thread = new Thread(run);
		thread.start();
//		executorService.submit(run);
		return 0;
	}
	//
	public static void register(TradeServiceImpl tradeServiceImpl) {
		String key = tradeServiceImpl.brokerId+tradeServiceImpl.userId;
		if(mapTradeService.containsKey(key)){
			System.err.println("register 多个 TradeServiceImpl");
		}
		mapTradeService.put(key, tradeServiceImpl);
	}
	@Override
	public TradeService getTradeServiceBykey(String key) {
		return mapTradeService.get(key);
	}
	@Override
	public TradeService getTradeServiceByConIdKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void cleanClientStats() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void doClosedDayThings() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void tradingAccountRelease() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void doinitYPClosed() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void dopersisentAccAndPos() {
		// TODO Auto-generated method stub
		
	}

}
