package com.yachat.wechat;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.yachat.wechat.sys.WechatSystemWebSupport;
import com.yachat.wechat.task.WechatTaskManagerSupport;

import cn.zhouyafeng.itchat4j.demo.demo1.SimpleDemo;

public class WechatFactoryTest {

	@Test
	public void testCreateAccountRequestLong() throws IOException {
		WechatSystem system = new WechatSystemWebSupport();
		WechatTaskManager taskManager = new WechatTaskManagerSupport(Executors.newScheduledThreadPool(4),
				Executors.newFixedThreadPool(10), Executors.newFixedThreadPool(32));
		WechatFactory factory = new WechatFactory(system, taskManager , null);		
		Wechat wechat = factory.createAccountRequest(1 , new SimpleDemo());
		LocalFileUtils.open("D://images", wechat.getAccount());	
		
		wechat.start();
	}

	@Test
	public void testCreateAccountRequestLongMessageHandler() {
		fail("Not yet implemented");
	}

}
