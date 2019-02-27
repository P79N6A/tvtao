package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.utils.AnimationUtils;


public class UpperLayer extends View {
    private static float SCREENWIDTH, SCREENHEIGHT;
    private static boolean AnimationEnd = false;
    int i = 1;
    private String mTitle;// = "游戏视频";
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private RectF StripeArea;
    private float pxStripeLeft, pxStripeTop, pxStripeRight, pxStripeBottom,pxStripMarigin,pxTextMarigin,pxTextPaddingTop;
    private float A_X, A_Y, B_X, B_Y, C_X, C_Y, D_X, D_Y;
    private float pxSpace;
    private float textLeft,textLeftMargin;

    private Path path;
    private Path finalArea;
    private float textSize;
    private float obliqueMargin;

    public UpperLayer(Context context) {
        super(context);
        init();
    }

    public UpperLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpperLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isAnimationEnd() {
        return AnimationEnd;
    }

    public void init() {
    	mTitle = getResources().getString(R.string.str_remind_info_head);
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaint);
        AnimationEnd = false;


        pxStripeRight = getResources().getDimension(R.dimen.upperlayer_stripRight);
        pxStripeLeft = pxStripeRight;
        pxStripeTop = getResources().getDimension(R.dimen.upperlayer_stripTop);;
        pxStripeBottom = getResources().getDimension(R.dimen.upperlayer_stripBottom);
        pxStripMarigin =getResources().getDimension(R.dimen.upperlayer_StripMarigin);
        pxTextMarigin =getResources().getDimension(R.dimen.upperlayer_TextMarigin);
        obliqueMargin = getResources().getDimension(R.dimen.oblique_margin);
        textLeft = getResources().getDimension(R.dimen.UL_textleft);
        textLeftMargin = getResources().getDimension(R.dimen.UL_textleftMargin);
        textSize = getResources().getDimension(R.dimen.UL_textSize);
        pxTextPaddingTop = getResources().getDimension(R.dimen.UL_textTop);
        path = new Path();
//        int startcolor = 0xff0099FF;
        int startcolor = 0xffec5c1f;
//        int endcolor = 0xffCC3399;
        int endcolor = 0xffec872f;
        LinearGradient lg = new LinearGradient(pxStripeLeft, pxStripeTop, pxStripeRight, pxStripeTop, startcolor, endcolor, Shader.TileMode.MIRROR);
        StripeArea = new RectF(pxStripeLeft, pxStripeTop, pxStripeRight, pxStripeBottom);
        mPaint.setShader(lg);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTypeface(AnimationUtils.getXHTypeface());
    }

    private void updateLocation() {

        pxSpace = getResources().getDimension(R.dimen.oblique_line_up_end_y) / SCREENHEIGHT * getHeight();

        //计算截断区域
        A_Y = pxStripeTop;
        A_X = (pxStripeTop - pxSpace) * 5 / 4 - 4;
        B_X = pxStripeRight;
        B_Y = A_Y;
        C_X = B_X;

        C_Y = pxStripeBottom;
        D_Y = C_Y;
        D_X = (D_Y - pxSpace) * 5 / 4 - 4;


        //设置截断路径
        path.moveTo(A_X, A_Y);
        path.lineTo(B_X, B_Y);
        path.lineTo(C_X, C_Y);
        path.lineTo(D_X, D_Y);
        path.lineTo(A_X, A_Y);


    }

    @Override
    protected void onDraw(Canvas canvas) {

        i++;
        updateLocation();
        updateScripeLocation();
//        pxSpace = getResources().getDimension(R.dimen.oblique_line_up_end_y)/SCREENHEIGHT*getHeight();
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));

        canvas.clipPath(path, Region.Op.DIFFERENCE);
        if (!AnimationEnd) {
            canvas.drawRect(StripeArea, mPaint);
        } else {
            canvas.drawPath(finalArea, mPaint);
        }
        canvas.drawText(mTitle, textLeft, pxStripeTop + pxTextPaddingTop, mTextPaint);

    }

    public void updateScripeLocation() {
        if (pxStripeLeft >=pxStripMarigin) {

            StripeArea.set(pxStripeLeft, pxStripeTop, pxStripeRight, pxStripeBottom);
            pxStripeLeft = pxStripeLeft - i*2;
            if (textLeft > pxTextMarigin+textLeftMargin)
                textLeft = textLeft - i *2;
        } else {
            textLeft = pxTextMarigin+textLeftMargin;
            AnimationEnd = true;
            finalArea = new Path();
            finalArea.moveTo(pxStripMarigin, pxStripeTop);
            finalArea.lineTo(A_X-obliqueMargin, A_Y);
            finalArea.lineTo(D_X-obliqueMargin, D_Y);
            finalArea.lineTo(pxStripMarigin, D_Y);
            finalArea.lineTo(pxStripMarigin, pxStripeTop);
//            init();
        }
    }


    public void setPXALL(float width, float height) {
        SCREENWIDTH = width;
        SCREENHEIGHT = height;
    }


}
