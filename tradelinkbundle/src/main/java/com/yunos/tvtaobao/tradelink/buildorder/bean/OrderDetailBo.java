package com.yunos.tvtaobao.tradelink.buildorder.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by zhujun on 11/28/16.
 */

public class OrderDetailBo implements Serializable {
    private JSONObject jsonObject = null;

    public static OrderDetailBo resolveJson(JSONObject json) {

        OrderDetailBo bo = new OrderDetailBo();
        bo.jsonObject = json;
        return bo;
    }

    private OrderDetailBo() {

    }

    public boolean hasCoupon() {
        if (jsonObject == null) return false;
        try {
            if (jsonObject.has("data")) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray group = data.getJSONArray("group");
                for (int i = 0; i < group.length(); i++) {
                    JSONObject order = group.getJSONObject(i);
                    Iterator iterator = order.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONArray cells = order.getJSONArray(key);
                        for (int j = 0; j < cells.length(); j++) {
                            JSONObject cell = cells.getJSONObject(j);
                            if (cell.has("cellType")) {
                                String cellType = cell.getString("cellType");
                                if ("paydetail".equals(cellType)) {
                                    JSONArray cellData = cell.getJSONArray("cellData");
                                    for (int k = 0; k < cellData.length(); k++) {
                                        JSONObject oneData = cellData.getJSONObject(k);
                                        if (oneData.has("tag")) {
                                            String tag = oneData.getString("tag");
                                            if ("paydetail".equals(tag)) {
                                                if (oneData.has("fields") && oneData.getJSONObject("fields").has("details")) {
                                                    JSONArray details = oneData.getJSONObject("fields").getJSONArray("details");
                                                    for (int l = 0; l < details.length(); l++) {
                                                        JSONObject detail = details.getJSONObject(l);
                                                        if (detail.has("name") && "红包抵扣".equals(detail.getString("name")))
                                                            return true;
                                                    }
                                                }
                                            } else continue;
                                        } else continue;
                                    }
                                } else
                                    continue;
                            } else
                                continue;
                        }
                    }
                }

            }
        } catch (Exception e) {

        }
        return false;
    }
}
