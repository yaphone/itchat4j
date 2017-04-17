package cn.zhouyafeng.itchat4j.components;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.Tools;

public class Message {
	private static Logger logger = Logger.getLogger("Message");
	private static Core core = Core.getInstance();

	public static Object getDownloadFn() {
		// TODO
		return null;
	};

	public static JSONArray produceMsg(JSONArray msgList) {
		JSONArray result = new JSONArray();
		List<Integer> srl = Arrays.asList(40, 43, 50, 52, 53, 9999);
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = new JSONObject();
			JSONObject m = msgList.getJSONObject(i);
			if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) {
				produceGroupChat(core, m);
				m.remove("Content");
			} else {
				Tools.msgFormatter(m, "Content");
			}
			if (m.getInteger("MsgType") == 1) { // words
				if (m.getString("Url").length() != 0) {
					String regEx = "(.+?\\(.+?\\))";
					Matcher matcher = Tools.getMatcher(regEx, m.getString("Content"));
					String data = "Map";
					if (matcher.find()) {
						data = matcher.group(1);
					}
					msg.put("Type", "Map");
					msg.put("Text", data);
				} else {
					msg.put("Type", "Text");
					msg.put("Text", m.getString("Content"));
				}
			} else if (m.getInteger("MsgType") == 3 || m.getInteger("MsgType") == 47) { // picture
				getDownloadFn();
			} else if (m.getInteger("MsgType") == 34) { // voidce

			} else if (m.getInteger("MsgType") == 37) {// friends

			} else if (m.getInteger("MsgType") == 42) { // name card

			} else if (m.getInteger("MsgType") == 43 || m.getInteger("MsgType") == 62) {// tiny
																						// video

			} else if (m.getInteger("MsgType") == 49) { // sharing

			} else if (m.getInteger("MsgType") == 51) {// phone init

			} else if (m.getInteger("MsgType") == 10000) {//

			} else if (m.getInteger("MsgType") == 10002) {

			} else {
				logger.info("Useless msg");
			}
			m.put("Type", msg.getString("Type"));
			m.put("Text", msg.getString("Text"));
			result.add(m);
		}
		System.out.println(result);
		return result;
	}

	static void produceGroupChat(Core core, JSONObject m) {
		// TODO
	};

}
