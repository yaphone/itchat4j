package cn.zhouyafeng.itchat4j.utils;

import java.io.DataOutputStream;
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
public class MyHttpClient {
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
	public InputStream doPost(String url, String params) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 解决javax.net.ssl.SSLProtocolException问题
		InputStream in = null;
		try {
			URL realUrl = new URL(url);
			URLConnection urlConnection = realUrl.openConnection();
			HttpURLConnection connection = null;
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				connection.setRequestProperty("User-Agent", Config.USER_AGENT);
				connection.setDoOutput(true);
				connection.setDoInput(true);
				// 默认是 GET方式
				connection.setRequestMethod("POST");
				// Post 请求不能使用缓存
				connection.setUseCaches(false);
				// 设置本次连接是否自动重定向
				connection.setInstanceFollowRedirects(true);
				// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
				// 意思是正文是urlencoded编码过的form参数
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
				// 要注意的是connection.getOutputStream会隐含的进行connect。
				connection.connect();
				DataOutputStream out = new DataOutputStream(connection.getOutputStream());
				// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
				// String content = "字段名=" + URLEncoder.encode("字符串值", "UTF-8");
				// System.out.println(content);
				// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
				out.write(params.getBytes("UTF-8"));
				// 流用完记得关
				out.flush();
				out.close();

				// 获取响应
				in = connection.getInputStream();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;

	}
}