package com.bbkmobile.iqoo.service;

import java.util.List;

import com.bbkmobile.iqoo.bean.AppView;

/**
 * search service interface
 * 
 * @author yangzt
 *
 */
public interface SearchService {

	public SearchServiceResult search(SearchServiceForm form);
	public SearchServiceResult searchWithPkgName(SearchServiceForm form, String... pkgName);

	public AppView get(Integer appId);
	
	public List<AppView> getList(Integer... appId);
	
	public AppView getByPkgName(String pkgName);
	public List<AppView> getListByPkgNames(String... pkgNames);
}
