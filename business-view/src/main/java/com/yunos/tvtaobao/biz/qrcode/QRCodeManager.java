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
package com.yunos.tvtaobao.biz.qrcode;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年3月23日 上午11:30:22
 */
public class QRCodeManager {

    /**
     * 生成二维码图片
     * @param str
     * @param width
     * @param height
     * @param icon
     * @return
     * @throws WriterException
     */
    public static Bitmap create2DCode(String str, int width, int height, Bitmap icon) throws WriterException {

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
            canvas.drawBitmap(icon, (bitmap.getWidth() - icon.getWidth()) / 2,
                    (bitmap.getHeight() - icon.getHeight()) / 2, new Paint());
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }

}
