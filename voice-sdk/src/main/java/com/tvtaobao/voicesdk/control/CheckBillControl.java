package com.tvtaobao.voicesdk.control;

import android.content.Context;

import com.google.gson.Gson;
import com.tvtaobao.voicesdk.NLPDeal;
import com.tvtaobao.voicesdk.bo.BillData;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.dialogs.BillQueryDialog;
import com.tvtaobao.voicesdk.request.CheckBillRequest;
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

public class CheckBillControl extends BizBaseControl {
    private BillQueryDialog billQueryDialog;
    Map<String, String> p = null;

    @Override
    public void execute(DomainResultVo domainResultVO) {
        onBillQuery(domainResultVO.getResultVO().getTimeText(), domainResultVO.getResultVO().getBeginTime(), domainResultVO.getResultVO().getEndTime());

        p = getProperties(configVO.asr_text);
        p.put("asr_time", domainResultVO.getResultVO().getTimeText());
    }

    /**
     * 账单查询
     *
     * @param timeText 时间：昨天
     * @param start    时间开始：如2018-04-16
     * @param end      时间结束：如2018-04-17
     */
    private void onBillQuery(String timeText, String start, String end) {

        if (ActivityUtil.isRunningForeground(mWeakService.get())) {
            BusinessRequest.getBusinessRequest().baseRequest(new CheckBillRequest(timeText, start, end), new BillRequestListenerV2(), false);
        } else {
            BusinessRequest.getBusinessRequest().baseRequest(new CheckBillRequest(timeText, start, end, "1.0"), new BillRequestListenerV1(), false);
        }
    }

    class BillRequestListenerV2 implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject object, int resultCode, String msg) {
            LogPrint.i(TAG, TAG + ".BillRequest resultCode : " + resultCode + " .msg : " + msg);
            if (resultCode != 200) {
                if (resultCode == 102 || resultCode == 104) {
                    startLoginActivity();
                } else {
                    notDeal();
                }
                return;
            }

            Gson gson = new Gson();
            BillData billData = gson.fromJson(object.toString(), BillData.class);

            if (billQueryDialog != null) {
                billQueryDialog.dismiss();
                billQueryDialog = null;
            }
            Context context = ActivityUtil.getTopActivity();
            if (context != null) {
                billQueryDialog = new BillQueryDialog(context);
                billQueryDialog.setData(billData);
                billQueryDialog.show();
            } else {
                onTTS(billData.getTts());
            }

            p.put("tts", billData.getTts());
            Utils.utCustomHit("VoiceCard_check_bill", p);
        }
    }

    class BillRequestListenerV1 implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject object, int resultCode, String msg) {
            LogPrint.i(TAG, TAG + ".BillRequest resultCode : " + resultCode + " .msg : " + msg);
            if (resultCode != 200) {
                if (resultCode == 102 || resultCode == 104) {
                    startLoginActivity();
                } else {
                    notDeal();
                }
                return;
            }

            Gson gson = new Gson();
            BillData billData = gson.fromJson(object.toString(), BillData.class);

            onTTS(billData.getTts());

            p.put("tts", billData.getTts());
            Utils.utCustomHit("Voice_check_bill_" + Config.getChannelName(), p);
        }
    }
}
