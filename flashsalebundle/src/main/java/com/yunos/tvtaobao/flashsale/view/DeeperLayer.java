package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.view.View;

import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.utils.AnimationUtils;
//import com.yunos.flashsale.utils.LogUtil;

/**
 * Created by guozhuoxing on 15-四月-17.
 */
public class DeeperLayer extends View {

    private static int START_ANGLE = 180;
    private static boolean STEP2_ANIMATION = false;
    private static boolean step3_animation_finish = false;
    float sweepAngle;
    float lineXend = 0;
    float upLineStart_X;
    float upLineStart_Y;
    float upLineEnd_X;
    float upLineEnd_Y;
    float E_X, E_Y, BottomEnd_X, BottomEnd_Y, BottomStart_X, BottomStart_Y;
    float bottomLine_StartX;
    float bottomLine_StartY;
    float bottomLine_EndX;
    float bottomLine_EndY;
    int step2 = 1;
    int step3 = 1;
    float px_UpLineStart_X;
    float px_UpLineStart_Y;
    float px_UpLineEnd_X;
    float px_UpLineEnd_Y;
    float upMargin;
    float px_BottomLine_StartX;
    float px_BottomLine_StartY;
    float px_BottomLine_EndX = 0;
    float px_BottomLine_EndY = 0;
    float BottomLineXdistance;
    float BottomLineYdistance;
    float UplineXdistance;
    float UplineYdistance;
    int lightgreen = 0xffed6760;//0xff40E0D0;
    float Xincrement = 0;
    float Yincrement = 0;
    private int step = 0;
    private RectF mCircleArea;
    private float lineX = 0, line1Y = 0, line2Y = 0, line3Y = 0;
    private float menu_lines_x = 0, menu_line_1y = 0, menu_line_2y = 0, menu_line_3y = 0;
    private float menu_lines_ends = 0;
    private float image_left = 00, image_top = 00, image_right = 00, image_bottem = 00;
    private Bitmap mBitmapScreen;
//    private Bitmap mBitmapHead;
    private float bitmap_layoutwidth;
    private float bitmap_layoutheight;
    private int mAlpha = 0;
    private float A_X = 0, A_Y = 0;
    private float B_X = 0, B_Y = 0;
    private float C_X = 0, C_Y = 0;
    private float D_X = 0, D_Y = 0;
    private float circle_location_left = 0;
    private float circle_location_top = 0;
    private float circle_location_right = 0;
    private float circle_location_bottom = 0;
    private float circle_area_left = 0;
    private float circle_area_top = 0;
    private float circle_area_right = 0;
    private float circle_area_bottom = 0;
    private RectF mImageAreaD;
    private RectF mHeadArea;
    private float headLeft, headTop, headRight, headBottom;
    private float headWidth, headHeight;
    private RectF mStripeArea;
    private float stripeLeft = 0, stripeRight = 0, stripTop = 0, stripBottom = 0;
    private float stripeHeight;
    private String screenInfo = "无数据";
    private String acthorName = "无数据";
    private String infoNumber = "无数据";
    private float screenInfoLeft;
    private float screenInfoTop;
    private float bottomNameLeft;
    private float bottomNameTop;
    private float bottomInfoLeft;
    private float bottomInfoTop;
    private float keyInfoSize;
    private float screenInfoSize;
    private float anchorNameSize;
    private float infoNumberSize;
    private float SCREEN_WIDTH = 1920;
    private float SCREEN_HEIGHT = 1080;
    private float SCTMaginLeft,SCTMaginBottom;

    private Path path;
    private Path finalArea;
    private Paint mArcPaint;
    private Paint mLinePaint;
    private Paint mImagePaint;
    private Paint mTextPaint;
    private Paint mTextPaintST;
    private Paint mStripPaint;
    private float circleDistance;
    private String keyText = "";
    private float anchorMargin,anchorInfoMargin;
//    private float keyTextSize,screenInfoSize,acthorNameSize,acthorInfoSize;


    public DeeperLayer(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        initLocation();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }


    public void setSweepAngl(float angle) {
        sweepAngle = angle;
    }


    public void setLineXend(float x) {
        lineXend = x;
    }


    public void setStep(int step) {
        this.step = step;
    }


    public void setImage(Bitmap bitmap, Bitmap head) {
        mBitmapScreen = bitmap;
//        mBitmapHead = head;
    }

    public void setImageProperty(float left, int step3, int alpha, boolean tag) {
        image_left = left;
        this.step3 = step3;
        mAlpha = alpha;
        STEP2_ANIMATION = tag;
    }

    public boolean isStep2LineAnimationEnd() {
        return STEP2_ANIMATION;
    }


    public void initLocation() {

        mLinePaint = new Paint() {
            {
                setDither(true);
                setStyle(Paint.Style.STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeJoin(Paint.Join.ROUND);
                setColor(Color.WHITE);
                setStrokeWidth(3.0f);
                setAntiAlias(true);
            }
        };

        mArcPaint = new Paint() {
            {
                setDither(true);
                setStyle(Paint.Style.STROKE);
                setColor(Color.GRAY);
                setStrokeWidth(1.0f);
                setAntiAlias(true);
            }
        };

        mImagePaint = new Paint();
        mImagePaint.setAntiAlias(true);

        mTextPaint = new Paint() {
            {
                setTextAlign(Paint.Align.LEFT);
                setTextSize(20);
                setColor(Color.WHITE);
                setAntiAlias(true);
                setTypeface(AnimationUtils.getXHTypeface());
            }
        };

        mTextPaintST = new Paint() {
            {
                setTextAlign(Paint.Align.LEFT);
                setTextSize(14);
                setColor(Color.WHITE);
                setAntiAlias(true);
                setTypeface(AnimationUtils.getXHTypeface());
            }
        };

        mStripPaint = new Paint() {
            {
                setColor(Color.BLUE);
                setAntiAlias(true);
                setStyle(Paint.Style.FILL);
            }
        };

        keyText = getResources().getString(R.string.str_remind_key);
        screenInfoSize = getResources().getDimension(R.dimen.ScreenTextSize);
        anchorNameSize = getResources().getDimension(R.dimen.AnchorTextSize);
        infoNumberSize = getResources().getDimension(R.dimen.AnchorInfoSize);
        keyInfoSize = getResources().getDimension(R.dimen.keySize);
        anchorMargin = getResources().getDimension(R.dimen.AnchorTextMargin);
        anchorInfoMargin = getResources().getDimension(R.dimen.AnchorInfoMargin);

        circle_location_left = getResources().getDimension(R.dimen.circle_area_marginLeft);
        circle_location_top = getResources().getDimension(R.dimen.circle_area_marginTop);
        circle_location_right = getResources().getDimension(R.dimen.circle_area_right);
        circle_location_bottom = getResources().getDimension(R.dimen.circle_area_bottom);

        lineX = getResources().getDimension(R.dimen.menu_lines_start_x);
        line1Y = getResources().getDimension(R.dimen.menu_line_1_y);
        line2Y = getResources().getDimension(R.dimen.menu_line_2_y);
        line3Y = getResources().getDimension(R.dimen.menu_line_3_y);

        E_X = getResources().getDimension(R.dimen.oblique_line_up_end_x);
        E_Y = getResources().getDimension(R.dimen.oblique_line_up_end_y);
//        upLineEnd_X=upLineStart_X;

        upLineStart_X = getResources().getDimension(R.dimen.oblique_line_up_start_x);
        upLineStart_Y = getResources().getDimension(R.dimen.oblique_line_up_start_y);
        upLineEnd_X = upLineStart_X;
        upLineEnd_Y = upLineStart_Y;

        bottomLine_StartX = getResources().getDimension(R.dimen.oblique_line_down_start_x);
        bottomLine_StartY = getResources().getDimension(R.dimen.oblique_line_down_start_y);

        BottomStart_X = getResources().getDimension(R.dimen.oblique_line_down_start_x);
        BottomStart_Y = getResources().getDimension(R.dimen.oblique_line_down_start_y);


        BottomEnd_X = getResources().getDimension(R.dimen.oblique_line_down_end_x);
        BottomEnd_Y = getResources().getDimension(R.dimen.oblique_line_down_end_y);
        bottomLine_EndX = bottomLine_StartX;
        bottomLine_EndY = bottomLine_StartY;

        SCTMaginLeft = getResources().getDimension(R.dimen.screenInfo_maringleft);
        SCTMaginBottom = getResources().getDimension(R.dimen.screenInfo_maringbottom);

        bitmap_layoutwidth = getResources().getDimension(R.dimen.image_layout_width);
        bitmap_layoutheight = getResources().getDimension(R.dimen.image_layout_height);
        mHeadArea = new RectF(headLeft, headTop, headRight, headBottom);
        mImageAreaD = new RectF(image_left, image_top, image_right, image_bottem);
        headWidth = getResources().getDimension(R.dimen.head_width);
        headHeight = getResources().getDimension(R.dimen.head_height);
        mStripeArea = new RectF(stripeLeft, stripTop, stripeRight, stripBottom);
        stripeHeight = getResources().getDimension(R.dimen.stripe_height);
        circleDistance = getResources().getDimension(R.dimen.circle_area_distance);
        Xincrement = 0;
        Yincrement = 0;

        BottomLineXdistance = BottomEnd_X - BottomStart_X;
        BottomLineYdistance = BottomEnd_Y - BottomStart_Y;
        UplineXdistance = E_X - upLineStart_X;
        UplineYdistance = E_Y - upLineStart_Y;

        path = new Path();
        step3_animation_finish = false;

    }

    public void updateLocation() {
//    	if(AppConfig.DEBUG){
//    		LogUtil.w("updateLocation()!");
//    	}
        circle_area_left = (circle_location_left / SCREEN_WIDTH) * getWidth();
        circle_area_top = (circle_location_top / SCREEN_HEIGHT) * getHeight();
        circle_area_right = (circle_location_right / SCREEN_WIDTH) * getWidth();
        circle_area_bottom = (circle_location_bottom / SCREEN_HEIGHT) * getHeight();

        menu_lines_x = (lineX / SCREEN_WIDTH) * getWidth();
        menu_line_1y = (line1Y / SCREEN_HEIGHT) * getHeight();
        menu_line_2y = (line2Y / SCREEN_HEIGHT) * getHeight();
        menu_line_3y = (line3Y / SCREEN_HEIGHT) * getHeight();
        menu_lines_ends = (lineXend / SCREEN_WIDTH) * getWidth();

        mCircleArea = new RectF(circle_area_left, circle_area_top, circle_area_right, circle_area_bottom);

        px_UpLineStart_X = (upLineStart_X / SCREEN_WIDTH) * getWidth();
        px_UpLineStart_Y = (upLineStart_Y / SCREEN_HEIGHT) * getHeight();
        px_UpLineEnd_X = (upLineEnd_X / SCREEN_WIDTH) * getWidth();
        px_UpLineEnd_Y = (upLineEnd_Y / SCREEN_HEIGHT) * getHeight();
        upMargin = (E_Y / SCREEN_HEIGHT) * getHeight();

        px_BottomLine_StartX = (bottomLine_StartX / SCREEN_WIDTH) * getWidth();
        px_BottomLine_StartY = (bottomLine_StartY / SCREEN_HEIGHT) * getHeight();

        px_BottomLine_EndX = (bottomLine_EndX / SCREEN_WIDTH) * getWidth();
        px_BottomLine_EndY = (bottomLine_EndY / SCREEN_HEIGHT) * getHeight();
//        float px_head_width = (headWidth/SCREEN_WIDTH)*getWidth();
//        float px_head_height = (headHeight/SCREEN_HEIGHT)*getHeight();

        if (step == 2) {
            step2 += 2;
//            //延伸斜线坐标
            bottomLine_EndX = BottomStart_X + (BottomLineXdistance * 20 / 500) * step2 * 3;
            bottomLine_EndY = BottomStart_Y + (BottomLineYdistance * 20 / 500) * step2 * 3;

            upLineEnd_X = (UplineXdistance * 20 / 500) * step2 * 3 + upLineStart_X;
            upLineEnd_Y = (UplineYdistance * 20 / 500) * step2 * 3 + upLineStart_Y;

            if (bottomLine_EndY < getResources().getDimension(R.dimen.oblique_line_down_end_y)) {
            } else {
                STEP2_ANIMATION = true;
            }
        }


        if (step == 3) {
            float distance = circleDistance / SCREEN_WIDTH * getWidth();
            if (Xincrement < distance) {
//                float increment = getResources().getDimension(R.dimen.mean_velocity) / SCREEN_WIDTH * getWidth();
                float increment = 1;
//                if(AppConfig.DEBUG){
//                	LogUtil.w("onDraw第" + step3 + "次"+"    Xincrent "+Xincrement+" distance "+ distance);
//                }
                step3++;

                Xincrement = increment * step3 * 2;
                Yincrement = ((4 * increment) / 5) * step3 * 2;

                menu_lines_x = menu_lines_x - Xincrement;
                menu_line_1y = menu_line_1y - Yincrement;
                menu_line_2y = menu_line_2y - Yincrement;
                menu_line_3y = menu_line_3y - Yincrement;
                menu_lines_ends = menu_lines_ends - Xincrement;
                px_UpLineStart_X = px_UpLineStart_X - Xincrement;
                px_UpLineStart_Y = px_UpLineStart_Y - Yincrement;

                px_BottomLine_StartX = px_BottomLine_StartX - Xincrement;
                px_BottomLine_StartY = px_BottomLine_StartY - Yincrement;
                int incrementA = 20;
                mAlpha = step3 * incrementA;
                if (mAlpha > 255) mAlpha = 255;


                int imageincrement = step3 * 7;
                if (image_left < getWidth() - bitmap_layoutwidth)
                    image_left = circle_area_right + imageincrement;
                else {
                    image_left = getWidth() - bitmap_layoutwidth;
                }
                image_top = 0;
                image_bottem = bitmap_layoutheight;
                image_right = circle_area_right + bitmap_layoutwidth + imageincrement;
                if (image_right < getWidth()) {
                    step3_animation_finish = false;
                } else {
                    finalArea = new Path();
                    float E_X = getWidth();
                    float E_Y = B_Y;
                    float F_X = E_X;
                    float F_Y = stripBottom;
                    finalArea.moveTo(B_X, B_Y);
                    finalArea.lineTo(E_X, E_Y);
                    finalArea.lineTo(F_X, F_Y);

                    finalArea.lineTo(C_X - 6, C_Y);
                    finalArea.lineTo(B_X - 7, B_Y);
                    image_right = getRight();
//                    if(AppConfig.DEBUG){
//                    	LogUtil.w("Animation End !!!  ");
//                    }
                    step3_animation_finish = true;

                }
                mImageAreaD.set(image_left, image_top, image_right, image_bottem);


                stripeLeft = circle_area_left;
                stripTop = image_bottem;
                stripeRight = image_right;
                stripBottom = stripTop + stripeHeight;

                headLeft = image_left;
                headTop = stripTop + 5;
                headRight = headLeft + headWidth;
                headBottom = headTop + headHeight;
                mHeadArea.set(headLeft, headTop, headRight, headBottom);

                bottomNameLeft = image_left + headWidth;


                A_X = 0;
                A_Y = stripTop;
                B_Y = stripTop;
                B_X = (B_Y - upMargin) * 5 / 4 + 4;
                C_Y = stripBottom;
                C_X = (C_Y - upMargin) * 5 / 4 + 4;
                D_X = 0;
                D_Y = stripBottom;
                path.moveTo(A_X, A_Y);
                path.lineTo(B_X - 8, B_Y);
                path.lineTo(C_X - 6, C_Y);
                path.lineTo(D_X, D_Y);
                path.lineTo(A_X, A_Y);


                mStripeArea = new RectF(stripeLeft, stripTop, stripeRight, stripBottom);
                LinearGradient lg = new LinearGradient(stripeLeft, stripTop, stripeRight, stripBottom, lightgreen, 0xffe98903/*Color.BLUE*/, Shader.TileMode.MIRROR);
                mStripPaint.setShader(lg);

//
                mCircleArea = new RectF(circle_area_left - Xincrement, circle_area_top - Yincrement, circle_area_right - Xincrement, circle_area_bottom - Yincrement);
//

            } else {
            }
        }
    }


    @Override
    public void onDraw(Canvas canvas) {

//    	if(AppConfig.DEBUG){
//    		LogUtil.w("depperLayer onDraw() view_width " + getWidth() + " view_height " + getHeight());
//    	}

//        if (step == 1) {
        //同步画圆和菜单键
        //展开斜线
        updateLocation();

        if (menu_lines_ends > menu_lines_x || step == 3) {
            canvas.drawLine(menu_lines_x, menu_line_1y, menu_lines_ends, menu_line_1y, mLinePaint);
            canvas.drawLine(menu_lines_x, menu_line_2y, menu_lines_ends, menu_line_2y, mLinePaint);
            canvas.drawLine(menu_lines_x, menu_line_3y, menu_lines_ends, menu_line_3y, mLinePaint);
        }

        //画圆
//        if(AppConfig.DEBUG){
//        	LogUtil.w("mCircleArea left " + mCircleArea.left + " top " + mCircleArea.top);
//        }
        canvas.drawArc(mCircleArea, START_ANGLE, sweepAngle, false, mArcPaint);
//        }
        if (step >= 2) {
            //动态画斜线
            if (px_BottomLine_EndY < 720) {
                canvas.drawLine(px_BottomLine_StartX, px_BottomLine_StartY, px_BottomLine_EndX, px_BottomLine_EndY, mArcPaint);
                canvas.drawLine(px_UpLineStart_X, px_UpLineStart_Y, px_UpLineEnd_X, px_UpLineEnd_Y, mArcPaint);

            } else {
//                canvas.drawLine(px_BottomLine_StartX, px_BottomLine_StartY, px_BottomLine_EndX, px_BottomLine_EndY, mArcPaint);
//                canvas.drawLine(px_UpLineStart_X, px_UpLineStart_Y, px_UpLineEnd_X, px_UpLineEnd_Y, mArcPaint);

            }
        }


        if (step == 3) {
//        	if(AppConfig.DEBUG){
//        		LogUtil.w("image mAlpha " + mAlpha + " imageleft " + mImageAreaD.left + "imagebotom" + mImageAreaD.bottom);
//        	}
            mImagePaint.setAlpha(mAlpha);
            mTextPaint.setAlpha(mAlpha);
            mTextPaintST.setAlpha(mAlpha);
            screenInfoLeft = mImageAreaD.left+SCTMaginLeft;
            screenInfoTop = mImageAreaD.bottom - SCTMaginBottom;
            mTextPaint.setTextSize(screenInfoSize);
            mTextPaint.setTypeface(AnimationUtils.getXHTypeface());
            canvas.drawBitmap(mBitmapScreen, null, mImageAreaD, mImagePaint);
            canvas.drawText(screenInfo, screenInfoLeft+SCTMaginLeft, screenInfoTop, mTextPaint);
//            canvas.clipPath(path);
            canvas.clipPath(path, Region.Op.DIFFERENCE);

            if (mStripeArea.right > B_X) ;
            if (step3_animation_finish == true) {
//            	if(AppConfig.DEBUG){
//            		LogUtil.w("animationEnd");
//            	}
                mTextPaintST.setTextSize(keyInfoSize);
                canvas.drawText(keyText, px_BottomLine_StartX+10, px_BottomLine_StartY , mTextPaintST);
                canvas.drawPath(finalArea, mStripPaint);
            } else {
//            	if(AppConfig.DEBUG){
//            		LogUtil.w("animationnot not End");
//            	}
                canvas.drawRect(mStripeArea, mStripPaint);
            }
            canvas.drawLine(px_BottomLine_StartX, px_BottomLine_StartY, px_BottomLine_EndX, px_BottomLine_EndY, mArcPaint);
            mTextPaint.setTextSize(anchorNameSize);
            mTextPaint.setTypeface(AnimationUtils.getXHTypeface());
//            Typeface font = Typeface.create(GameLiveUtils.getXHTypeface(), Typeface.BOLD);
//            mTextPaint.setTypeface(font);
            mTextPaint.setFakeBoldText(true);
            bottomNameTop = stripTop + anchorMargin;
            canvas.drawText(acthorName, bottomNameLeft, bottomNameTop, mTextPaint);
            mTextPaint.setTextSize(infoNumberSize);
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setTypeface(AnimationUtils.getXHTypeface());
            bottomInfoTop = bottomNameTop + mTextPaint.getTextSize()+anchorInfoMargin;
            canvas.drawText(infoNumber, bottomNameLeft, bottomInfoTop, mTextPaint);
//            canvas.drawBitmap(mBitmapHead, null, mHeadArea, mImagePaint);


        }

        super.onDraw(canvas);
//        }
    }


    public void setData(Bitmap screen, Bitmap head, String screenInfo, String acthorName, String infoNumber) {
        mBitmapScreen = screen;
//        mBitmapScreen = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
//        mBitmapHead = head;
//        mBitmapHead = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        if (screenInfo != null)
            this.screenInfo = screenInfo;
        if (acthorName != null)
            this.acthorName = acthorName;
        if (infoNumber != null)
            this.infoNumber = infoNumber;
    }

    public boolean isAllAnimationEnd() {
        return step3_animation_finish;
    }

    public void setPXALL(float width, float height) {
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
    }


}
