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
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SearchRequestMtopZtc extends BaseMtopRequest {

    private String API = "mtop.taobao.tvtao.tvtaosearchservice.getbyq";
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

    private String ip = null;

    public void setSort(String sort) {
        this.sort = sort;
    }

    //    private String sort = "s";
//    private String cat = null; // 前台类目
//    private String category = null; //后台类目
//    private String prop = null; //属性，格式: pid:vid;pid:vid
//    private String start_price = null; // 开始价格（单位：元）
//    private String end_price = null; // 结束价格（单位：元）
//
//    private String user_id = null; // 卖家数字ID
//    private String from = "tvtaobao"; // 调用方名称，必选
//    private String auction_tag = null; // 商品标签

    @Override
    protected Map<String, String> getAppData() {
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

//            if (!StringUtils.isEmpty(cat)) {
//                addParams("cat", cat);
//            }
//
//            if (!StringUtils.isEmpty(category)) {
//                addParams("category", category);
//            }
        }

        if (!TextUtils.isEmpty(cateId)) {
            addParams("cateId", cateId);
        }

        if (per > 0) {
            addParams("per", "" + per);
        }

        if (!TextUtils.isEmpty(ip)) {
            addParams("ip", ip);
        }
        addParams("ztcC", "" + ztcc);
        if (!TextUtils.isEmpty(sort)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sort", sort);
                addParams("sortexts", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

//        addParams("sort", sort);
//        if (!StringUtils.isEmpty(prop)) {
//            addParams("prop", prop);
//        }
//        if (!StringUtils.isEmpty(start_price)) {
//            addParams("start_price", start_price);
//        }
//        if (!StringUtils.isEmpty(end_price)) {
//            addParams("end_price", end_price);
//        }
//        if (!StringUtils.isEmpty(auction_tag)) {
//            addParams("auction_tag", auction_tag);
//        }
//        if (!StringUtils.isEmpty(user_id)) {
//            addParams("user_id", user_id);
//        }
//        addParams("from", from);
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
    public GoodsSearchZtcResult resolveResponse(JSONObject obj) throws Exception {
        return GoodsSearchZtcResult.resolveFromJson(obj);
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    //    public void setSort(String sort) {
//        this.sort = sort;
//    }

//    public void setCat(String cat) {
//        this.cat = cat;
//    }

//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    public void setProp(String prop) {
//        this.prop = prop;
//    }
//
//    public void setStartPrice(String start_price) {
//        this.start_price = start_price;
//    }
//
//    public void setEndPrice(String end_price) {
//        this.end_price = end_price;
//    }

    public void setItemIds(String item_ids) {
        this.item_ids = item_ids;
    }

//    public void setUserId(String user_id) {
//        this.user_id = user_id;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public void setAuctionTag(String auction_tag) {
//        this.auction_tag = auction_tag;
//    }
}
