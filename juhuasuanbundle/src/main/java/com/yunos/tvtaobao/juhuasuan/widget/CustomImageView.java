package com.yunos.tvtaobao.juhuasuan.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.view.ImageView;

public class CustomImageView extends ImageView {

    private String TAG = "CustomImageView";

    private Context mContext = null;
    private Bitmap mBitmap = null;

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        onInitCustomImageView(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        onInitCustomImageView(context);
    }

    public CustomImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        onInitCustomImageView(context);

    }

    public void onInitCustomImageView(Context context) {
        mContext = context;

    }

    public void onSetDispalyBitmap(Bitmap dismap) {

        if (dismap == null)
            return;

        mBitmap = dismap;
        setImageBitmap(mBitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        if (mBitmap == null)
            return;
        if (mBitmap.isRecycled()) {
            AppDebug.e(TAG, "mBitmap.isRecycled ---> mBitmap = " + mBitmap);
            return;
        }

        super.onDraw(canvas);
    }

}
