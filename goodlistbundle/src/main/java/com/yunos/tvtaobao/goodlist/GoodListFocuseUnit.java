package com.yunos.tvtaobao.goodlist;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tvtaobao.biz.widget.SdkViewPager;
import com.yunos.tvtaobao.goodlist.view.GoodListMenuFocusFrameLayout;

public class GoodListFocuseUnit {

    public MenuView_Info getViewPagerInfo() {
        return new MenuView_Info();
    }

    // 每种菜单的商品界面显示信息
    public class MenuView_Info {

        // 当前菜单所对应的页面布局View
        public FocusFlipGridView lifeUiGridView = null;

        // 当前菜单所对应的适配器
        public BaseAdapter goodListGirdViewAdapter = null;

        // 在切换菜单的时候是否已经请求过数据
        public boolean HasRequestData_ChangeMenu = false;

        public boolean mInLayout = true;

        // 实际返回的结果
        public int ReturnTotalResults = 0;

        // 蒙版显示状态
        public boolean Mask_display = false;

        // 背景显示的状态
        public boolean CommonState = false;

        //  当前菜单的请求数据是否已经结束
        public boolean End_Request = false;

        // 总共的行数
        public int TotalRows = 0;

        // 当前第几行
        public int CurrentRows = 0;

        // 当前请求第几页
        public int CurReqPageCount = 1;

        public FrameLayout.LayoutParams viewPagerLayoutParams = null;

        // 对应的菜单中文名字
        public String menuText = null;

        public String orderby = null;

        public String table = null;

        public String keyWords = null;
        public String catMap = null;
        public String tab = null;

        // 跟埋点有关的数据
        public String tbs_p = null;
        public String tbs_name = null;

        // 当前菜单的编号
        public int Index = 0;

        // 当前菜单的icon图标是否有排序[针对商品列表界面]
        public int sortFlag = SORTFLAG.NO;

        // 当前菜单商品中， 选中哪一个商品
        public int CurrentSelectedItem = -1;

        // 选中的VIEW
        public View Selectview = null;

        // 前一次选中的View
        public View OldSelectview = null;

        // 前一次选中的商品
        public int OldSelectedItem = -1;

        // GridView 第一次布局是否完成
        public boolean onLayoutDoneOfIsFirst = false;

        // 商品列表得到焦点
        public boolean gridViewGainFocus = false;

        // 第一次请求数据
        public boolean firstStartRequestData = true;

        // 针对低配版设置的标志
        public boolean checkVisibleItemOfLayoutDone = false;

        // 是否有商品
        public boolean haveGoods = true;

        // 焦点是否可以从菜单移到右边商品上
        public boolean canMoveRight = true;

        // 是否有推荐页面
        public boolean haveRecommend = false;

        // 是否有活动主题
        public boolean haveActivityShow = false;

    }

    // 每个商品列表的数据信息
    public final class VISUALCONFIG {

        public static final int LIMIT_WORDS = 15;

        //  请求行数
        public static final int EACH_REQUEST_ROW_COUNT = 10;

        // 每行显示的条目
        public static final int COLUMNS_COUNT = 4;

        // 每种排序最大显示的行数
        public static final int ROWS_TOTAL = 50;

        // 最多显示的商品数
        public static final int GOODS_MAX_TOTAL = ROWS_TOTAL * COLUMNS_COUNT;

        // 数据缓冲去中允许多准备的行数
        public static final int ROWS_SPACE = 3;

        // 图片缓冲区中所保留的图片   
        public static final int ROWS_KEEP = 3;

        // 开始允许加载的条目数
        public static final int START_POSITION = COLUMNS_COUNT * 2;

    }

    //  获得焦点位置， 
    public final class CURRENTBACKVIEW {

        public static final int MENU_VIEW = 1; // 菜单得到焦点
        public static final int GOODS_VIEW = 2; // 商品得到焦点
    }

    /**
     * 按钮条目的状态
     * @author Administrator
     */

    public final class STATE {

        public static final int NORMAL = 10;
        public static final int SELECT = 11;
        public static final int FOCUS = 12;

    };

    /**
     * @author Administrator
     */

    public final class SORTFLAG {

        public static final int NO = 10;
        public static final int UP = 11;
        public static final int DOWN = 12;

    };

    public static class ViewPager_Node_Info {

        public SdkViewPager viewPager = null;
        public int index = -1;
        public TextView textView = null;
        public String orderby = null;
    }

    // 焦点菜单和商品之间的切换
    public static interface GoodListButtonFocusChangedListen {

        public boolean goodlistbuttonFocusChanged(boolean gain);
    }

    // 设置选中条目时的监听
    public static interface SelectMenuItemListen {

        public boolean onSelectMenuItem(boolean isSelect, GoodListMenuFocusFrameLayout menuView, int position);
    }

    public static interface onItemHandleListener {

        boolean onClickPageItem(String ordey, int pageIndex, int itemIndex);

        boolean onInstantiateItemOfHandle(ViewGroup container, int newPageIndex);

        boolean onRequestImageFunc(ImageView itemView, String ordey, int pageIndex, int itemIndex);

        boolean onRemoveItemOfHandle(ViewGroup container, int newPageIndex);
    }

    public static interface onVerticalItemHandleListener {

        boolean onGetview(ViewGroup container, String ordey, int position);
    }
}
