package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by https://github.com/kuangcp on 17-6-7  下午8:09
 * 为了测试jar读取外部文件
 */
public class ReadProperties {
    public static void main(String[]s){
        try {
            Properties properties = new Properties();
            File file = new File("something.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            System.out.println(properties.getProperty("v"));
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("有异常");
        }
    }

}
