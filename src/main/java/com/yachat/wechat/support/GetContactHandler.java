package com.yachat.wechat.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yachat.wechat.Account;
import com.yachat.wechat.http.Request;
import com.yachat.wechat.http.Response;
import com.yachat.wechat.http.RetryHttpClient;
import com.yachat.wechat.http.TryRetryClient;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

public class GetContactHandler extends AbstractAccountHandler<Void> {
	
	private TryRetryClient retryClient ;
	
	

	public GetContactHandler(TryRetryClient retryClient) {
		this.retryClient = retryClient;
	}

	@Override
	public Request createRequest(Account account) {
		String url = String.format(URLEnum.WEB_WX_GET_CONTACT.getUrl() , account.getLoginInfo(StorageLoginInfoEnum.url.getKey()));
		return new Request(url).addAll(account.getParamMap());
	}

	@Override
	public Response<Void> createResponse(HttpEntity entity, Account account) throws Exception {
		String result = EntityUtils.toString(entity, Consts.UTF_8);
		JSONObject fullFriendsJsonList = JSON.parseObject(result);
		// 查看seq是否为0，0表示好友列表已全部获取完毕，若大于0，则表示好友列表未获取完毕，当前的字节数（断点续传）
		long seq = 0;
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (fullFriendsJsonList.get("Seq") != null) {
			seq = fullFriendsJsonList.getLong("Seq");
		}
		account.setMemberCount(fullFriendsJsonList.getInteger(StorageLoginInfoEnum.MemberCount.getKey()));
		JSONArray member = fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey());
		// 循环获取seq直到为0，即获取全部好友列表 ==0：好友获取完毕 >0：好友未获取完毕，此时seq为已获取的字节数
		while (seq > 0) {
			// 设置seq传参
			Request request = this.createRequest(account);
			request.add("r", String.valueOf(System.currentTimeMillis()));
			request.add("seq", String.valueOf(seq));
			
			fullFriendsJsonList = retryClient.get(request , (entity2)-> {
				String text2 = getEntity(entity2);
				JSONObject fullFriendsJsonList2 = JSON.parseObject(text2);
				return Response.success(fullFriendsJsonList2);
			});
			if (fullFriendsJsonList.get("Seq") != null) {
				seq = fullFriendsJsonList.getLong("Seq");
			}
			// 累加好友列表
			member.addAll(fullFriendsJsonList.getJSONArray(StorageLoginInfoEnum.MemberList.getKey()));
		}
		account.setMemberCount(member.size());
		for (Iterator<?> iterator = member.iterator(); iterator.hasNext();) {
			JSONObject o = (JSONObject) iterator.next();
			if ((o.getInteger("VerifyFlag") & 8) != 0) { // 公众号/服务号
				account.getPublicUsersList().add(o);
			} else if (Config.API_SPECIAL_USER.contains(o.getString("UserName"))) { // 特殊账号
				account.getSpecialUsersList().add(o);
			} else if (o.getString("UserName").indexOf("@@") != -1) { // 群聊
				if (!account.getGroupIdList().contains(o.getString("UserName"))) {
					account.getGroupNickNameList().add(o.getString("NickName"));
					account.getGroupIdList().add(o.getString("UserName"));
					account.getGroupList().add(o);
				}
			} else if (o.getString("UserName").equals(account.getUserSelf().getString("UserName"))) { // 自己
				account.getContactList().remove(o);
			} else { // 普通联系人
				account.getContactList().add(o);
			}
		}
		return this.buildSuccess();
	}

}
