package com.yunos.tvtaobao.tradelink.buildorder.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemPayComponent;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderPreSale;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRebateBo;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.GoodsDisplayInfo;
import com.yunos.tvtaobao.tradelink.buildorder.component.GoodsSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderBondSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.OrderSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.SyntheticComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhujun on 9/28/16.
 */
//TODO 交互, 精简ViewHolder代码
public class BuildOrderCartItemListAdapter extends BaseAdapter {
    private String TAG = "BuildOrderCartItemListAdapter";
    private Map<String, List<SyntheticComponent>> components;
    private List<ItemInfoComponent> invalidGoods;
    private List<String> sellers;
    private List<String> validSellers;
    private Context mContext;
    private BuildOrderPreSale mBuildOrderPreSale;
    private int listWidth;
    private boolean isFisrst = true;
    private List<BuildOrderRebateBo> rebateBoList;

    public void setListWidth(int listWidth) {
        boolean changed = this.listWidth != listWidth;
        this.listWidth = listWidth;
        if (changed) notifyDataSetChanged();
    }

    public BuildOrderCartItemListAdapter(Context context, List<String> sellers,
                                         Map<String, List<SyntheticComponent>> orderComponents, List<BuildOrderRebateBo> rebateBoList, BuildOrderPreSale buildOrderPreSale) {
        super();
        this.mContext = context;
        components = orderComponents;
        this.rebateBoList = rebateBoList;
        this.sellers = sellers;
        this.mBuildOrderPreSale = buildOrderPreSale;
        findAndSortInvalidGroups();
    }

    private void findAndSortInvalidGroups() {
        validSellers = new ArrayList<String>();
        invalidGoods = new ArrayList<ItemInfoComponent>();
        for (String id : sellers) {
            List<SyntheticComponent> syntheticComponents = components.get(id);
            boolean invalid = true;
            for (SyntheticComponent c : syntheticComponents) {
                if (c instanceof OrderSyntheticComponent) {
                    OrderSyntheticComponent syntheticComponent = (OrderSyntheticComponent) c;
                    if (syntheticComponent.getOrderComponent() != null && syntheticComponent.getOrderComponent().getFields().getBoolean("valid")) {
                        if (!validSellers.contains(id)) validSellers.add(id);
                        invalid = false;
                        break;
                    }
                } else if (c instanceof OrderBondSyntheticComponent) {
                    OrderBondSyntheticComponent orderBondSyntheticComponent = (OrderBondSyntheticComponent) c;
                    for (OrderSyntheticComponent orderSyntheticComponent : orderBondSyntheticComponent.orders)
                        if (orderSyntheticComponent.getOrderComponent() != null && orderSyntheticComponent.getOrderComponent().getFields().getBoolean("valid")) {
                            if (!validSellers.contains(id)) validSellers.add(id);
                            invalid = false;
                            break;
                        }
                }
            }
            if (invalid) {
                for (SyntheticComponent c : syntheticComponents) {
                    if (c instanceof OrderSyntheticComponent) {
                        OrderSyntheticComponent syntheticComponent = (OrderSyntheticComponent) c;
                        for (GoodsSyntheticComponent comp : syntheticComponent.getGoodsSyntheticComponents()) {
                            invalidGoods.add(comp.getItemInfoComponent());
                        }
                    } else if (c instanceof OrderBondSyntheticComponent) {
                        OrderBondSyntheticComponent syntheticComponent = (OrderBondSyntheticComponent) c;
                        for (OrderSyntheticComponent comp : syntheticComponent.orders) {
                            if (comp.getOrderComponent() != null && !comp.getOrderComponent().getFields().getBoolean("valid")) {
                                for (GoodsSyntheticComponent goods : comp.getGoodsSyntheticComponents()) {
                                    invalidGoods.add(goods.getItemInfoComponent());
                                }
                            }

                        }
                    }
                }
            }

        }
    }

    @Override
    public int getCount() {
        boolean hasInvald = invalidGoods != null && invalidGoods.size() > 0;
        return (validSellers == null ? 0 : validSellers.size()) + (hasInvald ? 1 : 0);
    }

    @Override
    public Object getItem(int i) {
        boolean hasInvald = invalidGoods != null && invalidGoods.size() > 0;
        if (hasInvald) {
            if (i == 0)
                return "invalid";
            return validSellers == null ? null : validSellers.get(i - 1);
        } else {
            return validSellers == null ? null : validSellers.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        boolean hasInvald = invalidGoods != null && invalidGoods.size() > 0;
        if (viewGroup.getMeasuredWidth() > 0) {
            listWidth = viewGroup.getMeasuredWidth() - viewGroup.getPaddingLeft() - viewGroup.getPaddingRight();
        }
        AppDebug.d("test", "getView at Pos: " + i + ",convertView:" + convertView + "validSellers = " + getCount());
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.ytm_activity_buildorder_gooditemview, null);
            CartItemViewHolder vh = new CartItemViewHolder();
            vh.titleView = (TextView) convertView.findViewById(R.id.buildorder_title);
            vh.shopicon = (ImageView) convertView.findViewById(R.id.buildorder_shopicon);
            vh.detailContainer = (LinearLayout) convertView.findViewById(R.id.detail_container);
            convertView.setTag(vh);
        }
        CartItemViewHolder holder = (CartItemViewHolder) convertView.getTag();
        if (hasInvald) {
            if (i == 0)
                holder.fillInvalidGoods(invalidGoods, i == getCount() - 1);
            else
                holder.fillGoodsInfoComponents(components.get(sellers.get(i - 1)), i == getCount() - 1);
        } else {
            holder.fillGoodsInfoComponents(components.get(sellers.get(i)), i == getCount() - 1);
        }
        return convertView;
    }

    private class CartItemDetailViewHolder {
        private LinearLayout listView;
        private TextView itemView;
        private TextView sumMsg;
        private RelativeLayout layoutRebate;
        private TextView tvRebate;
        private ImageView ivRebate;
        private TextView skuView;
        private LinearLayout ll_presale;

        private LinearLayout presaleAddress;
        private TextView presaleAddressLabel;

        private ImageView divider;
        private TextView sumMsg2;//定金抵扣
    }

    private class CartItemViewHolder {
        private TextView titleView;
        private ImageView shopicon;

        private LinearLayout detailContainer;

        private void fillInvalidGoods(List<ItemInfoComponent> components, boolean isLast) {
            detailContainer.removeAllViews();//todo
            LinearLayout detail = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ytm_activity_buildorder_gooditemsubview, null);
            CartItemDetailViewHolder detailViewHolder = new CartItemDetailViewHolder();
            detailViewHolder.listView = (LinearLayout) detail.findViewById(R.id.buildorder_info_listview);
            detailViewHolder.sumMsg = (TextView) detail.findViewById(R.id.sumMsg);
            detailViewHolder.itemView = (TextView) detail.findViewById(R.id.buildorder_item);
            detailViewHolder.skuView = (TextView) detail.findViewById(R.id.buildorder_sku);
            detailViewHolder.ll_presale = (LinearLayout) detail.findViewById(R.id.ll_presale);
            detailViewHolder.presaleAddress = (LinearLayout) detail.findViewById(R.id.presaleaddress);
            detailViewHolder.presaleAddressLabel = (TextView) detail.findViewById(R.id.presaleaddresslabel);
            detailViewHolder.divider = (ImageView) detail.findViewById(R.id.divider);
            detailViewHolder.sumMsg2 = (TextView) detail.findViewById(R.id.sumMsg2);
            detailViewHolder.listView.removeAllViews();
            titleView.setText("以下商品无法购买");

            detailViewHolder.itemView.setVisibility(View.GONE);
            detailViewHolder.skuView.setMaxLines(3);
            detailViewHolder.skuView.setVisibility(View.VISIBLE);
            detailViewHolder.itemView.setVisibility(View.GONE);
            detailViewHolder.sumMsg.setVisibility(View.GONE);
            detailViewHolder.listView.setVisibility(View.GONE);
            shopicon.setVisibility(View.GONE);
            detailViewHolder.ll_presale.setVisibility(View.GONE);

            ArrayList<String> showgoods = new ArrayList<String>();
            for (ItemInfoComponent comp : components) {
                showgoods.add(comp.getTitle());
            }
            TextPaint paint = detailViewHolder.skuView.getPaint();
            int paddingLeft = detailViewHolder.skuView.getPaddingLeft();
            int paddingRight = detailViewHolder.skuView.getPaddingRight();
            int bufferWidth = (int) paint.getTextSize() * 1;
            int availableTextWidth = (listWidth - paddingLeft - paddingRight) - bufferWidth;
            StringBuilder invalid = new StringBuilder();
            invalid.setLength(0);
            if (showgoods.size() > 1) {
                for (int index = 0; index < showgoods.size(); index++) {
                    String ellipsizeStr = (String) TextUtils.ellipsize(showgoods.get(index), paint, availableTextWidth, TextUtils.TruncateAt.END);
                    invalid.append(ellipsizeStr);
                    invalid.append('\n'); // 添加换行符
                }
            } else {
                invalid.append(showgoods.get(0));
            }
            detailViewHolder.skuView.setText(invalid.toString());
            int textColor = mContext.getResources().getColor(R.color.ytm_buildorder_invalid);
            detailViewHolder.skuView.setTextColor(textColor);

            detailContainer.addView(detail);
        }

        private void fillGoodsInfoComponents(List<SyntheticComponent> components, boolean isLast) {

            detailContainer.removeAllViews();

            for (SyntheticComponent c : components) {
                //rebateCoupon默认为0不展示返利金额
                String rebateCoupon = "0";
                String rebateCouponMessage = null;
                String rebatePic = null;
                if (c instanceof OrderSyntheticComponent) {
                    OrderSyntheticComponent component = (OrderSyntheticComponent) c;//TODO add orderbond
                    boolean onlyOneItem = component.getGoodsSyntheticComponents().size() == 1;
                    // 初始化左侧内容的显示位置
                    boolean hasInvalid = false;
                    boolean hasValid = false;
                    if (component.getOrderInfoComponent() == null)//skip order that is in a bond
                        continue;
                    LinearLayout detail = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ytm_activity_buildorder_gooditemsubview, null);
                    CartItemDetailViewHolder detailViewHolder = new CartItemDetailViewHolder();
                    detailViewHolder.listView = (LinearLayout) detail.findViewById(R.id.buildorder_info_listview);
                    detailViewHolder.sumMsg = (TextView) detail.findViewById(R.id.sumMsg);
                    detailViewHolder.itemView = (TextView) detail.findViewById(R.id.buildorder_item);
                    detailViewHolder.skuView = (TextView) detail.findViewById(R.id.buildorder_sku);
                    detailViewHolder.ll_presale = (LinearLayout) detail.findViewById(R.id.ll_presale);
                    detailViewHolder.presaleAddress = (LinearLayout) detail.findViewById(R.id.presaleaddress);
                    detailViewHolder.presaleAddressLabel = (TextView) detail.findViewById(R.id.presaleaddresslabel);
                    detailViewHolder.divider = (ImageView) detail.findViewById(R.id.divider);
                    detailViewHolder.listView.removeAllViews();
                    detailViewHolder.sumMsg2 = (TextView) detail.findViewById(R.id.sumMsg2);
                    detailViewHolder.tvRebate = (TextView) detail.findViewById(R.id.tv_buildorder_rebate);
                    detailViewHolder.ivRebate = (ImageView) detail.findViewById(R.id.iv_buildorder_rebate_item);
                    detailViewHolder.layoutRebate = (RelativeLayout) detail.findViewById(R.id.layout_buildorder_rebate);

                    ArrayList<String> invalidGoods = new ArrayList<String>();
                    ArrayList<String> validGoods = new ArrayList<String>();

                    //返利信息
                    if (rebateBoList != null) {
                        for (BuildOrderRebateBo buildOrderRebateBo : rebateBoList) {
                            if (buildOrderRebateBo.getOrderId().equals(component.getOrderPayComponent().getId())) {
                                rebateCoupon = buildOrderRebateBo.getCoupon();
                                rebateCouponMessage = buildOrderRebateBo.getMessage();
                                rebatePic = buildOrderRebateBo.getPicUrl();
                            }
                        }
                    }
                    //rebateCoupon不为空，则是正常返利
                    if (!TextUtils.isEmpty(rebateCoupon)) {
                        rebateCoupon = Utils.getRebateCoupon(rebateCoupon);
                        if (rebateCoupon != null) {
                            if (!TextUtils.isEmpty(rebateCouponMessage)) {
                                rebateCouponMessage = rebateCouponMessage + ": ";
                            }
                            detailViewHolder.layoutRebate.setVisibility(View.VISIBLE);
                            SpannableStringBuilder builder = new SpannableStringBuilder(rebateCouponMessage + rebateCoupon + "元");
                            builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.aliuser_color_black)), 0, rebateCouponMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            detailViewHolder.tvRebate.setText(builder);
                            if (!TextUtils.isEmpty(rebatePic)) {
                                detailViewHolder.ivRebate.setVisibility(View.VISIBLE);
                                ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebatePic, detailViewHolder.ivRebate);
                            } else {
                                detailViewHolder.ivRebate.setVisibility(View.GONE);
                            }

                        } else {
                            detailViewHolder.layoutRebate.setVisibility(View.GONE);
                        }
                    } else {
                        //rebateCoupon为空，则是预售返利

                        detailViewHolder.layoutRebate.setVisibility(View.VISIBLE);
                        detailViewHolder.tvRebate.setText(rebateCouponMessage);
                        if (!TextUtils.isEmpty(rebatePic)) {
                            detailViewHolder.ivRebate.setVisibility(View.VISIBLE);
                            ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebatePic, detailViewHolder.ivRebate);
                        } else {
                            detailViewHolder.ivRebate.setVisibility(View.GONE);
                        }
                    }


                    if (component.getPresaleAddressComponent() != null) {
                        detailViewHolder.presaleAddress.setVisibility(View.VISIBLE);
                        detailViewHolder.presaleAddressLabel.setText(String.format("%s\n%s", component.getPresaleAddressComponent().getFields().getString("value"),
                                component.getPresaleAddressComponent().getFields().getString("desc")));
                    } else {
                        detailViewHolder.presaleAddress.setVisibility(View.GONE);
                    }
                    if (component.getOrderComponent() == null || !component.getOrderComponent().getFields().getBoolean("valid")) {
                        if (!hasInvalid)
                            invalidGoods.add(mContext.getResources().getString(R.string.ytm_buildorder_invalid));
                        for (GoodsSyntheticComponent goodsSyntheticComponent : component.getGoodsSyntheticComponents()) {
                            invalidGoods.add(goodsSyntheticComponent.getItemInfoComponent().getTitle());
                        }
                        hasInvalid = true;
                    } else {
                        //valid
                        hasValid = true;
                        titleView.setText(component.getOrderInfoComponent().getTitle());
                        ImageLoaderManager.getImageLoaderManager(mContext).displayImage(component.getOrderInfoComponent().getIcon(), shopicon);

                        for (GoodsSyntheticComponent goodComponent : component.getGoodsSyntheticComponents()) {
                            ItemInfoComponent itemInfoComponent = goodComponent.getItemInfoComponent();
                            ItemPayComponent itemPayComponent = goodComponent.getItemPayComponent();
                            ItemComponent itemComponent = goodComponent.getItemComponent();
                            validGoods.add(itemInfoComponent.getTitle());
                            if (onlyOneItem) {
                                detailViewHolder.skuView.setText(itemInfoComponent.getSkuInfo());
                                detailViewHolder.itemView.setText(itemInfoComponent.getTitle());
                                detailViewHolder.itemView.setVisibility(View.VISIBLE);
                            } else {
                                detailViewHolder.itemView.setVisibility(View.GONE);
                            }
                        }
                        component.buildPriorityGoodItems(mContext);
                        List<GoodsDisplayInfo.GoodsItem> goodsItems = component.getGoodsItems();
                        for (int i = 0; i < goodsItems.size(); i++) {
                            View v = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_info_item_layout, null);
                            ((TextView) v.findViewById(R.id.goods_info_documents)).setText(goodsItems.get(i).documents);
                            ((TextView) v.findViewById(R.id.goods_info_value)).setText(goodsItems.get(i).value);
                            detailViewHolder.listView.addView(v);
                            if (i == 0) {
                                if (mBuildOrderPreSale != null) {
                                    AppDebug.e(TAG, "mBuildOrderPreSale = " + mBuildOrderPreSale.toString());
                                    //定金
                                    if (!TextUtils.isEmpty(mBuildOrderPreSale.getPriceTitle()) && !TextUtils.isEmpty(mBuildOrderPreSale.getPriceText())) {
                                        View v3 = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_info_item_layout, null);
                                        ((TextView) v3.findViewById(R.id.goods_info_documents)).setText(mBuildOrderPreSale.getPriceTitle());
                                        TextView value = ((TextView) v3.findViewById(R.id.goods_info_value));
                                        value.setText(mBuildOrderPreSale.getPriceText() + "元");
                                        value.setTextColor(mContext.getResources().getColor(R.color.ytm_buildorder_total));
                                        detailViewHolder.listView.addView(v3);
                                    }
                                    //尾款
                                    if (!TextUtils.isEmpty(mBuildOrderPreSale.getPresale()) && !TextUtils.isEmpty(mBuildOrderPreSale.getPresaleTitle())) {
                                        View v2 = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_info_item_layout, null);
                                        ((TextView) v2.findViewById(R.id.goods_info_documents)).setText(mBuildOrderPreSale.getPresaleTitle());
                                        TextView value2 = ((TextView) v2.findViewById(R.id.goods_info_value));
                                        value2.setText(mBuildOrderPreSale.getPresale() + "元");
                                        value2.setTextColor(mContext.getResources().getColor(R.color.ytm_buildorder_total));
                                        detailViewHolder.listView.addView(v2);

                                    }

                                }
                            }


                        }
//                    for (GoodsDisplayInfo.GoodsItem item : goodsItems) {
//                        View v = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_info_item_layout, null);
//                        ((TextView) v.findViewById(R.id.goods_info_documents)).setText(item.documents);
//                        ((TextView) v.findViewById(R.id.goods_info_value)).setText(item.value);
//                        listView.addView(v);
//                    }

                        if (component.getFinalPayComponent() != null) {
//                            String yuan = mContext.getResources().getString(
//                                    R.string.ytm_buildorder_unit_yuan);
//                            SpannableStringBuilder ssb = onHandlerSpanned(String.format("合计: %s", price + yuan), true);
//                            ssb.insert(0, String.format("共%s件商品 ", orderSyntheticComponent.orderPayComponent.getQuantity()));
//                            SpannableStringBuilder ssb = onHandlerSpanned(String.format("%s %s", "尾款",
//                                    component.getFinalPayComponent().getFields().getString("desc")), true);
                            String payValue = component.getFinalPayComponent().getFields().getString("desc");
                            Pattern pattern = Pattern.compile("\\((.*)\\)");
                            Matcher matcher = pattern.matcher(payValue);
                            SpannableStringBuilder ssb = null;
                            if (matcher.find() && !TextUtils.isEmpty(matcher.group(1)) && matcher.start() > 0) {
                                ssb = onHandlerSpanned(String.format("%s %s", "尾款小计",
                                        payValue.substring(0, matcher.start())), true);
                                ssb.insert(0, String.format("共%s件商品 ", component.getOrderPayComponent().getQuantity()));
                                detailViewHolder.sumMsg2.setText(matcher.group(1));
                                detailViewHolder.sumMsg2.setVisibility(View.VISIBLE);
                            } else {
                                ssb = onHandlerSpanned(String.format("%s %s", "尾款小计", payValue), true);
                                ssb.insert(0, String.format("共%s件商品 ", component.getOrderPayComponent().getQuantity()));
                                detailViewHolder.sumMsg2.setVisibility(View.GONE);
                            }
                            detailViewHolder.sumMsg.setText(ssb);
                            detailViewHolder.sumMsg.setVisibility(View.VISIBLE);
                        } else if (component.getOrderPayComponent() != null) {
                            String price = component.getOrderPayComponent().getPrice();
                            if (!TextUtils.isEmpty(price)) {
                                String yuan = mContext.getResources().getString(
                                        R.string.ytm_buildorder_unit_yuan);
                                SpannableStringBuilder ssb = onHandlerSpanned(String.format("小计: %s", price + yuan), true);
                                ssb.insert(0, String.format("共%s件商品 ", component.getOrderPayComponent().getQuantity()));
                                detailViewHolder.sumMsg.setText(ssb);
                                detailViewHolder.sumMsg.setVisibility(View.VISIBLE);
                            }
                        } else {
                            detailViewHolder.sumMsg.setVisibility(View.INVISIBLE);
                        }
                    }


                    if (mBuildOrderPreSale != null) {
                        if (isFisrst) {
                            detailViewHolder.ll_presale.setVisibility(View.VISIBLE);
                            View presaleView = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_item_presale, null);
                            TextView tv_promotion = (TextView) presaleView.findViewById(R.id.tv_info_promotion);
                            TextView tv_rest = (TextView) presaleView.findViewById(R.id.tv_info_rest);
                            TextView tv_deliver = (TextView) presaleView.findViewById(R.id.tv_info_deliver);
                            TextView tv_notify = (TextView) presaleView.findViewById(R.id.tv_info_notify);
                            String promotion = mBuildOrderPreSale.getPromotion();
                            if (!TextUtils.isEmpty(promotion)) {
                                promotion = promotion.replace("：", ": ");
                                tv_promotion.setText(promotion);
                            } else {
                                tv_promotion.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(mBuildOrderPreSale.getTip())) {
                                String tip = mBuildOrderPreSale.getTip();
                                tip = tip.replace("：", ": ");
                                tv_rest.setText(tip);
                            } else {
                                tv_rest.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(mBuildOrderPreSale.getDeliverTitle()) && !TextUtils.isEmpty(mBuildOrderPreSale.getDeliverValue())) {
                                tv_deliver.setText(mBuildOrderPreSale.getDeliverTitle() + ": " + mBuildOrderPreSale.getDeliverValue());
                            } else {
                                tv_deliver.setVisibility(View.GONE);
                            }

                            if (!TextUtils.isEmpty(mBuildOrderPreSale.getNotifyTitle()) && !TextUtils.isEmpty(mBuildOrderPreSale.getNotifyValue())) {
                                String title = mBuildOrderPreSale.getNotifyTitle();
                                title = title.replace("：", ": ");
                                tv_notify.setText(title + mBuildOrderPreSale.getNotifyValue());
                            } else {
                                tv_notify.setVisibility(View.GONE);
                            }
                            detailViewHolder.ll_presale.addView(presaleView);
                            isFisrst = false;
                        }

                    } else {
                        detailViewHolder.ll_presale.setVisibility(View.GONE);
                    }
                    int size = 0;
                    ArrayList<String> showgoods = invalidGoods;
                    int textColor = mContext.getResources().getColor(R.color.ytm_buildorder_invalid);
                    if (hasInvalid)

                    {
                        showgoods = invalidGoods;
                        size = invalidGoods.size();
                    }

                    if (size <= 0) {
                        // 如果没有无效的商品宝贝，那么显示有效的商品宝贝
                        showgoods = validGoods;
                        textColor = mContext.getResources().getColor(R.color.ytm_buildorder_sku);
                    }

                    if (showgoods != null) {
                        size = showgoods.size();
                    }

                    if (hasInvalid) {
                        detailViewHolder.skuView.setMaxLines(3);
                        detailViewHolder.itemView.setVisibility(View.GONE);
                    } else if (!onlyOneItem) {
                        detailViewHolder.skuView.setMaxLines(2);
                        detailViewHolder.itemView.setVisibility(View.GONE);
                    } else {
                        detailViewHolder.skuView.setMaxLines(1);
                        detailViewHolder.itemView.setVisibility(View.VISIBLE);
                    }
                    shopicon.setVisibility(View.VISIBLE);
                    detailViewHolder.listView.setVisibility(View.VISIBLE);

                    //SKUVIIEw
                    if (!onlyOneItem) {
                        TextPaint paint = detailViewHolder.skuView.getPaint();
                        int paddingLeft = detailViewHolder.skuView.getPaddingLeft();
                        int paddingRight = detailViewHolder.skuView.getPaddingRight();
                        int bufferWidth = (int) paint.getTextSize() * 1;
                        int availableTextWidth = (listWidth - paddingLeft - paddingRight) - bufferWidth;
                        StringBuilder invalid = new StringBuilder();
                        invalid.setLength(0);
                        for (int index = 0; index < size; index++) {
                            String ellipsizeStr = (String) TextUtils.ellipsize(showgoods.get(index), paint, availableTextWidth, TextUtils.TruncateAt.END);
                            invalid.append(ellipsizeStr);
                            invalid.append('\n'); // 添加换行符
                        }
                        detailViewHolder.skuView.setText(invalid.toString());
                        detailViewHolder.skuView.setTextColor(textColor);
                    }
                    if (isLast) {
                        detailViewHolder.divider.setVisibility(View.GONE);
                    }
                    detailContainer.addView(detail);
                } else if (c instanceof OrderBondSyntheticComponent) {
                    detailContainer.removeAllViews();
                    ArrayList<String> invalidGoods = new ArrayList<String>();
                    ArrayList<String> validGoods = new ArrayList<String>();
                    OrderBondSyntheticComponent component = (OrderBondSyntheticComponent) c;

                    // 初始化左侧内容的显示位置
                    if (component.orderInfoComponent != null) {
                        titleView.setText(component.orderInfoComponent.getTitle());
                        ImageLoaderManager.getImageLoaderManager(mContext).displayImage(component.orderInfoComponent.getIcon(), shopicon);
                    }
                    boolean firstRow = true;

                    for (OrderSyntheticComponent orderSyntheticComponent : component.orders) {

                        boolean onlyOneItem = orderSyntheticComponent.getGoodsSyntheticComponents().size() == 1;
                        boolean hasInvalid = false;
                        if (orderSyntheticComponent.getOrderComponent() == null || !orderSyntheticComponent.getOrderComponent().getFields().getBoolean("valid")) {
                            if (!hasInvalid)
                                invalidGoods.add(mContext.getResources().getString(R.string.ytm_buildorder_invalid));
                            for (GoodsSyntheticComponent goodsSyntheticComponent : orderSyntheticComponent.getGoodsSyntheticComponents()) {
                                invalidGoods.add(goodsSyntheticComponent.getItemInfoComponent().getTitle());
                            }
                            hasInvalid = true;
                        }
                        LinearLayout detail = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ytm_activity_buildorder_gooditemsubview, null);
                        CartItemDetailViewHolder detailViewHolder = new CartItemDetailViewHolder();
                        detailViewHolder.listView = (LinearLayout) detail.findViewById(R.id.buildorder_info_listview);
                        detailViewHolder.sumMsg = (TextView) detail.findViewById(R.id.sumMsg);
                        detailViewHolder.itemView = (TextView) detail.findViewById(R.id.buildorder_item);
                        detailViewHolder.skuView = (TextView) detail.findViewById(R.id.buildorder_sku);
                        detailViewHolder.ll_presale = (LinearLayout) detail.findViewById(R.id.ll_presale);
                        detailViewHolder.presaleAddress = (LinearLayout) detail.findViewById(R.id.presaleaddress);
                        detailViewHolder.presaleAddressLabel = (TextView) detail.findViewById(R.id.presaleaddresslabel);
                        detailViewHolder.divider = (ImageView) detail.findViewById(R.id.divider);
                        detailViewHolder.sumMsg2 = (TextView) detail.findViewById(R.id.sumMsg2);
                        detailViewHolder.tvRebate = (TextView) detail.findViewById(R.id.tv_buildorder_rebate);
                        detailViewHolder.ivRebate = (ImageView) detail.findViewById(R.id.iv_buildorder_rebate_item);
                        detailViewHolder.layoutRebate = (RelativeLayout) detail.findViewById(R.id.layout_buildorder_rebate);
                        detailViewHolder.listView.removeAllViews();


                        //返利信息
                        //返利信息
                        if (rebateBoList != null)
                            for (BuildOrderRebateBo buildOrderRebateBo : rebateBoList) {
                                if (orderSyntheticComponent != null && orderSyntheticComponent.getOrderPayComponent() != null && orderSyntheticComponent.getOrderPayComponent().getId() != null && buildOrderRebateBo != null && buildOrderRebateBo.getOrderId() != null) {
                                    if (buildOrderRebateBo.getOrderId().equals(orderSyntheticComponent.getOrderPayComponent().getId())) {
                                        rebateCoupon = buildOrderRebateBo.getCoupon();
                                        rebateCouponMessage = buildOrderRebateBo.getMessage();
                                        rebatePic = buildOrderRebateBo.getPicUrl();
                                    }
                                }
                            }

                        //rebateCoupon不为空，则是正常返利
                        if (!TextUtils.isEmpty(rebateCoupon)) {
                            rebateCoupon = Utils.getRebateCoupon(rebateCoupon);
                            if (rebateCoupon != null) {
                                if (!TextUtils.isEmpty(rebateCouponMessage)) {
                                    rebateCouponMessage = rebateCouponMessage + ": ";
                                }
                                detailViewHolder.layoutRebate.setVisibility(View.VISIBLE);
                                SpannableStringBuilder builder = new SpannableStringBuilder(rebateCouponMessage + rebateCoupon + "元");
                                builder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.aliuser_color_black)), 0, rebateCouponMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                detailViewHolder.tvRebate.setText(builder);
                                if (!TextUtils.isEmpty(rebatePic)) {
                                    detailViewHolder.ivRebate.setVisibility(View.VISIBLE);
                                    ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebatePic, detailViewHolder.ivRebate);
                                } else {
                                    detailViewHolder.ivRebate.setVisibility(View.GONE);
                                }

                            } else {
                                detailViewHolder.layoutRebate.setVisibility(View.GONE);
                            }
                        } else {
                            //rebateCoupon为空，则是预售返利

                            detailViewHolder.layoutRebate.setVisibility(View.VISIBLE);
                            detailViewHolder.tvRebate.setText(rebateCouponMessage);
                            if (!TextUtils.isEmpty(rebatePic)) {
                                detailViewHolder.ivRebate.setVisibility(View.VISIBLE);
                                ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebatePic, detailViewHolder.ivRebate);
                            } else {
                                detailViewHolder.ivRebate.setVisibility(View.GONE);
                            }
                        }


                        //todo

                        if (orderSyntheticComponent.getPresaleAddressComponent() != null) {
                            detailViewHolder.presaleAddress.setVisibility(View.VISIBLE);
                            detailViewHolder.presaleAddressLabel.setText(String.format("%s\n%s", orderSyntheticComponent.getPresaleAddressComponent().getFields().getString("value"),
                                    orderSyntheticComponent.getPresaleAddressComponent().getFields().getString("desc")));
                        } else {
                            detailViewHolder.presaleAddress.setVisibility(View.GONE);
                        }
                        int count = 0;
                        double money = 0;
                        for (GoodsSyntheticComponent goodComponent : orderSyntheticComponent.getGoodsSyntheticComponents()) {
                            ItemInfoComponent itemInfoComponent = goodComponent.getItemInfoComponent();
                            ItemPayComponent itemPayComponent = goodComponent.getItemPayComponent();
                            ItemComponent itemComponent = goodComponent.getItemComponent();
                            if (itemComponent != null) {
                                count += Integer.valueOf(itemPayComponent.getQuantity());
                                money += Double.valueOf(itemPayComponent.getAfterPromotionPrice());
                            }

                            validGoods.add(itemInfoComponent.getTitle());
                            if (orderSyntheticComponent.getGoodsSyntheticComponents().size() <= 1) {
                                detailViewHolder.skuView.setText(itemInfoComponent.getSkuInfo());
                                detailViewHolder.itemView.setText(itemInfoComponent.getTitle());
                                detailViewHolder.itemView.setVisibility(View.VISIBLE);
                            } else {
                                detailViewHolder.itemView.setVisibility(View.GONE);
                            }
                        }
                        orderSyntheticComponent.buildPriorityGoodItems(mContext);
                        List<GoodsDisplayInfo.GoodsItem> goodsItems = orderSyntheticComponent.getGoodsItems();
                        for (int i = 0; i < goodsItems.size(); i++) {
                            View v = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_info_item_layout, null);
                            ((TextView) v.findViewById(R.id.goods_info_documents)).setText(goodsItems.get(i).documents);
                            ((TextView) v.findViewById(R.id.goods_info_value)).setText(goodsItems.get(i).value);
                            detailViewHolder.listView.addView(v);
                        }

                        if (orderSyntheticComponent.getFinalPayComponent() != null) {
//                            String yuan = mContext.getResources().getString(
//                                    R.string.ytm_buildorder_unit_yuan);
//                            SpannableStringBuilder ssb = onHandlerSpanned(String.format("合计: %s", price + yuan), true);
//                            ssb.insert(0, String.format("共%s件商品 ", orderSyntheticComponent.orderPayComponent.getQuantity()));
                            String payValue = orderSyntheticComponent.getFinalPayComponent().getFields().getString("desc");
                            Pattern pattern = Pattern.compile("\\((.*)\\)");
                            Matcher matcher = pattern.matcher(payValue);
                            SpannableStringBuilder ssb = null;
                            if (matcher.find() && !TextUtils.isEmpty(matcher.group(1)) && matcher.start() > 0) {
                                ssb = onHandlerSpanned(String.format("%s %s", "尾款小计",
                                        payValue.substring(0, matcher.start())), true);
                                ssb.insert(0, String.format("共%s件商品 ", count));
                                detailViewHolder.sumMsg2.setText(matcher.group(1));
                                detailViewHolder.sumMsg2.setVisibility(View.VISIBLE);
                            } else {
                                ssb = onHandlerSpanned(String.format("%s %s", "尾款小计", payValue), true);
                                ssb.insert(0, String.format("共%s件商品 ", count));
                                detailViewHolder.sumMsg2.setVisibility(View.GONE);
                            }

                            detailViewHolder.sumMsg.setText(ssb);
                            detailViewHolder.sumMsg.setVisibility(View.VISIBLE);
                        } else {
                            String yuan = mContext.getResources().getString(
                                    R.string.ytm_buildorder_unit_yuan);
                            SpannableStringBuilder ssb = onHandlerSpanned(String.format("小计: %s", money + yuan), true);
                            ssb.insert(0, String.format("共%s件商品 ", count));
                            detailViewHolder.sumMsg.setText(ssb);
                            detailViewHolder.sumMsg.setVisibility(View.VISIBLE);
                        }

                        ArrayList<String> showgoods = new ArrayList<String>();
                        int size = 0;
                        for (GoodsSyntheticComponent goodsSyntheticComponent : orderSyntheticComponent.getGoodsSyntheticComponents()) {
                            if (goodsSyntheticComponent.getItemInfoComponent() != null)
                                showgoods.add(goodsSyntheticComponent.getItemInfoComponent().getTitle());
                        }
                        if (showgoods != null) {
                            size = showgoods.size();
                        }

                        if (hasInvalid) {
                            detailViewHolder.skuView.setMaxLines(3);
                            detailViewHolder.itemView.setVisibility(View.GONE);
                        } else if (!onlyOneItem) {
                            detailViewHolder.skuView.setMaxLines(2);
                            detailViewHolder.itemView.setVisibility(View.GONE);
                        } else {
                            detailViewHolder.skuView.setMaxLines(1);
                            detailViewHolder.itemView.setVisibility(View.VISIBLE);
                        }
                        shopicon.setVisibility(View.VISIBLE);
                        detailViewHolder.listView.setVisibility(View.VISIBLE);

                        //SKUVIIEw
                        if (!onlyOneItem) {
                            TextPaint paint = detailViewHolder.skuView.getPaint();
                            int paddingLeft = detailViewHolder.skuView.getPaddingLeft();
                            int paddingRight = detailViewHolder.skuView.getPaddingRight();
                            int bufferWidth = (int) paint.getTextSize() * 1;
                            int availableTextWidth = (listWidth - paddingLeft - paddingRight) - bufferWidth;
                            StringBuilder invalid = new StringBuilder();
                            invalid.setLength(0);
                            for (int index = 0; index < size; index++) {
                                String ellipsizeStr = (String) TextUtils.ellipsize(showgoods.get(index), paint, availableTextWidth, TextUtils.TruncateAt.END);
                                invalid.append(ellipsizeStr);
                                invalid.append('\n'); // 添加换行符
                            }
                            detailViewHolder.skuView.setText(invalid.toString());
                            int textColor = mContext.getResources().getColor(R.color.ytm_buildorder_invalid);
                            detailViewHolder.skuView.setTextColor(textColor);
                        }
                        if (isLast && component.orders.indexOf(orderSyntheticComponent) == component.orders.size() - 1)
                            detailViewHolder.divider.setVisibility(View.GONE);
                        detailContainer.addView(detail);
                    }

                }


            }

        }
    }

    private SpannableStringBuilder onHandlerSpanned(String src, boolean change) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        if (change) {
            int foregroundColor = 0xffff6600;
            Pattern p = Pattern.compile("¥*\\d+(.\\d)*元*");
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
//                if (end < src.length()) {
//                    end += 1; // 带上单位, 或者小数点
//                }
                style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        return style;
    }
}
