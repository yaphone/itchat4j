package com.yachat.wechat.constant;

public enum PatternConstant implements KeyValue {

	UUID("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";");

	;

	private String pattern;

	private PatternConstant(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String getKey() {
		return this.pattern;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
