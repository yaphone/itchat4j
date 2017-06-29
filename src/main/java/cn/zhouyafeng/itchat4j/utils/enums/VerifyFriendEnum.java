package cn.zhouyafeng.itchat4j.utils.enums;

/**
 * 确认添加好友Enum
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年6月29日 下午9:47:14
 * @version 1.0
 *
 */
public enum VerifyFriendEnum {

	ACCEPT(2, "接受"), ADD(3, "添加");

	private int code;
	private String desc;

	private VerifyFriendEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

}
