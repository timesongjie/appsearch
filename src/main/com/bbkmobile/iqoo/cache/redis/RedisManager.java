package com.bbkmobile.iqoo.cache.redis;

import redis.clients.jedis.ShardedJedis;

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
public class RedisManager {

	private boolean cacheSwitch;

	public void destroy() {
		RedisShardProvider.destroy();
	}

	// sharded
	public ShardedJedis getShardedJedis() {
		ShardedJedis jedis = RedisShardProvider.getJedis();
		return jedis;
	}

	public void returnShardedJedis(ShardedJedis jedis) {
		RedisShardProvider.returnJedis(jedis);
	}
	
	public void returnBrokenShardedJedis(ShardedJedis jedis) {
		RedisShardProvider.returnJedis(jedis);
	}


	public boolean isCacheSwitch() {
		return cacheSwitch;
	}

	public void setCacheSwitch(boolean cacheSwitch) {
		this.cacheSwitch = cacheSwitch;
	}

}
