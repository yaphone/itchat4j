package com.yachat.wechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AccountFriends {
	
	private volatile int memberCount = 0;
	private List<JSONObject> memberList = new ArrayList<JSONObject>(); 	// 好友+群聊+公众号+特殊账号
	private List<JSONObject> contactList = new ArrayList<JSONObject>();	// 好友
	private List<JSONObject> groupList = new ArrayList<JSONObject>(); 	// 群
	private Map<String, JSONArray> groupMemeberMap = new HashMap<String, JSONArray>(); // 群聊成员字典
	private List<JSONObject> publicUsersList = new ArrayList<JSONObject>();;// 公众号／服务号
	private List<JSONObject> specialUsersList = new ArrayList<JSONObject>();;// 特殊账号
	private List<String> groupIdList = new ArrayList<String>(); 		// 群ID列表
	private List<String> groupNickNameList = new ArrayList<String>(); // 群NickName列表
	private Map<String, JSONObject> userInfoMap = new HashMap<String, JSONObject>();

}
