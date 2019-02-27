package com.yunos.tvtaobao.biz.request.bo;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zhujun on 06/06/2017.
 */

public class GoodsSearchZtcResult implements Serializable {

    private static final String TAG = GoodsSearchZtcResult.class.getSimpleName();

    /**
     * 当前页码
     */
    private String current_page;
    /**
     * 每页商品数
     */
    private String page_size;

    /**
     * 商品列表
     */
    private SearchedGoods[] goodlist;

    /**
     * 当前页码
     *
     * @param current_page
     */
    public void setCurrentPage(String current_page) {
        this.current_page = current_page;
    }

    /**
     * 每页商品数
     *
     * @param page_size
     */
    public void setPageSize(String page_size) {
        this.page_size = page_size;
    }


    /**
     * 商品列表
     *
     * @param goodlist
     */
    public void setGoodList(SearchedGoods[] goodlist) {
        this.goodlist = goodlist;
    }

    /**
     * 获取当前页码
     *
     * @return
     */
    public String getCurrentPage() {
        return current_page;
    }

    /**
     * 获取每页商品数
     *
     * @return
     */
    public String getPageSize() {
        return page_size;
    }


    /**
     * 获取商品列表
     *
     * @return
     */
    public SearchedGoods[] getGoodList() {
        return this.goodlist;
    }

    @Override
    public String toString() {
        String text = "{ current_page = " + current_page + ", page_size = " + page_size + ", goodlist = " + goodlist + " , size = " + goodlist.length + " }";
        return text;
    }

    /**
     * 解析商品数据
     *
     * @return
     */
    public static GoodsSearchZtcResult resolveFromJson(String response) throws JSONException {
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        //替换掉转义字符，避免生成json失败，导致无法搜索到宝贝
        response = response.replaceAll("\\\\", "");
        if (response.length() == 0) {
            return null;
        }

        JSONObject obj = new JSONObject(response);
        JSONObject objPage = null;
        if (!obj.isNull("page")) {
            objPage = obj.getJSONObject("page");
        }

        if (objPage == null) {
            return null;
        }

        GoodsSearchZtcResult goodsSearchResult = new GoodsSearchZtcResult();

        if (!objPage.isNull("current_page")) {
            goodsSearchResult.setCurrentPage(objPage.getString("current_page"));
        }
        if (!objPage.isNull("page_size")) {
            goodsSearchResult.setPageSize(objPage.getString("page_size"));
        }
        if (!obj.isNull("searchItems")) {
            JSONArray array = obj.getJSONArray("searchItems");
            GoodsWithZtc[] goodsList = new GoodsWithZtc[array.length()];
            for (int i = 0; i < array.length(); i++) {
                goodsList[i] = GoodsWithZtc.resolveFromJson(array.getJSONObject(i));
            }
            goodsSearchResult.setGoodList(goodsList);
        }

        AppDebug.v(TAG, "GoodsSearchResult.resolveFromJson.goodsSearchResult = " + goodsSearchResult);
        return goodsSearchResult;
    }

    /**
     * 解析商品数据
     *
     * @return
     */
    public static GoodsSearchZtcResult resolveFromJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            AppDebug.e(TAG, "GoodsSearchResult.resolveFromJson.obj == null ");
            return null;
        }

        GoodsSearchZtcResult goodsSearchResult = new GoodsSearchZtcResult();

//        if (!objPage.isNull("currentPage")) {
//            goodsSearchResult.setCurrentPage(objPage.getString("currentPage"));
//        }
//        if (!objPage.isNull("pageSize")) {
//            goodsSearchResult.setPageSize(objPage.getString("pageSize"));
//        }

        if (!obj.isNull("searchItems")) {
            JSONArray array = obj.getJSONArray("searchItems");
            SearchedGoods[] goodsList = new SearchedGoods[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                if (!"item".equals(jsonObject.getString("type"))) {
                    goodsList[i] = GoodsWithZtc.resolveFromJson(jsonObject);
                }
                else {
                    goodsList[i] = GoodsTmail.resolveFromJson(jsonObject);
                }

            }
            goodsSearchResult.setGoodList(goodsList);
        }

        AppDebug.v(TAG, "GoodsSearchZtcResult.resolveFromJson.GoodsSearchZtcResult = " + goodsSearchResult);
        return goodsSearchResult;
    }
}
