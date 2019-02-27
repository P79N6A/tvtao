package com.tvtaobao.voicesdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

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
import com.tvtaobao.voicesdk.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 *     author : pan
 *     e-mail : panbeixing@zhiping.tech
 *     time   : 2017/11/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class QRCodeUtil {
    private static QRCodeUtil mUtil;

    private DisplayHandler myHandler;

    private Context mContext;

    public static QRCodeUtil getInstance(Context context) {
        if (mUtil == null)
            mUtil = new QRCodeUtil(context);

        return mUtil;
    }

    private QRCodeUtil(Context context) {
        mContext = context;
        if (myHandler == null)
            myHandler = new DisplayHandler();
    }

    private ImageView mQRCode;

    public void intData(String url, ImageView qrImage) {
        mUtil.mQRCode = qrImage;

        OkHttpClient okHttp = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputInstream = response.body().byteStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] buffer = new byte[1024];
                    while ((len = inputInstream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    inputInstream.close();
                    Message message = new Message();
                    message.what = 1;
                    message.obj = outStream;
                    mUtil.myHandler.sendMessage(message);

                }
            }
        });
    }

    private static class DisplayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ByteArrayOutputStream outStream = (ByteArrayOutputStream) msg.obj;
                    byte[] bmp_buffer = outStream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bmp_buffer, 0, bmp_buffer.length);
                    //替换png图片中透明的部分为白色。
                    bitmap = replaceBitmapColor(bitmap, Color.TRANSPARENT, Color.WHITE);
                    //解析二维码，取出uri
                    if (bitmap != null) {
                        try {
                            String uri = handleBitmap(bitmap);
                            Bitmap icon = BitmapFactory.decodeResource(mUtil.mContext.getResources(), R.drawable.icon_zhifubao);

                            Bitmap bitmap2 = create2DCode(uri, mUtil.mQRCode.getWidth(), mUtil.mQRCode.getHeight(), icon);

                            if (bitmap != null) {
                                mUtil.mQRCode.setImageBitmap(bitmap2);
                            } else {
                                mUtil.mQRCode.setImageBitmap(bitmap);
                            }

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }


                    }
            }
        }
    }

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

        if (str.isEmpty()) {
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
            icon = ThumbnailUtils.extractThumbnail(icon, bitmap.getWidth()/6, bitmap.getHeight()/6, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            canvas.drawBitmap(icon, (bitmap.getWidth() - icon.getWidth()) / 2,
                    (bitmap.getHeight() - icon.getHeight()) / 2, new Paint());
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return bitmap;
    }
}
