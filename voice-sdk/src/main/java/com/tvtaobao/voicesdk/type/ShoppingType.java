package com.tvtaobao.voicesdk.type;

/**
 * Created by pan on 2017/9/29.
 */

public class ShoppingType {
    public final static int TYPE_SDK_INIT = 999;                    //SDK初始化
    public final static int TYPE_SHOPPING_SEARCH = 0;               //商品搜索
    public final static int TYPE_SHOPPING_TOBUY = 1;                //快捷购买
    public final static int TYPE_SHOPPING_ADDCART = 2;              //加入购物车
    public final static int TYPE_SHOPPING_COLLECT = 3;              //收藏
    public final static int TYPE_SHOPPING_TODETAIL = 4;             //跳转详情页
    public final static int TYPE_SHOPPING_TAKEOUT = 5;              //外卖搜索
    public final static int TYPE_SHOPPING_TOTAKEOUTSHOPHOME = 6;    //外卖搜索

    public final static int TYPE_INIT_SHOW_TAKEOUT_TIPS = 101;      //初始化是否展示外卖提示
    public final static int TYPE_INIT_SHOW_TVTAO_SEARCH = 102;      //初始化是否展示应用内搜索浮层
}
