package com.bbkmobile.iqoo.dao.sugrec;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.bbkmobile.iqoo.bean.Model;
import com.bbkmobile.iqoo.bean.vo.SugRecApp;

@Repository
public class SugRecDaoImpl extends HibernateDaoSupport implements SugRecDao {

	@Autowired
	public void init(SessionFactory factory) {
		setSessionFactory(factory);
	}
	
    @SuppressWarnings("unchecked")
    @Override
    public List<SugRecApp> getSugRecApps(final String key, Model model)
            throws Exception {
        Session session = getSession();
        return AppInfoFilter.execute(session, null, model, SugRecApp.class,
                new SqlBuffer() {
                    @Override
                    public String preSql() {
                        return "select ";
                    }

                    @Override
                    public String postSql() {
                        return " from t_search_word_relation this_,t_app_info appinfo1_ where this_.app_id = appinfo1_.id and relation like :relation";
                    }

                    @Override
                    public String orderSq() {
                        return null;
                    }

                    @Override
                    public void queryAssign(PreparedStatement statement,
                            Map<String, Integer> placeholder)
                            throws SQLException {
                        statement.setString(placeholder.get("relation"), "%"
                                + key + "%");
                    }

                });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SugRecApp> getMatchRecApps(final String key, Model model)
            throws Exception {
        Session session = getSession();
        return AppInfoFilter.execute(session, null, model, SugRecApp.class,
                new SqlBuffer() {
                    @Override
                    public String preSql() {
                        return "select ";
                    }

                    @Override
                    public String postSql() {
                        return " from t_app_info appinfo1_ where  appCnName like :match ";
                    }

                    @Override
                    public String orderSq() {
                        return " order by  official desc, appinfo1_.downloadCount desc ";
                    }

                    @Override
                    public void queryAssign(PreparedStatement statement,
                            Map<String, Integer> placeholder)
                            throws SQLException {
                        statement.setString(placeholder.get("match"), key + "%");
                    }
                });
    }
}
