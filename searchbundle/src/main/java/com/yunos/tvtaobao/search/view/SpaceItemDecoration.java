package com.yunos.tvtaobao.search.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class SpaceItemDecoration extends ItemDecoration {
    private int rightSpace;
    private int bottomSpace;

    public SpaceItemDecoration(int rightSpace) {
        this.rightSpace = rightSpace;
    }
    public SpaceItemDecoration(int rightSpace,int bottomSpace) {
        this.rightSpace = rightSpace;
        this.bottomSpace = bottomSpace;
    }


    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        outRect.right = this.rightSpace;
        outRect.bottom = this.bottomSpace;

    }
}
