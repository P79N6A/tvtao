package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yunos.tv.app.widget.LinearLayout;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * Created by chenjiajuan on 17/12/26.
 *
 * @describe
 */

public class PromptPop extends PopupWindow {
    private View view;
    private TextView tvPrompt;
    private static PromptPop promptPop;
    private BaseActivity context;

    public static PromptPop getInstance(Context context) {
        if (promptPop == null) {
            synchronized (SkuSelectDialog.class) {
                if (promptPop == null) {
                    promptPop = new PromptPop(context);
                }
            }
        }
        return promptPop;
    }

    public PromptPop(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.pop_prompt, null);
        this.setContentView(view);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        tvPrompt = (TextView) view.findViewById(R.id.tv_prompt);
        this.context = (BaseActivity) context;
    }

    public void showPromptWindow(BaseActivity context,String text) {
        if (!TextUtils.isEmpty(text) && tvPrompt != null) {
            tvPrompt.setText(text);
        }
        this.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        if (context != null) {
            context.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 2000);
        }
    }

    public void showPromptWindow(String text) {
        if (!TextUtils.isEmpty(text) && tvPrompt != null) {
            tvPrompt.setText(text);
        }
        this.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        if (context != null) {
            context.getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, 2000);
        }
    }
    public static void destory() {
        if (promptPop != null) {
            if (promptPop.view != null) {
                promptPop.view = null;
            }
            if (promptPop.tvPrompt != null) {
                promptPop.tvPrompt = null;
            }
            if (promptPop.context != null) {
                promptPop.context = null;
            }
            promptPop = null;
        }
    }
}
