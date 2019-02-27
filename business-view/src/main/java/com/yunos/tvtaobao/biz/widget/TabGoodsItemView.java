package com.yunos.tvtaobao.biz.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.businessview.R;

public abstract class TabGoodsItemView extends RelativeLayout implements ItemListener {

    protected final String TAG = "TabGoodsItemView";
    protected Context mContext;

    // Item的标题VIEW
    protected TextView mGoodsTitleTextView;

    // Item的价格VIEW
    protected TextView mGoodsPriceTextView;


    // Item的返利VIEW
    protected TextView mGoodsRebateCoupon;

    // Item的返利IconVIEW
    protected ImageView mGoodsRebateIcon;




    // Item的背景VIEW
    protected View mBackgroudImageView;

    // 判断是否为Headerview
    protected boolean mbHeaderView;

    protected boolean mSetGoodsDrawable;
    protected boolean mSetInfoDrawable;

    // 当前界面是否选中
    protected boolean mIsSelect;

    // item 的 mPosition
    private int mPosition;
    // item 关联的 TabKey
    private String mTabKey;

    // 微调的区域
    private Rect mItemFocusBound;

    public TabGoodsItemView(Context context) {
        super(context);
        onInitTabGoodsItemView(context);
    }

    public TabGoodsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitTabGoodsItemView(context);
    }

    public TabGoodsItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitTabGoodsItemView(context);
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        onAjustItemFouceBound(focuse);
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
    }

    @Override
    public int getItemWidth() {
        if (mBackgroudImageView == null) {
            mBackgroudImageView = findViewById(getBackgroudViewResId());
        }
        return mBackgroudImageView.getWidth();
    }

    @Override
    public int getItemHeight() {
        if (mBackgroudImageView == null) {
            mBackgroudImageView = findViewById(getBackgroudViewResId());
        }
        return mBackgroudImageView.getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }

    /**
     * 初始化item
     * @param context
     */
    private void onInitTabGoodsItemView(Context context) {

        mContext = context;
        mSetGoodsDrawable = false;
        mSetInfoDrawable = false;
        mIsSelect = false;

        mItemFocusBound = new Rect();
        mItemFocusBound.setEmpty();

        // 焦点框微调
        mItemFocusBound.left = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);
        mItemFocusBound.right = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);
        mItemFocusBound.top = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_2);
        mItemFocusBound.bottom = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);

        mBackgroudImageView = (ImageView) findViewById(getBackgroudViewResId());
        mGoodsTitleTextView = (TextView) findViewById(getGoodsTitleViewResId());
        mGoodsPriceTextView = (TextView) findViewById(getGoodsPriceViewResId());
        mGoodsRebateCoupon = (TextView) findViewById(getGoodsRebateCouponViewResId());
        mGoodsRebateIcon  = (ImageView) findViewById(getGoodsRebateIconViewResId());

        mbHeaderView = false;

    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    /**
     * 调整Item的Fouce区域
     * @param rt
     */
    private void onAjustItemFouceBound(Rect rt) {

        Rect rect = new Rect();
        rect.left = getPaddingLeft();
        rect.right = getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = getPaddingBottom();

        // 减去Padding
        rt.left += rect.left;
        rt.top += rect.top;
        rt.right -= rect.right;
        rt.bottom -= rect.bottom;

        // 微调
        Rect focusbound = getFocusBoundRect();
        rt.left += focusbound.left;
        rt.top += focusbound.top;
        rt.right -= focusbound.right;
        rt.bottom -= focusbound.bottom;

    }

    /**
     * 设置是否是Header中的ITEM
     * @param bHeaderView
     */
    public void setIsHeaderView(boolean bHeaderView) {
        mbHeaderView = bHeaderView;
    }

    public boolean isSetDefaultDrawable(int position) {
        if (position == mPosition) {
            if (mSetGoodsDrawable) {
                return false;
            }
        }
        return true;
    }

    public void setDefaultDrawable(boolean setGoodsDrawable) {
        mSetGoodsDrawable = setGoodsDrawable;
    }

    public void setHideInfoDrawable(boolean setInfoDrawable) {
        mSetInfoDrawable = setInfoDrawable;
    }

    public boolean isHideInfoDrawable(int position) {
        if (position == mPosition) {
            if (mSetInfoDrawable) {
                return false;
            }
        }
        return true;
    }

    private void handlerTitleView() {
        if (isShowTitleOfNotSelect()) {
            return;
        }
        TextView title = (TextView) findViewById(getGoodsTitleViewResId());
        if (title != null) {
            if (mIsSelect) {
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置标题是否显示
     * @param visibility
     */
    private void onSetTitleVisibility(int visibility) {
        if (!isShowTitleOfNotSelect()) {
            return;
        }
        if (mGoodsTitleTextView == null) {
            mGoodsTitleTextView = (TextView) findViewById(getGoodsTitleViewResId());
        }
        mGoodsTitleTextView.setVisibility(visibility);

        if(mGoodsPriceTextView==null){
            mGoodsPriceTextView = (TextView) findViewById(getGoodsPriceViewResId());
        }
        mGoodsPriceTextView.setVisibility(visibility);

        if(mGoodsRebateCoupon==null){
            mGoodsRebateCoupon = (TextView) findViewById(getGoodsRebateCouponViewResId());
        }
        mGoodsRebateCoupon.setVisibility(visibility);

        if (mGoodsRebateIcon==null){
            mGoodsRebateIcon  = (ImageView) findViewById(getGoodsRebateIconViewResId());

        }
        mGoodsRebateIcon.setVisibility(visibility);

    }

    /**
     * 设置默认的商品图片
     * @param d
     * @param position
     */
    public void onSetGoodsListDefaultDrawable(Drawable d, int position) {
        if (position != mPosition) {
            return;
        }

        ImageView imageView = (ImageView) findViewById(getGoodsDrawableViewResId());

        if (imageView != null) {
            imageView.setBackgroundDrawable(d);
        }
    }

    /**
     * 设置商品图片
     * @param drawable
     * @param position
     */
    public void onSetGoodsListDrawable(Drawable drawable, int position) {

        AppDebug.i(TAG, "onSetGoodsListDrawable   position =  " + position + ";    mPosition = " + mPosition
                + "; drawable = " + drawable);

        if (position != mPosition) {
            return;
        }

        handlerTitleView();

        // 如果已经设置过，那么不要再重新设置
        if (mSetGoodsDrawable) {
            return;
        }

        ImageView imageView = (ImageView) findViewById(getGoodsDrawableViewResId());
        if (imageView != null) {
            imageView.setBackgroundDrawable(drawable);
            mSetGoodsDrawable = true;
        }
    }

    /**
     * 获取商品图片的ImageView
     * @return
     */
    public ImageView onGetGoodsListImageView() {
        ImageView imageView = (ImageView) findViewById(getGoodsDrawableViewResId());
        return imageView;
    }

    /**
     * 设置商品的信息
     * @param drawable
     * @param position
     */
    public void onSetInfoListDrawable(Drawable drawable, int position) {

        AppDebug.i(TAG, "onSetInfoListDrawable   position =  " + position + ";    mPosition = " + mPosition
                + "; drawable = " + drawable);

        if (position != mPosition) {
            return;
        }

        handlerTitleView();

        if (mSetInfoDrawable) {
            return;
        }

        ImageView imageView = (ImageView) findViewById(getInfoDrawableViewResId());
        imageView.setBackgroundDrawable(drawable);
        imageView.setVisibility(View.VISIBLE);
        mSetInfoDrawable = true;

        // 显示标题
        onSetTitleVisibility(View.VISIBLE);
    }

    /**
     * 获取商品的信息
     * @return
     */
    public ImageView onGetInfoImageView() {
        ImageView imageView = (ImageView) findViewById(getInfoDrawableViewResId());
        return imageView;
    }

    /**
     * 隐藏信息，同时也隐藏标题
     */
    public void onHideInfoImageView() {
        ImageView imageView = (ImageView) findViewById(getInfoDrawableViewResId());
        imageView.setVisibility(View.INVISIBLE);

        onSetTitleVisibility(View.INVISIBLE);
    }

    /**
     * 处理当选中条目时的情况
     * @param isSelected
     * @param fatherView
     */
    public void onItemSelected(boolean isSelected, View fatherView) {

        handlerItemSelected(mTabKey, mPosition, isSelected, fatherView);

        mIsSelect = isSelected;

        requestLayout();
    }

    /**
     * 设置关联的 TabKey
     * @param orderby
     */
    public void onSetTabKey(String tabkey) {
        mTabKey = tabkey;
    }

    /**
     * 获取关联的 TabKey
     * @return
     */
    public String onGetTabKey() {
        return mTabKey;
    }

    /**
     * 初始化变量值
     */
    public void onInitVariableValue() {
        if (!mIsSelect) {
            onSetTitleVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置ITEM的position
     * @param position
     */
    public void onSetPosition(int position) {
        AppDebug.i(TAG, "onSetPosition  mPosition = " + mPosition);
        mPosition = position;
    }

    /**
     * 获取 Position
     * @return
     */
    public int onGetPosition() {
        return mPosition;
    }

    /**
     * 释放资源
     */
    public void onDestroyAndClear() {

    }

    /**
     * 如果需要微调Focus，那么可重写此方法
     * @return
     */
    protected Rect getFocusBoundRect() {
        return mItemFocusBound;
    }

    /**
     * 当前ITEM不是选中时，是否显示title
     * @return
     */
    protected abstract boolean isShowTitleOfNotSelect();

    /**
     * 获取背景VIEW的RES id
     * @return
     */
    protected abstract int getBackgroudViewResId();

    /**
     * 获取商品标题View的Res ID
     * @return
     */
    protected abstract int getGoodsTitleViewResId();

    /**
     * 获取商品价格View的Res ID
     * @return
     */
    protected abstract int getGoodsPriceViewResId();

    /**
     * 获取商品返利View的Res ID
     * @return
     */
    protected abstract int getGoodsRebateCouponViewResId();

    /**
     * 获取商品返利图标View的Res ID
     * @return
     */
    protected abstract int getGoodsRebateIconViewResId();

    /**
     * 获取商品信息View的Res ID
     * @return
     */
    protected abstract int getInfoDrawableViewResId();

    /**
     * 获取商品图片View的Res ID
     * @return
     */
    protected abstract int getGoodsDrawableViewResId();

    /**
     * 选中当前条目的处理
     * @param tabkey
     * @param position
     * @param isSelected
     * @param fatherView
     */
    protected abstract void handlerItemSelected(String tabkey, int position, boolean isSelected, View fatherView);

}
