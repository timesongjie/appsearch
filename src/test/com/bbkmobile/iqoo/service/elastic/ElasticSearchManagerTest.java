package com.bbkmobile.iqoo.service.elastic;  

import javax.annotation.Resource;

import junit.framework.Assert;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;

import com.bbkmobile.iqoo.BaseJunit4Test;
  
public class ElasticSearchManagerTest extends BaseJunit4Test{

	@Resource
	private ElasticSearchManager client;
	
	
	@Test
	public void test(){
		TransportClient c = (TransportClient) client.getClient();
		Assert.assertTrue(c.connectedNodes().size() > 0);
		Assert.assertTrue(c.listedNodes().size()>0);
		
		System.out.println(""+c.connectedNodes().size()+" , "+c.listedNodes().size() );
		
		System.out.println( c.toString() );
		
		c.close();
		client.close();
	}
}
