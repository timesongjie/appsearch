package com.bbkmobile.iqoo.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

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
@Repository
public class AppInfoDaoImpl extends HibernateDaoSupport implements AppInfoDao {

	public AppInfo getById(Long id) throws Exception {
		String queryString = "FROM AppInfo WHERE id = ?";
		Session session = getSession();
		Query queryObj = session.createQuery(queryString);
		queryObj.setParameter(0, id);
		if (queryObj.list() != null && queryObj.list().size() > 0) {
			return (AppInfo) queryObj.list().get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AppInfo> getByIds(Long... ids) throws Exception {
		String queryString = "FROM AppInfo WHERE id IN (:aList)";
		Session session = getSession();
		Query queryObj = session.createQuery(queryString);
		queryObj.setParameterList("aList", ids);
		return queryObj.list();
	}

	@Override
	public AppInfo getByPkgName(String pkgName) throws Exception {
		String queryString = "FROM AppInfo WHERE appPackage = ?";
		Session session = getSession();
		Query queryObj = session.createQuery(queryString);
		queryObj.setParameter(0, pkgName);
		if (queryObj.list() != null && queryObj.list().size() > 0) {
			return (AppInfo) queryObj.list().get(0);
		}
		return null;
	}

}
