package com.yachat.wechat.keys;

import java.util.regex.Matcher;

import com.yachat.wechat.utils.MatchUtils;

public enum PatternKeys implements KeyValue {

	UUID("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";"), 
	SyncStatus("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}"), 
	CheckLogin("window.code=(\\d+)"),
	ProcessLoginInfo("window.redirect_uri=\"(\\S+)\";"),
	WechatMessage("(.+?\\(.+?\\))") ,
	EmojiFormatter("<span class=\"emoji emoji(.{1,10})\"></span>") ,
	;

	private String pattern;

	private PatternKeys(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String getKey() {
		return this.pattern;
	}

	@Override
	public String getValue() {
		return null;
	}

	public Matcher match(String text) {
		return MatchUtils.getMatcher(this.pattern, text);
	}
	
	public String match1(String text) {	
		return matchIndex(text, 1);
	}
	
	public String matchIndex(String text , int index) {	
		Matcher matcher = this.match(text);
		return matcher.find() ? matcher.group(index) :  null;
	}

}
