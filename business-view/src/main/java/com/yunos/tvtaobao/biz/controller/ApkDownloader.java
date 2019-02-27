package com.yunos.tvtaobao.biz.controller;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.util.CheckAPK;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author quanquan.rqq
 *         download and install
 */
public class ApkDownloader extends ABDownloader {

    private static final String TAG = "ApkDownloader";

    private String mSource;

    private String mTarget;

    private String mMd5;

    private String mVersion;

    private long mSize;

    private long mSleepTime;

    private String mReleaseNote;

    private Context mContext;

    private Handler mHandler;

    public ApkDownloader(String source, String target, String mD5, String version,String releaseNote, long size, long sleepTime,
                         Context context, Handler myHandler) {
        super(context, myHandler);
        this.mSource = source;
        this.mTarget = target;
        mMd5 = mD5;
        this.mVersion = version;
        this.mSize = size;
        this.mSleepTime = sleepTime;
        this.mReleaseNote = releaseNote;
        this.mContext = context;
        this.mHandler = myHandler;
    }

    @SuppressWarnings("resource")
    public void download() throws Exception {
        SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        String oldVersion = sp.getString(UpdatePreference.SP_KEY_VERSION, "");
        String oldFilePath = sp.getString(UpdatePreference.SP_KEY_PATH, "");
        String oldMD5 = sp.getString(UpdatePreference.SP_KEY_MD5, "");
        AppDebug.d(TAG, TAG + ".download old version: " + oldVersion + " new version: " + mVersion + " old filepath: "
                + oldFilePath + " new filename: " + mTarget + " old MD5: " + oldMD5 + " new MD5: " + mMd5);
        // 检查更新文件信息是否一致，不一致则删除旧更新文件
        if (!oldVersion.equalsIgnoreCase(mVersion) || !oldFilePath.equalsIgnoreCase(mTarget)
                || !oldMD5.equalsIgnoreCase(mMd5)) {
            Log.w(TAG, TAG + ".download.updateInfo mismatch, delete old file, download new version");
            File oldFile = new File(oldFilePath);
            if (!deleteFile(oldFile))
                return;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(UpdatePreference.SP_KEY_VERSION, mVersion);
            editor.putString(UpdatePreference.SP_KEY_PATH, mTarget);
            editor.putString(UpdatePreference.SP_KEY_MD5, mMd5);
            editor.putString(UpdatePreference.SP_KEY_RELEASE_NOTE, mReleaseNote);
            editor.apply();
        }

        File newAPK = new File(mTarget);
        long curPos = newAPK.length();
        AppDebug.d(TAG, TAG + ".download.current file size: " + curPos + ", mSize = " + mSize + ", mMd5 = " + mMd5);
        // 检查文件是否已经存在
        if (curPos == mSize && mMd5.equalsIgnoreCase(MD5Util.getMD5(newAPK))) {
            Message msg = mHandler.obtainMessage(UpdatePreference.NEW_APK_EXIST);
            mHandler.sendMessage(msg);
            if (!CheckAPK.checkAPKFile(mContext, mTarget, mVersion)) {
                AppDebug.e(TAG, TAG + ".download.update apk check failed");
                if (!deleteFile(newAPK))
                    return;
                msg = mHandler.obtainMessage(UpdatePreference.NEW_APK_INVALID);
                mHandler.sendMessage(msg);
                return;
            }
            AppDebug.d(TAG, TAG + ".download.integrated file, valid apk");
            msg = mHandler.obtainMessage(UpdatePreference.NEW_APK_VALID);
            mHandler.sendMessage(msg);
            return;
        } else if (curPos >= mSize) {
            // 无效文件
            Log.w(TAG, TAG + ".download.invalid old file, download new file");
            if (!deleteFile(newAPK)) {
                return;
            }
            curPos = newAPK.length();
        } else if (curPos != 0) {
            // 断点续传
            AppDebug.d(TAG, TAG + ".download.resume from break point, from: " + curPos);
        }

        long oldSize = curPos;

        RandomAccessFile file = new RandomAccessFile(newAPK, "rw");
        file.seek(curPos);

        URL url = new URL(mSource);
        AppDebug.d(TAG, TAG + ".download.url: " + url);
        HttpURLConnection conn = null;
        InputStream in = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000); // 建立连接15秒超时
            conn.setReadTimeout(15000); // 获取数据15秒超时
            conn.setRequestProperty("Range", "bytes=" + curPos + "-"); // 断点续传
            AppDebug.d(TAG, TAG + ".download.http stream size: " + conn.getContentLength());
            in = conn.getInputStream();
        } catch (Exception e) {
            // http请求发生异常
            AppDebug.e(TAG, TAG + ".download.http connection exception: " + e);
            closeResource(in, conn, file);
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_TIMEOUT);
            mHandler.sendMessage(msg);
            AppDebug.d(TAG, TAG + ".download.close and send message");
            return;
        }
        byte[] buffer = new byte[1024];
        int hasRead = 0;
        long sTime = System.currentTimeMillis();
        long lastTime = sTime;
        long lastPos = curPos;
        int readTimes = 0;
        // 下载
        try {
            while ((hasRead = in.read(buffer)) != -1) {
                readTimes++;
                if (Thread.currentThread().isInterrupted()) { // 中断下载线程
                    AppDebug.d(TAG, TAG + ".download.download thread is interrupted, finish download process");
                    closeResource(in, conn, file);
                    Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_INTERRUPT);
                    mHandler.sendMessage(msg);
                    return;
                }
                file.write(buffer, 0, hasRead);
                curPos += hasRead;
                // test 每秒显示下载百分比和速度
                if (System.currentTimeMillis() - lastTime >= 500) {
                    int progress = (int) (curPos * 100 / mSize);
                    Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_PROGRESS_UPDATE, progress, 0);
                    mHandler.sendMessage(msg);
                    AppDebug.d(TAG, TAG + ".download." + curPos + " " + progress + "% " + (curPos - lastPos) * 1000
                            / 1024 / (System.currentTimeMillis() - lastTime) + "KB/s "
                            + Thread.currentThread().getName());
                    lastTime = System.currentTimeMillis();
                    lastPos = curPos;
                }
                if (readTimes % 5 == 0) {
                    readTimes = 0;
                    Thread.sleep(mSleepTime); // 控制下载速度
                }
            }
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_PROGRESS_UPDATE, 100, 0);
            mHandler.sendMessage(msg);
            long eTime = System.currentTimeMillis();
            AppDebug.d(TAG, TAG + ".download.elapsed time: " + (eTime - sTime) + "ms, average speed: " + (curPos - oldSize)
                    * 1000 / 1024 / (eTime - sTime) + "KB/s");
        } catch (InterruptedException e) {
            // 中断线程
            AppDebug.w(TAG, TAG + ".download. thread is interrupted(InterruptedExcetpion), finish download process");
            closeResource(in, conn, file);
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_INTERRUPT);
            mHandler.sendMessage(msg);
            return;
        } catch (Exception e) {
            // 下载中出现异常
            AppDebug.e(TAG, TAG + ".download. encounter exception, may be caused by network problem, retry");
            closeResource(in, conn, file);
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_TIMEOUT);
            mHandler.sendMessage(msg);
            return;
        }
        closeResource(in, conn, file);
        newAPK = new File(mTarget);
        String newMD5 = MD5Util.getMD5(newAPK);
        AppDebug.d(TAG, TAG + ".download. finish, curPos = " + curPos + ", mSize = " + mSize + ", mMd5 = " + mMd5
                + ", newMD5 = " + newMD5);
        if (curPos == mSize && mMd5.equalsIgnoreCase(newMD5)) { // 校验文件
            AppDebug.d(TAG, TAG + ".download. finish, valid new apk");
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_DONE, curPos);
            mHandler.sendMessage(msg);
        } else {
            // 无效文件，重新下载
            AppDebug.e(TAG, TAG + ".download.invalid new apk, need to redownload");
            if (!deleteFile(newAPK)) {
                return;
            }

            Message msg = mHandler.obtainMessage(UpdatePreference.NEW_APK_INVALID);
            mHandler.sendMessage(msg);
        }
    }

//    // 删除文件
//    private boolean deleteFile(File file) {
//        AppDebug.v(TAG, TAG + ".deleteFile.file = " + file);
//        if (file == null || !file.exists()) {
//            return true;
//        }
//
//        AppDebug.d(TAG, TAG + ".deleteFile.file.length: " + file.length() + ",filePath = " + file.getPath());
//        boolean deleted = file.delete();//mContext.deleteFile(file.getPath());
//        AppDebug.d(TAG, TAG + ".deleteFile.deleted = " + deleted);
//        if (!deleted) {
//            Message msg = mHandler.obtainMessage(UpdatePreference.EXCEPTION);
//            mHandler.sendMessage(msg);
//            return false;
//        }
//        return true;
//    }

//    // 关闭资源
//    private void closeResource(InputStream in, HttpURLConnection conn, RandomAccessFile file) {
//        if (in != null) {
//            try {
//                in.close();
//            } catch (IOException e) {
//            }
//        }
//        if (file != null) {
//            try {
//                file.close();
//            } catch (IOException e) {
//            }
//        }
//        if (conn != null)
//            conn.disconnect();
//    }

}
