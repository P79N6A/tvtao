package com.yunos.tvtaobao.tvlive.tvtaomsg.enums;

/**
 * 通讯的枚举
 * Created by zhiping on 16/12/20.
 */
public enum BizTypeEnum {
    NOTIFY_LIVE_BLACKLIST("添加黑名单请求",1),
    SUBSCRIBE_LIVE_TOPIC("订阅直播",2),
    UNSUBSCRIBE_LIVE_TOPIC("取消订阅直播",3),
    NOTIFY_CNR_CHANGE_ITEM_ID("改变央广直播的商品id",4),
    NOTIFY_TVTAOBAO_UPDATE("通知更新电视淘宝", 98),
    NOTIFY_TVTAOBAO_PROMOTION("通知电视淘宝活动", 99),
    NOTIFY_CHANGE_ASR_CONFIG("改变ASR跳转配置", 101),
    NOTIFY_CHANFE_GLOBAL_CONFIG("改变全局配置", 102);

    private int    type;
    private String name;
    private String cnName;

    BizTypeEnum(String cnName, int type){
        this.name = this.name();
        this.cnName = cnName;
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
