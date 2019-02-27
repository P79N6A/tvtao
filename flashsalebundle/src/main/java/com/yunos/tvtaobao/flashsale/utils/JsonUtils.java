package com.yunos.tvtaobao.flashsale.utils;

import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by huangdaju on 17/7/13.
 */

public class JsonUtils {

    public static GoodsInfo resolveResponseBySeckill(JSONObject obj) {
        if (null == obj) {
            return null;
        }
        GoodsInfo info = new GoodsInfo();

        info.setType(GoodsInfo.ITEM_TYPE_SECKILL_INFO);
        info.setItemId(obj.optString("itemId"));
        info.setName(obj.optString("name"));
        info.setPrice(obj.optDouble("price"));
        info.setSalePrice(obj.optDouble("salePrice"));
        info.setIsFuture(obj.optBoolean("future"));
        info.setSoldNum(obj.optInt("itemSoldNum"));
        info.setStockPercent(obj.optDouble("soldRate"));
        info.setPicUrl(obj.optString("picUrl"));
        String time = obj.optString("startTime");
        if (null != time) {
            info.setStartTime(DateUtils.formatConvert(time));
        }
        info.setRemainingNum(obj.optInt("quantity"));

        return info;
    }

    public static GoodsInfo resolveResponse(JSONObject obj) {
        if (null == obj) {
            return null;
        }
        GoodsInfo info = new GoodsInfo();
        info.setShowStockPercent(obj.optBoolean("showStockPercent"));
        info.setSoldNum(obj.optInt("itemSoldNum"));
        info.setPicUrl(obj.optString("pic_url"));
        info.setRemainingNum(obj.optInt("remaining_stock"));
        info.setSeckillId(obj.optString("id"));
        info.setIsFuture(obj.optBoolean("future"));
        info.setName(obj.optString("name"));
        info.setSalePrice(obj.optDouble("sale_price"));
        info.setType(obj.optInt("itemType"));
        info.setItemId(obj.optString("itemId"));
        info.setUrl(obj.optString("url"));
        info.setStockPercent(100 - obj.optDouble("stock_percent"));
        info.setPrice(obj.optDouble("price"));
        info.setRemindTime(obj.optLong("remindTime"));
        info.setEndTime(obj.optString("end_time"));
        info.setItemViewerNum(obj.optInt("itemViewerNum"));
        info.setStartTime(obj.optString("start_time"));
        if (100 == info.getStockPercent()) {
            /** 读取相关数据 */
            JSONArray array = obj.optJSONArray("saleMessage");
            if (null != array && array.length() >= 2) {
                info.setSoldOutInfo(array.optString(0) + array.optString(1));
            }
        } else {
            info.setSoldOutInfo(obj.optString("sold_out_info"));
        }

        return info;
    }
}

