package com.bbkmobile.iqoo.service.elastic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchService;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;
import com.bbkmobile.iqoo.service.local.AppStoreService;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.SearchProperties;
import com.bbkmobile.iqoo.util.SearchUtil;

/**
 * Search by ElasticSearch
 * 
 * @author yangzt
 *
 */
@Service("elasticSearchSvc")
public class ElasticSearchService implements SearchService {
	private final static Log LOG = LogFactory
			.getLog(ElasticSearchService.class);

	private final static Client client = ElasticSearchProvider
			.getTransportClient();

	@Resource
	private AppStoreService appStoreSvc;
	@Resource
	private SearchProperties searchProperties;

	@Override
	public SearchServiceResult search(SearchServiceForm form) {
		return search0(form, null);
	}

	@Override
	public SearchServiceResult searchWithPkgName(SearchServiceForm form,
			String... pkgName) {
		return search0(form, pkgName);
	}

	private SearchServiceResult search0(SearchServiceForm form, String... pkgNames) {
		long t0 = System.currentTimeMillis();
		LOG.debug("Elastic Search svc: " + form);

		// modelStr -> modelId
		form.setModel(appStoreSvc.getModelId(form.getModel()).toString());

		// multi match query
		MultiMatchQueryBuilder mmQuery = QueryBuilders.multiMatchQuery(
				form.getKeyword(), "cnName^3", "keyword"); // XXX ^3

		// way 1: filter by model
		BoolFilterBuilder filter = FilterBuilders.boolFilter().mustNot(
				FilterBuilders.regexpFilter("filterModel",
						"*," + form.getModel() + ","));

		// way 2: filter model and version
		AndFilterBuilder andFilter = FilterBuilders.andFilter();
		andFilter.add(FilterBuilders.notFilter(FilterBuilders.regexpFilter(
				"filterModel", "*," + form.getModel() + ",")));
		int appVersion = Integer.valueOf(form.getVersion());
		if (appVersion > -1) {
			andFilter.add(FilterBuilders.rangeFilter("minSdkVersion").lte(
					appVersion));
			// andFilter.add(FilterBuilders.rangeFilter("maxSdkVersion").gte(
			// appVersion));
		}
		// andFilter.add(filter); //XXX filter model

		if(pkgNames!=null){
			int nn = pkgNames.length;
			FilterBuilder[] filters = new FilterBuilder[nn];
			for(int i=0;i<nn;i++){
				if(pkgNames[i]!=null){
					filters[i]=FilterBuilders.termFilter("pkgName",pkgNames[i].toLowerCase());
				}
			}
			if(filters.length>0){
				andFilter.add(FilterBuilders.orFilter(filters));
			}
		}
		

		// do search
		SearchResponse response = client
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(mmQuery)
				.setPostFilter(andFilter)
				.setTrackScores(true)
				.setMinScore(searchProperties.ES_MIN_SCORE)
				// min score
				.addSort(SortBuilders.scoreSort())
				.addSort("downloadCount", SortOrder.DESC)
				.addSort("autoUpdate", SortOrder.DESC)
				.setFrom(form.getPageIndex() * form.getPageSize())
				.setSize(form.getPageSize() + 1).setExplain(true).execute()
				.actionGet();
		SearchHits hits = response.getHits();
		// LOG.error("es_search_svc_took " + response.getTookInMillis()
		// + " ms, real " + (System.currentTimeMillis() - t0) + " ms");

		// return search result
		SearchServiceResult ssr = new SearchServiceResult();
		ArrayList<AppView> data = new ArrayList<AppView>();

		SearchHit[] jsonApps = hits.getHits();
		int n = jsonApps.length;
		ssr.setPageSizeRaw(n); // raw page size
		n = n == 21 ? 20 : n; // if hits 21 then return 20
		AppView av = null;
		LOG.debug("es_search_svc hits " + n + ", "
				+ (System.currentTimeMillis() - t0) + " ms");
		for (int i = 0; i < n; i++) {
			av = JsonObjectUtil.toObject(jsonApps[i].sourceAsString(),
					AppView.class);
			if(av==null){
				LOG.error("AppView json->object error : " + jsonApps[i].sourceAsString());
				continue;
			}
			// check model
			if ("-1".equals(form.getModel())) {
				// unknown model
				if (av.getFilterModel() != null
						&& av.getFilterModel().replace(",", "").trim().length() > 0) {
					continue;
				}
			} else {
				// filter model
				if (av.getFilterModel() != null
						&& av.getFilterModel().indexOf(
								"," + form.getModel() + ",") >= 0) {
					continue;
				}
			}

			av = addUrlPrefix(av);
			data.add(av);
		}
		jsonApps = null;
		av = null;
		LOG.debug("Elastic search svc json to app "
				+ (System.currentTimeMillis() - t0) + " ms");

		// wrap result
		ssr.setApps(data);

		ssr.setKeyword(form.getKeyword());
		ssr.setPageIndex(form.getPageIndex());
		ssr.setPageSize(form.getPageSize());
		ssr.setFrom(SearchUtil.FROM_LOCAL);
		int p = (int) (hits.getTotalHits() / form.getPageSize());
		if ((int) (hits.getTotalHits() % form.getPageSize()) == 0) {
			ssr.setMaxPage(p);
		} else {
			ssr.setMaxPage(p + 1);
		}
		ssr.setResult(true);
		ssr.setTotalCount(hits.getTotalHits());

		// filter system package
		ssr.setApps(filerSysPkg(ssr.getApps()));

		LOG.debug("Elastic Search svc return: "
				+ (System.currentTimeMillis() - t0) + " ms ");
		
		response = null;
		hits=null;
		return ssr;
	}

	private List<AppView> filerSysPkg(List<AppView> apps) {
		long t0 = System.currentTimeMillis();

		if (apps == null) {
			return null;
		}
		if (apps.size() < 1) {
			return new ArrayList<AppView>();
		}
		List<String> pkgs = new ArrayList<String>();
		for (AppView app : apps) {
			pkgs.add(app.getPkgName());
		}

		String[] pkgNames = new String[pkgs.size()];
		pkgs.toArray(pkgNames);
		Map m = appStoreSvc.getSysPkg(pkgNames);
		List<AppView> res = new ArrayList<AppView>();
		for (AppView app : apps) {
			if (m.containsKey(app.getPkgName())) {
				continue;
			}
			res.add(app);
		}
		pkgs = null;
		pkgNames = null;
		m = null;
		LOG.debug("filter pkg " + (System.currentTimeMillis() - t0) + " ms");
		return res;
	}

	/**
	 * 
	 * @Description:
	 * @param appId
	 * @return
	 * @Author:yangzt
	 * @see:
	 * @since: 1.0
	 * @Create Date:2014年10月14日
	 */
	public AppView get(Integer appId) {
		long t0 = System.currentTimeMillis();
		LOG.debug("elastic search get id : " + appId);
		AppView app = null;
		try {
			GetResponse response = client
					.prepareGet(ElasticSearchManager.INDEX_NAME,
							ElasticSearchManager.INDEX_TYPE, appId.toString())
					.execute().actionGet();
			if (response != null && response.isExists()
					&& !response.isSourceEmpty()) {
				app = JsonObjectUtil.toObject(response.getSourceAsString(),
						AppView.class);
				app = addUrlPrefix(app);
			}
			response = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOG.debug("elastic search get id return: "
				+ (System.currentTimeMillis() - t0) + " ms ");
		return app;
	}

	/**
	 * 
	 * @Description:
	 * @param appIds
	 * @return
	 * @Author:yangzt
	 * @see:
	 * @since: 1.0
	 * @Create Date:2014年10月23日
	 */
	public List<AppView> getList(Integer... appIds) {
		long t0 = System.currentTimeMillis();
		if (appIds == null) {
			return null;
		}
		List<AppView> apps = new ArrayList<AppView>();
		if (appIds.length < 1) {
			return apps;
		}
		LOG.debug("elastic search get ids : " + appIds.length);

		MultiGetRequestBuilder mgrb = client.prepareMultiGet();
		for (Integer appId : appIds) {
			mgrb.add(ElasticSearchManager.INDEX_NAME,
					ElasticSearchManager.INDEX_TYPE, appId.toString());
		}

		MultiGetResponse response = null;
		try {
			response = mgrb.execute().actionGet();

			if (response != null) {
				AppView app = null;
				for (MultiGetItemResponse a : response.getResponses()) {
					if (a != null && a.getResponse() != null
							&& a.getResponse().isExists()) {
						app = JsonObjectUtil.toObject(a.getResponse()
								.getSourceAsString(), AppView.class);
						app = addUrlPrefix(app);
						apps.add(app);
					}
				}
				app = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			response = null;
		}

		LOG.debug("elastic search get ids return: "
				+ (System.currentTimeMillis() - t0) + " ms ");
		return apps;
	}

	public AppView getByPkgName(String pkgName) {
		if (pkgName == null || pkgName.trim().equals("")) {
			return null;
		}
		AppView app = null;
		SearchResponse resp = client
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				// way 1
				// .setQuery(QueryBuilders.termQuery("pkgName",
				// pkgName.toLowerCase())) 
				// way 2
				.setQuery(QueryBuilders.matchAllQuery())
				.setPostFilter(
						FilterBuilders.termFilter("pkgName",
								pkgName.toLowerCase())).execute().actionGet();
		if (resp != null) {
			SearchHits hits = resp.getHits();
			if (hits != null && hits.totalHits() > 0) {
				SearchHit[] sh = hits.getHits();
				if (sh != null && sh.length>0) {
					app = JsonObjectUtil.toObject(sh[0].getSourceAsString(),
							AppView.class);
					app = addUrlPrefix(app);
				}
			}
		}
		resp = null;

		return app;
	}
	
	public List<AppView> getListByPkgNames(String... pkgNames) {
		if(pkgNames==null){
			return null;
		}
		
		List<AppView> apps = new ArrayList<AppView>();
		int n = pkgNames.length;
		if(n<1){
			return apps;
		}
		
		FilterBuilder[] filters = new FilterBuilder[n];
		for(int i=0;i<n;i++){
			if(pkgNames[i]!=null){
				filters[i]=FilterBuilders.termFilter("pkgName",pkgNames[i].toLowerCase());
			}
		}
		if(filters.length<1){
			return apps;
		}
		
		SearchResponse resp = client
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setQuery(QueryBuilders.matchAllQuery())
				.setPostFilter(
						FilterBuilders.orFilter(filters)
				).execute().actionGet();
		
		
		if (resp != null) {
			SearchHits hits = resp.getHits();
			if (hits != null && hits.totalHits() > 0) {
				SearchHit[] sh = hits.getHits();
				if (sh != null && sh.length>0) {
					AppView app = null;
					for(SearchHit hit:sh){
						app = JsonObjectUtil.toObject(hit.getSourceAsString(),
							AppView.class);
						app = addUrlPrefix(app);
						apps.add(app);
					}
				}
			}
		}
		resp = null;
		filters = null;
		
		return apps;
	}
	
	@Deprecated
	protected Boolean existSearch(){
		Boolean n = null;
		
		// TODO 
		//client.s
		
		return null;
	}
	
	// demo query 
	/*{
	    "query" : {
	        "term" : { "user" : "kimchy" }
	    }
	}
	*/
	@Deprecated
	protected Long count(String query){
		Long n = null;
		CountResponse resp = client.count(new CountRequest(query)).actionGet();
		if(resp!=null){
			n = resp.getCount();
		}
		return n;
	}
	
	private AppView addUrlPrefix(AppView app){
		if(app!=null){
			app.setIconUrl(SearchProperties.LOCAL_APP_ICON_PREFIX
					+ app.getIconUrl());
			app.setDownloadUrl(SearchProperties.LOCAL_APK_URL_PREFIX
					+ "?id="+app.getId()+"&app_version="+app.getVersionCode()+"&from=searchlist");
			app.setFrom(SearchUtil.FROM_LOCAL);
		}
		return app;
	}
	

	// test
	public static void testOriginalSearch() {
		// ElasticSearchManager esManager = new ElasticSearchManager();
		// ElasticSearchService svc = new ElasticSearchService();
		// svc.setEsManager(esManager);
		// Client client = svc.getEsManager().getClient();

		// ik analyzer
		// QueryStringQueryBuilder qb = new
		// QueryStringQueryBuilder(form.getKeyword());
		// qb.analyzer("ik").field("cnName").field("keyword");

		// multi fields
		// QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(
		// form.getKeyword(), "cnName", "developer", "keyword");

		// and filter

		String keyword = "com.duomi";
		String filterModel = "55";

		FilterBuilder filter = FilterBuilders.orFilter(
				FilterBuilders.termFilter("cnName", keyword),
				FilterBuilders.termFilter("keyword", keyword),
				FilterBuilders.termFilter("developer", keyword),
				FilterBuilders.termFilter("pkgName", keyword));

		FilterBuilder filter2 = FilterBuilders.andFilter(FilterBuilders
				.notFilter(FilterBuilders
						.termFilter("filterModel", filterModel))); // XXX

		QueryBuilders.filteredQuery(
				QueryBuilders.termsQuery("cnName", keyword), filter2);

		SearchResponse response = client
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.matchAllQuery()).setPostFilter(filter)
				.setFrom(0).setSize(20).setExplain(true).execute().actionGet();
		SearchHits hits = response.getHits();
		System.out.println(hits.getTotalHits());
		for (int i = 0; i < hits.getHits().length; i++) {
			System.out.print(hits.getHits()[i].getScore());
			System.out.println("   " + hits.getAt(i).sourceAsString());
		}
	}

	// main test
	public static void main(String[] args) {
		ApplicationContext app = new ClassPathXmlApplicationContext(
				"classpath:resources/applicationContext.xml");

		ElasticSearchService svc = (ElasticSearchService) app
				.getBean("elasticSearchSvc");

		/*
		 * long t0 = System.currentTimeMillis(); List<AppView> apps =
		 * svc.getList(new Integer []{5177,5189});
		 * System.out.println(apps.size());
		 * System.out.println(">> first time "+(System.currentTimeMillis()-t0)
		 * +" ms");
		 * 
		 * Integer [] ids = new Integer
		 * []{5177,5189,1142,1147,1154,1173,1180,1217,1224,1243,1267,1279,1281,
		 * 1286,1293,1298,1301,1318,1320,1332}; for(int i=0;i<20;i++){ t0 =
		 * System.currentTimeMillis(); apps = svc.getList(ids);
		 * System.out.println("> time "
		 * +(i+1)+" "+(System.currentTimeMillis()-t0) +" ms"); }
		 */

		SearchServiceForm form = new SearchServiceForm();
		form.setKeyword("支付宝");
		form.setPageIndex(0);
		form.setPageSize(20);
		form.setModel("vivo X5L");
		form.setVersion("531");
		// SearchServiceResult res= svc.search(form);
		 SearchServiceResult res = svc.searchWithPkgName(form,"com.alipay.android.client.pad","com.eg.android.AlipayGphone");
		// com.eg.android.AlipayGphone

		// com.alipay.android.client.pad
		// com.alipay.m.portal
		// com.iboxpay.minicashbox
		
		 if (res != null && res.getApps() != null) { 
				System.out.println("apps: "); 
				for (Object a : res.getApps()) {
					System.out.println((AppView) a); 
				}
				  
		 }else {
			 System.out.println("res=null"); 
		 }
		 

		/*
		List<AppView> apps = svc.getListByPkgNames(new String[] {"com.alipay.android.client.pad","com.alipay.m.portal","com.eg.android.AlipayGphone"});
		if(apps!=null){
			for(AppView av:apps){
				System.out.println("app: " + av);
			}
		}else{
			System.out.println("apps=null");
		}
		*/
	}
}
