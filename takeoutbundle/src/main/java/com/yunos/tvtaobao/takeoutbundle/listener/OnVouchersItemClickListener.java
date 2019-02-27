package com.yunos.tvtaobao.takeoutbundle.listener;

import android.view.View;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/19
 * @Desc：领代金券点击事件
 */
public interface OnVouchersItemClickListener {
    /**
     * @param isTaken 是否已领取
     * @param activityId   券活动 id
     * @param exchangeType 兑换类型 -1:年终奖 0:免费 1:权益 2:红包 3:奖励金
     * @param storeIdType  storeId 类型，0 为淘宝店铺id（默认），1 为饿了么中后台店铺id）
     * */
    void onClick(View view,boolean isTaken,String activityId, String exchangeType, String storeIdType);
}
