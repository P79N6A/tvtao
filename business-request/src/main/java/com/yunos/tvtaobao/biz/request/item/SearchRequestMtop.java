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

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResult;

import org.json.JSONObject;

import java.util.Map;

public class SearchRequestMtop extends BaseMtopRequest {

    private static final long serialVersionUID = 505653598028887525L;
    private String API = "mtop.tmall.search.searchProduct";
    private String version = "1.0";

    private int page_no = 1; // 页码
    private int page_size = 40; // 每页商品数
    private String q = ""; // 关键字
    private String sort = "s";
    private String cat = null; // 前台类目
    private String category = null; //后台类目
    private String prop = null; //属性，格式: pid:vid;pid:vid
    private String start_price = null; // 开始价格（单位：元）
    private String end_price = null; // 结束价格（单位：元）
    private String item_ids = null; // 查询指定的商品
    private String user_id = null; // 卖家数字ID
    private String from = "tvtaobao"; // 调用方名称，必选
    private String auction_tag = null; // 商品标签

    @Override
    protected Map<String, String> getAppData() {
        if (page_no <= 0) {
            page_no = 1;
        }
        if (page_no > 100) {
            page_no = 100;
        }
        if (page_size <= 0) {
            page_size = 40;
        }
        if (page_size > 100) {
            page_size = 100;
        }
        if (TextUtils.isEmpty(q)) {
            q = "";// 默认
        }
        if (TextUtils.isEmpty(sort)) {
            sort = "s";// 默认
        }
        if (TextUtils.isEmpty(cat)) {
            cat = "";// 默认
        }
        if (TextUtils.isEmpty(category)) {
            category = "";// 默认
        }
        if (TextUtils.isEmpty(prop)) {
            prop = "";// 默认
        }
        if (TextUtils.isEmpty(start_price)) {
            start_price = "";// 默认
        }
        if (TextUtils.isEmpty(end_price)) {
            end_price = "";// 默认
        }
        if (TextUtils.isEmpty(auction_tag)) {
            auction_tag = "";// 默认
        }
        if (TextUtils.isEmpty(item_ids)) {
            item_ids = "";// 默认
        }
        if (TextUtils.isEmpty(user_id)) {
            user_id = "";// 默认
        }
        if (TextUtils.isEmpty(from)) {
            from = "tvtaobao";// 默认
        }

        if (!TextUtils.isEmpty(item_ids)) {
            addParams("item_ids", item_ids);
        } else {
            if (!TextUtils.isEmpty(q)) {
                addParams("q", q);
            }

            if (!TextUtils.isEmpty(cat)) {
                addParams("cat", cat);
            }

            if (!TextUtils.isEmpty(category)) {
                addParams("category", category);
            }
        }

        addParams("page_no", String.valueOf(page_no));
        addParams("page_size", String.valueOf(page_size));
        addParams("sort", sort);
        if (!TextUtils.isEmpty(prop)) {
            addParams("prop", prop);
        }
        if (!TextUtils.isEmpty(start_price)) {
            addParams("start_price", start_price);
        }
        if (!TextUtils.isEmpty(end_price)) {
            addParams("end_price", end_price);
        }
        if (!TextUtils.isEmpty(auction_tag)) {
            addParams("auction_tag", auction_tag);
        }
        if (!TextUtils.isEmpty(user_id)) {
            addParams("user_id", user_id);
        }
        addParams("from", from);
        return null;
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
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoodsSearchResult resolveResponse(JSONObject obj) throws Exception {
        return GoodsSearchResult.resolveFromJson(obj);
    }

    public void setPageNo(int page_no) {
        this.page_no = page_no;
    }

    public void setPageSize(int page_size) {
        this.page_size = page_size;
    }

    public void setQueryKW(String q) {
        this.q = q;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public void setStartPrice(String start_price) {
        this.start_price = start_price;
    }

    public void setEndPrice(String end_price) {
        this.end_price = end_price;
    }

    public void setItemIds(String item_ids) {
        this.item_ids = item_ids;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setAuctionTag(String auction_tag) {
        this.auction_tag = auction_tag;
    }
}
