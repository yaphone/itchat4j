package com.yachat.wechat.sys;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.WechatSystem;
import com.yachat.wechat.http.RetryHandler;
import com.yachat.wechat.http.TryRetryClient;

@SuppressWarnings("rawtypes")
public class WechatSystemWebSupport implements WechatSystem {
	
	enum WechatOperationType {
		UUID, 
		QR, 
		LOGIN, 
		INIT, 
		STATUS_NOTIFY, 
		SYNC , 
		SYNC_STATUS,
		GET_CONTACT , 
		BATCH_GET_CONTACT
		;
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

	private <IN, OUT> OUT get(IN account, WechatOperationType operationType) {
		if (operationType == null) {
			return null;
		}
		if( !this.handlers.containsKey(operationType)) {
			return null;
		}
		return retryClient.get(account, getHandler(operationType));
	}

	private <IN, OUT> OUT post(IN account, WechatOperationType operationType) {
		if (operationType == null) {
			return null;
		}
		if( !this.handlers.containsKey(operationType)) {
			return null;
		}
		return retryClient.post(account, getHandler(operationType));
	}

	@Override
	public String getUuid() {
		return get(null, WechatOperationType.UUID);
	}

	@Override
	public InputStream getQR(String uuid) {
		InputStream stream = get(uuid, WechatOperationType.QR);
		return stream;
	}

	@Override
	public boolean login(Account account) {
		return get(account, WechatOperationType.LOGIN);
	}

	@Override
	public boolean webWxInit(Account account) {
		return get(account, WechatOperationType.INIT);
	}

	@Override
	public void wxStatusNotify(Account account) {
		post(account, WechatOperationType.STATUS_NOTIFY);
	}

	@Override
	public void webWxGetContact(Account account) {
		post(account, WechatOperationType.GET_CONTACT);
	}

	@Override
	public void WebWxBatchGetContact(Account account) {
		post(account, WechatOperationType.BATCH_GET_CONTACT);
	}

	@Override
	public JSONObject sync(Account account) {
		return post(account, WechatOperationType.SYNC);
	}

	@Override
	public Map<String, String> syncStatus(Account account) {
		return get(account, WechatOperationType.SYNC_STATUS);
	}

}
