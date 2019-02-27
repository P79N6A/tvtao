package com.yunos.tvtaobao.cartbag.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.zxing.WriterException;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FooterComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.dialog.QRCodeDialog;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.biz.widget.FocusNoDeepFrameLayout;
import com.yunos.tvtaobao.biz.widget.InnerFocusGroupHorizonalListView;
import com.yunos.tvtaobao.cartbag.R;
import com.yunos.tvtaobao.cartbag.adapter.ShopCartListAdapter;
import com.yunos.tvtaobao.cartbag.adapter.ShopCartListAdapter.OnShopItemControllerClickListener;
import com.yunos.tvtaobao.cartbag.adapter.ShopCartListAdapter.ShopItemAction;
import com.yunos.tvtaobao.cartbag.component.CartGoodsComponent;
import com.yunos.tvtaobao.cartbag.view.CartBuilder;
import com.yunos.tvtaobao.cartbag.view.CartBuilder.CartSubmitResult;
import com.yunos.tvtaobao.cartbag.view.CartBuilder.CartSubmitResultType;
import com.yunos.tvtaobao.cartbag.view.ShopCartHintFocusLayout;
import com.yunos.tvtaobao.cartbag.view.ShopCartItemFocusLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 显示的列表购物车列表
 */
public class ShopCartListActivity extends BaseActivity {

    private static final String TAG = "ShopCartListActivity";
    public static int SHOP_CART_ACTIVITY_FOR_RESULT_CODE = 1; // 购物车调用页返回的结果代码

    private int REQUEST_GOODS_DATA_DIFF = 9; // 请求的商品跟列表的商品相差多少个时候发起下个请求
    private FocusPositionManager mFocusPositionManager; // 整体布局
    private FocusNoDeepFrameLayout mAccountView; // 结算的View
    private TextView mAccountShopCountView; // 结算商品的个数
    private TextView mAccountPriceView; // 结算商品的总价格
    private TextView mAccountExpressView; // 结算商品的邮费信息
    private int mWhiteColor; // 白色的颜色值
    private ViewGroup mShopCartEmptyLayout; // 购物车列表为空的布局
    private InnerFocusGroupHorizonalListView mInnerFocusGroupHorizonalListView; // 购物车列表带Focus内部功能
    private ShopCartListAdapter mShopCartListAdapter; // 购物车列表的适配器
    private CartBuilder mCartBuilder; // 购物数据的Buidler
    private TextView mShopCartTitle; // 购物车列表个数的title
    private ImageView mShopCartIcon; //购物车打标
    private int mSkuEditPosition; // 选中sku编译商品位置
    private boolean mIsRquestingNextData; // 是否还在请求下次的数据
    private boolean mIsNotNextData; // 是否没有后续的数据
    private boolean mIsListViewScrolling; // 列表是否还有滚动中
    private boolean mHaveNewDataNeedUpdate; // 是否有新的数据需要更新
    private TvTaoBaoDialog mDeletedDialog; // 删除商品的对话框
    private boolean mIsRquestingDelete; // 是否在请求网络删除中
    private int mSubmitRequestCode = 666; //结算按钮的request code
    private String cartFrom = "default_client";
    private boolean mFirstUpdateList;// 第一次更新列表

    // 二维码
    protected QRCodeDialog mQRCodeDialog;
    private static String deleteId;
    private static List<String> deleteIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ytm_activity_cart_shop_list);
        registerLoginListener();

        AppDebug.i(TAG, TAG + ".onCreate bundle=" + getIntent().getExtras());
        cartFrom = getIntent().getStringExtra(BaseConfig.INTENT_KEY_CARTFROM);
        if (cartFrom == null) {
            cartFrom = "default_client";
        }
        //判断登录状态
        if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            setLoginActivityStartShowing();
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
            return;
        }

        mFirstUpdateList = true;
        initCartBudiler();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).free();
        unRegisterLoginListener();
        // 退出时关闭显示着的对话框
        if (mDeletedDialog != null && mDeletedDialog.isShowing()) {
            mDeletedDialog.dismiss();
        }
        mFirstUpdateList = false;
        super.onDestroy();
    }

    /**
     * 初始化购物车数据控制类
     */
    private void initCartBudiler() {
        //因为是单例,所以初始化时先清空下数据
        CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).free();
        mCartBuilder = new CartBuilder(this);
        OnWaitProgressDialog(true);
        mCartBuilder.queryBagRequest(new QueryBagListener(new WeakReference<BaseActivity>(this),
                QueryBagListener.QueryBagType.REQUEST_NEW_DATA), cartFrom);
    }

    /**
     * 初始化UI页面
     */
    private void initView() {
        mShopCartTitle = (TextView) findViewById(R.id.shop_cart_title);
        mShopCartIcon = (ImageView) findViewById(R.id.shop_cart_icon);
        String text = String.format(getResources().getString(R.string.ytm_shop_market_cart_title), 0);
        mShopCartTitle.setText(text);
        mShopCartEmptyLayout = (ViewGroup) findViewById(R.id.shop_cart_empty);
        initAccountView();
        initGroupListView(R.id.shop_cart_list_view);
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.shop_cart_list_layout);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));
        mFocusPositionManager.requestFocus();
        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (gc != null && gc.getShopCartFlag() != null && gc.getShopCartFlag().isActing()) {
            String imageUrl = gc.getShopCartFlag().getShopCatIcon();
            ImageLoaderManager.getImageLoaderManager(this).displayImage(imageUrl, mShopCartIcon);

            Map<String, String> properties = Utils.getProperties();
            properties.put("name", "购物车返现");
            Utils.utCustomHit("Expose_shopcart_lable", properties);
        }
    }

    /**
     * 初始化购物列表的UI
     *
     * @param resId
     */
    private void initGroupListView(int resId) {
        mInnerFocusGroupHorizonalListView = (InnerFocusGroupHorizonalListView) findViewById(resId);
        mShopCartListAdapter = new ShopCartListAdapter(this, mInnerFocusGroupHorizonalListView);
        mInnerFocusGroupHorizonalListView.setFlipScrollFrameCount(12);

        mInnerFocusGroupHorizonalListView.setAdapter(mShopCartListAdapter);
        mInnerFocusGroupHorizonalListView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                AppDebug.i(TAG, TAG + ".onItemSelected position=" + position + " isSelected=" + isSelected
                        + ".selectedview = " + v);
                if (v instanceof ShopCartHintFocusLayout) {
                    mShopCartListAdapter.selectedHintView(isSelected, position, (ShopCartHintFocusLayout) v);
                } else if (v instanceof ShopCartItemFocusLayout) {
                    mShopCartListAdapter.selectedShopItemView(isSelected, position, (ShopCartItemFocusLayout) v);
                }

                if (isSelected) {
                    checkLoadNextListData(position);
                }
            }
        });
        mInnerFocusGroupHorizonalListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDebug.i(TAG, TAG + ".onItemClick.position=" + position + ",id = " + id + ", view = " + view);
                // 点击分组的View
                if (view instanceof ShopCartHintFocusLayout) {
                    mShopCartListAdapter.hideShopItemControllerView();
                    //                    mShopCartListAdapter.notifyDataSetChanged();
                    mShopCartListAdapter.checkShopHint(position, (ShopCartHintFocusLayout) view);
                    updateAccountInfo();
                } else if (view instanceof ShopCartItemFocusLayout) {
                    mShopCartListAdapter.hideShopItemControllerView();
                    //                    mShopCartListAdapter.notifyDataSetChanged();
                    mShopCartListAdapter.checkShopItem(position, (ShopCartItemFocusLayout) view);
                    updateAccountInfo();
                }
            }
        });
        mInnerFocusGroupHorizonalListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    mIsListViewScrolling = false;
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

        mShopCartListAdapter.setOnShopItemControllerClickListener(new OnShopItemControllerClickListener() {

            @Override
            public void onShopItemControllerClick(ShopItemAction action, int position) {
                AppDebug.v(TAG, TAG + ".onShopItemControllerClick.action = " + action + ".position = " + position);
                switch (action) {
                    case DELETE: {
                        // 删除
                        AppDebug.i(TAG, "onShopItemControllerClick DELETE mIsRquestingDelete=" + mIsRquestingDelete);
                        showDeletedShop(position);
                    }
                    break;
                    case EDIT: {
                        // 进入sku页面编辑
                        CartGoodsComponent data = mShopCartListAdapter.getGoodsItemData(position);
                        if (data != null && data.getItemComponent() != null) {
                            mSkuEditPosition = position;
                            Intent intent = new Intent();
                            intent.setClassName(ShopCartListActivity.this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
                            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, data.getItemComponent().getItemId());
                            intent.putExtra(BaseConfig.INTENT_KEY_SKU_TYPE, TradeType.EDIT_CART);
                            intent.putExtra(BaseConfig.INTENT_KEY_SKUID, data.getItemComponent().getSku().getSkuId());
                            intent.putExtra(BaseConfig.INTENT_KEY_BUY_COUNT, data.getItemComponent().getItemQuantity().getQuantity());
                            startActivityForResult(intent, SHOP_CART_ACTIVITY_FOR_RESULT_CODE);
                        }
                    }
                    break;
                    case DETAIL: {
                        // 打开详情页
                        CartGoodsComponent data = mShopCartListAdapter.getGoodsItemData(position);
                        if (data != null && data.getItemComponent() != null) {
                            Intent intent = new Intent();
                            intent.setClassName(ShopCartListActivity.this, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
                            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, data.getItemComponent().getItemId());
                            startActivity(intent);
                        }
                    }
                    break;
                    case CHECKED: {
                        // 结算
                        View childView = mInnerFocusGroupHorizonalListView.getChildAt(position
                                - mInnerFocusGroupHorizonalListView.getFirstPosition());
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
                        showDeletedAllInvalidShop(position);
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始化结算UI
     */
    private void initAccountView() {
        mWhiteColor = getResources().getColor(R.color.ytm_white);

        // 商品个数
        mAccountView = (FocusNoDeepFrameLayout) findViewById(R.id.account_btn);
        // 设置Focus框的空隙
        mAccountView.setCustomerFocusPaddingRect(new Rect(getResources().getDimensionPixelSize(
                R.dimen.ytm_shop_cart_item_account_padding_left), getResources().getDimensionPixelSize(
                R.dimen.ytm_shop_cart_item_account_padding_top), 0, 0));
        mAccountView.setVisibility(View.INVISIBLE);
        mAccountShopCountView = (TextView) mAccountView.findViewById(R.id.account_count);
        // 价格
        mAccountPriceView = (TextView) mAccountView.findViewById(R.id.account_price);
        mAccountExpressView = (TextView) mAccountView.findViewById(R.id.account_express);
        // 默认不选中
        focusedAccountView(false);
        mAccountView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                focusedAccountView(hasFocus);
            }
        });
        mAccountView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 埋点统计
                Map<String, String> p = Utils.getProperties();
                TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "Cart_Buy", null, "Click"),
                        Utils.getKvs(p));

                CartSubmitResult result = mCartBuilder.submitCart();
                AppDebug.i(TAG, "mAccountView onClick result=" + result.mResultType);
                if (result != null && result.mResultType == CartSubmitResultType.SUCCESS) {
                    if (result.mBuildOrderRequestBo != null) {
                        Intent intent = new Intent();
                        intent.setClassName(ShopCartListActivity.this, BaseConfig.SWITCH_TO_BUILDORDER_ACTIVITY);
                        result.mBuildOrderRequestBo.setFrom(BaseConfig.ORDER_FROM_CART);
                        intent.putExtra("mBuildOrderRequestBo", result.mBuildOrderRequestBo);
                        startActivityForResult(intent, mSubmitRequestCode);
                    }

                } else if (result != null && result.mResultType == CartSubmitResultType.ERROR_H5_ORDER) {
                    // 不支持TV端的就弹出二维码进行扫描
                    showItemQRCodeFromUrl(result.mErrorMsg, BaseConfig.CART_URL, null, true, null);
                } else {
                    showErrorDialog(result.mErrorMsg, false);
                }
            }
        });
    }

    /**
     * Focus状态的变化设置不同的UI显示
     *
     * @param hasFocus
     */
    private void focusedAccountView(boolean hasFocus) {
        if (hasFocus) {
            mAccountShopCountView.setTextColor(mWhiteColor);
            mAccountShopCountView.setAlpha(1.0f);

            mAccountPriceView.setTextColor(mWhiteColor);
            mAccountPriceView.setAlpha(1.0f);

            mAccountExpressView.setTextColor(mWhiteColor);
            mAccountExpressView.setAlpha(1.0f);
        } else {
            mAccountShopCountView.setTextColor(mWhiteColor);
            mAccountShopCountView.setAlpha(0.5f);

            mAccountPriceView.setTextColor(mWhiteColor);
            mAccountPriceView.setAlpha(0.5f);

            mAccountExpressView.setTextColor(mWhiteColor);
            mAccountExpressView.setAlpha(0.5f);
        }
    }

    /**
     * 显示空的购物车UI
     */
    private void showCartEmpty() {
        mInnerFocusGroupHorizonalListView.setVisibility(View.GONE);
        mAccountView.setVisibility(View.GONE);
        mShopCartEmptyLayout.setVisibility(View.VISIBLE);
        mFocusPositionManager.focusStop();
    }

    /**
     * 检查是否要更后续的数据
     *
     * @param currentSelectedPos
     */
    private void checkLoadNextListData(int currentSelectedPos) {
        AppDebug.i(TAG, "checkLoadNextListData currentSelectedPos=" + currentSelectedPos + " listCount="
                + mShopCartListAdapter.getCount() + " mIsRquestingNextData=" + mIsRquestingNextData
                + " mIsNotNextData=" + mIsNotNextData);
        // 当前位置之差小于一定的值，不在上次请求过程中，后续还有新的数据
        if ((mShopCartListAdapter.getCount() - currentSelectedPos) <= REQUEST_GOODS_DATA_DIFF && !mIsRquestingNextData
                && !mIsNotNextData) {
            mIsRquestingNextData = true;
            //OnWaitProgressDialog(true);
            mCartBuilder.queryBagRequest(new QueryBagListener(
                    new WeakReference<BaseActivity>(ShopCartListActivity.this),
                    QueryBagListener.QueryBagType.REQUEST_NEXT_DATA), cartFrom);
        }
    }

    /**
     * 显示购物车列表
     */
    private void showCartList() {
        mInnerFocusGroupHorizonalListView.setVisibility(View.VISIBLE);
        mAccountView.setVisibility(View.VISIBLE);
        mShopCartEmptyLayout.setVisibility(View.GONE);
        mInnerFocusGroupHorizonalListView.requestFocus();
    }

    /**
     * 更新购物列表数据
     *
     * @param updateGoodsCount 是否更新购物车总数
     */
    private void updateShopCartList(boolean updateGoodsCount) {
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
            String text = String.format(getResources().getString(R.string.ytm_shop_market_cart_title),
                    mCartBuilder.getCartGoodsCount());
            AppDebug.i(TAG, TAG + ".updateShopCartList.GoodsCount = " + text);
            mShopCartTitle.setText(text);
        }

        Map<String, List<CartGoodsComponent>> data = mCartBuilder.getCartData();
        if (data != null && data.size() > 0) {
            showCartList();
            mShopCartListAdapter.setShopItemData(data);
            mShopCartListAdapter.notifyDataSetChanged();
            onHandleAnaylisysTaoke(data);
            if (mInnerFocusGroupHorizonalListView != null) {
                checkLoadNextListData(mInnerFocusGroupHorizonalListView.getSelectedItemPosition());
            }
            updateAccountInfo();
            // 重新开始显示Focus框，并且Focuse新的View
            mFocusPositionManager.focusStart();
            mFocusPositionManager.resetFocused();
        } else {
            showCartEmpty();
        }
        mHaveNewDataNeedUpdate = false;
        mFirstUpdateList = false;
    }

    /**
     * 更新结算信息
     */
    private void updateAccountInfo() {
        FooterComponent footerComponent = mCartBuilder.getCartFooterComponent();
        if (footerComponent != null) {

            if (mCartBuilder != null && mShopCartTitle != null) {
                String text = String.format(getResources().getString(R.string.ytm_shop_market_cart_title),
                        mCartBuilder.getCartGoodsCount());
                AppDebug.i(TAG, TAG + ".updateShopCartList.GoodsCount = " + text);
                mShopCartTitle.setText(text);
            }

            // 商品个数
            String text = String.format(getResources().getString(R.string.ytm_shop_cart_account_count), footerComponent
                    .getQuantity().getValue());
            mAccountShopCountView.setText(text);

            // 商品价格
            String price = String.format(getResources().getString(R.string.ytm_shop_cart_account_price),
                    footerComponent.getPay().getPriceTitle());
            mAccountPriceView.setText(price);

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
        if (mDeletedDialog != null && mDeletedDialog.isShowing()) {
            mDeletedDialog.dismiss();
        }

        mDeletedDialog = new TvTaoBaoDialog.Builder(this)
                .setMessage(getResources().getString(R.string.ytm_shop_cart_delete_shop))
                .setPositiveButton(getResources().getString(R.string.ytbv_confirm),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedShop confirm");
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
                                        mCartBuilder.updateBagRequest(items, type, cartFrom, new QueryBagListener(
                                                new WeakReference<BaseActivity>(ShopCartListActivity.this),
                                                QueryBagListener.QueryBagType.REQUEST_DELETE));
                                    }
                                }
                                mDeletedDialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.ytbv_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedShop cancel");
                                mDeletedDialog.dismiss();
                            }

                        }).create();
        mDeletedDialog.show();
    }

    /**
     * 显示删除全部无效商品对话框
     */
    private void showDeletedAllInvalidShop(final int selectedPosition) {
        if (isFinishing()) {
            return;
        }
        if (mDeletedDialog != null && mDeletedDialog.isShowing()) {
            mDeletedDialog.dismiss();
        }
        mDeletedDialog = new TvTaoBaoDialog.Builder(this)
                .setMessage(getResources().getString(R.string.ytm_shop_cart_delete_all_invalid_shop))
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
                                        }
                                    }
                                    OnWaitProgressDialog(true);
                                    mCartBuilder.updateBagRequest(items, type, cartFrom, new QueryBagListener(
                                            new WeakReference<BaseActivity>(ShopCartListActivity.this),
                                            QueryBagListener.QueryBagType.REQUEST_DELETE));
                                }
                                mDeletedDialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.ytbv_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AppDebug.i(TAG, "showDeletedAllInvalidShop cancel");
                                mDeletedDialog.dismiss();
                            }

                        }).create();
        mDeletedDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        AppDebug.i(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == SHOP_CART_ACTIVITY_FOR_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            String skuId = data.getStringExtra(BaseConfig.INTENT_KEY_SKUID);
            int quantity = data.getIntExtra(BaseConfig.INTENT_KEY_BUY_COUNT, -1);
            // 编辑SKU
            if (skuId != null) {
                CartGoodsComponent goodsData = mShopCartListAdapter.getGoodsItemData(mSkuEditPosition);
                if (goodsData != null && goodsData.getItemComponent() != null) {
                    AppDebug.i(TAG, "onActivityResult skuId=" + skuId + " preSkuId="
                            + goodsData.getItemComponent().getSku().getSkuId() + " preQuantity="
                            + goodsData.getItemComponent().getItemQuantity().getQuantity() + " quantity=" + quantity);
                    // 设置skuID
                    if (!skuId.equals(goodsData.getItemComponent().getSku().getSkuId())) {
                        goodsData.getItemComponent().setSkuId(skuId);
                        List<Component> items = new ArrayList<Component>();
                        items.add(goodsData.getItemComponent());
                        QueryBagListener querBagListenr = new QueryBagListener(new WeakReference<BaseActivity>(
                                ShopCartListActivity.this), QueryBagListener.QueryBagType.REQUEST_UPDATE_SKU);
                        // SKU不是一样的，等待SKU更新完成后再设置商品数量
                        if (goodsData.getItemComponent().getItemQuantity().getQuantity() != quantity) {
                            querBagListenr.setNextActionType(ActionType.UPDATE_QUANTITY, mSkuEditPosition, goodsData
                                    .getItemComponent().getCartId(), quantity);
                        }
                        AppDebug.v(TAG, TAG + ".onActivityResult.items = " + items + ",items.size = " + items.size());
                        mCartBuilder.updateBagRequest(items, ActionType.UPDATE_SKU, cartFrom, querBagListenr);
                    } else {
                        // SKU是一样的，设置商品数量
                        if (goodsData.getItemComponent().getItemQuantity().getQuantity() != quantity) {
                            updateGoodsQuanity(goodsData.getItemComponent(), quantity);
                        }
                    }

                }
            }
        } else if (requestCode == mSubmitRequestCode && resultCode == RESULT_OK) {
            refreshData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 更新商品的数量
     *
     * @param position
     * @param cartId
     * @param quantity
     */
    private void updateGoodsQuanitiy(int position, String cartId, int quantity) {
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
        }
        // 更新失败提示
        Toast.makeText(this, getString(R.string.ytm_shop_cart_update_quantity_fail), Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新商品的数量
     *
     * @param itemComponent
     * @param quantity
     */
    private void updateGoodsQuanity(ItemComponent itemComponent, int quantity) {
        if (itemComponent != null) {
            itemComponent.setQuantity(quantity);
            List<Component> items = new ArrayList<Component>();
            items.add(itemComponent);
            mCartBuilder.updateBagRequest(items, ActionType.UPDATE_QUANTITY, cartFrom, new QueryBagListener(
                    new WeakReference<BaseActivity>(ShopCartListActivity.this),
                    QueryBagListener.QueryBagType.REQUEST_UPDATE_QUANTITY));
        }
    }

    /**
     * 新数据请求完成
     */
    private void requestNewDataDone(boolean noData) {
        AppDebug.i(TAG, "requestNewDataDone noData=" + noData);
        mIsNotNextData = noData;
        mIsRquestingNextData = false;
    }

    /**
     * 数据请求完成
     */
    private void requestDataDone(boolean success) {
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
     * 接口返回监听
     */
    public static class QueryBagListener extends BizRequestListener<String> {

        private static enum QueryBagType {
            REQUEST_NEW_DATA, REQUEST_NEXT_DATA, REQUEST_UPDATE_SKU, REQUEST_UPDATE_QUANTITY, REQUEST_DELETE
        }

        ;

        private int mNextActionPostion; // 下次需要处理的商品位置
        private String mNextActionCartId; // 下次需要处理的商品购物车的ID
        private ActionType mNextActionType; // 下次处理类型
        private Object mNextActionObj; // 数据类型
        private QueryBagType mQueryBagType; // 数据请求的类型

        public QueryBagListener(WeakReference<BaseActivity> mBaseActivityRef, QueryBagType requestType) {
            super(mBaseActivityRef);
            mQueryBagType = requestType;
        }

        /**
         * 设置下次需要请求更新数据的相关信息
         *
         * @param cartId
         * @param listPosition
         * @param cartId
         * @param obj
         */
        public void setNextActionType(ActionType nextActionType, int listPosition, String cartId, Object obj) {
            mNextActionType = nextActionType;
            mNextActionPostion = listPosition;
            mNextActionCartId = cartId;
            mNextActionObj = obj;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopCartListActivity shopCartListActivity = (ShopCartListActivity) mBaseActivityRef.get();
            nextAction(false);
            if (shopCartListActivity != null) {
                shopCartListActivity.requestDataDone(false);
                AppDebug.i(TAG, "QueryBagListener onError mQueryBagType=" + mQueryBagType + " resultCode=" + resultCode);
                if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA || mQueryBagType == QueryBagType.REQUEST_NEXT_DATA) {
                    if (resultCode == ServiceCode.API_NO_DATA.getCode()) {
                        shopCartListActivity.requestNewDataDone(true);
                        return true;
                    }
                }

                // 只有请求首次数据失败才会清空列表，其它的只给出提示框
                if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA) {
                    shopCartListActivity.showCartEmpty();
                }
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
            ShopCartListActivity shopCartListActivity = (ShopCartListActivity) mBaseActivityRef.get();
            if (shopCartListActivity != null) {
                shopCartListActivity.requestDataDone(true);
                AppDebug.i(TAG, "QueryBagListener onSuccess mQueryBagType=" + mQueryBagType + ", data = " + data);
                try {
                    CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).parseByStructure(JSON.parseObject(data));
                    // 新数据请求完成
                    if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA
                            || mQueryBagType == QueryBagType.REQUEST_NEXT_DATA) {
                        shopCartListActivity.requestNewDataDone(!shopCartListActivity.mCartBuilder.haveNextData());
                    }
                    if (mQueryBagType != QueryBagType.REQUEST_NEW_DATA) {
                        shopCartListActivity.mCartBuilder.reBuildView(deleteIdList);
                        deleteId = null;
                        //如果不是最后一页,且都是失效商品时,断续请求下一页
                        if (!CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).isEndPage()
                                && shopCartListActivity.mCartBuilder.getCartData().isEmpty()) {
                            shopCartListActivity.OnWaitProgressDialog(true);
                            shopCartListActivity.mCartBuilder.queryBagRequest(new QueryBagListener(
                                    new WeakReference<BaseActivity>(shopCartListActivity),
                                    QueryBagListener.QueryBagType.REQUEST_NEXT_DATA), shopCartListActivity.cartFrom);
                            return;
                        }
                        // 如果只是更新sku、数量、来至于购物车，则不更新购物车数量
                        if (mQueryBagType == QueryBagType.REQUEST_UPDATE_SKU
                                || mQueryBagType == QueryBagType.REQUEST_UPDATE_QUANTITY
                                || !TextUtils.isEmpty(shopCartListActivity.cartFrom)) {
                            shopCartListActivity.updateShopCartList(false);
                        } else {
                            shopCartListActivity.updateShopCartList(true);
                        }
                    } else {
                        boolean result = shopCartListActivity.mCartBuilder.buildView();
                        if (!result) {
                            shopCartListActivity.finish();
                        } else {
                            //如果不是最后一页,且都是失效商品时,断续请求下一页
                            if (!CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).isEndPage()
                                    && shopCartListActivity.mCartBuilder.getCartData().isEmpty()) {
                                shopCartListActivity.OnWaitProgressDialog(true);
                                shopCartListActivity.mCartBuilder
                                        .queryBagRequest(
                                                new QueryBagListener(new WeakReference<BaseActivity>(
                                                        shopCartListActivity),
                                                        QueryBagListener.QueryBagType.REQUEST_NEXT_DATA),
                                                shopCartListActivity.cartFrom);
                                return;
                            }
                            shopCartListActivity.updateShopCartList(true);
                        }
                    }
                    // 完成后处理后续的请求
                    nextAction(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 出错显示空购物车
                    shopCartListActivity.showCartEmpty();
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            // 如果是数据下次请求弹出错误提示框不做退出
            if (mQueryBagType == QueryBagType.REQUEST_NEW_DATA) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 处理下次请求（有时候处理需要依赖上次处理的结果，如果下次处理的数据为空，就不会进行下次处理）
         *
         * @param preRequestSuccess
         */
        private void nextAction(boolean preRequestSuccess) {
            ShopCartListActivity shopCartListActivity = (ShopCartListActivity) mBaseActivityRef.get();
            if (shopCartListActivity != null) {
                AppDebug.i(TAG, "nextAction mNextActionCartId=" + mNextActionCartId + " mNextActionPostion="
                        + mNextActionPostion + " mNextActionType=" + mNextActionType + " mNextActionCartId="
                        + mNextActionCartId + " mNextActionObj=" + mNextActionObj);
                if (preRequestSuccess && !TextUtils.isEmpty(mNextActionCartId)) {
                    if (mNextActionType.compareTo(ActionType.UPDATE_QUANTITY) == 0) {
                        shopCartListActivity.updateGoodsQuanitiy(mNextActionPostion, mNextActionCartId,
                                (Integer) mNextActionObj);
                    }

                }
            }
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
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return qrBitmap;
    }

    /**
     * 登录
     */
    @Override
    protected void refreshData() {
        initCartBudiler();
        initView();
        mFocusPositionManager.focusStop();
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
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return mFocusPositionManager;
    }
}
