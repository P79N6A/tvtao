package com.tvtaobao.voicesdk.dialogs.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.interfaces.ASRHandler;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pan on 2017/10/27.
 */

public class BaseDialog extends Dialog implements ASRHandler {
    private final String TAG = "BaseDialog";

    private Animation mAnimationIn; // 进入时的动画
    private Animation mAnimationOut; // 消失时的动画

    private ASRNotify mNotify;
    private TimeHandler timeHandler;

    public BaseDialog(Context context) {
        this(context, R.style.voice_card_dialog);
    }

    private BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        LogPrint.e(TAG, TAG + ".构造");

        mNotify = ASRNotify.getInstance();

//        getWindow().setWindowAnimations(R.style.voice_card_animation);
        Window mWindow = getWindow();
//        mWindow.setType(WindowManager.LayoutParams.TYPE_PHONE);// 系统对话框
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);// 增加硬件加速
        WindowManager.LayoutParams l = mWindow.getAttributes();
        l.dimAmount = 0.0f;
        mWindow.setAttributes(l);

        mAnimationIn = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.voice_dialog_enter);
        mAnimationOut = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.voice_dialog_exit);
    }

    @Override
    public void show() {
        LogPrint.e(TAG, TAG + ".show : " + this.getClass().getSimpleName());
        DialogManager.getManager().dismissAllDialog();
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        super.show();
        mNotify.setHandler(this);
//        DialogManager.getManager().dismissBeforeDialog();
        ActivityUtil.addVoiceDialog(this);
    }

    @Override
    public PageReturn onASRNotify(DomainResultVo object) {
        PageReturn pageReturn = new PageReturn();
        if (ActionType.BACK.equals(object.getIntent())) {
            pageReturn.isHandler = true;

            dismiss();
        }
        return pageReturn;
    }

    @Override
    public void dismiss() {
        LogPrint.e(TAG, TAG + ".dismiss : " + this.getClass().getSimpleName());
        AppDebug.i("dialogASRNotify", "setHandler(null)");
        mNotify.setHandler(null);
        ActivityUtil.addVoiceDialog(null);
        super.dismiss();
//        if (mAnimationOut != null) {
//            mAnimationOut.setAnimationListener(new Animation.AnimationListener() {
//
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    BaseDialog.super.dismiss();
//                    onDestory();
//                }
//            });
//            if (mFocusPositionManager != null) {
//                mFocusPositionManager.startAnimation(mAnimationOut);
//            }
//        }
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
//        return false;
//    }
//

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void playTTS(String msg) {
        mNotify.playTTS(msg);
    }

    public void delayDismiss(int delayMillis) {
        if (timeHandler == null) {
            timeHandler = new TimeHandler();
        }
        timeHandler.sendEmptyMessageDelayed(0, delayMillis);
    }


    private static class TimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DialogManager.getManager().dismissAllDialog();
        }
    }

    /**
     * 获取统计最简的Properties
     *
     * @return
     */
    protected Map<String, String> getProperties() {
        return getProperties(null);
    }

    protected Map<String, String> getProperties(String asr) {
        Map<String, String> p = new HashMap<String, String>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }
        p.put("channel", Config.getChannelName());
        if (!TextUtils.isEmpty(asr)) {
            p.put("asr", asr);
        }
        return p;
    }
}
