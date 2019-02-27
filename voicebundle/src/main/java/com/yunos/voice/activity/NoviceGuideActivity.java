package com.yunos.voice.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.voice.R;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/25
 *     desc : 新手引导页面
 *     version : 1.0
 * </pre>
 */

public class NoviceGuideActivity extends BaseActivity {
    private static final String TAG = NoviceGuideActivity.class.getSimpleName();
    private String tts = "购买带有快捷标签的商品，可直接生成订单并寄送到默认地址。语音购物就是这么简单。";

    private TextView reply;
    private TimerHandler timerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogPrint.i(TAG, TAG + ".onCreate");
        setContentView(R.layout.activity_novice_guide);

        reply = (TextView) findViewById(R.id.novice_guide_reply);

        int timer = 9;
        boolean isToBuy = getIntent().getBooleanExtra("tobuy", false);
        if (isToBuy) {
            tts += "继续为您下单。";
            timer += 3;
        }

        playTTS(tts);

        timerHandler = new TimerHandler(this);
        timerHandler.sendEmptyMessageDelayed(0, timer * 1000);
        LogPrint.i(TAG, TAG + ".onCreate timer : " + timer);

        SharePreferences.put("isShowNoviceGuide", false);
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        PageReturn pageReturn = new PageReturn();
        switch (object.getIntent()) {
            case ActionType.BACK:
                pageReturn.isHandler = true;
                finish();
                break;
            case ActionType.OK:
                pageReturn.isHandler = true;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(9990);
                        finish();
                    }
                }, 300);
                break;
        }

        return pageReturn;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogPrint.i(TAG, TAG + ".onPause");
        timerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogPrint.i(TAG, TAG + ".onDestroy");
    }

    public void playTTS(String tts) {
        reply.setText(tts);

        ASRNotify.getInstance().playTTS(tts);
    }

    private static class TimerHandler extends Handler {
        private WeakReference<NoviceGuideActivity> noviceGuideActivityWR;
        public TimerHandler(NoviceGuideActivity noviceGuideActivity) {
            noviceGuideActivityWR = new WeakReference<NoviceGuideActivity>(noviceGuideActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogPrint.i(TAG, TAG + ".TimerHandler what : " + msg.what);
            switch (msg.what) {
                case 0:
                    if (noviceGuideActivityWR != null && noviceGuideActivityWR.get() != null) {
                        LogPrint.i(TAG, TAG + ".TimerHandler setResult");
                        noviceGuideActivityWR.get().setResult(9990);
                        noviceGuideActivityWR.get().finish();
                    }
                    break;
            }
        }
    }
}
