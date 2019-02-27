/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.buildorder
 * FILE NAME: ViewBuilder.java
 * CREATED TIME: 2015-3-9
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.InputComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.LabelComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.MultiSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.MultiSelectGroup;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.TermsComponent;
import com.taobao.wireless.trade.mbuy.sdk.engine.BuyEngine;
import com.taobao.wireless.trade.mbuy.sdk.engine.ExpandParseRule;
import com.taobao.wireless.trade.mbuy.sdk.engine.LinkageAction;
import com.taobao.wireless.trade.mbuy.sdk.engine.LinkageDelegate;
import com.taobao.wireless.trade.mbuy.sdk.engine.LinkageNotification;
import com.taobao.wireless.trade.mbuy.sdk.engine.ValidateResult;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.pay.YunOSOrderManager;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.paysdk.AliTVPayClient;
import com.yunos.tv.paysdk.AliTVPayResult;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.dialog.QRDialog;
import com.yunos.tvtaobao.biz.dialog.QuitPayConfirmDialog;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderPreSale;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRebateBo;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.CreateOrderResult;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.payment.alipay.AlipayPaymentManager;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.AddressEditActivity;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;
import com.yunos.tvtaobao.tradelink.buildorder.OrderBuider;
import com.yunos.tvtaobao.tradelink.buildorder.PaidAdvertHandle;
import com.yunos.tvtaobao.tradelink.buildorder.PurchaseViewFactory;
import com.yunos.tvtaobao.tradelink.buildorder.PurchaseViewType;
import com.yunos.tvtaobao.tradelink.buildorder.adapter.BuildOrderAddressAdapter;
import com.yunos.tvtaobao.tradelink.buildorder.adapter.BuildOrderCartItemListAdapter;
import com.yunos.tvtaobao.tradelink.buildorder.adapter.ViewAdpter;
import com.yunos.tvtaobao.tradelink.buildorder.bean.GoodsDisplayInfo;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.bean.OrderDetailBo;
import com.yunos.tvtaobao.tradelink.buildorder.component.GoodsSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.InstallmentChoiceComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderBondSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.SyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;
import com.yunos.tvtaobao.tradelink.dialog.InstallMentPickerDialog;
import com.yunos.tvtaobao.tradelink.dialog.MultiOptionPickDialog;
import com.yunos.tvtaobao.tradelink.listener.QRCodeKeyListener;
import com.yunos.tvtaobao.tradelink.request.OrderDetailRequest;
import com.yunos.tvtaobao.tradelink.request.TvTaobaoBusinessRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ali.auth.third.core.context.KernelContext.getApplicationContext;

public class ViewBuilder {
    private final static String TAG = "ViewBuilder";
    private final static int ADDRESS_CHANGE_MSG = 1;
    private BuildOrderActivity mBuildOrderActivity;
    // 网络请求引擎
    private TvTaobaoBusinessRequest mTvTaobaoBusinessRequest;
    // 确认下单的引擎
    private BuyEngine mBuyEngine;
    // 网络请求
    private BusinessRequest mBusinessRequest;


    private boolean hasPresaleAddress = false;

    // 确认下单SDK的委托类
    private BuildorderLinkageDelegate mBuildorderLinkageDelegate;

    private Handler mHandler;

    private ViewAdpter viewAdapter;
    // 组件的ListView
    private BuildorderFocusListView purchaseListView;


    // 网络请求返回后的组件
    private List<Component> mComponents;

    //返利请求返回list
    private List<BuildOrderRebateBo> rebateBoList;

    private Map<String, List<SyntheticComponent>> orderComponents;
    private List<String> mSellers;

//    private TextView mGoodsTitle;
//    private TextView mGoodsSku;

    //cartItem list view
    private BuildOrderLeftFocusContainer cartlistFocusContainer;
    private ListView cartItemListView;
    private BuildOrderCartItemListAdapter mAdapter;

//    private ListView infoListview;
//    private BuildOrderGoodsAdapter mBuildOrderGoodsAdapter;

    private TextView totalPrice;
    private TextView tvRebate;
    private TextView tvRabateMassage;
    private ImageView ivRebate;
    private FocusLinearLayout submitBuildOrder;

    private BuildOrderGallery addressGallery;
    private BuildOrderAddressAdapter mBuildOrderAddressAdapter;
    private ArrayList<Address> mAddresslist;

    // 跳转到支付成功后弹出的广告页
    private PaidAdvertHandle mPaidAdvertHandle;

    // 数据处理类
    private OrderBuider mOrderBuider;

    private BuildOrderPreSale mBuildOrderPreSale;

    private boolean isFromCart;

    private List<String> itemIdList = new ArrayList<>();

    private BuildOrderRequestBo mBuildOrderRequestBo;

    public ViewBuilder(BuildOrderActivity buildOrderActivity) {
        mBuildOrderActivity = buildOrderActivity;
        mTvTaobaoBusinessRequest = TvTaobaoBusinessRequest.getBusinessRequest();
        mBuyEngine = new BuyEngine();
        mBuyEngine.registerExpandParseRule(new ExpandParseRule() {
            @Override
            public Component expandComponent(JSONObject data, BuyEngine engine) {
                String typeDesc = data.getString("type");
                String tagDesc = data.getString("tag");
                if (typeDesc == null || tagDesc == null) {
                    return null;
                }
                if (typeDesc.equals("biz") && tagDesc.equals("tvtaoFanliInfo")) {
                    Component component = new Component(data, engine) {
                    };
                    return component;
                }
                return null;
            }
        });
        mBuildorderLinkageDelegate = new BuildorderLinkageDelegate(new WeakReference<BuildOrderActivity>(
                mBuildOrderActivity));

        mOrderBuider = new OrderBuider(buildOrderActivity, mBuyEngine);

        // 初始化变量值
        initValue();
        // 初始化界面
        initView();
    }

    private int listWidth = 0;

    /**
     * 初始化view
     */
    private void initView() {

        isFromCart = mBuildOrderActivity.getIntent().getBooleanExtra(BaseConfig.INTENT_KEY_IS_FROM_CART_TO_BUILDORDER, false);
        purchaseListView = (BuildorderFocusListView) mBuildOrderActivity
                .findViewById(R.id.buildorder_buyorder_listview);
        cartlistFocusContainer = (BuildOrderLeftFocusContainer) mBuildOrderActivity.findViewById(R.id.ytm_buildorder_info_layout);

        purchaseListView.setOnTabKeyDownListener(new TabFocusListView.OnTabKeyDownListener() {

            @Override
            public boolean onTabKeyDown(View selectView, int selectPos, int keyCode, KeyEvent event) {
                if (selectView instanceof BuildOrderItemView) {
                    BuildOrderItemView view = (BuildOrderItemView) selectView;
                    OnKeyListener onKeyListener = view.getOnKeyListener();
                    if (onKeyListener != null) {
                        return onKeyListener.onKey(selectView, keyCode, event);
                    }
                }
                return false;
            }
        });

        purchaseListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Component component = viewAdapter.getComponents().get(position);
                if (component instanceof Vip88CardComponent) {
                    MultiOptionPickDialog dialog = new MultiOptionPickDialog<Vip88CardComponent>(mBuildOrderActivity);
                    dialog.setMultiOptionComponent((Vip88CardComponent) component);
                    dialog.setDelegate(new MultiOptionPickDialog.MultiOptionPickDialogDelegate() {
                        @Override
                        public void onConfirm(MultiOptionPickDialog dialog, MultiOptionComponent component) {
                            component.applyChanges();
                        }

                        @Override
                        public void onCancel(MultiOptionPickDialog dialog, MultiOptionComponent component) {
                            component.discardChanges();
                        }
                    });
                    dialog.show();
                    //TODO
                    Map<String, String> params = Utils.getProperties();
                    params.put("controltag", component.getTag());
                    params.put("controlname", ((Vip88CardComponent) component).getEntryTitle());
                    mBuildOrderActivity.utControlHit(component.getTag(), params);
                } else if (component instanceof InstallmentPickerComponent) {
                    InstallMentPickerDialog installmentDialog = new InstallMentPickerDialog(mBuildOrderActivity);
                    installmentDialog.setMultiOptionComponent(new InstallmentChoiceComponent((InstallmentPickerComponent) component));

                    installmentDialog.setDelegate(new MultiOptionPickDialog.MultiOptionPickDialogDelegate() {
                        @Override
                        public void onConfirm(MultiOptionPickDialog dialog, MultiOptionComponent component) {
                            component.applyChanges();
                        }

                        @Override
                        public void onCancel(MultiOptionPickDialog dialog, MultiOptionComponent component) {
                            component.discardChanges();
                        }
                    });
                    installmentDialog.show();
//                    Toast.makeText(mBuildOrderActivity, "not implemented yet", Toast.LENGTH_LONG).show();
                }
            }
        });

        purchaseListView.setNextFocusLeftId(cartlistFocusContainer.getId());

        purchaseListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                AppDebug.i(TAG, "handlerMask onScrollStateChanged --> scrollState = " + scrollState);
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    handlerMask(purchaseListView.getSelectedItemPosition(), true, false);
                }
            }

            @Override
            public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        // 设置布局状态监听类
        purchaseListView.setOnLayoutStateListener(new TabFocusListView.OnLayoutStateListener() {

            @Override
            public void layoutFinish(com.yunos.tv.app.widget.ListView fatherView) {
                handlerMask(purchaseListView.getSelectedItemPosition(), true, false);
            }
        });

        purchaseListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                changeArrowResId(!hasFocus);
                if (purchaseListView != null) {
                    purchaseListView.drawChildViewDivider();
                }
            }
        });

        cartItemListView = (ListView) mBuildOrderActivity.findViewById(R.id.cartitemlist);
        cartItemListView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
//        cartItemListView.setDivider(mBuildOrderActivity.getResources().getDrawable(R.drawable.hejifengexian));
        cartItemListView.setDivider(new ColorDrawable(Color.TRANSPARENT));

//        cartItemListView.setDividerHeight((int) (mBuildOrderActivity.getResources().getDisplayMetrics().density * 63));
        cartItemListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                AppDebug.d(TAG, "list width: " + view.getWidth());
                listWidth = cartItemListView.getWidth() - cartItemListView.getPaddingLeft() - cartItemListView.getPaddingRight();
                if (mAdapter != null)
                    mAdapter.setListWidth(listWidth);
            }
        });

        cartlistFocusContainer.setPreKeyDownListener(new BuildOrderLeftFocusContainer.PreKeyDownListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public boolean preOnKeyDown(BuildOrderLeftFocusContainer container, KeyEvent event) {
                AppDebug.d("test", "prekeydown:" + event.getKeyCode());
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
//                    int pos = cartItemListView.getFirstVisiblePosition();
//                    View child = cartItemListView.getChildAt(0);
//                    if (child.getTop() >= cartItemListView.getScrollY() - 15)//// FIXME: 9/29/16 15:为了应对listview上翻显示不完全情况
//                        pos--;
//                    if (pos < 0) pos = 0;
//                    cartItemListView.setSelection(pos);
                    cartItemListView.smoothScrollBy((int) (-cartItemListView.getHeight() * 0.8), 400);
//                    cartItemListView.smoothScrollToPositionFromTop(pos, 10);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//                    int pos = cartItemListView.getFirstVisiblePosition() + 1;
//                    if (pos > mAdapter.getCount() - 1) pos = mAdapter.getCount() - 1;
//                    cartItemListView.setSelection(pos);
                    cartItemListView.smoothScrollBy((int) (cartItemListView.getHeight() * 0.8), 400);
//                    cartItemListView.smoothScrollToPositionFromTop(pos, 10);
                    return true;
                }
                return false;
            }

        });
        cartlistFocusContainer.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
            }
        });

        purchaseListView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                if (v != null) v.setSelected(isSelected);
                if (purchaseListView != null) {
                    purchaseListView.drawChildViewDivider();
                }
            }
        });

        purchaseListView.setFlipScrollMaxStep(100);
        purchaseListView.setFlipScrollFrameCount(5);
        purchaseListView.setFocusBackground(true);


//        mGoodsTitle = (TextView) mBuildOrderActivity.findViewById(R.id.buildorder_title);
//        mGoodsSku = (TextView) mBuildOrderActivity.findViewById(R.id.buildorder_sku);
//        infoListview = (ListView) mBuildOrderActivity.findViewById(R.id.buildorder_info_left_listview);

        totalPrice = (TextView) mBuildOrderActivity.findViewById(R.id.buildorder_total_price);
        submitBuildOrder = (FocusLinearLayout) mBuildOrderActivity.findViewById(R.id.buildorder_button_wrapper);
        addressGallery = (BuildOrderGallery) mBuildOrderActivity.findViewById(R.id.buildorder_address_gallery);

        tvRebate = (TextView) mBuildOrderActivity.findViewById(R.id.tv_buildorder_rebate);
        tvRabateMassage = (TextView) mBuildOrderActivity.findViewById(R.id.tv_buildorder_rebate_message);
        ivRebate = (ImageView) mBuildOrderActivity.findViewById(R.id.iv_buildorder_rebate);


        handlerClickSubmit();

        onInitAddressGallery();

        setSkuViewGlobalLayoutListener();

        submitBuildOrder.requestFocus();
    }

    /**
     * 处理右侧组件列表的上下蒙版
     *
     * @param position
     * @param isSelected
     */
    private void handlerMask(int position, boolean isSelected, boolean firstlayout) {
        if (mBuildOrderActivity != null) {
            View topView = mBuildOrderActivity.findViewById(R.id.ytm_buildorder_mask_up);
            View bottomView = mBuildOrderActivity.findViewById(R.id.ytm_buildorder_mask_down);
            View firstVisibleView = null;
            View lastVisibleView = null;
            int listView_top = 0;
            int listView_bottom = 0;

            if (purchaseListView != null) {
                firstVisibleView = purchaseListView.getFirstVisibleChild();
                lastVisibleView = purchaseListView.getLastVisibleChild();
                listView_top = purchaseListView.getTop();
                listView_bottom = purchaseListView.getBottom();
            }

            AppDebug.i(TAG, "handlerMask -->  firstlayout = " + firstlayout + ";listView_top =" + listView_top
                    + "; listView_bottom =" + listView_bottom + "; firstVisibleView = " + firstVisibleView
                    + "; lastVisibleView = " + lastVisibleView);

            if (firstVisibleView != null && firstVisibleView.getTop() < listView_top) {
                topView.setVisibility(View.VISIBLE);
            } else {
                topView.setVisibility(View.GONE);
            }

            if (lastVisibleView != null && lastVisibleView.getBottom() > listView_bottom) {
                bottomView.setVisibility(View.VISIBLE);
            } else {
                bottomView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 改变Selector
     *
     * @param resId
     */
    private void changeSelector(int resId) {
        FocusPositionManager focusPositionManager = (FocusPositionManager) mBuildOrderActivity
                .findViewById(R.id.buildorder_mainlayout);
        focusPositionManager
                .setSelector(new StaticFocusDrawable(mBuildOrderActivity.getResources().getDrawable(resId)));
    }

    /**
     * 改变地址栏的箭头
     *
     * @param hasFocus
     */
    public void changeArrowResId(boolean hasFocus) {
        AppDebug.i(TAG, "changeArrowResId --> hasFocus = " + hasFocus + "; mBuildOrderActivity = "
                + mBuildOrderActivity);
        if (mBuildOrderActivity != null) {
            ImageView rightarrowView = (ImageView) mBuildOrderActivity.findViewById(R.id.addres_right_arrow);
            ImageView leftarrowView = (ImageView) mBuildOrderActivity.findViewById(R.id.addres_left_arrow);
            if (leftarrowView == null || rightarrowView == null)
                return;
            int rightarrowResId = R.drawable.ytm_buildorder_address_right_hover;
            int leftarrowResId = R.drawable.ytm_buildorder_address_left_hover;
            if (!hasFocus) {
                rightarrowResId = R.drawable.ytm_buildorder_address_right_normal;
                leftarrowResId = R.drawable.ytm_buildorder_address_left_normal;
            }
            if (rightarrowView != null) {
                rightarrowView.setBackgroundResource(rightarrowResId);
            }
            if (leftarrowView != null) {
                leftarrowView.setBackgroundResource(leftarrowResId);
            }
            leftarrowView.setVisibility(addressGallery.getSelectedItemPosition() == 0 ? View.INVISIBLE : View.VISIBLE);
            AppDebug.i(TAG, "changeArrowResId --> rightarrowView = " + rightarrowView + "; leftarrowView = "
                    + leftarrowView + "; rightarrowResId = " + rightarrowResId + "; leftarrowResId = " + leftarrowResId);
        }
    }

    /**
     * 对mGoodsSku的内容进行裁剪
     */
    private void setSkuViewGlobalLayoutListener() {
        AppDebug.w(TAG, "setSkuViewGlobalLayoutListener not used!!");
//        mGoodsSku.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//
//                if (mOrderBuider == null) {
//                    return;
//                }
//
//                if (mOrderBuider.onlyOneItem) {
//                    return;
//                }
//
//                GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.getGoodsDisplayInfo();
//                if (mGoodsDisplayInfo == null) {
//                    return;
//                }
//
//                // 只能是多个商品
//                SparseArray<String> showgoods = mGoodsDisplayInfo.getInvalidGoods();
//                int size = 0;
//                int textColor = mBuildOrderActivity.getResources().getColor(R.color.ytm_buildorder_invalid);
//                if (showgoods != null) {
//                    size = showgoods.size();
//                }
//
//                if (size <= 0) {
//                    // 如果没有无效的商品宝贝，那么显示有效的商品宝贝
//                    showgoods = mGoodsDisplayInfo.getValidGoods();
//                    textColor = mBuildOrderActivity.getResources().getColor(R.color.ytm_buildorder_sku);
//                }
//
//                if (showgoods != null) {
//                    size = showgoods.size();
//                }
//
//                AppDebug.i(TAG, "setSkuViewGlobalLayoutListener --> size = " + size + "; showgoods = " + showgoods);
//
//                if (mGoodsSku.getWidth() > 0 && size > 0) {
//                    TextPaint paint = mGoodsSku.getPaint();
//                    int paddingLeft = mGoodsSku.getPaddingLeft();
//                    int paddingRight = mGoodsSku.getPaddingRight();
//                    int bufferWidth = (int) paint.getTextSize() * 1;
//                    int availableTextWidth = (mGoodsSku.getWidth() - paddingLeft - paddingRight) - bufferWidth;
//                    StringBuilder invalid = new StringBuilder();
//                    invalid.setLength(0);
//                    for (int index = 0; index < size; index++) {
//                        String ellipsizeStr = (String) TextUtils.ellipsize(showgoods.get(index), (TextPaint) paint,
//                                availableTextWidth, TextUtils.TruncateAt.END);
//                        invalid.append(ellipsizeStr);
//                        invalid.append('\n'); // 添加换行符
//                    }
//                    mGoodsSku.setText(invalid.toString());
//                    mGoodsSku.setTextColor(textColor);
//                }
//            }
//        });
    }

    /**
     * 初始化参数变量值
     */
    private void initValue() {
        onInitAddress();

        mHandler = new BuildorderHandler(new WeakReference<BuildOrderActivity>(mBuildOrderActivity));
    }

    /**
     * 初始化地址相关的变量
     */
    public void onInitAddress() {
        mAddresslist = new ArrayList<Address>();
        mAddresslist.clear();

        // 添加一个空的数据结构，用来表示编辑地址
        Address edit = new Address();
        mAddresslist.add(edit);

        mBuildOrderAddressAdapter = new BuildOrderAddressAdapter(mBuildOrderActivity, mAddresslist);
    }

    private String itemid;

    /**
     * 获取订单信息
     *
     * @param mBuildOrderRequestBo
     */
    public void buildOrderRequest(BuildOrderRequestBo mBuildOrderRequestBo, BuildOrderPreSale buildOrderPreSale) {
        this.mBuildOrderRequestBo = mBuildOrderRequestBo;
        itemid = mBuildOrderRequestBo.getItemId();
        this.mBuildOrderPreSale = buildOrderPreSale;
        mBuildOrderActivity.OnWaitProgressDialog(true);
        mBuildOrderRequestBo.setPreSell(buildOrderPreSale != null);//TODO 购物车带入的标是否要加上 目前逻辑不加
        if (mBuildOrderRequestBo != null)
            AppDebug.e(TAG, "mBuildOrderRequestBo = " + mBuildOrderRequestBo.toString());
        mTvTaobaoBusinessRequest.buildOrder(mBuildOrderRequestBo, isFromCart, new BuildOrderListener(
                new WeakReference<BaseActivity>(mBuildOrderActivity)));
//        getResponCode(mBuildOrderRequestBo);
    }


//    public void getResponCode(BuildOrderRequestBo mBuildOrderRequestBo) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        String url = "https://acs.m.taobao.com/gw/mtop.trade.buildOrder/3.0/?data="+getParams(getAppData(mBuildOrderRequestBo));
//        final Request request = new Request.Builder().url(url).build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                AppDebug.e("response.code",  response.code()+"");
//            }
//        });
//    }

//    protected Map<String, String> getAppData(BuildOrderRequestBo mBuildOrderRequestBo) {
//        Map<String, String> obj = new HashMap<String, String>();
//        if (mBuildOrderRequestBo != null) {
//            if (!TextUtils.isEmpty(mBuildOrderRequestBo.getDeliveryId())) {
//                obj.put("deliveryId", mBuildOrderRequestBo.getDeliveryId());
//            }
//
//            if (!TextUtils.isEmpty(mBuildOrderRequestBo.getCartIds())) {
//                obj.put("cartIds", mBuildOrderRequestBo.getCartIds());
//            }
//
//            if (mBuildOrderRequestBo.isSettlementAlone()) {
//                obj.put("isSettlementAlone", "true");
//                obj.put("buyParam", mBuildOrderRequestBo.getBuyParam());
//            } else if (!TextUtils.isEmpty(mBuildOrderRequestBo.getCartIds())) {
//                obj.put("cartIds", mBuildOrderRequestBo.getCartIds());
//            }
//
//            String s = mBuildOrderRequestBo.getExtParams();
//            Log.i(TAG, "1 exParams : " + s);
//            if (TextUtils.isEmpty(s)) {
//                org.json.JSONObject job = new org.json.JSONObject();
//                try {
//                    job.put("coVersion", "2.0");
//                    job.put("coupon", "true");
//                    org.json.JSONObject tvtaoEx = new org.json.JSONObject();
//                    String appkey = SharePreferences.getString("device_appkey", "");
//                    String brandName = SharePreferences.getString("device_brandname", "");
//                    if (appkey.equals("10004416") && brandName.equals("海尔")) {
//                        tvtaoEx.put("appKey", "2017092310");
//                    } else {
//                        tvtaoEx.put("appKey", Config.getChannel());
//                    }
//                    tvtaoEx.put("deviceId", DeviceUtil.getStbID());
//                    tvtaoEx.put("cartFlag", "cart".equals(mBuildOrderRequestBo.getFrom()) ? "1" : "0");
//                    job.put("TvTaoEx", tvtaoEx);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                s = job.toString();
//            } else {
//                try {
//                    org.json.JSONObject jobj = new org.json.JSONObject(s);
//                    jobj.put("coVersion", "2.0");
//                    jobj.put("coupon", "true");
//                    org.json.JSONObject TvTaoEx;
//                    if (jobj.has("TvTaoEx")) {
//                        TvTaoEx = jobj.getJSONObject("TvTaoEx");
//                    } else {
//                        TvTaoEx = new org.json.JSONObject();
//                    }
//                    String appkey = SharePreferences.getString("device_appkey", "");
//                    String brandName = SharePreferences.getString("device_brandname", "");
//                    if (appkey.equals("10004416") && brandName.equals("海尔")) {
//                        TvTaoEx.put("appKey", "2017092310");
//                    } else {
////            addParams("appKey", "2017092310");
//                        TvTaoEx.put("appKey", Config.getChannel());
//                    }
//                    TvTaoEx.put("deviceId", DeviceUtil.getStbID());
//                    TvTaoEx.put("cartFlag", mBuildOrderRequestBo.getFrom().equals("cart") ? "1" : "0");
//                    if (!jobj.has("TvTaoEx")) {
//                        jobj.put("TvTaoEx", TvTaoEx);
//                    }
//                    s = jobj.toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            Log.w(TAG, "2 exParams : " + s);
//            obj.put("exParams", s);//mBuildOrderRequestBo.getExtParams());
//
//            obj.put("buyNow", String.valueOf(mBuildOrderRequestBo.isBuyNow()));
//
//            if (mBuildOrderRequestBo.isBuyNow()) {
//                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getItemId())) {
//                    obj.put("itemId", mBuildOrderRequestBo.getItemId());
//                }
//
//                obj.put("quantity", String.valueOf(mBuildOrderRequestBo.getQuantity()));
//
//                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getSkuId())) {
//                    obj.put("skuId", mBuildOrderRequestBo.getSkuId());
//                }
//
//                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getServiceId())) {
//                    obj.put("serviceId", mBuildOrderRequestBo.getServiceId());
//                }
//
//                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getActivityId())) {
//                    obj.put("activityId", mBuildOrderRequestBo.getActivityId());
//                }
//
//                if (!TextUtils.isEmpty(mBuildOrderRequestBo.getTgKey())) {
//                    obj.put("tgKey", mBuildOrderRequestBo.getTgKey());
//                }
//            }
//        }
//
//        return obj;
//    }
//
//
//    public String getParams(Map<String,String> dataParams){
//        StringBuilder sb = new StringBuilder();
//        int i = 0;
//        int len = dataParams.size();
//        for (Map.Entry<String, String> entry : dataParams.entrySet()) {
//            sb.append(entry.getKey()).append("=")
//                    .append(DataEncoder.encodeUrl(DataEncoder.decodeUrl(entry.getValue())));
//            if (i < (len - 1)) {
//                sb.append("&");
//            }
//        }
//        return sb.toString();
//    }


    /**
     * 获取地址信息
     */
    private void requestAddress() {
        mBuildOrderActivity.OnWaitProgressDialog(true);
        mTvTaobaoBusinessRequest.requestGetAddressList(new AddressListBusinessRequestListener(
                new WeakReference<BaseActivity>(mBuildOrderActivity)));
    }

    /**
     * 设置确认下单按键的监听
     */
    private void handlerClickSubmit() {
        submitBuildOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppDebug.v(TAG, TAG + ".handlerClickSubmit.onClick.v = " + v);
                mBuildOrderActivity.utControlHit("Immediateorder", Utils.getProperties());
                gotoSubmitBuildOrder();
            }
        });

        submitBuildOrder.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.v(TAG, TAG + ".onFocusChange.v = " + v + ".hasFocus = " + hasFocus);
                onChangeSubmitButton(hasFocus);
                changeArrowResId(!hasFocus);
            }
        });

    }

    public boolean gotoSubmitBuildOrder() {
        AppDebug.v(TAG, TAG + ".gotoSubmitBuildOrder");
        if (mOrderBuider.submitOrderOk) {
            if (addressGallery != null) {
                View selectView = addressGallery.getSelectedView();
                if (selectView != null && selectView instanceof AddressEditRelativeLayout) {
                    // 如果当前选中的是添加或编辑地址，那么不能下单
                    String address_select = mBuildOrderActivity.getResources().getString(
                            R.string.ytm_buildorder_select_address);
                    mBuildOrderActivity.showErrorDialog(address_select, false);
                    return true;
                }
            }

            if (!validateComponent()) {
                // 检查一下，是不是能下单
                AppDebug.v(TAG, TAG + ".gotoSubmitBuildOrder.cannot submit build order!");
                return true;
            }

            String controlName = Utils.getControlName(mBuildOrderActivity.getFullPageName(), "SubmitOnClick", null);
            Map<String, String> pro = Utils.getProperties();
            GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.mGoodsDisplayInfo;
            if (mGoodsDisplayInfo != null) {
                if (!TextUtils.isEmpty(mGoodsDisplayInfo.getmItemIdTbs())) {
                    pro.put("item_id", mGoodsDisplayInfo.getmItemIdTbs());
                }
                if (!TextUtils.isEmpty(mGoodsDisplayInfo.getTitle())) {
                    pro.put("name", mGoodsDisplayInfo.getTitle());
                }
            }

            TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(pro));

            //下单
            String params = mBuyEngine.generateFinalSubmitDataWithZip();
            mBuildOrderActivity.onTextProgressDialog(" 提交订单支付中...", true);
            mTvTaobaoBusinessRequest.createOrder(params, new CreateOrderBusinessRequestListener(
                    new WeakReference<BaseActivity>(mBuildOrderActivity)));

        } else {
            String no_order = mBuildOrderActivity.getResources().getString(R.string.ytm_buildorder_no_order);
            mBuildOrderActivity.showErrorDialog(no_order, false);
        }

        return true;
    }

    /**
     * 检查输入框组件的内容信息；如果输入框中有mobileNO或者contacts信息，那么用默认的手机号填入[针对首次]
     */

    private void changedInputComponentContent() {
        String defalutContants = "";
        if (mAddresslist != null && mAddresslist.get(0) != null) {
            // 获取默认地址的手机号码
            defalutContants = mAddresslist.get(0).getMobile();
        }
        AppDebug.i(TAG, "changedInputComponentContent --> defalutContants = " + defalutContants);
        if (viewAdapter != null && !TextUtils.isEmpty(defalutContants)) {
            List<Component> components = viewAdapter.getComponents();
            boolean changed = false;
            if (components == null) {
                return;
            }
            for (Component component : components) {
                int index = PurchaseViewFactory.getItemViewType(component);
                if (PurchaseViewType.INPUT.getIndex() == index) {
                    InputComponent inputComponent = (InputComponent) component;
                    if (inputComponent == null) {
                        continue;
                    }
                    JSONObject data = inputComponent.getFields();
                    AppDebug.i(TAG, "changedInputComponentContent --> data = " + data);
                    if (data != null && (data.containsKey("bizName") || data.containsKey("plugin"))) {
                        String plugin = data.getString("plugin");
                        String bizName = data.getString("bizName");
                        if (TextUtils.equals("contacts", plugin) || TextUtils.equals("mobileNO", bizName)) {
                            inputComponent.setValue(defalutContants);
                            changed = true;
                        }
                    }

                }
            }

            if (changed) {
                viewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 下单之前，检查各组件是否都满足条件，如果不满足条件，那么弹出对话框提示
     *
     * @return
     */
    private boolean validateComponent() {
        AppDebug.v(TAG, TAG + ".validateComponent");
        if (viewAdapter != null && mBuildOrderActivity != null) {
            List<Component> components = viewAdapter.getComponents();
            if (components != null) {
                for (Component component : components) {
                    if (component != null) {
                        ValidateResult result = component.validate();
                        AppDebug.i(
                                TAG,
                                "checkSubmitOrder --> isValid = " + result.isValid() + "; Msg = "
                                        + result.getErrorMsg() + "; component = " + component);
                        if (result != null && !result.isValid()) {
                            mBuildOrderActivity.showErrorDialog(result.getErrorMsg(), false);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 改变确认下单按钮的状态
     *
     * @param hasFocus
     */
    public void onChangeSubmitButton(boolean hasFocus) {
        if (hasFocus) {
            int textColor = mBuildOrderActivity.getResources().getColor(R.color.ytm_white);
            ((BuildOrderTextView) submitBuildOrder.findViewById(R.id.buildorder_button)).setTextColor(textColor);
            ((TextView) submitBuildOrder.findViewById(R.id.ytm_buildorder_ok_hint)).setTextColor(textColor);
            ((TextView) mBuildOrderActivity.findViewById(R.id.rebate_money)).setTextColor(textColor);
            ((TextView) mBuildOrderActivity.findViewById(R.id.rebate_money)).setTextColor(mBuildOrderActivity.getResources().getColor(R.color.ytm_create_order_rebate_focuse));
            submitBuildOrder.requestFocus();
        } else {
            int textColor = mBuildOrderActivity.getResources().getColor(R.color.ytm_buildorder_ok_color);
            ((BuildOrderTextView) submitBuildOrder.findViewById(R.id.buildorder_button)).setTextColor(textColor);
            ((TextView) submitBuildOrder.findViewById(R.id.ytm_buildorder_ok_hint)).setTextColor(textColor);
            ((TextView) mBuildOrderActivity.findViewById(R.id.rebate_money)).setTextColor(mBuildOrderActivity.getResources().getColor(R.color.ytm_create_order_rebate_unfocuse));

        }
    }

    /**
     * 进入收货地址界面
     */
    private void enterAddressActivity() {
        Intent intent = new Intent();
        intent.setClass(mBuildOrderActivity, AddressEditActivity.class);
        mBuildOrderActivity.startActivity(intent);
    }

    /**
     * 进入广告推荐页
     */
    private void enterPaidAdvertActivity() {
        if (mPaidAdvertHandle != null) {
            mPaidAdvertHandle.enterPaidAdvertActivity(mBuildOrderActivity);
        }
    }

    /**
     * 初始化地址界面
     */
    private void onInitAddressGallery() {
        addressGallery.setFlingScrollFrameCount(5);
        addressGallery.setFlingScrollMaxStep(100);
        addressGallery.setLoopDraw(false);

        // 设置单击
        addressGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int size = mBuildOrderAddressAdapter.getAddressesSize();
                int realPos = mBuildOrderAddressAdapter.getRealPosition(position);
                AppDebug.i(TAG, "onInitAddressGallery --> setOnItemClickListener -> position = " + position
                        + ";  size = " + size + "; realPos = " + realPos);
                if (realPos == size - 1) {
                    enterAddressActivity();
                    return;
                }
            }
        });

        // 选择监听
        addressGallery.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                AppDebug.i(TAG, "onSetAddressComponentSelectedId -- onItemSelected --> position = " + position
                        + "; isSelected = " + isSelected);
                if (mOrderBuider.mAddressComponent != null && mAddresslist != null && isSelected) {
                    sendAddressChangeMessage(v, position);
                }
                ImageView leftarrowView = (ImageView) mBuildOrderActivity.findViewById(R.id.addres_left_arrow);
                leftarrowView.setVisibility(addressGallery.getSelectedItemPosition() == 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });

        addressGallery.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                AppDebug.i(TAG, "onSetAddressComponentSelectedId -- onScrollStateChanged --> scrollState = "
                        + scrollState);
                if (OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                    if (mOrderBuider.mAddressComponent != null && mAddresslist != null) {
                        View v = addressGallery.getSelectedView();
                        int position = addressGallery.getSelectedItemPosition();
                        ImageView leftarrowView = (ImageView) mBuildOrderActivity.findViewById(R.id.addres_left_arrow);
                        leftarrowView.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
                        sendAddressChangeMessage(v, position);
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.removeMessages(ADDRESS_CHANGE_MSG);
                        mHandler.removeCallbacksAndMessages(null);
                        AppDebug.i(TAG, "removeMessages -- onScrollStateChanged --> scrollState = " + scrollState);
                    }
                }
            }

            @Override
            public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        addressGallery.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeArrowResId(hasFocus);
            }
        });
    }

    public void sendAddressChangeMessage(View v, int position) {
        if (addressGallery != null) {
            // 加这个方法的目的是：避免出现显示不全；
            if (v != null) {
                v.forceLayout();
                addressGallery.requestLayout();
            }
        }
        if (mHandler != null) {
            mHandler.removeMessages(ADDRESS_CHANGE_MSG);
            Message msg = mHandler.obtainMessage(ADDRESS_CHANGE_MSG, position, 0);
            mHandler.sendMessageDelayed(msg, 1000);
            AppDebug.i(TAG, "sendAddressChangeMessage --> v = " + v + "; position = " + position);
        }
    }

    /**
     * 设置引擎中的地址
     *
     * @param position
     */
    public void onSetAddressComponentSelectedId(int position) {

        String optionAddrStr = mOrderBuider.getComponentSelectedAddress();
        if (TextUtils.isEmpty(optionAddrStr)) {
            return;
        }

        // 获取实际的POS值
        int realPos = mBuildOrderAddressAdapter.getRealPosition(position);
        AppDebug.i(TAG, "onSetAddressComponentSelectedId --> realPos = " + realPos + "; position = " + position);

        if (mAddresslist == null || mAddresslist.isEmpty() || realPos < 0) {
            return;
        }
        int size = mAddresslist.size();
        if (realPos >= size) {
            return;
        }

        Address address = mAddresslist.get(realPos);
        if (address == null) {
            return;
        }

        String addressId = address.getDeliverId();
        if (optionAddrStr.equals(addressId)) {
            // 如果相等，那么不重新请求数据
            return;
        }
        if (mOrderBuider.mAddressComponent != null) {
            mOrderBuider.mAddressComponent.setSelectedId(addressId);
        }

        // 设置埋点

        TBS.Adv.ctrlClicked(CT.Button, "address_edit", "address_id=" + addressId, "uuid=" + CloudUUIDWrapper.getCloudUUID(),
                "from_channel=" + mBuildOrderActivity.getmFrom());
    }

    /**
     * 创建布局
     *
     * @return
     */
    private boolean buildView() {
        if (mComponents == null) {
            return false;
        }
//        if (TextUtils.isEmpty(json)) {
//            return false;
//        }
//        JSONObject jobj = JSON.parseObject(json);
//        AppDebug.d(TAG, "refresh flag:" + jobj.getBooleanValue("reload"));
//        mComponents = mBuyEngine.parse(jobj);
        if (mBuildOrderPreSale != null) {
            AppDebug.e(TAG, "预售商品----》");
            for (Component component : mComponents) {
                //通知手机号
                if (component instanceof InputComponent) {
                    AppDebug.e(TAG, "component = " + component.getType() + " " + component.getTag());
                    if (component.getType().equals(ComponentType.INPUT) && component.getTag().equals("remain")) {
                        InputComponent inputComponent = (InputComponent) component;
                        AppDebug.e(TAG, "InputComponent.name = " + inputComponent.getName() + " value = " + inputComponent.getValue());
                        mBuildOrderPreSale.setNotifyTitle(inputComponent.getName());
                        mBuildOrderPreSale.setNotifyValue(inputComponent.getValue());
                    }
                }

                //定金翻倍提示
                if (component instanceof LabelComponent) {
                    LabelComponent labelComponent = (LabelComponent) component;
                    if (labelComponent.getType().equals(ComponentType.LABEL) && labelComponent.getTag().equals("promotion")) {
                        String promotion = labelComponent.getValue();
                        if (!TextUtils.isEmpty(promotion)) {
                            mBuildOrderPreSale.setPromotion(promotion);
                        }

                    }
                }
                if (component instanceof GoodsSyntheticComponent) {

                    GoodsSyntheticComponent goodsSyntheticComponent = (GoodsSyntheticComponent) component;
                    //发货时间
                    ItemInfoComponent itemInfoComponent = goodsSyntheticComponent.getItemInfoComponent();
                    if (itemInfoComponent.getTag().equals("itemInfo") && itemInfoComponent.getType().equals(ComponentType.BIZ)) {
                        if (itemInfoComponent != null && itemInfoComponent.getSkuLevelInfos() != null && itemInfoComponent.getSkuLevelInfos().get(0) != null) {
                            String deliverTitle = itemInfoComponent.getSkuLevelInfos().get(0).getName();
                            String deliverValue = itemInfoComponent.getSkuLevelInfos().get(0).getValue();
                            if (!TextUtils.isEmpty(deliverTitle)) {
                                mBuildOrderPreSale.setDeliverTitle(deliverTitle);
                            }
                            if (!TextUtils.isEmpty(deliverValue)) {
                                mBuildOrderPreSale.setDeliverValue(deliverValue);
                            }

                        }
                    }
                    //定金,尾款
                    ItemPayComponent itemPayComponent = goodsSyntheticComponent.getItemPayComponent();
                    if (itemPayComponent.getType().equals(ComponentType.BIZ) && itemPayComponent.getTag().equals("itemPay")) {
                        JSONObject jsonObject = itemPayComponent.getFields();
                        if (jsonObject != null) {
                            String prePay = jsonObject.getString("prePay");
                            if (!TextUtils.isEmpty(prePay) && !prePay.equals("null")) {
                                mBuildOrderPreSale.setPriceText(prePay.toString());
                                mBuildOrderPreSale.setPriceTitle("定金");
                            }
                            String price = jsonObject.getString("finalPay");
                            if (!TextUtils.isEmpty(price) && !price.equals("null")) {
                                mBuildOrderPreSale.setPresale(price);
                                mBuildOrderPreSale.setPresaleTitle("尾款");
                            }
                        }

                    }

                }

            }
        } else {
            AppDebug.e(TAG, "非预售商品--->");
        }
        //淘客打点
        taokePoint();
        AppDebug.e(TAG, "非预售商品--->");
//        for (Component component : mComponents) {
//            AppDebug.d(TAG, "component type:" + component.getType() + ", tag:" + component.getTag() + ", object:" + component);
//            AppDebug.d(TAG, "component data:" + component.getData());
//
//        }
        orderComponents = new HashMap<String, List<SyntheticComponent>>();
        mSellers = new ArrayList<String>();
        for (Component component : mComponents) {
            if ("preSellAddress".equals(component.getTag())) {
                hasPresaleAddress = true;
            }
            AppDebug.d(TAG, "synthetic component:" + component);
            if (component instanceof OrderSyntheticComponent) {
                OrderSyntheticComponent comp = (OrderSyntheticComponent) component;
                if (comp.getOrderInfoComponent() == null) continue;
                AppDebug.d(TAG, "synthetic order component:" + comp.getOrderComponent().getId());
                if (comp.getOrderComponent() != null && comp.getOrderComponent().getFields().getBoolean("valid")) {
                    String sellerid = "unknown";
                    if (comp.getOrderInfoComponent() != null)
                        sellerid = comp.getOrderInfoComponent().getFields().getString("uid");
                    List<SyntheticComponent> orders = orderComponents.get(sellerid);
                    if (orders == null) {
                        orders = new ArrayList<SyntheticComponent>();
                        mSellers.add(sellerid);
                    }
                    orders.add(comp);
                    orderComponents.put(sellerid, orders);
                } else {
                    String sellerid = comp.getOrderComponent().getId();
                    List<SyntheticComponent> orders = orderComponents.get(sellerid);
                    if (orders == null) {
                        orders = new ArrayList<SyntheticComponent>();
                        mSellers.add(sellerid);
                    }
                    orders.add(comp);
                    orderComponents.put(sellerid, orders);
                }
            } else {
                if (component instanceof OrderBondSyntheticComponent) {
                    OrderBondSyntheticComponent comp = (OrderBondSyntheticComponent) component;
                    AppDebug.d(TAG, "synthetic bond component:" + comp.orderPayComponent.getId());
                    String sellerid = "unknown";
                    if (comp.orderInfoComponent != null)
                        sellerid = comp.orderInfoComponent.getFields().getString("uid");
                    for (OrderSyntheticComponent orderSyntheticComponent : comp.orders) {
                        if (orderSyntheticComponent.getOrderComponent() != null && orderSyntheticComponent.getOrderComponent().getFields().getBoolean("valid")) {
                            List<SyntheticComponent> orders = orderComponents.get(sellerid);
                            if (orders == null) {
                                orders = new ArrayList<SyntheticComponent>();
                                mSellers.add(sellerid);
                            }
                            orders.add(comp);
                            orderComponents.put(sellerid, orders);
                            break;
                        }
//                        else {
//                            sellerid = orderSyntheticComponent.getOrderComponent().getId();
//                            List<SyntheticComponent> orders = orderComponents.get(sellerid);
//                            if (orders == null) {
//                                orders = new ArrayList<SyntheticComponent>();
//                                mSellers.add(sellerid);
//                            }
//                            orders.add(comp);
//                            orderComponents.put(sellerid, orders);
//                        }
                    }
                }
            }
            //预售条款
            if (component instanceof TermsComponent) {
                if (component.getType().equals(ComponentType.BIZ) && component.getTag().equals("terms")) {
                    TermsComponent termsComponent = (TermsComponent) component;
                    termsComponent.setAgree(true);
                }
            }

        }

        if (mComponents == null || mComponents.isEmpty()) {
            return false;
        }

        initListView(mComponents);
        upCommonContent();

        if (mAddresslist == null || (mAddresslist != null && mAddresslist.size() <= 1)) {
            // 请求地址
            requestAddress();
        }

        mBuyEngine.setLinkageDelegate(mBuildorderLinkageDelegate);

        //处理0元送货上门安装服务TODO: display
        for (Component component : mComponents) {
            if (component.getType() == ComponentType.MULTISELECT && component.getTag().equals("service")) {
                MultiSelectComponent mSelComponent = (MultiSelectComponent) component;
                if (mSelComponent.getSelectedIds().size() == 0)
                    for (MultiSelectGroup group : mSelComponent.getGroups()) {
                        for (SelectOption option : group.getOptions()) {
                            if ("送货入户并安装".equals(option.getName())) {
                                mSelComponent.setSelectedId(option.getId());
                                mSelComponent.notifyLinkageDelegate();
                                break;
                            }
                        }
                    }
            }
        }

        // 如果没有店铺优惠就提示
        if (!mOrderBuider.isHasShopPromotion && mBuildOrderActivity != null) {
            String mFrom = mBuildOrderActivity.getmFrom();
            if (mFrom != null && mFrom.toLowerCase().contains("tvhongbao")) {
                Toast.makeText(mBuildOrderActivity, "本次交易无法使用代金券", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    //淘客打点
    public void taokePoint() {
        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin()) {
            //淘客登录打点
            long historyTime = SharedPreferencesUtils.getTaoKeLogin(mBuildOrderActivity);
            long currentTime = System.currentTimeMillis();
            AppDebug.d(TAG, "TaokeLoginAnalysis  currentTime =  " + currentTime + "historyTime = " + historyTime);
            if (currentTime >= historyTime) {
                mBusinessRequest.requestTaokeLoginAnalysis(User.getNick(), new TaokeLoginBussinessRequestListener(new WeakReference<BaseActivity>(mBuildOrderActivity)));
            }

            //商品打点
            //下单商品id的list
            List<String> itemIdList = new ArrayList<>();
            for (Component component : mComponents) {
                //OrderSyntheticComponent
                if (component instanceof OrderSyntheticComponent) {
                    OrderSyntheticComponent orderSyntheticComponent = (OrderSyntheticComponent) component;
                    List<GoodsSyntheticComponent> goodsSyntheticComponentList = orderSyntheticComponent.getGoodsSyntheticComponents();
                    OrderComponent orderComponent = orderSyntheticComponent.getOrderComponent();
                    String sellerId = orderComponent.getFields().getString("sellerId");
                    AppDebug.d(TAG, "taoke sellerId = " + sellerId);
                    for (GoodsSyntheticComponent goodsSyntheticComponent : goodsSyntheticComponentList) {
                        String itemId = goodsSyntheticComponent.getItemComponent().getItemId();
                        AppDebug.d(TAG, "taoke itemId = " + sellerId);
                        itemIdList.add(itemId);
                    }

                }
                //OrderBondSyntheticComponent
                if (component instanceof OrderBondSyntheticComponent) {
                    OrderBondSyntheticComponent orderBondSyntheticComponent = (OrderBondSyntheticComponent) component;
                    List<OrderSyntheticComponent> orders = orderBondSyntheticComponent.orders;
                    for (OrderSyntheticComponent orderSyntheticComponent : orders) {
                        List<GoodsSyntheticComponent> goodsSyntheticComponentList = orderSyntheticComponent.getGoodsSyntheticComponents();
                        OrderComponent orderComponent = orderSyntheticComponent.getOrderComponent();
                        String sellerId = orderComponent.getFields().getString("sellerId");
                        AppDebug.d(TAG, "taoke sellerId = " + sellerId);
                        for (GoodsSyntheticComponent goodsSyntheticComponent : goodsSyntheticComponentList) {
                            String itemId = goodsSyntheticComponent.getItemComponent().getItemId();
                            AppDebug.d(TAG, "taoke itemId = " + sellerId);
                            itemIdList.add(itemId);
                        }

                    }
                }
            }

            if (itemIdList != null && itemIdList.size() > 0) {
                String allItemIdString = "";
                AppDebug.d(TAG, "taoke itemIdList = " + itemIdList.toString());
                for (int i = 0; i < itemIdList.size(); i++) {
                    if (i == 0) {
                        allItemIdString += itemIdList.get(0);

                    } else {
                        allItemIdString += "," + itemIdList.get(i);
                    }
                }
                AppDebug.d(TAG, "taoke itemIdList = " + allItemIdString);
                String stbId = DeviceUtil.initMacAddress(getApplicationContext());
                mBusinessRequest.requestTaokeDetailAnalysis(stbId, User.getNick(), allItemIdString, null, null, new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(mBuildOrderActivity)));
            }
        }
    }

    /**
     * 更新数据,重新生成布局
     */
    private void reBuildView() {
        AppDebug.i(TAG, "reBuildView ....");

        if (viewAdapter != null) {
            viewAdapter.notifyDataSetChanged();
        }

        upCommonContent();
    }

    /**
     * 显示界面
     */
    private void showAllCommon() {

        View leftLayout = mBuildOrderActivity.findViewById(R.id.ytm_buildorder_info_layout);
        View rightLayout = mBuildOrderActivity.findViewById(R.id.ytm_buildorder_buyorder_layout);

        leftLayout.setVisibility(View.VISIBLE);
        rightLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 更新界面的内容
     */
    private void upCommonContent() {
        // 计算需要显示的数据
        mOrderBuider.fillGoodsInfoComponents();

        if (!checkItemComponent()) {
            return;
        }

        showAllCommon();
        // 更新左侧的内容
        upDateLeftContent();
        //  更新地址
        upAddressContent();
    }

    /**
     * 如果返回的组件中，检查有没有默认地址，根据默认地址调整布局
     */
    private void reAdjustLayout() {
        AppDebug.i(TAG, "reAdjustLayout -->  mOrderBuider.mAddressComponent = " + mOrderBuider.mAddressComponent);
        if (mOrderBuider.mAddressComponent == null) {
            // 隐藏地址列表
            BuildOrderAddressRelativeLayout layout = (BuildOrderAddressRelativeLayout) mBuildOrderActivity
                    .findViewById(R.id.buildorder_address_linearlayout);
            layout.setVisibility(View.GONE);
            // 组件listview 向下获得焦点是确认下单按钮
            purchaseListView.setNextFocusDownId(R.id.buildorder_button);
            // 确认下单按钮向上获得焦点是 组件listview
            if (viewAdapter.getCount() == 0) {
                submitBuildOrder.setNextFocusUpId(submitBuildOrder.getId());
            } else
                submitBuildOrder.setNextFocusUpId(R.id.buildorder_buyorder_listview);
            submitBuildOrder.setNextFocusLeftId(cartlistFocusContainer.getId());

            // 把组件列表的高度变成540dp
            BuildorderFocusListView listview = (BuildorderFocusListView) mBuildOrderActivity.findViewById(R.id.buildorder_buyorder_listview);
            int focuslistheight = mBuildOrderActivity.getResources().getDimensionPixelSize(R.dimen.dp_540);
            LayoutParams listviewLp = listview.getLayoutParams();
            listviewLp.height = focuslistheight;
            listview.requestLayout();
        } else {
            BuildOrderAddressRelativeLayout layout = (BuildOrderAddressRelativeLayout) mBuildOrderActivity
                    .findViewById(R.id.buildorder_address_linearlayout);
            layout.setVisibility(View.VISIBLE);
            View addressHint = layout.findViewById(R.id.presaleaddresshint);
            addressHint.setVisibility(hasPresaleAddress ? View.VISIBLE : View.GONE);
            // 组件listview 向下获得焦点是地址列表
            purchaseListView.setNextFocusDownId(R.id.buildorder_address_linearlayout);
            // 确认下单按钮向上获得焦点是 地址列表
            submitBuildOrder.setNextFocusUpId(R.id.buildorder_address_linearlayout);
            submitBuildOrder.setNextFocusLeftId(cartlistFocusContainer.getId());

            // 把组件列表的高度变成386dp
//            FocusRelativeLayout frameLayout = (FocusRelativeLayout) mBuildOrderActivity
//                    .findViewById(R.id.buildorder_listview_framelayout);
            BuildorderFocusListView listview = (BuildorderFocusListView) mBuildOrderActivity.findViewById(R.id.buildorder_buyorder_listview);
            int focuslistheight = mBuildOrderActivity.getResources().getDimensionPixelSize(R.dimen.dp_386);
            LayoutParams listviewLp = listview.getLayoutParams();
            listviewLp.height = focuslistheight;
            listview.requestLayout();
        }
    }

    /**
     * 初始化listview
     *
     * @param components
     */
    private void initListView(List<Component> components) {
        if (viewAdapter != null) {
            viewAdapter.setData(mOrderBuider.filterComponents(components));
            viewAdapter.notifyDataSetChanged();

            int select = purchaseListView.getSelectedItemPosition();
            int componentscount = viewAdapter.getCount();
            if (select > componentscount - 1) {
                select = componentscount - 1;
                purchaseListView.setSelection(select);
            }
            if (purchaseListView.isFocused()) {
                FocusPositionManager focusPositionManager = (FocusPositionManager) mBuildOrderActivity
                        .findViewById(R.id.buildorder_mainlayout);
                focusPositionManager.resetFocused();
                purchaseListView.requestFocus();
                handlerMask(select, true, false);
            }
            AppDebug.i(TAG, "initListView --> select  = " + select + "; componentscount = " + componentscount);
        } else {
            viewAdapter = new ViewAdpter(mOrderBuider.filterComponents(components), mBuildOrderActivity);
            purchaseListView.setAdapter(viewAdapter);
            purchaseListView.setSelection(0);
        }

        int count = 0;
        if (viewAdapter != null) {
            count = viewAdapter.getCount();
        }
        if (count <= 0) {
            purchaseListView.setVisibility(View.GONE);
        } else {
            purchaseListView.setVisibility(View.VISIBLE);
        }

        reAdjustLayout();
    }

    /**
     * 检查当前商品或店铺是否有无效商品
     *
     * @return
     */
    private boolean checkItemComponent() {

        AppDebug.i(TAG, "checkItemComponent --> onlyOneItem = " + mOrderBuider.onlyOneItem
                + "; mItemComponent_Single = " + mOrderBuider.mItemComponent_Single + "; mHaveValidGoods = "
                + mOrderBuider.mHaveValidGoods);
        String qrText = "";
        if (!mOrderBuider.mVaildOrder) {
            qrText = mBuildOrderActivity.getResources().getString(R.string.ytbv_qr_buy_nottv);
        }

        if (!mOrderBuider.mHaveValidGoods) {
            //如果当前订单中没有有效的商品，那么弹二维码提示
            if (mOrderBuider.onlyOneItem) {
                String reason = mOrderBuider.mItemComponent_Single.getReason();
                if (!TextUtils.isEmpty(reason)) {
                    mBuildOrderActivity.showErrorDialog(reason, true);
                    return false;
                }
            }
            mBuildOrderActivity.showItemQRCode(qrText, new QRCodeKeyListener() {

                @Override
                public boolean onQRCodeKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (mBuildOrderActivity != null) {
                        mBuildOrderActivity.finish();
                    }
                    return true;
                }
            }, true);
            return false;
        }

        return true;
    }

    /**
     * 更新左侧内容
     */
    private void upDateLeftContent() {
        // 更新标题
        setTitleText();
        // 更新总价
        setTotalPrice();

        // 更新小票的内容
        upDateGoodsInfo();
    }

    /**
     * 更新地址内容信息
     */
    private void upAddressContent() {
        if (mOrderBuider.mAddressComponent != null && mAddresslist != null && addressGallery != null) {
            String addressId = mOrderBuider.getComponentSelectedAddress();
            if (!TextUtils.isEmpty(addressId)) {
                int size = mAddresslist.size();
                for (int index = 0; index < size; index++) {
                    Address addr = mAddresslist.get(index);
                    if (addr == null) {
                        continue;
                    }
                    String id = addr.getDeliverId();
                    if (addressId.equals(id)) {
                        int defaultposition = reAdjustPosition(index);
                        addressGallery.setSelection(defaultposition);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 更新标题和SKU或者失效的商品名称
     */
    private void setTitleText() {

//        // FIXME: 9/28/16
//        GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.mGoodsDisplayInfo;
//        if (mGoodsDisplayInfo == null) {
//            return;
//        }
//        String title = mGoodsDisplayInfo.getTitle();
//        String sku = mGoodsDisplayInfo.getSku();
//        int validSize = mGoodsDisplayInfo.getInvalidGoods().size();
//        if (validSize <= 0) {
//            validSize = mGoodsDisplayInfo.getValidGoods().size();
//        }
//        AppDebug.i(TAG, "setTitleText --> title = " + title + "; sku = " + sku + "; validSize = " + validSize);
//        if (!TextUtils.isEmpty(title)) {
//            mGoodsTitle.setText(title);
//            mGoodsTitle.setVisibility(View.VISIBLE);
//        } else {
//            mGoodsTitle.setVisibility(View.GONE);
//        }
//
//        if (!TextUtils.isEmpty(sku)) {
//            mGoodsSku.setText(sku);
//            mGoodsSku.setVisibility(View.VISIBLE);
//        } else {
//            if (validSize > 0) {
//                mGoodsSku.setVisibility(View.VISIBLE);
//                mGoodsSku.requestLayout();
//            } else {
//                mGoodsSku.setVisibility(View.GONE);
//            }
//        }
    }

    /**
     * 更新总价
     */
    private void setTotalPrice() {
        GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.mGoodsDisplayInfo;
        if (mGoodsDisplayInfo == null) {
            return;
        }
        String fuhaoString = mBuildOrderActivity.getResources().getString(R.string.ytbv_price_unit_text);
        String price = mGoodsDisplayInfo.getTotalPrice();
        if (!TextUtils.isEmpty(price)) {
            totalPrice.setText(" ¥ " + price);
            totalPrice.setVisibility(View.VISIBLE);
        }

        long rebateCoupon = 0;
        String rebateCouponMessage = null;
        String rebatePic = null;
//        isNull表示返利金额是否为null，如果为null则是预售
        boolean isNull = true;
        if (rebateBoList != null && rebateBoList.size() > 0) {
            for (BuildOrderRebateBo rebateBo : rebateBoList) {
                if (rebateBo != null) {
                    if (!TextUtils.isEmpty(rebateBo.getCoupon())) {
                        long coupon = Long.parseLong(rebateBo.getCoupon());
                        rebateCoupon = rebateCoupon + coupon;
                        isNull = false;
                    }
                    if (TextUtils.isEmpty(rebateCouponMessage) && rebateBo.getMessage() != null) {
                        rebateCouponMessage = rebateBo.getMessage();
                    }
                    if (TextUtils.isEmpty(rebatePic) && rebateBo.getPicUrl() != null) {
                        rebatePic = rebateBo.getPicUrl();
                    }
                }
            }
            //isNull为false，则是正常返利
            if (!isNull) {
                if (!TextUtils.isEmpty(rebateCouponMessage)) {
                    rebateCouponMessage = rebateCouponMessage + ":";
                    tvRabateMassage.setText(rebateCouponMessage);
                    tvRabateMassage.setVisibility(View.VISIBLE);
                } else {
                    tvRabateMassage.setVisibility(View.GONE);

                }
                String rebateCouponResult = Utils.getRebateCoupon(rebateCoupon + "");
                if (!TextUtils.isEmpty(rebateCouponResult)) {
                    tvRebate.setText(" ¥ " + rebateCouponResult);
                    tvRabateMassage.setVisibility(View.VISIBLE);
                    tvRebate.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(rebatePic)) {
                        ivRebate.setVisibility(View.VISIBLE);
                        ImageLoaderManager.getImageLoaderManager(mBuildOrderActivity).displayImage(rebatePic, ivRebate);
                    } else {/**/
                        ivRebate.setVisibility(View.GONE);
                    }
                } else {
                    tvRabateMassage.setVisibility(View.GONE);
                    tvRebate.setVisibility(View.GONE);
                    ivRebate.setVisibility(View.GONE);
                }
            } else {
                if (!TextUtils.isEmpty(rebateCouponMessage)) {
                    rebateCouponMessage = rebateCouponMessage + ":";
                    tvRabateMassage.setText(rebateCouponMessage);
                    tvRabateMassage.setVisibility(View.VISIBLE);
                } else {
                    tvRabateMassage.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(rebatePic)) {
                    ivRebate.setVisibility(View.VISIBLE);
                    ImageLoaderManager.getImageLoaderManager(mBuildOrderActivity).displayImage(rebatePic, ivRebate);
                } else {/**/
                    ivRebate.setVisibility(View.GONE);
                }

            }


        } else {
            tvRabateMassage.setVisibility(View.GONE);
            tvRebate.setVisibility(View.GONE);
            ivRebate.setVisibility(View.GONE);

        }
    }

    /**
     * 更新左侧列表中的显示信息
     */
    private void upDateGoodsInfo() {
        mAdapter = new BuildOrderCartItemListAdapter(mBuildOrderActivity, mSellers, orderComponents, rebateBoList, mBuildOrderPreSale);
        AppDebug.d(TAG, "list width: " + cartItemListView.getWidth() + ": -> " + cartItemListView.getMeasuredWidth());
//        mAdapter.setListWidth(cartItemListView.getWidth());
        cartItemListView.setAdapter(mAdapter);


//        GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.mGoodsDisplayInfo;
//        if (mGoodsDisplayInfo == null) {
//            return;
//        }
//        if (mBuildOrderGoodsAdapter == null) {
//            mBuildOrderGoodsAdapter = new BuildOrderGoodsAdapter(mBuildOrderActivity, mGoodsDisplayInfo.getInfoList());
//            infoListview.setAdapter(mBuildOrderGoodsAdapter);
//        } else {
//            mBuildOrderGoodsAdapter.setGoodsInfoList(mGoodsDisplayInfo.getInfoList());
//            mBuildOrderGoodsAdapter.notifyDataSetChanged();
//        }
    }

    /**
     * 当没有收货地址时的处理
     *
     * @param buildOrderActivity
     */
    private void onNoAddress(BuildOrderActivity buildOrderActivity) {
        String address = mBuildOrderActivity.getResources().getString(R.string.ytsdk_no_address);
        mBuildOrderActivity.showErrorDialog(address, true);
    }

    /**
     * 处理地址请求返回后的结果
     *
     * @param data
     */
    private void matchData(List<Address> data) {

        if (data == null) {
            return;
        }

        if (mAddresslist == null) {
            mAddresslist = new ArrayList<Address>();
        }

        // 查找默认地址，并把默认地址放在第一个
        LinkedList<Address> linkedList = new LinkedList<Address>();
        for (Address address : data) {
            int status = address.getStatus();
            if (status == Address.DEFAULT_DELIVER_ADDRESS_STATUS) {
                // 将默认地址放在第一个
                linkedList.addFirst(address);
            } else {
                linkedList.add(address);
            }
        }
        //清空原来的地址信息
        mAddresslist.clear();
        // 把重新整理后的数据填入mAddresslist中
        mAddresslist.addAll(linkedList);

        // 在最后添加一个空的数据结构，用来表示编辑地址
        Address edit = new Address();
        mAddresslist.add(edit);

        linkedList = null;

        if (mBuildOrderAddressAdapter == null) {
            mBuildOrderAddressAdapter = new BuildOrderAddressAdapter(mBuildOrderActivity, mAddresslist);
        } else {
            mBuildOrderAddressAdapter.setData(mAddresslist);
            mBuildOrderAddressAdapter.notifyDataSetChanged();
        }

        if (addressGallery.getAdapter() == null) {
            addressGallery.setAdapter(mBuildOrderAddressAdapter);
        }
        int defaultPos = getDefaultAddressPos();
        addressGallery.setSelection(defaultPos);
        // 地址请求返回后，检查输入组件是否需要重新改变内容
        changedInputComponentContent();
    }

    /**
     * 获取默认显示地址的位置
     *
     * @return
     */
    private int getDefaultAddressPos() {
        int defaultposition = 0;
        if (mOrderBuider.mAddressComponent != null && mAddresslist != null) {
            String optionAddrStr = mOrderBuider.getComponentSelectedAddress();
            if (!TextUtils.isEmpty(optionAddrStr)) {
                int size = mAddresslist.size();
                for (int index = 0; index < size; index++) {
                    Address address = mAddresslist.get(index);
                    if (address == null) {
                        continue;
                    }
                    String addressId = address.getDeliverId();
                    if (TextUtils.isEmpty(addressId)) {
                        continue;
                    }

                    AppDebug.d(TAG, "getDefaultAddressPos ---> mAddressComponent optionAddrStr  = " + optionAddrStr
                            + "; addressId = " + addressId);

                    if (addressId.equals(optionAddrStr)) {
                        // 如果相等，
                        defaultposition = index;

                        break;
                    }
                }
            }
        }

        defaultposition = reAdjustPosition(defaultposition);

        AppDebug.i(TAG, "getDefaultAddressPos --> defaultposition = " + defaultposition);
        return defaultposition;
    }

    /**
     * 根据循环值，重新定位选中项
     */
    private int reAdjustPosition(int index) {
        AppDebug.i(TAG, "reAdjustPosition --> index = " + index);
        int re_position = 0;
        if (mBuildOrderAddressAdapter == null) {
            return 0;
        }
        int position = mBuildOrderAddressAdapter.getCount() / 2;
        int size = mBuildOrderAddressAdapter.getAddressesSize();
        if (size > 0) {
            int remainder = position % size;
            position -= remainder;
            re_position = position + index;
            AppDebug.i(TAG, "getDefaultAddressPos --> remainder = " + remainder + ";  size = " + size + "; position = "
                    + position);
        }
        return re_position;
    }

    /**
     * 获取数据处理类对象
     *
     * @return
     */
    public OrderBuider getOrderBuider() {
        return mOrderBuider;
    }

    /**
     * 支付完成,旧版
     *
     * @param isSuccess 支付是否成功
     */
    public void payDone(boolean isSuccess) {
        mBuildOrderActivity.onTextProgressDialog(null, false);
        AppDebug.v(TAG, TAG + ".payDone.isSuccess = " + isSuccess);
        // 支付成功设置返回值
        Intent data = new Intent();
        if (isSuccess) {
            data.putExtra("pay_result", true);
        } else {
            data.putExtra("pay_result", false);
        }
        mBuildOrderActivity.setResult(Activity.RESULT_OK, data);
        mBuildOrderActivity.leavePage();
        mBuildOrderActivity.finish();
//        if (isSuccess) {
        // enterPaidAdvertActivity();
//        }
    }

    public void payDone(boolean isSuccess, double price, String account, String taobaoBizOrderIds) {
        AppDebug.v(TAG, TAG + ".payDone.isSuccess = " + isSuccess);
        // 支付成功设置返回值
        Intent data = new Intent();
        if (isSuccess) {
            data.putExtra("pay_result", true);
        } else {
            data.putExtra("pay_result", false);
        }

        if (isSuccess) {
            gotoResultActivity(isSuccess, price, account, listToString(itemIdList), taobaoBizOrderIds);
        }
        mBuildOrderActivity.setResult(Activity.RESULT_OK, data);
        mBuildOrderActivity.leavePage();
        mBuildOrderActivity.finish();
//        if (isSuccess) {
        // enterPaidAdvertActivity();
//        }
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    private void gotoResultActivity(boolean success, double price, String account, String orderGoodsIds, String bizOrderIds) {
        Uri uri;
        if (TextUtils.isEmpty(bizOrderIds)) {
            uri = Uri.parse(String.format("tvtaobao://home?module=payresult&success=%b&price=%.2f&account=%s&orderGoodsIds=%s", success, price, account, orderGoodsIds));
        } else {
            uri = Uri.parse(String.format("tvtaobao://home?module=payresult&success=%b&price=%.2f&account=%s&orderGoodsIds=%s&orderIds=%s", success, price, account, orderGoodsIds, bizOrderIds));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mBuildOrderActivity.startActivity(intent);
    }

    /**
     * 下单失败
     *
     * @param resultCode
     * @param msg
     * @return
     */
    public boolean onCreateOrderError(int resultCode, String msg) {
        AppDebug.i(TAG, "onCreateOrderError -->  resultCode = " + resultCode + "; msg = " + msg);
        mBuildOrderActivity.onTextProgressDialog(null, false);

        if (resultCode == ServiceCode.API_ERROR.getCode()) {
            String text = mBuildOrderActivity.getResources().getString(R.string.ytbv_qr_buy_error);
            mBuildOrderActivity.showItemQRCode(text, new QRCodeKeyListener() {

                @Override
                public boolean onQRCodeKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (mBuildOrderActivity != null) {
                        mBuildOrderActivity.finish();
                    }
                    return true;
                }
            }, false);

            return true;
        }
        return false;
    }

    /**
     * 下单成功
     *
     * @param orderResult
     * @return
     */
    public boolean onCreateOrderSuccess(CreateOrderResult orderResult) {

        AppDebug.i(TAG, "onCreateOrderSuccess --> orderResult = " + orderResult);

        mBuildOrderActivity.onTextProgressDialog(null, false);
        String[] bizOrderids = orderResult.getBizOrderId().split(",");
//        AppHolder appHolder = (AppHolder) mBuildOrderActivity.getApplicationContext();
//        GlobalConfig gc = appHolder.getGlobalConfig();
        for (String s : bizOrderids) {
            if (!TextUtils.isEmpty(s)) {

                try {
                    org.json.JSONObject object = new org.json.JSONObject();
                    object.put("umToken", Config.getUmtoken(mBuildOrderActivity));
                    object.put("wua", Config.getWua(mBuildOrderActivity));
                    object.put("isSimulator", Config.isSimulator(mBuildOrderActivity));
                    object.put("userAgent", Config.getAndroidSystem(mBuildOrderActivity));
                    String extParams = object.toString();


                    AppDebug.d(TAG, "upload bizid:" + s);
                    TvTaobaoBusinessRequest.getBusinessRequest()
                            .requestQueryCreateTvTaoOrderRequest(null, s, mBuildOrderActivity.mBuildOrderRequestBo.getFrom(), Config.getChannel(), SystemConfig.APP_VERSION, extParams,
                                    new RequestListener<String>() {
                                        @Override
                                        public void onRequestDone(String data, int resultCode, String msg) {
                                            AppDebug.d(TAG, "request done:" + resultCode + ",result data:" + data);
                                        }
                                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        AppDebug.d(TAG, "orderIds:" + bizOrderids);

        if (mPaidAdvertHandle == null) {
            mPaidAdvertHandle = new PaidAdvertHandle(mBuildOrderActivity);
        }
        mPaidAdvertHandle.getPaidAdvertContent(mBuildOrderActivity, orderResult.getBizOrderId());


        final GoodsDisplayInfo mGoodsDisplayInfo = mOrderBuider.mGoodsDisplayInfo;
        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (bizOrderids.length > 1 || (gc != null && gc.taobaoPay))//|| (gc != null && gc.getDoubleShopCart() != null && gc.getDoubleShopCart().isBoolShopCartMergeOrders())) {
        {    //TODO
            mBuildOrderActivity.onTextProgressDialog(null, false);
            showQRPayDialog(bizOrderids);
            return true;
        } else if (mOrderBuider.mGoodsDisplayInfo.isInstallMentPay()) {
            mBuildOrderActivity.onTextProgressDialog(null, false);
            showQRPayDialog(bizOrderids);
            return true;
        } else if (!orderResult.isSimplePay()) {
            // 设置返回值
            mBuildOrderActivity.onTextProgressDialog(null, false);
            mBuildOrderActivity.setResult(Activity.RESULT_OK);
            // 如果不调起支付，那么直接提示下单成功
            String msg = mBuildOrderActivity.getResources().getString(R.string.ytm_buildorder_success);
            mBuildOrderActivity.showErrorDialog(msg, true);
            return true;
        } else {
//            AppDebug.d("test", "supportCoupon:" + gc.getCoupon().supportTaobaoCoupon());
//            if (gc.getCoupon().supportTaobaoCoupon()) {//支付宝代付 不支持 红包//// FIXME: 11/30/16 应当是!supportTaobaoCoupon
//            queryCoupon(bizOrderids[0], mGoodsDisplayInfo, orderResult);
            doYunosPay(mGoodsDisplayInfo, orderResult);
            return true;
//            }
        }

//        doYunosPay(mGoodsDisplayInfo, orderResult);
        // 新版支付


//        return true;
    }

    private void doYunosPay(final GoodsDisplayInfo mGoodsDisplayInfo,
                            final CreateOrderResult orderResult) {
        mBuildOrderActivity.onTextProgressDialog(" 提交订单支付中...", true);
        YunOSOrderManager orderManager = new YunOSOrderManager();
        final String subject = mGoodsDisplayInfo.getTitle();// 商品名称
        double totalPrice = 0.0f;
        try {
            totalPrice = Double.parseDouble(mGoodsDisplayInfo.getTotalPrice());
        } catch (Exception e) {
            AppDebug.i(TAG, "onHandleRequestCreateOrder --> e = " + e.toString());
        }

        String price = String.valueOf(totalPrice * 100);// 价格，以分为单位
        String alipayTradeNo = String.valueOf(orderResult.getAlipayOrderId());
        String taobaoOrderNo = String.valueOf(orderResult.getBizOrderId());
        orderManager.GenerateOrder("orderNo=" + alipayTradeNo + "&subject=" + subject + "&price=" + price
                + "&orderType=trade" + "&taobaoOrderNo=" + taobaoOrderNo);
        String order = orderManager.getOrder();
        String sign = orderManager.getSign();
        Bundle bundle = new Bundle();
        bundle.putString("provider", "alipay");
        if (Config.isAgreementPay())
            doAgreementPay(mBuildOrderActivity, totalPrice, null, taobaoOrderNo);
        else
            doOriginalPay(subject, String.valueOf(mGoodsDisplayInfo.getmItemIdTbs()), order, sign, bundle);


//        try {
//            AliTVPayClient payer = new AliTVPayClient();
//            payer.aliTVPay(mBuildOrderActivity, order, sign, bundle, new IPayCallback() {
//
//                @Override
//                public void onPayProcessEnd(AliTVPayResult arg0) {
//                    AppDebug.i(TAG, TAG + ".onCreateOrderSuccess.onPayProcessEnd.arg0 = " + arg0);
//                    if (arg0 == null) {
//                        return;
//                    }
//                    String controlName = Utils.getControlName(mBuildOrderActivity.getFullPageName(), "Pay", null);
//                    Map<String, String> lProperties = Utils.getProperties();
//                    if (!StringUtil.isEmpty(subject)) {
//                        lProperties.put("title", subject);
//                    }
//
//                    lProperties.put("item_id", String.valueOf(mGoodsDisplayInfo.getmItemIdTbs()));
//
//                    AppDebug.i(TAG, TAG + ".onCreateOrderSuccess.onPayProcessEnd.getPayResult = " + arg0.getPayResult());
//                    if (arg0.getPayResult()) {
//                        lProperties.put("result", "success");
//                        payDone(true);
//                    } else {
//                        lProperties.put("result", "fail");
//                        payDone(false);
//                    }
//                    Utils.utCustomHit(mBuildOrderActivity.getFullPageName(), controlName, lProperties);
//                }
//            });
//        } catch (Exception e) {
//            // 提示支付初始化失败
//            String payfail = mBuildOrderActivity.getResources().getString(R.string.ytm_buildorder_pay_initfail);
//            AppDebug.e(TAG, TAG + ".onCreateOrderSuccess.payfail = " + payfail);
//            mBuildOrderActivity.showErrorDialog(payfail, new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    payDone(false);
//                }
//            }, new DialogInterface.OnKeyListener() {
//
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                        dialog.dismiss();
//                        payDone(false);
//                        return true;
//                    }
//                    return false;
//                }
//            });
//        }

        // 下单埋点
        Map<String, String> lProperties = mBuildOrderActivity.getPageProperties();
        lProperties.put("order_id", orderResult.getBizOrderId());// 订单ID
        lProperties.put("shop_name", mGoodsDisplayInfo.getShopName());// 店铺名称
        Utils.utCustomHit(mBuildOrderActivity.getFullPageName(), "Tt_order", lProperties);
    }

    private void doOriginalPay(final String subject, final String itemIds, String
            order, String sign, Bundle parameters) {
        try {
            AliTVPayClient payer = new AliTVPayClient();
            payer.aliTVPay(mBuildOrderActivity, order, sign, parameters, new AliTVPayClient.IPayCallback() {

                @Override
                public void onPayProcessEnd(AliTVPayResult arg0) {
                    AppDebug.i(TAG, TAG + ".onCreateOrderSuccess.onPayProcessEnd.arg0 = " + arg0);
                    if (arg0 == null) {
                        return;
                    }
                    String controlName = Utils.getControlName(mBuildOrderActivity.getFullPageName(), "Pay", null);
                    Map<String, String> lProperties = Utils.getProperties();
                    if (!TextUtils.isEmpty(subject)) {
                        lProperties.put("title", subject);
                    }

                    lProperties.put("item_id", itemIds);

                    AppDebug.i(TAG, TAG + ".onCreateOrderSuccess.onPayProcessEnd.getPayResult = " + arg0.getPayResult());
                    if (arg0.getPayResult()) {
                        lProperties.put("result", "success");
                        payDone(true);
                    } else {
                        lProperties.put("result", "fail");
                        payDone(false);
                    }
                    Utils.utCustomHit(mBuildOrderActivity.getFullPageName(), controlName, lProperties);
                }
            });
        } catch (Exception e) {
            // 提示支付初始化失败
            String payfail = mBuildOrderActivity.getResources().getString(R.string.ytm_buildorder_pay_initfail);
            AppDebug.e(TAG, TAG + ".onCreateOrderSuccess.payfail = " + payfail);
            mBuildOrderActivity.showErrorDialog(payfail, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    payDone(false);
                }
            }, new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        payDone(false);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void doAgreementPay(final Context context, final double totalPrice,
                                final String alipayUserId, final String taobaoOrderNo) {
        BuildOrderRequestBo bo = mBuildOrderActivity.mBuildOrderRequestBo;
        AlipayPaymentManager.doPay(mBuildOrderActivity, totalPrice, null, taobaoOrderNo, bo != null && !TextUtils.isEmpty(bo.getCartIds()), mBuildOrderPreSale != null, new AlipayPaymentManager.AlipayAgreementPayListener() {
            @Override
            public void paymentSuccess(double price, String account) {
                mBuildOrderActivity.onTextProgressDialog(null, false);
                payDone(true, price, account, taobaoOrderNo);
            }

            @Override
            public void paymentFailure(String errorMsg) {
                mBuildOrderActivity.onTextProgressDialog(null, false);
                payDone(false);

            }

            @Override
            public void paymentCancel() {
                mBuildOrderActivity.onTextProgressDialog(null, false);
                payDone(false);
            }
        });
    }

    @Deprecated
    private void queryCoupon(final String orderid,
                             final GoodsDisplayInfo goodsDisplayInfo, final CreateOrderResult orderResult) {
        mBuildOrderActivity.onTextProgressDialog(" 提交订单支付中...", true);
        BusinessRequest.getBusinessRequest().baseRequest(new OrderDetailRequest(orderid), new BizRequestListener<OrderDetailBo>(new WeakReference<BaseActivity>(mBuildOrderActivity)) {
            @Override
            public boolean onError(int resultCode, String msg) {
                mBuildOrderActivity.onTextProgressDialog(null, false);
                mBuildOrderActivity.showErrorDialog("获取订单状态失败", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        payDone(false);
                    }
                }, new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            payDone(false);
                            return true;
                        }
                        return false;
                    }
                });
                return true;
            }

            @Override
            public void onSuccess(OrderDetailBo data) {
                mBuildOrderActivity.onTextProgressDialog("", false);
                if (data.hasCoupon()) {
                    showQRPayDialog(new String[]{orderid});
                } else {
                    doYunosPay(goodsDisplayInfo, orderResult);
                }
            }

            @Override
            public boolean ifFinishWhenCloseErrorDialog() {
                return false;
            }

        }, true);
    }

    private SpannableStringBuilder onHandlerSpanned(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("\\d+(.)*\\d+");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            if (start > 0) {
                String jianhao = src.substring(start - 1, start);
                if ("-".equals(jianhao)) {
                    start--;
                }
            }
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    private void showQRPayDialog(String[] bizIds) {
        Bitmap qrBitmap = null;
        int width = (int) mBuildOrderActivity.getResources().getDimensionPixelSize(R.dimen.dp_334);
        int height = width;
        try {
//            Bitmap bm = BitmapFactory.decodeResource(mBuildOrderActivity.getResources(), R.drawable.icontaobao);

            qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/mlapp/olist.html?OrderListType=wait_to_pay", width, height, null);
//            qrBitmap = QRCodeManager.create2DCode("http://tm.m.taobao.com/list.htm?OrderListType=wait_to_pay", width, height, bm);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        final QRDialog dialog = new QRDialog.Builder(mBuildOrderActivity).setCancelable(true).setQrCodeBitmap(qrBitmap).setTitle(createSpannableTitle("打开【手机淘宝】扫码付款"))
                .setQRCodeText(onHandlerSpanned("订单合计" + mOrderBuider.getGoodsDisplayInfo().getTotalPrice() + "元")).create();
        if (bizIds != null && bizIds.length > 0) {

            ArrayList<String> ids = new ArrayList<String>();
            Collections.addAll(ids, bizIds);
//            for (String id : bizIds) {
//                ids.add(id);
//            }
            dialog.setBizOrderIds(ids);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        Utils.utCustomHit("Expose_STJumpcode_pop_out_payment", Utils.getProperties());
                        QuitPayConfirmDialog confirmDialog = new QuitPayConfirmDialog.Builder(mBuildOrderActivity).setTitle("退出支付").setMessage("支付未完成,是否确认退出?").setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.utControlHit("STJumpcode_pop_out_payment", "button_out", Utils.getProperties());
                                dialogInterface.dismiss();
                                dialog.dismiss();
                                payDone(false);
                            }
                        }).setNegativeButton("继续支付", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.utControlHit("STJumpcode_pop_out_payment", "button_cancel", Utils.getProperties());
                                dialogInterface.dismiss();
                            }
                        }).create();
                        confirmDialog.show();
                    }
                    return true;
                }
                return false;
            }
        });
        dialog.setDelegate(new QRDialog.QRDialogDelegate() {
            @Override
            public void QRDialogSuccess(QRDialog dialog, boolean success) {
                payDone(success);
            }
        });
        dialog.show();
    }

    /**
     * 创建spannableString
     */
    private SpannableStringBuilder createSpannableTitle(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("【手机淘宝】");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    /**
     * 预下单调整接口返回监听
     */
    public static class BuildOrderListener extends BizRequestListener<String> {

        public BuildOrderListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.i(TAG, "BuildOrderListener --> onError --> resultCode = " + resultCode + "; msg = " + msg);
            final BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (resultCode == 419) {
                if (buildOrderActivity != null) {
                    buildOrderActivity.finish();
                }
            }
            if (buildOrderActivity != null) {
                buildOrderActivity.OnWaitProgressDialog(false);

                ViewBuilder viewBuilder = buildOrderActivity.mViewBuilder;
                //如果是没有收货地址,
                if (ServiceCode.NO_ADDRESS.getCode() == resultCode) {
                    viewBuilder.onNoAddress(buildOrderActivity);
                    return true;
                }

                if (buildOrderActivity.mFirstRequestOfBuildOrder && ServiceCode.API_ERROR.getCode() == resultCode) {
                    AppDebug.e(TAG, "买不了3，requestcod==106");
                    //第一次创建订单出现错误，并且resultCode 为 106，那么弹出二维码
                    buildOrderActivity.showItemQRCode(null, new QRCodeKeyListener() {

                        @Override
                        public boolean onQRCodeKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            buildOrderActivity.finish();
                            return true;
                        }
                    }, true);
                    buildOrderActivity.mFirstRequestOfBuildOrder = false;
                    return true;
                }
            }

            return false;
        }

        @Override
        public void onSuccess(String data) {
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {

//                buildOrderActivity.OnWaitProgressDialog(false);
//                if (buildOrderActivity.mFirstRequestOfBuildOrder) {
//                    // 如果是第一次那么初始化界面的Focus
//                    buildOrderActivity.onInitBuildOrder();
//                }

                buildOrderActivity.mViewBuilder.getRebate(data);

//                try {
//                    boolean result = buildOrderActivity.mViewBuilder.buildView();
//                    if (!result) {
//                        buildOrderActivity.finish();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    buildOrderActivity.finish();
//                }
//                buildOrderActivity.mFirstRequestOfBuildOrder = false;
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }


    private void getRebate(String json) {
        itemIdList.clear();
        if (TextUtils.isEmpty(json)) {
            return;
        }
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        JSONObject jobj = JSON.parseObject(json);
        AppDebug.d(TAG, "refresh flag:" + jobj.getBooleanValue("reload"));
        mComponents = mBuyEngine.parse(jobj);
        AppDebug.e(TAG, "预售商品----》");
        JSONArray jsonArray = new JSONArray();
        String tagId;
        String tvOptions;
        String appKey;
        double priceDouble = 0;
        double quantityDouble = 0;
        double priceResult = 0;
        JSONArray jsonArrayRebate = new JSONArray();

        for (int i = 0; i < mComponents.size(); i++) {
            Component component = mComponents.get(i);
            if (component instanceof OrderSyntheticComponent) {
                OrderSyntheticComponent orderSyntheticComponent = (OrderSyntheticComponent) component;
                //返利需要传的订单号和每笔订单的价格
                org.json.JSONObject objectRebate = new org.json.JSONObject();
                OrderPayComponent orderPayComponent = orderSyntheticComponent.getOrderPayComponent();
                if (orderPayComponent != null) {
                    String payValue = orderPayComponent.getFields().getString("price");
                    try {
                        objectRebate.put("orderId", orderPayComponent.getId());
                        objectRebate.put("payment", payValue);

                        List<GoodsSyntheticComponent> goodsSyntheticComponentList = orderSyntheticComponent.getGoodsSyntheticComponents();
                        for (GoodsSyntheticComponent goodsSyntheticComponent : goodsSyntheticComponentList) {
                            if (goodsSyntheticComponent.getItemComponent() != null) {
                                String itemId = goodsSyntheticComponent.getItemComponent().getItemId();
                                itemIdList.add(itemId);
                                if (goodsSyntheticComponent.getItemPayComponent() != null) {
                                    if (!TextUtils.isEmpty(goodsSyntheticComponent.getItemPayComponent().getAfterPromotionPrice())) {
                                        priceDouble = Double.parseDouble(goodsSyntheticComponent.getItemPayComponent().getAfterPromotionPrice());
                                    }
                                    if (!TextUtils.isEmpty(goodsSyntheticComponent.getItemPayComponent().getQuantity())) {
                                        quantityDouble = Double.parseDouble(goodsSyntheticComponent.getItemPayComponent().getQuantity());
                                    }
                                    priceResult = priceDouble * quantityDouble;
                                }
                                tagId = null;
                                tvOptions = null;
                                appKey = null;
                                if (goodsSyntheticComponent.getRebateComponent() != null && goodsSyntheticComponent.getRebateComponent().getFields() != null) {
                                    tagId = goodsSyntheticComponent.getRebateComponent().getFields().getString("tagId");
                                    tvOptions = goodsSyntheticComponent.getRebateComponent().getFields().getString("tvOptions");
                                    appKey = goodsSyntheticComponent.getRebateComponent().getFields().getString("appKey");
                                }
                                //                                如果tvOptions为空,则设置tvOptions

                                if (TextUtils.isEmpty(tvOptions)) {
                                    tvOptions = TvOptionsConfig.getTvOptions();
                                    if (mBuildOrderRequestBo.getFrom().equals("cart")) {
                                        tvOptions = tvOptions.substring(0, 5) + "1";
                                    }

                                }

                                if (TextUtils.isEmpty(tagId)) {
                                    if (!mBuildOrderRequestBo.getFrom().equals("cart")) {
                                        tagId = mBuildOrderRequestBo.getTagId();
                                    }
                                }
                                org.json.JSONObject objectDetail = new org.json.JSONObject();
                                objectDetail.put("itemId", itemId);
                                objectDetail.put("tagId", tagId);
                                objectDetail.put("tvOptions", tvOptions);
                                objectDetail.put("appKey", appKey);
                                objectDetail.put("price", priceResult);
                                jsonArray.put(objectDetail);
                            }
//
                        }

                        objectRebate.put("detail", jsonArray);
                        jsonArrayRebate.put(objectRebate);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            //OrderBondSyntheticComponent
            if (component instanceof OrderBondSyntheticComponent) {
                OrderBondSyntheticComponent orderBondSyntheticComponent = (OrderBondSyntheticComponent) component;

                List<OrderSyntheticComponent> orders = orderBondSyntheticComponent.orders;
                for (OrderSyntheticComponent orderSyntheticComponent : orders) {
//                返利需要传的订单号和每笔订单的价格
                    org.json.JSONObject objectRebate = new org.json.JSONObject();

                    OrderPayComponent orderPayComponent = orderSyntheticComponent.getOrderPayComponent();
                    if (orderPayComponent != null) {
                        String payValue = orderPayComponent.getFields().getString("price");
                        try {
                            objectRebate.put("orderId", orderPayComponent.getId());
                            objectRebate.put("payment", payValue);

                            List<GoodsSyntheticComponent> goodsSyntheticComponentList = orderSyntheticComponent.getGoodsSyntheticComponents();
                            for (GoodsSyntheticComponent goodsSyntheticComponent : goodsSyntheticComponentList) {
                                String itemId = goodsSyntheticComponent.getItemComponent().getItemId();
                                itemIdList.add(itemId);
//                          AppDebug.d(TAG, "taoke itemId = " + sellerId);
                                if (goodsSyntheticComponent.getItemPayComponent() != null) {
                                    if (!TextUtils.isEmpty(goodsSyntheticComponent.getItemPayComponent().getAfterPromotionPrice())) {
                                        priceDouble = Double.parseDouble(goodsSyntheticComponent.getItemPayComponent().getAfterPromotionPrice());
                                    }
                                    if (!TextUtils.isEmpty(goodsSyntheticComponent.getItemPayComponent().getQuantity())) {
                                        quantityDouble = Double.parseDouble(goodsSyntheticComponent.getItemPayComponent().getQuantity());
                                    }
                                    priceResult = priceDouble * quantityDouble;
                                }

                                tagId = null;
                                tvOptions = null;
                                appKey = null;
                                if (goodsSyntheticComponent.getRebateComponent() != null && goodsSyntheticComponent.getRebateComponent().getFields() != null) {
                                    tagId = goodsSyntheticComponent.getRebateComponent().getFields().getString("tagId");
                                    tvOptions = goodsSyntheticComponent.getRebateComponent().getFields().getString("tvOptions");
                                    appKey = goodsSyntheticComponent.getRebateComponent().getFields().getString("appKey");
                                }
                                if (TextUtils.isEmpty(tvOptions)) {
                                    tvOptions = TvOptionsConfig.getTvOptions();
                                    if (mBuildOrderRequestBo.getFrom().equals("cart")) {
                                        tvOptions = tvOptions.substring(0, 5) + "1";
                                    }
                                }
                                if (TextUtils.isEmpty(tagId)) {
                                    if (!mBuildOrderRequestBo.getFrom().equals("cart")) {
                                        tagId = mBuildOrderRequestBo.getTagId();
                                    }
                                }
                                org.json.JSONObject object = new org.json.JSONObject();
                                object.put("itemId", itemId);
                                object.put("tagId", tagId);
                                object.put("tvOptions", tvOptions);
                                object.put("appKey", appKey);
                                object.put("price", priceResult + "");
                                jsonArray.put(object);
                            }

                            objectRebate.put("detail", jsonArray);
                            jsonArrayRebate.put(objectRebate);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }


        }
        try {
            org.json.JSONObject object = new org.json.JSONObject();
            object.put("umToken", Config.getUmtoken(mBuildOrderActivity));
            object.put("wua", Config.getWua(mBuildOrderActivity));
            object.put("isSimulator", Config.isSimulator(mBuildOrderActivity));
            object.put("userAgent", Config.getAndroidSystem(mBuildOrderActivity));
            String extParams = object.toString();

            mBusinessRequest.requestBuildOrderRebateMoney(jsonArrayRebate.toString(), extParams, new GetRebateBusinessRequestListener(new WeakReference<BaseActivity>(mBuildOrderActivity)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class GetRebateBusinessRequestListener extends BizRequestListener<List<BuildOrderRebateBo>> {

        public GetRebateBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {//商品太多返利接口超时导致加载异常
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {
//                rebateBoList = data;
                buildOrderActivity.OnWaitProgressDialog(false);
                if (buildOrderActivity.mFirstRequestOfBuildOrder) {
                    // 如果是第一次那么初始化界面的Focus
                    buildOrderActivity.onInitBuildOrder();
                }

                try {
                    boolean result = buildOrderActivity.mViewBuilder.buildView();
                    if (!result) {
                        buildOrderActivity.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    buildOrderActivity.finish();
                }
                buildOrderActivity.mFirstRequestOfBuildOrder = false;
            }
            return false;
        }

        @Override
        public void onSuccess(List<BuildOrderRebateBo> data) {
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {
                rebateBoList = data;
                buildOrderActivity.OnWaitProgressDialog(false);
                if (buildOrderActivity.mFirstRequestOfBuildOrder) {
                    // 如果是第一次那么初始化界面的Focus
                    buildOrderActivity.onInitBuildOrder();
                }

                try {
                    boolean result = buildOrderActivity.mViewBuilder.buildView();
                    if (!result) {
                        buildOrderActivity.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    buildOrderActivity.finish();
                }
                buildOrderActivity.mFirstRequestOfBuildOrder = false;
            }

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 请求地址列表数据
     */
    public static class AddressListBusinessRequestListener extends BizRequestListener<List<Address>> {

        public AddressListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.i(TAG, "AddressListBusinessRequestListener --> onError --> resultCode = " + resultCode
                    + "; msg = " + msg);
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {
                buildOrderActivity.OnWaitProgressDialog(false);
                buildOrderActivity.mViewBuilder.matchData(null);
            }
            if (resultCode == ServiceCode.NO_ADDRESS.getCode()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

        @Override
        public void onSuccess(List<Address> data) {
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {
                buildOrderActivity.OnWaitProgressDialog(false);
                buildOrderActivity.mViewBuilder.matchData(data);
            }
        }
    }

    /**
     * 确认下单监听类
     */
    public static class CreateOrderBusinessRequestListener extends BizRequestListener<CreateOrderResult> {

        public CreateOrderBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.i(TAG, "CreateOrderBusinessRequestListener --> onError --> resultCode = " + resultCode
                    + "; msg = " + msg);
            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (resultCode == 419) {
                if (buildOrderActivity != null) {
                    buildOrderActivity.finish();
                }
            }
            if (buildOrderActivity != null) {
                return buildOrderActivity.mViewBuilder.onCreateOrderError(resultCode, msg);
            }
            return false;
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

        @Override
        public void onSuccess(CreateOrderResult data) {
            AppDebug.i(TAG, "CreateOrderBusinessRequestListener --> onSuccess --> data = " + data);

            BuildOrderActivity buildOrderActivity = (BuildOrderActivity) mBaseActivityRef.get();
            if (buildOrderActivity != null) {
                buildOrderActivity.mViewBuilder.onCreateOrderSuccess(data);
            }
        }
    }

    /**
     * 确认下单界面的主Handler
     */
    public static class BuildorderHandler extends Handler {

        private final WeakReference<BuildOrderActivity> weakReference;

        public BuildorderHandler(WeakReference<BuildOrderActivity> weakReference) {
            this.weakReference = weakReference;
        }

        public void handleMessage(android.os.Message msg) {
            BuildOrderActivity buildOrderActivity = weakReference.get();
            if (buildOrderActivity != null) {
                switch (msg.what) {
                    case ADDRESS_CHANGE_MSG:
                        buildOrderActivity.mViewBuilder.onSetAddressComponentSelectedId(msg.arg1);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /**
     * 确认下单SDK的联动关系类
     */
    private static class BuildorderLinkageDelegate implements LinkageDelegate {

        private WeakReference<BuildOrderActivity> mRefActivity;

        public BuildorderLinkageDelegate(WeakReference<BuildOrderActivity> ref) {
            mRefActivity = ref;
        }

        @Override
        public void respondToLinkage(LinkageNotification linkageNotification) {
            AppDebug.i(
                    TAG,
                    "respondToLinkage --> linkageNotification.getLinkageAction() = "
                            + linkageNotification.getLinkageAction());
            if (mRefActivity != null) {
                BuildOrderActivity buildOrderActivity = mRefActivity.get();
                if (buildOrderActivity != null) {
                    if (linkageNotification.getLinkageAction() == LinkageAction.REFRESH) {
                        // 刷新本地的数据
                        ViewBuilder viewBuilder = buildOrderActivity.mViewBuilder;
                        if (viewBuilder != null) {
                            viewBuilder.reBuildView();
                        }
                    } else {
                        // 重新网络数据
                        buildOrderActivity.OnWaitProgressDialog(true);
                        ViewBuilder viewBuilder = buildOrderActivity.mViewBuilder;
                        if (viewBuilder != null) {
                            Component source = linkageNotification.getTrigger();
                            String params = viewBuilder.mBuyEngine.generateAsyncRequestDataWithZip(source);
                            viewBuilder.mTvTaobaoBusinessRequest.adjustBuildOrder(params, new BuildOrderListener(
                                    new WeakReference<BaseActivity>(buildOrderActivity)));
                        }
                    }
                }
            }
        }
    }


    private class TaokeBussinessRequestListener extends BizRequestListener<org.json.JSONObject> {

        public TaokeBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(org.json.JSONObject data) {
            AppDebug.d(TAG, "taoke ViewBuilder Success ");

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 淘客登录打点监听
     */
    private class TaokeLoginBussinessRequestListener extends BizRequestListener<org.json.JSONObject> {

        public TaokeLoginBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(org.json.JSONObject data) {
            AppDebug.d(TAG, "taoke ViewBuilder Success ");
            long nextHistoryTime = System.currentTimeMillis() + 604800000;//7天
            SharedPreferencesUtils.saveTvBuyTaoKe(mBuildOrderActivity, nextHistoryTime);

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
}
