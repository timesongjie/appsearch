package com.bbkmobile.iqoo.bean;

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
public class SystemPackage {

	private long id;
	private char tag;// 0 系统应用 1去重应用
	private String appCnName;
	private String systemPackage;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSystemPackage() {
		return this.systemPackage;
	}

	public void setSystemPackage(String systemPackage) {
		this.systemPackage = systemPackage;
	}

	public char getTag() {
		return tag;
	}

	public void setTag(char tag) {
		this.tag = tag;
	}

	public String getAppCnName() {
		return appCnName;
	}

	public void setAppCnName(String appCnName) {
		this.appCnName = appCnName;
	}

	@Override
	public String toString() {
		return "SystemPackage [id=" + id + ", tag=" + tag + ", appCnName="
				+ appCnName + ", systemPackage=" + systemPackage + "]";
	}

}
