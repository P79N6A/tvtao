/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.detailbundle.flash;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.yunos.tv.lib.DisplayUtil;
import com.yunos.tvtaobao.detailbundle.R;


public final class AppConfig {
	/** 调试开关 */
	public final static boolean DEBUG = false;
	
	public final static String APP_TAG = "TaoQiangGou";

	public final static boolean REMINDSETTINGACTIVITY_DEBUG = false;
	private static boolean mIsLoaded = false;

	/** 定义箭头的宽度, 单位dp */
	public static int ARROWBAR_WIDTH;

	/** 定义左右箭头离边界的距离 */
	public static int ARROWBAR_MARGIN;

	/** 定义左右箭头的宽度 */
	public static int ARROW_WIDTH;

	/** 定义左右箭头的高度 */
	public static int ARROW_HEIGHT;

	/**文字与箭头的距离*/
	public static int ARROW_OFFSET;
	
	/**提前提醒的时间,提前3分钟提醒*/
	public static long AHEAD_OF_TIME = 3 * 60 * 1000;
	
	/** 定义左右箭头文字显示的颜色 */
	public final static int ARROW_TEXT_COLOR = 0xFF000000;

	/** 定义左右箭头文字显示的大小, sp */
	public static int ARROW_TEXT_SIZE;

	/** 切换动画的时间 */
	public final static long ANIM_DURATION_TIMEOUT = 500;

	/** 定义数据更新时间, 暂定1min */
	public final static long UPDATE_DATA_TIMEOUT = 60 * 1000;

	/** 定义左右箭头文字显示的颜色 */
	public final static int TITLE_DETAIL_TEXT_COLOR = Color.WHITE;

	/** 定义左右箭头文字显示的大小, sp */
	public final static int TITLE_DETAIL_TEXT_SIZE = 30;

	/** GridView每行显示的条目 */
	public static final int COLUMNS_COUNT_GRIDVIEW = 2;

	/** GridView每页可见的行数 */
	public static final int ONE_PAGE_ROWS_GRIDVIEW = 3;
	
	/**顶部遮罩的宽度*/
	public static int MASK_TOP_WIDTH;
	public static int MASK_TOP_HEIGHT;
//	public static int MASK_TOP_MARGIN_TOP;
//	public static int MASK_TOP_MARGIN_LEFT;
	
	/** 定义gridview 顶部偏移数据 */
	public static int GRIDVIEW_OFFSET_TOP;

	/** 定义gridview 底偏移数据 */
	public static int GRIDVIEW_OFFSET_BOTTOM;
	
	/** 定义gridview 左边偏移数据 */
	public static int GRIDVIEW_OFFSET_LEFT;
	
	/** 定义gridview item的宽度 */
	public static int GRIDVIEW_ITEM_WIDTH;

	/** 定义gridview item的高度 */
	public static int GRIDVIEW_ITEM_HEIGHT;

	/** 定义gridview 的水平间距 */
	public static int GRIDVIEW_VERTICAL_SPACE;
	public static int GRIDVIEW_HORIZONTAL_SPACE;

	/** 定影价格突出时字体的的大小 */
	public static int PRICE_HIGHTLIGHT_FONT_SIZE;
	
	/** 相关推荐页定影价格突出时字体的的大小 */
	public static int PRICE_HIGHTLIGHT_SIZE;
	/** 相关推荐页定影价格突出时字体的的大小 */
	public static int PRICE_HIGHTLIGHT_SIZE_FOR_ITEM;
	
	public static int HLISTVIEW_SPACE = 15;
	
	/**提醒界面的时间字体*/
	public static int REMIND_TIME_FONT_SIZE;

	/** 我的关注中价格的颜色相关配置 */
	public final static int PRICE_COLOR = 0xB2000000;

	public final static int GREEN_SALEPRICE_COLOR = 0XFF045225;
	public final static int RED_SALEPRICE_COLOR = 0XFF930034;
	public final static int PINK_SALEPRICE_COLOR = 0XFF7B118B;

	public final static int RED_QIANGGOU_COLOR = 0xCC9d1540;
	public final static int GREEN_QIANGGOU_COLOR = 0XCC045225;
	public final static int PINK_QIANGGOU_COLOR = 0XCC7B118B;
	
	/**标题栏标题字体大小*/
	public static int TITLEBAR_TITLE_TEXT_SIZE;
	/**标题栏标题字体大小*/
	public static int TITLEBAR_TIP_TEXT_SIZE;

	/** 图片渐变时间 */
	public final static int IMAGELOAD_SWITCH_TIMEOUT = -1000;

	/** 显示秒杀字体的大小 */
	public static int SECKILL_FONT_SIZE;

	/** 定义时间轴动画参数 */
	public final static int TIME_AXIS_LINE_BGCOLOR = 0xCCCCCCCC; // 时间轴颜色
	public static int TIME_AXIS_LINE_WIDTH; // 时间轴的宽度
	// 小圆圈相关参数定义
	public final static int TIME_AXIS_LEVEL2_BGCOLOR = 0XFFCDCDCD; // 时间轴最小
	public static int TIME_AXIS_LEVEL2_RADIUS; // 小圆圈的半径
	public static int TIME_AXIS_LEVEL2_SPACE; // 小圆圈的间距
	// 次大圆圈的参数定义
	public final static int TIME_AXIS_LEVEL1_BGCOLOR = 0XFFCDCDCD; // 时间轴最小
	public final static int TIME_AXIS_LEVEL1_COLOR = 0xCC000000;
	public final static int TIME_AXIS_LEVEL1_STATUS_COLOR = 0xCC000000;
	public static int TIME_AXIS_LEVEL1_RADIUS; // 次圆圈的半径
	public static int TIME_AXIS_LEVEL1_SPACE; // 小圆圈的间距
	public static int TIME_AXIS_LEVEL1_FONTSIZE; // 时间显示的字体
	public static int TIME_AXIS_LEVEL1_STATUS_FONTSIZE; // 状态显示的字体大小

	/** 最大圆的皮配置 */
	public final static int TIME_AXIS_LEVEL0_BGCOLOR = 0xFFFFFFFF;
	public final static int TIME_AXIS_LEVEL0_COLOR = 0xFF000000;
	public final static int TIME_AXIS_LEVEL0_STATUS_COLOR = 0xCC000000;
	public static int TIME_AXIS_LEVEL0_RADIUS; // 次圆圈的半径
	public static int TIME_AXIS_LEVEL0_SPACE; // 小圆圈的间距
	public static int TIME_AXIS_LEVEL0_FONTSIZE; // 时间显示的字体
	public static int TIME_AXIS_LEVEL0_STATUS_FONTSIZE; // 状态显示的字体大小

	/** 进在抢购绘制参数 */
	public final static int TIME_AXIS_BUYING_BGCOLOR = 0XFFF72E4A;
	public final static int TIME_AXIS_BUYING_COLOR = 0xCCFFFFFF;
	public final static int TIME_AXIS_BUYING_STATUS_COLOR = 0xCCFFFFFF;
	public final static int TIME_AXIS_BUYING_SHADOW_COLOR = 0XBF260214;
	
	/** 获取焦点的半径和颜色 */
	public static int TIME_AXIS_SELECT_RADIUS;
	public final static int TIME_AXIS_SELECT_COLOR = 0XBF260214;

	public final static int TIME_AXIS_TEXT_SPACE = 2;
	
	/**投影设置*/
	public final static int TIME_AXIS_SHADOW_COLOR = 0XBF794B10;
	public final static int TIME_AXIS_SHADOW_COLOR_EX = 0X8C794B10;
	public static int TIME_AXIS_SHADOW_WIDTH;

	/**定义时段抢购的参数*/
	public static int PERIODBUY_MARGIN;
	
	/**定义偏移左侧的距离*/
	public static int PERIOD_BUYING_OFFSET;
	
	/**相关推荐列表padding*/
	public static int HLISTVIEW_PADDING_LEFT;
	public static int HLISTVIEW_PADDING_TOP;
	
	public static void loadConstData(Context con){
		if( mIsLoaded){
			return;
		}
		Resources res = con.getResources();
		
		MASK_TOP_WIDTH = res.getDimensionPixelSize(R.dimen.dp_20);
		MASK_TOP_HEIGHT = res.getDimensionPixelSize(R.dimen.dp_4);
		
		GRIDVIEW_OFFSET_TOP = res.getDimensionPixelSize(R.dimen.dp_16);
		GRIDVIEW_OFFSET_BOTTOM = res.getDimensionPixelSize(R.dimen.dp_70);	
		GRIDVIEW_OFFSET_LEFT = res.getDimensionPixelSize(R.dimen.dp_20);
		GRIDVIEW_ITEM_WIDTH = res.getDimensionPixelSize(R.dimen.dp_414);
		GRIDVIEW_ITEM_HEIGHT = res.getDimensionPixelSize(R.dimen.dp_228);
		
		GRIDVIEW_VERTICAL_SPACE = res.getDimensionPixelSize(R.dimen.dp_16);
		GRIDVIEW_HORIZONTAL_SPACE = res.getDimensionPixelSize(R.dimen.dp_16);		
		
		ARROWBAR_WIDTH = res.getDimensionPixelSize(R.dimen.dp_160);
		ARROWBAR_MARGIN = res.getDimensionPixelSize(R.dimen.dp_5);
		ARROW_OFFSET = res.getDimensionPixelSize(R.dimen.dp_10);
		ARROW_WIDTH = res.getDimensionPixelSize(R.dimen.dp_20);
		ARROW_HEIGHT = res.getDimensionPixelSize(R.dimen.dp_65);
//		ARROW_TEXT_SIZE = CommUtil.sp2px(con, 12);
		ARROW_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.dp_18);
		PERIOD_BUYING_OFFSET = res.getDimensionPixelSize(R.dimen.dp_130);

		HLISTVIEW_PADDING_LEFT = res.getDimensionPixelSize(R.dimen.dp_24);
		HLISTVIEW_PADDING_TOP = res.getDimensionPixelSize(R.dimen.dp_20);
		
		PRICE_HIGHTLIGHT_FONT_SIZE = res.getDimensionPixelSize(R.dimen.sp_32);
		PRICE_HIGHTLIGHT_SIZE = res.getDimensionPixelSize(R.dimen.sp_20);
		PRICE_HIGHTLIGHT_SIZE_FOR_ITEM = res.getDimensionPixelSize(R.dimen.sp_24);
	
		SECKILL_FONT_SIZE = res.getDimensionPixelSize(R.dimen.sp_16);
		
		TITLEBAR_TITLE_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.sp_24);
		TITLEBAR_TIP_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.sp_20);
		
		REMIND_TIME_FONT_SIZE = res.getDimensionPixelSize(R.dimen.sp_32);

		/** 时间轴的定义 */
		TIME_AXIS_LINE_WIDTH = res.getDimensionPixelSize(R.dimen.dp_2);
		TIME_AXIS_LEVEL2_RADIUS = res.getDimensionPixelSize(R.dimen.dp_3);
		TIME_AXIS_LEVEL2_SPACE = res.getDimensionPixelSize(R.dimen.dp_10);

		TIME_AXIS_LEVEL1_RADIUS = res.getDimensionPixelSize(R.dimen.dp_25);
		TIME_AXIS_LEVEL1_SPACE = res.getDimensionPixelSize(R.dimen.dp_13);
		TIME_AXIS_LEVEL1_FONTSIZE = res.getDimensionPixelSize(R.dimen.sp_18);
		TIME_AXIS_LEVEL1_STATUS_FONTSIZE = DisplayUtil.sp2px(con, 12);

		TIME_AXIS_LEVEL0_RADIUS = res.getDimensionPixelSize(R.dimen.dp_37);
//		TIME_AXIS_LEVEL0_RADIUS = res.getDimensionPixelSize(R.dimen.dp_55);
		TIME_AXIS_LEVEL0_SPACE = res.getDimensionPixelSize(R.dimen.dp_16);
		TIME_AXIS_LEVEL0_FONTSIZE = res.getDimensionPixelSize(R.dimen.sp_20);
//		TIME_AXIS_LEVEL0_FONTSIZE = res.getDimensionPixelSize(R.dimen.sp_43);
		TIME_AXIS_LEVEL0_STATUS_FONTSIZE = res.getDimensionPixelSize(R.dimen.sp_16);
		
		TIME_AXIS_SELECT_RADIUS = res.getDimensionPixelSize(R.dimen.dp_40);		
		TIME_AXIS_SHADOW_WIDTH = res.getDimensionPixelSize(R.dimen.dp_1);
		PERIODBUY_MARGIN = res.getDimensionPixelSize(R.dimen.dp_5);
		mIsLoaded = true;
	}

}
