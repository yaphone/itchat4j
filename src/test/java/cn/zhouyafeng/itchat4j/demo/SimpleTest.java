package cn.zhouyafeng.itchat4j.demo;

import java.util.Date;
import java.util.Random;

public class SimpleTest {
	public static void main(String[] args) {
		// String filePath = "D:/itchat4j/pic/test.jpg";
		// File f = new File(filePath);
		// System.out.println(f.length());
		// print str(int(time.time() * 1000)) + \
		// str(random.random())[:5].replace('.', '')
		String tmp = String.valueOf(new Date().getTime()) + String.valueOf(new Random().nextLong()).substring(0, 4);
		System.out.println(tmp.length());

	}
}
