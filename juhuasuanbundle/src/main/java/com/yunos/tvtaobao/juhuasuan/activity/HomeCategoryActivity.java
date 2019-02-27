package com.yunos.tvtaobao.juhuasuan.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.display.FadeInBitmapDisplayer;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeItemsBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.enumeration.OptionType;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToCategoryItems;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToCategoryItems.HomeBundle;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToHomeCategory;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToPinPaiTuan;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToTaoBaoSdk;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.config.ConstValues;
import com.yunos.tvtaobao.juhuasuan.config.ConstValues.HomeItemTypes;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.homepage.HomeIndexPageRelativeLayout;
import com.yunos.tvtaobao.juhuasuan.homepage.HomepageItems;
import com.yunos.tvtaobao.juhuasuan.homepage.IloadImageFormUri;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.ActivityResultListenerManager;
import com.yunos.tvtaobao.juhuasuan.util.GsonUtil;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.view.ScrollHorizontalScrollView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import java.util.List;

public class HomeCategoryActivity extends JuBaseActivity implements IloadImageFormUri {

    private static final String TBS_PAGE_TAG = "Home";

    public static final String TAG = "HomeCategoryActivity";

    public static final String CURRENT_PAGE = "Currentpage";
    //进其他页面前被选中的位置,注意是静态变量，该页面destroy后该值还是存在，然后再次onCreate时使用这个变量
    private static Integer mSelectionPosition;

    private HomeCatesResponse mCategorys;

    private List<CategoryMO> mListCategoryMo;

    private HomepageItems mHomepageItems;

    private int countSize = 0;

    private View load_progress;

    // 背景图片
    private View homeContainer;

    private FrameLayout pager;

    private IndexPageFragment mPageConainer;

    private ImageView title_icon;

    private ImageLoaderManager mImageLoaderManager;

    private int mCurrentPage = 0;
    /**
     * 是否是首次执行动画
     */
    private boolean firstAm = true;

    private boolean isFinish = false;

    private FrameLayout mVersionLayout;

    private ActivityResultListenerManager activityResultListenerManager;

    //首页聚焦定位Handle
    private FocusePositionHandle focusePositionHandle = new FocusePositionHandle(this);

    private View.OnClickListener mOnCategoryItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!NetWorkUtil.isNetWorkAvailable()) {
                NetWorkCheck.netWorkError(HomeCategoryActivity.this);
                return;
            }

            handlerItemClick(v);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstValues.INTENT_KEY_HOMEPAGEITEMS, mHomepageItems);
        outState.putInt(ConstValues.BUNDLE_KEY_SUMSIZE, countSize);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        AppDebug.i(TAG, TAG + ".onRestoreInstanceState");
        //        super.onRestoreInstanceState(savedInstanceState);

        addVersionCode();

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        activityResultListenerManager = ActivityResultListenerManager.getInstance();

        AppDebug.i(TAG, TAG + ".onCreate is running");
        if (savedInstanceState != null && savedInstanceState.containsKey(ConstValues.INTENT_KEY_HOMEPAGEITEMS)) {
            AppDebug.i(TAG, TAG + ".onCreate createView");
            createView(savedInstanceState);
        } else {
            AppDebug.i(TAG, TAG + ".onCreate getHomeCategoryItems");
            getHomeCategoryItems();
        }
    }

    protected void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ConstValues.INTENT_KEY_HOMEPAGEITEMS)) {
                mHomepageItems = (HomepageItems) savedInstanceState
                        .getSerializable(ConstValues.INTENT_KEY_HOMEPAGEITEMS);
            }
        } else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mHomepageItems = (HomepageItems) bundle.getSerializable(ConstValues.INTENT_KEY_HOMEPAGEITEMS);
            }
        }
        if (mHomepageItems != null) {
            mCategorys = mHomepageItems.HomeCates;
            mListCategoryMo = HomepageItems.convertJson(mHomepageItems.JsonListCategoryMo);
            countSize = getItemSize();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.style_launch_animation);
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(TAG);
        addVersionCode();

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        activityResultListenerManager = ActivityResultListenerManager.getInstance();

        AppDebug.i(TAG, TAG + ".onCreate is running");
        if (savedInstanceState != null && savedInstanceState.containsKey(ConstValues.INTENT_KEY_HOMEPAGEITEMS)) {
            AppDebug.i(TAG, TAG + ".onCreate createView");
            createView(savedInstanceState);
        } else {
            AppDebug.i(TAG, TAG + ".onCreate getHomeCategoryItems");
            getHomeCategoryItems();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tvtaobao.juhuasuan.activity.BaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        AppDebug.i(TAG, TAG + ".onResume");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        AppDebug.i(TAG, TAG + ".onStart");
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestart()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        AppDebug.i(TAG, TAG + ".onRestart isFinish=" + isFinish);
        if (!isFinish) {
            getHomeCategoryItems();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (null != mPageConainer) {
            mPageConainer.removeRunnable();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addVersionCode() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);

            mVersionLayout = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mVersionLayout.setLayoutParams(params);
            TextView versionCode = new TextView(this);
            versionCode.setTextColor(Color.WHITE);
            versionCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_18));
            versionCode.setText(pi.versionName);
            FrameLayout.LayoutParams textparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            textparams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            textparams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dp_25);
            mVersionLayout.addView(versionCode, textparams);

            setContentView(mVersionLayout);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createView(Bundle bundle) {

        //        if (ToHomeCategory.isFinished()) {
        //            finish();
        //            return;
        //        }
        restoreInstanceState(bundle);

        // 将窗口的背景设置为null，释放背景图
        getWindow().setBackgroundDrawable(null);

        if (mVersionLayout != null) {
            mVersionLayout.setVisibility(View.GONE);
        }
        setContentView(R.layout.jhs_category_home_page);
        // mContainer = (PagerContainer) findViewById(R.id.pager_container);
        load_progress = findViewById(R.id.load_progress);

        homeContainer = findViewById(R.id.HomeContainer);

        title_icon = (ImageView) findViewById(R.id.title_icon);
        // ViewPager pager = mContainer.getViewPager();

        pager = (FrameLayout) findViewById(R.id.viewpager);

        // 初始化界面
        init();

        load_progress.setVisibility(View.GONE);
        isFinish = true;
        ToHomeCategory.clean();
    }

    private void init() {
        // load_progress.setVisibility(View.GONE);
        if (mCategorys != null && mCategorys.getHomeBackground() != null) {
            String bg_url = mCategorys.getHomeBackground().getBg_img();
            if (bg_url != null && !"".equals(bg_url)) {
                mImageLoaderManager.loadImage(bg_url, new ImageLoadingListener() {

                    @Override
                    public void onLoadingCancelled(String arg0, View arg1) {
                    }

                    @Override
                    public void onLoadingComplete(String url, View view, Bitmap bitmap) {
                        // 刷新背景图片
                        BitmapDrawable drawable = new BitmapDrawable(bitmap);
                        drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
                        drawable.setDither(true);
                        homeContainer.setBackgroundDrawable(drawable);
                        //                        view.setBackgroundDrawable(drawable);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                    }

                    @Override
                    public void onLoadingStarted(String arg0, View arg1) {
                    }
                });
            }

            String icon_url = mCategorys.getHomeBackground().getLogo();
            if (icon_url != null && !"".equals(icon_url)) {
                loadImageFormUrl(title_icon, icon_url, R.drawable.jhs_logo);
            }
        }

        mPageConainer = new IndexPageFragment(this);
        pager.addView(mPageConainer);

        if (null != mSelectionPosition && mSelectionPosition >= 0) {
            focusePositionHandle.sendEmptyMessageDelayed(0, 100);
        }
    }

    /**
     * 定位到上次因为内存不足退出时聚焦的View
     */
    private void requestLastLiveFocuseView() {
        AppDebug.i(TAG, TAG + ".requestLastLiveFocuseView afterApiLoad mSelectionPosition=" + mSelectionPosition);
        if (null != mSelectionPosition && mSelectionPosition >= 0) {
            mPageConainer.getContainer().setIndexOfSelectedView(mSelectionPosition);
            final View view = mPageConainer.getContainer().getSelectedView();
            if (null != view) {

                mPageConainer.getContainer().setSelectedView(view);
                mSelectionPosition = null;
                mPageConainer.getContainer().scrollToRightView(view);
                AppDebug.i(TAG, TAG + ".init afterApiLoad mPageConainer.getContainer()="
                        + mPageConainer.getContainer().mIndex + ", " + mPageConainer.getContainer().getSelectedView());
            }
        }
    }

    private void getCategoryList(final HomeCatesResponse homeCates) {
        //        JuBusinessRequest.getBusinessRequest().requestGetOption(this, OptionType.Today.getplatformId(), null, 1, 200,
        //                new OptionBusinessRequestListener(this, homeCates));
        MyBusinessRequest.getInstance().requestGetOption(OptionType.Today.getplatformId(), null, 1, 200,
                new JuRetryRequestListener<CountList<Option>>(this, true) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                        HomeCategoryActivity activity = null;
                        if (baseActivity instanceof HomeCategoryActivity) {
                            activity = (HomeCategoryActivity) baseActivity;
                        }
                        if (null == activity) {
                            return;
                        }
                        CountList<CategoryMO> mListCategoryMo = ModelTranslator.translateCategory(data);
                        if (mListCategoryMo != null) {
                            Bundle bundle = new Bundle();
                            activity.mHomepageItems = new HomepageItems();
                            activity.mHomepageItems.HomeCates = homeCates;
                            activity.mHomepageItems.JsonListCategoryMo = JSON.toJSONString(mListCategoryMo);
                            bundle.putSerializable(ConstValues.INTENT_KEY_HOMEPAGEITEMS, activity.mHomepageItems);
                            activity.createView(bundle);
                        }

                    }
                });
    }

    private void getHomeCategoryItems() {
        MyBusinessRequest.getInstance().requestGetTmsHomeCates(
                new JuRetryRequestListener<HomeCatesResponse>(this, true) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, HomeCatesResponse response) {
                        if (!(baseActivity instanceof HomeCategoryActivity)) {
                            return;
                        }
                        HomeCategoryActivity activity = (HomeCategoryActivity) baseActivity;
                        activity.mCategorys = response;
                        activity.getCategoryList(activity.mCategorys);
                    }
                });
    }

    // 加载网络图片
    private void loadImageFormUrl(ImageView view, String url, int defaultId) {
        if (url != null) {
            DisplayImageOptions options = null;
            if (defaultId > 0) {
                options = new DisplayImageOptions.Builder().cacheOnDisc(true).bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT).showImageForEmptyUri(defaultId)
                        .showImageOnFail(defaultId).showImageOnLoading(defaultId).showImageForEmptyUri(defaultId)
                        .displayer(new FadeInBitmapDisplayer(0)).build();
            } else {
                options = new DisplayImageOptions.Builder().cacheOnDisc(true).bitmapConfig(Config.RGB_565)
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT).displayer(new FadeInBitmapDisplayer(0)).build();
            }
            AppDebug.i(TAG, TAG + ".loadImageFormUrl url=" + url);
            mImageLoaderManager.displayImage(url, view, options, new CustomsItemIconLoader());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityResultListenerManager.notifyListeners(requestCode, resultCode, data);
    }

    private CategoryMO findCategoryMoByID(String cid) {
        if (mListCategoryMo != null) {
            for (int i = 0; i < mListCategoryMo.size(); i++) {
                CategoryMO category = mListCategoryMo.get(i);
                if (category.getCid().equals(cid)) {
                    return category;
                }
            }
        }
        return null;
    }

    private void handlerItemClick(View v) {
        Object obj = v.getTag();
        if (obj == null) {
            return;
        }
        HomeItemTypes type = null;
        HomeItemsBo homeItem = null;
        HomeCatesBo homeCate = null;

        //埋点代码
        String iconName = null;
        if (obj instanceof HomeCatesBo) {
            homeCate = (HomeCatesBo) obj;
            type = HomeItemTypes.valueOfs(homeCate.getType());
            //            // 埋点统计点击次数。控件名称：Home_P_0_1, 参数：name=聚名品
            //            TBS.Adv.ctrlClicked(CT.Button, "Home_P_" + mCurrentPage + "_" + v.getTag(R.id.tag_position), "name="
            //                    + homeCate.getName());
            //埋点代码
            iconName = homeCate.getName();

        } else if (obj instanceof HomeItemsBo) {// 首页Item类目
            homeItem = (HomeItemsBo) obj;
            type = HomeItemTypes.valueOfs(homeItem.getType());
            //            // 埋点统计点击次数。控件名称：Home_P_0_1, 参数：name=聚名品
            //            TBS.Adv.ctrlClicked(CT.Button, "Home_P_" + mCurrentPage + "_" + v.getTag(R.id.tag_position), "name="
            //                    + homeItem.getTitle());
            iconName = homeItem.getTitle();
        }
        // 埋点统计点击次数。控件名称：Ju_Home_P_1_聚名品
        String controlName = Utils.getControlName(getFullPageName(), "P", (Integer) v.getTag(R.id.tag_position),
                iconName);

        TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(Utils.getProperties()));

        if (type != null) {
            AppDebug.i(TAG, TAG + ".handlerItemClick type=" + type.name() + ", obj=" + obj);
            // 选择跳转
            switch (type) {
                case JMP:
                    ToCategoryItems.category(this, homeCate, homeItem, null);
                    break;
                case CATE:
                    HomeBundle homeBundle = new HomeBundle(homeCate, homeItem, null);
                    if (!StringUtil.isEmpty(homeBundle.getCid())) {
                        CategoryMO category = findCategoryMoByID(homeBundle.getCid());
                        homeBundle.setData(category);
                        ToCategoryItems.category(this, homeBundle);
                    }
                    break;
                case ORDER:
                    ToTaoBaoSdk.order(this);
                    break;
                case PPT:
                    ToPinPaiTuan.ppt(this);
                    break;
                case PPTDETAIL:
                    ToPinPaiTuan.pptDetail(this, homeCate, homeItem);
                    break;
                default:
                    break;
            }
            //            if ((type == HomeItemTypes.JMP || type == HomeItemTypes.BDCATE || type == HomeItemTypes.CATE
            //                    || type == HomeItemTypes.PPT || type == HomeItemTypes.PPTDETAIL)
            //                    && SystemConfig.DIPEI_BOX) {
            //                finish();
            //            }
        }

    }

    private int getItemSize() {
        countSize = 0;
        if (mCategorys == null) {
            return countSize;
        }

        if (mCategorys.getItems() != null) {
            countSize = countSize + mCategorys.getItems().size();
        }

        if (mCategorys.getCates() != null) {
            countSize = countSize + mCategorys.getCates().size();
        }
        return countSize;
    }

    @Override
    protected void onDestroy() {
        AppDebug.i(TAG, TAG + ".onDestroy");
        ToHomeCategory.clean();
        super.onDestroy();
    }

    private static final class IndexPageFragmentHandle extends AppHandler<IndexPageFragment> {

        public IndexPageFragmentHandle(IndexPageFragment t) {
            super(t);
        }

    }

    private class IndexPageFragment extends FrameLayout
            implements HomeIndexPageRelativeLayout.onHomeItemFocusListener, HomeIndexPageRelativeLayout.OnRequestScroll {

        private View homeContainer;

        // 前景图片
        private ImageView firstFront, secondFront, thirdFront;

        private HomeIndexPageRelativeLayout mContainer;

        private int animationMoveBigX = getResources().getDimensionPixelSize(R.dimen.dp_30);
        private int animationMoveSmallX = getResources().getDimensionPixelSize(R.dimen.dp_15);

        private IndexPageFragmentHandle mHandler = new IndexPageFragmentHandle(this);

        private ScrollHorizontalScrollView mScroller;

        private Runnable moveAnimationRunnable;

        public IndexPageFragment(Context context) {
            super(context);
            init(context);
        }

        public View init(Context context) {
            View layout = LayoutInflater.from(context).inflate(R.layout.jhs_category_home_index, this, true);
            // FocusedRelativeLayout parent = initFocusLayout(layout);

            mContainer = (HomeIndexPageRelativeLayout) layout.findViewById(R.id.container);

            mScroller = (ScrollHorizontalScrollView) layout.findViewById(R.id.scroller);

            mContainer.setRequestScroll(this);

            // 前景图片
            homeContainer = layout.findViewById(R.id.homeContainer);
            firstFront = (ImageView) homeContainer.findViewById(R.id.first_front);
            secondFront = (ImageView) homeContainer.findViewById(R.id.second_front);
            thirdFront = (ImageView) homeContainer.findViewById(R.id.third_front);

            showHomePage(true);

            mContainer.updateValues(mCategorys, HomeCategoryActivity.this, mOnCategoryItemClickListener, this);
            mContainer.setCurrentPage(0);
            mContainer.requestFocus();

            return layout;
        }

        private void showHomePage(boolean visable) {
            if (visable) {
                homeContainer.setVisibility(View.VISIBLE);
                homeContainer.bringToFront();
            } else {
                homeContainer.setVisibility(View.GONE);
            }
        }

        private void moveAnimation(final View view, final int moveX) {
            AppDebug.i(TAG, TAG + ".moveAnimation view=" + view + ", moveX=" + moveX);

            final TranslateAnimation mRightAnimation = new TranslateAnimation(0.0f, moveX, 0, 0);
            mRightAnimation.setDuration(100);
            mRightAnimation.setInterpolator(new LinearInterpolator());

            mRightAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.clearAnimation();
                    view.layout(view.getLeft() + moveX, view.getTop(), view.getRight() + moveX, view.getBottom());
                    // view.setVisibility(View.VISIBLE);
                }
            });
            view.startAnimation(mRightAnimation);
            AppDebug.i(TAG, TAG + ".Animation toLeft2=" + view.getX());
        }

        private void runAnimation(final View view, final int moveX) {
            moveAnimationRunnable = new Runnable() {

                @Override
                public void run() {
                    moveAnimation(view, moveX);
                }
            };
            mHandler.postAtFrontOfQueue(moveAnimationRunnable);
        }

        @Override
        public void onFocusChanged(View view, int position, boolean hasFocus) {
            AppDebug.i(TAG, TAG + ".onFocusChanged position=" + position + ", firstAm=" + firstAm);
            if (firstAm) { // 首次进入不执行动画
                firstAm = false;
                return;
            }
            int moveXtemp = 0;
            switch (position) {
                case HomeIndexPageRelativeLayout.POSITION_0:
                    if (hasFocus) {
                        moveXtemp = animationMoveBigX;
                    } else {
                        moveXtemp = -animationMoveBigX;
                    }
                    runAnimation(firstFront, moveXtemp);
                    break;
                case HomeIndexPageRelativeLayout.POSITION_1:

                    if (hasFocus) {
                        moveXtemp = animationMoveBigX;
                    } else {
                        moveXtemp = -animationMoveBigX;
                    }
                    runAnimation(secondFront, moveXtemp);
                    break;
                case HomeIndexPageRelativeLayout.POSITION_2:
                    if (hasFocus) {
                        moveXtemp = animationMoveBigX;
                    } else {
                        moveXtemp = -animationMoveBigX;
                    }
                    runAnimation(thirdFront, moveXtemp);
                    break;
            }
        }

        @Override
        public void onLoadFrontImage(String url, int position, View backItem) {
            switch (position) {
                case HomeIndexPageRelativeLayout.POSITION_0:
                    loadImageFormUrl(firstFront, url, -1);
                    break;
                case HomeIndexPageRelativeLayout.POSITION_1:
                    loadImageFormUrl(secondFront, url, -1);
                    break;
                case HomeIndexPageRelativeLayout.POSITION_2:
                    loadImageFormUrl(thirdFront, url, -1);
                    break;
            }
        }

        @Override
        public void startScrollRequest(int position, int dx, int duration) {
            AppDebug.i(TAG, TAG + ".startScrollRequest position=" + position + ", dx=" + dx + ", duration=" + duration
                    + ", mScroller=" + mScroller);
            if (mScroller != null) {
                mScroller.startScroll(dx, 0, duration);
            }
            if (position == 0) {
                mScroller.resetScroll();
            }
            // homeContainer.layout(homeContainer.getLeft() - dx,
            // homeContainer.getTop(), homeContainer.getWidth(),
            // homeContainer.getHeight());
        }

        @Override
        public void prepareScrollRequest() {
            homeContainer.setVisibility(View.GONE);
        }

        public void removeRunnable() {
            if (null != moveAnimationRunnable) {
                return;
            }
            mHandler.removeCallbacks(moveAnimationRunnable);
        }

        public HomeIndexPageRelativeLayout getContainer() {
            return mContainer;
        }
    }

    class CustomsItemIconLoader implements ImageLoadingListener {

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }

        @Override
        public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
            if (view instanceof ImageView && null != bitmap) {
                ((ImageView) view).setImageBitmap(bitmap);
                view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoadingFailed(String arg0, View view, FailReason reason) {
        }

        @Override
        public void onLoadingStarted(String arg0, View view) {
        }
    }

    static class FocusePositionHandle extends AppHandler<HomeCategoryActivity> {

        public FocusePositionHandle(HomeCategoryActivity t) {
            super(t);
        }

        /*
         * (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            HomeCategoryActivity activity = getT();
            if (null == activity) {
                return;
            }
            activity.requestLastLiveFocuseView();
        }

    }

    @Override
    public String getPageName() {
        return TBS_PAGE_TAG;
    }

    @Override
    public void requestLoadImage(ImageView image, String url, int defRes) {
        loadImageFormUrl(image, url, defRes);
    }

    @Override
    public void afterApiLoad(boolean success, String errorMsg, Object obj) {
        load_progress.setVisibility(View.GONE);
        if (success && SystemConfig.DIPEI_BOX && obj instanceof Intent) {
            Intent intent = (Intent) obj;
            AppDebug.i(
                    TAG,
                    TAG + ".afterApiLoad intent="
                            + intent.getBooleanExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, false));
            if (intent.getBooleanExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, false)) {
                AppDebug.i(TAG, TAG + ".afterApiLoad call finish");
                //                View view = findTerminalFocusView(mPageConainer.getFocusedChild());
                if (null != mPageConainer && null != mPageConainer.getContainer()) {
                    mSelectionPosition = mPageConainer.getContainer().mIndex;
                    AppDebug.i(TAG, TAG + ".afterApiLoad mSelectionPosition=" + mSelectionPosition);
                }
                finish();
            }
        }
    }
}
