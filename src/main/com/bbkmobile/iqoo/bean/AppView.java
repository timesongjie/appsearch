package com.bbkmobile.iqoo.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

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
@XmlAccessorType(XmlAccessType.FIELD)
public class AppView implements Serializable {

	private static final long serialVersionUID = -2064623929833432563L;

	@XmlElement(name = "id")
	private Long id;
	@XmlElement(name = "title_zh")
	private String cnName;
	@XmlElement(name = "title_en")
	private String enName;
	@XmlElement(name = "package_name")
	private String pkgName;
	private String developer;
	@XmlTransient
	private String keyword;

	@XmlElement(name = "download_count")
	private String downloadCount;
	private String official;
	private Integer autoUpdate;
	private String appStatus;
	@XmlTransient
	private String filterModel;
	@XmlTransient
	private Integer minSdkVersion;
	@XmlTransient
	private Integer maxSdkVersion;

	@XmlElement(name = "icon_url")
	private String iconUrl;
	@XmlElement(name = "download_url")
	private String downloadUrl;
	@XmlElement(name = "version_name")
	private String versionName;
	@XmlElement(name = "version_code")
	private long versionCode;
	@XmlElement(name = "size")
	private long pkgSize;
	@XmlElement(name = "from")
	private String from;
	@XmlElement(name = "avgComment")
	private float avgComment;
	@XmlElement(name = "commentCount")
	private int commentCount;
	@XmlElement(name = "patchs")
	private String patchs;
	@XmlTransient
	private boolean checkedApkUrl;
	@XmlTransient
	private boolean checkedApkOK;
	@XmlTransient
	private String updateApkUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
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

	public String getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(String downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getOfficial() {
		return official;
	}

	public void setOfficial(String official) {
		this.official = official;
	}

	public Integer getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(Integer autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public String getFilterModel() {
		return filterModel;
	}

	public void setFilterModel(String filterModel) {
		this.filterModel = filterModel;
	}

	public Integer getMinSdkVersion() {
		return minSdkVersion;
	}

	public void setMinSdkVersion(Integer minSdkVersion) {
		this.minSdkVersion = minSdkVersion;
	}

	public Integer getMaxSdkVersion() {
		return maxSdkVersion;
	}

	public void setMaxSdkVersion(Integer maxSdkVersion) {
		this.maxSdkVersion = maxSdkVersion;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public long getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(long versionCode) {
		this.versionCode = versionCode;
	}

	public long getPkgSize() {
		return pkgSize;
	}

	public void setPkgSize(long pkgSize) {
		this.pkgSize = pkgSize;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isCheckedApkUrl() {
		return checkedApkUrl;
	}

	public void setCheckedApkUrl(boolean checkedApkUrl) {
		this.checkedApkUrl = checkedApkUrl;
	}

	public String getUpdateApkUrl() {
		return updateApkUrl;
	}

	public void setUpdateApkUrl(String updateApkUrl) {
		this.updateApkUrl = updateApkUrl;
	}

	public boolean isCheckedApkOK() {
		return checkedApkOK;
	}

	public void setCheckedApkOK(boolean checkedApkOK) {
		this.checkedApkOK = checkedApkOK;
	}

	public float getAvgComment() {
		return avgComment;
	}

	public void setAvgComment(float avgComment) {
		this.avgComment = avgComment;
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
		return "AppView [id=" + id + ", cnName=" + cnName + ", enName="
				+ enName + ", pkgName=" + pkgName + ", developer=" + developer
				+ ", keyword=" + keyword + ", downloadCount=" + downloadCount
				+ ", official=" + official + ", autoUpdate=" + autoUpdate
				+ ", appStatus=" + appStatus + ", filterModel=" + filterModel
				+ ", minSdkVersion=" + minSdkVersion + ", maxSdkVersion="
				+ maxSdkVersion + ", iconUrl=" + iconUrl + ", downloadUrl="
				+ downloadUrl + ", versionName=" + versionName
				+ ", versionCode=" + versionCode + ", pkgSize=" + pkgSize
				+ ", from=" + from + ", avgComment=" + avgComment
				+ ", commentCount=" + commentCount + ", patchs=" + patchs
				+ ", checkedApkUrl=" + checkedApkUrl + ", checkedApkOK="
				+ checkedApkOK + ", updateApkUrl=" + updateApkUrl + "]";
	}

	public AppViewJson toJson(){
		AppViewJson a = new AppViewJson();
		
		a.setId(id);
		a.setTitle_zh(cnName);
		a.setTitle_en(enName);
		a.setPackage_name(pkgName);
		a.setDeveloper(developer);
		a.setKeyword(keyword);
		
		a.setDownload_count(downloadCount);
		a.setOfficial(official);
		a.setIcon_url(iconUrl);
		a.setDownload_url(downloadUrl);
		a.setVersion_name(versionName);
		a.setVersion_code(versionCode);
		
		a.setSize(pkgSize);
		a.setFrom(from);
		a.setScore(avgComment);
		a.setCommentCount(commentCount);
		a.setPatchs(patchs);
		
		return a;
	}
}
