package cn.zhouyafeng.itchat4j;

public class Wechat {
	private static Wechat instance;

	private Wechat() {
		System.setProperty("jsse.enableSNIExtension", "false");

	};

	public Wechat getInstance() {
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
