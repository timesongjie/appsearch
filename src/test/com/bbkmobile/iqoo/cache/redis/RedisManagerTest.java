package com.bbkmobile.iqoo.cache.redis;  

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.ShardedJedis;

import com.bbkmobile.iqoo.BaseJunit4Test;
  
public class RedisManagerTest extends BaseJunit4Test{

	@Resource
	private RedisManager redisManager;
	
	private ShardedJedis jd;
	private static final String key = "yangzt";
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		jd = redisManager.getShardedJedis();
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		
		Long d = jd.del(key);
		jd.close();
		jd = null;
	}
	
	@Test
	public void testGetJedis(){
		
		ShardedJedis jd = redisManager.getShardedJedis();
		
		Assert.assertNotNull(jd);
		
		String v = jd.get(key);
		if (v==null){
			String ok = jd.set(key, "hello");
			Assert.assertEquals("OK", ok);
		}
		
		Assert.assertTrue("hello".equals(jd.get(key)));
		jd.close();
	}
}
