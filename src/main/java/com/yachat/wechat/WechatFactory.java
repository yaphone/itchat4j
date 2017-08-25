package com.yachat.wechat;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WechatFactory {

	private Map<Long, Wechat> wechats;
	private WechatSystem system;
	private WechatTaskManager taskManager;
	private MessageHandler defaultMessageHandler;

	public WechatFactory(WechatSystem system, WechatTaskManager taskManager, MessageHandler defaultMessageHandler) {
		super();
		this.system = system;
		this.taskManager = taskManager;
		this.defaultMessageHandler = defaultMessageHandler;
		this.wechats = new ConcurrentHashMap<>();
	}

	/**
	 * 创建 账户请求信息
	 * 
	 * @param uid
	 * @return
	 */
	public Wechat createAccountRequest(long uid) {
		return this.createAccountRequest(uid, this.defaultMessageHandler);
	}

	/**
	 * 创建 账户请求信息
	 * 
	 * @param uid
	 * @return
	 */
	public Wechat createAccountRequest(long uid, MessageHandler messageHandler) {
		Account account = new Account();
		String uuid = system.getUuid();
		InputStream inputStream = system.getQR(uuid);
		account.setUid(uid);
		account.setUuid(uuid);
		account.setQrStream(inputStream);
		Wechat wechat = new Wechat(account, system, messageHandler, this.taskManager);
		this.wechats.put(uid, wechat);
		return wechat;
	}

	/**
	 * 获取所以微信登录信息
	 * 
	 * @return
	 */
	public Map<Long, Wechat> getWechats() {
		return wechats;
	}

}
