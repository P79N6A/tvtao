package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ouzy on 2018/8/2.
 * desc:首页猜你喜欢数据(与购物车区分开，因为返回的数据格式不一致）
 */

public class GetHomeGuessLikeRequest extends BaseMtopRequest {

    private static final String API = "mtop.taobao.wireless.guess.get";

    public GetHomeGuessLikeRequest(String channel) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("currencyCode", "CNY");
            jsonObject.put("countryNumCode", "156");
            jsonObject.put("countryId", "CN");
            jsonObject.put("actualLanguageCode", "zh-CN");
            addParams("edition", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("channel", channel);
        addParams("pageNum", "0");
        addParams("nick", User.getNick());
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GuessLikeGoodsBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        // 首页猜你喜欢和购物车猜你喜欢返回的数据格式不一致，手动构建GuessLikeGoodsBean
        GuessLikeGoodsBean guessLikeGoodsBean = new GuessLikeGoodsBean();
        GuessLikeGoodsBean.ResultVO resultVO = new GuessLikeGoodsBean.ResultVO();
        List<GuessLikeGoodsBean.ResultVO.RecommendVO> list = new ArrayList<>();
        try {
            JSONObject datas = obj.getJSONObject("data");
            //获取数据层级key
            JSONObject hierarchy = obj.getJSONObject("hierarchy");
            JSONObject structure = hierarchy.getJSONObject("structure");
            String root = hierarchy.getString("root");
            JSONArray hierarchyKey = structure.getJSONArray(root);
            //根据key获取数据
            for (int i = 0; i < hierarchyKey.length(); i++) {
                JSONObject data = datas.getJSONObject(hierarchyKey.getString(i));
                GuessLikeGoodsBean.ResultVO.RecommendVO recommendVO = new GuessLikeGoodsBean.ResultVO.RecommendVO();
                JSONObject fields = data.getJSONObject("fields");
                JSONObject trackParamShow = fields.optJSONObject("trackParamShow");
                // 猜你喜欢的商品没有商品itemId,过滤掉
                String itemId = "";
                if (trackParamShow != null) {
                    if(trackParamShow.has("itemId")){
                        itemId = trackParamShow.getString("itemId");
                    }else {
                        continue;
                    }
                    GuessLikeFieldsVO guessLikeFieldsVO = JSON.parseObject(fields.toString(), GuessLikeFieldsVO.class);
                    guessLikeFieldsVO.setItemId(itemId);
                    recommendVO.setFields(guessLikeFieldsVO);
                    recommendVO.setBizName(data.getString("bizName"));
                    recommendVO.setType(data.getString("type"));
                    list.add(recommendVO);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AppDebug.v("GetGuessLikeRequest", "homePage GuessLikeData error " + e.toString());
        }
        resultVO.setRecommedResult(list);
        guessLikeGoodsBean.setResult(resultVO);
        return guessLikeGoodsBean;
    }
}