package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4OnTimeInfo implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String icon;
    private int code;
    private String desc;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static TakeOutOrderInfoDetails4OnTimeInfo resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4OnTimeInfo onTimeInfo = new TakeOutOrderInfoDetails4OnTimeInfo();

        if (obj != null) {
            onTimeInfo.setCode(obj.optInt("code", 0));
            onTimeInfo.setDesc(obj.optString("desc"));
            onTimeInfo.setIcon(obj.optString("icon"));
        }

        return onTimeInfo;
    }
}
