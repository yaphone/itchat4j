package cn.zhouyafeng.itchat4j;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.DownloadTools;
import cn.zhouyafeng.itchat4j.utils.MsgType;

/**
 * 消息处理类
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月20日 上午12:19:52
 * @version 1.0
 *
 */
public class MsgHandler implements IMsgHandlerFace {

	public String textMsgHandle(JSONObject msg) {
		String text = msg.getString("Text");
		return text;
	}

	public String picMsgHandle(JSONObject msg) {
		String path = "D:" + File.separator + "test.jpg";
		DownloadTools.getDownloadFn(msg, MsgType.PIC, path);
		return "下载图片成功";
	}

	public String voiceMsgHandle(JSONObject msg) {
		String path = "D:" + File.separator + "test.mp3";
		DownloadTools.getDownloadFn(msg, MsgType.VOICE, path);
		return "下载声音成功";
	}

	public String viedoMsgHandle(JSONObject msg) {
		String path = "D:" + File.separator + "test.mp4";
		DownloadTools.getDownloadFn(msg, MsgType.VIEDO, path);
		return "下载小视频成功";
	}

}
