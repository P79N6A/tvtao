/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.adapter;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.display.FadeInBitmapDisplayer;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.listener.CreateViewAndCallback;
import com.yunos.tvtaobao.flashsale.listener.ItemAdapterCallback;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class BaseItemAdapter<T> extends BaseAdapter {

	final private List<T> mData = new ArrayList<T>(50);
	protected LayoutInflater mInflater;
	protected ImageLoaderManager mImageLoaderManager;
	protected DisplayImageOptions mOptions;
	protected Object mUserData;
	protected boolean mIsScroll = false;
	
	protected boolean mHasMask = false;
	protected float mFocusAlpha;
	protected float mUnfocusAlpha;
	protected Context mActivityContext;

	public void setMaskAlpha(float focusAlpha, float unFocusAlpha){
		mHasMask = true;
		mFocusAlpha = focusAlpha;
		mUnfocusAlpha = unFocusAlpha;
	}
	public void setScroll(boolean isScroll){
		mIsScroll = isScroll;
	}
	
	public void clearData(){
		mData.clear();
	}
	public BaseItemAdapter(Context context) {
		try {
			mActivityContext = context;
			mInflater = (LayoutInflater) mActivityContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mImageLoaderManager = ImageLoaderManager
					.getImageLoaderManager(mActivityContext);
			OnInit();
		}catch (Exception  e){
			e.printStackTrace();
		}

	}

	public DisplayImageOptions getDisplayImageOptions() {
		return mOptions;
	}

	public ImageLoaderManager getImageLoaderManager() {
		return mImageLoaderManager;
	}

	public Object getUserData() {
		return mUserData;
	}

	public List<T> getData() {
		return mData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		T info = mData.get(position);

		if (null == convertView) {
			convertView = createViewAndCallbackEX(info, position);
		}
		@SuppressWarnings("rawtypes")
		ItemAdapterCallback callback = (ItemAdapterCallback) convertView;
		callback.display(mImageLoaderManager, mOptions, info, mUserData, mIsScroll);
		
		if( mHasMask ){
			convertView.setAlpha(mUnfocusAlpha);
		}	
		return convertView;

	}

	@SuppressWarnings("unused")
	protected void OnInit() {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.common_default)
				.showImageOnFail(R.drawable.common_default)
				.showImageOnLoading(R.drawable.common_default)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
		// .resetViewBeforeLoading();

		if (AppConfig.IMAGELOAD_SWITCH_TIMEOUT > 0) {
			builder.displayer(new FadeInBitmapDisplayer(
					AppConfig.IMAGELOAD_SWITCH_TIMEOUT));
		}
		mOptions = builder.build();
	}

	public void setAdapter(List<T> list) {
		mData.clear();
		if( null != list){
			mData.addAll(list);
		}
		super.notifyDataSetInvalidated();
//		super.notifyDataSetChanged();
	}

	private CreateViewAndCallback<T> mCallback;

	public void setOnCreateViewAndCallback(CreateViewAndCallback<T> l) {
		mCallback = l;
	}

	public CreateViewAndCallback<T> getOnCreateViewAndCallback() {
		return mCallback;
	}

	protected View createViewAndCallback(T info, int position) {
		throw new IllegalStateException("need create item view function");
	}

	protected View createViewAndCallbackEX(T info, int position) {
		View v = null;

		if (null != mCallback) {
			v = mCallback.OnCreateViewAndCallback(info, position);
		} else {
			v = createViewAndCallback(info, position);
		}
		if (null == v || !(v instanceof ItemAdapterCallback)) {
			throw new IllegalStateException(
					"create item view type error or null");
		}
		return v;

	}

}
