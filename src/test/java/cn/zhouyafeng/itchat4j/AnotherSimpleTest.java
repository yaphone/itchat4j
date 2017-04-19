package cn.zhouyafeng.itchat4j;

import java.util.concurrent.TimeUnit;

public class AnotherSimpleTest {
	public static void main(String[] args) throws Exception {
		while (true) {
			int i = 0;
			if (i == 0) {
				System.out.println("NO");
				continue;
			} else {
				System.out.println("OK");
			}
			TimeUnit.SECONDS.sleep(2);
		}

	}
}