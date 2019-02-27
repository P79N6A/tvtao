package com.yunos.tvtaobao.juhuasuan.classification;


import android.content.Context;
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
import android.graphics.Typeface;

import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tv.core.common.AppDebug;

public class ImageHandleUnit {

    private static String TAG = "ImageHandleUnit";

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

    public static void drawNinepath(Canvas c, Bitmap bmp, Rect r1) {
        NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
        patch.draw(c, r1);
    }

    public static class ClassficationImageHandle {

        private Paint mPaint_Red = null;
        private Paint mPaint_BLACK = null;
        private Paint mPaint_Green = null;
        private Paint mPaint_White = null;

        public Paint mPaint_White_Frame = null;

        private Typeface mFont = null;

        private Context mContext = null;

        private Bitmap mDiscountBitmap_Big = null;
        private Bitmap mDiscountBitmap_Small = null;

        private Bitmap mInfoBackGroudBitmap = null;

        private Bitmap mFrameBackGroudBitmap = null;

        private String TAG = "ClassficationImageHandle";

        private int bound = ClassiFicationValuesDimen.Dimen_10;

        //        private   Bitmap onCreateTabel_Big(final int dispalyWidth, final int dispalyHeight, final Distabel_Info mDistabel_Info) {
        //
        //            String discountpriceStrOfNatural = mDistabel_Info.discountpriceStrOfNatural;
        //            String discountpriceStrOfFractional = mDistabel_Info.discountpriceStrOfFractional;
        //            
        //            String soldStr = mDistabel_Info.soldStr;
        //            String discountStr = mDistabel_Info.discountStr;
        //            String originalPriceStr = mDistabel_Info.originalPriceStr;
        //
        //            int textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_BIG;
        //            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.RGB_565);
        //            Canvas canvas = new Canvas(bitmap);
        //            canvas.drawColor(VisualMarkConfig.DISPALY_TABEL_COLOR);
        //
        //            float x = ClassiFicationValuesDimen.Dimen_16;
        //            float y = textTabHeight - ClassiFicationValuesDimen.Dimen_16;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_24);
        //            canvas.drawText("￥", x, y, mPaint_Red);
        //
        //            x = ClassiFicationValuesDimen.Dimen_35;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_16;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_42);
        //            canvas.drawText(discountpriceStrOfNatural, x, y, mPaint_Red);
        //
        //            x = x + mPaint_Red.measureText(discountpriceStrOfNatural);
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_16;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_24);
        //            canvas.drawText(discountpriceStrOfFractional, x, y, mPaint_Red);
        //
        //            // 显示折扣图片
        //            x = ClassiFicationValuesDimen.Dimen_285;
        //            y = ClassiFicationValuesDimen.Dimen_12;
        //            canvas.drawBitmap(mDiscountBitmap_Big, x, y, null);
        //
        //            // 显示折扣率
        //            x += ClassiFicationValuesDimen.Dimen_10;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_32;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_21);
        //            canvas.drawText(discountStr, x, y, mPaint_Red);
        //
        //            x = x + mPaint_Red.measureText(discountStr);
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_12);
        //            canvas.drawText("折", x, y, mPaint_Red);
        //
        //            String mXPrice = "￥" + originalPriceStr;
        //            x = ClassiFicationValuesDimen.Dimen_285;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_12;
        //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_14);
        //            canvas.drawText(mXPrice, x, y, mPaint_BLACK);
        //
        //            // 在原始价格上加上删除线
        //            int xPriceWidth = (int) mPaint_BLACK.measureText(mXPrice);
        //            y = y - ClassiFicationValuesDimen.Dimen_4;
        //            canvas.drawLine(x, y, x + xPriceWidth + ClassiFicationValuesDimen.Dimen_4, y, mPaint_BLACK);
        //   
        //            
        //           // 显示购买人数
        //            x = dispalyWidth;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_35;
        //            mPaint_Green.setTextSize(ClassiFicationValuesDimen.Dimen_22);
        //
        //            int mSoldWidth = (int) mPaint_Green.measureText(soldStr);
        //             x = x - mSoldWidth - bound;
        //            canvas.drawText(soldStr, x, y, mPaint_Green);
        //
        //            // 显示文字"人已买"
        //            String RenHasSale = "人已买";
        //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_16);
        //            int mRenHasSaleWidth = (int) mPaint_BLACK.measureText(RenHasSale);
        //            x = dispalyWidth - mRenHasSaleWidth - bound;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_15;
        //            canvas.drawText(RenHasSale, x, y, mPaint_BLACK);
        //
        //            return bitmap;
        //
        //        }
        //
        //        private   Bitmap onCreateTabel_Small(final int dispalyWidth, final int dispalyHeight, final Distabel_Info mDistabel_Info) {
        //
        //            
        //            String discountpriceStrOfNatural = mDistabel_Info.discountpriceStrOfNatural;
        //            String discountpriceStrOfFractional = mDistabel_Info.discountpriceStrOfFractional;
        //            
        //            
        //            String soldStr = mDistabel_Info.soldStr;
        //            String discountStr = mDistabel_Info.discountStr;
        //            String originalPriceStr = mDistabel_Info.originalPriceStr;
        //
        //            int textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_SMALL;
        //            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.RGB_565);
        //            Canvas canvas = new Canvas(bitmap);
        //            canvas.drawColor(VisualMarkConfig.DISPALY_TABEL_COLOR);
        //
        //            float x = ClassiFicationValuesDimen.Dimen_4;
        //            float y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_16);
        //            canvas.drawText("￥", x, y, mPaint_Red);
        //
        //            x = ClassiFicationValuesDimen.Dimen_21;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_28);
        //            canvas.drawText(discountpriceStrOfNatural, x, y, mPaint_Red);
        //
        //            x = x + mPaint_Red.measureText(discountpriceStrOfNatural);
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_16);
        //            canvas.drawText(discountpriceStrOfFractional, x, y, mPaint_Red);
        //
        //            // 显示折扣图片
        //            x = ClassiFicationValuesDimen.Dimen_188;
        //            y = ClassiFicationValuesDimen.Dimen_8;
        //            canvas.drawBitmap(mDiscountBitmap_Small, x, y, null);
        //
        //            // 显示折扣率
        //            x += ClassiFicationValuesDimen.Dimen_4;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_24;
        //            
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_15);
        //            canvas.drawText(discountStr, x, y, mPaint_Red);
        //
        //            x = x + mPaint_Red.measureText(discountStr);
        //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_8);
        //            canvas.drawText("折", x, y, mPaint_Red);
        //
        //            String mXPrice = "￥" + originalPriceStr;
        //            x = ClassiFicationValuesDimen.Dimen_188;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
        //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_12);
        //            canvas.drawText(mXPrice, x, y, mPaint_BLACK);
        //
        //            // 在原始价格上加上删除线
        //            int xPriceWidth = (int) mPaint_BLACK.measureText(mXPrice);
        //            y = y  - ClassiFicationValuesDimen.Dimen_4;
        //            canvas.drawLine(x, y, x + xPriceWidth + ClassiFicationValuesDimen.Dimen_4, y, mPaint_BLACK);
        //
        //            // 显示购买人数
        //            x = dispalyWidth;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_22;
        //            mPaint_Green.setTextSize(ClassiFicationValuesDimen.Dimen_14);
        //
        //            int mSoldWidth = (int) mPaint_Green.measureText(soldStr);
        //            x = x - mSoldWidth - bound;
        //            canvas.drawText(soldStr, x, y, mPaint_Green);
        //
        //            // 显示文字"人已买"
        //            String RenHasSale = "人已买";
        //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_12);
        //            int mRenHasSaleWidth = (int) mPaint_BLACK.measureText(RenHasSale);
        //            x = dispalyWidth - mRenHasSaleWidth - bound;
        //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
        //            canvas.drawText(RenHasSale, x, y, mPaint_BLACK);
        //
        //            return bitmap;
        //
        //        }

        public Bitmap onCreateTabel_Big(final int dispalyWidth, final int dispalyHeight,
                                        final Distabel_Info mDistabel_Info) {

            String discountpriceStrOfNatural = mDistabel_Info.discountpriceStrOfNatural;
            String discountpriceStrOfFractional = mDistabel_Info.discountpriceStrOfFractional;

            String soldStr = mDistabel_Info.soldStr;
            String discountStr = mDistabel_Info.discountStr;
            String originalPriceStr = mDistabel_Info.originalPriceStr;

            int textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_BIG;

            //            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.ARGB_8888);
            //            Canvas canvas = new Canvas(bitmap);
            //            canvas.drawColor(Color.TRANSPARENT);

            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);

            Rect rt = new Rect(0, 0, dispalyWidth, textTabHeight);
            drawNinepath(canvas, mInfoBackGroudBitmap, rt);

            float optionLeft = 0;

            float x = ClassiFicationValuesDimen.Dimen_15;
            float y = textTabHeight - ClassiFicationValuesDimen.Dimen_12;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_14);
            mPaint_Red.setFakeBoldText(false);
            String moneyUnit = mContext.getResources().getString(R.string.jhs_dollar_sign);
            canvas.drawText(moneyUnit, x, y, mPaint_Red);

            float left = mPaint_Red.measureText(moneyUnit);

            optionLeft = x;
            optionLeft += left;

            mPaint_Red.setTextSkewX(-0.2f);
            mPaint_Red.setFakeBoldText(true);
            x = x + left;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_12;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_35);
            canvas.drawText(discountpriceStrOfNatural, x, y, mPaint_Red);

            optionLeft += mPaint_Red.measureText(discountpriceStrOfNatural);
            mPaint_Red.setTextSkewX(0.0f);

            x = x + mPaint_Red.measureText(discountpriceStrOfNatural);
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_10;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            canvas.drawText(discountpriceStrOfFractional, x, y, mPaint_Red);

            optionLeft += mPaint_Red.measureText(discountpriceStrOfFractional);

            // 显示折扣图片
            x = ClassiFicationValuesDimen.Dimen_12;
            y = 0;
            canvas.drawBitmap(mDiscountBitmap_Big, x, y, null);

            // 显示折扣率
            x = ClassiFicationValuesDimen.Dimen_16;
            y = textTabHeight - 62;
            mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            mPaint_White.setFakeBoldText(true);
            mPaint_White.setTextSkewX(-0.5f);
            canvas.drawText(discountStr, x, y, mPaint_White);

            mPaint_White.setTextSkewX(0.0f);
            x = x + mPaint_White.measureText(discountStr) + ClassiFicationValuesDimen.Dimen_8;
            mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            canvas.drawText(mContext.getResources().getString(R.string.jhs_discount_unit), x, y, mPaint_White);

            String mXPrice = moneyUnit + originalPriceStr;
            x = optionLeft;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_10;
            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_14);
            canvas.drawText(mXPrice, x, y, mPaint_BLACK);

            // 在原始价格上加上删除线
            int xPriceWidth = (int) mPaint_BLACK.measureText(mXPrice);
            y = y - ClassiFicationValuesDimen.Dimen_4;
            canvas.drawLine(x, y, x + xPriceWidth + ClassiFicationValuesDimen.Dimen_4, y, mPaint_BLACK);

            String RenHasSale = mContext.getResources().getString(R.string.jhs_detail_sold_desc) + soldStr;
            mPaint_Red.setTextSize(18);
            mPaint_Red.setFakeBoldText(false);
            int mRenHasSaleWidth = (int) mPaint_Red.measureText(RenHasSale);
            x = dispalyWidth - mRenHasSaleWidth - bound;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_15;
            canvas.drawText(RenHasSale, x, y, mPaint_Red);

            return bitmap;

        }

        public Bitmap onCreateTabel_Small(final int dispalyWidth, final int dispalyHeight,
                                          final Distabel_Info mDistabel_Info) {

            String discountpriceStrOfNatural = mDistabel_Info.discountpriceStrOfNatural;
            String discountpriceStrOfFractional = mDistabel_Info.discountpriceStrOfFractional;

            String soldStr = mDistabel_Info.soldStr;
            String discountStr = mDistabel_Info.discountStr;
            String originalPriceStr = mDistabel_Info.originalPriceStr;

            int textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_SMALL;

            //            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.RGB_565);
            //            Canvas canvas = new Canvas(bitmap);
            //            canvas.drawColor(VisualMarkConfig.DISPALY_TABEL_COLOR);
            //
            //            float x = ClassiFicationValuesDimen.Dimen_4;
            //            float y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
            //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_16);
            //            canvas.drawText("￥", x, y, mPaint_Red);
            //
            //            x = ClassiFicationValuesDimen.Dimen_21;
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
            //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_28);
            //            canvas.drawText(discountpriceStrOfNatural, x, y, mPaint_Red);
            //
            //            x = x + mPaint_Red.measureText(discountpriceStrOfNatural);
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_4;
            //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_16);
            //            canvas.drawText(discountpriceStrOfFractional, x, y, mPaint_Red);
            //
            //            // 显示折扣图片
            //            x = ClassiFicationValuesDimen.Dimen_188;
            //            y = ClassiFicationValuesDimen.Dimen_8;
            //            canvas.drawBitmap(mDiscountBitmap_Small, x, y, null);
            //
            //            // 显示折扣率
            //            x += ClassiFicationValuesDimen.Dimen_4;
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_24;
            //
            //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_15);
            //            canvas.drawText(discountStr, x, y, mPaint_Red);
            //
            //            x = x + mPaint_Red.measureText(discountStr);
            //            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_8);
            //            canvas.drawText("折", x, y, mPaint_Red);
            //
            //            String mXPrice = "￥" + originalPriceStr;
            //            x = ClassiFicationValuesDimen.Dimen_188;
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
            //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_12);
            //            canvas.drawText(mXPrice, x, y, mPaint_BLACK);
            //
            //            // 在原始价格上加上删除线
            //            int xPriceWidth = (int) mPaint_BLACK.measureText(mXPrice);
            //            y = y - ClassiFicationValuesDimen.Dimen_4;
            //            canvas.drawLine(x, y, x + xPriceWidth + ClassiFicationValuesDimen.Dimen_4, y, mPaint_BLACK);
            //
            //            // 显示购买人数
            //            x = dispalyWidth;
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_22;
            //            mPaint_Green.setTextSize(ClassiFicationValuesDimen.Dimen_14);
            //
            //            int mSoldWidth = (int) mPaint_Green.measureText(soldStr);
            //            x = x - mSoldWidth - bound;
            //            canvas.drawText(soldStr, x, y, mPaint_Green);
            //
            //            // 显示文字"人已买"
            //            String RenHasSale = "人已买";
            //            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_12);
            //            int mRenHasSaleWidth = (int) mPaint_BLACK.measureText(RenHasSale);
            //            x = dispalyWidth - mRenHasSaleWidth - bound;
            //            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
            //            canvas.drawText(RenHasSale, x, y, mPaint_BLACK);

            /**********************/

            Bitmap bitmap = Bitmap.createBitmap(dispalyWidth, textTabHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT);

            Rect rt = new Rect(0, 0, dispalyWidth, textTabHeight);
            drawNinepath(canvas, mInfoBackGroudBitmap, rt);

            float optionLeft = 0;

            float x = ClassiFicationValuesDimen.Dimen_14;
            float y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_14);
            mPaint_Red.setFakeBoldText(false);
            String moneyUnit = mContext.getResources().getString(R.string.jhs_dollar_sign);
            canvas.drawText(moneyUnit, x, y, mPaint_Red);

            float left = mPaint_Red.measureText(moneyUnit);

            optionLeft = x;
            optionLeft += left;

            mPaint_Red.setTextSkewX(-0.2f);
            mPaint_Red.setFakeBoldText(true);
            x = x + left;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_35);
            canvas.drawText(discountpriceStrOfNatural, x, y, mPaint_Red);

            optionLeft += mPaint_Red.measureText(discountpriceStrOfNatural);
            mPaint_Red.setTextSkewX(0.0f);

            x = x + mPaint_Red.measureText(discountpriceStrOfNatural);
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_8;
            mPaint_Red.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            canvas.drawText(discountpriceStrOfFractional, x, y, mPaint_Red);

            optionLeft += mPaint_Red.measureText(discountpriceStrOfFractional);

            // 显示折扣图片
            x = ClassiFicationValuesDimen.Dimen_12;
            y = 0;
            canvas.drawBitmap(mDiscountBitmap_Big, x, y, null);

            // 显示折扣率
            x = ClassiFicationValuesDimen.Dimen_16;
            y = textTabHeight - 62;
            mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            mPaint_White.setFakeBoldText(true);
            mPaint_White.setTextSkewX(-0.5f);
            canvas.drawText(discountStr, x, y, mPaint_White);

            mPaint_White.setTextSkewX(0.0f);
            x = x + mPaint_White.measureText(discountStr) + ClassiFicationValuesDimen.Dimen_8;
            mPaint_White.setTextSize(ClassiFicationValuesDimen.Dimen_24);
            canvas.drawText(mContext.getResources().getString(R.string.jhs_discount_unit), x, y, mPaint_White);

            String mXPrice = moneyUnit + originalPriceStr;
            x = optionLeft;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_10;
            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_14);
            canvas.drawText(mXPrice, x, y, mPaint_BLACK);

            // 在原始价格上加上删除线
            int xPriceWidth = (int) mPaint_BLACK.measureText(mXPrice);
            y = y - ClassiFicationValuesDimen.Dimen_4;
            canvas.drawLine(x, y, x + xPriceWidth + ClassiFicationValuesDimen.Dimen_4, y, mPaint_BLACK);

            String RenHasSale = mContext.getResources().getString(R.string.jhs_detail_sold_desc) + soldStr;
            mPaint_Red.setTextSize(18);
            mPaint_Red.setFakeBoldText(false);
            int mRenHasSaleWidth = (int) mPaint_Red.measureText(RenHasSale);
            x = dispalyWidth - mRenHasSaleWidth - bound;
            y = textTabHeight - ClassiFicationValuesDimen.Dimen_15;
            canvas.drawText(RenHasSale, x, y, mPaint_Red);

            return bitmap;

        }

        public Bitmap onHandleDisplayBitmap(Bitmap mbitmap, final int dispalyWidth, final int dispalyHeight,
                                            final int ItemType, final ItemMO info) {

            if (mbitmap == null)
                return null;

            if (mbitmap.isRecycled()) {
                AppDebug.e(TAG, "mbitmap = " + mbitmap);
                return null;
            }

            if (info == null)
                return null;

            long mOriginalPrice = info.getOriginalPrice();
            double mDiscount = info.getDiscount();
            int mSoldCount = info.getSoldCount();
            long mDiscountprice = info.getActivityPrice();

            String discountpriceStrOfNatural = String.valueOf(mDiscountprice / 100);
            String discountpriceStrOfFractional = "." + String.valueOf(mDiscountprice % 100);
            String soldStr = String.valueOf(mSoldCount);
            String discountStr = String.valueOf(mDiscount);
            String originalPriceStr = String.valueOf(mOriginalPrice / 100) + "." + String.valueOf(mOriginalPrice % 100);

            Distabel_Info mDistabel_Info = new Distabel_Info();
            mDistabel_Info.discountpriceStrOfNatural = discountpriceStrOfNatural;
            mDistabel_Info.discountpriceStrOfFractional = discountpriceStrOfFractional;
            mDistabel_Info.discountStr = discountStr;
            mDistabel_Info.soldStr = soldStr;
            mDistabel_Info.originalPriceStr = originalPriceStr;

            int textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_BIG;
            Bitmap bitmap = null;

            if (ItemType == VisualMarkConfig.ITEM_TYPE_BIG) {
                textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_BIG;
                bitmap = onCreateTabel_Big(dispalyWidth, dispalyHeight, mDistabel_Info);
            } else if (ItemType == VisualMarkConfig.ITEM_TYPE_SMALL) {
                textTabHeight = VisualMarkConfig.TABELTISHI_HEIGHT_SMALL;
                bitmap = onCreateTabel_Small(dispalyWidth, dispalyHeight, mDistabel_Info);
            }

            int url_bitmap_width = mbitmap.getWidth();
            int url_bitmap_height = mbitmap.getHeight();
            int value_url_bitmap_height = dispalyHeight - textTabHeight;
            int offset_bitmap_x = dispalyWidth <= url_bitmap_width ? 0 : (dispalyWidth - url_bitmap_width) / 2;
            int offset_bitmap_y = value_url_bitmap_height <= url_bitmap_height ? 0
                    : (value_url_bitmap_height - url_bitmap_height) / 2;

            Bitmap displayBitmap = Bitmap.createBitmap(dispalyWidth, dispalyHeight, Bitmap.Config.RGB_565);
            Canvas mCanvas = new Canvas(displayBitmap);
            mCanvas.drawColor(Color.WHITE);

            mCanvas.drawBitmap(mbitmap, offset_bitmap_x, offset_bitmap_y, null);
            mCanvas.drawBitmap(bitmap, 0, dispalyHeight - textTabHeight, null);

            OnRecycled(mbitmap);
            OnRecycled(bitmap);

            Rect rt = new Rect(0, 0, dispalyWidth, dispalyHeight);
            mCanvas.drawRoundRect(new RectF(rt), 5, 5, mPaint_White_Frame);

            //          Bitmap resultBitmap = displayBitmap; 
            Bitmap resultBitmap = toRoundCorner(displayBitmap, 5, true);

            return resultBitmap;

        }

        public Bitmap onHandleDisplayBitmap(Bitmap mbitmap, ItemFrameLayout itemFrameLayout, final ItemMO info) {

            int dispalyWidth = itemFrameLayout.onGetItemWidth();
            int dispalyHeight = itemFrameLayout.onGetItemHeight();
            int ItemType = itemFrameLayout.onGetImageType();

            return onHandleDisplayBitmap(mbitmap, dispalyWidth, dispalyHeight, ItemType, info);

        }

        public void OnRecycled(Bitmap bitmap) {

            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }

        public void onInitPaint(Context context) {

            mContext = context;

            mFont = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

            //            

            mPaint_White_Frame = new Paint();
            mPaint_White_Frame.setColor(Color.WHITE);
            mPaint_White_Frame.setStyle(Paint.Style.STROKE);
            mPaint_White_Frame.setStrokeJoin(Paint.Join.ROUND);
            mPaint_White_Frame.setStrokeCap(Paint.Cap.ROUND);
            mPaint_White_Frame.setDither(true);
            mPaint_White_Frame.setStrokeWidth(ClassiFicationValuesDimen.Dimen_4);
            mPaint_White_Frame.setAntiAlias(true);
            mPaint_White_Frame.setFakeBoldText(false);
            mPaint_White_Frame.setSubpixelText(true);

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

            mPaint_BLACK = new Paint();
            mPaint_BLACK.setColor(VisualMarkConfig.DISPALY_COLOR_BLACK);
            mPaint_BLACK.setStyle(Paint.Style.FILL);
            mPaint_BLACK.setStrokeJoin(Paint.Join.ROUND);
            mPaint_BLACK.setStrokeCap(Paint.Cap.ROUND);
            mPaint_BLACK.setDither(true);
            mPaint_BLACK.setStrokeWidth(ClassiFicationValuesDimen.Dimen_1);
            mPaint_BLACK.setTextSize(ClassiFicationValuesDimen.Dimen_15);
            mPaint_BLACK.setAntiAlias(true);
            mPaint_BLACK.setFakeBoldText(false);
            mPaint_BLACK.setSubpixelText(true);

            mPaint_Green = new Paint();
            mPaint_Green.setColor(VisualMarkConfig.DISPALY_COLOR_GREEN);
            mPaint_Green.setStyle(Paint.Style.FILL);
            mPaint_Green.setStrokeJoin(Paint.Join.ROUND);
            mPaint_Green.setStrokeCap(Paint.Cap.ROUND);
            mPaint_Green.setDither(true);
            mPaint_Green.setStrokeWidth(ClassiFicationValuesDimen.Dimen_1);
            mPaint_Green.setAntiAlias(true);
            mPaint_Green.setFakeBoldText(false);
            mPaint_Green.setSubpixelText(true);

            //            mDiscountBitmap_Big = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.classification_discount_big);
            //            mDiscountBitmap_Small = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.classification_discount_small);

            mDiscountBitmap_Big = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.jhs_img_discount_normal);
            mDiscountBitmap_Small = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.jhs_classification_discount_small);

            //            
            mInfoBackGroudBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.jhs_item_info_bg);

            //            
            mFrameBackGroudBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.jhs_goods_item_frame);
        }

        public class Distabel_Info {

            public String discountpriceStrOfNatural = null;
            public String discountpriceStrOfFractional = null;

            public String soldStr = null;
            public String discountStr = null;
            public String originalPriceStr = null;

        }
    }

}
