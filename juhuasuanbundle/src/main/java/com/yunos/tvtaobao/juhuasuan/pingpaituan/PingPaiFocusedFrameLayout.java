package com.yunos.tvtaobao.juhuasuan.pingpaituan;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.activity.BrandHomeActivity.Item_Info;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.juhuasuan.widget.FocusParams;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class PingPaiFocusedFrameLayout extends FocusedFrameLayout {

    private int mPageIndex = 0;
    private PingPaiPagerAdapter mPingPaiPagerAdapter = null;
    private Context mContext = null;

    private Activity mActivity = null;

    private OnPingpaiItemHandleListener onitemHandleListener = null;
    private List<PingPaiItemRelativeLayout> mItemViewArray = null;
    public ArrayList<BrandMO> mListData = null;

    private String mTabelkey = null;
    private boolean mFource = false;
    private int mPageCount = 4;

    public PingPaiFocusedFrameLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        // TODO Auto-generated constructor stub

        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public PingPaiFocusedFrameLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        // TODO Auto-generated constructor stub
        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public PingPaiFocusedFrameLayout(Context contxt) {
        super(contxt);
        // TODO Auto-generated constructor stub
        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public PingPaiFocusedFrameLayout(Context contxt, int pageCount) {
        super(contxt);
        // TODO Auto-generated constructor stub
        mPageCount = pageCount;
        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public int getPageCount() {
        return mPageCount;
    }

    private void onInitClassficationFocusedRelativeLayout(Context contxt) {
        mContext = contxt;

        mItemViewArray = new ArrayList<PingPaiItemRelativeLayout>();
        mItemViewArray.clear();

        createPositionManager(initFocusParams());
        //        setViewRight(0);
        //        setViewLeft(1);
        setHorizontalMode(FocusedFrameLayout.HORIZONTAL_FULL);
        setAutoSearchFocus(true);
        //        setOnItemSelectedListener(null);
        //        setFocusStateListener(null); 

        setFocusable(true);

        onSetLayout();

    }

    protected FocusParams initFocusParams() {
        //logd("getFocusParams");
        FocusParams params = new FocusParams();
        // params.setScaleMode(FocusedBasePositionManager.SCALED_FIXED_X);
        params.setFocusMode(FocusedBasePositionManager.FOCUS_ASYNC_DRAW);
        //        params.setItemScaleFixedX(10);
        params.setItemScaleValue(1.06f, 1.06f);
        params.setFrameRate(3, 3);
        //        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.goodlist_item_selector));
        //        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.goodlist_item_selector));

        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.jhs_box_right));
        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.jhs_box_right));

        return params;
    }

    public void onSetPagerAdapter(PingPaiPagerAdapter classificationPagerAdapter) {
        mPingPaiPagerAdapter = classificationPagerAdapter;

        onSetEachItemOfPagerAdapter(mPingPaiPagerAdapter);
    }

    public void onSetPageIndex(int pageindex) {

        mPageIndex = pageindex;

        onSetEachItemOfPageIndex(mPageIndex);
    }

    public void onSetOnItemHandleListener(OnPingpaiItemHandleListener l) {
        onitemHandleListener = l;
    }

    public void onSetActivity(Activity activity) {
        mActivity = activity;
    }

    public void onSetLayout() {

        int mItemIndex = 0;
        int left, top;

        for (int index = 0; index < mPageCount; index++) {

            FrameLayout.LayoutParams lpTop = new FrameLayout.LayoutParams(PingPaiDimension.ITEM_WIDTH,
                    PingPaiDimension.ITEM_HEIGHT);

            if (index < 2) {
                lpTop.setMargins(PingPaiDimension.PAGE_MARGIN_LEFT + index
                        * (PingPaiDimension.ITEM_WIDTH + PingPaiDimension.ITEM_SPACE_X),
                        PingPaiDimension.PAGE_MARGIN_TOP, 0, 0);
            } else {
                lpTop.setMargins(
                        PingPaiDimension.PAGE_MARGIN_LEFT + (index - 2)
                                * (PingPaiDimension.ITEM_WIDTH + PingPaiDimension.ITEM_SPACE_X),
                        PingPaiDimension.PAGE_MARGIN_TOP + PingPaiDimension.ITEM_HEIGHT + PingPaiDimension.ITEM_SPACE_Y,
                        0, 0);
            }

            final PingPaiItemRelativeLayout itemFrameLayoutTop = new PingPaiItemRelativeLayout(mContext);

            itemFrameLayoutTop.onSetItemIndex(mItemIndex);

            itemFrameLayoutTop.onSetDefultBitmap(PingPaiTuanImageHandleUint.mDefaultBitmap);

            itemFrameLayoutTop.onSetActivity(mActivity);

            itemFrameLayoutTop.onSetPagerAdapter(mPingPaiPagerAdapter);

            itemFrameLayoutTop.setFocusable(true);

            //            if(index > 2)
            //            {
            //                itemFrameLayoutTop.setFocusable(false);
            //                itemFrameLayoutTop.setVisibility(View.GONE);
            //            }
            //            else
            //            { 
            //               itemFrameLayoutTop.setFocusable(true);
            //            }

            itemFrameLayoutTop.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub 
                    int itemIndex = itemFrameLayoutTop.onGetItemIndex();

                    if (onitemHandleListener != null) {
                        onitemHandleListener.onClickPageItem(PingPaiFocusedFrameLayout.this, itemFrameLayoutTop,
                                mTabelkey, mPageIndex, itemIndex);
                    }

                }

            });

            addView(itemFrameLayoutTop, lpTop);

            mItemViewArray.add(itemFrameLayoutTop);

            mItemIndex++;
        }

    }

    public void onSetSelectView() {
        mFource = true;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);

        if (mFource) {
            mFource = false;
            requestFocus();
        }

    }

    public void onRecycleRes() {

        if (mItemViewArray == null)
            return;
        int count = mItemViewArray.size();

        for (int index = 0; index < count; index++) {

            PingPaiItemRelativeLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;

            refIv.onRecycleDisplayBitmap(PingPaiTuanImageHandleUint.mDefaultBitmap);
            refIv.onRecycleLogoBitmap(PingPaiTuanImageHandleUint.mRecycleBitmap);
        }

    }

    /**
     * 刷新数据，在刷新之前，判断一下，当前是否满屏显示？
     * @param arraylist
     * @param position
     */
    public void onRefreshItem(CountList<BrandMO> arraylist, int position) {

        if (mItemViewArray == null)
            return;

        if (arraylist == null)
            return;

        int positionStart = position;

        int count = mItemViewArray.size();

        int needSize = (position + 1) * Item_Info.PAGE_COUNT;
        int currentSize = arraylist.size();

        if (needSize > currentSize) {
            count = currentSize + Item_Info.PAGE_COUNT - needSize;
        }

        int index = 0;

        // 要显示的条目
        for (index = 0; index < count; index++) {

            positionStart = position * Item_Info.PAGE_COUNT + index;

            if (positionStart >= arraylist.size()) {
                return;
            }

            PingPaiItemRelativeLayout refIv = mItemViewArray.get(index);

            if (refIv == null)
                continue;

            refIv.setVisibility(View.VISIBLE);

            refIv.onRefreshItemInfo(arraylist.get(positionStart), position);

        }

        // 隐藏多余的条目VIEW
        count = mItemViewArray.size();

        for (int iex = index; iex < count; iex++) {

            PingPaiItemRelativeLayout refIv = mItemViewArray.get(iex);
            if (refIv == null)
                continue;

            refIv.setVisibility(View.GONE);

        }

    }

    /**
     * 设置每一页的值
     * @param pageIndex
     */
    public void onSetEachItemOfPageIndex(int pageIndex) {

        if (mItemViewArray == null)
            return;

        int count = mItemViewArray.size();

        for (int index = 0; index < count; index++) {

            PingPaiItemRelativeLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;

            refIv.onSetPageIndex(pageIndex);
        }

    }

    /**
     * 设置每一页的适配器
     */
    public void onSetEachItemOfPagerAdapter(PingPaiPagerAdapter adapter) {

        if (mItemViewArray == null)
            return;

        int count = mItemViewArray.size();

        for (int index = 0; index < count; index++) {

            PingPaiItemRelativeLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;

            refIv.onSetPagerAdapter(adapter);
        }

    }

    /**
     * 清除资源，以及变量清理，和释放
     */
    public void ClearAndDestroy() {

        onClearSelectedItem();

        if (mItemViewArray != null) {
            int count = mItemViewArray.size();
            for (int index = 0; index < count; index++) {

                PingPaiItemRelativeLayout refIv = mItemViewArray.get(index);
                if (refIv == null)
                    continue;

                refIv.onRecycleDisplayBitmap(PingPaiTuanImageHandleUint.mRecycleBitmap);
                refIv.onRecycleLogoBitmap(PingPaiTuanImageHandleUint.mRecycleBitmap);
            }

            mItemViewArray.clear();
            mItemViewArray = null;
        }

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub

        AppDebug.i(TAG, "gainFocus = " + gainFocus + ";--- >direction = " + direction);

        if (mPingPaiPagerAdapter != null) {
            mPingPaiPagerAdapter.upDateFoucs(gainFocus, mPageIndex);
        }

        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * 获取当前选中的子元素
     * @return 当前选中的子元素
     */
    @Override
    public View getSelectedView() {
        int indexOfView = mIndex;
        View selectedView = getSelectedView(indexOfView);
        return selectedView;
    }

    private View getSelectedView(int index) {
        mIndex = index;
        View selectedView = getChildAt(index);
        if (selectedView == null) {
            index = index - 2;
            if (index >= 0) {
                selectedView = getSelectedView(index);
            }
        }
        return selectedView;
    }

}
