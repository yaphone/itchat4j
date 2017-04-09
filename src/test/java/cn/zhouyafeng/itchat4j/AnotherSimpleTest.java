package cn.zhouyafeng.itchat4j;

import org.w3c.dom.Document;

import cn.zhouyafeng.itchat4j.utils.Tools;

public class AnotherSimpleTest {
	public static void main(String[] args) {
		String xml = "<error><ret>0</ret><message></message><skey>@crypt_6b6c25c8_5477a2caeaaacdccb0be3a95767cdcb2</skey><wxsid>YN+FLgA86evqRqiH</wxsid><wxuin>264833395</wxuin><pass_ticket>8eKch9x6iOhJwsaVboGK611dSxjvjxtZujuq%2BKS7ZEg%3D</pass_ticket><isgrayscale>1</isgrayscale></error>";
		Document doc = Tools.xmlParser(xml);
		System.out.println(doc.getElementsByTagName("skey").item(0).getFirstChild().getNodeValue());
	}
}
