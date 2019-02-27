package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvlife.app.widget.FocusedRelativeLayout;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;

import java.util.List;

/**
 * 1.显示商品单页的布局，每个页面分上下层
 * 2.根据聚焦的变化显示聚焦的动画，使用Win8的分格
 * 3.监听并截取KEY事件自定义商品前后聚焦的控件位置
 * @author tim
 */
public class PageItemView extends FocusedRelativeLayout {

    private final String TAG = "PageItemView";
    public static final int ITEM_COUNT_EACH_PAGE = 5;//Page的商品个数
    private GoodsNormalItemView[] mAboveChildViews;//单页上层的布局子view
    //上层view的ID
    private int[] mAboveChildViewIds = { R.id.item1, R.id.item2 };
    private GoodsNormalItemView[] mBelowChildViews;//单页下层的布局子view
    //下层viewId
    private int[] mBelowChildViewIds = { R.id.item3, R.id.item4, R.id.item5 };
    private View mItemView;
    private CategoryMO mCate;
    private boolean mIsLastPage;
    private boolean mIsFirstPage;
    private Context mContext;
    private boolean mIsAnimRunning;
    private boolean mIsRefreshImageAtTime;
    private int mBgPadding;
    private int mItemWidth;
    private int mItemHeight;

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public PageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        AppDebug.i(TAG, TAG + ".PageItemView ==> 1");
    }

    /**
     * 设置当前商品页是否为隐藏
     * @param isHide
     */
    public void setHide(boolean isHide) {
        for (GoodsNormalItemView view : mAboveChildViews) {
            view.setHide(isHide);
        }
        for (GoodsNormalItemView view : mBelowChildViews) {
            view.setHide(isHide);
        }
    }

    @SuppressLint("MissingSuperCall")
    protected void onFinishInflate() {
        AppDebug.i(TAG, TAG + ".onFinishInflate ==> 2");
        mItemView = findViewById(R.id.page_layout);
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        AppDebug.i(TAG, TAG + ".initUI ==> 3");
        mAboveChildViews = new GoodsNormalItemView[mAboveChildViewIds.length];
        for (int i = 0; i < mAboveChildViewIds.length; i++) {
            mAboveChildViews[i] = (GoodsNormalItemView) mItemView.findViewById(mAboveChildViewIds[i]);
            //上排的view设置为true
            mAboveChildViews[i].setTag(true);
            mAboveChildViews[i].setNextFocusUpId(R.id.category_List_bar);
        }
        mBelowChildViews = new GoodsNormalItemView[mBelowChildViewIds.length];
        for (int i = 0; i < mBelowChildViewIds.length; i++) {
            mBelowChildViews[i] = (GoodsNormalItemView) mItemView.findViewById(mBelowChildViewIds[i]);
            //下排的view设置为false
            mBelowChildViews[i].setTag(false);
        }
        mBgPadding = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_item_bg_padding);
        mItemWidth = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_item_width);
        mItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_item_height);
        AppDebug.i(TAG, TAG + ".initUI ==> 4");
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (mAboveChildViews != null && mAboveChildViews.length > 0) {
            NinePatchDrawable bg = (NinePatchDrawable) mContext.getResources().getDrawable(R.drawable.jhs_item_bg);
            bg.setBounds(mAboveChildViews[0].getLeft() - mBgPadding, mAboveChildViews[0].getTop() - mBgPadding,
                    mItemWidth, mItemHeight);
            bg.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    /**
     * 设置是否需要立刻刷新商品的图片
     * @param state
     */
    public void setRefreshImageAtTime(boolean state) {
        mIsRefreshImageAtTime = state;
    }

    /**
     * 设置分类的ID
     * @param cate
     */
    public void setCategory(CategoryMO cate) {
        mCate = cate;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        AppDebug.i(TAG, "ItemListPageItemView onLayout changed=" + changed);
        if (changed) {
            super.onLayout(changed, l, t, r, b);
        }
    }

    /**
     * 根据位置找到商品的信息View
     * @param position
     * @return ItemListGoodsNormalItemView
     */
    public GoodsNormalItemView findChildViewForPosition(int position) {
        for (GoodsNormalItemView view : mAboveChildViews) {
            if (view.getPosition() == position) {
                return view;
            }
        }
        for (GoodsNormalItemView view : mBelowChildViews) {
            if (view.getPosition() == position) {
                return view;
            }
        }
        return null;
    }

    /**
     * 设置当前为最后一页
     */
    public void setIsLastPage(boolean isLast) {
        AppDebug.i(TAG, TAG + ".setIsLastPage isLast=" + isLast);
        mIsLastPage = isLast;
    }

    /**
     * 设置当前为最前一页
     */
    public void setIsFirstPage(boolean isFirst) {
        mIsFirstPage = isFirst;
    }

    /**
     * 设置Page里面商品的数据，没有数据的隐藏不显示
     * @param listData
     * @param pagePosition
     * @param fragment
     */
    public void setPageData(List<ItemMO> listData, int pagePosition, Fragment fragment) {
        if (listData != null) {
            AppDebug.i(TAG, TAG + ".setPageData listData=" + listData.size() + ", pagePosition=" + pagePosition);
            int position = pagePosition * ITEM_COUNT_EACH_PAGE;
            int aboverViewSize = mAboveChildViews.length;
            //设置上层的数据
            int aboveSetDataCount = aboverViewSize;
            if (listData.size() < aboverViewSize) {
                //隐藏没有数据的商品
                for (int i = listData.size(); i < aboverViewSize; i++) {
                    AppDebug.d(TAG, TAG + ".setPageData above view at " + i + " set INVISIBLE");
                    mAboveChildViews[i].setVisibility(View.INVISIBLE);
                    mBelowChildViews[i].setFocusable(false);
                }
                aboveSetDataCount = listData.size();
            }
            for (int i = 0; i < aboveSetDataCount; i++) {
                mAboveChildViews[i].setCategory(mCate);
                mAboveChildViews[i].setFragment(fragment);

                mAboveChildViews[i].setVisibility(View.VISIBLE);
                mBelowChildViews[i].setFocusable(true);
                //只有首进入的时候显示图片,后面页由滚动的出来再显示
                if (mIsRefreshImageAtTime) {
                    mAboveChildViews[i].refreshGoodsItem(true, listData.get(i), position, pagePosition);
                } else {
                    mAboveChildViews[i].refreshGoodsItem(false, listData.get(i), position, pagePosition);
                }
                //如果是最前一页第一个item不能向前focus
                if (mIsFirstPage && i == 0) {
                    mAboveChildViews[i].setCanPreFocus(false);
                } else {
                    mAboveChildViews[i].setCanPreFocus(true);
                }
                //如果是最前一页最后一个不能向前focus
                if (mIsLastPage && (i == aboveSetDataCount - 1)) {
                    mAboveChildViews[i].setCanNextFocus(false);
                } else {
                    mAboveChildViews[i].setCanNextFocus(true);
                }
                position++;
            }

            //设置下层的数据
            if (listData.size() > aboverViewSize) {
                int belowViewSize = mBelowChildViews.length;
                int leftItemDataSize = listData.size() - aboverViewSize;
                AppDebug.i(TAG, TAG + ".setPageData leftItemDataSize=" + leftItemDataSize + ", listData.size()="
                        + listData.size() + ", belowViewSize=" + belowViewSize);
                if (leftItemDataSize < belowViewSize) {
                    //隐藏没有数据的商品
                    for (int i = leftItemDataSize; i < belowViewSize; i++) {
                        AppDebug.d(TAG, TAG + ".setPageData below view at " + i + " set INVISIBLE");
                        mBelowChildViews[i].setVisibility(View.INVISIBLE);
                        mBelowChildViews[i].setFocusable(false);
                    }
                } else {
                    //不能让设置的数据大于view的个数
                    leftItemDataSize = belowViewSize;
                }
                for (int i = 0; i < leftItemDataSize; i++) {
                    mBelowChildViews[i].setCategory(mCate);
                    mBelowChildViews[i].setFragment(fragment);
                    //只有首进入的时候显示图片,后面页由滚动的出来再显示
                    if (mIsRefreshImageAtTime) {
                        mBelowChildViews[i].refreshGoodsItem(true, listData.get(aboverViewSize + i), position++,
                                pagePosition);
                    } else {
                        mBelowChildViews[i].refreshGoodsItem(false, listData.get(aboverViewSize + i), position++,
                                pagePosition);
                    }
                    mBelowChildViews[i].setVisibility(View.VISIBLE);
                    mBelowChildViews[i].setFocusable(true);
                    //如果是最前一页第一个item不能向前focus
                    if (mIsFirstPage && i == 0) {
                        mBelowChildViews[i].setCanPreFocus(false);
                    } else {
                        mBelowChildViews[i].setCanPreFocus(true);
                    }
                    //如果是最前一页最后一个不能向前focus
                    if (mIsLastPage && (i == leftItemDataSize - 1)) {
                        mBelowChildViews[i].setCanNextFocus(false);
                    } else {
                        mBelowChildViews[i].setCanNextFocus(true);
                    }
                }
            } else {
                //隐藏没有数据的商品
                for (int i = 0; i < mBelowChildViews.length; i++) {
                    AppDebug.d(TAG, TAG + ".setPageData all view at " + i + " set INVISIBLE");
                    mBelowChildViews[i].setVisibility(View.INVISIBLE);
                    mBelowChildViews[i].setFocusable(false);
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.i(TAG, TAG + ".dispatchKeyEvent event=" + event);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //取得当前聚焦的控件并判断是否可以左右变化聚焦
            View currView = getSelectedView();
            if (currView instanceof GoodsNormalItemView) {
                GoodsNormalItemView currItemView = (GoodsNormalItemView) currView;
                AppDebug.i(TAG, TAG + ".dispatchKeyEvent canNext=" + currItemView.getCanNextFocus() + " canPre="
                        + currItemView.getCanPreFocus());
                if (!currItemView.getCanNextFocus() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.jhs_page_last_goods_anim);
                    anim.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            mIsAnimRunning = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mIsAnimRunning = false;
                        }
                    });
                    if (mIsAnimRunning == false) {
                        startAnimation(anim);
                    }
                    //                    Toast.makeText(getContext(), getContext().getString(R.string.no_more_goods), 200).show();
                    return false;
                }
                if (!currItemView.getCanPreFocus() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.jhs_page_first_goods_anim);
                    anim.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            mIsAnimRunning = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mIsAnimRunning = false;
                        }
                    });
                    if (mIsAnimRunning == false) {
                        startAnimation(anim);
                    }
                    //                    Toast.makeText(getContext(), getContext().getString(R.string.is_first_goods), 200).show();
                    return false;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //    @Override
    //    public boolean onKeyDown(int keycode, KeyEvent event) {
    //        boolean ret = super.onKeyDown(keycode, event);
    //        if (!ret && keycode == KeyEvent.KEYCODE_DPAD_DOWN) {
    //            View view = findViewById(R.id.item3);
    //            if (null != view && view.isFocusable()) {
    //                View lastSelectView = getSelectedView();
    //                if (null != view && lastSelectView.getId() == R.id.item2) {
    //                    selectView(view, keycode, lastSelectView, null, false);
    //                }
    //            }
    //        }
    //        return ret;
    //    }

    /**
     * 更新商品
     */
    public void refreshGoodsView() {
        for (GoodsNormalItemView view : mBelowChildViews) {
            if (view.getVisibility() == View.VISIBLE) {
                view.refreshImage();
                view.refreshGoodsInfo();
            }
        }
        for (GoodsNormalItemView view : mAboveChildViews) {
            if (view.getVisibility() == View.VISIBLE) {
                view.refreshImage();
                view.refreshGoodsInfo();
            }
        }
    }

    /**
     * 回收商品信息的Bitmap
     */
    public void recycleGoodsInfoBitmap() {
        for (GoodsNormalItemView view : mAboveChildViews) {
            view.recycleGoodsInfoBitmap();
        }
        for (GoodsNormalItemView view : mBelowChildViews) {
            view.recycleGoodsInfoBitmap();
        }
    }

    /**
     * 取消商品图片加载
     */
    public void cancelLoadImage() {
        for (GoodsNormalItemView view : mAboveChildViews) {
            view.cancelLoadImage();
        }
        for (GoodsNormalItemView view : mBelowChildViews) {
            view.cancelLoadImage();
        }
    }

    @Override
    public void release() {
        AppDebug.i(TAG, TAG + ".release recyleMemory");
        for (GoodsNormalItemView view : mAboveChildViews) {
            view.release();
            //            view.recycleGoodsInfoBitmap();
            //            view.recycleLoadBitmap();
        }
        super.release();
    }

    /**
     * 设置是否正在滑动
     */
    public void setSlide(boolean isSlide) {
        for (GoodsNormalItemView view : mAboveChildViews) {
            view.setSlide(isSlide);
        }
        for (GoodsNormalItemView view : mBelowChildViews) {
            view.setSlide(isSlide);
        }
    }

}
