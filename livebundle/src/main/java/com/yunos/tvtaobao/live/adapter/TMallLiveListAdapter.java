package com.yunos.tvtaobao.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.activity.TBaoLiveActivity;
import com.yunos.tvtaobao.live.activity.TMallLiveActivity;
import com.yunos.tvtaobao.live.controller.LiveDataController;

import java.util.List;
import java.util.Map;

/**
 * Created by pan on 16/10/12.
 */

public class TMallLiveListAdapter extends RecyclerView.Adapter<TMallLiveListAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<TMallLiveBean> mDataBeanList;
    private ImageLoaderManager imageLoaderManager;
    private LiveDataController mLiveDataController;
    private Drawable dr_playing_unfocus, dr_current_unfocus, dr_current_focused, dr_playing_focused;

    public TMallLiveListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        mLiveDataController = LiveDataController.getInstance();
        dr_current_focused = context.getResources().getDrawable(R.drawable.live_state_current_focused);
        dr_current_unfocus = context.getResources().getDrawable(R.drawable.live_state_current_unfocus);
        dr_playing_focused = context.getResources().getDrawable(R.drawable.live_state_playing_focused);
        dr_playing_unfocus = context.getResources().getDrawable(R.drawable.live_state_playing_unfocus);
    }

    public void setData(List<TMallLiveBean> dataBeanList) {
        if (mDataBeanList != null) {
            mDataBeanList.clear();
        }
        mDataBeanList = dataBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_tmall_live_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        imageLoaderManager.displayImage(mDataBeanList.get(position).getImg_url(), holder.iv_tmall_live_icon, null, null);
        holder.iv_tmall_live_name.setText(mDataBeanList.get(position).getName());
        if (isCurrentLive(position)) {
            holder.state_of_play.setImageDrawable(dr_current_unfocus);
            mLiveDataController.setCurrentTMallLivePos(position);
        } else {
            holder.state_of_play.setImageDrawable(dr_playing_unfocus);
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    holder.iv_tmall_live_name.setTextColor(0xffffffff);
                    holder.state_of_play.setImageDrawable(isCurrentLive(position) ? dr_current_focused : dr_playing_focused);
                } else {
                    holder.iv_tmall_live_name.setTextColor(0xff000000);
                    holder.state_of_play.setImageDrawable(isCurrentLive(position) ? dr_current_unfocus : dr_playing_unfocus);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> properties = Utils.getProperties();
                properties.put("live_name", mDataBeanList.get(position).getName());
                properties.put("p", position + "");
                properties.put("live_id", mDataBeanList.get(position).getId());
                properties.put("is_login", User.isLogined() ? "1" : "0");
                Utils.utControlHit("telecast_detail_list_listlive", properties);

                if (mContext instanceof TMallLiveActivity) {
                    if (mLiveDataController.getTMallItemsBean() != null
                            && mLiveDataController.getTMallItemsBean().getId().equals(mDataBeanList.get(position).getId())
                            && mContext instanceof TMallLiveActivity)
                        return;
                    if (((TMallLiveActivity) mContext).getLoadingType() != 0)
                        return;

                    ((TMallLiveActivity) mContext).changeLive(position, mDataBeanList.get(position));
                } else {
                    Intent intent = new Intent();
                    intent.setClass(mContext, TMallLiveActivity.class);
                    intent.putExtra("liveId", mDataBeanList.get(position).getLive_id());
                    intent.putExtra("liveUrl", mDataBeanList.get(position).getStream_url());
                    mLiveDataController.setCurrentTMallLivePos(position);
                    mLiveDataController.setTMallItemsBean(mDataBeanList.get(position));
                    mContext.startActivity(intent);
                    if (mContext instanceof TBaoLiveActivity) {
                        ((TBaoLiveActivity) mContext).finish();
                    }
                }

            }
        });
        holder.itemView.setId(mDataBeanList.get(position).hashCode());
        holder.itemView.setNextFocusDownId(position == mDataBeanList.size() - 1 ? holder.itemView.getId() : View.NO_ID);
    }

    private boolean isCurrentLive(int position) {
        return mLiveDataController.getTMallItemsBean() != null
                && mLiveDataController.getTMallItemsBean().getId().equals(mDataBeanList.get(position).getId())
                && mContext instanceof TMallLiveActivity;
    }

    @Override
    public int getItemCount() {
        return mDataBeanList == null ? 0 : mDataBeanList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_tmall_live_icon, state_of_play;
        public TextView iv_tmall_live_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_tmall_live_icon = (ImageView) itemView.findViewById(R.id.iv_tmall_live_icon);
            iv_tmall_live_name = (TextView) itemView.findViewById(R.id.iv_tmall_live_name);
            state_of_play = (ImageView) itemView.findViewById(R.id.state_of_play);
        }
    }
}
