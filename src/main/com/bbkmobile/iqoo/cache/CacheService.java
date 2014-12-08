package com.bbkmobile.iqoo.cache;

import java.util.List;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchServiceResult;

/**
 * cache service
 * 
 * @author yangzt
 *
 */
public interface CacheService {

	// switch
	public boolean isCacheReadOpen();
	public boolean isCacheWriteOpen();
	
	// common
	public String get(String key);
	public String set(String key, String value);
	public Long delete(String key);

	// search result
	public SearchServiceResult getRes(String key);
	public String setRes(String key, SearchServiceResult res);

	// apps - bd_xxx or vv_xxx  
	public AppView getApp(String key);
	public List<AppView> getAppList(String... key);
	public String setApp(String key, AppView app);
	public String setAppList(String key, List<AppView> app);
}
