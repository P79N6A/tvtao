/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */

package com.yunos.tvtaobao.flashsale.activity;

import android.os.Bundle;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.listener.ContextListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;

import java.lang.ref.WeakReference;

public abstract class FlashSaleBaseActivity extends BaseActivity {

	@Override
	protected String getAppTag() {
		// TODO Auto-generated method stub
		return AppConfig.APP_TAG;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppConfig.loadConstData(this);
	}
	private FlashSaleContextListener mFlashSaleContextListener;
	
	public FlashSaleContextListener getFlashSaleContextListener(){
		if( null == mFlashSaleContextListener ){
			mFlashSaleContextListener = new FlashSaleContextListener(this);
		}
		return mFlashSaleContextListener;
	}
	
	public static class FlashSaleContextListener implements ContextListener {
		WeakReference<FlashSaleBaseActivity> mBaseRef;

		public FlashSaleContextListener(FlashSaleBaseActivity activity) {
			mBaseRef = new WeakReference<FlashSaleBaseActivity>(activity);
		}

		@Override
		public void OnWaitProgressDialog(boolean show) {
			// TODO Auto-generated method stub
			BaseActivity baseActivity = mBaseRef.get();
			if (null != baseActivity && !baseActivity.isFinishing()) {
				baseActivity.OnWaitProgressDialog(show);
			}
		}

		@Override
		public void showNetworkErrorDialog(final boolean isfinishActivity) {
			// TODO Auto-generated method stub
			FlashSaleBaseActivity baseActivity = mBaseRef.get();
			if (null != baseActivity && !baseActivity.isFinishing()) {
				baseActivity.showNetworkErrorDialog(isfinishActivity);
			}
		}

		@Override
		public FlashSaleBaseActivity getFlashSaleBaseActivity() {
			// TODO Auto-generated method stub
			FlashSaleBaseActivity baseActivity = mBaseRef.get();
			return baseActivity;
		}

		@Override
		public void showErrorDialog(String msg, boolean isFinishActivity) {
			BaseActivity baseActivity = mBaseRef.get();
			if (null != baseActivity && !baseActivity.isFinishing()) {
				baseActivity.showErrorDialog(msg, isFinishActivity);
			}			
		}
	};

	public abstract void enterDetail(GoodsInfo info);

}
