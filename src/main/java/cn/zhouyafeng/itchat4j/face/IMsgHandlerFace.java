package cn.zhouyafeng.itchat4j.face;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息处理接口
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月20日 上午12:13:49
 * @version 1.0
 *
 */
public interface IMsgHandlerFace {
	/**
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月20日 上午12:15:00
	 * @param msg
	 * @return
	 */
	public String textMsgHandle(JSONObject msg);

	/**
	 * 处理图片信息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月21日 下午11:07:06
	 * @param msg
	 * @return
	 */
	public String picMsgHandle(JSONObject msg);

	/**
	 * 处理声音信息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月22日 上午12:09:44
	 * @param msg
	 * @return
	 */
	public String voiceMsgHandle(JSONObject msg);

	/**
	 * 处理小视频信息
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午12:19:50
	 * @param msg
	 * @return
	 */
	public String viedoMsgHandle(JSONObject msg);

}
