package com.yunos.tvtaobao.juhuasuan.pingpaituan;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.juhuasuan.classification.ClassiFicationValuesDimen;
import com.yunos.tvtaobao.juhuasuan.classification.VisualMarkConfig;
import com.yunos.tvtaobao.juhuasuan.util.MeasureTextUtil;

import java.text.NumberFormat;

public class PingPaiTuanImageHandleUint {

    private static String TAG = "PingPaiTuanImageHandleUint";
    private static Paint mPaint_Red = null;
    private static Paint mPaint_White = null;
    private static Bitmap mInfoBackGroudBitmap = null;

    // 默认的图片
    public static Bitmap mDefaultBitmap = null;
    public static Bitmap mRecycleBitmap = null;
    private static Object mLockerObject = new Object();

    public static void recycle() {
        synchronized (mLockerObject) {
            mPaint_Red = null;
            mPaint_White = null;
            mInfoBackGroudBitmap = null;
            mDefaultBitmap = null;
            mRecycleBitmap = null;
        }
    }

    /**
     * 初始化图片处理类的资源
     */
    public static void onInitPaint() {
        synchronized (mLockerObject) {
            mPaint_White = new Paint();
            mPaint_White.setColor(Color.WHITE);
            mPaint_White.setStyle(Paint.Style.FILL);
            mPaint_White.setStrokeJoin(Paint.Join.ROUND);
            mPaint_White.setStrokeCap(Paint.Cap.ROUND);
            mPaint_White.setDither(true);
            mPaint_White.setStrokeWidth(ClassiFicationValuesDimen.Dimen_1);
            mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_15);
            mPaint_White.setAntiAlias(true);
            mPaint_White.setFakeBoldText(false);
            mPaint_White.setSubpixelText(true);

            mPaint_Red = new Paint();
            mPaint_Red.setColor(VisualMarkConfig.DISPALY_COLOR_RED);
            mPaint_Red.setStyle(Paint.Style.FILL);
            mPaint_Red.setStrokeJoin(Paint.Join.ROUND);
            mPaint_Red.setStrokeCap(Paint.Cap.ROUND);
            mPaint_Red.setDither(true);
            mPaint_Red.setStrokeWidth(ClassiFicationValuesDimen.Dimen_1);
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_15);
            mPaint_Red.setAntiAlias(true);
            mPaint_Red.setFakeBoldText(false);
            mPaint_Red.setSubpixelText(true);

            mInfoBackGroudBitmap = BitmapFactory.decodeResource(CoreApplication.getApplication().getResources(),
                    R.drawable.jhs_pingpaituan_discount);

            //为释放图片使用
            mRecycleBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);

            //默认图片
            mDefaultBitmap = PingPaiTuanImageHandleUint.onHandleDisplayBitmap(BitmapFactory.decodeResource(CoreApplication
                    .getApplication().getResources(), R.drawable.jhs_item_default_image), PingPaiDimension.ITEM_WIDTH,
                    PingPaiDimension.ITEM_HEIGHT, true);
        }
    }

    /**
     * 释放图片
     * @param bm
     */
    private static void onRecycle(Bitmap bm) {

        if ((bm != null) && (!bm.isRecycled())) {
            bm.recycle();
            bm = null;
        }

    }

    /**
     * 处理圆角
     * @param bitmap 原始图片
     * @param pixels 圆角值
     * @param recycle 是否释放原图
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels, boolean recycle) {

        if (bitmap == null)
            return null;

        AppDebug.i(TAG, "toRoundCorner  bitmap =  " + bitmap + " + pixels =  " + pixels);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (recycle) {
            onRecycle(bitmap);
        }

        return output;
    }

    /**
     * 处理点九图
     * @param c 画布
     * @param bmp 点九图
     * @param r1 图片显示区域
     */
    public static void drawNinepath(Canvas c, Bitmap bmp, Rect r1) {
        NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
        patch.draw(c, r1);
    }

    /**
     * 图片合成
     * @param mbitmap
     * @param dispalyWidth
     * @param dispalyHeight
     * @param Ninepath
     * @return
     */
    private static Bitmap onHandleDisplayBitmap(Bitmap mbitmap, final int dispalyWidth, final int dispalyHeight,
                                                final boolean Ninepath) {
        if (mbitmap == null)
            return null;

        if (mbitmap.isRecycled()) {
            AppDebug.e(TAG, "onHandleDisplayBitmap --->  mbitmap.isRecycled, mbitmap = " + mbitmap);
            return null;
        }

        Bitmap displayBitmap = Bitmap.createBitmap(dispalyWidth, dispalyHeight, Bitmap.Config.RGB_565);
        Canvas mCanvas = new Canvas(displayBitmap);
        mCanvas.drawColor(Color.WHITE);

        Rect dst = new Rect();
        dst.set(0, 0, dispalyWidth, dispalyHeight);

        if (Ninepath) {
            drawNinepath(mCanvas, mbitmap, dst);
        } else {
            mCanvas.drawBitmap(mbitmap, 0, 0, null);
        }

        Bitmap resultBitmap = toRoundCorner(displayBitmap, PingPaiDimension.ROUNDED, true);
        return resultBitmap;
    }

    /**
     * 合成最终的图片， 包括信息图片跟商品图片
     * @param mbitmap
     * @param dispalyWidth
     * @param dispalyHeight
     * @param infoWidth
     * @param infoHeight
     * @param itemData
     * @return
     */
    public static Bitmap onHandleDisplayBitmap(Bitmap mbitmap, final int dispalyWidth, final int dispalyHeight,
                                               final int infoWidth, final int infoHeight, final BrandMO itemData) {
        if (mbitmap == null)
            return null;

        if (mbitmap.isRecycled()) {
            AppDebug.e(TAG, "onHandleDisplayBitmap -- > mbitmap.isRecycled, mbitmap = " + mbitmap);
            return null;
        }

        Bitmap displayBitmap = Bitmap.createBitmap(dispalyWidth, dispalyHeight, Bitmap.Config.RGB_565);
        Canvas mCanvas = new Canvas(displayBitmap);
        mCanvas.drawColor(Color.WHITE);

        //        Rect src = new Rect();
        //        src.set(0, 0, mbitmap.getWidth(), mbitmap.getHeight());
        //
        //        Rect dst = new Rect();
        //        dst.set(0, 0, PingPaiDimension.GOODS_WIDTH, dispalyHeight);
        //
        //        mCanvas.drawBitmap(mbitmap, src, dst, null);

        mCanvas.drawBitmap(mbitmap, 0, 0, null);

        Bitmap infobitmap = onHandleDisplayInfo(infoWidth, infoHeight, itemData);
        if (infobitmap != null) {
            int top = dispalyHeight - infoHeight;
            mCanvas.drawBitmap(infobitmap, 0, top, null);
            onRecycle(infobitmap);
        }

        // 备注： 由JuImageLoader下载器中的虚引用释放图片
        //        onRecycle(mbitmap);

        Bitmap resultBitmap = toRoundCorner(displayBitmap, PingPaiDimension.ROUNDED, true);
        return resultBitmap;
    }

    private final static String safeText(String origString) {
        if (origString == null)
            return "";
        else
            return origString;
    }

    public static Bitmap getmInfoBackGroudBitmap() {
        if (null == mInfoBackGroudBitmap || mInfoBackGroudBitmap.isRecycled()) {
            mInfoBackGroudBitmap = BitmapFactory.decodeResource(CoreApplication.getApplication().getResources(),
                    R.drawable.jhs_pingpaituan_discount);
        }
        return mInfoBackGroudBitmap;
    }

    /**
     * 根据 BrandModel 类中有用的信息，合成图片
     * @param infoWidth
     * @param infoHeight
     * @param itemData
     * @return
     */
    public static Bitmap onHandleDisplayInfo(final int infoWidth, final int infoHeight, final BrandMO itemData) {

        if (itemData == null) {
            return null;
        }
        synchronized (mLockerObject) {
            String mTitle = safeText(itemData.getJuSlogo());
            String mDiscount = safeText(itemData.getJuDiscount());

            double dis = Double.parseDouble(itemData.getJuDiscount());
            NumberFormat ddf1 = NumberFormat.getNumberInstance();
            ddf1.setMaximumFractionDigits(1);
            mDiscount = ddf1.format(dis);

            //           BigDecimal volumn = new BigDecimal("0");

            Bitmap displayBitmap = Bitmap.createBitmap(infoWidth, infoHeight, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(displayBitmap);
            mCanvas.drawColor(Color.TRANSPARENT);

            float x = 0;
            float y = 0;

            y = infoHeight - PingPaiDimension.INFOTABEL_BITMAP_HEIGHT;

            mCanvas.drawBitmap(getmInfoBackGroudBitmap(), x, y, null);
            AppDebug.i(TAG, TAG + ".onHandleDisplayInfo getmInfoBackGroudBitmap.size="
                    + getmInfoBackGroudBitmap().getHeight() + ", " + getmInfoBackGroudBitmap().getWidth());

            x = ClassiFicationValuesDimen.Dimen_14;
            y = infoHeight - ClassiFicationValuesDimen.Dimen_12;
            if (mPaint_White != null) {
                mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_24);
                mPaint_White.setFakeBoldText(false);

                mTitle = MeasureTextUtil.onCalulatStringLen(mTitle, ClassiFicationValuesDimen.Dimen_24, "...",
                        (int) (PingPaiDimension.GOODS_WIDTH - x));

                if (mTitle != null && mTitle.length() > 0)
                    mCanvas.drawText(mTitle, x, y, mPaint_White);
            }

            x = PingPaiDimension.LOGO_MARGIN_LEFT;
            y = infoHeight - ClassiFicationValuesDimen.Dimen_73;
//            if (mPaint_Red != null) {
//                mPaint_Red.setFakeBoldText(true);
//                mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_54);
//                mCanvas.drawText(mDiscount, x, y, mPaint_Red);
//                float left = mPaint_Red.measureText(mDiscount);
//
//                x += left;
//
//                mPaint_Red.setFakeBoldText(false);
//                mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_21);
//
//                mCanvas.drawText("折起", x, y, mPaint_Red);
//            }

            return displayBitmap;
        }

    }

}
