package com.yunos.voice.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.ImageLoader;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.voice.R;
import com.yunos.voice.activity.VoiceSearchActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/6/27.
 */

public class VoiceSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<ProductDo> getmList() {
        return mList;
    }

    private List<ProductDo> mList;
    private LayoutInflater inflater;
    private ImageLoaderManager imageLoader;
    private Context mContext;
    private int type = 1; //0 下单购买，1 跳转详情

    private int[] num_resouce = {R.drawable.item_tag_num_1, R.drawable.item_tag_num_2, R.drawable.item_tag_num_3,
            R.drawable.item_tag_num_4, R.drawable.item_tag_num_5, R.drawable.item_tag_num_6};

    private enum ItemType {
        ITEM_TYPE,
        SLIDE_TYPE
    }

    public VoiceSearchAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderManager.getImageLoaderManager(context);
    }

    public void setProductList(List<ProductDo> product) {
        mList = product;
        notifyDataSetChanged();
    }

    public void setClickType(int type) {
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product, parent, false);
        view.setFocusable(true);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;

        final ProductDo mProduct = getItem(position);

        imageLoader.displayImage(mProduct.getPicUrl().replace("_60x60.jpg", "_300x300.jpg"), itemHolder.item_product_pic);
        itemHolder.item_product_price.setText("¥" + mProduct.getDiscntPrice());
        itemHolder.item_product_title.setText(mProduct.getTitle());
        itemHolder.item_product_sold.setText("月销" + mProduct.getBiz30daySold() + "笔");
        if ((mProduct.getStdSkuSize() != null && Integer.parseInt(mProduct.getStdSkuSize()) > 1)
                || mProduct.getPresale().equals("true")) {
            itemHolder.item_product_tag_quick.setVisibility(View.GONE);
        } else {
            itemHolder.item_product_tag_quick.setVisibility(View.VISIBLE);
        }

        String postage = mProduct.getPostageTextInfo();
        if ("包邮".equals(postage)) {
            itemHolder.item_tag_exempt_postage.setVisibility(View.VISIBLE);
            itemHolder.item_product_tag_postage.setVisibility(View.GONE);
        } else {
            itemHolder.item_tag_exempt_postage.setVisibility(View.GONE);
            itemHolder.item_product_tag_postage.setVisibility(View.VISIBLE);
            itemHolder.item_product_tag_postage.setText(postage);
        }

        //展现已买过标签
        if (mProduct.getTopTag() != null && mProduct.getTopTag().equals("1000")) {
            itemHolder.item_tag_already_buy.setVisibility(View.VISIBLE);
        } else {
            if (itemHolder.item_tag_already_buy.getVisibility() == View.VISIBLE)
                itemHolder.item_tag_already_buy.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mProduct.getCoupon())) {
            itemHolder.item_product_rebate.setText("预估 ¥" + mProduct.getCoupon());
        }else if(mProduct.getCouponMessage()!=null){
            itemHolder.item_product_rebate.setText(mProduct.getCouponMessage());
        }

        ImageLoader.getInstance().displayImage(mProduct.getCouponPicUrl(),itemHolder.item_tag_rebate);

        itemHolder.item_product_tag_num.setImageDrawable(mContext.getResources().getDrawable(num_resouce[position % 6]));

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("VoiceSearchActivity", "VoiceSearchAdapter.onClick itemHolder");
                if (type == 0) {
                    Intent intent = new Intent();
                    if (Integer.parseInt(mProduct.getStdSkuSize()) > 1) {
                        intent.setData(Uri.parse("tvtaobao://home?module=sureJoin&itemId=" + mProduct.getItemId() + "&from=voice_system&notshowloading=true"));
                        mContext.startActivity(intent);

                    } else {
                        TvOptionsConfig.setTvOptionsVoice(true);
                        TvOptionsConfig.setTvOptionsSystem(true);
                        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.FAST_ORDER);
                        intent.setData(Uri.parse("tvtaobao://home?app=voice&module=createorder&itemId=" +
                                mProduct.getItemId() + "&skuSize=" + mProduct.getStdSkuSize() +
                                "&rebate=" + mProduct.getCoupon()+"&price="+mProduct.getDiscntPrice()+"&from=voice_system&notshowloading=true" ));
                        mContext.startActivity(intent);
                    }

                    type = 1;
                    if (mContext instanceof VoiceSearchActivity) {
                        VoiceSearchActivity activity = (VoiceSearchActivity) mContext;
                        Map<String, String> map = activity.getProperties();
                        map.put("itemId", mProduct.getItemId());
                        Utils.utCustomHit(activity.getPageName(), "Voice_search_buy_" + Config.getChannelName(), map);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse("tvtaobao://home?module=detail&itemId=" + mProduct.getItemId() + "&from=voice_system&notshowloading=true"));
                    mContext.startActivity(intent);

                    type = 1;
                    if (mContext instanceof VoiceSearchActivity) {
                        VoiceSearchActivity activity = (VoiceSearchActivity) mContext;
                        Map<String, String> map = activity.getProperties();
                        map.put("itemId", mProduct.getItemId());
                        Utils.utCustomHit(activity.getPageName(), "Voice_search_see_" + Config.getChannelName(), map);
                    }
                }
            }
        });

        itemHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ViewCompat.animate(view).scaleX(1.08f).scaleY(1.08f).setDuration(300).start();
                } else {
                    ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                }
            }
        });

    }

    private ProductDo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() >= 6 ? 6 : mList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private RoundImageView item_product_pic;
        private LinearLayout item_product_rebate_layout;
        private ImageView item_product_tag_quick, item_product_tag_num, item_tag_exempt_postage, item_tag_already_buy,item_tag_rebate;
        private TextView item_product_title, item_product_price, item_product_sold, item_product_tag_postage, item_product_rebate;

        public ItemHolder(View itemView) {
            super(itemView);
            item_product_pic = (RoundImageView) itemView.findViewById(R.id.item_product_pic);
            item_product_pic.setRound(true, false, true, false);
            item_product_tag_quick = (ImageView) itemView.findViewById(R.id.item_product_tag_quick);
            item_product_tag_postage = (TextView) itemView.findViewById(R.id.item_product_tag_postage);
            item_product_title = (TextView) itemView.findViewById(R.id.item_product_title);
            item_product_price = (TextView) itemView.findViewById(R.id.item_product_price);
            item_product_sold = (TextView) itemView.findViewById(R.id.item_product_sold);
            item_product_rebate = (TextView) itemView.findViewById(R.id.item_product_rebate);
            item_product_tag_num = (ImageView) itemView.findViewById(R.id.item_product_tag_num);
            item_tag_exempt_postage = (ImageView) itemView.findViewById(R.id.item_tag_exempt_postage);
            item_tag_already_buy = (ImageView) itemView.findViewById(R.id.item_tag_already_buy);
            item_product_rebate_layout = (LinearLayout) itemView.findViewById(R.id.item_product_rebate_layout);
            item_tag_rebate = itemView.findViewById(R.id.img_rebate_tag);
        }
    }
}
