package com.yunos.tvtaobao.request;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tvtaobao.bo.DeviceBo;
import com.yunos.tvtaobao.payment.request.ScanBindRequest;
import com.yunos.tvtaobao.request.TvTaobaoBusinessRequest;

import java.lang.ref.WeakReference;

/**
 * Created by huangdaju on 17/5/2.
 */

public class DeviceChannelBuilder {
    private static final String TAG = "DeviceChannelBuilder";
    private TvTaobaoBusinessRequest mBusinessRequest;
    private Context mContext;

    public DeviceChannelBuilder(Context context) {
        mContext = context;
        mBusinessRequest = TvTaobaoBusinessRequest.getBusinessRequest();
    }

    public void onRequestData() {
        String model = Build.MODEL;
        AppDebug.d(TAG, " app model " + model);
        if (!TextUtils.isEmpty(model)) {
            mBusinessRequest.requestDevice(model, new DeviceBoListener(new WeakReference<Context>(mContext)));
        }
    }


    /**
     * 处理设备型号监听
     */
    private static class DeviceBoListener implements RequestListener<DeviceBo> {

        private WeakReference<Context> ref;

        public DeviceBoListener(WeakReference<Context> service) {
            ref = service;
        }

        @Override
        public void onRequestDone(DeviceBo data, int resultCode, String msg) {
            if (ref == null && ref.get() == null) {
                return;
            }
            if (resultCode != 200) {
                AppDebug.d(TAG, " DeviceBoListener resultCode " + resultCode + " msg " + msg);
                return;
            }
            AppDebug.d(TAG, "DeviceBo " + data);
            if (data != null) {
                AppDebug.d(TAG, "DeviceBo " + data.toString());
                if (!TextUtils.isEmpty(data.getAppKey())) {
                    SharePreferences.put("device_appkey", data.getAppKey());
                    ScanBindRequest.setAppKey(data.getAppKey());

                }
                if (!TextUtils.isEmpty(data.getBrandName())) {
                    SharePreferences.put("device_brandname", data.getBrandName());
                }
            } else {
                AppDebug.d(TAG, " DeviceBoListener onError data " + data + "msg" + " 获取设备请求数据出错");
            }
        }

    }
}
