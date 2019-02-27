package com.yunos.tvtaobao.biz.request.info;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;

/**
 * Created by huangdaju on 17/8/14.
 */

public class GlobalConfigInfo {
    private static String TAG = "GlobalConfigInfo";
    private static GlobalConfigInfo instance = null;
    private BusinessRequest mBusinessRequest;
    private GlobalConfig mGlobalConfig;

    private volatile boolean requesting = false;

    private GlobalConfigInfo() {

    }

    public static GlobalConfigInfo getInstance() {
        if (instance == null) {
            instance = new GlobalConfigInfo();
        }
        return instance;
    }

    public GlobalConfig getGlobalConfig() {
        return mGlobalConfig;
    }

    public void requestGlobalConfigData() {
        if (mBusinessRequest == null) {
            mBusinessRequest = BusinessRequest.getBusinessRequest();
        }
        if (mGlobalConfig == null) {
            if (!requesting) {
                requesting = true;

                try {

                mBusinessRequest.requestGlobalConfig(new RequestListener<GlobalConfig>() {

                    @Override
                    public void onRequestDone(GlobalConfig data, int resultCode, String msg) {
                        requesting = false;
                        AppDebug.d(TAG, "GlobalConfig resultCode=" + resultCode + "data: " + data);
                        if (resultCode == 200 && data != null) {
                            mGlobalConfig = data;
                            String[] blackList = data.getZtcConfig().blackList;
                            String[] blockList = data.agreementModelBlockList;
                            String[] blockChannel = data.agreementChannelBlockList;

                            boolean isBlitzShop = mGlobalConfig.isBlitzShop();
                            boolean isMoHeLogOn = mGlobalConfig.isMoHeLogOn();
                            boolean isLianMengLogOn = mGlobalConfig.isLianMengLogOn();
                            boolean isYiTiJiLogOn = mGlobalConfig.isYiTiJiLogOn();
                            boolean isSimpleOn = mGlobalConfig.isSimpleOn();
                            SharePreferences.put("isBlitzShop", isBlitzShop);
                            SharePreferences.put("is_mohe_log_on", isMoHeLogOn);
                            SharePreferences.put("is_lianmeng_log_on", isLianMengLogOn);
                            SharePreferences.put("is_yitiji_log_on", isYiTiJiLogOn);
                            SharePreferences.put("isSimpleOn", isSimpleOn);
                        }
                    }

                });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
