package cn.zhouyafeng.itchat4j.utils.enums;

/**
 * URL
 * Created by xiaoxiaomo on 2017/5/6.
 */
public enum URLEnum {



    BASE_URL("https://login.weixin.qq.com","基本的URL"),
    UUID_URL(BASE_URL.url+"/jslogin","UUIDLURL"),
    QRCODE_URL(BASE_URL.url+"/qrcode/","初始化URL"),
    STATUS_NOTIFY_URL(BASE_URL.url+"/webwxstatusnotify?lang=zh_CN&pass_ticket=%s","微信状态通知"),
    LOGIN_URL(BASE_URL.url+"/cgi-bin/mmwebwx-bin/login","登陆URL"),
    INIT_URL("%s/webwxinit?r=%s&pass_ticket=%s","初始化URL"),
    SYNC_CHECK_URL("/synccheck","检查心跳URL"),
    WEB_WX_SYNC_URL("%s/webwxsync?sid=%s&skey=%s&pass_ticket=%s","web微信消息同步URL"),
    WEB_WX_GET_CONTACT("%s/webwxgetcontact","web微信获取联系人信息URL"),
    WEB_WX_SEND_MSG("%s/webwxsendmsg","发送消息URL"),

    ;

    private String url;
    private String msg;

    URLEnum(String url, String msg) {
        this.url = url;
        this.msg = msg;
    }


    public String getUrl() {
        return url;
    }
}
