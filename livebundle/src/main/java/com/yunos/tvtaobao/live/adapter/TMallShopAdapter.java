package com.yunos.tvtaobao.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveShopList;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 16/9/29.
 */

public class TMallShopAdapter extends RecyclerView.Adapter<TMallShopAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<TMallLiveShopList.ModelBean.DataBean> mList;
    ImageLoaderManager imageLoaderManager;

    // 网络请求
    private BusinessRequest mBusinessRequest;

    public TMallShopAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        mBusinessRequest = BusinessRequest.getBusinessRequest();
    }

    public void setData(List<TMallLiveShopList.ModelBean.DataBean> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_live_product, parent, false);

        return new ViewHolder(view);
    }

    private TMallLiveShopList.ModelBean.DataBean items;
    private ImageView favImg;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TMallLiveShopList.ModelBean.DataBean item = getItem(position);

        holder.tv_live_name.setText(item.getTitle());
        holder.tv_live_price.setText(Tools.getPrice(mContext, item.getPrice()));

        imageLoaderManager.loadImage(item.getPicUrl(), holder.iv_live_icon,null);
        holder.iv_live_cart.setImageResource(item.getFavorate() ? R.drawable.live_shop_aleryfav_btn : R.drawable.live_shop_tofav_btn);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Map<String, String> properties = Utils.getProperties();
                    properties.put("item_name", getItem(position).getTitle());
                    properties.put("p", position+"");
                    properties.put("item_id", getItem(position).getItemId());
                    properties.put("is_login", User.isLogined()? "1" : "0");
                    Utils.utControlHit("telecast_detail_list_commodity_Collect", properties);

                    //Toast.makeText(mContext,"加入购物车成功", Toast.LENGTH_SHORT).show();
                    items = getItem(position);
                    favImg = holder.iv_live_cart;
                    manageFav(items);
                } catch (Exception e) {
                    Toast.makeText(mContext, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getItem(position) == null)
                    return;
                if(b){
                    holder.iv_live_cart.setImageResource(getItem(position).getFavorate() ? R.drawable.live_shop_aleryfav_btn_focused : R.drawable.live_shop_tofav_btn_focused);
                    holder.tv_live_name.setTextColor(0xffffffff);
                }else {
                    holder.iv_live_cart.setImageResource(getItem(position).getFavorate() ? R.drawable.live_shop_aleryfav_btn : R.drawable.live_shop_tofav_btn);
                    holder.tv_live_name.setTextColor(0xff99aabb);
                }
            }
        });
    }

    // 添加收藏
    public void manageFav(TMallLiveShopList.ModelBean.DataBean item) {
        String func = "";
        if (item.getFavorate()) {
            func = "delAuction";
        } else {
            func = "addAuction";
        }

        mBusinessRequest.manageFav(item.getItemId(), func, new ManageFavBusinessRequestListener(new WeakReference<BaseActivity>(
                (BaseActivity) mContext)));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();

    }

    /**
     * 收藏管理的请求监听类
     */
    private class ManageFavBusinessRequestListener extends BizRequestListener<String> {

        public ManageFavBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if ("该宝贝已收藏".equals(msg)) {
                items.setFavorate(true);
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
            onHandleRequestManageFav();
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }

    /**
     * 处理收藏管理请求的返回
     *
     * @return
     */
    private void onHandleRequestManageFav() {
        String msg = "";
        if (items.getFavorate()) {
            msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_cancel_success);
            items.setFavorate(false);
            if (favImg != null) {
                favImg.setImageResource(R.drawable.live_shop_tofav);
            }
        } else {
            msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_add_success);
            items.setFavorate(true);
            if (favImg != null) {
                favImg.setImageResource(R.drawable.live_shop_aleryfav);
            }
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private TMallLiveShopList.ModelBean.DataBean getItem(int position) {
        if(mList != null){
            return mList.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_live_cart;
        public ImageView iv_live_icon;
        public LinearLayout ll_item_top;
        public TextView tv_live_name;
        public TextView tv_live_price;



        public ViewHolder(View itemView) {
            super(itemView);
            iv_live_cart = (ImageView) itemView.findViewById(R.id.iv_live_cart);
            iv_live_icon = (ImageView) itemView.findViewById(R.id.iv_live_icon);
            ll_item_top = (LinearLayout) itemView.findViewById(R.id.ll_item_top);
            tv_live_name = (TextView) itemView.findViewById(R.id.tv_live_name);
            tv_live_price = (TextView) itemView.findViewById(R.id.tv_live_price);

        }
    }
}
