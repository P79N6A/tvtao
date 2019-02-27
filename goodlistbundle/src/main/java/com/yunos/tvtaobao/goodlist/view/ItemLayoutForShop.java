/**
 * 
 */
package com.yunos.tvtaobao.goodlist.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.Cat;
import com.yunos.tvtaobao.goodlist.R;

/**
 * @author yunzhong.qyz
 */
public class ItemLayoutForShop extends FrameLayout implements ItemListener {

    private int mPosition = -1;
    private Cat mCat = null;

    /**
     * @param context
     */
    public ItemLayoutForShop(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ItemLayoutForShop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ItemLayoutForShop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setCat(Cat cat) {
        mCat = cat;
    }

    public Cat getCat() {
        return mCat;
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {

    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {

    }

    @Override
    public FocusRectParams getFocusParams() {

        View backgroud = this.findViewById(R.id.shop_classify_imageview);
        Rect r = new Rect();
        r.setEmpty();

        //        View backgroud =  this.findViewById(R.id.shop_classify_textview);
        //        Rect r = new Rect();
        //        r.setEmpty();

        r.left = backgroud.getLeft();
        r.right = backgroud.getRight();
        r.top = backgroud.getTop();
        r.bottom = backgroud.getBottom();

        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isScale() {
        return false;
    }

    public Rect getFocusedRect() {
        Rect r = new Rect();
        //        getFocusedRect(r);

        r.setEmpty();
        r.right = getItemWidth();
        r.bottom = getItemHeight();
        return r;
    }

    public void setBackgroudView(int visibility) {
        ImageView backgroud = (ImageView) this.findViewById(R.id.shop_classify_imageview);
        backgroud.setVisibility(View.VISIBLE);
        if (visibility == View.VISIBLE) {
            backgroud.setBackgroundResource(R.drawable.ytsdk_ui2_goodlist_left_select);
        } else {
            backgroud.setBackgroundColor(Color.TRANSPARENT);
        }
        AppDebug.i("ItemLayoutForShop", "setBackgroudView  -->   visibility  = " + visibility + "; backgroud = "
                + backgroud);
    }

    public void setTextViewFocusable(boolean focusable) {
        TextView classify = (TextView) this.findViewById(R.id.shop_classify_textview);
        classify.setFocusable(focusable);
    }

    public TextView getItemTextView() {
        TextView classify = (TextView) this.findViewById(R.id.shop_classify_textview);
        return classify;
    }
}
