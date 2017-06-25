package cn.zhouyafeng.itchat4j.api;

import java.util.*;

import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.BaseParaEnum;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

	private static MyHttpClient myHttpClient = core.getMyHttpClient();

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
	public static List<JSONObject> getGroupList() {
		return core.getGroupList();
	}

	public static List<String> getGroupIdList() {
		return core.getGroupIdList();
	}

	/**
	 * 根据groupIdList返回群成员列表
	 *
	 * @date 2017年6月13日 下午11:12:31
	 * @param groupIdList
	 * @return
	 */
	public static JSONObject getMemberListByGroupId(String groupIdList) {

		JSONArray memberList=null;
		for (JSONObject o : getGroupList()) {
			if (o.getString("UserName").equals(groupIdList)) {
				memberList=o.getJSONArray("MemberList");
			}
		}

		Map<String, String> resultMap = new HashMap<String, String>();
		// 组装请求URL和参数
		String url = "https://wx2.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact?type=ex&pass_ticket="+StorageLoginInfoEnum.pass_ticket.getType()+"&" +
				"r="+String.valueOf(new Date().getTime());
		JSONObject postData = new JSONObject();

		JSONObject baseRequest_JSON = new JSONObject();
		for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
			baseRequest_JSON.put(baseRequest.para().toLowerCase(),
					core.getLoginInfo().get(baseRequest.value()).toString());
		}
		JSONArray Liat_JSON = new JSONArray();
		for(Object memberO:memberList){
			JSONObject member= (JSONObject) memberO;
			String UserName= (String) member.get("UserName");
			JSONObject User = new JSONObject();
			User.put("UserName",UserName);
			User.put("EncryChatRoomId","");
			Liat_JSON.add(User);
		}
		postData.put("BaseRequest",baseRequest_JSON);
		postData.put("Count",memberList.size());
		postData.put("List",Liat_JSON);

		try {
			HttpEntity entity = myHttpClient.doPost(url, postData.toJSONString());
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			JSONObject fullFriendsJsonList = JSON.parseObject(result);

			return fullFriendsJsonList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

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

	public static void setUserInfo() {
		for (JSONObject o : core.getContactList()) {
			core.getUserInfoMap().put(o.getString("NickName"), o);
			core.getUserInfoMap().put(o.getString("UserName"), o);
		}
	}

	/**
	 * 
	 * @date 2017年5月27日 上午12:21:40
	 * @param userName
	 * @param remName
	 */
	public static void remarkNameByNickName(String nickName, String remName) {
		String url = String.format(URLEnum.WEB_WX_REMARKNAME.getUrl(), core.getLoginInfo().get("url"),
				core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		Map<String, Object> msgMap = new HashMap<String, Object>();
		Map<String, Object> msgMap_BaseRequest = new HashMap<String, Object>();
		msgMap.put("CmdId", 2);
		msgMap.put("RemarkName", remName);
		msgMap.put("UserName", core.getUserInfoMap().get(nickName).get("UserName"));
		msgMap_BaseRequest.put("Uin", core.getLoginInfo().get(StorageLoginInfoEnum.wxuin.getKey()));
		msgMap_BaseRequest.put("Sid", core.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()));
		msgMap_BaseRequest.put("Skey", core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()));
		msgMap_BaseRequest.put("DeviceID", core.getLoginInfo().get(StorageLoginInfoEnum.deviceid.getKey()));
		msgMap.put("BaseRequest", msgMap_BaseRequest);
		try {
			String paramStr = JSON.toJSONString(msgMap);
			HttpEntity entity = core.getMyHttpClient().doPost(url, paramStr);
			// String result = EntityUtils.toString(entity, Consts.UTF_8);
			LOG.info("修改备注" + remName);
		} catch (Exception e) {
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

}
