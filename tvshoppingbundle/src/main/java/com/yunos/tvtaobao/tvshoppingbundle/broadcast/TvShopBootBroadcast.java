/**
 * $
 * PROJECT NAME: HomeActivity
 * PACKAGE NAME: com.yunos.tvshopping.broadcast
 * FILE NAME: TvShopBootBroadcast.java
 * CREATED TIME: 2015年1月4日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tvshoppingbundle.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tvshoppingbundle.service.TvShopService;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年1月4日 下午3:07:25
 */
public class TvShopBootBroadcast extends BroadcastReceiver {

    private String TAG = "TvShopBootBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            AppDebug.v(TAG, TAG + ".onReceive package = " + intent.getPackage() + ".action = " + action);
            intent.setClass(context, TvShopService.class);
            context.startService(intent);
    }
}
