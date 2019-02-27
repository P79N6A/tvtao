package com.tvtaobao.voicesdk.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanqihui on 2018/2/28.
 */

public class TipsActivity extends Activity {
    private final String TAG = "TipsDialog";

    private Context mContext;

    private TextView mTts;
    private AutoTextView mTips;
    private String tts;
    private ArrayList<String> tips = new ArrayList<>();
    private View tipsView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_tips);
        initView();
    }

//    public TipsDialog(Context context) {
//        super(context);
//        this.mContext = context;
//
//
//    }

    public void initView() {
        Log.d(TAG, TAG + ".initView");
        mTts = (TextView) findViewById(R.id.voice_card_search_tts);
        mTips = (AutoTextView) findViewById(R.id.voice_card_search_tips);


        tts = getIntent().getStringExtra("tts");
        tips = getIntent().getStringArrayListExtra("tips");
        setTts(tts);
        setTips(tips);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 7000);
    }

//    @Override
//    public void show() {
//        DialogManager.getManager().pushDialog(this);
//        new android.os.Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismiss();
//            }
//        },5000);
//        super.show();
//    }
//
//    @Override
//    public boolean onASRNotify(String key) {
//        return super.onASRNotify(key);
//    }

    //    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        return super.onKeyUp(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//
//        return super.onKeyUp(keyCode, event);
//    }
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }

    //设置tts语音播报
    public void setTts(String tts) {
        Log.d(TAG, TAG + ".setPrompt");
        if (mTts != null) {
            mTts.setText(tts);
        }
        ASRNotify.getInstance().playTTS(tts);
    }

    //设置tips
    public void setTips(List<String> tips) {
        StringBuilder stringBuilder = new StringBuilder();
        if (tips != null && tips.size() > 0 && mTips != null) {
//            for (int i = 0; i < tips.size(); i++) {
//                stringBuilder.append(tips.get(i));
//                if (i < tips.size() - 1) {
//                    stringBuilder.append("     ");
//                }
//            }
//            if (stringBuilder != null) {
//                mTips.setText(stringBuilder);
//            }
            mTips.autoScroll(tips);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
