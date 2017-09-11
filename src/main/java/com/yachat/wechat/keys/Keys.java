package com.yachat.wechat.keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.yachat.wechat.utils.MatchUtils;

public class Keys {

	public static String getUrl(KeyValue constant) {
		return getKey(constant);
	}

	public static String getKey(KeyValue constant) {
		if (constant == null) {
			return null;
		}
		return constant.getKey();
	}

	@SafeVarargs
	public static Map<String, String> toMap(KeyValue... values) {
		if (values == null || values.length == 0) {
			return new HashMap<>();
		}
		Map<String, String> results = new HashMap<>();
		for (KeyValue kv : values) {
			results.put(kv.getKey(), kv.getValue());
		}
		return results;
	}

	public static Map<String, String> toMap(List<KeyValue> values) {
		if (values == null || values.size() == 0) {
			return new HashMap<>();
		}
		Map<String, String> results = new HashMap<>();
		for (KeyValue kv : values) {
			results.put(kv.getKey(), kv.getValue());
		}
		return results;
	}

	public static List<String> toKeyList(KeyValue... values) {
		if (values == null || values.length == 0) {
			return new ArrayList<>();
		}
		List<String> results = new ArrayList<>();
		for (KeyValue kv : values) {
			results.add(kv.getKey());
		}
		return results;
	}

	@SafeVarargs
	public static List<String> toValueList(KeyValue... values) {
		if (values == null || values.length == 0) {
			return new ArrayList<>();
		}
		List<String> results = new ArrayList<>();
		for (KeyValue kv : values) {
			results.add(kv.getValue());
		}
		return results;
	}

	public static String match(String text, KeyValue pattern, KeyValue keyValue) {
		Matcher matcher = MatchUtils.getMatcher(pattern.getKey(), text);
		if (matcher.find()) {
			if ((keyValue.getKey().equals(matcher.group(1)))) {
				return matcher.group(2);
			}
		}
		return null;
	}
	
	public static Matcher match2(String text, KeyValue pattern) {
		return MatchUtils.getMatcher(pattern.getKey(), text);
	}
}
