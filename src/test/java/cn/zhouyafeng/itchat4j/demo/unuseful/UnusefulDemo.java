package cn.zhouyafeng.itchat4j.demo.unuseful;

import java.io.IOException;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.AssistTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
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
	public String textMsgHandle(BaseMsg msg) {
		if (!msg.isGroupMsg()) { // 群消息不处理
			final String text = msg.getText(); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			if (text.equals("111")) {
				final String username = "yaphone";
				final String password = "123456";
				final String localPath = "D://itchat4j/pic/1.jpg";
				final String uploadUrl = "http://127.0.0.1/file/put";
				try {
					AssistTools.sendQrPicToServer(username, password, uploadUrl, localPath);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			return text;
		}
		return null;
	}

	@Override
	public String picMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String voiceMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String viedoMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String nameCardMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		final IMsgHandlerFace msgHandler = new UnusefulDemo();
		final Wechat wechat = new Wechat(msgHandler, "D://itchat4j/login");
		wechat.start();
	}

	@Override
	public void sysMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public String verifyAddFriendMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mediaMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void outlineHandle(BaseMsg msg) {
		// TODO Auto-generated method stub

	}

}
