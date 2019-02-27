package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.EntryInfo;
import com.yunos.tvtaobao.flashsale.listener.TimerListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.timer.TimerManager;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.req.ReqProcListener;
import com.yunos.tvtaobao.flashsale.utils.req.ReqStateInfo;

public class FlashsaleEntryView extends FocusLinearLayout implements
		ReqProcListener, TimerListener {
	private TextView mTime;
	private TextView mDesc;
	private ImageView mSockImage;
	private ReqStateInfo mReqStateInfo;
	private ImageLoaderManager mImageLoaderManager;
	private DisplayImageOptions mOptions;
	private RequestManager mRequestManager;
	private AppManager mAppManager;
	private TimerManager mTimerManager;

	private int mTimerId = -1;
	private long mReminderTime;

	public FlashsaleEntryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FlashsaleEntryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FlashsaleEntryView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mTime = (TextView) super.findViewById(R.id.fs_tv_time);
		mDesc = (TextView) super.findViewById(R.id.fs_tv_desc);
		mSockImage = (ImageView) super.findViewById(R.id.fs_iv_stock_image);
		mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(super
				.getContext());

		mAppManager = AppManager.getInstance(super.getContext());

		mRequestManager = mAppManager.getRequestManager();
		mTimerManager = mAppManager.getTimerManager();

		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.common_default)
				.showImageOnFail(R.drawable.common_default)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
		mOptions = builder.build();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		/** 请求数据 */
		if (null == mReqStateInfo) {
			mReqStateInfo = new ReqStateInfo(this);
		}
		mReqStateInfo.checkReq();
		startTimer();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		/** 防止不断刷新 */
		if (View.VISIBLE == visibility) {
			/** 检查是否需要数据刷新 */
			mReqStateInfo.checkReq();
			// startTimer();
		} else {
			// cancelTimer();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		cancelTimer();
	}

	@Override
	public void loadingData(Object userData) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean loadingDataError(Object userData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadingDataSuccess(Object userData, Object reqData) {
		// TODO Auto-generated method stub
		EntryInfo info = (EntryInfo) reqData;
			AppDebug.i("FlashsaleEntryView",
					"entry data: " + "futrue time: " + info.getFutureTime()
							+ "item ID:" + info.getItemId() + "local ref"
							+ info.getLocalRef() + "time:" + info.getSysTime());
		mTimerManager.setRef(DateUtils.string2Timestamp(info.getSysTime()),
				info.getLocalRef());

		mTime.setVisibility(View.VISIBLE);
		mDesc.setVisibility(View.VISIBLE);
		mDesc.setText(info.getName());
		mImageLoaderManager.displayImage(info.getItemPic(), mSockImage,
				mOptions);

		mReminderTime = DateUtils.string2Timestamp(info.getFutureTime());
		startTimer();

	}

	private RequestListener<EntryInfo> mGetEntryInfoListener= new RequestListener<EntryInfo>() {
		@Override
		public void onRequestDone(EntryInfo data, int resultCode, String msg) {
			if (RequestManager.CODE_SUCCESS == resultCode && null != data) {
				mReqStateInfo.loadingDataSuccess(data);
			} else {
				mReqStateInfo.loadingDataError(resultCode);
			}
			if( this == mGetEntryInfoListener){
				mGetEntryInfoListener = null;
			}					
		} 
	};


	@Override
	public void excuteReq(Object userData) {
		// TODO Auto-generated method stub
		mRequestManager.getEntryInfo(mGetEntryInfoListener);
			AppDebug.i("FlashsaleEntryView", "excuteReq");
	}

	@Override
	public boolean avaibleUpdate() {
		// TODO Auto-generated method stub
		return super.isShown();
	}

	public void cancelTimer() {
		if (mTimerId != -1) {
			mTimerManager.cancelTimer(mTimerId);
			mTimerId = -1;
		}
	}

	private void startTimer() {
		cancelTimer();
		if (mReminderTime > 0) {
			// mReminderTime = mTimerManager.getCurTime() + 5000;
			mTimerId = mTimerManager.createTimer(mReminderTime, this, null);

			// mTimerId = mTimerManager.createTimer(mTimerManager.getCurTime() +
			// 5000, this, null);
		}
	}

	@Override
	public void onTimer(int id, long remainingTime, Object userData) {
		// TODO Auto-generated method stub
		if (id == mTimerId) {
			/** 刷新界面 */
			mTime.setText(DateUtils.millisecond2String(remainingTime));
		}
	}

	@Override
	public void onEndTimer(int id, Object userData) {
		// TODO Auto-generated method stub
		if (id == mTimerId) {
			/** 设置时间 */
			mTime.setText("00:00:00");
			mReqStateInfo.setStatus(ReqStateInfo.STATE_INIT);
			mReqStateInfo.checkReq();
		}
	}
}
