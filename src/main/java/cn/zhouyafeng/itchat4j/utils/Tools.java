package cn.zhouyafeng.itchat4j.utils;

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
}
