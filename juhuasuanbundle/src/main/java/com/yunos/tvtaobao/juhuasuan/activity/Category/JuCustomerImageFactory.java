/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.activity.Category
 * FILE NAME: JuCustomerImageFactory.java
 * CREATED TIME: 2015-2-11
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;

import com.yunos.tvtaobao.juhuasuan.activity.Category.GoodsNormalItemView.GoodsNormalItemViewInfoRequestData;

/**
 * 聚划算
 * @version
 * @author hanqi
 * @data 2015-2-11 下午6:54:55
 */
public class JuCustomerImageFactory {

    public static Bitmap createGoodsNormalItemViewInfo(Context context, GoodsNormalItemViewInfoRequestData data) {
        if (null == context || null == data) {
            return null;
        }
        if (data.mWith <= 0 || data.mHeight < 0) {
            return null;
        }
        GoodsNormalItemViewInfo infoView = new GoodsNormalItemViewInfo(context, null);
        Bitmap bitmap = Bitmap.createBitmap(data.mWith, data.mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        infoView.setGoodsItemData(data.mItemData);
        infoView.measure(MeasureSpec.makeMeasureSpec(data.mWith, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(data.mHeight, MeasureSpec.EXACTLY));
        infoView.layout(0, 0, data.mWith, data.mHeight);
        infoView.draw(canvas);
        return bitmap;
    }
}
