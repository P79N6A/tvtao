package com.tvtaobao.voicesdk.control;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.dialogs.SearchDialog;
import com.tvtaobao.voicesdk.request.VoiceSearch;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/18
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class GoodsSearchControl extends BizBaseControl {
    private final String TAG = "GoodsSearchControl";

    private SearchDialog searchDialog;

    private SearchObject searchConfig;

    @Override
    public void execute(DomainResultVo domainResultVO) {
        LogPrint.e(TAG, "execute");
        String key_search = domainResultVO.getResultVO().getKeywords();
        searchConfig = configVO.searchConfig;
        if (searchConfig == null) {
            searchConfig = new SearchObject();
        }
        searchConfig.keyword = key_search;
        searchConfig.clearSift();
        requestSearch();
    }

    public void setSearchConfig(SearchObject config) {
        this.searchConfig = config;
    }

    public void requestSearch() {
        LogPrint.e(TAG, "gotoSearchRequest words : " + searchConfig.keyword);
        if (searchConfig == null && TextUtils.isEmpty(searchConfig.keyword)) {
            onTTS("不好意思，你想买什么呢");
            return;
        }

        if (searchConfig.showUI) {
            String className = null;
            //应用内判断栈顶的Activity
            if (ActivityUtil.isRunningForeground(mWeakService.get())) {
                if (ActivityUtil.getTopActivity() != null) {
                    className = ActivityUtil.getTopActivity().getClass().getName();
                }
            }

            //不在电视淘宝应用内；
            //或者在电视淘宝应用内，但是在语音登录页面和语音搜索页面；
            //或者应用内浮层搜索设置为不展示。
            //都直接弹出搜索页面。
            if (!ActivityUtil.isRunningForeground(mWeakService.get())
                    || "com.yunos.voice.activity.VoiceSearchActivity".equals(className)
                    || "com.yunos.voice.activity.LoginAuthActivity".equals(className)
                    || !SDKInitConfig.needTVTaobaoSearch()) {
                LogPrint.d(TAG, "requestSearch VoiceSearchActivity isTopActivity or TVTaobao is background app ");
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setData(Uri.parse("tvtaobao://home?app=voice&module=search&keyword=" + searchConfig.keyword + "&notshowloading=true"));
                intent.putExtra("searchObject", searchConfig);
                mWeakService.get().startActivity(intent);
                return;
            }
        }
        TvOptionsConfig.setTvOptionsVoice(true);
        TvOptionsConfig.setTvOptionsSystem(false);
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.VOICE_SEARCH_ORDER);
        BusinessRequest.getBusinessRequest().baseRequest(
                new VoiceSearch(searchConfig, TvOptionsConfig.getTvOptions()), new SearchListener(), false);
    }

    /**
     * 搜索请求监听
     */
    class SearchListener implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".SearchResponse resultCode : " + resultCode + " ,msg : " + msg);
            if (resultCode != 200) {
                LogPrint.e(TAG, TAG + ".SearchResponse FAILURE    msg : " + msg);
                CommandReturn errorReturn = new CommandReturn();
                errorReturn.mIsHandled = true;
                errorReturn.mAction = CommandReturn.TYPE_ERROR_INFO;
                errorReturn.mASRMessage = configVO.asr_text;
                errorReturn.mMessage = msg;
                errorReturn.mCode = resultCode;
                mWeakListener.get().callback(errorReturn);
                return;
            }

            if (!ActivityUtil.isRunningForeground(mWeakService.get())
                    || !SDKInitConfig.needTVTaobaoSearch()) {
                LogPrint.e(TAG, TAG + ".SearchResponse showUI :" + searchConfig.showUI);
                if (!searchConfig.showUI) {
                    LogPrint.e(TAG, TAG + ".SearchResponse searchResult showUI is false");
                    if (mWeakListener != null && mWeakListener.get() != null) {
                        mWeakListener.get().searchResult(data.toString());
                    } else {
                        notDeal();
                    }
                    return;
                }
            }

            try {
                String keyword = data.getString("keyword");
                String spoken = data.getString("spoken");
                List<ProductDo> mProducts = new ArrayList<>();
                if (data.has("model")) {
                    JSONArray model = data.getJSONArray("model");
                    LogPrint.e(TAG, TAG + ".SearchResponse size : " + model.length());
                    for (int i = 0; i < model.length(); i++) {
                        mProducts.add(GsonUtil.parseJson(model.getJSONObject(i).toString(),
                                new TypeToken<ProductDo>() {
                                }));
                    }
                }

                List<JinnangDo> mJinnangs = new ArrayList<>();
                if (data.has("jinNangItems")) {
                    JSONArray jinnang = data.getJSONArray("jinNangItems");
                    for (int i = 0; i < jinnang.length(); i++) {
                        mJinnangs.add(JinnangDo.resolverData(jinnang.getJSONObject(i)));
                    }
                }

                if (searchDialog != null) {
                    searchDialog.dismiss();
                    searchDialog = null;
                }
                Context context = ActivityUtil.getTopActivity();
                if (context != null) {
                    searchDialog = new SearchDialog(context);
                    searchDialog.setData(spoken, keyword, mProducts, mJinnangs);
                    searchDialog.show();
                } else {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("tvtaobao://home?app=voice&module=search&keyword=" + searchConfig.keyword + "&notshowloading=true"));
                    mWeakService.get().startActivity(intent);
                }


                Map<String, String> map = getProperties(configVO.asr_text);
                map.put("query", keyword);
                Utils.utCustomHit("VoiceCard_search", map);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
