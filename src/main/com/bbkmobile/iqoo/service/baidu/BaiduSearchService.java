package com.bbkmobile.iqoo.service.baidu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchService;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;
import com.bbkmobile.iqoo.service.baidu.api.SearchParameters;
import com.bbkmobile.iqoo.service.baidu.api.SearchResult;
import com.bbkmobile.iqoo.service.baidu.api.SearchXmlProcessor;
import com.bbkmobile.iqoo.service.baidu.xml.vo.AppVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.ResultVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.SearchResultVO;
import com.bbkmobile.iqoo.service.elastic.ElasticSearchService;
import com.bbkmobile.iqoo.service.local.AppStoreService;
import com.bbkmobile.iqoo.util.SearchProperties;
import com.bbkmobile.iqoo.util.SearchUtil;

/**
 * 
 * @Title: BaiduSearchService
 * @Description:
 * @Author:yangzt
 * @Since:2014年8月25日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
@Service("baiduSearchSvc")
public class BaiduSearchService implements SearchService {
	private final static Log LOG = LogFactory.getLog(BaiduSearchService.class);

	@Resource(name = "searchXmlProcessor")
	private SearchXmlProcessor xmlProc;
	@Resource
	private AppStoreService appStoreSvc;
	@Resource
	private ElasticSearchService elasticSearchSvc; 

	public SearchServiceResult search(SearchServiceForm form) {
		long t0 = System.currentTimeMillis();
		LOG.info("baidu search svc : " + form);
		// form -> parameters
		SearchParameters parameters = new SearchParameters();
		parameters.setId(form.getId());
		parameters.setPageNum(form.getPageIndex() * form.getPageSize());
		parameters.setRecordNum(form.getPageSize());
		parameters.setDpi(form.getDpi());
		parameters.setVersion(form.getVersion());
		parameters.setWord(form.getKeyword());

		// do baidu search
		String baiduXml = null;
		try {
			baiduXml = new SearchResult().search(parameters);
			LOG.info("search baidu api : " + parameters);
			LOG.info("search baidu api result: " + baiduXml);
			//System.out.println(baiduXml); // FIXME for test 
		} catch (Exception e) {
			e.printStackTrace();
		}
		SearchResultVO searchResultVO = xmlProc.processSearchXml(baiduXml);
		SearchServiceResult result = new SearchServiceResult();

		// parse & wrap search result
		if (searchResultVO == null || searchResultVO.getStatuscode() == null) {
			result.setResult(false);
			result.setResultMsg("baidu api error.");
			return result;
		}
		if (searchResultVO.getStatuscode().equalsIgnoreCase("0")) { // XXX
			result.setResult(true);

			// ResultVO -> searchServiceResult
			ResultVO resultVO = searchResultVO.getResult();

			result.setFrom(SearchConstants.FROM_BAIDU);
			result.setKeyword(form.getKeyword());
			if (resultVO.getDisp_num() % resultVO.getRn() > 0) {
				result.setMaxPage(resultVO.getDisp_num() / resultVO.getRn() + 1);
			} else {
				result.setMaxPage(resultVO.getDisp_num() / resultVO.getRn());
			}
			result.setPageIndex(resultVO.getPn()); // start from 0
			result.setPageSize(resultVO.getRn());
			if(result.getPageIndex()<result.getMaxPage()){
				result.setPageNext(result.getPageIndex()+1);
			}else{
				//result.setPageNext(0);// XXX
				result.setPageNext(1);
			}
			result.setTotalCount(resultVO.getDisp_num());

			List<AppVO> baiduApps = resultVO.getApps();
			List<AppView> localApps = new ArrayList<AppView>();
			AppView app = null;
			for (AppVO av : baiduApps) {
				app = new AppView();
				app.setCnName(av.getSname());
				app.setDeveloper(av.getDevelopername());
				app.setDownloadCount(av.getDownload_count());
				app.setPkgName(av.getPackagename());
				app.setOfficial(av.getOfficial());

				app.setDownloadUrl(av.getUrl());
				app.setIconUrl(av.getIcon());
				app.setPkgSize(av.getPackagesize()/1024);
				app.setVersionName(av.getVersionname());
				app.setVersionCode(av.getVersioncode());
				app.setFrom(SearchUtil.FROM_BAIDU);

				app.setId(Long.parseLong(av.getDocid()));// XXX docId as appId
				app.setKeyword(av.getCatename());
				app.setAvgComment(Float.parseFloat(av.getScore()) / 20);
				app.setCommentCount(Integer.parseInt(av.getScore_count()));

				localApps.add(app);
			}
			result.setApps(localApps);
		} else {
			// baidu is over !!!
			result.setResult(false);
			result.setResultMsg("[error when search baidu apps] "
					+ searchResultVO.getStatuscode() + " : "
					+ searchResultVO.getStatusmessage());
		}
		
		if (result.isResult()) {
			
			// filter system package
			Iterator it = result.getApps().iterator();
			Map m = null;
			String realUrl = null;
			AppView app = null;
			while (it.hasNext()) {
				app = (AppView) it.next();
				// check apk url , sys pkg , local apps
				m = appStoreSvc.getSysPkg(app.getPkgName());
				if (app.getDownloadUrl() == null
						|| !m.isEmpty()
						){//|| appStoreSvc.getAppByPkgName(app.getPkgName()) != null) {
					it.remove();
					continue;
				}

				// validate apk url
				// XXX close this when deploy

				/*
				 * if ( app.getDownloadUrl().trim().startsWith(SearchUtil.
				 * BAIDU_APK_URL_PREFIX)){ continue; } realUrl =
				 * SearchUtil.realUrl(app.getDownloadUrl(), 0);
				 * if(realUrl==null){ it.remove();
				 * LOG.error("unavailable app from baidu: " + app); }else{
				 * app.setDownloadUrl(realUrl);
				 * LOG.info("check app from baidu: " + app); }
				 */

			}
			it = null;
			m = null;
			app = null;
		}
		
		LOG.info("baidu search svc return: " +(System.currentTimeMillis()-t0)+" ms "+ result);
		return result;
	}
	
	// unused
	@Override
	public SearchServiceResult searchWithPkgName(SearchServiceForm form,
			String... pkgName) {
		// TODO Auto-generated method stub  
		return null;
	}

	// unused
	@Override
	public AppView get(Integer appId) {
		// TODO
		return null;
	}
	
	// unused
	@Override
	public List<AppView> getList(Integer... appId) {
		// TODO Auto-generated method stub  
		return null;
	}
	
	// unused
	@Override
	public AppView getByPkgName(String pkgName) {
		// TODO Auto-generated method stub  
		return null;
	}
	
	// unused
	@Override
	public List<AppView> getListByPkgNames(String... pkgNames) {
		// TODO Auto-generated method stub  
		return null;
	}

	// main test
	public static void main(String[] args) {
		ApplicationContext app = new ClassPathXmlApplicationContext(
				"classpath:resources/applicationContext.xml");

		BaiduSearchService svc = (BaiduSearchService) app
				.getBean("baiduSearchSvc");

		SearchServiceForm form = new SearchServiceForm();
		form.setKeyword("QQ");
		form.setPageSize(40);
		long t = System.currentTimeMillis();
		SearchServiceResult res = svc.search(form);
		System.out.println("time=" + (System.currentTimeMillis() - t) + "ms");

		System.out.println(res.toString());
	}

}
