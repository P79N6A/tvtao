/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.listener
 * FILE NAME: BizRetryRequestListener.java
 * CREATED TIME: 2015-2-12
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest.RequestLoadListener;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.core.DataRequest;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.core.ServiceResponse;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;

import java.lang.ref.WeakReference;

//import com.yunos.tvtaobao.blitz.account.LoginHelper;

/**
 * 与BizRequestListener的区别是，当网络连接再次连上时，能自动执行之前的数据请求
 *
 * @author hanqi
 * @data 2015-2-12 下午3:52:08
 */
public abstract class JuRetryRequestListener<T> extends BizRequestListener<T> {

    private final String TAG = "BizRetryRequestListener";

    protected WeakReference<DataRequest> mRequest;
    private boolean finishActivity = false;

    public JuRetryRequestListener(BaseActivity activity) {
        super(new WeakReference<BaseActivity>(activity));
    }

    public JuRetryRequestListener(BaseActivity activity, boolean finish) {
        super(new WeakReference<BaseActivity>(activity));
        finishActivity = finish;
    }

    public BaseActivity getActivity() {
        return mBaseActivityRef.get();
    }

    public void setDataRequest(DataRequest request) {
        mRequest = new WeakReference<DataRequest>(request);
    }

    public void onRequestDone(T data, int resultCode, String msg) {
        if (resultCode == 200) {
            onSuccess(data);
        } else {
            boolean result = onError(resultCode, msg); //用户定义的错误处理
            if (!result) {
                onDefaultError(resultCode, msg); //默认错误处理
            }
        }
    }

    @Override
    public void onSuccess(T data) {
        BaseActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        data = initData(data);
        onSuccess(activity, data);
    }

    /**
     * 数据在使用前初始化，如果需要请重写
     */
    public T initData(T data) {
        return data;
    }

    public abstract void onSuccess(BaseActivity baseActivity, T data);

    @Override
    public boolean onError(int resultCode, String msg) {
        BaseActivity activity = getActivity();
        if (null == activity) {
            return true;
        }
        if (!NetWorkUtil.isNetWorkAvailable()) {
            onNetworkError(activity);
            return true;
        }
        return onUserError(activity, resultCode, msg);
    }

    /**
     * 用户定义的错误处理
     *
     * @param baseActivity
     * @param resultCode
     * @param msg
     * @return
     */
    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
        return false;
    }

    @Override
    public boolean ifFinishWhenCloseErrorDialog() {
        return finishActivity;
    }

    /**
     * 网络连上之后回调
     */
    public void retry() {
        if (mRequest == null) {
            return;
        }
        final DataRequest request = mRequest.get();
        AppDebug.v(TAG, TAG + ".retry.request = " + request);

        if (null == request) {
            return;
        }
        if (request instanceof BaseMtopRequest) {
            MyBusinessRequest.getInstance().baseRequest((BaseMtopRequest) request, this, false, false);
        } else {
            MyBusinessRequest.getInstance().baseRequest(new RequestLoadListener<T>() {

                @Override
                public ServiceResponse<T> load() {
                    return MyBusinessRequest.getInstance().processHttpRequest((BaseHttpRequest) request, false);
                }
            }, this, false);
        }
    }

    /**
     * 返回结果的默认错误处理
     *
     * @param resultCode
     * @param msg
     */
    protected void onDefaultError(int resultCode, String msg) {
        BaseActivity baseActivity = mBaseActivityRef.get();
        if (null == baseActivity || baseActivity.isFinishing()) {
            return;
        }

        if (resultCode == ServiceCode.NET_WORK_ERROR.getCode()) {//网络未连接
            onNetworkError(baseActivity);
        } else if (resultCode == ServiceCode.API_NOT_LOGIN.getCode()
                || resultCode == ServiceCode.API_SID_INVALID.getCode()) {
            onNotLoginError(baseActivity);
        } else {
            onNormalError(baseActivity, resultCode, msg);
        }
    }

    /**
     * 网络未连接错误处理
     *
     * @param activity
     */
    protected void onNetworkError(BaseActivity activity) {
        activity.showNetworkErrorDialog(ifFinishWhenCloseErrorDialog());
        activity.setNetworkOkDoListener(new NetworkOkDoListener() {

            @Override
            public void todo() {
                retry();
            }
        });
    }

    /**
     * 用户未登陆或者session过期错误处理
     *
     * @param activity
     */
    protected void onNotLoginError(BaseActivity activity) {
        //用户未登录或session过期
        try {
            boolean loginStatus = CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin();
//            TYIDManager mTYIDManager = TYIDManager.get(CoreApplication.getApplication());
//            int loginStatus = mTYIDManager.yunosGetLoginState();
            if (loginStatus) {
                //已登录SID获取失败的情况,强制登录,并获取返回状态
                activity.setCurrLoginInvalid();
                AppDebug.i("lihaile------------", "juretryrequestlistener---------你这个傻逼也要启动这个页面");
                activity.startLoginActivity(activity.getApplicationInfo().packageName, true);
                AppDebug.i(TAG, TAG + ".onNotLoginError loginStatus = 200,forceLogin=true");
            } else {
                // 设置返回页面时检查是否已经登录，如果没有登录就直接finish
                activity.setCurrLoginInvalid();
                activity.setLoginActivityStartShowing();
                AppDebug.i("lihaile------------", "juretryrequestlistener2---------你这个傻逼也要启动这个页面");
                activity.startLoginActivity(activity.getApplicationInfo().packageName, false);
                AppDebug.i(TAG, TAG + ".onNotLoginError loginStatus = " + loginStatus + ",forceLogin=true");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 普通错误处理
     *
     * @param activity
     * @param resultCode
     * @param msg
     */
    protected void onNormalError(BaseActivity activity, int resultCode, String msg) {
        if (null == msg) {
            for (ServiceCode code : ServiceCode.values()) {
                if (code.getCode() == resultCode) {
                    msg = code.getMsg();
                    break;
                }
            }
            if (null == msg) {
                return;
            }
        }
        msg = initErrorMsg(resultCode, msg);
        if (null == msg) {
            return;
        }
        activity.showErrorDialog(msg, ifFinishWhenCloseErrorDialog());
    }

    /**
     * 错误消息内容处理
     *
     * @param resultCode
     * @param msg
     * @return
     */
    protected String initErrorMsg(int resultCode, String msg) {
        if (null == msg) {
            for (ServiceCode code : ServiceCode.values()) {
                if (code.getCode() == resultCode) {
                    msg = code.getMsg();
                    break;
                }
            }
            if (null == msg) {
                msg = whenMsgIsNull();
            }
        }
        return msg;
    }

    /**
     * 当错误中没有消息时内容时
     *
     * @return
     */
    protected String whenMsgIsNull() {
        BaseActivity activity = getActivity();
        if (null == activity) {
            return null;
        }
        return activity.getResources().getString(R.string.jhs_data_error);
    }
}
