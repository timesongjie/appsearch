package com.bbkmobile.iqoo.service;

import java.io.Serializable;

/**
 * search input form
 * 
 * @author yangzt
 *
 */
public class SearchServiceForm implements Serializable,Cloneable {

	private static final long serialVersionUID = -7789712572605505452L;
	private String id;
	private int pageIndex;
	private int pageSize;
	private String keyword;
	private String model;
	private String dpi;
	private String version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "SearchServiceForm [id=" + id + ", pageIndex=" + pageIndex
				+ ", pageSize=" + pageSize + ", keyword=" + keyword
				+ ", model=" + model + ", dpi=" + dpi + ", version=" + version
				+ "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SearchServiceForm form = (SearchServiceForm)super.clone();   
		// XXX deep copy if has object properties !!!
		// 
		return form;
	}
}
