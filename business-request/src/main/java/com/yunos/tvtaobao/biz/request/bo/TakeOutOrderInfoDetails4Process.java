package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4Process implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String processStatus;
    private boolean processed;
    private String time;

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static TakeOutOrderInfoDetails4Process resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4Process details4Process = new TakeOutOrderInfoDetails4Process();

        if (obj != null) {
            details4Process.setProcessStatus(obj.optString("processStatus"));
            details4Process.setProcessed(obj.optBoolean("processed", false));
            details4Process.setTime(obj.optString("time"));
        }

        return details4Process;
    }
}
