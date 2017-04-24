package cn.zhouyafeng.itchat4j.components;

import java.io.File;
import java.io.FileOutputStream;
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

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.Contact;
import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.Tools;

public class Login {
	private static Logger logger = Logger.getLogger("Wechat");
	private String baseUrl = Config.BASE_URL;
	private boolean isLoginIn = false;
	private Contact contact = new Contact();

	private Core core = Core.getInstance();

	private MyHttpClient myHttpClient = core.getMyHttpClient();

	public Login() {

	}

	public int login(String qrPath) {
		if (core.isAlive()) { // 已登陆
			logger.warning("itchat has already logged in.");
			return 0;
		}
		while (true) {
			for (int count = 0; count < 10; count++) {
				logger.info("Getting uuid of QR code.");
				while (getQRuuid() == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.info(e.getMessage());
					}
				}
				logger.info("Downloading QR code.");
				Boolean qrStarge = getQR(qrPath);
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
					logger.info(("登陆成功"));
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
		this.showMobileLogin();
		contact.getContact(true);
		Tools.clearScreen();
		logger.info(String.format("Login successfully as %s", core.getStorageClass().getNickName()));
		startReceiving();
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
		String result = null;
		String uuidUrl = baseUrl + "/jslogin";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("appid", "wx782c26e4c19acffb"));
		params.add(new BasicNameValuePair("fun", "new"));
		HttpEntity entity = myHttpClient.doGet(uuidUrl, params, true, null);
		try {
			result = EntityUtils.toString(entity);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
		Matcher matcher = Tools.getMatcher(regEx, result);
		if (matcher.find()) {
			if ((matcher.group(1).equals("200"))) {
				core.setUuid(matcher.group(2));//
			}
		}
		return core.getUuid();
	}

	/**
	 * 获取登陆二维码图片
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月20日 下午11:44:08
	 * @return
	 */
	public boolean getQR(String qrPath) {
		// String qrPath = Config.getLocalPath() + File.separator + "QR.jpg";
		qrPath = qrPath + File.separator + "QR.jpg";
		String qrUrl = baseUrl + "/qrcode/" + core.getUuid();
		HttpEntity entity = myHttpClient.doGet(qrUrl, null, true, null);
		try {
			OutputStream out = new FileOutputStream(qrPath);
			byte[] bytes = EntityUtils.toByteArray(entity);
			out.write(bytes);
			out.flush();
			out.close();
			Tools.printQr(qrPath);

		} catch (Exception e) {
			logger.info(e.getMessage());
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
		params.add(new BasicNameValuePair("uuid", core.getUuid()));
		params.add(new BasicNameValuePair("tip", "0"));
		params.add(new BasicNameValuePair("r", String.valueOf(localTime / 1579L)));
		params.add(new BasicNameValuePair("_", String.valueOf(localTime)));
		HttpEntity entity = myHttpClient.doGet(checkUrl, params, true, null);
		try {
			result = EntityUtils.toString(entity);
		} catch (Exception e) {
			logger.info(e.getMessage());
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

	/**
	 * 处理登陆信息
	 *
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	public void processLoginInfo(String loginContent) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = Tools.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			core.getLoginInfo().put("url", url);
			Map<String, List<String>> possibleUrlMap = getPossibleUrlMap();
			Iterator<Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			Map.Entry<String, List<String>> entry;
			String fileUrl;
			String syncUrl;
			while (iterator.hasNext()) {
				entry = iterator.next();
				String indexUrl = entry.getKey();
				fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (core.getLoginInfo().get("url").toString().contains(indexUrl)) {
					core.getLoginInfo().put("fileUrl", fileUrl);
					core.getLoginInfo().put("syncUrl", syncUrl);
					break;
				}
			}
			if (core.getLoginInfo().get("fileUrl") == null && core.getLoginInfo().get("syncUrl") == null) {
				core.getLoginInfo().put("fileUrl", url);
				core.getLoginInfo().put("syncUrl", url);
			}
			core.getLoginInfo().put("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			core.getLoginInfo().put("BaseRequest", new ArrayList<String>());
			String text = "";
			// // 禁止重定向

			try {
				HttpEntity entity = myHttpClient.doGet(originalUrl, null, false, null);
				text = EntityUtils.toString(entity);
			} catch (Exception e) {
				logger.info(e.getMessage());
				return;
			}
			Document doc = Tools.xmlParser(text);
			Map<String, Map<String, String>> BaseRequest = new HashMap<String, Map<String, String>>();
			Map<String, String> baseRequest = new HashMap<String, String>();
			if (doc != null) {
				core.getLoginInfo().put("skey",
						doc.getElementsByTagName("skey").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Skey", (String) core.getLoginInfo().get("skey"));
				core.getLoginInfo().put("wxsid",
						doc.getElementsByTagName("wxsid").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Sid", (String) core.getLoginInfo().get("wxsid"));
				core.getLoginInfo().put("wxuin",
						doc.getElementsByTagName("wxuin").item(0).getFirstChild().getNodeValue());
				baseRequest.put("Uin", (String) core.getLoginInfo().get("wxuin"));
				core.getLoginInfo().put("pass_ticket",
						doc.getElementsByTagName("pass_ticket").item(0).getFirstChild().getNodeValue());
				baseRequest.put("DeviceID", (String) core.getLoginInfo().get("pass_ticket"));
				BaseRequest.put("BaseRequest", baseRequest);
				core.getLoginInfo().put("baseRequest", BaseRequest);
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

	private JSONObject webInit() {
		JSONObject obj = null;
		String url = core.getLoginInfo().get("url") + "/webwxinit?&r=" + String.valueOf(new Date().getTime());
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> paramMap = (Map<String, Map<String, String>>) core.getLoginInfo()
				.get("baseRequest");
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(url, paramStr);
			String result = EntityUtils.toString(entity, "UTF-8");
			obj = JSON.parseObject(result);
			// TODO utils.emoji_formatter(dic['User'], 'NickName')
			core.getLoginInfo().put("InviteStartCount", obj.getInteger("InviteStartCount"));
			core.getLoginInfo().put("User", Tools.structFriendInfo(obj.getJSONObject("User"))); // 为userObj添加新字段
			core.getLoginInfo().put("SyncKey", obj.getJSONObject("SyncKey"));
			JSONArray syncArray = obj.getJSONObject("SyncKey").getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			String synckey = sb.toString();
			core.getLoginInfo().put("synckey", synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			core.getStorageClass().setUserName((obj.getJSONObject("User")).getString("UserName"));
			core.getStorageClass().setNickName((obj.getJSONObject("User")).getString("NickName"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	private void showMobileLogin() {
		String url = (String) core.getLoginInfo().get("url");
		String passTicket = (String) core.getLoginInfo().get("pass_ticket");
		String mobileUrl = String.format("%s/webwxstatusnotify?lang=zh_CN&pass_ticket=%s", url, passTicket);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> baseRequestMap = (Map<String, Map<String, String>>) core.getLoginInfo()
				.get("baseRequest");
		paramMap.put("BaseRequest", baseRequestMap.get("BaseRequest"));
		paramMap.put("Code", 3);
		paramMap.put("FromUserName", core.getStorageClass().getUserName());
		paramMap.put("ToUserName", core.getStorageClass().getUserName());
		paramMap.put("ClientMsgId", String.valueOf(new Date().getTime()));
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(mobileUrl, paramStr);
			EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {

		}
	}

	void startReceiving() {
		core.setAlive(true);
		new Thread(new Runnable() {
			int retryCount = 0;

			public void run() {
				while (core.isAlive()) {
					try {
						String i = syncCheck();
						if (i == null) {
							core.setAlive(false);
						} else if (i.equals("0")) {
							continue;
						} else {
							JSONArray msgList = new JSONArray();
							JSONArray contactList = new JSONArray();
							JSONObject msgObj = getMsg();
							if (msgObj != null) {
								msgList = msgObj.getJSONArray("AddMsgList");
								contactList = msgObj.getJSONArray("ModContactList");
								msgList = Message.produceMsg(msgList);
								for (int j = 0; j < msgList.size(); j++) {
									core.getMsgList().add(msgList.getJSONObject(j));
								}
								JSONArray chatroomList = new JSONArray();
								JSONArray otherList = new JSONArray();
								for (int k = 0; k < contactList.size(); k++) {
									if (contactList.getString(k).contains("@@")) {
										chatroomList.add(contactList.getString(k));
									} else {
										otherList.add(contactList.getString(k));
									}
								}
								// TODO chatroomMsg =
								// update_local_chatrooms(self, chatroomList)
								// TODO self.msgList.put(chatroomMsg)
								// TODO update_local_friends(self, otherList)
							}
						}
						retryCount = 0;

					} catch (Exception e) {
						logger.info(e.getMessage());
						retryCount += 1;
						if (core.getReceivingRetryCount() < retryCount) {
							core.setAlive(false);
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								logger.info(e.getMessage());
							}
						}
					}

				}
			}
		}).start();
	}

	/**
	 * 保活心跳
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月16日 上午11:11:34
	 * @return
	 */
	String syncCheck() {
		String result = null;
		String syncUrl = (String) core.getLoginInfo().get("syncUrl");
		// String syncUrl = "https://webpush.wx2.qq.com/cgi-bin/mmwebwx-bin";
		if (syncUrl == null || syncUrl.equals("")) {
			syncUrl = (String) core.getLoginInfo().get("url");
		}
		String url = String.format("%s/synccheck", syncUrl);
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("r", String.valueOf(new Date().getTime())));
		params.add(new BasicNameValuePair("skey", (String) core.getLoginInfo().get("skey")));
		params.add(new BasicNameValuePair("sid", (String) core.getLoginInfo().get("wxsid")));
		params.add(new BasicNameValuePair("uin", (String) core.getLoginInfo().get("wxuin")));
		params.add(new BasicNameValuePair("deviceid", (String) core.getLoginInfo().get("deviceid")));
		params.add(new BasicNameValuePair("synckey", (String) core.getLoginInfo().get("synckey")));
		params.add(new BasicNameValuePair("_", String.valueOf(new Date().getTime())));
		try {
			HttpEntity entity = myHttpClient.doGet(url, params, true, null);
			String text = EntityUtils.toString(entity);
			String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
			Matcher matcher = Tools.getMatcher(regEx, text);
			if (!matcher.find() || matcher.group(1).equals("2")) {
				logger.info(String.format("Unexpected sync check result: %s", text));
			} else {
				result = matcher.group(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	JSONObject getMsg() {
		JSONObject result = new JSONObject();
		String url = String.format("%s/webwxsync?sid=%s&skey=%s&pass_ticket=%s", core.getLoginInfo().get("url"),
				core.getLoginInfo().get("wxsid"), core.getLoginInfo().get("skey"),
				core.getLoginInfo().get("pass_ticket"));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> baseRequestMap = (Map<String, Map<String, String>>) core.getLoginInfo()
				.get("baseRequest");
		paramMap.put("BaseRequest", baseRequestMap.get("BaseRequest"));
		paramMap.put("SyncKey", core.getLoginInfo().get("SyncKey"));
		paramMap.put("rr", -new Date().getTime() / 1000);
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(url, paramStr);
			String text = EntityUtils.toString(entity, "UTF-8");
			JSONObject obj = JSON.parseObject(text);
			if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
				result = null;
			} else {
				result = obj;
				core.getLoginInfo().put("SyncKey", obj.getJSONObject("SyncCheckKey"));
				JSONArray syncArray = obj.getJSONObject("SyncKey").getJSONArray("List");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < syncArray.size(); i++) {
					sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
							+ syncArray.getJSONObject(i).getString("Val") + "|");
				}
				String synckey = sb.toString();
				core.getLoginInfo().put("synckey", synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return result;

	}

}
