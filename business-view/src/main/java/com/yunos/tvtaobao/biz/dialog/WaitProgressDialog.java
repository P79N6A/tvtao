package com.yunos.tvtaobao.biz.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.businessview.R;

public class WaitProgressDialog extends Dialog {

     
    public WaitProgressDialog(Context context) {
        super(context, R.style.ytbv_DialogNoTitleStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ytbv_dialog_wait_progress);
        AppDebug.d("WaitProgressDialog", "thread id:"+Thread.currentThread().getId());
    } 
}
