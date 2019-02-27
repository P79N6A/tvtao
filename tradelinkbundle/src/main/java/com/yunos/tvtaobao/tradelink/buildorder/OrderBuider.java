package com.yunos.tvtaobao.tradelink.buildorder;


import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentStatus;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentTag;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.LabelComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.MultiSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectOption;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.ToggleComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ActivityComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.AddressComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderBondComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.QuantityComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.RealPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.RootComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.SubmitOrderComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.TaxInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.engine.BuyEngine;
import com.taobao.wireless.trade.mbuy.sdk.engine.ExpandParseRule;
import com.taobao.wireless.trade.mbuy.sdk.engine.SplitJoinRule;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.GoodsDisplayInfo;
import com.yunos.tvtaobao.tradelink.buildorder.component.GoodsSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderBondSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderBuider {

    private final static String TAG = "OrderBuider";

    public BaseActivity mMainBaseActivity;

    // 确认下单商品信息的合并规则类
    private BuildorderSplitJoinRule mBuildorderSplitJoinRule;

    private SplitJoinRule mTestRule;

    // 确认下单的引擎
    private BuyEngine mBuyEngine;

    int foregroundColor = 0xffff6c0f;

    // 支持的组件列表
    private List<Integer> vaildComponentTypeList;

    //当为true时,左侧展示商品标题等信息,为false时展示店铺信息
    public boolean onlyOneItem;

    //当为true时,确认下单按钮有效， 为false时不能下单
    public boolean submitOrderOk;

    public boolean mHaveValidGoods;

    public boolean mVaildOrder;

    public boolean prePay = false;

    // 如果当前订单中只有一个商品，那么保存当前商品的ITEM信息
    public ItemComponent mItemComponent_Single;

    public AddressComponent mAddressComponent;

    // 存储小票显示的信息，包括埋点
    public final GoodsDisplayInfo mGoodsDisplayInfo;

    // 是否有店铺优惠
    public boolean isHasShopPromotion;

    public OrderBuider(BaseActivity mainBaseActivity, BuyEngine buyEngine) {

        mMainBaseActivity = mainBaseActivity;

        mBuyEngine = buyEngine;
        mBuildorderSplitJoinRule = new BuildorderSplitJoinRule();

        mGoodsDisplayInfo = new GoodsDisplayInfo();
        initValue();

        registerParseRule();

        // 注册合并规则
        registerSplitJoinRule();

        // 添加支持的组件
        initCompentType();
    }

    /**
     * 初始化参数变量值
     */
    private void initValue() {

        vaildComponentTypeList = new ArrayList<Integer>();
        vaildComponentTypeList.clear();

        onlyOneItem = true;
        mHaveValidGoods = true;
        mItemComponent_Single = null;
    }

    private void registerParseRule() {
//        mBuyEngine.registerExpandParseRule();
    }

    /**
     * 初始化支持的类型
     */
    private void initCompentType() {
        vaildComponentTypeList.add(PurchaseViewType.SELECT.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.TOGGLE.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.COUPON.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.DELIVERY_METHOD.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.VOUCHER.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.TERMS.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.INSTALLMENT_TOGGLE.getIndex());
        vaildComponentTypeList.add(PurchaseViewType.INSTALLMENT_PICKER.getIndex());
//        vaildComponentTypeList.add(PurchaseViewType.INSTALLMENT.getIndex());
//        vaildComponentTypeList.add(PurchaseViewType.CARD_PROMOTION.getIndex());
//        vaildComponentTypeList.add(PurchaseViewType.INSTALLMENT_TOGGLE.getIndex());
//        vaildComponentTypeList.add(PurchaseViewType.INPUT.getIndex());
    }

    /**
     * 注册合并规则
     * <p>
     * "orderBond_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d": [
     * "orderInfo_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "order_cad843c2e3cce2459dfe0bcb69a4cc6c",
     * "order_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "promotion_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "cardPromotion_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "orderPay_cad843c2e3cce2459d fe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d"
     * ],
     */
    public void registerSplitJoinRule() {
        AppDebug.i(TAG, "onRegisterSplitJoinRule --->");
        mBuyEngine.registerSplitJoinRule(ComponentTag.ITEM, mBuildorderSplitJoinRule);
        mBuyEngine.registerSplitJoinRule(ComponentTag.ORDER, new OrderSplitJoinRule());
        mBuyEngine.registerSplitJoinRule(ComponentTag.ORDER_BOND, new OrderBondSplitJoinRule());


    }

    /**
     * 反注册
     */

    public void unregisterSplitJoinRule() {
        mBuyEngine.registerSplitJoinRule(ComponentTag.ITEM, null);
        mBuyEngine.registerSplitJoinRule(ComponentTag.ORDER, null);
        mBuyEngine.registerSplitJoinRule(ComponentTag.ORDER_BOND, null);
    }

    public GoodsDisplayInfo getGoodsDisplayInfo() {
        return mGoodsDisplayInfo;
    }

    private boolean doubleCheck(Component component) {
        if (component instanceof DeliveryMethodComponent)
            return false;
        if ("installmentAdaptor".equals(component.getTag()))
            return false;
        if (component instanceof InstallmentPickerComponent) {//支持installmentPicker
            InstallmentPickerComponent installmentComponent = (InstallmentPickerComponent) component;
            mGoodsDisplayInfo.setInstallMentPay(false);
            for (InstallmentPickerComponent.OrderInstallmentPicker picker : installmentComponent.getDetails()) {
                if (picker.getOrderPrice() > 0) {
                    mGoodsDisplayInfo.setInstallMentPay(true);
                    break;
                }
            }
            return true;
        }
        if (component instanceof SelectComponent) {
            if ("promotion".equals(component.getTag()) && (component.getParent() instanceof ItemComponent || component.getParent() instanceof OrderComponent)) {
                return false;
            }
            if ("mnj1Promotion".equals(component.getTag()))
                return false;
            if ("invoice".equals(component.getTag())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断组件是否支持的业务需求
     *
     * @param component
     * @return
     */
    private boolean isVaild(Component component) {

        ComponentType type = component.getType();
        ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());
        String tag_string = component.getTag();

        if (type == ComponentType.INPUT && tag == ComponentTag.MEMO) {
            //给卖家留言
            return false;
        } else if (type == ComponentType.TOGGLE && tag == ComponentTag.AGENCY_PAY) {
            //找朋友代付
            return false;
        } else if (type == ComponentType.TOGGLE && TextUtils.equals("tmallBao", tag_string)) {
            // 天猫宝
            return false;
        }

        return true;
    }

    /**
     * 针对数字，替换为指定的颜色
     *
     * @param src    原来的文字内容
     * @param change 是否需要改为指定的颜色
     * @return
     */
    private SpannableStringBuilder onHandlerSpanned(String src, boolean change) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        if (change) {
            Pattern p = Pattern.compile("\\d+");
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
                if (end < src.length()) {
                    end += 1; // 带上单位, 或者小数点
                }
                style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        return style;
    }

    /**
     * 解析信息
     *
     * @param json
     * @return
     */
    public boolean parseComponents(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        List<Component> components = mBuyEngine.parse(JSON.parseObject(json));
        if (components == null || components.isEmpty()) {
            return false;
        }

        filterComponents(components);
        fillGoodsInfoComponents();
        return true;
    }

    /**
     * 过滤组件列表,只显示支持的组件
     * modification:
     * 在此处加入88会员优惠的汇总
     *
     * @param components
     * @return
     */
    public List<Component> filterComponents(List<Component> components) {
        mGoodsDisplayInfo.setInstallMentPay(false);
        List<Component> adapterComponents = new ArrayList<Component>();
        int itemInfoCount = 0;
        List<RichSelectComponent> cardPromotionList = new ArrayList<>();
        for (Component component : components) {
            int index = PurchaseViewFactory.getItemViewType(component);
            if (PurchaseViewType.CARD_PROMOTION.getIndex() == index && component instanceof RichSelectComponent) {
                RichSelectComponent richSelectComponent = (RichSelectComponent) component;
                if ("88VIP专享".equals(richSelectComponent.getTitle())) {
                    cardPromotionList.add(richSelectComponent);
                }
            }
            if (PurchaseViewType.ADDRESS.getIndex() == index) {
                // 保存地址
                mAddressComponent = (AddressComponent) component;
                AppDebug.d(TAG, "filterComponents ---> mAddressComponent id = " + mAddressComponent.getId());
            }

            AppDebug.d(TAG, "filterComponents ---> status = " + component.getStatus() + "; contains = " + vaildComponentTypeList.contains(index) + "; isVaild(component) = " + isVaild(component) + "; componentTag = " + component.getTag() + "; " + component.getType() + "; " + component.getFields());

            if (vaildComponentTypeList.contains(index) && component.getStatus() != ComponentStatus.HIDDEN
                    && isVaild(component) && doubleCheck(component)) {
                adapterComponents.add(component);
            }

            if (PurchaseViewType.ITEM_INFO.getIndex() == index) {
                if (component.getParent() != null && component.getParent().getParent() != null && !(component.getParent().getParent() instanceof ActivityComponent))
                    itemInfoCount++;
            }

        }

        if (itemInfoCount > 1) {
            onlyOneItem = false;
        } else {
            onlyOneItem = true;
        }

        if (cardPromotionList.size() > 0) {
            Vip88CardComponent vip88CardComponent = new Vip88CardComponent();
            for (RichSelectComponent richSelectComponent : cardPromotionList) {
                Component parentComponent = richSelectComponent.getParent();
                for (Component component : components) {
                    if (component instanceof OrderBondSyntheticComponent) {
                        if (((OrderBondSyntheticComponent) component).orderBondComponent == parentComponent) {
                            vip88CardComponent.addComponent(richSelectComponent, ((OrderBondSyntheticComponent) component).orderInfoComponent);
                            break;
                        }
                    } else if (component instanceof OrderSyntheticComponent) {
                        if (((OrderSyntheticComponent) component).getOrderInfoComponent() == null)
                            continue;
                        if (((OrderSyntheticComponent) component).getOrderComponent() == parentComponent) {
                            vip88CardComponent.addComponent(richSelectComponent, ((OrderSyntheticComponent) component).getOrderInfoComponent());
                            break;
                        }
                    }
                    continue;
                }
            }
            adapterComponents.add(0, vip88CardComponent);
            Map<String, String> params = Utils.getProperties();
            params.put("disc_name", "88vip");
            params.put("is_prebuy", "" + prePay);
            Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
        }
        doSecondFilter(adapterComponents);//此前计算时onlyitem值仍未计算出来，故此处再遍历一遍。。。

        return adapterComponents;
    }

    private void doSecondFilter(List<Component> components) {
        Iterator<Component> iterator = components.iterator();
        while (iterator.hasNext()) {
            if (!doubleCheck(iterator.next())) {
                iterator.remove();
            }
        }
    }

    /**
     * 判断字符串是否跟0相等
     *
     * @param src
     * @return
     */
    private boolean isEqualZero(String src) {
        boolean equal = false;
        if (!TextUtils.isEmpty(src)) {
            if (TextUtils.equals(src, "0.00") || TextUtils.equals(src, "0.0") || TextUtils.equals(src, "0")) {
                equal = true;
            }
        } else {
            // 如果src是空的，那么默认为相等
            equal = true;
        }
        AppDebug.i(TAG, "isEqualZero --> src = " + src + "; equal = " + equal);
        return equal;
    }

    /**
     * 填充左侧需要的信息
     */
    public void fillGoodsInfoComponents() {
        List<Component> outComponent = mBuyEngine.getContext().getOutput();

        AppDebug.i(TAG, "fillGoodsInfoComponents --> outComponent = " + outComponent);

        // 初始化左侧内容的显示位置
        int listlocation = 1;
        if (onlyOneItem) {
            listlocation = 2;
        }
        mGoodsDisplayInfo.getInfoList().clear();

        // 无效商品
        SparseArray<String> mInvalidGoods = mGoodsDisplayInfo.getInvalidGoods();
        mInvalidGoods.clear();

        // 有效商品
        SparseArray<String> mValidGoods = mGoodsDisplayInfo.getValidGoods();
        mValidGoods.clear();

        StringBuilder itemIdsBuilder = new StringBuilder();
        StringBuilder itemNamesBuilder = new StringBuilder();
        itemIdsBuilder.setLength(0);
        // 默认为都是无效的商品
        mHaveValidGoods = false;

        // 默认为支持下单
        mVaildOrder = true;
        prePay = false;

        for (Component component : outComponent) {
            if (component == null) {
                continue;
            }
//            if (component instanceof OrderBondComponent) {
//                mVaildOrder = false;
//                return;
//            }
            ComponentType type = component.getType();
            ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());

            AppDebug.i(TAG, "fillGoodsInfoComponents --> tag = " + tag + "; type = " + type + "; onlyOneItem = "
                    + onlyOneItem);

            switch (type) {
                case TABLE:
                    String tagvalue = component.getTag();
                    AppDebug.i(TAG, "fillGoodsInfoComponents --> tagvalue = " + tagvalue + "; type = " + type
                            + "; onlyOneItem = " + onlyOneItem);
                    if (TextUtils.equals(tagvalue, "stepPay")) {
                        // 单个商品，如果是分期付款的商品，那么弹出二维码界面！在TV端不支持下单
                        // 把有效商品的标志设为false, 并且退出检查
                        prePay = true;
//                        return;
                    }
                    break;
                case LABEL:
                    switch (tag) {
                        case PROMOTION:
                            LabelComponent labelComponent = (LabelComponent) component;
                            String value = labelComponent.getValue();
                            if (!TextUtils.isEmpty(value)) {
                                String shopDiscount = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_shopdiscount);
                                int index = value.indexOf(shopDiscount, 0);
                                if (index >= 0) {
                                    //  只要店铺优惠的数据  
                                    SpannableStringBuilder valuespan = null;
                                    if (isEqualZero(labelComponent.getData().getString("quark"))) {
                                        // 如果等于0，那么截取要显示的内容
                                        String content = value.substring(shopDiscount.length());
                                        if (!TextUtils.isEmpty(content)) {
                                            // 去掉两边的空格
                                            content = content.trim();
                                            valuespan = onHandlerSpanned(content, false);
                                        }
                                    } else {
                                        String youhui = mMainBaseActivity.getResources().getString(
                                                R.string.ytm_buildorder_youhui);
                                        valuespan = onHandlerSpanned(labelComponent.getData().getString("quark") + youhui, true);
                                    }
                                    if (valuespan != null) {
                                        isHasShopPromotion = true;
                                        String discount = mMainBaseActivity.getResources().getString(
                                                R.string.ytm_buildorder_discount);
                                        mGoodsDisplayInfo.setPutInfo(listlocation, discount, valuespan);
                                        listlocation++;
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;

                case SELECT:
                    if (TextUtils.equals(component.getTag(), "voucher")) {
                        SelectComponent selectComponent = (SelectComponent) component;
                        if (selectComponent != null) {
                            SelectOption option = selectComponent.getSelectedOption();
                            SpannableStringBuilder valuespan = null;
                            if (option != null) {
                                String tmallCoupon = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_tmall_coupon);

                                String value = option.getPrice();
                                if (!TextUtils.isEmpty(value) && !isEqualZero(value)) {
                                    // 不是空字符，并且值不是0，那么显示处理
                                    try {
                                        int vulue_int = Integer.parseInt(value);
                                        float vulue_float = ((float) vulue_int / 100.0f);
                                        String yuan = mMainBaseActivity.getResources().getString(
                                                R.string.ytm_buildorder_youhui);
                                        String value_str = String.valueOf(vulue_float);
                                        if (vulue_int > 0) {
                                            value_str = "-" + value_str;
                                        }
                                        valuespan = onHandlerSpanned(value_str + yuan, true);

                                        if (valuespan != null) {
                                            mGoodsDisplayInfo.setPutInfo(listlocation, tmallCoupon, valuespan);
                                            listlocation++;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                    break;

                case TOGGLE:
                    switch (tag) {
                        case TMALL_POINT:
                            ToggleComponent toggleComponent = (ToggleComponent) component;
                            String value = toggleComponent.getData().getString("quark");
                            if (!TextUtils.isEmpty(value) && toggleComponent.isChecked()) {
                                String shopDiscount = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_tianmaojifen);
                                double tamll_d = 0.0f;
                                try {
                                    tamll_d = Double.parseDouble(value);
                                } catch (Exception e) {
                                }
                                int tamll = (int) (Math.abs(tamll_d) * 100.0);
                                String src = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_jifenshiyong, tamll_d, tamll);
                                SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                mGoodsDisplayInfo.setPutInfo(listlocation, shopDiscount, valuespan);
                                listlocation++;
                            }
                            break;

                        case TBGOLD:
                            // 小票处显示淘金币
                            ToggleComponent toggleComponent_tbgold = (ToggleComponent) component;
                            String value_tbgold = toggleComponent_tbgold.getData().getString("quark");
                            if (!TextUtils.isEmpty(value_tbgold) && toggleComponent_tbgold.isChecked()) {
                                String shopDiscount = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_taojinbi);
                                double taojinbi_d = 0.0f;
                                try {
                                    taojinbi_d = Double.parseDouble(value_tbgold);
                                } catch (Exception e) {
                                }
                                int taojinbi = (int) (Math.abs(taojinbi_d) * 100.0);
                                String src = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_taojinbishiyong, taojinbi_d, taojinbi);
                                SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                mGoodsDisplayInfo.setPutInfo(listlocation, shopDiscount, valuespan);
                                listlocation++;
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case BIZ:
                    switch (tag) {
                        case ADDRESS:
                            // 保存地址
                            mAddressComponent = (AddressComponent) component;
                            break;

                        case DELIVERY_METHOD:
                            DeliveryMethodComponent deliveryMethodComponent = (DeliveryMethodComponent) component;
                            String selectedId = deliveryMethodComponent.getSelectedId();
                            if (!TextUtils.isEmpty(selectedId)) {
                                String yuan = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_unit_yuan);
                                SpannableStringBuilder value = onHandlerSpanned(
                                        deliveryMethodComponent.getOptionById(selectedId).getPrice() + yuan, true);
                                String peisong = deliveryMethodComponent.getTitle();
                                String peisongfangshi = mMainBaseActivity.getResources().getString(
                                        R.string.ytm_buildorder_peisongfangshi);
                                int pos = peisong.indexOf(peisongfangshi);
                                if (pos >= 0) {
                                    String yunfei = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_yunfei);
                                    mGoodsDisplayInfo.setPutInfo(listlocation, yunfei, value);
                                } else {
                                    mGoodsDisplayInfo.setPutInfo(listlocation, peisong, value);
                                }
                                listlocation++;
                            }
                            break;

                        case ORDER_INFO:
                            OrderInfoComponent orderInfoComponent = (OrderInfoComponent) component;
                            if (!onlyOneItem) {
                                mGoodsDisplayInfo.setTitle(orderInfoComponent.getTitle());
                                mGoodsDisplayInfo.setSku(null);
                            }
                            mGoodsDisplayInfo.setShopName(orderInfoComponent.getTitle());
                            break;

                        case REAL_PAY:
                            RealPayComponent realPayComponent = (RealPayComponent) component;
                            mGoodsDisplayInfo.setTotalPrice(realPayComponent.getPrice());
                            String jian = mMainBaseActivity.getResources().getString(R.string.ytm_buildorder_unit_jian);
                            SpannableStringBuilder quantity = onHandlerSpanned(realPayComponent.getQuantity() + jian,
                                    false);

                            String shuliang = mMainBaseActivity.getResources().getString(
                                    R.string.ytm_buildorder_quantity);
                            if (onlyOneItem) {
                                mGoodsDisplayInfo.setPutInfo(1, shuliang, quantity);
                            } else {
                                mGoodsDisplayInfo.setPutInfo(0, shuliang, quantity);
                            }
                            break;

                        case SUBMIT_ORDER:
                            SubmitOrderComponent submitOrderComponent = (SubmitOrderComponent) component;
                            ComponentStatus status = submitOrderComponent.getStatus();
                            if (status == ComponentStatus.DISABLE) {
                                submitOrderOk = false;
                            } else {
                                submitOrderOk = true;
                            }
                            break;
                        case ORDER_PAY:
                            OrderPayComponent orderPayComponent = (OrderPayComponent) component;
                            if (orderPayComponent != null && !TextUtils.isEmpty(orderPayComponent.getWeight())) {
                                SpannableStringBuilder weight = onHandlerSpanned(orderPayComponent.getWeight() + "kg",
                                        false);
                                if (onlyOneItem) {
                                    mGoodsDisplayInfo.setPutInfo(3, "总重", weight);
                                } else {
                                    mGoodsDisplayInfo.setPutInfo(2, "总重", weight);
                                }
                            }

                            break;
                        case ROOT:
                            RootComponent rootComponent = (RootComponent) component;
                            break;
                        default:
                            break;
                    }
                    break;

                case SYNTHETIC:
                    if (component instanceof GoodsSyntheticComponent) {
                        GoodsSyntheticComponent buildorderSyntheticComponent = (GoodsSyntheticComponent) component;
                        ItemInfoComponent itemInfoComponent = buildorderSyntheticComponent.getItemInfoComponent();
                        ItemPayComponent itemPayComponent = buildorderSyntheticComponent.getItemPayComponent();
                        ItemComponent itemComponent = buildorderSyntheticComponent.getItemComponent();
                        if (!itemComponent.isValid()) {
                            int size = mInvalidGoods.size();
                            String text = null;
                            if (size <= 0) {
                                text = mMainBaseActivity.getResources().getString(R.string.ytm_buildorder_invalid);
                                mGoodsDisplayInfo.setPutInvalidGoods(0, text);
                            }
                            size = mInvalidGoods.size();
                            text = itemInfoComponent.getTitle();
                            mGoodsDisplayInfo.setPutInvalidGoods(size, text);

                            if (onlyOneItem) {
                                // 如果是单个商品，那么保留当前无效的信息
                                mItemComponent_Single = itemComponent;
                            }
                        } else {
                            mHaveValidGoods = true;
                            int size = mValidGoods.size();
                            String text = itemInfoComponent.getTitle();
                            mGoodsDisplayInfo.setPutValidGoods(size, text);
                        }
                        // 保存商品的ID号，相同的商品ID号，只保存一个，
                        String itemIds = itemIdsBuilder.toString();
                        if (!TextUtils.isEmpty(itemComponent.getItemId()) && !itemIds.contains(itemComponent.getItemId())) {
                            itemIdsBuilder.append(itemComponent.getItemId());
                            itemIdsBuilder.append(",");
                        }

                        String itemNames = itemNamesBuilder.toString();
                        if (!TextUtils.isEmpty(itemInfoComponent.getTitle()) && !itemNames.contains(itemInfoComponent.getTitle())) {
                            itemNamesBuilder.append(itemInfoComponent.getTitle());
                            itemNamesBuilder.append(",");
                        }

                        if (onlyOneItem) {
                            mGoodsDisplayInfo.setTitle(itemInfoComponent.getTitle());
                            mGoodsDisplayInfo.setSku(itemInfoComponent.getSkuInfo());
                            String yuan = mMainBaseActivity.getResources().getString(R.string.ytm_buildorder_unit_yuan);
                            SpannableStringBuilder price = onHandlerSpanned(itemPayComponent.getAfterPromotionPrice()
                                    + yuan, true);
                            String danjia = mMainBaseActivity.getResources().getString(R.string.ytm_buildorder_price);
                            mGoodsDisplayInfo.setPutInfo(0, danjia, price);
                        }
                    } else if (component instanceof OrderBondSyntheticComponent) {
                        OrderBondSyntheticComponent bondSyntheticComponent = (OrderBondSyntheticComponent) component;
                        for (OrderSyntheticComponent orderComponent : bondSyntheticComponent.orders) {
                            AppDebug.d("test", orderComponent.toString());
                        }
                    }
                    break;

                default:
                    break;

            }
        }
        // 组装全部商品的ID
        mGoodsDisplayInfo.setmItemIdTbs(itemIdsBuilder.toString());
        mGoodsDisplayInfo.setItemNames(itemNamesBuilder.toString());
    }

    /**
     * 从地址组件中获得地址的ID
     *
     * @return
     */
    public String getComponentSelectedAddress() {
        String addr = null;
        if (mAddressComponent != null) {
            addr = mAddressComponent.getSelectedId();
        }
        return addr;
    }

    /**
     * 确认下单界面中商品信息的合并规则类
     */
    private static class BuildorderSplitJoinRule implements SplitJoinRule {

        @Override
        public List<Component> execute(List<Component> input) {

            GoodsSyntheticComponent syntheticComponent = new GoodsSyntheticComponent();
            for (Component component : input) {
                switch (ComponentTag.getComponentTagByDesc(component.getTag())) {
                    case ITEM:
                        syntheticComponent.setItemComponent((ItemComponent) component);
                        break;
                    case ITEM_INFO:
                        syntheticComponent.setItemInfoComponent((ItemInfoComponent) component);
                        break;
                    case ITEM_PAY:
                        syntheticComponent.setItemPayComponent((ItemPayComponent) component);
                        break;
                    case QUANTITY:
                        syntheticComponent.setQuantityComponent((QuantityComponent) component);
                        break;

                    default:
                        if (component.getTag().equals("tvtaoFanliInfo")) {
                            syntheticComponent.setRebateComponent(component);
                        }
                        break;
                }
            }

            List<Component> output = new ArrayList<Component>(input.size());
            for (Component component : input) {
                switch (ComponentTag.getComponentTagByDesc(component.getTag())) {
                    case ITEM_INFO:
                        output.add(syntheticComponent);
                        continue;
                    case ITEM:
                        continue;
                    case ITEM_PAY:
                        continue;
                    case QUANTITY:
                        continue;
                    default:
                        output.add(component);
                }
            }
            return output;
        }
    }

    private class OrderBondSplitJoinRule implements SplitJoinRule {
        @Override
        public List<Component> execute(List<Component> input) {
            OrderBondSyntheticComponent bondComponent = new OrderBondSyntheticComponent();
            for (Component component : input) {
                AppDebug.d(TAG, "Join Component " + component + ", tag: " + component.getTag() + " type: " + component.getType());
                if (component instanceof OrderInfoComponent) {
                    bondComponent.orderInfoComponent = (OrderInfoComponent) component;
                    continue;
                }
                ComponentType type = component.getType();
                ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());
                if ("mnj1Promotion".equals(component.getTag()) && component instanceof SelectComponent) {
                    SpannableStringBuilder valuespan = null;
                    SelectComponent comp = (SelectComponent) component;
                    String title = comp.getTitle();
                    String youhui = mMainBaseActivity.getResources().getString(
                            R.string.ytm_buildorder_youhui);
                    String value = comp.getSelectedOption().getPrice();
                    valuespan = onHandlerSpanned(value + youhui, true);
                    bondComponent.addGoodItem(title, valuespan);
                    Map<String, String> params = Utils.getProperties();
                    params.put("disc_name", comp.getTag());
                    params.put("is_prebuy", "" + prePay);
                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                    continue;
                }

                switch (type) {
                    case TABLE:
                        String tagvalue = component.getTag();
                        break;
                    case SYNTHETIC:
                        if (component instanceof OrderSyntheticComponent) {
                            bondComponent.orders.add((OrderSyntheticComponent) component);
                        }
                        break;
                    case LABEL:
                        switch (tag) {
                            case PROMOTION:
                                LabelComponent labelComponent = (LabelComponent) component;
                                String value = labelComponent.getValue();
                                if (!TextUtils.isEmpty(value)) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_shopdiscount);
                                    int index = value.indexOf(shopDiscount, 0);
                                    if (index >= 0) {
                                        //  只要店铺优惠的数据
                                        SpannableStringBuilder valuespan = null;
                                        if (isEqualZero(labelComponent.getData().getString("quark"))) {
                                            // 如果等于0，那么截取要显示的内容
                                            String content = value.substring(shopDiscount.length());
                                            if (!TextUtils.isEmpty(content)) {
                                                // 去掉两边的空格
                                                content = content.trim();
                                                valuespan = onHandlerSpanned(content, false);
                                            }
                                        } else {
                                            String youhui = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_youhui);
                                            valuespan = onHandlerSpanned(labelComponent.getData().getString("quark") + youhui, true);
                                        }
                                        if (valuespan != null) {
                                            isHasShopPromotion = true;
                                            String discount = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_discount);
                                            bondComponent.addGoodItem(discount, valuespan);
                                            Map<String, String> params = Utils.getProperties();
                                            params.put("disc_name", labelComponent.getTag());
                                            params.put("is_prebuy", "" + prePay);
                                            Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                        }
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case SELECT:
                        if (TextUtils.equals(component.getTag(), "promotion")) {
                            SelectComponent selectComponent = (SelectComponent) component;
                            if (selectComponent.getParent() instanceof ItemComponent)
                                break;
                            if (selectComponent != null) {
                                SelectOption option = selectComponent.getSelectedOption();
                                SpannableStringBuilder valuespan = null;
                                if (option != null) {
                                    String shopDiscount = selectComponent.getTitle();

                                    String value = option.getPrice();
                                    if (!TextUtils.isEmpty(value) && !isEqualZero(value)) {
                                        // 不是空字符，并且值不是0，那么显示处理
                                        try {
                                            int vulue_int = Integer.parseInt(value);
                                            float vulue_float = ((float) vulue_int / 100.0f);
                                            String yuan = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_youhui);
                                            String value_str = String.valueOf(vulue_float);
                                            if (vulue_int > 0) {
                                                value_str = "-" + value_str;
                                            }
                                            valuespan = onHandlerSpanned(value_str + yuan, true);

                                            if (valuespan != null) {
                                                Map<String, String> params = Utils.getProperties();
                                                params.put("disc_name", selectComponent.getTag());
                                                params.put("is_prebuy", "" + prePay);
                                                Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                                bondComponent.addGoodItem(shopDiscount, valuespan);
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        valuespan = onHandlerSpanned(option.getName(), false);
                                        valuespan.setSpan(new ForegroundColorSpan(foregroundColor), 0, option.getName().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                        Map<String, String> params = Utils.getProperties();
                                        params.put("disc_name", selectComponent.getTag());
                                        params.put("is_prebuy", "" + prePay);
                                        Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                        bondComponent.addGoodItem(shopDiscount, valuespan);
                                    }
                                }
                            }
                        }
                        break;

                    case TOGGLE:
                        switch (tag) {
                            case TMALL_POINT:
                                ToggleComponent toggleComponent = (ToggleComponent) component;
                                String value = toggleComponent.getData().getString("quark");
                                if (!TextUtils.isEmpty(value) && toggleComponent.isChecked()) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_tianmaojifen);
                                    double tamll_d = 0.0f;
                                    try {
                                        tamll_d = Double.parseDouble(value);
                                    } catch (Exception e) {
                                    }
                                    int tamll = (int) (Math.abs(tamll_d) * 100.0);
                                    String src = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_jifenshiyong, tamll_d, tamll);
                                    SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                    Map<String, String> params = Utils.getProperties();
                                    params.put("disc_name", toggleComponent.getTag());
                                    params.put("is_prebuy", "" + prePay);
                                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                    bondComponent.addGoodItem(shopDiscount, valuespan);
                                }
                                break;

                            case TBGOLD:
                                // 小票处显示淘金币
                                ToggleComponent toggleComponent_tbgold = (ToggleComponent) component;
                                String value_tbgold = toggleComponent_tbgold.getData().getString("quark");
                                if (!TextUtils.isEmpty(value_tbgold) && toggleComponent_tbgold.isChecked()) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_taojinbi);
                                    double taojinbi_d = 0.0f;
                                    try {
                                        taojinbi_d = Double.parseDouble(value_tbgold);
                                    } catch (Exception e) {
                                    }
                                    int taojinbi = (int) (Math.abs(taojinbi_d) * 100.0);
                                    String src = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_taojinbishiyong, taojinbi_d, taojinbi);
                                    SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                    Map<String, String> params = Utils.getProperties();
                                    params.put("disc_name", toggleComponent_tbgold.getTag());
                                    params.put("is_prebuy", "" + prePay);
                                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                    bondComponent.addGoodItem(shopDiscount, valuespan);
                                }
                                break;

                            default:
                                break;
                        }
                        break;

                    case BIZ:
                        switch (tag) {
                            case ADDRESS:
                                // 保存地址
                                mAddressComponent = (AddressComponent) component;
                                break;
                            case ORDER:
//                                AppDebug.d(TAG, "OrderComponent id: " + ((OrderComponent) component).getId());
//                                bondComponent.orders.add((OrderComponent) component);
                                break;
                            case ORDER_INFO:
                                OrderInfoComponent orderInfoComponent = (OrderInfoComponent) component;
                                AppDebug.d(TAG, "OrderInfoComponent title:" + orderInfoComponent.getTitle());
                                bondComponent.orderInfoComponent = orderInfoComponent;
                                break;

                            case ORDER_PAY:
                                OrderPayComponent orderPayComponent = (OrderPayComponent) component;
                                AppDebug.d(TAG, "OrderPayComponent quantity:" + ((OrderPayComponent) component).getQuantity());
                                bondComponent.orderPayComponent = orderPayComponent;
                                break;

                            case ORDER_BOND:
                                OrderBondComponent orderBondComponent = (OrderBondComponent) component;
                                AppDebug.d(TAG, "OrderBondComponent:" + component);
                                bondComponent.orderBondComponent = orderBondComponent;
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
            List<Component> output = new ArrayList<Component>();
            output.addAll(input);
            output.add(bondComponent);
            return output;
        }
    }

    private class OrderSplitJoinRule implements SplitJoinRule {
        @Override
        public List<Component> execute(List<Component> input) {

            int insertPos = 0;//确保数量和运费在最前
            OrderSyntheticComponent orderSyntheticComponent = new OrderSyntheticComponent();
            for (Component component : input) {
                AppDebug.d(TAG, "Join Component " + component + ", tag: " + component.getTag() + " type: " + component.getType());
                if (component instanceof GoodsSyntheticComponent) {
                    if (orderSyntheticComponent.getGoodsSyntheticComponents() == null)
                        orderSyntheticComponent.setGoodsSyntheticComponents(new ArrayList<GoodsSyntheticComponent>());
                    orderSyntheticComponent.getGoodsSyntheticComponents().add((GoodsSyntheticComponent) component);
                    continue;
                }
                if ("mnj1Promotion".equals(component.getTag()) && component instanceof SelectComponent) {
                    SpannableStringBuilder valuespan = null;
                    SelectComponent comp = (SelectComponent) component;
                    String title = comp.getTitle();
                    String youhui = mMainBaseActivity.getResources().getString(
                            R.string.ytm_buildorder_youhui);
                    String value = comp.getSelectedOption().getPrice();
                    valuespan = onHandlerSpanned(value + youhui, true);
                    orderSyntheticComponent.addGoodItem(title, valuespan);
                    Map<String, String> params = Utils.getProperties();
                    params.put("disc_name", comp.getTag());
                    params.put("is_prebuy", "" + prePay);
                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                    continue;
                }
                ComponentType type = component.getType();
                ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());

                switch (type) {
                    case TABLE:
                        String tagvalue = component.getTag();
                        break;
                    case LABEL:
                        switch (tag) {
                            case PROMOTION:
                                LabelComponent labelComponent = (LabelComponent) component;
                                String value = labelComponent.getValue();
                                if (!TextUtils.isEmpty(value)) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_shopdiscount);
                                    int index = value.indexOf(shopDiscount, 0);
                                    if (index >= 0) {
                                        //  只要店铺优惠的数据
                                        SpannableStringBuilder valuespan = null;
                                        if (isEqualZero(labelComponent.getData().getString("quark"))) {
                                            // 如果等于0，那么截取要显示的内容
                                            String content = value.substring(shopDiscount.length());
                                            if (!TextUtils.isEmpty(content)) {
                                                // 去掉两边的空格
                                                content = content.trim();
                                                valuespan = onHandlerSpanned(content, false);
                                            }
                                        } else {
                                            String youhui = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_youhui);
                                            valuespan = onHandlerSpanned(labelComponent.getData().getString("quark") + youhui, true);
                                        }
                                        if (valuespan != null) {
                                            isHasShopPromotion = true;
                                            String discount = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_discount);
                                            orderSyntheticComponent.addGoodItem(discount, valuespan);
                                            Map<String, String> params = Utils.getProperties();
                                            params.put("disc_name", labelComponent.getTag());
                                            params.put("is_prebuy", "" + prePay);
                                            Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                        }
                                    }
                                }
                                break;

                            default:
                                if ("preSellAddress".equals(component.getTag())) {
                                    orderSyntheticComponent.setPresaleAddressComponent(component);
                                }
                                if ("finalPay".equals(component.getTag())) {
                                    orderSyntheticComponent.setFinalPayComponent(component);
                                }
                                break;
                        }
                        break;

                    case SELECT:
                        if (TextUtils.equals(component.getTag(), "promotion")) {
                            SelectComponent selectComponent = (SelectComponent) component;
                            if (selectComponent.getParent() instanceof ItemComponent)
                                break;
                            if (selectComponent != null) {
                                SelectOption option = selectComponent.getSelectedOption();
                                SpannableStringBuilder valuespan = null;
                                if (option != null) {
                                    String shopDiscount = selectComponent.getTitle();

                                    String value = option.getPrice();
                                    if (!TextUtils.isEmpty(value) && !isEqualZero(value)) {
                                        // 不是空字符，并且值不是0，那么显示处理
                                        try {
                                            int vulue_int = Integer.parseInt(value);
                                            float vulue_float = ((float) vulue_int / 100.0f);
                                            String yuan = mMainBaseActivity.getResources().getString(
                                                    R.string.ytm_buildorder_youhui);
                                            String value_str = String.valueOf(vulue_float);
                                            if (vulue_int > 0) {
                                                value_str = "-" + value_str;
                                            }
                                            valuespan = onHandlerSpanned(value_str + yuan, true);

                                            if (valuespan != null) {
                                                Map<String, String> params = Utils.getProperties();
                                                params.put("disc_name", selectComponent.getTag());
                                                params.put("is_prebuy", "" + prePay);
                                                Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                                orderSyntheticComponent.addGoodItem(shopDiscount, valuespan);
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        valuespan = onHandlerSpanned(option.getName(), false);
                                        valuespan.setSpan(new ForegroundColorSpan(foregroundColor), 0, option.getName().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                        Map<String, String> params = Utils.getProperties();
                                        params.put("disc_name", selectComponent.getTag());
                                        params.put("is_prebuy", "" + prePay);
                                        Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                        orderSyntheticComponent.addGoodItem(shopDiscount, valuespan);
                                    }
                                }
                            }
                        }
                        break;

                    case TOGGLE:
                        switch (tag) {
                            case TMALL_POINT:
                                ToggleComponent toggleComponent = (ToggleComponent) component;
                                String value = toggleComponent.getData().getString("quark");
                                if (!TextUtils.isEmpty(value) && toggleComponent.isChecked()) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_tianmaojifen);
                                    double tamll_d = 0.0f;
                                    try {
                                        tamll_d = Double.parseDouble(value);
                                    } catch (Exception e) {
                                    }
                                    int tamll = (int) (Math.abs(tamll_d) * 100.0);
                                    String src = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_jifenshiyong, tamll_d, tamll);
                                    SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                    Map<String, String> params = Utils.getProperties();
                                    params.put("disc_name", toggleComponent.getTag());
                                    params.put("is_prebuy", "" + prePay);
                                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                    orderSyntheticComponent.addGoodItem(shopDiscount, valuespan);
                                }
                                break;

                            case TBGOLD:
                                // 小票处显示淘金币
                                ToggleComponent toggleComponent_tbgold = (ToggleComponent) component;
                                String value_tbgold = toggleComponent_tbgold.getData().getString("quark");
                                if (!TextUtils.isEmpty(value_tbgold) && toggleComponent_tbgold.isChecked()) {
                                    String shopDiscount = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_taojinbi);
                                    double taojinbi_d = 0.0f;
                                    try {
                                        taojinbi_d = Double.parseDouble(value_tbgold);
                                    } catch (Exception e) {
                                    }
                                    int taojinbi = (int) (Math.abs(taojinbi_d) * 100.0);
                                    String src = mMainBaseActivity.getResources().getString(
                                            R.string.ytm_buildorder_taojinbishiyong, taojinbi_d, taojinbi);
                                    SpannableStringBuilder valuespan = onHandlerSpanned(src, true);
                                    Map<String, String> params = Utils.getProperties();
                                    params.put("disc_name", toggleComponent_tbgold.getTag());
                                    params.put("is_prebuy", "" + prePay);
                                    Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                    orderSyntheticComponent.addGoodItem(shopDiscount, valuespan);
                                }
                                break;

                            default:
                                break;
                        }
                        break;
                    case BIZ:
                        switch (tag) {
                            case ADDRESS:
                                // 保存地址
                                mAddressComponent = (AddressComponent) component;
                                break;
                            case ORDER:
                                AppDebug.d(TAG, "OrderComponent id: " + ((OrderComponent) component).getId());
                                ((OrderComponent) component).getFields().getString("sellerId");

                                orderSyntheticComponent.setParent(component.getParent());
                                orderSyntheticComponent.setOrderComponent((OrderComponent) component);
                                break;
                            case DELIVERY_METHOD:
                                DeliveryMethodComponent deliveryMethodComponent = (DeliveryMethodComponent) component;
                                orderSyntheticComponent.setDeliveryMethodComponent(deliveryMethodComponent);
                                break;
                            case TAX_INFO:
                                TaxInfoComponent taxInfoComponent = (TaxInfoComponent) component;
                                SpannableStringBuilder value = onHandlerSpanned(
                                        (taxInfoComponent.getDesc() == null ? "" : taxInfoComponent.getDesc()) + taxInfoComponent.getValue(), false);
                                value.setSpan(new ForegroundColorSpan(foregroundColor), 0, value.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                                Map<String, String> params = Utils.getProperties();
                                params.put("disc_name", taxInfoComponent.getTag());
                                params.put("is_prebuy", "" + prePay);
                                Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
                                orderSyntheticComponent.addGoodItem(0, taxInfoComponent.getTitle(), value);
                                break;
                            case ORDER_INFO:
                                OrderInfoComponent orderInfoComponent = (OrderInfoComponent) component;
                                AppDebug.d(TAG, "OrderInfoComponent title:" + orderInfoComponent.getTitle());
                                orderSyntheticComponent.setOrderInfoComponent(orderInfoComponent);
                                break;

                            case ORDER_PAY:
                                OrderPayComponent orderPayComponent = (OrderPayComponent) component;
                                AppDebug.d(TAG, "OrderPayComponent quantity:" + ((OrderPayComponent) component).getQuantity());
                                orderSyntheticComponent.setOrderPayComponent(orderPayComponent);
                                break;
                            default:
                                break;
                        }
                        break;

                }
            }

            List<Component> output = new ArrayList<Component>();
            output.addAll(input);
//                        for (GoodsDisplayInfo.GoodsItem item : orderSyntheticComponent.getGoodsItems()) {
//                            AppDebug.d(TAG, "goodsitem title:" + item.documents + ",goodsitem value:" + item.value);
//                        }
//            if (orderSyntheticComponent.getOrderInfoComponent() != null)
            output.add(orderSyntheticComponent);
            return output;
        }
    }

    private static class TvTaoExpandParseRule implements ExpandParseRule {

        @Override
        public Component expandComponent(JSONObject data, BuyEngine engine) {
            return null;
        }
    }
}
