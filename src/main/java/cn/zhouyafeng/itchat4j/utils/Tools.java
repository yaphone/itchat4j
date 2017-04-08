package cn.zhouyafeng.itchat4j.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月8日 下午10:59:55
 * @version 1.0
 *
 */
public class Tools {

	public static boolean printQr(String qrPath) {

		switch (Config.getOsName()) {
		case WINDOWS:
			if (Config.getOsName().equals(OsName.WINDOWS)) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("cmd /c start " + qrPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 正则表达式处理工具
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 上午12:27:10
	 * @return
	 */
	public static Matcher getMatcher(String regEx, String result) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(result);
		return matcher;
	}
}
