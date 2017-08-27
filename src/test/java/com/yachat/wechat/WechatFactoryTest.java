package com.yachat.wechat;

import java.io.IOException;
import java.util.concurrent.Executors;

import com.yachat.wechat.support.WechatSystemWebSupport;
import com.yachat.wechat.support.WechatTaskManagerSupport;

public class WechatFactoryTest {

	public static void main(String[] args) throws IOException {
		WechatSystemWebSupport system = new WechatSystemWebSupport();
		WechatTaskManager taskManager = new WechatTaskManagerSupport(Executors.newScheduledThreadPool(4),
				Executors.newFixedThreadPool(10), Executors.newFixedThreadPool(32));
		WechatFactory factory = new WechatFactory(system, taskManager, new SimpleMessageHandler(system));
		Wechat wechat = factory.createAccountRequest(1);
		LocalFileUtils.open("/Users/louis/Downloads", wechat.getAccount());
		wechat.start();
		taskManager.start();
	}
}
