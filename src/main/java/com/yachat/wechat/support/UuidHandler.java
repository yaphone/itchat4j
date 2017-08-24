package com.yachat.wechat.support;

import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;

import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.UUIDParaEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class UuidHandler implements RetryHandler<Void, String> {

	@Override
	public Request createRequest(Void in) {
		Request request = new Request(URLEnum.UUID_URL.getUrl());
		request.add(UUIDParaEnum.APP_ID.para(), UUIDParaEnum.APP_ID.value())
				.add(UUIDParaEnum.FUN.para(), UUIDParaEnum.FUN.value())
				.add(UUIDParaEnum.LANG.para(), UUIDParaEnum.LANG.value())
				.add(UUIDParaEnum.UNDERLINE.para(), String.valueOf(System.currentTimeMillis()));
		return request;
	}

	@Override
	public Response<String> createResponse(HttpEntity entity, Void in) throws Exception {
		String result = EntityUtils.toString(entity);
		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
		Matcher matcher = CommonTools.getMatcher(regEx, result);
		if (matcher.find()) {
			if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
				return Response.success(matcher.group(2));
			}
		}
		return Response.error();
	}

}
