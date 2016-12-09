package com.whyse.web.redis.util;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

/**
 * 提供了丰富的api，对共享在缓存的对象直接进行操作，
 * 而不需要先拿出来，操作一下，再放回去。节省了很多时间
 * 当然，最常见的还是直接拿缓存key,value使用,比如map
 * author:xumin 
 * 2016-12-9 上午11:54:16
 */
public class JedisServerImpl {

	private static JedisPool pool = null;
	private final static String HOST = "127.0.0.1";
	private final static int PORT = 6380;
	
	static {
		getPool();
	}
	// ===========================================
	/**
	 * @param args
	 *            author:xumin 2016-12-9 上午10:16:51
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		set("key","v1");
		set("key","v2");
		String v = get("key");
		System.err.println(v);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String aa = get("key");
		System.err.println(aa);
	}

	// ======================================================================
	private static JedisPool getPool() {
		if (pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setBlockWhenExhausted(true);// 连接耗尽时是否阻塞，或者抛出异常
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(1000 * 11);
			
			config.setMaxTotal(100);// 设置最大连接数
			config.setMaxIdle(95);// 最大空闲数
			config.setMinIdle(2);
			
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(false);
			// 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
			config.setMinEvictableIdleTimeMillis(1800000);
			// 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数
			// 时直接逐出,不再根据MinEvictableIdleTimeMillis判断 (默认逐出策略)
			config.setSoftMinEvictableIdleTimeMillis(1800000);

			// 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
			config.setTimeBetweenEvictionRunsMillis(1000 * 300);
			

//			pool = new JedisPool(config, HOST, PORT);
			pool = new JedisPool(config, HOST, PORT, 6000, "xumin");
		}
		return pool;
	}
	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		String value = null;

		Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			value = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 返还到连接池,新的方法
			if(jedis!=null)
				jedis.close();
		}
		return value;
	}
	/**
	 * 可更新，不过期
	 * @param key
	 * @param value
	 * @return
	 * author:xumin 
	 * 2016-12-9 下午2:27:00
	 */
	public static String set(String key,String value) {
		Jedis jedis = null;
		String ret = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			ret = jedis.set(key,value);
			System.err.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 返还到连接池,新的方法
			if(jedis!=null)
				jedis.close();
		}
		return ret;
	}
	/**
	 * 最常见的设置
	 * @param key
	 * @param value
	 * @param seconds 
	 * @return
	 * author:xumin 
	 * 2016-12-9 下午2:23:55
	 */
	public static String setex(String key,String value,int seconds) {

		Jedis jedis = null;
		String ret = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			ret = jedis.setex(key,seconds,value);
			System.err.println(ret);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 返还到连接池,新的方法
			if(jedis!=null)
				jedis.close();
		}
		return ret;
	}
	public void pipelineSetAll() { 
	    Jedis jedis = null;
		try {
			pool = getPool();
			jedis = pool.getResource();
			Pipeline pipeline = jedis.pipelined();
			for (int i = 0; i < 100000; i++) { 
		        pipeline.set("p" + i, "p" + i); 
		    }
			List<Object> results = pipeline.syncAndReturnAll();
			System.err.println(results);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 返还到连接池,新的方法
			if(jedis!=null)
				jedis.close();
		}
	} 
	// =================================================

}
