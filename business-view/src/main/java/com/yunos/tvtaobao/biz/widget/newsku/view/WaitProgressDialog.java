package com.yunos.tvtaobao.biz.widget.newsku.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yunos.tvtaobao.businessview.R;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/7/24
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class WaitProgressDialog extends Dialog {
    public WaitProgressDialog(@NonNull Context context) {
        super(context, R.style.DialogNoTitleStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
    }
}
