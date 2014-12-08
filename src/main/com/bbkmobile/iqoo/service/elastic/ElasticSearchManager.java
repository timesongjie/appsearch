package com.bbkmobile.iqoo.service.elastic;

import junit.framework.Assert;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ElasticSearch Client
 * 
 * @author yangzt
 *
 */
public class ElasticSearchManager {

	public static final String INDEX_NAME = "appsearch";
	public static final String INDEX_TYPE = "apps_vivo";

	public ElasticSearchManager() {
		// nothing
	}

	public Client getClient() {
		return ElasticSearchProvider.getTransportClient();
	}

	public void close() {
		ElasticSearchProvider.getTransportClient().close();
	}

	// main test
	public static void main(String[] args) {
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath:resources/applicationContext.xml");
		
		ElasticSearchManager m = (ElasticSearchManager)app.getBean("esManager");
		// type cast to visit nodes num.
		TransportClient c = (TransportClient) m.getClient();
		Assert.assertTrue(c.connectedNodes().size() > 0);
		Assert.assertTrue(c.listedNodes().size()>0);

		c.close();
		m.close();
	}
}
