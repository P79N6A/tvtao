/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.activity.FlashSaleBaseActivity;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache;
import com.yunos.tvtaobao.flashsale.listener.ContextListener;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;
import com.yunos.tvtaobao.flashsale.utils.req.ReqProcListener;
import com.yunos.tvtaobao.flashsale.utils.req.ReqStateInfo;

import java.util.Map;

public abstract class FliperItemView extends FrameLayout implements FlipperItemListener, ReqProcListener {

    protected final static String TAG = "FliperItemView";
    protected LayoutInflater mInflater;
    protected ArrowBarView mLeftBar;
    protected ArrowBarView mRightBar;
    protected ContentContainerView mContentContainer;
    protected FocusFlipperView mFocusViewFlipper;
    protected TitlebarView mViewTitlebar;
    protected Context mAppContext;
    protected RequestManager mRequestManager;
    protected FocusPositionManager mFocusPositionManager;
    protected MyConcernCache mMyConcernCache;

    protected int mGridViewVerticalSpacing;
    protected int mGridViewHorizontalSpacing;
    protected int mGridViewItemWidth;
    protected int mGridViewItemHeight;
    protected int mGridViewOffsetBottom;
    protected View mFocus;

    protected ContextListener mContextListener;
    public static int DRAW_NOTHING = 0;
    public static int DRAW_MASK_TOP = 0x1;
    public static int DRAW_MASK_BOTTOM = 1 << 1;
    private ImageView mTopMaskView;
    protected boolean mIsDestroy;
    protected Context mActivityContext;

    public FliperItemView(FocusFlipperView flipper, Context context) {
        super(context);
        mActivityContext = context;

        FlashSaleBaseActivity baseActivity = (FlashSaleBaseActivity) context;

        mContextListener = baseActivity.getFlashSaleContextListener();

        mAppContext = context.getApplicationContext();
        AppManager appManager = AppManager.getInstance(mAppContext);
        mRequestManager = appManager.getRequestManager();

        mFocusViewFlipper = flipper;
        mMyConcernCache = appManager.getMyConcernCache();
        getFocusPositionManager();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = mInflater.inflate(R.layout.fs_layout_viewpage_item, null);
        super.addView(v);

        init();
    }

    public FocusPositionManager getFocusPositionManager() {
        if (null == mFocusPositionManager) {
            ViewParent parent = mFocusViewFlipper;
            while (null != parent) {
                if (parent instanceof FocusPositionManager) {
                    mFocusPositionManager = (FocusPositionManager) parent;
                    break;
                }
                parent = parent.getParent();
            }
        }
        return mFocusPositionManager;
    }

    @Override
    public void loadingData(Object userData) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean loadingDataError(Object userData) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void excuteReq(Object userData) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte getPageType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
            AppDebug.d(TAG, "dispatchDraw:" + this.getClass().toString());
        super.dispatchDraw(canvas);
    }

    private void init() {
        mGridViewVerticalSpacing = AppConfig.GRIDVIEW_VERTICAL_SPACE;
        mGridViewHorizontalSpacing = AppConfig.GRIDVIEW_HORIZONTAL_SPACE;
        mGridViewOffsetBottom = AppConfig.GRIDVIEW_OFFSET_BOTTOM;
        mGridViewItemWidth = AppConfig.GRIDVIEW_ITEM_WIDTH;
        mGridViewItemHeight = AppConfig.GRIDVIEW_ITEM_HEIGHT;

        mLeftBar = (ArrowBarView) super.findViewById(R.id.action_bar_left);
        mRightBar = (ArrowBarView) super.findViewById(R.id.action_bar_right);

        mContentContainer = (ContentContainerView) super.findViewById(R.id.content);

        mViewTitlebar = (TitlebarView) super.findViewById(R.id.titlebar);
        mReqStateInfo = onCreateReqStateInfo();
        mReqStateInfo.setContextListener(mContextListener);
        onAddContentView();
        initView();
    }

    public void updateViewTitlebar() {
        FlipperItemListener item = (FlipperItemListener) mFocusViewFlipper.getCurrentView();
        if (null != item) {
            // LogUtil.d("item.getTitleBarType() = " + item.getTitleBarType());
            mViewTitlebar.setTitleBarType(item.getPageType());
            mViewTitlebar.invalidate();
        }
    }

    private void updateArrow(int selectPos) {
        int childCount = mFocusViewFlipper.getChildCount();
        mLeftBar.restore();
        mRightBar.restore();
        if (childCount <= (selectPos + 1)) {
            mLeftBar.setVisibility(View.VISIBLE);
            mRightBar.setVisibility(View.INVISIBLE);
        } else if (selectPos <= 0) {
            mLeftBar.setVisibility(View.INVISIBLE);
            mRightBar.setVisibility(View.VISIBLE);
        } else {
            mLeftBar.setVisibility(View.VISIBLE);
            mRightBar.setVisibility(View.VISIBLE);
        }
    }

    protected View getCurFocus() {
        View v = mFocusPositionManager.getFocused();
        if (null != v && v instanceof ItemListener) {
            ViewParent parent = v.getParent();

            while (null != parent) {
                if (parent == this) {
                        AppDebug.i(TAG, "focus class: " + this);

                    return v;
                }
                parent = parent.getParent();
            }

        }
            AppDebug.i(TAG, "get focus class null in : " + this);
        return null;
    }

    @Override
    public void OnViewChange(View selectView, int selectPos) {
        updateArrow(selectPos);
    }

    @Override
    public void onPageSelected(View selectView, int selectPos) {
        AppDebug.e(TAG,"-----onPageSelected加载数据");
        // TODO Auto-generated method stub
        updateArrow(selectPos);
        AppDebug.i(TAG, "onPageSelected: selectView = " + selectView + " selectPos = " + selectPos);
        mFocusPositionManager.focusStart();
        /** 加载数据 */
        ReqStateInfo reqInfo = mReqStateInfo;
        if (null != reqInfo) {
            reqInfo.checkReq();
        }

    }

    @Override
    public void onPageWillSelected(View selectView, int selectPos, View unselectView, int unselectPos) {
        AppDebug.i(TAG, "onPageWillSelected: selectView = " + selectView + " selectPos = " + selectPos);
        if (selectPos > unselectPos) {
            if (null != unselectView && unselectView instanceof FliperItemView) {
                FliperItemView other = (FliperItemView) unselectView;
                mLeftBar.reset(other.mRightBar);
            }
            mLeftBar.setVisibility(View.VISIBLE);
        } else {
            mRightBar.setVisibility(View.INVISIBLE);
        }
        /** 埋点 */
        byte type = getPageType();
        Map<String, String> prop = Utils.getProperties();

        if (type == FlipperItemListener.TYPE_FINALLY_BUY) {

            prop.put("spm", SPMConfig.TAOQIANGGOU_MAIN_BUTTON_LAST);
            Utils.utControlHit("Page_TaoQiangGou_Home", "Button-"+TbsUtil.CLICK_Remainder, prop);
//            TBS.Adv.ctrlClicked(CT.Button, TbsUtil.CLICK_Remainder, TbsUtil.getKeyValue(prop));
        } else if (type == FlipperItemListener.TYPE_MYCONCERN) {
            TBS.Adv.ctrlClicked(CT.Button, TbsUtil.CLICK_My_Favorites, TbsUtil.getKeyValue(prop));
        } else if (type == FlipperItemListener.TYPE_PERIOD_BUY) {
            /** 关注页面返回 */
            if (selectPos > unselectPos) {
                TBS.Adv.ctrlClicked(CT.Button, TbsUtil.CLICK_left_home, TbsUtil.getKeyValue(prop));
            } else {
                TBS.Adv.ctrlClicked(CT.Button, TbsUtil.CLICK_right_home, TbsUtil.getKeyValue(prop));
            }
        }
    }

    @Override
    public void onPageUnselected(View unselectView, int unselectPos) {
        // TODO Auto-generated method stub
        // mRightBar.setVisibility(View.INVISIBLE);
        // mLeftBar.setVisibility(View.INVISIBLE);
        if (null != mTopMaskView) {
            mTopMaskView.setVisibility(View.GONE);
        }
        AppDebug.i(TAG, "onPageUnselected: unselectView = " + unselectView + " unselectPos = " + unselectPos);
    }

    @Override
    public void onPageWillUnselected(View selectView, int selectPos, View unselectView, int unselectPos) {
        // TODO Auto-generated method stub
        if (selectPos > unselectPos) {
            mRightBar.setVisibility(View.INVISIBLE);
        } else {
            // mLeftBar.setVisibility(View.INVISIBLE);
        }
        AppDebug.i(TAG, "onPageWillUnselected: selectView = " + selectView + " selectPos = " + selectPos
                + " unselectView=" + unselectView + " unselectPos=" + unselectPos);

        mFocus = getCurFocus();
        mFocusPositionManager.focusStop();
    }

    @Override
    public boolean OnSwitch(int keyCode) {
        return true;
    }

    /**
     * create content view
     */
    public abstract void onAddContentView();

    /**
     * init relative view
     */
    protected void initView() {

    }

    protected ReqStateInfo mReqStateInfo;

    protected ReqStateInfo onCreateReqStateInfo() {
        return new ReqStateInfo(this);
    }

    @Override
    public void loadingDataSuccess(Object userData, Object reqData) {
        updateViewTitlebar();
    }

    @Override
    public boolean avaibleUpdate() {
        // TODO Auto-generated method stub
        return super.isShown();
    }

    @Override
    public void onResume() {
        int childCount = mFocusViewFlipper.getChildCount();
        int myConcernCount = mMyConcernCache.size();
        boolean reset = false;

        View curFocus = null;
        if (getPageType() != FlipperItemListener.TYPE_MYCONCERN) {
            curFocus = mFocusPositionManager.getFocused();
        }

        if (childCount == FlipperItemListener.TYPE_MAX) {
            if (myConcernCount <= 0) {
                View removeView = mFocusViewFlipper.getChildAt(0);
                removeMyConcernViewBeforeFocusNextView(removeView);
                mFocusViewFlipper.removeViewAt(0);
                reset = true;
            }
        } else {
            if (myConcernCount > 0) {
                mFocusViewFlipper.addView(
                        new MyConcernView(mFocusViewFlipper, mContextListener.getFlashSaleBaseActivity()), 0);
                reset = true;
            }
        }
        if (reset) {
            if (null != curFocus) {
                mFocusPositionManager.setFirstFocusChild(curFocus);
                mFocusPositionManager.resetFocused();
                mFocusPositionManager.setFirstFocusChild(null);
            } else {
                mFocusPositionManager.resetFocused();
            }
        }
    }

    @Override
    public void onDestroy() {
        mIsDestroy = true;
    }

    /**
     * 显示或者隐藏遮罩
     * @param v
     */
    public void showMaskView(View v, int maskFlag) {

        if (maskFlag == DRAW_NOTHING || v == null) {
            if (null != mTopMaskView) {
                mTopMaskView.setVisibility(View.GONE);
            }
            return;
        }

        if ((maskFlag & DRAW_MASK_TOP) != 0) {
            if (null == mTopMaskView) {
                final Rect rect = new Rect();
                v.getDrawingRect(rect);
                this.offsetDescendantRectToMyCoords(v, rect);
                //				LogUtil.d("left = " + rect.left + ";top = " + rect.top
                //						+ ";rect.right = " + rect.right + ";rect.bottom = "
                //						+ rect.bottom);
                int width = rect.right - rect.left + 2 * AppConfig.MASK_TOP_WIDTH;

                FrameLayout.LayoutParams mask_lp = new FrameLayout.LayoutParams(width, AppConfig.MASK_TOP_HEIGHT);
                mask_lp.setMargins(rect.left - AppConfig.MASK_TOP_WIDTH, rect.top - AppConfig.MASK_TOP_HEIGHT / 2, 0, 0);

                mTopMaskView = new ImageView(mAppContext);
                mTopMaskView.setImageDrawable(getResources().getDrawable(R.drawable.mask_top_line));
                super.addView(mTopMaskView, mask_lp);
            }
            mTopMaskView.setVisibility(View.VISIBLE);
        }

        if ((maskFlag & DRAW_MASK_BOTTOM) != 0) {
            /** 底部绘制 */

        }

    }

    protected void requestFocusedView(View view) {
        if (view == null) {
            Log.w(TAG, "requestFocusedView view is null");
            return;
        }
        FocusPositionManager focusPosition = getFocusPositionManager();
        if (focusPosition != null) {
            View currFocusedView = focusPosition.getFocused();
            boolean isCurrViewChildView = isChildView(focusPosition, currFocusedView);
            boolean isChildView = isChildView(focusPosition, view);
            AppDebug.i(TAG, "requestFocusedView currFocusedView=" + currFocusedView + " view=" + view
                    + " isCurrViewChildView=" + isCurrViewChildView + " isChildView=" + isChildView);
            if (isCurrViewChildView && isChildView && currFocusedView != view) {
                focusPosition.requestFocus(view, View.FOCUS_DOWN);
            } else {
                Log.w(TAG, "requestFocusedView preFocusedView is invalid view=" + currFocusedView);
            }
        }
    }

    protected boolean isChildView(ViewGroup parent, View child) {
        if (parent == null || child == null) {
            return false;
        }
        ViewParent temp = child.getParent();
        while (temp != null) {
            if (temp == parent) {
                return true;
            }
            temp = temp.getParent();
        }

        return false;
    }

    private void removeMyConcernViewBeforeFocusNextView(View removeView) {
        View currFocusedView = mFocusPositionManager.getFocused();
        boolean isFocusedView = false;
        if (removeView == currFocusedView) {
            isFocusedView = true;
        } else {
            if (removeView instanceof ViewGroup) {
                isFocusedView = isChildView((ViewGroup) removeView, currFocusedView);
            }
        }
        AppDebug.i(TAG, "onResume remove myConcernView removeView=" + removeView + " currFocusedView=" + currFocusedView
                + " isFocusedView=" + isFocusedView + " pageType=" + getPageType());
        // current focus is remove view, change focused view to next
        if (removeView != null && isFocusedView) {
            FocusListener nextFocusView = null;
            // find next FliperItemView
            int flipChildCount = mFocusViewFlipper.getChildCount();
            for (int i = 0; i < flipChildCount; i++) {
                View temp = mFocusViewFlipper.getChildAt(i);
                if (temp != removeView && temp instanceof FliperItemView) {
                    FliperItemView itemView = (FliperItemView) temp;
                    if (itemView.getValidFocusView() != null) {
                        nextFocusView = itemView.getValidFocusView();
                        break;
                    }
                }
            }
            AppDebug.i(TAG, "onResume remove myConcernView focus nextView=" + nextFocusView);
            if (nextFocusView != null) {
                mFocusPositionManager.requestFocus((View) nextFocusView, View.FOCUS_DOWN);
            }
        }
        mFocusPositionManager.getPositionManager().release();
    }

    protected abstract FocusListener getValidFocusView();
}
