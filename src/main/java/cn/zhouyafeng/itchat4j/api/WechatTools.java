package cn.zhouyafeng.itchat4j.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zhouyafeng.itchat4j.controller.LoginController;
import cn.zhouyafeng.itchat4j.service.ILoginService;
import cn.zhouyafeng.itchat4j.service.impl.LoginServiceImpl;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum;

/**
 * 微信小工具，如获好友列表等
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月4日 下午10:49:16
 */
public class WechatTools {
    private static Logger LOG = LoggerFactory.getLogger(WechatTools.class);

    private static Core core = Core.getInstance();

    private static ILoginService loginService = LoginServiceImpl.getInstance();

    /**
     * 根据用户名发送文本消息
     *
     * @param msg
     * @param toUserName
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午10:43:14
     */
    public static void sendMsgByUserName(String msg, String toUserName) {
        MessageTools.sendMsgById(msg, toUserName);
    }

    /**
     * <p>
     * 通过RealName获取本次UserName
     * </p>
     * <p>
     * 如NickName为"yaphone"，则获取UserName=
     * "@1212d3356aea8285e5bbe7b91229936bc183780a8ffa469f2d638bf0d2e4fc63"，
     * 可通过UserName发送消息
     * </p>
     *
     * @param
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午10:56:31
     */
    public static String getUserNameByNickName(String nickName) {
        for (JSONObject o : core.getContactList()) {
            if (o.getString("NickName").equals(nickName)) {
                return o.getString("UserName");
            }
        }
        return null;
    }

    /**
     * 返回好友昵称列表
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午11:37:20
     */
    public static List<String> getContactNickNameList() {
        List<String> contactNickNameList = new ArrayList<String>();
        for (JSONObject o : core.getContactList()) {
            contactNickNameList.add(o.getString("NickName"));
        }
        return contactNickNameList;
    }

    /**
     * 返回好友完整信息列表
     *
     * @return
     * @date 2017年6月26日 下午9:45:39
     */
    public static List<JSONObject> getContactList() {
        return core.getContactList();
    }

    /**
     * 返回群列表
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月5日 下午9:55:21
     */
    public static List<JSONObject> getGroupList() {
        return core.getGroupList();
    }

    /**
     * 获取群ID列表
     *
     * @return
     * @date 2017年6月21日 下午11:42:56
     */
    public static List<String> getGroupIdList() {
        return core.getGroupIdList();
    }

    /**
     * 获取群NickName列表
     *
     * @return
     * @date 2017年6月21日 下午11:43:38
     */
    public static List<String> getGroupNickNameList() {
        return core.getGroupNickNameList();
    }

    /**
     * 根据groupIdList返回群成员列表
     *
     * @param groupId
     * @return
     * @date 2017年6月13日 下午11:12:31
     */
    public static JSONArray getMemberListByGroupId(String groupId) {
        return core.getGroupMemeberMap().get(groupId);
    }

    /**
     * 退出微信
     *
     * @author https://github.com/yaphone
     * @date 2017年5月18日 下午11:56:54
     */
    public static void logout() {
        webWxLogout();
    }

    private static boolean webWxLogout() {
        String url = String.format(URLEnum.WEB_WX_LOGOUT.getUrl(),
                core.getLoginInfo().get(StorageLoginInfoEnum.url.getKey()));
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("redirect", "1"));
        params.add(new BasicNameValuePair("type", "1"));
        params.add(
                new BasicNameValuePair("skey", (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey())));
        try {
            HttpEntity entity = core.getMyHttpClient().doGet(url, params, false, null);
            String text = EntityUtils.toString(entity, Consts.UTF_8); // 无消息
            return true;
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        return false;
    }

    public static void setUserInfo() {
        for (JSONObject o : core.getContactList()) {
            core.getUserInfoMap().put(o.getString("NickName"), o);
            core.getUserInfoMap().put(o.getString("UserName"), o);
        }
    }

    /**
     * 根据用户昵称设置备注名称
     *
     * @param
     * @param remName
     * @date 2017年5月27日 上午12:21:40
     */
    public static void remarkNameByNickName(String nickName, String remName) {
        String url = String.format(URLEnum.WEB_WX_REMARKNAME.getUrl(), core.getLoginInfo().get("url"),
                core.getLoginInfo().get(StorageLoginInfoEnum.pass_ticket.getKey()));
        Map<String, Object> msgMap = new HashMap<String, Object>();
        Map<String, Object> msgMap_BaseRequest = new HashMap<String, Object>();
        msgMap.put("CmdId", 2);
        msgMap.put("RemarkName", remName);
        msgMap.put("UserName", core.getUserInfoMap().get(nickName).get("UserName"));
        msgMap_BaseRequest.put("Uin", core.getLoginInfo().get(StorageLoginInfoEnum.wxuin.getKey()));
        msgMap_BaseRequest.put("Sid", core.getLoginInfo().get(StorageLoginInfoEnum.wxsid.getKey()));
        msgMap_BaseRequest.put("Skey", core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey()));
        msgMap_BaseRequest.put("DeviceID", core.getLoginInfo().get(StorageLoginInfoEnum.deviceid.getKey()));
        msgMap.put("BaseRequest", msgMap_BaseRequest);
        try {
            String paramStr = JSON.toJSONString(msgMap);
            HttpEntity entity = core.getMyHttpClient().doPost(url, paramStr);
            // String result = EntityUtils.toString(entity, Consts.UTF_8);
            LOG.info("修改备注" + remName);
        } catch (Exception e) {
            LOG.error("remarkNameByUserName", e);
        }
    }

    /**
     * 获取微信在线状态
     *
     * @return
     * @date 2017年6月16日 上午12:47:46
     */
    public static boolean getWechatStatus() {
        return core.isAlive();
    }

    /**
     * 重新获取二维码登录
     *
     * @return
     */
    public static boolean reLogin() {
        if (core.isAlive() || core.isLogin()) {
            return false;
        } else {
            LOG.info("+++++++++++++++++++开始重新启动+++++++++++++++++++++");
            new Thread(() -> {
                loginService.reLogin();
                LoginController login = new LoginController();
                login.login(core.getQrPath());
            }).start();
            return true;
        }
    }
}
