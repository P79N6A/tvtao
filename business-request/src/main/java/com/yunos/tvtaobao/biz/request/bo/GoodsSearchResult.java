/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: GoodsSearchResult.java
 * CREATED TIME: 2015年11月13日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 搜索天猫的商品数据结构
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年11月13日 下午4:18:45
 */
public class GoodsSearchResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9094487275465471716L;

    /**
     * 当前页码
     */
    private String current_page;
    /**
     * 每页商品数
     */
    private String page_size;
    /**
     * 商品总数
     */
    private String total_num;
    /**
     * 总页数
     */
    private String total_page;

    /**
     * 商品列表
     */
    private GoodsTmail[] goodlist;

    /**
     * 当前页码
     * @param current_page
     */
    public void setCurrentPage(String current_page) {
        this.current_page = current_page;
    }

    /**
     * 每页商品数
     * @param page_size
     */
    public void setPageSize(String page_size) {
        this.page_size = page_size;
    }

    /**
     * 总商品数
     * @param total_num
     */
    public void setTotalNum(String total_num) {
        this.total_num = total_num;
    }

    /**
     * 总页数
     * @param total_page
     */
    public void setTotalPage(String total_page) {
        this.total_page = total_page;
    }

    /**
     * 商品列表
     * @param goodlist
     */
    public void setGoodList(GoodsTmail[] goodlist) {
        this.goodlist = goodlist;
    }

    /**
     * 获取当前页码
     * @return
     */
    public String getCurrentPage() {
        return current_page;
    }

    /**
     * 获取每页商品数
     * @return
     */
    public String getPageSize() {
        return page_size;
    }

    /**
     * 获取商品总数
     * @return
     */
    public String getTotalNum() {
        return total_num;
    }

    /**
     * 获取商品列表
     * @return
     */
    public GoodsTmail[] getGoodList() {
        return this.goodlist;
    }

    /**
     * 获取总页数
     * @return
     */
    public String getTotalPage() {
        return total_page;
    }

    @Override
    public String toString() {
        String text = "{ current_page = " + current_page + ", page_size = " + page_size + ", total_num = " + total_num
                + ", total_page = " + total_page + ", goodlist = " + goodlist + " }";
        return text;
    }

    /**
     * 解析商品数据
     * @return
     */
    public static GoodsSearchResult resolveFromJson(String response) throws JSONException {
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

        GoodsSearchResult goodsSearchResult = new GoodsSearchResult();

        if (!objPage.isNull("current_page")) {
            goodsSearchResult.setCurrentPage(objPage.getString("current_page"));
        }
        if (!objPage.isNull("page_size")) {
            goodsSearchResult.setPageSize(objPage.getString("page_size"));
        }
        if (!objPage.isNull("total_num")) {
            goodsSearchResult.setTotalNum(objPage.getString("total_num"));
        }
        if (!objPage.isNull("total_page")) {
            goodsSearchResult.setTotalPage(objPage.getString("total_page"));
        }

        if (!obj.isNull("item")) {
            JSONArray array = obj.getJSONArray("item");
            GoodsTmail[] goodsList = new GoodsTmail[array.length()];
            for (int i = 0; i < array.length(); i++) {
                goodsList[i] = GoodsTmail.resolveFromJson(array.getJSONObject(i));
            }
            goodsSearchResult.setGoodList(goodsList);
        }

        AppDebug.v("GoodsSearchResult", "GoodsSearchResult.resolveFromJson.goodsSearchResult = " + goodsSearchResult);
        return goodsSearchResult;
    }

    /**
     * 解析商品数据
     * @return
     */
    public static GoodsSearchResult resolveFromJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            AppDebug.e("GoodsSearchResult", "GoodsSearchResult.resolveFromJson.obj == null ");
            return null;
        }

        JSONObject objPage = null;
        if (!obj.isNull("page")) {
            objPage = obj.getJSONObject("page");
        }

        if (objPage == null) {
            AppDebug.e("GoodsSearchResult", "GoodsSearchResult.resolveFromJson.objPage == null ");
            return null;
        }

        GoodsSearchResult goodsSearchResult = new GoodsSearchResult();

        if (!objPage.isNull("currentPage")) {
            goodsSearchResult.setCurrentPage(objPage.getString("currentPage"));
        }
        if (!objPage.isNull("pageSize")) {
            goodsSearchResult.setPageSize(objPage.getString("pageSize"));
        }
        if (!objPage.isNull("totalNum")) {
            goodsSearchResult.setTotalNum(objPage.getString("totalNum"));
        }
        if (!objPage.isNull("totalPage")) {
            goodsSearchResult.setTotalPage(objPage.getString("totalPage"));
        }

        if (!obj.isNull("productList")) {
            JSONArray array = obj.getJSONArray("productList");
            GoodsTmail[] goodsList = new GoodsTmail[array.length()];
            for (int i = 0; i < array.length(); i++) {
                goodsList[i] = GoodsTmail.resolveFromJson(array.getJSONObject(i));
            }
            goodsSearchResult.setGoodList(goodsList);
        }

        AppDebug.v("GoodsSearchResult", "GoodsSearchResult.resolveFromJson.goodsSearchResult = " + goodsSearchResult);
        return goodsSearchResult;
    }
}
