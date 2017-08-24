package com.yachat.wechat.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.yachat.wechat.Account;
import com.yachat.wechat.WechatSystem;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.TryRetryClient;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.LoginParaEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.StatusNotifyParaEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.UUIDParaEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class WechatSystemWebSupport implements WechatSystem {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatSystemWebSupport.class);
	private MyHttpClient httpClient = MyHttpClient.getInstance();
	private TryRetryClient retryClient = new TryRetryClient();
	
	
	@Override
	public String getUuid() {
		Request request = new Request(URLEnum.UUID_URL.getUrl());
		request.add(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value())
			.add(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value())
			.add(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value())
			.add(UUIDParaEnum.UNDERLINE.para(), String.valueOf(System.currentTimeMillis()));
		
		String uuid = retryClient.tryRetryGet(request, 3, 300 , (entity)-> {
			String result = EntityUtils.toString(entity);
			String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
			Matcher matcher = CommonTools.getMatcher(regEx, result);
			if (matcher.find()) {
				if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
					return Response.success(matcher.group(2));
				}
			}
			return Response.error();
		});
		
		return uuid;
		
//		ImmutableMap<String, String> params = ImmutableMap.of(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value(),
//				UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value(), UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value(),
//				UUIDParaEnum.UNDERLINE.para(), String.valueOf(System.currentTimeMillis()));
//		HttpEntity entity = httpClient.doGet(URLEnum.UUID_URL.getUrl(), true, params, null);
//		try {
//			String result = EntityUtils.toString(entity);
//			String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
//			Matcher matcher = CommonTools.getMatcher(regEx, result);
//			if (matcher.find()) {
//				if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
//					return matcher.group(2);
//				}
//			}
//		} catch (Exception e) {
//			LOGGER.error(e.getMessage(), e);
//		}
//		return null;
	}

	@Override
	public InputStream getQR(String uuid) {
		String qrUrl = URLEnum.QRCODE_URL.getUrl() + uuid;
		HttpEntity entity = httpClient.doGet(qrUrl, true, null, null);
		try {
			byte[] bytes = EntityUtils.toByteArray(entity);
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			return stream;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean login(Account account) {
		boolean isLogin = false;
		// long time = 4000;
		while (!isLogin) {
			// SleepUtils.sleep(time += 1000);
			long millis = System.currentTimeMillis();
			Map<String, String> params = ImmutableMap.of(LoginParaEnum.LOGIN_ICON.para(),
					LoginParaEnum.LOGIN_ICON.value(), LoginParaEnum.UUID.para(), account.getUuid(),
					LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value(), LoginParaEnum.R.para(),
					String.valueOf(millis / 1579L), LoginParaEnum._.para(), String.valueOf(millis));
			HttpEntity entity = httpClient.doGet(URLEnum.LOGIN_URL.getUrl(), true, params, null);
			try {
				String result = EntityUtils.toString(entity);
				String status = checklogin(result);
				if (ResultEnum.SUCCESS.getCode().equals(status)) {
					processLoginInfo(result, account); // 处理结果
					isLogin = true;
					// core.setAlive(isLogin);
					account.setAlive(isLogin);
					break;
				}
				if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
					LOGGER.info("请点击微信确认按钮，进行登陆");
				}

			} catch (Exception e) {
				LOGGER.error("微信登陆异常！", e);
			}
		}
		return isLogin;
	}

	/**
	 * 检查登陆状态
	 *
	 * @param result
	 * @return
	 */
	private String checklogin(String result) {
		String regEx = "window.code=(\\d+)";
		Matcher matcher = CommonTools.getMatcher(regEx, result);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * 处理登陆信息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	private void processLoginInfo(String loginContent, Account account) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = CommonTools.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			account.getLoginInfo().put("url", url);
			Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
			Iterator<Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			Map.Entry<String, List<String>> entry;
			String fileUrl;
			String syncUrl;
			while (iterator.hasNext()) {
				entry = iterator.next();
				String indexUrl = entry.getKey();
				fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (account.getLoginInfo().get("url").toString().contains(indexUrl)) {
					account.setIndexUrl(indexUrl);
					account.getLoginInfo().put("fileUrl", fileUrl);
					account.getLoginInfo().put("syncUrl", syncUrl);
					break;
				}
			}
			if (account.getLoginInfo().get("fileUrl") == null && account.getLoginInfo().get("syncUrl") == null) {
				account.getLoginInfo().put("fileUrl", url);
				account.getLoginInfo().put("syncUrl", url);
			}
			account.getLoginInfo().put("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			account.getLoginInfo().put("BaseRequest", new ArrayList<String>());
			String text = "";

			try {
				HttpEntity entity = httpClient.doGet(originalUrl, null, false, null);
				text = EntityUtils.toString(entity);
			} catch (Exception e) {
				LOGGER.info(e.getMessage());
				return;
			}
			// add by 默非默 2017-08-01 22:28:09
			// 如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
			String msg = getLoginMessage(text);
			if (!"".equals(msg)) {
				LOGGER.info(msg);
				throw new RuntimeException("The wechat login error.");
			}
			Document doc = CommonTools.xmlParser(text);
			if (doc != null) {
				account.getLoginInfo().put(StorageLoginInfoEnum.skey.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.skey.getKey()).item(0).getFirstChild()
								.getNodeValue());
				account.getLoginInfo().put(StorageLoginInfoEnum.wxsid.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxsid.getKey()).item(0).getFirstChild()
								.getNodeValue());
				account.getLoginInfo().put(StorageLoginInfoEnum.wxuin.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxuin.getKey()).item(0).getFirstChild()
								.getNodeValue());
				account.getLoginInfo().put(StorageLoginInfoEnum.pass_ticket.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.pass_ticket.getKey()).item(0).getFirstChild()
								.getNodeValue());
			}
		}
	}

	/**
	 * 解析登录返回的消息，如果成功登录，则message为空
	 * 
	 * @param result
	 * @return
	 */
	private String getLoginMessage(String result) {
		String[] strArr = result.split("<message>");
		String[] rs = strArr[1].split("</message>");
		if (rs != null && rs.length > 1) {
			return rs[0];
		}
		return "";
	}

	private Map<String, List<String>> getPossibleUrlMap() {
		Map<String, List<String>> possibleUrlMap = new HashMap<String, List<String>>();
		possibleUrlMap.put("wx.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});

		possibleUrlMap.put("wx2.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx2.qq.com");
				add("webpush.wx2.qq.com");
			}
		});
		possibleUrlMap.put("wx8.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx8.qq.com");
				add("webpush.wx8.qq.com");
			}
		});

		possibleUrlMap.put("web2.wechat.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.web2.wechat.com");
				add("webpush.web2.wechat.com");
			}
		});
		possibleUrlMap.put("wechat.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.web.wechat.com");
				add("webpush.web.wechat.com");
			}
		});
		return possibleUrlMap;
	}

	@Override
	public boolean webWxInit(Account account) {
		account.setAlive(true);
		account.setLastNormalRetcodeTime(System.currentTimeMillis());
		// 组装请求URL和参数
		String url = String.format(URLEnum.INIT_URL.getUrl(),
				account.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
				String.valueOf(System.currentTimeMillis() / 3158L),
				account.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		Map<String, Object> paramMap = account.getParamMap();

		// 请求初始化接口
		HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));
		try {
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject obj = JSON.parseObject(result);

			JSONObject user = obj.getJSONObject(StorageLoginInfoEnum.User.getKey());
			JSONObject syncKey = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey());

			account.getLoginInfo().put(StorageLoginInfoEnum.InviteStartCount.getKey(),
					obj.getInteger(StorageLoginInfoEnum.InviteStartCount.getKey()));
			account.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), syncKey);

			JSONArray syncArray = syncKey.getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			// 1_661706053|2_661706420|3_661706415|1000_1494151022|
			String synckey = sb.toString();

			// 1_661706053|2_661706420|3_661706415|1000_1494151022
			account.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(),
					synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			account.setUserName(user.getString("UserName"));
			account.setNickName(user.getString("NickName"));
			account.setUserSelf(obj.getJSONObject("User"));

			String chatSet = obj.getString("ChatSet");
			String[] chatSetArray = chatSet.split(",");
			for (int i = 0; i < chatSetArray.length; i++) {
				if (chatSetArray[i].indexOf("@@") != -1) {
					// 更新GroupIdList
					account.getGroupIdList().add(chatSetArray[i]); //
				}
			}
			// JSONArray contactListArray = obj.getJSONArray("ContactList");
			// for (int i = 0; i < contactListArray.size(); i++) {
			// JSONObject o = contactListArray.getJSONObject(i);
			// if (o.getString("UserName").indexOf("@@") != -1) {
			// core.getGroupIdList().add(o.getString("UserName")); //
			// // 更新GroupIdList
			// core.getGroupList().add(o); // 更新GroupList
			// core.getGroupNickNameList().add(o.getString("NickName"));
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void wxStatusNotify(Account account) {
		// 组装请求URL和参数
		String url = String.format(URLEnum.STATUS_NOTIFY_URL.getUrl(),
				account.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		Map<String, Object> paramMap = account.getParamMap();
		paramMap.put(StatusNotifyParaEnum.CODE.para(), StatusNotifyParaEnum.CODE.value());
		paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para(), account.getUserName());
		paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para(), account.getUserName());
		paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para(), System.currentTimeMillis());
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = httpClient.doPost(url, paramStr);
			EntityUtils.toString(entity, Consts.UTF_8);
		} catch (Exception e) {
			LOGGER.error("微信状态通知接口失败！", e);
		}
	}

	@Override
	public void startReceiving(Account account) {
		

	}

	@Override
	public void webWxGetContact(Account account) {

		String url = String.format(URLEnum.WEB_WX_GET_CONTACT.getUrl(),
				account.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
		Map<String, Object> paramMap = account.getParamMap();
		HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));

		try {
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject fullFriendsJsonList = JSON.parseObject(result);
			// 查看seq是否为0，0表示好友列表已全部获取完毕，若大于0，则表示好友列表未获取完毕，当前的字节数（断点续传）
			long seq = 0;
			long currentTime = 0L;
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			if (fullFriendsJsonList.get("Seq") != null) {
				seq = fullFriendsJsonList.getLong("Seq");
				currentTime = new Date().getTime();
			}
			account.setMemberCount(fullFriendsJsonList.getInteger(StorageLoginInfoEnum.MemberCount.getKey()));
			JSONArray member = fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey());
			// 循环获取seq直到为0，即获取全部好友列表 ==0：好友获取完毕 >0：好友未获取完毕，此时seq为已获取的字节数
			while (seq > 0) {
				// 设置seq传参
				params.add(new BasicNameValuePair("r", String.valueOf(currentTime)));
				params.add(new BasicNameValuePair("seq", String.valueOf(seq)));
				entity = httpClient.doGet(url, params, false, null);

				params.remove(new BasicNameValuePair("r", String.valueOf(currentTime)));
				params.remove(new BasicNameValuePair("seq", String.valueOf(seq)));

				result = EntityUtils.toString(entity, Consts.UTF_8);
				fullFriendsJsonList = JSON.parseObject(result);

				if (fullFriendsJsonList.get("Seq") != null) {
					seq = fullFriendsJsonList.getLong("Seq");
					currentTime = new Date().getTime();
				}

				// 累加好友列表
				member.addAll(fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey()));
			}
			account.setMemberCount(member.size());
			for (Iterator<?> iterator = member.iterator(); iterator.hasNext();) {
				JSONObject o = (JSONObject) iterator.next();
				if ((o.getInteger("VerifyFlag") & 8) != 0) { // 公众号/服务号
					account.getPublicUsersList().add(o);
				} else if (Config.API_SPECIAL_USER.contains(o.getString("UserName"))) { // 特殊账号
					account.getSpecialUsersList().add(o);
				} else if (o.getString("UserName").indexOf("@@") != -1) { // 群聊
					if (!account.getGroupIdList().contains(o.getString("UserName"))) {
						account.getGroupNickNameList().add(o.getString("NickName"));
						account.getGroupIdList().add(o.getString("UserName"));
						account.getGroupList().add(o);
					}
				} else if (o.getString("UserName").equals(account.getUserSelf().getString("UserName"))) { // 自己
					account.getContactList().remove(o);
				} else { // 普通联系人
					account.getContactList().add(o);
				}
			}
			return;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return;
	}

	@Override
	public void WebWxBatchGetContact(Account account) {
		String url = String.format(URLEnum.WEB_WX_BATCH_GET_CONTACT.getUrl(),
				account.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()), new Date().getTime(),
				account.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		Map<String, Object> paramMap = account.getParamMap();
		paramMap.put("Count", account.getGroupIdList().size());
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < account.getGroupIdList().size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("UserName", account.getGroupIdList().get(i));
			map.put("EncryChatRoomId", "");
			list.add(map);
		}
		paramMap.put("List", list);
		HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));
		try {
			String text = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject obj = JSON.parseObject(text);
			JSONArray contactList = obj.getJSONArray("ContactList");
			for (int i = 0; i < contactList.size(); i++) { // 群好友
				if (contactList.getJSONObject(i).getString("UserName").indexOf("@@") > -1) { // 群
					account.getGroupNickNameList().add(contactList.getJSONObject(i).getString("NickName")); // 更新群昵称列表
					account.getGroupList().add(contactList.getJSONObject(i)); // 更新群信息（所有）列表
					account.getGroupMemeberMap().put(contactList.getJSONObject(i).getString("UserName"),
							contactList.getJSONObject(i).getJSONArray("MemberList")); // 更新群成员Map
				}
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
		}
	}

}
