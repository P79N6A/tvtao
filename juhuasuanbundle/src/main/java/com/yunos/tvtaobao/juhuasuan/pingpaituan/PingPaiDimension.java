package com.yunos.tvtaobao.juhuasuan.pingpaituan;


import android.content.Context;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tvtaobao.juhuasuan.R;

public class PingPaiDimension {

    public static Context mContext = null;

    //    // 界面的缩放比
    //    public static double Display_Scale = 1.0f;

    public static int ITEM_WIDTH = 0;
    public static int ITEM_HEIGHT = 0;

    public static int GOODS_WIDTH = 0;
    public static int GOODS_HEIGHT = 0;

    public static int LOGO_WIDTH = 0;
    public static int LOGO__HEIGHT = 0;

    public static int PAGE_MARGIN_LEFT = 0;
    public static int PAGE_MARGIN_TOP = 0;

    public static int ITEM_SPACE_X = 0;
    public static int ITEM_SPACE_Y = 0;

    public static int LOGO_MARGIN_LEFT = 0;
    public static int LOGO_MARGIN_TOP = 0;

    public static int INFOTABEL__HEIGHT = 0;
    public static int INFOTABEL_BITMAP_HEIGHT = 0;

    public static int PAGEVIEW_GAP = 0;

    public static int ITEM_SHADOW = 0;

    public static int GOODS_URL_BITMAP__WIDTH = 0;
    public static int LOGO_URL_BITMAP__WIDTH = 0;

    public static int PAGE_WIDTH = 0;
    public static int PAGE_HEIGHT = 0;

    public static int ROUNDED = 8;

    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    public static void onInitValue() {

        ITEM_WIDTH = getDimensionPixelSize(R.dimen.dp_520);
        ITEM_HEIGHT = getDimensionPixelSize(R.dimen.dp_220);

        GOODS_WIDTH = getDimensionPixelSize(R.dimen.dp_360);
        GOODS_HEIGHT = getDimensionPixelSize(R.dimen.dp_180);

        LOGO_WIDTH = getDimensionPixelSize(R.dimen.dp_120);
        LOGO__HEIGHT = getDimensionPixelSize(R.dimen.dp_60);

        PAGE_MARGIN_LEFT = getDimensionPixelSize(R.dimen.dp_105);
        PAGE_MARGIN_TOP = getDimensionPixelSize(R.dimen.dp_75);

        ITEM_SPACE_X = getDimensionPixelSize(R.dimen.dp_30);
        ITEM_SPACE_Y = getDimensionPixelSize(R.dimen.dp_30);

        LOGO_MARGIN_LEFT = getDimensionPixelSize(R.dimen.dp_380);
        LOGO_MARGIN_TOP = getDimensionPixelSize(R.dimen.dp_20);

        INFOTABEL__HEIGHT = getDimensionPixelSize(R.dimen.dp_140);
        INFOTABEL_BITMAP_HEIGHT = getDimensionPixelSize(R.dimen.dp_40);

        PAGEVIEW_GAP = getDimensionPixelSize(R.dimen.dp_30);
        ITEM_SHADOW = getDimensionPixelSize(R.dimen.dp_180);

        GOODS_URL_BITMAP__WIDTH = getDimensionPixelSize(R.dimen.dp_360);
        LOGO_URL_BITMAP__WIDTH = getDimensionPixelSize(R.dimen.dp_120);

        ROUNDED = getDimensionPixelSize(R.dimen.dp_8);

        SCREEN_WIDTH = getDimensionPixelSize(R.dimen.dp_1280);
        SCREEN_HEIGHT = getDimensionPixelSize(R.dimen.dp_630);

        //        if (Display_Scale != 1.0f) {
        //            onHandleScale();
        //        }

        // 每页的页宽度 以及 页高度  
        PAGE_WIDTH = (ITEM_WIDTH * 2) + ITEM_SPACE_X;
        PAGE_HEIGHT = (ITEM_HEIGHT * 2) + ITEM_SPACE_Y;

    }

    //    private static void onHandleScale() {
    //
    //        ITEM_WIDTH = (int) (ITEM_WIDTH * Display_Scale);
    //        ITEM_HEIGHT = (int) (ITEM_HEIGHT * Display_Scale);
    //
    //        GOODS_WIDTH = (int) (GOODS_WIDTH * Display_Scale);
    //        GOODS_HEIGHT = (int) (GOODS_HEIGHT * Display_Scale);
    //
    //        LOGO_WIDTH = (int) (LOGO_WIDTH * Display_Scale);
    //        LOGO__HEIGHT = (int) (LOGO__HEIGHT * Display_Scale);
    //
    //        PAGE_MARGIN_LEFT = (int) (PAGE_MARGIN_LEFT * Display_Scale);
    //        PAGE_MARGIN_TOP = (int) (PAGE_MARGIN_TOP * Display_Scale);
    //
    //        ITEM_SPACE_X = (int) (ITEM_SPACE_X * Display_Scale);
    //        ITEM_SPACE_Y = (int) (ITEM_SPACE_Y * Display_Scale);
    //
    //        LOGO_MARGIN_LEFT = (int) (LOGO_MARGIN_LEFT * Display_Scale);
    //        LOGO_MARGIN_TOP = (int) (LOGO_MARGIN_TOP * Display_Scale);
    //
    //        INFOTABEL__HEIGHT = (int) (INFOTABEL__HEIGHT * Display_Scale);
    //        INFOTABEL_BITMAP_HEIGHT = (int) (INFOTABEL_BITMAP_HEIGHT * Display_Scale);
    //
    //        PAGEVIEW_GAP = (int) (PAGEVIEW_GAP * Display_Scale);
    //        ITEM_SHADOW = (int) (ITEM_SHADOW * Display_Scale);
    //
    //        SCREEN_WIDTH = (int) (SCREEN_WIDTH * Display_Scale);
    //        SCREEN_HEIGHT = (int) (SCREEN_HEIGHT * Display_Scale);
    //
    //        ROUNDED = (int) (ROUNDED * Display_Scale);
    //
    //    }

    //    /**
    //     * 获得系统的硬件信息
    //     */
    //    public static void getScreenTypeFromDevice(double scale) {
    //        Display_Scale = scale;
    //    }

    public static int getDimensionPixelSize(int resId) {
        return CoreApplication.getApplication().getResources().getDimensionPixelSize(resId);
    }

    /**
     * 定义显示屏类型
     * @author Administrator
     */
    public class SCREENTYPE {

        // 屏幕为 720P
        public static final int ScreenType_720p = 1280;

        // 屏幕为 1080P
        public static final int ScreenType_1080P = 1920;
    };

}
