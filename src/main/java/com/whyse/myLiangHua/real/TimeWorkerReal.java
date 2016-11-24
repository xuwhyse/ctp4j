package com.whyse.myLiangHua.real;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.whyse.main.TimeUtil;

public class TimeWorkerReal {

	static String startB = "13:30:10";
	static String endB = "15:00:10";
	static String startA = "09:00:10";
	static String endA = "11:30:10";
	//--------下面是-----------
	static String startC = "21:00:05";
	static String endC= "23:59:59";
	static String startD = "00:00:3";
	static String endD = "02:00:10";
	/**
	 * @param args
	 *            author:xumin 2016-11-22 下午7:48:14
	 */
	public static void main(String[] args) {
		if(isJYTime()){
			System.err.println("交易时间");
		}else{
			System.err.println("休息时间");
		}
		
	}

	/**
	 * 没隔一分钟执行一次，获取一分k author:xumin 2016-11-22 下午7:53:10
	 */
	public static void initCYTimeWorker1() {
		Runnable runnable = new Runnable() {
			public void run() {
				if(isJYTime()){
					//------交易时间才触发每分钟执行-------
					for(String sym : MDLHClientMngImpl.listSym){
						MDLHClientImpl item = MDLHClientMngImpl.mapClient.get(sym);
						item.miniteWriteEvent();
					}
				}
//				System.err.println("每分s钟执行");
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 6, 60, TimeUnit.SECONDS);
	}

	protected static boolean isJYTime() {
		String time = TimeUtil.getTodayTime();
		boolean flag = ((time.compareTo(startA)>0 && time.compareTo(endA)<0)) || 
				((time.compareTo(startB)>0 && time.compareTo(endB)<0)) ||
				((time.compareTo(startC)>0 && time.compareTo(endC)<0)) ||
				((time.compareTo(startD)>0 && time.compareTo(endD)<0)) ;
		return flag;
	}

	public static void savePerHour() {
		Runnable runnable = new Runnable() {
			public void run() {
				for(String sym : MDLHClientMngImpl.listSym){
					MDLHClientImpl item = MDLHClientMngImpl.mapClient.get(sym);
					item.saveToFile();
				}
				System.err.println("每小时执行");
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 1, 60, TimeUnit.MINUTES);
	}

}
