package com.bbkmobile.iqoo.dao;

import java.util.List;

import com.bbkmobile.iqoo.bean.SystemPackage;

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
public interface SystemPackageDao {

	public SystemPackage get(String pkgName);
	
	public List<SystemPackage> getList(String... pkgNames);
	
}
