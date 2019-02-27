package com.yunos.tvtaobao.juhuasuan.util;


import android.content.Context;

import com.yunos.tvtaobao.juhuasuan.R;

public class JuApiUtils {

    public enum ItemMoStateEnum {
        WAIT_FOR_START, AVAIL_BUY, EXIST_HOLDER, NO_STOCK, OUT_OF_TIME
    };

    /**
     * 是否未开始
     * @return
     */
    public static boolean isNotStart(int itemStatus) {
        return itemStatus == ItemMoStateEnum.WAIT_FOR_START.ordinal();
    }

    /**
     * 是否已结束
     * @return
     */
    public static boolean isOver(int itemStatus) {
        return itemStatus == ItemMoStateEnum.OUT_OF_TIME.ordinal();
    }

    /**
     * 是否卖完了
     * @return
     */
    public static boolean isNoStock(int itemStatus) {
        return itemStatus == ItemMoStateEnum.NO_STOCK.ordinal() || itemStatus == ItemMoStateEnum.EXIST_HOLDER.ordinal();
    }

    /**
     * 是否可购买
     * @return
     */
    public static boolean isAbleBuy(int itemStatus) {
        return itemStatus == ItemMoStateEnum.AVAIL_BUY.ordinal();
    }

    /**
     * 显示服务器错误信息
     * @param context
     */
    public static void showServerError(Context context) {
        DialogUtils.show(context, R.string.jhs_server_error);
    }
}
