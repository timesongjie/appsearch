package com.bbkmobile.iqoo.service.elastic;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;


public class ElasticRecommendationServiceTest extends BaseJunit4Test {

	@Resource(name="elasticRecSvc")
	private ElasticRecommendationService svc;

	@Test
	public void testHotword() {
		// TODO 
	}

	@Test
	public void testMoreLikeThis() {
		List d = svc.moreLikeThis("com.tencent.qq", "X3", 530);
		Assert.assertTrue(d.size() > 0);
	}

	@Test
	public void testSuggest() {
		// TODO 
		//List d = svc.suggest("qq", "50", "550");
		// Assert.assertTrue(d.size() > 0);
	}

}
