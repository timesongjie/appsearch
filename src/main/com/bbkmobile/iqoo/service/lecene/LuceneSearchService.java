package com.bbkmobile.iqoo.service.lecene;

import java.util.List;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.SearchService;
import com.bbkmobile.iqoo.service.SearchServiceForm;
import com.bbkmobile.iqoo.service.SearchServiceResult;

/**
 * search by lucene [UNUSED]
 * 
 * @author yangzt
 *
 */
public class LuceneSearchService implements SearchService {

	@Override
	public SearchServiceResult search(SearchServiceForm form) {
		// TODO Auto-generated method stub

		return null;// XXX maybe needed !!!
	}

	@Override
	public SearchServiceResult searchWithPkgName(SearchServiceForm form,
			String... pkgName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppView get(Integer appId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppView> getList(Integer... appId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AppView getByPkgName(String pkgName) {
		// TODO Auto-generated method stub  
		return null;
	}

	@Override
	public List<AppView> getListByPkgNames(String... pkgNames) {
		// TODO Auto-generated method stub  
		return null;
	}

}
