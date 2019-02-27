package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopHomeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by GuoLiDong on 2018/10/21.
 */
public class DataCenter {
    private static final String TAG = DataCenter.class.getSimpleName();

    private BaseActivity context;
    private ShopDetailData mShopDetailData;
    private String shopId;
    private String serviceId;
    private Location location;
    private Map<String, Object> stateRecord = new HashMap<>();
    private TakeOutBag takeOutBag;//购物车数据


    public DataCenter(BaseActivity context, String shopId, String serviceId, Location location) {
        this.context = context;
        this.shopId = shopId;
        this.serviceId = serviceId;
        this.location = location;
    }

    public boolean qryShopHomeDetail(CallBack callBack) {
        if ("yes".equals(stateRecord.get("qryShopHomeDetail"))) {
            return false;
        }
        stateRecord.put("qryShopHomeDetail", "yes");
        BusinessRequest.getBusinessRequest().requestShopHomeDetail(shopId, serviceId
                , getLongitude(), getLatitude()
                , "[\"1\"]", "1", null,
                new GetShopHomeDetailListener(new WeakReference<BaseActivity>(context), 1, callBack));
        return true;
    }

    public boolean qryTakeOutBag(CallBack callBack) {
        if ("yes".equals(stateRecord.get("qryTakeOutBag"))) {
            return false;
        }
        stateRecord.put("qryTakeOutBag", "yes");
        BusinessRequest.getBusinessRequest().getTakeOutBag(shopId,
                getLongitude(), getLatitude(), ""
                , new GetShopHomeBagListener(new WeakReference<BaseActivity>(context), callBack));
        return true;
    }

    public TakeOutBag.CartItemListBean findCartItemByGood(ItemListBean data) {
        if (data!=null && takeOutBag != null && takeOutBag.cartItemList != null) {
            for (int i = 0; i < takeOutBag.cartItemList.size(); i++) {
                if (takeOutBag.cartItemList.get(i)!=null
                        && (""+data.getItemId())
                        .equals(takeOutBag.cartItemList.get(i).itemId)){
                    return takeOutBag.cartItemList.get(i);
                }
            }
        }
        return null;
    }

    public static String getSkuId(ItemListBean data) {
        String skuId = data.getSkuList() != null && data.getSkuList().size() > 0 ? data.getSkuList().get(0).getSkuId() : null;
        return skuId;
    }

    private boolean insertGoodToBag(ItemListBean data, final CallBack callBack) {
        if ("yes".equals(stateRecord.get("insertGoodToBag"))) {
            return false;
        }
        stateRecord.put("insertGoodToBag", "yes");
        BusinessRequest.getBusinessRequest().requestTakeOutAddBag(data.getItemId(), getSkuId(data), "1"
                , "{\"lifeShopId\":\"" + shopId + "\",\"skuProperty\":\"\"}"
                , "taolife_client",
                new BizRequestListener<AddBagBo>(new WeakReference<BaseActivity>(context)) {
                    @Override
                    public boolean onError(int resultCode, String msg) {
                        stateRecord.remove("insertGoodToBag");
                        if (callBack != null) {
                            callBack.onError(resultCode, msg);
                        }
                        return false;
                    }

                    @Override
                    public void onSuccess(AddBagBo data) {
                        stateRecord.remove("insertGoodToBag");
                        if (callBack != null) {
                            callBack.onSuccess(data);
                        }
                    }

                    @Override
                    public boolean ifFinishWhenCloseErrorDialog() {
                        return false;
                    }
                });
        return true;
    }

    private boolean updateGoodInBag(CartAction action, String paramList, CallBack callBack) {
        if ("yes".equals(stateRecord.get("qryTakeOutUpdate"))) {
            return false;
        }
        stateRecord.put("qryTakeOutUpdate", "yes");
        String operateType = "";
        if (action == CartAction.clear) {
            operateType = "3";
        } else if (action == CartAction.update_add) {
            operateType = "1";
        } else if (action == CartAction.update_subtract) {
            operateType = "1";
        } else if (action == CartAction.update_rmv) {
            operateType = "2";
        }
        BusinessRequest.getBusinessRequest().requestTakeOutUpdate(shopId,
                getLatitude(), getLongitude(), operateType,
                paramList, new GetShopHomeBagListener(new WeakReference<BaseActivity>(context), callBack));
        return true;
    }

    public void add(ItemListBean data, final CallBack callBack){
        TakeOutBag.CartItemListBean cartItem = findCartItemByGood(data);
        if (cartItem!=null){
            // 已在购物车中，直接更新数量
            add(cartItem,callBack);
        } else {
            // 不在购物车中，添加新条目
            insertGoodToBag(data,callBack);
        }
    }

    public void subtract(ItemListBean  data, final CallBack callBack){
        TakeOutBag.CartItemListBean cartItem = findCartItemByGood(data);
        if (cartItem!=null){
            // 已在购物车中，直接更新数量
            subtract(cartItem,callBack);
        } else {
            // 不在购物车中，减个毛？
        }
    }

    public void add(TakeOutBag.CartItemListBean  data, final CallBack callBack){
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", data.itemId);
            jsonObject.put("skuId", data.skuId);
            jsonObject.put("quantity", data.amount + 1);
            jsonObject.put("cartId", data.cartId);
            JSONArray array = new JSONArray();
            array.put(jsonObject);
            json = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateGoodInBag(CartAction.update_add,json,callBack);
    }

    public void subtract(TakeOutBag.CartItemListBean  data, final CallBack callBack){
        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", data.itemId);
            jsonObject.put("skuId", data.skuId);
            jsonObject.put("quantity", data.amount - 1);
            jsonObject.put("cartId", data.cartId);
            JSONArray array = new JSONArray();
            array.put(jsonObject);
            json = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataCenter.CartAction action = data.amount==1
                ?DataCenter.CartAction.update_rmv
                :DataCenter.CartAction.update_subtract;
        updateGoodInBag(action,json,callBack);
    }

    public void clear(final CallBack callBack){
        List<TakeOutBag.CartItemListBean> dataList = takeOutBag.__cartItemList;
        JSONArray array = new JSONArray();
        for (TakeOutBag.CartItemListBean itemListBean : dataList) {
            if (itemListBean.itemId != null) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("itemId", itemListBean.itemId);
                    jsonObject.put("skuId", itemListBean.skuId);
                    jsonObject.put("quantity", itemListBean.amount);
                    jsonObject.put("cartId", itemListBean.cartId);
                    array.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        final String json = array.toString();
        if (!TextUtils.isEmpty(json)) {
            // 网络默认main 线程执行 需要切换js 线程
            context.getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    updateGoodInBag(CartAction.clear,json,callBack);
                }
            });
        }
    }

    public TakeOutBag getTakeOutBag() {
        return takeOutBag;
    }

    public ShopDetailData getShopDetailData() {
        return mShopDetailData;
    }

    public String getLatitude() {
        return location == null ? "" : location.x;
    }

    public String getLongitude() {
        return location == null ? "" : location.y;
    }

    public Map<String, Object> getStateRecord() {
        return stateRecord;
    }

    /**
     * 店铺信息回调
     * 店铺数据需要分页
     */
    private class GetShopHomeDetailListener extends BizRequestListener<ShopDetailData> {
        int mIndex;
        CallBack callBack;

        public GetShopHomeDetailListener(WeakReference<BaseActivity> baseActivityRef, int index, CallBack callBack) {
            super(baseActivityRef);
            this.mIndex = index;
            this.callBack = callBack;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            stateRecord.remove("qryShopHomeDetail");
            if (callBack != null) {
                callBack.onError(resultCode, msg);
            }
            return false;
        }

        @Override
        public void onSuccess(ShopDetailData data) {
            TakeOutShopHomeActivity activity = (TakeOutShopHomeActivity) mBaseActivityRef.get();

            if (mShopDetailData == null) {
                mShopDetailData = data;
            } else {
                mShopDetailData.marge(data);
            }

            if (activity != null && data != null && TextUtils.isEmpty(data.getGenreIds())) {
                optimizeData();
                stateRecord.remove("qryShopHomeDetail");
                if (callBack != null) {
                    callBack.onSuccess(data);
                }
                return;
            }

            if (activity != null && data != null && !TextUtils.isEmpty(data.getGenreIds())) {
                BusinessRequest.getBusinessRequest().requestShopHomeDetail(shopId, serviceId, location == null ? "120.008787" : location.y,
                        location == null ? "30.279712" : location.x, "[\"1\"]", String.valueOf(mIndex + 1), data.getGenreIds(),
                        new GetShopHomeDetailListener(mBaseActivityRef, (mIndex + 1), callBack));
            }
        }


        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

        /**
         * 优化数据，剔除一些无效条目，重新组织结构化的数据
         */
        private void optimizeData() {
            List<ShopDetailData.ItemGenreWithItemsListBean> listModels = getShopDetailData().getItemGenreWithItemsList();
            Iterator<ShopDetailData.ItemGenreWithItemsListBean> iterator = listModels.iterator();
            while (iterator.hasNext()) {// 移除无效数据数据
                List<ItemListBean> itemList = iterator.next().getItemList();
                if (itemList == null || itemList.size() == 0) {
                    iterator.remove();
                }
            }

            // 排序
            Collections.reverse(getShopDetailData().getItemGenreWithItemsList());

            // 整合在一个列表中
            List<ItemListBean> goodAllInOne = new ArrayList<>();
            iterator = listModels.iterator();
            while (iterator.hasNext()) {// 移除无效数据数据
                List<ItemListBean> itemList = iterator.next().getItemList();
                goodAllInOne.addAll(itemList);
            }
        }
    }

    /**
     * 获取购物车回调  and  修改购物车回调
     */
    private class GetShopHomeBagListener extends BizRequestListener<TakeOutBag> {

        CallBack callBack;

        public GetShopHomeBagListener(WeakReference<BaseActivity> baseActivityRef
                , CallBack callBack) {
            super(baseActivityRef);
            this.callBack = callBack;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            stateRecord.remove("qryTakeOutUpdate");
            stateRecord.remove("qryTakeOutBag");
            if (callBack != null) {
                callBack.onError(resultCode, msg);
            }
            return true;
        }

        @Override
        public void onSuccess(TakeOutBag data) {
            takeOutBag = data;
            takeOutBag.__cartItemList = new ArrayList<>();
            if (takeOutBag.cartItemList != null) {
                takeOutBag.__cartItemList.addAll(takeOutBag.cartItemList);
                int needPackageCount = 0;
                for (int i = takeOutBag.cartItemList.size() - 1; i >= 0; i--) {
                    if (i < takeOutBag.cartItemList.size()) {
                        TakeOutBag.CartItemListBean cartItemListBean = takeOutBag.cartItemList.get(i);
                        if (cartItemListBean != null) {
                            // 删除无效条目
                            if (cartItemListBean.quantity <= 0) {
                                takeOutBag.cartItemList.remove(i);
                                continue;
                            }
                            if (cartItemListBean.packingFee > 0) {
                                needPackageCount++;
                            }
                        }
                    }
                }

                Collections.reverse(takeOutBag.cartItemList);

                // 增加快餐盒项
                if (takeOutBag.packingFee > 0) {
                    TakeOutBag.CartItemListBean bean = new TakeOutBag.CartItemListBean();
                    bean.title = "餐盒费";
                    bean.amount = 0;
                    bean.totalPromotionPrice = takeOutBag.packingFee;
                    bean.isPackingFee = true;
                    bean.amount = needPackageCount > 0 ? needPackageCount : 1;
                    takeOutBag.cartItemList.add(0,bean);
                }
            }

            // 更新种类选中的数量
            for (ShopDetailData.ItemGenreWithItemsListBean itemGenreWithItemsListBean
                    : getShopDetailData().getItemGenreWithItemsList()) {
                itemGenreWithItemsListBean.totleGoodCount = 0;
                for (ItemListBean itemListBean : itemGenreWithItemsListBean.getItemList()) {
                    itemListBean.__intentCount = 0;
                    String itemId = itemListBean.getItemId();
                    if (itemId == null) {
                        continue;
                    }
                    if(takeOutBag.cartItemList!=null){
                        for (TakeOutBag.CartItemListBean bean : takeOutBag.cartItemList) {
                            if (bean != null && itemId.equals(bean.itemId)) {
                                if (bean.limitQuantity != null) {
                                    try {
                                        itemListBean.__limitQuantity = Integer.parseInt(bean.limitQuantity);
                                        itemListBean.__limitSkuId = bean.skuId;
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // 更新商品的选中数量
                                itemListBean.__intentCount += bean.amount;
                                // 更新品类的选中
                                itemGenreWithItemsListBean.totleGoodCount += bean.amount;
                            }
                        }
                    }

                }
            }

            stateRecord.remove("qryTakeOutUpdate");
            stateRecord.remove("qryTakeOutBag");
            if (callBack != null) {
                callBack.onSuccess(takeOutBag);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    public interface CallBack {
        void onError(int resultCode, String msg);

        void onSuccess(Object data);
    }

    public static abstract class CallBackImpl implements CallBack {
        @Override
        public void onError(int resultCode, String msg) {

        }

        @Override
        public void onSuccess(Object data) {

        }
    }

    public static enum CartAction {
        insert, update_add, update_subtract,update_rmv, clear;
    }

    public static class Location {
        /**
         * address : 乐佳国际 2号楼
         * area : 余杭区
         * areaCode : 330110
         * available : true
         * city : 杭州市
         * cityCode : 330100
         * fullAddress : true
         * id : 7319083501
         * mobile : 15179238435
         * name : 王林
         * prov : 浙江省
         * status : 0
         * street : 五常街道
         * streetCode : 330110005
         * x : 30.279712
         * y : 120.008787
         * userName : 街角小林
         */

        public String address;
        public String area;
        public String areaCode;
        public String available;
        public String city;
        public String cityCode;
        public String fullAddress;
        public String id;
        public String mobile;
        public String name;
        public String prov;
        public String status;
        public String street;
        public String streetCode;
        public String x;
        public String y;
        public String userName;
    }
}
