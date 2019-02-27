//package com.aliyun.base;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.view.View;
//
//import com.aliyun.base.exception.ExceptionManager;
//import com.aliyun.base.exception.NoNetworkException;
//import com.aliyun.base.net.NetworkManager;
//
//public abstract class WorkAsyncTask<Result> extends AsyncTask<Object, Object, Result>  {
//	private static final String TAG = "WorkAsyncTask";
//
//	protected View[] mViews;
//
//	private boolean mIsHttpTask = true;
//
//	private Context mContext;
//
//	private Exception mException = null;
//
//	public Exception getmException() {
//		return mException;
//	}
//
//	public WorkAsyncTask(Context context) {
//		this(context, true, (View[]) null);
//	}
//
//	public WorkAsyncTask(Context context, View... views) {
//		this(context, true, views);
//	}
//
//	public WorkAsyncTask(Context context, boolean isHttpTask) {
//		this(context, isHttpTask, (View[]) null);
//	}
//
//	public WorkAsyncTask(Context context, boolean isHttpTask, View... views) {
//		this.mIsHttpTask = isHttpTask;
//		this.mContext = context;
//		mViews = views;
//	}
//
//	@Override
//	final protected void onPreExecute() {
//		try {
//			if (mViews != null) {
//				for (View view : mViews) {
//					if (view != null) {
//						view.setEnabled(false);
//					}
//				}
//			}
//			onPre();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	final protected Result doInBackground(Object... params) {
//		try {
//			if(isCancelled()) {
//				return null;
//			}
//			if (mIsHttpTask && (!NetworkManager.isNetworkAvailable(mContext))) {
//				throw new NoNetworkException();
//			}
//			return doProgress();
//		} catch (Exception e) {
//			mException = e;
//			return null;
//		}
//	}
//
//	@Override
//	final protected void onProgressUpdate(Object... values) {
//		try {
//			onUpdate(values);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	final protected void onPostExecute(Result resultObject) {
//		if (mViews != null) {
//			for (View view : mViews) {
//				if (view != null) {
//					view.setEnabled(true);
//				}
//			}
//		}
//		try {
//			if (mException != null) {
//				boolean isHandled = ExceptionManager.handleException(mContext, mException);//异常处理管理
//				Log.w(TAG, "onPostExecute -- mException -- isHandled:" + isHandled);
//				if (!isHandled) { // 避免重复处理
//					onError(mException);
//				}
//				onPost(false, resultObject);
//			} else {
//				onPost(true, resultObject);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	final protected void onCancelled() {
//		super.onCancelled();
//		if (mViews != null) {
//			for (View view : mViews) {
//				view.setEnabled(true);
//			}
//		}
//		try {
//			if (mException != null) {
//				boolean isHandled = ExceptionManager.handleException(mContext, mException);//异常处理管理
//				if (!isHandled) { // 避免重复处理
//					onError(mException);
//				}
//				onCancel(false);
//			} else {
//				onCancel(true);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public abstract Result doProgress() throws Exception;
//
//	public void onPre() throws Exception {}
//
//	public void onUpdate(Object... values) throws Exception {}
//
//	/**
//	 *
//	 * @param isSuccess 如果true代表成功。如果false代表有错误，交给ExceptionManager和onError处理
//	 * @param resultObject
//	 * @throws Exception
//	 */
//	public void onPost(boolean isSuccess, Result resultObject) throws Exception {}
//
//	public void onCancel(boolean isSuccess) {}
//
//	public void onError(Exception e) {}
//
//	protected Context getContext() {
//		return mContext;
//	}
//
//}
