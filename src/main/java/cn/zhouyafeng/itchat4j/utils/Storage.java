package cn.zhouyafeng.itchat4j.utils;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * 存储登陆信息、好友列表等，全局只保存一份，单例模式
 * 
 * @author Email:zhouyaphone@163.com
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
	private List<JSONObject> memberList = new ArrayList<JSONObject>();
	private List<JSONObject> mpList = new ArrayList<JSONObject>();
	private List<Object> chatroomList = new ArrayList<Object>();
	private List<Object> msgList = new ArrayList<Object>();
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

	public List<JSONObject> getMpList() {
		return mpList;
	}

	public void setMpList(List<JSONObject> mpList) {
		this.mpList = mpList;
	}

	public List<Object> getChatroomList() {
		return chatroomList;
	}

	public void setChatroomList(List<Object> chatroomList) {
		this.chatroomList = chatroomList;
	}

	public List<Object> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<Object> msgList) {
		this.msgList = msgList;
	}

}