package com.bbkmobile.iqoo.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.bbkmobile.iqoo.bean.Model;
@Repository
public class ModelInfoDaoImpl extends HibernateDaoSupport implements
		ModelInfoDao {

	@SuppressWarnings("unchecked")
	public List<Model> getModelInfo() throws Exception {
		return getHibernateTemplate()
				.find("from Model order by show_order ASC");
	}

	@Override
	public Model findModelById(Short id) throws Exception {
		try {
			return (Model) getHibernateTemplate().find("from Model where id=?",
					id).get(0);
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Model findModelByMdName(String md_name) throws Exception {
		Model model = null;
		try {
			List<Model> models = getHibernateTemplate().find(
					"from Model where md_name =?", md_name);
			if (null != models && models.size() > 0) {
				model = (Model) models.get(0);
			}
		} catch (Exception e) {
			throw e;
		}
		return model;
	}

}
