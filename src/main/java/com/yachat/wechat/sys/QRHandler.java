package com.yachat.wechat.sys;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;

import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

public class QRHandler implements RetryHandler<String, InputStream> {

	@Override
	public Request createRequest(String uuid) {
		String qrUrl = URLEnum.QRCODE_URL.getUrl() + uuid;
		return new Request(qrUrl).enableRedirect();
	}

	@Override
	public Response<InputStream> createResponse(HttpEntity entity, String in) throws Exception {
		byte[] bytes = EntityUtils.toByteArray(entity);
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		return Response.success(stream);
	}

}
