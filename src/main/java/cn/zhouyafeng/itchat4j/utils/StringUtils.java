package cn.zhouyafeng.itchat4j.utils;

/**
 * Created by loneyfall on 2019/5/16.
 */
public class StringUtils {

    /**
     * 判断是否为空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断字符串是否非空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
