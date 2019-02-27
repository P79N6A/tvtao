package com.yunos.tvtaobao.biz.widget;

import android.content.Context;

import com.yunos.tvtaobao.businessview.R;

/**
 * Created by linmu on 2018/10/23.
 */

public class CustomDialogShow {
    public static CustomDialog customDialog;
    public static void onPromptDialog(String prompt, Context context) {
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(context)
                .setType(1)
                .setResultMessage(prompt).setStyle(R.style.NoBackgroundRoundRoundCornerDialog);
        customDialog = mBuilder.create();

        customDialog.show();
    }
}
