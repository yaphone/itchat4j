package cn.zhouyafeng.itchat4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import cn.zhouyafeng.itchat4j.utils.Config;

public class AnotherSimpleTest {
	public static void main(String[] args) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = "http://127.0.0.1:8080/blog/page/all";
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-type", "application/json; charset=utf-8");
		request.setHeader("User-Agent", Config.USER_AGENT);
		try {
			HttpResponse response = httpClient.execute(request);
			System.out.println("*****************************************");
			System.out.println(EntityUtils.toString(response.getEntity()));
			System.out.println("*****************************************");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
	}
}
