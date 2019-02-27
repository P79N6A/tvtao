package com.tvlife.imageloader.core.assist;


public enum DrawableScaleType {
    NONE(0), 
    FIT_XY(1), 
    FIT_SUITABLE(2),
    /*图片不放大，根据VIEW的大小进行裁剪图片，并且左上角和右上角是圆角*/
    FIT_NONE_ROUND_LT_RT(3);

    DrawableScaleType(int ni) {
        nativeInt = ni;
    } 
    final int nativeInt; 
}
