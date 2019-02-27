package com.yunos.tvtaobao.biz.focus_impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by GuoLiDong on 2018/10/15.
 * 焦点树根，一个页面唯一，用于组织焦点流程
 */

public class FocusRoot extends FocusArea {
    public static final String TAG_FLAG_FOR_ROOT = "root";
    public FocusRoot(@NonNull Context context) {
        this(context, null);
    }

    public FocusRoot(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusRoot(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getNode().setRoot(true);
        setTag(TAG_FLAG_FOR_ROOT);
    }

    @Override
    public View focusSearch(int direction) {
        //return super.focusSearch(direction);
        return null;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        //return super.focusSearch(focused, direction);
        return null;
    }



    private FocusNode currFocusOn;


    public FocusNode getCurrFocusOn() {
        return currFocusOn;
    }

    public void setCurrFocusOn(FocusNode currFocusOn) {
        this.currFocusOn = currFocusOn;
    }
}
