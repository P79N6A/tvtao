package com.yunos.tv.msg;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


/**
 * 非UI线程的Handler
 * @author tim
 */
public class MessageHandler{
	private static final String TAG = "MessageHandler";
	private static MessageHandler mMessageHandler;
	private Handler mHandler;
	private Thread mThread;
	private Looper mThreadLooper;
	public static MessageHandler getInstance(){
		if(mMessageHandler == null){
			mMessageHandler = new MessageHandler();
		}
		return mMessageHandler;
	}
	
	
	/**
	 * 退出（退出线程并销毁，之前在列表里面的请求也同时会被删除而不会执行）
	 */
	public void destroy(){
		Log.i(TAG, "destroy");
		if(mThreadLooper != null){
			mThreadLooper.quit();
		}
		mMessageHandler = null;
	}
	
	public Handler getHandler(){
		return mHandler;
	}
	
	
	/**
	 * 请求
	 * @param runnable
	 */
	public void post(Runnable runnable){
		mHandler.post(runnable);
	}
	
	
	/**
	 * 延迟请求
	 * @param runnable
	 * @param delayMillis
	 */
	public void postDelay(Runnable runnable, long delayMillis){
		mHandler.postDelayed(runnable, delayMillis);
	}
	
	
	/**
	 * 删除指定的
	 * @param runnable
	 */
	public void remove(Runnable runnable){
		mHandler.removeCallbacks(runnable);
	}
	
	
	/**
	 * 删除所有
	 */
	public void removeAll(){
		mHandler.removeMessages(0);
	}
	
	
	/**
	 * 创建对象（同步启动线程）
	 */
	private MessageHandler(){
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				synchronized (mThread) {
					mHandler = new Handler();
					Log.i(TAG, "mThread is started");
					mThread.notifyAll();
				}
				mThreadLooper = Looper.myLooper();
				Looper.loop();
				Log.i(TAG, "mThread is stopped");
			}
		});
		mThread.start();
		Log.i(TAG, "mThread is start");
		//等待线程启动
		synchronized (mThread) {
			while (mHandler == null) {
				try {
					Log.i(TAG, "mThread is waitting start");
					mThread.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Log.i(TAG, "mThread is continue");
	}
}
