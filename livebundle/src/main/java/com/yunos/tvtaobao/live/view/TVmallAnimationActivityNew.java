package com.yunos.tvtaobao.live.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.activity.TBaoLiveActivity;

import java.util.Map;

public class TVmallAnimationActivityNew extends FrameLayout {
    private ImageView iv_reciprocal_bubble_count1;
    private ImageView iv_reciprocal_bubble_count2;
    private ImageView iv_reciprocal_bubble_count3;
    private ImageView iv_reciprocal_bubble_count4;
    private ImageView iv_reciprocal_bubble_count5;
    private ImageView iv_reciprocal_bubble;
    private ImageView iv_come_toast_frame;
    private ImageView iv_red_mall;
    private ImageView iv_wheat_bg;
    private ImageView iv_new_cat;
    private ImageView iv_toast;
    private TextView get_bonus_success, get_bonus, get_bonus_lucky;
    private LinearLayout ll_text;
    private Handler mHandler = new Handler();
    private Context mContext;
//    是否显示登录领取的提示
    private boolean isShowivToast;

    public TVmallAnimationActivityNew(Context context) {
        super(context);
        mContext = context;
        initView();
        setFocusable(false);
    }

    public TVmallAnimationActivityNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        setFocusable(false);
    }

    public TVmallAnimationActivityNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
        setFocusable(false);
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_tvmall_animation_new, this, true);

        findView(view);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAnimation();
    }

    public void setAnimation() {
        TranslateAnimation wholeCatAnimation = new TranslateAnimation(getResources()
                .getDisplayMetrics().widthPixels + 200, 0, 0, 0);
        wholeCatAnimation.setDuration(500);
        wholeCatAnimation.setFillAfter(true);
        iv_red_mall.startAnimation(wholeCatAnimation);//整个猫动画

        AnimationSet redToastAnimationSet = new AnimationSet(true);
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        final ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        redToastAnimationSet.addAnimation(alphaAnimation);
        redToastAnimationSet.addAnimation(scaleAnimation);
        redToastAnimationSet.setDuration(500);
        iv_come_toast_frame.startAnimation(redToastAnimationSet);//一大波红包来袭图片

        wholeCatAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_come_toast_frame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimationSet animationSetShow = new AnimationSet(true);
                        AlphaAnimation reciprocalAlphaAnimaltion = new AlphaAnimation(0, 1);
                        ScaleAnimation reciprocalscaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        animationSetShow.addAnimation(reciprocalAlphaAnimaltion);
                        animationSetShow.addAnimation(reciprocalscaleAnimation);
                        animationSetShow.setDuration(80);

                        iv_reciprocal_bubble.startAnimation(animationSetShow);//倒计时动画
                        iv_reciprocal_bubble_count5.startAnimation(animationSetShow);//倒计时5动画
                        iv_reciprocal_bubble_count5.setVisibility(View.VISIBLE);
                        iv_reciprocal_bubble.setVisibility(View.VISIBLE);

                        animationSetShow.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                AnimationSet animationSetDismiss = new AnimationSet(true);
                                AlphaAnimation dismissAlphaAnimaltion = new AlphaAnimation(1, 0);
                                ScaleAnimation dismiscaleAnimation = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                animationSetDismiss.addAnimation(dismissAlphaAnimaltion);
                                animationSetDismiss.addAnimation(dismiscaleAnimation);
                                animationSetDismiss.setDuration(300);
                                animationSetDismiss.setFillAfter(true);

                                iv_come_toast_frame.startAnimation(animationSetDismiss);//一大波红包来袭文字消失

                                animationSetDismiss.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                iv_reciprocal_bubble_count5.setVisibility(View.INVISIBLE);

                                                iv_reciprocal_bubble.setVisibility(View.VISIBLE);
                                                AnimationSet animationSetShowTect4 = new AnimationSet(true);
                                                AlphaAnimation reciprocalAlphaAnimaltion = new AlphaAnimation(0, 1);
                                                ScaleAnimation reciprocalscaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                animationSetShowTect4.addAnimation(reciprocalAlphaAnimaltion);
                                                animationSetShowTect4.addAnimation(reciprocalscaleAnimation);
                                                animationSetShowTect4.setDuration(80);

                                                iv_reciprocal_bubble_count4.startAnimation(animationSetShowTect4);//文字4出现动画
                                                iv_reciprocal_bubble_count4.setVisibility(View.VISIBLE);

                                                animationSetShowTect4.setAnimationListener(new Animation.AnimationListener() {
                                                    @Override
                                                    public void onAnimationStart(Animation animation) {

                                                    }

                                                    @Override
                                                    public void onAnimationEnd(Animation animation) {
                                                        mHandler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                iv_reciprocal_bubble_count4.setVisibility(View.INVISIBLE);

                                                                AnimationSet animationSetShowTect3 = new AnimationSet(true);
                                                                AlphaAnimation reciprocalAlphaAnimaltion = new AlphaAnimation(0, 1);
                                                                ScaleAnimation reciprocalscaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                                animationSetShowTect3.addAnimation(reciprocalAlphaAnimaltion);
                                                                animationSetShowTect3.addAnimation(reciprocalscaleAnimation);
                                                                animationSetShowTect3.setDuration(80);

                                                                iv_reciprocal_bubble_count3.startAnimation(animationSetShowTect3);
                                                                iv_reciprocal_bubble_count3.setVisibility(View.VISIBLE);

                                                                animationSetShowTect3.setAnimationListener(new Animation.AnimationListener() {
                                                                    @Override
                                                                    public void onAnimationStart(Animation animation) {

                                                                    }

                                                                    @Override
                                                                    public void onAnimationEnd(Animation animation) {
                                                                        mHandler.postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                iv_reciprocal_bubble_count3.setVisibility(View.INVISIBLE);

                                                                                AnimationSet animationSetShowTect2 = new AnimationSet(true);
                                                                                AlphaAnimation reciprocalAlphaAnimaltion = new AlphaAnimation(0, 1);
                                                                                ScaleAnimation reciprocalscaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                                                animationSetShowTect2.addAnimation(reciprocalAlphaAnimaltion);
                                                                                animationSetShowTect2.addAnimation(reciprocalscaleAnimation);
                                                                                animationSetShowTect2.setDuration(80);

                                                                                iv_reciprocal_bubble_count2.startAnimation(animationSetShowTect2);
                                                                                iv_reciprocal_bubble_count2.setVisibility(View.VISIBLE);

                                                                                animationSetShowTect2.setAnimationListener(new Animation.AnimationListener() {
                                                                                    @Override
                                                                                    public void onAnimationStart(Animation animation) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                        mHandler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                iv_reciprocal_bubble_count2.setVisibility(View.INVISIBLE);

                                                                                                AnimationSet animationSetShowTect1 = new AnimationSet(true);
                                                                                                AlphaAnimation reciprocalAlphaAnimaltion = new AlphaAnimation(0, 1);
                                                                                                ScaleAnimation reciprocalscaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                                                                animationSetShowTect1.addAnimation(reciprocalAlphaAnimaltion);
                                                                                                animationSetShowTect1.addAnimation(reciprocalscaleAnimation);
                                                                                                animationSetShowTect1.setDuration(80);

                                                                                                iv_reciprocal_bubble_count1.startAnimation(animationSetShowTect1);
                                                                                                iv_reciprocal_bubble_count1.setVisibility(View.VISIBLE);

                                                                                                animationSetShowTect1.setAnimationListener(new Animation.AnimationListener() {
                                                                                                    @Override
                                                                                                    public void onAnimationStart(Animation animation) {

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                                        mHandler.postDelayed(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                iv_reciprocal_bubble_count1.setVisibility(View.INVISIBLE);

                                                                                                                AnimationSet animationSet = new AnimationSet(true);
                                                                                                                TranslateAnimation wholeCatAnimationDismiss = new TranslateAnimation(0, getResources()
                                                                                                                        .getDisplayMetrics().widthPixels + 100, 0, 0);
                                                                                                                animationSet.addAnimation(wholeCatAnimationDismiss);
                                                                                                                animationSet.setDuration(200);

                                                                                                                //整个猫消失
                                                                                                                iv_reciprocal_bubble.startAnimation(animationSet);
                                                                                                                iv_red_mall.startAnimation(animationSet);

                                                                                                                animationSet.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                    @Override
                                                                                                                    public void onAnimationStart(Animation animation) {
                                                                                                                        iv_reciprocal_bubble_count1.setVisibility(View.INVISIBLE);
                                                                                                                        iv_reciprocal_bubble.setVisibility(View.INVISIBLE);
                                                                                                                        iv_red_mall.setVisibility(View.INVISIBLE);

                                                                                                                        AnimationSet animationSet1 = new AnimationSet(true);
                                                                                                                        AlphaAnimation wheatBGAlphaAnimation = new AlphaAnimation(0, 1);
                                                                                                                        TranslateAnimation wholeCatAnimation = new TranslateAnimation(getResources().getDisplayMetrics().widthPixels + 336, 0, 0, 0);
                                                                                                                        animationSet1.addAnimation(wholeCatAnimation);
                                                                                                                        animationSet1.addAnimation(wheatBGAlphaAnimation);
                                                                                                                        animationSet1.setDuration(500);

                                                                                                                        animationSet1.setStartOffset(-300);

                                                                                                                        iv_wheat_bg.startAnimation(animationSet1); //背景出来

                                                                                                                        animationSet1.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                            @Override
                                                                                                                            public void onAnimationStart(Animation animation) {
                                                                                                                                iv_wheat_bg.setVisibility(View.VISIBLE);
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onAnimationEnd(Animation animation) {
                                                                                                                                ScaleAnimation scaleAnimation1 = new ScaleAnimation(0.15f, 1.1f, 0.15f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
                                                                                                                                scaleAnimation1.setDuration(200);
                                                                                                                                scaleAnimation1.setStartOffset(-150);

                                                                                                                                iv_new_cat.startAnimation(scaleAnimation1);

                                                                                                                                scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onAnimationStart(Animation animation) {
                                                                                                                                        iv_new_cat.setVisibility(View.VISIBLE);
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                                                                        mHandler.postDelayed(new Runnable() {
                                                                                                                                            @Override
                                                                                                                                            public void run() {
                                                                                                                                                final AnimationSet animationSet = new AnimationSet(true);
                                                                                                                                                AlphaAnimation alphaAnimation1 = new AlphaAnimation(0, 1);
                                                                                                                                                ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                                                                                                                animationSet.addAnimation(alphaAnimation1);
                                                                                                                                                animationSet.addAnimation(scaleAnimation);
                                                                                                                                                animationSet.setDuration(150);

                                                                                                                                                mHandler.postDelayed(new Runnable() {
                                                                                                                                                    @Override
                                                                                                                                                    public void run() {
                                                                                                                                                        ll_text.startAnimation(animationSet);
                                                                                                                                                    }
                                                                                                                                                }, 200);

                                                                                                                                                animationSet.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onAnimationStart(Animation animation) {
                                                                                                                                                        ll_text.setVisibility(View.VISIBLE);
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                                                                                        mHandler.postDelayed(new Runnable() {
                                                                                                                                                            @Override
                                                                                                                                                            public void run() {
                                                                                                                                                                AnimationSet animationSet5 = new AnimationSet(true);
                                                                                                                                                                AlphaAnimation alphaAnimation2 = new AlphaAnimation(1, 0);
                                                                                                                                                                TranslateAnimation wholeCatAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                                                                                                                                                animationSet5.addAnimation(alphaAnimation2);
                                                                                                                                                                animationSet5.addAnimation(wholeCatAnimation);
                                                                                                                                                                animationSet5.setDuration(1000);

                                                                                                                                                                ll_text.startAnimation(animationSet5);
                                                                                                                                                                iv_new_cat.startAnimation(animationSet5);
                                                                                                                                                                iv_wheat_bg.startAnimation(animationSet5); //背景退出


                                                                                                                                                                animationSet5.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onAnimationStart(Animation animation) {
                                                                                                                                                                        AnimationSet animationSet2 = new AnimationSet(true);
                                                                                                                                                                        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0, 1);
                                                                                                                                                                        TranslateAnimation wholeCatAnimation = new TranslateAnimation(getResources().getDisplayMetrics().widthPixels + 432, 0, 0, 0);
                                                                                                                                                                        animationSet2.addAnimation(alphaAnimation1);
                                                                                                                                                                        animationSet2.addAnimation(wholeCatAnimation);
                                                                                                                                                                        animationSet2.setFillAfter(true);
                                                                                                                                                                        animationSet2.setDuration(1000);
                                                                                                                                                                        AppDebug.e("TVmallAnimationActivityNew","isShowivToast = "+isShowivToast);
                                                                                                                                                                        if (isShowivToast) {
                                                                                                                                                                            iv_toast.startAnimation(animationSet2);
                                                                                                                                                                        }else{
                                                                                                                                                                            WindowManager mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                                                                                                                                                                            mWindowManager.removeView(TVmallAnimationActivityNew.this);

                                                                                                                                                                        }

                                                                                                                                                                        wholeCatAnimation.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onAnimationStart(Animation animation) {
                                                                                                                                                                                iv_toast.setVisibility(View.VISIBLE);
                                                                                                                                                                            }

                                                                                                                                                                            @Override
                                                                                                                                                                            public void onAnimationEnd(Animation animation) {

                                                                                                                                                                                new Handler().postDelayed(new Runnable() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void run() {
                                                                                                                                                                                        AlphaAnimation alphaAnimation1 = new AlphaAnimation(1, 0);
                                                                                                                                                                                        alphaAnimation1.setDuration(2000);

                                                                                                                                                                                        iv_toast.startAnimation(alphaAnimation1);

                                                                                                                                                                                        alphaAnimation1.setAnimationListener(new Animation.AnimationListener() {
                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onAnimationStart(Animation animation) {
                                                                                                                                                                                                iv_toast.setVisibility(View.INVISIBLE);

                                                                                                                                                                                            }

                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onAnimationEnd(Animation animation) {
                                                                                                                                                                                                WindowManager mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                                                                                                                                                                                                mWindowManager.removeView(TVmallAnimationActivityNew.this);
                                                                                                                                                                                            }

                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onAnimationRepeat(Animation animation) {

                                                                                                                                                                                            }
                                                                                                                                                                                        });
                                                                                                                                                                                    }
                                                                                                                                                                                }, 2000);
                                                                                                                                                                            }

                                                                                                                                                                            @Override
                                                                                                                                                                            public void onAnimationRepeat(Animation animation) {

                                                                                                                                                                            }
                                                                                                                                                                        });
                                                                                                                                                                    }

                                                                                                                                                                    @Override
                                                                                                                                                                    public void onAnimationEnd(Animation animation) {
                                                                                                                                                                        iv_wheat_bg.setVisibility(View.INVISIBLE);
                                                                                                                                                                        ll_text.setVisibility(View.INVISIBLE);
                                                                                                                                                                        iv_new_cat.setVisibility(View.INVISIBLE);
                                                                                                                                                                    }

                                                                                                                                                                    @Override
                                                                                                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                                                                                                    }
                                                                                                                                                                });
                                                                                                                                                            }
                                                                                                                                                        }, 5000);

                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                            }
                                                                                                                                        }, 30);
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }

                                                                                                                            @Override
                                                                                                                            public void onAnimationRepeat(Animation animation) {

                                                                                                                            }
                                                                                                                        });
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onAnimationEnd(Animation animation) {


                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }, 800);

                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }, 800);
                                                                                    }

                                                                                    @Override
                                                                                    public void onAnimationRepeat(Animation animation) {

                                                                                    }
                                                                                });
                                                                            }
                                                                        }, 800);
                                                                    }

                                                                    @Override
                                                                    public void onAnimationRepeat(Animation animation) {

                                                                    }
                                                                });
                                                            }
                                                        }, 800);
                                                    }

                                                    @Override
                                                    public void onAnimationRepeat(Animation animation) {

                                                    }
                                                });
                                            }
                                        }, 800);

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 1500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void findView(View view) {
        iv_reciprocal_bubble_count1 = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble_count1);
        iv_reciprocal_bubble_count2 = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble_count2);
        iv_reciprocal_bubble_count3 = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble_count3);
        iv_reciprocal_bubble_count4 = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble_count4);
        iv_reciprocal_bubble_count5 = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble_count5);
        iv_red_mall = (ImageView) view.findViewById(R.id.iv_red_mall);
        iv_come_toast_frame = (ImageView) view.findViewById(R.id.iv_come_toast_frame);
        iv_reciprocal_bubble = (ImageView) view.findViewById(R.id.iv_reciprocal_bubble);
        iv_wheat_bg = (ImageView) view.findViewById(R.id.iv_wheat_bg);
        iv_toast = (ImageView) view.findViewById(R.id.iv_toast);
        iv_new_cat = (ImageView) view.findViewById(R.id.iv_new_cat);
        ll_text = (LinearLayout) view.findViewById(R.id.ll_text);
        get_bonus_success = (TextView) view.findViewById(R.id.get_bonus_success);
        get_bonus = (TextView) view.findViewById(R.id.get_bonus);
        get_bonus_lucky = (TextView) view.findViewById(R.id.get_bonus_lucky);
    }

    /**
     *
     * @param type 0 双11现金红包, 1 优惠券, 2 双11购物券, 3 流量券, 4 没中奖
     * @param bouns 金额
     */
    public void setText(int type, String bouns) {
        Map<String, String> properties = Utils.getProperties();
        properties.put("spm", SPMConfig.LIVE_RED_PACKET_SHOW_SPM);
        //淘宝红包=tbHongBao
        //支付宝红包=zfbHongBao
        //优惠券=voucher
        //购物津贴=JinTie
        isShowivToast = true;
        if (type == 0) {
            get_bonus_lucky.setText("运气不错");
            get_bonus_success.setText("成功抢到电视淘宝红包");
            get_bonus.setText(bouns);
            properties.put("name","tbHongBao");
        } else if (type == 1){
            get_bonus_lucky.setText("不错哦");
            get_bonus_success.setText("获得双11优惠券");
            get_bonus.setText(bouns);
            properties.put("name","voucher");
        } else if (type == 2) {
            get_bonus_lucky.setText("运气不错");
            get_bonus_success.setText("抢到双11购物券");
            get_bonus.setText(bouns);
            properties.put("name","JinTie");
        } else if (type == 3) {
            get_bonus_lucky.setText("运气不错");
            get_bonus_success.setText("抢到阿里通信流量券");
            get_bonus.setText(bouns);
            properties.put("name","voucher");
        } else if (type == 4) {
            get_bonus_lucky.setText("不错哦");
            get_bonus_success.setText("获得店铺优惠券");
            get_bonus.setText(bouns);
            properties.put("name","voucher");
        } else {
            get_bonus_lucky.setText("哎哟");
            get_bonus_success.setText("就差一点点就中奖了");
            get_bonus.setText("");
            isShowivToast = false;
        }
        Utils.utControlHit(((TBaoLiveActivity)mContext).getFullPageName(),"Expose_GiveRight_name", properties);
    }
}