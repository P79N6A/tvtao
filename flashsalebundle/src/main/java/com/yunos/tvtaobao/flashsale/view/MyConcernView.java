/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.adapter.GoodsAdapter;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyConcernView extends FliperItemView implements OnItemClickListener {

    private GoodsGridView mFocusFlipGridView;
    private GoodsAdapter mAdapter;
    private final static int GRIDVIEW_COLUMN_COUNT = 2;
    private Context mActivityContext;

    public MyConcernView(FocusFlipperView flipper, Context context) {
        super(flipper, context);
        mActivityContext = context;
    }

    @Override
    protected void initView() {
        mLeftBar.setVisibility(View.INVISIBLE);
        mRightBar.setVisibility(View.VISIBLE);
        super.setBackgroundDrawable(mAppContext.getResources().getDrawable(R.drawable.myconcern_bg));

        mLeftBar.setContent(ArrowBarView.ARROW_LEFT, R.drawable.arrow_mid_12, 0, AppConfig.ARROW_TEXT_COLOR,
                AppConfig.ARROW_TEXT_SIZE);

        mRightBar.setContent(ArrowBarView.ARROW_RIGHT, R.drawable.arrow_mid_12, R.string.str_period_of_buying,
                AppConfig.ARROW_TEXT_COLOR, AppConfig.ARROW_TEXT_SIZE);

    }

    @Override
    public void onAddContentView() {
        // TODO Auto-generated method stub
        mFocusFlipGridView = new GoodsGridView(super.getContext());
        mFocusFlipGridView.setFocusable(true);
        int width = mGridViewItemWidth * GRIDVIEW_COLUMN_COUNT + (GRIDVIEW_COLUMN_COUNT - 1)
                * mGridViewHorizontalSpacing + 2 * AppConfig.GRIDVIEW_OFFSET_LEFT;

        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER;
        mContentContainer.addView(mFocusFlipGridView, param);

        mFocusFlipGridView.setNumColumns(GRIDVIEW_COLUMN_COUNT);
        mFocusFlipGridView.setFlipScrollFrameCount(5);
        mFocusFlipGridView.setNeedAutoSearchFocused(false);
        mFocusFlipGridView.setAnimateWhenGainFocus(false, true, true, true);

        mFocusFlipGridView.setVerticalSpacing(mGridViewVerticalSpacing);
        mFocusFlipGridView.setHorizontalSpacing(mGridViewHorizontalSpacing);
        mAdapter = new GoodsAdapter(mActivityContext, mFocusFlipGridView);
        mAdapter.setPageType(FlipperItemListener.TYPE_MYCONCERN);
        mFocusFlipGridView.setAdapter(mAdapter);

        // mFocusFlipGridView.setFadingEdgeLength(mGridViewOffsetBottom);
        // mFocusFlipGridView.setPadding(AppConfig.GRIDVIEW_OFFSET_LEFT,
        // 0/*mGridViewOffsetBottom*/,
        // AppConfig.GRIDVIEW_OFFSET_LEFT, mGridViewOffsetBottom);
        mFocusFlipGridView.setPadding(AppConfig.GRIDVIEW_OFFSET_LEFT, AppConfig.GRIDVIEW_OFFSET_TOP,
                AppConfig.GRIDVIEW_OFFSET_LEFT, AppConfig.GRIDVIEW_OFFSET_BOTTOM);

        mFocusFlipGridView.setOnItemClickListener(this);
        mFocusFlipGridView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                showMaskView(mFocusFlipGridView, mFocusFlipGridView.getPrivateFlag());
            }
        });
        mFocusFlipGridView.setContextListener(mReqStateInfo.getContextListener());

    }

    @Override
    public void onPageSelected(View selectView, int selectPos) {
        // TODO Auto-generated method stub
        AppManager appManager = AppManager.getInstance(mAppContext);
        if (appManager.getTimerManager().hasReference()) {
            MyConcernCache cache = appManager.getMyConcernCache();
            cache.autoClear();
        }
        super.onPageSelected(selectView, selectPos);
        requestFocusedView(mFocusFlipGridView);
    }

    @Override
    public void onPageUnselected(View unselectView, int unselectPos) {
        // TODO Auto-generated method stub
        super.onPageUnselected(unselectView, unselectPos);
        mFocusFlipGridView.removeAllViewsInLayout();
        // mFocusFlipGridView.removeAllViews();
        mAdapter.setAdapter(null);
        mFocusFlipGridView.stopFlip();
    }

    private void removeSelf() {
        View v = (View) this.getParent();
        if (v != null && v instanceof FocusFlipperView) {
            this.setVisibility(View.GONE);
            ((FocusFlipperView) v).removeViewAt(0);
            FocusPositionManager focusMange = getFocusPositionManager();
            if (null != focusMange) {
                focusMange.resetFocused();
            }
        }
    }

    @Override
    public boolean OnSwitch(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int curPos = mFocusFlipGridView.getSelectedItemPosition();
            int count = mFocusFlipGridView.getCount();

            if (CommUtil.isLastRight(count, GRIDVIEW_COLUMN_COUNT, curPos)) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public byte getPageType() {
        // TODO Auto-generated method stub
        return FlipperItemListener.TYPE_MYCONCERN;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        int size = mAdapter.getCount();
        GoodsInfo goods = null;
        if (position < size) {
            goods = (GoodsInfo) mAdapter.getItem(position);
        }
        if (null == goods) {
            return;
        }
        /** 埋点信息 */
        tbsClickEvent(goods, position);

        if (NetWorkUtil.isNetWorkAvailable()) {
            ((GoodsGridView) mFocusFlipGridView).handlerItemClick(goods);
        } else {
            if (null != mContextListener) {
                mContextListener.showNetworkErrorDialog(false);
            }
        }
    }

    private void tbsClickEvent(GoodsInfo info, int position) {
        String event = TbsUtil.getControlName(null, TbsUtil.CLICK_Favorites_P, 1 + position);
        Map<String, String> prop = TbsUtil.getGoodsInfoProperty(info);
        String start = info.getStartTime();
        if (!TextUtils.isEmpty(start)) {
            prop.put("begin_time", DateUtils.getTime(start));

            long time = DateUtils.string2Timestamp(start);
            long curTime = AppManager.getInstance(super.getContext()).getTimerManager().getCurTime();
            if (curTime < time) {
                prop.put("duration", String.valueOf(time - curTime));
            } else {
                prop.put("duration", "0");
            }
        }
        prop.put("favorites_amount", String.valueOf(info.getItemViewerNum()));
        TBS.Adv.ctrlClicked(CT.Button, event, TbsUtil.getKeyValue(prop));
    }

    @Override
    public void loadingData(Object userData) {

    }

    @Override
    public boolean loadingDataError(Object userData) {
        return false;
    }

    @Override
    public void loadingDataSuccess(Object userData, Object reqData) {
        super.loadingDataSuccess(userData, reqData);
        @SuppressWarnings("unchecked")
        List<GoodsInfo> data = (List<GoodsInfo>) reqData;

        MultReq req = mMultReq;
        if (null != req) {
            mMultReq.cancel();
            mMultReq = null;
        }

        if (data != null) {
            AppDebug.d(TAG, "data.size = " + data.size());
            mAdapter.setAdapter(data);
            mFocusPositionManager.resetFocused();
        }
    }

    @Override
    public void excuteReq(Object userData) {
        mMyconcernList = AppManager.getInstance(mAppContext).getMyConcernCache().getMyconcerList(mMyconcernList);
        if (mMyconcernList.size() <= 0) {
            removeSelf();
            return;
        }
        MultReq multReq = mMultReq;
        if (null != multReq) {
            multReq.cancel();
        }
        mMultReq = new MultReq(mMyconcernList);
        mMultReq.excuteReq();
    }

    private List<MyConcernCache.MyconcernInfo> mMyconcernList;
    private MultReq mMultReq;

    private class MultReq {

        private final int REQ_MAX_COUNT = 20;

        private boolean mCancel = false;
        final private List<GoodsInfo> mData = new ArrayList<GoodsInfo>();
        private String[] mReqParam;
        private int mCurCount = 0;

        public MultReq(List<MyConcernCache.MyconcernInfo> concernList) {
            int size = concernList.size();
            AppDebug.d(TAG, "size = " + size);
            if (size > 0) {
                mReqParam = new String[size];
                for (int index = 0; index < size; index++) {
                    mReqParam[index] = concernList.get(index).mItemId;
                }
            }
        }

        public void cancel() {
            mCancel = true;
        }

        public void excuteReq() {
            if (null == mReqParam || mReqParam.length <= mCurCount) {
                mReqStateInfo.loadingDataSuccess(mData);
                AppDebug.i(TAG, "excuteReq: is null or success");
                return;
            }

            StringBuilder sb = new StringBuilder();
            boolean sperator = false;
            int count = 0;
            for (; mCurCount < mReqParam.length; mCurCount++) {
                if (null != mReqParam[mCurCount]) {
                    if (sperator) {
                        sb.append(",");
                    }
                    sb.append(mReqParam[mCurCount]);
                }
                count++;
                if (count >= REQ_MAX_COUNT) {
                    mCurCount++;
                    break;
                }
                sperator = true;
            }
                AppDebug.i(TAG, "req data: " + sb.toString());
            mRequestManager.getStockByBatchId(sb.toString(), mGetStockByBatchId);
        }

        private RequestListener<List<GoodsInfo>> mGetStockByBatchId = new RequestListener<List<GoodsInfo>>() {

            @Override
            public void onRequestDone(List<GoodsInfo> data, int resultCode, String msg) {
                if (RequestManager.CODE_SUCCESS == resultCode) {
                    if (mCancel) {
                        return;
                    }
                        AppDebug.d(TAG,"data is null = " + (data == null));
                    if (null != data) {
                        mData.addAll(data);
                    }
                    /** 还需要继续请求 */
                    excuteReq();
                } else {
                    mReqStateInfo.loadingDataError(resultCode);
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        //移除已经取消的设置提醒
        if (mMyConcernCache.size() > 0) {
            int focusPosition = mFocusFlipGridView.getSelectedItemPosition();
            if (focusPosition < 0) {
                return;
            }
            GoodsInfo goodInfo = (GoodsInfo) mAdapter.getItem(focusPosition);
            if (null != goodInfo) {
                if (!mMyConcernCache.hasReminder(goodInfo.getSeckillId())) {
                    /** 当前焦点处被删除 */
                    List<GoodsInfo> list = mAdapter.getData();
                    if (null != list && list.size() > focusPosition) {
                        list.remove(focusPosition);
                        mAdapter.notifyDataSetInvalidated();
                        mFocusPositionManager.resetFocused();
                        return;
                    }
                }
            }
        }

        if (null != mFocusFlipGridView) {
            mFocusFlipGridView.forceSelectItem();
        }
    }

    @Override
    protected FocusListener getValidFocusView() {
        return mFocusFlipGridView;
    }
}
