package com.yunos.tvtaobao.juhuasuan.request.listener;


import android.app.Dialog;

import com.yunos.tvtaobao.biz.activity.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * BrandHomeActivity 中 CategorySelecteDialog 类调用接口时用到的侦听
 * @version
 * @author hanqi
 * @data 2015-2-27 下午1:00:05
 */
public abstract class JuBrandCateSelectDialogRetryRequestListener<T> extends JuRetryRequestListener<T> {

    protected WeakReference<Dialog> mDialog;

    public JuBrandCateSelectDialogRetryRequestListener(BaseActivity activity, Dialog dialog) {
        super(activity);
        mDialog = new WeakReference<Dialog>(dialog);
    }

    public JuBrandCateSelectDialogRetryRequestListener(BaseActivity activity, Dialog dialog, boolean finish) {
        super(activity, finish);
        mDialog = new WeakReference<Dialog>(dialog);
    }

    public Dialog getDialog() {
        return mDialog.get();
    }

}
