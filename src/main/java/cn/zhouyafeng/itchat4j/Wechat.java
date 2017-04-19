package cn.zhouyafeng.itchat4j;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.components.Login;
import cn.zhouyafeng.itchat4j.components.Message;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.Core;

public class Wechat {
	private static Logger logger = Logger.getLogger("Wechat");
	private static Wechat instance;
	private static Core core = Core.getInstance();

	private IMsgHandlerFace msgHandler;

	Wechat(IMsgHandlerFace msgHandler) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误

		this.msgHandler = msgHandler;
		Login login = new Login();
		login.login();

	};

	public void start() {
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					if (core.getMsgList().size() > 0) {
						if (((JSONObject) core.getMsgList().get(0)).getString("Content").length() > 0) {
							JSONObject msg = (JSONObject) core.getMsgList().get(0);
							String result = msgHandler.textMsgHandle(msg);
							Message.send(result, ((JSONObject) core.getMsgList().get(0)).getString("FromUserName"), "");
							core.getMsgList().remove(0);
						}
					}
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// public static Wechat getInstance() {
	// if (instance == null) {
	// synchronized (Wechat.class) {
	// if (instance == null) {
	// instance = new Wechat();
	// }
	// }
	// }
	// return instance;
	// }

	// public void send() {
	// new Thread(new Runnable() {
	// public void run() {
	// while (true) {
	// if (core.getMsgList().size() > 0) {
	// if (((JSONObject) core.getMsgList().get(0)).getString("Content").length()
	// > 0)
	// Message.send("Hello World",
	// ((JSONObject) core.getMsgList().get(0)).getString("FromUserName"), "");
	// core.getMsgList().remove(0);
	// }
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// logger.info(e.getMessage());
	// }
	// }
	// }
	// }).start();
	// }

}
