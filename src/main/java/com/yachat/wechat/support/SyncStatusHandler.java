package com.yachat.wechat.support;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;

import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;
import cn.zhouyafeng.itchat4j.utils.enums.parameters.BaseParaEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class SyncStatusHandler extends AbstractAccountHandler<Map<String, String>> {

	@Override
	public Request createRequest(Account account) {
		// 组装请求URL和参数
		String url = account.getLoginInfo(StorageLoginInfoEnum.syncUrl.getKey()) + URLEnum.SYNC_CHECK_URL.getUrl();
		Request request = new Request(url);
		for (BaseParaEnum baseRequest : BaseParaEnum.values()) {
			request.add(baseRequest.para().toLowerCase(), account.getLoginInfo(baseRequest.value().toString()));
		}
		request.add("r", String.valueOf(System.currentTimeMillis()));
		request.add("synckey", account.getLoginInfo("synckey"));
		request.add("_", String.valueOf(System.currentTimeMillis()));
		return request;
	}

	@Override
	public Response<Map<String, String>> createResponse(HttpEntity entity, Account account) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (entity == null) {
			resultMap.put("retcode", "9999");
			resultMap.put("selector", "9999");
			return Response.success(resultMap);
		}
		String text = EntityUtils.toString(entity);
		String regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}";
		Matcher matcher = CommonTools.getMatcher(regEx, text);
		if (!matcher.find() || matcher.group(1).equals("2")) {
			LOGGER.info(String.format("Unexpected sync check result: %s", text));
		} else {
			resultMap.put("retcode", matcher.group(1));
			resultMap.put("selector", matcher.group(2));
		}
		return Response.success(resultMap);
	}

}
