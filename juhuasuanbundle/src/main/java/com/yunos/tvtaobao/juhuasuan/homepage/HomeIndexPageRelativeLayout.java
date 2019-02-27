package com.yunos.tvtaobao.juhuasuan.homepage;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeItemsBo;
import com.yunos.tvtaobao.juhuasuan.view.CategoryItemView;
import com.yunos.tvtaobao.juhuasuan.widget.FocusParams;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.*;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedFrameLayout;
import com.yunos.tvtaobao.juhuasuan.widget.ScrollFocusedFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeIndexPageRelativeLayout extends ScrollFocusedFrameLayout
        implements PositionInterface, ContainInterface {

    public final String TAG = "HomeIndexPageRelativeLayout";
    private Context mContext = null;

    public static final int DEFAULT_NULL = -1;
    public static final int POSITION_0 = 0;
    public static final int POSITION_1 = 1;
    public static final int POSITION_2 = 2;

    private final int ITEM_PAG_TOP = getResources().getDimensionPixelSize(R.dimen.dp_6);
    private final int ITEM_PAG_LEFT = getResources().getDimensionPixelSize(R.dimen.dp_6);
    private final int ITEM_WIDTH = getResources().getDimensionPixelSize(R.dimen.dp_246);
    private final int ITEM_HEIGHT = getResources().getDimensionPixelSize(R.dimen.dp_164);

    private static final int ITEM_EACHROW_COUNT = 3;

    private static int ITEM_PADDINGLEFT = 0;
    private static int ITEM_PADDINGTOP = 0;

    // 固定元素
    private ItemHomeImageView frist, second, third;

    //    private CategoryItemView  category1, category2, category3;
    private int mIndexCategorySize = 3;

    private int mCurrentPage = 0;

    private int mScreenWidth = 0;

    private IFocusChanged mFocusChanged = null;

    private OnRequestScroll mRequestScroll;

    private List<CategoryItemView> items = new ArrayList<CategoryItemView>();

    private int mCountCateSize = 0;
    //上次因为进入下一个页面退出时聚焦的view位置
    private Integer lastPosition;

    public static interface onHomeItemFocusListener {

        public void onFocusChanged(View view, int position, boolean hasFocus);

        public void onLoadFrontImage(String url, int position, View backItem);
    }

    public static interface OnRequestScroll {

        public void prepareScrollRequest();

        public void startScrollRequest(int position, int dx, int dur);
    }

    public void setRequestScroll(OnRequestScroll scroll) {
        mRequestScroll = scroll;
    }

    public ItemHomeImageView getSecondView() {
        return second;
    }

    public HomeIndexPageRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        onInitRelativeLayout(contxt);
    }

    public HomeIndexPageRelativeLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        onInitRelativeLayout(contxt);
    }

    public HomeIndexPageRelativeLayout(Context contxt) {
        super(contxt);
        onInitRelativeLayout(contxt);
    }

    private void onInitRelativeLayout(Context contxt) {
        mContext = contxt;

        mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        AppDebug.i(TAG, TAG + ".onInitRelativeLayout mScreenWidth=" + mScreenWidth);

        createPositionManager(initFocusParams());
        //        setViewRight(0);
        //        setViewLeft(1);
        setHorizontalMode(FocusedFrameLayout.HORIZONTAL_SINGEL);
        setAutoSearchFocus(true);
        //        setOnItemSelectedListener(null);
        //        setFocusStateListener(null); 

        setFocusable(true);

        onSetLayout(contxt);
    }

    protected FocusParams initFocusParams() {
        //logd("getFocusParams");
        FocusParams params = new FocusParams();
        params.setScaleMode(FocusedBasePositionManager.SCALED_FIXED_X);
        //        params.setFocusMode(FocusedBasePositionManager.FOCUS_ASYNC_DRAW);
        //        params.setItemScaleFixedX(10);
        params.setItemScaleFixedX(getResources().getDimensionPixelSize(R.dimen.dp_15));
        params.setItemScaleFixedY(getResources().getDimensionPixelSize(R.dimen.dp_15));
        params.setFrameRate(6);
        params.setItemScaleValue(1.03f, 1.03f);
        params.setFrameRate(3, 3);
        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.jhs_focus));
        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.jhs_focus));

        return params;
    }

    public ItemHomeImageView getTestItem() {
        return frist;
    }

    // 创建页面视图
    public void onSetLayout(Context context) {
        frist = new ItemHomeImageView(context);
        frist.setScaleType(ScaleType.FIT_XY);
        frist.setFocusable(true);
        frist.setViewScale(true);
        //        frist.setImageResource(R.drawable.first);
        FrameLayout.LayoutParams paramsRrist = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(
                R.dimen.dp_496), getResources().getDimensionPixelSize(R.dimen.dp_507));
        paramsRrist.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_75);
        paramsRrist.topMargin = getResources().getDimensionPixelSize(R.dimen.dp_30);
        frist.setLayoutParams(paramsRrist);
        addView(frist, paramsRrist);
        frist.setTag(R.id.tag_position, 0);

        second = new ItemHomeImageView(context);
        second.setScaleType(ScaleType.FIT_XY);
        second.setFocusable(true);
        //        second.setImageResource(R.drawable.second_01);
        FrameLayout.LayoutParams paramsSecond = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(
                R.dimen.dp_376), getResources().getDimensionPixelSize(R.dimen.dp_250));
        paramsSecond.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_576);
        paramsSecond.topMargin = getResources().getDimensionPixelSize(R.dimen.dp_30);
        addView(second, paramsSecond);
        second.setTag(R.id.tag_position, 1);

        third = new ItemHomeImageView(context);
        third.setScaleType(ScaleType.FIT_XY);
        third.setFocusable(true);
        //        third.setImageResource(R.drawable.third_01);
        FrameLayout.LayoutParams paramsThird = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(
                R.dimen.dp_376), getResources().getDimensionPixelSize(R.dimen.dp_250));
        paramsThird.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp_576);
        paramsThird.topMargin = getResources().getDimensionPixelSize(R.dimen.dp_285);
        third.setLayoutParams(paramsThird);
        addView(third, paramsThird);
        third.setTag(R.id.tag_position, 2);

        ITEM_PADDINGLEFT = getResources().getDimensionPixelSize(R.dimen.dp_960);
    }

    private void addNewRow(int index) {

        ITEM_PADDINGTOP = getResources().getDimensionPixelSize(R.dimen.dp_30);

        // 创建一行坑位
        for (int i = 0; i < ITEM_EACHROW_COUNT; i++) {
            CategoryItemView categoryItemView = new CategoryItemView(mContext);
            categoryItemView.setFocusable(true);
            FrameLayout.LayoutParams paramsCategory1 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
            paramsCategory1.leftMargin = ITEM_PADDINGLEFT;
            paramsCategory1.topMargin = ITEM_PADDINGTOP;

            // 最后一行
            if (index + i >= mCountCateSize - ITEM_EACHROW_COUNT) {
                paramsCategory1.rightMargin = getResources().getDimensionPixelSize(R.dimen.dp_75);
            }
            addView(categoryItemView, paramsCategory1);
            categoryItemView.setVisibility(View.GONE);
            items.add(categoryItemView);
            int position = mIndexCategorySize + index + i;
            //            category.setId(mIndexCategorySize + index + i);
            categoryItemView.setTag(R.id.tag_position, position);
            ITEM_PADDINGTOP = ITEM_PADDINGTOP + ITEM_HEIGHT + ITEM_PAG_TOP;
        }

        ITEM_PADDINGLEFT = ITEM_PADDINGLEFT + ITEM_WIDTH + ITEM_PAG_LEFT;
    }

    public void updateValues(HomeCatesResponse categorys, IloadImageFormUri mLoadimage, View.OnClickListener Itemclick,
                             final onHomeItemFocusListener listener) {

        frist.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listener.onFocusChanged(v, POSITION_0, hasFocus);                
            }
        });

        second.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listener.onFocusChanged(v, POSITION_1, hasFocus);
            }
        });

        third.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                listener.onFocusChanged(v, POSITION_2, hasFocus);
            }
        });

        if (null != categorys && null != categorys.getItems() && categorys.getItems().size() >= 3) {
            HomeItemsBo categoryItem1 = categorys.getItems().get(0);
            if (categoryItem1 != null) {
                //                mImageLoader.loadImage(categoryItem1.bg_img, frist, new CustomsItemIconLoader());
                //                mLoadimage.requestLoadImage(frist, categoryItem1.bg_img, R.drawable.first);
                mLoadimage.requestLoadImage(frist, categoryItem1.getBg_img(), DEFAULT_NULL);
                frist.setTag(categoryItem1);
                frist.setOnClickListener(Itemclick);

                // 前景色
                listener.onLoadFrontImage(categoryItem1.getFront_img(), POSITION_0, frist);
            }

            HomeItemsBo categoryItem2 = categorys.getItems().get(1);
            if (categoryItem2 != null) {
                //                mImageLoader.loadImage(categoryItem2.bg_img, second, new CustomsItemIconLoader());
                //                mLoadimage.requestLoadImage(second, categoryItem2.bg_img, R.drawable.second_01);
                mLoadimage.requestLoadImage(second, categoryItem2.getBg_img(), DEFAULT_NULL);
                second.setTag(categoryItem2);
                second.setOnClickListener(Itemclick);

                // 前景色
                listener.onLoadFrontImage(categoryItem2.getFront_img(), POSITION_1, second);
            }

            HomeItemsBo categoryItem3 = categorys.getItems().get(2);
            if (categoryItem3 != null) {
                // 背景色
                //                mImageLoader.loadImage(categoryItem3.bg_img, third, new CustomsItemIconLoader());
                //                mLoadimage.requestLoadImage(third, categoryItem3.bg_img, R.drawable.third_01);
                mLoadimage.requestLoadImage(third, categoryItem3.getBg_img(), DEFAULT_NULL);
                third.setTag(categoryItem3);
                third.setOnClickListener(Itemclick);

                // 前景色
                listener.onLoadFrontImage(categoryItem3.getFront_img(), POSITION_2, third);
            }

            int position = 0;
            mCountCateSize = categorys.getCates().size();
            for (int i = 0; i < categorys.getCates().size(); i++) {
                if ((position > 0 && position % ITEM_EACHROW_COUNT == 0) || position == 0) {
                    addNewRow(position);
                }

                CategoryItemView item = items.get(i);
                if (item != null) {
                    if (position < categorys.getCates().size()) {
                        HomeCatesBo categoryItem = categorys.getCates().get(position);
                        item.updateView(categoryItem);
                        item.setOnClickListener(Itemclick);
                        position++;
                    }
                }
            }
        }
        invalidate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = super.onKeyDown(keyCode, event);

        // 计算scroller
        View select = getSelectedView();
        scrollToView(select, keyCode);
        //        if (select != null) {
        //            int[] location = new int[2];
        //            select.getLocationOnScreen(location);
        //            int left = location[0];
        //
        //            // 计算并滚动
        //            calcDx(select, keyCode, left);
        //        }

        return flag;
    }

    public void scrollToRightView(View view) {
        scrollToView(view, KeyEvent.KEYCODE_DPAD_RIGHT);
    }

    public void scrollToView(View view, int dir) {
        if (null == view) {
            return;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];

        // 计算并滚动
        calcDx(view, dir, left);
    }

    public void calcDx(View select, int dir, int left) {
        int dx = 0;
        int TotalWidth = select.getWidth() + ITEM_PAG_LEFT;
        int right = left + TotalWidth;

        if (dir == KeyEvent.KEYCODE_DPAD_LEFT) {

            int pLeft = select.getLeft();

            if (left < 0) {
                dx = TotalWidth;
                // 第一个元素
                if (pLeft < 0 || pLeft - TotalWidth < 0) {
                    dx = pLeft + TotalWidth;
                }
                AppDebug.i(TAG, TAG + ".calcDx dx=" + dx + ", pLeft=" + pLeft + ", left=" + left + ", TotalWidth="
                        + TotalWidth);

                smoothScrollBy(mIndex, -dx, 80);
            }
        } else if (dir == KeyEvent.KEYCODE_DPAD_RIGHT) {

            if (right > mScreenWidth) {

                int widgetWidth = getWidth();

                int pRight = select.getLeft() + TotalWidth;

                dx = TotalWidth;
                // 最后一个元素
                if (pRight > widgetWidth || pRight + TotalWidth > widgetWidth) {
                    dx = widgetWidth - (pRight - TotalWidth);
                }
                while (right - dx > mScreenWidth) {
                    dx += TotalWidth;
                }
                AppDebug.i(TAG, TAG + ".calcDx dx=" + dx + ", widgetWidth=" + widgetWidth + ", left=" + left
                        + ", TotalWidth=" + TotalWidth + ", pRight=" + pRight + ", mScreenWidth=" + mScreenWidth);

                smoothScrollBy(mIndex, dx, 80);
            }
        }
    }

    public void smoothScrollBy(int position, int dx, int duration) {
        if (mRequestScroll != null) {
            mRequestScroll.startScrollRequest(position, dx, duration);
        }
    }

    public void setFocusChangeListener(IFocusChanged listener) {
        mFocusChanged = listener;
    }

    //    private int getDp(int value) {
    //        AppDebug.i(TAG, TAG + ".getDp value=" + value + " => " + ((int) (value * SystemUtil.getMagnification())));
    //        return (int) (value * SystemUtil.getMagnification());
    //    }

    public void setCurrentPage(int page) {
        mCurrentPage = page;
    }
}
