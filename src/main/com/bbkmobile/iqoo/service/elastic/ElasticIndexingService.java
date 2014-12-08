package com.bbkmobile.iqoo.service.elastic;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.IndexingService;
import com.bbkmobile.iqoo.util.JsonObjectUtil;

/**
 * Indexing by elastic search
 * 
 * @author yangzt
 *
 */
@Service("elasticIndexingSvc")
public class ElasticIndexingService implements IndexingService {
	private final static Log LOG = LogFactory.getLog(ElasticIndexingService.class);

	@Resource(name = "esManager")
	private ElasticSearchManager esManager;

	public ElasticSearchManager getEsManager() {
		return esManager;
	}

	public void setEsManager(ElasticSearchManager esManager) {
		this.esManager = esManager;
	}

	/**
	 * create document
	 * 
	 * @param indexName
	 */
	private void initIndexName(String indexName) {
		Client es = esManager.getClient();
		CreateIndexResponse resp = es.admin().indices()
				.prepareCreate(indexName).execute().actionGet();

		boolean ack = resp.isAcknowledged();
		// TODO: log here
	}

	/**
	 * create mapping
	 * 
	 * @throws IOException
	 * 
	 */
	private void initMapping(String indexName, String indexType)
			throws IOException {
		Client es = esManager.getClient();
		XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
				.startObject(indexType)
				.startObject("properties")
				.startObject("id")
				.field("type", "integer")
				.field("store", "yes")
				.endObject()
				.startObject("cnName")
				.field("type", "string")
				.field("boost", 1.0)
				// boost
				.field("indexAnalyzer", "ik")
				.field("searchAnalyzer", "ik")
				// analyzer
				.endObject().startObject("enName").field("type", "string")
				.endObject().startObject("pkgName").field("type", "string")
				.endObject().startObject("iconUrl").field("type", "string")
				.field("index", "not_analyzed").endObject()
				.startObject("createDate").field("type", "date").endObject() // TODO 补齐其他字段
				.endObject().endObject().endObject();
		PutMappingRequest mappingRequest = Requests
				.putMappingRequest(indexName).type(indexType).source(mapping);
		PutMappingResponse resp = es.admin().indices()
				.putMapping(mappingRequest).actionGet();

		boolean ack = resp.isAcknowledged();
		// TODO log here
	}

	/**
	 * init
	 * 
	 * @param indexName
	 * @param indexType
	 * @throws IOException
	 */
	public void init(String indexName, String indexType) throws IOException {
		initIndexName(indexName);
		initMapping(indexName, indexType);
		
		LOG.info("init index name and mapping : " + indexName +" > " + indexType);
	}

	@Override
	public AppView getById(String indexId) {
		Client es = esManager.getClient();
		GetResponse response = es
				.prepareGet(ElasticSearchManager.INDEX_NAME,
						ElasticSearchManager.INDEX_TYPE, indexId).execute()
				.actionGet();
		AppView app = JsonObjectUtil.toObject(response.getSourceAsString(),
				AppView.class);
		
		LOG.info("getById " + indexId);
		return app;
	}

	@Override
	public String getSidByField(String fieldName, String fieldValue) {
		Client es = esManager.getClient();
		SearchResponse resp = es.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setQuery(QueryBuilders.termQuery(fieldName, fieldValue))
				.execute().actionGet();

		SearchHits hits = resp.getHits();
		
		LOG.info("getSidByBiz : " + fieldName +"=" +fieldValue);
		if (hits.getTotalHits() < 1) {
			return null;
		} else {
			return hits.getAt(0).getId();
		}
	}

	@Override
	public void add(AppView app) {
		Client es = esManager.getClient();
		IndexResponse response = es
				.prepareIndex(ElasticSearchManager.INDEX_NAME,
						ElasticSearchManager.INDEX_TYPE)
				.setSource(JsonObjectUtil.toJson(app)).execute().actionGet();

		// unused
		String id = response.getId();
		String index = response.getIndex();

		LOG.info("add index : id=" + id +", index=" + index);
	}

	// Overwrite
	public void add(List<AppView> apps) throws IOException {
		Client es = esManager.getClient();
		BulkRequestBuilder bulkRequest = es.prepareBulk();
		for (AppView app : apps) {
			bulkRequest.add(es.prepareIndex(ElasticSearchManager.INDEX_NAME,
					ElasticSearchManager.INDEX_TYPE, app.getId().toString())
					.setSource(JsonObjectUtil.toJson(app)));
		}
		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			// TODO 处理错误
			LOG.fatal("bulk indexing failed : " + apps);
		}else{
			LOG.info("bulk indexing OK : " + apps);
		}
	}

	@Override
	public void delete(AppView app) {
		Client es = esManager.getClient();
		// delete by id
		DeleteResponse response = es
				.prepareDelete(ElasticSearchManager.INDEX_NAME,
						ElasticSearchManager.INDEX_TYPE, app.getId().toString())
				.execute().actionGet();
		LOG.info("delete indexing : " + app);
	}

	@Override
	public void update(AppView app) {
		delete(app);
		LOG.debug("delete then add index ...");
		add(app);
		LOG.info("update indexing : " + app);
	}

	// main test
	public static void main(String[] args) {
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				"resources/applicationContext.xml");
		ElasticIndexingService svc = (ElasticIndexingService) appContext
				.getBean("elasticIndexingSvc");

		AppView app = new AppView();
		app.setId(121l);
		app.setCnName("愤怒的小鸟");
		// ...
		// svc.add(app);

		//
		System.out.println(svc.getSidByField("pkgName", "com.flightmanager.view"));//"com.tencent.qq"));
	}

}
