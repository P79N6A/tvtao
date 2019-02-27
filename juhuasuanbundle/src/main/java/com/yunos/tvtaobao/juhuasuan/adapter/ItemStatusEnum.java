package com.yunos.tvtaobao.juhuasuan.adapter;


import java.util.HashMap;
import java.util.Map;

/**
 * 商品状态
 * 
 * @author hanqi
 */
public enum ItemStatusEnum {
    WAIT_FOR_START(0, "即将开始"), AVAIL_BUY(1, "可购买"), EXIST_HOLDER(2, "有占座"), NO_STOCK(3, "卖完了"), OUT_OF_TIME(4, "已结束");

    private static Map<Integer, ItemStatusEnum> pool = new HashMap<Integer, ItemStatusEnum>();
    static {
        for (ItemStatusEnum each : ItemStatusEnum.values()) {
            pool.put(each.value, each);
        }
    }

    private int value;
    private String desc;
    private Integer drawableId;

    private ItemStatusEnum(int value, String desc, Integer drawableId) {
        this.value = value;
        this.desc = desc;
        this.drawableId = drawableId;
    }

    private ItemStatusEnum(int value, String desc) {
        this(value, desc, null);
    }

    public static ItemStatusEnum index(int value) {
        return pool.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getDrawableId() {
        return drawableId;
    }

}
