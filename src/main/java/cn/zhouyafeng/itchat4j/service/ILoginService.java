package cn.zhouyafeng.itchat4j.service;

/**
 * 登陆服务接口
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月13日 上午12:07:21
 * @version 1.0
 *
 */
public interface ILoginService {

	/**
	 * 登陆
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:07
	 * @return
	 */
	boolean login();

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
	 * @return
	 */
	boolean getQR();

	/**
	 * web初始化
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:13
	 * @return
	 */
	boolean webWxInit();

	/**
	 * 微信状态通知
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:24
	 */
	void wxStatusNotify();

	/**
	 * 接收消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 上午12:14:37
	 */
	void startReceiving();

	/**
	 * 获取微信联系人
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月13日 下午2:26:18
	 */
	void webWxGetContact();

	/**
	 * 批量获取联系人信息
	 * 
	 * @date 2017年6月22日 下午11:24:35
	 */
	void WebWxBatchGetContact();

}
