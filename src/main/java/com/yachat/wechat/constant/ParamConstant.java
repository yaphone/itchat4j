package com.yachat.wechat.constant;

public enum ParamConstant implements KeyValue {
	
	 APP_ID("appid", "wx782c26e4c19acffb"),
	 FUN("fun", "new"),
	 LANG("lang", "zh_CN"),
	 UNDERLINE("_" , "时间戳")
	;

	private String key;
	private String value;

	private ParamConstant(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
