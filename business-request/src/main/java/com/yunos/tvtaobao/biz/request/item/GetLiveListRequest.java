package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 16/9/18.
 */

public class GetLiveListRequest  extends BaseMtopRequest {
    //延迟直播列表接口
    private String API = "mtop.taobao.tvtao.liveservice.getlivelist";
    //private String API = "mtop.mediaplatform.live.videolist";
    private String version = "1.0";
    private List<TBaoLiveListBean> list;

    public GetLiveListRequest() {
        addParams("appkey", Config.getAppKey());
        list = new ArrayList<TBaoLiveListBean>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<TBaoLiveListBean> resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            JSONArray dataList = obj.getJSONArray("result");
            int lenght = dataList.length();
            for (int i = 0; i < lenght ; i++) {
                    TBaoLiveListBean tblistBean = JSON.parseObject(dataList.getJSONObject(i).toString(),TBaoLiveListBean.class);
                    if (tblistBean != null){
                        list.add(tblistBean);
                    }
            }
            return list;

        }
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return version;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}
