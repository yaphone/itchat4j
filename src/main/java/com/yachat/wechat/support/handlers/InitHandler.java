package com.yachat.wechat.support.handlers;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;


public class InitHandler extends AbstractAccountHandler<Boolean> {

	@Override
	public Request createRequest(Account account) {
		return Builders.of(UrlKeys.INIT_URL, WechatKeys.url.get(account) , 
				String.valueOf(System.currentTimeMillis() / 3158L) ,
				WechatKeys.pass_ticket.get(account))
			.build()
			.addAll(account.getParamMap());
	}

	
	@Override
	public Response<Boolean> createResponse(String text, Account account) throws Exception {
		account.setAlive(true);
		account.setLastNormalRetcodeTime(System.currentTimeMillis());
		
		JSONObject obj = JSON.parseObject(text);
		JSONObject user = WechatKeys.User.get(obj);
		JSONObject syncKey = WechatKeys.SyncKey.get(obj);
		WechatKeys.InviteStartCount.setInteger(account, obj);
		WechatKeys.SyncKey.setJsonObject(account, obj);
		
		JSONArray syncArray = syncKey.getJSONArray("List");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < syncArray.size(); i++) {
			sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
					+ syncArray.getJSONObject(i).getString("Val") + "|");
		}
		String synckey = sb.toString();
		if( StringUtils.isNotBlank(synckey)) {
			WechatKeys.synckey.set(account, synckey.substring(0, synckey.length() - 1));
		}
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
		return Response.success(true);
	}

}
