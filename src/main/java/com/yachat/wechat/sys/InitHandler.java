package com.yachat.wechat.sys;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;

import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

public class InitHandler extends AbstractAccountHandler<Boolean> {

	@Override
	public Request createRequest(Account account) {
		account.setAlive(true);
		account.setLastNormalRetcodeTime(System.currentTimeMillis());
		
		String url = String.format(URLEnum.INIT_URL.getUrl(),
				account.getLoginInfo(StorageLoginInfoEnum.url.getKey()),
				String.valueOf(System.currentTimeMillis() / 3158L),
				account.getLoginInfo(StorageLoginInfoEnum.pass_ticket.getKey()));
		
		return new Request(url).addAll(account.getParamMap());
	}

	@Override
	public Response<Boolean> createResponse(HttpEntity entity, Account account) throws Exception {
		String result = EntityUtils.toString(entity, Consts.UTF_8);
		JSONObject obj = JSON.parseObject(result);
		JSONObject user = obj.getJSONObject(StorageLoginInfoEnum.User.getKey());
		JSONObject syncKey = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey());
		account.getLoginInfo().put(StorageLoginInfoEnum.InviteStartCount.getKey(), obj.getInteger(StorageLoginInfoEnum.InviteStartCount.getKey()));
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
		return Response.success(true);
	}

}
