package cn.zhouyafeng.itchat4j.demo.unuseful;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.AssistTools;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

/**
 * 自用的测试类，请无视
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月22日 下午10:41:44
 * @version 1.0
 *
 */
public class UnusefulDemo implements IMsgHandlerFace {

	@Override
	public String textMsgHandle(JSONObject msg) {
		if (!msg.getBoolean("groupMsg")) { // 群消息不处理
			String text = msg.getString("Text"); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			if (text.equals("111")) {
				String username = "yaphone";
				String password = "123456";
				String localPath = "D://itchat4j/pic/1.jpg";
				String uploadUrl = "http://127.0.0.1/file/put";
				try {
					AssistTools.sendQrPicToServer(username, password, uploadUrl, localPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return text;
		}
		return null;
	}

	@Override
	public String picMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String voiceMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String viedoMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String nameCardMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new UnusefulDemo();
		Wechat wechat = new Wechat(msgHandler, "D://itchat4j/login");
		wechat.start();
	}

	@Override
	public void sysMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
	}

}
