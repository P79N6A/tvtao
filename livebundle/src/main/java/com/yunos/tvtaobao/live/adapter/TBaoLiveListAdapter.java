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

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.live.activity.TBaoLiveActivity;
import com.yunos.tvtaobao.live.activity.TMallLiveActivity;
import com.yunos.tvtaobao.live.controller.LiveDataController;
import com.yunos.tvtaobao.live.data.TBaoProductBean;
import com.yunos.tvtaobao.live.view.Displayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 16/12/15.
 */

public class TBaoLiveListAdapter extends RecyclerView.Adapter<TBaoLiveListAdapter.ViewHolder> {

    private Context mContext;
    private List<TBaoLiveListBean> mDataBeanList;
    private LayoutInflater inflater;
    private ImageLoaderManager imageLoaderManager;
    private DisplayImageOptions roundImageOptions;
    private Drawable dr_playing_unfocus, dr_current_unfocus, dr_current_focused, dr_playing_focused;
    private LiveDataController mLiveDataController;
    public TBaoLiveListAdapter(Context context,List<TBaoLiveListBean> dataBeanList) {
        this.mContext = context;
        this.mDataBeanList = dataBeanList;
        inflater = LayoutInflater.from(context);

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        mLiveDataController = LiveDataController.getInstance();
        roundImageOptions = new DisplayImageOptions.Builder().displayer(new Displayer(0)).build();
        dr_current_focused = context.getResources().getDrawable(R.drawable.live_state_current_focused);
        dr_current_unfocus = context.getResources().getDrawable(R.drawable.live_state_current_unfocus);
        dr_playing_focused = context.getResources().getDrawable(R.drawable.live_state_playing_focused);
        dr_playing_unfocus = context.getResources().getDrawable(R.drawable.live_state_playing_unfocus);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_tbao_live_list, parent, false));
    }

    private ImageView oldPlayingView;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(mDataBeanList!=null){
            TBaoLiveListBean tBaoLiveListBean = mDataBeanList.get(position);
            if(tBaoLiveListBean!=null){
                imageLoaderManager.displayImage(tBaoLiveListBean.getCoverImg(), holder.iv_tbao_live_icon, null, null);
                imageLoaderManager.displayImage(tBaoLiveListBean.getHeadImg(), holder.li_tbao_live_head, roundImageOptions, null);
                holder.lv_tbao_live_nick.setText(tBaoLiveListBean.getAccountNick());
                holder.lv_tbao_live_name.setText(tBaoLiveListBean.getTitle());
            }
        }

        if (isCurrentLive(position)) {
            holder.state_of_play.setImageDrawable(dr_current_unfocus);
            mLiveDataController.setCurrentTBaoLivePos(position);
            oldPlayingView = holder.state_of_play;
        } else {
            holder.state_of_play.setImageDrawable(dr_playing_unfocus);
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.lv_tbao_live_name.setTextColor(0xffffffff);
                    holder.lv_tbao_live_nick.setTextColor(0xffffffff);
                    holder.state_of_play.setImageDrawable(isCurrentLive(position) ? dr_current_focused : dr_playing_focused);
                } else {
                    holder.lv_tbao_live_name.setTextColor(0xff000000);
                    holder.lv_tbao_live_nick.setTextColor(0xff000000);
                    holder.state_of_play.setImageDrawable(isCurrentLive(position) ? dr_current_unfocus : dr_playing_unfocus);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> properties = Utils.getProperties();
                properties.put("p", position+"");
                properties.put("spm", SPMConfig.LIVE_LIST_CLICK_SPM);
                Utils.utControlHit(((TBaoLiveActivity)mContext).getFullPageName(),"video_p", properties);

                if (mContext instanceof TBaoLiveActivity) {
                    if (mLiveDataController.getTBaoItemsBean() != null
                            && mLiveDataController.getTBaoItemsBean().getLiveId().equals(mDataBeanList.get(position).getLiveId())
                            && mContext instanceof TBaoLiveActivity) {
                        return;
                    }

                    ((TBaoLiveActivity) mContext).changeLive(position, mDataBeanList.get(position));
                    holder.state_of_play.setImageDrawable(dr_current_focused);
                    if (oldPlayingView != null)
                        oldPlayingView.setImageDrawable(dr_playing_unfocus);

                    oldPlayingView = holder.state_of_play;
                } else  {
                    Intent intent = new Intent(mContext, TBaoLiveActivity.class);
                    intent.putExtra("liveId", mDataBeanList.get(position).getLiveId());
                    intent.putExtra("accountId", mDataBeanList.get(position).getAccountId());
                    intent.putExtra("topic", mDataBeanList.get(position).getLiveId());
                    intent.putExtra("liveUrl", mDataBeanList.get(position).getInputStreamUrl());
                    mLiveDataController.setCurrentTBaoLivePos(position);
                    mLiveDataController.setTBaoItemsBean(mDataBeanList.get(position));
                    mContext.startActivity(intent);
                    if (mContext instanceof TMallLiveActivity) {
                        ((TMallLiveActivity) mContext).finish();
                    }
                }

            }
        });
    }

    private boolean isCurrentLive(int position) {
        return mLiveDataController.getTBaoItemsBean() != null
                && mLiveDataController.getTBaoItemsBean().getLiveId().equals(mDataBeanList.get(position).getLiveId())
                && mContext instanceof TBaoLiveActivity;
    }

    @Override
    public int getItemCount() {
        return mDataBeanList == null ? 0 : mDataBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_tbao_live_icon, state_of_play, li_tbao_live_head;
        private TextView lv_tbao_live_name, lv_tbao_live_nick;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_tbao_live_icon = (ImageView) itemView.findViewById(R.id.iv_tbao_live_icon);
            state_of_play = (ImageView) itemView.findViewById(R.id.state_of_play);
            li_tbao_live_head = (ImageView) itemView.findViewById(R.id.li_tbao_live_head);
            lv_tbao_live_name = (TextView) itemView.findViewById(R.id.lv_tbao_live_name);
            lv_tbao_live_nick = (TextView) itemView.findViewById(R.id.lv_tbao_live_nick);
        }
    }
}
