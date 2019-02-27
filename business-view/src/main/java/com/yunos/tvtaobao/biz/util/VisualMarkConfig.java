package com.yunos.tvtaobao.biz.util;


import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.yunos.tvtaobao.businessview.R;


public final class VisualMarkConfig {

//    public static Context   mContext                 = null;

    // 界面的缩放比
    public static float     Display_Scale            = 1.0f;
    
    
    public static final  int  LIMIT_WORDS            = 15;

    //  请求页数
    public static final int EACH_REQUEST_PAGE_COUNT  = 4;

    // 每页显示的条目
    public static final int PAGE_COUNT               = 8;
    // 行数
    public static final int ROW_COUNT                = 2;

    // 列数
    public static final int COL_COUNT                = PAGE_COUNT / ROW_COUNT;

    // 条目之间的空隙
    public static int       ITEM_SPACE               = 7;

    // 每个条目的宽度以及高度
    public static int       EACH_WIDTH               = 0;
    public static int       EACH_HEIGHT              = 0;

    //	// 每页的页宽度 以及 页高度  
    public static int       PAGE_WIDTH               = EACH_WIDTH * COL_COUNT + ITEM_SPACE * (COL_COUNT - 1);
    public static int       PAGE_HEIGHT              = EACH_HEIGHT * ROW_COUNT + ITEM_SPACE * (ROW_COUNT - 1);

    public static int       PAGEVIEW_GAP             = 0;
    
    
    public  static int      SCREEN_WIDTH             = 0;
    public  static int      SCREEN_HEIGHT            = 0;
    
    
    
    

    //     Margin

    public static int       PAGE_VIEW_MARGIN_LEFT    = 0;
    public static int       PAGE_VIEW_MARGIN_TOP     = 0;
    public static int       ITEM_SHADOW              = 0;
    

    // 每种排序最大显示的页数
    public static final int PAGE_TOTAL               = 52;

    // SD卡缓冲区中最大存储的文件数
    public static final int SD_CARD_CACHE_FILE_COUNT = 20;                                                    // 5 *  4 * PAGE_COUNT

    // 价格提示标签的高度
    public static int       TABEL_HEIGHT             = 0;

    // 请求图片的所需尺寸

    private static String URL_Bitmap_Size_270      = "_270x270.jpg";
    private static String URL_Bitmap_Size_400      = "_400x400.jpg";
    public static String URL_Bitmap_Size          = URL_Bitmap_Size_270;

    public static final String onGetURL_Bitmap_Size(int size) {
        String bitmapSize = "";

        if (size > 0) {
            String w = String.valueOf(size);
            bitmapSize = "_" + w + "x" + w + ".jpg";
        }

        return bitmapSize;

    }

    /*** 字体有关 ****/

    // 关键字和总页数的字体大小
    public static int          mPageTextSize      = 0;

    // 关键字和总页数的定位
    public static int          mPage_Offsex_X     = 0;
    public static int          mPage_Offsex_Y     = 0;

    //  标签中的字体大小以及高度
    public static int          textSize           = 0;
    public static int          textHeight         = 0;

    //  标签中的定位
    public static int          mTableBar_Offset_X = 0;
    public static int          mTableBar_Offset_Y = 0;

    //  标签之间的间距
    public static int          offset_space       = 0;

    //  标签项失去焦点后的颜色
    public static final int    color_r            = 0x33;
    public static final int    color_g            = 0x33;
    public static final int    color_b            = 0x33;

    //  标签项获取焦点后的颜色
    public static final int    color_focus_r      = 0xff;
    public static final int    color_focus_g      = 0;
    public static final int    color_focus_b      = 0;
    
    
    //  标签项选中提示的颜色
    public static final int    color_select_r      = 0xff;
    public static final int    color_select_g      = 0x66;
    public static final int    color_select_b      = 0;
    

    // 条目中  “已售” 字样的颜色
    public static final int    mSoldText_Color    = Color.rgb(0x99, 0x99, 0x99);

    // 条目中  “价格” 字样的颜色
    public static final int    mPriceText_Color   = Color.rgb(0xff, 0x66, 0);

    // 商品名称的颜色
    public static final int    mGoodName_Color    = Color.rgb(0x33, 0x33, 0x33);

    // 条目中  图片边框的颜色
    public static final int    mBitmapBound_Color = Color.rgb(246, 246, 246);

    //  UI 整体背景色
    public static final int    mUiBackgroundColor = Color.argb(0xff, 0xe8, 0xe5, 0xe5);

    // 
    public static String mItemDefaultImage  = "goodlist_default_image_270.png";
    public static final String mSelectedBoxImage  = "page_item_selector.9.png";
    
//    public  static  int       mItemDefaultImageResId = R.drawable.ytsdk_ui2_goodlist_default_image;
    public static int mSelectedBoxImageResId = R.drawable.ytbv_common_focus;
    
    
    

    /**
     * 获得系统的硬件信息
     */
    public static void getScreenTypeFromDevice(Context context) {

        if (context == null)
            return; 

        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();

        int screenWidth = dm.widthPixels;

        mItemDefaultImage = "goodlist_default_image_270.png";
        Display_Scale = 1.0f;

        if (screenWidth == 1920) {
            mItemDefaultImage = "goodlist_default_image_400.png";
            Display_Scale = 1.5f;
        }
    }

    public static void onInitValue() {

        // 条目之间的空隙
        ITEM_SPACE = 7;

        // 每个条目的宽度以及高度
        EACH_WIDTH = 270;
        EACH_HEIGHT = 270;

        PAGEVIEW_GAP = 14;

        PAGE_VIEW_MARGIN_LEFT = 85;
        PAGE_VIEW_MARGIN_TOP  = 118;
        ITEM_SHADOW           = 50;
        
        
        
        SCREEN_WIDTH             = 1280;
        SCREEN_HEIGHT            = 672;
        
        

        // 价格提示标签的高度
        TABEL_HEIGHT = 69;

        /*** 字体有关 ****/

        // 关键字和总页数的字体大小
        mPageTextSize = 30;

        // 关键字和总页数的定位
        mPage_Offsex_X = 82;
        mPage_Offsex_Y = 36;

        //  标签中的字体大小以及高度
        textSize = 24;
        textHeight = 25;

        //  标签中的定位
        mTableBar_Offset_X = 780;
        mTableBar_Offset_Y = 40;

        //  标签之间的间距
        offset_space = 50;

        //图片尺寸
        URL_Bitmap_Size = URL_Bitmap_Size_270;

        if (Display_Scale != 1.0f) {
            onHandleScale();
        }

        // 每页的页宽度 以及 页高度  
        PAGE_WIDTH = EACH_WIDTH * COL_COUNT + ITEM_SPACE * (COL_COUNT - 1);
        PAGE_HEIGHT = EACH_HEIGHT * ROW_COUNT + ITEM_SPACE * (ROW_COUNT - 1);

    }

    public static void onHandleScale() {
        // 条目之间的空隙
        ITEM_SPACE = (int) (ITEM_SPACE * Display_Scale);

        // 再加5个像素
        ITEM_SPACE += 5;

        // 每个条目的宽度以及高度
        EACH_WIDTH = (int) (EACH_WIDTH * Display_Scale);
        EACH_HEIGHT = (int) (EACH_HEIGHT * Display_Scale);

        PAGEVIEW_GAP = (int) (PAGEVIEW_GAP * Display_Scale);

        //再加5个像素 
        PAGEVIEW_GAP += 5;

        PAGE_VIEW_MARGIN_LEFT = (int) (PAGE_VIEW_MARGIN_LEFT * Display_Scale);
        PAGE_VIEW_MARGIN_TOP = (int) (PAGE_VIEW_MARGIN_TOP * Display_Scale);
        ITEM_SHADOW = (int) (ITEM_SHADOW * Display_Scale);
        
        
 
        
        SCREEN_WIDTH = (int) (SCREEN_WIDTH * Display_Scale);
        SCREEN_HEIGHT = (int) (SCREEN_HEIGHT * Display_Scale);
         
         

        // 价格提示标签的高度
        TABEL_HEIGHT = (int) (TABEL_HEIGHT * Display_Scale);

        /*** 字体有关 ****/

        // 关键字和总页数的字体大小
        mPageTextSize = (int) (mPageTextSize * Display_Scale);

        // 关键字和总页数的定位
        mPage_Offsex_X = (int) (mPage_Offsex_X * Display_Scale);
        mPage_Offsex_Y = (int) (mPage_Offsex_Y * Display_Scale);

        //  标签中的字体大小以及高度
        textSize = (int) (textSize * Display_Scale);
        textHeight = (int) (textHeight * Display_Scale);

        //  标签中的定位
        mTableBar_Offset_X = (int) (mTableBar_Offset_X * Display_Scale);
        mTableBar_Offset_Y = (int) (mTableBar_Offset_Y * Display_Scale);

        //  标签之间的间距
        offset_space = (int) (offset_space * Display_Scale);

        //图片尺寸
        URL_Bitmap_Size = URL_Bitmap_Size_400;

    }

}
