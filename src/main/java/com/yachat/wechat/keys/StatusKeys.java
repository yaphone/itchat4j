package com.yachat.wechat.keys;

public enum StatusKeys implements KeyValue {

	SUCCESS("200", "成功"), 
	WAIT_CONFIRM("201", "请在手机上点击确认"), 
	WAIT_SCAN("400", "请扫描二维码") ,
	
	NORMAL("0", "普通"), 
	LOGIN_OUT("1102", "退出"), 
	LOGIN_OTHERWHERE("1101", "其它地方登陆"), 
	MOBILE_LOGIN_OUT("1102", "移动端退出"), 
	UNKOWN("9999", "未知")
	;

	private String status;
	private String description;

	private StatusKeys(String status, String description) {
		this.status = status;
		this.description = description;
	}

	@Override
	public String getKey() {
		return this.status;
	}

	@Override
	public String getValue() {
		return this.description;
	}
	
	public boolean is(String value) {
		return value != null && this.status.equals(value);
	}
	
	public static StatusKeys of(String key) {
		for (StatusKeys enum1 : values()) {
			if (enum1.is(key)) {
				return enum1;
			}
		}
		return null;
	}

}
