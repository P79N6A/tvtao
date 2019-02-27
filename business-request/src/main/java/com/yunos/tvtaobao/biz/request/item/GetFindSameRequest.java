package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.FindSameBean;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoubo on 2018/7/12.
 * zhoubo on 2018/7/12 17:06
 * describition
 */

public class GetFindSameRequest extends BaseMtopRequest {

    private final static String API = "com.taobao.wireless.chanel.realTimeRecommond";

    private static final String TAG = "findSameRequest";

    //构造中可以传递需要的参数
    private int pageSize = 10;
    private int currentPage = 1;

    private String catid;

    private String nid;

    public GetFindSameRequest(Integer pageSize, Integer currentPage, String catid, String nid) {
        this.pageSize = pageSize == null ? 10 : pageSize;
        this.currentPage = currentPage == null ? 1 : currentPage;
        this.catid = catid;
        this.nid = nid;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "2.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("catid", catid);
            jsonObject.put("nid", nid);
            jsonObject.put("tabid", "default");
            jsonObject.put("appid", "2016");
            jsonObject.put("istmall", "");
            addParams("param", jsonObject.toString());
        } catch (JSONException e) {
            AppDebug.d(TAG, e.toString());
        }
        addParams("albumId", "NEW_FIND_SIMILAR");
        addParams("pageSize", String.valueOf(pageSize));
        addParams("currentPage", String.valueOf(currentPage));
        addParams("h5version", String.valueOf(2));
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected FindSameContainerBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            AppDebug.d(TAG, "--------->>findsameBean is null<<------------");
            return null;
        }

        JSONObject jsonObject = new JSONObject(obj.toString()).optJSONObject("model");
        if (jsonObject == null) return null;
        JSONObject jsonResult = jsonObject.getJSONObject("result");
        if (jsonResult == null) return null;
        JSONObject recommedResult = jsonResult.getJSONObject("recommedResult");
        if (recommedResult == null) return null;
        //获取集合
        String recommedResultString = recommedResult.getString("result");
        //获取带有Pic练级的String
        String sourceItemStr = recommedResult.getString("sourceItem");
        //获取真实图片路径
        FindSameContainerBean findSameContainerBean = new FindSameContainerBean();

        JSONObject jsonObject2 = new JSONObject(sourceItemStr);
        String pic = jsonObject2.getString("pic");
        List<FindSameBean> findSameBeans = com.alibaba.fastjson.JSONObject.parseArray(recommedResultString, FindSameBean.class);
        //封装pci&&list列表展示
        findSameContainerBean.setFindSameBeanList(findSameBeans);
        findSameContainerBean.setPic(pic);
        return findSameContainerBean;
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
