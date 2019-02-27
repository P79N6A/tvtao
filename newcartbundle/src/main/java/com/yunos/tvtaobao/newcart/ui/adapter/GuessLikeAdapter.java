package com.yunos.tvtaobao.newcart.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;


import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.DynamicRecommend;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.newcart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */

public class GuessLikeAdapter extends RecyclerView.Adapter {

    private OnItemListener onItemListener;
    private Context mContext;
    DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().
            bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).cacheInMemory(false).build();
    List<GuessLikeGoodsBean.ResultVO.RecommendVO> data = new ArrayList<>();
    private GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods;
    private boolean isEmpty;
    private boolean hasDynamicRecommend;

    public GuessLikeAdapter(Context context, OnItemListener listener, boolean isEmpty) {
        this.mContext = context;
        this.onItemListener = listener;
        this.isEmpty = isEmpty;
    }

    //设置动态配置的热门推荐
    public void setHasDynamicRecommend(boolean hasDynamic){
        hasDynamicRecommend = hasDynamic;
    }

    public void setData(List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommendVOList) {
        data.clear();
        data.addAll(recommendVOList);
        notifyDataSetChanged();
    }

    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    private final static int TYPE_FOOTER = 2;
    private final static int TYPE_DYNAMIC = 3;

    @Override
    public int getItemViewType(int position) {
        if (!isEmpty) {
            if (position == 0) { // 头部
                return TYPE_HEAD;
            } else {
                return TYPE_CONTENT;
            }
        } else {
            if(hasDynamicRecommend && position == 0){
                return TYPE_DYNAMIC;
            } else {
                return TYPE_CONTENT;
            }
        }
    }

    public interface OnItemListener {

        void onItemSelected(int position);

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public boolean isHead(int position) {
        return position == 0;
    }

    public boolean isFoot(int position) {
        return position == data.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_guess_like_head, parent, false);
            return new HeadHolder(itemView);
        } else if (viewType == TYPE_DYNAMIC){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dynamic_recommend_item, parent, false);
            return new DynamicRecommendHolder(itemView);
        } else {
            View itemView = null;
            if (!isEmpty) {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_guess_like_item, parent, false);
            } else {
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shop_cart_empty_guess_like_item, parent, false);
            }
            return new ContentHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadHolder) { // 头部
        } else if (holder instanceof DynamicRecommendHolder){//运营位
            final DynamicRecommendHolder myHolder = (DynamicRecommendHolder) holder;
            GuessLikeGoodsBean.ResultVO.RecommendVO recommendVO = data.get(0);
            if(recommendVO!=null){
                final DynamicRecommend recommend = recommendVO.getRecommend();
                if(recommend!=null){
                    ImageLoaderManager.getImageLoaderManager(mContext).
                            displayImage(recommend.getImgUrl(), myHolder.imgDynamicRecommend,imageOptions);
                    myHolder.layoutGuessGoods.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasfocus) {
                            AppDebug.e("GuessLike position=", position + "");
                            if (hasfocus) {
                                onItemListener.onItemSelected(position);
                                myHolder.goodFocusStatus.setVisibility(View.VISIBLE);

                                if (isEmpty) {
                                    myHolder.layoutGuessGoods.setScaleX(1.05f);
                                    myHolder.layoutGuessGoods.setScaleY(1.05f);
                                } else {
                                    myHolder.layoutGuessGoods.setScaleX(1.05f);
                                    myHolder.layoutGuessGoods.setScaleY(1.05f);
                                }

                            } else {
                                myHolder.goodFocusStatus.setVisibility(View.INVISIBLE);
                                myHolder.layoutGuessGoods.setScaleX(1.0f);
                                myHolder.layoutGuessGoods.setScaleY(1.0f);
                            }
                            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String link = recommend.getLink();
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }


        } else if (holder instanceof ContentHolder) { // 内容
            final ContentHolder myHolder = (ContentHolder) holder;
            myHolder.layoutGuessGoods.setScaleX(1.0f);
            myHolder.layoutGuessGoods.setScaleY(1.0f);
            AppDebug.v("GuessLike", "数据-----" + data.get(position).toString());
            if (data != null && data.get(position) != null) {
                recommendGoods = data.get(position);

                if (recommendGoods.getFields().getMasterPic() != null
                        && recommendGoods.getFields().getMasterPic().getPicUrl() != null) {
                    String url = recommendGoods.getFields().getMasterPic().getPicUrl();
                    String tag = (String) myHolder.imgGuessLikeGoods.getTag();
                    if (!url.equals(tag)) {
                        myHolder.imgGuessLikeGoods.setTag(url);
                        //重用时会先显示已经加载出的图片，先重置为默认图
                        myHolder.imgGuessLikeGoods.setImageResource(R.drawable.new_shop_cart_img_default);
                        //设置图片
                        ImageLoaderManager.getImageLoaderManager(mContext).displayImage
                                (recommendGoods.getFields().getMasterPic().getPicUrl(), myHolder.imgGuessLikeGoods,imageOptions);
                    }
                }
                if (recommendGoods.getFields().getTitle() != null && recommendGoods.getFields().getTitle().getContext() != null
                        && recommendGoods.getFields().getTitle().getContext().getContent() != null) {
                    myHolder.tvGoodsName.setText(recommendGoods.getFields().getTitle().getContext().getContent());
                }
                if (recommendGoods.getFields().getPrice() != null && (recommendGoods.getFields().getPrice().getSymbol() != null) ||
                        recommendGoods.getFields().getPrice().getYuan() != null) {

                    String price;
                    if (!TextUtils.isEmpty(recommendGoods.getFields().getPrice().getCent())) {
                        price = recommendGoods.getFields().getPrice().getYuan()
                                + "." + recommendGoods.getFields().getPrice().getCent();
                    } else {
                        price = recommendGoods.getFields().getPrice().getYuan();
                    }
                    AppDebug.e("guessLikePrice =", recommendGoods.getFields().getPrice().toString());
                    AppDebug.e("guessLikePriceYuan =", recommendGoods.getFields().getPrice().getYuan());
                    if(recommendGoods.getFields().getPrice().getCent() != null){
                        AppDebug.e("guessLikePriceCent =", recommendGoods.getFields().getPrice().getCent());
                    }

                    price = price.replaceAll(",", "");
                    price = price.replaceAll("，", "");
                    if (price.indexOf(".") > 0) {
                        //正则表达
                        price = price.replaceAll("0+?$", "");//去掉后面无用的零
                        price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                    }
                    myHolder.tvGoodsPrice.setText(recommendGoods.getFields().getPrice().getSymbol() + price);
                }
                if (recommendGoods.getFields().getBottomTip() != null && recommendGoods.getFields().getBottomTip().getText() != null &&
                        recommendGoods.getFields().getBottomTip().getText().getContent() != null) {
                    myHolder.tvGoodsBuyCount.setText(recommendGoods.getFields().getBottomTip().getText().getContent());
                }
                //返利
                if (recommendGoods.getRebateBo() != null) {
                    RebateBo rebateBo = recommendGoods.getRebateBo();
                    Boolean isMJF=rebateBo.isMjf();
                    if(isMJF){
                        myHolder.goodlistGridItemReturnRedPacket.setVisibility(View.VISIBLE);
                    }else {
                        myHolder.goodlistGridItemReturnRedPacket.setVisibility(View.GONE);
                    }
                    String rebateBoCoupon = rebateBo.getCoupon();
                    //返利金额不为空正常展示
                    if (!TextUtils.isEmpty(rebateBoCoupon)) {
                        //couponString处理后,展示的返利金额
                        String couponString = Utils.getRebateCoupon(rebateBoCoupon);
                        if(!TextUtils.isEmpty(couponString)) {
                            AppDebug.e("Rebate", "couponString = " + couponString);
                            myHolder.layoutRebate.setVisibility(View.VISIBLE);
                            myHolder.tvRebateCoupon.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                                myHolder.tvRebateCoupon.setText(rebateBo.getCouponMessage() + " ¥ " + couponString);
                            } else {
                                myHolder.tvRebateCoupon.setText("预估 ¥ " + couponString);
                            }
                            if (!TextUtils.isEmpty(rebateBo.getPicUrl())) {
                                myHolder.ivRebateIcon.setVisibility(View.VISIBLE);
                                ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebateBo.getPicUrl(), myHolder.ivRebateIcon,imageOptions);
                            } else {
                                myHolder.ivRebateIcon.setVisibility(View.GONE);
                            }
                        }else {
                            myHolder.layoutRebate.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        //返利金额为空为预售商品
                        if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                            myHolder.tvRebateCoupon.setText(rebateBo.getCouponMessage() );
                        }
                        if (!TextUtils.isEmpty(rebateBo.getPicUrl())) {
                            myHolder.ivRebateIcon.setVisibility(View.VISIBLE);
                            ImageLoaderManager.getImageLoaderManager(mContext).displayImage(rebateBo.getPicUrl(), myHolder.ivRebateIcon,imageOptions);
                        } else {
                            myHolder.ivRebateIcon.setVisibility(View.GONE);
                        }
                        myHolder.layoutRebate.setVisibility(View.VISIBLE);
                    }
                } else {
                    myHolder.layoutRebate.setVisibility(View.INVISIBLE);
                }

                myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.utControlHit(((CoreActivity) mContext).getFullPageName(), "Items_click",
                                initTBSProperty(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY_CLICK));
                        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY_CLICK);

                        Intent intent = new Intent();
                        intent.setClassName(mContext, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
                        intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, data.get(position).getFields().getItemId());
                        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.GUESSLIKE);
                        mContext.startActivity(intent);
                    }

                });

                myHolder.layoutGuessGoods.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasfocus) {
                        AppDebug.e("GuessLike position=", position + "");
                        if (hasfocus) {
                            onItemListener.onItemSelected(position);
                            myHolder.tvGoodsName.setMaxLines(2);
                            myHolder.goodFocusStatus.setVisibility(View.VISIBLE);

                            if (isEmpty) {
                                myHolder.layoutGuessGoods.setScaleX(1.025f);
                                myHolder.layoutGuessGoods.setScaleY(1.025f);
                            } else {
                                myHolder.layoutGuessGoods.setScaleX(1.05f);
                                myHolder.layoutGuessGoods.setScaleY(1.05f);
                            }

                        } else {
                            myHolder.tvGoodsName.setMaxLines(1);
                            myHolder.goodFocusStatus.setVisibility(View.INVISIBLE);
                            myHolder.layoutGuessGoods.setScaleX(1.0f);
                            myHolder.layoutGuessGoods.setScaleY(1.0f);
                        }
                    }
                });
            }
        }
    }

    // 头部
    private class HeadHolder extends RecyclerView.ViewHolder {

        public HeadHolder(View itemView) {
            super(itemView);
        }
    }

    // 内容
    private class ContentHolder extends RecyclerView.ViewHolder {
        ImageView imgGuessLikeGoods;
        TextView tvGoodsName;
        TextView tvGoodsPrice;
        TextView tvGoodsBuyCount;
        FrameLayout layoutGuessGoods;
        ImageView goodFocusStatus;
        ImageView ivRebateIcon;
        TextView tvRebateCoupon;
        RelativeLayout layoutRebate;
        ImageView goodlistGridItemReturnRedPacket;

        public ContentHolder(View itemView) {
            super(itemView);
            imgGuessLikeGoods = (ImageView) itemView.findViewById(R.id.img_guess_like_goods);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
            tvGoodsBuyCount = (TextView) itemView.findViewById(R.id.tv_buy_count);
            layoutGuessGoods = (FrameLayout) itemView.findViewById(R.id.layout_guess_goods);
            goodFocusStatus = (ImageView) itemView.findViewById(R.id.good_focus_status);
            ivRebateIcon = (ImageView) itemView.findViewById(R.id.goodlist_grid_item_rebate_icon);
            tvRebateCoupon = (TextView) itemView.findViewById(R.id.goodlist_grid_item_rebate_coupon);
            layoutRebate = (RelativeLayout) itemView.findViewById(R.id.layout_rebate);
            goodlistGridItemReturnRedPacket = (ImageView) itemView.findViewById(R.id.goodlist_grid_item_return_red_packet);
        }

    }

    //动态推荐运营位
    private class DynamicRecommendHolder extends RecyclerView.ViewHolder {
        FrameLayout layoutGuessGoods;
        ImageView goodFocusStatus;
        ImageView imgDynamicRecommend;

        public DynamicRecommendHolder(View itemView) {
            super(itemView);
            layoutGuessGoods = (FrameLayout) itemView.findViewById(R.id.layout_guess_goods);
            goodFocusStatus = (ImageView) itemView.findViewById(R.id.good_focus_status);
            imgDynamicRecommend = (ImageView) itemView.findViewById(R.id.img_dynamic_recommend);
        }
    }

    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();


        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

        return p;

    }

}
