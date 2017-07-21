package cn.zhouyafeng.itchat4j.demo.demo3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;

/**
 * 此示例演示如何获取所有好友的头像
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年6月26日 下午11:27:46
 * @version 1.0
 *
 */
public class PicYourFriends implements IMsgHandlerFace {
	private static Logger LOG = LoggerFactory.getLogger(PicYourFriends.class);
	private static final Core core = Core.getInstance();
	private static final MyHttpClient myHttpClient = core.getMyHttpClient();
	private static final String path = "D://itchat4j//head"; // 这里需要设置保存头像的路径

	@Override
	public String textMsgHandle(BaseMsg msg) {

		if (!msg.isGroupMsg()) { // 群消息不处理
			String text = msg.getText(); // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
			String baseUrl = "https://" + core.getIndexUrl(); // 基础URL
			String skey = (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey());
			if (text.equals("111")) {
				LOG.info("开始下载好友头像");
				List<JSONObject> friends = WechatTools.getContactList();
				for (int i = 0; i < friends.size(); i++) {
					JSONObject friend = friends.get(i);
					String url = baseUrl + friend.getString("HeadImgUrl") + skey;
					// String fileName = friend.getString("NickName");
					String headPicPath = path + File.separator + i + ".jpg";

					HttpEntity entity = myHttpClient.doGet(url, null, true, null);
					try {
						OutputStream out = new FileOutputStream(headPicPath);
						byte[] bytes = EntityUtils.toByteArray(entity);
						out.write(bytes);
						out.flush();
						out.close();

					} catch (Exception e) {
						LOG.info(e.getMessage());
					}

				}
			}
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

	@Override
	public void sysMsgHandle(BaseMsg msg) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		String qrPath = "D://itchat4j//login"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		IMsgHandlerFace msgHandler = new PicYourFriends(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler, qrPath); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
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

}
