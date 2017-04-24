package cn.zhouyafeng.itchat4j;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AnotherSimpleTest {
	public static void main(String[] args) {
		String dateString = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
		System.out.println(dateString);
	}

}