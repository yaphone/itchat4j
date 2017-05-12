package cn.zhouyafeng.itchat4j.core;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.MsgType;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTool;

public class MsgCenter {
	private static Logger LOG = LoggerFactory.getLogger(MsgCenter.class);

	private static Core core = Core.getInstance();

	/**
	 * 接收消息，放入队列
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList
	 * @return
	 */
	public static JSONArray produceMsg(JSONArray msgList) {
		JSONArray result = new JSONArray();
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = new JSONObject();
			JSONObject m = msgList.getJSONObject(i);
			m.put("groupMsg", false);// 是否是群消息
			if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) { // 群聊消息
				// produceGroupChat(core, m);
				// m.remove("Content");
				if (m.getString("FromUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("FromUserName"))) {
					core.getGroupIdList().add((m.getString("FromUserName")));
				} else if (m.getString("ToUserName").contains("@@")
						&& !core.getGroupIdList().contains(m.getString("ToUserName"))) {
					core.getGroupIdList().add((m.getString("ToUserName")));
				}
				// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
				if (m.getString("Content").contains("<br/>")) {
					String content = m.getString("Content").substring(m.getString("Content").indexOf("<br/>") + 5);
					m.put("Content", content);
					m.put("groupMsg", true);
				}
			} else {
				CommonTool.msgFormatter(m, "Content");
			}
			if (m.getInteger("MsgType") == MsgType.MSGTYPE_TEXT) { // words 文本消息
				if (m.getString("Url").length() != 0) {
					String regEx = "(.+?\\(.+?\\))";
					Matcher matcher = CommonTool.getMatcher(regEx, m.getString("Content"));
					String data = "Map";
					if (matcher.find()) {
						data = matcher.group(1);
					}
					msg.put("Type", "Map");
					msg.put("Text", data);
				} else {
					msg.put("Type", MsgType.TEXT);
					msg.put("Text", m.getString("Content"));
				}
				m.put("Type", msg.getString("Type"));
				m.put("Text", msg.getString("Text"));
			} else if (m.getInteger("MsgType") == MsgType.MSGTYPE_IMAGE
					|| m.getInteger("MsgType") == MsgType.MSGTYPE_EMOTICON) { // 图片消息
				m.put("Type", MsgType.PIC);
			} else if (m.getInteger("MsgType") == MsgType.MSGTYPE_VOICE) { // 语音消息
				m.put("Type", MsgType.VOICE);
			} else if (m.getInteger("MsgType") == 37) {// friends 好友确认消息

			} else if (m.getInteger("MsgType") == 42) { // 共享名片
				m.put("Type", MsgType.NAMECARD);

			} else if (m.getInteger("MsgType") == MsgType.MSGTYPE_VIDEO
					|| m.getInteger("MsgType") == MsgType.MSGTYPE_MICROVIDEO) {// viedo
				m.put("Type", MsgType.VIEDO);
			} else if (m.getInteger("MsgType") == 49) { // sharing 分享链接

			} else if (m.getInteger("MsgType") == 51) {// phone init 微信初始化消息

			} else if (m.getInteger("MsgType") == 10000) {// 系统消息

			} else if (m.getInteger("MsgType") == 10002) { // 撤回消息

			} else {
				LOG.info("Useless msg");
			}
			result.add(m);
		}
		return result;
	}

}
