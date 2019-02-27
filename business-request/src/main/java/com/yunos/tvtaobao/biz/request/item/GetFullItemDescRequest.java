package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetFullItemDescRequest extends BaseHttpRequest {

    private static final long serialVersionUID = 9159601022400516693L;
    private String itemId;
    //eg /%7B%22item_num_id%22%3A%{221500011673805}%22%7D
    private String REQ_DATA = "%7B%22item_num_id%22%3A%22${itemId}%22%7D";

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public GetFullItemDescRequest(String itemId) {
        this.itemId = itemId;
    }

    /**
     * 得到请求参数信息
     * @return
     */
    public String getDataInfo() {
        return REQ_DATA.replace("${itemId}", itemId);
    }

    @Override
    public String getUrl() {
        return getHttpDomain() + "?data=" + getDataInfo();
    }

    @Override
    protected String getHttpDomain() {
        return "https://hws.alicdn.com/cache/mtop.wdetail.getItemFullDesc/4.1/";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String resolveResult(String response) throws Exception {
        String result = "";
        JSONObject obj = null;
        response = response.replace("\n", "");
        obj = new JSONObject(response);
        if (obj != null && obj.has("data")) {
            obj = obj.getJSONObject("data");
        }
        if (obj != null && obj.has("desc")) {
            result = obj.getString("desc");
        }
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

}
