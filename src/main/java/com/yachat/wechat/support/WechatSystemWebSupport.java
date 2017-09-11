package com.yachat.wechat.support;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vdurmont.emoji.EmojiParser;
import com.yachat.wechat.Account;
import com.yachat.wechat.MessageHandler;
import com.yachat.wechat.MessageSender;
import com.yachat.wechat.WechatInterface;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHandler;
import com.yachat.wechat.http.TryRetryClient;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.MessageKeys;
import com.yachat.wechat.keys.PatternKeys;
import com.yachat.wechat.keys.StatusKeys;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;
import com.yachat.wechat.message.Message;
import com.yachat.wechat.message.TextMessageResponse;
import com.yachat.wechat.support.handlers.BatchGetContactHandler;
import com.yachat.wechat.support.handlers.GetContactHandler;
import com.yachat.wechat.support.handlers.InitHandler;
import com.yachat.wechat.support.handlers.LoginHandler;
import com.yachat.wechat.support.handlers.QRHandler;
import com.yachat.wechat.support.handlers.StatusNotifyHandler;
import com.yachat.wechat.support.handlers.SyncHandler;
import com.yachat.wechat.support.handlers.SyncStatusHandler;
import com.yachat.wechat.support.handlers.UuidHandler;
import com.yachat.wechat.utils.MatchUtils;

@SuppressWarnings("rawtypes")
public class WechatSystemWebSupport implements WechatInterface, MessageSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatSystemWebSupport.class);

	enum WechatOperationType {
		UUID, QR, LOGIN, INIT, 
		STATUS_NOTIFY, 
		SYNC, 
		SYNC_STATUS , 
		GET_CONTACT, 
		BATCH_GET_CONTACT ,
		SYNC_MESSAGE ,
		SEND_MESSAGE;
	}

	private TryRetryClient retryClient;
	private Map<WechatOperationType, RetryHandler> handlers;

	public WechatSystemWebSupport() {
		this.retryClient = new TryRetryClient();
		this.handlers = new ConcurrentHashMap<>();
		this.init();
	}

	private void init() {
		this.handlers.put(WechatOperationType.UUID, new UuidHandler());
		this.handlers.put(WechatOperationType.QR, new QRHandler());
		this.handlers.put(WechatOperationType.LOGIN, new LoginHandler(this.retryClient));
		this.handlers.put(WechatOperationType.INIT, new InitHandler());
		this.handlers.put(WechatOperationType.STATUS_NOTIFY, new StatusNotifyHandler());
		this.handlers.put(WechatOperationType.SYNC, new SyncHandler());
		this.handlers.put(WechatOperationType.SYNC_STATUS, new SyncStatusHandler());
		this.handlers.put(WechatOperationType.GET_CONTACT, new GetContactHandler(this.retryClient));
		this.handlers.put(WechatOperationType.BATCH_GET_CONTACT, new BatchGetContactHandler());
	}

	@SuppressWarnings("unchecked")
	private <IN, OUT> RetryHandler<IN, OUT> getHandler(WechatOperationType operationType) {
		return this.handlers.get(operationType);
	}

	private <IN, OUT> OUT get(IN in, WechatOperationType operationType) {
		if (operationType == null) {
			return null;
		}
		if (!this.handlers.containsKey(operationType)) {
			return null;
		}
		return retryClient.retryGet(in, getHandler(operationType));
	}

	private <IN, OUT> OUT post(IN account, WechatOperationType operationType) {
		if (operationType == null) {
			return null;
		}
		if (!this.handlers.containsKey(operationType)) {
			return null;
		}
		return retryClient.retryPost(account, getHandler(operationType));
	}

	@Override
	public String getUuid() {
		return get(null, WechatOperationType.UUID);
	}

	@Override
	public InputStream getQR(String uuid) {
		InputStream stream = post(uuid, WechatOperationType.QR);
		return stream;
	}

	@Override
	public boolean login(Account account) {
		account.setCookie(new BasicCookieStore());
		return get(account, WechatOperationType.LOGIN);
	}

	@Override
	public boolean wxInit(Account account) {
		return post(account, WechatOperationType.INIT);
	}

	@Override
	public void wxStatusNotify(Account account) {
		post(account, WechatOperationType.STATUS_NOTIFY);
	}

	@Override
	public void wxGetContact(Account account) {
		post(account, WechatOperationType.GET_CONTACT);
	}

	@Override
	public void wxBatchGetContact(Account account) {
		post(account, WechatOperationType.BATCH_GET_CONTACT);
	}

	private JSONObject sync(Account account) {
		return post(account, WechatOperationType.SYNC);
	}

	private Map<String, String> syncStatus(Account account) {
		return get(account, WechatOperationType.SYNC_STATUS);
	}

	@Override
	public void wxSyncMessage(Account account, MessageHandler messageHandler) {
		if (!account.isAlive()) {
			return;
		}
		Map<String, String> status = this.syncStatus(account);
		LOGGER.info(JSONObject.toJSONString(status));
		String retcode = WechatKeys.retcode.get(status);
		String selector = WechatKeys.selector.get(status);
		if (StatusKeys.NORMAL.is(retcode)) {
			account.setLastNormalRetcodeTime(System.currentTimeMillis()); // 最后收到正常报文时间
			JSONObject msgObj = this.sync(account);
			if (selector.equals("2")) {
				this.executeSyncMessage(account, msgObj, messageHandler);
			} else if (selector.equals("6")) {
				this.addGroupInfo(msgObj, account);
			} else if (selector.equals("7")) {
				this.sync(account);
			}
		} else {
			StatusKeys enum1 = StatusKeys.of(retcode);
			if (enum1 != null) {
				LOGGER.info(enum1.getValue());
				if (enum1.isOffline()) {
					account.offline();
				}
			} else {
				this.sync(account);
			}
		}
	}

	private void executeSyncMessage(Account account, JSONObject message, MessageHandler messageHandler) {
		List<Message> messages = new ArrayList<>();
		if (message == null) {
			return;
		}
		JSONArray msgList = message.getJSONArray("AddMsgList");
		msgList = this.buildWechatMessage(msgList, account);
		for (int j = 0; j < msgList.size(); j++) {
			Message baseMsg = JSON.toJavaObject(msgList.getJSONObject(j), Message.class);
			messages.add(baseMsg);
		}
		this.handler( account , messages, messageHandler);
	}

	private void addGroupInfo(JSONObject msg, Account account) {
		JSONArray msgList = new JSONArray();
		msgList = msg.getJSONArray("AddMsgList");
		JSONArray modContactList = msg.getJSONArray("ModContactList"); // 存在删除或者新增的好友信息
		msgList = this.buildWechatMessage(msgList, account);
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
				formatter(m, "Content");
			}

			int msgType = m.getInteger("MsgType").intValue();
			MessageKeys messageKey = MessageKeys.of(msgType);
			if (messageKey == null) {
				LOGGER.info("Useless msg");
			} else if (messageKey.isText()) {
				if (m.getString("Url").length() != 0) {
					String data = PatternKeys.WechatMessage.match1(m.getString("Content"));
					msg.put("Type", "Map");
					msg.put("Text", StringUtils.isNotBlank(data) ? data : "Map");
				} else {
					msg.put("Type", MessageKeys.TEXT.getKey());
					msg.put("Text", m.getString("Content"));
				}
				m.put("Type", msg.getString("Type"));
				m.put("Text", msg.getString("Text"));
			} else {
				MessageKeys type = messageKey.getType();
				if (type != null) {
					m.put("Type", type.getKey());
				}
			}
			LOGGER.info("收到消息一条，来自: " + m.getString("FromUserName"));
			result.add(m);
		}
		return result;
	}

	private void handler(Account account, List<Message> messages, MessageHandler messageHandler) {
		if (messages == null || messages.size() == 0) {
			return;
		}
		for (Message msg : messages) {
			if (msg.getType() != null) {
				try {
					if (MessageKeys.TEXT.is(msg)) {
						String result = messageHandler.text(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.PIC.is(msg)) {
						String result = messageHandler.picture(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.VOICE.is(msg)) {
						String result = messageHandler.voice(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.VIEDO.is(msg)) {
						String result = messageHandler.video(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.NAMECARD.is(msg)) {
						String result = messageHandler.card(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.SYS.is(msg)) { // 系统消息
						messageHandler.sys(account, msg);
					} else if (MessageKeys.VERIFYMSG.is(msg)) { // 确认添加好友消息
						String result = messageHandler.verifyAddFriend(account, msg);
						LOGGER.info(result);
					} else if (MessageKeys.MEDIA.is(msg)) { // 多媒体消息
						String result = messageHandler.media(account, msg);
						LOGGER.info(result);
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 消息格式化
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午4:19:08
	 * @param d
	 * @param k
	 */
	private void formatter(JSONObject d, String k) {
		d.put(k, d.getString(k).replace("<br/>", "\n"));
		emojiFormatter(d, k);
		// TODO 与emoji表情有部分兼容问题，目前暂未处理解码处理 d.put(k,
		// StringEscapeUtils.unescapeHtml4(d.getString(k)));
	}

	/**
	 * 处理emoji表情
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午2:39:04
	 * @param d
	 * @param k
	 */
	private void emojiFormatter(JSONObject d, String k) {
		Matcher matcher = MatchUtils.getMatcher(PatternKeys.EmojiFormatter.getKey(), d.getString(k));
		StringBuilder sb = new StringBuilder();
		String content = d.getString(k);
		int lastStart = 0;
		while (matcher.find()) {
			String str = matcher.group(1);
			if (str.length() == 6) {

			} else if (str.length() == 10) {

			} else {
				str = "&#x" + str + ";";
				String tmp = content.substring(lastStart, matcher.start());
				sb.append(tmp + str);
				lastStart = matcher.end();
			}
		}
		if (lastStart < content.length()) {
			sb.append(content.substring(lastStart));
		}
		if (sb.length() != 0) {
			d.put(k, EmojiParser.parseToUnicode(sb.toString()));
		} else {
			d.put(k, content);
		}
	}

	@Override
	public boolean sendTextMessage(Account account, String toUser, int messageType, String content) {
		Request request = Builders.of(UrlKeys.WEB_WX_SEND_MSG, WechatKeys.url.get(account)).build();
		Map<String, Object> msgMap = new HashMap<String, Object>();
		msgMap.put("Type", messageType);
		msgMap.put("Content", content);
		msgMap.put("FromUserName", account.getUserName());
		msgMap.put("ToUserName", toUser == null ? account.getUserName() : toUser);
		msgMap.put("LocalID", System.currentTimeMillis() * 10);
		msgMap.put("ClientMsgId", System.currentTimeMillis() * 10);
		Map<String, Object> paramMap = account.getParamMap();
		paramMap.put("Msg", msgMap);
		paramMap.put("Scene", 0);
		request.addAll(paramMap);
		request.setCookie(account.getCookie());
		try {
			return retryClient.post(request, (entity) -> {
				String content2 = EntityUtils.toString(entity, Consts.UTF_8);
				TextMessageResponse response = JSON.parseObject(content2, TextMessageResponse.class);
				LOGGER.info(content2);
				return Response.success(response.isSuccess());
			});
		} catch (Exception e) {
			LOGGER.error("The Wechat send message error.", e);
			return false;
		}
	}

	@Override
	public boolean sendImageMessage(Account account) {
		return false;
	}

}
