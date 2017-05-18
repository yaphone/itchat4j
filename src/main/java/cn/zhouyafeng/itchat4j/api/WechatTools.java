package cn.zhouyafeng.itchat4j.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.core.Core;
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
		for (JSONObject o : core.getContactList()) {
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
	public static List<String> getContactList() {
		List<String> contactList = new ArrayList<String>();
		for (JSONObject o : core.getContactList()) {
			contactList.add(o.getString("NickName"));
		}
		return contactList;
	}

	/**
	 * 返回群列表
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月5日 下午9:55:21
	 * @return
	 */
	public static List<String> getGroupList() {
		List<String> groupList = new ArrayList<String>();
		for (JSONObject o : core.getGroupList()) {
			groupList.add(o.getString("Name"));
		}
		return groupList;
	}

	public static List<String> getGroupIdList() {
		return core.getGroupIdList();
	}

	/**
	 * 退出微信
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月18日 下午11:56:54
	 */
	public static void logout() {
		webWxLogout();
		core.setAlive(false);
	}

	private static boolean webWxLogout() {
		String url = String.format(URLEnum.WEB_WX_LOGOUT.getUrl(),
				core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("redirect", "1"));
		params.add(new BasicNameValuePair("type", "1"));
		params.add(
				new BasicNameValuePair("skey", (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey())));
		try {
			HttpEntity entity = core.getMyHttpClient().doGet(url, params, false, null);
			String text = EntityUtils.toString(entity, Consts.UTF_8); // 无消息
			return true;
		} catch (Exception e) {
			LOG.debug(e.getMessage());
		}
		return false;
	}

}
