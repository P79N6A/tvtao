package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.tvtaobao.voicesdk.view.AutoTextView;
import com.tvtaobao.voicesdk.R;

import java.util.List;

/**
 * Created by yuanqihui on 2018/2/28.
 */

public class TipsDialog extends BaseDialog {
    private final String TAG = "TipsDialog";

    private TextView mTts;
    private AutoTextView mTips;
    private RelativeLayout mLayoutTips;

    public TipsDialog(Context context) {
        super(context);

        this.setContentView(R.layout.dialog_tips);
        initView();
    }

    public void initView() {
        Log.d(TAG, TAG + ".initView");
        mTts = (TextView) findViewById(R.id.voice_card_search_tts);
        mTips = (AutoTextView) findViewById(R.id.voice_card_search_tips);
        mLayoutTips = (RelativeLayout) findViewById(R.id.voice_layout_tips);
    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
        delayDismiss(7 * 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        dismiss();
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }

    //设置tts语音播报
    public void setTts(String tts, String spoken) {
        LogPrint.d(TAG, TAG + ".setPrompt");
        if (mTts != null) {
            mTts.setText(tts);
        }

        playTTS(spoken);
    }

    //设置tips
    public void setTips(List<String> tips) {
        if (tips != null && tips.size() > 0 && mTips != null) {
            mLayoutTips.setVisibility(View.VISIBLE);
            mTips.autoScroll(tips);
        } else {
            mLayoutTips.setVisibility(View.INVISIBLE);
        }
    }
}
