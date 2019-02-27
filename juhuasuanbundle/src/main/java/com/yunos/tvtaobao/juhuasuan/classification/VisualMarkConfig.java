package com.yunos.tvtaobao.juhuasuan.classification;


import com.yunos.tvtaobao.juhuasuan.R;

public class VisualMarkConfig {

    // 条目类型
    public static final int ITEM_TYPE_BIG = 1;
    public static final int ITEM_TYPE_SMALL = 2;

    // 颜色
    public static final int DISPALY_COLOR_RED = 0xffc6223f;
    public static final int DISPALY_COLOR_BLACK = 0xff333333;
    public static final int DISPALY_COLOR_GREEN = 0xff339900;

    // 显示框的背景色
    public static final int DISPALY_TABEL_COLOR = 0xfffff3c8;

    // 标题的背景色
    public static final int TITLE_BLACKGROUD_COLOR = 0x99000000;

    public static int REQUESTIMAGE_SIZE_BIG = 500;
    public static int REQUESTIMAGE_SIZE_SMALL = 320;

    // 每个条目的宽度以及高度
    public static int PAGE_WIDTH = 950;
    public static int EACH_HEIGHT = 528;

    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 630;

    // 每一项的间隔
    public static int PAGEVIEW_GAP = 74;

    // Margin 
    public static int PAGE_VIEW_MARGIN_LEFT = 164;
    public static int PAGE_VIEW_MARGIN_TOP = 140;
    public static int ITEM_SHADOW = 50;

    public static int ITEM_BIG_WIDTH = 470;
    public static int ITEM_BIG_HEIGHT = 310;

    public static int ITEM_SMALL_WIDTH = 310;
    public static int ITEM_SMALL_HEIGHT = 208;

    public static int ITEM_SPACE = 10;

    //    public static int       TABELTISHI_HEIGHT_BIG   = 64;
    //    public static int       TABELTISHI_HEIGHT_SMALL = 46;

    public static int TABELTISHI_HEIGHT_BIG = 92;
    public static int TABELTISHI_HEIGHT_SMALL = 92;

    public static int HEAD_MARGIN_LEFT = 536;
    public static int HEAD_MARGIN_TOP = 19;

    public static int HEAD_MARGIN_LOCAL_LEFT = 984;
    public static int HEAD_MARGIN_LOCAL_TOP = 14;

    public static int ACTIONS_MARGIN_LEFT_BIG = 100;
    public static int ACTIONS_MARGIN_BUTTOM_BIG = 40;

    public static int ACTIONS_MARGIN_LEFT_SMALL = 100;
    public static int ACTIONS_MARGIN_BUTTOM_SMALL = 26;

    public static int TITLE_HEIGHT_SPACE = 20;
    public static int TITLE_HEIGHT_BIG = ITEM_BIG_HEIGHT - TABELTISHI_HEIGHT_BIG - TITLE_HEIGHT_SPACE;
    public static int TITLE_HEIGHT_SMALL = ITEM_SMALL_HEIGHT - TABELTISHI_HEIGHT_SMALL - TITLE_HEIGHT_SPACE;

    public static int MARK_RESOUS_NO_STOCK = R.drawable.jhs_no_stock;
    public static int MARK_RESOUS_NO_START = R.drawable.jhs_not_start;
    public static int MARK_RESOUS_SOLD_OVER = R.drawable.jhs_sold_over;
}
