package com.bbkmobile.iqoo.action;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bbkmobile.iqoo.platform.base.BaseStreamAction;
import com.bbkmobile.iqoo.service.SearchSugService;
import com.bbkmobile.iqoo.service.local.AppStoreService;
import com.bbkmobile.iqoo.util.CommonResult;
import com.bbkmobile.iqoo.util.JsonObjectUtil;

@Component("searchSugOldAction")
@Scope("prototype")
public class SearchSugOldAction extends BaseStreamAction {

	private static final long serialVersionUID = -7587713490850659151L;

	private Log log = LogFactory.getLog(SearchSugOldAction.class);

	private String imei;// imei码
	private String model;// 机型号
	private String word;// 关键词
	private String app_version;

	@Resource(name = "mixSearchSugSvc")
	private SearchSugService mixSearchSugSvc;
	@Resource(name = "appStoreSvc")
	private AppStoreService appStoreSvc;

	public void searchSug() throws Exception {

		try {
			// String reponse =
			// "<response><statuscode>0</statuscode><statusmessage>done</statusmessage><sugs></sugs></response>";
			// write(reponse, "text/xml;charset=utf8");
			app_version = StringUtils.defaultIfEmpty(app_version, "530");
			if (StringUtils.isNumeric(app_version)) {
				write(getMixSearchSugSvc().searchSugs(getWord(),
						Float.valueOf(app_version), this.model),
						"text/plain;charset=utf8");
			}
			// if (Constants.SAVE_SEARCH_KEY_LOG) {
			// HttpServletRequest request = getHttpServletRequest();
			// String cfrom = StringUtils.defaultIfEmpty(
			// request.getParameter("cfrom"), "2");
			// String ip = RequestUtil.getClientIP(request);
			// RequestParameter requestParameter = new RequestParameter();
			// requestParameter.setImei(imei);
			// requestParameter.setModel(model);
			// requestParameter.setIp(ip);
			// requestParameter.setCfrom(Short.valueOf(cfrom));
			// // requestParameter.setIdStr(idStr);
			// requestParameter.setElapsedtime(request
			// .getParameter("elapsedtime"));
			// if (StringUtils.isNotBlank(app_version)
			// && StringUtils.isNumeric(app_version)) {
			// requestParameter.setApp_version(Float
			// .parseFloat(app_version));
			// }
			// requestParameter.setWord(getWord());
			// appInfoService.saveSeachWordLog(requestParameter);
			// }
		} catch (Exception e) {
			log.error(e.toString());
			CommonResult res = new CommonResult();
			res.setResult(false);
			res.setResultMsg("search sug exception");
			outwrite(JsonObjectUtil.toJson(res), "text/plain;charset=utf-8");
		}

	}

	public void searchHotwords() throws Exception {
		try {
			String data = appStoreSvc.getSearchHotWords(model);
			write(data, "text/plain;charset=utf8");
		} catch (Exception e) {
			LOG.error("获取搜索热门词汇时出错:model=" + model + "，error=" + e.getMessage());
			CommonResult res = new CommonResult();
			res.setResult(false);
			res.setResultMsg("hotwords exception");
			outwrite(JsonObjectUtil.toJson(res), "text/plain;charset=utf-8");
		}
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public SearchSugService getMixSearchSugSvc() {
		return mixSearchSugSvc;
	}

	public void setMixSearchSugSvc(SearchSugService mixSearchSugSvc) {
		this.mixSearchSugSvc = mixSearchSugSvc;
	}

	public AppStoreService getAppStoreSvc() {
		return appStoreSvc;
	}

	public void setAppStoreSvc(AppStoreService appStoreSvc) {
		this.appStoreSvc = appStoreSvc;
	}

}
