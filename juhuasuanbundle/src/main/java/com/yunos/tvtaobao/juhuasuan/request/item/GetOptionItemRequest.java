package com.yunos.tvtaobao.juhuasuan.request.item;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.request.JsonResolver;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 通过option获取商品列表
 * @author zhuli.zhul
 * @date 2013 2013-8-21 上午10:47:11
 */
public class GetOptionItemRequest extends BaseMtopRequest {

    /** */
    private static final long serialVersionUID = -6581982688196861810L;

    /** API名称 */
    private static final String API = "mtop.ju.optionitem.get";

    /** 参数 */
    private static final String OPT_STR = "optStr";

    /** 当前页 */
    private static final String CURRENT_PAGE = "currentPage";

    /** 每页大小 */
    private static final String PAGE_SIZE = "pageSize";

    public String platformId;
    private String optStr;
    private int currentPage;
    private int pageSize;
    //需要城市维度查询时选填
    private String city;
    //排序方式：default, soldCount,discount,activityPrice,distance
    private String sort;
    //是否倒序：sort有小到大：up； sort有大到小:down
    private String reverses;
    //关键字
    private String word;

    public GetOptionItemRequest(String platformId, String optStr, int currentPage, int pageSize) {
        this(platformId, optStr, currentPage, pageSize, null, null, null, null);
    }

    public GetOptionItemRequest(String platformId, String optStr, int currentPage, int pageSize, String city) {
        this(platformId, optStr, currentPage, pageSize, city, null, null, null);
    }

    public GetOptionItemRequest(String platformId, String optStr, int currentPage, int pageSize, String city,
                                String sort, String reverses, String word) {
        this.platformId = platformId;
        this.optStr = optStr;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.city = city;
        this.sort = sort;
        this.word = word;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("platformId", this.platformId);
        addParams(OPT_STR, this.optStr);
        addParams(CURRENT_PAGE, String.valueOf(this.currentPage));
        addParams(PAGE_SIZE, String.valueOf(this.pageSize));
        addParams("city", this.city);
        addParams("sort", this.sort);
        addParams("reverses", this.reverses);
        addParams("word", this.word);
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    /**
     * 解析视频
     */
    @SuppressWarnings("unchecked")
    @Override
    protected CountList<ItemMO> resolveResponse(JSONObject obj) throws Exception {
        return JsonResolver.resolveOptionItems(obj);
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}
