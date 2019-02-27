package com.tvlife.imageloader.utils;

import android.graphics.Bitmap;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;

/**
 * Created by GuoLiDong on 2018/9/27.
 */

public class ClassicOptions {
    public static DisplayImageOptions dio565 = new DisplayImageOptions.Builder().cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true).cacheInMemory(false)
            .imageScaleType(ImageScaleType.EXACTLY).build();
}
