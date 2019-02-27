package com.yunos.tvtaobao.answer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import com.yunos.tvtaobao.answer.R;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class BackDialog extends Dialog {
    public BackDialog(@NonNull Context context) {
        super(context);
    }

    public BackDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_hq_video_back);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                return true;
            }

            if (event.getAction() == KeyEvent.ACTION_UP) {
                dismiss();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
