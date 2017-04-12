package cn.zhouyafeng.itchat4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map.Entry;

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
			Long InviteStartCount = obj.getLong("InviteStartCount");
			// System.out.println(InviteStartCount);
			// System.out.println(obj.get("User"));
			Iterator<Entry<String, Object>> it = ((JSONObject) obj.get("User")).entrySet().iterator();
			while (it.hasNext()) {
				System.out.println(it.next().getKey());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
