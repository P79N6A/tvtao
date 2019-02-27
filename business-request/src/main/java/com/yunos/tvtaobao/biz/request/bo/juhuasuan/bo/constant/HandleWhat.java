/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.bo.constant
 * FILE NAME: HandleWhat.java
 * CREATED TIME: 2014-11-17
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.constant;


/**
 * handler发送的信息中的what值
 * 命名规则：Activity+what
 * @version
 * @author hanqi
 * @data 2014-11-17 下午3:13:33
 */
public interface HandleWhat {

    /* ================商品列表页HomeActivity==================================================== */
    final int HOME_CHECK_ANIMATION_END = 0;
    /* ================详情页DetailActivity==================================================== */
    final int DETAIL_MSG_LEFT_SCROLL = 0;
    final int DETAIL_MSG_REFRESH_NORMAL = 1;
    final int DETAIL_MSG_REFRESH_IMAGE = 2;
    final int DETAIL_MSG_REFRESH_DETAIL = 3;
    final int DETAIL_MSG_REFRESH_GUIGE = 4;
    final int DETAIL_MSG_REFRESH_SHOP = 5;
    final int DETAIL_MSG_REFRESH_COMMENTS = 6;
    final int DETAIL_MSG_VIDEO_PAUSE = 7;
    /* ================GoodsNormalItemView==================================================== */
    final int ITEMVIEW_MSG_REFRESH_IMAGE = 1;
    final int ITEMVIEW_MSG_CREATEINFO_IMAGE = 2;
}
