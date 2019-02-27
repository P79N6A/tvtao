package com.yunos.tvtaobao.search.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;


/**
 * Created by xtt
 * on 2019/1/7
 * desc
 */
public class CustomImageSpan extends ImageSpan {
    public CustomImageSpan(Context arg0, int arg1) {
        super(arg0, arg1);
    }

    public CustomImageSpan(@NonNull Context context, @NonNull Bitmap b) {
        super(context, b);
    }

    public CustomImageSpan(@NonNull Context context, @NonNull Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            FontMetricsInt fmPaint=paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight=rect.bottom-rect.top;

            int top= drHeight/2 - fontHeight/4;
            int bottom=drHeight/2 + fontHeight/4;

            fm.ascent=-bottom;
            fm.top=-bottom;
            fm.bottom=top;
            fm.descent=top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom-top) - b.getBounds().bottom)/2+top;
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}

