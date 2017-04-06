package cn.zhouyafeng.itchat4j;

import java.util.HashMap;
import java.util.Map;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.HttpClient;

public class Wechat {
	private String qrUrl = Config.BASE_URL + "/jslogin";
	private Wechat instance = null;
	private HttpClient httpClient = new HttpClient();

	Wechat() {

	}

	public int login() {
		return 0;
	}

	public String getQr() {

		return null;
	}

	public String getQRuuid() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", "wx782c26e4c19acffb");
		params.put("fun", "new");
		return httpClient.doGet(qrUrl, params);
	}
}
