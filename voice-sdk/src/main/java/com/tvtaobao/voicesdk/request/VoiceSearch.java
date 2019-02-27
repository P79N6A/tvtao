package com.tvtaobao.voicesdk.request;

import android.text.TextUtils;

import com.tvtaobao.voicesdk.bo.SearchObject;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 2017/7/24.
 */

public class VoiceSearch extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.speech.item.search";
    private final String VERSION = "1.0";

    public VoiceSearch(SearchObject searchConfig,String tvOptions) {
        addParams("q", searchConfig.keyword);
        addParams("s", searchConfig.startIndex + "");
        addParams("n", searchConfig.endIndex + "");
        addParams("v", "2.0");
        String exquery = "{\"needJinNangs\":" + searchConfig.needJinnang + ", \"needFeeds\":" + searchConfig.needFeeds +", \"tvOptions\":" + tvOptions+ "}";
        AppDebug.e("TVTao_VoiceSearch", "VoiceSearch q : " + searchConfig.keyword + ", s : " + searchConfig.startIndex + " ,n : " + searchConfig.endIndex + ", exquery : " + exquery);
        addParams("exquery", exquery);
        addParams("appkey", Config.getChannel());

        if (!TextUtils.isEmpty(searchConfig.priceScope))
            addParams("price", searchConfig.priceScope);

        /**
         * 排序方式
         * 按照销量排序
         *      sales:des 30天确认收获人数
         *      sales:des   30天付款人数
         * 按照价格排序
         *      price:des
         *      price:asc
         */
        if (!TextUtils.isEmpty(searchConfig.sorting)) {
            addParams("sort", searchConfig.sorting);
        }
        addParams("appkey", Config.getChannel());
    }

    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {

        return obj;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
