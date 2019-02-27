package com.yunos.tvtaobao.juhuasuan.classification;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.classification.ImageHandleUnit.ClassficationImageHandle;
import com.yunos.tvtaobao.juhuasuan.util.JuApiUtils;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.AccelerateFrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.FrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tv.core.common.AppDebug;

public class ItemFrameLayout extends FrameLayout implements ItemInterface {

    private String TAG = "ItemFrameLayout";

    private Context mContext = null;
    private ImageView mImageView = null;
    private TextView mTextView = null;

    private ImageView mImageView_GouMai = null;

    // 表示的条目编号
    private int mItemIndex = 0;

    // 表示所在页
    private int mPageIndex = 0;

    private Bitmap mBitmap = null;

    private int mImageType = 0;

    private int mItemWidth = 0;
    private int mItemHeight = 0;

    public ItemFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub

        onInitItemFrameLayout(context);
    }

    public ItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        onInitItemFrameLayout(context);
    }

    public ItemFrameLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        onInitItemFrameLayout(context);
    }

    private void onInitItemFrameLayout(Context context) {
        mContext = context;
    }

    @Override
    public int getItemWidth() {
        // TODO Auto-generated method stub
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        // TODO Auto-generated method stub
        return getHeight();
    }

    @Override
    public Rect getOriginalRect() {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();

        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);

        int imgW = getWidth();
        int imgH = getHeight();

        rect.left = (int) (getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f);
        rect.right = (int) (rect.left + imgW * scaledX - 0.5f);
        rect.top = (int) (getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        rect.bottom = (int) (rect.top + imgH * scaledY - 0.5f);
        return rect;
    }

    @Override
    public boolean getIfScale() {
        // TODO Auto-generated method stub
        return true;
    }

    AccelerateFrameInterpolator mScaleInterpolator = new AccelerateFrameInterpolator();
    AccelerateFrameInterpolator mFocusInterpolator = new AccelerateFrameInterpolator(0.5f);

    @Override
    public FrameInterpolator getFrameScaleInterpolator() {
        // TODO Auto-generated method stub
        return mScaleInterpolator;
    }

    @Override
    public FrameInterpolator getFrameFocusInterpolator() {
        // TODO Auto-generated method stub
        return mFocusInterpolator;
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onSetImageType(int type) {

        mImageType = type;
    }

    public int onGetImageType() {
        return mImageType;
    }

    public void onInitItemFrameLayout(int imagetype) {
        mImageType = imagetype;

        int titleHeight = 0;
        if (mImageType == 1) {
            titleHeight = VisualMarkConfig.TITLE_HEIGHT_BIG;
        } else {
            titleHeight = VisualMarkConfig.TITLE_HEIGHT_SMALL;
        }

        FrameLayout.LayoutParams lpImageView = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lpImageView.setMargins(0, 0, 0, 0);
        mImageView = new ImageView(mContext);
        mImageView.setAdjustViewBounds(true);
        mImageView.setScaleType(ScaleType.FIT_XY);

        addView(mImageView, lpImageView);

        //        FrameLayout.LayoutParams lpTextView = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //        lpTextView.setMargins(0, titleHeight, 0, 0);
        //        mTextView = new TextView(mContext);
        //
        //        mTextView.setFocusable(false);
        //        mTextView.setMaxLines(1);
        //        mTextView.setVisibility(View.GONE);
        //        mTextView.setTextColor(Color.WHITE);
        //        mTextView.setBackgroundColor(VisualMarkConfig.TITLE_BLACKGROUD_COLOR);
        //        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        //
        //        addView(mTextView, lpTextView);
        //
        //        mTextView.setVisibility(View.GONE);

        int left = 0;
        int buttom = 0;
        if (mImageType == 1) {

            left = VisualMarkConfig.ACTIONS_MARGIN_LEFT_BIG;
            buttom = VisualMarkConfig.ACTIONS_MARGIN_BUTTOM_BIG;

        } else {
            left = VisualMarkConfig.ACTIONS_MARGIN_LEFT_SMALL;
            buttom = VisualMarkConfig.ACTIONS_MARGIN_BUTTOM_SMALL;
        }

        FrameLayout.LayoutParams lpgoumaiView = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lpgoumaiView.setMargins(left, buttom, 0, 0);
        mImageView_GouMai = new ImageView(mContext);

        mImageView_GouMai.setFocusable(false);
        mImageView_GouMai.setVisibility(View.GONE);
        //        mImageView_GouMai.setBackgroundColor(VisualMarkConfig.TITLE_BLACKGROUD_COLOR); 

        addView(mImageView_GouMai, lpgoumaiView);

    }

    public void onSetItemSize(int width, int height) {
        mItemWidth = width;
        mItemHeight = height;
    }

    public int onGetItemWidth() {
        return mItemWidth;
    }

    public int onGetItemHeight() {
        return mItemHeight;
    }

    public void onSetDisplayImageBitmap(Bitmap bm) {

        if (bm == null)
            return;

        if (mImageView == null)
            return;

        mBitmap = bm;
        mImageView.setImageBitmap(bm);
    }

    public void onRecycleDisplayBitmap(Bitmap defaultBm) {

        mImageView.setImageBitmap(defaultBm);

        if ((mBitmap != null) && (!mBitmap.isRecycled())) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void onSetTitle(String title) {
        if (title == null)
            return;
        if (mTextView == null)
            return;
        mTextView.setText(title);
    }

    public void onShowTitle(boolean show) {
        if (mTextView == null)
            return;

        if (show) {
            // mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
        }
    }

    public void onShowActions(boolean show) {

        if (mImageView_GouMai == null)
            return;

        if (show) {
            mImageView_GouMai.setVisibility(View.VISIBLE);
        } else {
            mImageView_GouMai.setVisibility(View.GONE);
        }

    }

    public void onsetActionsBitmap(int resId) {

        if (mImageView_GouMai == null)
            return;

        mImageView_GouMai.setImageResource(resId);
        mImageView_GouMai.setVisibility(View.VISIBLE);

    }

    public void onsetActionsBitmap(ItemMO juitem) {

        if (mImageView_GouMai == null)
            return;

        if (juitem == null) {
            return;
        }

        int resId = VisualMarkConfig.MARK_RESOUS_NO_STOCK;

        //        juitem.itemStatus = ItemMoStateEnum.NO_STOCK.ordinal();

        //卖光了
        if (JuApiUtils.isNoStock(juitem.getItemStatus())) {
            resId = VisualMarkConfig.MARK_RESOUS_NO_STOCK;
        }

        // 结束
        else if (JuApiUtils.isOver(juitem.getItemStatus())) {
            resId = VisualMarkConfig.MARK_RESOUS_SOLD_OVER;
        }

        // 未开始
        else if (JuApiUtils.isNotStart(juitem.getItemStatus())) {
            resId = VisualMarkConfig.MARK_RESOUS_NO_START;
        } else {
            mImageView_GouMai.setVisibility(View.GONE);

            return;
        }

        mImageView_GouMai.setImageResource(resId);
        mImageView_GouMai.setVisibility(View.VISIBLE);

    }

    public void onSetItemIndex(int index) {
        mItemIndex = index;
    }

    public int onGetItemIndex() {
        return mItemIndex;
    }

    public void onSetPageIndex(int pageindex) {
        mPageIndex = pageindex;
    }

    public int onGetPageIndex() {
        return mPageIndex;
    }

    public void onReUpdateInfo(ClassficationImageHandle classficationImageHandle, ItemMO juitemSummary) {

        AppDebug.i(TAG, "onReUpdateInfo   classficationImageHandle -->  " + classficationImageHandle
                + ", juitemSummary =  " + juitemSummary);

        if ((classficationImageHandle == null) || (juitemSummary == null))
            return;

        Bitmap bm = classficationImageHandle.onHandleDisplayBitmap(mBitmap, this, juitemSummary);
        onSetDisplayImageBitmap(bm);
    }

}
