package com.yunos.tvtaobao.flashsale.utils;


import android.text.TextUtils;

import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.bo.RecommendInfo;

import java.util.Map;

public class TbsUtil {

    // 2001 进入页面事件
    public final static String PAGE_TaoQiangGou_Home = "TaoQiangGou_Home"; // 进入主页
    public final static String PAGE_Sell_Out = "Sell_Out"; // 进入关注页面

    // 2010 点击事件
    public final static String CLICK_My_Favorites = "My_Favorites"; // 点击进入我的关注
    public final static String CLICK_Remainder = "Remainder"; // 点击进入最后疯抢
    public final static String CLICK_left_home = "left_home"; // 关注提醒界面返回主页面
    public final static String CLICK_right_home = "right_home"; // 关注提醒界面返回主页面

    public final static String CLICK_TIME = "Time";
    public final static String CLICK_Home_P = "Home_P";
    public final static String CLICK_Favorites_P = "Favorites_P";
    public final static String CLICK_Remainder_P = "Remainder_P";
    public final static String CLICK_Recommend_P = "Recommend_P";

    public final static String CLICK_PopUp_Click = "PopUp_Click"; // 关注提醒界面进入关注界面

    // 19999 自定义事件
    public final static String CUSTOM_PopUp_Disappear = "PopUp_Disappear"; // 提醒框消失的方式
    public final static String CUSTOM_PopUp = "PopUp"; // 弹出消息提醒
    public final static String CUSTOM_PopUp_Click = "PopUp_Click"; //

    /**
     * 拼装埋点数据
     * @param pageName
     *            页面名
     * @param controlName
     *            点击名
     * @param position
     *            点击的位置
     * @param args
     *            key - value键值
     * @return
     */
    public static String getControlName(String pageName, String controlName, int position, String... args) {
        StringBuilder sb = new StringBuilder();
        boolean hasUnderline = false;

        if (!TextUtils.isEmpty(pageName)) {
            sb.append(pageName);
            hasUnderline = true;
        }
        if (!TextUtils.isEmpty(controlName)) {
            if (hasUnderline) {
                sb.append("_");
            } else {
                hasUnderline = true;
            }
            sb.append(controlName);
        }

        if (position >= 0) {
            if (hasUnderline) {
                sb.append("_");
            } else {
                hasUnderline = true;
            }
            if (position < 10) {
                sb.append(0);
            }
            sb.append(position);
        }

        if (null != args && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (hasUnderline) {
                    sb.append("_");
                } else {
                    hasUnderline = true;
                }
                if (!TextUtils.isEmpty(args[i])) {
                    sb.append(args[i]);
                }
            }
        }
        return sb.toString();
    }

    public static String[] getKeyValue(Map<String, String> p) {
        String[] kvs = new String[p.size()];
        int i = 0;
        for (Object key : p.keySet()) {
            kvs[i++] = key.toString() + "=" + p.get(key).toString();
        }
        return kvs;
    }

    public static Map<String, String> getGoodsInfoProperty(GoodsInfo info) {
        Map<String, String> prop = Utils.getProperties();

        /** 商品id */
        String itemId = info.getItemId();
        if (!TextUtils.isEmpty(itemId)) {
            prop.put("item_id", itemId);
        }
        /** 商品名称 */
        String name = info.getName();
        if (!TextUtils.isEmpty(name)) {
            prop.put("item_name", name);
        }
        /** 现价 */
        prop.put("now_prise", String.valueOf(info.getSalePrice()));
        /** 原价 */
        prop.put("original_price", String.valueOf(info.getPrice()));
        /** 秒杀/普通商品 */
        prop.put("class", String.valueOf(info.getType()));

        return prop;
    }

    public static Map<String, String> getTbsProperty(GoodsInfo info, String defaultTime, String nowTime,
                                                     String endTime, String status) {
        Map<String, String> prop = getGoodsInfoProperty(info);

        /** 默认进入的时间轴 */
        if (!TextUtils.isEmpty(defaultTime)) {
            prop.put("time", defaultTime);
        }
        /** 当前点击所处的时间轴 */
        if (!TextUtils.isEmpty(nowTime)) {
            prop.put("now_time", nowTime);
        }
        /** 距离结束时间 */
        if (!TextUtils.isEmpty(endTime)) {
            prop.put("end_time", endTime);
        }
        /** 该时间轴的状态 */
        if (!TextUtils.isEmpty(status)) {
            prop.put("status", status);
        }
        prop.put("bought_amount", String.valueOf(info.getSoldNum()));
        /** 已抢购百分比 */
        prop.put("rate", String.valueOf(info.getStockPercent()));
        return prop;
    }

    public static Map<String, String> getTbsProperty(RecommendInfo info) {
        Map<String, String> prop = Utils.getProperties();

        /** 商品id */
        String itemId = info.getItemId();
        if (!TextUtils.isEmpty(itemId)) {
            prop.put("item_id", itemId);
        }

        /** 商品名称 */
        String title = info.getTitle();
        if (!TextUtils.isEmpty(title)) {
            prop.put("item_name", title);
        }

        /** 现价 */
        prop.put("now_prise", String.valueOf(info.getSalePrice()));
        /** 原价 */
        prop.put("original_price", String.valueOf(info.getReservePrice()));

        prop.put("class", String.valueOf(GoodsInfo.ITEM_TYPE_STOCK_INFO));

        return prop;
    }

}
