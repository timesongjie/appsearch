package com.bbkmobile.iqoo.dao;  

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.SystemPackage;
  
public class SystemPackageDaoTest extends BaseJunit4Test{

	@Resource
	private SystemPackageDaoImpl systemPackageDao;
	
	@Test
	public void getTest(){
		SystemPackage sp = systemPackageDao.get("air.com.tencent.qqpasture");//("bbk.music.widget");
		
		Assert.assertNotNull(sp);
		
		System.out.println(this.getClass().getName() + ": ");
		System.out.println(sp);
	}
	
	//@Test
	public void getListTest(){
		List<SystemPackage> sps = systemPackageDao.getList(new String[] {"bbk.music.widget","bbk.photo.widget","com.android.ActivityNetwork"});
		
		Assert.assertTrue(sps!=null && sps.size()>0);
		
		System.out.println(this.getClass().getName() + ": ");
		for (SystemPackage sp: sps)
			System.out.println(sp);
	}
}
