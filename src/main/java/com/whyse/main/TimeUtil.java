package com.whyse.main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.whyse.lib.trader.TraderLibrary;

public class TimeUtil {

	static DateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
	static DateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

	/**
	 * @param args
	 *            author:xumin 2016-4-22 下午8:01:21
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.err.println(getDay(0));

	}

	public static String getToday() {
		Date date = new Date();
		String time = formatDate.format(date);
		return time;
	}

	public static String getTodayTime() {
		Date date = new Date();
		String time = formatTime.format(date);
		return time;
	}

	public static String getDay(int days) {
		Date dat = null;
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, days);
		dat = cd.getTime();
		String time = formatDate.format(dat);
		return time;
	}

	public static String getTodayDayTime() {
		Date date = new Date();
		String day = formatDate.format(date);
		String time = formatTime.format(date);
		return day+" "+time;
	}

}
