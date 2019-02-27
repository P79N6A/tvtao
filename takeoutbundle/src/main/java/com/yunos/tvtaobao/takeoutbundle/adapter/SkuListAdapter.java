package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.listener.OnSkuValueListener;

import java.util.List;
import java.util.Map;


/**
 * Created by chenjiajuan on 17/10/16.
 */

public class SkuListAdapter extends RecyclerView.Adapter<SkuListAdapter.ItemViewHolder> {
    private static final String TAG = "SkuListAdapter";
    private Context context;
    private OnSkuValueListener onSkuValueListener;
    private OnItemSelectedClickListener onItemSelectedClickListener;
    private ImageView ivLastSkuSelect = null;
    private TextView tvLastSkuValue = null;
    private LinearLayout llLastSkuItem = null;
    private String pageName = null;
    private String itemId = null;
    private int outPosition = 0;
    private boolean isSelected = false;
    private int lastPosition = 0;
    private static final String skuTAG="xxx";//用以描述规格


    private List<ItemListBean.SkuListBean> skuValueList;
    private OnItemFocusChangeListener onItemFocusChangeListener;

    public SkuListAdapter(Context context, OnSkuValueListener onSkuValueListener,
                          List<ItemListBean.SkuListBean> skuLists,
                          String pageName, String itemId, int outPosition) {
        this.skuValueList = skuLists;
        this.context = context;
        this.onSkuValueListener = onSkuValueListener;
        this.pageName = pageName;
        this.itemId = itemId;
        this.outPosition = outPosition;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flow_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.tvSkuValue.setText(skuValueList.get(position).getTitle());
        //默认为灰色
        holder.ivSkuSelect.setImageLevel(3);

        String quantity=skuValueList.get(position).getQuantity();
        //设置失效的sku属性
        if (!TextUtils.isEmpty(quantity)){
            if ("0".equals(quantity)){
                setSkuDisable(holder);
            }else {
                holder.llSkuItem.setEnabled(true);
                holder.llSkuItem.setFocusable(true);
                holder.tvSkuValue.getPaint().setFlags(0);
            }
        }else {
            setSkuDisable(holder);
        }
        holder.tvSkuValue.getPaint().setAntiAlias(true);
        holder.llSkuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected()) {
                    utSkuPropSelectedClick();
                    holder.llSkuItem.setSelected(true);
                    //添加当前项
                    onSkuValueListener.addSelectSkuData(skuValueList.get(position).getSkuId(), skuTAG, skuValueList.get(position).getTitle());
                    //移除上一项
                    if (lastPosition != position) {
                        onSkuValueListener.removeSelectedView(skuTAG, skuValueList.get(lastPosition).getTitle());
                    }
                    //获取商品价格，优先获取优惠价
                    String newPrice="";
                    if (!TextUtils.isEmpty(skuValueList.get(position).getPromotionPrice())&&!skuValueList.get(position).getPromotionPrice().equals("0")){
                        newPrice=skuValueList.get(position).getPromotionPrice();
                    }else {
                        if (!TextUtils.isEmpty(skuValueList.get(position).getPrice())){
                            newPrice=skuValueList.get(position).getPrice();
                        }
                    }
                    AppDebug.e(TAG,""+skuValueList.get(position).getPrice()+" , "+skuValueList.get(position).getPromotionPrice());
                    //更新商品的数量和价格
                    onSkuValueListener.updatePriceQuantity(newPrice, skuValueList.get(position).getQuantity());
                    if (v.isFocusable()) {
                        holder.ivSkuSelect.setImageLevel(2);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ffffff));

                    } else {
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_202020));
                        holder.ivSkuSelect.setImageLevel(4);

                    }
                    if (lastPosition!=position){
                        if (llLastSkuItem != null) {
                            llLastSkuItem.setSelected(false);
                        }
                        if (ivLastSkuSelect != null) {
                            ivLastSkuSelect.setImageLevel(3);
                        }
                        if (tvLastSkuValue != null) {
                            tvLastSkuValue.setTextColor(context.getResources().getColor(R.color.color_202020));
                        }
                    }
                    isSelected = true;
                    if (onItemSelectedClickListener!=null){
                        AppDebug.e(TAG,"onItemSelectedClickListener!=null。。。。。。。");
                        onItemSelectedClickListener.onItemSelectedClick(outPosition,position);
                    }
                } else {
                    utSkuPropCancelClick();
                    holder.llSkuItem.setSelected(false);
                    if (v.isFocusable()) {
                        //当未被选中，已被聚焦时，圈为白色
                        holder.ivSkuSelect.setImageLevel(1);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ffffff));

                    } else {
                        //当未被选中，未聚焦，圈为灰色
                        holder.ivSkuSelect.setImageLevel(3);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_202020));
                    }
                    onSkuValueListener.removeSelectedView(skuTAG, skuValueList.get(position).getTitle());
                    isSelected = false;
                }

                llLastSkuItem = holder.llSkuItem;
                ivLastSkuSelect = holder.ivSkuSelect;
                tvLastSkuValue = holder.tvSkuValue;
                lastPosition = position;
            }
        });
        holder.llSkuItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //焦点移动时,聚焦
                if (hasFocus) {
                    //当已被选中，icon为√红，背景白色
                    if (v.isSelected()) {
                        holder.ivSkuSelect.setImageLevel(2);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ffffff));
                    } else {
                        //当未被选中，圈为白色全
                        holder.ivSkuSelect.setImageLevel(1);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ffffff));
                    }
                    if (onItemFocusChangeListener != null) {
                        onItemFocusChangeListener.onItemFocusChange(outPosition, position);
                    }
                } else {
                    //焦点移动，未聚焦
                    if (v.isSelected()) {
                        //当已被选中，√为白色，背景为红色
                        holder.ivSkuSelect.setImageLevel(4);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ff6000));
                    } else {
                        //当未被选中，圈为灰色
                        holder.ivSkuSelect.setImageLevel(3);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_202020));

                    }
                }

            }
        });


    }

    public void setSkuDisable(ItemViewHolder holder){
        holder.llSkuItem.setEnabled(false);
        holder.llSkuItem.setFocusable(false);
        holder.ivSkuSelect.setBackgroundResource(R.drawable.icon_sku_disable);
        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_a2aaba));
        holder.tvSkuValue.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    public void setOnItemSelectedClickListener(OnItemSelectedClickListener onItemClickListener){
        this.onItemSelectedClickListener=onItemClickListener;

    }

    public boolean isSelected() {
        return isSelected;
    }

    /**
     * 规格取消事件
     */
    private void utSkuPropCancelClick() {
        Map<String, String> properties = Utils.getProperties();
        if (!TextUtils.isEmpty(itemId)) {
            properties.put("item id", itemId);
        }

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_SPEC);

        if (!TextUtils.isEmpty(pageName)) {
            Utils.utControlHit(pageName, "Page_waimai_shop_grant_sku_button_cancel", properties);
        }
    }

    /**
     * 规格选中事件
     */
    private void utSkuPropSelectedClick() {
        Map<String, String> properties = Utils.getProperties();
        if (!TextUtils.isEmpty(itemId)) {
            properties.put("item id", itemId);
        }

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());
        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_SPEC);

        if (!TextUtils.isEmpty(pageName)) {
            Utils.utControlHit(pageName, "Page_waimai_shop_grant_sku_button_choose", properties);
        }


    }

    @Override
    public int getItemCount() {
        return skuValueList == null ? 0 : skuValueList.size();

    }

    public interface OnItemFocusChangeListener {
        void onItemFocusChange(int outPosition, int position);
    }

    public interface  OnItemSelectedClickListener{
        void onItemSelectedClick(int outPosition,int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSkuValue;
        private ImageView ivSkuSelect;
        private LinearLayout llSkuItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvSkuValue = (TextView) itemView.findViewById(R.id.tv_sku_value);
            ivSkuSelect = (ImageView) itemView.findViewById(R.id.iv_sku_select);
            llSkuItem = (LinearLayout) itemView.findViewById(R.id.ll_sku_item);
        }
    }
}
