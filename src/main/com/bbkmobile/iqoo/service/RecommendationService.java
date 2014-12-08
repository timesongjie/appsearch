package com.bbkmobile.iqoo.service;

import java.util.List;

import com.bbkmobile.iqoo.bean.AppView;

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
public interface RecommendationService {

	public List hotword(String model, Integer appVersion);

	public List<AppView> moreLikeThis(String pkgName,String model, Integer appVersion);

	public List suggest(String keyword,String model, Integer appVersion);

}
