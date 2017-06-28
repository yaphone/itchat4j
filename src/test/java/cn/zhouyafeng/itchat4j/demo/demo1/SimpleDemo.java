package cn.zhouyafeng.itchat4j.demo.demo1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;

/**
 * 简单示例程序，收到文本信息自动回复原信息，收到图片、语音、小视频后根据路径自动保存
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月25日 上午12:18:09
 * @version 1.0
 *
 */
public class SimpleDemo implements IMsgHandlerFace {
	Logger LOG = Logger.getLogger(SimpleDemo.class);

	@Override
	public String textMsgHandle(JSONObject msg) {
		if (!msg.getBoolean("groupMsg")) { // 群消息不处理
			String text = msg.getString("Text"); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			return text;
		}
		return null;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());// 这里使用收到图片的时间作为文件名
		String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg"; // 调用此方法来保存图片
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath); // 保存图片的路径
		return "图片保存成功";
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
		return "声音保存成功";
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
		String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
		DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
		return "视频保存成功";
	}

	@Override
	public String nameCardMsgHandle(JSONObject msg) {
		return "收到名片消息";
	}

	@Override
	public void sysMsgHandle(JSONObject msg) { // 收到系统消息
		String text = msg.getString("Content");
		LOG.info(text);
	}

	@Override
	public String verifyAddFriendMsgHandle(JSONObject msg) {
		String nickName = msg.getJSONObject("RecommendInfo").getString("NickName");
		String text = "你好" + nickName + "， 欢迎添加我为好友！";
		return text;
	}

}
