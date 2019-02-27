package com.yunos.tvtaobao.newcart.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.tvtaobao.voicesdk.utils.Util;
import com.yunos.tv.app.widget.RelativeLayout;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.FindSameBean;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.entity.FindSameIntentBean;
import com.yunos.tvtaobao.newcart.ui.activity.FindSameActivity;
import com.yunos.tvtaobao.newcart.ui.holder.FindSameHeaderHolder;
import com.yunos.tvtaobao.newcart.ui.holder.FindSameItemHolder;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by zhoubo on 2018/7/12.
 * zhoubo on 2018/7/12 10:26
 * describition 找相似数据展示Adpter
 */

public class NewShopCartFindSameAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private FindSameContainerBean mFindSameContainerBean;

    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    private final LayoutInflater mInflater;
    private FindSameIntentBean mFindSameBean;
    private final int colorWhite;
    private final int colorGrey;
//    private OnItemListener onItemListener;

    public NewShopCartFindSameAdapter(Context context, FindSameContainerBean findSameContainerBean, FindSameIntentBean mFindSameBean) {
        this.mContext = context;
        this.mFindSameContainerBean = findSameContainerBean;
        mInflater = LayoutInflater.from(mContext);
        this.mFindSameBean = mFindSameBean;
        colorWhite = mContext.getResources().getColor(R.color.text_color_white);
        colorGrey = mContext.getResources().getColor(R.color.tui_text_color_grey);
    }

//    public void setOnItemListener(OnItemListener onItemListener){
//        this.onItemListener = onItemListener;
//    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEAD : TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View typeView = mInflater.inflate(R.layout.layout_new_shop_cart_header_findsame, parent, false);
            return new FindSameHeaderHolder(typeView);
        } else {
            View typeView = mInflater.inflate(R.layout.layout_new_shop_cart_item_findsame, parent, false);
            return new FindSameItemHolder(typeView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0 && holder instanceof FindSameHeaderHolder) {
            if (mFindSameBean != null) {
                FindSameHeaderHolder findSameHeaderHolder = (FindSameHeaderHolder) holder;
                //展示头部的图片
                ImageLoaderManager.getImageLoaderManager(mContext).displayImage(mFindSameContainerBean.getPic(), findSameHeaderHolder.ivFindSameDes);
                //价格
                String price = mFindSameBean.getPrice();
                price = price.replaceAll("￥", "¥ ");
                price = price.replaceAll(",", "");
                if (price.indexOf(".") > 0) {
                    //正则表达
                    price = price.replaceAll("0+?$", "");//去掉后面无用的零
                    price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                }
                findSameHeaderHolder.tvFindSamePrice.setText(price);
                //简介
                findSameHeaderHolder.tvFinsSameDes.setText(mFindSameBean.getTitle());
            }
        } else if (holder instanceof FindSameItemHolder) {

            int itemPos = position - 1;
            final FindSameBean findSameBean = mFindSameContainerBean.getFindSameBeanList().get(itemPos);
            final FindSameItemHolder itemHolder = (FindSameItemHolder) holder;
            itemHolder.layoutFindSame.setScaleX(1.0f);
            itemHolder.layoutFindSame.setScaleY(1.0f);
            //商品介绍
            itemHolder.tvItemGoods.setText(findSameBean.getItemName());
            String url = findSameBean.getPic();
            String tag = (String) itemHolder.ivItemDes.getTag();
            //展示图片
            if (!url.equals(tag)) {
                itemHolder.ivItemDes.setTag(url);
                itemHolder.ivItemDes.setImageResource(R.drawable.new_shop_cart_img_default);
                ImageLoaderManager.getImageLoaderManager(mContext).displayImage(findSameBean.getPic(), itemHolder.ivItemDes);
            }
            //price
            String price = findSameBean.getPrice();
            price.replaceAll(",", "");
            if (price.indexOf(".") > 0) {
                //正则表达
                price = price.replaceAll("0+?$", "");//去掉后面无用的零
                price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            }
            itemHolder.tvItemPrice.setText(String.format(mContext.getString(R.string.new_shop_cart_item_price), price));
            //付款人数
            if (!TextUtils.isEmpty(findSameBean.getSold())) {
                itemHolder.tvItemPaid.setText(findSameBean.getSold() + "人付款");
                itemHolder.tvItemPaid.setVisibility(View.VISIBLE);
            } else {
                itemHolder.tvItemPaid.setVisibility(View.GONE);

            }
            //返利
            if (findSameBean.getRebateBo() != null) {
                RebateBo rebateBo = findSameBean.getRebateBo();
                Boolean isMJF=rebateBo.isMjf();
                if(isMJF){
                    itemHolder.goodlist_grid_item_return_red_packet.setVisibility(View.VISIBLE);
                }else {
                    itemHolder.goodlist_grid_item_return_red_packet.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(rebateBo.getCoupon())) {
//                    !TextUtils.isEmpty(rebateBo.getCoupon()) 正常商品返利展示
                    String couponString = Utils.getRebateCoupon(rebateBo.getCoupon());
                    if(couponString!=null) {
                        itemHolder.tvRebateCoupon.setVisibility(View.VISIBLE);
                        itemHolder.layoutRebate.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                            itemHolder.tvRebateCoupon.setText(rebateBo.getCouponMessage() + " ¥ " + couponString);
                        } else {
                            itemHolder.tvRebateCoupon.setText("预估" + " ¥ " + couponString);
                        }
                        if (!TextUtils.isEmpty(rebateBo.getPicUrl())) {
                            itemHolder.ivRebateIcon.setVisibility(View.VISIBLE);
                            ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebateBo.getPicUrl(), itemHolder.ivRebateIcon);
                        } else {
                            itemHolder.ivRebateIcon.setVisibility(View.GONE);
                        }
                    }else {
                        itemHolder.layoutRebate.setVisibility(View.GONE);
                    }
                } else {
//                    TextUtils.isEmpty(rebateBo.getCoupon()) 预售商品返利展示
                    if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                        itemHolder.tvRebateCoupon.setText(rebateBo.getCouponMessage());
                    }
                    if (!TextUtils.isEmpty(rebateBo.getPicUrl())) {
                        itemHolder.ivRebateIcon.setVisibility(View.VISIBLE);
                        ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebateBo.getPicUrl(), itemHolder.ivRebateIcon);
                    } else {
                        itemHolder.ivRebateIcon.setVisibility(View.GONE);
                    }

                    itemHolder.layoutRebate.setVisibility(View.GONE);
                }

            } else {
                itemHolder.layoutRebate.setVisibility(View.GONE);

            }

            itemHolder.layoutFindSame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext != null && mContext instanceof FindSameActivity) {
                        Utils.utControlHit(((FindSameActivity) mContext).getFullPageName(), "Button",
                                initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY_DETAIL));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY_DETAIL);

                        ((FindSameActivity) mContext).onFindSameDetailClick(findSameBean);
                    }
                }
            });

            itemHolder.layoutFindSame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        AppDebug.e("findSame position=", position + "");
                        itemHolder.tvItemGoods.setMaxLines(2);
                        itemHolder.ivFindSameStatus.setVisibility(View.VISIBLE);

//                        ViewCompat.animate(v).scaleX(1.05f).scaleY(1.01f).start();
                        itemHolder.layoutFindSame.setScaleX(1.05f);
                        itemHolder.layoutFindSame.setScaleY(1.05f);
                    } else {
                        itemHolder.tvItemGoods.setMaxLines(1);
                        itemHolder.ivFindSameStatus.setVisibility(View.GONE);

//                        ViewCompat.animate(v).scaleX(1.0f).scaleY(1.0f).start();
                        itemHolder.layoutFindSame.setScaleX(1.0f);
                        itemHolder.layoutFindSame.setScaleY(1.0f);

                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mFindSameContainerBean.getFindSameBeanList() == null ? 0 : mFindSameContainerBean.getFindSameBeanList().size() + 1;
    }

    public boolean isHead(int positon) {
        return positon == 0;
    }

    @SuppressWarnings("unused")
    public interface IFindSameAddCartClickListener {

        void onFindSameAddCartClick(FindSameBean findSameBean);

        void onFindSameDetailClick(FindSameBean findSameBean);

    }

    public interface OnItemListener {

        void onItemSelected(int position);

    }

    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();


        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

        return p;

    }

}
