package com.bbkmobile.iqoo.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.json.xml.XMLSerializer;

/**
 * 
 * @Title:
 * @Description:
 * @Author:yangzt
 * @Since:2014年9月17日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
public class XmlObjectUtil {

	public static String toXml(Object obj) {
		String xmlStr = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();
			marshaller.marshal(obj, sw);
			xmlStr = sw.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return xmlStr;
	}

	public static <T> T toObject(String xml, Class<T> c) {
		JAXBContext context;
		T t = null;
		try {
			context = JAXBContext.newInstance(c.getClass());
			Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			t = (T) jaxbUnmarshaller.unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return t;
	}

	public static String toJson(String xml) {
		return new XMLSerializer().read(xml).toString();
	}

}
