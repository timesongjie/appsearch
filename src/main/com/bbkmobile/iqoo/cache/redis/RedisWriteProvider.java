package com.bbkmobile.iqoo.cache.redis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import com.bbkmobile.iqoo.util.ResourcesUtil;

public class RedisWriteProvider {
	private final static Logger log = LoggerFactory.getLogger(RedisWriteProvider.class);
	
	private final static int POOL_MAX_RETRY_TIME = 3;
	private final static int POOL_MAX_TOTAL;
	private final static int POOL_MAX_IDLE;
	private final static int POOL_MAX_WAIT_MILLIS ;
	private final static int POOL_MIN_IDLE ;

	static {
		Properties pp = null;
		try {
			pp = ResourcesUtil
					.getResourceAsProperties("redis.master.properties");

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (pp != null) {
			if(pp.containsKey("redis.pool.maxTotal")){
			POOL_MAX_TOTAL = Integer.parseInt(pp.getProperty(
					"redis.pool.maxTotal").trim());
			}else{
				POOL_MAX_TOTAL = 50;
			}
			if(pp.containsKey("redis.pool.maxIdle")){
				POOL_MAX_IDLE = Integer.parseInt(pp.getProperty(
					"redis.pool.maxIdle").trim());
			}else{
				POOL_MAX_IDLE = 5;
			}
			if(pp.containsKey("redis.pool.maxWaitMillis")){
				POOL_MAX_WAIT_MILLIS = Integer.parseInt(pp.getProperty(
					"redis.pool.maxWaitMillis").trim());
			}else{
				POOL_MAX_WAIT_MILLIS = 3000;
			}
			if(pp.containsKey("redis.pool.minIdle")){
				POOL_MIN_IDLE = Integer.parseInt(pp.getProperty(
					"redis.pool.minIdle").trim());
			}else{
				POOL_MIN_IDLE = 5;
			}
		} else {
			POOL_MAX_TOTAL = 50;
			POOL_MAX_IDLE = 5;
			POOL_MAX_WAIT_MILLIS = 3000;
			POOL_MIN_IDLE = 5; 
			log.error("[ERROR] not found : redis.master.properties ");
		}

	}

	private String ip;
	private int port;

	/**
	 * 私有构造器.
	 */
	private RedisWriteProvider() {

	}

	private static Map<String, JedisPool> maps = new HashMap<String, JedisPool>();

	/**
	 * 获取连接池.
	 * 
	 * @return 连接池实例
	 */
	private static JedisPool getPool(String ip, int port) {
		String key = ip + ":" + port;
		JedisPool pool = null;
		if (!maps.containsKey(key)) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(POOL_MAX_TOTAL);
			config.setMaxIdle(POOL_MAX_IDLE);
			config.setMaxWaitMillis(POOL_MAX_WAIT_MILLIS);
			config.setMinIdle(POOL_MIN_IDLE);
			config.setTestOnBorrow(true); // validate when borrow
			config.setTestOnReturn(false);
			try {
				pool = new JedisPool(config, ip, port);
				if (pool != null) {
					maps.put(key, pool);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			pool = maps.get(key);
		}
		return pool;
	}

	private static class RedisProviderHolder {
		private static RedisWriteProvider instance = new RedisWriteProvider();
	}

	public static RedisWriteProvider getInstance() {
		return RedisProviderHolder.instance;
	}

	/**
	 * 获取Redis实例.
	 * 
	 * @return Redis工具类实例
	 */
	public Jedis getJedis(String ip, int port) {
		Jedis jedis = null;
		int count = 0;
		do {
			try {
				jedis = getPool(ip, port).getResource();
			} catch (Exception e) {
				log.error("get redis master failed [" + count + "]times", e);
				getPool(ip, port).returnBrokenResource(jedis);
			}
			count++;
		} while (jedis == null && count < POOL_MAX_RETRY_TIME);
		return jedis;
	}

	/**
	 * 释放redis实例到连接池.
	 * 
	 * @param jedis
	 *            redis实例
	 */
	public void returnJedis(Jedis jedis, String ip, int port) {
		if (jedis != null) {
			try{
				getPool(ip, port).returnResource(jedis);
			}catch(JedisException je){
				je.printStackTrace();
				log.error(" WOOOO return jedis exception here !!!");
				returnBrokenJedis(jedis, ip, port);
			}
		}
	}
	
	public void returnBrokenJedis(Jedis jedis, String ip, int port) {
		if (jedis != null) {
			getPool(ip, port).returnBrokenResource(jedis);
		}
	}

	public void destroy() {
		getPool(ip, port).destroy();
	}

	// getter & setter
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
