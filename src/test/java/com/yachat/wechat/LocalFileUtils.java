package com.yachat.wechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

public class LocalFileUtils {

	public static void open(String filePath, Account account) throws IOException {

		String qrPath = filePath + File.separator + "QR.jpg"; // 保存登陆二维码图片的路径，这里需要在本地新建目录
		OutputStream outputStream = new FileOutputStream(qrPath);
		IOUtils.copy(account.getQrStream(), outputStream);
		IOUtils.closeQuietly(outputStream);
		CommonTools.printQr(qrPath); // 打开登陆二维码图片
	}

}
