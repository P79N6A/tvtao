/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.bo
 * FILE NAME: Option.java
 * CREATED TIME: 2014-10-29
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import com.yunos.tvtaobao.biz.request.core.JsonResolver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2014-10-29 下午8:10:38
 */
public class Option extends BaseMO {

    private static final long serialVersionUID = 3250136711541187306L;
    private String type;
    private String value;
    private String displayName;
    private String count;
    private String optStr;
    private Map<String, String> params;
    private OptionExtend extend;
    /**
     * f服务器没有该字段，是miscData中获取的
     */
    private String bkgImgUrl;
    /**
     * 子类目
     */
    private ArrayList<Option> children;

    public static Option resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        Option item = new Option();
        item.setType(obj.optString("type"));
        item.setValue(obj.optString("value"));
        item.setDisplayName(obj.optString("displayName"));
        item.setCount(obj.optString("count"));
        item.setOptStr(obj.optString("optStr"));
        if (obj.has("params")) {
            item.setParams(JsonResolver.jsonobjToMap(obj.getJSONObject("params")));
        }
        item.setExtend(OptionExtend.resolveFromJson(obj.optJSONObject("extend")));
        item.setBkgImgUrl(obj.optString("bkgImgUrl"));
        return item;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Option [type=" + type + ", value=" + value + ", displayName=" + displayName + ", count=" + count
                + ", optStr=" + optStr + ", params=" + params + ", extend=" + extend + ", bkgImgUrl=" + bkgImgUrl
                + ", children=" + children + "]";
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return the optStr
     */
    public String getOptStr() {
        return optStr;
    }

    /**
     * @param optStr the optStr to set
     */
    public void setOptStr(String optStr) {
        this.optStr = optStr;
    }

    /**
     * @return the extend
     */
    public OptionExtend getExtend() {
        return extend;
    }

    /**
     * @param extend the extend to set
     */
    public void setExtend(OptionExtend extend) {
        this.extend = extend;
    }

    /**
     * @return the bkgImgUrl
     */
    public String getBkgImgUrl() {
        return bkgImgUrl;
    }

    /**
     * @param bkgImgUrl the bkgImgUrl to set
     */
    public void setBkgImgUrl(String bkgImgUrl) {
        this.bkgImgUrl = bkgImgUrl;
    }

    /**
     * @return the children
     */
    public ArrayList<Option> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(ArrayList<Option> children) {
        this.children = children;
    }

    /**
     * @return the params
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
