package com.yunos.tvtaobao.mytaobao.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.common.DrawRect;
import com.yunos.tvtaobao.mytaobao.R;

public class ItemLayoutForCollect extends LinearLayout implements ItemListener {

    private int rectLOffset = 1;
    private int rectROffset = 0;
    private int rectTOffset = 2;
    private int rectBOffset = 0;

    private int mPosition = -1;

    private DrawRect mDrawRect;

    public ItemLayoutForCollect(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitItemLayoutForCollect(context);

    }

    public ItemLayoutForCollect(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitItemLayoutForCollect(context);
    }

    public ItemLayoutForCollect(Context context) {
        super(context);
        onInitItemLayoutForCollect(context);
    }

    private void onInitItemLayoutForCollect(Context context) {
        rectLOffset = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
        rectTOffset = context.getResources().getDimensionPixelSize(R.dimen.dp_2);

        rectROffset = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
        rectBOffset = context.getResources().getDimensionPixelSize(R.dimen.dp_3);
        mDrawRect = new DrawRect(this);
    }

    /**
     * 设置是否被选中
     * @param selected
     */
    public void setSelect(boolean selected) {
        if (!selected) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawRect.drawRect(canvas, this);
    }

    public void setImage(int visible) {
    }

    public void onSetPosition(int position) {
        mPosition = position;
    }

    public int onGetPosition() {
        return mPosition;
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {
    }

    @Override
    public FocusRectParams getFocusParams() {

        Rect focuse = getFocusedRect();

        onAdjustFocusedRect(focuse);

        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);

        return focusRectParams;
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
        return true;
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    private void onAdjustFocusedRect(Rect rt) {
        rt.left += rectLOffset;
        rt.top += rectTOffset;
        rt.right -= rectROffset;
        rt.bottom -= rectBOffset;
    }

    public ImageView getItemImageView() {
        ImageView itemImageView = (ImageView) this.findViewById(R.id.collect_item_img);
        return itemImageView;
    }

    public TextView getItemSummaryView() {
        TextView itemSummaryView = (TextView) this.findViewById(R.id.collect_item_summary);
        return itemSummaryView;
    }

    public TextView getItemPriceView() {
        TextView itemPriceView = (TextView) this.findViewById(R.id.collect_item_price);
        return itemPriceView;
    }

    public TextView getItemCollectNumView() {
        TextView itemcollectNumView = (TextView) this.findViewById(R.id.collect_item_collector_count);
        return itemcollectNumView;
    }
}
