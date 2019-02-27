package com.yunos.tvtaobao.biz.util;


import com.yunos.tv.core.config.Config;


import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {

    private static final String hashType = "MD5";

    private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f' };

    public static String getMD5(File file) throws Exception {
        return getHash(new FileInputStream(file));
    }

    private static String getHash(InputStream in) throws Exception {
        byte[] buffer = new byte[32 * 1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int numRead = 0;
        while ((numRead = in.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        in.close();
        return toHexString(md5.digest());
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        FileInputStream fileInputStream = null;

        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (FileNotFoundException e) {
            if (Config.isDebug()) {
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            if (Config.isDebug()) {
                e.printStackTrace();
            }
            return null;
        } catch (NoSuchAlgorithmException e) {
            if (Config.isDebug()) {
                e.printStackTrace();
            }
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                if (Config.isDebug()) {
                    e.printStackTrace();
                }
            }
        }
    }
}
