package com.tvtaobao.voicesdk.control;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.view.OrderDeliveryDialog;
import com.yunos.tv.core.util.Utils;

import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TakeOutProgressControl extends BizBaseControl {

    @Override
    public void execute(DomainResultVo domainResultVO) {
        if (domainResultVO.getResultVO() != null && domainResultVO.getResultVO().getTbMainOrderId() != null) {
            showOrderProgress(domainResultVO.getResultVO().getTbMainOrderId());
        } else if (!TextUtils.isEmpty(domainResultVO.getToUri())) {
//            if (CoreActivity.currentPage != null && CoreActivity.currentPage.equals("Page_waimai_Order")) {
//                showDialog();
//            } else {
            gotoActivity(domainResultVO.getToUri());
//            }
        } else {
//            Intent againIntent = new Intent(mWeakService.get(), TipsActivity.class);
//            againIntent.putExtra("tts", domainResultVO.getSpokenTxt());
//            againIntent.putStringArrayListExtra("tips", (ArrayList<String>) domainResultVO.getTips());
//            againIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mWeakService.get().startActivity(againIntent);
            Context context = ActivityUtil.getTopActivity();
            TTSUtils.getInstance().showDialog(context, 1);

        }

        Map<String, String> paramProgress = Utils.getProperties();
        paramProgress.put("asr", configVO.asr_text);
        paramProgress.put("tts", domainResultVO.getSpoken());
        Utils.utCustomHit("Voice_FoodCheck_order", paramProgress);
    }

    private void showOrderProgress(String tbMainOrderId) {
        Intent intent = new Intent();
        intent.setClass(mWeakService.get(), OrderDeliveryDialog.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tbMainOrderId", tbMainOrderId);
        mWeakService.get().startActivity(intent);

    }
}
