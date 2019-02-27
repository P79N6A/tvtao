/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.dialog
 * FILE NAME: DialogFocusNoDeepLinearLayout.java
 * CREATED TIME: 2015年6月5日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.dialog;


import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tvtaobao.biz.widget.FocusNoDeepLinearLayout;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年6月5日 下午3:23:15
 */
public class DialogFocusNoDeepLinearLayout extends FocusNoDeepLinearLayout {

    public DialogFocusNoDeepLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DialogFocusNoDeepLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogFocusNoDeepLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean isScale() {
        return false;
    }
}
