package com.yunos.tvtaobao.live.tvtaomsg.utility;

/**
 * 字节工具
 * Created by zhiping on 16/12/21.
 */
public class ByteUtil {

    //字节转十六进制
    public static String hexStr( byte[] b) {
        StringBuilder subf = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            subf.append(hex.toUpperCase());
        }

        return subf.toString();
    }
}
