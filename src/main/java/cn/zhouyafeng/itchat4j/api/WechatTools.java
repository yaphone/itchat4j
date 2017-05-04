package cn.zhouyafeng.itchat4j.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Core;

/**
 * 微信小工具，如获好友列表等
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月4日 下午10:49:16
 * @version 1.0
 *
 */
public class WechatTools {

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
		MessageTools.sendMsg(msg, toUserName);
	}

	/**
	 * 获取好友列表，JSONObject格式
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月4日 下午10:55:18
	 * @return
	 */
	private static List<JSONObject> getJsonContactList() {
		return core.getContactList();
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

}
