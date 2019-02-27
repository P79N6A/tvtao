package com.yunos.tvtaobao.juhuasuan.classification;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.widget.FocusParams;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ClassficationFocusedRelativeLayout extends FocusedFrameLayout {

    private int mPageIndex = 0;
    private ClassificationPagerAdapter mClassificationPagerAdapter = null;
    private Context mContext = null;
    private Bitmap mDefaultBitmap = null;
    private Bitmap mRecycleBitmap = null;

    private onItemHandleListener onitemHandleListener = null;
    private List<ItemFrameLayout> mItemViewArray = null;

    private String mTabelkey = null;
    private boolean mFource = false;

    public ClassficationFocusedRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        // TODO Auto-generated constructor stub

        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public ClassficationFocusedRelativeLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        // TODO Auto-generated constructor stub
        onInitClassficationFocusedRelativeLayout(contxt);
    }

    public ClassficationFocusedRelativeLayout(Context contxt) {
        super(contxt);
        // TODO Auto-generated constructor stub
        onInitClassficationFocusedRelativeLayout(contxt);
    }

    private void onInitClassficationFocusedRelativeLayout(Context contxt) {
        mContext = contxt;

        mRecycleBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        mDefaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jhs_item_default_image);

        mItemViewArray = new ArrayList<ItemFrameLayout>();
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
        params.setItemScaleFixedX(10);
        params.setItemScaleValue(1.16f, 1.16f);
        params.setFrameRate(3, 3);
        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.jhs_goodlist_item_selector));
        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.jhs_goodlist_item_selector));

        //                params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.classfication_focuse));
        //                params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.classfication_focuse));

        //      params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.box_right));
        //      params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.box_right));

        return params;
    }

    public void onSetPagerAdapter(ClassificationPagerAdapter classificationPagerAdapter) {
        mClassificationPagerAdapter = classificationPagerAdapter;
    }

    public void onSetPageIndex(int pageindex) {

        mPageIndex = pageindex;

        onSetEachItemOfPageIndex(mPageIndex);
    }

    public void onSetOnItemHandleListener(onItemHandleListener l) {
        onitemHandleListener = l;
    }

    public void onSetLayout() {

        int mItemIndex = 0;

        for (int index = 0; index < 2; index++) {
            FrameLayout.LayoutParams lpTop = new FrameLayout.LayoutParams(VisualMarkConfig.ITEM_BIG_WIDTH,
                    VisualMarkConfig.ITEM_BIG_HEIGHT);
            lpTop.setMargins(VisualMarkConfig.PAGE_VIEW_MARGIN_LEFT + index
                    * (VisualMarkConfig.ITEM_BIG_WIDTH + VisualMarkConfig.ITEM_SPACE), VisualMarkConfig.ITEM_SHADOW, 0,
                    0);

            final ItemFrameLayout itemFrameLayoutTop = new ItemFrameLayout(mContext);
            itemFrameLayoutTop.onInitItemFrameLayout(VisualMarkConfig.ITEM_TYPE_BIG);

            itemFrameLayoutTop.onSetItemIndex(mItemIndex);
            itemFrameLayoutTop.onSetItemSize(VisualMarkConfig.ITEM_BIG_WIDTH, VisualMarkConfig.ITEM_BIG_HEIGHT);

            itemFrameLayoutTop.onSetDisplayImageBitmap(mDefaultBitmap);

            //            itemFrameLayoutTop.onSetTitle("itemFrameLayoutTop -- > " + "mPageIndex = " + mPageIndex + ", index  = " + index);

            itemFrameLayoutTop.setFocusable(true);

            itemFrameLayoutTop.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub 
                    itemFrameLayoutTop.onShowTitle(hasFocus);
                }
            });

            itemFrameLayoutTop.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub 
                    int itemIndex = itemFrameLayoutTop.onGetItemIndex();

                    if (onitemHandleListener != null) {
                        onitemHandleListener.onClickPageItem(ClassficationFocusedRelativeLayout.this,
                                itemFrameLayoutTop, mTabelkey, mPageIndex, itemIndex);
                    }

                }

            });

            addView(itemFrameLayoutTop, lpTop);

            mItemViewArray.add(itemFrameLayoutTop);

            mItemIndex++;
        }

        for (int index = 0; index < 3; index++) {
            FrameLayout.LayoutParams lpButtom = new FrameLayout.LayoutParams(VisualMarkConfig.ITEM_SMALL_WIDTH,
                    VisualMarkConfig.ITEM_SMALL_HEIGHT);
            lpButtom.setMargins(VisualMarkConfig.PAGE_VIEW_MARGIN_LEFT + index
                    * (VisualMarkConfig.ITEM_SMALL_WIDTH + VisualMarkConfig.ITEM_SPACE), VisualMarkConfig.ITEM_SHADOW
                    + VisualMarkConfig.ITEM_BIG_HEIGHT + VisualMarkConfig.ITEM_SPACE, 0, 0);

            final ItemFrameLayout itemFrameLayoutButtom = new ItemFrameLayout(mContext);
            itemFrameLayoutButtom.onInitItemFrameLayout(VisualMarkConfig.ITEM_TYPE_SMALL);
            itemFrameLayoutButtom.onSetItemIndex(mItemIndex);
            itemFrameLayoutButtom.onSetItemSize(VisualMarkConfig.ITEM_SMALL_WIDTH, VisualMarkConfig.ITEM_SMALL_HEIGHT);

            itemFrameLayoutButtom.onSetDisplayImageBitmap(mDefaultBitmap);

            //            itemFrameLayoutButtom.onSetTitle("itemFrameLayoutButtom -- > " + "mPageIndex = " + mPageIndex + ", index  = " + index);
            itemFrameLayoutButtom.setFocusable(true);

            itemFrameLayoutButtom.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    itemFrameLayoutButtom.onShowTitle(hasFocus);
                }
            });

            itemFrameLayoutButtom.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    int itemIndex = itemFrameLayoutButtom.onGetItemIndex();

                    if (onitemHandleListener != null) {
                        onitemHandleListener.onClickPageItem(ClassficationFocusedRelativeLayout.this,
                                itemFrameLayoutButtom, mTabelkey, mPageIndex, itemIndex);
                    }
                }

            });

            addView(itemFrameLayoutButtom, lpButtom);

            mItemViewArray.add(itemFrameLayoutButtom);

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

            ItemFrameLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;

            refIv.onRecycleDisplayBitmap(mDefaultBitmap);
        }

    }

    public void onRefreshItem() {

        if (onitemHandleListener == null)
            return;
        if (mItemViewArray == null)
            return;

        int count = mItemViewArray.size();

        for (int index = 0; index < count; index++) {

            ItemFrameLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;
            int itemIndex = refIv.onGetItemIndex();
            onitemHandleListener.onRequestImageFunc(this, refIv, null, mPageIndex, itemIndex);
        }

    }

    public void onSetEachItemOfPageIndex(int pageIndex) {

        if (mItemViewArray == null)
            return;

        int count = mItemViewArray.size();

        for (int index = 0; index < count; index++) {

            ItemFrameLayout refIv = mItemViewArray.get(index);
            if (refIv == null)
                continue;

            refIv.onSetPageIndex(pageIndex);
        }

    }

    public void ClearAndDestroy() {

        //        onClearSelectedItem();

        if (mItemViewArray != null) {
            int count = mItemViewArray.size();
            for (int index = 0; index < count; index++) {

                ItemFrameLayout refIv = mItemViewArray.get(index);
                if (refIv == null)
                    continue;

                refIv.onRecycleDisplayBitmap(mRecycleBitmap);
            }

            mItemViewArray.clear();
            mItemViewArray = null;
        }

        if ((mDefaultBitmap != null) && (!mDefaultBitmap.isRecycled())) {
            mDefaultBitmap.recycle();
            mDefaultBitmap = null;
        }
    }

}
