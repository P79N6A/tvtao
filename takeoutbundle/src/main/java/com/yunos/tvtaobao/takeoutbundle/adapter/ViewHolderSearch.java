package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopSearchActivity;
import com.yunos.tvtaobao.takeoutbundle.listener.OnDialogReturnListener;
import com.yunos.tvtaobao.takeoutbundle.view.PromptPop;
import com.yunos.tvtaobao.takeoutbundle.view.SkuSelectDialog;
import com.yunos.tvtaobao.takeoutbundle.widget.InnerFocusDispatchFrameLayout;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by haoxiang on 2017/12/12. 商品 item
 */

public class ViewHolderSearch extends PowViewHolder<ItemListBean> {
    private final static DecimalFormat format = new DecimalFormat("¥#.##");


    private InnerFocusDispatchFrameLayout innerFocusDispatchFrameLayout;
    private ImageView good_add;
    private TextView good_count;
    private ImageView good_reduce;

    private TextView good_name;   // 商品名字
    private TextView good_make;   // 商品描述
    private ImageView good_pic;
    private View good_focus_status;
    private ImageView good_shop_mark;
    private TextView good_sell_count;
    private TextView good_real_price;
    private TextView good_price;
    private View good_pic_toast;
    private TextView sale_tip[] = new TextView[6];

    private boolean isFocus = false;
    private ImageView currentFocus;
    private AdapterDelegate<? super ItemListBean> mMultipleAdapter;
    private int mPosition = 0;
    private InnerFocusDispatchFrameLayout.OnFocusDropListener mOnFocusDropListener = new InnerFocusDispatchFrameLayout.OnFocusDropListener() {


        @Override
        public void onFocusLeave(View... unFocusView) {
            if (isFocus) {
                isFocus = false;
                good_focus_status.setVisibility(View.INVISIBLE);
                mItemView.clearAnimation();
                currentFocus = null;
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);
                }
            }
        }

        @Override
        public void onFocusEnter(View focusView, View... unFocusView) {
            if (!isFocus) {
                isFocus = true;
                good_focus_status.setVisibility(View.VISIBLE);
                mItemView.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.toke_out_good_focus_in));
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);

                    // todo 埋点
                    Map<String, String> properties = Utils.getProperties();
                    properties.put("shop id", mData.getShopId());
                    BaseActivity baseActivity = (BaseActivity) mActivity;
                    Utils.utControlHit(baseActivity.getFullPageName(), "Focus_waimai_shop_floor_name_p" + mPosition, properties);
                    AppDebug.e("ViewHolder", "focus");
                }
            }

            if (currentFocus != focusView) {
                currentFocus = (ImageView) focusView;
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);
                }
            }
        }

        @Override
        public void onFocusClick(View focus, View... unFocusView) {
            isFocus = true;
            currentFocus = (ImageView) focus;
            performClick(focus);
            if (mMultipleAdapter != null) {
                loadData(mMultipleAdapter, mData, -1);
            }
        }
    };

    public ViewHolderSearch(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);

        innerFocusDispatchFrameLayout = (InnerFocusDispatchFrameLayout) mItemView;
        good_add = findViewById(R.id.good_add);
        good_count = findViewById(R.id.good_count);
        good_reduce = findViewById(R.id.good_reduce);
        good_focus_status = findViewById(R.id.good_focus_status);
        good_shop_mark = findViewById(R.id.good_shop_mark);
        good_sell_count = findViewById(R.id.good_sell_count);
        sale_tip[0] = findViewById(R.id.sale_tip_0);
        sale_tip[1] = findViewById(R.id.sale_tip_0_more);
        sale_tip[2] = findViewById(R.id.sale_tip_1);
        sale_tip[3] = findViewById(R.id.sale_tip_1_more);
        sale_tip[4] = findViewById(R.id.sale_tip_2);
        sale_tip[5] = findViewById(R.id.sale_tip_2_more);

        good_real_price = findViewById(R.id.good_real_price);
        good_price = findViewById(R.id.good_price);
        good_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        good_name = findViewById(R.id.good_name);
        good_make = findViewById(R.id.good_make);
        good_pic = findViewById(R.id.good_pic);
        good_pic_toast = findViewById(R.id.good_pic_toast);

        innerFocusDispatchFrameLayout.setOnFocusDropListener(mOnFocusDropListener);


    }

    /**
     * @param view 点击view
     */
    private void performClick(View view) {
        if (view == null) return;

        // todo 埋点
        Map<String, String> map = Utils.getProperties();
        map.put("item id", mData.getItemId());
        BaseActivity base = (BaseActivity) mActivity;
        Utils.utControlHit(base.getFullPageName(), "Page_waimai_shop_pit_name_p" + mPosition, map);
        AppDebug.e("ViewHolder", "focus");

        if (view.getId() == R.id.good_add) {
            switch (mData.getGoodStatus()) {
                case edit:
                case normal:
                    addItemToCollection();
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.getItemId());
            BaseActivity baseActivity = (BaseActivity) mActivity;
            Utils.utControlHit(baseActivity.getFullPageName(), "Page_waimai_shop_button_increase", properties);
            AppDebug.e("ViewHolder", "focus");

        } else if (view.getId() == R.id.good_reduce) {
            switch (mData.getGoodStatus()) {
                case edit:
                    removeItemFromCollection();
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.getItemId());
            BaseActivity baseActivity = (BaseActivity) mActivity;
            Utils.utControlHit(baseActivity.getFullPageName(), "Page_waimai_shop_button_decrease", properties);
            AppDebug.e("ViewHolder", "focus");
        }
    }

    /**
     * 加入购物车(一条一条加入)
     */
    private void addItemToCollection() {
        if ((mData.getMultiAttr() != null && mData.getMultiAttr().getAttrList() != null && mData.getMultiAttr().getAttrList().size() > 0)
                || (mData.getSkuList() != null && mData.getSkuList().size() > 1)) {// 含有多选参数
            try {
                SkuSelectDialog.getSkuDialogInstance(mActivity, mActivity.getPackageName()).setSkuData(BusinessRequest.getBusinessRequest(), new OnDialogReturnListener() {
                            @Override
                            public void addBagSuccess() {
                                AppDebug.e("TAG", "addItemToCollection .addBagSuccess");
                                ((TakeOutShopSearchActivity) mActivity).notifyCollectionChange(true);
                            }

                            @Override
                            public void addBagFailure(int resultCode, String msg) {
                                AppDebug.e("TAG", "addItemToCollection .addBagFailure");
                                PromptPop.getInstance(mActivity).showPromptWindow(mActivity.getResources().getString(R.string.sku_addbag_error));
                            }
                        },
                        mData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mActivity;
            activity.addGoodToBag(mData);
        }
    }

    /**
     * 移除购物车(一条一条移除)
     *
     * @return
     */
    private void removeItemFromCollection() {
        if (mData.__intentCount > 0) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mActivity;
            activity.updateBag(mData);
        }
    }


    @Override
    protected int getItemViewRes() {
        if (Build.VERSION.SDK_INT >= 21) {
            return R.layout.item_takeout_good_search_api_high;
        } else {
            return R.layout.item_takeout_good_search_api_low;
        }
    }


    @Override
    public void loadData(AdapterDelegate<? super ItemListBean> multipleAdapter, ItemListBean data, int position) {
        mMultipleAdapter = multipleAdapter;
        mPosition = position >= 0 ? position : mPosition;

        good_shop_mark.setImageLevel(0);
        good_add.setImageLevel(1);
        good_reduce.setImageLevel(1);
        good_pic_toast.setVisibility(View.INVISIBLE);

        // 商品名字
        good_name.setText(mData.getTitle());
        // 商品描述
        good_make.setText(mData.getDescription());
        // 商品图片
        if (!TextUtils.isEmpty(mData.getItemPicts())) {
            ImageLoaderManager.getImageLoaderManager(mActivity).displayImage(mData.getItemPicts(), good_pic, ClassicOptions.dio565);
        } else {
            good_pic.setImageResource(R.drawable.good_icon_no_pic);
        }
        // 购物车数量
        good_count.setText(String.valueOf(mData.__intentCount));

        // 新品特性 新品 招牌
        for (int i = 0; mData.getItemAttrList() != null && i < mData.getItemAttrList().size(); i++) {
            ItemListBean.ItemAttrListBean itemAttrListBean = mData.getItemAttrList().get(i);
            if (itemAttrListBean != null && "1".equals(itemAttrListBean.getAttrType())) {
                good_shop_mark.setImageLevel(1);
                break;
            }
            if (itemAttrListBean != null && "2".equals(itemAttrListBean.getAttrType())) {
                good_shop_mark.setImageLevel(2);
                break;
            }
            if (itemAttrListBean != null && "3".equals(itemAttrListBean.getAttrType())) {
                good_shop_mark.setImageLevel(3);
                break;
            }
        }

        // 价格
        int promotionPrice = mData.getPromotionPrice() != null && mData.getPromotionPrice().matches("^[0-9]*$") ? Integer.valueOf(mData.getPromotionPrice()) : -1;
        int price = mData.getPrice() != null && mData.getPrice().matches("^[0-9]*$") ? Integer.valueOf(mData.getPrice()) : -1;
        if (promotionPrice < 0) {
            good_real_price.setText(format.format(promotionPrice / 100f));
            good_price.setText(format.format(promotionPrice / 100f));
        } else {
            good_real_price.setText(format.format(promotionPrice / 100f));
            good_price.setText(format.format(price / 100f));
        }
        if (promotionPrice < price) {
            good_price.setVisibility(View.VISIBLE);
        } else {
            good_price.setVisibility(View.GONE);
        }

        // 月销
        good_sell_count.setText(String.format("月销%s笔", TextUtils.isEmpty(mData.getSaleCount()) ? "0" : mData.getSaleCount()));

        // 优惠提示 tip
        int index = 0;
        for (int i = 0; mData.tagList != null && i < mData.tagList.size() && index < sale_tip.length; i++) {
            ItemListBean.Tag tag = mData.tagList.get(i);
            boolean hasContent = false;
            if (tag != null && !TextUtils.isEmpty(tag.text)) {
                sale_tip[index].setText(tag.text);
                sale_tip[index].setVisibility(View.VISIBLE);
                hasContent = true;
            } else {
                sale_tip[index].setVisibility(View.GONE);
            }
            if (tag != null && !TextUtils.isEmpty(tag.subText)) {
                sale_tip[index + 1].setText(tag.subText);
                sale_tip[index + 1].setVisibility(View.VISIBLE);
                hasContent = true;
            } else {
                sale_tip[index + 1].setVisibility(View.GONE);
            }
            if (hasContent) {
                index += 2;
            }
        }
        for (; index < sale_tip.length; index++) {
            sale_tip[index].setVisibility(View.GONE);
        }

        switch (data.getGoodStatus()) {
            case normal:
                good_count.setVisibility(View.GONE);
                good_reduce.setVisibility(View.GONE);
                good_add.setVisibility(View.VISIBLE);
                if (currentFocus != null) {
                    currentFocus.setImageLevel(2);
                }
                break;
            case edit:
                good_count.setVisibility(View.VISIBLE);
                good_reduce.setVisibility(View.VISIBLE);
                good_add.setVisibility(View.VISIBLE);
                if (currentFocus != null) {
                    currentFocus.setImageLevel(2);
                }
                break;
            case outStock:
                good_count.setVisibility(View.GONE);
                good_reduce.setVisibility(View.GONE);
                good_add.setVisibility(View.GONE);
                good_pic_toast.setVisibility(View.VISIBLE);
                good_add.setImageLevel(0);
                break;
            case rest:
                good_count.setVisibility(View.GONE);
                good_reduce.setVisibility(View.GONE);
                good_add.setVisibility(View.VISIBLE);
                good_add.setImageLevel(0);
                break;
        }

        innerFocusDispatchFrameLayout.notifyFocusChangeIfNeed();


        // todo  限购数量 数据不全 无法判断 过
//                if ("1".equals(tag.type)) {      // 提取限够数量
//                    int l = tag.text.indexOf("限") + 1;
//                    int r = tag.text.indexOf("份");
//                    if (l >= 0 && r >= 0 && l < r) {
//                        String middle = tag.text.substring(l, r);
//                        if (middle.matches("^[0-9]*$")) {
//                            int limitSaleCount = Integer.valueOf(middle);
//                        }
//                    }
//                }


    }


}












































