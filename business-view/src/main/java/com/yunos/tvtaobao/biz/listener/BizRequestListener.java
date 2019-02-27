/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.listener
 * FILE NAME: BizRequestListener.java
 * CREATED TIME: 2015-1-16
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.listener;


import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;

import java.lang.ref.WeakReference;

public abstract class BizRequestListener<T> implements RequestListener.RequestListenerWithLogin<T> {

    private final String TAG = "BizRequestListener";
    protected final WeakReference<BaseActivity> mBaseActivityRef;
    private RequestErrorListener mRequestErrorListener;

    public BizRequestListener(WeakReference<BaseActivity> baseActivityRef) {
        this.mBaseActivityRef = baseActivityRef;
    }

    @Override
    public void onStartLogin() {
        if (mBaseActivityRef != null && mBaseActivityRef.get() != null) {
            mBaseActivityRef.get().setLoginActivityStartShowing();
        }
    }

    public void onRequestDone(T data, int resultCode, String msg) {
        AppDebug.e("TAG", "data : " + data + " resultCode : " + resultCode + " msg : " + msg);
        if (mBaseActivityRef.get() == null) {
            return;
        }
        if (mBaseActivityRef.get().isFinishing()) {
            return;
        }
        if (resultCode == 200) {
            onSuccess(data);
        } else {
            initErrorListener(resultCode);
            boolean result = onError(resultCode, msg);
            if (!result && mRequestErrorListener != null) {
                mRequestErrorListener.onError(resultCode, msg);
            }
        }
    }

    /**
     * 当接口返回错误时的处理
     * 如果return false将执行默认的处理
     *
     * @return
     */
    public abstract boolean onError(int resultCode, String msg);

    /**
     * 当接口返回成功时的处理
     *
     * @param data
     */
    public abstract void onSuccess(T data);

    /**
     * 关闭错误提示框时是否finish activity
     *
     * @return
     */
    public abstract boolean ifFinishWhenCloseErrorDialog();

    /**
     * 是否显示error dialog
     */
    public boolean isShowErrorDialog() {
        return true;
    }

    /**
     * 返回结果的默认处理
     */
    private void initErrorListener(int resultCode) {
        //网络未连接
        if (resultCode == 1) {
            mRequestErrorListener = new RequestErrorListener() {

                @Override
                public boolean onError(int errorCode, String errorMsg) {
                    BaseActivity baseActivity = mBaseActivityRef.get();
                    if (baseActivity != null && !baseActivity.isFinishing()) {
                        baseActivity.showNetworkErrorDialog(ifFinishWhenCloseErrorDialog());
                    }
                    return true;
                }
            };
        } else if (resultCode == ServiceCode.CLIENT_LOGIN_ERROR.getCode()) {
            mRequestErrorListener = null;
        } else if (resultCode == ServiceCode.API_NOT_LOGIN.getCode() || resultCode == ServiceCode.API_SID_INVALID.getCode()) {
            //用户未登录或session过期
            mRequestErrorListener = new RequestErrorListener() {

                @Override
                public boolean onError(int errorCode, String errorMsg) {
                    BaseActivity baseActivity = mBaseActivityRef.get();
                    if (baseActivity != null) {
                        try {
//                            TYIDManager mTYIDManager = TYIDManager.get(CoreApplication.getApplication());
//                            int loginStatus = mTYIDManager.yunosGetLoginState();
                            boolean loginStatus = CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin();
                            if (loginStatus) {
                                //已登录SID获取失败的情况,强制登录,并获取返回状态
                                baseActivity.setCurrLoginInvalid();
                                baseActivity.startLoginActivity(baseActivity.getApplicationInfo().packageName, true);
                                AppDebug.i(TAG, "startLoginActivity loginStatus = 200,forceLogin=true");
                            } else {
                                // 设置返回页面时检查是否已经登录，如果没有登录就直接finish
                                baseActivity.setCurrLoginInvalid();
                                baseActivity.setLoginActivityStartShowing();
                                baseActivity.startLoginActivity(baseActivity.getApplicationInfo().packageName, false);
                                AppDebug.i(TAG, "startLoginActivity loginStatus = " + loginStatus + ",forceLogin=true");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            };

        } else {
            mRequestErrorListener = new RequestErrorListener() {
                @Override
                public boolean onError(int errorCode, String errorMsg) {
                    BaseActivity baseActivity = mBaseActivityRef.get();
                    if (baseActivity != null && !baseActivity.isFinishing() && isShowErrorDialog()) {
                        baseActivity.showErrorDialog(errorMsg, ifFinishWhenCloseErrorDialog());
                    }
                    return true;
                }
            };
        }
    }

}
