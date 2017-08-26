package com.yachat.wechat.support.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.Keys;
import com.yachat.wechat.keys.PatternKeys;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;

public class SyncStatusHandler extends AbstractAccountHandler<Map<String, String>> {

	@Override
	public Request createRequest(Account account) {
		String url = WechatKeys.syncUrl.get(account) + UrlKeys.SYNC_CHECK_URL.getKey();
		return Builders.of(url)
			.add(WechatKeys.r , String.valueOf(System.currentTimeMillis()))
			.add(WechatKeys.synckey , WechatKeys.synckey.get(account))
			.add(WechatKeys.underline, String.valueOf(System.currentTimeMillis()))
			.build()
			.addStringAll(WechatKeys.getBaseMap(account))
			.setCookie(account.getCookie());
	}

	@Override
	public Response<Map<String, String>> createResponse(HttpEntity entity, Account account) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (entity == null) {
			resultMap.put("retcode", "9999");
			resultMap.put("selector", "9999");
			return Response.success(resultMap);
		}
		String text = EntityUtils.toString(entity);
		Matcher matcher = Keys.match2(text, PatternKeys.SyncStatus);
		if (!matcher.find() || matcher.group(1).equals("2")) {
			LOGGER.info(String.format("Unexpected sync check result: %s", text));
		} else {
			resultMap.put("retcode", matcher.group(1));
			resultMap.put("selector", matcher.group(2));
		}
		return Response.success(resultMap);
	}

}
