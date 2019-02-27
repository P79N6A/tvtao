package com.yunos.tvtaobao.juhuasuan.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.yunos.tv.core.CoreApplication;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 图片处理
 * @author hanqi
 */
public class ImageUtil {

    public static int[] allowSizes = { 20, 30, 40, 60, 70, 80, 90, 100, 110, 120, 130, 160, 170, 180, 190, 200, 210,
            220, 230, 240, 250, 270, 290, 300, 310, 320, 350, 360, 400, 430, 460, 480, 490, 540, 560, 570, 580, 600,
            640, 670, 720, 760 };
    public static Map<Integer, Integer> allowSizesMap = new HashMap<Integer, Integer>();
    public static String imageExt = ".jpg";//.jpg.webp

    static {
        for (int i = 0; i < allowSizes.length; i++) {
            allowSizesMap.put(allowSizes[i], allowSizes[i]);
        }
    }

    public static String getImageUrlExtraBySize(int resId) {
        int size = CoreApplication.getApplication().getResources().getDimensionPixelSize(resId);
        if (!allowSizesMap.containsValue(size)) {
            int tmpSize = 0;
            for (int i = 0; i < allowSizes.length; i++) {
                if (allowSizes[i] <= size && allowSizes[i] > tmpSize) {
                    tmpSize = allowSizes[i];
                } else if (allowSizes[i] > size) {
                    size = tmpSize;
                    break;
                }
            }
            if (size != tmpSize && tmpSize > 0) {
                size = tmpSize;
            }
        }
        return getImageUrlExtraBySize(size, size);
    }

    public static String getImageUrlExtraBySize(int width, int height) {
        return "_" + width + "x" + height + imageExt;
    }

    /**
     * Drawable 转 Bitmap
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        Bitmap bm;
        if (d instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) d;
            bm = bd.getBitmap();
        } else {
            bm = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(),
                    android.graphics.Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            d.draw(canvas);
        }
        return bm;
    }

    public static String Bitmap2Base64String(Bitmap bm) {
        return Base64.encode(Bitmap2Bytes(bm), Base64.DEFAULT).toString();
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (null == bm) {
            return baos.toByteArray();
        }
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Base64String2Bimap(String s) {
        if (null != s) {
            byte[] b = Base64.decode(s, Base64.DEFAULT);
            return Bytes2Bimap(b);
        }
        return null;
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }
}
