/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.item
 * FILE NAME: SearchRequestMtop.java
 * CREATED TIME: 2016年1月27日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SearchResultRequest extends BaseMtopRequest {

    private String API = "mtop.taobao.tvtao.tvtaosearchservice.getbyq2";
    private String version = "1.0";

    private int s = 1; // 页码 从0开始
    private int n = 40; // 每页商品数
    private String q = ""; // 关键字
    private String item_ids = null; // 查询指定的商品
    private String flag = null;//l:左侧直通车, r:右侧直通车, z 全直通车, null:全普通
    private int ztcc = 0;//直通车商品数
    private String sort = null;
    private String cateId = null;//类目id
    private int per = 4;
    private boolean isTmail;//是否是搜天猫

    public SearchResultRequest(List<String> list, boolean isFromCartToBuildOrder, boolean isFromBuildOrder,boolean isTmail) {
        this.isTmail = isTmail;
        //为穿透返利传递参数
        String tvOptions = TvOptionsConfig.getTvOptions();
        if (!TextUtils.isEmpty(tvOptions)) {
            if (isFromCartToBuildOrder) {
                String tvOptionsSubstring = tvOptions.substring(0, tvOptions.length() - 1);
                addParams("tvOptions", tvOptionsSubstring + "1");
                AppDebug.v(TAG, "tvOptions = " + tvOptionsSubstring + "1");
            } else {
                addParams("tvOptions", tvOptions);
                AppDebug.v(TAG, "tvOptions = " + tvOptions);
            }
        }
        String appKey = Config.getChannel();
        if (!TextUtils.isEmpty(appKey)) {
            AppDebug.e(TAG, "appKey = " + appKey);
            addParams("appKey", appKey);
        }
        //是否穿透买就返
        addParams("mjf", "true");
        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            array.put(list.get(i));
        }
        AppDebug.v(TAG, "traceRoutes =" + array);
        addParams("traceRoutes", array.toString());

        JSONObject object = new JSONObject();
        try {
            object.put("umToken", Config.getUmtoken(CoreApplication.getApplication()));
            object.put("wua", Config.getWua(CoreApplication.getApplication()));
            object.put("isSimulator", Config.isSimulator(CoreApplication.getApplication()));
            object.put("userAgent", Config.getAndroidSystem(CoreApplication.getApplication()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String extParams = object.toString();
        addParams("extParams", extParams);
    }

    @Override
    protected Map<String, String> getAppData() {
        //原来搜索结果需传递的参数
        if (s < 0) {
            s = 0;
        }
        if (s > 100) {
            s = 100;
        }
        if (n <= 0) {
            n = 40;
        }
        if (n > 100) {
            n = 100;
        }
        addParams("s", String.valueOf(s));
        addParams("n", String.valueOf(n));

        boolean supportZtc = GlobalConfig.instance == null || !GlobalConfig.instance.isBeta();
        if (!TextUtils.isEmpty(flag) && supportZtc) {
            addParams("flag", flag);
        }

        if (!TextUtils.isEmpty(item_ids)) {
            addParams("nid", item_ids);
        } else {
            if (!TextUtils.isEmpty(q)) {
                addParams("q", q);
            }
        }

        if (!TextUtils.isEmpty(cateId)) {
            addParams("cateId", cateId);
        }

        if (per > 0) {
            addParams("per", "" + per);
        }

        //设置IP地址
        if (!TextUtils.isEmpty(NetWorkUtil.getIpAddress())) {
            addParams("ip", NetWorkUtil.getIpAddress());
        }

//        addParams("ztcC", "" + ztcc);
        try {
            if (!TextUtils.isEmpty(sort)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sort", sort);
                if(isTmail) {
                    jsonObject.put("user_type", "1");
                }
                addParams("sortexts", jsonObject.toString());
            } else {
                JSONObject jsonObject = new JSONObject();
                if(isTmail) {
                    jsonObject.put("user_type", "1");
                }
                addParams("sortexts", jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public int getPer() {
        return per;
    }

    public void setZtcc(int ztcc) {
        this.ztcc = ztcc;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    @SuppressWarnings("unchecked")
    @Override
    public GoodsSearchResultDo resolveResponse(JSONObject obj) throws Exception {
        return GoodsSearchResultDo.resolveFromJson(obj);
    }

    public void setPageNo(int page_no) {
        this.s = page_no;
    }

    public void setPageSize(int page_size) {
        this.n = page_size;
    }

    public void setQueryKW(String q) {
        this.q = q;
    }

    public void setItemIds(String item_ids) {
        this.item_ids = item_ids;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

}
