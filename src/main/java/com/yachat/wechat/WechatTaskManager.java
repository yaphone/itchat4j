package com.yachat.wechat;

public interface WechatTaskManager {

	void addWechat(Wechat wechat);

	void removeWechat(Wechat wechat);

	void start();

	void stop();

}
