package com.yachat.wechat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgCodeEnum;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.enums.RetCodeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

/**
 * 微信
 * 
 * @author huangjiu@zto.cn
 *
 */
public class Wechat {

	private static final Logger LOGGER = LoggerFactory.getLogger(Wechat.class);
	private Account account;
	private WechatSystem system;
	private MessageHandler messageHandler;
	private WechatTaskManager wechatTaskManager;

	public Wechat(Account account, WechatSystem system, MessageHandler messageHandler,
			WechatTaskManager wechatTaskManager) {
		super();
		this.account = account;
		this.system = system;
		this.messageHandler = messageHandler;
	}

	private boolean isLogin() {
		return this.system.login(this.account);
	}

	public void start() {
		if (this.isLogin()) {
			this.start0();
		}
	}

	private void start0() {

		LOGGER.info("5. 登陆成功，微信初始化");
		this.system.webWxInit(this.account);
		LOGGER.info("6. 开启微信状态通知");
		this.system.wxStatusNotify(this.account);

		// LOGGER.info("7. 清除。。。。");
		// CommonTools.clearScreen();
		LOGGER.info(String.format("欢迎回来， %s", this.account.getNickName()));

		LOGGER.info("8. 开始接收消息");
		// this.system.startReceiving(this.account);

		// this.wechatTaskManager.addMessageListenter(this.account, messageHandler);

		LOGGER.info("9. 获取联系人信息");
		this.system.webWxGetContact(this.account);

		LOGGER.info("10. 获取群好友及群好友列表");
		this.system.WebWxBatchGetContact(this.account);

		LOGGER.info("11. 缓存本次登陆好友相关消息");
		this.cacheLoginFriendInfo(); // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

		LOGGER.info("12.开启微信状态检测线程");
		// new Thread(new CheckLoginStatusThread()).start();

		// this.wechatTaskManager.addCheckStatus(this.account);
		this.wechatTaskManager.addWechat(this);
	}

	private void cacheLoginFriendInfo() {
		for (JSONObject o : account.getContactList()) {
			account.getUserInfoMap().put(o.getString("NickName"), o);
			account.getUserInfoMap().put(o.getString("UserName"), o);
		}
	}

	public void stop() {
		this.wechatTaskManager.removeWechat(this);
		this.account.setAlive(false);
	}

	public synchronized Account getAccount() {
		return account;
	}

	public synchronized void offline() {
		this.account.setAlive(false);
	}

	public synchronized void online() {
		this.account.setAlive(true);
	}

	public boolean isOnline() {
		return this.account.isAlive();
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void receivingMessage() {
		if (!this.isOnline() ) {
			return;
		}
		Map<String, String> status = system.syncStatus(this.account);
		LOGGER.info(JSONObject.toJSONString(status));
		String retcode = status.get("retcode");
		String selector = status.get("selector");

		if (retcode.equals(RetCodeEnum.NORMAL.getCode())) {
			JSONObject msgObj = this.system.sync(account);
			account.setLastNormalRetcodeTime(System.currentTimeMillis()); // 最后收到正常报文时间
			if (selector.equals("2")) {
				List<BaseMsg> messages = this.buildMessages(msgObj);
				this.handler(messages);
			} else if (selector.equals("6")) {
				this.addGroupInfo(msgObj);
			} else if (selector.equals("7")) {
				this.system.sync(account);
			}
		} else {
			RetCodeEnum enum1 = RetCodeEnum.of(retcode);
			if (enum1 != null) {
				LOGGER.info(enum1.getType());
			} else {
				this.system.sync(account);
			}
		}
	}

	private List<BaseMsg> buildMessages(JSONObject msg) {
		List<BaseMsg> messages = new ArrayList<>();
		if (msg == null) {
			return messages;
		}
		JSONArray msgList = msg.getJSONArray("AddMsgList");
		msgList = this.buildWechatMessage(msgList, this.account);
		for (int j = 0; j < msgList.size(); j++) {
			BaseMsg baseMsg = JSON.toJavaObject(msgList.getJSONObject(j), BaseMsg.class);
			messages.add(baseMsg);
		}
		return messages;
	}

	private void addGroupInfo(JSONObject msg) {
		JSONArray msgList = new JSONArray();
		msgList = msg.getJSONArray("AddMsgList");
		JSONArray modContactList = msg.getJSONArray("ModContactList"); // 存在删除或者新增的好友信息
		msgList = this.buildWechatMessage(msgList, this.account);
		for (int j = 0; j < msgList.size(); j++) {
			JSONObject userInfo = modContactList.getJSONObject(j);
			// 存在主动加好友之后的同步联系人到本地
			account.getContactList().add(userInfo);
		}
	}

	private JSONArray buildWechatMessage(JSONArray msgList, Account account) {
		JSONArray result = new JSONArray();
		for (int i = 0; i < msgList.size(); i++) {
			JSONObject msg = new JSONObject();
			JSONObject m = msgList.getJSONObject(i);
			m.put("groupMsg", false);// 是否是群消息
			if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) { // 群聊消息
				if (m.getString("FromUserName").contains("@@")
						&& !account.getGroupIdList().contains(m.getString("FromUserName"))) {
					account.getGroupIdList().add((m.getString("FromUserName")));
				} else if (m.getString("ToUserName").contains("@@")
						&& !account.getGroupIdList().contains(m.getString("ToUserName"))) {
					account.getGroupIdList().add((m.getString("ToUserName")));
				}
				// 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
				if (m.getString("Content").contains("<br/>")) {
					String content = m.getString("Content").substring(m.getString("Content").indexOf("<br/>") + 5);
					m.put("Content", content);
					m.put("groupMsg", true);
				}
			} else {
				CommonTools.msgFormatter(m, "Content");
			}
			if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_TEXT.getCode())) { // words
																						// 文本消息
				if (m.getString("Url").length() != 0) {
					String regEx = "(.+?\\(.+?\\))";
					Matcher matcher = CommonTools.getMatcher(regEx, m.getString("Content"));
					String data = "Map";
					if (matcher.find()) {
						data = matcher.group(1);
					}
					msg.put("Type", "Map");
					msg.put("Text", data);
				} else {
					msg.put("Type", MsgTypeEnum.TEXT.getType());
					msg.put("Text", m.getString("Content"));
				}
				m.put("Type", msg.getString("Type"));
				m.put("Text", msg.getString("Text"));
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_IMAGE.getCode())
					|| m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_EMOTICON.getCode())) { // 图片消息
				m.put("Type", MsgTypeEnum.PIC.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VOICE.getCode())) { // 语音消息
				m.put("Type", MsgTypeEnum.VOICE.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode())) {// friends
				// 好友确认消息
				// MessageTools.addFriend(core, userName, 3, ticket); // 确认添加好友
				m.put("Type", MsgTypeEnum.VERIFYMSG.getType());

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) { // 共享名片
				m.put("Type", MsgTypeEnum.NAMECARD.getType());

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode())
					|| m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {// viedo
				m.put("Type", MsgTypeEnum.VIEDO.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MEDIA.getCode())) { // 多媒体消息
				m.put("Type", MsgTypeEnum.MEDIA.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {// phone
				// init
				// 微信初始化消息

			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {// 系统消息
				m.put("Type", MsgTypeEnum.SYS.getType());
			} else if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) { // 撤回消息

			} else {
				LOGGER.info("Useless msg");
			}
			LOGGER.info("收到消息一条，来自: " + m.getString("FromUserName"));
			result.add(m);
		}
		return result;
	}

	private void handler(List<BaseMsg> messages) {
		if (messages == null || messages.size() == 0) {
			return;
		}
		for (BaseMsg msg : messages) {
			if (msg.getType() != null) {
				try {
					if (msg.getType().equals(MsgTypeEnum.TEXT.getType())) {
						String result = messageHandler.textMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					} else if (msg.getType().equals(MsgTypeEnum.PIC.getType())) {
						String result = messageHandler.picMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					} else if (msg.getType().equals(MsgTypeEnum.VOICE.getType())) {
						String result = messageHandler.voiceMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					} else if (msg.getType().equals(MsgTypeEnum.VIEDO.getType())) {
						String result = messageHandler.viedoMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					} else if (msg.getType().equals(MsgTypeEnum.NAMECARD.getType())) {
						String result = messageHandler.nameCardMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					} else if (msg.getType().equals(MsgTypeEnum.SYS.getType())) { // 系统消息
						messageHandler.sysMsgHandle(msg);
					} else if (msg.getType().equals(MsgTypeEnum.VERIFYMSG.getType())) { // 确认添加好友消息
						String result = messageHandler.verifyAddFriendMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getRecommendInfo().getUserName());
					} else if (msg.getType().equals(MsgTypeEnum.MEDIA.getType())) { // 多媒体消息
						String result = messageHandler.mediaMsgHandle(msg);
						MessageTools.sendMsgById(result, account.getMsgList().get(0).getFromUserName());
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

}
