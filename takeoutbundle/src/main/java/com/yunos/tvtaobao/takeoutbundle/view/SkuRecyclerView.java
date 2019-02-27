package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;


/**
 * Created by chenjiajuan on 17/12/20.
 *
 * @describe  sku属性
 */

public class SkuRecyclerView extends RecyclerView {
    private  boolean canFocus=false;
    public SkuRecyclerView(Context context) {
        super(context);
    }

    public SkuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SkuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
    }


    public  void setCanFocus(boolean canFocus){
        this.canFocus=canFocus;

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.e("TAG","event= "+event.getAction());
//        if (!canFocus){
//            return true;
//        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public View focusSearch(int direction) {
//        Log.e("TAG","direction......"+direction);
//        if (!canFocus){
//            return null;
//        }
        return super.focusSearch(direction);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        if (!canFocus){
//            return true;
//        }
//        Log.e("TAG","requestFocus.......");
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
//        Log.e("TAG","requestChildFocus.......");
        super.requestChildFocus(child, focused);
    }
}
