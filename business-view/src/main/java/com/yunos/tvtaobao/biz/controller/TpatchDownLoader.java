package com.yunos.tvtaobao.biz.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by huangdaju on 17/8/22.
 */

public class TpatchDownLoader extends ABDownloader {

    private static final String TAG = "Downloader2";

    private String mSource;

    private String mTarget;

    private String mMd5;

    private String mVersionName;

    private long mSize;

    private long mSleepTime;

    private Context mContext;

    private Handler mHandler;

    private static final int UPDATEINFO_MISMATCH = 0;
    private static final int UPDATEINFO_DELETE_ERROR = 1;
    private static final int UPDATEINFO_TPATCH_EXITS = 2;
    private static final int UPDATEINFO_DOWNLOAD_RESUME = 3;
    private static final int UPDATEINFO_FILE_INVALID = 4;


    public TpatchDownLoader(String source, String target, String md5, String versionName, long size, long sleepTime, Context context, Handler handler) {
        super(context, handler);
        mSource = source;
        mTarget = target;
        mMd5 = md5;
        mVersionName = versionName;
        mSize = size;
        mSleepTime = sleepTime;
        mContext = context;
        mHandler = handler;
    }

    public int checkTpatch(Context context, String versionName, String target, String MD5, long size) throws Exception {
        // 检查更新文件信息是否一致，不一致则删除旧更新文件
        SharedPreferences sp = context.getSharedPreferences(UpdatePreference.SP_TPATCH_FILE_NAME, Context.MODE_PRIVATE);
        String oldVersionName = sp.getString(UpdatePreference.SP_KEY_VERSION_NAME, "");
        String oldFilePath = sp.getString(UpdatePreference.SP_KEY_PATH, "");
        String oldMD5 = sp.getString(UpdatePreference.SP_KEY_MD5, "");
        if (!oldVersionName.equalsIgnoreCase(versionName) || !oldFilePath.equalsIgnoreCase(target) || !oldMD5.equalsIgnoreCase(MD5)) {
            AppDebug.w(TAG, TAG + ".download.updateInfo mismatch, delete old file, download new version");
            File oldFile = new File(oldFilePath);
            if (!deleteFile(oldFile))
                return UPDATEINFO_DELETE_ERROR;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(UpdatePreference.SP_KEY_VERSION_NAME, versionName);
            editor.putString(UpdatePreference.SP_KEY_PATH, target);
            editor.putString(UpdatePreference.SP_KEY_MD5, MD5);
            editor.apply();
        }
        File newAPK = new File(target);
        long curPos = newAPK.length();
        AppDebug.d(TAG, TAG + ".download.current file size: " + curPos + ", mSize = " + size + ", mMd5 = " + MD5);
        // 检查文件是否已经存在
        if (curPos == size && MD5.equalsIgnoreCase(MD5Util.getMD5(newAPK))) {
            //存在有效的tpatch文件
            AppDebug.d(TAG, TAG + ".download.integrated file, valid apk");
            return UPDATEINFO_TPATCH_EXITS;
        } else if (curPos >= size) {
            // 无效文件
            AppDebug.w(TAG, TAG + ".download.invalid old file, download new file");
            if (!deleteFile(newAPK)) {
                return UPDATEINFO_DELETE_ERROR;
            }
        } else if (curPos != 0) {
            // 断点续传
            AppDebug.d(TAG, TAG + ".download.resume from break point, from: " + curPos);
            return UPDATEINFO_DOWNLOAD_RESUME;
        }
        return -1;

    }

    @Override
    public void download() throws Exception {
        int flag = checkTpatch(mContext, mVersionName, mTarget, mMd5, mSize);
        File newAPK = new File(mTarget);
        long curPos = newAPK.length();
        long oldSize = curPos;
        switch (flag) {
            case UPDATEINFO_DELETE_ERROR:
                return;
            case UPDATEINFO_FILE_INVALID:
                break;
            case UPDATEINFO_DOWNLOAD_RESUME:
                break;
            case UPDATEINFO_TPATCH_EXITS:
                Message msg = mHandler.obtainMessage(UpdatePreference.NEW_TPATCH_VALID);
                mHandler.sendMessage(msg);
                return;
        }

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
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_TPATCH_INTERRUPT);
            mHandler.sendMessage(msg);
            return;
        } catch (Exception e) {
            // 下载中出现异常
            AppDebug.e(TAG, TAG + ".download. encounter exception, may be caused by network problem, retry");
            closeResource(in, conn, file);
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_TPATCH_TIMEOUT);
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
            Message msg = mHandler.obtainMessage(UpdatePreference.DOWNLOAD_TPATCH_DONE, curPos);
            mHandler.sendMessage(msg);
        } else {
            // 无效文件，重新下载
            AppDebug.e(TAG, TAG + ".download.invalid new apk, need to redownload");
            if (!deleteFile(newAPK)) {
                return;
            }

            Message msg = mHandler.obtainMessage(UpdatePreference.NEW_TPATCH_INVALID);
            mHandler.sendMessage(msg);
        }
    }
}
