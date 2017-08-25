package com.yachat.wechat.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.CookieStore;

public class Request {

	private String url;
	private Map<String, Object> parameters;
	private boolean redirect;
	private Map<String, String> headers;
	private CookieStore cookie;

	public Request(String url) {
		this(url, false, null, null);
	}

	public Request(String url, boolean redirect, Map<String, Object> parameters, Map<String, String> headers) {
		super();
		this.url = url;
		this.parameters = parameters;
		this.redirect = redirect;
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, String> getStringParameters() {
		if (this.parameters == null) {
			return null;
		}
		HashMap<String, String> stringParams = new HashMap<>();
		for (Entry<String, Object> entry : this.parameters.entrySet()) {
			stringParams.put(entry.getKey(), (String) entry.getValue());
		}
		return stringParams;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public CookieStore getCookie() {
		return cookie;
	}

	public Request setCookie(CookieStore cookie) {
		this.cookie = cookie;
		return this;
	}

	public Request enableRedirect() {
		this.redirect = true;
		return this;
	}

	public Request add(String key, Object value) {
		if (this.parameters == null) {
			this.parameters = new HashMap<>();
		}
		this.parameters.put(key, value);
		return this;
	}

	public Request addAll(Map<String, Object> values) {
		if (this.parameters == null) {
			this.parameters = new HashMap<>();
		}
		this.parameters.putAll(values);
		return this;
	}

	public Request addStringAll(Map<String, String> values) {
		if (this.parameters == null) {
			this.parameters = new HashMap<>();
		}
		this.parameters.putAll(values);
		return this;
	}

	public Request addHeader(String key, String value) {
		if (this.headers == null) {
			this.headers = new HashMap<>();
		}
		this.headers.put(key, value);
		return this;
	}

}
