package com.bbkmobile.iqoo.service.local;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.AppInfo;

public class AppStoreServiceTest extends BaseJunit4Test {

	@Resource
	private AppStoreService appStoreSvc;

	@Test
	public void getAppByIdTest() {

		AppInfo app = appStoreSvc.getAppById(40439l);

		Assert.assertTrue(app != null);
		
		System.out.println(app.getAppCnName());
	}
	
	@Test
	public void getAppsByIdsTest() {

		List d = appStoreSvc.getAppsByIds(143l,148l,166l);

		Assert.assertTrue(d != null && d.size() > 0);
	}
	
	@Test
	public void getAppByPkgNameTest() {

		AppInfo app = appStoreSvc.getAppByPkgName("net.hidroid.hibalance.cn");

		Assert.assertTrue(app != null);
		
		System.out.println(app.getAppCnName());
	}
	

	@Test
	public void getModelIdTest() {
		int id = appStoreSvc.getModelId("vivo Y1");

		Assert.assertTrue(id > 0);
	}

	@Test
	public void getSysPkg() {

		Map m = appStoreSvc.getSysPkg("bbk.music.widget", "bbk.music.widget.11","bbk.music.widget.22");

		Assert.assertTrue(m != null && !m.isEmpty());
	}
}
