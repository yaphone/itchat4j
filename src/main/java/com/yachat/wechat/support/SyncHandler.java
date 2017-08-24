package com.yachat.wechat.support;

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

public class SyncHandler extends AbstractAccountHandler<JSONObject> {

	@Override
	public Request createRequest(Account account) {
		String url = String.format(URLEnum.WEB_WX_SYNC_URL.getUrl(),
				account.getLoginInfo(StorageLoginInfoEnum.url.getKey()),
				account.getLoginInfo(StorageLoginInfoEnum.wxsid.getKey()),
				account.getLoginInfo(StorageLoginInfoEnum.skey.getKey()),
				account.getLoginInfo(StorageLoginInfoEnum.pass_ticket.getKey()));
		Request request = new Request(url);
		request.addAll(account.getParamMap());
		request.add(StorageLoginInfoEnum.SyncKey.getKey(), account.getLoginInfo(StorageLoginInfoEnum.SyncKey.getKey()));
		request.add("rr", -System.currentTimeMillis() / 1000);
		return request;
	}

	@Override
	public Response<JSONObject> createResponse(HttpEntity entity, Account account) throws Exception {
		String text = EntityUtils.toString(entity, Consts.UTF_8);
		JSONObject obj = JSON.parseObject(text);
		if (obj.getJSONObject("BaseResponse").getInteger("Ret") != 0) {
			return Response.error();
		} else {
			account.setLoginInfo(StorageLoginInfoEnum.SyncKey.getKey(), obj.getJSONObject("SyncCheckKey"));
			JSONArray syncArray = obj.getJSONObject(StorageLoginInfoEnum.SyncKey.getKey()).getJSONArray("List");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < syncArray.size(); i++) {
				sb.append(syncArray.getJSONObject(i).getString("Key") + "_"
						+ syncArray.getJSONObject(i).getString("Val") + "|");
			}
			String synckey = sb.toString();
			account.setLoginInfo(StorageLoginInfoEnum.synckey.getKey(), synckey.substring(0, synckey.length() - 1));// 1_656161336|2_656161626|3_656161313|11_656159955|13_656120033|201_1492273724|1000_1492265953|1001_1492250432|1004_1491805192
			return Response.success(obj);
		}
	}

}
