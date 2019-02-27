/**
 * $
 * PROJECT NAME: TopicBuy
 * PACKAGE NAME: com.yunos.tv.topicbuy.activity.huabao
 * FILE NAME: FixedSpeedScroller.java
 * CREATED TIME: 2014年8月28日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年8月28日 下午5:45:41
 */
public class FixedSpeedScroller extends Scroller {

    private int mDuration = 500;

    /**
     * @param context
     */
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // TODO Auto-generated method stub
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // TODO Auto-generated method stub
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public int getmDuration() {
        return mDuration;
    }

}
