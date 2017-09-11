package com.yachat.wechat.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtils {

	
	/**
	 * 正则表达式处理工具
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 上午12:27:10
	 * @return
	 */
	public static Matcher getMatcher(String regEx, String text) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);
		return matcher;
	}
}
