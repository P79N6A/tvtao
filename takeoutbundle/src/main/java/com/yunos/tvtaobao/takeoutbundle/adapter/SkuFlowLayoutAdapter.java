package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
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
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.listener.OnSkuValueListener;

import java.util.List;
import java.util.Map;


/**
 * Created by chenjiajuan on 17/10/16.
 *
 * @describe 非价格规格属性
 */

public class SkuFlowLayoutAdapter extends RecyclerView.Adapter<SkuFlowLayoutAdapter.ItemViewHolder> {
    private static final String TAG = "SkuFlowLayoutAdapter";
    private Context context;
    private OnSkuValueListener onSkuValueListener;
    private int lastPosition = 0;
    private ItemListBean.MultiAttrBean.AttrListBean attrLists;
    private List<String> valueBeans;
    private ImageView ivLastSkuSelect = null;
    private TextView tvLastSkuValue = null;
    private LinearLayout llLastSkuItem = null;
    private String itemId;
    private String pageName;
    private int outPosition = 0;
    private boolean isSelected = false;
    private OnItemFocusChangeListener onItemFocusChangeListener;
    private OnItemSelectedClickListener  onItemSelectedClickListener;

    public SkuFlowLayoutAdapter(Context context, OnSkuValueListener onSkuValueListener, ItemListBean.MultiAttrBean.AttrListBean attrLists,
                                String itemId, String pageName, int position) {
        this.attrLists = attrLists;
        this.valueBeans = attrLists.getValue();
        this.context = context;
        this.onSkuValueListener = onSkuValueListener;
        this.itemId = itemId;
        this.pageName = pageName;
        this.outPosition = position;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flow_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.tvSkuValue.setText(attrLists.getValue().get(position));
        //默认为灰色
        holder.ivSkuSelect.setImageLevel(3);
        holder.llSkuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected()) {
                    utSkuPropSelectedClick();
                    holder.llSkuItem.setSelected(true);
                    isSelected = true;
                    //添加这一项
                    onSkuValueListener.addSelectSkuData(null, attrLists.getName(), valueBeans.get(position));
                    //移除上一项
                    if (lastPosition != position) {
                        onSkuValueListener.removeSelectedView(attrLists.getName(), valueBeans.get(lastPosition));
                    }
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
                    if (onItemSelectedClickListener!=null){
                        AppDebug.e(TAG,"onItemSelectedClickListener!=null");
                        onItemSelectedClickListener.onItemSelectedClick(outPosition,position);
                    }
                } else {
                    utSkuPropCancelClick();
                    holder.llSkuItem.setSelected(false);
                    isSelected = false;
                    if (v.isFocusable()) {
                        //当未被选中，已被聚焦时，圈为白色
                        holder.ivSkuSelect.setImageLevel(1);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_ffffff));

                    } else {
                        //当未被选中，未聚焦，圈为灰色
                        holder.ivSkuSelect.setImageLevel(3);
                        holder.tvSkuValue.setTextColor(context.getResources().getColor(R.color.color_202020));
                    }
                    onSkuValueListener.removeSelectedView(attrLists.getName(), valueBeans.get(position));
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

        if (!TextUtils.isEmpty(pageName)) {
            Utils.utControlHit(pageName, "Page_waimai_shop_grant_sku_button_choose", properties);
        }


    }

    @Override
    public int getItemCount() {
        return valueBeans == null ? 0 : valueBeans.size();

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
