package com.tvtaobao.voicesdk.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.bo.LogisticsDo;
import com.tvtaobao.voicesdk.utils.DateUtil;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.tv.core.common.ImageLoaderManager;

import java.util.List;


/**
 * Created by xutingting on 2017/9/16.
 */

public class LogisticsQueryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    //    private boolean needShowMore = false;
    private List<LogisticsDo> mList;
    private ImageLoaderManager mImageLoaderManager;
    private LayoutInflater inflater;

//    private enum ItemType {
//        TYPE_LOGISTICS, TYPE_MORE
//    }


    public LogisticsQueryAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);


    }

    private boolean isOnKey = false;

    public void setAction(boolean isOnKey) {
        this.isOnKey = isOnKey;
    }

    public void initData(List<LogisticsDo> list) {
//        if (list != null && list.size() >= 3) {
//            needShowMore = false;
//        } else {
//            needShowMore = true;
//        }
        this.mList = list;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (viewType == ItemType.TYPE_LOGISTICS.ordinal()) {
        View view = inflater.inflate(R.layout.dialog_logistics_query_item, parent, false);
        return new LogisticsQueryViewHolder(view);
//        } else {
//            View view = inflater.inflate(R.layout.dialog_logistics_query_item_more, parent, false);
//            return new MoreViewHolder(view);
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isOnKey) {
            holder.itemView.setFocusable(true);
        } else {
            holder.itemView.setFocusable(false);
        }

        LogisticsQueryViewHolder logisticsQueryViewHolder = (LogisticsQueryViewHolder) holder;
        LogisticsDo logisticsDo = mList.get(position);

        if (!TextUtils.isEmpty(logisticsDo.getAuctionPicUrl())) {
            mImageLoaderManager.displayImage(logisticsDo.getAuctionPicUrl(), logisticsQueryViewHolder.ivItem);
        }
        if (!TextUtils.isEmpty(logisticsDo.getTraceStanderdDesc())) {
            logisticsQueryViewHolder.tvInfo.setText(logisticsDo.getTraceStanderdDesc());
        }
        if (!TextUtils.isEmpty(logisticsDo.getTraceStatusDesc())) {
            Log.d("getTraceStatusDesc",logisticsDo.getTraceStatusDesc());
            logisticsQueryViewHolder.ivStatus.setVisibility(View.VISIBLE);
            if (logisticsDo.getTraceStatusDesc().equals("已签收")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_signed);
            } else if (logisticsDo.getTraceStatusDesc().equals("在途中") || logisticsDo.getTraceStatusDesc().equals("运输中")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_in_transit);
            } else if (logisticsDo.getTraceStatusDesc().equals("已揽件")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_received);
            } else if (logisticsDo.getTraceStatusDesc().equals("派件中") || logisticsDo.getTraceStatusDesc().equals("派送中")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_send_pieces);
            } else if (logisticsDo.getTraceStatusDesc().equals("已发货")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_shipped);
            } else if(logisticsDo.getTraceStatusDesc().equals("未发货")) {
                logisticsQueryViewHolder.ivStatus.setImageResource(R.drawable.icon_logistics_not_shipped);
            }else {
                logisticsQueryViewHolder.ivStatus.setVisibility(View.INVISIBLE);
                logisticsQueryViewHolder.ivStatus.setImageResource(0);
            }
        } else {
            logisticsQueryViewHolder.ivStatus.setVisibility(View.INVISIBLE);
            logisticsQueryViewHolder.ivStatus.setImageResource(0);
        }
        if (!TextUtils.isEmpty(logisticsDo.getTraceGmtCreate())) {
            String date = DateUtil.getStrTime(DateUtil.dateToStamp(logisticsDo.getTraceGmtCreate()));
            logisticsQueryViewHolder.tvTime.setText(date);
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ViewCompat.animate(view).scaleX(1.17f).scaleY(1.17f).start();
                } else {
                    ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).start();
                }
            }
        });

    }

//    @Override
//    public int getItemViewType(int position) {
//        if (needShowMore) {
//            if (position == getItemCount() - 1) {
//                return ItemType.TYPE_MORE.ordinal();
//            } else {
//                return ItemType.TYPE_LOGISTICS.ordinal();
//            }
//        } else {
//            return ItemType.TYPE_LOGISTICS.ordinal();
//        }
//    }

    @Override
    public int getItemCount() {
//        return mList == null ? 1 : mList.size() >= 3 ? mList.size() : mList.size() + 1;
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    class LogisticsQueryViewHolder extends RecyclerView.ViewHolder {
        private RoundImageView ivItem;
        private TextView tvInfo;
        private ImageView ivStatus;
        private TextView tvTime;

        public LogisticsQueryViewHolder(View itemView) {
            super(itemView);
            ivItem = (RoundImageView) itemView.findViewById(R.id.iv_item);
            ivItem.setRound(true, true, false, false);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            ivStatus = (ImageView) itemView.findViewById(R.id.iv_status);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

    class MoreViewHolder extends RecyclerView.ViewHolder {

        public MoreViewHolder(View itemView) {
            super(itemView);
        }
    }

}
