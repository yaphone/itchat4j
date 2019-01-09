package cn.zhouyafeng.itchat4j.demo.demo1;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class MyWechat {

	private static MyWechat instance;

	private MyWechat() {

	}

	public static MyWechat getInstance() {
		if (instance == null) {
			synchronized (MyWechat.class) {
				instance = new MyWechat();
			}
		}
		return instance;
	}

	public Wechat wechat = null;

//	public static void main(String[] args) {
//		MyWechat.login();
//	}

	public void login(boolean reload) {
		final String qrPath = "/Users/zhoushiwei/Downloads"; //

		// 保存登陆二维码图片的路径，这里需要在本地新建目录
		final IMsgHandlerFace msgHandler = new SimpleDemo(); // 实现IMsgHandlerFace接口的类
		wechat = new Wechat(msgHandler, qrPath, true); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片

	}
}