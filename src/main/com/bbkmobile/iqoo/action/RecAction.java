package com.bbkmobile.iqoo.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.platform.base.BaseStreamAction;
import com.bbkmobile.iqoo.service.baidu.BaiduRecommendationService;
import com.bbkmobile.iqoo.service.baidu.SearchConstants;
import com.bbkmobile.iqoo.service.elastic.ElasticRecommendationService;
import com.bbkmobile.iqoo.util.CommonResult;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.XmlObjectUtil;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年8月26日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
@Service("recAction")
@Scope("prototype")
public class RecAction extends BaseStreamAction {

	private static final long serialVersionUID = 6045243656084503959L;

	@Resource
	private ElasticRecommendationService elasticRecSvc;
	@Resource
	private BaiduRecommendationService baiduRecSvc;
	
	private CommonResult recResult;

	public CommonResult getRecResult() {
		return recResult;
	}

	public void setRecResult(CommonResult recResult) {
		this.recResult = recResult;
	}

	public String moreLikeThis() throws Exception{
		// old api
		// http://219.130.55.42:8087/app/rec?
		// target=local&package_name=com.zch.safelottery&app_version=530
		// &model=vivo+Xplay3S&elapsedtime=90098151&cs=0&imei=250860756400010

		HttpServletRequest request = ServletActionContext.getRequest();
		String target = request.getParameter("target");
		String pkgName = request.getParameter("package_name");
		String model = request.getParameter("model");
		String appVersionStr = request.getParameter("app_version");
		String elapsedTime = request.getParameter("elapsedtime");
		String cs = request.getParameter("cs");
		String imei = request.getParameter("imei");
		String format = request.getParameter("format");
		if (null == format || format.trim().equals("")) {
			format = "JSON";
		}

		if (null == model || "".equals(model.trim())) {
			model = "-1";
		}
		if (null == target || "".equals(target.trim())) {
			target = SearchConstants.TARGET_LOCAL;
		}
		Integer appVersion = 0;
		if (null == appVersionStr || "".equals(appVersionStr.trim())) {
			appVersion = 0;
		} else {
			appVersion = Integer.valueOf(appVersionStr);
		}

		try {
			if (null == pkgName || "".equals(pkgName.trim())) {
				CommonResult res = new CommonResult();
				res.setResult(false);
				res.setResultMsg("invalid package_name[" + pkgName + "]");
				outwrite(JsonObjectUtil.toJson(res), "text/plain;charset=utf-8");
			}

			// do search
			List<AppView> apps = null;
			if (SearchConstants.TARGET_BAIDU.equalsIgnoreCase(target)) {
				apps = baiduRecSvc.moreLikeThis(pkgName, model, appVersion);
			} else {
				apps = elasticRecSvc.moreLikeThis(pkgName, model, appVersion);
			}
			recResult = new CommonResult();
			if (apps==null){
				recResult.setResult(false);
				recResult.setResultMsg("service error: not found pkgName="+pkgName);
			}else if(apps.size()==0){
				recResult.setResult(true);
				recResult.setResultMsg("no result.");
			}else{
				recResult.setResult(true);
				recResult.setApps(apps);
			}
			
			// format
			if ("JSON".equalsIgnoreCase(format)) {
				ObjectMapper objectMapper = new ObjectMapper();
				String dataJSON = objectMapper.writeValueAsString(recResult.toJson());
				write(dataJSON, "application/json;charset=utf8");
			} else if ("XML".equalsIgnoreCase(format)) {
				String dataXML = XmlObjectUtil.toXml(recResult);
				write(dataXML,"text/xml;charset=utf8");
			} else if ("HTML".equalsIgnoreCase(format)) {
				//write("Not implement format [HTML]", "text/html;charset=utf-8");
				return "rec_result";
			} else {
				write("{'result':false;'error':'invalid format[" + format
						+ "]'", "application/json;charset=utf-8",404); // XXX for nginx
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			CommonResult res = new CommonResult();
			res.setResult(false);
			res.setResultMsg("rec IOException");
			write(JsonObjectUtil.toJson(res), "text/plain;charset=utf-8",404);// XXX for nginx
		} catch (Exception e) {
			e.printStackTrace();
			CommonResult res = new CommonResult();
			res.setResult(false);
			res.setResultMsg("rec Exception");
			write(JsonObjectUtil.toJson(res), "text/plain;charset=utf-8",404);// XXX for nginx
		}
		return null;
	}
}