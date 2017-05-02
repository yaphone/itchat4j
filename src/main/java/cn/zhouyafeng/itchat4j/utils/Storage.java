package cn.zhouyafeng.itchat4j.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * 存储登陆信息、好友列表等，全局只保存一份，单例模式
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月23日 下午12:04:48
 * @version 1.0
 *
 */
public class Storage {

	private static Storage instance;

	private Storage() {
	};

	public static Storage getInstance() {
		if (instance == null) {
			synchronized (Storage.class) {
				if (instance == null) {
					instance = new Storage();
				}
			}
		}
		return instance;
	}

	private String userName;
	private String nickName;
	private List<JSONObject> msgList = new ArrayList<JSONObject>();

	private List<JSONObject> userSelfList = new ArrayList<JSONObject>(); // 登陆账号自身信息
	private List<JSONObject> memberList = new ArrayList<JSONObject>(); // 好友+群聊+公众号+特殊账号
	private List<JSONObject> contactList = new ArrayList<JSONObject>();// 好友
	private List<JSONObject> groupList = new ArrayList<JSONObject>(); // 群
	private List<JSONObject> groupMemeberList = new ArrayList<JSONObject>(); // 群聊成员字典
	private List<JSONObject> publicUsersList = new ArrayList<JSONObject>();// 公众号／服务号
	private List<JSONObject> specialUsersList = new ArrayList<JSONObject>();// 特殊账号

	private String lastInputUserName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public String getLastInputUserName() {
		return lastInputUserName;
	}

	public void setLastInputUserName(String lastInputUserName) {
		this.lastInputUserName = lastInputUserName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public List<JSONObject> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<JSONObject> memberList) {
		this.memberList = memberList;
	}

	public List<JSONObject> getUserSelfList() {
		return userSelfList;
	}

	public void setUserSelfList(List<JSONObject> userSelfList) {
		this.userSelfList = userSelfList;
	}

	public List<JSONObject> getContactList() {
		return contactList;
	}

	public void setContactList(List<JSONObject> contactList) {
		this.contactList = contactList;
	}

	public List<JSONObject> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<JSONObject> groupList) {
		this.groupList = groupList;
	}

	public List<JSONObject> getGroupMemeberList() {
		return groupMemeberList;
	}

	public void setGroupMemeberList(List<JSONObject> groupMemeberList) {
		this.groupMemeberList = groupMemeberList;
	}

	public List<JSONObject> getPublicUsersList() {
		return publicUsersList;
	}

	public void setPublicUsersList(List<JSONObject> publicUsersList) {
		this.publicUsersList = publicUsersList;
	}

	public List<JSONObject> getSpecialUsersList() {
		return specialUsersList;
	}

	public void setSpecialUsersList(List<JSONObject> specialUsersList) {
		this.specialUsersList = specialUsersList;
	}

	public List<JSONObject> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<JSONObject> msgList) {
		this.msgList = msgList;
	}

}