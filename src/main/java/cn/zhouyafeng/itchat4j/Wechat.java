package cn.zhouyafeng.itchat4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zhouyafeng.itchat4j.controller.LoginController;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.core.MsgCenter;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.SleepUtils;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

public class Wechat {
	private static final Logger LOG = LoggerFactory.getLogger(Wechat.class);
	private IMsgHandlerFace msgHandler;
	private Core core = Core.getInstance();
	private MyHttpClient myHttpClient = core.getMyHttpClient();

	public Wechat(IMsgHandlerFace msgHandler, String qrPath) {
		System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
		this.msgHandler = msgHandler;

		// 登陆
		LoginController login = new LoginController();
		login.login(qrPath);
		restart();
	}

	public void start() {
		LOG.info("+++++++++++++++++++开始消息处理+++++++++++++++++++++");
		new Thread(new Runnable() {
			@Override
			public void run() {
				MsgCenter.handleMsg(msgHandler);
			}
		}).start();
	}

	public void restart() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					while (!core.isAlive()) {
						LOG.info("******************尝试重新登陆************************");
						String uin = MyHttpClient.getCookie("wxuin");
						if (uin != null) {
							String url = String.format(URLEnum.WEB_WX_PUSH_LOGIN.getUrl(),
									core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()), uin);
							List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
							params.add(new BasicNameValuePair("uin", uin));
							LOG.info(uin);
							HttpEntity entity = myHttpClient.doGet(url, null, true, null);
							try {
								String result = EntityUtils.toString(entity);
								System.out.println(result);
							} catch (Exception e) {
								LOG.info("*********");
							}
						}
						SleepUtils.sleep(5000);

					}
					SleepUtils.sleep(2000);
				}
			}
		}) {
		}.start();
	}

}
