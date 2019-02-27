package com.yunos.tvtaobao.newcart.util;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuhaoteng on 2018/8/30.
 * 购物车结算时预估返管理类
 */

public class RebateManager {
    private static final String TAG = "RebateManager";
    //    电视淘宝返利标
    private final static String TVTAO_FANLI_ICON = "TVTAO_FANLI_ICON";
    private Set<String> mShopIdSet;
    private Set<String> mCartIdSet;
    private HashMap<String,Long> mCartNumMap;

    private RebateManager() {
        if (mShopIdSet == null) {
            mShopIdSet = new HashSet<>();
        }

        if (mCartIdSet == null) {
            mCartIdSet = new HashSet<>();
        }

        if(mCartNumMap == null){
            mCartNumMap = new HashMap<>();
        }
    }

    public static RebateManager getInstance() {
        return Holder.rebateManager;
    }

    private static class Holder {
        private static final RebateManager rebateManager = new RebateManager();
    }

    /**
     * @param shopId 店铺id
     * @param cartId 商品id
     * @param num   商品数量
     */
    public void add(String shopId, String cartId,long num) {
        if (!mShopIdSet.contains(shopId)) {
            mShopIdSet.add(shopId);
            AppDebug.i(TAG, "add shopId = " + shopId);
        }

        if (!mCartIdSet.contains(cartId)) {
            mCartIdSet.add(cartId);
            mCartNumMap.put(cartId,num);
            AppDebug.i(TAG, "add cartId = " + cartId+", num = "+num);
        }

    }


    /**
     * @param cartId 商品id
     */
    public void remove(String cartId) {
        if (mCartIdSet.contains(cartId)) {
            mCartIdSet.remove(cartId);
            mCartNumMap.remove(cartId);
            AppDebug.i(TAG, "remove cartId = " + cartId);
        }
    }

    /**
     * 购物车页面销毁时清除
     */
    public void clear() {
        mShopIdSet.clear();
        mCartIdSet.clear();
        mCartNumMap.clear();
    }



    /**
     * 获取当前勾选的返利信息
     * @param cartData
     */
    public String getChooseRebate(Map<String, List<CartGoodsComponent>> cartData) {
        float sumRebate = 0f;
        Iterator<String> shopIdIter = mShopIdSet.iterator();
        while (shopIdIter.hasNext()) {
            //遍历所有店铺
            String shopId = shopIdIter.next();
            if (cartData != null && cartData.containsKey(shopId)) {
                List<CartGoodsComponent> cartGoodsComponents = cartData.get(shopId);
                if (cartGoodsComponents != null) {
                    for (int i = 0; i < cartGoodsComponents.size(); i++) {
                        //店铺下的单个商品
                        CartGoodsComponent cartGoodsComponent = cartGoodsComponents.get(i);
                        if (cartGoodsComponent != null) {
                            ItemComponent itemComponent = cartGoodsComponent.getItemComponent();
                            if (itemComponent != null) {
                                //商品id
                                String cartId = itemComponent.getCartId();
                                if (mCartIdSet.contains(cartId)) {
                                    //数量
                                    Long num = mCartNumMap.get(cartId);
                                    float itemRebate = calculateItemRebate(itemComponent) * num;
                                    sumRebate = sumRebate + itemRebate;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (sumRebate != 0f) {
            DecimalFormat format=new DecimalFormat("0.00");
            String result = format.format(sumRebate);
            return "预计可返 ¥ " + result;
        } else {
            return "";
        }
    }


    /**
     * 计算单个商品的返利
     *
     * @param itemComponent
     */
    public float calculateItemRebate(ItemComponent itemComponent) {
        float rebateResult = 0f;
        if(itemComponent !=null){
            JSONObject jsonObjectFields = itemComponent.getFields();
            if (jsonObjectFields.containsKey("bizIcon")) {
                JSONObject jsonObject = jsonObjectFields.getJSONObject("bizIcon");
                if(jsonObject != null && jsonObject.containsKey("TV")){
                    JSONArray jsonArray = jsonObject.getJSONArray("TV");
                    for (int i = 0;i<jsonArray.size();i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if(jsonObject1!=null&&jsonObject1.containsKey("iconIDEnum")
                                &&jsonObject1.getString("iconIDEnum").equals(TVTAO_FANLI_ICON)){
                            if (jsonObject1.containsKey("text") && !TextUtils.isEmpty(jsonObject1.getString("text"))) {
                                String rebateCoupon = jsonObject1.getString("text");
                                if (rebateCoupon.contains("|")) {
                                    String[] rebate = rebateCoupon.split("\\|");
                                    if (rebate.length >= 2 && !TextUtils.isEmpty(rebate[1])) {
                                        String coupon = Utils.getRebateCoupon(rebate[1]);
                                        if (coupon != null) {
                                            try {
                                                rebateResult = Float.parseFloat(coupon);
                                                AppDebug.i(TAG,itemComponent.getTitle()+" rebate : "+rebateResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return rebateResult;
    }

}
