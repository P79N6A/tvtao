package com.yunos.tvtaobao.flashsale.utils;

import android.util.Log;

public class FullVideoCountdown extends CountDownTimerCustom{

    static final String TAG = "countdown";

    public interface ICountdownListener{
        public void onTick(long millsUntilFinished);
        public void onFinish();
        public void onAnimtionPause();
    }
   
    private ICountdownListener mListener = null;
    public FullVideoCountdown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onTick(long millisUntilFinished) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onTick:" + millisUntilFinished);
        if(mListener != null){
            mListener.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onFinish");
        if(mListener != null){
            mListener.onFinish();
        }
    }

    @Override
    public void onAnimtionPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause");
        if(mListener != null){
            mListener.onAnimtionPause();
        }
    }

    public ICountdownListener getListener() {
        return mListener;
    }

    public void setListener(ICountdownListener listener) {
        this.mListener = listener;
    }

}
