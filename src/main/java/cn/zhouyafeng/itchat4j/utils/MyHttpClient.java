package cn.zhouyafeng.itchat4j.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP访问类，对Apache HttpClient进行简单封装，适配器模式
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月9日 下午7:05:04
 * @version 1.0
 *
 */
public class MyHttpClient {

	private static final Logger logger = LoggerFactory.getLogger(MyHttpClient.class);
	private static CloseableHttpClient httpClient = HttpClients.createDefault();

	private static MyHttpClient instance = null;

	private static CookieStore cookieStore;
	private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36";
	private static Header agentHeader;
	private static HttpHost proxy = new HttpHost("172.18.13.171", 3128, HttpHost.DEFAULT_SCHEME_NAME);// 代理
	static {
		cookieStore = new BasicCookieStore();
		agentHeader = new BasicHeader("User-Agent", USER_AGENT);
		// 将CookieStore设置到httpClient中
		logger.info("proxy:" + proxy.getHostName() + ":" + proxy.getPort());
		httpClient = HttpClients.custom().setProxy(proxy).setDefaultCookieStore(cookieStore)
				.setDefaultHeaders(Arrays.asList(agentHeader)).build();
	}

	public static String getCookie(String name) {
		final List<Cookie> cookies = cookieStore.getCookies();
		for (final Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) {
				return cookie.getValue();
			}
		}
		return null;

	}

	public static void setCookie(CookieStore dumpCookieStore) {
		if (dumpCookieStore != null) {
			cookieStore = dumpCookieStore;
			try {
				httpClient.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			httpClient = HttpClients.custom().setProxy(proxy).setDefaultCookieStore(cookieStore)
					.setDefaultHeaders(Arrays.asList(agentHeader)).build();
		}
	}

	private MyHttpClient() {

	}

	/**
	 * 获取cookies
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月7日 下午8:37:17
	 * @return
	 */
	public static MyHttpClient getInstance() {
		if (instance == null) {
			synchronized (MyHttpClient.class) {
				if (instance == null) {
					instance = new MyHttpClient();
				}
			}
		}
		return instance;
	}

	/**
	 * 处理GET请求
	 *
	 * @author https://github.com/yaphone
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
				final String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
				httpGet = new HttpGet(url + "?" + paramStr);
			} else {
				httpGet = new HttpGet(url);
			}
			if (!redirect) {
				httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
			}
			httpGet.setHeader("User-Agent", Config.USER_AGENT);
			if (headerMap != null) {
				final Set<Entry<String, String>> entries = headerMap.entrySet();
				for (final Entry<String, String> entry : entries) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			final CloseableHttpResponse response = httpClient.execute(httpGet);
			entity = response.getEntity();
		} catch (final ClientProtocolException e) {
			logger.error("", e);
		} catch (final IOException e) {
			logger.error("", e);
		}

		return entity;
	}

	/**
	 * 处理POST请求
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午7:06:35
	 * @param url
	 * @param params
	 * @return
	 */
	public HttpEntity doPost(String url, String paramsStr) {
		HttpEntity entity = null;
		HttpPost httpPost = new HttpPost();
		try {
			final StringEntity params = new StringEntity(paramsStr, Consts.UTF_8);
			httpPost = new HttpPost(url);
			httpPost.setEntity(params);
			httpPost.setHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("User-Agent", Config.USER_AGENT);
			final CloseableHttpResponse response = httpClient.execute(httpPost);
			entity = response.getEntity();
		} catch (final ClientProtocolException e) {
			logger.info(e.getMessage());
		} catch (final IOException e) {
			logger.info(e.getMessage());
		}

		return entity;
	}

	/**
	 * 上传文件到服务器
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月7日 下午9:19:23
	 * @param url
	 * @param reqEntity
	 * @return
	 */
	public HttpEntity doPostFile(String url, HttpEntity reqEntity) {
		HttpEntity entity = null;
		final HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("User-Agent", Config.USER_AGENT);
		httpPost.setEntity(reqEntity);
		try {
			final CloseableHttpResponse response = httpClient.execute(httpPost);
			entity = response.getEntity();

		} catch (final Exception e) {
			logger.info(e.getMessage());
		}
		return entity;
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public static CookieStore getCookieStore() {
		return cookieStore;
	}

}