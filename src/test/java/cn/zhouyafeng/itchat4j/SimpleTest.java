package cn.zhouyafeng.itchat4j;

public class SimpleTest {
	public static void main(String[] args) {
		Wechat wechat = new Wechat();
		System.out.println(wechat.getQRuuid());
		wechat.getQR();
		// HttpClient httpClient = new HttpClient();
		// String result = httpClient.doGet("https://www.baidu.com", null);
		// System.out.println(result);
	}

}
