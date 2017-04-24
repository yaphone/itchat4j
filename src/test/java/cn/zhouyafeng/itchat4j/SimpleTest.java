package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.demo.TulingRobot;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class SimpleTest {
	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new TulingRobot();
		Wechat wechat = new Wechat(msgHandler, "D://login");
		wechat.start();
	}
}
