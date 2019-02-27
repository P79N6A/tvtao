package com.yunos.tv.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;

public class DebugTestBuilder {
    private static final String TAG = "DebugTestBuilder";
    private static final long TIME_SPACE = 3000; // 有效时间的间隔
    private boolean isUp;
    private boolean isDown;
    private boolean isLeft;
    private boolean isRight;
    private Handler mHandler = new Handler();
    private Context mContext;
    private DebugTestDialog dialog;
    private String key;
    private boolean bl_env=true;
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            isLeft = false;
            isUp = false;
            isDown = false;
            isRight = false;
            key = null;
            mHandler.postDelayed(this, TIME_SPACE);
        }
    };

    public DebugTestBuilder(Context context) {
        mContext = context;
        mHandler.postDelayed(runnable, TIME_SPACE);
    }


    public void showDialog() {
        if (Config.isDebug()) {
            isLeft = false;
            isUp = false;
            isDown = false;
            isRight = false;
            key = null;
            dialog = new DebugTestDialog(mContext);
            dialog.show();
        }
    }

    public void showDialog(Boolean bl_env) {
        if (Config.isDebug()) {
            isLeft = false;
            isUp = false;
            isDown = false;
            isRight = false;
            key = null;
            dialog = new DebugTestDialog(mContext,bl_env);
            dialog.show();
        }
    }

    public void onDistroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        dialog = null;
        mContext = null;
    }

    public void onKeyAction(int keyCode) {
        AppDebug.d(TAG,"keycode ++++++ " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            isLeft = true;
            key = "l";
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            isUp = true;
            key += "u";
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            isDown = true;
            key += "d";
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            isRight = true;
            key += "r";
        }
        if (isUp && isDown && isLeft && isRight && "ludr".equals(key)) {
            showDialog();
        }
        if(isLeft&& isRight && "lrrr".equals(key)){
            Intent intent=new Intent();
            intent.setClassName(mContext, "com.yunos.tvtaobao.live.activity.TBaoLiveActivity");
            mContext.startActivity(intent);
        }
        if(isLeft&&isUp && isRight  && "lurr".equals(key)){
            showDialog(bl_env);
        }
    }
}