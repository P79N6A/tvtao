package com.yunos.tvtaobao.biz.request.bo;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * 主搜索结果实体
 * @author shengchun
 *
 */
public class GoodsSearchMO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2821380640539172830L;
    
    /**
     * 总条数
     */
    private int totalResults;
    
    /**
     * 总页数
     */
    private int totalPage;
    
    /**
     * 每页显示数量
     */
    private int pageSize;
    
    /**
     * 当前页码值
     */
    private int page;
    
    /**
     * 排序字段
     */
    private String orderBy;
    
    /**
     * 查询的关键词
     */
    private String paramValue;
    
    /**
     * 查询商品列表
     */
    private Goods[] goodsList;


    public int getTotalResults() {
        return totalResults;
    }


    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }


    public int getTotalPage() {
        return totalPage;
    }


    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }


    public int getPageSize() {
        return pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public int getPage() {
        return page;
    }


    public void setPage(int page) {
        this.page = page;
    }


    public String getOrderBy() {
        return orderBy;
    }


    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }


    public String getParamValue() {
        return paramValue;
    }


    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }


    public Goods[] getGoodsList() {
        return goodsList;
    }


    public void setGoodsList(Goods[] goodsList) {
        this.goodsList = goodsList;
    }

    /**
     * 解析JSON串
     * @param obj
     * @return
     * @throws JSONException
     */
    @SuppressWarnings("unused")
    public static GoodsSearchMO resolveFromMTOP(String response) throws JSONException {
        if (TextUtils.isEmpty(response)) {
            return null;
        }
        //替换掉转义字符，避免生成json失败，导致无法搜索到宝贝
        response = response.replaceAll("\\\\","");
        if (response.length() == 0) return null;
        JSONObject obj = new JSONObject(response);

        if (obj == null) return null;
        GoodsSearchMO goodsSearchMO = new GoodsSearchMO();
        if (!obj.isNull("totalResults")) {
            goodsSearchMO.setTotalResults(obj.getInt("totalResults"));
        }
        if (!obj.isNull("totalPage")) {
            goodsSearchMO.setTotalPage(obj.getInt("totalPage"));
        }
        if (!obj.isNull("pageSize")) {
            goodsSearchMO.setPageSize(obj.getInt("pageSize"));
        }
        if (!obj.isNull("page")) {
            goodsSearchMO.setPage(obj.getInt("page"));
        }
        if (!obj.isNull("paramValue")) {
            goodsSearchMO.setParamValue(obj.getString("paramValue"));
        }
        if (!obj.isNull("order_by")) {
            goodsSearchMO.setOrderBy(obj.getString("order_by"));
        }
        if (!obj.isNull("itemsArray")) {
            JSONArray array = obj.getJSONArray("itemsArray");
            Goods[] goodsList = new Goods[array.length()];
            for (int i = 0; i < array.length(); i++) {
                goodsList[i] = Goods.resolveFromMTOP(array.getJSONObject(i));
            }
            goodsSearchMO.setGoodsList(goodsList);
        }
        return goodsSearchMO;
    }
}
