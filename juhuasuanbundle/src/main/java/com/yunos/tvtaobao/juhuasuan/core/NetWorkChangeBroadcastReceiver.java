package com.yunos.tvtaobao.juhuasuan.core;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck.*;
/**
 * 网络连接后接收器
 * @author hanqi
 */
public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "NetWorkChangeBroadcastReceiver";
    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private boolean mFirstStartReceiver = true;
    private NetWorkConnectedCallBack mCallBack;
    private TvTaoBaoDialog mDialog;
    private NetWorkHandle mHandler = new NetWorkHandle(this);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!mFirstStartReceiver) {
            String action = intent.getAction();
            boolean netWork = NetWorkUtil.isNetWorkAvailable();
            AppDebug.i(TAG, TAG + ".onReceive action=" + action + ", isNetWorkAvailable=" + netWork
                    + ", " + TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION) + ", context="
                    + context.getClass().getName());
            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
                if (netWork) {
                    AppDebug.i(TAG, TAG + ".onReceive mDialog=" + mDialog + ", mCallBack=" + mCallBack);
                    mDialog.dismiss();
                    NetWorkCheck.unRegisterReceiver(context);

                    AppDebug.i(TAG, TAG + ".onReceive mCallBack =" + mCallBack);
                    if (null != mCallBack) {
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                AppDebug.i(TAG, TAG + ".onReceive mHandler.run");
                                mCallBack.connected();
                            }
                        }, 3000);
                    }
                } else if (!netWork && null != mDialog) {
                    mDialog.show();
                }
            } else {
                AppDebug.i(TAG, TAG + ".onReceive action=" + action + ", " + CONNECTIVITY_CHANGE_ACTION);
            }
        } else {
            mFirstStartReceiver = false;
        }
    }

    public void setCallBack(NetWorkConnectedCallBack callBack) {
        mCallBack = callBack;
    }

    public void setDialog(TvTaoBaoDialog dialog) {
        if (null != mDialog) {
            mDialog.dismiss();
        }
        mDialog = dialog;
    }

    public TvTaoBaoDialog getDialog() {
        return mDialog;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NetWorkChangeBroadcastReceiver[mCallBack=" + mCallBack + ", mDialog=" + mDialog + ", this="
                + super.toString() + "]";
    }

    private static final class NetWorkHandle extends AppHandler<NetWorkChangeBroadcastReceiver> {

        public NetWorkHandle(NetWorkChangeBroadcastReceiver t) {
            super(t);
        }

    }

}
