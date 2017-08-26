package com.yachat.wechat.support.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.UrlKeys;

public class QRHandler implements RetryHandler<String, InputStream> {

	@Override
	public Request createRequest(String uuid) {
		return Builders.of(UrlKeys.QRCODE_URL, uuid).build().enableRedirect();
	}

	@Override
	public Response<InputStream> createResponse(HttpEntity entity, String in) throws Exception {
		byte[] bytes = EntityUtils.toByteArray(entity);
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		return Response.success(stream);
	}

}
