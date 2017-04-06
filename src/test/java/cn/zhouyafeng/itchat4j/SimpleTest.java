package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.utils.HttpClient;

public class SimpleTest {
	public static void main(String[] args) {
		// Wechat wechat = new Wechat();
		// System.out.println(wechat.getQRuuid());
		HttpClient httpClient = new HttpClient();
		String result = httpClient.doGet("http://baidu.com", null);
		System.out.println(result);
	}

}
