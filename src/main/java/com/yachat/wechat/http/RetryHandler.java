package com.yachat.wechat.http;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

public interface RetryHandler<IN, OUT> {

	Request createRequest(IN in);

	default Response<OUT> createResponse(HttpEntity entity, IN in) throws Exception {
		return this.createResponse(getEntity(entity), in);
	};
	
	default Response<OUT> createResponse(String text, IN in) throws Exception {
		return this.buildError();
	}

	default int retryTimes() {
		return -1;
	};

	default long retryTimeoutMillis() {
		return -1;
	}

	default String getEntity(HttpEntity entity) throws ParseException, IOException {
		if( entity != null ) {
			return EntityUtils.toString(entity, Consts.UTF_8);
		}
		return null;
	}

	default Response<OUT> buildSuccess(OUT out) {
		return Response.success(out);
	}

	default Response<OUT> buildSuccess() {
		return Response.success();
	}

	default Response<OUT> buildError(OUT out) {
		return Response.error(out);
	}

	default Response<OUT> buildError() {
		return Response.error();
	}

}
