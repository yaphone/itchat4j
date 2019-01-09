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

import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.core.MsgCenter;
import cn.zhouyafeng.itchat4j.service.ILoginService;
import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.SleepUtils;
import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.enums.RetCodeEnum;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.BaseParaEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.LoginParaEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.StatusNotifyParaEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.UUIDParaEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

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

	private final Core core = Core.getInstance();
	private final MyHttpClient httpClient = core.getMyHttpClient();

	private final MyHttpClient myHttpClient = core.getMyHttpClient();

	public LoginServiceImpl() {

	}

	@Override
	public boolean login() {

		boolean isLogin = false;
		// 组装参数和URL
		final List<BasicNameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(LoginParaEnum.LOGIN_ICON.para(), LoginParaEnum.LOGIN_ICON.value()));
		params.add(new BasicNameValuePair(LoginParaEnum.UUID.para(), core.getUuid()));
		params.add(new BasicNameValuePair(LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value()));

		// long time = 4000;
		while (!isLogin) {
			// SleepUtils.sleep(time += 1000);
			final long millis = System.currentTimeMillis();
			params.add(new BasicNameValuePair(LoginParaEnum.R.para(), String.valueOf(millis / 1579L)));
			params.add(new BasicNameValuePair(LoginParaEnum._.para(), String.valueOf(millis)));
			final HttpEntity entity = httpClient.doGet(URLEnum.LOGIN_URL.getUrl(), params, true, null);

			try {
				final String result = EntityUtils.toString(entity);
				final String status = checklogin(result);

				if (ResultEnum.SUCCESS.getCode().equals(status)) {
					processLoginInfo(result); // 处理结果
					isLogin = true;
					core.setAlive(isLogin);
					break;
				}
				if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
					LOG.info("请点击微信确认按钮，进行登陆");
				}

			} catch (final Exception e) {
				LOG.error("微信登陆异常！", e);
			}
		}
		return isLogin;
	}

	@Override
	public String getUuid() {
		// 组装参数和URL
		final List<BasicNameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value()));
		params.add(new BasicNameValuePair(UUIDParaEnum._.para(), String.valueOf(System.currentTimeMillis())));

		final HttpEntity entity = httpClient.doGet(URLEnum.UUID_URL.getUrl(), params, true, null);

		try {
			final String result = EntityUtils.toString(entity);
			final String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
			final Matcher matcher = CommonTools.getMatcher(regEx, result);
			if (matcher.find()) {
				if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
					core.setUuid(matcher.group(2));
				}
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return core.getUuid();
	}

	@Override
	public boolean getQR(String qrPath) {
		qrPath = qrPath + File.separator + "QR.jpg";
		final String qrUrl = URLEnum.QRCODE_URL.getUrl() + core.getUuid();
		final HttpEntity entity = myHttpClient.doGet(qrUrl, null, true, null);
		try {
			final OutputStream out = new FileOutputStream(qrPath);
			final byte[] bytes = EntityUtils.toByteArray(entity);
			out.write(bytes);
			out.flush();
			out.close();
			try {
				CommonTools.printQr(qrPath); // 打开登陆二维码图片
			} catch (final Exception e) {
				LOG.info(e.getMessage());
			}

		} catch (final Exception e) {
			LOG.info(e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean webWxInit() {
		core.setAlive(true);
		core.setLastNormalRetcodeTime(System.currentTimeMillis());
		// 组装请求URL和参数
		final String url = String.format(URLEnum.INIT_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
				String.valueOf(System.currentTimeMillis() / 3158L),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		final Map<String, Object> paramMap = core.getParamMap();

		// 请求初始化接口
		final HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));
		try {
			final String result = EntityUtils.toString(entity, Consts.UTF_8);
			final JSONObject obj = JSON.parseObject(result);

			final JSONObject user = obj.getJSONObject(StorageLoginInfoEnum.User.getKey());
			final JSONObject syncKey = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey());

			core.getLoginInfo().put(StorageLoginInfoEnum.InviteStartCount.getKey(),
					obj.getInteger(StorageLoginInfoEnum.InviteStartCount.getKey()));
			core.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), syncKey);

			final JSONArray syncArray = syncKey.getJSONArray("List");
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			// 1_661706053|2_661706420|3_661706415|1000_1494151022|
			final String synckey = sb.toString();

			// 1_661706053|2_661706420|3_661706415|1000_1494151022
			core.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(), synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			core.setUserName(user.getString("UserName"));
			core.setNickName(user.getString("NickName"));
			core.setUserSelf(obj.getJSONObject("User"));

			final String chatSet = obj.getString("ChatSet");
			final String[] chatSetArray = chatSet.split(",");
			for (int i = 0; i < chatSetArray.length; i++) {
				if (chatSetArray[i].indexOf("@@") != -1) {
					// 更新GroupIdList
					core.getGroupIdList().add(chatSetArray[i]); //
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
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void wxStatusNotify() {
		// 组装请求URL和参数
		final String url = String.format(URLEnum.STATUS_NOTIFY_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));

		final Map<String, Object> paramMap = core.getParamMap();
		paramMap.put(StatusNotifyParaEnum.CODE.para(), StatusNotifyParaEnum.CODE.value());
		paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para(), core.getUserName());
		paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para(), core.getUserName());
		paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para(), System.currentTimeMillis());
		final String paramStr = JSON.toJSONString(paramMap);

		try {
			final HttpEntity entity = httpClient.doPost(url, paramStr);
			EntityUtils.toString(entity, Consts.UTF_8);
		} catch (final Exception e) {
			LOG.error("微信状态通知接口失败！", e);
		}

	}

	@Override
	public void startReceiving() {
		core.setAlive(true);
		new Thread(() -> {
			while (core.isAlive()) {
				try {
					final Map<String, String> resultMap = syncCheck();
					// LOG.info(JSONObject.toJSONString(resultMap));
					final String retcode = resultMap.get("retcode");
					final String selector = resultMap.get("selector");
					if (retcode.equals(RetCodeEnum.UNKOWN.getCode())) {
						// LOG.info(RetCodeEnum.UNKOWN.getType());
						continue;
					} else if (retcode.equals(RetCodeEnum.LOGIN_OUT.getCode())) { // 退出
						LOG.info(RetCodeEnum.LOGIN_OUT.getType());
						break;
					} else if (retcode.equals(RetCodeEnum.LOGIN_OTHERWHERE.getCode())) { // 其它地方登陆
						LOG.info(RetCodeEnum.LOGIN_OTHERWHERE.getType());
						break;
					} else if (retcode.equals(RetCodeEnum.MOBILE_LOGIN_OUT.getCode())) { // 移动端退出
						LOG.info(RetCodeEnum.MOBILE_LOGIN_OUT.getType());
						break;
					} else if (retcode.equals(RetCodeEnum.NORMAL.getCode())) {
						core.setLastNormalRetcodeTime(System.currentTimeMillis()); // 最后收到正常报文时间
						final JSONObject msgObj = webWxSync();
						if (selector.equals("2")) {
							if (msgObj != null) {
								try {
									JSONArray msgList1 = new JSONArray();
									msgList1 = msgObj.getJSONArray("AddMsgList");
									msgList1 = MsgCenter.produceMsg(msgList1);
									for (int j1 = 0; j1 < msgList1.size(); j1++) {
										final BaseMsg baseMsg = JSON.toJavaObject(msgList1.getJSONObject(j1),
												BaseMsg.class);
										core.getMsgList().add(baseMsg);
									}
								} catch (final Exception e1) {
									LOG.info(e1.getMessage());
								}
							}
						} else if (selector.equals("7")) {
							webWxSync();
						} else if (selector.equals("4")) {
							continue;
						} else if (selector.equals("3")) {
							continue;
						} else if (selector.equals("6")) {
							if (msgObj != null) {
								try {
									JSONArray msgList2 = new JSONArray();
									msgList2 = msgObj.getJSONArray("AddMsgList");
									final JSONArray modContactList = msgObj.getJSONArray("ModContactList"); // 存在删除或者新增的好友信息
									msgList2 = MsgCenter.produceMsg(msgList2);
									for (int j2 = 0; j2 < msgList2.size(); j2++) {
										final JSONObject userInfo = modContactList.getJSONObject(j2);
										// 存在主动加好友之后的同步联系人到本地
										core.getContactList().add(userInfo);
									}
								} catch (final Exception e2) {
									LOG.info(e2.getMessage());
								}
							}

						}
					} else {
						final JSONObject obj = webWxSync();
					}
				} catch (final Exception e3) {
					LOG.info(e3.getMessage());

				}

			}
		}).start();

	}

	@Override
	public void webWxGetContact() {
		final String url = String.format(URLEnum.WEB_WX_GET_CONTACT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
		final Map<String, Object> paramMap = core.getParamMap();
		HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));

		try {
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject fullFriendsJsonList = JSON.parseObject(result);
			// 查看seq是否为0，0表示好友列表已全部获取完毕，若大于0，则表示好友列表未获取完毕，当前的字节数（断点续传）
			long seq = 0;
			long currentTime = 0L;
			final List<BasicNameValuePair> params = new ArrayList<>();
			if (fullFriendsJsonList.get("Seq") != null) {
				seq = fullFriendsJsonList.getLong("Seq");
				currentTime = new Date().getTime();
			}
			core.setMemberCount(fullFriendsJsonList.getInteger(StorageLoginInfoEnum.MemberCount.getKey()));
			final JSONArray member = fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey());
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
			core.setMemberCount(member.size());
			for (final Iterator<?> iterator = member.iterator(); iterator.hasNext();) {
				final JSONObject o = (JSONObject) iterator.next();
				if ((o.getInteger("VerifyFlag") & 8) != 0) { // 公众号/服务号
					core.getPublicUsersList().add(o);
				} else if (Config.API_SPECIAL_USER.contains(o.getString("UserName"))) { // 特殊账号
					core.getSpecialUsersList().add(o);
				} else if (o.getString("UserName").indexOf("@@") != -1) { // 群聊
					if (!core.getGroupIdList().contains(o.getString("UserName"))) {
						core.getGroupNickNameList().add(o.getString("NickName"));
						core.getGroupIdList().add(o.getString("UserName"));
						core.getGroupList().add(o);
					}
				} else if (o.getString("UserName").equals(core.getUserSelf().getString("UserName"))) { // 自己
					core.getContactList().remove(o);
				} else { // 普通联系人
					core.getContactList().add(o);
				}
			}
			return;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return;
	}

	@Override
	public void WebWxBatchGetContact() {
		final String url = String.format(URLEnum.WEB_WX_BATCH_GET_CONTACT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()), new Date().getTime(),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		final Map<String, Object> paramMap = core.getParamMap();
		paramMap.put("Count", core.getGroupIdList().size());
		final List<Map<String, String>> list = new ArrayList<>();
		for (int i = 0; i < core.getGroupIdList().size(); i++) {
			final HashMap<String, String> map = new HashMap<>();
			map.put("UserName", core.getGroupIdList().get(i));
			map.put("EncryChatRoomId", "");
			list.add(map);
		}
		paramMap.put("List", list);
		final HttpEntity entity = httpClient.doPost(url, JSON.toJSONString(paramMap));
		try {
			final String text = EntityUtils.toString(entity, Consts.UTF_8);
			// LOG.info(text);
			final JSONObject obj = JSON.parseObject(text);
			final JSONArray contactList = obj.getJSONArray("ContactList");
			core.getGroupList().clear();
			core.getGroupNickNameList().clear();
			for (int i = 0; i < contactList.size(); i++) { // 群好友
				final JSONObject group = contactList.getJSONObject(i);
				if (group.getString("UserName").indexOf("@@") > -1) { // 群
					core.getGroupNickNameList().add(group.getString("NickName")); // 更新群昵称列表
					core.getGroupList().add(group); // 更新群信息（所有）列表

					final JSONArray members = group.getJSONArray("MemberList");
					core.getGroupMemeberMap().put(group.getString("UserName"), members); // 更新群成员Map
					core.getGroupMap().put(group.getString("UserName"), group);
					for (int m = 0; m < members.size(); m++) {
						final JSONObject member = members.getJSONObject(m);
						core.getGroupMemberNickMap().put(
								group.getString("UserName") + "-" + member.getString("UserName"),
								member.getString("NickName"));
					}

					core.getGroupNickMap().put(group.getString("UserName"), group.getString("NickName")); // 更新群成员Map
				}
			}
		} catch (final Exception e) {
			LOG.info(e.getMessage());
		}
	}

	/**
	 * 检查登陆状态
	 *
	 * @param result
	 * @return
	 */
	public String checklogin(String result) {
		final String regEx = "window.code=(\\d+)";
		final Matcher matcher = CommonTools.getMatcher(regEx, result);
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
	private void processLoginInfo(String loginContent) {
		final String regEx = "window.redirect_uri=\"(\\S+)\";";
		final Matcher matcher = CommonTools.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			final String originalUrl = matcher.group(1);
			final String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			core.getLoginInfo().put("url", url);
			final Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
			final Iterator<Entry<String, List<String>>> iterator = possibleUrlMap.entrySet().iterator();
			Map.Entry<String, List<String>> entry;
			String fileUrl;
			String syncUrl;
			while (iterator.hasNext()) {
				entry = iterator.next();
				final String indexUrl = entry.getKey();
				fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (core.getLoginInfo().get("url").toString().contains(indexUrl)) {
					core.setIndexUrl(indexUrl);
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

			try {
				final HttpEntity entity = myHttpClient.doGet(originalUrl, null, false, null);
				text = EntityUtils.toString(entity);
			} catch (final Exception e) {
				LOG.info(e.getMessage());
				return;
			}
			// add by 默非默 2017-08-01 22:28:09
			// 如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
			final String msg = getLoginMessage(text);
			if (!"".equals(msg)) {
				LOG.info(msg);
				System.exit(0);
			}
			final Document doc = CommonTools.xmlParser(text);
			if (doc != null) {
				core.getLoginInfo().put(StorageLoginInfoEnum.skey.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.skey.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.wxsid.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxsid.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.wxuin.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.wxuin.getKey()).item(0).getFirstChild()
								.getNodeValue());
				core.getLoginInfo().put(StorageLoginInfoEnum.pass_ticket.getKey(),
						doc.getElementsByTagName(StorageLoginInfoEnum.pass_ticket.getKey()).item(0).getFirstChild()
								.getNodeValue());
			}

		}
	}

	private Map<String, List<String>> getPossibleUrlMap() {
		final Map<String, List<String>> possibleUrlMap = new HashMap<>();
		possibleUrlMap.put("wx.qq.com", new ArrayList<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});

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
		final String url = String.format(URLEnum.WEB_WX_SYNC_URL.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		final Map<String, Object> paramMap = core.getParamMap();
		paramMap.put(StorageLoginInfoEnum.SyncKey.getKey(),
				core.getLoginInfo().get(StorageLoginInfoEnum.SyncKey.getKey()));
		paramMap.put("rr", -new Date().getTime() / 1000);
		final String paramStr = JSON.toJSONString(paramMap);
		try {
			final HttpEntity entity = myHttpClient.doPost(url, paramStr);
			final String text = EntityUtils.toString(entity, Consts.UTF_8);
			final JSONObject obj = JSON.parseObject(text);
			if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
				result = null;
			} else {
				result = obj;
				core.getLoginInfo().put(StorageLoginInfoEnum.SyncKey.getKey(), obj.getJSONObject("SyncCheckKey"));
				final JSONArray syncArray = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey())
						.getJSONArray("List");
				final StringBuilder sb = new StringBuilder();
				for (int i = 0; i < syncArray.size(); i++) {
					sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
							+ syncArray.getJSONObject(i).getString("Val") + "|");
				}
				final String synckey = sb.toString();
				core.getLoginInfo().put(StorageLoginInfoEnum.synckey.getKey(),
						synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			}
		} catch (final Exception e) {
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
		final Map<String, String> resultMap = new HashMap<>();
		// 组装请求URL和参数
		final String url = core.getLoginInfo().get(StorageLoginInfoEnum.syncUrl.getKey())
				+ URLEnum.SYNC_CHECK_URL.getUrl();
		final List<BasicNameValuePair> params = new ArrayList<>();
		for (final BaseParaEnum baseRequest : BaseParaEnum.values()) {
			params.add(new BasicNameValuePair(baseRequest.para().toLowerCase(),
					core.getLoginInfo().get(baseRequest.value()).toString()));
		}
		params.add(new BasicNameValuePair("r", String.valueOf(new Date().getTime())));
		params.add(new BasicNameValuePair("synckey", (String) core.getLoginInfo().get("synckey")));
		params.add(new BasicNameValuePair("_", String.valueOf(new Date().getTime())));
		SleepUtils.sleep(1000);
		try {
			final HttpEntity entity = myHttpClient.doGet(url, params, true, null);
			if (entity == null) {
				resultMap.put("retcode", "9999");
				resultMap.put("selector", "9999");
				return resultMap;
			}
			final String text = EntityUtils.toString(entity);
			final String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
			final Matcher matcher = CommonTools.getMatcher(regEx, text);
			if (!matcher.find() || matcher.group(1).equals("2")) {
				LOG.info(String.format("Unexpected sync check result: %s", text));
			} else {
				resultMap.put("retcode", matcher.group(1));
				resultMap.put("selector", matcher.group(2));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 解析登录返回的消息，如果成功登录，则message为空
	 *
	 * @param result
	 * @return
	 */
	public String getLoginMessage(String result) {
		final String[] strArr = result.split("<message>");
		final String[] rs = strArr[1].split("</message>");
		if (rs != null && rs.length > 1) {
			return rs[0];
		}
		return "";
	}

	@Override
	public String pushLogin() {

		MyHttpClient.setCookie(WechatTools.readCookieStore());
		final String url = String.format(URLEnum.WEB_WX_PUSH_LOGIN_URL.getUrl(), MyHttpClient.getCookie("wxuin"));
		final HttpEntity entity = myHttpClient.doGet(url, null, true, null);
		try {
			final String result = EntityUtils.toString(entity);
			final JSONObject data = JSONObject.parseObject(result);
			if (data.containsKey("uuid")) {
				return data.getString("uuid");
			}
		} catch (final Exception e) {
			LOG.error("微信pushLogin异常！", e);
		}

		return null;
	}
}
