package com.yachat.wechat.utils;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DomUtils {
	
	
	/**
	 * xml解析器
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午6:24:25
	 * @param text
	 * @return
	 */
	public static Document toDoc(String text) {
		Document doc = null;
		StringReader sr = new StringReader(text);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

}
