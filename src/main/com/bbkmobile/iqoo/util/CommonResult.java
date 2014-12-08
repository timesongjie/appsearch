package com.bbkmobile.iqoo.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.bean.AppViewJson;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月18日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */

@XmlRootElement(name = "PackageList")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AppView.class })
public class CommonResult {

	private boolean result;
	private String resultMsg;
	@XmlElement(name = "Package")
	private List<AppView> apps;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public List<AppView> getApps() {
		return apps;
	}

	public void setApps(List<AppView> apps) {
		this.apps = apps;
	}

	@Override
	public String toString() {
		return "CommonResult [result=" + result + ", resultMsg=" + resultMsg
				+ ", apps=" + apps + "]";
	}

	public CommonResultJson toJson() {
		CommonResultJson c = new CommonResultJson();
		c.setResult(result);
		c.setResultMsg(resultMsg);
		c.setApps(null);
		List<AppViewJson> value = new ArrayList<AppViewJson>();
		if (apps != null) {
			for (AppView a : apps) {
				if (a != null) {
					value.add(a.toJson());
				}
			}
		}
		c.setValue(value);

		return c;
	}
}
