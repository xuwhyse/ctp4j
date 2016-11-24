package com.whyse.myLiangHua.test;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.alibaba.fastjson.JSON;
import com.whyse.myLiangHua.util.FileUtils;

public class TimeWorker {

	static String qihuoMinLinePath = "G:/lianghua/qihuo/qihuoMinLine";

	/**
	 * @param args
	 *            author:xumin 2016-11-22 下午7:48:14
	 */
	public static void main(String[] args) {
		for(int i=1;i<86;i++)
			MDLHHelper.lastPriceInQueue(MDLHHelper.queMin34, i*1.0);
		saveToFile(MDLHHelper.queMin34);
		tryReadQueMin(MDLHHelper.queMin34);
		System.err.println(MDLHHelper.queMin34);
	}

	/**
	 * 没隔一分钟执行一次，获取一分k author:xumin 2016-11-22 下午7:53:10
	 */
	public static void initCYTimeWorker1() {
		Runnable runnable = new Runnable() {
			public void run() {
				// task to run goes here
				MDLHHelper.miniteWriteEvent();
				saveToFile(MDLHHelper.queMin34);
				System.err.println("每分s钟执行");
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 6, 60, TimeUnit.SECONDS);
	}

	public static void savePerHour() {
		Runnable runnable = new Runnable() {
			public void run() {
				saveToFile(MDLHHelper.queMin34);
				System.err.println("每小时执行");
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 1, 60, TimeUnit.MINUTES);
	}
	/**
	 * 保存到文件
	 * @param queMin34
	 * author:xumin 
	 * 2016-11-22 下午8:38:00
	 */
	protected static void saveToFile(Queue<Double> queMin34) {
		String str = JSON.toJSONString(queMin34);
		FileUtils.writeToFileAll(str,qihuoMinLinePath);
	}

	@SuppressWarnings("unchecked")
	public static void tryReadQueMin(Queue<Double> queMin34) {
		try{
			String str = FileUtils.readFileAll(qihuoMinLinePath);
			MDLHHelper.queMin34 = JSON.parseObject(str, queMin34.getClass());//这个是读取用的
		}catch(Exception e){
			
		}
	}

}
