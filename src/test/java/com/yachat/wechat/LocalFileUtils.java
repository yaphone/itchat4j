package com.yachat.wechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class LocalFileUtils {

	public static void open(String filePath, Account account) throws IOException {
		String qrPath = filePath + File.separator + "QR.jpg"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		OutputStream outputStream = new FileOutputStream(qrPath);
		IOUtils.copy(account.getQrStream(), outputStream);
		IOUtils.closeQuietly(outputStream);
//		CommonTools.printQr(qrPath); // 打开登陆二维码图片
		printQr(qrPath);
	}
	
	
	public static boolean printQr(String qrPath) {

		switch (getOsNameEnum()) {
		case WINDOWS:
			if (getOsNameEnum().equals(OsNameEnum.WINDOWS)) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("cmd /c start " + qrPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case MAC:
			if (getOsNameEnum().equals(OsNameEnum.MAC)) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("open " + qrPath);
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
	 * 获取系统平台
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月8日 下午10:27:53
	 */
	public static OsNameEnum getOsNameEnum() {
		String os = System.getProperty("os.name").toUpperCase();
		if (os.indexOf(OsNameEnum.DARWIN.toString()) >= 0) {
			return OsNameEnum.DARWIN;
		} else if (os.indexOf(OsNameEnum.WINDOWS.toString()) >= 0) {
			return OsNameEnum.WINDOWS;
		} else if (os.indexOf(OsNameEnum.LINUX.toString()) >= 0) {
			return OsNameEnum.LINUX;
		} else if (os.indexOf(OsNameEnum.MAC.toString()) >= 0) {
			return OsNameEnum.MAC;
		}
		return OsNameEnum.OTHER;
	}
	
	public enum OsNameEnum {
		WINDOWS, LINUX, DARWIN, MAC, OTHER
	}


}
