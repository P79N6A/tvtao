package com.yunos.tvtaobao.newcart.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.taobao.wireless.trade.mcart.sdk.co.biz.DynamicCrossShopPromotion;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FooterComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.bridge.eventbus.KillGuessLikeActEvent;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.dialog.QRCodeDialog;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.biz.widget.FocusNoDeepFrameLayout;
import com.yunos.tvtaobao.biz.widget.InnerFocusGroupListView;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.newsku.TvTaoSkuActivity;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.newcart.entity.CartBuilder;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.newcart.entity.FindSameIntentBean;
import com.yunos.tvtaobao.newcart.entity.QueryBagType;
import com.yunos.tvtaobao.newcart.entity.ShopCartGoodsBean;
import com.yunos.tvtaobao.newcart.ui.adapter.GuessLikeAdapter;
import com.yunos.tvtaobao.newcart.ui.adapter.NewShopCartListAdapter;
import com.yunos.tvtaobao.newcart.ui.contract.ShopCartContract;
import com.yunos.tvtaobao.newcart.ui.model.ShopCartModel;
import com.yunos.tvtaobao.newcart.ui.presenter.ShopCartPresenter;
import com.yunos.tvtaobao.biz.widget.CustomDialog;
import com.yunos.tvtaobao.newcart.view.FullScreenDialog;
import com.yunos.tvtaobao.newcart.view.NoScaleFocusImageView;
import com.yunos.tvtaobao.newcart.view.ShopCartHintFocusLayout;
import com.yunos.tvtaobao.newcart.view.ShopCartItemFocusLayout;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;

import org.json.JSONArray;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 显示的列表购物车列表
 */
public class NewShopCartListActivity extends BaseMVPActivity<ShopCartPresenter> implements ShopCartContract.View {

    private static final String TAG = "NewShopCartListActivity";
    public static int SHOP_CART_ACTIVITY_FOR_RESULT_CODE = 1; // 购物车调用页返回的结果代码

    private int REQUEST_GOODS_DATA_DIFF = 20; // 请求的商品跟列表的商品相差多少个时候发起下个请求
    private FocusPositionManager mFocusPositionManager; // 整体布局
    private FocusNoDeepFrameLayout mAccountView; // 结算的View
    private FrameLayout mNewShopCartContainerLayout;
    private TextView mAccountShopCountView; // 结算商品的个数
    private TextView mAccountPriceView; // 结算商品的总价格
    private TextView mAccountExpressView; // 结算商品的邮费信息
    private TextView mChooseRebateTxt;    //结算商品的返利总额
    private LinearLayout mShopCartEmptyLayout; // 购物车列表为空的布局
    private InnerFocusGroupListView innerFocusGroupListView; // 购物车列表带Focus内部功能
    private NewShopCartListAdapter mShopCartListAdapter; // 购物车列表的适配器
    private CartBuilder mCartBuilder; // 购物数据的Buidler
    private TextView mShopCartTitle; // 购物车列表个数的title
    private NoScaleFocusImageView mShopCartIcon; //购物车打标
    private ImageView mShopCartEmptyIcon;
    private int mSkuEditPosition; // 选中sku编译商品位置
    private boolean mIsRquestingNextData; // 是否还在请求下次的数据
    private boolean mIsNotNextData; // 是否没有后续的数据
    private boolean mIsListViewScrolling; // 列表是否还有滚动中
    private boolean mHaveNewDataNeedUpdate; // 是否有新的数据需要更新
    private TvTaoBaoDialog mDeletedDialog; // 删除商品的对话框
    private boolean mIsRquestingDelete; // 是否在请求网络删除中
    private int mSubmitRequestCode = 666; //结算按钮的request code
    private int mBackFromLikeRequestCode = 777; //猜你喜欢返回的request code
    private String cartFrom = "default_client";
    private boolean mFirstUpdateList;// 第一次更新列表

    // 二维码
    protected QRCodeDialog mQRCodeDialog;
    private static String deleteId;
    private static List<String> deleteIdList = new ArrayList<>();
    private String updateSkuId;
    private int updateQuantity = 0;
    private CustomDialog commonDialog;
    private TextView tvSubmit;
    private View foot;
    private int currentSelectedPosition = 0;
    private ArrayList<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList;
    private ArrayList<GuessLikeGoodsBean.ResultVO.RecommendVO> guessLikeList;
    private TvRecyclerView mShopCartEmptyRecyclerView;
    private GuessLikeAdapter guessLikeAdapter;
    private LinearLayout layoutSubmit;
    private LinearLayout layoutBenefit;
    private TextView tvPost;
    private boolean isCheck;
    // 1 删除商品更新选中状态；2 结算后更新选中状态
    private int TYPE_DELAY_UPDATE_SELECTED_STATE = -1;

    private Handler handler;

    private CartFrom cartFromEnum;

    private boolean bottonLineFlag;
    //private AsyncTask<Map<String, List<CartGoodsComponent>>, Integer, String> mGetRebateTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
        cartFrom = getIntent().getStringExtra(BaseConfig.INTENT_KEY_CARTFROM);
        if (cartFrom == null) {
            cartFrom = CartFrom.DEFAULT_CLIENT.getValue();
            cartFromEnum = CartFrom.DEFAULT_CLIENT;
        } else if ("tsm_client_native".equalsIgnoreCase(cartFrom)) {
            cartFromEnum = CartFrom.TSM_NATIVE_TAOBAO;
        } else {
            cartFromEnum = CartFrom.parseCartFrom(cartFrom);
        }
        super.onCreate(savedInstanceState);
        registerLoginListener();
        handler = new Handler();
        AppDebug.i(TAG, TAG + this + ".onCreate bundle=" + getIntent().getExtras());

        AppDebug.d(TAG, "cartFrom:" + cartFrom);
        //判断登录状态
//        if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
//            setLoginActivityStartShowing();
//            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
//            return;
//        }

        mFirstUpdateList = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        hideCartList();
        AppDebug.i(TAG, TAG + this + ".onCreate bundle=" + getIntent().getExtras());
        cartFrom = getIntent().getStringExtra(BaseConfig.INTENT_KEY_CARTFROM);
        if (cartFrom == null) {
            cartFrom = CartFrom.DEFAULT_CLIENT.getValue();
            cartFromEnum = CartFrom.DEFAULT_CLIENT;
        } else if ("tsm_client_native".equalsIgnoreCase(cartFrom)) {
            cartFromEnum = CartFrom.TSM_NATIVE_TAOBAO;
        } else {
            cartFromEnum = CartFrom.parseCartFrom(cartFrom);
        }
        AppDebug.d(TAG, "cartFrom:" + cartFrom);
        //判断登录状态
        if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            setLoginActivityStartShowing();
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
            return;
        }

        mFirstUpdateList = true;
        refreshData();//todo
        setIntent(new Intent());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        CartEngine.getInstance(cartFromEnum).free();
        unRegisterLoginListener();
        //RebateManager.getInstance().clear();

        // 退出时关闭显示着的对话框
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
        mFirstUpdateList = false;
        // 移除已有的猜你喜欢实例
        EventBus.getDefault().post(new KillGuessLikeActEvent());
        super.onDestroy();
    }

    /**
     * 初始化购物车数据控制类
     */
    private void initCartBudiler() {
        //因为是单例,所以初始化时先清空下数据
        CartEngine.getInstance(cartFromEnum).free();
        mCartBuilder = new CartBuilder(this, cartFrom);
        showProgressDialog(true);
        mPresenter.getShopCartList(this, mCartBuilder, cartFrom, QueryBagType.REQUEST_NEW_DATA);
        mPresenter.getGuessLikeGoods(this, cartFrom);
        mPresenter.getCartStyle(this);
    }

    /**
     * 初始化UI页面
     */
    public void initView() {
        initCartBudiler();
//        mShopCartEmptyLayout = (ViewGroup) findViewById(R.id.shop_cart_empty);
        initAccountView();
        initGroupListView(R.id.recyclerview);
        mNewShopCartContainerLayout = (FrameLayout) findViewById(R.id.layout_new_shop_cart_container);
        layoutSubmit = (LinearLayout) findViewById(R.id.layout_submit);
        layoutBenefit = (LinearLayout) findViewById(R.id.layout_benefit);
        tvPost = (TextView) findViewById(R.id.tv_freight);
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.position_manager);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.new_cart_select_item)));
        mFocusPositionManager.requestFocus();

        mShopCartEmptyLayout = (LinearLayout) findViewById(R.id.layout_shop_cart_empty);
        mShopCartEmptyIcon = (ImageView) findViewById(R.id.shop_cart_icon_empty);
        mShopCartEmptyRecyclerView = (TvRecyclerView) findViewById(R.id.recyclerview_guesslike);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        guessLikeAdapter = new GuessLikeAdapter(this, new GuessLikeAdapter.OnItemListener() {
            @Override
            public void onItemSelected(int position) {
            }
        }, true);

//        mShopCartEmptyRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int position = parent.getChildAdapterPosition(view); // item position
//                int spacing = getResources().getDimensionPixelSize(R.dimen.dp_8);
////                outRect.top = spacing;
////                outRect.bottom = spacing; // item bottom
//
//                outRect.left = spacing; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//
//
//            }
//        });
        mShopCartEmptyRecyclerView.setLayoutManager(gridLayoutManager);
        mShopCartEmptyRecyclerView.setAdapter(guessLikeAdapter);
        Map<String, String> properties = Utils.getProperties();
        properties.put("version", SystemConfig.APP_VERSION);
        Utils.utCustomHit(getFullPageName(), "tbCart", properties);

    }

    @Override
    protected ShopCartPresenter createPresenter() {
        return new ShopCartPresenter(new ShopCartModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_new_shop_cart;
    }

    /**
     * 初始化购物列表的UI
     *
     * @param resId
     */
    private void initGroupListView(int resId) {
        innerFocusGroupListView = (InnerFocusGroupListView) findViewById(resId);
        innerFocusGroupListView.setParams(new Params(1.05f, 1.05f, 10, null, true, 20,
                new AccelerateDecelerateFrameInterpolator()));

        if (innerFocusGroupListView.getHeaderViewsCount() == 0) {
            InnerFocusLayout head = (InnerFocusLayout) getLayoutInflater().inflate(R.layout.layout_new_shop_cart_head, innerFocusGroupListView, false);
            mShopCartTitle = (TextView) head.findViewById(R.id.shop_cart_title);
            mShopCartIcon = (NoScaleFocusImageView) head.findViewById(R.id.shop_cart_icon);
            innerFocusGroupListView.addHeaderView(head, null, true);
        }
        innerFocusGroupListView.setNextFocusRightId(mAccountView.getId());
        String text = String.format(getResources().getString(R.string.newcart_title), 0);
        if (mShopCartTitle != null)
            mShopCartTitle.setText(text);

        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (gc != null && gc.getShopCartFlag() != null && gc.getShopCartFlag().isActing()) {
            String imageUrl = gc.getShopCartFlag().getNewShopCatIcon();
            AppDebug.e(TAG + "imageUrl = ", imageUrl);
            /*if (mShopCartIcon != null) {
                ImageLoaderManager.getImageLoaderManager(this).displayImage(imageUrl, mShopCartIcon);
            }*/

            Map<String, String> properties = Utils.getProperties();
            properties.put("name", "购物车返现");
            Utils.utCustomHit("Expose_shopcart_lable", properties);
        }
        mShopCartListAdapter = new NewShopCartListAdapter(this, mCartBuilder, innerFocusGroupListView);
        innerFocusGroupListView.setFlipScrollFrameCount(5);
        innerFocusGroupListView.setAdapter(mShopCartListAdapter);
        innerFocusGroupListView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                AppDebug.i(TAG, TAG + ".onItemSelected position=" + position + " isSelected=" + isSelected
                        + ".selectedview = " + v);
                try {
                    if (position == 0 && innerFocusGroupListView.getHeaderViewsCount() > 0) {

                        if (isSelected && !mFirstUpdateList) {
                            if (v != null && v instanceof InnerFocusLayout)
                                ((InnerFocusLayout) v).setNextFocusSelected();
                            mFocusPositionManager.setSelector(new StaticFocusDrawable(new ColorDrawable(Color.TRANSPARENT)));
                            if (shopIconTagUrlSplit != null && shopIconTagUrlSplit.length >= 2) {
                                if (!TextUtils.isEmpty(shopIconTagUrlSplit[1])) {
                                    ImageLoaderManager.getImageLoaderManager(NewShopCartListActivity.this)
                                            .displayImage(shopIconTagUrlSplit[1], mShopCartIcon, new ImageLoadingListener() {
                                                @Override
                                                public void onLoadingStarted(String imageUri, View view) {

                                                }

                                                @Override
                                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                                }

                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                    view.requestLayout();
                                                }

                                                @Override
                                                public void onLoadingCancelled(String imageUri, View view) {

                                                }
                                            });
                                }
                            }

                        } else {
                            mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                    R.drawable.new_cart_select_item)));
                            if (shopIconTagUrlSplit != null && shopIconTagUrlSplit.length >= 2) {
                                if (!TextUtils.isEmpty(shopIconTagUrlSplit[0])) {
                                    ImageLoaderManager.getImageLoaderManager(NewShopCartListActivity.this)
                                            .displayImage(shopIconTagUrlSplit[0], mShopCartIcon, new ImageLoadingListener() {
                                                @Override
                                                public void onLoadingStarted(String imageUri, View view) {

                                                }

                                                @Override
                                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                                }

                                                @Override
                                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                    view.requestLayout();
                                                }

                                                @Override
                                                public void onLoadingCancelled(String imageUri, View view) {

                                                }
                                            });
                                }
                            }
                        }
                    } else {
                        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                R.drawable.new_cart_select_item)));
                    }
                    if (v instanceof ShopCartHintFocusLayout) {
                        mShopCartListAdapter.selectedHintView(isSelected, position, (ShopCartHintFocusLayout) v);
                    } else if (v instanceof ShopCartItemFocusLayout) {
                        mShopCartListAdapter.selectedShopItemView(isSelected, position, (ShopCartItemFocusLayout) v);
                    }
                    if (isSelected && position >= innerFocusGroupListView.getHeaderViewsCount()) {
                        checkLoadNextListData(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        innerFocusGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if(tagCueUrl!=null){
                        String[] urlArray = tagCueUrl.split(",");
                        List<String> urls = Arrays.asList(urlArray);
                        FullScreenDialog dialog = new FullScreenDialog(NewShopCartListActivity.this, urls);
                        dialog.show();
                    }
                }
            }
        });

        innerFocusGroupListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    mIsListViewScrolling = false;
                    boolean flag = isListViewReachBottomEdge(innerFocusGroupListView);
                    AppDebug.v("-----滑动到底部----", flag + "");
                    // 滚动停止后如果还有数据未更新就更新数据
                    if (mHaveNewDataNeedUpdate) {
                        updateShopCartList(false);
                    }
                } else {
                    mIsListViewScrolling = true;
                }
            }

            @Override
            public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        innerFocusGroupListView.setOnKeyListener(new InnerFocusGroupListView.OnKeyListener() {
            @Override
            public void onExceedingBottom() {
                AppDebug.i(TAG, "onExceedBottom haveNextData" + mCartBuilder.haveNextData() + ",currentCount:" + (currentSelectedPosition >= mShopCartListAdapter.getCount()) + ",mIsRquestingNextData:" + mIsRquestingNextData + ",mIsNotNextData" + mIsNotNextData);
                if (!mCartBuilder.haveNextData() && currentSelectedPosition >= mShopCartListAdapter.getCount()
                        && !mIsRquestingNextData) {

                    Intent intent = new Intent();
                    intent.setClass(NewShopCartListActivity.this, GuessLikeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, mBackFromLikeRequestCode);
                }
            }

        });
        //店铺选中
        mShopCartListAdapter.setOnShopControllerClickListener(new NewShopCartListAdapter.OnShopControllerClickListener() {
            @Override
            public void onShopControllerClick(NewShopCartListAdapter.ShopItemAction action, int position, View view, List<ItemComponent> itemComponentList) {
                AppDebug.i(TAG, TAG + ".onItemClick.position--onShopControllerClick=" + position + ", view = " + view);
                switch (action) {
                    case SHOP_CHECKED: {
                        if (itemComponentList != null && itemComponentList.get(0) != null) {
                            Utils.utControlHit(getFullPageName(), "Button-SelecShop",
                                    initTBSProperty(null, null, itemComponentList.get(0).getShopId(), null,
                                            true, SPMConfig.NEW_SHOP_CART_LIST_SPM_SHOP_SELECT));
                            Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_SHOP_SELECT);
                        }
                        mShopCartListAdapter.checkShopHint(position, (ShopCartHintFocusLayout) view);
                        isCheck = true;
                        if (itemComponentList != null) {
                            Utils.utControlHit(getFullPageName(), "Button-SelecShop",
                                    initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_SHOP_SELECT));
                            Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_SHOP_SELECT);

                            List<Component> items = new ArrayList<Component>();
//                            data.getItemComponent().setChecked(true, false);
                            items.addAll(itemComponentList);
                            OnWaitProgressDialog(true);
                            mPresenter.updateBagRequest(NewShopCartListActivity.this, items, ActionType.CHECK, cartFrom, QueryBagType.REQUEST_CHECK);
                        } else {
                            updateAccountInfo();
                        }
                    }
                    break;
                }
            }
        });
        mShopCartListAdapter.setOnShopItemControllerClickListener(new NewShopCartListAdapter.OnShopItemControllerClickListener() {

            @Override
            public void onShopItemControllerCheck(ShopCartGoodsBean bean, int position) {
                ItemComponent itemComponent = bean.getCartGoodsComponent().getItemComponent();

                if (bean.isInvalid()) {
                    boolean skuInvalid = false;
                    if (itemComponent.getSku() != null) {
                        skuInvalid = itemComponent.getSku().isSkuInvalid();
                    }
                    if (skuInvalid) {
                        // TODO: 2018/7/2 跳转到SKU选择页，重选
                        Utils.utControlHit(getFullPageName(), "Button-Edit",
                                initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_EDIT));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_EDIT);

                        this.onShopItemControllerClick(NewShopCartListAdapter.ShopItemAction.EDIT, position);

                    } else {
                        //找相似
                        Utils.utControlHit(getFullPageName(), "Button-SearchSimilarity", initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_FIND_SIMILARITY));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_FIND_SIMILARITY);

                        openFindSameActivity(itemComponent);
                    }
                } else {
                    //聚划算未开团，未开始
                    // TODO: 2018/7/3
                    if (!itemComponent.isValid() && itemComponent.isPreBuyItem()) {
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                        commonDialog = new CustomDialog.Builder(NewShopCartListActivity.this).setType(1)
                                .setResultMessage(getString(R.string.new_shop_cart_item_isPreBuy))
                                .create();
                        commonDialog.show();

                    } else if (!itemComponent.isValid() && itemComponent.isPreSell()) {
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                        commonDialog = new CustomDialog.Builder(NewShopCartListActivity.this).setType(1)
                                .setResultMessage(getString(R.string.new_shop_cart_item_isPreSell))
                                .create();
                        commonDialog.show();
                    } else {
                        //可选中购买
//                        if (!itemComponent.isChecked()) {
//                            itemComponent.setChecked(true);
//                        } else {
//                            itemComponent.setChecked(false);
//                        }
                        Utils.utControlHit(getFullPageName(), "Button-SelectItems",
                                initTBSProperty(itemComponent.getId(), itemComponent.getTitle(), itemComponent.getShopId(), null, true, SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_SELECT));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_SELECT);

                        View childView = innerFocusGroupListView.getChildAt(position
                                - innerFocusGroupListView.getFirstPosition() + 1);
                        if (childView instanceof ShopCartItemFocusLayout) {
                            mShopCartListAdapter.checkShopItem(position, (ShopCartItemFocusLayout) childView);
                        }
                        isCheck = true;
                        if (itemComponent != null) {
                            List<Component> items = new ArrayList<Component>();
//                            data.getItemComponent().setChecked(true, false);
                            items.add(itemComponent);
                            OnWaitProgressDialog(true);
                            mPresenter.updateBagRequest(NewShopCartListActivity.this, items, ActionType.CHECK, cartFrom, QueryBagType.REQUEST_CHECK);
                        }
//                        updateAccountInfo();
                    }
                }
            }

            @Override
            public void onShopItemControllerClick(NewShopCartListAdapter.ShopItemAction action, int position) {
                AppDebug.v(TAG, TAG + ".onShopItemControllerClick.action = " + action + ".position = " + position);
                switch (action) {
                    case DELETE: {
                        // 删除
                        CartGoodsComponent itemData = mShopCartListAdapter.getGoodsItemData(position);
                        AppDebug.i(TAG, "onShopItemControllerClick DELETE mIsRquestingDelete=" + mIsRquestingDelete);
                        if (!itemData.getItemComponent().isValid() &&
                                !itemData.getItemComponent().isPreBuyItem() && !itemData.getItemComponent().isPreSell()) {
                            Utils.utControlHit(getFullPageName(), "Button-Delete",
                                    initTBSProperty(null, null, null, "false", true, SPMConfig.NEW_SHOP_CART_LIST_SPM_DELETE_YES_GOODS));
                        } else {
                            Utils.utControlHit(getFullPageName(), "Button-Delete",
                                    initTBSProperty(null, null, null, "true", false, SPMConfig.NEW_SHOP_CART_LIST_SPM_DELETE_YES_GOODS));
                        }
                        showDeletedShop(position);
                    }
                    break;
                    case EDIT: {
                        // 进入sku页面编辑

                        CartGoodsComponent data = mShopCartListAdapter.getGoodsItemData(position);
                        if (data != null && data.getItemComponent() != null) {
                            //sku埋点
                            Utils.utControlHit(getFullPageName(), "Button-Edit",
                                    initTBSProperty(data.getItemComponent().getItemId(), data.getItemComponent().getTitle(), data.getItemComponent().getShopId(), null, true, SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_EDIT));
                            Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_EDIT);
                            mSkuEditPosition = position;
                            Intent intent = new Intent();
                            intent.setClass(NewShopCartListActivity.this, TvTaoSkuActivity.class);
                            if (!TextUtils.isEmpty(data.getItemComponent().getSku().getAreaId()))
                                intent.putExtra(BaseConfig.INTENT_KEY_AREAID, data.getItemComponent().getSku().getAreaId());
                            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, data.getItemComponent().getItemId());
                            intent.putExtra(BaseConfig.INTENT_KEY_SKU_TYPE, TradeType.EDIT_CART);
                            intent.putExtra(BaseConfig.INTENT_KEY_SKUID, data.getItemComponent().getSku().getSkuId());
                            intent.putExtra(BaseConfig.INTENT_KEY_BUY_COUNT, (int)data.getItemComponent().getItemQuantity().getQuantity());
                            boolean isPre = false;
                            if (!data.getItemComponent().isValid() && data.getItemComponent().isPreBuyItem()) {
                                isPre = true;
                            }
                            intent.putExtra(BaseConfig.INTENT_KEY_IS_PRE, isPre);

                            startActivityForResult(intent, SHOP_CART_ACTIVITY_FOR_RESULT_CODE);
                        }
                    }
                    break;
                    case DETAIL: {
                        // 打开详情页
                        Utils.utControlHit(getFullPageName(), "Button-Detail",
                                initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_DETAILS));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_DETAILS);
                        CartGoodsComponent data = mShopCartListAdapter.getGoodsItemData(position);
                        if (data != null && data.getItemComponent() != null) {
                            Intent intent = new Intent();
                            intent.setClassName(NewShopCartListActivity.this, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
                            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, data.getItemComponent().getItemId());
                            TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
                            TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.OTHER);
                            startActivity(intent);
                        }
                    }
                    break;
                    case CHECKED: {
                        // 结算
                        View childView = innerFocusGroupListView.getChildAt(position
                                - innerFocusGroupListView.getFirstPosition());
                        if (childView instanceof ShopCartItemFocusLayout) {
                            mShopCartListAdapter.checkShopItem(position, (ShopCartItemFocusLayout) childView);
                            updateAccountInfo();
                        } else {
                            AppDebug.i(TAG, "-------点击不到------=");
                        }
                    }
                    break;
                    case DELETE_ALL: {
                        // 删除全部，给出删除提示框

                        Utils.utControlHit(getFullPageName(), "Button-DeleteInvalid",
                                initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_DELETE_ALL_INVALID));

                        showDeletedAllInvalidShop(position);
                    }
                    break;
                    default:
                        break;
                }

            }
        });
    }

    public boolean isListViewReachBottomEdge(final InnerFocusGroupListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        ;
        return result;
    }

    private void openFindSameActivity(ItemComponent itemComponent) {
        try {
            if (itemComponent.getItemRecParamId() != null) {
                String itemRecParamId = itemComponent.getItemRecParamId();
                JSONObject jsonObject = new JSONObject(itemRecParamId);
                String catid = null;
                String nid = null;
                jsonObject.getString("catid");
                if (jsonObject.has("catid")) {
                    catid = jsonObject.getString("catid");
                    AppDebug.e(TAG + "catid  = ", catid);
                }
                if (jsonObject.has("nid")) {
                    nid = jsonObject.getString("nid");
                    AppDebug.e(TAG + "nid  = ", nid);
                }

                String skuId = itemComponent.getSku().getSkuId();

                Intent intent = new Intent(NewShopCartListActivity.this, FindSameActivity.class);
                FindSameIntentBean findSameIntentBean = new FindSameIntentBean();
                findSameIntentBean.setCatid(catid);
                findSameIntentBean.setNid(nid);
                findSameIntentBean.setSkuId(skuId);
                findSameIntentBean.setUrl(itemComponent.getPic());
                findSameIntentBean.setPrice(itemComponent.getItemPay().getNowTitle());
                findSameIntentBean.setTitle(itemComponent.getTitle());
                intent.putExtra(FindSameIntentBean.class.getName(), findSameIntentBean);
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化结算UI
     */
    private void initAccountView() {

        // 商品个数
        mAccountView = (FocusNoDeepFrameLayout) findViewById(R.id.account_btn);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        // 设置Focus框的空隙
        int px1 = getResources().getDimensionPixelSize(
                R.dimen.newcart_item_account_padding_left);
        mAccountView.setCustomerFocusPaddingRect(new Rect(px1, px1, px1, px1 * 5));
        mAccountView.setVisibility(View.INVISIBLE);
        mAccountShopCountView = (TextView) findViewById(R.id.tv_goods_count);
        // 价格
        mAccountPriceView = (TextView) findViewById(R.id.tv_goods_total_price);
        mAccountExpressView = (TextView) findViewById(R.id.tv_freight);
        mChooseRebateTxt = (TextView) findViewById(R.id.txt_choose_rebate);
        // 默认不选中
        focusedAccountView(false);
        mAccountView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i(TAG, "mAccountView setOnFocusChangeListener hasFocus=" + hasFocus);
                focusedAccountView(hasFocus);
                if (mFirstUpdateList) {
                    mAccountView.setFocusable(false);
                }
            }
        });
        mAccountView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 结算埋点统计
                Map<String, String> p = Utils.getProperties();
                p.put("version", AppInfo.getAppVersionName());
                p.put("appkeyid", Config.getChannel());
                p.put("spm", SPMConfig.SHOP_CART_LIST_SPM_CHECK);
                Utils.utControlHit(getFullPageName(), "Button-TbCart_Cart_Buy_Click", p);
                Utils.updateNextPageProperties(SPMConfig.SHOP_CART_LIST_SPM_CHECK);


                CartBuilder.CartSubmitResult result = mCartBuilder.submitCart();
                AppDebug.i(TAG, "mAccountView onClick result=" + result.mResultType);
                if (result != null && result.mResultType == CartBuilder.CartSubmitResultType.SUCCESS) {
                    if (result.mBuildOrderRequestBo != null) {
                        Intent intent = new Intent();
                        intent.setClassName(NewShopCartListActivity.this, BaseConfig.SWITCH_TO_BUILDORDER_ACTIVITY);
                        result.mBuildOrderRequestBo.setFrom(BaseConfig.ORDER_FROM_CART);
                        intent.putExtra("mBuildOrderRequestBo", result.mBuildOrderRequestBo);
                        intent.putExtra(BaseConfig.INTENT_KEY_IS_FROM_CART_TO_BUILDORDER, true);
                        startActivityForResult(intent, mSubmitRequestCode);
                    }

                } else if (result != null && result.mResultType == CartBuilder.CartSubmitResultType.ERROR_H5_ORDER) {
                    // 不支持TV端的就弹出二维码进行扫描
                    showItemQRCodeFromUrl(result.mErrorMsg, BaseConfig.CART_URL, null, true, null);
                } else {
                    showErrorDialog(result.mErrorMsg, 1);
                }
            }
        });
    }

    public void showErrorDialog(String err, int type) {
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        if (type == 1) {
            commonDialog = new CustomDialog.Builder(NewShopCartListActivity.this).setType(1)
                    .setResultMessage(err)
                    .create();
        } else if (type == 2) {
            commonDialog = new CustomDialog.Builder(NewShopCartListActivity.this).setType(2)
                    .setMessage(err).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
        }

        commonDialog.show();
    }

    /**
     * Focus状态的变化设置不同的UI显示
     *
     * @param hasFocus
     */
    private void focusedAccountView(boolean hasFocus) {
        if (hasFocus) {
            tvSubmit.setTextColor(getResources().getColor(R.color.ytm_white));
            mChooseRebateTxt.setTextColor(getResources().getColor(R.color.ytm_white));
        } else {
            tvSubmit.setTextColor(getResources().getColor(R.color.ytm_detail_original_price_));
            mChooseRebateTxt.setTextColor(getResources().getColor(R.color.ytm_detail_original_price_));
        }
    }


    @Override
    public void requestNextData() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /*
     *获取猜你喜欢数据
     */
    @Override
    public void setGuessLikeGoodsData(final GuessLikeGoodsBean guessLikeGoodsData) {

        if (guessLikeGoodsData != null) {
            recommemdList = new ArrayList<>();
            guessLikeList = new ArrayList<>();
            if (guessLikeGoodsData.getResult() != null && guessLikeGoodsData.getResult().getRecommedResult() != null) {
                int size = guessLikeGoodsData.getResult().getRecommedResult().size() > 5 ? 6 : guessLikeGoodsData.getResult().getRecommedResult().size();
                for (int i = 0; i < guessLikeGoodsData.getResult().getRecommedResult().size(); i++) {
                    if (guessLikeGoodsData.getResult().getRecommedResult().get(i).getType().equals("item")) {
                        recommemdList.add(guessLikeGoodsData.getResult().getRecommedResult().get(i));
                        if (recommemdList.size() == size - 1) {
                            break;
                        }
                    }
                }

                if (recommemdList != null) {

                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < recommemdList.size(); i++) {
                            GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods = recommemdList.get(i);
                            String itemId = recommendGoods.getFields().getItemId();
                            String price = null;
                            if (recommendGoods.getFields().getPrice() != null && (recommendGoods.getFields().getPrice().getCent() != null) ||
                                    recommendGoods.getFields().getPrice().getYuan() != null) {
                                if (!TextUtils.isEmpty(recommendGoods.getFields().getPrice().getCent())) {
                                    price = recommendGoods.getFields().getPrice().getYuan()
                                            + "." + recommendGoods.getFields().getPrice().getCent();
                                } else {
                                    price = recommendGoods.getFields().getPrice().getYuan();
                                }
                            }
//                          String itemS11Pre = goods.getS11Pre();

                            AppDebug.e(TAG, "Rebate itemId = " + itemId + ";itemS11Pre = false" + ";price =" + price);
                            JSONObject object = new JSONObject();
                            object.put("itemId", itemId);
//                        object.put("isPre", itemS11Pre);
                            object.put("price", price);
                            jsonArray.put(object);
                        }
                        AppDebug.e(TAG, "Rebate" + jsonArray.toString());
                        mPresenter.getGuessLikeRebate(jsonArray.toString(),
                                ActivityPathRecorder.getInstance().getCurrentPath(this), recommemdList, this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    guessLikeAdapter.setData(recommemdList);
                }


//                guessLikeAdapter.setData(recommemdList);


                for (int i = 0; i < guessLikeGoodsData.getResult().getRecommedResult().size(); i++) {
                    if (guessLikeGoodsData.getResult().getRecommedResult().get(i).getType().equals("item")) {
                        guessLikeList.add(guessLikeGoodsData.getResult().getRecommedResult().get(i));
                    }
                }
            }
        }
    }

    @Override
    public void setCartStyleData(CartStyleBean data) {
        String cartUrl = data.getCartUrl();
        final String tagUrl = data.getTagUrl();
        String tagCueUrl = data.getTagCueUrl();
        ImageLoaderManager.getImageLoaderManager(this).loadImage(cartUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mNewShopCartContainerLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_new_shop_cart));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mNewShopCartContainerLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_new_shop_cart));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mNewShopCartContainerLayout.setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        initTagView(mShopCartIcon, tagUrl, tagCueUrl);
        initTagView(mShopCartEmptyIcon, tagUrl, tagCueUrl);
        //手动设置猜你喜欢列表上方的可聚焦view
        mShopCartEmptyRecyclerView.setUpFoucsView(mShopCartEmptyIcon);
    }

    private String[] shopIconTagUrlSplit;
    private String tagCueUrl;

    private void initTagView(final ImageView tagView, final String tagUrl, final String tagCueUrl) {
        this.tagCueUrl = tagCueUrl;
        if (tagView != null&&tagUrl!=null) {
            shopIconTagUrlSplit = tagUrl.split(",");
            ImageLoaderManager.getImageLoaderManager(NewShopCartListActivity.this).displayImage(shopIconTagUrlSplit[0], tagView);
            if (tagView == mShopCartEmptyIcon) {
                tagView.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean focus) {
                        if (shopIconTagUrlSplit.length == 2) {
                            if (focus) {
                                mFocusPositionManager.setSelector(new StaticFocusDrawable(new ColorDrawable(0xffffff)));
                                ImageLoaderManager.getImageLoaderManager(NewShopCartListActivity.this)
                                        .displayImage(shopIconTagUrlSplit[1], tagView);
                            } else {
                                mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                        R.drawable.new_cart_select_item)));
                                ImageLoaderManager.getImageLoaderManager(NewShopCartListActivity.this)
                                        .displayImage(shopIconTagUrlSplit[0], tagView);
                            }
                        }
                    }
                });
            }

            tagView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tagCueUrl==null){
                        return;
                    }
                    String[] urlArray = tagCueUrl.split(",");
                    List<String> urls = Arrays.asList(urlArray);
                    FullScreenDialog dialog = new FullScreenDialog(NewShopCartListActivity.this, urls);
                    dialog.show();
                }
            });
        }
    }

    @Override
    public void showFindsameRebateResult(List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList, List<RebateBo> data) {

        if (data != null && data.size() > 0) {
            for (RebateBo rebateBo : data) {
                for (GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods : recommemdList) {
                    if (rebateBo != null && recommendGoods != null && rebateBo.getItemId().equals(recommendGoods.getFields().getItemId())) {
                        recommendGoods.setRebateBo(rebateBo);
                        break;
                    }
                }
            }
            guessLikeAdapter.setData(recommemdList);
        } else {
            guessLikeAdapter.setData(recommemdList);
        }


    }


    /**
     * 检查是否要更后续的数据
     *
     * @param currentSelectedPos
     */
    private void checkLoadNextListData(int currentSelectedPos) {
        AppDebug.i(TAG, "checkLoadNextListData currentSelectedPos=" + currentSelectedPos + " listCount="
                + mShopCartListAdapter.getCount() + " mIsRquestingNextData=" + mIsRquestingNextData
                + " mIsNotNextData=" + !mCartBuilder.haveNextData());
        currentSelectedPosition = currentSelectedPos;
        // 当前位置之差小于一定的值，不在上次请求过程中，后续还有新的数据
        if ((mShopCartListAdapter.getCount() - currentSelectedPos) <= REQUEST_GOODS_DATA_DIFF && !mIsRquestingNextData
                && mCartBuilder.haveNextData()) {
            mIsRquestingNextData = true;
            OnWaitProgressDialog(true);
            mPresenter.getShopCartList(NewShopCartListActivity.this, mCartBuilder, cartFrom, QueryBagType.REQUEST_NEXT_DATA);
            if (foot != null) {
                foot.setVisibility(View.GONE);
            }
        } else {

            if (!bottonLineFlag) {
                bottonLineFlag = true;
                if (foot != null) {
                    innerFocusGroupListView.removeFooterView(foot);
                }

                if (mShopCartListAdapter.getTotalItemCount() == 1) {
                    foot = getLayoutInflater().inflate(R.layout.layout_onitem_new_shop_cart_foot, null, false);
                } else {
                    foot = getLayoutInflater().inflate(R.layout.layout_new_shop_cart_foot, null, false);
                }

                innerFocusGroupListView.addFooterView(foot, null, false);

            }

            if (foot != null) {
                foot.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 显示空的购物车UI
     */
    @Override
    public void showCartEmpty() {
        innerFocusGroupListView.setVisibility(View.GONE);
        mAccountView.setVisibility(View.GONE);
        layoutSubmit.setVisibility(View.GONE);
        mShopCartEmptyLayout.setVisibility(View.VISIBLE);
        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        /*if (gc != null && gc.getShopCartFlag() != null && gc.getShopCartFlag().isActing()) {
            String imageUrl = gc.getShopCartFlag().getNewShopCatIcon();
            ImageLoaderManager.getImageLoaderManager(this).displayImage(imageUrl, mShopCartEmptyIcon);
        }*/
        Map<String, String> properties = Utils.getProperties();
        properties.put("name", "购物车返现");
        Utils.utCustomHit("Expose_shopcart_lable", properties);
        mShopCartEmptyRecyclerView.requestFocus();

        mFocusPositionManager.focusStop();
    }

    /**
     * 显示购物车列表
     */
    private void showCartList() {
        innerFocusGroupListView.setVisibility(View.VISIBLE);
        mAccountView.setVisibility(View.VISIBLE);
        layoutSubmit.setVisibility(View.VISIBLE);
        mShopCartEmptyLayout.setVisibility(View.GONE);
//        innerFocusGroupListView.requestFocus();
    }

    private void hideCartList() {
        innerFocusGroupListView.setVisibility(View.INVISIBLE);
        mAccountView.setVisibility(View.INVISIBLE);
        layoutSubmit.setVisibility(View.INVISIBLE);
        mShopCartEmptyLayout.setVisibility(View.GONE);
        if (mShopCartIcon != null)
        {
            mShopCartIcon.setImageDrawable(null);
        }
        if (mShopCartEmptyIcon != null)
            mShopCartEmptyIcon.setImageDrawable(null);
        mFocusPositionManager.focusHide();
    }

    /**
     * 更新购物列表数据
     *
     * @param updateGoodsCount 是否更新购物车总数
     */
    @Override
    public void updateShopCartList(boolean updateGoodsCount) {
        AppDebug.i(TAG, TAG + ".updateShopCartList.mFirstUpdateList = " + mFirstUpdateList
                + ", mIsListViewScrolling = " + mIsListViewScrolling + ", cartFrom = " + cartFrom
                + ", updateGoodsCount = " + updateGoodsCount);
        // 如果是非首次并且列表还在滚动中
        if (!mFirstUpdateList && mIsListViewScrolling) {
            // 记录标识等待滚动完成后再刷新数据
            mHaveNewDataNeedUpdate = true;
            return;
        }

        // 第一次创建和当需要更新时时更新数目
        if (updateGoodsCount || mFirstUpdateList) {
            String text = String.format(getResources().getString(R.string.newcart_title),
                    mCartBuilder.getCartGoodsCount());
            AppDebug.i(TAG, TAG + ".updateShopCartList.GoodsCount = " + text);
            if (mShopCartTitle != null)
                mShopCartTitle.setText(text);
        }

        Map<String, List<CartGoodsComponent>> data = mCartBuilder.getCartData();
//        data = null;//fixme remove
        if (data != null && data.size() > 0) {
            showCartList();
            mShopCartListAdapter.setShopItemData(data);
            mShopCartListAdapter.notifyDataSetChanged();
            onHandleAnaylisysTaoke(data);
            if (innerFocusGroupListView != null) {
                if (!bottonLineFlag) {
                    bottonLineFlag = true;
                    if (foot != null) {
                        innerFocusGroupListView.removeFooterView(foot);
                    }

                    if (mShopCartListAdapter.getTotalItemCount() == 1) {
                        foot = getLayoutInflater().inflate(R.layout.layout_onitem_new_shop_cart_foot, null, false);
                    } else {
                        foot = getLayoutInflater().inflate(R.layout.layout_new_shop_cart_foot, null, false);
                    }

                    innerFocusGroupListView.addFooterView(foot, null, false);
                }
                checkLoadNextListData(innerFocusGroupListView.getSelectedItemPosition());
                if (TYPE_DELAY_UPDATE_SELECTED_STATE > 0) {
                    innerFocusGroupListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (TYPE_DELAY_UPDATE_SELECTED_STATE) {
                                    case 1:
                                        innerFocusGroupListView.onItemSelected(true);
                                        break;
                                    case 2:
                                        innerFocusGroupListView.onItemSelected(false);
                                        break;
                                }
                                TYPE_DELAY_UPDATE_SELECTED_STATE = -1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 200);
                }
            }
            updateAccountInfo();
            // 重新开始显示Focus框，并且Focuse新的View
//
//            innerFocusGroupListView.setSelection(2);
            mFocusPositionManager.focusStart();
            mFocusPositionManager.resetFocused();
//            innerFocusGroupListView.requestFocus();
            if (mFirstUpdateList) {
                if (mShopCartListAdapter.getGroupCount() >= 1 && mShopCartListAdapter.getItemCount(0) >= 1) {
                    currentSelectedPosition = 2;//todo
                    innerFocusGroupListView.setSelection(2);
                    mShopCartListAdapter.setSelectedPos(2);
//                    innerFocusGroupListView.manualFindFocusInner(KeyEvent.KEYCODE_DPAD_LEFT);
                    simulateKeystroke(KeyEvent.KEYCODE_DPAD_LEFT);
                    innerFocusGroupListView.resetInnerFocusState();
                    innerFocusGroupListView.onItemSelected(true);
                }
            }
            mAccountView.setFocusable(true);
        } else {
            showCartEmpty();
        }
        mHaveNewDataNeedUpdate = false;
        mFirstUpdateList = false;
    }

    @Override
    public void rebuildView() {
        mCartBuilder.reBuildView(deleteIdList);
        deleteId = null;
    }

    public void simulateKeystroke(final int keyCode) {


        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                View view = innerFocusGroupListView.getSelectedView();
                if (view != null && view instanceof InnerFocusLayout) {
                    ((InnerFocusLayout) view).findFirstFocus(keyCode);
                    ((InnerFocusLayout) view).setNextFocusSelected();
                }
            }
        });
    }

    /**
     * 更新结算信息
     */
    private void updateAccountInfo() {
        FooterComponent footerComponent = mCartBuilder.getCartFooterComponent();
        if (footerComponent != null) {
            List<DynamicCrossShopPromotion> dynamicCrossShopPromotions = footerComponent.getDynamicCrossShopPromotions();
            if (mCartBuilder != null && mShopCartTitle != null) {
                String text = String.format(getResources().getString(R.string.newcart_title),
                        mCartBuilder.getCartGoodsCount());
                AppDebug.i(TAG, TAG + ".updateShopCartList.GoodsCount = " + text);
                mShopCartTitle.setText(text);
            }

            mAccountShopCountView.setText("选中" + footerComponent.getQuantity().getValue() + "件宝贝");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (footerComponent.getPay() != null) {
                if (layoutBenefit != null) {
                    layoutBenefit.removeAllViews();
                }
                if (footerComponent.getPay().getTotalDiscountTitle() != null) {
                    if (footerComponent.getPay().getTotalDiscount() != null && !footerComponent.getPay().getTotalDiscount().equals("￥0")) {
                        TextView tvDiscount = new TextView(NewShopCartListActivity.this);
                        tvDiscount.setTextColor(getResources().getColor(R.color.new_cart_98badf));
                        tvDiscount.setTextSize(18);
                        tvDiscount.setLayoutParams(layoutParams);
                        layoutBenefit.setVisibility(View.VISIBLE);
                        tvDiscount.setText(footerComponent.getPay().getTotalDiscountTitle().replaceAll("￥", " ¥ "));
                        layoutBenefit.addView(tvDiscount);
                    }

                }
                if (footerComponent.getPay().getPostTitle() != null) {
                    tvPost.setVisibility(View.VISIBLE);
                    tvPost.setText(footerComponent.getPay().getPostTitle());
                } else {
                    tvPost.setVisibility(View.INVISIBLE);
                }
                if (footerComponent.getPay().getPrice() != 0 && footerComponent.getPay().getTsmTotalDiscount() != null) {
                    TextView tvTsm = new TextView(NewShopCartListActivity.this);
                    tvTsm.setTextColor(getResources().getColor(R.color.new_cart_98badf));
                    tvTsm.setTextSize(18);
                    tvTsm.setLayoutParams(layoutParams);
                    layoutBenefit.setVisibility(View.VISIBLE);
                    tvTsm.setText(footerComponent.getPay().getTsmTotalDiscount().replaceAll("￥", " ¥ "));
                    layoutBenefit.addView(tvTsm);
                }
                if (footerComponent.getPay().getTotalDiscountTitle() == null &&
                        footerComponent.getPay().getTsmTotalDiscount() == null) {
                    layoutBenefit.removeAllViews();
                    layoutBenefit.setVisibility(View.GONE);
                }
            } else {
                layoutBenefit.removeAllViews();
                layoutBenefit.setVisibility(View.GONE);
            }
            // 商品价格
            mAccountPriceView.setText("" + footerComponent.getPay().getPriceTitle().replaceAll("￥", " ¥ "));
            //返利金额
//            Map<String, List<CartGoodsComponent>> cartData = mCartBuilder.getCartData();
//
//             mGetRebateTask = new GetRebateTask(new OnGetRebateListener() {
//                @Override
//                public void onReuslt(String rebate) {
//                    if (rebate != null && !"".equals(rebate)) {
//                        mChooseRebateTxt.setText(rebate);
//                        mChooseRebateTxt.setVisibility(View.VISIBLE);
//                    } else {
//                        mChooseRebateTxt.setVisibility(View.GONE);
//                    }
//                }
//            }).execute(cartData);

            mAccountView.setVisibility(View.VISIBLE);
        } else {
            mAccountView.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 显示删除商品对话框
     */
    private void showDeletedShop(final int selectedPosition) {
        if (isFinishing()) {
            return;
        }
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }

        commonDialog = new CustomDialog.Builder(this).setType(2)
                .setMessage(getResources().getString(R.string.newcart_delete_shop))
                .setPositiveButton(getResources().getString(R.string.ytbv_confirm),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedShop confirm");
                                Utils.utControlHit(getFullPageName(),
                                        "Button-Delete_Confirm_sure", initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_DELETE_YES));
                                // 删除商品
                                if (!mIsRquestingDelete) {
                                    ActionType type = ActionType.DELETE;
                                    mIsRquestingDelete = true;
                                    CartGoodsComponent data = mShopCartListAdapter.getGoodsItemData(selectedPosition);
                                    if (data != null && data.getItemComponent() != null) {
                                        List<Component> items = new ArrayList<Component>();
                                        data.getItemComponent().setChecked(true, false);
                                        items.add(data.getItemComponent());
                                        deleteId = data.getItemComponent().getItemId();
                                        deleteIdList.add(deleteId);
                                        OnWaitProgressDialog(true);
                                        TYPE_DELAY_UPDATE_SELECTED_STATE = 1;
                                        mPresenter.updateBagRequest(NewShopCartListActivity.this, items, type, cartFrom, QueryBagType.REQUEST_DELETE);
                                    }
                                }
                                commonDialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.ytbv_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedShop cancel");
                                Utils.utControlHit(getFullPageName(),
                                        "Button-Delete_Confirm_sure", initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_DELETE_NO));

                                commonDialog.dismiss();
                            }

                        }).create();
        Utils.utCustomHit("", "Expose_tbCart_Button-Delete_Confirm", initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_DELETE));
        commonDialog.show();
    }

    /**
     * 显示删除全部无效商品对话框
     */
    private void showDeletedAllInvalidShop(final int selectedPosition) {
        if (isFinishing()) {
            return;
        }
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CustomDialog.Builder(this).setType(2)
                .setMessage(getResources().getString(R.string.newcart_delete_all_invalid_shop))
                .setPositiveButton(getResources().getString(R.string.ytbv_confirm),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedAllInvalidShop confirm");
                                // 删除全部无效商品
                                ActionType type = ActionType.DELETE;
                                List<CartGoodsComponent> dataList = mShopCartListAdapter
                                        .getGoodsItemDataList(selectedPosition);
                                if (dataList != null) {
                                    List<Component> items = null;
                                    for (CartGoodsComponent data : dataList) {
                                        if (data != null && data.getItemComponent() != null) {
                                            if (items == null) {
                                                items = new ArrayList<Component>();
                                            }
                                            items.add(data.getItemComponent());
                                            deleteId = data.getItemComponent().getItemId();
                                            deleteIdList.add(deleteId);
                                        }
                                    }
                                    OnWaitProgressDialog(true);
                                    TYPE_DELAY_UPDATE_SELECTED_STATE = 1;
                                    mPresenter.updateBagRequest(NewShopCartListActivity.this, items, type, cartFrom, QueryBagType.REQUEST_DELETE);
                                }
                                commonDialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.ytbv_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedAllInvalidShop cancel");
                                commonDialog.dismiss();
                            }

                        }).create();
        commonDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppDebug.i(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == SHOP_CART_ACTIVITY_FOR_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            updateSkuId = data.getStringExtra(BaseConfig.INTENT_KEY_SKUID);
            updateQuantity = data.getIntExtra(BaseConfig.INTENT_KEY_BUY_COUNT, -1);
            // 编辑SKU
            CartGoodsComponent goodsData = mShopCartListAdapter.getGoodsItemData(mSkuEditPosition);
            if (goodsData != null && goodsData.getItemComponent() != null) {
                AppDebug.i(TAG, "onActivityResult skuId=" + updateSkuId + " preSkuId="
                        + goodsData.getItemComponent().getSku().getSkuId() + " preQuantity="
                        + goodsData.getItemComponent().getItemQuantity().getQuantity() + " quantity=" + updateQuantity);
                // 设置skuID
                if (updateSkuId != null && !updateSkuId.equals(goodsData.getItemComponent().getSku().getSkuId())) {
                    if (!goodsData.getItemComponent().isPreSell()) {
                        goodsData.getItemComponent().setSkuId(updateSkuId);
                    }
                    List<Component> items = new ArrayList<Component>();
                    items.add(goodsData.getItemComponent());
                    // SKU不是一样的，等待SKU更新完成后再设置商品数量
                    if (goodsData.getItemComponent().getItemQuantity().getQuantity() != updateQuantity) {
                        mPresenter.setNextActionType(ActionType.UPDATE_QUANTITY, mSkuEditPosition, goodsData
                                .getItemComponent().getCartId(), updateQuantity);
                    }
                    AppDebug.v(TAG, TAG + ".onActivityResult.items = " + items + ",items.size = " + items.size());
                    mPresenter.updateBagRequest(NewShopCartListActivity.this, items, ActionType.UPDATE_SKU, cartFrom, QueryBagType.REQUEST_UPDATE_SKU);
                } else {
                    // SKU是一样的，设置商品数量
                    if (goodsData.getItemComponent().getItemQuantity().getQuantity() != updateQuantity) {
                        updateGoodsQuanity(goodsData.getItemComponent(), updateQuantity);
                    }
                }

            }
        } else if (requestCode == mSubmitRequestCode && resultCode == RESULT_OK) {
            hideCartList();
            TYPE_DELAY_UPDATE_SELECTED_STATE = 2;
            initCartBudiler();
        } else if (requestCode == mBackFromLikeRequestCode && resultCode == RESULT_OK) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 更新商品的数量
     *
     * @param itemComponent
     * @param quantity
     */
    private void updateGoodsQuanity(ItemComponent itemComponent, int quantity) {
        if (itemComponent != null) {
            if (!itemComponent.isPreSell()) {
                itemComponent.setQuantity(quantity);
            }
            List<Component> items = new ArrayList<Component>();
            items.add(itemComponent);
            mPresenter.updateBagRequest(NewShopCartListActivity.this, items, ActionType.UPDATE_QUANTITY, cartFrom, QueryBagType.REQUEST_UPDATE_QUANTITY);
        }
    }

    /**
     * 新数据请求完成
     */
    @Override
    public void requestNewDataDone(boolean noData) {
        AppDebug.i(TAG, "requestNewDataDone noData=" + noData);
        mIsNotNextData = noData;
        mIsRquestingNextData = false;
    }

    @Override
    public void updateGoodsQuanitiy(int position, String cartId, Integer quantity) {
        CartGoodsComponent goodsData = mShopCartListAdapter.getGoodsItemData(position);
        if (goodsData != null && goodsData.getItemComponent() != null) {
            String currCartId = goodsData.getItemComponent().getCartId();
            AppDebug.i(TAG, "updateGoodsQuanitiy currQuantity=" + goodsData.getItemComponent().getItemQuantity().getQuantity()
                    + " quantity=" + quantity + " currCartId=" + currCartId + " cartId=" + cartId);
            // 相同的CartId
            if (goodsData.getItemComponent().getItemQuantity().getQuantity() != quantity && currCartId != null
                    && currCartId.compareTo(cartId) == 0) {
                updateGoodsQuanity(goodsData.getItemComponent(), quantity);
                return;
            }
        } else {
            // 更新失败提示
            if (commonDialog != null && commonDialog.isShowing()) {
                commonDialog.dismiss();
            }
            commonDialog = new CustomDialog.Builder(NewShopCartListActivity.this).setType(1)
                    .setResultMessage(getString(R.string.newcart_update_quantity_fail))
                    .create();
            commonDialog.show();
        }
    }

    /**
     * 数据请求完成
     */
    @Override
    public void requestDataDone(boolean success) {
        mIsRquestingDelete = false;
        mIsRquestingNextData = false;
        OnWaitProgressDialog(false);
    }

    /**
     * 自定义淘客详情页打点
     */
    private void onHandleAnaylisysTaoke(Map<String, List<CartGoodsComponent>> data) {
        AppDebug.d(TAG, "anaylisysTaoke User.isLogined " + User.isLogined());
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            StringBuilder sellerIds = new StringBuilder();
            StringBuilder shopTypes = new StringBuilder();
            StringBuilder itemIds = new StringBuilder();
            for (Iterator<?> it = data.keySet().iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                List<CartGoodsComponent> goodsList = data.get(key);
                if (goodsList != null) {
                    for (int i = 0; i < goodsList.size(); i++) {
                        CartGoodsComponent goods = goodsList.get(i);

                        sellerIds.append(goods.getItemComponent().getSellerId()).append(",");
                        itemIds.append(goods.getItemComponent().getItemId()).append(",");
                        String toBuy = goods.getItemComponent().getToBuy();
                        // 淘宝天猫识别
                        if (toBuy != null && toBuy.equalsIgnoreCase("tmall")) {
                            shopTypes.append("B").append(",");
                        } else {
                            shopTypes.append("C").append(",");
                        }
                    }
                }
            }
            String stbId = DeviceUtil.initMacAddress(this);
            BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), itemIds.toString(), shopTypes.toString(), sellerIds.toString(), new TaokeDetailListener(new WeakReference<BaseActivity>(this)));
        }
    }

    @Override
    public void showProgressDialog(boolean show) {
        OnWaitProgressDialog(show);
    }

    /**
     * 淘客详情打点
     */
    private static class TaokeDetailListener extends BizRequestListener<JSONObject> {

        public TaokeDetailListener(WeakReference<BaseActivity> baseActivityRef) {
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

    /**
     * 显示商品二维码
     *
     * @param itemUrl
     */
    public void showItemQRCodeFromUrl(String text, String itemUrl, Bitmap icon, boolean show,
                                      DialogInterface.OnKeyListener onKeyListener) {
        Bitmap qrBitmap = getQrCodeBitmap(itemUrl, icon);

        onQRCodeDialog(text, qrBitmap, show, onKeyListener);
    }

    /**
     * 显示二维码对话框
     *
     * @param show
     */
    private void onQRCodeDialog(String text, Bitmap bitmap, boolean show, DialogInterface.OnKeyListener onKeyListener) {
        if (mQRCodeDialog != null && mQRCodeDialog.isShowing()) {
            mQRCodeDialog.dismiss();
            mQRCodeDialog = null;
        }

        if (isFinishing()) {
            return;
        }

        mQRCodeDialog = new QRCodeDialog.Builder(this).setQRCodeText(text).setQrCodeBitmap(bitmap).create();
        if (onKeyListener != null) {
            mQRCodeDialog.setOnKeyListener(onKeyListener);
        }

        if (show) {
            mQRCodeDialog.show();
        } else {
            mQRCodeDialog.dismiss();
        }
    }

    /**
     * 获取二维码图片
     *
     * @param itemUrl
     * @param icon
     * @return
     */
    public Bitmap getQrCodeBitmap(String itemUrl, Bitmap icon) {
        Bitmap qrBitmap = null;
        int width = (int) getResources().getDimensionPixelSize(com.yunos.tvtaobao.businessview.R.dimen.dp_422);
        int height = width;
        try {
            qrBitmap = QRCodeManager.create2DCode(itemUrl, width, height, icon);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return qrBitmap;
    }

    /**
     * 登录
     */
    @Override
    protected void refreshData() {
        mFocusPositionManager.focusStop();
        initView();

    }

    /**
     * 登录取消
     */
    @Override
    protected void onLoginCancel() {
        finish();
    }

    @Override
    public String getPageName() {
        return "Cart";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put("version", AppInfo.getAppVersionName());
        p.put("appkeyid", Config.getChannel());
        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }
        p.put(SPMConfig.SPM_CNT, SPMConfig.SHOP_CART_LIST_SPM);
        return p;
    }

    public Map<String, String> initTBSProperty(String item_id, String name, String shop_id, String valid, boolean showUserId, String spm) {

        Map<String, String> p = Utils.getProperties();

        if (!TextUtils.isEmpty(spm)) {
            p.put("item_id", item_id);
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("name", name);
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("shop_id", shop_id);
        }
        if (!TextUtils.isEmpty(valid)) {
            p.put("valid", valid);
        }

        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin() && showUserId) {
            p.put("user_id", User.getUserId());
        }

        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

        return p;

    }

    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();


        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

        return p;

    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return mFocusPositionManager;
    }


    public interface OnGetRebateListener {
        void onReuslt(String rebate);
    }

}
