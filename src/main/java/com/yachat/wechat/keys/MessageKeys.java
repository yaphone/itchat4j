package com.yachat.wechat.keys;

import com.yachat.wechat.message.Message;

public enum MessageKeys implements KeyValue {
	
	TEXT("Text", "文本消息"),
	PIC("Pic", "图片消息"),
	VOICE("Voice", "语音消息"),
	VIEDO("Viedo", "小视频消息"),
	NAMECARD("NameCard", "名片消息"),
	SYS("Sys", "系统消息"),
	VERIFYMSG("VerifyMsg", "添加好友"),
	MEDIA("app", "文件消息") ,
	
	MSGTYPE_TEXT(1, "文本消息类型" , TEXT ),
	MSGTYPE_IMAGE(3, "图片消息" , PIC),
	MSGTYPE_VOICE(34, "语音消息" , VOICE),
	MSGTYPE_VIDEO(43, "小视频消息" , VIEDO),
	MSGTYPE_MICROVIDEO(62, "短视频消息" , VIEDO),
	MSGTYPE_EMOTICON(47, "表情消息" ,  PIC),
	MSGTYPE_MEDIA(49, "多媒体消息" , MEDIA),
	MSGTYPE_VOIPMSG(50, ""),
	MSGTYPE_VOIPNOTIFY(52, ""),
	MSGTYPE_VOIPINVITE(53, ""),
	MSGTYPE_LOCATION(48, ""),
	MSGTYPE_STATUSNOTIFY(51, ""),
	MSGTYPE_SYSNOTICE(9999, ""),
	MSGTYPE_POSSIBLEFRIEND_MSG(40, ""),
	MSGTYPE_VERIFYMSG(37, "好友请求" , VERIFYMSG),
	MSGTYPE_SHARECARD(42, "" , NAMECARD),
	MSGTYPE_SYS(10000, "系统消息" , SYS),
	MSGTYPE_RECALLED(10002, "") ,
	
	
	
	;

	
	private int code;
	private String key;
	private String value;
	private MessageKeys type;
	
	MessageKeys(int code, String value) {
		this(code, value , null);
	}

	MessageKeys(int code, String value , MessageKeys type) {
		this.code = code;
		this.value = value;
		this.type = type;
	}
	
	MessageKeys(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getCode() {
		return this.code;
	}
	
	@Override
	public String getKey() {
		return  this.key == null ?  this.code + "" : this.key;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	public MessageKeys getType() {
		return this.type;
	}
	
	public boolean isText() {
		return this.type != null && this.type.equals(TEXT);
	}
	
	public boolean is(int status) {
		return this.code == status;
	}
	
	public boolean is(String key) {
		return this.key.equals(key);
	}
	
	public boolean is(Message message) {
		return this.key.equals(message.getType());
	}
	
	public static MessageKeys of(int status) {
		for(MessageKeys msg : values()) {
			if( msg.is(status) ) {
				return msg;
			}
		}
		return null;
	}

}
