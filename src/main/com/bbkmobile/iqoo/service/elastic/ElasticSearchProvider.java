package com.bbkmobile.iqoo.service.elastic;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchProvider {

	static Map<String, String> pool = new HashMap<String, String>();
	private static TransportClient client;

	static {
		// config
		ResourceBundle bundle = ResourceBundle.getBundle("elasticsearch");
		if (bundle == null) {
			throw new IllegalArgumentException(
					"[elasticsearch.properties] is not found!");
		}
		String clusterName = bundle.getString("es.cluster.name");
		String esHost = bundle.getString("es.node.host");
		int esPort = Integer.parseInt(bundle.getString("es.node.port"));

		Settings settings = ImmutableSettings.settingsBuilder().put(pool)
				.put("client.transport.ping_timeout", "10s")
				.put("cluster.name", clusterName).put("client", true)
				.put("data", false).put("client.transport.sniff", true).build();

		// new client
		try {
			Class<?> clazz = Class.forName(TransportClient.class.getName());
			Constructor<?> constructor = clazz
					.getDeclaredConstructor(Settings.class);
			constructor.setAccessible(true);
			client = (TransportClient) constructor.newInstance(settings);
			client.addTransportAddress(new InetSocketTransportAddress(esHost,
					esPort));
			if (bundle.containsKey("es.node2.host")
					&& bundle.containsKey("es.node2.port")) {
				client.addTransportAddress(new InetSocketTransportAddress(
						bundle.getString("es.node2.host"), Integer
								.parseInt(bundle.getString("es.node2.port"))));
			}
			if (bundle.containsKey("es.node3.host")
					&& bundle.containsKey("es.node3.port")) {
				client.addTransportAddress(new InetSocketTransportAddress(
						bundle.getString("es.node3.host"), Integer
								.parseInt(bundle.getString("es.node3.port"))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// FIXME really need synchronized ??? 
	public synchronized static TransportClient getTransportClient() {
		return client;
	}
}
