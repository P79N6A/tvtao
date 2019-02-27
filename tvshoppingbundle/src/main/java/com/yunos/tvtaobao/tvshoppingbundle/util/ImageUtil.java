/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.common
 * FILE NAME: ImageUtil.java
 * CREATED TIME: 2015年12月22日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.tvshoppingbundle.util;


import android.text.TextUtils;

import java.util.Locale;

public class ImageUtil {

    /**
     * 获取固定大小图片的链接
     * @param srcUrl
     * @param width
     * @param height
     * @return
     */
    public static String getFixSizeImage(String srcUrl, int width, int height) {
        if (!TextUtils.isEmpty(srcUrl) && width > 0 && height > 0) {
            int lastIndex = srcUrl.lastIndexOf(".");
            if (lastIndex == -1) {
                return null;
            }

            return srcUrl + "@" + width + "w_" + height + (srcUrl.substring(lastIndex)).toLowerCase(Locale.US);
        }
        return null;
    }
}
