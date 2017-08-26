package com.yachat.wechat.support.handlers;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;

public class StatusNotifyHandler extends AbstractAccountHandler<Void> {

	@Override
	public Request createRequest(Account account) {
		Request request = Builders
				.of(UrlKeys.STATUS_NOTIFY_URL , WechatKeys.pass_ticket.get(account))
				.addAll(WechatKeys.CODE , WechatKeys.FROM_USERNAME , WechatKeys.TO_USERNAME)
				.add(WechatKeys.CLIENT_MSG_ID, System.currentTimeMillis())
				.build();
		request
			.addAll(account.getParamMap())
			.setCookie(account.getCookie());
		return request;
	}
	
	@Override
	public Response<Void> createResponse(String text, Account in) throws Exception {
		return this.buildSuccess();
	}

}
