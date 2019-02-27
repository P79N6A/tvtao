
package com.yunos.tvtaobao.biz.blitz;


import android.content.Context;
import android.os.Build;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.blitz.listener.BzJsCallBaseListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;

public class TaobaoBaseBzJsCallBaseListener implements BzJsCallBaseListener {

    private final String TAG = "TaobaoBaseBzJsCallBaseListener";

    @Override
    public String onBaseGetDeviceInfo(Context context, String param) {
        AppDebug.i(TAG, "onBaseGetDeviceInfo , param  = " + param);
        BzResult result = new BzResult();
        result.addData("uuid", CloudUUIDWrapper.getCloudUUID());
        result.addData("model", Build.MODEL);
        result.addData("totalMemory", DeviceJudge.getTotalMemorySizeInMB());
        result.setSuccess();
        return result.toJsonString();
    }

}