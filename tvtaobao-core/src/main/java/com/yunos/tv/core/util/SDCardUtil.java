package com.yunos.tv.core.util;


import android.os.Environment;

/**
 * 针对SD卡的操作
 * @author tingmeng.ytm
 *
 */
public class SDCardUtil {

    /**
     * SD卡是否可写
     * @return
     */
    public static boolean isSdCardWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * SD卡是否可读
     * @return
     */
    public static boolean isSdCardReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 取得SD卡的路径
     * @return
     */
    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
