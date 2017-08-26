package com.yachat.wechat.support.handlers;

import static com.yachat.wechat.keys.WechatKeys.BaseRequest;
import static com.yachat.wechat.keys.WechatKeys.deviceid;
import static com.yachat.wechat.keys.WechatKeys.loginicon;
import static com.yachat.wechat.keys.WechatKeys.pass_ticket;
import static com.yachat.wechat.keys.WechatKeys.r;
import static com.yachat.wechat.keys.WechatKeys.skey;
import static com.yachat.wechat.keys.WechatKeys.tip;
import static com.yachat.wechat.keys.WechatKeys.underline;
import static com.yachat.wechat.keys.WechatKeys.uuid;
import static com.yachat.wechat.keys.WechatKeys.wxsid;
import static com.yachat.wechat.keys.WechatKeys.wxuin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.TryRetryClient;
import com.yachat.wechat.keys.Builders;
import com.yachat.wechat.keys.PatternKeys;
import com.yachat.wechat.keys.StatusKeys;
import com.yachat.wechat.keys.UrlKeys;
import com.yachat.wechat.keys.WechatKeys;
import com.yachat.wechat.utils.DomUtils;

public class LoginHandler extends AbstractAccountHandler<Boolean> {

	private TryRetryClient retryClient;

	public LoginHandler(TryRetryClient retryClient) {
		this.retryClient = retryClient;
	}

	@Override
	public Request createRequest(Account account) {
		long millis = System.currentTimeMillis();
		return Builders.of(UrlKeys.LOGIN_URL)
				.addAll(loginicon, tip)
				.add(uuid, account.getUuid())
				.add(r, String.valueOf(millis / 1579L))
				.add(underline, String.valueOf(millis)).build();
	}

	@Override
	public int retryTimes() {
		return 60;
	}

	@Override
	public long retryTimeoutMillis() {
		return 1000;
	}

	@Override
	public Response<Boolean> createResponse(String text, Account account) throws Exception {
		String status = PatternKeys.CheckLogin.match1(text); // 检查登陆状态
		if (StatusKeys.SUCCESS.is(status)) {
			String originalUrl = PatternKeys.ProcessLoginInfo.match1(text);
			if (StringUtils.isNotBlank(originalUrl)) {
				processLoginInfo(originalUrl, account); // 处理结果
			}
			account.setAlive(true);
			return Response.success(true);
		}
		if (StatusKeys.WAIT_CONFIRM.is(status)) {
			LOGGER.info("请点击微信确认按钮，进行登陆");
		}
		return Response.error(false);
	}

	/**
	 * 处理登陆信息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	private void processLoginInfo(String originalUrl, Account account) {
		String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
		account.getLoginInfo().put("url", url);
		Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
		for (Entry<String, List<String>> entry : possibleUrlMap.entrySet()) {
			String indexUrl = entry.getKey();
			String fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
			String syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";	
			if( WechatKeys.url.get(account).contains(indexUrl) ) {
				account.setIndexUrl(indexUrl);
				WechatKeys.fileUrl.set(account, fileUrl);
				WechatKeys.syncUrl.set(account, syncUrl);;
				break;
			}
		}
		if (WechatKeys.fileUrl.get(account) == null &&  WechatKeys.syncUrl.get(account)  == null) {
			WechatKeys.fileUrl.set(account, url);
			WechatKeys.syncUrl.set(account, url);;
		}
		deviceid.set(account, "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
		BaseRequest.set(account, new ArrayList<String>());
		
		boolean isLogined = retryClient.get2(new Request(originalUrl), (entity) -> {
			String text = getEntity(entity);
			// add by 默非默 2017-08-01 22:28:09
			// 如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
			String msg = getLoginMessage(text);
			if (!"".equals(msg)) {
				LOGGER.info(msg);
				return buildError(false);
			}
			Document doc = DomUtils.toDoc(text);
			if (doc != null) {			
				skey.setDoc(account, doc);
				wxsid.setDoc(account, doc);
				wxuin.setDoc(account, doc);
				pass_ticket.setDoc(account, doc);
			}
			return buildSuccess(true);
		});

		if (!isLogined) {
			throw new RuntimeException("Login ERROR .");
		}
	}

	/**
	 * 解析登录返回的消息，如果成功登录，则message为空
	 * 
	 * @param result
	 * @return
	 */
	private String getLoginMessage(String result) {
		String[] strArr = result.split("<message>");
		String[] rs = strArr[1].split("</message>");
		if (rs != null && rs.length > 1) {
			return rs[0];
		}
		return "";
	}

	private Map<String, List<String>> getPossibleUrlMap() {
		Map<String, List<String>> possibleUrlMap = new HashMap<String, List<String>>();
		possibleUrlMap.put("wx.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx.qq.com");
				add("webpush.wx.qq.com");
			}
		});

		possibleUrlMap.put("wx2.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx2.qq.com");
				add("webpush.wx2.qq.com");
			}
		});
		possibleUrlMap.put("wx8.qq.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.wx8.qq.com");
				add("webpush.wx8.qq.com");
			}
		});

		possibleUrlMap.put("web2.wechat.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.web2.wechat.com");
				add("webpush.web2.wechat.com");
			}
		});
		possibleUrlMap.put("wechat.com", new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("file.web.wechat.com");
				add("webpush.web.wechat.com");
			}
		});
		return possibleUrlMap;
	}

}
