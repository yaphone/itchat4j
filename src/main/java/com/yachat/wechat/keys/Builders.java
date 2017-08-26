package com.yachat.wechat.keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yachat.wechat.http.Request;

public class Builders {

	private KeyValue urlKey;
	private List<KeyValue> parameters;
	private Map<String, Object> parametersMap;
	private String url;
	private Object[] urlArgs;

	public Builders() {
		this.parameters = new ArrayList<>();
		this.parametersMap = new HashMap<>();
	}

	public Builders url(KeyValue urlKey) {
		this.urlKey = urlKey;
		return this;
	}

	public Builders url(KeyValue urlKey, Object... args) {
		this.urlKey = urlKey;
		this.urlArgs = args;
		return this;
	}

	public Builders add(KeyValue keyValue) {
		if (keyValue != null) {
			this.parameters.add(keyValue);
		}
		return this;
	}

	public Builders addAll(KeyValue... keyValues) {
		if (keyValues != null) {
			for (KeyValue kv : keyValues) {
				this.parameters.add(kv);
			}
		}
		return this;
	}

	public Builders add(KeyValue key, Object value) {
		if(value != null) {
			this.parametersMap.put(key.getKey(), value);
		}
		return this;
	}

	public Builders add(String key, Object value) {
		this.parametersMap.put(key, value);
		return this;
	}

	public Request build() {
		String url = this.buildUrl();
		Request request = new Request(url);
		Map<String, String> values = Keys.toMap(this.parameters);
		request.addAll(this.parametersMap);
		request.addStringAll(values);
		return request;
	}
	
	private String buildUrl() {
		if( StringUtils.isNotBlank(this.url) ) {
			return this.url;
		}
		return urlArgs != null ? String.format(this.urlKey.getKey(), this.urlArgs) : this.urlKey.getKey();
	}

	public static Builders of(KeyValue url, Object... args) {
		return new Builders().url(url, args);
	}

	public static Builders of(KeyValue url) {
		return new Builders().url(url);
	}
	
	public static Builders of(String url) {
		Builders builder = new Builders();
		builder.url = url;
		return builder;
	}

}
