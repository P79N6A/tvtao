package com.yunos.tvtaobao.search.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.search.R;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
@SuppressLint("AppCompatCustomView")
public class TabTextView extends TextView {
    private static final String TAG = "TabTextView";
    private FocusState curState = FocusState.UNFOCUSED;

    public enum FocusState {
        //未选中，聚焦，非聚焦
        UNSELECTED, FOCUSED, UNFOCUSED
    }

    public TabTextView(Context context) {
        super(context);
        init(context);
    }

    public TabTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    public TabTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
    }

    public void setState(FocusState state) {
        this.curState = state;
        setFocusBackGround(state);
    }

    @Override
    public int getNextFocusUpId() {
        int id = super.getNextFocusUpId();
        AppDebug.i(TAG, "getNextFocusUpId id : " + id);
        return id;
    }

    @Override
    public int getNextFocusRightId() {
        int id = super.getNextFocusRightId();
        AppDebug.i(TAG, "getNextFocusRightId id : " + id);
        return id;
    }

//    @Override
//    public View focusSearch(int direction) {
//        View view = super.focusSearch(direction);
//        if (view instanceof TabTextView) {
//            setState(FocusState.UNFOCUSED);
//        } else {
//            setState(FocusState.UNSELECTED);
//        }
//        AppDebug.i(TAG, "focusSearch view : " + view + " ,direction : " + direction);
//        return view;
//    }

    @Override
    public View findFocus() {
        View view = super.findFocus();
        AppDebug.i(TAG, "findFocus view : " + view);
        return view;
    }

//    @Override
//    public void onFocusChange(View view, boolean b) {
//        AppDebug.i(TAG, "onFocusChange this : " + this);
//        AppDebug.i(TAG, "onFocusChange view : " + view + " ,b : " + b);
//        if (b) {
//            setState(FocusState.FOCUSED);
//        }
//    }

    private void setFocusBackGround(FocusState state) {
        switch (state) {
            case FOCUSED:
                setBackgroundResource(R.drawable.bg_tab_text_focus);
                break;
            case UNFOCUSED:
                setBackgroundDrawable(null);
                break;
            case UNSELECTED:
                setBackgroundResource(R.drawable.bg_tab_text_unfocus);
                break;
        }
    }

}
