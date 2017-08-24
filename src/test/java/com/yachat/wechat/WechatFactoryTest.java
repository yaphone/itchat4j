package com.yachat.wechat;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.yachat.wechat.support.WechatSystemWebSupport;

import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class WechatFactoryTest {

	@Test
	public void testCreateAccountRequestLong() throws IOException {
		
		WechatSystem system = new WechatSystemWebSupport();
		WechatFactory factory = new WechatFactory(system, null);		
		Wechat wechat = factory.createAccountRequest(1);
		LocalFileUtils.open("D://images", wechat.getAccount());		
		wechat.start();
	}

	@Test
	public void testCreateAccountRequestLongMessageHandler() {
		fail("Not yet implemented");
	}

}
