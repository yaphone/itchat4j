package cn.zhouyafeng.itchat4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;

public class AnotherSimpleTest {
	public static void main(String[] args) {
		String path = "D:\\itchat.txt";
		File file = new File(path);
		Reader r;
		try {
			r = new FileReader(file);
			String text = IOUtils.readAll(r);
			JSONObject obj = JSON.parseObject(text);
			System.out.println(((JSONObject) obj.get("User")).getString("NickName"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
