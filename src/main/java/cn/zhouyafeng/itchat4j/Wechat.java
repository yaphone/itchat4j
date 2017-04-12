package cn.zhouyafeng.itchat4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.Tools;

public class Wechat {
	private boolean alive = false;
	private String uuid = null;
	private String baseUrl = Config.BASE_URL;
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private static Logger logger = Logger.getLogger("Wechat");
	private boolean isLoginIn = false;
	private Map<String, Object> loginInfo = new HashMap<String, Object>();

	public static HttpClientContext context = null;
	public static CookieStore cookieStore = null;
	public static RequestConfig requestConfig = null;

	Wechat() {

		System.setProperty("jsse.enableSNIExtension", "false");
		// getQRuuid();
	}

	public int login() throws InterruptedException {
		if (alive) { // 已登陆
			logger.warning("itchat has already logged in.");
			return 0;
		}
		while (true) {
			for (int count = 0; count < 10; count++) {
				logger.info("Getting uuid of QR code.");
				while (getQRuuid() == null) {
					Thread.sleep(1000);
				}
				logger.info("Downloading QR code.");
				Boolean qrStarge = getQR();
				if (qrStarge) { // 获取登陆二维码图片成功
					logger.info("Get QR success");
					break;
				} else if (count == 10) {
					logger.info("Failed to get QR code, please restart the program.");
					System.exit(0);
				}
			}
			logger.info("Please scan the QR code to log in.");
			while (!isLoginIn) {
				String status = checkLogin();
				if (status.equals("200")) {
					isLoginIn = true;
					System.out.println("登陆成功");
				} else if (status.equals("201")) {
					logger.info("Please press confirm on your phone.");
					isLoginIn = false;
				} else {
					break;
				}
			}
			if (isLoginIn)
				break;
			logger.info("Log in time out, reloading QR code");
		}
		this.webInit();
		return 0;
	}

	/**
	 * 生成UUID
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月11日 上午12:51:29
	 * @return
	 */
	public String getQRuuid() {
		String result = "";
		String uuidUrl = baseUrl + "/jslogin";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("appid", "wx782c26e4c19acffb"));
		params.add(new BasicNameValuePair("fun", "new"));
		try {
			String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpGet httpGet = new HttpGet(uuidUrl + "?" + paramStr);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
		Matcher matcher = Tools.getMatcher(regEx, result);
		if (matcher.find()) {
			if ((matcher.group(1).equals("200"))) {
				uuid = matcher.group(2);//
				System.out.println(uuid);
			}
		}
		return uuid;
	}

	public boolean getQR() {
		String qrPath = Config.getLocalPath() + File.separator + "QR.jpg";
		String qrUrl = baseUrl + "/qrcode/" + this.uuid;
		HttpGet httpGet = new HttpGet(qrUrl);
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				OutputStream out = new FileOutputStream(qrPath);
				byte[] bytes = EntityUtils.toByteArray(entity);
				out.write(bytes);
				out.flush();
				out.close();
				Tools.printQr(qrPath);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 检查登陆状态
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月8日 下午11:22:16
	 * @return
	 */
	public String checkLogin() {
		String result = "";
		String checkUrl = baseUrl + "/cgi-bin/mmwebwx-bin/login";
		Long localTime = new Date().getTime();
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("loginicon", "true"));
		params.add(new BasicNameValuePair("uuid", uuid));
		params.add(new BasicNameValuePair("tip", "0"));
		params.add(new BasicNameValuePair("r", String.valueOf(localTime / 1579L)));
		params.add(new BasicNameValuePair("_", String.valueOf(localTime)));
		try {
			String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			HttpGet httpGet = new HttpGet(checkUrl + "?" + paramStr);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regEx = "window.code=(\\d+)";
		Matcher matcher = Tools.getMatcher(regEx, result);
		if (matcher.find()) {
			if (matcher.group(1).equals("200")) { // 已登陆
				processLoginInfo(result);
				return "200";
			} else if (matcher.group(1).equals("201")) { // 已扫描，未登陆
				return "201";
			}
		}
		return "400";
	}

	// /**
	// * 处理登陆信息
	// *
	// * @author Email:zhouyaphone@163.com
	// * @date 2017年4月9日 下午12:16:26
	// * @param result
	// */
	// public void processLoginInfo(String result) {
	// String regEx = "window.redirect_uri=\"(\\S+)\";";
	// Matcher matcher = Tools.getMatcher(regEx, result);
	// if (matcher.find()) {
	// String originalUrl = matcher.group(1);
	// String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); //
	// https:wx2.qq.com/cgi-bin/mmwebwx-bin
	// loginInfo.put("url", url);
	// Map<String, List<String>> possibleUrlMap = getPossibleUrlMap();
	// Iterator<Entry<String, List<String>>> iterator =
	// possibleUrlMap.entrySet().iterator();
	// while (iterator.hasNext()) {
	// Map.Entry<String, List<String>> entry = iterator.next();
	// String indexUrl = entry.getKey();
	// String fileUrl = "https://" + entry.getValue().get(0) +
	// "/cgi-bin/mmwebwx-bin";
	// String syncUrl = "https://" + entry.getValue().get(1) +
	// "/cgi-bin/mmwebwx-bin";
	// // System.out.println(fileUrl);
	// // System.out.println(syncUrl);
	// if (loginInfo.get("url").toString().contains(indexUrl)) {
	// loginInfo.put("fileUrl", fileUrl);
	// loginInfo.put("syncUrl", syncUrl);
	// break;
	// }
	// }
	// if (loginInfo.get("fileUrl") == null && loginInfo.get("syncUrl") == null)
	// {
	// loginInfo.put("fileUrl", url);
	// loginInfo.put("syncUrl", url);
	// }
	// loginInfo.put("deviceid", "e" + String.valueOf(new
	// Random().nextLong()).substring(1, 16)); // 生成15位随机数
	// loginInfo.put("BaseRequest", new ArrayList<String>());
	// BufferedReader br = new BufferedReader(new
	// InputStreamReader(myHttpClient.doGet(originalUrl, null)));
	// String text = "";
	// String current;
	// try {
	// while ((current = br.readLine()) != null) {
	// text += current;
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// System.out.println(text);
	// Document doc = Tools.xmlParser(text);
	// Map<String, String> baseRequest = new HashMap<String, String>();
	// if (doc != null) {
	// loginInfo.put("skey",
	// doc.getElementsByTagName("skey").item(0).getFirstChild().getNodeValue());
	// baseRequest.put("Skey", (String) loginInfo.get("skey"));
	// loginInfo.put("wxsid",
	// doc.getElementsByTagName("wxsid").item(0).getFirstChild().getNodeValue());
	// baseRequest.put("Sid", (String) loginInfo.get("wxsid"));
	// loginInfo.put("wxuin",
	// doc.getElementsByTagName("wxuin").item(0).getFirstChild().getNodeValue());
	// baseRequest.put("Uin", (String) loginInfo.get("wxuin"));
	// loginInfo.put("pass_ticket",
	// doc.getElementsByTagName("pass_ticket").item(0).getFirstChild().getNodeValue());
	// baseRequest.put("DeviceID", (String) loginInfo.get("pass_ticket"));
	// loginInfo.put("baseRequest", baseRequest);
	// }
	//
	// }
	// }

	/**
	 * 处理登陆信息
	 *
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	public void processLoginInfo(String result) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = Tools.getMatcher(regEx, result);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			loginInfo.put("url", url);
			Map<String, List<String>> possibleUrlMap = getPossibleUrlMap();
			Iterator<Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, List<String>> entry = iterator.next();
				String indexUrl = entry.getKey();
				String fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				String syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (loginInfo.get("url").toString().contains(indexUrl)) {
					loginInfo.put("fileUrl", fileUrl);
					loginInfo.put("syncUrl", syncUrl);
					break;
				}
			}
			if (loginInfo.get("fileUrl") == null && loginInfo.get("syncUrl") == null) {
				loginInfo.put("fileUrl", url);
				loginInfo.put("syncUrl", url);
			}
			loginInfo.put("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			loginInfo.put("BaseRequest", new ArrayList<String>());
			String text = "";
			HttpGet httpGet = new HttpGet(originalUrl);
			httpGet.setHeader("User-Agent", Config.USER_AGENT);
			httpGet.setConfig(RequestConfig.custom().setRedirectsEnabled(false).build()); // 禁止重定向
			try {
				CloseableHttpResponse response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					text = EntityUtils.toString(entity);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			// System.out.println(text);
			Document doc = Tools.xmlParser(text);
			Map<String, String> baseRequest = new HashMap<String, String>();
			if (doc != null) {
				loginInfo.put("skey", doc.getElementsByTagName("skey").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Skey", (String) loginInfo.get("skey"));
				loginInfo.put("wxsid", doc.getElementsByTagName("wxsid").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Sid", (String) loginInfo.get("wxsid"));
				loginInfo.put("wxuin", doc.getElementsByTagName("wxuin").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Uin", (String) loginInfo.get("wxuin"));
				loginInfo.put("pass_ticket",
						doc.getElementsByTagName("pass_ticket").item(0).getFirstChild().getNodeValue());
				baseRequest.put("DeviceID", (String) loginInfo.get("pass_ticket"));
				loginInfo.put("baseRequest", baseRequest);
			}

		}
	}

	Map<String, List<String>> getPossibleUrlMap() {
		Map<String, List<String>> possibleUrlMap = new HashMap<String, List<String>>();
		possibleUrlMap.put("wx2.qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx2.qq.com");
				add("webpush.wx2.qq.com");
			}
		});
		possibleUrlMap.put("wx8.qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx8.qq.com");
				add("webpush.wx8.qq.com");
			}
		});
		possibleUrlMap.put("qq.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});
		possibleUrlMap.put("web2.wechat.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web2.wechat.com");
				add("webpush.web2.wechat.com");
			}
		});
		possibleUrlMap.put("wechat.com", new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.web.wechat.com");
				add("webpush.web.wechat.com");
			}
		});
		return possibleUrlMap;
	}

	boolean webInit() {
		String url = loginInfo.get("url") + "/webwxinit?&r=" + String.valueOf(new Date().getTime());
		System.out.println(url);

		@SuppressWarnings("unchecked")
		Map<String, String> baseRequest = (Map<String, String>) loginInfo.get("baseRequest");
		Map<String, Map<String, String>> paramMap = new HashMap<String, Map<String, String>>();
		paramMap.put("BaseRequest", baseRequest);
		String paramsStr = JSON.toJSONString(paramMap);
		// String paramsStr = String.format(
		// "{\"BaseRequest\":{\"Uin\":\"%s\",\"Skey\":\"%s\",\"DeviceID\":\"%s\",
		// \"Sid\":\"%s\"}}",
		// baseRequest.get("Uin"), baseRequest.get("Skey"),
		// baseRequest.get("DeviceID"), baseRequest.get("Sid"));
		HttpPost request = new HttpPost(url);
		try {
			StringEntity params = new StringEntity(paramsStr);
			request.setHeader("Content-type", "application/json; charset=utf-8");
			request.setHeader("User-Agent", Config.USER_AGENT);
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			String result = EntityUtils.toString(response.getEntity(), "UTF-8");
			JSONObject obj = JSON.parseObject(result);
			logger.info(obj.getJSONObject("User").get("NickName").toString());
			// TODO utils.emoji_formatter(dic['User'], 'NickName')

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
