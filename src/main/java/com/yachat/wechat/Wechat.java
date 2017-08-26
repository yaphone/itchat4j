package com.yachat.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信
 * 
 * @author huangjiu@zto.cn
 *
 */
public class Wechat {

	private static final Logger LOGGER = LoggerFactory.getLogger(Wechat.class);
	private volatile boolean reading;
	private volatile boolean checking;
	private Account account;
	private WechatInterface system;
	private MessageHandler messageHandler;
	private WechatTaskManager wechatTaskManager;

	public Wechat(Account account, WechatInterface system, MessageHandler messageHandler,
			WechatTaskManager wechatTaskManager) {
		super();
		this.account = account;
		this.system = system;
		this.messageHandler = messageHandler;
		this.wechatTaskManager = wechatTaskManager;
		this.reading = false;
	}

	private boolean isLogin() {
		return this.system.login(this.account);
	}


	public void start() {
		if (this.isLogin()) {
			this.start0();
		}
	}

	private void start0() {

		LOGGER.info("5. 登陆成功，微信初始化");
		this.system.wxInit(this.account);
		LOGGER.info("6. 开启微信状态通知");
		this.system.wxStatusNotify(this.account);

		// LOGGER.info("7. 清除。。。。");
		// CommonTools.clearScreen();
		LOGGER.info(String.format("欢迎回来， %s", this.account.getNickName()));

		LOGGER.info("8. 开始接收消息");
		// this.system.startReceiving(this.account);

		// this.wechatTaskManager.addMessageListenter(this.account, messageHandler);

		LOGGER.info("9. 获取联系人信息");
		this.system.wxGetContact(this.account);

		LOGGER.info("10. 获取群好友及群好友列表");
		this.system.wxBatchGetContact(this.account);

		LOGGER.info("11. 缓存本次登陆好友相关消息");
		this.cacheLoginFriendInfo(); // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

		LOGGER.info("12.开启微信状态检测线程");
		// new Thread(new CheckLoginStatusThread()).start();

		// this.wechatTaskManager.addCheckStatus(this.account);
		this.wechatTaskManager.addWechat(this);
	}

	private void cacheLoginFriendInfo() {
		for (JSONObject o : account.getContactList()) {
			account.getUserInfoMap().put(o.getString("NickName"), o);
			account.getUserInfoMap().put(o.getString("UserName"), o);
		}
	}

	public void stop() {
		this.wechatTaskManager.removeWechat(this);
		this.account.setAlive(false);
	}

	public Account getAccount() {
		return account;
	}

	public void offline() {
		this.account.setAlive(false);
	}

	public void online() {
		this.account.setAlive(true);
	}

	public boolean isOnline() {
		return this.account.isAlive();
	}
	

	public void enableReading() {
		this.reading = true;
	}

	public void disableReading() {
		this.reading = false;
	}

	public void enableChecking() {
		this.checking = true;
	}

	public void disableChecking() {
		this.checking = false;
	}

	public boolean isChecking() {
		return this.checking;
	}

	public boolean isReading() {
		return this.reading;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void receivingMessage() {
		if (!this.isOnline()) {
			return;
		}
		this.system.wxSyncMessage(account, messageHandler);
	}

}
