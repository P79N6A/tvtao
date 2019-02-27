/**
 * $
 * PROJECT NAME: BusinessView
 * PACKAGE NAME: com.yunos.tvtaobao.biz.qrcode
 * FILE NAME: QRCodeBitmap.java
 * CREATED TIME: 2015年3月23日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.payment.qrcode;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年3月23日 上午11:30:22
 */
public class QRCodeManager {

    /**
     * 生成二维码图片
     *
     * @param str
     * @param width
     * @param height
     * @param icon
     * @return
     * @throws WriterException
     */
    public static Bitmap create2DCode(String str, int width, int height, Bitmap icon) throws WriterException {


        return create2DCode(str, width, height, icon, 0, 0);
    }

    /**
     * 生成二维码图片
     *
     * @param str
     * @param width
     * @param height
     * @param icon
     * @return
     * @throws WriterException
     */
    public static Bitmap create2DCode(String str, int width, int height, Bitmap icon, int iconWidth, int iconHeight) throws WriterException {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败

        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height, hints);
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if (icon != null) {
            Canvas canvas = new Canvas(bitmap);
            int logoWidth = iconWidth <= 0 ? icon.getWidth() : iconWidth;
            int logoHeight = iconHeight <= 0 ? icon.getHeight() : iconHeight;

            Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
            Rect dst = new Rect((bitmap.getWidth() - logoWidth) / 2, (bitmap.getHeight() - logoHeight) / 2, (bitmap.getWidth() + logoWidth) / 2, (bitmap.getHeight() + logoHeight) / 2);
            canvas.drawBitmap(icon, src, dst, new Paint());
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }

    public static Bitmap create2DCode(Bitmap codeBm, int width, int height, Bitmap icon, int iconWidth, int iconHeight) {

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect srcRect = new Rect();
        srcRect.set(0, 0, codeBm.getWidth(), codeBm.getHeight());
        Rect distRect = new Rect();
        distRect.set(0, 0, width, height);
        canvas.drawBitmap(codeBm, srcRect, distRect, null);//draw qrcode bitmap
        if (icon != null) {//draw icon bitmap
            int logoWidth = iconWidth > 0 ? iconWidth : icon.getWidth();
            int logoHeight = iconHeight > 0 ? iconHeight : icon.getHeight();
            Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
            Rect dst = new Rect((bitmap.getWidth() - logoWidth) / 2, (bitmap.getHeight() - logoHeight) / 2, (bitmap.getWidth() + logoWidth) / 2, (bitmap.getHeight() + logoHeight) / 2);
            canvas.drawBitmap(icon, src, dst, new Paint());
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }

}
