package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.core.common.AppDebug;

import java.lang.reflect.Field;

/**
 * 解决更新数据的同时按键概率性导致focus框的问题
 * @author tingmeng.ytm
 *
 */
public class RefreshDataFocusFlipGridView extends FocusFlipGridView {
    private final String TAG = "RefreshDataFocusFlipGridView";
    public RefreshDataFocusFlipGridView(Context context) {
        super(context);
        init();
    }
    
    public RefreshDataFocusFlipGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public RefreshDataFocusFlipGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        int flags = getGroupFlags();
        flags &= ~CLIP_TO_PADDING_MASK;
        setGroupFlags(flags);
    }
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        int selectedPos = getSelectedItemPosition();
        AppDebug.i(TAG, "preOnKeyDown selectedPos="+selectedPos+" count="+getHeaderViewsCount());
        if(selectedPos < getHeaderViewsCount()){
            //headerView
            View view = getSelectedView();
            if(view instanceof FocusRelativeLayout){
                // 跟原生控件的区别就是只有上下键的时候如果进入了headerView就不做处理
                if ((keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) && 
                        !isFlipFinished()) {
                    return false;
                } else {
                    FocusRelativeLayout headerView = (FocusRelativeLayout)view;
                    boolean header = headerView.preOnKeyDown(keyCode, event);
                    if(header == true){
                        return true;
                    }
                }
            }
        }
        boolean ret = super.preOnKeyDown(keyCode, event);
        AppDebug.i(TAG, "preOnKeyDown ret="+ret);
        return ret;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyDown(keyCode, event);
        AppDebug.i(TAG, "onKeyDown ret="+ret+" adapter="+getAdapter()+" childCount="+getChildCount());
        return ret;
    }
    
    @Override
    protected void layoutChildren() {
        AppDebug.i(TAG, "layoutChildren mNextSelectedPosition="+mNextSelectedPosition+" mSelectedPosition="+mSelectedPosition);
        // 如果两个不相等就强制更新参数，因为在更新数据的同时按键对item进行选中就导致focus参数不更新
        if (mNextSelectedPosition != mSelectedPosition) {
            boolean success = setNeedResetParamEnable();
            AppDebug.i(TAG, "layoutChildren success="+success);
        }
        super.layoutChildren();
    }
    
    /**
     * 通过反射设置重新设置focus的参数
     * @return
     */
    protected boolean setNeedResetParamEnable() {
        try {
            Class<?> flipGridViewClass = FocusFlipGridView.class;
            Field  mNeedResetParamField = flipGridViewClass.getDeclaredField("mNeedResetParam");
            mNeedResetParamField.setAccessible(true);
            mNeedResetParamField.setBoolean(this, true);
            return true;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
