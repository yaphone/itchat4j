package cn.zhouyafeng.itchat4j;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.zhouyafeng.itchat4j.utils.Config;
import cn.zhouyafeng.itchat4j.utils.HttpClient;

public class Wechat {
	private boolean alive = false;
	private String uuid = null;
	private String baseUrl = Config.BASE_URL;
	private Wechat instance = null;
	private HttpClient httpClient = new HttpClient();
	private static Logger logger = Logger.getLogger("Wechat");

	Wechat() {

	}

	public int login() throws InterruptedException {
		if (alive) { // 已登陆
			logger.warning("itchat has already logged in.");
			return 0;
		}
		while (true) {
			for (int count = 0; count < 10; count++) {
				logger.info("Getting uuid of QR code.");
				while (getQRuuid() == null) {
					Thread.sleep(1);
				}
				logger.info("Downloading QR code.");

			}
		}
	}

	public String getQRuuid() {
		this.uuid = null;
		String uuidUrl = baseUrl + "/jslogin";
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", "wx782c26e4c19acffb");
		params.put("fun", "new");
		InputStream in = httpClient.doGet(uuidUrl, params);
		if (in != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String result = "";
			String current;
			try {
				while ((current = br.readLine()) != null) {
					result += current;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			String regEx = "window.QRLogin.code = (\\d+); window.QRLogin.uuid = (\\S+?);";
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				if ((matcher.group(1).equals("200"))) {
					String orgUuid = matcher.group(2); // "Aakzcf8mLQ=="
					uuid = orgUuid.substring(1, orgUuid.length() - 1); // 去掉双引号
				}
			}
		}
		return uuid;
	}

	public boolean getQR() {
		String qrUrl = baseUrl + "/qrcode/" + this.uuid;
		System.out.println(qrUrl);
		InputStream in = httpClient.doGet(qrUrl, null);
		OutputStream out = null;
		try {
			int bytesWritten = 0;
			int byteCount = 0;

			out = new FileOutputStream("D://QR.jpg");
			byte[] bytes = new byte[1024 * 40];
			while ((byteCount = in.read(bytes)) != -1) {
				out.write(bytes, bytesWritten, byteCount);
				bytesWritten += byteCount;

			}
			in.close();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}
