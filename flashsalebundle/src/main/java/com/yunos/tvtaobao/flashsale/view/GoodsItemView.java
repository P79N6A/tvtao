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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.listener.ItemAdapterCallback;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;

public class GoodsItemView extends FocusLinearLayout implements
		ItemAdapterCallback<GoodsInfo> {
	private ImageView mStockImage;
	private TextView mStockTip;
	private TextView mDesc;
	// private ViewGoodPrice mPrice;
	private ProgressBar mProgressBar;
	private TextView mPercent;
	private TextView mPriceView;
	private TextView mSalePriceView;
	private TextView mBuyingTime;
	private Resources mRes;
	private View mLine;
	private ImageView mQiangguang,img_buy_rebate;
	private LinearLayout mLayoutRebateInfo;
	private ImageView mRebateTagImg;
	private TextView mRebateTxt;
	private Context mConetxt;
	// changed focus animation params
    protected Params mParams = new Params(1.1f, 1.1f, 5, null, true, 10, new AccelerateDecelerateFrameInterpolator());

	static final private AbsoluteSizeSpan mHightlightSpan = new AbsoluteSizeSpan(
			AppConfig.PRICE_HIGHTLIGHT_FONT_SIZE);

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mStockImage = (ImageView) super.findViewById(R.id.fs_iv_stock_image);
		mStockTip = (TextView) super.findViewById(R.id.fs_iv_stock_tip);
		mDesc = (TextView) super.findViewById(R.id.fs_tv_desc);
		// mPrice = (ViewGoodPrice) super.findViewById(R.id.fs_ll_middle);
		mProgressBar = (ProgressBar) super.findViewById(R.id.fs_pb_percent);
		mPercent = (TextView) super.findViewById(R.id.fs_tv_percent);
		mPriceView = (TextView) super.findViewById(R.id.fs_tv_price);
		mSalePriceView = (TextView) super.findViewById(R.id.fs_tv_saleprice);
		mBuyingTime = (TextView) super.findViewById(R.id.fs_tv_time);
		mSalePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		mLine = super.findViewById(R.id.fs_line);
		mQiangguang = (ImageView) super.findViewById(R.id.fs_iv_qiangguang);
		mLayoutRebateInfo = (android.widget.LinearLayout) findViewById(R.id.layout_rebate_info);
		img_buy_rebate= (ImageView) findViewById(R.id.img_buy_rebate);
		mRebateTagImg = (ImageView) findViewById(R.id.img_rebate_tag);
		mRebateTxt = (TextView) findViewById(R.id.txt_rebate);

		mRes = super.getContext().getResources();

	}

	public GoodsItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mConetxt = context;
		// TODO Auto-generated constructor stub
	}

	public GoodsItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mConetxt = context;
		// TODO Auto-generated constructor stub
	}

	public GoodsItemView(Context context) {
		super(context);
		mConetxt = context;
		// TODO Auto-generated constructor stub
	}

	public void setPrice(double price, int color, double salePrice,
			int saleColor) {

		StringBuilder sb = new StringBuilder();
		sb.append("￥");
		sb.append(String.format("%d", (int) salePrice));
		mSalePriceView.setTextColor(saleColor);
		// mSalePriceView.setTextSize(saleFontSize);
		mSalePriceView.setText(sb.toString());

		mPriceView.setTextColor(color);
		// mPriceView.setTextSize(fontSize);
		String strTemp = "￥" + String.format("%.2f", price);

		int end = strTemp.indexOf('.');
		if (end > 0) {
			SpannableStringBuilder style = new SpannableStringBuilder(strTemp);
			style.setSpan(mHightlightSpan, 1, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			mPriceView.setText(style);
		} else {
			mPriceView.setText(strTemp);
		}
	}


	public void setRebate(RebateBo rebateBo){

		if(rebateBo!=null){
			String rebateBoCoupon= rebateBo.getCoupon();
			if(rebateBo.isMjf()){
				img_buy_rebate.setVisibility(VISIBLE);
			}else {
				img_buy_rebate.setVisibility(GONE);
			}
			if(!TextUtils.isEmpty(rebateBoCoupon)) {
				String couponString = Utils.getRebateCoupon(rebateBoCoupon);
				if(couponString!=null) {
					mLayoutRebateInfo.setVisibility(VISIBLE);
					mRebateTxt.setVisibility(VISIBLE);
					if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
						mRebateTxt.setText(rebateBo.getCouponMessage() + " ¥ " + couponString);
					} else {
						mRebateTxt.setText("预估" + " ¥ " + couponString);

					}
					if (!TextUtils.isEmpty(rebateBo.getPicUrl())) {
						ImageLoaderManager.getImageLoaderManager(mConetxt).loadImage(rebateBo.getPicUrl() + "_240x240.jpg", mRebateTagImg
								, new SimpleImageLoadingListener() {
									@Override
									public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
										super.onLoadingComplete(imageUri, view, loadedImage);
										mRebateTagImg.setImageBitmap(loadedImage);
										mRebateTagImg.setVisibility(VISIBLE);
									}
								});
					}else {
						mRebateTagImg.setVisibility(GONE);

					}
				}else {
					mLayoutRebateInfo.setVisibility(INVISIBLE);
				}
			}else {
				mLayoutRebateInfo.setVisibility(INVISIBLE);
			}

		}
	}

	private void setProgress(int stockCount, int stockPercent,
			GoodItemDrawParam property) {
		int percent = stockPercent;

		if (percent < 0) {
			percent = 0;
		} else if (percent > 100) {
			percent = 100;
		}
		String strFinalBuy = super.getContext().getResources()
				.getString(R.string.str_panic_buying);
		StringBuilder sb = new StringBuilder(strFinalBuy);
		sb.append(percent);
		sb.append("% ");
		sb.append(stockCount);
		sb.append(mRes.getString(R.string.str_jian));

		mPercent.setText(sb.toString());
		// mProgressBar.setBackgroundResource(property.getPrgbarBgResId());
		mProgressBar.setProgressDrawable(mRes.getDrawable(property
				.getPrgbarResId()));
		mProgressBar.setProgress(percent);
		mProgressBar.setVisibility(View.VISIBLE);

	}

	private void setQianggou(String time, String percent, int resId, int color) {

		if (resId > 0) {
			mQiangguang.setVisibility(View.VISIBLE);
			mQiangguang.setBackgroundResource(resId);
		}
		if (null != time) {
			mBuyingTime.setVisibility(View.VISIBLE);
			mBuyingTime.setTextColor(color);
			mBuyingTime.setText(time);
		}
		if (null != percent) {
			mPercent.setText(percent);
		} else {
			mPercent.setText("");
		}
	}

	private String mPicUrl;

	public void displayImage(ImageLoaderManager imageLoader,
			DisplayImageOptions option) {
		// if( AppConfig.DEBUG){
		// LogUtil.i("GoodsItemView", "displayImage: " + mPicUrl);
		// }
		if (!TextUtils.isEmpty(mPicUrl)) {
			imageLoader.displayImage(mPicUrl, mStockImage);
		} else {
			mStockImage.setImageResource(R.drawable.common_default);
		}
	}

	@Override
	public void display(ImageLoaderManager imageLoader,
                        DisplayImageOptions option, GoodsInfo info, Object userData,
                        boolean isScroll) {
		// TODO Auto-generated method stub
		final GoodItemDrawParam properity = (GoodItemDrawParam) userData;

		mProgressBar.setVisibility(View.GONE);
		mQiangguang.setVisibility(View.GONE);
		mBuyingTime.setVisibility(View.GONE);
		mStockTip.setVisibility(View.GONE);

		mPicUrl = info.getPicUrl();
		setPrice(info.getSalePrice(), properity.getSalePriceColor(),
				info.getPrice(), properity.getPriceColor());

		setRebate(info.getRebateBo());

		String name = info.getName();
		if (!TextUtils.isEmpty(name)) {
			mDesc.setText(name);
		}
		if (!isScroll) {
			displayImage(imageLoader, option);
		} else {
			/** 设置默认图 */
			mStockImage.setImageResource(R.drawable.common_default);
		}
		if (info.getType() == GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
			mLine.setVisibility(View.GONE);
			mStockTip.setText(mRes.getString(R.string.str_seckill));
			mStockTip.setVisibility(View.VISIBLE);
			/** 看看秒杀是否已经被抢光了 */
			if (info.isFuture()) {
				setQianggou(
						DateUtils.getTime(info.getStartTime())
								+ mRes.getString(R.string.str_buying), null,
						-1, properity.getQianggouColor());
			} else {
				setQianggou(null, null,
						(info.getRemainingNum() <= 0) ? R.drawable.fs_robbed
								: -1, 0);
			}
			return;
		}
		mLine.setVisibility(View.VISIBLE);
		if (info.isFuture()) {
			int resId = -1;

			MyConcernCache cache = AppManager.getInstance(getContext())
					.getMyConcernCache();
			if (properity.getPageType() == FlipperItemListener.TYPE_PERIOD_BUY) {
				// if (AppConfig.DEBUG) {
				// LogUtil.d("data.getType() = "
				// + info.getType()
				// + ";info.getSeckillId = "
				// + info.getSeckillId()
				// + ";iset = "
				// + cache.queryCacheById((byte) info.getType(),
				// info.getSeckillId()));
				// }
				String seckillId = info.getSeckillId();
				if (!TextUtils.isEmpty(seckillId)
						&& cache.queryCacheById((byte) info.getType(),
								seckillId)) {
					resId = R.drawable.fs_remind;
				}
			}
			String strMyConcern = null;

			if (info.getType() != GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
				strMyConcern = info.getItemViewerNum()
						+ mRes.getString(R.string.str_people_concern);
			}
			/** 还未开抢 */
			setQianggou(
					DateUtils.getTime(info.getStartTime())
							+ mRes.getString(R.string.str_buying),
					strMyConcern, resId, properity.getQianggouColor());

			// int discount = (int) (100 - 10*info.getDiscountPercent()) ;
			//
			// if (discount > 0 && discount < 100) {
			// StringBuilder sb = new
			// StringBuilder(mRes.getString(R.string.str_straight_down));
			// sb.append("\n");
			// sb.append(discount);
			// sb.append("%");
			//
			// mStockTip.setText(sb.toString());
			// mStockTip.setVisibility(View.VISIBLE);
			// }

		} else {
			/** 已经开抢 */
			int stockPercent = (int) info.getStockPercent();
			if (100 == stockPercent) {
				/** 需要截取信息 */
				if (info.getType() != GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
					String soldOutInfo = info.getSoldOutInfo();
					String time = null, des = null;

					if (!TextUtils.isEmpty(soldOutInfo)) {
						int index = soldOutInfo.indexOf("秒");
						if (index < 0) {
							index = soldOutInfo.indexOf("分");
						}
						int size = soldOutInfo.length();

						if (index > 0 && index < size - 1) {
							time = soldOutInfo.substring(0, ++index);
							des = soldOutInfo.substring(index, size);
						}
					}
					setQianggou(time, des, R.drawable.fs_robbed,
							properity.getQianggouColor());
				} else {
					/** 只显示抢光多少 */
					String des = mRes.getString(R.string.str_qiangguang)
							+ info.getSoldNum();

					setQianggou(null, des, R.drawable.fs_robbed,
							properity.getQianggouColor());
				}
			} else {
				/** 还在抢购 */
				setProgress(info.getSoldNum(), stockPercent, properity);

			}
		}

		mLine.setBackgroundColor(properity.getLineResId());
	}

	public static class GoodItemDrawParam {
		/** 分割线条资源ID */
		private int mLineResId;

		/** 定义销售价格颜色 */
		private int mSalePriceColor;

		/** 定义原始价格显示颜色 */
		private int mPriceColor;

		/** 开抢颜色 */
		private int mQianggouColor;

		private byte mPageType = -1;

		/** 设置左侧图片的默认背景 */
		private int mDefaultBgResId;

		/** 设置进度条的风格 */
		private int mPrgbarResId;

		// public void setPrgbarBgResId(int id) {
		// mPrgbarBgResId = id;
		// }
		//
		// public int getPrgbarBgResId() {
		// return mPrgbarBgResId;
		// }

		public void setPrgbarResId(int id) {
			mPrgbarResId = id;
		}

		public int getPrgbarResId() {
			return mPrgbarResId;
		}

		public int getDefaultBgResId() {
			return mDefaultBgResId;
		}

		public void setDefaultBgResId(int id) {
			mDefaultBgResId = id;
		}

		public void setQianggouColor(int color) {
			mQianggouColor = color;
		}

		public int getQianggouColor() {
			return mQianggouColor;
		}

		public void setLineResId(int id) {
			mLineResId = id;
		}

		public int getLineResId() {
			return mLineResId;
		}

		public void setSalePriceColor(int color) {
			mSalePriceColor = color;
		}

		public int getSalePriceColor() {
			return mSalePriceColor;
		}

		public void setPriceColor(int color) {
			mPriceColor = color;
		}

		public int getPriceColor() {
			return mPriceColor;
		}

		public void setPageType(byte type) {
			mPageType = type;
		}

		public byte getPageType() {
			return mPageType;
		}

	}

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return true;
	}
}
