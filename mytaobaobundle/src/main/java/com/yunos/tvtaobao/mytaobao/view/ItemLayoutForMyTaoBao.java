package com.yunos.tvtaobao.mytaobao.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.common.DrawRect;
import com.yunos.tvtaobao.mytaobao.R;

public class ItemLayoutForMyTaoBao extends LinearLayout implements ItemListener {

    private TextView title;
    private ImageView image;
    private ImageView imageFlow;
    private ImageView imageIcon;
    private TextView detail;
    private String attrTitle;
    private String attrDetail;
    private int attrImage;

    private float adjustLeft = 0;
    private float adjustTop = 0;
    private float adjustRight = 0;
    private float adjustBottom = 0;

    private Context mContext;

    private DrawRect mDrawRect;
    private View mItemLayout;

    public ItemLayoutForMyTaoBao(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initAttrs(context, attrs);
        initViews(context);
    }

    public ItemLayoutForMyTaoBao(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(context, attrs);
        initViews(context);
    }

    public ItemLayoutForMyTaoBao(Context context) {
        super(context);
        mContext = context;
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ytsdk);
        attrTitle = attr.getString(R.styleable.ytsdk_mtTitle);
        attrDetail = attr.getString(R.styleable.ytsdk_mtDetail);
        attrImage = attr.getResourceId(R.styleable.ytsdk_mtImage, -1);
        AppDebug.d("ItemLayoutForMyTaoBao", " attrTitle " + attrTitle + " attrDetail " + attrDetail + " attrImage " + attrImage);
        attr.recycle();
    }

    private void initViews(Context context) {
        mItemLayout = LayoutInflater.from(context).inflate(R.layout.ytsdk_itemlayout_for_mytaobao, null);
        title = (TextView) mItemLayout.findViewById(R.id.title);
        image = (ImageView) mItemLayout.findViewById(R.id.image);
        imageFlow = (ImageView) mItemLayout.findViewById(R.id.image_flow);
        imageIcon = (ImageView) mItemLayout.findViewById(R.id.image_icon);
        detail = (TextView) mItemLayout.findViewById(R.id.detail);
        title.setText(attrTitle);
        detail.setText(attrDetail);
        image.setImageResource(attrImage);
        mDrawRect = new DrawRect(this);
        addView(mItemLayout);
    }

    private void adjustParam(Rect rect) {
        adjustLeft = mContext.getResources().getDimension(R.dimen.dp_14);
        adjustTop = mContext.getResources().getDimension(R.dimen.dp_18);
        adjustRight = mContext.getResources().getDimension(R.dimen.dp_14);
        adjustBottom = mContext.getResources().getDimension(R.dimen.dp_17);

        int left = (int) Math.rint(adjustLeft); // 四舍五入取整
        int top = (int) Math.rint(adjustTop);
        int right = (int) Math.rint(adjustRight);
        int bottom = (int) Math.rint(adjustBottom);

        rect.left += left;
        rect.top += top;
        rect.right -= right;
        rect.bottom -= bottom;
    }

    public ImageView getImageIcon() {
        return imageIcon;
    }

    public ImageView getImageFlow() {
        return imageFlow;
    }

    public void setDetail(String detail) {
        this.detail.setText(detail);
    }

    /**
     * 该view是否被focus
     * @param focus
     */
    public void setFocus(boolean focus) {
        if (!focus) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawRect.drawRect(canvas, mItemLayout);
    }

    @Override
    public boolean isScale() {
        return true;
    }

    public Rect getFocusedRect() {
        Rect rect = new Rect();
        getFocusedRect(rect);
        adjustParam(rect);
        return rect;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = getFocusedRect();
        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
