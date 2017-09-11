package com.yachat.wechat.http;

import org.apache.http.HttpEntity;

@FunctionalInterface
public interface Callback<T> {

	Response<T> call(HttpEntity entity) throws Exception;
}
