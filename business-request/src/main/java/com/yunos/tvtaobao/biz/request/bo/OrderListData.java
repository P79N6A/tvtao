package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class OrderListData implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private int total;

    /**
     * 得到订单列表数量
     */
    public int getTotal() {
        return total;
    }

    /**
     * 设置订单类别数量
     */
    public void setTotal(int total) {
        this.total = total;
    }

    public static OrderListData resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        OrderListData orderListData = new OrderListData();
        orderListData.setTotal(0);

        JSONObject obj1 = obj.optJSONObject("data");
        if (obj1 != null) {
            JSONObject obj2 = obj1.optJSONObject("meta");
            if (obj2 != null) {
                JSONObject obj3 = obj2.optJSONObject("page");
                if (obj3 != null) {
                    JSONObject obj4 = obj3.optJSONObject("fields");
                    if (obj4 != null) {
                        orderListData.setTotal(obj4.optInt("totalNumber", 0));
                    }
                }
            }
        }

        return orderListData;
    }
}
