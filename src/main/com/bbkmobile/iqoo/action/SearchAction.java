package com.bbkmobile.iqoo.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.cache.CacheService;
import com.bbkmobile.iqoo.platform.base.BaseStreamAction;
import com.bbkmobile.iqoo.service.SearchService;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;
import com.bbkmobile.iqoo.service.local.AppStoreService;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.SearchProperties;
import com.bbkmobile.iqoo.util.SearchUtil;

/**
 * search action
 * 
 * @author yangzt
 *
 */
@Service("searchAction")
@Scope("prototype")
public class SearchAction extends BaseStreamAction {

	@Resource
	private SearchService elasticSearchSvc;
	@Resource
	private SearchService baiduSearchSvc;
	@Resource
	private CacheService redisCacheSvc;
	@Resource
	private AppStoreService appStoreSvc;

	// action with data
	private SearchServiceResult sresult;

	public SearchServiceResult getSresult() {
		return sresult;
	}

	public void setSresult(SearchServiceResult sresult) {
		this.sresult = sresult;
	}

	/**
	 * search method
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public String search() throws IOException, Exception {
		long t0 = System.currentTimeMillis();
		try {
			// //////// step 1 : parse parameters from original HTTP REQUEST
			HttpServletRequest request = ServletActionContext.getRequest();

			// {0=all|other=catalog id}
			String id = request.getParameter("id");
			if (null == id || "".equals(id.trim())) {
				id = "all"; // XXX
			}

			Integer pageSize = Integer.valueOf(request
					.getParameter("apps_per_page"));
			pageSize=pageSize>20?20:pageSize;

			Integer pageIndex = Integer.valueOf(request
					.getParameter("page_index"));
			if(pageIndex>0){
				pageIndex-=1;
			}

			String keyword = new String(request.getParameter("key").trim().toLowerCase().getBytes("utf-8"));
			
			String modelStr = request.getParameter("model");
			String imei = request.getParameter("imei");

			// device code {0-smartphone| 1-pc}
			String cs = request.getParameter("cs");
			Long elapsedTime = null;
			try{
				if(request.getParameter("elapsedtime")!=null){
					elapsedTime = Long.valueOf(request.getParameter("elapsedtime"));
				}
			}catch(NumberFormatException e){
				e.printStackTrace();
				log.error("elapsedtime invalid : " +request.getParameter("elapsedtime") +", trace: " + e.getMessage());
			}
					

			String osVersion = request.getParameter("osversion");
			String dpi = request.getParameter("dpi");

			// {local|baidu}
			String target = request.getParameter("target");
			if (target == null || "".equals(target.trim())) {
				target = SearchUtil.TARGET_LOCAL;
			}
			String cfrom = request.getParameter("cfrom");

			// app protocol version: focus on 300,530 !!!
			Integer clientVersion = 531;
			if(request.getParameter("app_version")!=null){
				clientVersion = Integer.valueOf(request
					.getParameter("app_version"));
			}

			// default format: JSON
			String format = request.getParameter("format");
			if (null == format || format.trim().equals("")) {
				format = "JSON";
			}
			if (clientVersion < SearchUtil.APP_VERSION_530) {
				format = "XML";
			}
			if ("1".equals(cs)) {
				format = "XML";
			}

			long t1 = System.currentTimeMillis();
			LOG.debug("search_time_debug t1 " + elapsedTime+" "+(t1 - t0) +" ms");
			
			if(keyword.isEmpty()){
				returnError("keyword is empty", null);
				return null;
			}

			// ///////// step 1.1: wrap new serach form
			SearchServiceForm sform = new SearchServiceForm();
			sform.setId(id);
			sform.setKeyword(keyword);
			sform.setModel(modelStr);
			sform.setPageIndex(pageIndex);
			sform.setPageSize(pageSize);
			sform.setDpi(dpi);
			sform.setVersion(clientVersion.toString()); // XXX
			
			// ////////////// step 2: log search request here
			// TODO
			// SQL: insert into
			// t_search_word_log_"+ date+ "(imei,model,ip,word,cfrom,cs,page_index,elapsedtime,version)
			// values(...);

			// ///////////// step 3: do search service
			sresult = null;

			// //////////// step 3.1: process target: local | baidu
			String cacheKey = "s_" + keyword + "_" + modelStr + "_"
					+ clientVersion + "_" + pageIndex + "_" + pageSize + "_"
					+ target;
			if (SearchUtil.TARGET_BAIDU.equalsIgnoreCase(target)
					|| clientVersion < SearchUtil.APP_VERSION_300) {
				if (SearchProperties.BAIDU_API_OPEN) {
					sresult = redisCacheSvc.isCacheReadOpen() ? redisCacheSvc
							.getRes(cacheKey) : null;
					if (sresult == null) {
						// missing cache , or cache closed
						sresult = baiduSearchSvc.search(sform);
						if (sresult!=null){
							if (sresult.getPageSize()==0){
								sresult.setPageSize(20);
							}
							// filter local apps
							if(sresult.getApps()!=null){
								sresult = filterLocalApp(sresult,sform);
							}
							// write cache
							if(redisCacheSvc.isCacheWriteOpen()){
								sresult = writeCache(cacheKey, sresult);
							}
						}else{
							sresult = new SearchServiceResult();
							sresult.setResult(false);
							sresult.setResultMsg("null from baidu.");
						}
					} else {
						// hit cache
						//int n = sresult.getAppIds().size();
						//String [] ids = new String[n] ;
						//for(int i=0;i<n;i++){
						//	ids[i] = SearchUtil.CACHE_KEY_BAIDU_PREFIX +sresult.getAppIds().get(i).split(":")[0];
						//}
						//sresult.setApps(redisCacheSvc.getAppList(ids));
						if(sresult.getAppIds()!=null){
							sresult.setApps(idToApps(sresult.getAppIds(), modelStr));
						}else{
							sresult.setApps(new ArrayList<AppView>());
						}
						sresult.setAppIds(null);
					}
				} else {
					sresult = new SearchServiceResult();
					sresult.setResult(false);
					sresult.setResultMsg("no result(API closed).");
				}
				long t2 = System.currentTimeMillis();
				LOG.debug("search_action_time_debug baidu t2 "+ elapsedTime+" " + (t2 - t0));
			} else { // means target=local or target=null

				// /////////////// step 3.2 : search local ,
				// /////////////// or additional baidu first page apps
				// /////////////// if not a full page local apps

				// cache here !!!
				sresult = redisCacheSvc.isCacheReadOpen() ? redisCacheSvc
						.getRes(cacheKey) : null;
				if (sresult == null) { // not hit cache
					sresult = elasticSearchSvc.search(sform);

					long t2 = System.currentTimeMillis();
					LOG.debug("search_action_time_debug localsearch t2 " + elapsedTime+" "+ (t2 - t0) +" ms");

					// check if need to add baidu apps
					if (sresult.getPageSizeRaw() < pageSize + 1) {
						if (SearchProperties.BAIDU_API_OPEN) {
							SearchServiceForm baiduForm = (SearchServiceForm) sform
									.clone();
							baiduForm.setPageIndex(0);
							baiduForm.setPageSize(40); // FIXME  
							SearchServiceResult baiduResult = baiduSearchSvc
									.search(baiduForm);

							// check if full page
							if (sresult.getPageSizeRaw() == pageSize) {
								sresult.setPageIndex(0);
							} else {
								sresult.setPageIndex(0);
								if (sresult.getApps()==null){
									sresult.setApps(new ArrayList<AppView>());
								}
								sresult.getApps().addAll(baiduResult.getApps());
							}

							// update from = baidu
							sresult.setFrom(SearchUtil.FROM_BAIDU);
							sresult.setPageNext(0);
							sresult.setMaxPage(baiduResult.getMaxPage());
							sresult.setTotalCount(baiduResult.getTotalCount());
						}
					} else {
						// more local apps
						sresult.setPageIndex(sform.getPageIndex());
						sresult.setPageNext(sform.getPageIndex()+1);
						sresult.setFrom(SearchUtil.FROM_LOCAL);
					}

					// new cache
					sresult = writeCache(cacheKey, sresult);

					t2 = System.currentTimeMillis();
					LOG.debug("search_action_time_debug newcache t2 " + elapsedTime+" "+ (t2 - t0)+" ms");
				} else {
					// hit cache here
					// System.out.println("hit cache >>> " + cacheKey);
					long t2 = System.currentTimeMillis();
					LOG.debug("search_action_time_debug hitcache t2 " + elapsedTime+" "+ (t2 - t0)+" ms");

					sresult.setApps(idToApps(sresult.getAppIds(), modelStr));
					sresult.setAppIds(null);
					long t22 = System.currentTimeMillis();
					LOG.debug("search_action_time_debug hitcache t22 "+ elapsedTime+" " + (t22 - t0)+" ms"); 
				}
			}

			// step 4: process return data format : sresult
			// step 4.1 : JSON
			if ("JSON".equalsIgnoreCase(format)) {
				String dataJSON = JsonObjectUtil.toJson(sresult.toJson());
				long t3 = System.currentTimeMillis();
				LOG.info("search_action_write_time JSON " + elapsedTime+" "+ (t3 - t0) +" ms");
				write(dataJSON, "application/json;charset=utf8");

				// step 4.2 : XML
			} else if ("XML".equalsIgnoreCase(format)) {
				//String dataXML = XmlObjectUtil.toXml(sresult);
				long t3 = System.currentTimeMillis();
				LOG.info("search_action_write_time XML " + elapsedTime+" "+ (t3 - t0) +" ms");
				write(sresult.toXml(), "text/xml;charset=utf8");

				// step 4.3 : WEB PAGE maybe
			} else if ("HTML".equalsIgnoreCase(format)) {
				long t3 = System.currentTimeMillis();
				LOG.info("search_action_write_time HTML "+ elapsedTime+" " + (t3 - t0) +" ms");
				return "search_result";
			} else {
				long t3 = System.currentTimeMillis();
				LOG.info("search_action_write_time invalid format "+ elapsedTime+" " + (t3 - t0) +" ms");
				returnError("invalid format value: [" + format + "]", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			write("search service error:"+ e.getMessage(),"text/xml;charset=utf8",404);// XXX for nginx
		}
		// XXX So magic code !!!
		// Please team review this method !!!
		//LOG.error("search_action_write_time null " + (System.currentTimeMillis() - t0) +" ms");
		return null;
	}
	
	// ids _> apps
	private List<AppView> idToApps(List<String> idFroms,String modelStr){
		int modelId = appStoreSvc.getModelId(modelStr);
		
		List<AppView> res = new ArrayList<AppView>();
		
		List<String> baiduIds = new ArrayList<String>();
		List<Integer> vivoIds = new ArrayList<Integer>();
		String [] idf = null;
		for(String idFrom: idFroms){
			idf = idFrom.split(":");
			if(SearchUtil.FROM_BAIDU.equals(idf[1])){
				baiduIds.add(SearchUtil.CACHE_KEY_BAIDU_PREFIX+idf[0]);
			}else if(SearchUtil.FROM_LOCAL.equals(idf[1])){
				vivoIds.add(Integer.parseInt(idf[0]));
			}else{
				// ERROR unknown from 
			}
		}
		idf = null;
		
		Map<String,AppView> apps= new HashMap<String,AppView>();
		if(baiduIds!=null && baiduIds.size()>0){
			String [] baiduIdsArray = new String[baiduIds.size()];
			baiduIds.toArray(baiduIdsArray);
			List<AppView> baiduList = redisCacheSvc.getAppList(baiduIdsArray);
			if(baiduList!=null){
				for(AppView a:baiduList){
					apps.put(""+a.getId()+":baidu", a);
				}
			}
			baiduIdsArray = null;
			baiduList = null;
			
		}
		if(vivoIds!=null && vivoIds.size()>0){
			Integer [] vivoIdsArray = new Integer[vivoIds.size()];
			vivoIds.toArray(vivoIdsArray);
			List<AppView> vivoList = elasticSearchSvc.getList(vivoIdsArray);
			if(vivoList!=null){
				for(AppView a:vivoList){
					if(a.getAppStatus().equals("13")){
						if(a.getFilterModel()!=null && a.getFilterModel().indexOf(","+modelId+",")>0){
							continue; // filter this model 
						}else{
							apps.put(""+a.getId()+":local", a);
						}
					}else{
						apps.put(""+a.getId()+":local", a);
					}
				}
			}
			vivoIdsArray = null;
			vivoList = null;
		}
		
		for(String idFrom: idFroms){
			if(apps.get(idFrom)!=null){
				res.add(apps.get(idFrom));
			}
		}
		
		baiduIds = null;
		vivoIds = null;
		apps = null;
		
		return res;
	}
	
	// use vivo app if exists and not searched
	private SearchServiceResult filterLocalApp(SearchServiceResult result,SearchServiceForm form){
		if(result==null || result.getApps()==null){
			return result;
		}
		int n = result.getApps().size();
		String[] pkgNames = new String[n];
		for(int i=0;i<n;i++){
			pkgNames[i] = result.getApps().get(i).getPkgName();
		}
		
		// search ES 
		List<AppView> localApps = elasticSearchSvc.getListByPkgNames(pkgNames);
		if(localApps==null || localApps.size()<1){
			return result;
		}
		SearchServiceResult res = elasticSearchSvc.searchWithPkgName(form, pkgNames);
		List<AppView> searchedApps = res.getApps();
		
		// list -> map 
		Map<String,AppView> localMap = new HashMap<String,AppView>();
		for(AppView a:localApps){
			localMap.put(a.getPkgName(), a);
		}
		
		Map<String,AppView> searchedMap = new HashMap<String,AppView>();
		if(searchedApps!=null){
			for(AppView a:searchedApps){
				searchedMap.put(a.getPkgName(), a);
			}
		}
		
		// loop 
		AppView a = null;
		for(int i=0;i<n;i++){
			a = result.getApps().get(i);
			if(localMap.containsKey(a.getPkgName())){
				// is local app
				if(searchedMap.containsKey(a.getPkgName())){
					result.getApps().set(i, null);
				}else{
					result.getApps().set(i, localMap.get(a.getPkgName()));
				}
			}else{
				continue;
			}
		}
		a = null;
		localApps = null;
		localMap = null;
		searchedApps = null;
		searchedMap = null;
		
		Iterator<AppView> it = result.getApps().iterator();
		while(it.hasNext()){
			if(it.next()==null){
				it.remove();
			}
		}
		it = null;
		
		return result;
	}
	
	private SearchServiceResult filterLocalAppOld(SearchServiceResult result,SearchServiceForm form){
		SearchServiceResult ssr = null;
		if(result==null || result.getApps()==null){
			return result;
		}
		int n = result.getApps().size();
		AppView a = null;
		AppView localApp = null;
		for(int i=0;i<n;i++){
			a = result.getApps().get(i);
			
			localApp = elasticSearchSvc.getByPkgName(a.getPkgName());
			if(localApp != null){
				ssr = elasticSearchSvc.searchWithPkgName(form, a.getPkgName());
				// local has app
				if(ssr!=null && ssr.getApps()!=null && ssr.getApps().size()>0 ){
					// be searched
					if(a.getPkgName().equals(((AppView)ssr.getApps().get(0)).getPkgName())){
						// XXX need this check ???
					}
					result.getApps().set(i, null);
				}else{
					// instead here !
					a = null;
					localApp.setIconUrl(SearchProperties.LOCAL_APP_ICON_PREFIX
							+ localApp.getIconUrl());
					localApp.setDownloadUrl(SearchProperties.LOCAL_APK_URL_PREFIX
							+ localApp.getDownloadUrl());
					localApp.setFrom(SearchUtil.FROM_LOCAL);
					result.getApps().set(i, localApp);
					continue;
				}
			}
		}// end of for
		ssr = null;
		a = null;
		localApp = null;
		
		// delete null
		Iterator it = result.getApps().iterator();
		while(it.hasNext()){
			if(it.next()==null){
				it.remove();
			}
		}
		it = null;
		
		return result;
	}

	// write search cache 
	private SearchServiceResult writeCache(String cacheKey,
			SearchServiceResult sresult) {
		if(sresult==null || sresult.getApps()==null){
			return sresult;
		}
		if (redisCacheSvc.isCacheWriteOpen()) {
			// add to cache
			List<String> ids = new ArrayList<String>();
			for (AppView a : sresult.getApps()) {
				// XXX [ appId:appFrom,... ]
				ids.add(a.getId().toString() + ":" + a.getFrom());
				if (SearchUtil.FROM_BAIDU.equals(a.getFrom())) {
					redisCacheSvc.setApp(
							SearchUtil.CACHE_KEY_BAIDU_PREFIX + a.getId(),
							 a);
				} else if (SearchUtil.FROM_LOCAL.equals(a.getFrom())) {
					// no cache local apps
				} else {
					// nothing
					log.error("unknown from : [" + a.getFrom() + "] in app "
							+ a.toString());
				}
			}
			sresult.setAppIds(ids);

			List<AppView> apps = sresult.getApps();
			sresult.setApps(null);
			redisCacheSvc.setRes(cacheKey, sresult);
			sresult.setApps(apps);
			sresult.setAppIds(null);

			return sresult;
		}

		return sresult;
	}

	// error return content
	private final void returnError(String msg, String exceptionMsg)
			throws Exception {
		String data = "{'result':'false','code':'500','error':'" + msg
				+ "','message':'" + exceptionMsg + "'}";
		write(data, "application/json;charset=utf-8");
		LOG.error("search error: " + "msg=" + msg + ", exception="
				+ exceptionMsg);
	}

	private static final long serialVersionUID = 2847683312112822589L;
	private final static Log LOG = LogFactory.getLog(SearchAction.class);
}
