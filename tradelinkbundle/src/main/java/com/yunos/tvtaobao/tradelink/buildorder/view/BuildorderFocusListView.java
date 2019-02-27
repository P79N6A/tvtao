package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.buildorder.PurchaseViewType;


public class BuildorderFocusListView extends TabFocusListView {

    public BuildorderFocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BuildorderFocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuildorderFocusListView(Context context) {
        super(context);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawChildViewDivider();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getSelectedView() instanceof BuildOrderItemView) {
            boolean result = getSelectedView().onKeyUp(keyCode, event);
            if (result) return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getSelectedView() instanceof BuildOrderItemView) {
            boolean result = getSelectedView().onKeyDown(keyCode, event);
            if (result) return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 画子view的分割线
     */
    public void drawChildViewDivider() {
        ListAdapter listAdapter = getAdapter();
        if (listAdapter == null) {
            return;
        }
        int selectpos = getSelectedItemPosition();
        boolean isfocused = isFocused();
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        for (int index = firstVisiblePosition; index < lastVisiblePosition; index++) {
            View view = getChildAt(index - firstVisiblePosition);
            if (view != null && view instanceof BuildOrderItemView) {
                BuildOrderItemView buildOrderItemView = (BuildOrderItemView) view;
                boolean drawdivider = true;
                if (isfocused) {
                    if (index == selectpos) {
                        drawdivider = false;
                    } else if (selectpos > 0 && index == selectpos - 1) {
                        drawdivider = false;
                    }
                }
                int typeCurrent = listAdapter.getItemViewType(index);
                if (typeCurrent == PurchaseViewType.INPUT.getIndex()) {
                    drawdivider = false;
                }
                if (index + 1 < lastVisiblePosition) {
                    int typeNext = listAdapter.getItemViewType(index + 1);
                    if (typeNext == PurchaseViewType.INPUT.getIndex()) {
                        drawdivider = false;
                    }
                }
                AppDebug.i(TAG, "drawChildViewDivider --> drawdivider = " + drawdivider + "; firstVisiblePosition = "
                        + firstVisiblePosition + "; lastVisiblePosition = " + lastVisiblePosition + "; index = "
                        + index);
                buildOrderItemView.drawDivider(drawdivider);
            }
        }
    }

}
