package com.yunos.tvtaobao.payment.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunos.tvtaobao.payment.R;
import com.yunos.tvtaobao.payment.analytics.Utils;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2017/12/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AlphaDialog extends Dialog {

    private Window window = null;

    public AlphaDialog(Context context, View view) {
        super(context);

        setContentView(view);

        windowDeploy();
    }

    public AlphaDialog(Context context, @LayoutRes int resId) {
        super(context);

        setContentView(resId);

        windowDeploy();
    }

    @Override
    public void show() {
        super.show();
        Utils.utCustomHit("Expore_login_Disuse", Utils.getProperties());

    }

    //设置窗口显示
    public void windowDeploy() {
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.AlphaAnimDialog); //设置窗口弹出动画
        window.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(wl);
    }
}
