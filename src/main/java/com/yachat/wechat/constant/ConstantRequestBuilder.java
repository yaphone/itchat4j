package com.yachat.wechat.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yachat.wechat.http.Request;

public class ConstantRequestBuilder {

	private KeyValue urlKey;
	private List<KeyValue> parameters;
	private Map<String, Object> parametersMap;

	public ConstantRequestBuilder() {
		this.parameters = new ArrayList<>();
		this.parametersMap = new HashMap<>();
	}

	public ConstantRequestBuilder url(KeyValue urlKey) {
		this.urlKey = urlKey;
		return this;
	}

	public ConstantRequestBuilder add(KeyValue keyValue) {
		if (keyValue != null) {
			this.parameters.add(keyValue);
		}
		return this;
	}

	public ConstantRequestBuilder addAll(KeyValue... keyValues) {
		if (keyValues != null) {
			for (KeyValue kv : keyValues) {
				this.parameters.add(kv);
			}
		}
		return this;
	}

	public ConstantRequestBuilder add(KeyValue key, Object value) {
		this.parametersMap.put(key.getKey(), value);
		return this;
	}

	public ConstantRequestBuilder add(String key, Object value) {
		this.parametersMap.put(key, value);
		return this;
	}

	public Request build() {
		Request request = new Request(this.urlKey.getKey());
		Map<String, String> values = KeyValueUtils.toMap(this.parameters);
		request.addAll(this.parametersMap);
		request.addStringAll(values);
		return request;
	}

	public static ConstantRequestBuilder of(KeyValue url) {
		return new ConstantRequestBuilder().url(url);
	}

}
