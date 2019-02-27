package com.yunos.tvtaobao.search.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.base.BaseTabMVPActivity;
import com.yunos.tvtaobao.biz.base.RightSideView;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.bean.TabData;
import com.yunos.tvtaobao.search.contract.SearchResultContract;
import com.yunos.tvtaobao.search.fragment.GoodsListFragment;
import com.yunos.tvtaobao.search.model.SearchResultModel;
import com.yunos.tvtaobao.search.presenter.SearchResultPresenter;
import com.yunos.tvtaobao.search.view.TabView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchResultActivity extends BaseTabMVPActivity<SearchResultPresenter> implements SearchResultContract.View {
    public final static String TAG = "Page_TbGoodsSeachResult";
    private TabView tabView;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private GoodsListFragment defaultFragment, salesFragment, tmDefaultFragment, tmSalesFragment;
    private Map<String, String> lProperties = Utils.getProperties();
    public String keyword;
    private int page_size = 40;//每页条数
    private int curTabType = 1;//当前选中的搜索类型
    private Map<Integer, GoodsListFragment> fragmentMaps;

    @Override
    public void initView() {
        super.initView();
        // 获取外部传入的数据
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String from = intent.getStringExtra(CoreIntentKey.URI_FROM);
        //如果来自语音，说明是来自语音查看更多页面
        if ("voice_application".equals(from)) {
            TvOptionsConfig.setTvOptionVoiceSystem(from);
            TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.VOICE_VIEW_MORE);
        }
        tabView = (TabView) findViewById(R.id.tv_tab_view);
        TabData tabData = new TabData();
        tabData.put(getString(R.string.search_all_goods), false);
        tabData.put(getString(R.string.search_default_goods), true);
        tabData.put(getString(R.string.search_sales_goods), true);
        tabData.put(getString(R.string.search_all_goods_tmail), false);
        tabData.put(getString(R.string.search_default_goods_tmail), true);
        tabData.put(getString(R.string.search_sales_goods_tmail), true);
        tabView.setTabData(tabData);
        fragmentManager = getFragmentManager();
    }

    @Override
    protected String getNewFullPageName() {
        return super.getFullPageName();
    }

    @Override
    public void initData() {
        super.initData();
        keyword = getIntent().getStringExtra(BaseConfig.INTENT_KEY_KEYWORDS);
        requestSearchProduct(keyword, 1, null, false);
        fragmentMaps = new HashMap<>();
    }

    @Override
    public void initListener() {
        super.initListener();
        setRightClickListener();
        setRightFocusChangeListener();
        tabView.setItemLongFocusListener(new TabView.OnItemLongFocusListener() {
            @Override
            public void onItemLongFocus(int tabType) {
                //如果当前聚焦的和要选中的类型一致，不处理。
                //否则切换tabType
                if (curTabType == tabType) {
                    return;
                }
                curTabType = tabType;
                switch (tabType) {
                    case 1:
                        AppDebug.i(TAG, TAG + ".onItemLongFocus 商品综合排序");
                        Utils.utControlHit(getFullPageName(), "全部_综合", initTBSProperty(SPMConfig.GOODS_LIST_SPM_LIST_P_NAME));

                        changeTab(defaultFragment, null, false);
                        break;
                    case 2:
                        AppDebug.i(TAG, TAG + ".onItemLongFocus 商品销量优先");
                        Utils.utControlHit(getFullPageName(), "全部_销量", initTBSProperty(SPMConfig.GOODS_LIST_SPM_LIST_P_NAME));

                        changeTab(salesFragment, "sales", false);
                        break;
                    case 4:
                        AppDebug.i(TAG, TAG + ".onItemLongFocus 天猫综合排序");
                        Utils.utControlHit(getFullPageName(), "天猫_综合", initTBSProperty(SPMConfig.GOODS_LIST_SPM_LIST_P_NAME));

                        changeTab(tmDefaultFragment, null, true);
                        break;
                    case 5:
                        AppDebug.i(TAG, TAG + ".onItemLongFocus 天猫销量优先");
                        Utils.utControlHit(getFullPageName(), "天猫_销量", initTBSProperty(SPMConfig.GOODS_LIST_SPM_LIST_P_NAME));

                        changeTab(tmSalesFragment, "sales", true);
                        break;
                }
            }
        });
    }

    private void changeTab(Fragment fragment, String sorts, boolean isTmail) {
        if (fragment == null) {
            requestSearchProduct(keyword, 1, sorts, isTmail);
        } else {
            if (((GoodsListFragment) fragment).hasData()) {
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fl_right_layout, fragment);
                transaction.commit();
            } else {
                requestSearchProduct(keyword, 1, sorts, isTmail);
            }
        }
    }

    public void requestSearchProduct(String keyword, int page, String sort, boolean isTmail) {
        showProgressDialog(true);
        mPresenter.requestSearchProduct(SearchResultActivity.this, keyword, page_size, page, 4,
                sort, null, "r", ActivityPathRecorder.getInstance().getCurrentPath(this),
                false, false, isTmail);
    }

    @Override
    protected SearchResultPresenter createPresenter() {
        return new SearchResultPresenter(new SearchResultModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_search_result;
    }

    @Override
    public void setSearchResultData(GoodsSearchResultDo data) {
        AppDebug.i(TAG, TAG + ".setSearchResultData curTabType : " + curTabType);
        showProgressDialog(false);

        switch (curTabType) {
            case 1:
                if (defaultFragment == null) {
                    defaultFragment = new GoodsListFragment();
                    fragmentMaps.put(curTabType, defaultFragment);
                }

                addData(defaultFragment, data, null, true, true);
                break;
            case 2:
                if (salesFragment == null) {
                    salesFragment = new GoodsListFragment();
                    fragmentMaps.put(curTabType, salesFragment);
                }

                addData(salesFragment, data, "sales", true, false);
                break;
            case 4:
                if (tmDefaultFragment == null) {
                    tmDefaultFragment = new GoodsListFragment();
                    fragmentMaps.put(curTabType, tmDefaultFragment);
                }

                addData(tmDefaultFragment, data, null, true, false);
                break;
            case 5:
                if (tmSalesFragment == null) {
                    tmSalesFragment = new GoodsListFragment();
                    fragmentMaps.put(curTabType, tmSalesFragment);
                }

                addData(tmSalesFragment, data, "sales", true, false);
                break;
        }
    }


    private void addData(GoodsListFragment fragment, GoodsSearchResultDo data, String sales, boolean isTmall, boolean isFirstInit) {
        if (fragment.hasData()) {
            fragment.addData(data);
            return;
        }

        transaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);
        bundle.putSerializable("searchData", data);
        if (!TextUtils.isEmpty(sales)) {
            bundle.putString("sales", sales);
        }
        bundle.putBoolean("isTmall", isTmall);
        if (isFirstInit) {
            bundle.putBoolean("isFirstInit", isFirstInit);
        }
        fragment.setArguments(bundle);

        transaction.replace(R.id.fl_right_layout, fragment);
        transaction.commit();
    }

    public void setRightClickListener() {
        rightSideView.setOnItemViewClickListener(new RightSideView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick(View view) {
                if (view.getId() == R.id.fiv_pierce_home_focusd) {
                    lProperties.put("controlname", "home");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_HOME);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_home", lProperties);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_HOME);
                    Intent intent = new Intent();
                    intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //setFrom("");
                    startActivity(intent);
                } else if (view.getId() == R.id.fiv_pierce_my_focusd) {
                    lProperties.put("controlname", "TbMyTaoBao");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_MY);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_TbMyTaoBao", lProperties);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_MY);
                    Intent intent = new Intent();
                    intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
                    intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                    //setFrom("");
                    startActivity(intent);
                } else if (view.getId() == R.id.fiv_pierce_cart_focusd) {
                    lProperties.put("controlname", "Cart");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_CART);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_Cart", lProperties);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_CART);
                    Intent intent = new Intent();
                    intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                    //setFrom("");
                    startActivity(intent);
                } else if (view.getId() == R.id.fiv_pierce_red_packet_focusd) {
                    lProperties.put("controlname", "red");
                    lProperties.remove("spm");
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_red", lProperties);
                    Intent intent = new Intent();
                    if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                        String page = PageMapConfig.getPageUrlMap().get("coupon");
                        if (!TextUtils.isEmpty(page)) {
                            intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                        } else {
                            intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                        }
                    } else {
                        intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                    }
                    intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                    startActivity(intent);
                } else if (view.getId() == R.id.fiv_pierce_block_focusd) {
                    lProperties.put("controlname", "coupon");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_COUPON);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_coupon", lProperties);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_COUPON);
                    Intent intent = new Intent();
                    if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                        String page = PageMapConfig.getPageUrlMap().get("coupon");
                        if (!TextUtils.isEmpty(page)) {
                            intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                        } else {
                            intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                        }
                    } else {
                        intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                    }
                    intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                    //setFrom("");
                    startActivity(intent);
                } else if (view.getId() == R.id.fiv_pierce_contact_focusd) {
                    lProperties.put("controlname", "phone");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_CC);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_CC);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_phone", lProperties);
                    rightSideView.getTv_pierce_contact_focusd().setText("周一至周五 9:30~18:30");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rightSideView.getTv_pierce_contact_focusd().setText("客服:0571-85135297");
                        }
                    }, 3000);
                } else if (view.getId() == R.id.fiv_pierce_red_jifen_focusd) {
                    lProperties.put("controlname", "integral");
                    lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_POINT);
                    Utils.utControlHit(getFullPageName(), "Page_tool_button_integral", lProperties);
                    Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_POINT);
                    Intent intent = new Intent();
                    intent.setClassName(SearchResultActivity.this, BaseConfig.SWITCH_TO_POINT_ACTIVITY);
                    startActivity(intent);
                }
            }
        });
    }

    public void setRightFocusChangeListener() {
        rightSideView.setOnItemViewFocusChangeListener(new RightSideView.OnItemViewFocusChangeListener() {
            @Override
            public void onItemViewFocusChange(View view, boolean hasFocus) {
                if (view.getId() == R.id.fiv_pierce_home_focusd) {
                    lProperties.put("controlname", "home");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_home", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_home().setVisibility(View.VISIBLE);

                    } else {
                        rightSideView.getTv_pierce_home().setVisibility(View.INVISIBLE);
                    }
                } else if (view.getId() == R.id.fiv_pierce_my_focusd) {
                    lProperties.put("controlname", "TbMyTaoBao");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_TbMyTaoBao", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_my().setVisibility(View.VISIBLE);
                        if (User.isLogined()) {
                            rightSideView.getTv_pierce_my().setText(User.getNick());
                        }

                    } else {
                        rightSideView.getTv_pierce_my().setVisibility(View.INVISIBLE);
                    }
                } else if (view.getId() == R.id.fiv_pierce_cart_focusd) {
                    lProperties.put("controlname", "Cart");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_Cart", lProperties);
                    AppDebug.e(TAG, "GoodsListActivity---> 0");
                    GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
                    if (globalConfig != null) {
                        AppDebug.e(TAG, "GoodsListActivity---> 2");
                        GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
                        if (shopCartFlag != null && shopCartFlag.isActing()) {
                            lProperties.put("name", "购物车气泡");
                            Utils.utControlHit(getFullPageName(), "Expose_tool_cartbubble", lProperties);
                            if (hasFocus) {
                                rightSideView.getFiv_pierce_cart_focusd().setImageResource(R.drawable.goodlist_fiv_pierce_cart_focus);
                                rightSideView.getIv_pierce_cart_active().setVisibility(View.VISIBLE);
                            } else {
                                rightSideView.getFiv_pierce_cart_focusd().setImageDrawable(null);
                                rightSideView.getIv_pierce_cart_active().setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        AppDebug.e(TAG, "GoodsListActivity---> 3");
                        if (hasFocus) {
                            AppDebug.e(TAG, "GoodsListActivity---> 4");
                            rightSideView.getTv_pierce_cart().setVisibility(View.VISIBLE);
                        } else {
                            AppDebug.e(TAG, "GoodsListActivity---> 5");
                            rightSideView.getTv_pierce_cart().setVisibility(View.INVISIBLE);
                        }
                    }
                } else if (view.getId() == R.id.fiv_pierce_red_packet_focusd) {
                    lProperties.put("controlname", "red");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_red", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_red_packet().setVisibility(View.VISIBLE);
                    } else {
                        rightSideView.getTv_pierce_red_packet().setVisibility(View.INVISIBLE);
                    }
                } else if (view.getId() == R.id.fiv_pierce_block_focusd) {
                    lProperties.put("controlname", "coupon");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_coupon", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_block().setVisibility(View.VISIBLE);
                    } else {
                        rightSideView.getTv_pierce_block().setVisibility(View.INVISIBLE);
                    }
                } else if (view.getId() == R.id.fiv_pierce_contact_focusd) {
                    lProperties.put("controlname", "phone");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_phone", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_contact_focusd().setVisibility(View.VISIBLE);
                    } else {
                        rightSideView.getTv_pierce_contact_focusd().setVisibility(View.INVISIBLE);
                    }
                } else if (view.getId() == R.id.fiv_pierce_come_back_focusd) {
                    if (hasFocus) {
                        rightSideView.getTv_pierce_come_back().setVisibility(View.VISIBLE);
                    } else {
                        rightSideView.getTv_pierce_come_back().setVisibility(View.INVISIBLE);

                    }
                } else if (view.getId() == R.id.fiv_pierce_red_jifen_focusd) {
                    lProperties.put("controlname", "integral");
                    Utils.utCustomHit(getFullPageName(), "tool_button_focus_integral", lProperties);
                    if (hasFocus) {
                        rightSideView.getTv_pierce_red_jifen().setVisibility(View.VISIBLE);
                    } else {
                        rightSideView.getTv_pierce_red_jifen().setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void showProgressDialog(boolean show) {
        OnWaitProgressDialog(show);
    }

    @Override
    public String getFullPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, SPMConfig.GOODS_LIST_SPM + ".0.0");
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }
        if (!TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("version", AppInfo.getAppVersionName());
        }
        if (!TextUtils.isEmpty(Config.getAppKey())) {
            p.put("appkey", Config.getAppKey());
        }
        return p;
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(Config.getAppKey())) {
            p.put("appkey", Config.getAppKey());
        }
        try {
            if (!TextUtils.isEmpty(AppInfo.getAppVersionName())) {
                p.put("version", AppInfo.getAppVersionName());
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }
        return p;
    }
}
