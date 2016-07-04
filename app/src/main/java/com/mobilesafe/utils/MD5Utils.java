package com.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/7/4.
 */
public class MD5Utils {
    /**
     * MD5加密方法
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5Password(String password) {
        // 获取到一个信息摘要器
        MessageDigest digest = null;
        StringBuffer buffer = new StringBuffer();
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(password.getBytes());
            // 把每一个byte 做一个与运算 0xff
            for(byte b:result){
                int num = b&0xff;
                String str = Integer.toHexString(num);
                if(str.length() == 1){
                    buffer.append("0");
                }
                buffer.append(str);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
