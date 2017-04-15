package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.components.Login;
import cn.zhouyafeng.itchat4j.utils.Core;

public class Wechat {
	private static Wechat instance;

	private Wechat() {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		Core core = Core.getInstance();
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

}
