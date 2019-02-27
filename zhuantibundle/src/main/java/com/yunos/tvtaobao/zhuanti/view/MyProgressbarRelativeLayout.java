package com.yunos.tvtaobao.zhuanti.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.yunos.tvtaobao.zhuanti.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by dingbin on 2017/4/19.
 */

public class MyProgressbarRelativeLayout extends RelativeLayout {
    private long currentProgress;
    private Animation animation_fade_in;
    private Animation animation_fade_out;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout_control;
    private View view;
    private ImageView iv_on_progress;
    private TextView tv_total_time;
    private TextView tv_reachec_time;
    private RelativeLayout rl_contains_iv_on_progress;
    private LinearLayout ll_next_last_btn;

    public MyProgressbarRelativeLayout(Context context) {
        super(context);
        initProgress(context);
    }

    public MyProgressbarRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgress(context);
    }

    public MyProgressbarRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProgress(context);
    }

    public void initProgress(Context context){
        animation_fade_in = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation_fade_out = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.myprogressbar_layout, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        relativeLayout_control = (RelativeLayout) view.findViewById(R.id.control_relative);
        ll_next_last_btn = (LinearLayout) view.findViewById(R.id.ll_next_last_btn_layout);
        iv_on_progress = (ImageView) view.findViewById(R.id.iv_on_progressbar);
        tv_total_time = (TextView) view.findViewById(R.id.total_time);
        tv_reachec_time = (TextView) view.findViewById(R.id.reached_time);
        rl_contains_iv_on_progress = (RelativeLayout) view.findViewById(R.id.rl_contain_iv_on_progress);
        addView(view);
    }

    public void setMaxProgress(long totalprogress){
        setTotalTime(totalprogress);
        totalProgress = totalprogress;
        progressBar.setMax((int)totalProgress);
    }
    private long totalProgress;
    public void setCurrentProgress(long progress){
        //setTime(progress);
        currentProgress = progress;
        progressBar.setProgress((int)currentProgress);
        LayoutParams layoutParams = (LayoutParams) relativeLayout_control.getLayoutParams();
        float leftPosition = (progressBar.getWidth()*progress)/totalProgress + progressBar.getLeft()-relativeLayout_control.getWidth()/2;
        layoutParams.leftMargin =(int) Math.ceil(leftPosition);
        relativeLayout_control.setLayoutParams(layoutParams);
    }
    public int getCurrentProgress(){
        return progressBar.getProgress();
    }
    public void showOrHide(boolean b){
        if (b){
            setVisibility(VISIBLE);
        }else{
            setVisibility(INVISIBLE);
        }
    }
    public void setTotalTime(long totalTime){
        Date date = new Date(totalTime);
        if (totalTime/1000/60/60>=1) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            tv_total_time.setText(format.format(date));
        }else if (totalTime/1000/60/60<1&&totalTime/1000/60>=0){
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            tv_total_time.setText(format.format(date));
        }
    }
    public void setTime(long reachTime){
        Date date = new Date(reachTime);
        if (reachTime/1000/60/60>=1) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            tv_reachec_time.setText(format.format(date));
        }else if (reachTime/1000/60/60<1&&reachTime/1000/60>=0){
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            tv_reachec_time.setText(format.format(date));
        }
    }

    public void setCurrentProgressOnProgressBar(long progress){
        LayoutParams layoutParams = (LayoutParams) rl_contains_iv_on_progress.getLayoutParams();
        float leftPosition = (progressBar.getWidth()*progress)/totalProgress + progressBar.getLeft()-rl_contains_iv_on_progress.getWidth()/2;
        layoutParams.leftMargin =(int) Math.ceil(leftPosition);
        rl_contains_iv_on_progress.setLayoutParams(layoutParams);
    }
    public void hideControl(boolean flag){
        if (flag) relativeLayout_control.setVisibility(INVISIBLE);
        else relativeLayout_control.setVisibility(VISIBLE);

    }
    public void setControlImageview(boolean flag,Context context){
        if (flag){//显示大图标
            iv_on_progress.setImageDrawable(context.getResources().getDrawable(R.drawable.progress_iv_onfocus));
        }else{
            iv_on_progress.setImageDrawable(context.getResources().getDrawable(R.drawable.progress_iv_nofocus));
        }

    }

    /**
     * 只有一个视频时，隐藏上一个下一个按钮
     * @param flag
     */
    public void hideLastNextBtn(boolean flag){

        if (flag){
            ll_next_last_btn.setVisibility(INVISIBLE);
        }else{
            ll_next_last_btn.setVisibility(VISIBLE);
        }
    }
}
