package com.yunos.tv.core.util;


import com.yunos.tv.core.config.AppInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * 加密工具类<br>
 * 1.取得字符串对应的UTF-8的byte数组，如 data.getBytes("UTF-8")<br>
 * 2.md5函数对1步聚产生的byte数组进行运算，取得散列后的新byte数组<br>
 * 3.hex函数对2步骤的结果进行变换，生成16进制char[]数组，全部为小写
 * @author tianxiang
 * @date 2012-10-8 下午5:09:53
 */
public class DataEncoder {

    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    /**
     * 对二进制数组进行MD5算法加密,并将加密结果按照建行的协议算法进行转换
     * @param source
     * @return
     */
    public static byte[] encode2MD5(byte[] source) {
        byte[] result = new byte[0];

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);

            // MD5 的计算结果是一个 128 位的长整数，用字节表示就是 16 个字节
            result = md.digest();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 将字节数组转为16进制字符<br>
     * 实现细节:<br>
     * 1.每个byte转成2个char字符<br>
     * 2.先将byte与0xF0做与操作，得到高四位，右移四位，高位补0，得到高四位的字符<br>
     * 3.再将byte与0x0F做与操作,得到低四位
     * @author tianxiang
     * @date 2012-10-8上午10:56:10
     */
    private static char[] encode2Hex(byte[] data) {
        int len = data.length;
        char[] out = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return out;
    }

    public static String encode(String source) {
        if (source == null) {
            return null;
        }

        byte[] bytes = null;
        try {
            bytes = source.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        if (bytes == null) {
            return null;
        }

        return String.valueOf(encode2Hex(encode2MD5(bytes)));
    }

    public static String encodeGBK(String source) {
        if (source == null) {
            return null;
        }

        byte[] bytes = null;
        try {
            bytes = source.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        return String.valueOf(encode2Hex(encode2MD5(bytes)));
    }

    /**
     * md5的加密
     * @param bytes
     * @return
     */
    public static String md5Hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        return String.valueOf(encode2Hex(encode2MD5(bytes)));
    }

    /**
     * url编码
     * @author tianxiang
     * @date 2012-10-9 19:51:18
     */
    public static String encodeUrl(String str) {
        return urlEncode(str, AppInfo.HTTP_PARAMS_ENCODING);
    }

    /**
     * url解码
     * @author tianxiang
     * @date 2012-10-9 19:51:18
     */
    public static String decodeUrl(String str) {
        if (str == null) {
            return null;
        }
        String tempStr = null;

        try {
            tempStr = URLDecoder.decode(str, AppInfo.HTTP_PARAMS_ENCODING);
        } catch (Exception e1) {
            tempStr = null;
        }

        if (tempStr != null) {
            return tempStr;
        } else {
            return str;
        }
    }
    
    
    /**
     * 带编码的url ecode
     */
    public static String urlEncode(String str,  String charsetName) {
        if (str == null) {
            return null;
        }
        String tempStr = null;

        try {
            tempStr = URLEncoder.encode(str, charsetName);
        } catch (Exception e1) {
            tempStr = null;
        }

        if (tempStr != null) {
            return tempStr;
        } else {
            return str;
        }
    }

}