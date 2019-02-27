package com.yunos.tvtaobao.tvlive.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by pan on 16/12/18.
 */

public class TimerToast {
    private Context mContext;
    private CharSequence mText;
    private int mDuration = 0;
    private Toast toast;
    private int what = 1;
    private int time = 1000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == what) {
                toast.show();
                if (mDuration >= time) {
                    mDuration -= time;
                    sendEmptyMessageDelayed(what, time);
                } else {
                    removeMessages(what);
                }
            }
        }
    };

    public TimerToast(Context context, CharSequence text, int duration) {
        mContext = context;
        mText = text;
        mDuration = duration - time;
    }

    public static TimerToast makeText(Context context, CharSequence text, int duration) {
        TimerToast timerToast = new TimerToast(context, text, duration);
        return timerToast;
    }

    public void show() {
        toast = Toast.makeText(mContext, mText, Toast.LENGTH_SHORT);
        mHandler.sendEmptyMessage(what);
    }

    public void hide() {
        mHandler.removeMessages(what);
        if (toast != null) {
            toast.cancel();
        }
    }
}
