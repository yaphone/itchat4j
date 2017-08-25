package com.yachat.wechat.sys;

import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.constant.ConstantRequestBuilder;
import com.yachat.wechat.constant.KeyValueUtils;
import com.yachat.wechat.constant.ParamConstant;
import com.yachat.wechat.constant.PatternConstant;
import com.yachat.wechat.constant.StatusConstant;
import com.yachat.wechat.constant.UrlConstant;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;

import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class UuidHandler implements RetryHandler<Void, String> {

	@SuppressWarnings("unchecked")
	@Override
	public Request createRequest(Void in) {
		Request request = ConstantRequestBuilder.of(UrlConstant.UUID_URL)
				.addAll(ParamConstant.APP_ID , 
						ParamConstant.FUN , 
						ParamConstant.LANG, 
						ParamConstant.UNDERLINE)
				.add(ParamConstant.UNDERLINE, String.valueOf(System.currentTimeMillis()))
				.build();
		return request;
	}

//	@Override
//	public Response<String> createResponse(HttpEntity entity, Void in) throws Exception {
//		String result = EntityUtils.toString(entity);
//		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
//		Matcher matcher = CommonTools.getMatcher(regEx, result);
//		if (matcher.find()) {
//			if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
//				return Response.success(matcher.group(2));
//			}
//		}
//		return Response.error();
//	}
	
	@Override
	public Response<String> createResponse(String text, Void in) throws Exception {
//		String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";";
//		Matcher matcher = CommonTools.getMatcher(regEx , text);
//		if (matcher.find()) {
//			if ((ResultEnum.SUCCESS.getCode().equals(matcher.group(1)))) {
//				return this.buildSuccess(matcher.group(2));
//			}
//		}
		String ret = KeyValueUtils.match(text, PatternConstant.UUID , StatusConstant.SUCCESS);
		return ret == null  ? this.buildError() : this.buildSuccess(ret);
	}

}
