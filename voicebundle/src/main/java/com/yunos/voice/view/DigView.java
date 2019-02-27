package com.yunos.voice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yunos.voice.R;

/**
 * Created by pan on 2017/7/28.
 */

public class DigView extends View {
    private Rect mRect;

    public DigView(Context context) {
        super(context);
    }

    public DigView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDig(View v) {
        Log.e("DigView", "setDig left : " + v.getLeft() + " ,top : " + v.getTop());
        Log.e("DigView", "setDig width : " + v.getWidth() + " ,height : " + v.getHeight());
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        mRect = new Rect(loc[0], loc[1], loc[0] + v.getWidth(), loc[1] + v.getHeight());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("DigView", "onDraw Rect : " + mRect + "  ," + mRect.width() + " ," + mRect.height());
        if (mRect != null) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            Paint paint = new Paint();
            int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
            paint.setColor(getContext().getResources().getColor(R.color.color_000_7f));
            canvas.drawRect(0, 0, canvasWidth, canvasHeight, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(mRect, paint);
            paint.setXfermode(null);
            canvas.restoreToCount(layerId);
        }
    }
}
