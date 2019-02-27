/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.request.item
 * FILE NAME: GetTvShopAdvertTime.java
 * CREATED TIME: 2015年1月8日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tvshoppingbundle.request.item;


import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年1月8日 下午7:19:56
 */
public class GetTvShopAdvertTime extends BaseMtopRequest {

    private static final long serialVersionUID = -345243629891805821L;
    private static String API = "mtop.alitv.ProgramAdvertApiService.queryAdvertItem";
    private String version = "1.0";
    private String programId;// 电影id
    private String sequenceId;// 电影集数
    private boolean isLive; // 是否是直播
    private boolean isNews; //是否是资讯和回放
    private String fromApp;// 消息来源的影视package Name
    private String systemInfo;

    public GetTvShopAdvertTime(String programId, String sequenceId, boolean isLive, boolean isNews, String fromApp) {
        this.programId = programId;
        this.sequenceId = sequenceId;
        this.isLive = isLive;
        this.isNews = isNews;
        this.fromApp = fromApp;

        JSONObject object = new JSONObject();
        try {
            object.put("uuid", CloudUUIDWrapper.getCloudUUID());
            object.put("appVersion", SystemConfig.APP_VERSION);
            object.put("channelId", Config.getChannel());
            object.put("systemVersion", Build.VERSION.RELEASE);
            object.put("modelName", Build.MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.systemInfo = object.toString();
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("programId", programId);
        addParams("sequence", sequenceId);
        addParams("isLive", String.valueOf(isLive));
        addParams("isNews", String.valueOf(isNews));
        addParams("packageName", fromApp);
        addParams("systemInfo", systemInfo);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<TbTvShoppingItemBo> resolveResponse(JSONObject obj) throws Exception {
        if (obj == null || TextUtils.isEmpty(obj.toString()) || TextUtils.isEmpty(obj.optString("result"))) {
            return null;
        }

        return JSON.parseArray(obj.optString("result"), TbTvShoppingItemBo.class);
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
