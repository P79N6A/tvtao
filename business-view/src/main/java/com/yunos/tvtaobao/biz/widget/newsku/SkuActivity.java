package com.yunos.tvtaobao.biz.widget.newsku;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvlife.imageloader.utils.ClassicOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.base.IPresenter;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.biz.widget.CustomDialog;
import com.yunos.tvtaobao.biz.widget.newsku.interfaces.SkuInfoUpdate;
import com.yunos.tvtaobao.biz.widget.newsku.widget.SkuItem;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.biz.widget.newsku.view.ISkuView;
import com.yunos.tvtaobao.biz.widget.newsku.view.WaitProgressDialog;
import com.yunos.tvtaobao.biz.widget.newsku.widget.NumChooseLayout;
import com.yunos.tvtaobao.biz.widget.newsku.widget.SkuItemLayout;

import java.util.List;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/4
 *     desc : sku存在多种propKey，可能是由机型，颜色，大小等等决定的
 *            propKey组合是propId:valueId形成的
 *            propId可能是颜色的ID
 *            valueId可能是颜色下，红，黄，蓝等等的ID
 *     version : 1.0
 * </pre>
 */

public abstract class SkuActivity<T extends IPresenter> extends BaseMVPActivity implements ISkuView {
    private final static String TAG = "SkuActivity";

    private final int PROMPT_NOT_RESOURCE = -1;
    protected ImageView goodsImage;
    protected TextView goodsTitle, goodsPrice;
    protected LinearLayout skuLayout;
    protected NumChooseLayout numChooseLayout;
    private FrameLayout focusBackgroundLayout;
    protected Button okBtn;

    protected String itemId;
    protected String tradeType = TradeType.ADD_CART;
    protected int buyCount = 1;


    private CustomDialog customDialog;

    private boolean isFirstDoSku = false;

    protected T mSkuPresenter;

    @Override
    public void initView() {
        super.initView();
        showProgressDialog(true);
        goodsImage = (ImageView) findViewById(R.id.sku_goods_image);
        goodsTitle = (TextView) findViewById(R.id.sku_goods_title);
        goodsPrice = (TextView) findViewById(R.id.sku_goods_price);
        skuLayout = (LinearLayout) findViewById(R.id.activity_sku_info_layout);
        focusBackgroundLayout = (FrameLayout) findViewById(R.id.layout_focus_background);
        okBtn = (Button) findViewById(R.id.activity_sku_info_ok);
        numChooseLayout = new NumChooseLayout(SkuActivity.this);

        tradeType = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SKU_TYPE);
        itemId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ITEMID);
        buyCount = getIntent().getIntExtra(BaseConfig.INTENT_KEY_BUY_COUNT, 1);

        mSkuPresenter = (T)mPresenter;

        numChooseLayout.setTradeType(tradeType);

        okBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusBackgroundLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_sku_ok_focus_layout));
                } else {
                    focusBackgroundLayout.setBackgroundDrawable(null);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        isFirstDoSku = true;
    }

    @Override
    protected abstract T createPresenter();

    protected abstract void initPresenter();

    @Override
    public void initTitle(String title) {
        goodsTitle.setText(title);
    }

    @Override
    public void initSkuView(List<TBDetailResultV6.SkuBaseBean.PropsBeanX> propsList) {
        //判断是否存在sku组合
        if (propsList != null) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, (int) getResources().getDimension(R.dimen.dp_34), 0, 0);
            int propsSize = propsList.size();
            for (int i = 0; i < propsSize; i++) {
                SkuItemLayout skuItemLayout = new SkuItemLayout(SkuActivity.this);
                skuItemLayout.setSkuUpdateListener(skuInfoUpdate);
                skuItemLayout.setProps(propsList.get(i));
                //这步操作主要是为了让之后的布局，距离上面的有一定距离
                if (i > 0) {
                    skuItemLayout.setLayoutParams(lp);
                } else {
                    SkuItem skuItem = skuItemLayout.getSkuItem(0);
                    if(skuItem != null){
                        skuItem.requestFocus();
                    }
                }
                skuLayout.addView(skuItemLayout);
            }

            numChooseLayout.setLayoutParams(lp);
        } else {
            //如果没有SKU组合，则让选择框获取焦点
            numChooseLayout.getNumChooseItem().requestFocus();
        }

        skuLayout.addView(numChooseLayout);
        showProgressDialog(false);
    }

    @Override
    public void initSkuKuCunAndPrice(SkuPriceNum skuPriceNum) {
        if(skuPriceNum == null){
            return;
        }
        if(goodsPrice != null){
            SkuPriceNum.PriceBean price = skuPriceNum.getPrice();
            if(price!=null){
                String priceText = price.getPriceText();
                goodsPrice.setText(getResources().getString(R.string.new_shop_cart_sku_presale) + priceText);
            }
        }
        numChooseLayout.setBuyCount(buyCount);
        AppDebug.d(TAG, TAG + ".updateSkuKuCunAndPrice limit : " + skuPriceNum.getLimit());
        //判断此组合是否存在限购
        if (skuPriceNum.getLimit() == 0) {
            numChooseLayout.setKuCunNum(skuPriceNum.getQuantity(), 0);
        } else {
            numChooseLayout.setKuCunNum(skuPriceNum.getQuantity(), skuPriceNum.getLimit());
        }
    }

    @Override
    public void showUnitBuy(int times) {
        AppDebug.d(TAG, TAG + ".showUnitBuy times : " + times);
        numChooseLayout.showUnitBuy(times);
    }

    @Override
    public void updateSkuKuCunAndPrice(SkuPriceNum skuPriceNum) {
        if(skuPriceNum==null){
            return;
        }
        if(goodsPrice != null){
            SkuPriceNum.PriceBean price = skuPriceNum.getPrice();
            if(price != null){
                String priceText = price.getPriceText();
                goodsPrice.setText(getResources().getString(R.string.new_shop_cart_sku_presale) + priceText);
            }
        }
        AppDebug.d(TAG, TAG + ".updateSkuKuCunAndPrice limit : " + skuPriceNum.getLimit());
        if (isFirstDoSku) {
            numChooseLayout.setBuyCount(buyCount);
            isFirstDoSku = false;
        } else {
            numChooseLayout.setBuyCount(1);
        }
        //判断此组合是否存在限购
        if (skuPriceNum.getLimit() == 0) {
            numChooseLayout.setKuCunNum(skuPriceNum.getQuantity(), 0);
        } else {
            numChooseLayout.setKuCunNum(skuPriceNum.getQuantity(), skuPriceNum.getLimit());
        }
    }

    @Override
    public void updateImage(String url) {
        AppDebug.i(TAG, TAG + ".updateImage url : " + url);
        if (!url.startsWith("http:")) {
            url = "http:" + url;
        }
        ImageLoaderManager.getImageLoaderManager(SkuActivity.this).displayImage(url, goodsImage, ClassicOptions.dio565);
    }

    @Override
    public void updateValueViewStatus(Long propId, Long valueId, SkuItemLayout.VALUE_VIEW_STATUS status) {
        SkuItemLayout skuItemLayout = getSkuItemLayout(propId);
        if (skuItemLayout != null) {
            skuItemLayout.updateValueViewStatus(propId, valueId, status);
        }
    }

    @Override
    public void onShowError(String prompt) {
        onPromptDialog(PROMPT_NOT_RESOURCE, prompt);
    }

    @Override
    public void onPromptDialog(int resource, String prompt) {
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(SkuActivity.this)
                .setType(1)
                .setResultMessage(prompt);
        if (resource != PROMPT_NOT_RESOURCE) {
            mBuilder.setHasIcon(true).setIcon(resource);
        }
        customDialog = mBuilder.create();
        customDialog.show();
    }

    @Override
    public void onDialogDismiss() {
        if (customDialog != null) {
            if (customDialog.isShowing()) {
                customDialog.dismiss();
            }
            customDialog = null;
        }
    }



    /**
     * 通过propId获取
     *
     * @param propId
     * @return
     */
    private SkuItemLayout getSkuItemLayout(long propId) {
        int count = skuLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            SkuItemLayout prop = (SkuItemLayout) skuLayout.getChildAt(i);
            if (prop != null && prop.getPropId() == propId) {
                return prop;
            }
        }
        return null;
    }

    private WaitProgressDialog mWaitProgressDialog;
    @Override
    public void showProgressDialog(boolean show) {
        if (isFinishing()) {
            // 如果当前 mActivity已经finish，那么直接返回
            return;
        }

        if (mWaitProgressDialog == null) {
            mWaitProgressDialog = new WaitProgressDialog(SkuActivity.this);
        }

        if (show && mWaitProgressDialog.isShowing()) {
            return;
        }

        if (!show && !mWaitProgressDialog.isShowing()) {
            return;
        }

        if (show) {
            mWaitProgressDialog.show();
        } else {
            mWaitProgressDialog.dismiss();
        }
    }


    private SkuInfoUpdate skuInfoUpdate = new SkuInfoUpdate() {
        @Override
        public void addSelectedPropData(long propId, long valueId) {
            AppDebug.i(TAG, TAG + ".SkuInfoUpdate propId : " + propId + " ,valueId : " + valueId);
            SkuActivity.this.addSelectedPropData(propId,valueId);
        }

        @Override
        public void deleteSelectedPropData(long propId, long valueId) {
            SkuActivity.this.deleteSelectedPropData(propId,valueId);
        }
    };

    protected abstract void addSelectedPropData(long propId, long valueId);

    protected abstract void deleteSelectedPropData(long propId, long valueId);
}
