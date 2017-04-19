package cn.zhouyafeng.itchat4j;

import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

/**
 * 消息处理类
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月20日 上午12:19:52
 * @version 1.0
 *
 */
public class MsgHandler implements IMsgHandlerFace {

	public String textMsgHandle(JSONObject msg) {
		String text = msg.getString("Text");
		return text;
	}

}
