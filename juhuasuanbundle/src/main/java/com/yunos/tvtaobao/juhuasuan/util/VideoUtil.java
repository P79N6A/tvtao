/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.util
 * FILE NAME: VideoUtil.java
 * CREATED TIME: 2014-12-8
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.util;


import android.view.Surface;

import java.lang.reflect.Field;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2014-12-8 下午12:05:30
 */
public class VideoUtil {

    /**
     * videoview在4.2.2中存在内存泄露的问题
     * @param clazz
     * @param object videoview
     * @param surfaceFieldName
     */
    public static void forceReleaseSurface(Object object) {
        Class<?> clazz = object.getClass().getSuperclass();
        if (null == clazz) {
            return;
        }
        try {
            Field field = clazz.getDeclaredField("mSurface");

            field.setAccessible(true);
            Surface surface = (Surface) field.get(object);

            if (surface != null) {
                surface.release();
            }
        } catch (Exception e) {
        }
        try {
            Field field = clazz.getDeclaredField("mNewSurface");

            field.setAccessible(true);
            Surface surface = (Surface) field.get(object);

            if (surface != null) {
                surface.release();
            }
        } catch (Exception e) {
        }
    }
}
