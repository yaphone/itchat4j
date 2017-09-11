package com.yachat.wechat.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.yachat.wechat.utils.Constants;

public class RetryHttpClient {

	private final Logger LOGGER = LoggerFactory.getLogger(RetryHttpClient.class);
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient httpClient;
	private ThreadLocal<HttpRequestBase> holder;

	public RetryHttpClient() {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		this.connectionManager = new PoolingHttpClientConnectionManager();
		this.initConnectionManager();
		this.holder = new ThreadLocal<>();
	}

	private void initConnectionManager() {
		this.connectionManager.setMaxTotal(200);
		this.connectionManager.setDefaultMaxPerRoute(50);
	}

	public String getCookie(CookieStore cookieStore, String name) {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public HttpEntity get(String url, boolean redirect, Map<String, String> params, Map<String, String> headerMap,
			CookieStore cookie) {
		HttpGet httpGet = null;
		try {
			String requestUrl = url;
			if (params != null) {
				List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
				for (Entry<String, String> entry : params.entrySet()) {
					paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				requestUrl += "?" + EntityUtils.toString(new UrlEncodedFormEntity(paramsList, Consts.UTF_8));
			}
			httpGet = new HttpGet(requestUrl);
			if (!redirect) {
				httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
			}
			this.setHeaders(httpGet, headerMap);
			return this.execute(httpGet, cookie);
		} catch (ClientProtocolException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public HttpEntity post(String url, Map<String, Object> params, Map<String, String> headerMap,
			CookieStore cookieStore) {
		HttpPost post = new HttpPost(url);
		StringEntity body = new StringEntity(JSON.toJSONString(params), Consts.UTF_8);
		post.setEntity(body);
		post.setHeader("Content-type", "application/json; charset=utf-8");
		this.setHeaders(post, headerMap);
		return this.execute(post, cookieStore);
	}

	public HttpEntity post(String url, HttpEntity entity, Map<String, String> headerMap) {
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		this.setHeaders(post, headerMap);
		return this.execute(post, null);
	}

	private void setHeaders(HttpRequestBase request, Map<String, String> headerMap) {
		request.setHeader("User-Agent", Constants.USER_AGENT);
		if (headerMap != null) {
			for (Entry<String, String> entry : headerMap.entrySet()) {
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private HttpEntity execute(HttpRequestBase request, CookieStore cookieStore) {
		try {
			CloseableHttpClient httpClient = this.createClient();
			holder.set(request);
			if (cookieStore != null) {
				HttpContext context = new BasicHttpContext();
				context.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
				CloseableHttpResponse response = httpClient.execute(request, context);
				return response.getEntity();
			} else {
				CloseableHttpResponse response = httpClient.execute(request);
				return response.getEntity();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	private CloseableHttpClient createClient() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().setConnectionManager(this.connectionManager).build();
		}
		return httpClient;
	}

	public void close() {
		if (holder.get() != null) {
			holder.get().releaseConnection();
		}
	}

}
