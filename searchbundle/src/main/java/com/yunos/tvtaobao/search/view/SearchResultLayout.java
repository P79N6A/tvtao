package com.yunos.tvtaobao.search.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.lib.DisplayUtil;
import com.yunos.tvtaobao.biz.base.RightSideView;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SearchResultLayout extends ConstraintLayout {
    String TAG = "SearchResultLayout";

    public SearchResultLayout(Context context) {
        super(context);
    }

    public SearchResultLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchResultLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View view = super.focusSearch(focused, direction);
        AppDebug.d(TAG, "focusSearch focused="+focused+"" +
                " result=" + view);
        if (view instanceof TextView) {
            if (direction == FOCUS_LEFT) {
                ViewParent viewParent = view.getParent();
                if (viewParent != null && viewParent instanceof TabView) {
                    TabView tabView = (TabView) viewParent;
                    View nextView = tabView.findNextFocused(focused, direction);
                    if (nextView != null) {
                        return nextView;
                    }

                }
            }
        }
        if (view instanceof ImageView && direction == FOCUS_RIGHT) {
            ViewParent viewParent = view.getParent();
            if (viewParent instanceof RelativeLayout) {
                if (viewParent.getParent() != null && viewParent.getParent() instanceof RightSideView) {
                    RightSideView rightSideView = (RightSideView) viewParent.getParent();
                    View nextView = rightSideView.findNextFocused(focused, direction);
                    if (nextView != null) {
                        return nextView;
                    }
                }
            }
        }

        return view;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean rtn = super.dispatchKeyEvent(event);
        AppDebug.d(TAG, "dispatchKeyEvent focusedChild="+DisplayUtil.getViewStr(getFocusedChild()));
        AppDebug.i(TAG, "dispatchKeyEvent rtn="+rtn);
        return rtn;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        AppDebug.i(TAG, "requestChildFocus child : " + child + " ,focused : " + focused);
    }
}
