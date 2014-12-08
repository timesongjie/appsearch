package com.bbkmobile.iqoo.bean;

import java.io.Serializable;

/**
 * UNUSED !!!
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月4日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class AppViewJson implements Serializable {

	private static final long serialVersionUID = 972596803710538977L;

	private Long id;
	private String title_zh;
	private String title_en;
	private String package_name;
	private String developer;
	private String keyword;

	private String download_count;
	private String official;

	private String icon_url;
	private String download_url;
	private String version_name;
	private long version_code;
	private long size;
	private String from;
	private float score;
	private int commentCount;
	private String patchs;

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

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDownload_count() {
		return download_count;
	}

	public void setDownload_count(String download_count) {
		this.download_count = download_count;
	}

	public String getOfficial() {
		return official;
	}

	public void setOfficial(String official) {
		this.official = official;
	}

	public String getIcon_url() {
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public long getVersion_code() {
		return version_code;
	}

	public void setVersion_code(long version_code) {
		this.version_code = version_code;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getPatchs() {
		return patchs;
	}

	public void setPatchs(String patchs) {
		this.patchs = patchs;
	}

	@Override
	public String toString() {
		return "AppViewJson [id=" + id + ", title_zh=" + title_zh
				+ ", title_en=" + title_en + ", package_name=" + package_name
				+ ", developer=" + developer + ", keyword=" + keyword
				+ ", download_count=" + download_count + ", official="
				+ official + ", icon_url=" + icon_url + ", download_url="
				+ download_url + ", version_name=" + version_name
				+ ", version_code=" + version_code + ", size=" + size
				+ ", from=" + from + ", score=" + score + ", commentCount="
				+ commentCount + ", patchs=" + patchs + "]";
	}

}
