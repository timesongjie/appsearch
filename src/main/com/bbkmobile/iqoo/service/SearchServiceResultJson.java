package com.bbkmobile.iqoo.service;

import java.io.Serializable;
import java.util.List;

import com.bbkmobile.iqoo.bean.AppViewJson;

public class SearchServiceResultJson extends SearchServiceResult implements
		Serializable {

	private static final long serialVersionUID = 4473153707677868355L;

	private int pageNo;
	private List<AppViewJson> value;

	public List<AppViewJson> getValue() {
		return value;
	}

	public void setValue(List<AppViewJson> value) {
		this.value = value;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

}
