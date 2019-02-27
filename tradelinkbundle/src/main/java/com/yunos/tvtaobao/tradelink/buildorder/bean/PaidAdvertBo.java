package com.yunos.tvtaobao.tradelink.buildorder.bean;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PaidAdvertBo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3077520779503003747L;

    private String imageUrl;

    private String uri;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static PaidAdvertBo fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        PaidAdvertBo item = new PaidAdvertBo();
        item.setImageUrl(obj.optString("imgUrl"));
        item.setUri(obj.optString("uri"));
        return item;
    }

}
