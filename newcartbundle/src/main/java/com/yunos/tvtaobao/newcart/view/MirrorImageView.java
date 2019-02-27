package com.yunos.tvtaobao.newcart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ouzy on 2018/7/31.
 * desc:镜像imageview
 */
@SuppressLint("AppCompatCustomView")
public class MirrorImageView extends ImageView {

    public MirrorImageView(Context context) {
        super(context);
    }

    public MirrorImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MirrorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MirrorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.scale(1, -1, getWidth() / 2, getHeight() / 2);
        super.draw(canvas);
    }
}
