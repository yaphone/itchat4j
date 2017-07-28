package cn.zhouyafeng.itchat4j.utils;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRterminal {

	public static String getQr(String text) {
		String s = "生成二维码失败";
		int width = 40;
		int height = 40;
		// 用于设置QR二维码参数
		Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别——这里选择最高H级别
		qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, qrParam);
			s = toAscii(bitMatrix);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	public static String toAscii(BitMatrix bitMatrix) {
		StringBuilder sb = new StringBuilder();
		for (int rows = 0; rows < bitMatrix.getHeight(); rows++) {
			for (int cols = 0; cols < bitMatrix.getWidth(); cols++) {
				boolean x = bitMatrix.get(rows, cols);
				if (!x) {
					// white
					sb.append("\033[47m  \033[0m");
				} else {
					sb.append("\033[40m  \033[0m");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		String text = "https://github.com/zhangshanhai/java-qrcode-terminal ";

		System.out.println(getQr(text));
	}
}
