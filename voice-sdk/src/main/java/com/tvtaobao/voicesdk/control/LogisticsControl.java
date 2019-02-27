package com.tvtaobao.voicesdk.control;

import android.content.Context;

import com.google.gson.Gson;
import com.tvtaobao.voicesdk.NLPDeal;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.LogisticsData;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.dialogs.LogisticsQueryDialog;
import com.tvtaobao.voicesdk.request.CheckOrderRequest;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class LogisticsControl extends BizBaseControl {
    private LogisticsQueryDialog logisticsQueryDialog;
    Map<String, String> p = null;

    @Override
    public void execute(DomainResultVo domainResultVO) {
        onLogisticsQuery(domainResultVO.getResultVO().getTimeText(), domainResultVO.getResultVO().getKeywords(), domainResultVO.getResultVO().getBeginTime(), domainResultVO.getResultVO().getEndTime());

        p = getProperties(configVO.asr_text);
        p.put("asr_time", domainResultVO.getResultVO().getTimeText());
    }

    /**
     * 物流查询
     *
     * @param timeText
     * @param key_logistics 商品内容：西瓜
     * @param beginTime
     * @param endTime
     */
    private void onLogisticsQuery(String timeText, String key_logistics, String beginTime, String endTime) {
        if (ActivityUtil.isRunningForeground(mWeakService.get())) {
            BusinessRequest.getBusinessRequest().baseRequest(new CheckOrderRequest(timeText, key_logistics, beginTime, endTime), new LogisticsRequestListenerV2(), false);
        } else {
            BusinessRequest.getBusinessRequest().baseRequest(new CheckOrderRequest(timeText, key_logistics, beginTime, endTime, "1.0"), new LogisticsRequestListenerV1(), false);
        }
    }

    class LogisticsRequestListenerV2 implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.i(TAG, TAG + ".LogisticsRequest resultCode : " + resultCode + " .msg : " + msg);
            if (resultCode != 200) {
                if (resultCode == 102 || resultCode == 104) {
                    startLoginActivity();
                } else {
                    notDeal();
                }
                return;
            }

            Gson gson = new Gson();
            LogisticsData logisticsData = gson.fromJson(data.toString(), LogisticsData.class);

            if (logisticsQueryDialog != null) {
                logisticsQueryDialog.dismiss();
                logisticsQueryDialog = null;
            }
            Context context = ActivityUtil.getTopActivity();
            if (context != null) {
                logisticsQueryDialog = new LogisticsQueryDialog(context);
                logisticsQueryDialog.setData(logisticsData);
                logisticsQueryDialog.show();
            } else {
                onTTS(logisticsData.getTts());
            }

            p.put("tts", logisticsData.getTts());
            Utils.utCustomHit("VoiceCard_check_order_state", p);

        }
    }

    class LogisticsRequestListenerV1 implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.i(TAG, TAG + ".LogisticsRequest resultCode : " + resultCode + " .msg : " + msg);
            if (resultCode != 200) {
                if (resultCode == 102 || resultCode == 104) {
                    startLoginActivity();
                } else {
                    notDeal();
                }
                return;
            }

            Gson gson = new Gson();
            LogisticsData logisticsData = gson.fromJson(data.toString(), LogisticsData.class);

            onTTS(logisticsData.getTts());

            p.put("tts", logisticsData.getTts());
            Utils.utCustomHit("Voice_check_order_state_" + Config.getChannelName(), p);
        }
    }
}
