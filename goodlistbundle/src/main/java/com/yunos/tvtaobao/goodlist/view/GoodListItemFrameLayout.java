/**
 *
 */
package com.yunos.tvtaobao.goodlist.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.common.DrawRect;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.TabGoodsItemView;
import com.yunos.tvtaobao.goodlist.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class GoodListItemFrameLayout extends TabGoodsItemView {

    private final String TAG = "GoodListItemFrameLayout";
    private String mSymbol = " ¥ ";

    private SearchedGoods mGoods;

    private int ItemTitleWidth;
    private int ItemTitleHeight_No;
    private int ItemTitleHeight_Selected;
    private int ItemTitle_Layout_Left;
    private int ItemTitle_Layout_Top_No;
    private int ItemTitle_Layout_Top_Selected;



    private int ItemPriceWidth;
    private int ItemPriceHeight;
    private int ItemPrice_Layout_Left;
    private int ItemPrice_Layout_Top_No_Selected;
    private int ItemPrice_Layout_Top_Selected;


    // 微调的区域
    private Rect mItemFocusBound;
    private DrawRect mDrawRect;

    private ImageView ztcIcon;
    private TextView titleView;
    private TextView tvRebateCoupon;
    private ImageView ivRebate;
    private RelativeLayout layoutRebate;

    private TextView tvPrice;
    private ImageView d11_tag;
    private ImageView d11_pretag, goodlist_grid_item_presell, goodlist_grid_item_return_red_packet;
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions mImageOptions;


    /**
     * @param context
     */
    public GoodListItemFrameLayout(Context context) {
        super(context);
        onInitGoodListItemFrameLayout(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public GoodListItemFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitGoodListItemFrameLayout(context);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GoodListItemFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitGoodListItemFrameLayout(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        titleView = (TextView) findViewById(getGoodsTitleViewResId());
        tvPrice = (TextView) findViewById(getGoodsPriceViewResId());
        ztcIcon = (ImageView) findViewById(R.id.ztcIcon);
        d11_tag = (ImageView) findViewById(R.id.d11tag);
        d11_pretag = (ImageView) findViewById(R.id.d11pretag);
        goodlist_grid_item_return_red_packet = (ImageView) findViewById(R.id.goodlist_grid_item_return_red_packet);
        goodlist_grid_item_presell = (ImageView) findViewById(R.id.goodlist_grid_item_presell);
        tvRebateCoupon = (TextView) findViewById(getGoodsRebateCouponViewResId());
        ivRebate = (ImageView) findViewById(getGoodsRebateIconViewResId());
        layoutRebate = (RelativeLayout) findViewById(R.id.layout_rebate);
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(getContext());
        if (mImageOptions == null) {
            boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
            mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(beta).cacheInMemory(!beta).build();
        }
    }

    /**
     * 初始化条目VIew
     *
     * @param context
     */
    private void onInitGoodListItemFrameLayout(Context context) {

        ItemTitleWidth = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_226);
        ItemTitleHeight_No = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_46);
        ItemTitleHeight_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_64);

        ItemTitle_Layout_Left = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_0);
        ItemTitle_Layout_Top_No = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_210);
        ItemTitle_Layout_Top_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_174);

        //价格
        ItemPriceWidth = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_226);
        ItemPriceHeight = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_27);
        ItemPrice_Layout_Left = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_0);
        ItemPrice_Layout_Top_No_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_244);
        ItemPrice_Layout_Top_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_226);



        mItemFocusBound = new Rect();
        mItemFocusBound.setEmpty();
        // 焦点框微调
        mItemFocusBound.left = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);
        mItemFocusBound.right = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_2);
        mItemFocusBound.top = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_2);
        mItemFocusBound.bottom = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_4);
        mDrawRect = new DrawRect(this);
    }

    /**
     * 设置是否被选中
     *
     * @param selected
     */
    public void setSelect(boolean selected) {
        if (!selected) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawRect.drawRect(canvas, this);
    }

    /**
     * 设置 商品的数据
     *
     * @param goods
     */
    public void onSetItemGoods(SearchedGoods goods) {

        mGoods = goods;
//        ImageView postage_free = (ImageView) findViewById(R.id.goodlist_postage_item_imageview);
        if (mGoods != null) {
            String title_TextString = mGoods.getTitle();
            if (!TextUtils.isEmpty(title_TextString)) {
                titleView.setText(title_TextString);
            } else {
                titleView.setVisibility(View.GONE);
            }
            if (mGoods != null && mGoods.isPre() != null && mGoods.isPre()) {
                goodlist_grid_item_presell.setVisibility(VISIBLE);
            } else {
                goodlist_grid_item_presell.setVisibility(GONE);
            }

            if (mGoods != null && mGoods.isBuyCashback() != null && mGoods.isBuyCashback()) {
                goodlist_grid_item_return_red_packet.setVisibility(VISIBLE);
            } else {
                goodlist_grid_item_return_red_packet.setVisibility(GONE);
            }

            RebateBo rebateBo = mGoods.getRebateBo();
            layoutRebate.setVisibility(INVISIBLE);
            if (rebateBo != null) {
                String rebateBoCoupon = rebateBo.getCoupon();
                //rebateBoCoupon 为null怎么是预售商品，只展示返利标和文案，不展示金额
                if (!TextUtils.isEmpty(rebateBoCoupon)) {
                    String couponString = Utils.getRebateCoupon(rebateBoCoupon);
                    //rebateBoCoupon 为大于0则正常展示
                    if (!TextUtils.isEmpty(couponString)) {
                        layoutRebate.setVisibility(VISIBLE);
                        tvRebateCoupon.setVisibility(VISIBLE);
                        if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                            tvRebateCoupon.setText(rebateBo.getCouponMessage() + " ¥ " + couponString);
                        } else {
                            tvRebateCoupon.setText("预估" + " ¥ " + couponString);

                        }
                        if (!TextUtils.isEmpty(rebateBo.getPicUrl()) && mImageLoaderManager != null) {
                            mImageLoaderManager.loadImage(rebateBo.getPicUrl() + "_140x140.jpg", mImageOptions
                                    , new SimpleImageLoadingListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view, loadedImage);
                                            ivRebate.setImageBitmap(loadedImage);
                                            ivRebate.setVisibility(VISIBLE);
                                        }
                                    });
                        } else {
                            ivRebate.setVisibility(GONE);

                        }
                    } else {
                        //rebateBoCoupon 为等于0则不展示
                        layoutRebate.setVisibility(INVISIBLE);
                    }
                } else {
                    //rebateBoCoupon 为null怎么是预售商品，只展示返利标和文案，不展示金额
                    layoutRebate.setVisibility(VISIBLE);
                    if (!TextUtils.isEmpty(rebateBo.getCouponMessage())) {
                        tvRebateCoupon.setVisibility(VISIBLE);
                        tvRebateCoupon.setText(rebateBo.getCouponMessage());
                    }else {
                        tvRebateCoupon.setVisibility(GONE);
                        tvRebateCoupon.setText("");
                    }
                    if (!TextUtils.isEmpty(rebateBo.getPicUrl()) && mImageLoaderManager != null) {
                        mImageLoaderManager.loadImage(rebateBo.getPicUrl() + "_140x140.jpg", mImageOptions
                                , new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        ivRebate.setImageBitmap(loadedImage);
                                        ivRebate.setVisibility(VISIBLE);
                                    }
                                });
                    } else {
                        ivRebate.setVisibility(GONE);

                    }
                }

            } else {
                layoutRebate.setVisibility(GONE);


            }

            String priceWmTemp = mGoods.getWmPrice();// 无线端价格，如果没有则显示sku最优价格
            String priceTemp = mGoods.getPrice(); //sku最优价格

            if (TextUtils.isEmpty(priceWmTemp)) {
                priceWmTemp = priceTemp;
            }

            // 过滤 元 字
            if (priceTemp != null) {
                priceTemp = priceTemp.split("元")[0];
            }

            if (priceWmTemp != null) {
                priceWmTemp = priceWmTemp.split("元")[0];

                // 如果折扣价的位数超过 7 位，则原价不显示
                //            if (priceRateTemp.length() <= 7) {
                //                originalDisplay = true;
                //            }
            }
            String spaceStr = " ";// 增加空格是为了与其他字符对齐，因为直接读取人民币符号编码是英文字符。
            //为了处理后台返回的科学计数法格式的价格
            String finalPrice = "";
            if (!TextUtils.isEmpty(priceWmTemp)) {
                BigDecimal bigDecimal = new BigDecimal(priceWmTemp);
                finalPrice = bigDecimal.toPlainString();
            }
            String priceRateStr = spaceStr + mSymbol + finalPrice;

            tvPrice.setText(priceRateStr);


            //            if (postage_free != null) {
            //                String fastPostFeeTemp = mGoods.getFastPostFee();
            //                if ((fastPostFeeTemp != null) && (fastPostFeeTemp.equals("0.00"))) {
            //                    postage_free.setVisibility(View.VISIBLE);
            //                } else {
            //                    postage_free.setVisibility(View.GONE);
            //                }
            //            }

//            if (TextUtils.isEmpty(mGoods.getSold()) || "0".equals(mGoods.getSold().trim())) {
//                ItemTitle_Layout_Top_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_190);
//            } else {
//                ItemTitle_Layout_Top_Selected = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_174);
//            }
            if ("true".equals(mGoods.getS11())) {
                d11_tag.setVisibility(View.VISIBLE);
            } else {
                d11_tag.setVisibility(View.GONE);
            }

            if ("true".equals(mGoods.getS11Pre())) {
                d11_pretag.setVisibility(View.VISIBLE);
                if ("1".equals(GlobalConfig.instance.getDouble11Action().getStep())) {
                    d11_pretag.setImageResource(R.drawable.goodlist_d11_pretag1);
                } else if ("2".equals(GlobalConfig.instance.getDouble11Action().getStep())) {
                    d11_pretag.setImageResource(R.drawable.goodlist_d11_pretag2);
                }
            } else {
                d11_pretag.setVisibility(View.GONE);
            }

            loadZtcIcon();

        }
    }


    private void loadZtcIcon() {

        if (SearchedGoods.TYPE_ZTC.equals(mGoods.getType())) {
            GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
            if (gc == null || gc.getZtcConfig() == null)
                return;
            if (gc.getZtcConfig().is_show && !TextUtils.isEmpty(gc.getZtcConfig().icon_url)) {
                mImageLoaderManager.loadImage(gc.getZtcConfig().icon_url, mImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (SearchedGoods.TYPE_ZTC.equals(mGoods.getType()))
                            ztcIcon.setImageBitmap(loadedImage);
                        AppDebug.e(TAG, "ztcIcon onLoadingComplete");
                    }
                });
            }
        } else {
            ztcIcon.setImageDrawable(null);
        }


    }

    public SearchedGoods onGetItemGoods() {
        return mGoods;
    }

    @Override
    protected boolean isShowTitleOfNotSelect() {
        return true;
    }

    @Override
    protected int getBackgroudViewResId() {
        return R.id.goodlist_grid_backgroud_imageview;
    }

    @Override
    protected int getGoodsTitleViewResId() {
        return R.id.goodlist_grid_item_text;
    }

    @Override
    protected int getGoodsPriceViewResId() {
        return R.id.goodlist_grid_item_price;
    }

    @Override
    protected int getGoodsRebateCouponViewResId() {
        return R.id.goodlist_grid_item_rebate_money;
    }

    @Override
    protected int getGoodsRebateIconViewResId() {
        return R.id.goodlist_grid_item_rebate_icon;
    }

    @Override
    protected int getInfoDrawableViewResId() {
        return R.id.goodlist_grid_item_imageview;
    }

    @Override
    protected int getGoodsDrawableViewResId() {
        return R.id.goodlist_grid_goods_imageview;
    }

    @Override
    protected void handlerItemSelected(String tabkey, int position, boolean isSelected, View fatherView) {

        AppDebug.i(TAG, "onItemSelected  ---->  position = " + position + ";  isSelected = " + isSelected
                + ", this  = " + this);

        TextView title = (TextView) findViewById(getGoodsTitleViewResId());
        TextView price = (TextView) findViewById(getGoodsPriceViewResId());

        if (title == null) {
            return;
        }

        // 根据选中状态更新卡片的蒙版
        setSelect(isSelected);

//        RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(ItemTitleWidth, ItemTitleHeight_No);
//        titleLp.setMargins(ItemTitle_Layout_Left, ItemTitle_Layout_Top_No, 0, 0);
//        title.setLayoutParams(titleLp);
//
//        RelativeLayout.LayoutParams priceLp = new RelativeLayout.LayoutParams(ItemPriceWidth, ItemPriceHeight);
//        priceLp.setMargins(ItemPrice_Layout_Left, ItemPrice_Layout_Top_No_Selected, 0, 0);
//        price.setLayoutParams(priceLp);


//        if (isSelected) {
//            title.setLines(2);
//            FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(ItemTitleWidth, ItemTitleHeight_Selected);
//            titleLp.setMargins(ItemTitle_Layout_Left, ItemTitle_Layout_Top_Selected, 0, 0);
//            title.setLayoutParams(titleLp);
//
//            FrameLayout.LayoutParams priceLp = new FrameLayout.LayoutParams(ItemPriceWidth, ItemPriceHeight);
//            priceLp.setMargins(ItemPrice_Layout_Left, ItemPrice_Layout_Top_Selected, 0, 0);
//            price.setLayoutParams(priceLp);
//
//
//        } else {
//            title.setLines(1);
//            FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(ItemTitleWidth, ItemTitleHeight_No);
//            titleLp.setMargins(ItemTitle_Layout_Left, ItemTitle_Layout_Top_No, 0, 0);
//            title.setLayoutParams(titleLp);
//
//            FrameLayout.LayoutParams priceLp = new FrameLayout.LayoutParams(ItemPriceWidth, ItemPriceHeight);
//            priceLp.setMargins(ItemPrice_Layout_Left, ItemPrice_Layout_Top_No_Selected, 0, 0);
//            price.setLayoutParams(priceLp);
//
//        }


    }

    @Override
    protected Rect getFocusBoundRect() {
        return mItemFocusBound;
    }
}
