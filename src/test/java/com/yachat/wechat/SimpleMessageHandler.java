package com.yachat.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yachat.wechat.message.Message;

public class SimpleMessageHandler implements MessageHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMessageHandler.class);

	@Override
	public String text(Message message) {
		LOGGER.info(message.getContent());
		return message.getContent();
	}

	@Override
	public String picture(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String voice(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String video(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String card(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sys(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String verifyAddFriend(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String media(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

}
