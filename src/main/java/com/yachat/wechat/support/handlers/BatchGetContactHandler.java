package com.yachat.wechat.support.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;

public class BatchGetContactHandler extends AbstractAccountHandler<Void> {

	@Override
	public Request createRequest(Account account) {
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
		Request request = Builders.of(UrlKeys.WEB_WX_BATCH_GET_CONTACT	,  
				WechatKeys.url.get(account) , 
				System.currentTimeMillis() , 
				WechatKeys.pass_ticket.get(account))
			.build()
			.addAll(paramMap)
			.setCookie(account.getCookie());
		return request;
	}

	@Override
	public Response<Void> createResponse(HttpEntity entity, Account account) throws Exception {
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
		return this.buildSuccess();
	}

}
