package com.bbkmobile.iqoo.util;

import java.io.IOException;

import javax.xml.ws.http.HTTPException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @Title: SearchUtil
 * @Description: search utility constants and methods
 * @Author:yangzt
 * @Since:2014年8月23日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class SearchUtil {
	public final static String TARGET_LOCAL = "local";
	public final static String TARGET_BAIDU = "baidu";

	public final static String FROM_LOCAL = "local";
	public final static String FROM_BAIDU = "baidu";
	
	public final static String CACHE_KEY_SEARCH_PREFIX = "s_";
	public final static String CACHE_KEY_LOCAL_PREFIX = "vi_";
	public final static String CACHE_KEY_BAIDU_PREFIX = "bd_";
	
	public final static int APP_VERSION_300 = 300;
	public final static int APP_VERSION_530 = 530;

	public final static int CHECK_URL_RETRY_TIME = 5;
	public final static int CHECK_APK_MIN_SIZE = 5 * 1024;
	// baidu shouji web 
	public final static String BAIDU_APK_URL_PREFIX = "http://bs.baidu.com/appstore/apk_";
	// http://gdown.baidu.com  baidu api 
	public final static String BAIDU_APK_URL_PREFIX_2 = "http://gdown.baidu.com";
	
	private final static Log LOG = LogFactory.getLog(SearchUtil.class);

	/**
	 * 
	 * @Description:
	 * @param url
	 * @return
	 * @Author:yangzt
	 * @see:
	 * @since: 1.0
	 * @Create Date:2014年9月11日
	 */
	public static String realUrl(String url) {
		LOG.info("chech url :" + url);
		long t = System.currentTimeMillis();
		String finalUrl = null;

		// HttpConnectionManager httpConnectionManager = new
		// MultiThreadedHttpConnectionManager();
		// HttpConnectionManagerParams params =
		// httpConnectionManager.getParams();
		// params.setConnectionTimeout(5000);
		// params.setSoTimeout(20000);
		// params.setDefaultMaxConnectionsPerHost(32);//very important!!
		// params.setMaxTotalConnections(256);//very important!!
		// HttpClient http = new HttpClient(httpConnectionManager);
		//

		HttpClient http = new HttpClient();
		http.getParams()
				.setParameter(
						HttpMethodParams.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1");
		http.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		http.getParams().setConnectionManagerTimeout(5000);
		http.getParams().setSoTimeout(20000);

		GetMethod get = new GetMethod(url);
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// new DefaultHttpMethodRetryHandler(retryCount, false)

		try {
			int code = http.executeMethod(get);
			Header headerLocation = get.getResponseHeader("location");
			if (headerLocation != null) {
				finalUrl = headerLocation.getValue();
			}

			if (code == HttpStatus.SC_OK) {
				if (finalUrl == null) {
					finalUrl = "http://"
							+ get.getRequestHeader("host").getValue()
							+ get.getPath();
				}
				long apkSize = get.getResponseContentLength();
				if (apkSize < CHECK_APK_MIN_SIZE) {
					finalUrl = null;
				}
			} else {
				finalUrl = null;
				LOG.error("check url : error, code=" + code
						+ ", headerLocation=" + headerLocation);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPException e) {
			e.printStackTrace();
			// XXX
			// maybe throw CircularRedirectException
			// if from a.com redirect to a.com/b
		} finally {
			get.abort(); // IMPORTANT !!!
			get.releaseConnection();
		}
		LOG.info("check url : time=" + (System.currentTimeMillis() - t) + "ms");

		return finalUrl;
	}

	/**
	 * 
	 * @Description: 获取重定向后的最终URL地址
	 * @param url
	 * @param n
	 *            重定向次数，默认为0
	 * @return
	 * @Author:yangzt
	 * @see:
	 * @since: 1.0
	 * @Create Date:2014年9月9日
	 */
	public static String realUrl(String url, int n) {
		LOG.info("chech url[" + n + "] " + url);
		System.out.println("chech url[" + n + "] " + url);

		if (url == null || url.trim().equals("") || url.trim().length() < 10) {
			return null;
		}

		long t = System.currentTimeMillis();
		n += 1;
		String finalUrl = null;

		HttpClient http = new HttpClient();
		http.getParams()
				.setParameter(
						HttpMethodParams.USER_AGENT,
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1");
		http.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

		GetMethod get = new GetMethod(url);
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		get.setFollowRedirects(false);// close redirect

		try {
			int code = http.executeMethod(get);
			Header header = get.getResponseHeader("location");
			if (header != null) {
				finalUrl = header.getValue();
			}

			if (code == HttpStatus.SC_OK) {
				if (finalUrl == null) {
					finalUrl = url;
				}
				long apkSize = get.getResponseContentLength();
				if (apkSize < CHECK_APK_MIN_SIZE) {
					finalUrl = null;
				}
			} else if (code == HttpStatus.SC_MOVED_PERMANENTLY
					|| code == HttpStatus.SC_MOVED_TEMPORARILY) {
				if (n < CHECK_URL_RETRY_TIME + 1) {
					LOG.info("check url retry [" + n + "]:" + finalUrl);
					finalUrl = realUrl(finalUrl, n);
				} else {
					finalUrl = null; // XXX reach max retry times
				}
			} else {
				finalUrl = null;
				LOG.error("check url error : code=" + code);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPException e) {
			e.printStackTrace();
		} finally {
			get.abort(); // IMPORTANT !!!
			get.releaseConnection();
		}
		LOG.info("check url time=" + (System.currentTimeMillis() - t) + "ms");

		return finalUrl;
	}

	// main test
	public static void main(String[] args) {
		// redirect 3 times
		String initUrl = "http://www.appchina.com/market/d/2146632/cop.baidu_0/com.caynax.a6w.apk";
		String u2 = "http://down.mumayi.com/238519/mbaidu";
		// 404 offsale app
		String u3 = "http://file.m.163.com/app/free/201305/29/com.xk.house.af_4.apk";
		// baidu api url 
		String u4 = "http://m.baidu.com/api?action=redirect&token=vivo&from=563i&type=app&dltype=new&tj=soft_6959917_1858121943_QQ&blink=c874687474703a2f2f67646f776e2e62616964752e636f6d2f646174612f7769736567616d652f663333323265376130366166303330622f51515f3135362e61706b1154&crversion=1";

		System.out.println("initUrl=" + u4);
		long t = System.currentTimeMillis();
		String realUrl = realUrl(u4,0);
		System.out.println("time=" + (System.currentTimeMillis() - t) + "ms");
		System.out.println("realUrl=" + realUrl);
	}
}
