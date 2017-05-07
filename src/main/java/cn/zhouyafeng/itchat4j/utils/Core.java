package cn.zhouyafeng.itchat4j.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int memberCount = 0;

	private List<JSONObject> userSelfList = storageClass.getUserSelfList();
	private List<JSONObject> memberList = storageClass.getMemberList();
	private List<JSONObject> contactList = storageClass.getContactList();
	private List<JSONObject> groupList = storageClass.getGroupList();
	private List<JSONObject> groupMemeberList = storageClass.getGroupList();
	private List<JSONObject> publicUsersList = storageClass.getPublicUsersList();
	private List<JSONObject> specialUsersList = storageClass.getSpecialUsersList();
	private List<JSONObject> msgList = storageClass.getMsgList();

	private List<String> groupIdList = new ArrayList<String>(); // 群聊，以String格式保存群的userName，如@@37da24fee2114e9475729b942d130190ffddb669411228651da3e8a8933603c8

	Map<String, Object> loginInfo = new HashMap<String, Object>();
	// CloseableHttpClient httpClient = HttpClients.createDefault();
	MyHttpClient myHttpClient = MyHttpClient.getInstance();
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

	public Map<String, Object> getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(Map<String, Object> loginInfo) {
		this.loginInfo = loginInfo;
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

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
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

	public List<JSONObject> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<JSONObject> msgList) {
		this.msgList = msgList;
	}

	public void setMyHttpClient(MyHttpClient myHttpClient) {
		this.myHttpClient = myHttpClient;
	}

	public List<String> getGroupIdList() {
		return groupIdList;
	}

	public void setGroupIdList(List<String> groupIdList) {
		this.groupIdList = groupIdList;
	}

	public List<JSONObject> getUserSelfList() {
		return userSelfList;
	}

	public void setUserSelfList(List<JSONObject> userSelfList) {
		this.userSelfList = userSelfList;
	}

	public List<JSONObject> getContactList() {
		return contactList;
	}

	public void setContactList(List<JSONObject> contactList) {
		this.contactList = contactList;
	}

	public List<JSONObject> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<JSONObject> groupList) {
		this.groupList = groupList;
	}

	public List<JSONObject> getGroupMemeberList() {
		return groupMemeberList;
	}

	public void setGroupMemeberList(List<JSONObject> groupMemeberList) {
		this.groupMemeberList = groupMemeberList;
	}

	public List<JSONObject> getPublicUsersList() {
		return publicUsersList;
	}

	public void setPublicUsersList(List<JSONObject> publicUsersList) {
		this.publicUsersList = publicUsersList;
	}

	public List<JSONObject> getSpecialUsersList() {
		return specialUsersList;
	}

	public void setSpecialUsersList(List<JSONObject> specialUsersList) {
		this.specialUsersList = specialUsersList;
	}

}
