package com.yunos.tvtaobao.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;
import com.yunos.tvtaobao.biz.request.ztc.ZTCUtils;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.activity.SearchResultActivity;
import com.yunos.tvtaobao.search.widget.CustomImageSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GoodsListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "GoodsListAdapter";
    private Context context;
    private String searchTitle;
    private ArrayList<SearchedGoods> goodsList;
    private ImageLoaderManager imageLoader;
    private String priceSymbol = null;
    private DisplayImageOptions mImageOptions;

    public GoodsListAdapter(Context context) {
        this.context = context;
        imageLoader = ImageLoaderManager.getImageLoaderManager(context);
        goodsList = new ArrayList<>();
        priceSymbol = context.getResources().getString(R.string.ytm_search_price_symbol);
        if (mImageOptions == null) {
            boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
            mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(beta).cacheInMemory(!beta).build();
        }
    }

    public void setData(String searchTitle, GoodsSearchResultDo data) {
        this.goodsList.clear();
        this.searchTitle = searchTitle;
        if (data != null && data.getGoodList() != null && data.getGoodList().length > 0) {
            List<SearchedGoods> arrayList = new ArrayList(Arrays.asList(data.getGoodList()));
            this.goodsList.addAll(arrayList);
        }
    }

    public void addData(GoodsSearchResultDo data) {
        if (data.getGoodList() != null && data.getGoodList().length > 0) {
            List<SearchedGoods> arrayList = new ArrayList(Arrays.asList(data.getGoodList()));
            this.goodsList.addAll(arrayList);
            notifyItemChanged(getItemCount());
        }
    }

    public enum ItemType {
        TYPE_HEADER, TYPE_CONTENT, TYPE_FOOTER
    }

    public boolean isHead(int position) {
        return position == 0;
    }

    public boolean isFoot(int position) {
        return position == goodsList.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.TYPE_HEADER.ordinal()) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_goodslist_header, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == ItemType.TYPE_FOOTER.ordinal()) {
            return new FooterHolder(new TextView(context));
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product_layout, parent, false);
            view.setFocusable(true);
            return new GoodsHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.TYPE_HEADER.ordinal();
        } else if (position == getItemCount() - 1) {
            return ItemType.TYPE_FOOTER.ordinal();
        } else {
            return ItemType.TYPE_CONTENT.ordinal();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderHolder) {
            HeaderHolder headerViewHolder = (HeaderHolder) holder;
            headerViewHolder.titleText.setText(searchTitle);
        }
        if (holder instanceof GoodsHolder) {
            final GoodsHolder goodsHolder = (GoodsHolder) holder;
            final SearchedGoods searchedGoods = goodsList.get(position - 1);
            if(searchedGoods==null){
                return;
            }
            imageLoader.displayImage(searchedGoods.getImgUrl() + "_300x300.jpg", goodsHolder.goodsImage);
            //设置商品名前的标签
            String iconTag = searchedGoods.getTagPicUrl();
            AppDebug.i(TAG, TAG + ".position : " + position + ", iconTag : " + iconTag);
            if (!TextUtils.isEmpty(iconTag)) {
                imageLoader.loadImage(iconTag, mImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        AppDebug.i(TAG, TAG + ".loadImage position : " + position);
                        if (!TextUtils.isEmpty(searchedGoods.getTitle())) {
                            SpannableString spannableString = new SpannableString("  " + searchedGoods.getTitle());
                            CustomImageSpan imageSpan = new CustomImageSpan(context, imageScale(loadedImage, context.getResources().getDimensionPixelOffset(R.dimen.dp_20)), ImageSpan.ALIGN_BASELINE);
                            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            goodsHolder.goodsTitle.setText(spannableString);
                        }else {
                            goodsHolder.goodsTitle.setText("");
                        }
                    }
                });
            } else {
                if (!TextUtils.isEmpty(searchedGoods.getTitle())) {
                    goodsHolder.goodsTitle.setText(searchedGoods.getTitle());
                }else {
                    goodsHolder.goodsTitle.setText("");
                }
            }
            if(!TextUtils.isEmpty(searchedGoods.getPrice())) {
                String price = searchedGoods.getPrice();
                if (price.indexOf(".") > 0) {
                    //正则表达
                    price = price.replaceAll("0+?$", "");//去掉后面无用的零
                    price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                }
                goodsHolder.goodsPrice.setText(priceSymbol + price);
                goodsHolder.goodsPrice.setVisibility(View.VISIBLE);
            }else {
                goodsHolder.goodsPrice.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(searchedGoods.getSoldText())) {
                goodsHolder.goodsSales.setText(searchedGoods.getSoldText());
                goodsHolder.goodsSales.setVisibility(View.VISIBLE);
            } else {
                goodsHolder.goodsSales.setVisibility(View.GONE);
            }
            RebateBo rebateBo = searchedGoods.getRebateBo();
            //设置返利信息
            setRebate(rebateBo, goodsHolder);
            //设置店铺名
            if (!TextUtils.isEmpty(searchedGoods.getNick())) {
                goodsHolder.goodsShopName.setText(searchedGoods.getNick());
            } else {
                goodsHolder.goodsShopName.setText(com.yunos.tvtaobao.search.R.string.shop_name);
            }

            //设置标签：广告标，包邮，预售立减，买就返红包，满88包邮
            goodsHolder.tvTag1.setVisibility(View.INVISIBLE);
            goodsHolder.tvTag2.setVisibility(View.INVISIBLE);
            goodsHolder.tvTag3.setVisibility(View.INVISIBLE);
            if (searchedGoods.getTagNames() != null && searchedGoods.getTagNames().size() > 0) {
                List<String> listTagNames = searchedGoods.getTagNames();
                for (int i = 0; i < listTagNames.size(); i++) {
                    if (i == 0 && !TextUtils.isEmpty(listTagNames.get(i))) {
                        setTag(goodsHolder.tvTag1,listTagNames.get(i));
                    } else if (i == 1 && !TextUtils.isEmpty(listTagNames.get(i))) {
                        setTag(goodsHolder.tvTag2,listTagNames.get(i));
                    } else if (i == 2 && !TextUtils.isEmpty(listTagNames.get(i))) {
                        setTag(goodsHolder.tvTag3,listTagNames.get(i));
                    }
                }
            }

            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocus) {
                    if (isFocus) {
                        ViewCompat.animate(view).scaleX(1.05f).scaleY(1.05f).start();
                        goodsHolder.focusBox.setVisibility(View.VISIBLE);
                    } else {
                        ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).start();
                        goodsHolder.focusBox.setVisibility(View.GONE);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.utControlHit(SearchResultActivity.TAG, position + "", initTBSProperty(SPMConfig.GOODS_LIST_SPM_ITEM_P_NAME));


                    if (SearchedGoods.TYPE_ZTC.equals(searchedGoods.getType())) {
                        ZTCUtils.getZtcItemId((SearchResultActivity) context, searchedGoods.getEurl(), new ZTCUtils.ZTCItemCallback() {
                            @Override
                            public void onSuccess(String id) {
                                Map<String, String> exparams = new HashMap<String, String>();
                                exparams.put("isZTC", "true");
                                enterDisplayDetail(id, exparams);
                            }

                            @Override
                            public void onFailure(String msg) {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String name = context.getResources().getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_open);
                                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    } else {
                        enterDisplayDetail(searchedGoods.getItemId(), null);
                    }
                }
            });
        }

        if (holder instanceof FooterHolder) {

        }
    }

    public  void setTag(TextView view,String  text){
        if(view!=null&&!TextUtils.isEmpty(text)) {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
            if (text.equals("广告")) {
                view.setTextColor(context.getResources().getColor(R.color.takeout_color_a2aaba));
                view.setBackgroundResource(R.drawable.ytm_goods_item_tag_ad);
            } else {
                view.setTextColor(context.getResources().getColor(R.color.color_ff3333));
                view.setBackgroundResource(R.drawable.ytm_goods_item_tag);
            }
        }else {
            view.setVisibility(View.GONE);
        }

    }


    public Bitmap imageScale(Bitmap bitmap, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_h, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    /**
     * 进入详情页
     *
     * @param itemId 商品的ID号
     */
    protected void enterDisplayDetail(String itemId, Map<String, String> exParams) {

        AppDebug.i(TAG, "enterDisplayDetail itemId = " + itemId + "is");


        if (TextUtils.isEmpty(itemId)) {
            String name = context.getResources().getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_open);
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetWorkUtil.isNetWorkAvailable()) {
            String name = context.getResources().getString(com.yunos.tvtaobao.businessview.R.string.ytbv_network_connect_err);
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            StringBuilder urlBuilder = new StringBuilder("tvtaobao://home?app=taobaosdk&module=detail&itemId=");
            urlBuilder.append(itemId);
            if (exParams != null) {
                for (String key : exParams.keySet()) {
                    urlBuilder.append(String.format("&%s=%s", key, exParams.get(key)));
                }
            }
            String url = urlBuilder.toString();
            AppDebug.i("url", "url = " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, "tvtaobao");
            context.startActivity(intent);
            return;
        } catch (Exception e) {
            String name = context.getResources().getString(R.string.ytbv_not_open);
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
        }
    }

    //设置返利信息
    public void setRebate(RebateBo rebateBo, final GoodsHolder holder) {
        if (rebateBo != null && holder != null) {
            String rebateBoCoupon = rebateBo.getCoupon();
            //rebateBoCoupon 为null怎么是预售商品，只展示返利标和文案，不展示金额
            if (!TextUtils.isEmpty(rebateBoCoupon)) {
                String couponString = Utils.getRebateCoupon(rebateBoCoupon);
                //rebateBoCoupon 为大于0则正常展示
                if (!TextUtils.isEmpty(couponString)) {
                    holder.goodsRebate.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                        holder.goodsRebate.setText(rebateBo.getCouponMessage() + priceSymbol + couponString);
                    } else {
                        holder.goodsRebate.setText("预估" + priceSymbol + couponString);

                    }
                    if (!TextUtils.isEmpty(rebateBo.getPicUrl()) && imageLoader != null) {
                        imageLoader.loadImage(rebateBo.getPicUrl(), mImageOptions
                                , new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        holder.rebateTips.setImageBitmap(loadedImage);
                                        holder.rebateTips.setVisibility(View.VISIBLE);
                                    }
                                });
                    } else {
                        holder.rebateTips.setVisibility(View.GONE);
                    }
                } else {
                    //rebateBoCoupon 为等于0则不展示
                    holder.rebateTips.setVisibility(View.INVISIBLE);
                    holder.goodsRebate.setVisibility(View.INVISIBLE);

                }
            } else {
                //rebateBoCoupon 为null怎么是预售商品，只展示返利标和文案，不展示金额
                if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                    holder.goodsRebate.setVisibility(View.VISIBLE);
                    holder.goodsRebate.setText(rebateBo.getCouponMessage());
                } else {
                    holder.goodsRebate.setVisibility(View.GONE);
                    holder.goodsRebate.setText("");
                }
                if (!TextUtils.isEmpty(rebateBo.getPicUrl()) && imageLoader != null) {
                    imageLoader.loadImage(rebateBo.getPicUrl(), mImageOptions
                            , new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    holder.rebateTips.setImageBitmap(loadedImage);
                                    holder.rebateTips.setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    holder.rebateTips.setVisibility(View.GONE);

                }
            }

        } else {
            holder.rebateTips.setVisibility(View.INVISIBLE);
            holder.goodsRebate.setVisibility(View.INVISIBLE);

        }
    }


    @Override
    public int getItemCount() {
        if (goodsList == null) {
            return 0;
        }

        if (goodsList.size() <= 0) {
            return 0;
        }

        return goodsList.size() + 2;
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView titleText;

        public HeaderHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.item_iv_goods_title);
        }
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        private TextView footerText;

        public FooterHolder(View itemView) {
            super(itemView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.topMargin = (int) context.getResources().getDimension(R.dimen.dp_38);
            lp.bottomMargin = (int) context.getResources().getDimension(R.dimen.dp_60);
            footerText = (TextView) itemView;
            footerText.setText(R.string.no_more_goods);
            footerText.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_28));
            footerText.setTextColor(Color.parseColor("#8c94a3"));
            footerText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            footerText.setLayoutParams(lp);
        }
    }

    private class GoodsHolder extends RecyclerView.ViewHolder {
        private ImageView focusBox;
        private ImageView goodsImage, rebateTips;
        private TextView goodsTitle, goodsPrice, goodsRebate, goodsShopName, goodsSales;
        private LinearLayout goodsInfo;
        //标签
        private TextView tvTag1, tvTag2, tvTag3;


        public GoodsHolder(View itemView) {
            super(itemView);
            focusBox = (ImageView) itemView.findViewById(R.id.item_iv_goods_focus_box);
            goodsImage = itemView.findViewById(R.id.item_iv_product_picture);
            goodsTitle = itemView.findViewById(R.id.item_tv_title);
            goodsPrice = itemView.findViewById(R.id.item_tv_price);
//            返利标签
            rebateTips = (ImageView) itemView.findViewById(R.id.item_rebate_tips);
//            返利金额
            goodsRebate = (TextView) itemView.findViewById(R.id.item_tv_yugufan);
            goodsInfo = itemView.findViewById(R.id.item_ly_goods_info);
            goodsShopName = itemView.findViewById(R.id.item_tv_shopname);
            goodsSales = itemView.findViewById(R.id.item_iv_sale);
            tvTag1 = (TextView) itemView.findViewById(R.id.iv_tag1);
            tvTag2 = (TextView) itemView.findViewById(R.id.iv_tag2);
            tvTag3 = (TextView) itemView.findViewById(R.id.iv_tag3);
        }
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(Config.getAppKey())) {
            p.put("appkey", Config.getAppKey());
        }
        try {
            if (!TextUtils.isEmpty(AppInfo.getAppVersionName())) {
                p.put("version", AppInfo.getAppVersionName());
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }
        return p;
    }


}
