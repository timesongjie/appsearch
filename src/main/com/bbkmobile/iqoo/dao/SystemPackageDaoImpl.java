package com.bbkmobile.iqoo.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

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
@Repository
public class SystemPackageDaoImpl extends HibernateDaoSupport implements
		SystemPackageDao {

	@Override
	public SystemPackage get(String pkgName) {
		String queryString = "FROM SystemPackage WHERE systemPackage = ?";
		Session session = getSession();
		Query queryObj = session.createQuery(queryString);
		queryObj.setParameter(0, pkgName);
		if (queryObj.list() != null && queryObj.list().size() > 0) {
			return (SystemPackage) queryObj.list().get(0);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SystemPackage> getList(String... pkgNames) {
		String queryString = "FROM SystemPackage WHERE systemPackage IN (:aList)";
		Session session = getSession();
		Query queryObj = session.createQuery(queryString);
		queryObj.setParameterList("aList", pkgNames);
		return queryObj.list();
	}

}
