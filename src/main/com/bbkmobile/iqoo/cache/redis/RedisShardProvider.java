package com.bbkmobile.iqoo.cache.redis;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * @Title: RedisProvider: support sharded jedis
 * @Description: REF. http://snowolf.iteye.com/blog/1633196
 * @Author:yangzt
 * @Since:2014年8月29日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
@Service("redisShardProvider")
public class RedisShardProvider {

	private static ShardedJedisPool shardedPool;

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("redis");
		if (bundle == null) {
			throw new IllegalArgumentException(
					"[redis.properties] is not found!");
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(bundle
				.getString("redis.pool.maxTotal")));
		config.setMaxIdle(Integer.valueOf(bundle
				.getString("redis.pool.maxIdle")));
		config.setMaxWaitMillis(Long.valueOf(bundle
				.getString("redis.pool.maxWaitMillis")));
		config.setMinIdle(Integer.valueOf(bundle
				.getString("redis.pool.minIdle")));
		config.setTestOnBorrow(Boolean.valueOf(bundle
				.getString("redis.pool.testOnBorrow")));
		config.setTestOnReturn(Boolean.valueOf(bundle
				.getString("redis.pool.testOnReturn")));

		JedisShardInfo jedisShardInfo1 = new JedisShardInfo(
				bundle.getString("redis1.ip"), Integer.valueOf(bundle
						.getString("redis1.port")));

		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
		list.add(jedisShardInfo1);

		if (bundle.containsKey("redis2.ip")
				&& bundle.containsKey("redis2.port")) {
			JedisShardInfo jedisShardInfo2 = new JedisShardInfo(
					bundle.getString("redis2.ip"), Integer.valueOf(bundle
							.getString("redis2.port")));
			list.add(jedisShardInfo2);
		}
		if (bundle.containsKey("redis3.ip")
				&& bundle.containsKey("redis3.port")) {
			JedisShardInfo jedisShardInfo3 = new JedisShardInfo(
					bundle.getString("redis3.ip"), Integer.valueOf(bundle
							.getString("redis3.port")));
			list.add(jedisShardInfo3);
		}

		shardedPool = new ShardedJedisPool(config, list);
	}

	public static ShardedJedis getJedis() {
		return shardedPool.getResource();
	}

	public static void returnJedis(ShardedJedis jedis) {
		if (jedis != null) {
			shardedPool.returnResource(jedis);
		}
	}
	
	public static void returnBrokenJedis(ShardedJedis jedis) {
		if (jedis != null) {
			shardedPool.returnBrokenResource(jedis);
		}
	}

	public static void destroy() {
		if (shardedPool != null) {
			shardedPool.destroy();
		}
	}

}
