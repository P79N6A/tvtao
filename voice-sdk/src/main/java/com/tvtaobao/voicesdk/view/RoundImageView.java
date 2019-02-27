package com.tvtaobao.voicesdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tvtaobao.voicesdk.R;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/7/31
 *     desc :
 *     version : 1.0
 * </pre>
 */

@SuppressLint("AppCompatCustomView")
public class RoundImageView extends ImageView {
    private Paint paint;
    /**
     * 个人理解是
     *
     * 这是画圆的半径
     */
    private int round = getContext().getResources().getDimensionPixelSize(R.dimen.dp_6);
    private Paint paint2;

    private boolean leftUp = true, leftDown = true, rightUp = true, rightDown = true;

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundImageView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
            round = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, round);
            round = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, round);
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            round = (int) (round * density);
            round = (int) (round * density);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    public void setRound(boolean leftUp, boolean leftDown, boolean rightUp, boolean rightDown) {
        this.leftUp = leftUp;
        this.leftDown = leftDown;
        this.rightUp = rightUp;
        this.rightDown = rightDown;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        if (leftUp)
            drawLiftUp(canvas2);
        if (leftDown)
            drawLiftDown(canvas2);
        if (rightUp)
            drawRightUp(canvas2);
        if (rightDown)
            drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }

    private void drawLiftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, round);
        path.lineTo(0, 0);
        path.lineTo(round, 0);
        path.arcTo(new RectF(0, 0, round * 2, round * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight()-round);
        path.lineTo(0, getHeight());
        path.lineTo(round, getHeight());
        path.arcTo(new RectF(
                        0,
                        getHeight()-round*2,
                        0+round*2,
                        getHeight()),
                90,
                90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), round);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - round, 0);
        path.arcTo(new RectF(getWidth() - round * 2, 0, getWidth(), 0 + round * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth()-round, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight()-round);
        path.arcTo(new RectF(
                getWidth()-round*2,
                getHeight()-round*2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

}

