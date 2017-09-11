package com.yachat.wechat;

import com.yachat.wechat.message.Message;

/**
 * 消息处理接口
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月20日 上午12:13:49
 * @version 1.0
 *
 */
public interface MessageHandler {

	/**
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月20日 上午12:15:00
	 * @param msg
	 * @return
	 */
	public String text(Account account, Message message);

	/**
	 * 处理图片消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月21日 下午11:07:06
	 * @param msg
	 * @return
	 */
	public String picture(Account account, Message message);

	/**
	 * 处理声音消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月22日 上午12:09:44
	 * @param msg
	 * @return
	 */
	public String voice(Account account, Message message);

	/**
	 * 处理小视频消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午12:19:50
	 * @param msg
	 * @return
	 */
	public String video(Account account, Message message);

	/**
	 * 处理名片消息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月1日 上午12:50:50
	 * @param msg
	 * @return
	 */
	public String card(Account account, Message message);

	/**
	 * 处理系统消息
	 * 
	 * @author Relyn
	 * @date 2017年6月21日17:43:51
	 * @param msg
	 * @return
	 */
	public void sys(Account account, Message message);

	/**
	 * 处理确认添加好友消息
	 * 
	 * @date 2017年6月28日 下午10:15:30
	 * @param msg
	 * @return
	 */
	public String verifyAddFriend(Account account, Message message);

	/**
	 * 处理收到的文件消息
	 * 
	 * @date 2017年7月21日 下午11:59:14
	 * @param msg
	 * @return
	 */
	public String media(Account account, Message message);

}
