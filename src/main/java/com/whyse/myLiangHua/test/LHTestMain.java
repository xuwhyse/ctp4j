package com.whyse.myLiangHua.test;


public class LHTestMain {

	/**
	 * @param args
	 * author:xumin 
	 * 2016-11-15 上午11:52:00
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		MDLHHelper.initMyMD();
		Thread.sleep(4000);
		TraderHelper.initMyTrader();
	}

}
