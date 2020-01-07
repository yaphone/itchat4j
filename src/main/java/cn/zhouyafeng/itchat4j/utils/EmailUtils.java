package cn.zhouyafeng.itchat4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * 发送Email工具类
 * </p>
 *
 * @author zhu.yuancheng
 * @create 2020-01-03 14:26
 * @package com.ustcinfo.mail.utils
 */
public class EmailUtils {
    private static Logger LOG = LoggerFactory.getLogger(EmailUtils.class);
    private EmailConfig config;

    private static final class MailAuthenticator extends Authenticator {
        private String user;
        private String pwd;

        public MailAuthenticator(String user, String pwd) {
            this.user = user;
            this.pwd = pwd;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pwd);
        }
    }

    public static final class EmailConfig {
        private String smtpHost; // 邮件服务器地址
        private String sendUserName; // 发件人的用户名
        private String sendUserPass; // 发件人密码

        public EmailConfig(String smtpHost, String sendUserName, String sendUserPass) {
            this.smtpHost = smtpHost;
            this.sendUserName = sendUserName;
            this.sendUserPass = sendUserPass;
        }
    }

    public static final class Email {
        private String to; // 接收人
        private String[] cc; // 抄送人
        private String body; // 邮件正文
        private String subject; // 邮件标题
        private List<String> attachments; // 附件：文件地址

        public Email setTo(String to) {
            this.to = to;
            return this;
        }

        public Email setCc(String[] cc) {
            this.cc = cc;
            return this;
        }

        public Email setBody(String body) {
            this.body = body;
            return this;
        }

        public Email setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Email setAttachments(List<String> attachments) {
            this.attachments = attachments;
            return this;
        }
    }

    private MimeMessage mimeMsg; // 邮件对象
    private Session session;
    private Properties props;
    private Multipart mp;// 附件添加的组件

    /**
     * 初始化邮件配置
     *
     * @param config
     */
    public void init(EmailConfig config) {
        this.config = config;
        if (props == null) {
            props = System.getProperties();
        }
        props.put("mail.smtp.host", config.smtpHost);
        props.put("mail.smtp.auth", "true"); // 需要身份验证
        session = Session.getDefaultInstance(props, new MailAuthenticator(config.sendUserName, config.sendUserPass));
        // 置true可以在控制台（console)上看到发送邮件的过程
        session.setDebug(false);
        // 用session对象来创建并初始化邮件对象
        mimeMsg = new MimeMessage(session);
    }

    /**
     * 设置邮件内容
     *
     * @param email
     * @return
     */
    public boolean setMail(Email email) {
        if (null == config) {
            LOG.warn("邮件内容设置失败，未找到邮件配置");
            return false;
        }
        try {
            // 添加发送人
            mimeMsg.setFrom(new InternetAddress(config.sendUserName));

            // 添加接收人
            if (null != email.to) {
                mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.to));
            }

            // 添加抄送人
            if (null != email.cc && email.cc.length > 0) {
                mimeMsg.setRecipients(Message.RecipientType.CC, toInternetAddressList(email.cc));
            }

            // 生成附件组件的实例
            mp = new MimeMultipart();

            // 在组件上添加邮件文本
            BodyPart bp = new MimeBodyPart();
            bp.setContent("<meta http-equiv=Content-Type content=text/html; charset=UTF-8>" + email.body, "text/html;charset=UTF-8");
            mp.addBodyPart(bp);

            // 添加邮件标题
            mimeMsg.setSubject(email.subject);

            if (null != email.attachments) {
                for (String attachment : email.attachments) {
                    if (attachment != null && attachment.length() > 0) {
                        BodyPart attachment_bp = new MimeBodyPart();
                        FileDataSource filed = new FileDataSource(attachment);
                        attachment_bp.setDataHandler(new DataHandler(filed));
                        attachment_bp.setFileName(MimeUtility.encodeText(filed.getName(), "utf-8", null)); // 解决附件名称乱码
                        mp.addBodyPart(attachment_bp);// 添加附件
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送邮件
     *
     * @return
     * @throws Exception
     */
    public boolean send() throws Exception {
        if (null == config) {
            LOG.warn("邮件发送失败，未找到邮件配置");
            return false;
        }
        mimeMsg.setContent(mp);
        mimeMsg.saveChanges();
        LOG.info("正在发送邮件....");
        Transport transport = session.getTransport("smtp");
        // 连接邮件服务器并进行身份验证
        transport.connect(config.smtpHost, config.sendUserName, config.sendUserPass);
        // 发送邮件
        transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
        LOG.info("发送邮件成功！");
        transport.close();
        return true;
    }

    private Address[] toInternetAddressList(String[] cc) throws AddressException {
        Address[] addresses = new Address[cc.length];
        for (int i = 0; i < cc.length; i++) {
            addresses[i] = new InternetAddress(cc[i]);
        }
        return addresses;
    }
}
