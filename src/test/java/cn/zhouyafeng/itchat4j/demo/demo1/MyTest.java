package cn.zhouyafeng.itchat4j.demo.demo1;

import java.io.File;
import java.util.Scanner;

import javax.swing.JOptionPane;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

/**
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 上午12:44:10
 * @version 1.0
 *
 */
public class MyTest {
	public static void main(String[] args) {
		String qrPath = "D://itchat4j//login"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		File c=new File(qrPath);
		c.mkdirs();
		IMsgHandlerFace msgHandler = new SimpleDemo(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
		Scanner reader=new Scanner(System.in);
		while(true){
			if(WechatTools.getWechatStatus()) {
				System.out.print("begin"+WechatTools.getContactNickNameList());
				MessageTools.sendMsgByNickName(reader.nextLine(), "Edward Yu");
			}
		}
	}
}
