package com.yunos.tvtaobao.biz.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunos.tvtaobao.businessview.R;

public class TextProgressDialog extends Dialog {
    private TextView text;

    public void setText(CharSequence text) {
        this.text.setText(text);

    }

    public CharSequence getText() {
        return text.getText();
    }

    public TextProgressDialog(Context context) {
        super(context, R.style.ytbv_QR_Dialog);
        setContentView(R.layout.ytbv_text_progress);
        text = (TextView) findViewById(R.id.progressText);
        Window window = getWindow(); //得到对话框
        window.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);

    }
}
