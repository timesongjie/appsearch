package com.bbkmobile.iqoo.util;

import java.io.Serializable;
import java.util.List;

import com.bbkmobile.iqoo.bean.AppViewJson;

public class CommonResultJson extends CommonResult implements Serializable {

	private static final long serialVersionUID = 9092133870713973669L;

	private List<AppViewJson> value;

	public List<AppViewJson> getValue() {
		return value;
	}

	public void setValue(List<AppViewJson> value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "CommonResultJson [value=" + value + "]";
	}

}
