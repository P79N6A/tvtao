package com.yunos.tvtaobao.newsku;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.widget.newsku.bo.PropData;
import com.yunos.tvtaobao.biz.widget.newsku.presenter.ITvTaoSkuPresenter;
import com.yunos.tvtaobao.biz.widget.newsku.view.ISkuView;
import com.yunos.tvtaobao.biz.widget.newsku.widget.SkuItemLayout;
import com.yunos.tvtaobao.businessview.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/4
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TvTaoSkuPresenter implements ITvTaoSkuPresenter {
    private final static String TAG = "TvTaoSkuPresenter";

    private ISkuView skuView;
    // 所有的sku属性
    private ArrayList<PropData> mAllPropList;
    // 选择的sku属性<propId，属性对象>
    private Map<Long, PropData> mSelectedPropMap;
    // 商品详情对象
    private TBDetailResultV6 tbDetailResultV6;
    // 有效的sku组合
    private Map<String, String> mPPathIdmap;

    private String skuId = null;

    public TvTaoSkuPresenter(ISkuView skuview) {
        this.skuView = skuview;
        this.mAllPropList = new ArrayList<PropData>();
        this.mSelectedPropMap = new HashMap<Long, PropData>();
        this.mPPathIdmap = new HashMap<String, String>();
    }

    @Override
    public void doDetailRequest(String itemId, String arearId) {
        BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(itemId, arearId, new RequestListener<TBDetailResultV6>() {
            @Override
            public void onRequestDone(TBDetailResultV6 data, int resultCode, String msg) {
                if (resultCode == 200) {
                    AppDebug.e(TAG, TAG + ".start time : " + System.currentTimeMillis());
                    TvTaoSkuPresenter.this.tbDetailResultV6 = data;
                    resolveSkuEngine(tbDetailResultV6);
                } else {
                    skuView.onShowError(msg);
                }
            }
        });
    }

    @Override
    public void doDetailData(TBDetailResultV6 tbDetailResultV6) {
        this.tbDetailResultV6 = tbDetailResultV6;
        resolveSkuEngine(tbDetailResultV6);
    }

    @Override
    public void setDefaultSku(String skuId) {
        this.skuId = skuId;
    }

    /**
     * @param tbDetailResultV6
     */
    public void resolveSkuEngine(TBDetailResultV6 tbDetailResultV6) {
        if (tbDetailResultV6 == null) {
            AppDebug.e(TAG, TAG + ".initValidSkuMap data null");
            return;
        }

        List<TBDetailResultV6.SkuBaseBean.SkusBean> skus = tbDetailResultV6.getSkuBase().getSkus();
        if (skus != null) {
            for (int i = 0; i < skus.size(); i++) {
                String propPath = skus.get(i).getPropPath();
                String skuId = skus.get(i).getSkuId();
                mPPathIdmap.put(propPath, skuId);
            }

            Iterator<Map.Entry<String, String>> iterator = mPPathIdmap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String skuid = next.getValue();
                if (!TextUtils.isEmpty(skuid)) {
                    removeInvalidSkuId(iterator, skuid);
                } else {
                    iterator.remove();// 去除无效
                }
            }

            if (getPropsBean() != null) {
                int propsSize = getPropsBean().size();
                for (int i = 0; i < propsSize; i++) {
                    TBDetailResultV6.SkuBaseBean.PropsBeanX propsBeanX = getPropsBean().get(i);
                    long mPropId = Long.parseLong(propsBeanX.getPid());
                    int valueSize = propsBeanX.getValues().size();
                    for (int j = 0; j < valueSize; j++) {
                        PropData propData = new PropData();
                        TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean values = propsBeanX.getValues().get(j);
                        long valueId = Long.parseLong(values.getVid());
                        propData.propKey = getPropKey(mPropId, valueId);
                        propData.propId = mPropId;
                        propData.valueId = valueId;
                        if (values.getImage() != null)
                            propData.imageUrl = values.getImage();
                        addPropData(propData);
                    }
                }
            }
        }

        doUnitBuy();
        doInitView();
        doDefaultSkuItem();
        doDisableItem();
        AppDebug.e(TAG, TAG + ".end time : " + System.currentTimeMillis());
    }

    /**
     * 初始化sku view
     */
    private void doInitView() {
        //初始化sku view
        skuView.initSkuView(getPropsBean());
        //默认标题
        skuView.initTitle(tbDetailResultV6.getItem().getTitle());
        //默认图片
        skuView.updateImage(tbDetailResultV6.getItem().getImages().get(0));
        //默认的库存和价格
        skuView.initSkuKuCunAndPrice(resolvePriceNum("0"));
    }

    /**
     * 初始化，处理单个sku时，判断库存
     * 库存为0时，则默认无法选中
     */
    private void doDisableItem() {
        if (getPropsBean() == null) {
            return;
        }

        int propsSize = getPropsBean().size();
        if (propsSize > 1) {
            return;
        }

        if (propsSize == mPPathIdmap.size()) {
            return;
        }

        if (tbDetailResultV6.getApiStack() != null) {
            String value = tbDetailResultV6.getApiStack().get(0).getValue();
            if (value != null) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    for (int i = 0; i < propsSize; i++) {
                        TBDetailResultV6.SkuBaseBean.PropsBeanX propsBeanX = getPropsBean().get(i);
                        long mPropId = Long.parseLong(propsBeanX.getPid());
                        int valueSize = propsBeanX.getValues().size();
                        for (int j = 0; j < valueSize; j++) {
                            TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean values = propsBeanX.getValues().get(j);
                            long mValueId = Long.parseLong(values.getVid());
                            String skuId = getSkuIdById(getPropKey(mPropId, mValueId));
                            if (sku2info.has(skuId)) {
                                SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
                                if (skuPriceNum != null && skuPriceNum.getQuantity() == 0) {
                                    skuView.updateValueViewStatus(mPropId, mValueId, SkuItemLayout.VALUE_VIEW_STATUS.DISABLE);
                                }
                            } else {
                                skuView.updateValueViewStatus(mPropId, mValueId, SkuItemLayout.VALUE_VIEW_STATUS.DISABLE);
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取默认的sku属性，并对其做选中
     */
    private void doDefaultSkuItem() {
        if (TextUtils.isEmpty(skuId) || tbDetailResultV6.getSkuBase() == null) {
            AppDebug.e(TAG, TAG + ".doDefaultSkuItem skuId is null");
            return;
        }

        // 如果默认的skuid对应的数据不存在时 返回null
        if (tbDetailResultV6.getSkuBase().getProps() != null) {
            //SkuPriceAndQuanitiy skuPriceAndQuanity = mTBDetailResultVO.skuModel.skus.get(skuId);
            SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
            if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
                return;
            }
        }

        if (mPPathIdmap == null) {
            return;
        }

        String propPair = null;
        Iterator<Map.Entry<String, String>> iter = mPPathIdmap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals(skuId)) {
                propPair = key;
                break;
            }
        }

        if (!TextUtils.isEmpty(propPair)) {
            String[] propArray = propPair.split(";");
            if (propArray != null) {
                for (int i = 0; i < propArray.length; i++) {
                    if (!TextUtils.isEmpty(propArray[i])) {
                        String[] pair = propArray[i].split(":");
                        if (pair != null && pair.length == 2) {
                            long propId = Long.parseLong(pair[0]);
                            long valueId = Long.parseLong(pair[1]);
                            skuView.updateValueViewStatus(propId, valueId, SkuItemLayout.VALUE_VIEW_STATUS.SELECTED);
                        }
                    }
                }
            }
        }
    }

    /**
     * 做 单位购买
     * (天猫超市)商品某些商品存在加倍购买
     */
    private void doUnitBuy() {
        if (tbDetailResultV6.getApiStack() != null) {
            String value = tbDetailResultV6.getApiStack().get(0).getValue();
            try {
                if (value != null) {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject skuItem = skuCore.getJSONObject("skuItem");
                    if (skuItem.has("unitBuy")) {
                        int unitBuy = Integer.parseInt(skuItem.getString("unitBuy"));
                        skuView.showUnitBuy(unitBuy);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取sku的SkuPriceNum
     *
     * @param skuid
     * @return
     */
    private SkuPriceNum resolvePriceNum(String skuid) {
        if (TextUtils.isEmpty(skuid)) {
            return null;
        }

        AppDebug.i(TAG, TAG + ".resolvePriceNum skuId : " + skuid);
        if (tbDetailResultV6.getApiStack() != null) {
            String value = tbDetailResultV6.getApiStack().get(0).getValue();
            try {
                if (value != null) {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    if (sku2info.has(skuid)) {
                        AppDebug.i(TAG, TAG + ".resolvePriceNum sku2info has " + skuid);
                        JSONObject jsonObject1 = sku2info.getJSONObject(skuid);
                        Gson gson = new Gson();
                        SkuPriceNum skuPriceNum = gson.fromJson(jsonObject1.toString(), SkuPriceNum.class);
                        return skuPriceNum;
                    }
                    return null;


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (tbDetailResultV6.getMockData() != null) {
                String mockData = tbDetailResultV6.getMockData();
                try {
                    JSONObject mjsonObject = new JSONObject(mockData);
                    JSONObject mskuCore = mjsonObject.getJSONObject("skuCore");
                    JSONObject msku2info = mskuCore.getJSONObject("sku2info");
                    JSONObject mjsonObject1 = msku2info.getJSONObject(skuid);
                    SkuPriceNum skuPriceNum = JSON.parseObject(mjsonObject1.toString(), SkuPriceNum.class);
                    return skuPriceNum;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    /**
     * 加入购物车
     *
     * @param activity
     */
    public void addCart(Activity activity, String itemId, int quantity, String skuId) {
        BusinessRequest.getBusinessRequest().addBag(itemId, quantity, skuId, null, new AddCardListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }

    //TODO 拿到item返利标签再做实现
    private String buildAddBagExParams(){
        JSONObject outJsonObject = new JSONObject();
        JSONObject innerJsonObject = new JSONObject();
        try {
            innerJsonObject.put("outPreferentialId","");
            innerJsonObject.put("tagId","");
            TvOptionsConfig.setTvOptionsCart(true);
            innerJsonObject.put("tvOptions", TvOptionsConfig.getTvOptions());
            TvOptionsConfig.setTvOptionsCart(false);
            innerJsonObject.put("appKey",Config.getChannel());
            outJsonObject.put("tvtaoExtra",innerJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outJsonObject.toString();
    }

    /**
     * 加入购物车请求坚听
     */
    private class AddCardListener extends BizRequestListener<ArrayList<SearchResult>> {
        private WeakReference<BaseActivity> baseActivityWeakReference;

        public AddCardListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            this.baseActivityWeakReference = baseActivityRef;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(ArrayList<SearchResult> data) {
            if (baseActivityWeakReference != null && baseActivityWeakReference.get() != null) {
                String prompt = baseActivityWeakReference.get().getResources().getString(R.string.new_shop_add_cart_sucess);
                skuView.onPromptDialog(R.drawable.coupon_apply_success_icon, prompt);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skuView.onDialogDismiss();
                        baseActivityWeakReference.get().finish();
                    }
                }, 2000);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 增加属性data
     *
     * @param propData
     */
    public void addPropData(PropData propData) {
        if (propData == null) {
            return;
        }
        if (mAllPropList == null) {
            mAllPropList = new ArrayList<PropData>();
        }
        if (TextUtils.isEmpty(propData.propKey)) {
            propData.propKey = getPropKey(propData.propId, propData.valueId);
        }
        propData.enable = true;
        propData.selected = false;
        mAllPropList.add(propData);
    }

    public List<TBDetailResultV6.SkuBaseBean.PropsBeanX> getPropsBean() {
        if (tbDetailResultV6!=null&&tbDetailResultV6.getSkuBase() != null && tbDetailResultV6.getSkuBase().getProps() != null) {
            return tbDetailResultV6.getSkuBase().getProps();
        } else {
            return null;
        }
    }

    /**
     * 从所有的sku组合中，去除无效的skuId，以及库存为0的skuId
     *
     * @param iterator
     * @param skuid
     */
    private void removeInvalidSkuId(Iterator<Map.Entry<String, String>> iterator, String skuid) {
        if (tbDetailResultV6.getApiStack() != null) {
            String value = tbDetailResultV6.getApiStack().get(0).getValue();
            try {
                if (value != null) {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    if (sku2info.has(skuid)) {
                        AppDebug.w(TAG, TAG + ".removeInVaildSkuId skuId : " + skuid);
                        JSONObject skuIdObject = sku2info.getJSONObject(skuid);
                        int quantity = Integer.parseInt(skuIdObject.getString("quantity"));
                        if (quantity <= 0) {
                            iterator.remove();
                        }
                    } else {
                        iterator.remove();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPropKey(long propId, long valueId) {
        return propId + ":" + valueId;
    }

    /**
     * 从列表中找到所需的属性数据
     *
     * @param propId
     * @param valueId
     * @return
     */
    public PropData getPropDataFromList(long propId, long valueId) {
        if (mAllPropList == null) {
            return null;
        }
        for (int i = 0; i < mAllPropList.size(); i++) {
            PropData propData = mAllPropList.get(i);
            if (propData.propId == propId && propData.valueId == valueId) {
                return propData;
            }
        }

        return null;
    }

    /**
     * 更新属性值状态
     */
    private void updatePropValueStatus() {
        if (tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null || mPPathIdmap == null
                || tbDetailResultV6.getSkuBase().getProps() == null || mSelectedPropMap == null) {
            return;
        }

        // 每个属性受其他属性联合选择的影响
        Map<String, String> theUnionPropIncludeMap = new HashMap<String, String>();
        Long curPropId = null;// 用于判断当前属性值所在的属性类型，当属性类型发生变化，重新查找选择的组合的属性
        for (int i = 0; i < mAllPropList.size(); i++) {
            PropData data = mAllPropList.get(i);

            if (curPropId == null || curPropId != data.propId) {
                curPropId = data.propId;
                theUnionPropIncludeMap.clear();
                Iterator<Map.Entry<String, String>> pathIter = mPPathIdmap.entrySet().iterator();
                while (pathIter.hasNext()) {
                    Map.Entry<String, String> entry = pathIter.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Iterator<Map.Entry<Long, PropData>> selectPropIter = mSelectedPropMap.entrySet().iterator();
                    boolean contain = true;
                    while (selectPropIter.hasNext()) {
                        Map.Entry<Long, PropData> propEntry = selectPropIter.next();
                        Long propId = propEntry.getKey();
                        PropData propData = propEntry.getValue();
                        if (data.propId != propId) {// 取出同时存在的sku组合
                            if (!key.contains(propData.propKey)) {
                                contain = false;
                                break;
                            }
                        }
                    }

                    if (contain) {
                        theUnionPropIncludeMap.put(key, value);
                    }
                }
            }

            boolean contain = false;
            Iterator<Map.Entry<String, String>> unionIter = theUnionPropIncludeMap.entrySet().iterator();
            while (unionIter.hasNext()) {
                Map.Entry<String, String> entry = unionIter.next();
                String key = entry.getKey();
                if (key.contains(data.propKey)) {// 有一个组合存在这个属性值
                    contain = true;
                    String skuId = getSkuIdById(data.propKey);
                    SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
                    if (skuPriceNum != null && skuPriceNum.getQuantity() == 0) {
                        contain = false;
                    }
                    break;
                }
            }

            if (contain) {
                data.enable = true;
            } else {
                data.enable = false;
            }
        }

        // TODO 处理所有属性控件的状态
        for (int i = 0; i < mAllPropList.size(); i++) {
            PropData data = mAllPropList.get(i);
            SkuItemLayout.VALUE_VIEW_STATUS status;
            if (data.enable) {
                if (data.selected) {// 对于enable的view 如果改view本身是选择状态，则保持状态不变
                    status = SkuItemLayout.VALUE_VIEW_STATUS.SELECTED;
                } else {
                    status = SkuItemLayout.VALUE_VIEW_STATUS.ENABLE;
                }
            } else {
                status = SkuItemLayout.VALUE_VIEW_STATUS.DISABLE;
            }
            skuView.updateValueViewStatus(data.propId, data.valueId, status);
        }

        int mapSize = mSelectedPropMap.size();
        int propSize = tbDetailResultV6.getSkuBase().getProps().size();
        // 如果全部属性选中，判断该组合的库存是否为0，并且更新价格
        if (mapSize == propSize) {
            String skuId = getSkuId();
            if (!TextUtils.isEmpty(skuId)) {
                SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
                skuView.updateSkuKuCunAndPrice(skuPriceNum);
            }
        }
    }

    /**
     * 增加已选择的商品属性
     */
    public void addSelectedPropData(long propId, long valueId) {
        if (mSelectedPropMap == null) {
            mSelectedPropMap = new HashMap<Long, PropData>();
        }

        PropData propData = getPropDataFromList(propId, valueId);

        if (propData != null) {
            if (mSelectedPropMap.containsKey(propId)) {// 如果该类属性存在
                PropData theData = mSelectedPropMap.get(propId);
                if (theData == propData) {//如果属性值相同，表明已经被选中了
                    return;
                }

                //否则先删除，再加入新的
                PropData oldData = mSelectedPropMap.remove(propId);
                if (oldData != null) {
                    oldData.selected = false;
                }
            }
            propData.selected = true;
            mSelectedPropMap.put(propId, propData);
            if (!TextUtils.isEmpty(propData.imageUrl)) {
                skuView.updateImage(propData.imageUrl);
            }
            updatePropValueStatus();
        }
    }

    /**
     * 删除已选商品
     *
     * @param propId
     */
    public void deleteSelectedPropData(long propId) {
        if (mSelectedPropMap != null) {
            if (mSelectedPropMap.containsKey(propId)) {
                PropData data = mSelectedPropMap.get(propId);
                data.selected = false;
                mSelectedPropMap.remove(propId);
                updatePropValueStatus();
            }
        }
    }

    /**
     * 通过已选的sku组合，得到SKU ID
     *
     * @return
     */
    public String getSkuId() {
        if (tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || tbDetailResultV6.getSkuBase().getProps() == null || mPPathIdmap == null) {
            return null;
        }

        if (mSelectedPropMap.size() == tbDetailResultV6.getSkuBase().getProps().size()) {
            Iterator<Map.Entry<String, String>> iter = mPPathIdmap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                Iterator<Map.Entry<Long, PropData>> sPropIter = mSelectedPropMap.entrySet().iterator();
                boolean contain = true;
                while (sPropIter.hasNext()) {
                    Map.Entry<Long, PropData> sPropEntry = sPropIter.next();
                    PropData sPropValue = sPropEntry.getValue();
                    if (sPropValue != null) {
                        if (TextUtils.isEmpty(sPropValue.propKey)) {
                            sPropValue.propKey = getPropKey(sPropValue.propId, sPropValue.valueId);
                        }
                        if (!key.contains(sPropValue.propKey)) {
                            contain = false;
                            break;
                        }
                    }
                }
                if (contain) { // 找出包含所有的path，则可以找出对应的skuid
                    return value;
                }
            }
        }
        return null;
    }

    public String getSkuIdById(String propKey) {
        if (mPPathIdmap == null) {
            return null;
        }

        return mPPathIdmap.get(propKey);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {

    }
}
