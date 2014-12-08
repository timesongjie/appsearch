package com.bbkmobile.iqoo.service.baidu;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.service.RecommendationService;
import com.bbkmobile.iqoo.service.baidu.api.RelatedRecResult;
import com.bbkmobile.iqoo.service.baidu.api.SearchXmlProcessor;
import com.bbkmobile.iqoo.service.baidu.xml.vo.AppVO;
import com.bbkmobile.iqoo.service.baidu.xml.vo.UpdateResultVO;

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
@Service("baiduRecSvc")
public class BaiduRecommendationService implements RecommendationService {

	@Resource(name = "searchXmlProcessor")
	private SearchXmlProcessor xmlProc;

	@Override
	public List hotword(String model, Integer appVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppView> moreLikeThis(String pkgName, String model,
			Integer appVersion) {

		String relatedRecXml = new RelatedRecResult().relatedRecApps(pkgName);
		UpdateResultVO vo = null;
		try {
			vo = xmlProc.processRelatedRecXml(relatedRecXml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<AppVO> apps = null;
		if (vo != null) {
			apps = vo.getApps();
		}

		List<AppView> appViews = new ArrayList<AppView>();
		AppView app = null;
		if (apps != null) {
			for (AppVO av : apps) {
				app = new AppView();
				app.setCnName(av.getSname());
				app.setDeveloper(av.getDevelopername());// ?
				app.setDownloadCount(av.getDownload_count());// ?
				app.setOfficial(av.getOfficial());
				app.setPkgName(av.getPackagename());
				app.setIconUrl(av.getIcon());
				app.setDownloadUrl(av.getUrl());
				app.setPkgSize(av.getPackagesize());
				app.setVersionName(av.getVersionname());
				app.setVersionCode(av.getVersioncode());

				appViews.add(app);
			}
		}
		return appViews;
	}

	@Override
	public List suggest(String keyword, String model, Integer appVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	// main test
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:resources/applicationContext.xml");

		BaiduRecommendationService svc = (BaiduRecommendationService) ctx
				.getBean("baiduRecSvc");

		List<AppView> apps = svc.moreLikeThis("com.flightmanager.view", "55",
				530);
		for (AppView app : apps) {
			System.out.println(app);
		}
	}
}
