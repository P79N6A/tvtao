/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-20       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;


public class TitlebarView extends View {
	
	//P1-29 P2-280 P3-10 P4-398 P5-104 P6-18
	//M1-19 M2-12 M3-10 M4-11 M5-18 M6-12 M7-29 M8-9 M9-31
	//M10-29 M11-16 M12-13 M13-10 M14-9 M15-8 M16-7 M17-8 M18-7 M19-7
	//M20-6 M21-6 M22-7 M23-20 M24-14 M25-19 M26-12 M27-35 M28-3 M29-18 
	//1-228*228 2-40%,168*228 3-126*5 6-163*52 7-360*72
	
	/**“淘抢购”的头图片*/
	private final int HEAD_MARGIN_TOP = 29;
	private final int HEAD_MARGIN_LEFT = 398;    //p4-p2
	private final int HEAD_WIDTH = 163;
	private final int HEAD_HEIGHT = 52;
	
	/**文字的背景*/
	private final int BG_MARGIN_LEFT = HEAD_MARGIN_LEFT + HEAD_WIDTH + 3;    //+M28
	private final int BG_MARGIN_TOP = 18;
	private final int BG_WIDTH = 360;
	private final int BG_HEIGHT = 72;
	
	/**标题*/
	private final int TITLE_MARGIN_LEFT = HEAD_MARGIN_LEFT + HEAD_WIDTH + 14;    //+M24
	/**标题提示*/
	private final int TITLE_TIP_MARGIN_LEFT = TITLE_MARGIN_LEFT + 19;    //M25
	
	
	private Context mContext;
	private byte mTitlebarType = -1;
	
	/**“淘抢购”字样图片*/
	private Drawable mHeadDrawable;
	
	/**文字的背景*/
	private Drawable mBackgroundDrawable;
	
	/**标题文字画笔*/
	private Paint mTextPaintTitle;
	
	/**标题提示文字画笔*/
	private Paint mTextPaintTip;
	
	/**标题提示文字 */
	private String mTitltTip;
	
	private int title_imgs[] = {R.drawable.title_bg_my_concern,
			R.drawable.title_bg_period_buy,R.drawable.title_bg_finally_buy};
	
	private int title_strings[] = {R.string.str_my_concern,R.string.str_period_of_buying,R.string.str_finally_berserk};

	public TitlebarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initView();
	}

	public TitlebarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	public TitlebarView(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	private void initView() {
		
		mHeadDrawable = getResources().getDrawable(R.drawable.fs_title_head);
		
		mTextPaintTitle = new Paint();
		mTextPaintTitle.setTextSize(AppConfig.TITLEBAR_TITLE_TEXT_SIZE);
		mTextPaintTitle.setColor(0xffffd4d4);
		
		mTextPaintTip = new Paint();
		mTextPaintTip.setTextSize(AppConfig.TITLEBAR_TIP_TEXT_SIZE);
		mTextPaintTip.setColor(0xffffd4d4);
		
	}
	
	public void setTitleBarType(byte titlebarType){
		mTitlebarType = titlebarType;
	}
	
	public void setTitltTip(String titltTip){
		mTitltTip = titltTip;
		invalidate();	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		LogUtil.d("mTitlebarType = " + mTitlebarType);
		if(mTitlebarType == -1){
			return;
		}
		
		//绘制标题头“淘抢购”
		mHeadDrawable.setBounds(CommUtil.dip2px(mContext, HEAD_MARGIN_LEFT), CommUtil.dip2px(mContext, HEAD_MARGIN_TOP),
				CommUtil.dip2px(mContext, HEAD_MARGIN_LEFT) + CommUtil.dip2px(mContext, HEAD_WIDTH), 
				CommUtil.dip2px(mContext, HEAD_MARGIN_TOP) + CommUtil.dip2px(mContext, HEAD_HEIGHT));
		mHeadDrawable.draw(canvas);
		
		//绘制文字背景
		mBackgroundDrawable = getResources().getDrawable(getBackgroundId());
		mBackgroundDrawable.setBounds(CommUtil.dip2px(mContext, BG_MARGIN_LEFT), CommUtil.dip2px(mContext, BG_MARGIN_TOP), 
				CommUtil.dip2px(mContext, BG_MARGIN_LEFT) + CommUtil.dip2px(mContext, BG_WIDTH), 
				CommUtil.dip2px(mContext, BG_MARGIN_TOP) + CommUtil.dip2px(mContext, BG_HEIGHT));
		mBackgroundDrawable.draw(canvas);
		
		
		//绘制标题文字
		String title = getTitle();
		int titleTop = CommUtil.dip2px(mContext, BG_MARGIN_TOP + BG_HEIGHT/2 + 10);
		int titleLength = 0;
		if(!TextUtils.isEmpty(title)){
			canvas.drawText(title, CommUtil.dip2px(mContext, TITLE_MARGIN_LEFT), titleTop, mTextPaintTitle);
			titleLength = (int)mTextPaintTitle.measureText(title);
		}
		
		//绘制标题提示
		String titleTip = getTitleTip();
		int titleTipLeft = CommUtil.dip2px(mContext, TITLE_TIP_MARGIN_LEFT) + titleLength;
		int titleTipTop = titleTop - CommUtil.dip2px(mContext, 2);
		if(!TextUtils.isEmpty(titleTip)){
			canvas.drawText(titleTip, titleTipLeft, titleTipTop, mTextPaintTip);
		}
		
	}
	
	/**
	 * 获取标题文字的背景ID
	 * @return
	 */
	private int getBackgroundId(){
		int backgroundResId = -1;
		switch (mTitlebarType) {
		case FlipperItemListener.TYPE_MYCONCERN:
			backgroundResId = title_imgs[0];
			break;
		case FlipperItemListener.TYPE_PERIOD_BUY:
			backgroundResId = title_imgs[1];
			break;
		case FlipperItemListener.TYPE_FINALLY_BUY:
			backgroundResId = title_imgs[2];
			break;
		default:
			backgroundResId = title_imgs[1];
			break;
		}
		return backgroundResId;
	}
	
	/**
	 * 获取标题文字
	 * @return
	 */
	private String getTitle(){
		int titleTxtId = -1;
		switch (mTitlebarType) {
		case FlipperItemListener.TYPE_MYCONCERN:
			titleTxtId = title_strings[0];
			break;
		case FlipperItemListener.TYPE_PERIOD_BUY:
			titleTxtId = title_strings[1];
			break;
		case FlipperItemListener.TYPE_FINALLY_BUY:
			titleTxtId = title_strings[2];
			break;
		default:
			titleTxtId = title_strings[1];
			break;
		}
		return mContext.getResources().getString(titleTxtId);
	}
	
	
	/**
	 * 获取标题提示文字
	 * @return
	 */
	private String getTitleTip(){
		String titleTip = null;
		switch (mTitlebarType) {
		case FlipperItemListener.TYPE_MYCONCERN:
			titleTip = mContext.getResources().getString(R.string.str_title_myconcern_tip);
			break;
		case FlipperItemListener.TYPE_PERIOD_BUY:
			titleTip = mTitltTip;
			break;
		case FlipperItemListener.TYPE_FINALLY_BUY:
			titleTip = mContext.getResources().getString(R.string.str_title_finally_buy_tip);
			break;
		}
		return titleTip;
	}
	
	

}
