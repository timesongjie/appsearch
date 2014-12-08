package com.bbkmobile.iqoo.service;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.bean.AppViewJson;

/**
 * search result
 * 
 * @author yangzt
 *
 */
@XmlRootElement(name = "PackageList")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ AppView.class, AppViewJson.class })
public class SearchServiceResult implements Serializable {
	
	private static JAXBContext context;

	static {
		try {
			context = JAXBContext.newInstance(SearchServiceResult.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static final long serialVersionUID = 3985291584890424968L;
	// raw interface fields
	@XmlAttribute
	private boolean result;
	@XmlAttribute
	private String resultMsg;
	@XmlAttribute(name = "pageNo")
	private int pageIndex;
	@XmlAttribute
	private int pageSize;
	// page size of raw search result
	private int pageSizeRaw;
	@XmlAttribute
	private int pageNext;
	@XmlAttribute(name = "maxpage")
	private int maxPage; // only valid in fist page
	@XmlAttribute(name = "TotalCount")
	private long totalCount; // only valid in fist page
	@XmlElement(name = "Package")
	private List<AppView> apps;
	private List<String> appIds;
	@XmlAttribute
	private String from; // {local|baidu}

	// Extension fields
	@XmlAttribute
	private String keyword;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
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

	public List<AppView> getApps() {
		return apps;
	}

	public void setApps(List<AppView> apps) {
		this.apps = apps;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public List<String> getAppIds() {
		return appIds;
	}

	public void setAppIds(List<String> appIds) {
		this.appIds = appIds;
	}

	public int getPageSizeRaw() {
		return pageSizeRaw;
	}

	public void setPageSizeRaw(int pageSizeRaw) {
		this.pageSizeRaw = pageSizeRaw;
	}

	public int getPageNext() {
		return pageNext;
	}

	public void setPageNext(int pageNext) {
		this.pageNext = pageNext;
	}

	@Override
	public String toString() {
		return "SearchServiceResult [result=" + result + ", resultMsg="
				+ resultMsg + ", pageIndex=" + pageIndex + ", pageSize="
				+ pageSize + ", pageSizeRaw=" + pageSizeRaw + ", pageNext="
				+ pageNext + ", maxPage=" + maxPage + ", totalCount="
				+ totalCount + ", apps=" + apps + ", appIds=" + appIds
				+ ", from=" + from + ", keyword=" + keyword + "]";
	}

	public SearchServiceResultJson toJson() {
		SearchServiceResultJson s = new SearchServiceResultJson();
		s.setResult(result);
		s.setResultMsg(resultMsg);
		s.setPageIndex(pageIndex);
		s.setPageNext(pageNext);
		s.setPageSize(pageSize);
		s.setPageSizeRaw(pageSizeRaw);
		s.setMaxPage(maxPage);
		s.setTotalCount(totalCount);
		s.setAppIds(appIds);
		s.setFrom(from);
		s.setKeyword(keyword);

		List<AppViewJson> value = new ArrayList<AppViewJson>();
		if(apps!=null){
			for (AppView a : apps) {
				if (a != null) {
					value.add(a.toJson());
				}
			}
		}
		s.setValue(value);
		s.setApps(null);
		s.setPageNo(pageIndex + 1);

		return s;
	}
	

	public String toXml() {
		String xmlStr = null;
		try {
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();
			marshaller.marshal(this, sw);
			xmlStr = sw.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return xmlStr;
	}

}
