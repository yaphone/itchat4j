package com.yachat.wechat.sys;

import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;

import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.StatusNotifyParaEnum;

public class StatusNotifyHandler extends AbstractAccountHandler<Void> {

	@Override
	public Request createRequest(Account account) {
		// 组装请求URL和参数
		String url = String.format(URLEnum.STATUS_NOTIFY_URL.getUrl(),
				account.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
		Request request = new Request(url);
		Map<String, Object> paramMap = account.getParamMap();
		paramMap.put(StatusNotifyParaEnum.CODE.para(), StatusNotifyParaEnum.CODE.value());
		paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para(), account.getUserName());
		paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para(), account.getUserName());
		paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para(), System.currentTimeMillis());
		request.addAll(paramMap);
		return request;
	}

	@Override
	public Response<Void> createResponse(HttpEntity entity, Account in) throws Exception {
		EntityUtils.toString(entity, Consts.UTF_8);
		return this.buildSuccess();
	}

}
