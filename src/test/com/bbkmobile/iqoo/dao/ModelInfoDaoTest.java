package com.bbkmobile.iqoo.dao;  

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.Model;
  
public class ModelInfoDaoTest extends BaseJunit4Test{

	@Resource
	private ModelInfoDaoImpl modelInfoDao;
	
	@Test
	public void getByNameTest(){
		try {
			Model m = modelInfoDao.findModelByMdName("vivo Y1");
			
			Assert.assertNotNull(m);
			
			System.out.println(m);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
