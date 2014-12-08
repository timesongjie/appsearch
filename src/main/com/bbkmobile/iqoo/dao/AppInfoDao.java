package com.bbkmobile.iqoo.dao;

import java.util.List;

import com.bbkmobile.iqoo.bean.AppInfo;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月6日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public interface AppInfoDao {

	public AppInfo getById(Long id) throws Exception;

	List<AppInfo> getByIds(Long... ids) throws Exception;

	public AppInfo getByPkgName(String pkgName) throws Exception;
	
}
