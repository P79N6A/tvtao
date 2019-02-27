package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunos.tv.core.util.StringUtils;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;
import com.yunos.tvtaobao.takeoutbundle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiajuan on 17/12/25.
 *
 * @describe  外卖--优惠券
 */

public class VouchersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VouchersMO.DetailsBean> details;
    private static final int TYPE_TOP = 0;//头部
    private static final int TYPE_ITEM = 1;//item项
    private Context context;
    public VouchersAdapter(Context context,VouchersMO vouchersMO){
        this.context=context;
        details=new ArrayList<>();
        VouchersMO.DetailsBean detailsBean=new VouchersMO.DetailsBean();
        detailsBean.setStoreName("现已领取商家代金券");
        detailsBean.setStatus("可以在我的优惠中查看");
        this.details.add(detailsBean);
        this.details.addAll(vouchersMO.getDetails());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_TOP){
            View view=LayoutInflater.from(context).inflate(R.layout.item_vouchers_top,parent,false);
            return  new TopViewHolder(view);
        }else if (viewType==TYPE_ITEM){
            return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_vouchers_item,parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VouchersMO.DetailsBean detailsBean=details.get(position);
        if (holder instanceof TopViewHolder){
            if (!TextUtils.isEmpty(detailsBean.getStoreName())){
                ((TopViewHolder) holder).tvTop.setText(detailsBean.getStoreName());
            }
            if (!TextUtils.isEmpty(detailsBean.getStatus())){
                ((TopViewHolder) holder).tvLook.setText(detailsBean.getStatus());
            }
        }else if (holder instanceof ItemViewHolder ){
            if (!TextUtils.isEmpty(detailsBean.getStoreName())){
                ((ItemViewHolder) holder).tvStoreName.setText(detailsBean.getStoreName());
            }
            if (!TextUtils.isEmpty(detailsBean.getDeductAmount())){
                Double price=Double.parseDouble(detailsBean.getDeductAmount())/100;
                String price2=StringUtils.resolvePrice(String.valueOf(price));
                if (!TextUtils.isEmpty(price2)){
                     ((ItemViewHolder) holder).tvDeductAmount.setText("¥ "+price2);
                }
            }
            if (!TextUtils.isEmpty(detailsBean.getDeductThreshold())){
                Double price=Double.parseDouble(detailsBean.getDeductThreshold())/100;
                String price2=StringUtils.resolvePrice(String.valueOf(price));
                if (!TextUtils.isEmpty(price2)){
                    ((ItemViewHolder) holder).tvDeductThreshold.setText("满"+price2+"使用");
                }
            }
            if (!TextUtils.isEmpty(detailsBean.getEndTime())){
                ((ItemViewHolder) holder).tvEndTime.setText("有效期至"+detailsBean.getEndTime());
            }
            if (position%3==0){
             holder.itemView.setFocusable(true);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return  TYPE_TOP;

        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return details==null?0:details.size();
    }

    public class TopViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTop,tvLook;
        public TopViewHolder(View itemView) {
            super(itemView);
            tvLook= (TextView) itemView.findViewById(R.id.tv_look);
            tvTop= (TextView) itemView.findViewById(R.id.tv_top);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView tvStoreName;
        private TextView tvDeductAmount;
        private TextView tvDeductThreshold;
        private TextView tvEndTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvStoreName= (TextView) itemView.findViewById(R.id.tv_storeName);
            tvDeductAmount= (TextView) itemView.findViewById(R.id.tv_deductAmount);
            tvDeductThreshold= (TextView) itemView.findViewById(R.id.tv_deductThreshold);
            tvEndTime= (TextView) itemView.findViewById(R.id.tv_endTime);
        }
    }
}


