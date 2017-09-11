package com.yachat.wechat;

public interface MessageSender {

	boolean sendTextMessage(Account account, String toUser, int messageType, String content);

	boolean sendImageMessage(Account account);
	
}
