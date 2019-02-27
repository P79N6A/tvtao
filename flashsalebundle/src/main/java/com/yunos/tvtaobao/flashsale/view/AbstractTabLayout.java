package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.flashsale.listener.TabGridViewListener;
import com.yunos.tvtaobao.flashsale.listener.TabSwitchViewListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractTabLayout extends RelativeLayout {

    private final String TAG = "AbstractTabLayout";
    /**
     * TabLayout的ID
     */
    public static final int TAB_LAYOUT_ID = 10000;

    /**
     * 左边Tab布局ID
     */
    public static final int TAB_ID = TAB_LAYOUT_ID + 3;

    /**
     * defined tab switch listener
     */
    public interface TabSwitchListener {
        /**
         * This method will be invoked when the tab is switched
         *
         * @param tabKey     key of the current tab
         * @param preTtabKey key of the previous tab
         */
        public void onSwitchTab(String tabKey, String preTtabKey);
    }

    /**
     * 左边Tab布局的接口
     */
    protected TabSwitchViewListener mTabView;
    protected Context mActivityContext;

    public AbstractTabLayout(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    public AbstractTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public AbstractTabLayout(Context context) {
        super(context);
        onCreate();
    }

    private String mCurKey = null;
    private HashMap<String, TabGridViewListener> mContentView = new HashMap<String, TabGridViewListener>();

    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            AbstractTabLayout.this.onFocusChange(v, hasFocus);
        }
    };

    protected void onSwitchTab(String tabKey, String pretabKey) {
        TabGridViewListener cur = mContentView.get(tabKey);
        TabGridViewListener prev = mContentView.get(mCurKey);
        View v;
        if (null != prev) {
            v = prev.getView();
            v.setFocusable(false);
            v.setVisibility(View.GONE);
            prev.onUnselect();
        }

        if (null == cur) {
            RelativeLayout.LayoutParams params = getContentLayoutParam(tabKey);
            cur = onCreateContentView(tabKey);
            v = cur.getView();
            AbstractTabLayout.this.addView(v, params);
            v.setOnFocusChangeListener(mFocusChangeListener);
            mContentView.put(tabKey, cur);
        } else {
            v = cur.getView();
        }

        mCurKey = tabKey;
        if (null != v) {
            v.setFocusable(true);
            v.setVisibility(View.VISIBLE);
            v.bringToFront();
            cur.onSelect();
        }
    }

    protected void removeGridView(String tabKey) {
        AppDebug.i(TAG, "removeGridView tabKey=" + tabKey + " before=" + mContentView.size());
        TabGridViewListener gridView = mContentView.get(tabKey);
        if (gridView != null) {
            removeView(gridView.getView());
            mContentView.remove(tabKey);
        }
        AppDebug.i(TAG, "removeGridView after=" + mContentView.size());
    }

    private void onCreate() {
        mActivityContext = super.getContext();
        /** 添加左边的Tab栏接口 */
        mTabView = onCreateTabView();
        View tabView = mTabView.getView();
        RelativeLayout.LayoutParams tabParams = geTabtLayoutParam();
        super.addView(tabView, tabParams);
        tabView.setOnFocusChangeListener(mFocusChangeListener);
        tabView.setId(TAB_ID);

        mTabView.setTabSwitchListener(new TabSwitchListener() {
            @Override
            public void onSwitchTab(String tabKey, String preTtabKey) {
                // TODO Auto-generated method stub
                AbstractTabLayout.this.onSwitchTab(tabKey, preTtabKey);
            }
        });
        onInitTabBaseVariableValue();
    }

    /**
     * get the layout parameter of the tab view
     */
    protected abstract RelativeLayout.LayoutParams geTabtLayoutParam();

    /**
     * 创建左边Tab布局的接口
     *
     * @return
     */
    protected abstract TabSwitchViewListener onCreateTabView();

    /**
     * get the layout parameter of the tab view
     */
    protected abstract RelativeLayout.LayoutParams getContentLayoutParam(
            String tabKey);

    /**
     * get the view of the specified tab key
     */
    protected abstract TabGridViewListener onCreateContentView(String tabKey);

    /**
     * This method will be invoked when the tab or tab content foucs changed
     *
     * @param v        get focused view
     * @param hasFocus
     */
    protected void onFocusChange(View v, boolean hasFocus) {

    }

    /**
     * 初始化界面中的变量值
     */
    protected void onInitTabBaseVariableValue() {

    }

    /**
     * 清除缓冲区数据
     */
    protected void onDestroy() {
        if (mTabView != null) {
            mTabView.onDestroy();
        }
        Iterator<Map.Entry<String, TabGridViewListener>> iter = mContentView
                .entrySet().iterator();

        while (iter.hasNext()) {
            TabGridViewListener l = iter.next().getValue();
            if (null != l) {
                l.onDestroy();
            }
        }

        mContentView.clear();

    }

    /**
     * get tab switch view
     */
    public TabSwitchViewListener getTabSwitchViewListener() {
        return mTabView;
    }

    /**
     * get tab content view of the specified tab key
     */
    public TabGridViewListener getTabContentViewListener(String tabKey) {
        return mContentView.get(tabKey);
    }

    /**
     * get cur tab content view of the specified tab key
     */
    public TabGridViewListener getCurTabContentViewListener() {
        return mContentView.get(mCurKey);
    }

    /**
     * get current key
     */
    public String getCurKey() {
        return mCurKey;
    }

    public TabGridViewListener getCurView() {
        String key = mCurKey;
        if (!TextUtils.isEmpty(key)) {
            return mContentView.get(key);
        }
        return null;
    }

    // /**
    // * set current key
    // *
    // */
    // public void switchTabKey(String key) {
    // mTabView.setCurrentKey(key);
    // }
}
