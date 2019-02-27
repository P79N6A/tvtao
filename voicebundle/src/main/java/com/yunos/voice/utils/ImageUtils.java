package com.yunos.voice.utils;

/**
 * Created by chenjiajuan on 17/7/15.
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 图片处理工具类
 */


public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * 图片中色值替换
     *
     * @param oldColor
     * @param newColor
     * @return
     */
    public static Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        Bitmap mBitmap = oldBitmap.copy(Bitmap.Config.ARGB_8888, true);
        //循环获得bitmap所有像素点
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                //获得Bitmap 图片中每一个点的color颜色值
                //将需要填充的颜色值如果不是
                //在这说明一下 如果color 是全透明 或者全黑 返回值为 0
                //getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000
                //而不透明黑色是0xFF000000 如果不计算透明部分就都是0了
                int color = mBitmap.getPixel(j, i);
                //将颜色值存在一个数组中 方便后面修改
                if (color == oldColor) {
                    mBitmap.setPixel(j, i, newColor);
                }

            }
        }
        return mBitmap;
    }

    /**
     * 解析二维码
     *
     * @param bitmap
     */
    public static String handleBitmap(Bitmap bitmap) {
        Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
        // 解码设置编码方式为：utf-8
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        String uri = "";
        try {
            try {
                result = reader.decode(bitmap1, hints);
                uri = result.getText();
            } catch (ChecksumException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return uri;

    }

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

        if (TextUtils.isEmpty(str)) {
            return null;
        }

        if (width <= 0 || height <= 0) {
            return null;
        }

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
            icon = ThumbnailUtils.extractThumbnail(icon, bitmap.getWidth()/5, bitmap.getHeight()/5, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            canvas.drawBitmap(icon, (bitmap.getWidth() - icon.getWidth()) / 2,
                    (bitmap.getHeight() - icon.getHeight()) / 2, new Paint());
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }

}
