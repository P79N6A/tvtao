package com.aliyun.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.aliyun.base.ui.LoadingAlert;

import java.util.ArrayList;


public class BaseActivity extends Activity {
	
	public static final int ACTIVITY_STATE_IDLE = 0;
	public static final int ACTIVITY_STATE_CREATED = 1;
	public static final int ACTIVITY_STATE_RESTARED = 2;
	public static final int ACTIVITY_STATE_STARTED = 3;
	public static final int ACTIVITY_STATE_RESUMED = 4;
	public static final int ACTIVITY_STATE_PAUSED = 5;
	public static final int ACTIVITY_STATE_STOPED = 6;
	public static final int ACTIVITY_STATE_DESTROIED = 7;
	
	protected int mState = ACTIVITY_STATE_IDLE;

	
	private LoadingAlert mLoading;
	private Animation mShowAction = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mState = ACTIVITY_STATE_CREATED;
	}


	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mState = ACTIVITY_STATE_STARTED;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mState = ACTIVITY_STATE_RESUMED;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mState = ACTIVITY_STATE_PAUSED;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mState = ACTIVITY_STATE_STOPED;
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		mState = ACTIVITY_STATE_RESTARED;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelTask();
		mState = ACTIVITY_STATE_DESTROIED;
	}
	
	@Override 
	protected void onNewIntent(Intent intent) {      
	    super.onNewIntent(intent); 
	    setIntent(intent);
	}
	


	public Animation showAnimation() {
		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mShowAction.setDuration(150);
		return mShowAction;
	}

	public void cancelTask() {
//		if (mTaskList != null) {
//			for (WorkAsyncTask task : mTaskList) {
//				if (!task.isCancelled()) {
//					try {
//						task.cancel(true);
//						task = null;
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//				}
//			}
//		}
//		mTaskList = null;
	}

	public void showLoading() {
		showLoading("正在加载...");
	}

	public void showLoading(final String message) {
		if (mLoading == null) {
			mLoading = new LoadingAlert(BaseActivity.this);
		}
		mLoading.setMessage(message);
		mLoading.setVisibility(View.VISIBLE);
	}

	public void hideLoading() {
		if (mLoading != null) {
			mLoading.setVisibility(View.GONE);
		}
	}

	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int resId) {
		Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
	}
	public void alert(int resId) {
		alert(resId, getString(android.R.string.dialog_alert_title));
	}

	public void alert(String msg) {
		alert(msg, getString(android.R.string.dialog_alert_title));
	}

	public void alert(int msgId, String title) {
		alert(getString(msgId), title);
	}
	
	public void alert(int msgId, int titleId) {
		alert(getString(msgId), getString(titleId));
	}

	public void alert(String msg, String title) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create().show();
	}
	public void hideSoftKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public void hideSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = getCurrentFocus();
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void showSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void showSoftKeyBoard(View v) {
		v.setFocusable(true);
		v.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
	}
	
}
