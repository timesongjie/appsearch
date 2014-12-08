package com.bbkmobile.iqoo.service.elastic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppInfo;
import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.RecommendationService;
import com.bbkmobile.iqoo.service.local.AppStoreService;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.SearchProperties;

/**
 * Test more like this by elastic search
 * 
 * @author yangzt
 *
 */
@Service("elasticRecSvc")
public class ElasticRecommendationService implements RecommendationService {
	private final static Log LOG = LogFactory
			.getLog(ElasticRecommendationService.class);

	@Resource(name = "esManager")
	private ElasticSearchManager esManager;
	@Resource(name = "elasticIndexingSvc")
	private ElasticIndexingService elasticIndexingSvc;

	@Resource
	private AppStoreService appStoreSvc;
	@Resource
	private SearchProperties searchProperties;

	@Override
	public List hotword(String model, Integer appVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppView> moreLikeThis(String pkgName, String model,
			Integer appVersion) {

		String sid = elasticIndexingSvc.getSidByField("pkgName", pkgName);
		if (sid == null) {
			return null; // no exists pkgName !
		}
		int modelId = appStoreSvc.getModelId(model);

		List<AppView> apps = new ArrayList<AppView>();

		AndFilterBuilder andFilter = FilterBuilders.andFilter();
		andFilter.add(FilterBuilders.notFilter(FilterBuilders.termFilter(
				"filterModel", modelId)));
		if (appVersion > -1) {
			andFilter.add(FilterBuilders.rangeFilter("minSdkVersion").lte(
					appVersion));
			// TODO can not be null !!!
			// andFilter.add(FilterBuilders.rangeFilter("maxSdkVersion").gte(
			// appVersion));
		}
		MoreLikeThisQueryBuilder mltq = new MoreLikeThisQueryBuilder("cnName",
				"keyword");
		mltq.ids(sid).minTermFreq(1);
		// or by text like this:
		// mltq.likeText(text).analyzer("ik").minTermFreq(1);

		SearchResponse searchResponse = esManager.getClient()
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE).setQuery(mltq)
				.setPostFilter(andFilter) // XXX
				.setFrom(0).setSize(10).execute().actionGet();
		LOG.debug("search tooks "+searchResponse.getTookInMillis()+" ms");
		SearchHits hits = searchResponse.getHits();

		Iterator<SearchHit> it = hits.iterator();
		while (it.hasNext()) {
			apps.add(JsonObjectUtil.toObject(it.next().sourceAsString(),
					AppView.class));
		}
		
		// App -> AppView by appstore database
		Iterator<AppView> dataIt = apps.iterator();
		AppInfo appi = null;
		AppView appv = null;
		while (dataIt.hasNext()) {
			appv =  dataIt.next();
			appi = appStoreSvc.getAppById(appv.getId());
			appv.setIconUrl(searchProperties.LOCAL_APP_ICON_PREFIX
					+ appi.getAppIcon());
			appv.setDownloadUrl(searchProperties.LOCAL_APK_URL_PREFIX
					+ appi.getAppApk());
			appv.setVersionName(appi.getAppVersion());
			appv.setVersionCode(Integer.valueOf(appi.getAppVersionCode()));
			appv.setPkgSize(appi.getApkSize());
			appv.setFrom("local");
		}
		
		return apps;
	}

	/**
	 * search suggest and keyword auto completed
	 * 
	 */
	@Override
	public List suggest(String keyword, String model, Integer appVersion) {
		List<String> suggests = null;

		suggests = matchPrefixQuery(keyword);

		return suggests;
	}

	private List termSuggest(String keyword) {
		List<String> suggests = null;

		TermSuggestionBuilder suggestBuilder = new TermSuggestionBuilder(
				"suggest");
		suggestBuilder.field("cnName").size(1).text(keyword);
		// .maxTermFreq(10).minDocFreq(0).suggestMode("always");
		SearchResponse response = esManager.getClient()
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setSearchType(SearchType.COUNT).setSuggestText(keyword)
				.addSuggestion(suggestBuilder).execute().actionGet();

		Suggest sugg = response.getSuggest();
		List<? extends Entry<? extends Option>> list = sugg.getSuggestion(
				"suggest").getEntries(); // ???
		for (int i = 0; i < list.size(); i++) {
			List<?> options = list.get(i).getOptions();

			for (int j = 0; j < options.size(); j++) {
				if (options.get(j) instanceof Option) {
					Option op = (Option) options.get(j);
				} else {
					System.out.println("> not option");
				}
			}
		}

		return suggests;
	}

	private List phraseSuggest(String keyword) {
		List<String> suggests = null;

		PhraseSuggestionBuilder cs = new PhraseSuggestionBuilder(
				"my-phrase-suggest");
		cs.size(10);
		cs.field("cnName");

		SearchResponse response = esManager.getClient()
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE)
				.setSearchType(SearchType.COUNT).setSuggestText(keyword)
				.addSuggestion(cs).execute().actionGet();
		List<? extends Entry<? extends Option>> es = response.getSuggest()
				.getSuggestion("my-phrase-suggest").getEntries();

		return suggests;
	}

	private List pluginSuggest(String keyword) {
		List<String> suggests = null;
		// suggests = new SuggestRequestBuilder(esManager.getClient())
		// .field("cnName").term(keyword).size(10).similarity(1).execute()
		// .actionGet().suggestions();

		// refresh
		/*
		 * SuggestRefreshRequestBuilder builder = new
		 * SuggestRefreshRequestBuilder( esManager.getClient());
		 * builder.execute().actionGet();
		 */
		return suggests;
	}

	private List matchPrefixQuery(String keyword) {
		QueryBuilder qb1 = QueryBuilders.matchPhrasePrefixQuery("cnName",
				keyword);

		SearchResponse searchResponse1 = esManager.getClient()
				.prepareSearch(ElasticSearchManager.INDEX_NAME)
				.setTypes(ElasticSearchManager.INDEX_TYPE).setQuery(qb1)
				.setFrom(0).setSize(30).execute().actionGet();

		SearchHits hits = searchResponse1.getHits();
		if (hits == null) {
			return null;
		} else {
			List<String> a = new ArrayList<String>();
			int n = hits.getHits().length;
			AppView app = null;
			for (int i = 0; i < n; i++) {
				app = JsonObjectUtil.toObject(hits.getAt(i).sourceAsString(),
						AppView.class);
				// TODO: json ???
				a.add(app.getCnName() + ";" + app.getId() + ";"
						+ hits.getAt(i).getScore());
			}
			return a;
		}
	}
	
	// main test
		public static void main(String[] args) {
			ApplicationContext appctx = new ClassPathXmlApplicationContext(
					"classpath:resources/applicationContext.xml");

			ElasticRecommendationService svc = (ElasticRecommendationService) appctx
					.getBean("elasticRecSvc");

			// pkgName->id, model,appVersion

			//List<AppView> apps = svc.moreLikeThis("com.flightmanager.view", "X3", 7);
			List<AppView> apps = svc.moreLikeThis("com.yjbn", "X3", 510);
			for (AppView app : apps) {
				System.out.println("== " + app);
			}

			// List<String> s = svc.suggest("飞车", "50", "550");
			// for (String ss : s) {
			// System.out.println(ss);
			// }

		}
}
