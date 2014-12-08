package com.bbkmobile.iqoo.service;

import com.bbkmobile.iqoo.bean.AppView;

/**
 * indexing service interface 
 * 
 * @author yangzt
 *
 */
public interface IndexingService {

	public AppView getById(String indexId);
	
	public String getSidByField(String fieldName,String filedValue);
	
	public void add(AppView app);
	
	public void delete(AppView app);
	
	public void update(AppView app);
	
}
