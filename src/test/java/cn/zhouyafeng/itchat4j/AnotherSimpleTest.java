package cn.zhouyafeng.itchat4j;

import com.vdurmont.emoji.EmojiParser;

public class AnotherSimpleTest {
	public static void main(String[] args) throws Exception {
		// String str = "Here is a boy: \\u0001f494!";
		// String result = EmojiParser.parseToUnicode(str);
		// System.out.println(result);
		String str = "&#x1f494;&#x1f447;&#x1f434;&#x1f60f;&#x1f338;&#x1f451;&#x1f60c;&#x1f61d;&#x2764;&#x263a;&#x1f63a;&#x1f61c;&#x1f61a;&#x1f3c3;&#x1f64d;&#x1f62b;&#x1f525;&#x1f639;&#x1f633;&#x1f44d;&#x1f60a;&#x1f604;&#x1f631;&#x23f0;&#x1f63c;&#x1f63b;&#x1f444;&#x1f613;&#x1f60c;&#x1f62d;&#x1f435;&#x1f436;&#x1f63f;&#x1f63d;&#x1f47c;&#x1f4ab;";
		String result = EmojiParser.parseToUnicode(str);
		System.out.println(result);

	}

	public static String leftPad(String str, char c, int n) { // 左填充
		String result = str;
		if (str.length() < n) {
			StringBuilder sb = new StringBuilder(str);
			for (int i = 0; i < n - str.length(); i++) {
				sb.insert(0, c);
			}
			result = sb.toString();
		}
		return result;
	}

	public static String bytes2HexString(byte[] b) {
		String r = "";

		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			r += hex.toUpperCase();
		}

		return r;
	}

	/*
	 * 字符串转字节数组
	 */
	public static byte[] string2Bytes(String s) {
		byte[] r = s.getBytes();
		return r;
	}
}