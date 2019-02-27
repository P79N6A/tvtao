/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.buildorder
 * FILE NAME: ViewBuilder.java
 * CREATED TIME: 2015-3-9
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.newcart.entity;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.view.TextureView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.ComponentTag;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ActionType;
import com.taobao.wireless.trade.mcart.sdk.co.biz.AllItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BannerComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BundleComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.CartStructure;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FooterComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionBarComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ShopComponent;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngineContext;
import com.taobao.wireless.trade.mcart.sdk.engine.SplitJoinRule;
import com.taobao.wireless.trade.mcart.sdk.utils.CartResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.newcart.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

/**
 * 创建购物车相关数据
 */
public class CartBuilder {

    private String TAG = "CartBuilder";
    private String shopId;
    private ShopComponent mShopComponent;
    private boolean isPreSell;
    private String groupId;
    private GroupComponent mGroupComponent;

    // 提交结算的结果类型
    public enum CartSubmitResultType {
        SUCCESS, ERROR_SELECTED_NOTHING, ERROR_SELECTED_TOO_MUCH, ERROR_OTHER, ERROR_H5_ORDER
    }

    private BusinessRequest mTvTaobaoBusinessRequest;
    private Map<String, List<CartGoodsComponent>> mComponents = new LinkedHashMap<String, List<CartGoodsComponent>>();

    private FooterComponent mCartFooterComponent;
    private Context mContext;

    private String cartFrom;

    public CartBuilder(Context context, String cartFrom) {
        mContext = context;
        this.cartFrom = cartFrom;
        mTvTaobaoBusinessRequest = BusinessRequest.getBusinessRequest();
        registerCartGoodsComponent();
//        registerCartShopComponent();
    }

    //获取商品列表数据
    public Map<String, List<CartGoodsComponent>> getCartData() {
        return mComponents;
    }

    //获取底部结算数据
    public FooterComponent getCartFooterComponent() {
        CartStructure cartStructure = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData();
        if (cartStructure != null) {
            List<Component> list = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData().getFooter();
            for (Component component : list) {
                if (component instanceof FooterComponent) {
                    mCartFooterComponent = (FooterComponent) component;
                    AppDebug.i(TAG, TAG + ".getCartFooterComponent.component = " + component);
                    break;
                }
            }
        }

        return mCartFooterComponent;
    }

    //获取头部购物车商品总数信息
    public int getCartGoodsCount() {
        int count = 0;
        CartStructure cartStructure = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData();
        if (cartStructure != null) {
            List<Component> list = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData().getHeader();
            for (Component component : list) {
                if (component instanceof AllItemComponent) {
                    AllItemComponent allItemComponent = (AllItemComponent) component;
                    count = allItemComponent.getValue();
                    AppDebug.i(TAG, TAG + ".getCartGoodsCount.allItemComponent = " + allItemComponent + ".count = "
                            + count);
                    break;
                }
            }
        }

        return count;
    }

    //获取失效商品总数信息
    public int getInvalidCartGoodsCount() {
        int count = 0;
        if (mComponents != null) {
            Set set = mComponents.entrySet();

            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {

                Map.Entry mapEntry = (Map.Entry) iterator.next();

                if (!TextUtils.isEmpty(mapEntry.getKey() + "")) {
                    if (mapEntry.getKey().equals("invalid")) {
                        List<CartGoodsComponent> goodsList = (List<CartGoodsComponent>) mapEntry.getValue();
                        if (goodsList != null) {
                            count = goodsList.size();
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * 判断后续是否还有新的数据
     *
     * @return boolean
     */
    public boolean haveNextData() {
        CartEngineContext context = CartEngine.getInstance(getCartFrom(cartFrom)).getContext();
        if (context != null && context.getPageMeta() != null) {
            if (context.getPageMeta().getBoolean("isEndPage") || !context.getPageMeta().getBoolean("isNext")) {
                return false;
            }
        }
        return true;
    }


    /**
     * 提交选中商品,去结算
     */
    public CartSubmitResult submitCart() {
        //检查选中的商品数量
        List<ItemComponent> list = CartEngine.getInstance(getCartFrom(cartFrom)).getAllCheckedValidItemComponents();
        int maxCheck = CartEngine.getInstance(getCartFrom(cartFrom)).getCheckMax();

        if (list == null || (list != null && list.size() == 0)) {
            return new CartSubmitResult(CartSubmitResultType.ERROR_SELECTED_NOTHING,
                    mContext.getString(R.string.newcart_submit_error_no_shop), null);
            //Toast.makeText(mShopCartListActivity, "请选择商品后再结算", Toast.LENGTH_SHORT).show();
        }
        if (list.size() > maxCheck) {
            String text = String.format(mContext.getString(R.string.newcart_submit_error_goods_too_much),
                    maxCheck);
            return new CartSubmitResult(CartSubmitResultType.ERROR_SELECTED_TOO_MUCH, text, null);
            //Toast.makeText(mShopCartListActivity, "您选择商品过多,请先取消几个商品再结算", Toast.LENGTH_SHORT).show();
        }

        //检查商品互斥
        CartResult cartResult = CartEngine.getInstance(getCartFrom(cartFrom)).checkSubmitItems();
        if (!cartResult.isSuccess()) {
            return new CartSubmitResult(CartSubmitResultType.ERROR_OTHER, cartResult.getErrorMessage(), null);
            //Toast.makeText(mShopCartListActivity, cartResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }

        String settlements = "";
        for (ItemComponent temp : list) {
            settlements += temp.getSettlement() + ",";
            if (temp.isPreSell()) {
                isPreSell = true;
            }
        }
        settlements = settlements.substring(0, settlements.length() - 1);
        // H5下单检查
        CartResult cartResultForH5 = CartEngine.getInstance(getCartFrom(cartFrom)).orderByH5Check();
        if (cartResultForH5 != null && cartResultForH5.isSuccess()) {
            //H5下单,提示不支持
            return new CartSubmitResult(CartSubmitResultType.ERROR_H5_ORDER,
                    mContext.getString(R.string.newcart_submit_error_tv_not_support), null);
        } else {
            String cartIds = CartEngine.getInstance(getCartFrom(cartFrom)).buyCartIds();
            boolean isSettlementAlone = CartEngine.getInstance(getCartFrom(cartFrom)).isSettlementAlone();
            BuildOrderRequestBo mBuildOrderRequestBo = new BuildOrderRequestBo();
            mBuildOrderRequestBo.setBuyNow(false);
            mBuildOrderRequestBo.setCartIds(cartIds);
            mBuildOrderRequestBo.setSettlementAlone(isSettlementAlone);
            mBuildOrderRequestBo.setBuyParam(settlements);
            mBuildOrderRequestBo.setPreSell(isPreSell);
            return new CartSubmitResult(CartSubmitResultType.SUCCESS, null, mBuildOrderRequestBo);
        }
    }

    /**
     * 创建布局
     *
     * @return
     */
    public boolean buildView() {
        buildListData(null);
        return true;
    }

    /**
     * 更新数据,重新生成布局
     */
    public void reBuildView(List<String> deleteList) {
        buildListData(deleteList);
    }

    /**
     * 重新生成列表需要的数据结构
     */
    private void buildListData(List<String> deleteList) {

        CartStructure cartStructure = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData();
        if (cartStructure != null) {
            List<Component> list = CartEngine.getInstance(getCartFrom(cartFrom)).getCartStructureData().getBody();
            boolean isEndPage = false;
            isEndPage = CartEngine.getInstance(getCartFrom(cartFrom)).isEndPage();
            // 只有最后一页才展示推荐宝贝和失效宝贝
            getBodyDatas(list, isEndPage, deleteList);

        }
    }

    public void getBodyDatas(List<Component> list, boolean isEndPage, List<String> deleteList) {
        mComponents.clear();
        if (list == null) {
            return;
        }

        List<CartGoodsComponent> invalidlist = new ArrayList<CartGoodsComponent>();
        for (Component component : list) {
            if (component == null) {
                continue;
            }
            if (component instanceof CartGoodsComponent) {
                CartGoodsComponent cg = (CartGoodsComponent) component;
                if (cg.getShopComponent() != null) {
                    shopId = cg.getShopComponent().getSellerId() + "";
                    mShopComponent = cg.getShopComponent();
                }
                if (cg.getGroupComponent() != null) {
                    groupId = cg.getGroupComponent().getGroupId();
                    mGroupComponent = cg.getGroupComponent();
                }
                if (cg.getItemComponent() != null && shopId != null && mShopComponent != null && (cg.getItemComponent().getSellerId() + "").equals(shopId)) {
                    cg.setShopComponent(mShopComponent);
                }
//                if (cg.getItemComponent() != null && groupId != null && mGroupComponent != null && groupId.contains(cg.getItemComponent().getSellerId() + "")
//                        && mGroupComponent.getGroupPromotion() != null) {
//                    cg.setGroupComponent(mGroupComponent);
//                }
                boolean skuInvalid = false;
                if (cg.getItemComponent() != null && cg.getItemComponent().getSku() != null) {
                    skuInvalid = cg.getItemComponent().getSku().isSkuInvalid();
                }
                AppDebug.i("CartGoodsComponent", "CartGoodsComponent:" + cg);
                if (null != cg.getItemComponent() && (cg.getItemComponent().isValid() || cg.getItemComponent().isPreBuyItem()
                        || cg.getItemComponent().isPreSell() || (!cg.getItemComponent().isValid() && skuInvalid))) {
                    List<CartGoodsComponent> tmplist = null;
                    if (mComponents.containsKey(cg.getItemComponent().getShopId())) {
                        tmplist = mComponents.get(cg.getItemComponent().getShopId());
                        tmplist.add(cg);
                    } else {
                        tmplist = new ArrayList<CartGoodsComponent>();
                        tmplist.add(cg);
                        mComponents.put(cg.getItemComponent().getShopId(), tmplist);
                    }
                }

                AppDebug.i("deleteList", deleteList + "");
                if (null != cg.getItemComponent() && !cg.getItemComponent().isValid() &&
                        !cg.getItemComponent().isPreBuyItem() && !cg.getItemComponent().isPreSell() && !skuInvalid) {
                    invalidlist.add(cg);

                }
            }
        }

        if (invalidlist.size() == 0) {
            return;
        }

        if (deleteList != null) {
            for (int i = 0; i < deleteList.size(); i++) {
                for (int j = 0; j < invalidlist.size(); j++) {
                    if (deleteList.get(i) != null && invalidlist.get(j) != null && invalidlist.get(j).getItemComponent() != null &&
                            invalidlist.get(j).getItemComponent().getItemId() != null && deleteList.get(i).equals(invalidlist.get(j).getItemComponent().getItemId())) {
                        invalidlist.remove(invalidlist.get(j));
                    }
                }
            }
        }
        //最后一页时显示无效商品
        if (isEndPage) {
            mComponents.put("invalid", invalidlist);
        }
    }

    public JSONObject generatePageData() {

        JSONObject submitData = null;
        CartEngineContext context = CartEngine.getInstance(getCartFrom(cartFrom)).getContext();
        if (context != null && context.getPageMeta() != null) {
            submitData = new JSONObject();
            // 设置具体的操作
            JSONObject operate = new JSONObject();
            JSONArray actionKeylist = new JSONArray();
            operate.put("query", actionKeylist);
            submitData.put("operate", operate);

            JSONObject structure = new JSONObject();
            structure.put("structure", context.getStructure());

            submitData.put("hierarchy", structure);
            submitData.put("pageMeta", context.getPageMeta());
        }

        return submitData;
    }

    public String proccessRequestParameter(String p) {
        if (isGzip()) {
            return compress(p);
        } else {
            return p;
        }
    }

    private boolean isGzip() {
        boolean gzip = false;
        CartEngineContext context = CartEngine.getInstance(getCartFrom(cartFrom)).getContext();
        if (context != null) {
            JSONObject feature = context.getFeature();
            if (feature != null) {
                gzip = feature.getBooleanValue("gzip");
            }
        }
        return gzip;
    }

    public String getFeature() {
        String featureStr = "";
        CartEngineContext context = CartEngine.getInstance(getCartFrom(cartFrom)).getContext();
        if (context != null) {
            JSONObject feature = context.getFeature();
            if (feature != null) {
                featureStr = JSONObject.toJSONString(feature);
            }
        }
        return featureStr;
    }

    private String compress(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        byte[] compressed = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());

        try {
            boolean success = true;
            GZIPOutputStream gos = null;
            try {
                gos = new GZIPOutputStream(os);
                gos.write(str.getBytes("utf-8"));
            } catch (IOException e) {
                success = false;
            } finally {
                try {
                    if (gos != null) {
                        gos.close();
                    }
                } catch (IOException e) {
                    success = false;
                }
            }

            if (!success) {
                return str;
            }

            compressed = os.toByteArray();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                /* pass */
            }
        }

        return Base64.encodeToString(compressed, Base64.DEFAULT);
    }

    /**
     * 结构合并注册方法
     */
    public void registerCartGoodsComponent() {
        CartEngine engine = CartEngine.getInstance(getCartFrom(cartFrom));
        //注意这里第一个参数是指出待合并元素的父节点

//        engine.getAllCartComponents();
//        engine.registerSplitJoinRule(ComponentTag.ALL_ITEM, new CartSplitJoinRule());
        engine.registerSplitJoinRule(ComponentTag.SHOP, new CartSplitJoinRule());
        engine.registerSplitJoinRule(ComponentTag.GROUP, new CartSplitJoinRule());
        engine.registerSplitJoinRule(ComponentTag.ITEM, new CartSplitJoinRule());
    }

    public class CartSubmitResult {

        public CartSubmitResultType mResultType;
        public String mErrorMsg;
        public BuildOrderRequestBo mBuildOrderRequestBo;

        public CartSubmitResult(CartSubmitResultType type, String errorMsg, BuildOrderRequestBo buildOrderRequestBo) {
            mResultType = type;
            mErrorMsg = errorMsg;
            mBuildOrderRequestBo = buildOrderRequestBo;
        }
    }

    private static class CartSplitJoinRule implements SplitJoinRule {


        @Override
        public List<Component> execute(List<Component> input, CartFrom cartFrom) {
            CartGoodsComponent cartGoodsComponent = new CartGoodsComponent(cartFrom);
            for (Component component : input) {
                switch (ComponentTag.getComponentTagByDesc(component.getTag())) {
                    case ALL_ITEM:
                        cartGoodsComponent.setAllItemComponent((AllItemComponent) component);
                        break;
                    case ITEM:
                        cartGoodsComponent.setItemComponent((ItemComponent) component);
                        break;
                    case BUNDLE:
                        cartGoodsComponent.setBundleComponent((BundleComponent) component);
                        break;
                    case PROMOTION:
                        cartGoodsComponent.setPromotionComponent((PromotionComponent) component);
                        break;
                    case BANNER:
                        cartGoodsComponent.setBannerComponent((BannerComponent) component);
                        break;
                    case SHOP:
                        cartGoodsComponent.setShopComponent((ShopComponent) component);
                        break;
                    case FOOTER:
                        cartGoodsComponent.setFooterComponent((FooterComponent) component);
                        break;
                    case GROUP:
                        cartGoodsComponent.setGroupComponent((GroupComponent) component);
                        break;
                    case PROMOTIONBAR:
                        cartGoodsComponent.setPromotionBarComponent((PromotionBarComponent) component);
                        break;
                    default:
                        break;
                }
            }

            List<Component> output = new ArrayList<Component>(input.size());
            for (Component component : input) {
                switch (ComponentTag.getComponentTagByDesc(component.getTag())) {
                    case ITEM:
                        output.add(cartGoodsComponent);
                        break;
                    case ALL_ITEM:
                        output.add(cartGoodsComponent);
                        break;
                    case BUNDLE:
                        output.add(cartGoodsComponent);
                        break;
                    case BANNER:
                        continue;
                    case PROMOTION:
                        continue;
                    case PROMOTIONBAR:
                        continue;
                    case SHOP:
                        output.add(cartGoodsComponent);
                        break;
                    case FOOTER:
                        continue;
                    case GROUP:
                        output.add(cartGoodsComponent);
                        break;
                    default:
                        output.add(component);
                }
            }
            return output;
        }
    }

    private CartFrom getCartFrom(String cartFromString) {
        CartFrom cartFrom;
        if (cartFromString == null) {
            cartFrom = CartFrom.DEFAULT_CLIENT;
        } else if ("tsm_client_native".equalsIgnoreCase(cartFromString)) {
            cartFrom = CartFrom.TSM_NATIVE_TAOBAO;
        } else {
            cartFrom = CartFrom.parseCartFrom(cartFromString);
        }
        return cartFrom;
    }
}
