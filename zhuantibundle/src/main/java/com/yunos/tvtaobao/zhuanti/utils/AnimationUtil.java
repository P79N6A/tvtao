package com.yunos.tvtaobao.zhuanti.utils;


import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.zhuanti.R;
import com.yunos.tvtaobao.zhuanti.bo.enumration.AnimationTime;


/**
 * 动画辅助工具
 * @author hanqi
 * @date 2014-8-20
 */
public class AnimationUtil {

    /**
     * 根据方向获取一个移动的动画
     * @param direction
     *            为了显示true， 为了隐藏false
     *            偏移量
     * @return
     * @author hanqi
     * @date 2014-8-20
     */
    public static void startTranslateAnimation(final View view, int direction, Long duration) {
        boolean forShow = (view.getVisibility() == View.VISIBLE);
        startTranslateAnimation(view, direction, forShow, null, duration);
    }

    /**
     * 根据方向获取一个移动的动画
     * @param direction
     * @param forShow
     *            为了显示true， 为了隐藏false
     * @param listener
     * @return
     * @author hanqi
     * @date 2014-8-20
     */
    public static void startTranslateAnimation(final View view, int direction, final boolean forShow,
                                               final AnimationListener listener, Long duration) {
        if (null == view) {
            return;
        }
        if (null == duration) {
            duration = AnimationTime.DURATION;
        }
        float fromXDelta = 0;
        float toXDelta = 0;
        float fromYDelta = 0;
        float toYDelta = 0;
        View parent = (View) view.getParent();
        TranslateAnimation animation = null;
        switch (direction) {
            case View.FOCUS_DOWN:
                if (forShow) {
                    fromYDelta = parent.getHeight();
                } else {
                    toYDelta = -parent.getHeight();
                }
                animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                break;
            case View.FOCUS_UP:
                if (forShow) {
                    fromYDelta = -parent.getHeight();
                } else {
                    toYDelta = parent.getHeight();
                }
                animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                break;
            case View.FOCUS_LEFT:
                if (forShow) {
                    fromXDelta = -parent.getWidth();
                } else {
                    toXDelta = parent.getWidth();
                }
                animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                break;
            case View.FOCUS_RIGHT:
                if (forShow) {
                    fromXDelta = parent.getWidth();
                } else {
                    toXDelta = -parent.getWidth();
                }
                animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
                break;
            case View.FOCUS_FORWARD:
                animation = (TranslateAnimation) view.getAnimation();
                if (null == animation) {
                    startTranslateAnimation(view, View.FOCUS_DOWN, forShow, listener, duration);
                }
                break;
            default:
                animation = (TranslateAnimation) view.getAnimation();
                break;
        }
        if (null != animation) {
            animation.setDuration(duration);
            if (forShow) {
                animation.setStartTime(duration / 3);
            }
            if (null != listener) {
                animation.setAnimationListener(listener);
            }
            view.setAnimation(animation);
            view.startAnimation(animation);
        }
    }

   public static  void  alphaIn(final View view, long duration){
       AlphaAnimation alphaAnimation=new AlphaAnimation(0f,1f);
       alphaAnimation.setDuration(duration);
       alphaAnimation.setAnimationListener(new AnimationListener() {
           @Override
           public void onAnimationStart(Animation animation) {

           }

           @Override
           public void onAnimationEnd(Animation animation) {
               view.setVisibility(View.VISIBLE);

           }

           @Override
           public void onAnimationRepeat(Animation animation) {

           }
       });
       view.startAnimation(alphaAnimation);
   }

    public static  void alphaOut(final View view, long duration){

        AlphaAnimation alphaAnimation=new AlphaAnimation(1f,0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                 view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(alphaAnimation);
    }

    public static void transIn(final View view, long duration){
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translate = new TranslateAnimation(0, 0,-1f, 0);
        animationSet.addAnimation(translate);
        animationSet.setDuration(duration);
        animationSet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animationSet);
    }

    public static void transOut(final View view, long duration){

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translate = new TranslateAnimation(0, 0, 0, -1f);
        animationSet.addAnimation(translate);
        animationSet.setDuration(duration);
        animationSet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animationSet);
    }




    /**
     * 抖动动画
     * @param view  动画作用对象
     * @param shakeDegrees
     * @param duration
     */
    public static  void startShakeByPropertyAnim(View view, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }

    /**
     *
     * @param context 上下文
     * @param rl    activiry的根布局
     * @param iv    动画起始位置View，用以计算初始值
     * @param rlEnd  动画结束位置的View，用以计算结束值
     * @param duration  延时
     */
    public static  void addCartAnim(Context context, final RelativeLayout rl, ImageView iv, TextView rlEnd, int duration) {
        final ImageView goods = new ImageView(context);
        goods.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_bug_count));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(30, 30);
        rl.addView(goods, params);

        int[] parentLocation = new int[2];
        rl.getLocationInWindow(parentLocation);

        int startLoc[] = new int[2];
        iv.getLocationInWindow(startLoc);

        //得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        rlEnd.getLocationInWindow(endLoc);

//        正式开始计算动画开始/结束的坐标
        float startX = startLoc[0] - parentLocation[0] + iv.getWidth()/2;
        float startY = startLoc[1] - parentLocation[1]-iv.getHeight();


        float toX = endLoc[0] - parentLocation[0] + rlEnd.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];


        //开始绘制贝塞尔曲线
        Path path = new Path();
        path.moveTo(startX, startY); //设置起始点
        path.quadTo(startX+(toX-startX)/2, startY-200, toX, toY); //中间点和尾点

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] currentPosition = new float[2];
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(duration);
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                pathMeasure.getPosTan(value, currentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(currentPosition[0]);
                goods.setTranslationY(currentPosition[1]);
            }
        });
        valueAnimator.start();

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            //当动画结束后：
            @Override
            public void onAnimationEnd(Animator animation) {
                // 把移动的图片imageview从父布局里移除
                rl.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
