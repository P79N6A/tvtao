package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusPositionManager;

import java.lang.reflect.Field;

public class DetailFocusPositionManager extends FocusPositionManager {

    public DetailFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DetailFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailFocusPositionManager(Context context) {
        super(context);
    }

    /**
     * 根据反射设置首次获得焦点的VIEW
     * @param firstRequestChild
     * @return
     */
    public boolean setDetailFirstFocusedView(View firstRequestChild) {
        boolean result = false;
        try {
            Class<?> focusPositionManager = FocusPositionManager.class;
            Field mFocusedField = focusPositionManager.getDeclaredField("mFocused");
            if (mFocusedField != null) {
                mFocusedField.setAccessible(true);
                mFocusedField.set(this, firstRequestChild);
                result = true;
            } 
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
