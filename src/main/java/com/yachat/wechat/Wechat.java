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
	private Account account;
	private WechatSystem system;
	private MessageHandler messageHandler;

	public Wechat(Account account, WechatSystem system, MessageHandler messageHandler) {
		super();
		this.account = account;
		this.system = system;
		this.messageHandler = messageHandler;
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
		this.system.webWxInit(this.account);
		LOGGER.info("6. 开启微信状态通知");
		this.system.wxStatusNotify(this.account);

		// LOGGER.info("7. 清除。。。。");
		// CommonTools.clearScreen();
		LOGGER.info(String.format("欢迎回来， %s", this.account.getNickName()));

		LOGGER.info("8. 开始接收消息");
//		this.system.startReceiving(this.account);

		LOGGER.info("9. 获取联系人信息");
		this.system.webWxGetContact(this.account);

		LOGGER.info("10. 获取群好友及群好友列表");
		this.system.WebWxBatchGetContact(this.account);

		LOGGER.info("11. 缓存本次登陆好友相关消息");
		this.cacheLoginFriendInfo(); // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

		LOGGER.info("12.开启微信状态检测线程");
		// new Thread(new CheckLoginStatusThread()).start();
	}

	private void cacheLoginFriendInfo() {
		for (JSONObject o : account.getContactList()) {
			account.getUserInfoMap().put(o.getString("NickName"), o);
			account.getUserInfoMap().put(o.getString("UserName"), o);
		}
	}

	public void stop() {
		// this.system.
	}

	public Account getAccount() {
		return account;
	}

}
