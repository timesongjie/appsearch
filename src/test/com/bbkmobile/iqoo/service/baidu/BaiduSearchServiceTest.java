package com.bbkmobile.iqoo.service.baidu;

import javax.annotation.Resource;

import org.junit.Test;

import junit.framework.Assert;

import com.bbkmobile.iqoo.BaseJunit4Test;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月20日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class BaiduSearchServiceTest extends BaseJunit4Test {

	@Resource
	private BaiduSearchService baiduSearchSvc;

	@Test
	public void testSearch() {
		SearchServiceForm form = new SearchServiceForm();
		form.setId("all");
		form.setPageIndex(0);
		form.setPageSize(20);
		form.setKeyword("QQ");

		SearchServiceResult result = baiduSearchSvc.search(form);

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getApps());
		Assert.assertTrue(result.getApps().size() > 0);
		
		System.out.println(result);

	}
}
