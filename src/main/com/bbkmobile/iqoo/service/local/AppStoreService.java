package com.bbkmobile.iqoo.service.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppInfo;
import com.bbkmobile.iqoo.bean.Model;
import com.bbkmobile.iqoo.bean.SystemPackage;
import com.bbkmobile.iqoo.dao.AppInfoDaoImpl;
import com.bbkmobile.iqoo.dao.ModelInfoDao;
import com.bbkmobile.iqoo.dao.PopupWordDaoImpl;
import com.bbkmobile.iqoo.dao.SystemPackageDaoImpl;

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
@Service("appStoreSvc")
public class AppStoreService {
	@Resource
	private AppInfoDaoImpl appInfoDao;

	@Resource
	private ModelInfoDao modelInfoDao;

	@Resource
	private SystemPackageDaoImpl systemPackageDao;

	@Resource
	private PopupWordDaoImpl popupWordDao;

	public AppInfo getAppById(Long id) {
		try {
			return appInfoDao.getById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<AppInfo> getAppsByIds(Long... ids) {
		try {
			return appInfoDao.getByIds(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public AppInfo getAppByPkgName(String pkgName) {
		try {
			return appInfoDao.getByPkgName(pkgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Model getModel(String modeName) {
		Model m = null;
		try {
			m = modelInfoDao.findModelByMdName(modeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public Integer getModelId(String modelName) {
		if (modelName == null || modelName.trim().length() < 1) {
			return -1;
		}
		Model m = getModel(modelName);
		return m == null ? -1 : m.getId().intValue();
	}

	public Map<String, Object> getSysPkg(String... pkgNames) {
		Map<String, Object> m = new HashMap<String, Object>();
		List<SystemPackage> sps = systemPackageDao.getList(pkgNames);
		for (SystemPackage sp : sps) {
			m.put(sp.getSystemPackage(), sp);
		}

		return m;
	}

	public String getSearchHotWords(String model) throws Exception {
		try {
			StringBuilder sb = new StringBuilder();
			List<String> searchHotWords = popupWordDao.getSearchHotWord(model);
			if (null != searchHotWords) {
				for (String searchHotWord : searchHotWords) {
					sb.append(searchHotWord);
					sb.append(",");
				}
				return sb.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
