package com.yunos.voice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.tvtaobao.voicesdk.register.LPR;
import com.tvtaobao.voicesdk.register.bo.Register;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.register.type.RegisterType;
import com.tvtaobao.voicesdk.request.VoiceSearch;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.tvtaobao.voicesdk.utils.JSONUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.voice.R;
import com.yunos.voice.view.VoiceSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pan on 2017/7/24.
 */

public class VoiceSearchActivity extends BaseActivity {
    private final String TAG = "VoiceSearchActivity";
    private VoiceSearchView mView;

    private List<JinnangDo> mJingNang;

    private boolean isNewSearch = true;  //是否是新搜索。比如 换页 就不是新搜索

    private SearchObject searchObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TvOptionsConfig.setTvOptionsVoice(true);
        TvOptionsConfig.setTvOptionsSystem(true);
        //如果是语音快捷下单，之后在快捷下单接口会将channel置为快捷下单
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.VOICE_SEARCH_ORDER);
        LogPrint.e(TAG, TAG + ".onCreate");
        onKeepActivityOnlyOne(VoiceSearchActivity.class.getName());
        OnWaitProgressDialog(true);

        setContentView(R.layout.activity_voicesearch);
        mView = new VoiceSearchView(this);
        searchObject = (SearchObject) getIntent().getSerializableExtra("SearchObject");

        String keyword = getIntent().getExtras().getString("keyword");
        requestSearch(keyword);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String keyword = intent.getExtras().getString("keyword");
        requestSearch(keyword);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        //TODO 语点注册需要修改
        PageReturn pageReturn = new PageReturn();
        switch (object.getIntent()) {
            case ActionType.JINNANG:
                String keyword = object.getResultVO().getKeywords();
                requestSearch(keyword);

                Map<String, String> map = getProperties();
                map.put("screencase", keyword);
                Utils.utCustomHit(getPageName(), "Voice_search_screen_" + Config.getChannelName(), map);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在帮您搜索";
                break;
            case ActionType.NEXT_PAGE:
                mView.onNextPage(searchObject);

                pageReturn.isHandler = true;
                pageReturn.feedback = "好的";
                break;
            case ActionType.PREVIOUS_PAGE:
                mView.onPreviousPage();

                pageReturn.isHandler = true;
                pageReturn.feedback = "好的";
                break;
            case ActionType.OPEN_INDEX:
                mView.notifyClickType(1, Integer.parseInt(object.getResultVO().getNorm()));

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转详情";
                break;
            case ActionType.BUY_INDEX:
                mView.notifyClickType(0, Integer.parseInt(object.getResultVO().getNorm()));

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转购买";
                break;
            case ActionType.GOODS_SEARCH_SIFT:
                //TODO 语点注册需要修改
                requestSortSearch(null, null);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转购买";
                break;
        }

        return pageReturn;
    }

    /**
     * 去搜索
     *
     * @param keywords
     */
    public void requestSearch(String keywords) {
        if (searchObject == null) {
            searchObject = new SearchObject();
        }
        searchObject.clearSift();
        searchObject.keyword = keywords;

        requestSearch(searchObject);
    }

    /**
     * 进行排序搜索
     * @param priceScope
     * @param sorting
     */
    public void requestSortSearch(String priceScope, String sorting) {
        searchObject.priceScope = priceScope;
        searchObject.sorting = sorting;

        requestSearch(searchObject);
    }

    public void requestSearch(SearchObject searchObject) {
        this.searchObject = searchObject;
        BusinessRequest.getBusinessRequest().baseRequest(
                new VoiceSearch(searchObject, TvOptionsConfig.getTvOptions()), new SearchRequestListener(), false);

        Map<String, String> p = getProperties();
        p.put("query", searchObject.keyword);
        Utils.utControlHit(getPageName(), "Voice_search_" + Config.getChannelName(), p);
    }

    private void onSearchResult(String keyword, String spoken, List<String> tips, List<ProductDo> products, List<JinnangDo> jinnangs) {
        OnWaitProgressDialog(false);
        if (products.size() == 0) {
            if (TextUtils.isEmpty(spoken)) {
                mView.setPrompt("抱歉，没有搜索到与" + keyword + "相关的商品。", null);
            } else {
                mView.setPrompt(spoken, tips);
            }

            if (mView.getItemCount() <= 0) {
                mView.showNotResultPrompt();
            }
            return;
        }

        mView.notifyData(products, jinnangs, isNewSearch);
        AppDebug.e(TAG, "onSearchResult keyword : " + keyword + " ,product.size : " + products.size() + " ,jinanng.size : " + jinnangs.size());
        mView.searchPrompt(keyword, spoken, tips);

        isNewSearch = true;
    }

    class SearchRequestListener implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            if (resultCode == 200) {
                try {
                    String keyword = data.getString("keyword");
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

                    mJingNang = new ArrayList<>();
                    if (data.has("jinNangItems")) {
                        JSONArray jinnang = data.getJSONArray("jinNangItems");
                        Register register = new Register();
                        ConcurrentHashMap<String, String> map = register.getRegistedMap();
                        for (int i = 0; i < jinnang.length(); i++) {
                            JinnangDo jinnangDo = JinnangDo.resolverData(jinnang.getJSONObject(i));
                            mJingNang.add(jinnangDo);
                            String name = jinnangDo.getName();
                            String content = jinnangDo.getContent();
                            map.put(name, content);
                        }

                        register.setRegistedMap(map);
                        register.resgistedType = RegisterType.UPDATE;
                        register.className = VoiceSearchActivity.class.getCanonicalName();
                        register.bizType = "jinNang";
                        LPR.getInstance().registed(register);
                    }

                    String spoken = JSONUtil.getString(data, "spoken");
                    JSONArray tipsArray = JSONUtil.getArray(data, "tips");
                    List<String> tips = new ArrayList<>();
                    if (tipsArray != null) {
                        for (int i = 0; i < tipsArray.length(); i++) {
                            tips.add(tipsArray.getString(i));
                        }
                    }

                    onSearchResult(keyword, spoken, tips, mProducts, mJingNang);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取统计最简的Properties
     *
     * @return
     */
    public Map<String, String> getProperties() {
        return getProperties(null);
    }

    private Map<String, String> getProperties(String asr) {
        Map<String, String> p = new HashMap<>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }

        p.put("channel", Config.getChannelName());

        if (!TextUtils.isEmpty(asr)) {
            p.put("asr", asr);
        }
        return p;
    }

    @Override
    public String getPageName() {
        return "Voice_search_results_" + Config.getChannelName();
    }
}
