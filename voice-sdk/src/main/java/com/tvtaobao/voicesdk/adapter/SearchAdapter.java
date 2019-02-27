package com.tvtaobao.voicesdk.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.ImageLoader;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.dialogs.CreateOrderDialog;
import com.tvtaobao.voicesdk.dialogs.SearchDialog;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/10/31.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ProductDo> mList;
    private ImageLoaderManager mImageLoaderManager;
    private String keyWords;

    private int[] num_resouce = {R.drawable.item_tag_num_1, R.drawable.item_tag_num_2, R.drawable.item_tag_num_3};

    private boolean needShowMore = false;

    private int click_type = 1; //0 下单购买，1 跳转详情

    private CreateOrderDialog createOrderDialog = null;

    private DecimalFormat format = new DecimalFormat("0.##");

    private enum ItemType {
        TYPE_PRODUCT, TYPE_MORE
    }

    private SearchDialog mDialog;

    public SearchAdapter(Context context, SearchDialog dialog) {
        this.mContext = context;
        this.mDialog = dialog;
        inflater = LayoutInflater.from(context);
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
    }

    private boolean isOnKey;

    public void setAction(boolean isOnKey) {
        this.isOnKey = isOnKey;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public void initData(List<ProductDo> list) {
        if (list != null && list.size() >= 3) {
            needShowMore = false;
        } else {
            needShowMore = true;
        }
        // 外部有对List的操作，会导致数据一致性异常，所以弄个List自己用 #16967792
        List<ProductDo> tmp = new ArrayList<>();
        tmp.addAll(list);
        this.mList = tmp;
        notifyDataSetChanged();
    }

    public void setClickType(int type) {
        this.click_type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if (needShowMore) {
            if (position == getItemCount() - 1) {
                return ItemType.TYPE_MORE.ordinal();
            } else {
                return ItemType.TYPE_PRODUCT.ordinal();
            }
        } else {
            return ItemType.TYPE_PRODUCT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.TYPE_PRODUCT.ordinal()) {
            View view = inflater.inflate(R.layout.ictc_item_product, parent, false);
//            view.setFocusable(true);
            return new ProductHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_more, parent, false);
//            view.setFocusable(true);
            return new MoreHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isOnKey)
            holder.itemView.setFocusable(true);
        else
            holder.itemView.setFocusable(false);

        if (holder instanceof ProductHolder) {
            ProductHolder mProductHolder = (ProductHolder) holder;

            final ProductDo mProduct = mList.get(position);
            mProductHolder.mImage.setImageDrawable(null);
            mImageLoaderManager.displayImage(mProduct.getPicUrl(), mProductHolder.mImage);

            if (mProduct.getTopTag() != null && mProduct.getTopTag().equals("1000")) {
                mProductHolder.mTagAlready.setVisibility(View.VISIBLE);
            } else {
                mProductHolder.mTagAlready.setVisibility(View.GONE);
            }

            if ((mProduct.getStdSkuSize() != null && Integer.parseInt(mProduct.getStdSkuSize()) > 1)
                    || mProduct.getPresale().equals("true")) {
                mProductHolder.mTagQuick.setVisibility(View.GONE);
            } else {
                mProductHolder.mTagQuick.setVisibility(View.VISIBLE);
            }

            if (mProduct.getPostageTextInfo().equals("包邮")) {
                mProductHolder.mFreePostage.setVisibility(View.VISIBLE);
                mProductHolder.mPostage.setVisibility(View.GONE);
            } else {
                mProductHolder.mFreePostage.setVisibility(View.GONE);
                mProductHolder.mPostage.setText(mProduct.getPostageTextInfo());
                mProductHolder.mPostage.setVisibility(View.VISIBLE);
            }

            mProductHolder.mTitle.setText(mProduct.getTitle());
            mProductHolder.mPrice.setIncludeFontPadding(false);
            mProductHolder.mPrice.setText("¥ " + format.format(Double.parseDouble(mProduct.getDiscntPrice())));
            if(mProduct.getCoupon()!=null){
                mProductHolder.mRebate.setText("预估 ¥ " + format.format(Double.parseDouble(mProduct.getCoupon())));
            }else if(mProduct.getCouponMessage()!=null){
                //预售商品不返回coupon，但是会返回couponmessage
                mProductHolder.mRebate.setText(mProduct.getCouponMessage());
            }

            if(mProduct.getCouponPicUrl()!=null){
                ImageLoader.getInstance().displayImage(mProduct.getCouponPicUrl(),mProductHolder.mRebateTag);
            }

            mProductHolder.mNum.setImageDrawable(mContext.getResources().getDrawable(num_resouce[position % 3]));

            mProductHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (click_type == 0) {
                        if (Integer.parseInt(mProduct.getStdSkuSize()) > 1) {
                            mDialog.dismiss();

                            Intent intent = new Intent();
                            intent.setData(Uri.parse("tvtaobao://home?app=taobaosdk&module=sureJoin&itemId=" + mProduct.getItemId() + "&notshowloading=true"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } else {
                            if (createOrderDialog != null) {
                                createOrderDialog.dismiss();
                                createOrderDialog = null;
                            }
                            TvOptionsConfig.setTvOptionsVoice(true);
                            TvOptionsConfig.setTvOptionsSystem(false);
                            TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.FAST_ORDER);

                            createOrderDialog = new CreateOrderDialog(mContext);
                            createOrderDialog.setItemId(mProduct.getItemId());
                            createOrderDialog.setPrice(mProduct.getDiscntPrice());
                            createOrderDialog.show();
                        }

                        click_type = 1;

                        Map<String, String> map = getProperties();
                        map.put("itemId", mProduct.getItemId());
                        Utils.utCustomHit("VoiceCard_search_buy", map);
                    } else {
                        mDialog.dismiss();

                        Intent intent = new Intent();
                        intent.setData(Uri.parse("tvtaobao://home?module=detail&itemId=" + mProduct.getItemId() + "&notshowloading=true"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);

                        click_type = 1;

                        Map<String, String> map = getProperties();
                        map.put("itemId", mProduct.getItemId());
                        Utils.utCustomHit("VoiceCard_search_see" + Config.getChannelName(), map);
                    }
                }
            });
        } else if (holder instanceof MoreHolder) {
            MoreHolder moreHolder = (MoreHolder) holder;
            moreHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                    Intent intent = new Intent();
                    intent.setData(Uri.parse("tvtaobao://home?module=goodsList&keywords=" + keyWords + "&tab=mall&notshowloading=true&from=voice_application"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ViewCompat.animate(view).scaleX(1.14f).scaleY(1.14f).start();
                } else {
                    ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).start();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() >= 3 ? mList.size() : mList.size() + 1;
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        LinearLayout mRebateLayout;
        RoundImageView mImage;
        ImageView mTagQuick, mTagAlready, mFreePostage, mNum,mRebateTag;
        TextView mTitle, mPrice, mPostage,mRebate;

        public ProductHolder(View itemView) {
            super(itemView);
            mImage = (RoundImageView) itemView.findViewById(R.id.item_product_image);
            mImage.setRound(true, true, false, false);
            mTagQuick = (ImageView) itemView.findViewById(R.id.item_product_tag_quick);
            mTagAlready = (ImageView) itemView.findViewById(R.id.item_product_tag_already_buy);
            mFreePostage = (ImageView) itemView.findViewById(R.id.item_product_free_postage);
            mPostage = (TextView) itemView.findViewById(R.id.item_product_postage);
            mNum = (ImageView) itemView.findViewById(R.id.item_product_tag_num);
            mTitle = (TextView) itemView.findViewById(R.id.item_product_title);
            mPrice = (TextView) itemView.findViewById(R.id.item_product_price);
            mRebateLayout = (LinearLayout) itemView.findViewById(R.id.layout_rebate_info);
            mRebate = (TextView) itemView.findViewById(R.id.item_product_rebate);
            mRebateTag = itemView.findViewById(R.id.img_rebate_tag);
        }
    }

    class MoreHolder extends RecyclerView.ViewHolder {

        public MoreHolder(View itemView) {
            super(itemView);
        }
    }

    protected Map<String, String> getProperties() {
        Map<String, String> p = new HashMap<String, String>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }
        p.put("channel", Config.getChannelName());
        return p;
    }
}
