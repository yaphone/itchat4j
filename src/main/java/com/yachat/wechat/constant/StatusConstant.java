package com.yachat.wechat.constant;

public enum StatusConstant implements KeyValue {

	SUCCESS("200", "成功"), 
	WAIT_CONFIRM("201", "请在手机上点击确认"), 
	WAIT_SCAN("400", "请扫描二维码");
	;

	private String status;
	private String description;

	private StatusConstant(String status, String description) {
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

}
