package com.bbkmobile.iqoo.service.baidu;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.bbkmobile.iqoo.bean.vo.SugRecApp;
import com.bbkmobile.iqoo.bean.vo.SugSearchResultObject;
import com.bbkmobile.iqoo.service.SearchSugService;
import com.bbkmobile.iqoo.service.baidu.api.SearchSug;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.SearchUtil;

/**
 * NOT USED !!! PLEASE REF MixSearchSugServiceOldImpl IN com.bbkmobile.iqoo.service.mix
 * 
 *@Title:  
 *@Description:  
 *@Author:yangzt 
 *@Since:2014年9月24日  
 *@Modified By:
 *@Modified Date:
 *@Why & What is modified:
 *@Version:1.0
 */
//@Service("baiduSearchSugSvc")
public class BaiduSearchSugService implements SearchSugService {

	@Override
	public String searchSugs(String word, float appVersion, String model)
			throws Exception {
		SugRecApp rec = null;
		String xmlStr = new SearchSug().searchSug(word);
		if (appVersion >= SearchUtil.APP_VERSION_530) {
			SugSearchResultObject result = parseXml(xmlStr);

			result.setRec(rec);
			return JsonObjectUtil.toJson(result);
		} else {
			return xmlStr.replaceFirst("</statusmessage>",
					"</statusmessage><word>" + word + "</word>");
		}
	}

	private SugSearchResultObject parseXml(String xml) throws Exception {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(xml
				.getBytes("UTF-8")));
		Element root = document.getRootElement();
		SugSearchResultObject result = new SugSearchResultObject();
		List<Element> ls = root.elements();
		for (Element ele : ls) {
			// if ("statuscode".equals(ele.getName())) {
			// result.setStatuscode(ele.getStringValue());
			// } else if ("statusmessage".equals(ele.getName())) {
			// result.setStatusmessage(ele.getStringValue());
			// } else
			if ("word".equals(ele.getName())) {
				result.setWord(ele.getStringValue());
			} else if ("sugs".equals(ele.getName())) {
				List<Element> eles = ele.elements();
				if (eles != null && eles.size() > 0) {
					List<String> sugs = new ArrayList<String>(eles.size());
					for (Element element : eles) {
						sugs.add(element.getStringValue());
					}
					result.setValue(sugs);
				}
			}
		}
		result.setResult(true);
		return result;
	}
}
