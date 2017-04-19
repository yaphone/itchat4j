package cn.zhouyafeng.itchat4j;

import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.components.Login;
import cn.zhouyafeng.itchat4j.components.Message;
import cn.zhouyafeng.itchat4j.utils.Core;

public class Wechat {
	private static Logger logger = Logger.getLogger("Wechat");
	private static Wechat instance;
	private static Core core = Core.getInstance();

	private Wechat() {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误

		Login login = new Login();
		login.login();

	};

	public static Wechat getInstance() {
		if (instance == null) {
			synchronized (Wechat.class) {
				if (instance == null) {
					instance = new Wechat();
				}
			}
		}
		return instance;
	}

	public void send() {
		new Thread(new Runnable() {

			public void run() {
				while (true) {
					if (core.getMsgList().size() > 0) {
						System.out.println(((JSONObject) core.getMsgList().get(0)).getString("Content"));
						if (((JSONObject) core.getMsgList().get(0)).getString("Content").length() > 0)
							Message.send("Hello World",
									((JSONObject) core.getMsgList().get(0)).getString("FromUserName"), "");
						core.getMsgList().remove(0);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.info(e.getMessage());
					}
				}

			}
		}).start();
	}

}
