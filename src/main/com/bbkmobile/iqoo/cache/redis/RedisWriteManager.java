package com.bbkmobile.iqoo.cache.redis;

import redis.clients.jedis.Jedis;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月26日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class RedisWriteManager {
	private String ip;
	private Integer port;
	private boolean cacheSwitch;
	private int searchKeyTTL;
	private int appKeyTTL;

	public void destroy() {
		RedisWriteProvider.getInstance().destroy();
	}

	// biz
	public Jedis getJedis() {
		Jedis jedis = RedisWriteProvider.getInstance().getJedis(ip,port);
		return jedis;
	}

	public void returnJedis(Jedis jedis) {
		RedisWriteProvider.getInstance().returnJedis(jedis,ip,port);
	}
	
	public void returnBrokenJedis(Jedis jedis) {
		RedisWriteProvider.getInstance().returnBrokenJedis(jedis,ip,port);
	}

	// getter & setter
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isCacheSwitch() {
		return cacheSwitch;
	}

	public void setCacheSwitch(boolean cacheSwitch) {
		this.cacheSwitch = cacheSwitch;
	}

	public int getSearchKeyTTL() {
		return searchKeyTTL;
	}

	public void setSearchKeyTTL(int searchKeyTTL) {
		this.searchKeyTTL = searchKeyTTL;
	}

	public int getAppKeyTTL() {
		return appKeyTTL;
	}

	public void setAppKeyTTL(int appKeyTTL) {
		this.appKeyTTL = appKeyTTL;
	}

}
