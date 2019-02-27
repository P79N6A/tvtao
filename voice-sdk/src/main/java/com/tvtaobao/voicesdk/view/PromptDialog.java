package com.tvtaobao.voicesdk.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;

/**
 * Created by chenjiajuan on 17/12/26.
 *
 * @describe
 */

public class PromptDialog extends Activity {
    private View view;
    private TextView tvPrompt;
    private String errorDesc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_prompt);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        errorDesc = getIntent().getStringExtra("errorDesc");
        setPrompt(errorDesc);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }


    public void setPrompt(String text) {
        if (!TextUtils.isEmpty(text) && tvPrompt != null) {
            tvPrompt.setText(text);

            ASRNotify.getInstance().playTTS(text);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }

}
