package com.yunos.tvtaobao.flashsale.utils.req;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.flashsale.listener.ContextListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;

public class ReqStateInfo {
    private final String TAG = "ReqStateInfo";
    public final static long MODE_NOW = -2;
    public final static long MODE_NO_UPDATE = 0;

    public final static byte STATE_INIT = 0;
    public final static byte STATE_LOADING = 1;
    public final static byte STATE_SUCCESS = 2;    //请求结束并返回成功码
    public final static byte STATE_FINISH = 3;    //请求结束
    public byte mState = STATE_INIT;
    public long mCompleteTime;
    public Object mUserData;
    public Object mReqData;
    public long mTimeout = AppConfig.UPDATE_DATA_TIMEOUT;
    private ReqProcListener mReqProcListener;
    //		private boolean mIsHasError = false;
    private int mResultCode = RequestManager.CODE_SUCCESS;

    private ContextListener mContextListener;

    public ContextListener getContextListener() {
        return mContextListener;
    }
//		public ReqStateInfo(ReqProcListener proc, Object userData, long timeout){
//			mReqProcListener = proc;
//			mTimeout = timeout;
//			mUserData = userData;
//		}

    public ReqStateInfo(ReqProcListener proc) {
        mReqProcListener = proc;
        mTimeout = MODE_NOW;

    }

    public void setContextListener(ContextListener contextListener) {
        mContextListener = contextListener;
    }

    public void setStatus(byte status) {
        mState = status;
    }

    public void loadingDataSuccess(Object data) {
        AppDebug.d(TAG, "loadingDataSuccess, data is null = " + (data == null));
        mState = STATE_SUCCESS;
        mCompleteTime = System.currentTimeMillis();
        if (mReqProcListener.avaibleUpdate()) {
            if (null != mContextListener) {
                mContextListener.OnWaitProgressDialog(false);
            }
            mReqProcListener.loadingDataSuccess(mUserData, data);
        } else {
            mReqData = data;
        }
    }

    public void loadingDataError(int errorCode) {
        AppDebug.d(TAG, "loadingDataError");
        mState = STATE_INIT;
        if (mReqProcListener.avaibleUpdate()) {
            mResultCode = RequestManager.CODE_SUCCESS;
            if (null != mContextListener) {
                mContextListener.OnWaitProgressDialog(false);
            }

            if (!mReqProcListener.loadingDataError(mUserData)) {
                for (ServiceCode serviceCode : ServiceCode.values()) {
                    if (errorCode == serviceCode.getCode()) {
                        String errorMsg = serviceCode.getMsg();
                        if (null != mContextListener && null != errorMsg) {
                            mContextListener.showErrorDialog(errorMsg, false);
                        }
                    }
                }
            }

        } else {
            mResultCode = errorCode;
        }
    }

    public void checkReq() {
        boolean req = false;
        boolean dispProgress = true;

        if (STATE_SUCCESS == mState) {
            Object reqData = mReqData;
            if (null != reqData) {
                mReqData = null;

                /**刷新数据*/
                if (null != mContextListener) {
                    mContextListener.OnWaitProgressDialog(false);
                }
                mReqProcListener.loadingDataSuccess(mUserData, reqData);
                return;
            }
            if (MODE_NO_UPDATE == mTimeout) {
                return;
            }
            if (MODE_NOW != mTimeout) {
                if (mTimeout < 0) {
                    return;
                }
            }
            long curTime = System.currentTimeMillis();

            if ((curTime > (mCompleteTime + mTimeout))
                    || (curTime < mCompleteTime)) {
                req = true;
                /** 不需要调用进度条信息,后台更新 */
                dispProgress = false;
            }

        } else if (STATE_LOADING == mState) {
            /** 进度条信息 , 不做任何处理 */
            if (null != mContextListener) {
                mContextListener.OnWaitProgressDialog(true);
            }
        } else if (STATE_INIT == mState) {
            /** 请求，并显示进度条信息 */
            req = true;
        }
        if (mResultCode != RequestManager.CODE_SUCCESS) {
            loadingDataError(mResultCode);
        }
        if (req) {
            if (NetWorkUtil.isNetWorkAvailable()) {
                if (null != mContextListener) {
                    mContextListener.OnWaitProgressDialog(true);
                }
                mState = STATE_LOADING;
                if (dispProgress) {
                    mReqProcListener.loadingData(mUserData);
                }
                mReqProcListener.excuteReq(mUserData);
            } else {
                if (null != mContextListener) {
                    //网络不可以用，取消加载框（数据库加载的时候用到了加载框）
                    mContextListener.OnWaitProgressDialog(false);
                    mContextListener.showNetworkErrorDialog(false);
                }
            }
        }
    }

}
