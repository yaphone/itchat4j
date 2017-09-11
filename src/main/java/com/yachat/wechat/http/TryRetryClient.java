package com.yachat.wechat.http;

import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TryRetryClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(TryRetryClient.class);
	private RetryHttpClient retryHttpClient = new RetryHttpClient();

	public <IN, OUT> OUT retryGet(IN in, RetryHandler<IN, OUT> handler) {
		if (handler == null) {
			return null;
		}
		try {
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
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}

	}

	public <IN, OUT> OUT retryPost(IN in, RetryHandler<IN, OUT> handler) {
		if (handler == null) {
			return null;
		}
		try {
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
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	public <T> T get(Request request, Callback<T> callback) {
		try {
			HttpEntity entity = get( request);
			Response<T> response = doResponse(entity, callback);
			return response != null ? response.getData() : null;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} finally {
			retryHttpClient.close();
		}
	}

	public <T> T post(Request request, Callback<T> callback) {
		try {
			HttpEntity entity = post(request);
			Response<T> response = doResponse(entity, callback);
			return response != null ? response.getData() : null;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} finally {
			retryHttpClient.close();
		}
	}

	private <T> T tryRetryGet( Request request, int tryTimes, long tryTimeoutMillis,
			Callback<T> callback) {
		return tryRetry(true, request, tryTimes, tryTimeoutMillis, callback);
	}

	private <T> T tryRetryPost(Request request, int tryTimes, long tryTimeoutMillis,
			Callback<T> callback) {
		return tryRetry(false, request, tryTimes, tryTimeoutMillis, callback);
	}

	private <T> T tryRetry(boolean requestByGet, Request request, int tryTimes, long tryTimeoutMillis,
			Callback<T> callback) {
		try {
			int times = 1;
			do {
				HttpEntity entity = requestByGet ? get( request) : post( request);
				Response<T> response = doResponse(entity, callback);
				if (response != null && response.isSuccess()) {
					return response.getData();
				}
				times++;
				Thread.sleep(tryTimeoutMillis);
			} while (tryTimes > times);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage() , e);
			throw new RuntimeException(e);
		} finally {
			retryHttpClient.close();
		}

		throw new RuntimeException("Retry Http Request " + (tryTimes * tryTimeoutMillis / 1000) + "s TimeOut");
	}

	private HttpEntity get(Request request) {
		HttpEntity entity = retryHttpClient.get(request.getUrl(), request.isRedirect(),
				request.getStringParameters(), request.getHeaders(), request.getCookie());
		return entity;
	}

	private HttpEntity post(Request request) {
		HttpEntity entity = retryHttpClient.post(request.getUrl(), request.getParameters(),
				request.getHeaders(), request.getCookie());
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
