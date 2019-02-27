/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.view
 * FILE NAME: MenueFocusRelativeLayout.java
 * CREATED TIME: 2015年3月19日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.menu.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.menu.R;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年3月19日 下午4:32:06
 */
public class MenueItemRelativeLayout extends MenueRelativeLayout {

    private View mMenuImage;

    public MenueItemRelativeLayout(Context context) {
        super(context);
        init();
    }

    public MenueItemRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenueItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        if (mMenuImage != null) {
            mMenuImage.getFocusedRect(r);
            offsetDescendantRectToMyCoords(mMenuImage, r);
        } else {
            getFocusedRect(r);
        }
        return r;
    }

    @Override
    protected void onFinishInflate() {
        mMenuImage = findViewById(R.id.menu_item_image);
        super.onFinishInflate();
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        focuse.bottom -= getResources().getDimension(R.dimen.dp_1);
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
    }

    private void init() {
        mMenuImage = findViewById(R.id.menu_item_image);
    }
}
