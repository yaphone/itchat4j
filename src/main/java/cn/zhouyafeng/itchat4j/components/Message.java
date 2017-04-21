package cn.zhouyafeng.itchat4j.components;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.utils.Core;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.Tools;

public class Message {
	private static Logger logger = Logger.getLogger("Message");
	private static Core core = Core.getInstance();
	private static MyHttpClient myHttpClient = core.getMyHttpClient();

	public static Object getDownloadFn(Core core, String url, String msgId) {
		// TODO 处理下载
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
				// getDownloadFn();
			} else if (m.getInteger("MsgType") == 34) { // voice

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
		return result;
	}

	public static void produceGroupChat(Core core, JSONObject m) {
		// TODO
	};

	public static void send(String msg, String toUserName, String mediaId) {
		sendMsg(msg, toUserName);
	}

	public static void sendMsg(String msg, String toUserName) {
		logger.info(String.format("Request to send a text message to %s: %s", toUserName, msg));
		sendRawMsg(1, msg, toUserName);
	}

	public static void sendRawMsg(int msgType, String content, String toUserName) {
		String url = String.format("%s/webwxsendmsg", core.getLoginInfo().get("url"));

		Map<String, Object> paramMap = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> baseRequestMap = (Map<String, Map<String, String>>) core.getLoginInfo()
				.get("baseRequest");
		paramMap.put("BaseRequest", baseRequestMap.get("BaseRequest"));
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("Type", msgType);
		msgMap.put("Content", content);
		msgMap.put("FromUserName", core.getStorageClass().getUserName());
		msgMap.put("ToUserName", toUserName == null ? core.getStorageClass().getUserName() : toUserName);
		msgMap.put("LocalID", new Date().getTime() * 10);
		msgMap.put("ClientMsgId", new Date().getTime() * 10);
		paramMap.put("Msg", msgMap);
		paramMap.put("Scene", 0);
		try {
			StringEntity params = new StringEntity(JSON.toJSONString(paramMap), "UTF-8");
			HttpEntity entity = myHttpClient.doPost(url, params);
			EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

	}

}
