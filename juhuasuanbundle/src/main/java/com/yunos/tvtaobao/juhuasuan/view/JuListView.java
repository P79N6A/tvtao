package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

public class JuListView extends ListView {

    private int mSelectionItemTop = 0;

    public JuListView(Context context) {
        super(context);
    }

    public JuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onSetItemPosition(int selectionItemTop) {
        mSelectionItemTop = selectionItemTop;
    }

    /**
     * 使listview 获取焦点时，选中上次失去焦点时的item，而不是就近的item
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        //        if (gainFocus) {
        //            setSelection(lastSelectItem);
        //        }
        if (gainFocus) {
            setSelectionFromTop(lastSelectItem, mSelectionItemTop);
        }
    }
}
