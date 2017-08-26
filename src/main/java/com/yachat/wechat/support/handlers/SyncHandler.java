package com.yachat.wechat.support.handlers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;

public class SyncHandler extends AbstractAccountHandler<JSONObject> {

	@Override
	public Request createRequest(Account account) {
		return Builders.of(UrlKeys.WEB_WX_SYNC_URL, 
				WechatKeys.url.get(account) , 
				WechatKeys.wxsid.get(account) , 
				WechatKeys.skey.get(account) ,
				WechatKeys.pass_ticket.get(account))
			.add(WechatKeys.SyncKey , WechatKeys.SyncKey.get(account))
			.add(WechatKeys.rr , -System.currentTimeMillis() / 1000)
			.build()
			.addAll(account.getParamMap());
	}
	
	@Override
	public Response<JSONObject> createResponse(String text, Account account) throws Exception {
		JSONObject obj = JSON.parseObject(text);
		if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
			return Response.error();
		} else {
			WechatKeys.SyncKey.set(account, obj.getJSONObject(WechatKeys.SyncCheckKey.getKey()));
			JSONArray syncArray = obj.getJSONObject(WechatKeys.SyncKey.getKey()).getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			String synckey = sb.toString();
			WechatKeys.synckey.set(account, synckey.substring(0, synckey.length() - 1));
			return Response.success(obj);
		}
	}

}
