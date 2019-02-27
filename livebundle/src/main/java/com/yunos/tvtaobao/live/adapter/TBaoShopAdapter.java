package com.yunos.tvtaobao.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.activity.TBaoLiveActivity;
import com.yunos.tvtaobao.live.data.TBaoProductBean;
import com.yunos.tvtaobao.live.utils.Tools;
import com.yunos.tvtaobao.live.view.FocusRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by libin on 16/9/12.
 */

public class TBaoShopAdapter extends RecyclerView.Adapter {
    private String TAG = TBaoShopAdapter.class.getName();
    private Context mContext;
    private LayoutInflater mInflater;
    private OnFocusChangeListener focusChangeListener;
    private OnHotProductFocusListsner hotFocusListsner;


    private List<TBaoProductBean> mProductList;
    private List<TBaoShopBean.ItemListBean> itemList;
    private List<TBaoShopBean.ItemListBean.GoodsListBean> mHotList;
    private ImageLoaderManager imageLoaderManager;
    private BusinessRequest mBusinessRequest;

    private enum Type {
        TYPE_HOT_PRODUCT,
        TYPE_ITEM_PRODUCT
    }

    private WeakReference<View> rightView;

    public void setRightView(View view) {
        if (view == null) {
            rightView = null;
            return;
        }
        rightView = new WeakReference<View>(view);
    }

    public TBaoShopAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);

        mProductList = new ArrayList<>();

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
        mBusinessRequest = BusinessRequest.getBusinessRequest();
    }

    /**
     * 设置RecyclerView焦点监听
     * @param listener
     */
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        this.focusChangeListener = listener;
    }

    public void setHotFocusListsner(OnHotProductFocusListsner hotFocusListsner) {
        this.hotFocusListsner = hotFocusListsner;
    }

    public interface OnHotProductFocusListsner{
        void onFocusChange(View view, boolean b);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean b);
    }

    public void setData(TBaoShopBean tBaoShopBean) {
        this.mHotList = tBaoShopBean.getHotList();
        this.itemList = tBaoShopBean.getItemList();

        int itemListSize = itemList.size();
        for (int i = 0; i < itemListSize; i++) {
            int goodsListSize = itemList.get(i).getGoodsList().size();
            for (int j = 0; j < goodsListSize; j++) {
                TBaoProductBean productBean = new TBaoProductBean();
                productBean.setGoodsIndex(itemList.get(i).getGoodsIndex());
                productBean.setGoodsListBean(itemList.get(i).getGoodsList().get(j));
                mProductList.add(productBean);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Type.TYPE_HOT_PRODUCT.ordinal();
        } else {
            return Type.TYPE_ITEM_PRODUCT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Type.TYPE_HOT_PRODUCT.ordinal()) {
            View view = mInflater.inflate(R.layout.item_tbao_live_hotproduct, parent, false);
            return new HotViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_live_product, parent, false);
            return new ItemViewHolder(view);
        }
    }

    private String goodsIndex;
    private TBaoProductBean items;
    private TBaoShopBean.ItemListBean.GoodsListBean hotItems;
    private ImageView favImg;
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HotViewHolder) {
            if (mHotList.size() < 2) {
                ((HotViewHolder) holder).tbao_live_hot_product_layout.setVisibility(View.GONE);
                ((HotViewHolder) holder).tbao_live_hot_product_text.setVisibility(View.GONE);

                ((HotViewHolder) holder).tbao_live_hot_all_product.setText("全部宝贝(" + mProductList.size() + ")");
            } else {
                imageLoaderManager.displayImage(mHotList.get(0).getItemPic(), ((HotViewHolder) holder).tbao_live_hot_product_1);
                imageLoaderManager.displayImage(mHotList.get(1).getItemPic(), ((HotViewHolder) holder).tbao_live_hot_product_2);

                ((HotViewHolder) holder).tbao_live_hot_product_price_1.setText(Tools.getPrice(mContext, mHotList.get(0).getItemPrice()));
                ((HotViewHolder) holder).tbao_live_hot_product_price_2.setText(Tools.getPrice(mContext, mHotList.get(1).getItemPrice()));

                ((HotViewHolder) holder).tbao_live_hot_all_product.setText("全部宝贝(" + mProductList.size() + ")");

                ((HotViewHolder) holder).tbao_live_hot_product_layout_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //manageHotFav(mHotList.get(0));
                        favImg = ((HotViewHolder) holder).tbao_live_hot_product_fav_1;
                        hotItems = mHotList.get(0);

                        Map<String, String> properties = Utils.getProperties();
                        properties.put("p", "1");
                        properties.put("spm",SPMConfig.LIVE_HOT_SHOP_SPM);
                        Utils.utControlHit(((TBaoLiveActivity)mContext).getFullPageName(),"ItemList_hot_item_p", properties);
                        Utils.updateNextPageProperties(SPMConfig.LIVE_HOT_SHOP_SPM);
                        Intent intent = new Intent();
                        intent.setData(Uri.parse("tvtaobao://home?module=detail&itemId=" + hotItems.getItemId() + "&notshowloading=true"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });

                ((HotViewHolder) holder).tbao_live_hot_product_layout_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //manageHotFav(mHotList.get(1));
                        favImg = ((HotViewHolder) holder).tbao_live_hot_product_fav_2;
                        hotItems = mHotList.get(1);

                        Map<String, String> properties = Utils.getProperties();
                        properties.put("p", "2");
                        properties.put("spm",SPMConfig.LIVE_HOT_SHOP_SPM);
                        Utils.utControlHit(((TBaoLiveActivity)mContext).getFullPageName(),"ItemList_hot_item_p", properties);

                        Utils.updateNextPageProperties(SPMConfig.LIVE_HOT_SHOP_SPM);

                        Intent intent = new Intent();
                        intent.setData(Uri.parse("tvtaobao://home?module=detail&itemId=" + hotItems.getItemId() + "&notshowloading=true"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });

                ((HotViewHolder) holder).tbao_live_hot_product_layout_1.setOnFocusChangeListener(new FocusChangeListener(((HotViewHolder) holder)));
                ((HotViewHolder) holder).tbao_live_hot_product_layout_2.setOnFocusChangeListener(new FocusChangeListener(((HotViewHolder) holder)));
            }
        } else if (holder instanceof ItemViewHolder) {
            position = position - 1;
            final TBaoProductBean item = mProductList.get(position);


            if (item.getGoodsIndex().equals(goodsIndex)) {
                ((ItemViewHolder) holder).item_live_product_num.setVisibility(View.GONE);
                ((ItemViewHolder) holder).tv_live_name.setText(item.getGoodsListBean().getItemName());
            } else {
                ((ItemViewHolder) holder).item_live_product_num.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).item_live_product_num.setText(item.getGoodsIndex());
                ((ItemViewHolder) holder).tv_live_name.setText(item.getGoodsListBean().getItemName());
            }
            goodsIndex = item.getGoodsIndex();
            ((ItemViewHolder) holder).tv_live_price.setText(Tools.getPrice(mContext, item.getGoodsListBean().getItemPrice()));
            ((ItemViewHolder) holder).iv_live_cart.setImageResource(item.getGoodsListBean().getFavored().equals("true") ? R.drawable.live_shop_aleryfav_btn : R.drawable.live_shop_tofav_btn);

            imageLoaderManager.displayImage(item.getGoodsListBean().getItemPic(), ((ItemViewHolder) holder).iv_live_icon);


            final int finalPosition = position;
            ((ItemViewHolder) holder).ll_item_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppDebug.e(TAG, "finalPosition : " + finalPosition + " ,mProductList : " + mProductList);
                    Map<String, String> properties = Utils.getProperties();
                    TBaoProductBean tBaoProductBean = mProductList.get(finalPosition);
                    properties.put("p", finalPosition+"");
                    properties.put("spm",SPMConfig.LIVE_ALL_SHOP_CLICK_SPM);
                    Utils.utControlHit(((TBaoLiveActivity)mContext).getFullPageName(),"ItemList_normal_item_p", properties);
                    Utils.updateNextPageProperties(SPMConfig.LIVE_ALL_SHOP_CLICK_SPM);

                    items = mProductList.get(finalPosition);
                    favImg = ((ItemViewHolder) holder).iv_live_cart;

                    if(tBaoProductBean!=null){
                        TBaoShopBean.ItemListBean.GoodsListBean goodsListBean = tBaoProductBean.getGoodsListBean();
                        if(goodsListBean!=null){
                            Intent intent = new Intent();
                            intent.setData(Uri.parse("tvtaobao://home?module=detail&itemId=" + goodsListBean.getItemId() + "&notshowloading=true"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }

                    //manageFav(items);
                }
            });
            ((ItemViewHolder) holder).itemView.setId(item.hashCode());
            ((ItemViewHolder) holder).itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        if (rightView != null && rightView.get() != null) {
                            rightView.get().setNextFocusLeftId(view.getId());
                        }
                        if (((ItemViewHolder) holder).item_live_product_num.getVisibility() == View.VISIBLE) {
                            ((ItemViewHolder) holder).item_live_product_num.setBackgroundResource(R.drawable.tbao_live_shop_num_focused);
                        }
                        ((ItemViewHolder) holder).iv_live_cart.setImageResource(item.getGoodsListBean().getFavored().equals("true") ? R.drawable.live_shop_aleryfav_btn_focused : R.drawable.live_shop_tofav_btn_focused);
                        ((ItemViewHolder) holder).tv_live_name.setTextColor(0xffffffff);
                    } else {
                        if (((ItemViewHolder) holder).item_live_product_num.getVisibility() == View.VISIBLE) {
                            ((ItemViewHolder) holder).item_live_product_num.setBackgroundResource(R.drawable.tbao_live_shop_num_unfocus);
                        }
                        ((ItemViewHolder) holder).iv_live_cart.setImageResource(item.getGoodsListBean().getFavored().equals("true") ? R.drawable.live_shop_aleryfav_btn : R.drawable.live_shop_tofav_btn);
                        ((ItemViewHolder) holder).tv_live_name.setTextColor(0xff99aabb);
                    }

                    if (focusChangeListener != null) {
                        focusChangeListener.onFocusChange(view, b);
                    }
                }
            });
        }
    }

    private class FocusChangeListener implements View.OnFocusChangeListener {
        private HotViewHolder hotViewHolder;
        private FocusChangeListener(HotViewHolder holder) {
            this.hotViewHolder = holder;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            int i = view.getId();

            if(hotFocusListsner != null){
                hotFocusListsner.onFocusChange(view,b);
            }

            if (i == R.id.tbao_live_hot_product_layout_1) {
                if (b) {
                    hotViewHolder.focusbox_product1.setVisibility(View.VISIBLE);
                } else {
                    hotViewHolder.focusbox_product1.setVisibility(View.GONE);
                }
            }

            if (i == R.id.tbao_live_hot_product_layout_2) {
                if (b) {
                    hotViewHolder.focusbox_product2.setVisibility(View.VISIBLE);
                } else {
                    hotViewHolder.focusbox_product2.setVisibility(View.GONE);
                }
            }
        }
    }

    // 添加收藏
    public void manageFav(TBaoProductBean item) {
        String func = "";
        if (item.getGoodsListBean().getFavored().equals("true")) {
            func = "delAuction";
        } else {
            func = "addAuction";
        }

        mBusinessRequest.manageFav(item.getGoodsListBean().getItemId(), func, new ManageFavBusinessRequestListener(new WeakReference<BaseActivity>(
                (BaseActivity) mContext), 0));
    }

    public void manageHotFav(TBaoShopBean.ItemListBean.GoodsListBean item) {
        AppDebug.e(TAG, "favored : " + item.getFavored());
        String func = "";
        if (item.getFavored().equals("true")) {
            func = "delAuction";
        } else {
            func = "addAuction";
        }

        mBusinessRequest.manageFav(item.getItemId(), func, new ManageFavBusinessRequestListener(new WeakReference<BaseActivity>(
                (BaseActivity) mContext), 1));
    }

    @Override
    public int getItemCount() {
        return mProductList == null ? 0 : mProductList.size() + 1;
    }

    /**
     * 收藏管理的请求监听类
     */
    private class ManageFavBusinessRequestListener extends BizRequestListener<String> {

        int type;
        public ManageFavBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, int type) {
            super(mBaseActivityRef);
            this.type = type;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (msg.equals("您尚未登录淘宝账号，请先登录。")) {
                return false;
            }
            if (type == 0) {
                if ("您已经收藏过了，请不要重复收藏。".equals(msg)) {
                    items.getGoodsListBean().setFavored("true");
                    favImg.setImageResource(R.drawable.live_shop_aleryfav);
                    Toast.makeText(mContext, "添加收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    items.getGoodsListBean().setFavored("false");
                    favImg.setImageResource(R.drawable.live_shop_tofav);
                    Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                if ("您已经收藏过了，请不要重复收藏。".equals(msg)) {
                    hotItems.setFavored("true");
                    favImg.setImageResource(R.drawable.tbao_live_hot_shop_fav_focused);
                    Toast.makeText(mContext, "添加收藏成功", Toast.LENGTH_SHORT).show();
                } else {
                    hotItems.setFavored("false");
                    favImg.setImageResource(R.drawable.tbao_live_hot_shop_fav_unfocus);
                    Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }

        @Override
        public void onSuccess(String data) {
            onHandleRequestManageFav(type);
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
    private void onHandleRequestManageFav(int type) {
        String msg = "";
        if (type == 0) {
            if (items.getGoodsListBean().getFavored().equals("true")) {
                msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_cancel_success);
                items.getGoodsListBean().setFavored("false");
                if (favImg != null) {
                    favImg.setImageResource(R.drawable.live_shop_tofav);
                }
            } else {
                msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_add_success);
                items.getGoodsListBean().setFavored("true");
                if (favImg != null) {
                    favImg.setImageResource(R.drawable.live_shop_aleryfav);
                }
            }
        } else {
            if (hotItems.getFavored().equals("true")) {
                msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_cancel_success);
                hotItems.setFavored("false");
                if (favImg != null) {
                    favImg.setImageResource(R.drawable.tbao_live_hot_shop_fav_unfocus);
                }
            } else {
                msg = mContext.getResources().getString(R.string.ytsdk_detail_fav_add_success);
                hotItems.setFavored("true");
                if (favImg != null) {
                    favImg.setImageResource(R.drawable.tbao_live_hot_shop_fav_focused);
                }
            }
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_live_cart;
        public ImageView iv_live_icon;
        public LinearLayout ll_item_top;
        public TextView tv_live_name, item_live_product_num;
        public TextView tv_live_price;

        public ItemViewHolder(View itemView) {
            super(itemView);
            iv_live_cart = (ImageView) itemView.findViewById(R.id.iv_live_cart);
            iv_live_icon = (ImageView) itemView.findViewById(R.id.iv_live_icon);
            ll_item_top = (LinearLayout) itemView.findViewById(R.id.ll_item_top);
            tv_live_name = (TextView) itemView.findViewById(R.id.tv_live_name);
            tv_live_price = (TextView) itemView.findViewById(R.id.tv_live_price);
            item_live_product_num = (TextView) itemView.findViewById(R.id.item_live_product_num);
        }
    }

    private class HotViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout tbao_live_hot_product_layout;
        private FocusRelativeLayout tbao_live_hot_product_layout_1, tbao_live_hot_product_layout_2;
        private ImageView tbao_live_hot_product_1, tbao_live_hot_product_2, focusbox_product1, focusbox_product2;
        private TextView tbao_live_hot_product_text, tbao_live_hot_all_product;
        private TextView tbao_live_hot_product_price_1, tbao_live_hot_product_price_2;
        private ImageView tbao_live_hot_product_fav_1, tbao_live_hot_product_fav_2;
//        private FocusPositionManager focusedlinearlayout;

        public HotViewHolder(View itemView) {
            super(itemView);
            tbao_live_hot_product_text = (TextView) itemView.findViewById(R.id.tbao_live_hot_product_text);
            tbao_live_hot_all_product = (TextView) itemView.findViewById(R.id.tbao_live_hot_all_product);
            tbao_live_hot_product_1 = (ImageView) itemView.findViewById(R.id.tbao_live_hot_product_1);
            tbao_live_hot_product_2 = (ImageView) itemView.findViewById(R.id.tbao_live_hot_product_2);
            tbao_live_hot_product_layout = (LinearLayout) itemView.findViewById(R.id.tbao_live_hot_product_layout);
            tbao_live_hot_product_price_1 = (TextView) itemView.findViewById(R.id.tbao_live_hot_product_price_1);
            tbao_live_hot_product_price_2 = (TextView) itemView.findViewById(R.id.tbao_live_hot_product_price_2);
            tbao_live_hot_product_layout_1 = (FocusRelativeLayout) itemView.findViewById(R.id.tbao_live_hot_product_layout_1);
            tbao_live_hot_product_layout_2 = (FocusRelativeLayout) itemView.findViewById(R.id.tbao_live_hot_product_layout_2);
            tbao_live_hot_product_fav_1 = (ImageView) itemView.findViewById(R.id.tbao_live_hot_product_fav_1);
            tbao_live_hot_product_fav_2 = (ImageView) itemView.findViewById(R.id.tbao_live_hot_product_fav_2);
            focusbox_product1 = (ImageView) itemView.findViewById(R.id.focusbox_product1);
            focusbox_product2 = (ImageView) itemView.findViewById(R.id.focusbox_product2);
        }
    }

    public void clearData() {
        if (mProductList != null)
            mProductList.clear();
        if (mHotList != null)
            mHotList.clear();
        if (itemList != null)
            itemList.clear();
    }
}
