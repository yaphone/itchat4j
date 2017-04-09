package cn.zhouyafeng.itchat4j.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * HTTP访问类
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月9日 下午7:05:04
 * @version 1.0
 *
 */
public class HttpClient {
	String fullUrl = "";
	StringBuffer sb = new StringBuffer();

	/**
	 * 处理GET请求
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午7:06:19
	 * @param url
	 * @param params
	 * @return
	 */
	public InputStream doGet(String url, Map<String, String> params) {

		if (params != null) {
			for (String param : params.keySet()) {
				sb.append(param);
				sb.append("=");
				sb.append(params.get(param));
				sb.append("&");
			}
		}

		if (sb.toString().length() != 0) {
			fullUrl = url + "?" + sb.toString().substring(0, sb.length() - 1); // 完整的URL
		} else {
			fullUrl = url;
		}
		System.setProperty("jsse.enableSNIExtension", "false"); // 解决javax.net.ssl.SSLProtocolException问题
		InputStream in = null;
		try {
			URL realUrl = new URL(fullUrl);
			URLConnection urlConnection = realUrl.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				connection.setRequestProperty("User-Agent", Config.USER_AGENT);
			} else {
				System.out.println("请输入 URL 地址");
				return null;
			}
			in = connection.getInputStream();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}

	/**
	 * 处理POST请求
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午7:06:35
	 * @param url
	 * @param params
	 * @return
	 */
	public InputStream doPost(String url, Map<String, String> params) {
		// TODO

		return null;

	}
}