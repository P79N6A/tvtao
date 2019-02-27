package com.yunos.tvtaobao.biz.request.item;

import android.util.Log;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.BottomTipVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.BottomTipVO.TextVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.MasterPicVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.PriceVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.TitleVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO.TitleVO.ContextVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean.ResultVO.RecommendVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuhaoteng on 2018/8/7.
 * 支付成功后的猜你喜欢接口，做了一层数据结构解析和转化
 */

public class GetRealTimeRecommondRequest extends BaseMtopRequest {

    private static final String API = "com.taobao.wireless.chanel.realTimeRecommond";

    public GetRealTimeRecommondRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            //场景id，实际产生作用的参数
            jsonObject.put("appid", "1640");
            jsonObject.put("albumId", "1");
            jsonObject.put("enabled", "true");
            //jsonObject.put("orderId", "194246894347517");
            //jsonObject.put("catIds", "50023878");
            addParams("param", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("albumId", "PAY_SUCCESS");
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
        return null;
    }

    @Override
    protected GuessLikeGoodsBean resolveResponse(JSONObject obj) throws Exception {
        Log.i("GetRealTime", obj.toString());
        GuessLikeGoodsBean guessLikeGoodsBean = new GuessLikeGoodsBean();
        GuessLikeGoodsBean.ResultVO resultVO = new GuessLikeGoodsBean.ResultVO();
        List<RecommendVO> recommedResults = new ArrayList<>();

        JSONObject modelJsonObject = obj.getJSONObject("model");
        String currentPage = modelJsonObject.getString("currentPage");
        String currentTime = modelJsonObject.getString("currentTime");
        String empty = modelJsonObject.getString("empty");
        JSONObject resultJsonObject = modelJsonObject.getJSONObject("result");
        JSONArray recommedResultJsonArray = resultJsonObject.getJSONArray("recommedResult");
        JSONObject recommedResultJsonObject = recommedResultJsonArray.getJSONObject(0);
        JSONArray itemListJsonArray = recommedResultJsonObject.getJSONArray("itemList");
        for (int i = 0; i < itemListJsonArray.length(); i++) {
            RecommendVO recommendVO = new RecommendVO();
            GuessLikeFieldsVO guessLikeFieldsVO = new GuessLikeFieldsVO();
            BottomTipVO bottomTipVO = new BottomTipVO();
            MasterPicVO masterPicVO = new MasterPicVO();
            JSONObject itemJsonObject = itemListJsonArray.getJSONObject(i);
            String type = itemJsonObject.getString("type");
            if (type.equals("8")) {
                recommendVO.setType("item");
                TitleVO titleTextVo = new TitleVO();
                ContextVO contextVO = new ContextVO();
                if (itemJsonObject.has("title")) {
                    contextVO.setContent(itemJsonObject.getString("title"));
                } else {
                    continue;
                }
                titleTextVo.setContext(contextVO);
                guessLikeFieldsVO.setTitle(titleTextVo);
                JSONObject extMapJsonObject = itemJsonObject.getJSONObject("extMap");
                String recExc = extMapJsonObject.getString("recExc");
                TextVO bottomTextVo = new TextVO();
                bottomTextVo.setContent(recExc);
                bottomTipVO.setText(bottomTextVo);
                masterPicVO.setPicUrl(itemJsonObject.getString("picUrl"));
                guessLikeFieldsVO.setBottomTip(bottomTipVO);
                guessLikeFieldsVO.setMasterPic(masterPicVO);
                PriceVO priceVO = new PriceVO();
                priceVO.setYuan(itemJsonObject.getString("marketPrice"));
                priceVO.setSymbol("¥");
                guessLikeFieldsVO.setPrice(priceVO);
                guessLikeFieldsVO.setItemId(itemJsonObject.getString("itemId"));
                recommendVO.setFields(guessLikeFieldsVO);
                recommedResults.add(recommendVO);
            }
        }

        resultVO.setRecommedResult(recommedResults);
        guessLikeGoodsBean.setCurrentPage(currentPage);
        guessLikeGoodsBean.setCurrentTime(currentTime);
        guessLikeGoodsBean.setEmpty(empty);
        guessLikeGoodsBean.setResult(resultVO);

        return guessLikeGoodsBean;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
