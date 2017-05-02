package cn.zhouyafeng.itchat4j.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Contact {
	Logger logger = Logger.getLogger("Contact");
	Core core = Core.getInstance();
	CloseableHttpClient httpClient = core.getHttpClient();

	public List<Object> getContact(boolean update) {
		String url = String.format("%s/webwxgetcontact?r=%s&seq=0&skey=%s", core.loginInfo.get("url"),
				String.valueOf(new Date().getTime()), core.loginInfo.get("skey"));
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("ContentType", "application/json; charset=UTF-8");
		httpGet.setHeader("User-Agent", Config.USER_AGENT);
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String text = EntityUtils.toString(entity, "UTF-8");
				JSONObject obj = JSON.parseObject(text);
				JSONArray tmpList = obj.getJSONArray("MemberList");
				List<JSONObject> chatroomList = new ArrayList<JSONObject>();
				List<JSONObject> otherList = new ArrayList<JSONObject>();
				for (int i = 0; i < tmpList.size(); i++) {
					JSONObject m = tmpList.getJSONObject(i);
					if (m.containsKey("Sex")) {
						otherList.add(m);
					} else {
						JSONObject tmpObj = m.getJSONObject("UserName");
						if (tmpObj.toJSONString().contains("@@")) {
							chatroomList.add(m);
						} else if (tmpObj.toJSONString().contains("@")) {
							otherList.add(m);
						}
					}
				}
				if (chatroomList.size() > 0) {
					updateLocalChatrooms(chatroomList);
				}
				if (otherList.size() > 0) {
					updateLocalFriends(otherList);
				}
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;

	}

	Map<String, String> updateLocalChatrooms(List<JSONObject> chatrooms) {
		for (JSONObject chatroom : chatrooms) {
			// TODO utils.emoji_formatter(chatroom, 'NickName')
			// TODO
		}

		return null;
	}

	void updateLocalFriends(List<JSONObject> list) {
		List<JSONObject> fullList = new ArrayList<JSONObject>();
		fullList.addAll(list);
		fullList.addAll(core.getMemberList());
		for (int i = 0; i < fullList.size(); i++) {
			JSONObject friend = fullList.get(i);
			// TODO
			// if 'NickName' in friend:
			// utils.emoji_formatter(friend, 'NickName')
			// if 'DisplayName' in friend:
			// utils.emoji_formatter(friend, 'DisplayName')
			core.memberList.add(friend);
		}
	}
}
