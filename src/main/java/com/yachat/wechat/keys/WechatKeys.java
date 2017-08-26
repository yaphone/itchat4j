package com.yachat.wechat.keys;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;


public enum WechatKeys implements KeyValue {
	
	//URL
    url("url"),
    fileUrl("fileUrl"),
    syncUrl("syncUrl"),
    deviceid("deviceid"), //生成15位随机数

    
    skey("skey"),
    wxsid("wxsid"),
    wxuin("wxuin"),
    pass_ticket("pass_ticket"),

    BaseRequest("BaseRequest") ,
    InviteStartCount("InviteStartCount"),
    User("User"),
    SyncKey("SyncKey"),
    synckey("synckey"),
    SyncCheckKey("SyncCheckKey") ,

    MemberCount("MemberCount"),
    MemberList("MemberList"),

 
    Uin("Uin", "wxuin"),
    Sid("Sid", "wxsid"),
    Skey("Skey", "skey"),
    DeviceID("DeviceID", "pass_ticket") ,
	

	 
	CODE("Code", "3"),
	FROM_USERNAME("FromUserName", ""),
	TO_USERNAME("ToUserName", ""),
	CLIENT_MSG_ID("ClientMsgId", "") ,	
	
    
	appid("appid", "wx782c26e4c19acffb"),
	fun("fun", "new"),
	lang("lang", "zh_CN"),
    rr("rr"),
    r("r"),
    underline("_") ,
    loginicon("loginicon", "true"),
    uuid("uuid", ""),
    tip("tip", "0"),
    
    retcode("retcode"),
    selector("selector"),
	
	;

	private String key;
	private String value;

	private WechatKeys(String key) {
		this(key , null);
	}
	
	private WechatKeys(String key, String value) {
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
	
	public String get(Account account) {
		return account.getLoginInfo(this.getKey());
	}
	
	public String get(Map<String, String> obj) {
		return obj.get(this.getKey());
	}
	
	public JSONObject get(JSONObject obj) {
		return obj.getJSONObject(this.getKey());
	}
	
	public Integer getInt(JSONObject obj) {
		return obj.getInteger(this.getKey());
	}
	
	public  JSONArray getJSONArray(JSONObject obj) {
		return obj.getJSONArray(this.key);
	}
	
	public void setInteger(Account account , JSONObject obj) {
		account.setLoginInfo( this.getKey() , obj.getInteger(this.getKey()));
	}
	
	public void setJsonObject(Account account , JSONObject obj) {
		account.setLoginInfo( this.getKey() , obj.getJSONObject(this.getKey()));
	}
	
	public void set(Account account , Object value) {
		account.setLoginInfo( this.getKey() , value);
	}
	
	public void setDoc(Account account , Document doc) {
		account.setLoginInfo(this.getKey() , doc.getElementsByTagName(this.getKey()).item(0).getFirstChild().getNodeValue());
	}
	
	public static WechatKeys[] getBase() {
		return new WechatKeys[] {
			Uin,Sid, Skey, DeviceID	
		};
	}
	
	public static Map<String, String> getBaseMap(Account account) {
		Map<String, String> map = new HashMap<>();
		for (WechatKeys key : getBase()) {
			map.put(key.getKey().toLowerCase(), account.getLoginInfo(key.getValue()));
		}
		return map;
	}
}
