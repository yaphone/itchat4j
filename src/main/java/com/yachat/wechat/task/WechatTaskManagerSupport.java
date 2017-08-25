package com.yachat.wechat.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yachat.wechat.Wechat;
import com.yachat.wechat.WechatTaskManager;

public class WechatTaskManagerSupport implements WechatTaskManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(WechatTaskManagerSupport.class);
	private ScheduledExecutorService scheduler;
	private ExecutorService statusWorker;
	private ExecutorService messageWorker;
	private Map<String, Wechat> wechats;

	public WechatTaskManagerSupport(ScheduledExecutorService scheduler, ExecutorService statusWorker,
			ExecutorService messageWorker) {
		super();
		this.scheduler = scheduler;
		this.statusWorker = statusWorker;
		this.messageWorker = messageWorker;
		this.wechats = new ConcurrentHashMap<>();
	}

	@Override
	public void addWechat(Wechat wechat) {
		this.wechats.put(wechat.getAccount().getUuid(), wechat);
	}

	@Override
	public void removeWechat(Wechat wechat) {
		if (this.wechats.containsKey(wechat.getAccount().getUuid())) {
			this.wechats.remove(wechat.getAccount().getUuid());
		}
	}

	@Override
	public void start() {
		this.scheduler.scheduleAtFixedRate(() -> {
			this.checkWechatOnlineStatus();
			this.checkWechatMessages();
		}, 100, 100, TimeUnit.MILLISECONDS);
	}

	private void checkWechatOnlineStatus() {
		for (Wechat wechat : this.wechats.values()) {
			this.statusWorker.submit(() -> {
				if (System.currentTimeMillis() - wechat.getAccount().getLastNormalRetcodeTime() > 60 * 1000) { // 超过60秒，判为离线
					wechat.offline();
					LOGGER.info("微信已离线");
				}
			});
		}
	}

	private void checkWechatMessages() {
		for (Wechat wechat : this.wechats.values()) {
			if (wechat.isOnline()) {
				this.messageWorker.submit(() -> {
					wechat.receivingMessage();
				});
			}
		}
	}

	@Override
	public void stop() {
		this.scheduler.shutdown();
		this.statusWorker.shutdown();
		this.messageWorker.shutdown();
	}

}
