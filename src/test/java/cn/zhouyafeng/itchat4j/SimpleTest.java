package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.components.Login;

public class SimpleTest {
	public static void main(String[] args) {
		System.setProperty("jsse.enableSNIExtension", "false");
		Login login = new Login();
		// wechat.getQRuuid();
		// wechat.getQR();
		// wechat.checkLogin();
		try {
			login.login();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
