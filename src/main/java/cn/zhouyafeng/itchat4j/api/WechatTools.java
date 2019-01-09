package cn.zhouyafeng.itchat4j.api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

/**
 * 微信小工具，如获好友列表等
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月4日 下午10:49:16
 * @version 1.0
 *
 */
public class WechatTools {
	private static Logger LOG = LoggerFactory.getLogger(WechatTools.class);

	private static Core core = Core.getInstance();

	/**
	 * 根据用户名发送文本消息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月4日 下午10:43:14
	 * @param msg
	 * @param toUserName
	 */
	public static void sendMsgByUserName(String msg, String toUserName) {
		MessageTools.sendMsgById(msg, toUserName);
	}

	/**
	 * <p>
	 * 通过RealName获取本次UserName
	 * </p>
	 * <p>
	 * 如NickName为"yaphone"，则获取UserName=
	 * "@1212d3356aea8285e5bbe7b91229936bc183780a8ffa469f2d638bf0d2e4fc63"，
	 * 可通过UserName发送消息
	 * </p>
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月4日 下午10:56:31
	 * @param name
	 * @return
	 */
	public static String getUserNameByNickName(String nickName) {
		for (final JSONObject o : core.getContactList()) {
			if (o.getString("NickName").equals(nickName)) {
				return o.getString("UserName");
			}
		}
		return null;
	}

	/**
	 * 返回好友昵称列表
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月4日 下午11:37:20
	 * @return
	 */
	public static List<String> getContactNickNameList() {
		final List<String> contactNickNameList = new ArrayList<>();
		for (final JSONObject o : core.getContactList()) {
			contactNickNameList.add(o.getString("NickName"));
		}
		return contactNickNameList;
	}

	/**
	 * 返回好友完整信息列表
	 *
	 * @date 2017年6月26日 下午9:45:39
	 * @return
	 */
	public static List<JSONObject> getContactList() {
		return core.getContactList();
	}

	/**
	 * 返回群列表
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月5日 下午9:55:21
	 * @return
	 */
	public static List<JSONObject> getGroupList() {
		return core.getGroupList();
	}

	/**
	 * 获取群ID列表
	 *
	 * @date 2017年6月21日 下午11:42:56
	 * @return
	 */
	public static List<String> getGroupIdList() {
		return core.getGroupIdList();
	}

	/**
	 * 获取群NickName列表
	 *
	 * @date 2017年6月21日 下午11:43:38
	 * @return
	 */
	public static List<String> getGroupNickNameList() {
		return core.getGroupNickNameList();
	}

	/**
	 * 根据groupIdList返回群成员列表
	 *
	 * @date 2017年6月13日 下午11:12:31
	 * @param groupId
	 * @return
	 */
	public static JSONArray getMemberListByGroupId(String groupId) {
		return core.getGroupMemeberMap().get(groupId);
	}

	/**
	 * 退出微信
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年5月18日 下午11:56:54
	 */
	public static void logout() {
		webWxLogout();
	}

	private static boolean webWxLogout() {
		final String url = String.format(URLEnum.WEB_WX_LOGOUT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
		final List<BasicNameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("redirect", "1"));
		params.add(new BasicNameValuePair("type", "1"));
		params.add(
				new BasicNameValuePair("skey", (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey())));
		try {
			final HttpEntity entity = core.getMyHttpClient().doGet(url, params, false, null);
			final String text = EntityUtils.toString(entity, Consts.UTF_8); // 无消息
			return true;
		} catch (final Exception e) {
			LOG.debug(e.getMessage());
		}
		return false;
	}

	public static void setUserInfo() {
		for (final JSONObject o : core.getContactList()) {
			core.getUserInfoMap().put(o.getString("NickName"), o);
			core.getUserInfoMap().put(o.getString("UserName"), o);
		}
	}

	/**
	 *
	 * 根据用户昵称设置备注名称
	 *
	 * @date 2017年5月27日 上午12:21:40
	 * @param userName
	 * @param remName
	 */
	public static void remarkNameByNickName(String nickName, String remName) {
		final String url = String.format(URLEnum.WEB_WX_REMARKNAME.getUrl(), core.getLoginInfo().get("url"),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		final Map<String, Object> msgMap = new HashMap<>();
		final Map<String, Object> msgMap_BaseRequest = new HashMap<>();
		msgMap.put("CmdId", 2);
		msgMap.put("RemarkName", remName);
		msgMap.put("UserName", core.getUserInfoMap().get(nickName).get("UserName"));
		msgMap_BaseRequest.put("Uin", core.getLoginInfo().get(StorageLoginInfoEnum.wxuin.getKey()));
		msgMap_BaseRequest.put("Sid", core.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()));
		msgMap_BaseRequest.put("Skey", core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()));
		msgMap_BaseRequest.put("DeviceID", core.getLoginInfo().get(StorageLoginInfoEnum.deviceid.getKey()));
		msgMap.put("BaseRequest", msgMap_BaseRequest);
		try {
			final String paramStr = JSON.toJSONString(msgMap);
			final HttpEntity entity = core.getMyHttpClient().doPost(url, paramStr);
			// String result = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.info("修改备注" + remName);
		} catch (final Exception e) {
			LOG.error("remarkNameByUserName", e);
		}
	}

	/**
	 * 获取微信在线状态
	 *
	 * @date 2017年6月16日 上午12:47:46
	 * @return
	 */
	public static boolean getWechatStatus() {
		return core.isAlive();
	}

	/**
	 * 暂存cookie
	 */
	public static void dumpCookie() {

		FileOutputStream fs;
		try {
			fs = new FileOutputStream(Core.getHotReloadDir());
			final ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(MyHttpClient.getCookieStore());
			os.close();
		} catch (final IOException e) {
			LOG.error("", e);
		}

	}

	/**
	 * 读取cookie
	 * 
	 * @return
	 */
	public static CookieStore readCookieStore() {
		FileInputStream fs;
		try {
			fs = new FileInputStream(Core.getHotReloadDir());

			final ObjectInputStream ois = new ObjectInputStream(fs);
			final CookieStore cookieStore = (CookieStore) ois.readObject();
			ois.close();
			return cookieStore;
		} catch (IOException | ClassNotFoundException e) {
			LOG.error("", e);
		}
		return null;
	}

}
