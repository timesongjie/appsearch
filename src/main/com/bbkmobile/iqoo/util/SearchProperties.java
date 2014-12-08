package com.bbkmobile.iqoo.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月20日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
@Service
public class SearchProperties {

	public static final boolean BAIDU_API_OPEN;
	public static final String APP_MAIN_URL;
	public static final String LOCAL_APK_URL_PREFIX;
	public static final String LOCAL_APP_ICON_PREFIX;
	public static final float ES_MIN_SCORE ;

	static {
		Properties pp = null;
		try {
			pp = ResourcesUtil
					.getResourceAsProperties("search.constants.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (pp != null) {
			BAIDU_API_OPEN = Boolean.parseBoolean(pp
					.getProperty("search.baiduApiOpen"));
			APP_MAIN_URL = pp.getProperty("search.appMainUrl");
			LOCAL_APK_URL_PREFIX = pp.getProperty("search.localApkUrlPrefix");
			LOCAL_APP_ICON_PREFIX = pp.getProperty("search.localAppIconPrefix");
			ES_MIN_SCORE = Float.parseFloat(pp.getProperty("search.es.minscore"));
		}else{
			BAIDU_API_OPEN = true;
			APP_MAIN_URL = "";
			LOCAL_APK_URL_PREFIX = "";
			LOCAL_APP_ICON_PREFIX = "";
			ES_MIN_SCORE = 0.6f;
		}
	}

}
