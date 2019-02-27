package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBagAgain;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderCancelData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoBase;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderListData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderProductInfoBase;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutOrderDetailActivity;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutOrderListActivity;
import com.yunos.tvtaobao.takeoutbundle.view.PromptPop;
import com.yunos.tvtaobao.takeoutbundle.view.TOInnerFocusHorizontalListView;
import com.yunos.tvtaobao.takeoutbundle.view.TOOrderListItemFocusLayout;
import com.yunos.tvtaobao.takeoutbundle.view.TOOrderListPositionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zifuma on 14/12/2017.
 */
public class TOOrderListAdapter extends BaseAdapter {

    private final String TAG = "TOOrderListAdapter";

    // 商品动作类型
    public enum TO_ORDER_ACTION {
        PAY, CANCEL, DETAIL, DELIVERY, AGAIN, REFUND
    }

    private LayoutInflater mInflater;
    private TakeOutOrderListData orderListData;
    private TOInnerFocusHorizontalListView focusTOHListView;
    private TakeOutOrderListActivity orderListActivity;
    private ImageLoaderManager imageLoaderManager; // 图片加载器
    private DisplayImageOptions displayImageOptions; // 图片加载的参数设置
    private CountDownTimer timer;
    private Handler mHandler = new Handler(); // UI的Handler
    private BusinessRequest businessRequest = BusinessRequest.getBusinessRequest();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // getView() 会被过度调用  获取的 view 不一定被展示到listView上面  无法使用map
    // private SparseArray<TO_Order_HolderView> lstHolders = new SparseArray<>();
    private ArrayList<TO_Order_HolderView> lstHolders = new ArrayList<>();
    private TOOrderListPositionManager toOrderListPositionManager;

    private boolean isActionChangeStatue;
    private TO_Order_HolderView[] needReloadItemHolder = new TO_Order_HolderView[10000];

    public TOOrderListAdapter(TakeOutOrderListActivity activity, TOInnerFocusHorizontalListView listView, TOOrderListPositionManager manager) {
        this.toOrderListPositionManager = manager;
        orderListActivity = activity;
        focusTOHListView = listView;
        mInflater = (LayoutInflater) orderListActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(orderListActivity);
        displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(true).cacheInMemory(true).build();

        startUpdateTimer();
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void startUpdateTimer() {
        timer = new CountDownTimer(20 * 60 * 1000, 1000) {
            /**
             * 屏蔽case： 接口数据延迟 此定时器启动过早 会自动关闭
             */
            long startTime = System.currentTimeMillis();

            @Override
            public void onTick(long millisUntilFinished) {
                boolean goOn = false;
                long currentTime = System.currentTimeMillis();
                for (int i = 0; i < lstHolders.size(); i++) {
                    TO_Order_HolderView holderView = lstHolders.get(i);
                    try {
                        Date date = format.parse(holderView.orderInfoBase.getCreateGMT());
                        long timeDiff = currentTime - date.getTime();
                        holderView.updateTimeRemaining(timeDiff);
                        goOn = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!goOn && (System.currentTimeMillis() - startTime) > 100 * 1000) {
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    /**
     * getCount 的大小直接返回了远程服务器数据大小； 所以 getView可能返回未载入数据的空壳Holder; 在这里重新载入数据;
     *
     * @param data
     */
    public void appendData(TakeOutOrderListData data) {
        if (data != null && data.getOrderInfoBaseList() != null) {
            int firstIndex = orderListData.getOrderInfoBaseList().size();
            int len = data.getOrderInfoBaseList().size();
            orderListData.getOrderInfoBaseList().addAll(data.getOrderInfoBaseList());
            // 重新载入未加载数据的 holder;
            for (int i = 0; i < len; i++) {
                TO_Order_HolderView holderView = needReloadItemHolder[firstIndex + i];
                if (holderView != null && firstIndex + i == holderView.mPostion) {
                    getView(firstIndex + i, holderView.mainView, null);
                }
                needReloadItemHolder[firstIndex + i] = null;
            }
        }
    }

    public void updateData(TakeOutOrderListData data) {
        orderListData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return orderListData.getTotal();
    }

    public int getDataSize() {
        return orderListData.getOrderInfoBaseList().size();
    }

    @Override
    public Object getItem(int position) {
        return orderListData.getOrderInfoBaseList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 将数据展示到HolderView的控件之中
     *
     * @param holderView
     * @param orderInfoBase
     */
    private void showItemData(TO_Order_HolderView holderView, TakeOutOrderInfoBase orderInfoBase) {
        // 商品的图片
        holderView.storeName.setText(orderInfoBase.getStoreName());
        holderView.shopItem.setText(orderInfoBase.getFullProducts());
        holderView.shopItemCount.setText(orderInfoBase.getFullProductsCount());

        holderView.actionBtn1.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));

        holderView.shopTime.setText(orderInfoBase.getCreateGMT());
        holderView.shopStatus.setText(orderInfoBase.getStatus());
        holderView.shopPrice.setText("￥" + String.format("%.2f", orderInfoBase.getTotalFee() / 100f));

        holderView.storeLogoImage.setImageDrawable(null);
        imageLoaderManager.displayImage(
                orderInfoBase.getStoreLogo(),
                holderView.storeLogoImage, displayImageOptions);

        holderView.shopImage.setImageDrawable(null);
        imageLoaderManager.displayImage(
                orderInfoBase.getItemShowPic(),
                holderView.shopImage, displayImageOptions);

        if (orderInfoBase.getStatus().contentEquals("交易关闭") ||
                orderInfoBase.getStatus().contentEquals("等待卖家送餐") ||
                orderInfoBase.getStatus().contentEquals("已送达") ||
                orderInfoBase.getStatus().contentEquals("卖家已接单") ||
                orderInfoBase.getStatus().contentEquals("交易完成")) {
            showTwoAction(holderView, TO_ORDER_ACTION.AGAIN, R.string.order_action_again,
                    TO_ORDER_ACTION.DETAIL, R.string.order_action_detail);
        } else if (orderInfoBase.getStatus().contentEquals("等待买家付款")) {
            //showTwoAction(holderView, TO_ORDER_ACTION.PAY, R.string.order_action_pay, TO_ORDER_ACTION.CANCEL, R.string.order_action_cancel);
            showThreeAction(holderView, TO_ORDER_ACTION.PAY, R.string.order_action_pay, TO_ORDER_ACTION.CANCEL, R.string.order_action_cancel,
                    TO_ORDER_ACTION.DETAIL, R.string.order_action_detail);
        } else if (orderInfoBase.getStatus().contentEquals("配送中")) {
            showThreeAction(holderView, TO_ORDER_ACTION.DELIVERY, R.string.order_action_delivery,
                    TO_ORDER_ACTION.AGAIN, R.string.order_action_again,
                    TO_ORDER_ACTION.DETAIL, R.string.order_action_detail);
        } else { // 其他的所有状态都只显示两项
            showTwoAction(holderView, TO_ORDER_ACTION.AGAIN, R.string.order_action_again,
                    TO_ORDER_ACTION.DETAIL, R.string.order_action_detail);
        }

//         else if (orderInfoBase.getStatus().contentEquals("等待卖家接单")) {
//            showTwoAction(holderView, TO_ORDER_ACTION.CANCEL, R.string.order_action_cancel,
//                    TO_ORDER_ACTION.AGAIN, R.string.order_action_again);
//        }

    }

    private void showThreeAction(TO_Order_HolderView holderView,
                                 TO_ORDER_ACTION action1, int action1Res,
                                 TO_ORDER_ACTION action2, int action2Res,
                                 TO_ORDER_ACTION action3, int action3Res) {
        holderView.splitter.setVisibility(View.VISIBLE);
        holderView.actionBtn3.setVisibility(View.VISIBLE);

        holderView.actionBtn1.setTag(action1);
        holderView.actionBtn2.setTag(action2);
        holderView.actionBtn3.setTag(action3);

        holderView.actionBtn1.setText(action1Res);
        holderView.actionBtn2.setText(action2Res);
        holderView.actionBtn3.setText(action3Res);

        holderView.actionBtn2.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));
        holderView.actionBtn3.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom);
    }

    private void showTwoAction(TO_Order_HolderView holderView, TO_ORDER_ACTION action1, int action1Res,
                               TO_ORDER_ACTION action2, int action2Res) {
        holderView.splitter.setVisibility(View.GONE);
        holderView.actionBtn3.setVisibility(View.GONE);

        holderView.actionBtn1.setTag(action1);
        holderView.actionBtn2.setTag(action2);

        holderView.actionBtn1.setText(action1Res);
        holderView.actionBtn2.setText(action2Res);

        holderView.actionBtn2.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom);
    }

    /**
     * 订单状态行为，查看详情
     *
     * @param mainOrderId
     */
    private void orderDetails(String mainOrderId) {
        AppDebug.e(TAG, "Order details = " + mainOrderId);

        Intent intent = new Intent();
        intent.setClassName(orderListActivity, BaseConfig.SWITCH_TO_TAKEOUT_ORDER_DETAIL_ACTIVITY);
        intent.putExtra("tbmainorderid", mainOrderId);
        intent.putExtra("v_from", orderListActivity.getFrom());
        orderListActivity.startActivity(intent);
    }

    /**
     * 订单状态行为，支付
     *
     * @param infoBase
     */
    public void payOrder(TakeOutOrderInfoBase infoBase) {
        try {
            long currentTime = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(infoBase.getCreateGMT());
            long timeDiff = currentTime - date.getTime();
            if ((timeDiff / 1000) < 900) { // 15 * 60
                orderListActivity.payOrder(infoBase);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 订单状态行为，退单
     *
     * @param infoBase
     */
    private void refundOrder(TakeOutOrderInfoBase infoBase) {

    }

    /**
     * 订单状态行为，查看物流
     *
     * @param infoBase
     */
    private void showDelivery(TakeOutOrderInfoBase infoBase) {
        AppDebug.e(TAG, "Show Delivery = " + infoBase.getTbMainOrderId());
        orderListActivity.showDelivery(infoBase.getTbMainOrderId());
    }

    /**
     * 订单状态行为，再来一单
     *
     * @param infoBase
     */
    private void buyAgain(TakeOutOrderInfoBase infoBase) {
        AppDebug.e("BuyAgain", "Buy again = " + infoBase.getTbMainOrderId());

        String prefix = "tvtaobao://home?app=takeout&module=takeouthome&shopId=" + infoBase.getStoreId() + "&order_again_items=";
        List<TakeOutOrderProductInfoBase> productInfoBases = infoBase.getProductInfoBases();
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


        String shopId = infoBase.getStoreId();
        String par = array.toString();
        if (!TextUtils.isEmpty(par) && !TextUtils.isEmpty(shopId)) {
            BusinessRequest.getBusinessRequest().requestTakeOutAgain(infoBase.getStoreId(), par,
                    new GetBagAgainListener(new WeakReference<>((BaseActivity) orderListActivity), shopId));
        }

//        String url = prefix + array.toString();
//        Intent intent = new Intent();
//        intent.setData(Uri.parse(url));
//
//        AppDebug.e("BuyAgain", "Buy again uri = " + url);
//        orderListActivity.startActivity(intent);
    }

    /**
     * 订单状态行为，取消订单
     *
     * @param mainOrderId
     */
    private void cancelOrder(TO_Order_HolderView holder, String mainOrderId) {
        AppDebug.e(TAG, "Cancel Order = " + mainOrderId);

        businessRequest.cancelTakeOutOrder(mainOrderId,
                new CancelOrderListener(holder, holder.orderInfoBase, new WeakReference<BaseActivity>(orderListActivity)));
    }

    private void actionOrder(final TO_Order_HolderView holder, TextView textView, final TakeOutOrderInfoBase orderData) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TO_ORDER_ACTION toOrderAction = (TO_ORDER_ACTION) v.getTag();
                Map<String, String> trackMap = new HashMap<>();
                trackMap.put("shop_id", orderData.getStoreId());
                trackMap.put("shop_name", orderData.getStoreName());
                switch (toOrderAction) {
                    case PAY:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_PAY);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_PAY);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_Pay", trackMap);
                        payOrder(orderData);
                        break;
                    case AGAIN:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_ONE_MORE);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_ONE_MORE);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_OneMore", trackMap);
                        buyAgain(orderData);
                        break;
                    case CANCEL:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_CANCEL);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_CANCEL);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_Cancel", trackMap);
                        cancelOrder(holder, orderData.getTbMainOrderId());
                        break;
                    case DETAIL:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_DETAIL);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_DETAIL);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_OrderDetail", trackMap);
                        orderDetails(orderData.getTbMainOrderId());
                        break;
                    case DELIVERY:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_LOGISTICS);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_LOGISTICS);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_Logistics", trackMap);
                        showDelivery(orderData);
                        break;
                    case REFUND:
                        trackMap.put("spm", SPMConfig.WAIMAI_ORDER_REFUND);
                        Utils.updateNextPageProperties(SPMConfig.WAIMAI_ORDER_REFUND);
                        Utils.utControlHit(orderListActivity.getFullPageName(),
                                "Page_waimai_Order_Button_Refund", trackMap);
                        refundOrder(orderData);
                        break;
                }
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TO_Order_HolderView holderView;
        if (null == convertView) {
            holderView = new TO_Order_HolderView();
            convertView = mInflater.inflate(R.layout.item_to_order_list, null);
            holderView.mainView = (TOOrderListItemFocusLayout) convertView;
            holderView.storeName = (TextView) convertView.findViewById(R.id.shop_store_name);
            holderView.storeLogoImage = (ImageView) convertView.findViewById(R.id.shop_store_logo_image);
            holderView.shopItem = (TextView) convertView.findViewById(R.id.shop_item_name);
            holderView.shopItemCount = (TextView) convertView.findViewById(R.id.shop_item_count);
            holderView.shopPrice = (TextView) convertView.findViewById(R.id.shop_item_price);
            holderView.shopTime = (TextView) convertView.findViewById(R.id.shop_item_time);
            holderView.shopStatus = (TextView) convertView.findViewById(R.id.shop_item_status);
            holderView.shopImage = (ImageView) convertView.findViewById(R.id.shop_item_image);
            holderView.actionBtn1 = (TextView) convertView.findViewById(R.id.shop_item_first);
            holderView.actionBtn2 = (TextView) convertView.findViewById(R.id.shop_item_second);
            holderView.actionBtn3 = (TextView) convertView.findViewById(R.id.shop_item_third);
            holderView.splitter = convertView.findViewById(R.id.shop_item_second_splitter);

         //   holderView.actionBtn1.setHorizontallyScrolling(true);
         //   holderView.actionBtn1.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            convertView.setTag(holderView);
        } else {
            holderView = (TO_Order_HolderView) convertView.getTag();
            holderView.actionBtn1.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));
        }
        holderView.mPostion = position;

        // todo 如果当前index数据 未获取到  直接返回空壳holder;
        final TakeOutOrderInfoBase orderInfoBase = orderListData != null && orderListData.getOrderInfoBaseList() != null && position >= 0 && orderListData.getOrderInfoBaseList().size() > position
                ? orderListData.getOrderInfoBaseList().get(position) : null;


        if (orderInfoBase == null) {
            if (needReloadItemHolder != null && position >= 0) {
                needReloadItemHolder[position] = holderView;
            }
            return convertView;
        }

        holderView.orderInfoBase = orderInfoBase;
        showItemData(holderView, orderInfoBase);
        if ("等待买家付款".equals(orderInfoBase.getStatus())) {
            if (!lstHolders.contains(holderView)) {
                lstHolders.add(holderView);
            }
        }

        // 设置手动调整focus区域
        TOOrderListItemFocusLayout layout = (TOOrderListItemFocusLayout) convertView;
        layout.setCustomerPaddingRect(new Rect(0, 0, 0, 0));
        // 设置监听方法
        layout.setOnInnerItemSelectedListener(mOnInnerItemSelectedListener);

        layout.setFirstFocusView(holderView.actionBtn1);

        actionOrder(holderView, holderView.actionBtn1, orderInfoBase);
        actionOrder(holderView, holderView.actionBtn2, orderInfoBase);
        actionOrder(holderView, holderView.actionBtn3, orderInfoBase);

        if (!isActionChangeStatue) {
            // 解决重用之前已经放大过的view显示问题
            convertView.setScaleX(1.0f);
            convertView.setScaleY(1.0f);
        }


        // 如果是当前选中的商品就展示选中的UI
        int selected = focusTOHListView.getSelectedItemPosition();
        if (selected == position) {
            selectItemView(true);
        }

        AppDebug.e(TAG, "======== END ======== V V V V " + position);

        return convertView;
    }

    public void selectItemView(boolean selected) {
        if (selected) {
            // 自动Focus到商品里面的checked按钮上面，加个消息循环是为了让选中后View的变化完成再进行手动查找
            mHandler.post(new Runnable() {

                public void run() {
                    focusTOHListView.clearInnerFocusState();
                    focusTOHListView.manualFindFocusInner(KeyEvent.KEYCODE_DPAD_UP);
                }
            });
        }
    }

    /**
     * 内部选中的监听方法
     */
    private InnerFocusLayout.OnInnerItemSelectedListener mOnInnerItemSelectedListener =
            new InnerFocusLayout.OnInnerItemSelectedListener() {
                @Override
                public void onInnerItemSelected(View view, boolean isSelected, View parentView) {
                    AppDebug.i(TAG, "inner selected view=" + view + " isSelected=" + isSelected);
                    // 选中时更新不同的状态
                    int i = view.getId();
                    if (i == R.id.shop_item_first) {
                        TextView checkedView = (TextView) view;
                        if (isSelected) {
                            checkedView.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_highlight_color));
                        } else {
                            checkedView.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));
                        }
                    } else if (i == R.id.shop_item_second) {
                        View vv3 = parentView.findViewById(R.id.shop_item_third);
                        TextView view2 = (TextView) view;
                        if (vv3 != null && vv3.getVisibility() == View.VISIBLE) {
                            if (isSelected) {
                                view2.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_highlight_color));
                            } else {
                                view2.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));
                            }
                        } else {
                            if (isSelected) {
                                view2.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom_highlight);
                            } else {
                                view2.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom);
                            }
                        }
                    } else if (i == R.id.shop_item_third) {
                        TextView view3 = (TextView) view;
                        if (isSelected) {
                            //view3.setBackgroundColor(orderListActivity.getResources().getColor(R.color.ytm_button_focus));
                            view3.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom_highlight);
                        } else {
                            //view3.setBackgroundColor(orderListActivity.getResources().getColor(R.color.to_ol_normal_background));
                            view3.setBackgroundResource(R.drawable.ol_corner_shape_only_bottom);
                        }
                    }
                }
            };

    public class TO_Order_HolderView {
        public TOOrderListItemFocusLayout mainView;
        public ImageView storeLogoImage;
        public TextView storeName;
        public ImageView shopImage;
        public TextView shopItem;
        public TextView shopItemCount;
        public TextView shopPrice;
        public TextView shopTime;
        public TextView shopStatus;

        public TextView actionBtn1;
        public TextView actionBtn2;
        public TextView actionBtn3;
        public View splitter;
        public TakeOutOrderInfoBase orderInfoBase;
        public int mPostion;

        public boolean updateTimeRemaining(long timeDiff) {

            if ("等待买家付款".equals(orderInfoBase.getStatus())) {

                timeDiff = (900 - timeDiff / 1000);
                if (timeDiff >= 0) {
                    int seconds = (int) (timeDiff % 60);
                    int minutes = (int) ((timeDiff / 60) % 60);
                    //int hours = (int) ((timeDiff / (1000 * 60 * 60)) % 24);
                    if (seconds < 10) {
                        actionBtn1.setText("立即支付(剩" + minutes + ":0" + seconds + "自动关闭)");
                    } else {
                        actionBtn1.setText("立即支付(剩" + minutes + ":" + seconds + "自动关闭)");
                    }


                } else {
                    if (orderInfoBase != null) {
                        orderInfoBase.setStatus("交易关闭");
                    }
                    isActionChangeStatue = true;
                    getView(mPostion, mainView, null);
                    isActionChangeStatue = false;

                    if (mPostion == focusTOHListView.getSelectedItemPosition()) {
                        try {
                            toOrderListPositionManager.forceDrawFocus();
                            toOrderListPositionManager.invalidate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }


                return true;
            }

            return false;
        }
    }

    private class CancelOrderListener extends BizRequestListener<TakeOutOrderCancelData> {

        TO_Order_HolderView holderView;

        public CancelOrderListener(TO_Order_HolderView holder, TakeOutOrderInfoBase orderInfoBase, WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            holderView = holder;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            TakeOutOrderListActivity takeOutOrderListActivity =
                    (TakeOutOrderListActivity) mBaseActivityRef.get();
            if (takeOutOrderListActivity != null) {
                takeOutOrderListActivity.showError("取消订单失败");
            }
            return false;
        }

        @Override
        public void onSuccess(TakeOutOrderCancelData data) {
            TakeOutOrderListActivity takeOutOrderListActivity =
                    (TakeOutOrderListActivity) mBaseActivityRef.get();
            if (takeOutOrderListActivity != null) {
                if (data != null && data.isSuccess()) {
                    takeOutOrderListActivity.showError("订单已取消");
                    holderView.orderInfoBase.setStatus("交易关闭");
                    isActionChangeStatue = true;
                    getView(holderView.mPostion, holderView.mainView, null);
                    isActionChangeStatue = false;

                    if (holderView.mPostion == focusTOHListView.getSelectedItemPosition()) {
                        try {
                            toOrderListPositionManager.forceDrawFocus();
                            toOrderListPositionManager.invalidate();
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }

                    //holderView.shopStatus.setText("交易关闭");
                    //holderView.orderInfoBase.setStatus("交易关闭");
                } else {
                    takeOutOrderListActivity.showError(data.getErrorMsg());
                }
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
            TakeOutOrderListActivity activity = (TakeOutOrderListActivity) mBaseActivityRef.get();
            if (activity != null && data != null && data.success) {
                String prefix = "tvtaobao://home?app=takeout&module=takeouthome&shopId=" + mShopId;
                Intent intent = new Intent();
                intent.setData(Uri.parse(prefix));
                intent.putExtra("v_from", activity.getFrom() + "|order_list");
                activity.startActivity(intent);
            } else {
                if (activity != null && data != null && !TextUtils.isEmpty(data.errorDesc)) {
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