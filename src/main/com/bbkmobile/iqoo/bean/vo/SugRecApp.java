package com.bbkmobile.iqoo.bean.vo;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.util.SearchProperties;

/**
 * 搜索关联推荐详情
 * 
 * @author time
 * 
 */
public class SugRecApp {
	
	@Resource
	private static SearchProperties searchProperties;
	
	private Long id;
	private String title_zh;
	private String title_en;
	private Float score;
	private Integer raters_count;
	private String package_name;
	private String version_name;
	private String version_code;
	private String download_url;
	private Integer size;
	private String icon_url;
	private Character offical;
	private String patchs;

	private Short tag;
	private Integer parent_id;
	private String developer;
	private Integer sortOrder;
	private Long download_count;
	private String app_remark;// 小编推荐

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle_zh() {
		return title_zh;
	}

	public void setTitle_zh(String title_zh) {
		this.title_zh = title_zh;
	}

	public String getTitle_en() {
		return title_en;
	}

	public void setTitle_en(String title_en) {
		this.title_en = title_en;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public Integer getRaters_count() {
		return raters_count;
	}

	public void setRaters_count(Integer raters_count) {
		this.raters_count = raters_count;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getVersion_code() {
		return version_code;
	}

	public void setVersion_code(String version_code) {
		this.version_code = version_code;
	}

	public String getDownload_url() {
		if (this.download_url != null
				&& this.download_url.startsWith("http://")) {// from 百度
			return this.download_url;
		}
		return searchProperties.LOCAL_APK_URL_PREFIX+"/appinfo/downloadApkFile?id=" + id;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getIcon_url() {
		try {
			if (this.icon_url != null && this.icon_url.startsWith("http://")) {// from
																				// 百度
				return this.icon_url;
			}
			icon_url = StringUtils.defaultIfEmpty(icon_url, "");
			return searchProperties.LOCAL_APP_ICON_PREFIX+icon_url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public Character getOffical() {
		return offical;
	}

	public void setOffical(Character offical) {
		if (offical == null || offical.equals(' ')) {
			offical = '0';
		}
		this.offical = offical;
	}

	public String getPatchs() {
		return patchs;
	}

	public void setPatchs(String patchs) {
		this.patchs = patchs;
	}

	public Short getTag() {
		return tag;
	}

	public void setTag(Short tag) {
		this.tag = tag;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Long getDownload_count() {
		return download_count;
	}

	public void setDownload_count(Long download_count) {
		this.download_count = download_count;
	}

	public String getApp_remark() {
		return app_remark;
	}

	public void setApp_remark(String app_remark) {
		this.app_remark = app_remark;
	}
}
