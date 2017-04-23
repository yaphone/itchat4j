package cn.zhouyafeng.itchat4j.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HTTP访问类，对Apache HttpClient进行简单封装，适配器模式
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月9日 下午7:05:04
 * @version 1.0
 *
 */
public class MyHttpClient {
	private Logger logger = Logger.getLogger("MyHttpClient");

	private CloseableHttpClient httpClient = HttpClients.createDefault();

	public MyHttpClient() {

	}

	/**
	 * 处理GET请求
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午7:06:19
	 * @param url
	 * @param params
	 * @return
	 */
	public HttpEntity doGet(String url, List<BasicNameValuePair> params, boolean redirect,
			Map<String, String> headerMap) {
		HttpEntity entity = null;
		HttpGet httpGet = new HttpGet();

		try {
			if (params != null) {
				String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
				httpGet = new HttpGet(url + "?" + paramStr);
			} else {
				httpGet = new HttpGet(url);
			}
			if (!redirect) {
				httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
			}
			httpGet.setHeader("User-Agent", Config.USER_AGENT);
			if (headerMap != null) {
				Set<Entry<String, String>> entries = headerMap.entrySet();
				for (Entry<String, String> entry : entries) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			CloseableHttpResponse response = httpClient.execute(httpGet);
			entity = response.getEntity();
		} catch (ClientProtocolException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return entity;
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
	public HttpEntity doPost(String url, StringEntity params) {
		HttpEntity entity = null;
		HttpPost httpPost = new HttpPost();
		try {
			if (params != null) {
				httpPost = new HttpPost(url);
				httpPost.setEntity(params);
			} else {
				httpPost = new HttpPost(url);
			}
			httpPost.setHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("User-Agent", Config.USER_AGENT);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			entity = response.getEntity();
		} catch (ClientProtocolException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return entity;
	}
}