package com.yunos.tvtaobao.live.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.ImageLoader;
import com.yunos.tv.app.widget.FrameLayout;
import com.yunos.tvtaobao.live.R;

/**
 * Created by GuoLiDong on 2018/9/7.
 */

public class CareFocusTipView extends FrameLayout {
    public CareFocusTipView(Context context) {
        this(context,null);
    }

    public CareFocusTipView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CareFocusTipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    View background_layer = null;
    ImageView iv_head_icon = null;
    TextView name = null;
    TextView fans_num = null;
    TextView no_login_tip_1 = null;
    TextView no_login_tip_2 = null;
    TextView focus_tip = null;
    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.care_focus_tip,this,true);
        background_layer = findViewById(R.id.background_layer);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        name = (TextView) findViewById(R.id.name);
        fans_num = (TextView) findViewById(R.id.fans_num);
        no_login_tip_1 = (TextView) findViewById(R.id.no_login_tip_1);
        no_login_tip_2 = (TextView) findViewById(R.id.no_login_tip_2);
        focus_tip = (TextView) findViewById(R.id.focus_tip);

        background_layer.setBackgroundDrawable(new Drawable1());
        focus_tip.setBackgroundDrawable(new Drawable2());
    }

    boolean isShowingFlag = false;
    DisplayImageOptions roundImageOptions = new DisplayImageOptions.Builder().displayer(new Displayer(0)).build();
    public void show(String headUrl,String nameStr,int fansNum
            , boolean loginFlag, boolean focusFlag
            , View anchor){
        try {
            ImageLoader.getInstance().displayImage(headUrl, iv_head_icon, roundImageOptions);
            name.setText(nameStr);
            if (fansNum>10000){
                fans_num.setText(String.format("粉丝 %.02f万",fansNum/10000.0f));
            } else {
                fans_num.setText(String.format("粉丝 %d人",fansNum));
            }
            focus_tip.setVisibility(GONE);
            if (loginFlag){
                no_login_tip_1.setVisibility(GONE);
                no_login_tip_2.setVisibility(GONE);
                focus_tip.setVisibility(VISIBLE);
                if (focusFlag){
                    focus_tip.setText("已关注");
                } else {
                    focus_tip.setText("关注主播");
                }
            } else {
                no_login_tip_1.setVisibility(VISIBLE);
                no_login_tip_2.setVisibility(VISIBLE);
            }

            if (!isShowingFlag){
                measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));

                int w = getMeasuredWidth();
                int h = getMeasuredHeight();
                Rect anchorRect = new Rect();
                anchor.getGlobalVisibleRect(anchorRect);

                View root = ((Activity) getContext()).getWindow().getDecorView();
                if (root instanceof android.widget.FrameLayout){
                    android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(anchorRect.centerX()-w/2,anchorRect.top-h,0,0);
                    ((android.widget.FrameLayout) root).addView(this,lp);
                    isShowingFlag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hide(){
        View root = ((Activity) getContext()).getWindow().getDecorView();
        if (root instanceof android.widget.FrameLayout){
            ((android.widget.FrameLayout) root).removeView(this);
            isShowingFlag = false;
        }
    }

    public void playCareSuccessAnim(View anchor){
        final ImageView sh = new ImageView(getContext());
        sh.setImageDrawable(getResources().getDrawable(R.drawable.sweet_heart));

        sh.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));

        int w = sh.getMeasuredWidth();
        int h = sh.getMeasuredHeight();
        Rect anchorRect = new Rect();
        anchor.getGlobalVisibleRect(anchorRect);

        final View root = ((Activity) getContext()).getWindow().getDecorView();
        if (root instanceof android.widget.FrameLayout){
            android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(anchorRect.centerX()-w/2,anchorRect.top-h-20,0,0);
            ((android.widget.FrameLayout) root).addView(sh,lp);
            ValueAnimator animator = ValueAnimator.ofFloat(0,1);
            animator.setDuration(800);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    //sh.setTranslationY(*v);
                    sh.setScaleX(5*v);
                    sh.setScaleY(5*v);
                    sh.setAlpha((1-v));
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((android.widget.FrameLayout) root).removeView(sh);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    ((android.widget.FrameLayout) root).removeView(sh);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.setTarget(sh);
            animator.start();
        }
    }

    private class Drawable1 extends Drawable{

        int cornerRadius = 12;
        int padding = 5;
        int color = Color.parseColor("#c0000000");
        Paint paint = new Paint();
        Rect bglRect = new Rect();
        Rect headRect = new Rect();
        RectF tmpRect = new RectF();
        float tmpX,tmpY,tmpRadius;
        @Override
        public void draw(@NonNull Canvas canvas) {
            try {
                background_layer.getGlobalVisibleRect(bglRect);
                iv_head_icon.getGlobalVisibleRect(headRect);

                paint.setColor(color);
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);

                tmpRadius = headRect.height()/2+padding;

                Path path = new Path();
                tmpX = 0;
                tmpY = cornerRadius;
                path.moveTo(tmpX,tmpY);

                tmpRect.set(0,0, cornerRadius *2, cornerRadius *2);
                path.arcTo(tmpRect,180,90);

                tmpX = headRect.left-bglRect.left-padding;
                tmpY = 0;
                path.lineTo(tmpX,tmpY);

                tmpRect.set(tmpX,-1*tmpRadius,tmpX+tmpRadius*2,tmpRadius);
                path.arcTo(tmpRect,180,-180);

                tmpX = bglRect.width()-cornerRadius;
                tmpY = 0;
                path.lineTo(tmpX,tmpY);

                tmpRect.set(tmpX-cornerRadius,0,tmpX+cornerRadius,cornerRadius*2);
                path.arcTo(tmpRect,-90,90);

                tmpX = bglRect.width();
                tmpY = bglRect.height() - cornerRadius;
                path.lineTo(tmpX,tmpY);

                tmpRect.set(tmpX-cornerRadius*2,tmpY-cornerRadius,tmpX,tmpY+cornerRadius);
                path.arcTo(tmpRect,0,90);

                tmpX = cornerRadius;
                tmpY = bglRect.height();
                path.lineTo(tmpX,tmpY);

                tmpRect.set(tmpX-cornerRadius,tmpY-cornerRadius*2,tmpX+cornerRadius,tmpY);
                path.arcTo(tmpRect,90,90);

                path.close();

                canvas.drawPath(path,paint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private class Drawable2 extends Drawable {

        int circleRadius = 5;
        int lineWidth = 3;
        int padding2txt = 20;
        int padding2edge = 12;
        Rect txtRect = new Rect();
        Rect tvRect = new Rect();
        int circleClr = Color.WHITE;
        int[] clrsGradientLeft = {Color.parseColor("#00ffffff"),Color.parseColor("#ffffff")};
        int[] clrsGradientRight = {Color.parseColor("#ffffff"),Color.parseColor("#00ffffff")};
        float[] postions = {0,1};

        float tmpX,tmpY,tmpLength;
        RectF tmpRect = new RectF();
        Paint paint = new Paint();
        @Override
        public void draw(@NonNull Canvas canvas) {
            try {
                focus_tip.getPaint().getTextBounds(focus_tip.getText().toString(),0,focus_tip.getText().toString().length(),txtRect);
                txtRect.offsetTo(0,0);
                tvRect.set(0,0,focus_tip.getMeasuredWidth(),focus_tip.getMeasuredHeight());
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);

                tmpLength = (tvRect.width() - txtRect.width())/2- padding2txt - padding2edge;

                tmpX = padding2edge;
                tmpY = tvRect.height()/2;
                LinearGradient linearGradient = new LinearGradient(tmpX,tmpY,tmpX+tmpLength,tmpY,clrsGradientLeft,postions, Shader.TileMode.CLAMP);
                tmpRect.set(tmpX,tmpY-lineWidth/2,tmpX+tmpLength,tmpY+lineWidth/2);
                paint.setShader(linearGradient);
                canvas.drawRect(tmpRect,paint);

                paint.setShader(null);
                paint.setColor(circleClr);
                canvas.drawCircle(tmpRect.right,tmpRect.centerY(),circleRadius,paint);

                tmpX = tvRect.width()/2 + txtRect.width()/2 + padding2txt;
                tmpY = tvRect.height()/2;
                LinearGradient linearGradient2 = new LinearGradient(tmpX,tmpY,tmpX+tmpLength,tmpY,clrsGradientRight,postions, Shader.TileMode.CLAMP);
                tmpRect.set(tmpX,tmpY-lineWidth/2,tmpX+tmpLength,tmpY+lineWidth/2);
                paint.setShader(linearGradient2);
                canvas.drawRect(tmpRect,paint);

                paint.setShader(null);
                paint.setColor(circleClr);
                canvas.drawCircle(tmpRect.left,tmpRect.centerY(),circleRadius,paint);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private class SweetHeart extends Drawable{

        Paint paint = new Paint();
        int color = Color.parseColor("#ff0055");
        @Override
        public void draw(@NonNull Canvas canvas) {
            paint.setAntiAlias(true);
            paint.setColor(color);

            int w = canvas.getWidth();
            int h = canvas.getHeight();

            Path path = new Path();
            path.moveTo(0,h/3);
            //path.cubicTo();

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
