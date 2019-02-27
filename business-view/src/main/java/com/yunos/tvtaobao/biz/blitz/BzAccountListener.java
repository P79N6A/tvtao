package com.yunos.tvtaobao.biz.blitz;


import com.yunos.tv.blitz.listener.BzAppGlobalListener;
import com.yunos.tv.core.common.AppDebug;

public class BzAccountListener implements BzAppGlobalListener {

    private String TAG = "BzAccountListener";

    @Override
    public void onAccountUpdate(String arg0, String arg1) {
        AppDebug.v(TAG, TAG + ".onRequestDetainMent --> onAccountUpdate --> arg0 = " + arg0 + "; arg1 = " + arg1);
    }

}
