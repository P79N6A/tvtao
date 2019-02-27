package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class ManageFavRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 4510663894049267108L;

    /**
     * 宝贝ID
     */
    private String itemNumId = null;

    //功能分类,可取值为:addAuction，delAuction,getAuctions,addShop,delShop,getShops
    private String func = "addAuction";
    private int page = 1;
    private int pageSize = 10;

    private final static String API = "com.taobao.client.favorite.manage";

    /**
     * addAuction,delAuction时使用
     * @param itemNumId
     * @param func
     */
    public ManageFavRequest(String itemNumId, String func) {
        super();
        this.itemNumId = itemNumId;
        this.func = func;
    }

    /**
     * getAuctions时使用
     * @param func
     * @param page
     * @param pageSize
     */
    public ManageFavRequest(String func, int page, int pageSize) {
        super();
        this.func = func;
        this.page = page;
        this.pageSize = pageSize;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("sid", User.getSessionId());
        addParams("func", func);

        if (func.startsWith("add")) {
            addParams("itemNumId", itemNumId);
        } else if (func.startsWith("del")) {
            addParams("infoId", itemNumId);
        } else if (func.equals("getAuctions")) {
            addParams("page", String.valueOf(page));
            addParams("pageSize", String.valueOf(pageSize));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        return obj.toString();
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
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }
}
