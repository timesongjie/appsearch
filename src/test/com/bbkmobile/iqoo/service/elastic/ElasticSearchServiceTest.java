package com.bbkmobile.iqoo.service.elastic;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;

/**
 * Test search service by elastic
 * 
 * @author yangzt
 *
 */
public class ElasticSearchServiceTest extends BaseJunit4Test {

	@Resource
	private ElasticSearchManager client;
	@Resource(name="elasticSearchSvc")
	private ElasticSearchService svc;

	@SuppressWarnings("rawtypes")
	//@Test
	public void searchTest() {
		String keyword = "QQ";
		SearchServiceForm form = new SearchServiceForm();
		form.setKeyword(keyword);
		form.setModel("X3");
		form.setPageIndex(0);
		form.setPageSize(20);

		SearchServiceResult res = svc.search(form);
		Assert.assertTrue(res.isResult());

		List data = res.getApps();
		Assert.assertTrue(data.size() > 0);
	}
	
	//@Test
	public void getTest(){
		AppView app = svc.get(5177);
		
		Assert.assertNotNull(app);
	}
	
	@Test
	public void getListTest(){
		List<AppView> apps = svc.getList(5177,5189);
		
		Assert.assertTrue(apps!=null && apps.size()>0);
	}
}
