package com.yunos.tvtaobao.zhuanti.view;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunos.tvtaobao.zhuanti.R;


/**
 * Created by chenjiajuan on 17/5/11.
 */

public class PopAddTipDialog extends Dialog {
    private Context mContext;
    private TextView tv_pop_add_tip;

    public PopAddTipDialog(Context context) {
        this(context, R.style.Dialog_Fullscreen);
    }

    public PopAddTipDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.0f;
        //设置背景全部变暗的效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(R.style.pop_add_tip_animation);
        getWindow().setAttributes(l);
        setContentView(R.layout.cytz_pop_add_tip);
        tv_pop_add_tip= (TextView) findViewById(R.id.tv_pop_add_tip);
    }

    public void  showDialog(String text){
        tv_pop_add_tip.setText(text);
        if (!isShowing())
        show();
    }
}
