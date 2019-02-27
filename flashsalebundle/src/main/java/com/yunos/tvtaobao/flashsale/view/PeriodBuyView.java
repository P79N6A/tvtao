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
import android.view.View;
import android.widget.Toast;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.activity.FlashSaleBaseActivity;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.CategoryList;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.listener.TabGridViewListener;
import com.yunos.tvtaobao.flashsale.listener.TimerListener;
import com.yunos.tvtaobao.flashsale.listener.TitleBarListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.timer.TimerManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.req.ReqStateInfo;

public class PeriodBuyView extends FliperItemView implements TitleBarListener,
		TimerListener {
	private static final long TIME_STAMP_A_DAY = 24 * 3600L * 1000;

	/** 时段抢购Tab目录列表 */
	private CategoryList mCategoryList;

	/** 记录当前进行中场次的数据 */
	private CategoryItem mQianggou;

	/** 时段抢购视图 */
	private TabContentView mTabContentView;

	private AppManager mAppManager;

	private TimerManager mTimerManager;

	public CategoryItem getQianggou() {
		return mQianggou;
	}

	public PeriodBuyView(FocusFlipperView flipper, Context context) {
		super(flipper, context);
	}

	@Override
	protected void initView() {
		// mContentContainer.setPadding(AppConfig.PERIOD_BUYING_OFFSET , 0,
		// AppConfig.ARROWBAR_WIDTH, 0);
		mAppManager = AppManager.getInstance(super.getContext());
		mTimerManager = mAppManager.getTimerManager();
		mLeftBar.setVisibility(View.VISIBLE);
		mRightBar.setVisibility(View.VISIBLE);
		// 红色
		// super.setBackgroundColor(0xFFDA5354);
		super.setBackgroundDrawable(mAppContext.getResources().getDrawable(
				R.drawable.period_buy_bg));

		// mDrawable =
		// super.getResources().getDrawable(R.drawable.period_buy_bg);

		mLeftBar.setContent(ArrowBarView.ARROW_LEFT, R.drawable.arrow_mid_11,
				R.string.str_my_concern, AppConfig.ARROW_TEXT_COLOR,
				AppConfig.ARROW_TEXT_SIZE);

		mRightBar.setContent(ArrowBarView.ARROW_RIGHT, R.drawable.arrow_mid_21,
				R.string.str_finally_berserk, AppConfig.ARROW_TEXT_COLOR,
				AppConfig.ARROW_TEXT_SIZE);

		mViewTitlebar.setTitleBarType(getPageType());

		mContentContainer.setPadding(AppConfig.ARROWBAR_WIDTH
				- AppConfig.TIME_AXIS_SELECT_RADIUS, 0, AppConfig.ARROWBAR_WIDTH,
				0);
		mTabContentView.setPeriodBuyView(this);

		// mTimeAxisView = (TimeAxisView)
		// mTabGridView.getTabSwitchViewListener();
	}

	@Override
	public void onAddContentView() {
		// TODO Auto-generated method stub
		// mAppManager = AppManager.getInstance(mAppContext);
		mTabContentView = new TabContentView(mActivityContext);
		mTabContentView.setTitleBarListener(this);
		mContentContainer.addView(mTabContentView);
	}

	@Override
	public ReqStateInfo onCreateReqStateInfo() {
		ReqStateInfo info = super.onCreateReqStateInfo();

		info.mTimeout = ReqStateInfo.MODE_NO_UPDATE;
		return info;
	}

	private int mRetryCount = 1;

	@Override
	public boolean loadingDataError(Object userData) {
		AppDebug.e(TAG,"-----loadingDataError");
		if (mRetryCount <= 0) {
			Toast.makeText(mAppContext, mAppContext.getText(R.string.str_req_error),
					Toast.LENGTH_LONG).show();
			FlashSaleBaseActivity baseActivity = mContextListener
					.getFlashSaleBaseActivity();
			if (null != baseActivity && !baseActivity.isFinishing()) {
				baseActivity.finish();
			}
			return true;
		}
		mRetryCount--;
		ReqStateInfo reqInfo = mReqStateInfo;
		if (null != reqInfo) {
			reqInfo.checkReq();
		}
		return true;
	}

	@Override
	public void loadingDataSuccess(Object userData, Object reqData) {
		super.loadingDataSuccess(userData, reqData);
		AppDebug.e(TAG,"-----loadingDataSuccess");
		mCategoryList = (CategoryList) reqData;

		if (mCategoryList != null) {
			CategoryItem qianggou = mCategoryList.getCurItem();

			if (null == qianggou) {
				/** 没有进行中的场次,将最后一个场次作为当前进行中场次，以便统计 */
				qianggou = mCategoryList.getLastItem();
			}
			if (null != qianggou) {
				mStartTime = DateUtils.string2Timestamp(qianggou.getStartTime());
				mEndTime = DateUtils.string2Timestamp(qianggou.getEndTime());

				// mPeriodBuyStartTime = mStartTime;
				mPeriodBuyEndTime = mEndTime;
				mQianggou = qianggou;
				setTimer(mEndTime + TIME_STAMP_A_DAY,
						DateUtils.string2Timestamp(mCategoryList.getSysTime()),
						mCategoryList.getLocalRef());
			}
			mTabContentView.setObject(mCategoryList);
		} else {
			/** 数据获取失败，下次需要继续获取 */
			loadingDataError(userData);
		}
	}

	@Override
	public void loadingData(Object userData) {
		// TODO Auto-generated method stub
	}

	private RequestListener<CategoryList> mGetCategoryListListener = new RequestListener<CategoryList>() {
		@Override
		public void onRequestDone(CategoryList data, int resultCode, String msg) {
			if (RequestManager.CODE_SUCCESS == resultCode && null != data) {
				mReqStateInfo.loadingDataSuccess(data);
			} else {
				mReqStateInfo.loadingDataError(resultCode);
			}
		}
	};

	@Override
	public void excuteReq(Object userData) {
		AppDebug.e(TAG,"获取场次抢购列表");
		mRequestManager.getCategoryList(mGetCategoryListListener);
	}

	@Override
	public void onPageSelected(View selectView, int selectPos) {
		// TODO Auto-generated method stub
		super.onPageSelected(selectView, selectPos);
		// focus current grid view
		TabGridViewListener viewListener = mTabContentView.getCurView();
		if(viewListener != null && viewListener.getView() != null){
		    AppDebug.i(TAG, "onPageSelected viewListener="+viewListener);
		    requestFocusedView(viewListener.getView());
		}
		mTabContentView.onSelect();
	}

	@Override
	public void onPageUnselected(View unselectView, int unselectPos) {
		// TODO Auto-generated method stub
		super.onPageUnselected(unselectView, unselectPos);
		mTabContentView.onUnselect();
	}

	@Override
	public boolean OnSwitch(int keyCode) {
		if (null == mCategoryList) {
			return false;
		}
		return mTabContentView.OnSwitch(keyCode);
	}

	@Override
	public byte getPageType() {
		// TODO Auto-generated method stub
		return FlipperItemListener.TYPE_PERIOD_BUY;
	}

	@Override
	public void onResume() {
		super.onResume();
		TabGridView v = (TabGridView) mTabContentView.getCurView();
		if (null != v) {
			v.forceSelectItem();
		}
	}

	/** 记录当前页面的时间 */
	/** 如果都是过去场的话，那就不需要检测 */
	private boolean mStatusCheck = true;
	// private long mPeriodBuyStartTime;
	private long mPeriodBuyEndTime;
	private long mStartTime;
	private long mEndTime;
	// private byte mStatus = CategoryItem.STATUS_TYPE_CURRENT;
	private byte mTitleStatus = CategoryItem.STATUS_TYPE_CURRENT;

	private void updateTitlebar(long curTime) {
		/** 通过时间来判断当前场次的状态 */
		long remainingTime = 0;
		int resId = 0;

		if (curTime > mStartTime && curTime < mEndTime) {
			/** 表示当前场次处于进行中 */
			remainingTime = mEndTime - curTime;
			resId = R.string.str_title_current_tip;
			mTitleStatus = CategoryItem.STATUS_TYPE_CURRENT;
		} else if (curTime < mStartTime) {
			remainingTime = mStartTime - curTime;
			resId = R.string.str_title_future_tip;
			mTitleStatus = CategoryItem.STATUS_TYPE_FUTRUE;
		}

		if (remainingTime > 0) {
			StringBuilder sb = new StringBuilder(mAppContext.getResources()
					.getString(resId));

			sb.append("  ");
			sb.append(DateUtils.millisecond2String(remainingTime));
			mViewTitlebar.setTitltTip(sb.toString());
			mTitleStatus = CategoryItem.STATUS_TYPE_CURRENT;
		} else {
			/** 过去场次 */
			if (mTitleStatus != CategoryItem.STATUS_TYPE_PAST) {
				mTitleStatus = CategoryItem.STATUS_TYPE_PAST;
				String titleTip = mAppContext.getResources().getString(
						R.string.str_title_past_tip);
				mViewTitlebar.setTitltTip(titleTip);
			}
		}
	}

	@Override
	public void changeTitleBar(String status, String startTime, String endTime) {
		// mStatus = CommUtil.getStatusType(status);
		mStartTime = DateUtils.string2Timestamp(startTime);
		mEndTime = DateUtils.string2Timestamp(endTime);
		updateTitlebar(mTimerManager.getCurTime());
	}

	/** 当前正在进行中的场次 */
	// private long mRefTime;
	private int mTimerId = -1;

	private void cancelTimer() {
		if (mTimerId >= 0) {
			mTimerManager.cancelTimer(mTimerId);
			mTimerId = -1;
		}
	}

	private void setTimer(long reminder, long serverRef, long localRef) {
		cancelTimer();
		mTimerManager.setRef(serverRef, localRef);

			AppDebug.i("PeriodBuyView", DateUtils.timestamp2String(reminder));
		mTimerId = mTimerManager.createTimer(reminder, this, null);
	}

	@Override
	public void onTimer(int id, long remainingTime, Object userData) {
		// TODO Auto-generated method stub
		if (id != mTimerId) {
			return;
		}
		long curTime = mTimerManager.getCurTime();

		updateTitlebar(curTime);

		if (mStatusCheck) {
			/** 准点定时到，需要强制刷新数据 */
			if (curTime >= mPeriodBuyEndTime) {
				/** 需要切换状态，时间轴需要更新 */
				CategoryItem item = mQianggou;
				if (null != item && null != mCategoryList) {
					long time = DateUtils.string2Timestamp(item.getEndTime());
					/** 容错 */
					if (curTime < time) {
							AppDebug.i(TAG, "cur end time < end time");
						return;
					}
					item.setStatus(CategoryItem.STATUS_PAST);
					item = mCategoryList.getCurItem();
					if (null != item) {
						/** 切换进行中的场次 */
						mPeriodBuyEndTime = DateUtils.string2Timestamp(item
								.getEndTime());
						mQianggou = item;
						mTabContentView.setTimeAxisKey(item.getId());
							AppDebug.i(TAG, "switch time axis");
					} else {
						/** 如果当前没有场次，说明所有的场次都抢结束 */
						// mStatus = CategoryItem.STATUS_TYPE_PAST;
						mTabContentView.setTimeAxisKey(null);
						/** 如果没有进行中，那统计的时候就以最后一个场次作为进行中的数据 */
						// mQianggou = null;
						cancelTimer();
						mStatusCheck = false;
							AppDebug.i(TAG, "all end");
					}
					/** 强制刷数据 */
					updateGridview();

				} else {
					/** 删除定时器 */
					cancelTimer();
					mStatusCheck = false;
				}
			}
		}

	}

	private void updateGridview() {
		AppDebug.e(TAG,"-----updateGridview");
		/** 清除数据和刷新数据 */
		PeriodBuyView.this.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!avaibleUpdate()) {
					return;
				}
				mTabContentView.onUnselect();
				/** 如果当前焦点在gridview上 */
				FocusPositionManager focusManager = getFocusPositionManager();
				View v = focusManager.getFocused();

				if (null != v && v instanceof TabGridView) {
					View tabView = mTabContentView.getTabSwitchViewListener()
							.getView();

					/** 这段代码些的好纠结啊! */
					tabView.setFocusable(false);
					v.setFocusable(false);
					focusManager.resetFocused();
					v.setFocusable(true);
					focusManager.resetFocused();
					tabView.setFocusable(true);
				}
				mTabContentView.onSelect();

					AppDebug.i(TAG, "update data");
			}
		}, 200);
	}

	@Override
	public void onEndTimer(int id, Object userData) {
		// TODO Auto-generated method stub
		if (id != mTimerId) {
			return;
		}
			AppDebug.i("PeriodBuyView", "onEndTimer id: " + id);
		mTimerId = -1;
		if (!mIsDestroy) {
			/** 循环定时 */
			long reminder = mTimerManager.getCurTime() + TIME_STAMP_A_DAY;

			mTimerId = mTimerManager.createTimer(reminder, this, null);
			AppDebug.i("PeriodBuyView", "need start a timer d: " + id);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cancelTimer();

		mTabContentView.onDestroy();
	}

    @Override
    protected FocusListener getValidFocusView() {
        if(mTabContentView != null){
            TabGridViewListener viewListener = mTabContentView.getCurView();
            if(viewListener != null && viewListener.getView() instanceof FocusListener){
                return (FocusListener)viewListener.getView();
            }
        }
        return null;
    }

}
