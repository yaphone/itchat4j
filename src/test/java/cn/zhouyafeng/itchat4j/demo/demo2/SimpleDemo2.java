package cn.zhouyafeng.itchat4j.demo.demo2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.Wechat2;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MsgType;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;

/**
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月13日 下午2:44:58
 * @version 1.0
 *
 */
public class SimpleDemo2 implements IMsgHandlerFace {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleDemo2.class);

	@Override
	public String textMsgHandle(JSONObject msg) {
		String docFilePath = "D:/itchat4j/pic/test.docx";
		// String pngFilePath = "D:/itchat4j/pic/test.png";
		// String pdfFilePath = "D:/itchat4j/pic/测试.pdf";
		// String txtFilePath = "D:/itchat4j/pic/test.txt";
		MessageTools.sendFileMsgByUserId(msg.getString("FromUserName"), docFilePath);
		// MessageTools.sendFileMsgByNickName("yaphone", pngFilePath);
		// MessageTools.sendFileMsgByNickName("yaphone", pdfFilePath);
		// MessageTools.sendFileMsgByNickName("yaphone", txtFilePath);
		// logger.info("info" + msg.toJSONString());
		// System.out.println("*************");
		if (!msg.getBoolean("groupMsg")) {
			// MessageTools.sendFileMsgByUserId(msg.getString("FromUserName"),
			// docFilePath);
			LOG.info("联系人总数: " + WechatTools.getContactList().size());
			String text = msg.getString("Text");
			return text;
		}
		return null;
		// return null;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg";
		DownloadTools.getDownloadFn(msg, MsgType.PIC, picPath);
		return "图片保存成功";
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgType.VOICE, voicePath);
		return "声音保存成功";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		System.out.println(msg);
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgType.VIEDO, viedoPath);
		return "视频保存成功";
	}

	@Override
	public String nameCardMsgHandle(JSONObject msg) {
		return "收到名片消息";
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new SimpleDemo2();
		Wechat2 wechat = new Wechat2(msgHandler, "D://itchat4j/login");
		wechat.start();
	}
}
