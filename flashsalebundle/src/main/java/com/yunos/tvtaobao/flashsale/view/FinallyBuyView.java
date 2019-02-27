/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.adapter.GoodsAdapter;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.bo.TodayHotList;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;

import java.util.ArrayList;
import java.util.Map;

public class FinallyBuyView extends FliperItemView implements OnItemClickListener {

    private GoodsGridView mFocusFlipGridView;
    private GoodsAdapter mAdapter;
    private final static int GRIDVIEW_COLUMN_COUNT = 2;
    // private FinallyBuyCache mCache;

    public FinallyBuyView(FocusFlipperView flipper, Context context) {
        super(flipper, context);
    }


    @Override
    protected void initView() {
        // mLeftBar.setBackgroundResource(R.drawable.arrow_period_right);
        mRightBar.setVisibility(View.INVISIBLE);
        mLeftBar.setVisibility(View.VISIBLE);
        // mContentContainer.setBackgroundResource(R.drawable.finally_buy);
        // super.setBackgroundResource(R.drawable.finally_buy);

        // super.setBackgroundColor(0xFF91A836);
        super.setBackgroundDrawable(mAppContext.getResources().getDrawable(R.drawable.finally_buy_bg));

        mLeftBar.setContent(ArrowBarView.ARROW_LEFT, R.drawable.arrow_mid_22, R.string.str_period_of_buying,
                AppConfig.ARROW_TEXT_COLOR, AppConfig.ARROW_TEXT_SIZE);

        mRightBar.setContent(ArrowBarView.ARROW_RIGHT, R.drawable.arrow_mid_22, R.string.str_my_concern,
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
        mFocusFlipGridView.setAnimateWhenGainFocus(true, true, true, true);

        mFocusFlipGridView.setVerticalSpacing(mGridViewVerticalSpacing);
        mFocusFlipGridView.setHorizontalSpacing(mGridViewHorizontalSpacing);
        // mFocusFlipGridView.setStretchMode(GridView.NO_STRETCH);
        mAdapter = new GoodsAdapter(mActivityContext, mFocusFlipGridView);
        mAdapter.setPageType(FlipperItemListener.TYPE_FINALLY_BUY);
        mFocusFlipGridView.setAdapter(mAdapter);

        // mFocusFlipGridView.setFadingEdgeLength(mGridViewOffsetBottom);
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
    public void onPageWillSelected(View selectView, int selectPos, View unselectView, int unselectPos) {
        super.onPageWillSelected(selectView, selectPos, unselectView, unselectPos);

    }

    @Override
    public void onPageSelected(View selectView, int selectPos) {
        // TODO Auto-generated method stub
        super.onPageSelected(selectView, selectPos);
        requestFocusedView(mFocusFlipGridView);
    }

    @Override
    public void onPageUnselected(View unselectView, int unselectPos) {
        super.onPageUnselected(unselectView, unselectPos);
        mFocusFlipGridView.removeAllViewsInLayout();
        // mFocusFlipGridView.removeAllViews();
        mAdapter.setAdapter(null);
        mFocusFlipGridView.stopFlip();
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
        tbsClickEvent(goods, position);

        if (NetWorkUtil.isNetWorkAvailable()) {
            mFocusFlipGridView.handlerItemClick(goods);
        } else {
            if (null != mContextListener) {
                mContextListener.showNetworkErrorDialog(false);
            }
        }

    }

    private void tbsClickEvent(GoodsInfo info, int position) {
        CategoryItem qianggou = null;
        FliperItemView periodBuy = mFocusViewFlipper.getFliperItemView(FliperItemView.TYPE_PERIOD_BUY);
        if (null != periodBuy && periodBuy instanceof PeriodBuyView) {
            qianggou = ((PeriodBuyView) periodBuy).getQianggou();
        }

        if (null == info || position < 0 || null == qianggou) {
                AppDebug.i("TabGridView", "tbsClickEvent: info:" + (null != info) + " position:" + position
                        + " mQianggou: " + (null != qianggou) + " mCurItem: ");
            return;
        }

        String event = TbsUtil.getControlName(null, TbsUtil.CLICK_Remainder_P, 1 + position);
        Map<String, String> prop = TbsUtil.getTbsProperty(info, DateUtils.getTime(qianggou.getStartTime()),
                DateUtils.getTime(info.getStartTime()), DateUtils.getTime(info.getEndTime()), "past");

//        TBS.Adv.ctrlClicked(CT.Button, event, TbsUtil.getKeyValue(prop));
        prop.put("spm", SPMConfig.TAOQIANGGOU_MAIN_LAST_ITEM_P_NAME);
        Utils.utControlHit("Page_TaoQiangGou_Home", "Button-"+event, prop);
        Utils.updateNextPageProperties( SPMConfig.TAOQIANGGOU_MAIN_LAST_ITEM_P_NAME);
    }

    @Override
    public boolean OnSwitch(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            int curPos = mFocusFlipGridView.getSelectedItemPosition();
            int count = mFocusFlipGridView.getCount();

            if (CommUtil.isLastLeft(count, GRIDVIEW_COLUMN_COUNT, curPos)) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void loadingData(Object userData) {

    }

    @Override
    public boolean loadingDataError(Object userData) {
        return false;
    }

    public void loadingDataSuccess(Object userData, Object reqData) {
        super.loadingDataSuccess(userData, reqData);
        TodayHotList data = (TodayHotList) reqData;
        ArrayList<GoodsInfo> items = (null != data) ? data.getItems() : null;

        if (null != items) {
            //用于记录activity路径
            mAdapter.setAdapter(items);
        }
    }

    private RequestListener<TodayHotList> mGetTodayHotListListener = new RequestListener<TodayHotList>() {

        @Override
        public void onRequestDone(TodayHotList data, int resultCode, String msg) {
            // TODO Auto-generated method stub
            AppDebug.i(TAG,"getTodayHotList: resultCode = " + resultCode + " msg = " + msg);

            if (RequestManager.CODE_SUCCESS == resultCode) {
                mReqStateInfo.loadingDataSuccess(data);
            } else {
                mReqStateInfo.loadingDataError(resultCode);
            }

        }
    };

    public void excuteReq(Object userData) {
        AppDebug.e(TAG,"获取疯抢列表");
        mRequestManager.getTodayHotList(mGetTodayHotListListener);
    }

    @Override
    public byte getPageType() {
        // TODO Auto-generated method stub
        return FlipperItemListener.TYPE_FINALLY_BUY;
    }

    @Override
    public void onResume() {
        if (null != mFocusFlipGridView) {
            mFocusFlipGridView.forceSelectItem();
        }
    }

    @Override
    protected FocusListener getValidFocusView() {
        return mFocusFlipGridView;
    }

}
