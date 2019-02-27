package com.yunos.tvtaobao.biz.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;

/**
 * Created by huangdaju on 17/8/22.
 */

public abstract class ABDownloader {

    private static final String TAG = "ABDownloader";

    public Context mContext;

    public Handler mHandler;

    public ABDownloader(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public abstract void download() throws Exception;


    // 关闭资源
    public void closeResource(InputStream in, HttpURLConnection conn, RandomAccessFile file) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
            }
        }
        if (conn != null)
            conn.disconnect();
    }

    // 删除文件
    public boolean deleteFile(File file) {
        AppDebug.v(TAG, TAG + ".deleteFile.file = " + file);
        if (file == null || !file.exists()) {
            return true;
        }

        AppDebug.d(TAG, TAG + ".deleteFile.file.length: " + file.length() + ",filePath = " + file.getPath());
        boolean deleted = file.delete();//mContext.deleteFile(file.getPath());
        AppDebug.d(TAG, TAG + ".deleteFile.deleted = " + deleted);
        if (!deleted) {
            Message msg = mHandler.obtainMessage(UpdatePreference.EXCEPTION);
            mHandler.sendMessage(msg);
            return false;
        }
        return true;
    }
}
