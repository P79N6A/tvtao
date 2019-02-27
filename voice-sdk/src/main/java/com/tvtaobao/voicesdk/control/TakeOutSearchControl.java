package com.tvtaobao.voicesdk.control;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.Location;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.request.TakeOutSearchRequest;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TakeOutSearchControl extends BizBaseControl {

    private Location location;
    private String lat = null;
    private String lon = null;

    @Override
    public void execute(DomainResultVo domainResultVO) {

        String keyword = "";
//        showDialog(domainResultVO);
        if (domainResultVO.getResultVO() != null) {
            if (domainResultVO.getResultVO().getKeywords() != null) {
                keyword = domainResultVO.getResultVO().getKeywords();
            } else if (domainResultVO.getResultVO().getCategoryName() != null){
                keyword = domainResultVO.getResultVO().getCategoryName();
            } else {
                keyword = domainResultVO.getResultVO().getSecondCategoryName();
            }
        }

        boolean pageReturn = ASRNotify.getInstance().isActionSearch(keyword, domainResultVO.getSpokenTxt(), domainResultVO.getSpoken(), domainResultVO.getTips(), domainResultVO.getOtherCaseSpokens());
        if (pageReturn) {
//                            alreadyDeal(true);
            return;
        }

        //海信渠道请求数据
        if (!SDKInitConfig.needTakeOutUI()) {
            takeoutSearch(keyword, 1, 20, null);
            return;
        }

        takeoutSearch(keyword);
    }

    public void takeoutSearch(String keyword, int pageNo, int pageSize, String orderType) {
        BusinessRequest.getBusinessRequest().baseRequest(new TakeOutSearchRequest(keyword, pageNo, pageSize, SDKInitConfig.getLocation(), orderType), new TakeOutSearchListener(), false);
    }

    private void takeoutSearch(String keyword) {
        JSONObject dataJson = new JSONObject();
        if (!TextUtils.isEmpty(SDKInitConfig.getLocation())) {
            location = JSON.parseObject(SDKInitConfig.getLocation(), Location.class);
            lat = location.x;
            lon = location.y;
        }
        //TODO 原本徐志成和袁启辉采用传asr的方案，前端在做一次语义理解，不知道为什么。现在改成直接传递搜索词keywords给前端。
//        if (lat == null && lon == null) {
//            String loc = SharePreferences.getString("location");
//            if (loc != null) {
//                location = JSON.parseObject(loc, Location.class);
//            }
//            if (location != null) {
//                lat = location.x;
//                lon = location.y;
//            }
//        }
//        try {
//            dataJson.put("uuid", CloudUUIDWrapper.getCloudUUID());
//            dataJson.put("osVersion", SystemConfig.getSystemVersion());
//            dataJson.put("model", Build.MODEL);
//            dataJson.put("androidVersion", Build.VERSION.RELEASE);
//            dataJson.put("appPackage", AppInfo.getPackageName());
//            dataJson.put("appKey", Config.getChannel());
//            dataJson.put("versionCode", AppInfo.getAppVersionNum());
//            dataJson.put("versionName", AppInfo.getPackageName());
//            dataJson.put("lat", lat);
//            dataJson.put("lon", lon);
//            dataJson.put("asr", asr);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        try {
            //有地址选择 voiceSearchList
//            String url = "tvtaobao://home?module=takeoutWeb&page=" + URLEncoder.encode("https://tvos.taobao.com/wow/yunos/act/yuyinwaimaixuanzhe?wh_data=" +
//                    URLEncoder.encode(dataJson.toString(), "UTF-8") + "&wh_url=voiceSearchList", "UTF-8");

            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                dataJson.put("lat", lat);
                dataJson.put("lon", lon);
            }
            //判断电视淘宝应用是否在前台运行
            boolean isRunningForeground = ActivityUtil.isRunningForeground(mWeakService.get());
            String v_from = "voice_application";
            if (!isRunningForeground) {
                v_from = "voice_system";
            }
            dataJson.put("keywords", keyword);
            dataJson.put("v_from", v_from);
            //无地址选择（仅用于语音演示）
            String url = "tvtaobao://home?module=takeoutWeb&page=" + URLEncoder.encode("https://tvos.taobao.com/wow/yunos/act/yuyinwaimaisousuo?wh_data=" +
                    URLEncoder.encode(dataJson.toString(), "UTF-8"), "UTF-8");
            LogPrint.e(TAG, "intentSearchUrl-------------" + url);
            gotoActivity(url);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        private class TakeOutSearchListener implements RequestListener<String> {

            @Override
            public void onRequestDone(String data, int resultCode, String msg) {
                if (resultCode == 200) {
                    CommandReturn commandReturn = new CommandReturn();
                    commandReturn.mIsHandled = true;
                    commandReturn.mAction = CommandReturn.TYPE_TAKE_OUT_SEARCH;
                    commandReturn.mASRMessage = configVO.asr_text;
                    commandReturn.mData = data;
                    mWeakListener.get().callback(commandReturn);
                } else {
                    CommandReturn errorReturn = new CommandReturn();
                    errorReturn.mIsHandled = true;
                    errorReturn.mAction = CommandReturn.TYPE_ERROR_INFO;
                    errorReturn.mASRMessage = configVO.asr_text;
                    errorReturn.mMessage = msg;
                    errorReturn.mCode = resultCode;
                    mWeakListener.get().callback(errorReturn);
                }
            }
        }
    }
