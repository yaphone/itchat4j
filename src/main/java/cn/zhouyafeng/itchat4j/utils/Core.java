package cn.zhouyafeng.itchat4j.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

public class Core {
	private Core instance;

	private Core() {
		boolean alive = false;
		Storage storageClass = new Storage();
		List<Object> memberList = storageClass.getMemberList();
		List<Object> mpList = storageClass.getMpList();
		List<Object> chatroomList = storageClass.getChatroomList();
		List<Object> msgList = storageClass.getMsgList();
		Map<String, Object> loginInfo = new HashMap<String, Object>();
		HttpClient httpClient = HttpClients.createDefault();
		String uuid = null;
		Map<String, Object> functionDict = new HashMap<String, Object>();
		functionDict.put("FriendChat", new HashMap<Object, Object>());
		functionDict.put("GroupChat", new HashMap<Object, Object>());
		functionDict.put("MpChat", new HashMap<Object, Object>());
		boolean useHotReload = false;
		String hotReloadDir = "itchat.pkl";
		int receivingRetryCount = 5;

	}

	// 单例模式
	public Core getInstance() {
		if (instance == null) {
			synchronized (Core.class) {
				if (instance == null) {
					instance = new Core();
				}
			}
		}
		return instance;
	}

}
