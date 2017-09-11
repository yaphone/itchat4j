package com.yachat.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yachat.wechat.keys.MessageKeys;
import com.yachat.wechat.message.Message;

public class SimpleMessageHandler implements MessageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMessageHandler.class);
	private MessageSender sender;

	public SimpleMessageHandler(MessageSender sender) {
		this.sender = sender;
	}

	@Override
	public String text(Account account, Message message) {
		LOGGER.info(message.getContent());
		if (!message.isGroupMsg()) { // 群消息不处理
			String userId = message.getFromUserName();
			boolean send = sender.sendTextMessage(account, userId, MessageKeys.MSGTYPE_TEXT.getCode(),
					"re:" + message.getContent());
			if (send) {
				LOGGER.info("Send Message Successed.");
			}
		}
		return message.getContent();
	}

	@Override
	public String picture(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String voice(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String card(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sys(Account account, Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public String verifyAddFriend(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String media(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String video(Account account, Message message) {
		// TODO Auto-generated method stub
		return null;
	}

}
