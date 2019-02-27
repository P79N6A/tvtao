package com.yunos.tvtaobao.takeoutbundle.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.ViewGroup;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.SkuProp;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.SkuFlowLayoutAdapter;
import com.yunos.tvtaobao.takeoutbundle.adapter.SkuListAdapter;
import com.yunos.tvtaobao.takeoutbundle.listener.OnDialogReturnListener;
import com.yunos.tvtaobao.takeoutbundle.listener.OnSkuValueListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/21.
 *
 * @describe sku选择对话框
 */

public class SkuSelectDialog extends Dialog {
    private final static String TAG = "SkuSelectDialog";
    private final static String skuTAG = "xxx";//用以描述规格
    private final static DecimalFormat format = new DecimalFormat("¥#.##");
    private static volatile SkuSelectDialog skuSelectDialog;
    private static int TYPE_FIRST = 1;
    private static int TYPE_NEXT = 2;
    public OnDialogReturnListener onDialogReturnListener;
    public SkuEngine skuEngine;
    private List<ItemListBean.SkuListBean> skuLists;
    private List<ItemListBean.MultiAttrBean.AttrListBean> attrLists;
    private HashMap<Integer, FlowLayoutManager> flowLayoutManagerHashMap;
    private HashMap<Integer, SkuRecyclerView> skuRecyclerViewHashMap;
    private HashMap<Integer, Object> flowLayoutAdapterHashMap;
    private SkuNumLinearLayout skuNumLinearLayout;
    private MaxNestedScrollView scrollMaxNested;
    private TextView tvItemName, tvItemPrice;
    private BusinessRequest businessRequest;
    private Context context;
    private LinearLayout llSku;
    private Button btnSelected;
    private LinearLayout llSkuTitle;
    private String itemId;
    private String pageName;
    private boolean toNext = true;
    private int outPosition = 0;

    public static SkuSelectDialog getSkuDialogInstance(Context context, String pageName) {
        //TODO 同一家店铺页面被两次打开
//        if (skuSelectDialog == null) {
//            synchronized (SkuSelectDialog.class) {
//                if (skuSelectDialog == null&& context!=null) {
                    skuSelectDialog = new SkuSelectDialog(context);
                    skuSelectDialog.pageName = pageName;
//                }
//            }
//        }
        AppDebug.d(TAG,"skuSelectDialog : "+skuSelectDialog+" , context : "+context);
        return skuSelectDialog;
    }


    public SkuSelectDialog(@NonNull Context context) {
        this(context, R.style.takeout_Dialog_Fullscreen);
    }

    public SkuSelectDialog(@NonNull final Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        setContentView(R.layout.dialog_sku_select);
        Window window = getWindow();
        final WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.0f;
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(params);
        llSku = (LinearLayout) this.findViewById(R.id.ll_sku);
        llSkuTitle = (LinearLayout) this.findViewById(R.id.ll_sku_title);
        btnSelected = (Button) findViewById(R.id.btn_selected);
        skuNumLinearLayout = (SkuNumLinearLayout) this.findViewById(R.id.ll_sku_number);
        tvItemName = (TextView) this.findViewById(R.id.tv_item_name);
        tvItemPrice = (TextView) this.findViewById(R.id.tv_item_price);
        scrollMaxNested = (MaxNestedScrollView) this.findViewById(R.id.scroll_maxNested);
        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skuEngine != null) {
                    if (skuEngine.isSkuDataAllSelected()) {
                        SkuProp skuProp = skuEngine.getSkuProp();
                        String itemId = skuProp.getItemId();
                        String skuId = skuProp.getSkuId();
                        String quantity = "" + skuNumLinearLayout.getCount();
                        String property = "";
                        AppDebug.e(TAG, "skuProp.getPropList  : " + skuProp.getPropList().toString());
                        if (skuProp != null && skuProp.getPropList() != null) {
                            for (int i = 0; i < skuProp.getPropList().size(); i++) {
                                SkuProp.Prop prop = skuProp.getPropList().get(i);
                                //排除第skuList结构项
//                            if (skuProp.isHasSku()) {
                                if (!skuTAG.equals(prop.getName())) {
                                    AppDebug.e(TAG, "remove 规格项目");
                                    if (i == (skuProp.getPropList().size() - 1)) {
                                        property += (prop.getName() + ":" + prop.getValue());
                                    } else {
                                        property += (prop.getName() + ":" + prop.getValue()) + ";";
                                    }
                                }
//                            }
                            }
                        }

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("lifeShopId", skuProp.getShopId());
                            jsonObject.put("skuProperty", property);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String exParams = jsonObject.toString();
                        AppDebug.e(TAG, "itemId = " + itemId + ",skuId = " + skuId + " ,quantity = " + quantity +
                                ", exParams = " + exParams);
                        String cartFrom = "taolife_client";
                        //sku选好了
                        utSkuPropDone();

                        if (businessRequest != null) {
                            businessRequest.requestTakeOutAddBag(itemId, skuId, quantity, exParams,
                                    cartFrom, new GetTakeOutAddBagListener(new WeakReference<>((BaseActivity) context),
                                            skuSelectDialog));
                        }
                    } else {
                        AppDebug.e(TAG, "isSkuDataAllSelected = " + skuEngine.isSkuDataAllSelected());
                    }
                } else {
                    AppDebug.e(TAG, "skuEngine == null ");
                }
            }
        });
    }

    /**
     * 设置浮层数据
     *
     * @param businessRequest
     * @param onDialogReturnListener 浮层回调
     * @param itemListBean           skuItem数据
     */
    public void setSkuData(BusinessRequest businessRequest, OnDialogReturnListener onDialogReturnListener,
                           ItemListBean itemListBean) {
        AppDebug.e(TAG, "setSkuData...........");
        if (itemListBean == null) {
            return;
        }
        tvItemName.getViewTreeObserver().addOnGlobalLayoutListener(skuTitleLayoutListener);
        tvItemPrice.getViewTreeObserver().addOnGlobalLayoutListener(skuTitleLayoutListener);
        this.onDialogReturnListener = onDialogReturnListener;
        this.businessRequest = businessRequest;
        skuLists = itemListBean.getSkuList();
        if (itemListBean.getMultiAttr() != null) {
            attrLists = itemListBean.getMultiAttr().getAttrList();
        }
        flowLayoutManagerHashMap = new HashMap<>();
        flowLayoutAdapterHashMap = new HashMap<>();
        skuRecyclerViewHashMap = new HashMap<>();
        this.itemId = itemListBean.getItemId();
        if (!TextUtils.isEmpty(itemListBean.getPromotioned())) {
            int price = Integer.parseInt(itemListBean.getPromotionPrice());
            String priceText = format.format(price / 100f);
            tvItemPrice.setText(priceText);
        } else {
            if (!TextUtils.isEmpty(itemListBean.getPrice())) {
                int price = Integer.parseInt(itemListBean.getPrice());
                String priceText = format.format(price / 100f);
                tvItemPrice.setText(priceText);
            }
        }
        if (!TextUtils.isEmpty(itemListBean.getTitle())) {
            tvItemName.setText(itemListBean.getTitle());
        }
        if (skuNumLinearLayout != null) {
            skuNumLinearLayout.setCount("1");
            skuNumLinearLayout.setPageNameAddItemId(pageName, itemId);
            if (skuLists == null) {
                if (!TextUtils.isEmpty(itemListBean.getOriginStock())) {
                    skuNumLinearLayout.setQuantity(Integer.parseInt(itemListBean.getOriginStock()));
                } else {
                    skuNumLinearLayout.setQuantity(Integer.MAX_VALUE);
                }
            }
        }
        skuEngine = new SkuEngine(itemListBean.getItemId(), itemListBean.getTitle(), skuLists, attrLists);
        if (!TextUtils.isEmpty(itemListBean.getStoreId())) {
            skuEngine.setStoreId(itemListBean.getStoreId());
        }
        if (skuLists != null) {
            //如果有规格，设置默认最大量
            skuNumLinearLayout.setQuantity(Integer.parseInt(skuLists.get(0).getQuantity()));
            skuEngine.isHasSku(true);
            //父容器
            LinearLayout linearLayout = getLinearLayout();

            //sku名称
            TextView tvSkuName = getTvSkuView(TYPE_FIRST, context.getResources().getString(R.string.sku_first_line_name));
            linearLayout.addView(tvSkuName);

            //sku的value
            final SkuRecyclerView skuValueRy = new SkuRecyclerView(context);
            skuValueRy.addItemDecoration(new SpaceItemDecoration(10));
            int skuValueHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
            int skuValueWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
            LinearLayout.LayoutParams skuValue = new LinearLayout.LayoutParams(skuValueWidth, skuValueHeight);
            skuValueRy.setLayoutParams(skuValue);
            final FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
            skuValueRy.setLayoutManager(flowLayoutManager);
            final SkuListAdapter skuListAdapter = new SkuListAdapter(context, onSkuValueListener,
                    skuLists, pageName, itemId, 0);
            flowLayoutManagerHashMap.put(0, flowLayoutManager);
            flowLayoutAdapterHashMap.put(0, skuListAdapter);
            skuRecyclerViewHashMap.put(0, skuValueRy);
            skuValueRy.setAdapter(skuListAdapter);
            skuValueRy.post(new Runnable() {
                @Override
                public void run() {
                    View view = flowLayoutManager.getFirstView();
                    scrollMaxNested.fullScroll(NestedScrollView.FOCUS_UP);
                    if (view != null) {
                        view.requestFocus();
                    }

                }
            });
            skuListAdapter.setOnItemFocusChangeListener(new SkuListAdapter.OnItemFocusChangeListener() {
                @Override
                public void onItemFocusChange(int outPosition, int position) {
                    toNext = flowLayoutManager.nextLines(position);
                    skuSelectDialog.outPosition = outPosition;

                }
            });
            skuListAdapter.setOnItemSelectedClickListener(new SkuListAdapter.OnItemSelectedClickListener() {
                @Override
                public void onItemSelectedClick(int outPosition, int position) {
                    AppDebug.e(TAG, "skuListAdapter.setOnItemSelectedClickListener.......");
                    jumpNextLine();
                }
            });
            linearLayout.addView(skuValueRy);

            //添加分割线
            View view = getSkuSegmentingLineView();
            linearLayout.addView(view);


            llSku.addView(linearLayout);

        } else {
            skuEngine.isHasSku(false);
        }
        int j = 0;
        if (attrLists != null && attrLists.size() > 0) {
            for (int i = 0; i < attrLists.size(); i++) {
                if (skuEngine.hasSku()) {
                    j = (i + 1);
                } else {
                    j = i;
                }
                //父容器
                LinearLayout linearLayout = getLinearLayout();

                //sku名称
                TextView tvSkuName = getTvSkuView(TYPE_NEXT, attrLists.get(i).getName());
                linearLayout.addView(tvSkuName);

                //sku的value
                SkuRecyclerView skuValueRy = new SkuRecyclerView(context);
                skuValueRy.addItemDecoration(new SpaceItemDecoration(10));
                int skuValueHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                int skuValueWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
                LinearLayout.LayoutParams skuValue = new LinearLayout.LayoutParams(skuValueWidth, skuValueHeight);
                skuValueRy.setLayoutParams(skuValue);
                final FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
                skuValueRy.setLayoutManager(flowLayoutManager);
                final SkuFlowLayoutAdapter flowLayoutAdapter = new SkuFlowLayoutAdapter(context, onSkuValueListener,
                        attrLists.get(i), itemId, pageName, j);
                flowLayoutManagerHashMap.put(j, flowLayoutManager);
                flowLayoutAdapterHashMap.put(j, flowLayoutAdapter);
                skuRecyclerViewHashMap.put(j, skuValueRy);
                skuValueRy.setAdapter(flowLayoutAdapter);
                if (!skuEngine.hasSku()) {
                    if (i == 0) {
                        skuValueRy.post(new Runnable() {
                            @Override
                            public void run() {
                                View view = flowLayoutManager.getFirstView();
                                scrollMaxNested.fullScroll(NestedScrollView.FOCUS_UP);
                                if (view != null) {
                                    view.requestFocus();
                                }

                            }
                        });
                    }
                }
                flowLayoutAdapter.setOnItemFocusChangeListener(new SkuFlowLayoutAdapter.OnItemFocusChangeListener() {
                    @Override
                    public void onItemFocusChange(int outPosition, int position) {
                        skuSelectDialog.outPosition = outPosition;
                        toNext = flowLayoutManager.nextLines(position);

                    }
                });
                flowLayoutAdapter.setOnItemSelectedClickListener(new SkuFlowLayoutAdapter.OnItemSelectedClickListener() {
                    @Override
                    public void onItemSelectedClick(int outPosition, int position) {
                        AppDebug.e(TAG, "flowLayoutAdapter.setOnItemSelectedClickListener...........");
                        jumpNextLine();
                    }
                });
                linearLayout.addView(skuValueRy);

                //添加分割线
                View view = getSkuSegmentingLineView();
                linearLayout.addView(view);

                llSku.addView(linearLayout);
            }
        }
        if (!isShowing()) {
            show();
            for (int i = 0; i < flowLayoutManagerHashMap.size(); i++) {
                flowLayoutManagerHashMap.get(i).getLinesIndex(i);
            }
            utSkuDialogExpose(itemId);
        }
    }


    /**
     * 获取父容器
     *
     * @return
     */
    private LinearLayout getLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(context);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;

    }

    /**
     * 获取属性名称
     *
     * @param name
     * @return
     */
    private TextView getTvSkuView(int type, String name) {
        TextView tvSkuName = new TextView(context);
        int skuNameHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        int skuNameWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams skuNameParams = new LinearLayout.LayoutParams(skuNameWidth, skuNameHeight);
        skuNameParams.leftMargin = (int) context.getResources().getDimension(R.dimen.dp_10);
        if (TYPE_NEXT == type) {
            skuNameParams.topMargin = (int) context.getResources().getDimension(R.dimen.dp_14);
        } else if (TYPE_FIRST == type) {
            skuNameParams.topMargin = (int) context.getResources().getDimension(R.dimen.dp_12);
        }
        tvSkuName.setLayoutParams(skuNameParams);
        int skuSize = (int) context.getResources().getDimension(R.dimen.sp_24);
        tvSkuName.setTextSize(TypedValue.COMPLEX_UNIT_PX, skuSize);
        tvSkuName.setText(name + ":");
        tvSkuName.setTextColor(context.getResources().getColor(R.color.color_8c94a3));
        return tvSkuName;
    }


    /**
     * 获取分割线
     *
     * @return
     */

    private View getSkuSegmentingLineView() {
        View view = new View(context);
        view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lines_sku));
        int lineWidth = (int) context.getResources().getDimension(R.dimen.dp_612);
        int lineHeight = (int) context.getResources().getDimension(R.dimen.dp_1_5);
        LinearLayout.LayoutParams linParams = new LinearLayout.LayoutParams(lineWidth,
                lineHeight);
        linParams.leftMargin = (int) context.getResources().getDimension(R.dimen.dp_10);
        view.setLayoutParams(linParams);
        return view;

    }

    /**
     * 调整文字和价格的位置，以文字优先展示完整
     */

    private ViewTreeObserver.OnGlobalLayoutListener skuTitleLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        int maxLines = 0;
        int children = 0;
        View textName;
        View textPrice;
        int marginLeft = 17;

        @Override
        public void onGlobalLayout() {
            children = 0;
            if (context != null) {
                marginLeft = (int) context.getResources().getDimension(R.dimen.dp_17);
                float dm = DeviceUtil.getDensityFromDevice(context);
                marginLeft = (int) (dm * marginLeft);
            } else {
                marginLeft = (int) (marginLeft * 1.5);
            }
            maxLines = (llSkuTitle.getWidth() - marginLeft);
            for (int i = 0; i < llSkuTitle.getChildCount(); i++) {
                View view = llSkuTitle.getChildAt(i);
                children += view.getWidth();
                if (i == 0) {
                    textName = view;
                } else {
                    textPrice = view;
                }
            }
            if (children > maxLines) {
                int width = maxLines - textPrice.getWidth();
                LinearLayout.LayoutParams textNameParams = new LinearLayout.LayoutParams(
                        width, LinearLayout.LayoutParams.WRAP_CONTENT);
                textName.setLayoutParams(textNameParams);
                LinearLayout.LayoutParams textPriceParams = new LinearLayout.LayoutParams(LinearLayout.
                        LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textPrice.setLayoutParams(textPriceParams);
            }
        }
    };
    /**
     * sku选项监听
     */
    private OnSkuValueListener onSkuValueListener = new OnSkuValueListener() {

        @Override
        public void addSelectSkuData(String skuId, String skuName, String skuValue) {
            skuEngine.addSelectSkuData(skuId, skuName, skuValue);
        }

        @Override
        public void removeSelectedView(String skuName, String sku) {
            skuEngine.removeSelectedView(skuName, sku);

        }

        @Override
        public void updatePriceQuantity(String price, String quantity) {
            AppDebug.e(TAG, "updatePriceQuantity.price = " + price + ",quantity = " + quantity);
            int priceInt = Integer.parseInt(price);
            String priceText = format.format(priceInt / 100f);
            tvItemPrice.setText(priceText);
            skuNumLinearLayout.setQuantity(Integer.parseInt(quantity));
        }
    };


    /**
     * 跳转到下一行，聚焦在第一个有效的sku值
     */
    private void jumpNextLine() {
        final int nextPosition = (outPosition + 1);
        AppDebug.e(TAG, "jumpNextLine  outPosition = " + outPosition + ",flowLayoutAdapterHashMap.size()  = " +
                flowLayoutAdapterHashMap.size() + ", nextPosition = " + nextPosition);
        //判断下一行是否存在
        if (flowLayoutAdapterHashMap.size() > nextPosition) {
            SkuRecyclerView skuRecyclerView = skuRecyclerViewHashMap.get(nextPosition);
            if (skuRecyclerView != null) {
                AppDebug.e(TAG, "skuRecyclerView!=null");
                //跳转焦点
                skuRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        AppDebug.e(TAG, " skuRecyclerView.post........");
                        if (flowLayoutManagerHashMap == null) {
                            AppDebug.e(TAG, "(flowLayoutManagerHashMap == null");
                            return;
                        }
                        View view = flowLayoutManagerHashMap.get(nextPosition).getFirstView();
                        if (view != null) {
                            AppDebug.e(TAG, "nextPosition = " + nextPosition + ", view = " + view.getId());
                            view.requestFocus();
                            view.post(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }
                });
            } else {
                AppDebug.e(TAG, "skuRecyclerView==null");
            }
        } else {
            if (skuNumLinearLayout != null) {
                skuNumLinearLayout.requestSkuNumFocus();
            }
        }
    }


    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (skuNumLinearLayout != null && skuNumLinearLayout.hasSkuNumLinearFocus()) {
                    scrollMaxNested.fullScroll(NestedScrollView.FOCUS_DOWN);
                    btnSelected.requestFocus();
                }
                if (flowLayoutAdapterHashMap != null && flowLayoutAdapterHashMap.get(outPosition) != null) {
                    AppDebug.e(TAG, "toNext = " + toNext + ",outPosition = " + outPosition);
                    if (flowLayoutAdapterHashMap.get(outPosition).getClass().equals(SkuListAdapter.class)) {
                        SkuListAdapter skuListAdapter = (SkuListAdapter) flowLayoutAdapterHashMap.get(outPosition);
                        if (!skuListAdapter.isSelected()) {
                            AppDebug.e(TAG, "skuListAdapter.isSelected().......");
                            if (!toNext) {
                                return true;
                            }
                        }
                    } else if (flowLayoutManagerHashMap != null && flowLayoutAdapterHashMap.get(outPosition).getClass()
                            .equals(SkuFlowLayoutAdapter.class)) {
                        SkuFlowLayoutAdapter skuFlowLayoutAdapter = (SkuFlowLayoutAdapter) flowLayoutAdapterHashMap.get(outPosition);
                        if (!skuFlowLayoutAdapter.isSelected()) {
                            AppDebug.e(TAG, "skuFlowLayoutAdapter.isSelected().......");
                            if (!toNext) {
                                return true;
                            }
                        }

                    }

                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void dismiss() {
        flowLayoutManagerHashMap = null;
        llSku.removeAllViews();
        llSku.clearFocus();
        utSkuPropCancel();
        LinearLayout.LayoutParams textPriceParams = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvItemPrice.getViewTreeObserver().removeOnGlobalLayoutListener(skuTitleLayoutListener);
        } else {
            tvItemPrice.getViewTreeObserver().removeGlobalOnLayoutListener(skuTitleLayoutListener);
        }
        tvItemPrice.setLayoutParams(textPriceParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvItemName.getViewTreeObserver().removeOnGlobalLayoutListener(skuTitleLayoutListener);
        } else {
            tvItemName.getViewTreeObserver().removeGlobalOnLayoutListener(skuTitleLayoutListener);
        }
        tvItemName.setLayoutParams(textPriceParams);

        super.dismiss();
    }

    /**
     * 规格浮层取消事件
     */
    private void utSkuPropCancel() {
        Map<String, String> properties = Utils.getProperties();
        if (!TextUtils.isEmpty(itemId)) {
            properties.put("item id", itemId);
        }

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        Utils.utCustomHit(pageName, "Page_waimai_shop_grant_sku_key_back", properties);
    }

    /**
     * 规格选择浮层曝光事件
     *
     * @param itemId
     */
    private void utSkuDialogExpose(String itemId) {
        Map<String, String> properties = Utils.getProperties();
        if (TextUtils.isEmpty(itemId)) {
            properties.put("item id", itemId);
        }

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_EXPOSE);

        Utils.utCustomHit(pageName, "Expose_waimai_shop_grant_sku", properties);
    }

    /**
     * sku选好点击事件
     */

    private void utSkuPropDone() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("item id", itemId);

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_DONE);

        Utils.utControlHit(pageName, "Page_waimai_shop_grant_sku_button_done", properties);
    }


    /**
     * 加入购物车回调
     */

    public static class GetTakeOutAddBagListener extends BizRequestListener<AddBagBo> {
        public SkuSelectDialog skuSelectDialog;

        public GetTakeOutAddBagListener(WeakReference<BaseActivity> baseActivityRef, SkuSelectDialog skuSelectDialog) {
            super(baseActivityRef);
            this.skuSelectDialog = skuSelectDialog;

        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "GetTakeOutAddBagListener.onError resultCode = " + resultCode + ",msg = " + msg);
            skuSelectDialog.onDialogReturnListener.addBagFailure(resultCode, msg);
            skuSelectDialog.dismiss();
            return true;
        }

        @Override
        public void onSuccess(AddBagBo data) {
            if (skuSelectDialog != null && skuSelectDialog.onDialogReturnListener != null) {
                AppDebug.e(TAG, "skuSelectDialog  skuEngine is not null");
                skuSelectDialog.onDialogReturnListener.addBagSuccess();
                skuSelectDialog.dismiss();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    public void destroySkuDialog() {
        if (context != null) {
            context = null;
        }
        if (skuEngine != null) {
            skuEngine = null;
        }
        if (onDialogReturnListener != null) {
            onDialogReturnListener = null;
        }
        if (flowLayoutManagerHashMap != null) {
            flowLayoutManagerHashMap = null;
        }
        if (flowLayoutAdapterHashMap != null) {
            flowLayoutAdapterHashMap = null;
        }
        if (skuNumLinearLayout != null) {
            skuNumLinearLayout = null;
        }
        if (skuLists != null) {
            skuLists = null;
        }
        if (attrLists != null) {
            attrLists = null;
        }
        if (skuSelectDialog != null) {
            skuSelectDialog = null;
        }
        if (businessRequest != null) {
            businessRequest = null;
        }
    }


}
