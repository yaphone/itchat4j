package cn.zhouyafeng.itchat4j.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 工具类
 * 
 * @author Email:zhouyaphone@163.com
 * @date 创建时间：2017年4月8日 下午10:59:55
 * @version 1.0
 *
 */
public class Tools {

	public static boolean printQr(String qrPath) {

		switch (Config.getOsName()) {
		case WINDOWS:
			if (Config.getOsName().equals(OsName.WINDOWS)) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("cmd /c start " + qrPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
		return true;
	}

	public static boolean clearScreen() {
		switch (Config.getOsName()) {
		case WINDOWS:
			if (Config.getOsName().equals(OsName.WINDOWS)) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("cmd /c " + "cls");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 正则表达式处理工具
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 上午12:27:10
	 * @return
	 */
	public static Matcher getMatcher(String regEx, String text) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(text);
		return matcher;
	}

	/**
	 * xml解析器
	 * 
	 * @author Email:zhouyaphone@163.com
	 * @date 2017年4月9日 下午6:24:25
	 * @param text
	 * @return
	 */
	public static Document xmlParser(String text) {
		Document doc = null;
		StringReader sr = new StringReader(text);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static JSONObject structFriendInfo(JSONObject userObj) {
		Map<String, Object> friendInfoTemplate = new HashMap<String, Object>();
		friendInfoTemplate.put("UserName", "");
		friendInfoTemplate.put("City", "");
		friendInfoTemplate.put("DisplayName", "");
		friendInfoTemplate.put("PYQuanPin", "");
		friendInfoTemplate.put("RemarkPYInitial", "");
		friendInfoTemplate.put("Province", "");
		friendInfoTemplate.put("KeyWord", "");
		friendInfoTemplate.put("RemarkName", "");
		friendInfoTemplate.put("PYInitial", "");
		friendInfoTemplate.put("EncryChatRoomId", "");
		friendInfoTemplate.put("Alias", "");
		friendInfoTemplate.put("Signature", "");
		friendInfoTemplate.put("NickName", "");
		friendInfoTemplate.put("RemarkPYQuanPin", "");
		friendInfoTemplate.put("HeadImgUrl", "");

		friendInfoTemplate.put("UniFriend", 0);
		friendInfoTemplate.put("Sex", 0);
		friendInfoTemplate.put("AppAccountFlag", 0);
		friendInfoTemplate.put("VerifyFlag", 0);
		friendInfoTemplate.put("ChatRoomId", 0);
		friendInfoTemplate.put("HideInputBarFlag", 0);
		friendInfoTemplate.put("AttrStatus", 0);
		friendInfoTemplate.put("SnsFlag", 0);
		friendInfoTemplate.put("MemberCount", 0);
		friendInfoTemplate.put("OwnerUin", 0);
		friendInfoTemplate.put("ContactFlag", 0);
		friendInfoTemplate.put("Uin", 0);
		friendInfoTemplate.put("StarFriend", 0);
		friendInfoTemplate.put("Statues", 0);

		friendInfoTemplate.put("MemberList", new ArrayList<Object>());

		JSONObject r = new JSONObject();
		Set<String> keySet = friendInfoTemplate.keySet();
		for (String key : keySet) {
			if (userObj.containsKey(key)) {
				r.put(key, userObj.get(key));
			} else {
				r.put(key, friendInfoTemplate.get(key));
			}
		}

		return r;
	}

	public static String getSynckey(JSONObject obj) {
		JSONArray obj2 = obj.getJSONArray("List");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < obj2.size(); i++) {
			JSONObject obj3 = (JSONObject) JSON.toJSON(obj2.get(i));
			sb.append(obj3.get("Val") + "|");
		}
		return sb.substring(0, sb.length() - 1); // 656159784|656159911|656159873|1491905341

	}

	public static JSONObject searchDictList(List<JSONObject> list, String key, String value) {
		JSONObject r = null;
		for (JSONObject i : list) {
			if (i.getString(key).equals(value)) {
				r = i;
				break;
			}
		}
		return r;
	}

}
