package com.yachat.wechat;

import java.io.InputStream;

public interface WechatSystem {
	
	/**
	 * 获取UUID
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:21:40
	 * @param qrPath
	 * @return
	 */
	String getUuid();

	/**
	 * 获取二维码图片
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:13:51
	 * @param qrPath
	 * @return
	 */
	InputStream getQR(String uuid);

	/**
	 * 登陆
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:07
	 * @return
	 */
	boolean login(Account account);


	/**
	 * web初始化
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:13
	 * @return
	 */
	boolean webWxInit(Account account);

	/**
	 * 微信状态通知
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:24
	 */
	void wxStatusNotify(Account account);

	/**
	 * 接收消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:37
	 */
	void startReceiving(Account account);

	/**
	 * 获取微信联系人
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 下午2:26:18
	 */
	void webWxGetContact(Account account);

	/**
	 * 批量获取联系人信息
	 * 
	 * @date 2017年6月22日 下午11:24:35
	 */
	void WebWxBatchGetContact(Account account);

}
