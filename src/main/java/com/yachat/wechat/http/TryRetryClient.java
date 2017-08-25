package com.yachat.wechat.http;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TryRetryClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(TryRetryClient.class);
	private RetryHttpClient httpClient = new RetryHttpClient();

	public <IN, OUT> OUT get(IN in, RetryHandler<IN, OUT> handler) {
		if (handler == null) {
			return null;
		}
		Request request = handler.createRequest(in);
		Callback<OUT> callback = (entity) -> {
			return handler.createResponse(entity, in);
		};
		if (handler.retryTimes() > -1) {
			return tryRetryGet(request, handler.retryTimes(),
					handler.retryTimeoutMillis() > -1 ? handler.retryTimeoutMillis() : 100, callback);
		} else {
			return get(request, callback);
		}
	}

	public <IN, OUT> OUT post(IN in, RetryHandler<IN, OUT> handler) {
		if (handler == null) {
			return null;
		}
		Request request = handler.createRequest(in);
		Callback<OUT> callback = (entity) -> {
			return handler.createResponse(entity, in);
		};
		if (handler.retryTimes() > -1) {
			return tryRetryPost(request, handler.retryTimes(),
					handler.retryTimeoutMillis() > -1 ? handler.retryTimeoutMillis() : 100, callback);
		} else {
			return post(request, callback);
		}
	}

	public <T> T get(Request request, Callback<T> callback) {
		HttpEntity entity = get(request);
		Response<T> response = doResponse(entity, callback);
		return response != null ? response.getData() : null;
	}

	public <T> T post(Request request, Callback<T> callback) {
		HttpEntity entity = post(request);
		Response<T> response = doResponse(entity, callback);
		return response != null ? response.getData() : null;
	}

	public <T> T tryRetryGet(Request request, int tryTimes, long tryTimeoutMillis, Callback<T> callback) {
		return tryRetry(true, request, tryTimes, tryTimeoutMillis, callback);
	}

	public <T> T tryRetryPost(Request request, int tryTimes, long tryTimeoutMillis, Callback<T> callback) {
		return tryRetry(true, request, tryTimes, tryTimeoutMillis, callback);
	}

	private <T> T tryRetry(boolean requestByGet, Request request, int tryTimes, long tryTimeoutMillis,
			Callback<T> callback) {
		int times = 1;
		do {
			HttpEntity entity = requestByGet ? get(request) : post(request);
			Response<T> response = doResponse(entity, callback);
			if (response != null && response.isSuccess()) {
				return response.getData();
			}
			times++;
			sleep(tryTimeoutMillis);
		} while (times >= tryTimes);

		throw new RuntimeException("Try " + tryTimes + " TimeOut");
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private HttpEntity get(Request request) {
		HttpEntity entity = httpClient.get(request.getUrl(), 
				request.isRedirect(), request.getStringParameters(),
				request.getHeaders() ,
				request.getCookie());
		return entity;
	}

	private HttpEntity post(Request request) {
		HttpEntity entity = httpClient.post(request.getUrl(), 
				request.getParameters() , 
				request.getHeaders() , 
				request.getCookie());
		return entity;
	}

	private <T> Response<T> doResponse(HttpEntity entity, Callback<T> callback) {
		try {
			return callback.call(entity);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

}
