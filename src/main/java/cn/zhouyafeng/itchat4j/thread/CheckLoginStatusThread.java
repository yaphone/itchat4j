package cn.zhouyafeng.itchat4j.thread;

import cn.zhouyafeng.itchat4j.service.ILoginService;
import cn.zhouyafeng.itchat4j.service.impl.LoginServiceImpl;
import cn.zhouyafeng.itchat4j.utils.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.utils.SleepUtils;

/**
 * 检查微信在线状态
 * <p>
 * 如何来感知微信状态？
 * 微信会有心跳包，LoginServiceImpl.syncCheck()正常在线情况下返回的消息中retcode报文应该为"0"，心跳间隔一般在25秒，
 * 那么可以通过最后收到正常报文的时间来作为判断是否在线的依据。若报文间隔大于60秒，则认为已掉线。
 * </p>
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月17日 下午10:53:15
 */
public class CheckLoginStatusThread implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(CheckLoginStatusThread.class);
    private Core core = Core.getInstance();

    @Override
    public void run() {
        while (core.isAlive()) {
            long t1 = System.currentTimeMillis(); // 秒为单位
            if (t1 - core.getLastNormalRetcodeTime() > 60 * 1000) { // 超过60秒，判为离线
                core.setAlive(false);
                core.setLogin(false);
                // 清除历史状态
                core.setMemberCount(0);

                core.getMemberList().clear();
                core.getContactList().clear();
                core.getGroupList().clear();

                core.getGroupMemeberMap().clear();
                core.getPublicUsersList().clear();
                core.getSpecialUsersList().clear();

                core.getGroupIdList().clear();
                core.getGroupNickNameList().clear();

                core.getLoginInfo().clear();
                LOG.info("微信已离线");

                if (null != core.getEmailUtils() && null != core.getEmails()) {
                    EmailUtils emailUtils = core.getEmailUtils();
                    for (EmailUtils.Email email : core.getEmails()) {
                        boolean flag = emailUtils.setMail(email);
                        if (flag) {
                            try {
                                emailUtils.send();
                            } catch (Exception e) {
                                e.printStackTrace();
                                LOG.info("邮件发送失败");
                            }
                        }
                    }
                }
            }
            SleepUtils.sleep(10 * 1000); // 休眠10秒
        }
    }

}
