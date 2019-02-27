package com.yunos.tvtaobao.live.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.live.adapter.TBaoShopAdapter;

/**
 * Created by pan on 16/10/16.
 */

public class ZPListView extends RecyclerView {
    private final String TAG = "ZPListView";
    private int count = 0;
    private View lastFocusView;

    public ZPListView(Context context) {
        super(context);
        init(context);
    }

    public ZPListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZPListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
/*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (getBottomItem() != null && getBottomItem().hasFocus()) {
                return true;
            }
            View focusChild = getFocusedChild();
            if (focusChild != null && getLayoutManager() != null) {
                int position = getLayoutManager().getPosition(focusChild);
                if (position < getLayoutManager().getItemCount() - 1 && getLayoutManager().findViewByPosition(position + 1) == null) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }*/


    private void init(Context context) {

    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof TBaoShopAdapter) {
            TBaoShopAdapter tBaoShopAdapter = (TBaoShopAdapter) adapter;
            tBaoShopAdapter.setOnFocusChangeListener(new TBaoShopAdapter.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    lastFocusView = view;
                    if (b) {
                        refreshFocusItemToCenter(view);
                    }
                }
            });

            tBaoShopAdapter.setHotFocusListsner(new TBaoShopAdapter.OnHotProductFocusListsner() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    lastFocusView = view;
                    if(b){
                        //滑倒顶部
                        smoothScrollToPosition(0);
                    }
                }
            });
        }
    }

    public View getLastFocusView() {
        return lastFocusView;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private View getBottomItem() {
        if (count != 0)
            return getLayoutManager().findViewByPosition(count - 1);


        return null;
    }

    public void refreshFocusItemToCenter(View tView){
//        View tView = getLayoutManager().getFocusedChild();
        AppDebug.i(TAG, "refreshFocusItemToCenter view : " + tView);
        if (tView == null) {
            return;
        }
        int[] tPosition = new int[2];
        tView.getLocationInWindow(tPosition);
        int tDes = (int) ((this.getY() + this.getHeight()/2) - tView.getHeight() * tView.getScaleY()/2);
        if (tPosition != null && tPosition[1] != tDes) {
            this.smoothScrollBy(0, tPosition[1] - tDes);
            postInvalidate();
        }
    }

    public void setCurrentItemToCenter(int position) {
        if (getLayoutManager().getChildCount() <= 0)
            return;

        View itemView = getLayoutManager().getChildAt(0);
        int itemHeight = itemView.getHeight();
        int scrollY = itemHeight * position;
        int distanceY = scrollY - getHeight() / 2 + itemHeight / 2;

        scrollBy(0, distanceY);
    }
}
