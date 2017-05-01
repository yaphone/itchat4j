package cn.zhouyafeng.itchat4j.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSONObject;

/**
 * 核心存储类，全局只保存一份，单例模式
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月23日 下午2:33:56
 * @version 1.0
 *
 */
public class Core {

	private static Core instance;

	private Core() {

	}

	public static Core getInstance() {
		if (instance == null) {
			synchronized (Core.class) {
				instance = new Core();
			}
		}
		return instance;
	}

	boolean alive = false;
	Storage storageClass = Storage.getInstance();
	List<JSONObject> memberList = storageClass.getMemberList();
	List<JSONObject> mpList = storageClass.getMpList();
	List<Object> chatroomList = storageClass.getChatroomList();
	List<JSONObject> msgList = storageClass.getMsgList();
	Map<String, Object> loginInfo = new HashMap<String, Object>();
	CloseableHttpClient httpClient = HttpClients.createDefault();
	MyHttpClient myHttpClient = new MyHttpClient();
	String uuid = null;

	Map<String, Object> functionDict = new HashMap<String, Object>() {
		{
			put("FriendChat", new HashMap<Object, Object>());
			put("GroupChat", new HashMap<Object, Object>());
			put("MpChat", new HashMap<Object, Object>());
		}
	};

	boolean useHotReload = false;
	String hotReloadDir = "itchat.pkl";
	int receivingRetryCount = 5;

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Storage getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(Storage storageClass) {
		this.storageClass = storageClass;
	}

	public List<JSONObject> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<JSONObject> memberList) {
		this.memberList = memberList;
	}

	public List<JSONObject> getMpList() {
		return mpList;
	}

	public void setMpList(List<JSONObject> mpList) {
		this.mpList = mpList;
	}

	public List<Object> getChatroomList() {
		return chatroomList;
	}

	public void setChatroomList(List<Object> chatroomList) {
		this.chatroomList = chatroomList;
	}

	public Map<String, Object> getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(Map<String, Object> loginInfo) {
		this.loginInfo = loginInfo;
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, Object> getFunctionDict() {
		return functionDict;
	}

	public void setFunctionDict(Map<String, Object> functionDict) {
		this.functionDict = functionDict;
	}

	public boolean isUseHotReload() {
		return useHotReload;
	}

	public void setUseHotReload(boolean useHotReload) {
		this.useHotReload = useHotReload;
	}

	public String getHotReloadDir() {
		return hotReloadDir;
	}

	public void setHotReloadDir(String hotReloadDir) {
		this.hotReloadDir = hotReloadDir;
	}

	public int getReceivingRetryCount() {
		return receivingRetryCount;
	}

	public void setReceivingRetryCount(int receivingRetryCount) {
		this.receivingRetryCount = receivingRetryCount;
	}

	public MyHttpClient getMyHttpClient() {
		return myHttpClient;
	}

	public void setMyHttpClient(MyHttpClient myHttpClient) {
		this.myHttpClient = myHttpClient;
	}

	public List<JSONObject> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<JSONObject> msgList) {
		this.msgList = msgList;
	}

}
