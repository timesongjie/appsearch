package com.bbkmobile.iqoo.cache.redis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchServiceResult;

public class RedisCacheServiceTest extends BaseJunit4Test {
	@Resource
	private RedisCacheService redisCacheSvc;
	
	private String key = "yangzt11";
	
	@Before
	public void before(){
		
	}
	
	@After
	public void after(){
		redisCacheSvc.delete(key);
	}
	
	@Test
	public void testSetThenGet(){
		SearchServiceResult s = new SearchServiceResult();
		s.setResult(true);
		List<AppView> apps = new ArrayList<AppView>();
		AppView app = new AppView();
		app.setId(100L);
		apps.add(app);
		s.setApps(apps);
		
		// set 
		String ok = redisCacheSvc.setRes(key,s);
		Assert.assertTrue("OK".equals(ok));

		// get 
		SearchServiceResult ss = redisCacheSvc.getRes(key);
		Assert.assertNotNull(ss);
		Assert.assertTrue(ss.getApps().size()==1);
	}
	
}
