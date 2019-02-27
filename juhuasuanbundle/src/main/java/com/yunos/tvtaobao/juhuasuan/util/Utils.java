/*
 * Copyright (C) 2012 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yunos.tvtaobao.juhuasuan.util;


import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.view.View;

import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;


/**
 * Class containing some static utility methods.
 */
public class Utils {

    private Utils() {
    };

    public static Point getPoint(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        view.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        Point point = new Point(location[0], location[1]);
        return point;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * 取得商品加载图片URl
     * @param itemMO
     * @return
     */
    public static String getGoodsImageUrl(ItemMO itemMO) {
        String itemImageUrl;
        if (itemMO.getPicWideUrl() != null && itemMO.getPicWideUrl().length() > 0) {
            itemImageUrl = SystemUtil.mergeImageUrl(itemMO.getPicWideUrl()) + "_540x540.jpg";
        } else {
            itemImageUrl = SystemUtil.mergeImageUrl(itemMO.getPicUrl()) + "_540x540.jpg";
        }
        return itemImageUrl;
    }

    public static final String INTENT_KEY_INNER = "frominner";

    /**
     * 设置内部调用url的参数
     * @param intent
     */
    public static void setInnerActivityIntent(Intent intent) {
        if (intent != null) {
            intent.putExtra(INTENT_KEY_INNER, true);
        }
    }
    /**
     * 取得商品加载图片URl
     * @param itemMO
     * @return
     */
    // public static String getJuCategoryImageUrl(CategoryMO itemMO) {
    // String itemImageUrl;
    // if (itemMO.g() != null && itemMO.getPicWideUrl().length() > 0) {
    // itemImageUrl = SystemUtil.mergeImageUrl(itemMO.getPicWideUrl())
    // + "_540x540.jpg";
    // } else {
    // itemImageUrl = SystemUtil.mergeImageUrl(itemMO.getImgUrl())
    // + "_540x540.jpg";
    // }
    // return itemImageUrl;
    //
    // }
}
