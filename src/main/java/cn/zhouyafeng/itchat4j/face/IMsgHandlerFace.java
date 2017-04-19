package cn.zhouyafeng.itchat4j.face;

import com.alibaba.fastjson.JSONObject;

/**
 * 消息处理接口
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月20日 上午12:13:49
 * @version 1.0
 *
 */
public interface IMsgHandlerFace {
	/**
	 * 处理文本信息
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月20日 上午12:15:00
	 */
	public String textMsgHandle(JSONObject msg);
}
