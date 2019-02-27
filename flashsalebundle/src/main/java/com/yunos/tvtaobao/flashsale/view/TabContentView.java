package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.CategoryList;
import com.yunos.tvtaobao.flashsale.listener.TabGridViewListener;
import com.yunos.tvtaobao.flashsale.listener.TabSwitchViewListener;
import com.yunos.tvtaobao.flashsale.listener.TitleBarListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;

import java.util.Map;

public class TabContentView extends AbstractTabLayout implements GoodsGridView.DirectionListener {

    private final static String TAG = "TabContentView";
    private TitleBarListener mTitleBarListener;
    private PeriodBuyView mPeriodBuyView;
    // private TabGridView mCurTabGridView;
    // private boolean mFirstFocus = true;
    private CategoryList mCategoryList;
    private CategoryItem mQianggou;
    private boolean mIsTabSwitch = false;
    private boolean mFirstFocus = true;


    public TabContentView(Context context) {
        super(context);
    }

    public void setTitleBarListener(TitleBarListener titleBarListener) {
        mTitleBarListener = titleBarListener;
    }

    public void setObject(CategoryList caegoryList) {
        if (caegoryList != null) {
            mCategoryList = caegoryList;
            mQianggou = mCategoryList.getCurItem();
        } else {
            mQianggou = null;
        }
        getTabSwitchViewListener().setObject(caegoryList);
    }

    public void setPeriodBuyView(PeriodBuyView v) {
        mPeriodBuyView = v;
    }

    @Override
    protected RelativeLayout.LayoutParams geTabtLayoutParam() {
        // TODO Auto-generated method stub
        RelativeLayout.LayoutParams tabParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        tabParam.addRule(RelativeLayout.ALIGN_TOP);
        tabParam.setMargins(AppConfig.PERIODBUY_MARGIN, 0, 0, 0);

        return tabParam;
    }

    private TimeAxisView mTimeAxisView;

    @Override
    protected TabSwitchViewListener onCreateTabView() {
        // TODO Auto-generated method stub
        mTimeAxisView = new TimeAxisView(mActivityContext);
        mTimeAxisView.setFocusable(false);

        mTimeAxisView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return mTimeAxisView;
    }

    @Override
    protected RelativeLayout.LayoutParams getContentLayoutParam(String tabKey) {
        // TODO Auto-generated method stub
        int girdViewWidth = AppConfig.COLUMNS_COUNT_GRIDVIEW * AppConfig.GRIDVIEW_ITEM_WIDTH
                + (AppConfig.COLUMNS_COUNT_GRIDVIEW - 1) * AppConfig.GRIDVIEW_HORIZONTAL_SPACE + 2
                * AppConfig.GRIDVIEW_OFFSET_LEFT;

        RelativeLayout.LayoutParams contentParam = new RelativeLayout.LayoutParams(girdViewWidth,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        contentParam.setMargins(AppConfig.PERIODBUY_MARGIN, 0, AppConfig.PERIODBUY_MARGIN, 0);

        contentParam.addRule(RelativeLayout.RIGHT_OF, TAB_ID);
        contentParam.addRule(RelativeLayout.ALIGN_TOP, TAB_ID);

        return contentParam;
    }

    @Override
    protected TabGridViewListener onCreateContentView(String tabKey) {
        // TODO Auto-generated method stub
        CategoryItem item = getItemInfos(tabKey);
        TabGridView tabGridView = new TabGridView(mActivityContext, this, item);

        initGridView(tabGridView);
        tabGridView.setObject(tabKey, null);

        return tabGridView;
    }

    private void initGridView(final TabGridView tabGridView) {
        tabGridView.setNumColumns(AppConfig.COLUMNS_COUNT_GRIDVIEW);
        tabGridView.setFlipScrollFrameCount(5);
        tabGridView.setNeedAutoSearchFocused(false);
        tabGridView.setAnimateWhenGainFocus(true, true, true, true);
        tabGridView.setVerticalSpacing(AppConfig.GRIDVIEW_VERTICAL_SPACE);
        tabGridView.setHorizontalSpacing(AppConfig.GRIDVIEW_HORIZONTAL_SPACE);
        tabGridView.setPadding(AppConfig.GRIDVIEW_OFFSET_LEFT, AppConfig.GRIDVIEW_OFFSET_TOP,
                AppConfig.GRIDVIEW_OFFSET_LEFT, AppConfig.GRIDVIEW_OFFSET_BOTTOM);

        tabGridView.setDirectionListener(this);

        // tabGridView.setDirectionListener(this);
        tabGridView.setContextListener(mPeriodBuyView.mContextListener);
        tabGridView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                if (null != mPeriodBuyView) {
                    mPeriodBuyView.showMaskView(tabGridView, tabGridView.getPrivateFlag());
                }
            }
        });
    }

    public void onSelect() {
        TabGridViewListener l = getCurTabContentViewListener();
        if (null != l) {
            l.onSelect();
        }
    }

    public void onUnselect() {
        TabGridViewListener l = getCurTabContentViewListener();
        if (null != l) {
            l.onUnselect();
        }
    }

    public boolean OnSwitch(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            return mTabView.getView().isFocused();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            TabGridViewListener l = getCurTabContentViewListener();
            if (null != l) {
                TabGridView gridView = (TabGridView) l.getView();
                return gridView.isFocused()
                        && CommUtil.isLastRight(gridView.getCount(), gridView.getColumnNum(),
                                gridView.getSelectedItemPosition());

            }
        }
        return false;
    }

    public CategoryItem getItemInfos(String key) {
        if (mCategoryList == null) {
            return null;
        }
        return mCategoryList.getItem(key);
    }

    public CategoryList getCategoryList() {
        return mCategoryList;
    }

    public CategoryItem getQianggou() {
        return mQianggou;
    }

    @Override
    public void onDirection(FocusFlipGridView flipView, int type) {
        // TODO Auto-generated method stub
        if (mIsTabSwitch) {
                AppDebug.i(TAG, "running switch");
            return;
        }
        if (type == GoodsGridView.DirectionListener.DIR_BOTTOM || type == GoodsGridView.DirectionListener.DIR_TOP) {
            FocusFlipGridView v = (FocusFlipGridView) super.getCurView();

            if (v == flipView && null != mTimeAxisView) {
                int code = (type == GoodsGridView.DirectionListener.DIR_BOTTOM) ? KeyEvent.KEYCODE_DPAD_DOWN
                        : KeyEvent.KEYCODE_DPAD_UP;

                if (mTimeAxisView.hasNavigationDirection(code)) {
                    mIsTabSwitch = true;
                    // FlipperFocusManager focusManger =
                    // mPeriodBuyView.getFocusPositionManager();
                    // focusManger.focusHide();
                    mTimeAxisView.setFocusable(false);
                    mTimeAxisView.navigationDirection(code);
                    mTimeAxisView.setFocusable(true);
                }
            }
        }

    }

    protected void onSwitchTab(String tabKey, String pretabKey) {
        FocusPositionManager focusManger = mPeriodBuyView.getFocusPositionManager();

        View v = null;
        boolean needFocusGridView = true;
        if (!mFirstFocus) {
            v = focusManger.getFocused();
        }
        if (null != v) {
            if (!(v instanceof TimeAxisView)) {
                /** 切换之前先隐藏焦点 */
                mTimeAxisView.setFocusable(false);
            } else {
                // fist focus need focused grid view
                if (!mFirstFocus) {
                    needFocusGridView = false;
                }
                v = null;
            }
        }
        String removeKey = getCurKey();
        super.onSwitchTab(tabKey, pretabKey);
        /** tab有切换 */
        // mCurTabGridView = (TabGridView) super.getCurView();
        String curKey = super.getCurKey();
        CategoryItem item = getItemInfos(curKey);

        if (null != item) {
            // 隐藏顶部遮罩
            mPeriodBuyView.showMaskView(null, FliperItemView.DRAW_NOTHING);

            if (null != mTitleBarListener) {
                mTitleBarListener.changeTitleBar(item.getStatus(), item.getStartTime(), item.getEndTime());
            }
            /** 切换时间轴 */
            CategoryItem cur = mCategoryList.getCurItem();
            String time, curTime;

            curTime = DateUtils.getTime(item.getStartTime());
            if (null != cur) {
                time = DateUtils.getTime(cur.getStartTime());
            } else {
                time = curTime;
            }
            String event = TbsUtil.getControlName(null, TbsUtil.CLICK_TIME, -1, curTime);
            Map<String, String> prop = Utils.getProperties();
            prop.put("time", time);
            prop.put("status", item.getStatus());
            prop.put("spm", SPMConfig.TAOQIANGGOU_MAIN_TIMELINE_P_NAME);
//            TBS.Adv.ctrlClicked(CT.Button, event, TbsUtil.getKeyValue(prop));
            Utils.utControlHit("Page_TaoQiangGou_Home", CT.Button+"-"+event, prop);

        }

        if (mIsTabSwitch) {
            mIsTabSwitch = false;
        }
        if (mFirstFocus) {
            mFirstFocus = false;
            mTimeAxisView.setFocusable(true);
        } else {
            if (!mTimeAxisView.isFocusable()) {
                mTimeAxisView.setFocusable(true);
            }
        }
        // if current focus view not TimeAxisView request focus grid view item
        AppDebug.i(TAG, "onSwitchTab needFocusGridView=" + needFocusGridView + " mFirstFocus=" + mFirstFocus);
        if (needFocusGridView) {
            TabGridViewListener viewListener = getCurView();
            if (viewListener != null && viewListener.getView() != null) {
                AppDebug.i(TAG, "onSwitchTab curKey=" + curKey + " viewListener=" + viewListener);
                focusManger.requestFocus(viewListener.getView(), View.FOCUS_DOWN);
            }
        }
        // remove pre tab grid view ,this must remove after focusManager requestFocus
        if (removeKey != null) {
            focusManger.getPositionManager().release();
            removeGridView(removeKey);
        }
    }

    public void setTimeAxisKey(String key) {
        mTimeAxisView.setTimeAxisKey(key);
    }
}
