package com.yunos.tvtaobao.juhuasuan.homepage;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PingPaiDimension;
import com.yunos.tvtaobao.juhuasuan.view.CategoryItemView;
import com.yunos.tvtaobao.juhuasuan.widget.FocusParams;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.ContainInterface;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.PositionInterface;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeItempage extends FocusedFrameLayout implements PositionInterface, ContainInterface {

    private Context mContext;

    private List<CategoryItemView> items = new ArrayList<CategoryItemView>();

    // 美页显示的Item数量
    private int mEachPageSize = 12;

    private int mCurrentPage = 0;

    private int mEachRowCount = 3;

    private IFocusChanged mFocusChanged = null;

    private final static int ITEM_GAP_PARENTLEFT = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_40);
    private final static int ITEM_GAP_VERTICAL = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_6);
    private final static int ITEM_GAP_HORIZONTAL = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_6);
    private final static int ITEM_WIDTH = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_246);
    private final static int ITEM_HEIGHT = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_164);
    private final static int TOPMATGIN = PingPaiDimension.getDimensionPixelSize(R.dimen.dp_30);

    private final static int ITEM_WIDTH_TOTAL = ITEM_WIDTH + ITEM_GAP_VERTICAL;
    private final static int ITEM_HEIGHT_TOTAL = ITEM_HEIGHT + ITEM_GAP_HORIZONTAL;

    public HomeItempage(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        onInitRelativeLayout(contxt);
    }

    public HomeItempage(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        onInitRelativeLayout(contxt);
    }

    public HomeItempage(Context contxt) {
        super(contxt);
        onInitRelativeLayout(contxt);
    }

    private void onInitRelativeLayout(Context contxt) {
        mContext = contxt;

        createPositionManager(initFocusParams());
        //        setViewRight(0);
        //        setViewLeft(1);
        setHorizontalMode(FocusedFrameLayout.HORIZONTAL_FULL);
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
        params.setItemScaleFixedX(PingPaiDimension.getDimensionPixelSize(R.dimen.dp_15));
        params.setItemScaleFixedY(PingPaiDimension.getDimensionPixelSize(R.dimen.dp_15));
        params.setFrameRate(6);
        params.setItemScaleValue(1.06f, 1.06f);

        params.setFrameRate(3, 3);
        params.setFocusDrawable(mContext.getResources().getDrawable(R.drawable.jhs_focus));
        params.setFocusShadowDrawable(mContext.getResources().getDrawable(R.drawable.jhs_focus));

        return params;
    }

    // 创建页面视图
    public void onSetLayout(Context context) {

        // row 1
        CategoryItemView category1 = new CategoryItemView(context);
        category1.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory1 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory1.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL;
        paramsCategory1.topMargin = TOPMATGIN;
        addView(category1, paramsCategory1);
        items.add(category1);

        CategoryItemView category2 = new CategoryItemView(context);
        FrameLayout.LayoutParams paramsCategory2 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        category2.setFocusable(true);
        paramsCategory2.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL;
        paramsCategory2.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL;
        addView(category2, paramsCategory2);
        items.add(category2);

        CategoryItemView category3 = new CategoryItemView(context);
        category3.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory3 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory3.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL;
        paramsCategory3.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL + ITEM_HEIGHT_TOTAL;
        addView(category3, paramsCategory3);
        items.add(category3);

        // row 2
        CategoryItemView category4 = new CategoryItemView(context);
        category4.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory4 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory4.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL;
        paramsCategory4.topMargin = TOPMATGIN;
        addView(category4, paramsCategory4);
        items.add(category4);

        CategoryItemView category5 = new CategoryItemView(context);
        FrameLayout.LayoutParams paramsCategory5 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        category5.setFocusable(true);
        paramsCategory5.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL;
        paramsCategory5.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL;
        addView(category5, paramsCategory5);
        items.add(category5);

        CategoryItemView category6 = new CategoryItemView(context);
        category6.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory6 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory6.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL;
        paramsCategory6.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL + ITEM_HEIGHT_TOTAL;
        addView(category6, paramsCategory6);
        items.add(category6);

        // row 3
        CategoryItemView category7 = new CategoryItemView(context);
        category7.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory7 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory7.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL;
        paramsCategory7.topMargin = TOPMATGIN;
        addView(category7, paramsCategory7);
        items.add(category7);

        CategoryItemView category8 = new CategoryItemView(context);
        FrameLayout.LayoutParams paramsCategory8 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        category8.setFocusable(true);
        paramsCategory8.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL;
        paramsCategory8.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL;
        addView(category8, paramsCategory8);
        items.add(category8);

        CategoryItemView category9 = new CategoryItemView(context);
        category9.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory9 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory9.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL;
        paramsCategory9.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL + ITEM_HEIGHT_TOTAL;
        addView(category9, paramsCategory9);
        items.add(category9);

        // row 4
        CategoryItemView category10 = new CategoryItemView(context);
        category10.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory10 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory10.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL
                + ITEM_WIDTH_TOTAL;
        paramsCategory10.topMargin = TOPMATGIN;
        addView(category10, paramsCategory10);
        items.add(category10);

        CategoryItemView category11 = new CategoryItemView(context);
        FrameLayout.LayoutParams paramsCategory11 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        category11.setFocusable(true);
        paramsCategory11.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL
                + ITEM_WIDTH_TOTAL;
        paramsCategory11.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL;
        addView(category11, paramsCategory11);
        items.add(category11);

        CategoryItemView category12 = new CategoryItemView(context);
        category6.setFocusable(true);
        FrameLayout.LayoutParams paramsCategory12 = new FrameLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT);
        paramsCategory12.leftMargin = ITEM_GAP_PARENTLEFT + ITEM_GAP_VERTICAL + ITEM_WIDTH_TOTAL + ITEM_WIDTH_TOTAL
                + ITEM_WIDTH_TOTAL;
        paramsCategory12.topMargin = TOPMATGIN + ITEM_HEIGHT_TOTAL + ITEM_HEIGHT_TOTAL;
        addView(category12, paramsCategory12);
        items.add(category12);
    }

    //    private int getDp(int value) {
    //        return (int) (value * SystemUtil.getMagnification());
    //    }

    public void updateItem(HomeCatesResponse categorys, int currentPos, View.OnClickListener Itemclick) {

        int position = currentPos;
        for (int i = 0; i < items.size(); i++) {
            CategoryItemView item = items.get(i);
            if (item != null) {
                if (position < currentPos + mEachPageSize && position < categorys.getCates().size()) {
                    HomeCatesBo categoryItem = categorys.getCates().get(position);
                    item.updateView(categoryItem);
                    item.setVisibility(View.VISIBLE);
                    item.setOnClickListener(Itemclick);
                    item.setId(position);
                    position++;
                } else {
                    item.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setFocusChangeListener(IFocusChanged listener) {
        mFocusChanged = listener;
    }

    public void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    // 翻页时确定焦点
    public void findFocus(int prePage, int position) {
        if (prePage == mCurrentPage) {
            return;
        }

        // 往前翻页
        if (prePage > mCurrentPage) {
            position = mEachPageSize - position;
            if (position < 0) {
                position = 0;
            }
        } else {
            position = position - mEachRowCount;
            if (position > items.size()) {
                position = items.size();
            }
        }

        if (position >= 0 && position < items.size()) {
            setIndexOfSelectedView(position);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keycode = event.getKeyCode();
        int selectedIndex = getIndexOfSelectedView() + 1;
        switch (keycode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (selectedIndex >= mEachRowCount && selectedIndex % mEachRowCount == 0) {
                    return false;
                }
                break;

            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

        // 失去焦点
        if (!gainFocus) {
            if (mFocusChanged != null) {
                mFocusChanged.onFocusOut(mCurrentPage, getIndexOfSelectedView());
            }
        }
        // 得到焦点
        else {
            if (mFocusChanged != null) {
                mFocusChanged.onFocusIn(this, mCurrentPage);
            }
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        ((View) getParent()).bringToFront();
    }
}
