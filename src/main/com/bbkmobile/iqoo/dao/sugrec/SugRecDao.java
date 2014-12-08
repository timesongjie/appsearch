package com.bbkmobile.iqoo.dao.sugrec;

import java.util.List;

import com.bbkmobile.iqoo.bean.Model;
import com.bbkmobile.iqoo.bean.vo.SugRecApp;

/**
 * 相关推荐DAO
 * @author time
 *
 */
public interface SugRecDao {

	 List<SugRecApp> getSugRecApps(String key,Model model)throws Exception;

	List<SugRecApp> getMatchRecApps(String key, Model model) throws Exception;
}
