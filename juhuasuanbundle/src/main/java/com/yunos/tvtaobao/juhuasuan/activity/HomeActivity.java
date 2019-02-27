package com.yunos.tvtaobao.juhuasuan.activity;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.ActivityQueueManager;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.core.ServerTimeSynchronizer;
import com.yunos.tvtaobao.biz.util.StringUtil;
//import com.yunos.tvtaobao.blitz.account.LoginHelper;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.juhuasuan.config.ConstValues.HomeItemTypes;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.fragment.BaseFragment;
import com.yunos.tvtaobao.juhuasuan.fragment.CategoryFragment;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;
import com.yunos.tvtaobao.juhuasuan.view.HorizontalListView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeActivity extends JuBaseActivity {

    public final static String TAG = "HomeActivity";
    private static String defaultCateId;

    //支付完成后退出activity的请求值
    public static final int ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE = 1;
    //刷新商品列表信息
    public static final int ACTIVITY_REQUEST_REFRESH_GOODS_INFO_CODE = 2;
    public static final String INTENT_QUANBU_ENABLED = "QuanbuEnabled";
    public static final String INTENT_CATEGORY_LIST = "CategoryList";
    public static final String INTENT_STATUS_CODE = "StatusCode";
    public static final String INTENT_STATUS_MSG = "StatusMsg";
    public static final String INTENT_FROM_HOME_CATE_ACTIVITY = "FromeHomeCateActivity";
    public static final int QUANBU_FRAGMENT_POSITION = 0; //全部插入位置，TV购、推荐、全部 三个是根据，位置一样表示依次是TV购、推荐、全部
    private static int DEFAULT_SELECTED_CATEGORY_POSITION = 0;
    private static final int DEFAULT_SELECTED_CATEGORY_NAME_RSID = R.string.jhs_quanbu;
    private int mContainerViewId;

    private boolean mQuanbuEnabled;
    private List<CategoryMO> mCategoryList;
    private String mCurrCateId;
    private String mType;
    //	private int mStatusCode = ServiceCode.SERVICE_OK.getCode().intValue();
    //	private String mStatusMsg;
    private String mCurrFragmentId;
    private HashMap<String, FragmentItem> mCategoryFragmentMap;
    private View mCurrSelectedView;
    private int mCurrSeletedPosition = -1;
    private boolean mListAdapterFocused;
    private FrameLayout mGoodsList;
    private HorizontalListView mCateogryListView;
    private AppHandler<HomeActivity> mRequestFocusHandler;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        AppDebug.i(TAG, TAG + ".onSaveInstanceState is running!");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne();

        AppDebug.i(TAG, TAG + ".onCreate is running!");
        mContainerViewId = R.id.HomeContainer;
        setContentView(R.layout.jhs_home_activity);
        if (SystemConfig.DIPEI_BOX) {
            setBackToHome(true);
        }
        //启动同步服务器的时间
        ServerTimeSynchronizer.getCurrentTime();
        Intent intent = getIntent();
        mCurrCateId = null;
        mType = "cate";
        if (intent != null) {
            mQuanbuEnabled = intent.getBooleanExtra(INTENT_QUANBU_ENABLED, false);
            mCategoryList = intent.getParcelableArrayListExtra(INTENT_CATEGORY_LIST);
            AppDebug.i(TAG, TAG + ".mCategoryList=" + mCategoryList);
            mCurrCateId = IntentDataUtil.getString(intent, "cateId", null);
            mType = IntentDataUtil.getString(intent, "type", "");
        }
        if (null != mCategoryList && mCategoryList.size() > 0) {
            createView();
            return;
        }

        View loadingView = findViewById(R.id.pre_loading);
        loadingView.setVisibility(View.VISIBLE);
        View contextView = findViewById(R.id.content_container);
        contextView.setVisibility(View.GONE);
        HomeItemTypes itemType = HomeItemTypes.valueOf(mType);
        switch (itemType) {
            case JMP:
                mCurrCateId = "1000090";//聚名品
            case CATE:
            default:
                requestCategory();
                break;
        }

    }

    /**
     * 自定义聚划算列表页打点
     */
    private void anaylisysTaoke() {
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            String stbId = DeviceUtil.initMacAddress(this);
            BusinessRequest.getBusinessRequest().requestTaokeJHSListAnalysis(stbId, User.getNick(), new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(this)));
        }
    }

    /**
     * 分类列表请求
     */
    private void requestCategory() {
        //        JuBusinessRequest.getBusinessRequest().requestCategory(this, 0, new CategoryBusinessRequestListener(this));
        MyBusinessRequest.getInstance().requestCategory(0, new JuRetryRequestListener<CountList<Option>>(this, true) {

            @Override
            public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                HomeActivity activity = null;
                if (baseActivity instanceof HomeActivity) {
                    activity = (HomeActivity) baseActivity;
                }
                if (null == activity) {
                    return;
                }
                if (data != null) {
                    List<CategoryMO> cates = ModelTranslator.translateCategory(data);
                    activity.mCategoryList = cates;
                    activity.createView();
                } else {
                    DialogUtils.show(activity, R.string.jhs_no_data);
                }
            }
        });
    }

    public void createView() {
        anaylisysTaoke();//自定义聚划算列表页打点

        View loadingView = findViewById(R.id.pre_loading);
        loadingView.setVisibility(View.GONE);
        View contextView = findViewById(R.id.content_container);
        contextView.setVisibility(View.VISIBLE);

        String defaultCategoryName = getString(DEFAULT_SELECTED_CATEGORY_NAME_RSID);

        /*
         * if ( mCategoryList == null){
         * mStatusCode = intent.getIntExtra(INTENT_STATUS_CODE, ServiceCode.HTTP_ERROR.getCode().intValue());
         * mStatusMsg = intent.getStringExtra(INTENT_STATUS_MSG);
         * }
         */
        if (mCategoryList != null) {

            //去除车品:36000L 及聚家装:1000091L
            for (Iterator<CategoryMO> iter = mCategoryList.iterator(); iter.hasNext(); ) {
                CategoryMO cate = iter.next();
                if (cate.getCid().equals("1000091")) {
                    iter.remove();
                } else if (cate.getCid().equals("1000090") && cate.getName().length() > 2) {
                    cate.setName(cate.getName().substring(1));
                }/* else if (cate.getName().length() > 2) {
                    cate.setName(cate.getName().substring(0, 2));
                }*/

            }

            mCategoryFragmentMap = new HashMap<String, FragmentItem>();

            //加入全部商品
            String quanbuString = getString(R.string.jhs_quanbu);
            if (mQuanbuEnabled) {
                CategoryMO quanbu = new CategoryMO();
                quanbu.setName(quanbuString);
                quanbu.setCid(MyBusinessRequest.VIDEO_CATEGORY_ID);
                mCategoryList.add(QUANBU_FRAGMENT_POSITION, quanbu);
            }

            mCurrSeletedPosition = DEFAULT_SELECTED_CATEGORY_POSITION;
            for (int i = 0, len = mCategoryList.size(); i < len; ++i) {
                BaseFragment fragment = null;
                CategoryMO iCatalog = mCategoryList.get(i);
                if (!StringUtil.isEmpty(mCurrCateId) && iCatalog.getCid().equals(mCurrCateId)) {
                    mCurrSeletedPosition = i;
                    DEFAULT_SELECTED_CATEGORY_POSITION = i;
                } else if (!StringUtil.isEmpty(mCurrCateId) && iCatalog.getName().equals(defaultCategoryName)) {
                    mCurrSeletedPosition = i;
                    DEFAULT_SELECTED_CATEGORY_POSITION = i;
                }
                AppDebug.i(TAG, TAG + ".createView mCurrSeletedPosition=" + mCurrSeletedPosition + ", mCurrCateId="
                        + mCurrCateId + ", iCatalog.getCid()=" + iCatalog.getCid() + ", mCurrCateId is empty="
                        + !StringUtil.isEmpty(mCurrCateId));

                fragment = CategoryFragment.newInstance(iCatalog, mContainerViewId, iCatalog.getName(), this);

                FragmentItem item = new FragmentItem();
                item.mFragment = fragment;
                item.mIsAdder = false;
                item.position = i;
                mCategoryFragmentMap.put(iCatalog.getCid(), item);
            }
            AppDebug.i(TAG, "HomeActivity-onCreate mCurrSeletedPosition = " + mCurrSeletedPosition + ", mCurrCateId = "
                    + mCurrCateId + ", DEFALUT_SELECTED_CATEGORY = " + DEFAULT_SELECTED_CATEGORY_POSITION);
            switchListItem(Math.max(mCurrSeletedPosition, DEFAULT_SELECTED_CATEGORY_POSITION));

        }

        //分类导航
        mCateogryListView = (HorizontalListView) findViewById(R.id.category_List_bar);
        final ListAdapter adapter = new ListAdapter();
        mCateogryListView.setAdapter(adapter);
        mCateogryListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i(TAG, TAG + ".mCateogryList-onFocusChange, hasFocus=" + hasFocus + ", mCurrSelectedView="
                        + mCurrSelectedView);
                mListAdapterFocused = hasFocus;
                if (mCurrSelectedView == null) {
                    mCurrSelectedView = mCateogryListView.getChildAt(DEFAULT_SELECTED_CATEGORY_POSITION);
                }

                switchFocusViewBackground(mCurrSelectedView, hasFocus, false);
                if (!hasFocus) {
                    //统计代码 统计聚划算各类目的使用情况
                    if (mCurrSeletedPosition >= 0 && mCurrSeletedPosition < mCategoryList.size()) {
                        CategoryMO cate = mCategoryList.get(mCurrSeletedPosition);
                        if (null != cate) {
                            String controlName = Utils.getControlName(getFullPageName(), "Switch", null);
                            Map<String, String> p = Utils.getProperties();
                            if (!StringUtil.isEmpty(cate.getName())) {
                                p.put("cate_name", cate.getName());
                            }
                            Utils.utCustomHit(getFullPageName(), controlName, p);
                        }
                    }
                }
            }
        });
        mCateogryListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                AppDebug.i(TAG, TAG + ".mCateogryList-onItemClick, arg0=" + arg0 + ", arg1=" + arg1 + ", position="
                        + position + ", mCurrSeletedPosition=" + mCurrSeletedPosition + ", arg3=" + arg3);
                if (position != mCurrSeletedPosition) {
                    mCurrSelectedView = arg1;
                    mCurrSeletedPosition = position;
                    arg0.clearFocus();
                    switchListItem(position);
                }
            }
        });
        mCateogryListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppDebug.i(TAG, TAG + ".mCateogryList-onItemSelected, position=" + position + ", id=" + id + ", view="
                        + view);
                if (view != null) {
                    //                    switchFocusViewBackground(mCurrSelectedView, false, true);
                    //                    switchFocusViewBackground(view, true, false);
                    mListAdapterFocused = true;
                    mCurrSeletedPosition = position;
                    mCurrSelectedView = view;
                }

                switchListItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mGoodsList = (FrameLayout) findViewById(R.id.fragment_container);

        if (mCurrSeletedPosition > 0) {
            mCateogryListView.setSelection(mCurrSeletedPosition);
        }
        delayFragmentRequestFocus();
    }

    @Override
    protected void onResume() {
        AppDebug.i(TAG, TAG + ".onResume is running");
        super.onResume();
    }

    @Override
    protected void onPause() {
        AppDebug.i(TAG, TAG + ".onPause is running");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        onRemoveKeepedActivity();
        AppDebug.i(TAG, TAG + ".onDestroy");
        //销毁商品数据
        if (null != mCategoryList && mCategoryList.size() > 0) {
            for (int i = 0; i < mCategoryList.size(); i++) {
                CategoryMO cate = mCategoryList.get(i);
                MyBusinessRequest.getInstance().removeMCategoryItemData(cate.getCid());
            }
        }

        DEFAULT_SELECTED_CATEGORY_POSITION = 0;

        if (null != mCategoryFragmentMap) {
            mCategoryFragmentMap.clear();
        }
        if (null != categoryTextViews) {
            categoryTextViews.clear();
        }
        if (null != switchBackgroundHandler) {
            switchBackgroundHandler.removeCallbacksAndMessages(null);
        }
        if (null != checkAnimationHandler) {
            checkAnimationHandler.removeCallbacksAndMessages(null);
        }
        if (null != mRequestFocusHandler) {
            mRequestFocusHandler.removeCallbacksAndMessages(null);
            mRequestFocusHandler = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String type = IntentDataUtil.getString(intent, "type", "");
        String cateId = IntentDataUtil.getString(intent, "cateId", null);
        AppDebug.i(TAG, TAG + ".onNewIntent type=" + type + ", cateId=" + cateId);
        if (mType == type || (null == type && !StringUtil.isEmpty(cateId))) {
            if (StringUtil.isEmpty(cateId) || null == mCategoryList || mCategoryList.size() <= 0) {
                return;
            }
            defaultCateId = cateId; //设置当前切换到这个分类，在onResume时，会自动定位到这个分类
            AppDebug.i(TAG, TAG + ".onNewIntent is running!");
            jumpToDefaultCate();
        } else {
            mCategoryList.clear();
            gcFragment(null, 0);
            mCategoryFragmentMap.clear();
            mCurrFragmentId = null;
            mCurrCateId = IntentDataUtil.getString(intent, "cateId", null);
            mType = type;
            View loadingView = findViewById(R.id.pre_loading);
            loadingView.setVisibility(View.VISIBLE);
            View contextView = findViewById(R.id.content_container);
            contextView.setVisibility(View.GONE);
            HomeItemTypes itemType = HomeItemTypes.valueOf(mType);
            switch (itemType) {
                case JMP:
                    mCurrCateId = "1000090";//聚名品
                case CATE:
                default:
                    requestCategory();
                    break;
            }
        }
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!StringUtil.isEmpty(mCurrCateId)) {
            p.put("cate_id", mCurrCateId);
        }
        return p;
    }

    private AppHandler<HomeActivity> switchBackgroundHandler = new SwitchBackgroundHandle(this);

    private void switchFocusViewBackground(View view, boolean isFocused, boolean loseAn) {
        if (null == view) {
            return;
        }
        AppDebug.i(
                TAG,
                TAG + ".switchFocusViewBackground mCateogryList view="
                        + ((TextView) view.findViewById(R.id.item_tv)).getText());
        int position = (Integer) view.getTag();
        AppDebug.i(
                TAG,
                TAG
                        + ".switchFocusViewBackground mCateogryList checkAnimationHandler and switchBackgroundHandler.removeMessges position="
                        + position);
        switchBackgroundHandler.removeMessages(position);
        checkAnimationHandler.removeMessages(position);
        Message msg = switchBackgroundHandler.obtainMessage(position, isFocused ? 1 : 0, loseAn ? 1 : 0, view);
        switchBackgroundHandler.sendMessageDelayed(msg, 50);
    }

    /**
     * 保存分类的TextView，在某些机器上2次view.findViewById(R.id.item_tv);得到的结果不是同一个
     * 而在部分机器上似乎不存在这样的问题，好坑爹啊
     */
    @SuppressLint("UseSparseArrays")
    private Map<Integer, TextView> categoryTextViews = new HashMap<Integer, TextView>();

    private void switchFocusViewBackgroundImp(int position, View view, boolean isFocused, boolean loseAn) {
        AppDebug.i(TAG, TAG + ".mCateogryList.switchFocusViewBackground position=" + position + ", isFocused="
                + isFocused + ", loseAn=" + loseAn + ", view=" + view);
        if (view != null) {
            TextView tv = categoryTextViews.get(position);
            if (null == tv) {
                tv = (TextView) view.findViewById(R.id.item_tv);
                categoryTextViews.put(position, tv);

            }
            AppDebug.i(TAG, TAG + ".mCateogryList.switchFocusViewBackground position=" + position + ", tv="
                    + tv.getText().toString());
            if (isFocused) {
                if (SystemConfig.DIPEI_BOX) {
                    BitmapDrawable bd;
                    tv.setBackgroundResource(R.drawable.jhs_navigation_item_bg_selector);
                } else {
                    tv.setBackgroundResource(R.drawable.jhs_navigation_animaition_down);
                    AnimationDrawableDoneReset anim = new AnimationDrawableDoneReset(position, tv, getResources()
                            .getDrawable(R.drawable.jhs_navigation_item_bg_selector));
                    anim.playAnimation();
                }
            } else {
                if (!loseAn) {
                    if (SystemConfig.DIPEI_BOX) {
                        tv.setBackgroundResource(R.drawable.jhs_navigation_ani_0);
                    } else {
                        tv.setBackgroundResource(R.drawable.jhs_navigation_animaition_up);
                        //                    AnimationDrawableDoneReset anim = new AnimationDrawableDoneReset(tv, getResources().getDrawable(R.drawable.navigation_ani_0));
                        //                    anim.playAnimation();
                        AnimationDrawable frameAnimation = (AnimationDrawable) tv.getBackground();
                        if (!frameAnimation.isRunning()) {
                            frameAnimation.start();
                        } else {
                            frameAnimation.stop();
                            frameAnimation.start();
                        }
                    }
                } else {
                    tv.setBackgroundResource(R.drawable.jhs_navigation_item_bg_selector);
                    //                    AnimationDrawableDoneReset anim = new AnimationDrawableDoneReset(tv, getResources().getDrawable(
                    //                            R.drawable.navigation_item_bg_selector));
                    //                    anim.playAnimation();
                }
            }
        }
    }

    public void switchListItem(int position) {
        AppDebug.i(TAG, TAG + ".switchListItem position=" + position);
        if (null == mCategoryList || position < 0 || position >= mCategoryList.size()) {
            return;
        }
        CategoryMO iCatalog = mCategoryList.get(position);
        if (mCurrFragmentId == iCatalog.getCid()) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        FragmentItem fromFragment = mCategoryFragmentMap.get(mCurrFragmentId);
        FragmentItem toFragment = getFragmentItem(position);
        //        FragmentItem toFragment = mCategoryFragmentMap.get(iCatalog.getCid());
        AppDebug.i(TAG, "switchListItem iCatalog.ci = " + iCatalog.getCid() + ", position = " + position + ", "
                + toFragment);
        if (StringUtil.isEmpty(mCurrFragmentId)) {
            AppDebug.i(TAG, "switchListItem position = " + position + ", mCurrFragmentId = " + mCurrFragmentId);
            transaction.add(R.id.fragment_container, toFragment.mFragment).commit();
            toFragment.mIsAdder = true;

            onFragmentShow(transaction, toFragment.mFragment);
        } else {
            if (toFragment != null && fromFragment != null) {
                AppDebug.i(TAG, "switchListItem toFragment.mFragment = " + toFragment.mFragment + ", position = "
                        + toFragment.position + ", " + "toFragment.mIsAdder=" + toFragment.mIsAdder);
                onFragmentHide(transaction, fromFragment.mFragment);
                if (!toFragment.mIsAdder) {
                    transaction.hide(fromFragment.mFragment).add(R.id.fragment_container, toFragment.mFragment)
                            .commit();
                    toFragment.mIsAdder = true;
                } else {
                    transaction.hide(fromFragment.mFragment).show(toFragment.mFragment).commit();
                }
                onFragmentShow(transaction, toFragment.mFragment);
            }
        }

        AppDebug.i(TAG, "switchListItem mCurrFragmentId=" + mCurrFragmentId + " switchTo=" + iCatalog.getCid()
                + " position=" + position + " toFragment=" + toFragment + " fromFragment=" + fromFragment);
        mCurrFragmentId = iCatalog.getCid();

        gcCategoryFragment(position);

    }

    private void gcCategoryFragment(int position) {
        // 默认保留2页内容
        int savePagenum = 2;
        if (SystemConfig.DIPEI_BOX) {//低配不保留
            savePagenum = 0;
        }
        AppDebug.i(TAG, TAG + ".gcCategoryFragment DIPEI_BOX=" + SystemConfig.DIPEI_BOX + ", savePagenum="
                + savePagenum);
        // 释放资源
        gcFragment(position, savePagenum);
        gcCategoryRequestData(position, savePagenum);
    }

    /**
     * 创建一个fragment
     *
     * @param position
     * @return
     */
    private FragmentItem getFragmentItem(int position) {
        CategoryMO iCatalog = mCategoryList.get(position);
        FragmentItem fragmentItem = mCategoryFragmentMap.get(iCatalog.getCid());
        if (null != fragmentItem && null == fragmentItem.mFragment) {
            BaseFragment fragment = null;

            fragment = CategoryFragment.newInstance(iCatalog, mContainerViewId, iCatalog.getName(), this);

            fragmentItem.mFragment = fragment;
        }
        return fragmentItem;
    }

    /**
     * 回收fragment
     *
     * @param position
     */
    private void gcFragment(Integer position, int savePagenum) {
        if (null != position && position < 0) {
            return;
        }
        int size = mCategoryList.size();
        for (int i = 0; i < size - 1; i++) {
            if (null == position || i < position - savePagenum || i > position + savePagenum) {
                CategoryMO iCatalog = mCategoryList.get(i);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                FragmentItem fragmentItem = mCategoryFragmentMap.get(iCatalog.getCid());
                if (null != fragmentItem && null != fragmentItem.mFragment
                        && fragmentItem.mFragment.getActivity() != null) {
                    transaction.remove(fragmentItem.mFragment).commit();
                    fragmentItem.mFragment.recyleMemory();
                    fragmentItem.mIsAdder = false;
                    fragmentItem.mFragment = null;
                }
            }
        }
    }

    public void onFragmentHide(FragmentTransaction transaction, Fragment fragment) {
        AppDebug.i(TAG, TAG + ".onFragmentHide is running");
        if (fragment != null) {
            BaseFragment f = (BaseFragment) fragment;
            f.onHide(transaction);
        }
    }

    public void onFragmentShow(FragmentTransaction transaction, Fragment fragment) {
        AppDebug.i(TAG, TAG + ".onFragmentShow is running");
        if (fragment != null) {
            BaseFragment f = (BaseFragment) fragment;
            f.onShow(transaction);
        }
    }

    /**
     * Class Descripton.
     *
     * @author hanqi
     * @data 2014-11-18 上午11:11:07
     */
    private static final class RequestFocusHandle extends AppHandler<HomeActivity> {

        /**
         * @param t
         */
        private RequestFocusHandle(HomeActivity t) {
            super(t);
        }

        @Override
        public void handleMessage(Message msg) {

            HomeActivity activity = getT();
            if (null == activity) {
                return;
            }
            AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle msg=" + msg);
            FragmentItem currFragmentItem = activity.mCategoryFragmentMap.get(activity.mCurrFragmentId);
            if (null == currFragmentItem || null == currFragmentItem.mFragment) {
                sendEmptyMessageDelayed(0, 100);
                return;
            }
            AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>1");
            if (currFragmentItem.mFragment instanceof CategoryFragment) {
                AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>2");
                View view = currFragmentItem.mFragment.getView();
                if (null == view) {
                    sendEmptyMessageDelayed(0, 100);
                    return;
                }
                AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>3 view=" + view);
                ViewPager pageView = (ViewPager) view.findViewById(R.id.goods_page);
                if (null == pageView) {
                    sendEmptyMessageDelayed(0, 100);
                    return;
                }
                AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>4");
                int position = pageView.getCurrentItem();
                AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>5 position=" + position);
                View pageItemView = view.findViewWithTag(currFragmentItem.mFragment.getId() + "_" + position);
                if (null == pageItemView) {
                    sendEmptyMessageDelayed(0, 100);
                    return;
                }
                pageItemView.requestFocus();
                AppDebug.i(activity.TAG, activity.TAG + ".RequestFocusHandle ==>6 pageItemView=" + pageItemView);
            } else if (currFragmentItem.mFragment instanceof BaseFragment) {
                currFragmentItem.mFragment.requestFocus();
            }
        }
    }

    /**
     * Class Descripton.
     *
     * @author hanqi
     * @data 2014-11-18 上午11:09:46
     */
    private static final class CheckAnimationHandle extends AppHandler<HomeActivity> {

        /**
         * @param t
         */
        private CheckAnimationHandle(HomeActivity t) {
            super(t);
        }

        public void handleMessage(Message msg) {
            HomeActivity activity = getT();
            if (null == activity) {
                return;
            }
            AnimationDrawableDoneReset animationDrawableDoneReset = (AnimationDrawableDoneReset) msg.obj;
            AnimationDrawable mAnimationDrawable = animationDrawableDoneReset.getmAnimationDrawable();

            /**
             * 要加这个次数判断，否则会死循环，AnimationDrawableDoneReset实际已经停止，但状态不对，肯定有原生代码BUG，原生不可信啊
             */
            if (msg.arg1 < AnimationDrawableDoneReset.CHECK_ANIMATION_IS_DONE_TIMES
                    && null != mAnimationDrawable
                    && mAnimationDrawable.isRunning()
                    && mAnimationDrawable.isVisible()
                    && mAnimationDrawable.getCurrent() != mAnimationDrawable.getFrame(mAnimationDrawable
                    .getNumberOfFrames() - 1)) {

                AppDebug.i(
                        activity.TAG,
                        activity.TAG
                                + ".mCateogryList-AnimationDrawableDoneReset.checkIfAnimationDone mAnimationDrawable.isOneShot()="
                                + mAnimationDrawable.isOneShot() + ", mAnimationDrawable.isRunning()="
                                + mAnimationDrawable.isRunning() + ", mAnimationDrawable.isVisible()="
                                + mAnimationDrawable.isVisible() + ", mAnimationDrawable.getCurrent()="
                                + mAnimationDrawable.getCurrent()
                                + ", mAnimationDrawable.getFrame(mAnimationDrawable.getNumberOfFrames()-1)="
                                + mAnimationDrawable.getFrame(mAnimationDrawable.getNumberOfFrames() - 1)
                                + ", msg.arg1=" + msg.arg1 + ", duration()="
                                + animationDrawableDoneReset.getmDuration());

                Message newMsg = obtainMessage(msg.what, msg.arg1 + 1, msg.arg2, msg.obj);
                sendMessageDelayed(newMsg, animationDrawableDoneReset.getmDuration());
            } else {
                animationDrawableDoneReset.setRealBackgrond();
            }
        }
    }

    /**
     * Class Descripton.
     *
     * @author hanqi
     * @data 2014-11-18 上午11:08:20
     */
    private static final class SwitchBackgroundHandle extends AppHandler<HomeActivity> {

        /**
         * @param t
         */
        private SwitchBackgroundHandle(HomeActivity t) {
            super(t);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeActivity activity = getT();
            if (null == activity) {
                return;
            }
            boolean isFocused = msg.arg1 == 1 ? true : false;
            boolean loasAn = msg.arg2 == 1 ? true : false;
            View view = (View) msg.obj;
            int position = msg.what;
            activity.switchFocusViewBackgroundImp(position, view, isFocused, loasAn);
        }
    }

    private class FragmentItem {

        BaseFragment mFragment;
        boolean mIsAdder;
        Integer position;
    }

    /**
     * 是否还有下个Fragment
     *
     * @param cid
     * @return
     */
    public boolean haveNextFragment(Long cid) {
        Integer position = getFragmentPosition(cid);
        if (position + 1 < mCategoryList.size()) {
            return true;
        }
        return false;
    }

    /**
     * 是否还有前一个Fragment
     *
     * @param cid
     * @return
     */
    public boolean havePreFragment(Long cid) {
        Integer position = getFragmentPosition(cid);
        if (position > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取分类所在Fragment的位置
     *
     * @param cid
     * @return
     */
    public Integer getFragmentPosition(Long cid) {
        FragmentItem fragmentItem = mCategoryFragmentMap.get(cid);
        return fragmentItem.position;
    }

    private AppHandler<HomeActivity> checkAnimationHandler = new CheckAnimationHandle(this);

    public class AnimationDrawableDoneReset extends AnimationDrawable {

        public static final int CHECK_ANIMATION_IS_DONE_TIMES = 3;
        private AnimationDrawable mAnimationDrawable;
        private int mDuration;
        private Drawable mAnimDoneDrawable;
        private TextView mView;
        private int mPosition;

        public AnimationDrawableDoneReset(int position, TextView view, Drawable animDoneDrawable) {
            mView = view;
            mPosition = position;
            mAnimationDrawable = (AnimationDrawable) mView.getBackground();
            for (int i = 0; i < mAnimationDrawable.getNumberOfFrames(); i++) {
                mDuration += mAnimationDrawable.getDuration(i);
            }
            mAnimDoneDrawable = animDoneDrawable;
            AppDebug.i(TAG, TAG + ".mCateogryList.AnimationDrawableDoneReset mAnimDoneDrawable=" + mAnimDoneDrawable);
        }

        private void checkIfAnimationDone() {
            AppDebug.i(
                    TAG,
                    TAG
                            + ".mCateogryList.AnimationDrawableDoneReset.checkIfAnimationDone checkAnimationHandler remveMessage mPosition="
                            + mPosition);
            checkAnimationHandler.removeMessages(mPosition);
            Message msg = checkAnimationHandler.obtainMessage(mPosition, this);
            checkAnimationHandler.sendMessageDelayed(msg, mDuration);
            //            checkAnimationHandler.sendEmptyMessageDelayed(what, delayMillis)(msg, mDuration);
        }

        ;

        public void playAnimation() {
            AppDebug.i(TAG, TAG + ".mCateogryList-AnimationDrawableDoneReset.playAnimation mView=" + mView);
            if (mView == null) {
                return;
            }
            if (mAnimationDrawable.isRunning()) {
                AppDebug.i(TAG, TAG
                        + ".mCateogryList-AnimationDrawableDoneReset.playAnimation mAnimationDrawable.isRunning()=true");
                mAnimationDrawable.stop();
                setRealBackgrond();
            } else {
                AppDebug.i(
                        TAG,
                        TAG
                                + ".mCateogryList-AnimationDrawableDoneReset.playAnimation mAnimationDrawable.isRunning()=false");
                mAnimationDrawable.start();
                checkIfAnimationDone();
            }
        }

        public void setRealBackgrond() {
            AppDebug.i(TAG, TAG + ".AnimationDrawableDoneReset.setRealBackgrond mAnimDoneDrawable=" + mAnimDoneDrawable);
            mView.setBackgroundDrawable(mAnimDoneDrawable);
        }

        public AnimationDrawable getmAnimationDrawable() {
            return mAnimationDrawable;
        }

        public int getmDuration() {
            return mDuration;
        }

    }

    @Override
    public void onBackPressed() {
        AppDebug.i(TAG, TAG + ".onBackPressed is running");
        if (null != mGoodsList && mGoodsList.findFocus() != null && null != mCateogryListView) {
            mCateogryListView.requestFocus();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 在fragment中切换
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i(TAG, TAG + ".onKeyDown is running!");
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_ESCAPE || keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_MENU) {
            if (null != mRequestFocusHandler) {
                mRequestFocusHandler.removeCallbacksAndMessages(null);
                mRequestFocusHandler = null;
            }
        }
        if (null != mGoodsList && mGoodsList.findFocus() != null) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mCurrSeletedPosition >= 0) {
                if (mCurrSeletedPosition <= 0) {//最前一个分类最前一个商品往前翻商品，提示没有更多商品了
                    //                    AppHolder.toast(R.string.jhs_no_more_goods);
                    return true;
                }
                int nextPosition = mCurrSeletedPosition - 1;
                switchListItem(nextPosition);
                //                mCateogryListView.requestFocus();

                AppDebug.i(
                        TAG,
                        TAG
                                + ".onKeyDown mCateogryList checkAnimationHandler and switchBackgroundHandler.removeMessges position="
                                + mCurrSeletedPosition);
                switchBackgroundHandler.removeMessages(mCurrSeletedPosition);
                checkAnimationHandler.removeMessages(mCurrSeletedPosition);

                if (null != categoryTextViews) {
                    TextView categoryTextView = categoryTextViews.get(mCurrSeletedPosition);
                    if (null == categoryTextView) {
                        View itemView = mCateogryListView.getSelectedView();
                        if (null != itemView) {
                            categoryTextView = (TextView) itemView.findViewById(R.id.item_tv);
                            categoryTextViews.put(mCurrSeletedPosition, categoryTextView);
                        }
                    }
                    if (null != categoryTextView) {
                        categoryTextView.setBackgroundResource(R.drawable.jhs_navigation_item_bg_selector);
                        //                        categoryTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigation_item_bg_selector));
                    }
                }

                mCateogryListView.setSelection(nextPosition);
                switchFocusViewBackground(mCateogryListView.getSelectedView(), false, false);
                delayFragmentRequestFocus();
                //                fragmentRequestFocus();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && mCurrSeletedPosition < mCategoryList.size()) {
                if (mCurrSeletedPosition + 1 >= mCategoryList.size()) {//最后一个分类最后一个商品往后翻提示没有更多商品了
                    //                    AppHolder.toast(R.string.jhs_no_more_goods);
                    return true;
                }
                int nextPosition = mCurrSeletedPosition + 1;
                switchListItem(nextPosition);
                //                mCateogryListView.requestFocus();

                AppDebug.i(
                        TAG,
                        TAG
                                + ".onKeyDown mCateogryList checkAnimationHandler and switchBackgroundHandler.removeMessges position="
                                + mCurrSeletedPosition);
                switchBackgroundHandler.removeMessages(mCurrSeletedPosition);
                checkAnimationHandler.removeMessages(mCurrSeletedPosition);

                if (null != categoryTextViews) {
                    TextView categoryTextView = categoryTextViews.get(mCurrSeletedPosition);
                    if (null == categoryTextView) {
                        View itemView = mCateogryListView.getSelectedView();
                        if (null != itemView) {
                            categoryTextView = (TextView) itemView.findViewById(R.id.item_tv);
                            categoryTextViews.put(mCurrSeletedPosition, categoryTextView);
                        }
                    }
                    if (null != categoryTextView) {
                        categoryTextView.setBackgroundDrawable(getResources().getDrawable(
                                R.drawable.jhs_navigation_item_bg_selector));
                    }
                }

                mCateogryListView.setSelection(nextPosition);
                switchFocusViewBackground(mCateogryListView.getSelectedView(), false, false);
                delayFragmentRequestFocus();
                //                fragmentRequestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        AppDebug.i(TAG, TAG + ".onStart is running");
        super.onStart();
        jumpToDefaultCate();
    }

    @Override
    public void onLowMemory() {
        AppDebug.i(TAG, TAG + ".onLowMemory is running!");
        super.onLowMemory();

        //        gcFragment(mCurrSeletedPosition); //回事fragment，
        //        gcCategoryRequestData(mCurrSeletedPosition); //回事商品列表数据
        gcCategoryFragment(mCurrSeletedPosition);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void jumpToDefaultCate() {
        if (!StringUtil.isEmpty(defaultCateId)) {
            FragmentItem item = mCategoryFragmentMap.get(defaultCateId);
            if (null != item) {
                TextView cagetoryTextView = categoryTextViews.get(mCurrSeletedPosition);
                if (null != cagetoryTextView) {
                    cagetoryTextView.setBackgroundResource(R.drawable.jhs_navigation_item_bg_selector);
                } else {
                    View view = mCateogryListView.getSelectedView();
                    if (null != view) {
                        switchFocusViewBackground(view, true, false);
                    }
                }

                switchListItem(item.position);
                mCateogryListView.requestFocus();
                mCateogryListView.setSelection(item.position);
                mCurrSelectedView = mCateogryListView.getSelectedView();
                if (null == mCurrSelectedView) {
                    mCurrSelectedView = mCateogryListView.getAdapter().getView(item.position, null, mCateogryListView);
                }
                mCurrSeletedPosition = item.position;
                if (null == item || null == item.mFragment) {
                    return;
                }
                View view = item.mFragment.getView();
                if (null != view) {
                    view.requestFocus();
                }
            }
            defaultCateId = null;
            fragmentRequestFocus();
        }
    }

    /**
     * 回收商品列表数据
     */
    public void gcCategoryRequestData(int position, int savePageNum) {
        AppDebug.i(TAG, TAG + ".gcCategoryRequestData position=" + position + ", savePageNum=" + savePageNum);
        if (position < 0) {
            return;
        }
        for (int i = 0; i < mCategoryList.size(); i++) {
            if (i < position - savePageNum || i > position + savePageNum) {
                CategoryMO cate = mCategoryList.get(i);
                AppDebug.i(TAG, TAG + ".gcCategoryRequestData i=" + i + ", cate=" + cate);
                MyBusinessRequest.getInstance().removeMCategoryItemData(cate.getCid());
            }
        }
    }

    /**
     * 让商品获取焦点
     */
    public void fragmentRequestFocus() {
        FragmentItem currFragmentItem = mCategoryFragmentMap.get(mCurrFragmentId);
        if (null == currFragmentItem || null == currFragmentItem.mFragment) {
            return;
        }
        currFragmentItem.mFragment.requestFocus();
    }

    private void delayFragmentRequestFocus() {
        mRequestFocusHandler = new RequestFocusHandle(this);
        mRequestFocusHandler.sendEmptyMessage(0);
    }

    public HorizontalListView getMCateogryListView() {
        return mCateogryListView;
    }

    @Override
    public String getPageName() {
        return "Category";
    }

    @Override
    protected boolean isTbs() {
        return true;
    }

    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mCategoryList == null) {
                return 0;
            } else {
                return mCategoryList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (mCategoryList == null || mCategoryList.size() == 0) {
                return null;
            } else {
                return mCategoryList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.jhs_navigation_item, null);
            }

            CategoryMO item = mCategoryList.get(position);

            TextView tv = (TextView) convertView.findViewById(R.id.item_tv);
            if (position == DEFAULT_SELECTED_CATEGORY_POSITION) {
                if (mCateogryListView.hasFocus()) {
                    tv.setBackgroundResource(R.drawable.jhs_navigation_focus);
                } else {
                    tv.setBackgroundResource(R.drawable.jhs_navigation_ani_0);
                }
            }
            tv.setText(item.getName());
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.requestFocus();
                }
            });
            //此处无需设置focus，默认载入导航无需焦点
            if (mCurrSeletedPosition == position && !mListAdapterFocused) {
                mCurrSelectedView = convertView;
            }

            convertView.setTag(position);

            return convertView;

        }
    }

    /**
     * 淘客打点监听
     */
    private static class TaokeBussinessRequestListener extends BizRequestListener<JSONObject> {

        public TaokeBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
//            AppDebug.d(TAG,data.toString());
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private void onKeepActivityOnlyOne() {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onDestroyActivityOfList(HomeActivity.class.getName());
        manager.onAddDestroyActivityToList(HomeActivity.class.getName(), this);
    }

    private void onRemoveKeepedActivity() {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onRemoveDestroyActivityFromList(HomeActivity.class.getName(), this);
    }

}
