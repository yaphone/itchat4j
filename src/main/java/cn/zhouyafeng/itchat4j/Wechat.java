package cn.zhouyafeng.itchat4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zhouyafeng.itchat4j.controller.LoginController;
import cn.zhouyafeng.itchat4j.core.MsgCenter;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class Wechat {
	private static final Logger LOG = LoggerFactory.getLogger(Wechat.class);
	private final IMsgHandlerFace msgHandler;
	private final LoginController login;

	public Wechat(IMsgHandlerFace msgHandler, String qrPath, boolean reload) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		this.msgHandler = msgHandler;

		// 登陆
		login = new LoginController();
		login.login(qrPath, reload);
	}

	public void start() {
		LOG.info("+++++++++++++++++++开始消息处理+++++++++++++++++++++");
		new Thread(() -> MsgCenter.handleMsg(msgHandler)).start();
	}

	public LoginController getLogin() {
		return login;
	}

}
