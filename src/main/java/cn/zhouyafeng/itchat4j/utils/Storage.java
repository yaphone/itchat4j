package cn.zhouyafeng.itchat4j.utils;

import java.util.ArrayList;
import java.util.List;

public class Storage {
	private String userName;
	private String nickName;
	private List<Object> memberList = new ArrayList<Object>();
	private List<Object> mpList = new ArrayList<Object>();
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

	public List<Object> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Object> memberList) {
		this.memberList = memberList;
	}

	public List<Object> getMpList() {
		return mpList;
	}

	public void setMpList(List<Object> mpList) {
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