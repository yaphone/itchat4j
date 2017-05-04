package cn.zhouyafeng.itchat4j;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.components.Login;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.MsgType;

/**
 * 主类，初始化工作
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月25日 上午12:42:54
 * @version 1.0
 *
 */
public class Wechat {
	private static Logger logger = Logger.getLogger("Wechat");
	private static Core core = Core.getInstance();

	private IMsgHandlerFace msgHandler;

	public Wechat(IMsgHandlerFace msgHandler, String qrPath) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误

		this.msgHandler = msgHandler;
		Login login = new Login();
		login.login(qrPath);

	};

	public void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (core.getMsgList().size() > 0 && core.getMsgList().get(0).getString("Content") != null) {
						// System.out.println(core.getMsgList().get(0));
						if (core.getMsgList().get(0).getString("Content").length() > 0) {
							JSONObject msg = core.getMsgList().get(0);
							if (msg.getString("Type") != null) {
								if (msg.getString("Type").equals(MsgType.TEXT)) {
									String result = msgHandler.textMsgHandle(msg);
									MessageTools.send(result, core.getMsgList().get(0).getString("FromUserName"), "");
								} else if (msg.getString("Type").equals(MsgType.PIC)) {
									String result = msgHandler.picMsgHandle(msg);
									MessageTools.send(result, core.getMsgList().get(0).getString("FromUserName"), "");
								} else if (msg.getString("Type").equals(MsgType.VOICE)) {
									String result = msgHandler.voiceMsgHandle(msg);
									MessageTools.send(result, core.getMsgList().get(0).getString("FromUserName"), "");
								} else if (msg.getString("Type").equals(MsgType.VIEDO)) {
									String result = msgHandler.viedoMsgHandle(msg);
									MessageTools.send(result, core.getMsgList().get(0).getString("FromUserName"), "");
								} else if (msg.getString("Type").equals(MsgType.NAMECARD)) {
									String result = msgHandler.nameCardMsgHandle(msg);
									MessageTools.send(result, core.getMsgList().get(0).getString("FromUserName"), "");
								}
							}
						}
						core.getMsgList().remove(0);
					}
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
