package cn.zhouyafeng.itchat4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String uuid = "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", "wx782c26e4c19acffb");
		params.put("fun", "new");
		String result = httpClient.doGet(qrUrl, params);
		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = (\\S+?);";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(result);
		if (matcher.find()) {
			if ((matcher.group(1).equals("200"))) {
				String orgUuid = matcher.group(2); // "Aakzcf8mLQ=="
				uuid = orgUuid.substring(1, orgUuid.length() - 1); // 去掉双引号
			}
		}
		return uuid;
	}
}
