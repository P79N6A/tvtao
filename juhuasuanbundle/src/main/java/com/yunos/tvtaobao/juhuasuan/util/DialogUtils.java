/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.util
 * FILE NAME: DialogUtils.java
 * CREATED TIME: 2015-1-7
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.util;


import android.content.Context;
import android.content.DialogInterface;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.DialogParams;

/**
 * dialog显示工具
 * @version
 * @author hanqi
 * @data 2015-1-7 下午7:46:42
 */
public class DialogUtils {

    private static final String TAG = "DialogUtils";

    public static void show(Context context, int msgResId) {
        show(DialogParams.makeParams(context).setMsgResId(msgResId));
    }

    public static void show(Context context, int msgResId, DialogInterface.OnClickListener positiveButtonListener) {
        show(DialogParams.makeParams(context).setMsgResId(msgResId).setPositiveButtonListener(positiveButtonListener));
    }

    public static void show(Context context, String msg) {
        show(DialogParams.makeParams(context).setMsg(msg));
    }

    public static void show(Context context, String msg, DialogInterface.OnClickListener positiveButtonListener) {
        show(DialogParams.makeParams(context).setMsg(msg).setPositiveButtonListener(positiveButtonListener));
    }

    public static void show(final DialogParams params) {
        TvTaoBaoDialog dialog = get(params);
        dialog.show();
        AppDebug.i(TAG, TAG + ".show msg=" + params.getMsg());
    }

    public static TvTaoBaoDialog get(final DialogParams params) {
        Context context = params.getContext();
        String msg = params.getMsg();
        if (null == msg && null != params.getMsgResId()) {
            msg = context.getString(params.getMsgResId());
        }
        String mPositiveButtonText = params.getPositiveButtonText();
        if (null == mPositiveButtonText && null != params.getPositiveButtonTextResId()) {
            mPositiveButtonText = context.getString(params.getPositiveButtonTextResId());
        }
        String mNegativeButtonText = params.getNegativeButtonText();
        if (null == mNegativeButtonText && null != params.getNegativeButtonTextResId()) {
            mNegativeButtonText = context.getString(params.getNegativeButtonTextResId());
        }
        //当没有positiveButton和没有negativeButton时，取positiveButton的默认文字
        if (null == mPositiveButtonText && null == mNegativeButtonText) {
            mPositiveButtonText = context.getString(R.string.jhs_confirm);
        }
        DialogInterface.OnClickListener mPositiveButtonListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != params.getPositiveButtonListener()) {
                    params.getPositiveButtonListener().onClick(dialog, which);
                }
                if (params.isPositiveButtonClickDismiss()) {
                    dialog.dismiss();
                }
            }
        };
        DialogInterface.OnClickListener mNegativeButtonListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != params.getNegativeButtonListener()) {
                    params.getNegativeButtonListener().onClick(dialog, which);
                }
                if (params.isNegativeButtonClickDismiss()) {
                    dialog.dismiss();
                }
            }
        };
        TvTaoBaoDialog dialog = new TvTaoBaoDialog.Builder(params.getContext()).setMessage(msg)
                .setPositiveButton(mPositiveButtonText, mPositiveButtonListener)
                .setNegativeButton(mNegativeButtonText, mNegativeButtonListener).setCancelable(params.getCancelable())
                .create();
        if (null != params.getOnKeyListener()) {
            dialog.setOnKeyListener(params.getOnKeyListener());
        }
        AppDebug.i(TAG, TAG + ".get msg=" + params.getMsg());
        return dialog;
    }
}
