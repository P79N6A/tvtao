/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.RecommendInfo;
import com.yunos.tvtaobao.flashsale.listener.ItemAdapterCallback;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;

public class RecommendItemView extends FocusLinearLayout implements
		ItemAdapterCallback<RecommendInfo> {
	private ImageView mStockImage;
	private TextView mDesc;
	private TextView mSalePriceView;
	private TextView mPriceView;

	static final private AbsoluteSizeSpan mHightlightSpan = new AbsoluteSizeSpan(
			AppConfig.PRICE_HIGHTLIGHT_SIZE_FOR_ITEM);

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mStockImage = (ImageView) super.findViewById(R.id.fs_fl_stock_image);
		mDesc = (TextView) super.findViewById(R.id.fs_tv_desc);
		mSalePriceView = (TextView) super.findViewById(R.id.fs_tv_saleprice);
		mPriceView = (TextView) super.findViewById(R.id.fs_tv_price);
		mPriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
	}

	public RecommendItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RecommendItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RecommendItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private void setSalePriceView(double salePrice) {

		String strTemp = "￥" + String.format("%.2f", salePrice);
		int end = strTemp.indexOf('.');
		if (end > 0) {
			SpannableStringBuilder style = new SpannableStringBuilder(strTemp);
			style.setSpan(mHightlightSpan, 1, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			mSalePriceView.setText(style);
		} else {
			mSalePriceView.setText(strTemp);
		}
	}

	@Override
	public void display(ImageLoaderManager imageLoader,
                        DisplayImageOptions option, RecommendInfo data, Object userData, boolean isScroll) {
		// TODO Auto-generated method stub

		String url = data.getPicUrl();
		if (null != url) {
			imageLoader.displayImage(url, mStockImage, option);
		}

		mDesc.setText(data.getTitle());
		setSalePriceView(data.getSalePrice());

		mPriceView.setText("￥" + data.getReservePrice());

	}

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return true;
	}

}
