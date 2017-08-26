package com.yachat.wechat.support.handlers;

import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.Keys;
import com.yachat.wechat.keys.PatternKeys;
import com.yachat.wechat.keys.StatusKeys;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;

public class UuidHandler implements RetryHandler<Void, String> {

	@Override
	public Request createRequest(Void in) {
		return Builders.of(UrlKeys.UUID_URL)
				.addAll(WechatKeys.appid, WechatKeys.fun, WechatKeys.lang)
				.add(WechatKeys.underline, String.valueOf(System.currentTimeMillis()))
				.build();
	}

	@Override
	public Response<String> createResponse(String text, Void in) throws Exception {
		String ret = Keys.match(text, PatternKeys.UUID, StatusKeys.SUCCESS);
		return ret == null ? this.buildError() : this.buildSuccess(ret);
	}

}
