package cn.zhouyafeng.itchat4j;

public class SimpleTest {
	public static void main(String[] args) {
		Wechat wechat = new Wechat();
		// wechat.getQRuuid();
		// wechat.getQR();
		// wechat.checkLogin();
		try {
			wechat.login();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
