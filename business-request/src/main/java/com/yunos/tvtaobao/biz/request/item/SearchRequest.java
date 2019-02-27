package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchMO;

import java.util.HashMap;
import java.util.Map;

public class SearchRequest extends BaseHttpRequest {

    private static final long serialVersionUID = 4041797502287250945L;

    private int curPage = 1;
    private int pageSize = 10;
    private String kw = "";
    private String tab = null; //引擎类型,默认是主搜索。tab=mall:调用商城搜索引擎；tab=discount：调用促销搜索
    private String sort = "_coefp";
    private String nid = null; // 商品id列表
    private String catmap = null; // 前台类目

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("vm", "nw");
        params.put("ttid", Config.getTTid());

        if (nid != null && nid.length() >= 1) {
            params.put("nid", nid);
        } else {
            if (!TextUtils.isEmpty(catmap)) {
                params.put("catmap", catmap);
            }

            if (!TextUtils.isEmpty(kw)) {
                params.put("q", kw);
            }
        }
        if (!TextUtils.isEmpty(tab) && ("mall".equals(tab) || "discount".equals(tab))) {
            params.put("tab", tab);
        }
        params.put("page", String.valueOf(curPage));
        params.put("sort", sort);
        params.put("n", String.valueOf(pageSize));

        return params;
    }

    @Override
    protected String getHttpDomain() {
        return "http://api.s.m.taobao.com/search.json";
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoodsSearchMO resolveResult(String response) throws Exception {
        return GoodsSearchMO.resolveFromMTOP(response);
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getCatmap() {
        return catmap;
    }

    public void setCatmap(String catmap) {
        this.catmap = catmap;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

}
