package com.yachat.wechat.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.TryRetryClient;

import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.LoginParaEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class LoginHandler extends AbstractAccountHandler<Boolean> {

	private TryRetryClient retryClient;

	public LoginHandler(TryRetryClient retryClient) {
		this.retryClient = retryClient;
	}

	@Override
	public Request createRequest(Account account) {
		long millis = System.currentTimeMillis();
		return new Request(URLEnum.LOGIN_URL.getUrl()).enableRedirect()
				.add(LoginParaEnum.LOGIN_ICON.para(), LoginParaEnum.LOGIN_ICON.value())
				.add(LoginParaEnum.UUID.para(), account.getUuid())
				.add(LoginParaEnum.TIP.para(), LoginParaEnum.TIP.value())
				.add(LoginParaEnum.R.para(), String.valueOf(millis / 1579L))
				.add(LoginParaEnum._.para(), String.valueOf(millis));
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
	public Response<Boolean> createResponse(HttpEntity entity, Account account) throws Exception {
		String result = EntityUtils.toString(entity);
		String status = checklogin(result);
		if (ResultEnum.SUCCESS.getCode().equals(status)) {
			processLoginInfo(result, account); // 处理结果
			account.setAlive(true);
			return Response.success(true);
		}
		if (ResultEnum.WAIT_CONFIRM.getCode().equals(status)) {
			LOGGER.info("请点击微信确认按钮，进行登陆");
		}
		return Response.error(false);
	}

	/**
	 * 检查登陆状态
	 *
	 * @param result
	 * @return
	 */
	private String checklogin(String result) {
		String regEx = "window.code=(\\d+)";
		Matcher matcher = CommonTools.getMatcher(regEx, result);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	/**
	 * 处理登陆信息
	 *
	 * @author https://github.com/yaphone
	 * @date 2017年4月9日 下午12:16:26
	 * @param result
	 */
	private void processLoginInfo(String loginContent, Account account) {
		String regEx = "window.redirect_uri=\"(\\S+)\";";
		Matcher matcher = CommonTools.getMatcher(regEx, loginContent);
		if (matcher.find()) {
			String originalUrl = matcher.group(1);
			String url = originalUrl.substring(0, originalUrl.lastIndexOf('/')); // https://wx2.qq.com/cgi-bin/mmwebwx-bin
			account.getLoginInfo().put("url", url);
			Map<String, List<String>> possibleUrlMap = this.getPossibleUrlMap();
			for (Entry<String, List<String>> entry : possibleUrlMap.entrySet()) {
				String indexUrl = entry.getKey();
				String fileUrl = "https://" + entry.getValue().get(0) + "/cgi-bin/mmwebwx-bin";
				String syncUrl = "https://" + entry.getValue().get(1) + "/cgi-bin/mmwebwx-bin";
				if (account.getLoginInfo("url").toString().contains(indexUrl)) {
					account.setIndexUrl(indexUrl);
					account.setLoginInfo("fileUrl", fileUrl);
					account.setLoginInfo("syncUrl", syncUrl);
					break;
				}
			}
			if (account.getLoginInfo("fileUrl") == null && account.getLoginInfo("syncUrl") == null) {
				account.setLoginInfo("fileUrl", url);
				account.setLoginInfo("syncUrl", url);
			}
			account.setLoginInfo("deviceid", "e" + String.valueOf(new Random().nextLong()).substring(1, 16)); // 生成15位随机数
			account.setLoginInfo("BaseRequest", new ArrayList<String>());

			boolean isLogined = retryClient.get(new Request(url), (entity) -> {
				String text = getEntity(entity);
				// add by 默非默 2017-08-01 22:28:09
				// 如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
				String msg = getLoginMessage(text);
				if (!"".equals(msg)) {
					LOGGER.info(msg);
					return buildError(false);
				}
				Document doc = CommonTools.xmlParser(text);
				if (doc != null) {
					account.setLoginInfo(StorageLoginInfoEnum.skey.getKey(),
							doc.getElementsByTagName(StorageLoginInfoEnum.skey.getKey()).item(0).getFirstChild()
									.getNodeValue());
					account.setLoginInfo(StorageLoginInfoEnum.wxsid.getKey(),
							doc.getElementsByTagName(StorageLoginInfoEnum.wxsid.getKey()).item(0).getFirstChild()
									.getNodeValue());
					account.setLoginInfo(StorageLoginInfoEnum.wxuin.getKey(),
							doc.getElementsByTagName(StorageLoginInfoEnum.wxuin.getKey()).item(0).getFirstChild()
									.getNodeValue());
					account.setLoginInfo(StorageLoginInfoEnum.pass_ticket.getKey(),
							doc.getElementsByTagName(StorageLoginInfoEnum.pass_ticket.getKey()).item(0).getFirstChild()
									.getNodeValue());
				}
				return buildSuccess(true);
			});

			if (!isLogined) {
				throw new RuntimeException("Login ERROR .");
			}
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
