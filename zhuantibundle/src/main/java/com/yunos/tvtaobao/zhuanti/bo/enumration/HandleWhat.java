package com.yunos.tvtaobao.zhuanti.bo.enumration;


/**
 * handler发送的信息中的what值
 * 命名规则：Activity+what
 * @author hanqi
 * @date 2014-6-24
 */
public interface HandleWhat {

    /* TvShoppingActivity */
    final int SWITCH_SCORE = 0;
    final int SWITCH_COMMENT = 1;
    final int SWITCH_VIEW = 2;
    final int PLAY_STATUS_SET = 3;
    // 模拟发送按键事件
    final int SEND_KEYEVENT = 4;
    // 播放摸个地址的视频
    final int VIDEO_PLAY_URL = 5;
    //视频开始播放
    final int VIDEO_AFTER_PALY = 6;
    //视频播放检测
    final int VIDEO_PALY_CHECK = 7;
    
    final int WHAT_HIDE_LISTVIEW = 8;
    final int WHAT_HIDE_TISHITVIEW = 9;
    
    final int WHAT_SHOW_DETAIL_DELAY = 10;
    

    /* huabao相关 */
    final int TEXT_IMAGE_LOAD_COMPLETE = 50;
    final int TEXT_IMAGE_DOWNLOAD_FORM_NETWORK = 51;
    final int MAIN_IMAGE_LOAD_COMPLETE = 52;
    final int MAIN_IMAGE_DOWNLOAD_FORM_NETWORK = 53;
    final int DETAIL_IMAGE_LOAD_COMPLETE = 54;
    final int DETAIL_IMAGE_DOWNLOAD_FORM_NETWORK = 55;
    final int TEXT_IMAGE_ANIMATION = 56;
    final int SHOW_PROGRESSBAR_MSG = 57;

    /* PreviousActivity */
    final int MSG_GO_TO_HUABAO_ACTIVITY = 100;
    final int MSG_GO_TO_TV_SHOPPING_ACTIVITY = 101;
    final int MSG_GO_TO_NEW_TV_SHOPPING_ACTIVITY = 108;
    final int MSG_GO_TO_OLD_TV_SHOPPING_ACTIVITY = 109;
    final int MSG_GO_TO_TEJIA_ACTIVITY = 106;
    final int MSG_GO_TO_TIANTIAN_ACTIVITY = 102;
    final int MSG_GO_TO_QINGCANG_ACTIVITY = 103;
    final int MSG_GO_TO_GOODS_SHOPPING_ACTIVITY = 104;
    final int MSG_GO_TO_TVCOMMEND_SINGLE_ACTIVITY = 105;
    final int MSG_GO_TO_FENLEI_ACTIVITY = 107;
}
