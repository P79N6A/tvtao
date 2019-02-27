/**
 * $
 * PROJECT NAME: zhuanti
 * PACKAGE NAME: com.yunos.tv.zhuanti.bo.constant
 * FILE NAME: AnimationTime.java
 * CREATED TIME: 2014-9-4
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.zhuanti.bo.enumration;


/**
 * 动画时间
 * @version
 * @author hanqi
 * @data 2014-9-4 下午2:44:44
 */
public interface AnimationTime {

    // 共用，默认动画时间
    final long DURATION = 500;

    // TvShoping 
    final long SWITCH_VIEW = 450; // 切换View
    final long SWITCH_SCORE = 150; // 切换评分
    final long SWITCH_COMMENT = 150; // 切换评论
}
