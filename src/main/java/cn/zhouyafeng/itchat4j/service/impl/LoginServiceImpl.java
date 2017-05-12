package cn.zhouyafeng.itchat4j.service.impl;

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
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.core.MsgCenter;
import cn.zhouyafeng.itchat4j.service.ILoginService;
import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTool;

/**
 * 登陆服务实现类
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月13日 上午12:09:35
 * @version 1.0
 *
 */
public class LoginServiceImpl implements ILoginService {
	private static Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);
	private String baseUrl = Config.BASE_URL;
	private boolean isLOGinIn = false;

	private Core core = Core.getInstance();

	private MyHttpClient myHttpClient = core.getMyHttpClient();

	public LoginServiceImpl() {

	}

	@Override
	public boolean login(String qrPath) {
		if (core.isAlive()) { // 已登陆
			LOG.info("itchat has already LOGged in.");
			return true;
		}
		while (true) {
			for (int count = 0; count < 10; count++) {
				LOG.info("Getting uuid of QR code.");
				while (getUuid() == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						LOG.info(e.getMessage());
					}
				}
				LOG.info("Downloading QR code.");
				Boolean qrStarge = getQR(qrPath);
				if (qrStarge) { // 获取登陆二维码图片成功
					LOG.info("Get QR success");
					break;
				} else if (count == 10) {
					LOG.info("Failed to get QR code, please restart the program.");
					System.exit(0);
				}
			}
			LOG.info("Please scan the QR code to LOG in.");
			while (!isLOGinIn) {
				String status = checkLogin();
				if (status.equals("200")) {
					isLOGinIn = true;
					LOG.info(("登陆成功"));
				} else if (status.equals("201")) {
					LOG.info("Please press confirm on your phone.");
					isLOGinIn = false;
				} else {
					break;
				}
			}
			if (isLOGinIn)
				break;
			LOG.info("LOG in time out, reloading QR code");
		}
		this.webWxInit();
		this.wxStatusNotify();
		CommonTool.clearScreen();
		LOG.info(String.format("LOGin successfully as %s", core.getNickName()));
		startReceiving();
		return true;
	}

	@Override
	public String getUuid() {
		String result = null;
		String uuidUrl = baseUrl + "/jslogin";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("appid", "wx782c26e4c19acffb"));
		params.add(new BasicNameValuePair("fun", "new"));
		HttpEntity entity = myHttpClient.doGet(uuidUrl, params, true, null);
		try {
			result = EntityUtils.toString(entity);
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
		Matcher matcher = CommonTool.getMatcher(regEx, result);
		if (matcher.find()) {
			if ((matcher.group(1).equals("200"))) {
				core.setUuid(matcher.group(2));//
			}
		}
		return core.getUuid();
	}

	@Override
	public boolean getQR(String qrPath) {
		qrPath = qrPath + File.separator + "QR.jpg";
		String qrUrl = baseUrl + "/qrcode/" + core.getUuid();
		HttpEntity entity = myHttpClient.doGet(qrUrl, null, true, null);
		try {
			OutputStream out = new FileOutputStream(qrPath);
			byte[] bytes = EntityUtils.toByteArray(entity);
			out.write(bytes);
			out.flush();
			out.close();
			try {
				CommonTool.printQr(qrPath); // 打开登陆二维码图片
			} catch (Exception e) {
				LOG.info(e.getMessage());
			}

		} catch (Exception e) {
			LOG.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean webWxInit() {
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
			core.getLoginInfo().put("User", CommonTool.structFriendInfo(obj.getJSONObject("User"))); // 为userObj添加新字段
			core.getLoginInfo().put("SyncKey", obj.getJSONObject("SyncKey"));
			JSONArray syncArray = obj.getJSONObject("SyncKey").getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			String synckey = sb.toString();
			core.getLoginInfo().put("synckey", synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			core.setUserName((obj.getJSONObject("User")).getString("UserName"));
			core.setNickName((obj.getJSONObject("User")).getString("NickName"));
			core.setUserSelf(obj.getJSONObject("User"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void wxStatusNotify() {
		String url = (String) core.getLoginInfo().get("url");
		String passTicket = (String) core.getLoginInfo().get("pass_ticket");
		String mobileUrl = String.format("%s/webwxstatusnotify?lang=zh_CN&pass_ticket=%s", url, passTicket);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> baseRequestMap = (Map<String, Map<String, String>>) core.getLoginInfo()
				.get("baseRequest");
		paramMap.put("BaseRequest", baseRequestMap.get("BaseRequest"));
		paramMap.put("Code", 3);
		paramMap.put("FromUserName", core.getUserName());
		paramMap.put("ToUserName", core.getUserName());
		paramMap.put("ClientMsgId", String.valueOf(new Date().getTime()));
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(mobileUrl, paramStr);
			EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {

		}

	}

	@Override
	public void startReceiving() {
		core.setAlive(true);
		new Thread(new Runnable() {
			int retryCount = 0;

			@Override
			public void run() {
				while (core.isAlive()) {
					try {
						Map<String, String> resultMap = syncCheck();
						String retcode = resultMap.get("retcode");
						String selector = resultMap.get("selector");
						if (retcode.equals("9999")) {
							continue;
						} else if (retcode.equals("1100")) { // 退出
							LOG.info("login out");
							break;
						} else if (retcode.equals("1101")) { // 其它地方登陆
							LOG.info("login otherwhere");
							break;
						} else if (retcode.equals("1102")) { // 移动端退出
							LOG.info("login quit on phone");
							break;
						} else if (retcode.equals("0")) {
							if (selector.equals("2")) {
								JSONObject msgObj = webWxSync();
								if (msgObj != null) {
									try {
										JSONArray msgList = new JSONArray();
										msgList = msgObj.getJSONArray("AddMsgList");
										msgList = MsgCenter.produceMsg(msgList);
										for (int j = 0; j < msgList.size(); j++) {
											core.getMsgList().add(msgList.getJSONObject(j));
										}
									} catch (Exception e) {
										LOG.info(e.getMessage());
									}
								} else if (selector.equals("7")) {
									webWxSync();
								} else if (selector.equals("4")) {
									// 保存群聊到通讯录
									// 修改群名称
									// 新增或删除联系人
									// 群聊成员数目变化
									// TODO
								} else if (selector.equals("3") || selector.equals("6")) {
									break;
								}
							}
						} else {
							JSONObject obj = webWxSync();
							LOG.info(obj.toJSONString());
						}
					} catch (Exception e) {
						LOG.info(e.getMessage());
						retryCount += 1;
						if (core.getReceivingRetryCount() < retryCount) {
							core.setAlive(false);
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
								LOG.info(e.getMessage());
							}
						}
					}

				}
			}
		}).start();

	}

	/**
	 * 检查登陆状态
	 * 
	 * @author https://github.com/yaphone
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
			LOG.info(e.getMessage());
		}
		String regEx = "window.code=(\\d+)";
		Matcher matcher = CommonTool.getMatcher(regEx, result);
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
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	private void processLoginInfo(String loginContent) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = CommonTool.getMatcher(regEx, loginContent);
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
				LOG.info(e.getMessage());
				return;
			}
			Document doc = CommonTool.xmlParser(text);
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
				baseRequest.put("DeviceID", (String) core.getLoginInfo().get("deviceid"));
				BaseRequest.put("BaseRequest", baseRequest);
				core.getLoginInfo().put("baseRequest", BaseRequest);
			}

		}
	}

	private Map<String, List<String>> getPossibleUrlMap() {
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

	/**
	 * 同步消息 sync the messages
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月12日 上午12:24:55
	 * @return
	 */
	private JSONObject webWxSync() {
		JSONObject result = null;
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
			LOG.info(e.getMessage());
		}
		return result;

	}

	/**
	 * 检查是否有新消息 check whether there's a message
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月16日 上午11:11:34
	 * @return
	 * 
	 */
	private Map<String, String> syncCheck() {
		Map<String, String> resultMap = new HashMap<String, String>();
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
			if (entity == null) {
				resultMap.put("retcode", "9999");
				resultMap.put("selector", "9999");
				return resultMap;
			}
			String text = EntityUtils.toString(entity);
			String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
			Matcher matcher = CommonTool.getMatcher(regEx, text);
			if (!matcher.find() || matcher.group(1).equals("2")) {
				LOG.info(String.format("Unexpected sync check result: %s", text));
			} else {
				resultMap.put("retcode", matcher.group(1));
				resultMap.put("selector", matcher.group(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

}
