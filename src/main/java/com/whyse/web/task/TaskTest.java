package com.whyse.web.task;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.whyse.main.TimeUtil;

@Service
public class TaskTest {

	final static Logger logger = Logger.getLogger("logstash").getLogger(TaskTest.class);
	public TaskTest(){
		System.err.println("TaskTest初始化！！！");
	}
	/**
	 * @param args
	 * author:xumin 
	 * 2016-6-28 下午3:23:34
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
////	@Scheduled(cron="0/30 * *  * * ? ")
//	public void everyDayNight(){
//		if(MainTestController.tradeService==null)
//			return;
//		String day = MainTestController.tradeService.getTradingDay();
//		String now = TimeUtil.getToday();
//		String time = TimeUtil.getTodayTime();
//		if(!day.equals(now)){
//			System.err.println("=================&&&&&&&&&找到了！："+"now:"+now+"-"+time+"   TradingDay:"+day);
//		}
//		System.err.println("now:"+now+"-"+time+"   TradingDay:"+day);
//	}
//	@Scheduled(cron="0/5 * *  * * ? ")
	public void myTest3(){
		logger.error("myTest3");
//		System.err.println("+++++");
	}

}
