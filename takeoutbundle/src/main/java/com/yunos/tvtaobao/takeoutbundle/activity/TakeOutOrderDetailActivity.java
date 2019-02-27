package com.yunos.tvtaobao.takeoutbundle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBagAgain;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderProductInfoBase;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.TOOrderDetailAdapter;
import com.yunos.tvtaobao.takeoutbundle.view.PromptPop;
import com.yunos.tvtaobao.takeoutbundle.view.TOFocusHorizontalListView;
import com.yunos.tvtaobao.takeoutbundle.view.TOFocusNoDeepRelativeLayout;
import com.yunos.tvtaobao.takeoutbundle.view.TOOrderDetailPositionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TakeOutOrderDetailActivity extends BaseActivity {

    private static final String TAG = "TakeOutOrderDetailActivity";

    private TOFocusHorizontalListView horizontalListView;
    private TextView mBuyCountView;
    private String mainOrderId;
    private TOFocusNoDeepRelativeLayout buyAgainFrame;
    private TOOrderDetailPositionManager focusPositionManager;
    private TOOrderDetailAdapter orderDetailAdapter;
    private BusinessRequest businessRequest = BusinessRequest.getBusinessRequest();
    private ImageLoaderManager imageLoaderManager; // 图片加载器
    private DisplayImageOptions displayImageOptions; // 图片加载的参数设置
    private Handler mHandler = new Handler(); // UI的Handler

    private ImageView scrollCover;
    private String v_from;

    @Override
    public String getFullPageName() {
        return "Page_waimai_OrderDetail";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, SPMConfig.WAIMAI_ORDER_PAGE_DETAIL+".0.0");
        return p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_out_order_detail);

        mainOrderId = getIntent().getExtras().getString("tbmainorderid");
        if (StringUtil.isEmpty(mainOrderId)) {
            //TODO 不能启动
            return;
        }

        mPageName = "";

        horizontalListView = (TOFocusHorizontalListView) findViewById(R.id.take_out_order_detail_list_view);
        horizontalListView.setFlingScrollMaxStep(100);
        horizontalListView.setFlipScrollFrameCount(10);

        focusPositionManager = (TOOrderDetailPositionManager) findViewById(R.id.to_order_detail_focus_root_layout);
        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.to_focus_round)));

        scrollCover = (ImageView) findViewById(R.id.food_item_scroll_cover);

        focusPositionManager.requestFocus();

        businessRequest.getTakeOutOrderDetail(mainOrderId,
                new GetTakeOutOrderDetailListener(new WeakReference<BaseActivity>(this)));

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(getApplicationContext());
        displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(true).cacheInMemory(false).build();

        v_from = getIntent().getStringExtra("v_from");
        initBuyAgain();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PromptPop.destory();
    }

    /**
     * Focus状态的变化设置不同的UI显示
     *
     * @param hasFocus
     */
    private void focusedAccountView(boolean hasFocus) {
        if (hasFocus) {
            mBuyCountView.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom_highlight);
        } else {
            mBuyCountView.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom);
        }
    }

    private void initBuyAgain() {
        buyAgainFrame = (TOFocusNoDeepRelativeLayout) findViewById(R.id.order_detail_host_full);
        mBuyCountView = (TextView) buyAgainFrame.findViewById(R.id.take_out_order_detail_order_again);
        // 设置Focus框的空隙
        //buyAgainFrame.setCustomerFocusPaddingRect(new Rect(1, 1, 0, 0));

        // 默认选中
        focusedAccountView(true);
        buyAgainFrame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                focusedAccountView(hasFocus);
            }
        });
        buyAgainFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String shopId = orderDetailAdapter.getOrderInfoDetails().getDetails4StoreInfo().getStoreId();
                Map<String, String> trackMap = new HashMap<>();
                trackMap.put("shop_id", shopId);
                trackMap.put("shop_name", orderDetailAdapter.getOrderInfoDetails().getDetails4StoreInfo().getStoreName());
                trackMap.put("spm",SPMConfig.WAIMAI_ORDER_DETAIL_ONEMORE);
                Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_DETAIL_ONEMORE);
                Utils.utControlHit(TakeOutOrderDetailActivity.this.getFullPageName(),
                        "Page_waimai_OrderDetail_Button_Onemore", trackMap);
                // 再来一单
                if (orderDetailAdapter.getOrderInfoDetails() != null) {
                    List<TakeOutOrderProductInfoBase> productInfoBases = orderDetailAdapter.getOrderInfoDetails().getProductInfoList();
                    JSONArray array = new JSONArray();
                    try {
                        for (TakeOutOrderProductInfoBase productInfoBase : productInfoBases) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("itemId", productInfoBase.getProductId());
                            jsonObject.put("skuId", productInfoBase.getSkuId());
                            jsonObject.put("quantity", productInfoBase.getQuantity());
                            jsonObject.put("itemTitle", productInfoBase.getProductTitle());
                            array.put(jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String par = array.toString();
                    if (!TextUtils.isEmpty(par)) {
                        BusinessRequest.getBusinessRequest().requestTakeOutAgain(shopId, par,
                                new GetBagAgainListener(new WeakReference<>((BaseActivity) TakeOutOrderDetailActivity.this), shopId));
                    }
                }
            }
        });
    }

    public void updateOrderData(TakeOutOrderInfoDetails orderInfoDetails) {
        orderDetailAdapter = new TOOrderDetailAdapter(getApplicationContext());
        orderDetailAdapter.setOrderInfoDetails(orderInfoDetails);

        horizontalListView.setAdapter(orderDetailAdapter);
        horizontalListView.setSpacing(30);

        horizontalListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                int firstVisibleItem = horizontalListView.getFirstVisiblePosition();
                View viewChild = horizontalListView.getChildAt(0);
                int top = (viewChild == null) ? 0 : viewChild.getLeft();
//                if (firstVisibleItem > 0 || top < 0) {
//                    if (scrollCover.getVisibility() != View.VISIBLE) {
//                        //Animation show = AnimationUtils.loadAnimation(TakeOutOrderDetailActivity.this, R.anim.order_detail_cover_visible);
//                        //scrollCover.startAnimation(show);
//                        scrollCover.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    if (scrollCover.getVisibility() != View.GONE) {
//                        //Animation hide = AnimationUtils.loadAnimation(TakeOutOrderDetailActivity.this, R.anim.order_detail_cover_gone);
//                        //scrollCover.startAnimation(hide);
//                        scrollCover.setVisibility(View.GONE);
//                    }
//                }
            }

            @Override
            public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //handleScrollChange();
            }
        });

        TextView storeName = (TextView) findViewById(R.id.take_out_order_detail_store_name);
        storeName.setText(orderInfoDetails.getDetails4StoreInfo().getStoreName());

        TextView total = (TextView) findViewById(R.id.shop_item_total_price);
        total.setText(String.format("￥%.2f", orderInfoDetails.getOrderInfoDetails4Fee().getTotalPrice() / 100.f));

        TextView address = (TextView) findViewById(R.id.take_out_order_detail_address);
        address.setText(orderInfoDetails.getDetails4Address().getFullAddress());

        ImageView storeLogo = (ImageView) findViewById(R.id.take_out_order_detail_store_logo);
        storeLogo.setImageDrawable(null);
        imageLoaderManager.displayImage(
                orderInfoDetails.getDetails4StoreInfo().getStoreLogo(),
                storeLogo, displayImageOptions);

        ((TextView) findViewById(R.id.shop_item_price)).setText(
                String.format("￥%.2f", orderInfoDetails.getOrderInfoDetails4Fee().getActualPaidFee() / 100.f));
        ((TextView) findViewById(R.id.shop_item_count)).setText("共" +
                orderInfoDetails.getProductInfoList().size() + "件商品");
        ((TextView) findViewById(R.id.shop_item_status)).setText(orderInfoDetails.getStatus());

        TextView orderId = (TextView) findViewById(R.id.take_out_order_detail_order_id);
        orderId.setText("订单编号: " + orderInfoDetails.getTbMainOrderId());
        TextView storePhone = (TextView) findViewById(R.id.take_out_order_detail_store_phone);
        storePhone.setText("店铺电话: " + orderInfoDetails.getContactInfo().getStorePhone());
        TextView orderTime = (TextView) findViewById(R.id.take_out_order_detail_order_time);
        orderTime.setText("订单日期: " + orderInfoDetails.getDetails4Trade().getCreateTime());

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.shop_item_extra_fee_detail);
        relativeLayout.removeAllViews();

        int initId = 10, belowId = 0;
        for (int k = 0; k < orderInfoDetails.getOrderInfoDetails4Fee().getFeeDetails().size(); k++) {
            addLeftDesc(initId, belowId, relativeLayout,
                    orderInfoDetails.getOrderInfoDetails4Fee().getFeeDetails().get(k).getFeeContent(),
                    orderInfoDetails.getOrderInfoDetails4Fee().getFeeDetails().get(k).getFee());
            belowId = initId;
            initId++;
        }

        // 自动Focus到商品里面的checked按钮上面，加个消息循环是为了让选中后View的变化完成再进行手动查找
        mHandler.post(new Runnable() {
            public void run() {
                focusPositionManager.requestFocus(buyAgainFrame, View.FOCUS_LEFT);
            }
        });
    }

    private void addLeftDesc(int Id, int below, RelativeLayout parent, String desc, int count) {
        // 参数
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        if (below > 0) {
            layoutParams.addRule(RelativeLayout.BELOW, below);
        }
        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.dp_100), 0);
        TextView textViewDesc = new TextView(this);
        textViewDesc.setId(Id);
//        textViewDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textViewDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.sp_20));
        textViewDesc.setText(desc);
        textViewDesc.setTextColor(getResources().getColor(R.color.white_ffffffff));

        parent.addView(textViewDesc, layoutParams);

        RelativeLayout.LayoutParams layoutParamsV2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsV2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParamsV2.addRule(RelativeLayout.ALIGN_BASELINE, Id);
        TextView textViewCount = new TextView(this);
        textViewCount.setTextColor(getResources().getColor(R.color.white_ffffffff));
//        textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textViewCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.sp_20));
        if (count > 0) {
            textViewCount.setText(String.format("￥%.2f", count / 100.f));
        } else {
            textViewCount.setText(String.format("-￥%.2f", -1 * count / 100.f));
        }

        parent.addView(textViewCount, layoutParamsV2);
    }


    private class GetTakeOutOrderDetailListener extends BizRequestListener<TakeOutOrderInfoDetails> {

        public GetTakeOutOrderDetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(TakeOutOrderInfoDetails data) {
            if (data != null) {
                TakeOutOrderDetailActivity takeOutOrderDetailActivity =
                        (TakeOutOrderDetailActivity) mBaseActivityRef.get();
                if (takeOutOrderDetailActivity != null) {
                    takeOutOrderDetailActivity.updateOrderData(data);
                }
            } else {
                //TODO 线上空白/ErrorView
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 再来一单
     */
    public static class GetBagAgainListener extends BizRequestListener<TakeOutBagAgain> {
        public GetBagAgainListener(WeakReference<BaseActivity> baseActivityRef, String shopId) {
            super(baseActivityRef);
            this.mShopId = shopId;
        }

        String mShopId;

        @Override
        public boolean onError(int resultCode, String msg) {
            if (mBaseActivityRef.get() != null) {
                PromptPop.getInstance(mBaseActivityRef.get()).showPromptWindow(mBaseActivityRef.get().
                        getResources().getString(R.string.order_action_again_fail));
            }
            return true;
        }

        @Override
        public void onSuccess(TakeOutBagAgain data) {
            TakeOutOrderDetailActivity activity = (TakeOutOrderDetailActivity) mBaseActivityRef.get();
            if (activity != null && data != null && data.success) {
                String prefix = "tvtaobao://home?app=takeout&module=takeouthome&shopId=" + mShopId;
                Intent intent = new Intent();
                intent.setData(Uri.parse(prefix));
                intent.putExtra("v_from", activity.v_from + "|order_detail");
                activity.startActivity(intent);
            }else {
                if(activity!=null && data!=null && !TextUtils.isEmpty(data.errorDesc)){
                    PromptPop.getInstance(mBaseActivityRef.get()).showPromptWindow(data.errorDesc);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


}
