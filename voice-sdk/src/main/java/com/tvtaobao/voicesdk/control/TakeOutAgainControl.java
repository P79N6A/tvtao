package com.tvtaobao.voicesdk.control;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.bo.DetailListVO;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.view.PromptDialog;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBagAgain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
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

public class TakeOutAgainControl extends BizBaseControl {

    @Override
    public void execute(DomainResultVo domainResultVO) {
        if (domainResultVO.getResultVO() != null && domainResultVO.getResultVO().getDetailList() != null) {
            buyAgain(domainResultVO.getResultVO());
        } else if (!TextUtils.isEmpty(domainResultVO.getToUri())) {
//            if (CoreActivity.currentPage != null && CoreActivity.currentPage.equals("Page_waimai_Order")) {
//                showDialog(domainResultVO);
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


        Map<String, String> params = Utils.getProperties();
        params.put("asr", configVO.asr_text);
        params.put("query", "");
        params.put("tts", domainResultVO.getSpoken());
        Utils.utCustomHit("Voice_Foodrecur", params);
    }

    /**
     * 订单状态行为，再来一单
     *
     * @param resultVO
     */
    private void buyAgain(DomainResultVo.ResultVO resultVO) {
        AppDebug.e(TAG, "Buy again = " + resultVO.getTbMainOrderId());

        List<DetailListVO> productInfoBases = resultVO.getDetailList();
        JSONArray array = new JSONArray();
        try {
            for (DetailListVO detailListVO : productInfoBases) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("itemId", detailListVO.getId());
                jsonObject.put("skuId", detailListVO.getSkuId());
                jsonObject.put("quantity", detailListVO.getQuantity());
                jsonObject.put("itemTitle", detailListVO.getName());
                array.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String shopId = resultVO.getStoreId();
        String par = array.toString();
        if (!TextUtils.isEmpty(par) && !TextUtils.isEmpty(shopId)) {
            BusinessRequest.getBusinessRequest().requestTakeOutAgain(resultVO.getStoreId(), par,
                    new GetBagAgainListener(shopId));
        }
    }

    class GetBagAgainListener implements RequestListener<TakeOutBagAgain> {

        String mShopId;

        public GetBagAgainListener(String shopId) {
            this.mShopId = shopId;
        }

        @Override
        public void onRequestDone(TakeOutBagAgain data, int resultCode, String msg) {
            if (data != null && data.success) {
                String prefix = "tvtaobao://home?app=takeout&module=takeouthome&shopId=" + mShopId + "&from=voice_application";
                Intent intent = new Intent();
//                intent.putStringArrayListExtra("tips", (ArrayList<String>) domainResultVo.getTips());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(prefix));
                mWeakService.get().startActivity(intent);
            } else {
                if (data != null && !TextUtils.isEmpty(data.errorDesc)) {
                    Intent intent = new Intent();
                    intent.setClass(mWeakService.get(), PromptDialog.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("errorDesc", data.errorDesc);
                    mWeakService.get().startActivity(intent);
//                    PromptPop.getInstance(getBaseContext()).showPromptWindow(data.errorDesc);
                }
            }
        }
    }
}
