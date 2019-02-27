package com.yunos.tvtaobao.biz.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.service.UpdateService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ljy on 2017/12/28.
 * Atlas热更新日志记录
 */

public class LogUtils {

    private static LogUtils instance = null;

    private String dirPath;//保存路径

    private int appid;//应用pid

    private Thread logThread;

    private LogRunnable runnable=null;

    private Thread logReadThread;

    private LogReadDataRunnable logReadrunnable=null;

    private static String LOG_NAME="atlasUpdate.log";

    private static String TAG="LogUtils";

    private static File file;

    //    Boolean bl=false;
    static ReadWriteLock readWriteLock=new ReentrantReadWriteLock();

    public static LogUtils getInstance(Context mContext){
        Log.v(TAG, "getInstance");
        if(instance == null){
            instance = new LogUtils(mContext);
        }
        return instance;
    }

    private LogUtils(Context mContext) {
        appid = android.os.Process.myPid();
        if(Utils.ExistSDCard()){
            dirPath = String.valueOf(mContext.getExternalCacheDir());
        }else {
            File filesFolder = mContext.getFilesDir();
            dirPath =filesFolder.getPath();
        }
        Log.v(TAG, "dirPath"+dirPath);
    }

    /**
     * 启动log日志保存
     */
    public void start(){
        Log.v(TAG,  "start");
        runnable=new LogRunnable(appid, dirPath);
        logThread = new Thread(runnable);
        logThread.start();
    }

    /**
     * 启动log日志保存
     */
    public void stop(){
        Log.v(TAG, "stop");
        try{
//            if(logThread != null){
//                logThread.interrupt();
//            }
//            if(logReadThread != null){
//                logReadThread.interrupt();
//            }
//            if(logReadrunnable!=null){
//                logReadrunnable.stopSource();
//            }
//            if(runnable!=null){
//                runnable.stopSource();
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static class LogRunnable implements Runnable{
        private Process mProcess;
        private FileOutputStream fos;
        private BufferedReader mReader;
        private String cmds;
        private String mPid;

        public void stopSource(){
            if(mProcess != null){
                mProcess.destroy();
                mProcess = null;
            }
            try {
                if(mReader != null){
                    mReader.close();
                    mReader = null;
                }
                if(fos != null){
                    fos.close();
                    fos = null;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        public LogRunnable(int pid,String dirPath) {
            Log.v(TAG,  "LogRunnable");
            this.mPid = ""+pid;
            try {
                file = new File(dirPath,LOG_NAME);

                if(file.exists()){
                    file.delete();
                }

                file.createNewFile();
                fos = new FileOutputStream(file,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            cmds = "logcat *:v | grep \"(" + mPid + ")\"";
            cmds = "logcat -v time | grep update";
        }

        @Override
        public void run() {
            Log.v(TAG,  "LogRunnable-run");
            readWriteLock.writeLock().lock();
            readWriteLock.readLock().lock();
            try {
                String[] commands=new String[]{"sh","-c",cmds};
                mProcess = Runtime.getRuntime().exec(commands);
                mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()),1024);
                String line;
                while((line = mReader.readLine()) != null){
                    if(line.length() == 0){
                        continue;
                    }
                    if(fos != null && line.contains(mPid)){
                        fos.write((FormatDate.getFormatTime()+" "+line+"\r\n").getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(mProcess != null){
                    mProcess.destroy();
                    mProcess = null;
                }
                try {
                    if(mReader != null){
                        mReader.close();
                        mReader = null;
                    }
                    if(fos != null){
                        fos.close();
                        fos = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                readWriteLock.writeLock().unlock();
                readWriteLock.readLock().unlock();
            }
        }
    }

    private static class FormatDate{

        public static String getFormatDate(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
            return sdf.format(System.currentTimeMillis());
        }

        public static String getFormatTime(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(System.currentTimeMillis());
        }
    }

    //上传日志
    public void logReceive(UpdateService mContext){
        this.mContext=mContext;
        Log.v(TAG,  "logReceive");

        if(Utils.ExistSDCard()){
            logPath =mContext.getExternalCacheDir()+File.separator+LOG_NAME;
        }else {
            File filesFolder = mContext.getFilesDir();
            logPath =filesFolder.getPath()+File.separator+LOG_NAME;
        }

        Log.v(TAG,  "logPath"+logPath);

        mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 10000);


    }
    private UpdateService mContext;
    String logPath="";
    private Handler mHandler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if(0==msg.what){
                logReadrunnable=new LogReadDataRunnable(mContext,logPath);
                logReadThread = new Thread(logReadrunnable);
                logReadThread.start();
            }
        }
    };


    private static class LogReadDataRunnable implements Runnable{
        private UpdateService mContext;
        private String logPath;
        private  BufferedReader br;
        private FileInputStream fin;

        public LogReadDataRunnable(UpdateService context,String mlogPath) {
            Log.v(TAG,  "LogReadDataRunnable");
            mContext=context;
            logPath=mlogPath;
        }
        public void stopSource(){
            try {
                if(br != null){
                    br.close();
                    br = null;
                }
                if(fin != null){
                    fin.close();
                    fin = null;
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        @Override
        public void run() {
            Log.v(TAG,  "LogReadDataRunnable-run");
            String log="";
            try {
//                readWriteLock.readLock().lock();
//                Thread.sleep();
//                this.wait(10000);
                try {
//                     file = new File(logPath);
                    fin = new FileInputStream(file);
                    br = null;
                    StringBuilder sb = new StringBuilder();
                    String line;
                    try {
                        br = new BufferedReader(new InputStreamReader(fin));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                    } catch (IOException e) {
//                    } finally {
//                        if (br != null) {
//                            try {
//                                br.close();
//                            } catch (IOException e) {
//                            }
//                        }
                    }
                    log= sb.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
//                readWriteLock.readLock().unlock();
                try {
                    if(br != null){
                        br.close();
                        br = null;
                    }
                    if(fin != null){
                        fin.close();
                        fin = null;
                    }

                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                Log.v(TAG, "log"+log);

                Message msg = ((Handler)mContext.getmServiceHandler()).obtainMessage(UpdatePreference.LOG_READ);
                msg.obj=log;

                ((Handler)mContext.getmServiceHandler()).sendMessage(msg);

            }

            Log.v(TAG, "runfinish");
        }
    }

}
