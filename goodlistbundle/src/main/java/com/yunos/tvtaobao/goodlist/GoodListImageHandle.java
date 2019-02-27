package com.yunos.tvtaobao.goodlist;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;

import java.math.BigDecimal;

public class GoodListImageHandle {

    private String TAG = "GoodListImageHandle";
//    private Activity mActivity = null;

    private Paint mPaint_Red = null;
    private Paint mPaint_BLACK = null;
    private Paint mPaint_Line = null;

    private ValuesDimen mValuesDimen = new ValuesDimen();

    private Bitmap mDefultBitmap = null;
    private Drawable mDefultDrawable = null;

    private Bitmap mRecycledBitmap = null;

    // 总的条目高度和宽度
    protected int itemWidth = 0;
    protected int itemHeight = 0;

    // 信息的宽度与高度
    protected int infoBitmapWidth = 0;
    protected int infoBitmapHeight = 0;

    // 信息的宽度与高度
    protected int goodsBitmapWidth = 0;
    protected int goodsBitmapHeight = 0;

    private String mGoodsPicSize = "_290x290.jpg";

    private String mSymbol = "￥";

    private int mMargin = 0;

    // 主要是用来检查此Activity 是否释放
    protected boolean isDestroyImageHandle = false;

    public GoodListImageHandle(Activity activity) {

//        mActivity = activity;
        mSymbol = activity.getString(R.string.ytbv_price_unit_text);

        if (activity != null) {

            mValuesDimen.onInitValuesDimen(activity.getResources());

            mDefultBitmap = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.ytsdk_ui2_goodlist_item_default);

            mDefultDrawable = activity.getResources().getDrawable(R.drawable.ytsdk_touming);

            // 确定总条目的宽度与高度
            itemWidth = (int) activity.getResources().getDimensionPixelSize(R.dimen.dp_226);
            itemHeight = (int) activity.getResources().getDimensionPixelSize(R.dimen.dp_306);

            // 确定商品信息的宽度与高度
            infoBitmapWidth = (int) activity.getResources().getDimensionPixelSize(R.dimen.dp_226);
            infoBitmapHeight = (int) activity.getResources().getDimensionPixelSize(R.dimen.dp_78);

            // 确定商品图片的宽度与高度
            goodsBitmapWidth = (int) activity.getResources().getDimensionPixelSize(R.dimen.dp_226);
            goodsBitmapHeight = itemHeight - infoBitmapHeight;

        }

        // 创建回收图片
        mRecycledBitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);

        mMargin = mValuesDimen.Dimen_6;
        mGoodsPicSize = "_230x230.jpg";
        boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
        if (!beta && DeviceUtil.getScreenScaleFromDevice(activity) >= 1.2f) {
            mGoodsPicSize = "_350x350.jpg";
            mMargin = mValuesDimen.Dimen_8;
        } else if (beta) {
            mGoodsPicSize = "_150x150.jpg";
        }

    }

    public void onDrawGoodsInfoBitmap(Canvas canvas, final int infoBitmapWidth, final int infoBitmapHeight,
                                      final SearchedGoods info) {

        //        boolean originalDisplay = false;

        boolean postagefree = false;

        if (isDestroyImageHandle) {
            return;
        }

        if (info == null) {
            return;
        }

        if (canvas == null) {
            return;
        }

        String priceWmTemp = info.getWmPrice();// 无线端价格，如果没有则显示sku最优价格
        String priceTemp = info.getPrice(); //sku最优价格
        String soldTemp = info.getSold();// 销售量
        String fastPostFeeTemp = info.getPostFee();// 运费

        AppDebug.v(TAG, TAG + ".onDrawGoodsInfoBitmap.priceWmTemp = " + priceWmTemp + ".priceTemp = " + priceTemp
                + ".soldTemp = " + soldTemp + ".fastPostFeeTemp = " + fastPostFeeTemp);
        if (TextUtils.isEmpty(priceWmTemp)) {
            priceWmTemp = priceTemp;
        }

        // 过滤 元 字
        if (priceTemp != null) {
            priceTemp = priceTemp.split("元")[0];
        }

        if (priceWmTemp != null) {
            priceWmTemp = priceWmTemp.split("元")[0];

            // 如果折扣价的位数超过 7 位，则原价不显示
            //            if (priceRateTemp.length() <= 7) {
            //                originalDisplay = true;
            //            }
        }

        //        try {
        //            int soldInt = Integer.parseInt(soldTemp);
        //            if (soldInt >= 10000) {
        //                double soldFloat = (double) (soldInt / 10000.0f);
        //                BigDecimal bg = new BigDecimal(soldFloat);
        //                double soldf1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //                soldTemp = String.valueOf(soldf1) + "万";
        //            }
        //        } catch (NumberFormatException e) {
        //            e.printStackTrace();
        //        }

        String spaceStr = " ";// 增加空格是为了与其他字符对齐，因为直接读取人民币符号编码是英文字符。


        //为了处理后台返回的科学计数法格式的价格
        String finalPrice = "";
        if (!TextUtils.isEmpty(priceWmTemp)) {
            BigDecimal bigDecimal = new BigDecimal(priceWmTemp);
            finalPrice = bigDecimal.toPlainString();
        }
        String priceRateStr = spaceStr + mSymbol + finalPrice;

        //String priceOriginalStr = spaceStr + mSymbol + priceOriginalTemp; //原价不显示

//        String fastPostFeeStr = null;
//        if ((fastPostFeeTemp != null) && (fastPostFeeTemp.equals("0.00"))) {
//            fastPostFeeStr = "免运费";
//            postagefree = true;
//        } else if (fastPostFeeTemp != null) {
//            fastPostFeeStr = "运费  " + fastPostFeeTemp;
//            postagefree = false;
//        } else {
//            postagefree = true;
//        }

        float x = 0;
        float y = 0;

        int bitmapHeiht = infoBitmapHeight;

        // 绘制无线端价格
        //        boolean isSamePrice = false;
//        if (!TextUtils.isEmpty(priceWmTemp)) {
//            x = mValuesDimen.Dimen_6;
//            y = bitmapHeiht - mValuesDimen.Dimen_21;
//
//            canvas.drawText(priceRateStr, x, y, mPaint_Red);
//            //            if (priceRateTemp.equals(priceOriginalTemp)) {
//            //                isSamePrice = true;
//            //            }
//        }

        //原价没有了
        //        if ((originalDisplay) && (!TextUtils.isEmpty(priceOriginalTemp)) && !isSamePrice) {
        //
        //            int width_priceRateStr = (int) mPaint_Red.measureText(priceRateStr) + mValuesDimen.Dimen_10;
        //
        //            // 绘制原价
        //            x = mValuesDimen.Dimen_10 + width_priceRateStr;
        //            y = bitmapHeiht - mValuesDimen.Dimen_21;
        //            canvas.drawText(priceOriginalStr, x, y, mPaint_BLACK);
        //
        //            // 绘制删划线
        //            x = x + mPaint_BLACK.measureText(mSymbol);
        //            y = bitmapHeiht - mValuesDimen.Dimen_27;
        //            int stopX = (int) (x + (int) mPaint_BLACK.measureText(priceOriginalStr) - mPaint_BLACK.measureText(mSymbol));
        //            canvas.drawLine(x, y, stopX, y, mPaint_Line);
        //        }

//        String soldStr = null;
//        if (!TextUtils.isEmpty(soldTemp) && !soldTemp.trim().equals("0")) {//如果销售量不为null也不为0，则显示销售量
//            soldStr = "月销量  " + soldTemp;
//        }
//        if (!TextUtils.isEmpty(soldStr)) {
//            // 绘制销量
//            x = mValuesDimen.Dimen_10;
//            y = bitmapHeiht - mValuesDimen.Dimen_54;
//            canvas.drawText(soldStr, x, y, mPaint_BLACK);
//        }

//        if ((!TextUtils.isEmpty(fastPostFeeTemp)) && (!postagefree)) {
//            // 绘制运费
//            int fastpostS_w = (int) mPaint_BLACK.measureText(fastPostFeeStr);
//            x = infoBitmapWidth - fastpostS_w - mValuesDimen.Dimen_10;
//            y = bitmapHeiht - mValuesDimen.Dimen_54;
//            canvas.drawText(fastPostFeeStr, x, y, mPaint_BLACK);
//        }
    }

    public InfoDiaplayDrawable onGetInfoDiaplayDrawable(SearchedGoods info) {
        return new InfoDiaplayDrawable(mMargin, info);
    }

    /**
     * 获取默认的图片
     *
     * @return
     */
    public Bitmap getDefultBitmap() {
        return mDefultBitmap;
    }

    public Drawable getDefultDrawable() {
        return mDefultDrawable;
    }

    /**
     * 获取释放后的图片
     *
     * @return
     */
    public Bitmap getRecycledBitmap() {

        return mRecycledBitmap;
    }

    public String onGetGoodsPicSize() {
        return mGoodsPicSize;
    }

    public void OnRecycled(Bitmap bitmap) {

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void onInitPaint() {

        /***
         * 打折的价格画笔
         */
        mPaint_Red = new Paint();
        mPaint_Red.setColor(ColorConfig.mPriceWithRate);
        mPaint_Red.setStyle(Paint.Style.FILL);
        mPaint_Red.setStrokeJoin(Paint.Join.ROUND);
        mPaint_Red.setStrokeCap(Paint.Cap.ROUND);
        mPaint_Red.setDither(true);
        mPaint_Red.setStrokeWidth(mValuesDimen.Dimen_1);
        mPaint_Red.setTextSize(mValuesDimen.Dimen_27);
        mPaint_Red.setAntiAlias(true);
        mPaint_Red.setFakeBoldText(false);
        mPaint_Red.setSubpixelText(true);

        /**
         * 原始价格和地点的画笔
         */
        mPaint_BLACK = new Paint();
        mPaint_BLACK.setColor(ColorConfig.mPriceWithOriginal);
        mPaint_BLACK.setStyle(Paint.Style.FILL);
        mPaint_BLACK.setStrokeJoin(Paint.Join.ROUND);
        mPaint_BLACK.setStrokeCap(Paint.Cap.ROUND);
        mPaint_BLACK.setDither(true);
        mPaint_BLACK.setStrokeWidth(mValuesDimen.Dimen_1);
        mPaint_BLACK.setTextSize(mValuesDimen.Dimen_14);
        mPaint_BLACK.setAntiAlias(true);
        mPaint_BLACK.setFakeBoldText(false);
        mPaint_BLACK.setSubpixelText(true);

        /**
         * 删划线的画笔
         */
        mPaint_Line = new Paint();
        mPaint_Line.setColor(ColorConfig.mPriceWithOriginal);
        mPaint_Line.setStyle(Paint.Style.STROKE);
        mPaint_Line.setStrokeJoin(Paint.Join.ROUND);
        mPaint_Line.setStrokeCap(Paint.Cap.ROUND);
        mPaint_Line.setDither(true);
        mPaint_Line.setStrokeWidth(mValuesDimen.Dimen_1);
        mPaint_Line.setAntiAlias(true);
        mPaint_Line.setFakeBoldText(false);
        mPaint_Line.setSubpixelText(true);

        isDestroyImageHandle = false;
    }

    public void onDestroyAndClear() {

        isDestroyImageHandle = true;

        if (mDefultBitmap != null) {
            OnRecycled(mDefultBitmap);
        }

        mDefultDrawable = null;

        // 默认图片指向释放图片
        mDefultBitmap = mRecycledBitmap;

    }

    /**
     * 处理点九图
     *
     * @param c   画布
     * @param bmp 点九图
     * @param r1  图片显示区域
     */
    public void drawNinepath(Canvas c, Bitmap bmp, RectF r1) {
        NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
        patch.draw(c, r1);
    }

    /**
     * 此类主要实现商品信息的图片
     *
     * @author yunzhong.qyz
     */
    public class InfoDiaplayDrawable extends Drawable {

        protected int margin;

        protected SearchedGoods mInfo = null;

        protected Bitmap mInfoBitmap = null;

        protected Canvas mInfoDisplayCanvas = null;
        protected Bitmap mInfoDisplayBitmap = null;

        private boolean mRecycle = false;

        public InfoDiaplayDrawable(int margin, SearchedGoods info) {

            this.margin = margin;
            mInfo = info;

            mInfoDisplayBitmap = mRecycledBitmap;

            /**
             * 创建信息图片
             */
            mInfoBitmap = Bitmap.createBitmap(infoBitmapWidth, infoBitmapHeight, Config.ARGB_8888);
            mInfoDisplayCanvas = new Canvas(mInfoBitmap);
            mInfoDisplayCanvas.drawColor(Color.WHITE);

            /**
             * 画商品信息
             */
            onDrawGoodsInfoBitmap(mInfoDisplayCanvas, infoBitmapWidth, infoBitmapHeight, mInfo);

            mInfoDisplayBitmap = mInfoBitmap;

            mRecycle = false;
        }

        /**
         * 释放资源
         */
        public void onDestroyInfoDiaplayDrawable() {

            mRecycle = true;
            mInfoDisplayBitmap = mRecycledBitmap;
            mInfoDisplayCanvas.setBitmap(mInfoDisplayBitmap);

            if ((mInfoBitmap != null) && (!mInfoBitmap.isRecycled())) {
                mInfoBitmap.recycle();
                mInfoBitmap = null;
            }
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);

            infoBitmapWidth = bounds.width();
            infoBitmapHeight = bounds.height();

        }

        @Override
        public void draw(Canvas canvas) {
            if (!mRecycle) {
                canvas.drawBitmap(mInfoDisplayBitmap, 0, 0, null);
            }

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }
    }

    private class ColorConfig {

        public static final int mPriceWithRate = 0xffff4b17;
        public static final int mPriceWithOriginal = 0x5f000000;
        public static final int mLine = 0x5f000000;
    }

    private class ValuesDimen {

        private Resources mResources = null;

        public int Dimen_1 = 0;
        public int Dimen_6 = 0;
        public int Dimen_8 = 0;
        public int Dimen_10 = 0;
        public int Dimen_14 = 0;
        public int Dimen_21 = 0;
        public int Dimen_27 = 0;
        public int Dimen_54 = 0;

        public void onInitValuesDimen(Resources res) {

            mResources = res;

            if (mResources == null)
                return;

            Dimen_1 = (int) mResources.getDimensionPixelSize(R.dimen.dp_1);

            Dimen_6 = (int) mResources.getDimensionPixelSize(R.dimen.dp_6);
            Dimen_8 = (int) mResources.getDimensionPixelSize(R.dimen.dp_8);

            Dimen_10 = (int) mResources.getDimensionPixelSize(R.dimen.dp_10);
            Dimen_14 = (int) mResources.getDimensionPixelSize(R.dimen.dp_14);
            Dimen_21 = (int) mResources.getDimensionPixelSize(R.dimen.dp_21);
            Dimen_27 = (int) mResources.getDimensionPixelSize(R.dimen.dp_27);
            Dimen_54 = (int) mResources.getDimensionPixelSize(R.dimen.dp_54);
        }

        // 释放资源
        public void onDestroyValuesDimen() {
            mResources = null;
        }

    }

    ;

}