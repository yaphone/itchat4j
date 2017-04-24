package cn.zhouyafeng.itchat4j.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;

/**
 * 图灵机器人示例
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月24日 上午12:13:26
 * @version 1.0
 *
 */
public class TulingRobot implements IMsgHandlerFace {

	MyHttpClient myHttpClient = new MyHttpClient();
	String apiKey = "597b34bea4ec4c85a775c469c84b6817";
	Logger logger = Logger.getLogger("TulingRobot");

	public String textMsgHandle(JSONObject msg) {
		String result = "";
		String text = msg.getString("Text");
		String url = "http://www.tuling123.com/openapi/api";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("key", apiKey);
		paramMap.put("info", text);
		paramMap.put("userid", "123456");
		String paramStr = JSON.toJSONString(paramMap);
		try {
			HttpEntity entity = myHttpClient.doPost(url, paramStr);
			result = EntityUtils.toString(entity, "UTF-8");
			JSONObject obj = JSON.parseObject(result);
			if (obj.getString("code").equals("100000")) {
				result = obj.getString("text");
			} else {
				result = "处理有误";
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return result;
	}

	public String picMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	public String voiceMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	public String viedoMsgHandle(JSONObject msg) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		IMsgHandlerFace msgHandler = new TulingRobot();
		Wechat wechat = new Wechat(msgHandler);
		wechat.start();
	}

}
