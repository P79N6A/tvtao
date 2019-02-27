package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class OrderOperateObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4025251979318382922L;
    
    /**
     * 
     */
    private String api;
    private String name;
    private ParamObject param;

    /**
     * @return the api
     */
    public String getApi() {
        return api;
    }

    /**
     * @param api
     *            the api to set
     */
    public void setApi(String api) {
        this.api = api;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the param
     */
    public ParamObject getParam() {
        return param;
    }

    /**
     * @param param
     *            the param to set
     */
    public void setParam(ParamObject param) {
        this.param = param;
    }
    
    public static OrderOperateObject resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        OrderOperateObject orderOperateObject = new OrderOperateObject();
        orderOperateObject.setApi(obj.getString("api"));
        orderOperateObject.setName(obj.optString("name"));
        orderOperateObject.setParam(ParamObject.resolverFromMtop(obj.getJSONObject("param")));
        return orderOperateObject;
    }
}
