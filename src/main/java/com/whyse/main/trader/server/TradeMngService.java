package com.whyse.main.trader.server;

import com.whyse.main.selfmodel.LoginBean;



public interface TradeMngService {
	/**
	 * 用户初始化登录，并注册实例给管理者
	 * getTradeServiceBykey 获取这边创建的实例
	 * @param loginBean
	 * @return
	 * author:xumin 
	 * 2016-5-10 下午3:16:42
	 */
	int newTradeService(LoginBean loginBean);
	/**
	 * 返回期货服务实例， key=券商代码(如国泰)+开户userid
	 * @param key brokerId+userId
	 * @return
	 * author:xumin 
	 * 2016-5-16 下午7:27:24
	 */
	TradeService getTradeServiceBykey(String key);
	/**
	 * 连接的id
	 * @param key
	 * @return
	 * author:xumin 
	 * 2016-5-20 上午9:54:30
	 */
	TradeService getTradeServiceByConIdKey(String key);
	void cleanClientStats();
	/**
	 * 下午4点5分收盘时，各个客户端做的相关信息
	 * 
	 * author:xumin 
	 * 2016-7-21 下午2:20:35
	 */
	void doClosedDayThings();
	/**
	 * 每晚6点所有账号主动登出
	 * 
	 * author:xumin 
	 * 2016-7-26 上午10:08:44
	 */
	void tradingAccountRelease();
	/**
	 * 2点31分定时任务
	 * author:xumin 
	 * 2016-8-4 下午2:44:12
	 */
	void doinitYPClosed();
	void dopersisentAccAndPos();

}
