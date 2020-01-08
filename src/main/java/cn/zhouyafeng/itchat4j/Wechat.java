package cn.zhouyafeng.itchat4j;

import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.utils.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zhouyafeng.itchat4j.controller.LoginController;
import cn.zhouyafeng.itchat4j.core.MsgCenter;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

import java.util.List;

public class Wechat {
    private static final Logger LOG = LoggerFactory.getLogger(Wechat.class);
    private IMsgHandlerFace msgHandler;

    private Core core = Core.getInstance();

    public Wechat(IMsgHandlerFace msgHandler, String qrPath) {
        System.setProperty("jsse.enableSNIExtension", "false"); // 防止SSL错误
        this.msgHandler = msgHandler;
        core.setQrPath(qrPath);

        // 登陆
        LoginController login = new LoginController();
        login.login(qrPath);
    }

    public void start() {
        LOG.info("+++++++++++++++++++开始消息处理+++++++++++++++++++++");
        new Thread(() -> MsgCenter.handleMsg(msgHandler)).start();
    }

    /**
     * 设置离线发送邮件提醒
     *
     * @param config
     * @param emails
     */
    public void setLoginOutSendEmail(EmailUtils.EmailConfig config, EmailUtils.Email... emails) {
        EmailUtils emailUtils = new EmailUtils();
        emailUtils.init(config);
        core.setLoginOutSendEmail(emailUtils, emails);
    }
}
