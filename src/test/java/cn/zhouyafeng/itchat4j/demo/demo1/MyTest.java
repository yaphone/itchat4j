package cn.zhouyafeng.itchat4j.demo.demo1;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.ResultEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;
import cn.zhouyafeng.itchat4j.utils.tools.QRCodeTools;
import java.util.regex.Matcher;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月28日 上午12:44:10
 */
public class MyTest {

	public static void main( String[] args ) {
		IMsgHandlerFace msgHandler = new SimpleDemo(); // 实现IMsgHandlerFace接口的类
		Wechat wechat = new Wechat(msgHandler); // 【注入】
		wechat.start(); // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
	}
}
