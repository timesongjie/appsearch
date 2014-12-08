package com.bbkmobile.iqoo.dao;  

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.Model;
import com.bbkmobile.iqoo.bean.vo.SugRecApp;
import com.bbkmobile.iqoo.dao.sugrec.SugRecDaoImpl;
  
public class SugRecDaoTest extends BaseJunit4Test{
	
	@Resource
	private SugRecDaoImpl sugRecDao;

	@Test
	public void getSugRecApps() throws Exception{
		Model model = null;
		List<SugRecApp> d = sugRecDao.getSugRecApps("qq", model);
		
		for(SugRecApp a: d){
			System.out.println(a);
		}
	}
}
