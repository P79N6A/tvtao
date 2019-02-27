package com.yunos.tvtaobao.takeoutbundle.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.SkuProp;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.widget.newsku.presenter.ITakeOutSkuPresent;
import com.yunos.tvtaobao.biz.widget.newsku.view.ISkuView;
import com.yunos.tvtaobao.takeoutbundle.view.SkuEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhaoteng on 2018/10/15.
 */

public class TakeOutSkuPresenter implements ITakeOutSkuPresent {
    private static final String TAG = "TakeOutSkuPresenter";
    private final static int STOCK_ENOUGH = 10000; //可以认为是库存足够
    private final static String skuTAG = "xxx";//用以描述规格
    private ItemListBean mItemListBean;
    private ISkuView mISkuView;
    private SkuEngine mSkuEngine;
    private Context mContext;

    public TakeOutSkuPresenter(Context context, ISkuView skuView) {
        mISkuView = skuView;
        mContext = context;
    }

    @Override
    public void initSkuPropsList(ItemListBean itemListBean) {
        mItemListBean = itemListBean;
        mSkuEngine = new SkuEngine(mItemListBean.getItemId(), mItemListBean.getTitle(),
                mItemListBean.getSkuList(), mItemListBean.getMultiAttr() != null ? mItemListBean.getMultiAttr().getAttrList() : null);
        if (!TextUtils.isEmpty(itemListBean.getStoreId())) {
            mSkuEngine.setStoreId(mItemListBean.getStoreId());
        }
        List<TBDetailResultV6.SkuBaseBean.PropsBeanX> propsList = new ArrayList<>();
        if (mItemListBean != null) {
            List<ItemListBean.SkuListBean> skuList = mItemListBean.getSkuList();
            if (skuList != null) {
                TBDetailResultV6.SkuBaseBean.PropsBeanX propsBeanX = new TBDetailResultV6.SkuBaseBean.PropsBeanX();
                List<TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean> valueBeans = new ArrayList<>();
                for (int i = 0; i < skuList.size(); i++) {
                    ItemListBean.SkuListBean skuListBean = skuList.get(i);
                    String skuId = skuListBean.getSkuId();
                    String title = skuListBean.getTitle();
                    String quantity = skuListBean.getQuantity();
                    String price = skuListBean.getPrice();
                    TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean valuesBean = new TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean();
                    valuesBean.setName(title);
                    //skuid作为vid
                    valuesBean.setVid(String.valueOf(i));
                    valueBeans.add(valuesBean);
                }
                propsBeanX.setPid("-1");
                propsBeanX.setName("规格");
                propsBeanX.setValues(valueBeans);
                propsList.add(propsBeanX);
            }

            ItemListBean.MultiAttrBean multiAttr = mItemListBean.getMultiAttr();
            if (multiAttr != null) {
                List<ItemListBean.MultiAttrBean.AttrListBean> attrList = multiAttr.getAttrList();
                if (attrList != null) {
                    for (int i = 0; i < attrList.size(); i++) {
                        ItemListBean.MultiAttrBean.AttrListBean attrListBean = attrList.get(i);
                        String name = attrListBean.getName();
                        List<String> value = attrListBean.getValue();
                        TBDetailResultV6.SkuBaseBean.PropsBeanX propsBeanX = new TBDetailResultV6.SkuBaseBean.PropsBeanX();
                        List<TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean> valueBeans = new ArrayList<>();
                        for (int j = 0; j < value.size(); j++) {
                            TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean valueBean = new TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean();
                            valueBean.setName(value.get(j));
                            //vid代表某个参数中的第几个选项
                            valueBean.setVid(String.valueOf(j));
                            valueBeans.add(valueBean);
                        }
                        //pid代表第几个参数（除去规格）
                        propsBeanX.setPid(String.valueOf(i));
                        propsBeanX.setName(name);
                        propsBeanX.setValues(valueBeans);
                        propsList.add(propsBeanX);
                    }
                }
            }

            //初始化sku view
            mISkuView.initSkuView(propsList);
            //默认标题
            mISkuView.initTitle(mItemListBean.getTitle());
            //默认图片
            mISkuView.updateImage(mItemListBean.getItemPicts());

            //初始化价格
            SkuPriceNum skuPriceNum = new SkuPriceNum();
            SkuPriceNum.PriceBean priceBean = new SkuPriceNum.PriceBean();
            int price = mItemListBean.getPrice() != null && mItemListBean.getPrice().matches("^[0-9]*$") ? Integer.valueOf(mItemListBean.getPrice()) : -1;
            priceBean.setPriceText(String.valueOf(price / 100f));
            skuPriceNum.setPrice(priceBean);
            //TODO 多sku，数量不做限制
            skuPriceNum.setQuantity(STOCK_ENOUGH);
            //skuPriceNum.setQuantity(mItemListBean.getStock());
            //skuPriceNum.setLimit(mItemListBean.__limitQuantity);
            mISkuView.initSkuKuCunAndPrice(skuPriceNum);
        }

    }

    @Override
    public void onChooseSkuComplete(int chooseNum) {
        if (mSkuEngine != null) {
            if (mSkuEngine.isSkuDataAllSelected()) {
                SkuProp skuProp = mSkuEngine.getSkuProp();
                String itemId = skuProp.getItemId();
                String skuId = skuProp.getSkuId();
                String quantity = "" + chooseNum;
                String property = "";
                AppDebug.e(TAG, "skuProp.getPropList  : " + skuProp.getPropList().toString());
                if (skuProp != null && skuProp.getPropList() != null) {
                    for (int i = 0; i < skuProp.getPropList().size(); i++) {
                        SkuProp.Prop prop = skuProp.getPropList().get(i);
                        //接口请求参数中出去 规格，skuId可以代表所选的规格
                        if (!skuTAG.equals(prop.getName())) {
                            property += (prop.getName() + ":" + prop.getValue()) + ";";
                        }
                    }
                    if(!property.isEmpty()){
                        property = property.substring(0,property.length() - 1);
                    }
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("lifeShopId", skuProp.getShopId());
                    jsonObject.put("skuProperty", property);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String exParams = jsonObject.toString();
                AppDebug.e(TAG, "itemId = " + itemId + ",skuId = " + skuId + " ,quantity = " + quantity +
                        ", exParams = " + exParams);
                String cartFrom = "taolife_client";
                //sku选好了

                BusinessRequest.getBusinessRequest().requestTakeOutAddBag(itemId, skuId, quantity, exParams,
                        cartFrom, new GetTakeOutAddBagListener(new WeakReference<>((BaseActivity) mContext),
                                mISkuView));
            } else {
                if(mISkuView!=null){
                    mISkuView.onShowError(mContext.getString(com.yunos.tvtaobao.businessview.R.string.take_out_sku_choose_product_info));
                }
                AppDebug.e(TAG, "isSkuDataAllSelected = " + mSkuEngine.isSkuDataAllSelected());
            }
        } else {
            AppDebug.e(TAG, "mSkuEngine == null ");
        }
    }

    /**
     * 加入购物车回调
     */

    public static class GetTakeOutAddBagListener extends BizRequestListener<AddBagBo> {
        private Activity mActivity;
        private ISkuView mSkuView;

        public GetTakeOutAddBagListener(WeakReference<BaseActivity> baseActivityRef, ISkuView skuView) {
            super(baseActivityRef);
            this.mSkuView = skuView;
            this.mActivity = baseActivityRef.get();

        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "GetTakeOutAddBagListener.onError resultCode = " + resultCode + ",msg = " + msg);
            if (mSkuView != null && mActivity != null) {
                mSkuView.onShowError(mActivity.getString(com.yunos.tvtaobao.businessview.R.string.take_out_sku_add_bag_error));
                mActivity.finish();
            }
            return true;
        }

        @Override
        public void onSuccess(AddBagBo data) {
            if (mSkuView != null && mActivity != null) {
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    public void addSelectedPropData(long propId, long valueId) {
        String hasSku = mItemListBean.getHasSku();
        if ("true".equals(hasSku) && propId == -1) {
            List<ItemListBean.SkuListBean> skuList = mItemListBean.getSkuList();
            if (skuList != null) {
                ItemListBean.SkuListBean skuListBean = skuList.get((int) valueId);
                mSkuEngine.addSelectSkuData(skuListBean.getSkuId(), skuTAG, skuListBean.getTitle());
                //更新价格
                SkuPriceNum skuPriceNum = new SkuPriceNum();
                SkuPriceNum.PriceBean priceBean = new SkuPriceNum.PriceBean();
                String priceStr = skuList.get((int) valueId).getPrice();
                int price = priceStr != null && priceStr.matches("^[0-9]*$") ? Integer.valueOf(priceStr) : -1;
                priceBean.setPriceText(String.valueOf(price / 100f));
                skuPriceNum.setPrice(priceBean);
                //TODO 多sku，数量不做限制
                skuPriceNum.setQuantity(STOCK_ENOUGH);
                //skuPriceNum.setQuantity(mItemListBean.getStock());
                if(Long.valueOf(skuListBean.getSkuId())== mItemListBean.__limitSkuId){
                    skuPriceNum.setLimit(mItemListBean.__limitQuantity);
                }else {
                    skuPriceNum.setLimit(0);
                }
                mISkuView.updateSkuKuCunAndPrice(skuPriceNum);
            }
        } else {
            ItemListBean.MultiAttrBean multiAttr = mItemListBean.getMultiAttr();
            mItemListBean.getMultiAttr();
            if (multiAttr != null) {
                List<ItemListBean.MultiAttrBean.AttrListBean> attrList = multiAttr.getAttrList();
                if (attrList != null) {
                    ItemListBean.MultiAttrBean.AttrListBean attrListBean = attrList.get((int) propId);
                    mSkuEngine.addSelectSkuData(null, attrListBean.getName(), attrListBean.getValue().get((int) valueId));
                }
            }
        }
    }

    public void deleteSelectedPropData(long propId, long valueId) {
        String hasSku = mItemListBean.getHasSku();
        if ("true".equals(hasSku) && propId == -1) {
            List<ItemListBean.SkuListBean> skuList = mItemListBean.getSkuList();
            if (skuList != null) {
                ItemListBean.SkuListBean skuListBean = skuList.get((int) valueId);
                mSkuEngine.removeSelectedView(skuTAG, skuListBean.getTitle());
                //更新价格
                SkuPriceNum skuPriceNum = new SkuPriceNum();
                SkuPriceNum.PriceBean priceBean = new SkuPriceNum.PriceBean();
                int price = mItemListBean.getPrice() != null && mItemListBean.getPrice().matches("^[0-9]*$") ? Integer.valueOf(mItemListBean.getPrice()) : -1;
                priceBean.setPriceText(String.valueOf(price / 100f));
                skuPriceNum.setPrice(priceBean);
                //TODO 多sku，数量不做限制
                skuPriceNum.setQuantity(STOCK_ENOUGH);
                //skuPriceNum.setQuantity(mItemListBean.getStock());
                if(Long.valueOf(skuListBean.getSkuId()) == mItemListBean.__limitSkuId){
                    skuPriceNum.setLimit(mItemListBean.__limitQuantity);
                }else {
                    skuPriceNum.setLimit(0);
                }
                mISkuView.updateSkuKuCunAndPrice(skuPriceNum);
            }
        } else {
            ItemListBean.MultiAttrBean multiAttr = mItemListBean.getMultiAttr();
            mItemListBean.getMultiAttr();
            if (multiAttr != null) {
                List<ItemListBean.MultiAttrBean.AttrListBean> attrList = multiAttr.getAttrList();
                if (attrList != null) {
                    ItemListBean.MultiAttrBean.AttrListBean attrListBean = attrList.get((int) propId);
                    mSkuEngine.removeSelectedView(attrListBean.getName(), attrListBean.getValue().get((int) valueId));
                }
            }
        }
    }


    @Override
    public void onStart() {

    }


    @Override
    public void onDestroy() {

    }
}
