package com.bbkmobile.iqoo.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @Title: JSON - Object converter
 * @Description:
 * @Author:yangzt
 * @Since:2014年8月21日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class JsonObjectUtil {

	public final static ObjectMapper objectMapper =new ObjectMapper();
	
	public static <T> T toObject(String json, Class<T> c) {
		T o = null;
		try {
			o = objectMapper.readValue(json, c);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return o;
	}

	public static String toJson(Object o) {
		ObjectMapper om = new ObjectMapper();
		Writer w = new StringWriter();
		String json = null;
		try {
			om.writeValue(w, o);
			json = w.toString();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static String toXml(String json){
		JSONObject jsonObj = JSONObject.fromObject(json);
		return new XMLSerializer().write(jsonObj);
	}
	
	public JsonObjectUtil() {
		// TODO Auto-generated constructor stub
	}
}
