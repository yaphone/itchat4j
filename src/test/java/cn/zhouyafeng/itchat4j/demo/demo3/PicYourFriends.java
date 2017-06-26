package cn.zhouyafeng.itchat4j.demo.demo3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;

public class PicYourFriends implements IMsgHandlerFace {
	private static Logger LOG = LoggerFactory.getLogger(PicYourFriends.class);
	private static final Core core = Core.getInstance();
	private static final MyHttpClient myHttpClient = core.getMyHttpClient();
	private static final String path = "D://itchat4j//head";

	@Override
	public String textMsgHandle(JSONObject msg) {

		if (!msg.getBoolean("groupMsg")) { // 群消息不处理
			String text = msg.getString("Text"); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			String baseUrl = "https://" + core.getIndexUrl(); // 基础URL
			String skey = (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey());
			List<String> headUrlList = new ArrayList<String>(); // 好友头像URL列表
			if (text.equals("111")) {
				List<JSONObject> friends = WechatTools.getContactList();
				for (JSONObject friend : friends) {
					headUrlList.add(friend.getString("HeadImgUrl"));
				}
			}
			for (int i = 0; i < headUrlList.size(); i++) {

				String url = baseUrl + headUrlList.get(i) + skey;
				LOG.info(url);
				String headPicPath = path + File.separator + i + ".jpg";
				HttpEntity entity = myHttpClient.doGet(url, null, true, null);
				try {
					OutputStream out = new FileOutputStream(headPicPath);
					byte[] bytes = EntityUtils.toByteArray(entity);
					out.write(bytes);
					out.flush();
					out.close();
					try {
						// CommonTools.printQr(qrPath); // 打开登陆二维码图片
					} catch (Exception e) {
						LOG.info(e.getMessage());
					}

				} catch (Exception e) {
					LOG.info(e.getMessage());
				}

			}
			System.out.println(headUrlList);
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
		String qrPath = "D://itchat4j//login"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		IMsgHandlerFace msgHandler = new PicYourFriends(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
	}

}
