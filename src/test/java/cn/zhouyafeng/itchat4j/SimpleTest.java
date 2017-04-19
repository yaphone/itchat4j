package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class SimpleTest {
	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new MsgHandler();
		Wechat wechat = new Wechat(msgHandler);
		wechat.start();
		// wechat.send();

	}
}
