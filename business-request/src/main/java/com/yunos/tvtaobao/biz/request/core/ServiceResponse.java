package com.yunos.tvtaobao.biz.request.core;


import java.util.ArrayList;
import java.util.List;

/**
 * 数据服务层响应对象
 * @author tianxiang
 * @date 2012-10-9 18:47:50
 * @param <T>
 */
public class ServiceResponse<T> {

    // 服务状态码
    private Integer statusCode;

    // 错误描述
    private String errorMsg;

    // 错误码
    private String errorCode;

    // 数据对象
    private T data;

    private List<RequestErrorListener> mRequestErrorListenerList = new ArrayList<RequestErrorListener>();

    public ServiceResponse() {
        this.statusCode = ServiceCode.SERVICE_OK.getCode();
    }

    @Override
    public String toString() {
        return "ServiceResponse [statusCode=" + statusCode + ", errorMsg=" + errorMsg + ", errorCode=" + errorCode
                + ", data=" + data + "]";
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer code) {
        this.statusCode = code;
    }

    public void update(int statusCode, String errorCode, String errorMsg, T data) {
        update(statusCode, errorCode, errorMsg);
        this.data = data;
    }

    public void update(int statusCode, String errorCode, String errorMsg) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = null;
    }

    public void update(ServiceCode serviceCode) {
        if (serviceCode == null) {
            return;
        }
        this.statusCode = serviceCode.getCode();
        this.errorCode = String.valueOf(serviceCode.getCode());
        this.errorMsg = serviceCode.getMsg();
    }

    public void update(ServiceCode serviceCode, T data) {
        update(serviceCode);
        this.data = data;
    }

    /**
     * 是否调用成功
     * @date 2012-11-5下午1:46:18
     * @return
     */
    public boolean isSucess() {
        return statusCode.intValue() == ServiceCode.SERVICE_OK.getCode();
    }

    /**
     * 是否网络错误
     * @date 2012-11-5下午2:10:27
     * @return
     */
    public boolean isNetError() {
        return statusCode.intValue() == ServiceCode.HTTP_ERROR.getCode();
    }

    /**
     * 是否没有登录
     * @date 2012-11-5下午1:46:27
     * @return
     */
    public boolean isNotLogin() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.API_NOT_LOGIN.getCode());
    }

    public boolean isNoAddress() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.NO_ADDRESS.getCode());
    }

    /**
     * 是否session超时
     * @date 2012-11-5下午1:47:56
     * @return or ["ERR_SID_INVALID::SESSION过期"]
     */
    public boolean isSessionTimeout() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.API_SID_INVALID.getCode());
    }

    public boolean isApiError() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.API_ERROR.getCode());
    }

    public boolean isAuthReject() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.API_ERRCODE_AUTH_REJECT.getCode());
    }

    public boolean isDeviceIdError() {
        return statusCode != null && (statusCode.intValue() == ServiceCode.PUSH_MESSAGE_ERROR_DEVICE_ID.getCode());
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String msg) {
        this.errorMsg = msg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void addErrorListener(RequestErrorListener listener) {
        mRequestErrorListenerList.add(listener);
    }

    public void actionErrorListener() {
        if (!isSucess()) {
            for (RequestErrorListener listener : mRequestErrorListenerList) {
                boolean ret = listener.onError(statusCode, errorMsg);
                if (ret) {
                    break;
                }
            }
        }
    }

    public interface RequestErrorListener {

        /**
         * 处理错误的监听方法（返回值为true）不进行后续的操作
         * @param errorCode
         * @param errorMsg
         * @return
         */
        public boolean onError(int errorCode, String errorMsg);
    }
}
