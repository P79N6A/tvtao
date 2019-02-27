/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.app.widget.FlipGridView.OnFlipRunnableListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.listener.ItemAdapterCallback;
import com.yunos.tvtaobao.flashsale.listener.OnMaskListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.view.GoodsGridView;
import com.yunos.tvtaobao.flashsale.view.GoodsItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class GoodsAdapter extends BaseItemAdapter<GoodsInfo> implements OnMaskListener {
	private static final String TAG = "BaseItemAdapter";
	private GoodsGridView mFocusFlipGridView;

	public GoodsAdapter(Context context, GoodsGridView gridView) {
		super(context);
		setMaskAlpha(1.0f, 0.9f);
		mFocusFlipGridView = gridView;
		mFocusFlipGridView.setOnFlipGridViewRunnableListener(mOnFlipRunnableListener);
		mFocusFlipGridView.setOnMaskListener(this);
	}

	@Override
	public void onMask() {
		int firstIndex, lastIndex;
		View selectView = mFocusFlipGridView.getSelectedView();
		
		firstIndex = mFocusFlipGridView.getFirstVisiblePosition();
		lastIndex = mFocusFlipGridView.getLastVisiblePosition();
		for (int index = firstIndex; index <= lastIndex; index++) {
			GoodsItemView v = (GoodsItemView) mFocusFlipGridView
					.getChildAt(index - firstIndex);
			if( v == selectView){
				v.setAlpha(mFocusAlpha);
			}else{
				v.setAlpha(mUnfocusAlpha);
			}

		}

	}
	
	private OnFlipRunnableListener mOnFlipRunnableListener = new OnFlipRunnableListener() {
		@Override
		public void onStart() {
			mImageLoaderManager.cancelLoadAllTaskFor();
			setScroll(true);
		}

		@Override
		public void onFlipItemRunnable(float moveRatio, View itemView,
				int index) {

		}

		@Override
		public void onFinished() {
			setScroll(false);
			/** 显示区域的刷新 */
			int firstIndex, lastIndex;
			firstIndex = mFocusFlipGridView.getFirstVisiblePosition();
			lastIndex = mFocusFlipGridView.getLastVisiblePosition();
			for (int index = firstIndex; index <= lastIndex; index++) {
				GoodsItemView v = (GoodsItemView) mFocusFlipGridView
						.getChildAt(index - firstIndex);
				v.displayImage(mImageLoaderManager, mOptions);

			}
		}
	};
	
	
	@Override
	public View createViewAndCallback(GoodsInfo info, int position) {
		return mInflater.inflate(R.layout.fs_good_item, null);
	}

	public void setPageType(byte pageType) {
		GoodsItemView.GoodItemDrawParam properity = (GoodsItemView.GoodItemDrawParam) getUserData();

		properity.setDefaultBgResId(R.drawable.common_default);
		if (properity.getPageType() != pageType) {
			properity.setPageType(pageType);
			
			switch (pageType) {
			
			case FlipperItemListener.TYPE_MYCONCERN:
				properity.setPriceColor(AppConfig.PRICE_COLOR);
				properity.setSalePriceColor(AppConfig.GREEN_SALEPRICE_COLOR);
//				properity.setLineResId(R.drawable.fs_line_01);
				properity.setLineResId(0xFF86B253);
				properity.setQianggouColor(0xCC9d1540);
				properity.setQianggouColor(AppConfig.GREEN_QIANGGOU_COLOR);
				properity.setPrgbarResId(R.drawable.fs_prgbar_3);
				
				break;

			case FlipperItemListener.TYPE_PERIOD_BUY:
				properity.setPriceColor(AppConfig.PRICE_COLOR);
				properity.setSalePriceColor(AppConfig.RED_SALEPRICE_COLOR);
//				properity.setLineResId(R.drawable.fs_line_02);
				properity.setLineResId(0xFFE25B64);				
				properity.setQianggouColor(AppConfig.RED_QIANGGOU_COLOR);
				properity.setPrgbarResId(R.drawable.fs_prgbar_2);
				
				break;
			case FlipperItemListener.TYPE_FINALLY_BUY:
				
				properity.setPriceColor(AppConfig.PRICE_COLOR);
				properity.setSalePriceColor(AppConfig.PINK_SALEPRICE_COLOR);
//				properity.setLineResId(R.drawable.fs_line_03);
				properity.setLineResId(0xFFC571E6);
				properity.setQianggouColor(AppConfig.PINK_QIANGGOU_COLOR);
				properity.setPrgbarResId(R.drawable.fs_prgbar_1);
				

				break;
			}

		}

	}

	@Override
	protected void OnInit() {
		super.OnInit();
		getUserData();
	}

	@Override
	public Object getUserData() {
		if (null == mUserData) {
			mUserData = new GoodsItemView.GoodItemDrawParam();
		}
		return mUserData;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		
		
		return v;
	}
	public void forceSelectItem() {
		int select = mFocusFlipGridView.getSelectedItemPosition();
		int size = mFocusFlipGridView.getCount();
		View v = mFocusFlipGridView.getSelectedView();
		if (select >= 0 && select < size && null != v) {
			GoodsInfo info = (GoodsInfo) getItem(select);

			if (null != info && v instanceof ItemAdapterCallback) {
				@SuppressWarnings("unchecked")
				ItemAdapterCallback<GoodsInfo> itemView = (ItemAdapterCallback<GoodsInfo>) v;
				itemView.display(mImageLoaderManager,
						mOptions, info,
						mUserData, false);
			}
		}
	}


	@Override
	public void setAdapter(List<GoodsInfo> list) {
		//先获取返利信息
		if(mActivityContext instanceof BaseActivity){
			getRebateInfo(list, (BaseActivity) mActivityContext);
		} else {
			super.setAdapter(list);
		}
	}

	public void notifyData(List<GoodsInfo> list){
		//获取完返利信息，再更新列表
		super.setAdapter(list);
	}


	private void getRebateInfo(List<GoodsInfo> goodsList,BaseActivity baseActivity){
		if(goodsList == null || goodsList.size() < 0){
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < goodsList.size(); i++) {
				GoodsInfo goods = goodsList.get(i);
				String itemId = goods.getItemId();
				String price = String.valueOf(goods.getSalePrice());
				AppDebug.e(TAG, "Rebate itemId = " + itemId + ";isPre = false" + ";price =" + price);
				JSONObject object = new JSONObject();
				object.put("itemId", itemId);
				object.put("isPre", false);
				object.put("price", price);
				jsonArray.put(object);
			}
			AppDebug.e(TAG, "Rebate" + jsonArray.toString());

			BusinessRequest BusinessRequest = new BusinessRequest();

				JSONObject object = new JSONObject();
				object.put("umToken", Config.getUmtoken(baseActivity));
				object.put("wua", Config.getWua(baseActivity));
				object.put("isSimulator", Config.isSimulator(baseActivity));
				object.put("userAgent", Config.getAndroidSystem(baseActivity));
				String extParams = object.toString();

			BusinessRequest.requestRebateMoney(jsonArray.toString()
					, ActivityPathRecorder.getInstance().getCurrentPath(baseActivity)
					,false,false, true,extParams,new GetRebateBusinessRequestListener(new WeakReference<BaseActivity>(baseActivity),goodsList));


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	private class GetRebateBusinessRequestListener extends BizRequestListener<List<RebateBo>> {
		private List<GoodsInfo> mGoodsList;


		public GetRebateBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef,List<GoodsInfo> goodsList) {
			super(baseActivityRef);
			mGoodsList = goodsList;
		}

		@Override
		public boolean onError(int resultCode, String msg) {
			AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
			return false;
		}

		@Override
		public void onSuccess(List<RebateBo> rebateBos) {
			AppDebug.e(TAG, "List<RebateBo> = " + rebateBos.toString());
			if(mGoodsList == null || mGoodsList.size() < 0){
				return;
			}
			for (int i = 0; i < mGoodsList.size(); i++) {
				GoodsInfo goods = mGoodsList.get(i);
				for(int j= 0; j< rebateBos.size(); j++){
					RebateBo rebateBo = rebateBos.get(j);
					if(goods != null && rebateBo != null){
						String itemId = goods.getItemId();
						if(itemId.equals(rebateBo.getItemId())){
							goods.setRebateBo(rebateBo);
							break;
						}
					}
				}
			}
			notifyData(mGoodsList);
		}

		@Override
		public boolean ifFinishWhenCloseErrorDialog() {
			return false;
		}
	}
}
