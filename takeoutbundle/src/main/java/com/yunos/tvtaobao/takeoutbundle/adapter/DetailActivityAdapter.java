package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopHomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiajuan on 17/12/15.
 *
 * @describe 详情页 公告与活动
 */

public class DetailActivityAdapter extends RecyclerView.Adapter<DetailActivityAdapter.ViewHolder> {
    private Context context;
    private List<Object> list;

    public DetailActivityAdapter(Context context, ShopDetailData.StoreDetailDTOBean storeDetailDTOBean) {
        this.context = context;
        list = new ArrayList<Object>();
        if (storeDetailDTOBean.getActivityList() != null && storeDetailDTOBean.getActivityList().size() > 0) {
            list.addAll(storeDetailDTOBean.getActivityList());
        }
        if (storeDetailDTOBean.getServiceList() != null && storeDetailDTOBean.getServiceList().size() > 0) {
            list.addAll(storeDetailDTOBean.getServiceList());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pop_detail_activity,
                parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.get(position).getClass().equals(ShopDetailData.StoreDetailDTOBean.ServiceListBean.class)) {
            ShopDetailData.StoreDetailDTOBean.ServiceListBean ServiceListBean = (ShopDetailData.
                    StoreDetailDTOBean.ServiceListBean) list.get(position);
            if (!TextUtils.isEmpty(ServiceListBean.getDescription())) {
                holder.tvDescription.setText(ServiceListBean.getDescription());
            }

            if (!TextUtils.isEmpty(ServiceListBean.getIcon())) {
                ImageLoaderManager.getImageLoaderManager(context).displayImage(ServiceListBean.getIcon(), holder.ivIcon);
                holder.ivIcon.setBackgroundColor(0x00ffffff);
            } else {
                if (!TextUtils.isEmpty(ServiceListBean.getType())) {
                    int resource = judeImageType(ServiceListBean.getType());
                    if (resource != -1) {
                        holder.ivIcon.setBackgroundResource(resource);
                    }
                }
            }
        } else if (list.get(position).getClass().equals(ShopDetailData.StoreDetailDTOBean.ActivityListBean.class)) {
            ShopDetailData.StoreDetailDTOBean.ActivityListBean activityListBea = (ShopDetailData.
                    StoreDetailDTOBean.ActivityListBean) list.get(position);
            if (!TextUtils.isEmpty(activityListBea.getDescription())) {
                holder.tvDescription.setText(activityListBea.getDescription());
            }

            if (!TextUtils.isEmpty(activityListBea.getIcon())) {
                ImageLoaderManager.getImageLoaderManager(context).displayImage(activityListBea.getIcon(), holder.ivIcon);
                holder.ivIcon.setBackgroundColor(0x00ffffff);
            } else {
                if (!TextUtils.isEmpty(activityListBea.getType())) {
                    int resource = judeImageType(activityListBea.getType());
                    if (resource != -1) {
                        holder.ivIcon.setBackgroundResource(resource);
                    }
                } else {
                    /**
                     description:"本店为超级外卖月活动商户"
                     icon:"https://gw.alicdn.com/tfs/TB1RzvLklfH8KJjy1XbXXbLdXXa-84-84.png"
                     id:"0"
                     name:"本店为超级外卖月活动商户"
                     sortId:"0"
                     storeId:"0"
                     */
                    //TODO 超级用户，无type字段,暂根据id。或可根据name/description,其他情况遇到再处理。。。。。。。。。
                    if (!TextUtils.isEmpty(activityListBea.getId()) && activityListBea.getId().equals("0")) {
                        holder.ivIcon.setBackgroundResource(R.drawable.icon_detail_member);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 判断图片类型
     *
     * @param type 类型值
     * @return 资源id
     */
    public int judeImageType(String type) {
        if (type.equals("1")) {
            //折
            return R.drawable.icon_detail_discount;
        } else if (type.equals("2")) {
            //票
            return R.drawable.icon_detail_ticket;
        } else if (type.equals("3")) {
            //准
            return R.drawable.icon_detail_punctual;
        } else if (type.equals("4")) {
            //特
            return R.drawable.icon_detail_bargain;
        } else if (type.equals("102")) {
            //减
            return R.drawable.icon_detail_reduce;
        } else if (type.equals("106")) {
            //赠
            return R.drawable.icon_detail_present;
        } else if (type.equals("401")) {
            //券
            return R.drawable.icon_detail_coupon;
        } else if (type.equals("402")) {
            //返
            return R.drawable.icon_detail_rebate;
        } else {
            return -1;
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
        }
    }
}
