package com.yunos.tvtaobao.tvlive.utils;

/**
 * Created by xihua
 * On 14:29 6/16/16.
 * For
 */
public class PowerMsgType {
    public static final int systemMsg=10001; //系统消息
    public static final int studioMsg=10002; //新增系统消息
    public static final int shareMsg=10003; //宝贝消息
    public static final int bizMsg=10004; //红包消息
    public static final int joinMsg=10005; //进出群消息
    public static final int statistics=10006; //统计信息
    public static final int mergeMsg =10007; //需要合并丢弃的消息
    public static final int shareGoodsListMsg = 10008; //新版发宝贝消息
    public static final int closeGoodsShowCaseMsg = 10009; //关闭宝贝橱窗消息
    public static final int tradeShowMsg = 10010; //加购消息
    public static final int linkLiveMsg = 10021;  //连麦消息
    public static final int linkLiveGameMsg = 10022;   //连连看消息
    public static final int playErrorMsg = 10098;
    public static final int h265Msg = 10099;

    //2开头的是手淘、天猫互通消息
    public static final int noticeMsg = 20002;  //公告消息
    public static final int barrageMsg = 20003; //弹幕消息
    public static final int biffMsg = 20004; //暴击消息
    public static final int nineGridMsg = 20005;    //9宫格消息

    //tvtaobao自定义
    public static final String KEY_FAVOR = "dig";
    public static final int LIVE_GIFT = 1001;//礼物消息
    public static final int LIVE_STREAM_BREAK = 1002;//liveVideoStreamBreak直播流断开
    public static final int LIVE_STREAM_RESTORE = 1003;//liveVideoStreamRestore直播流恢复
    public static final int LIVE_STREAM_END = 1004;//直播结束
    public static final int LIVE_ATTENTION = 1005;//关注
    public static final int LIVE_COMMENT_NOTIFY = 1006;
    public static final int LIVE_CHANGE_ONLINE_HEAD_MSG = 1007;
    public static final int LIVE_COMMENT_UP_NOTIFY = 1008;
    public static final int LIVE_PRAISE_SEND = 1009; //点赞上传
    public static final int LIVE_ERROR = 1010;
    public static final int LIVE_LOADING_NOTIFY = 1011;
}
