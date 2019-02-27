/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.sku
 * FILE NAME: SkuManager.java
 * CREATED TIME: 2015年2月28日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tradelink.sku;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年2月28日 下午5:22:53
 */
public class SkuEngine {
    private String TAG = "SkuEngine";
    // 所有的sku属性
    private ArrayList<PropData> mAllPropList;
    // 选择的sku属性<propId，属性对象>
    private Map<Long, PropData> mSelectedPropMap;
    // 商品详情对象
    //private TBDetailResultVO mTBDetailResultVO;
    private TBDetailResultV6 tbDetailResultV6;
    // 有效的sku组合
    private Map<String, String> mPPathIdmap;
    private Iterator<Entry<String, String>> iterator;

    public SkuEngine(TBDetailResultV6 tbDetailResultV6) {
        this.tbDetailResultV6 = tbDetailResultV6;
        mAllPropList = new ArrayList<PropData>();
        mSelectedPropMap = new HashMap<Long, PropData>();
        mPPathIdmap = new HashMap<String, String>();
        initValidSkuMap();
    }

    /**
     * 初始化有效的sku map
     */
    private void initValidSkuMap() {
        List<TBDetailResultV6.SkuBaseBean.SkusBean> skus = tbDetailResultV6.getSkuBase().getSkus();
        if (tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || skus == null ||
                tbDetailResultV6.getSkuBase().getProps() == null
                || mPPathIdmap == null) {
            AppDebug.v(TAG, TAG + ".initValidSkuMap data null");
            return;
        }
        //mPPathIdmap.putAll(mTBDetailResultVO.skuModel.ppathIdmap);

        for (int i = 0; i < skus.size(); i++) {
            String propPath = skus.get(i).getPropPath();
            String skuId = skus.get(i).getSkuId();
            mPPathIdmap.put(propPath, skuId);
        }


        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".initValidSkuMap first mPPathIdmap.size = " + mPPathIdmap.size());
        }

//        Iterator<Entry<String, String>> iter = mTBDetailResultVO.skuModel.ppathIdmap.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry<String, String> entry = iter.next();
//            String key = entry.getKey();
//            String skuId = entry.getValue();
//            if (!TextUtils.isEmpty(key)) {
//                if (!TextUtils.isEmpty(skuId)) {
//                    SkuPriceAndQuanitiy skuPriceAndQuanity = mTBDetailResultVO.skuModel.skus.get(skuId);
//                    if (skuPriceAndQuanity == null || skuPriceAndQuanity.quantity == null
//                            || skuPriceAndQuanity.quantity.intValue() <= 0) {
//                        mPPathIdmap.remove(key);// 去除库存为0的情况
//                        if (Config.isDebug()) {
//                            AppDebug.v(TAG, TAG + ".initValidSkuMap skuPriceAndQuanity == 0 key = " + key + ".skuId = "
//                                    + skuId);
//                        }
//                    }
//                } else {
//                    mPPathIdmap.remove(key);// 去除无效
//                }
//            }
//        }
        iterator = mPPathIdmap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String skuid = next.getValue();
            AppDebug.e(TAG, key + "----" + skuid);
            if (!TextUtils.isEmpty(key)) {
                if (!TextUtils.isEmpty(skuid)) {
                    removeInVaildSkuId(skuid, key);
                } else {
                    iterator.remove();// 去除无效

                }

            }
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".initValidSkuMap second mPPathIdmap.size = " + mPPathIdmap.size());
        }
    }

    public Map<String, String> getPPathIdmap() {
        return mPPathIdmap;
    }

    private JSONObject value;
    private JSONObject mjsonObject;
    private JSONObject mMockData;

    /**
     * 初始化时，移除数据中无效的skuId，以及库存为0的skuId
     *
     * @param skuid
     * @param key
     */
    private void removeInVaildSkuId(String skuid, String key) {
        if (tbDetailResultV6.getApiStack() != null) {

            if (value == null) {
                try {
                    String ui = tbDetailResultV6.getApiStack().get(0).getValue();
                    value = new JSONObject(ui);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                if (value != null) {
//                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = value.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    JSONObject jsonObject1 = null;
                    if (sku2info.has(skuid)) {
                        jsonObject1 = sku2info.getJSONObject(skuid);

                        SkuPriceNum skuPriceNum = JSON.parseObject(jsonObject1.toString(), SkuPriceNum.class);
                        if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
                            iterator.remove();// 去除库存为0的情况
                            if (Config.isDebug()) {
                                AppDebug.v(TAG, "apistack" + TAG + ".initValidSkuMap skuPriceAndQuanity == 0 key = " + key + ".skuId = "
                                        + skuid);
                            }
                        }
                    } else {
                        AppDebug.v(TAG, TAG + ".removeInVaildSkuId skuId = " + skuid);
                        iterator.remove();// 去除库存为0的情况
//                        //和接口开发者讨论后确定 由于某些原因skuid有可能会没有,mockdata作为兜底的
//                        if (tbDetailResultV6.getMockData() != null) {
//                            String mockData = tbDetailResultV6.getMockData();
//                            try {
//                                if (mMockData == null) {
//                                    mMockData = new JSONObject(mockData);
//                                }
//                                JSONObject mskuCore = mMockData.getJSONObject("skuCore");
//                                JSONObject msku2info = mskuCore.getJSONObject("sku2info");
//                                JSONObject mjsonObject1 = msku2info.getJSONObject(skuid);
//                                SkuPriceNum skuPriceNum = JSON.parseObject(mjsonObject1.toString(), SkuPriceNum.class);
//                                if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
//                                    iterator.remove();// 去除库存为0的情况
//                                    if (Config.isDebug()) {
//                                        AppDebug.v(TAG, "mocadata" + TAG + ".initValidSkuMap skuPriceAndQuanity == 0 key = " + key + ".skuId = "
//                                                + skuid);
//                                    }
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (tbDetailResultV6.getMockData() != null) {
                String mockData = tbDetailResultV6.getMockData();
                try {
                    if (mjsonObject == null) {
                        mjsonObject = new JSONObject(mockData);
                    }

                    JSONObject mskuCore = mjsonObject.getJSONObject("skuCore");
                    JSONObject msku2info = mskuCore.getJSONObject("sku2info");
                    JSONObject mjsonObject1 = msku2info.getJSONObject(skuid);
                    SkuPriceNum skuPriceNum = JSON.parseObject(mjsonObject1.toString(), SkuPriceNum.class);
                    if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
                        iterator.remove();// 去除库存为0的情况
                        if (Config.isDebug()) {
                            AppDebug.v(TAG, "mocadata" + TAG + ".initValidSkuMap skuPriceAndQuanity == 0 key = " + key + ".skuId = "
                                    + skuid);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 返回所有属性列表
     *
     * @return
     */
    public ArrayList<PropData> getAllPropList() {
        return mAllPropList;
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

    public String getPropKey(long propId, long valueId) {
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
     * 从列表中找到所需的属性数据
     *
     * @param
     * @param
     * @return
     */
    public PropData getPropDataFromList(String propKey) {
        if (mAllPropList == null) {
            return null;
        }
        for (int i = 0; i < mAllPropList.size(); i++) {
            PropData propData = mAllPropList.get(i);
            if (propData.propKey.equals(propKey)) {
                return propData;
            }
        }

        return null;
    }

    /**
     * 删除属性data
     *
     * @param propData
     */
    public void deletePropData(PropData propData) {
        if (propData == null) {
            return;
        }
        if (mAllPropList != null) {
            mAllPropList.remove(propData);
        }
    }

    /**
     * 清除所有属性
     */
    public void clearPropDataList() {
        if (mAllPropList != null) {
            mAllPropList.clear();
            mAllPropList = null;
        }
    }

    /**
     * 返回已选择的属性信息map
     *
     * @return
     */
    public Map<Long, PropData> getSelectedPropDataMap() {
        return mSelectedPropMap;
    }

    /**
     * 增加已选择的商品属性
     */
    public void addSelectedPropData(long propId, long valueId) {
        if (mSelectedPropMap == null) {
            mSelectedPropMap = new HashMap<Long, PropData>();
        }

        PropData propData = getPropDataFromList(propId, valueId);
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".addSelectedPropData.propId = " + propId + ".valueId = " + valueId + ".propData = "
                    + propData);
        }

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
            updateItemImage(propData);
            updatePropValueStatus();
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".addSelectedPropData.mSelectedPropMap = " + mSelectedPropMap + ".size = "
                    + mSelectedPropMap.size());
        }
    }

    /**
     * 删除已选商品
     *
     * @param propId
     */
    public void deleteSelectedPropData(long propId) {
        if (mSelectedPropMap != null) {
            PropData data = mSelectedPropMap.get(propId);
            data.selected = false;
            mSelectedPropMap.remove(propId);
            updatePropValueStatus();
        }
    }

    /**
     * 清除选择的属性
     */
    public void cleanSelectpropDataMap() {
        if (mSelectedPropMap != null) {
            mSelectedPropMap.clear();
            mSelectedPropMap = null;
        }
    }

    /**
     * 更新主图
     *
     * @param propData
     */
    private void updateItemImage(PropData propData) {
        if (mOnSkuPropLayoutListener != null) {
            mOnSkuPropLayoutListener.updateItemImage(propData);
        }
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
                Iterator<Entry<String, String>> pathIter = mPPathIdmap.entrySet().iterator();
                while (pathIter.hasNext()) {
                    Map.Entry<String, String> entry = pathIter.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Iterator<Entry<Long, PropData>> selectPropIter = mSelectedPropMap.entrySet().iterator();
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

            if (Config.isDebug()) {
                AppDebug.v(TAG, TAG + ".updatePropValueStatus.theUnionPropIncludeMap = " + theUnionPropIncludeMap);
            }

            boolean contain = false;
            Iterator<Entry<String, String>> unionIter = theUnionPropIncludeMap.entrySet().iterator();
            while (unionIter.hasNext()) {
                Map.Entry<String, String> entry = unionIter.next();
                String key = entry.getKey();
                if (key.contains(data.propKey)) {// 有一个组合存在这个属性值
                    contain = true;
                    break;
                }
            }

            if (contain) {
                data.enable = true;
            } else {
                data.enable = false;
            }
        }

        // 处理所以属性控件的状态
        for (int i = 0; i < mAllPropList.size(); i++) {
            PropData data = mAllPropList.get(i);
            SkuPropItemLayout.VALUE_VIEW_STATUS status;
            if (data.enable) {
                if (data.selected) {// 对于enable的view 如果改view本身是选择状态，则保持状态不变
                    status = SkuPropItemLayout.VALUE_VIEW_STATUS.UNKNOWN;
                } else {
                    status = SkuPropItemLayout.VALUE_VIEW_STATUS.ENABLE;
                }
            } else {
                status = SkuPropItemLayout.VALUE_VIEW_STATUS.DISABLE;
            }
            if (mOnSkuPropLayoutListener != null) {
                mOnSkuPropLayoutListener.updateSkuProp(data.propId, data.valueId, status);
            }
        }

        int mapSize = mSelectedPropMap.size();
        int propSize = tbDetailResultV6.getSkuBase().getProps().size();
        AppDebug.v(TAG, TAG + ".updatePropValueStatus.mSelectedPropMap.size = " + mapSize + ".skuProps.size = "
                + propSize);
        // 如果全部属性选中，判断该组合的库存是否为0，并且更新价格
        if (mapSize == propSize) {
            String skuId = getSkuId();
            AppDebug.v(TAG, TAG + ".updatePropValueStatus.skuId = " + skuId);
            if (!TextUtils.isEmpty(skuId)) {
                if (mOnSkuPropLayoutListener != null) {
                    mOnSkuPropLayoutListener.updateSkuKuCunAndrPrice(skuId);
                }
            }
        }
    }

    /**
     * 当所有商品选择后，获取skuID
     *
     * @return
     */
    public String getSkuId() {
        if (tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || tbDetailResultV6.getSkuBase().getProps() == null || mPPathIdmap == null) {
            return null;
        }

        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".getSkuId.mSelectedPropMap = " + mSelectedPropMap);
        }

        if (mSelectedPropMap.size() == tbDetailResultV6.getSkuBase().getProps().size()) {
            Iterator<Entry<String, String>> iter = mPPathIdmap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                Iterator<Entry<Long, PropData>> sPropIter = mSelectedPropMap.entrySet().iterator();
                boolean contain = true;
                while (sPropIter.hasNext()) {
                    Map.Entry<Long, PropData> sPropEntry = sPropIter.next();
                    PropData sPropValue = sPropEntry.getValue();
                    if (sPropValue != null) {
                        if (TextUtils.isEmpty(sPropValue.propKey)) {
                            sPropValue.propKey = getPropKey(sPropValue.propId, sPropValue.valueId);
                        }
                        if (Config.isDebug()) {
                            AppDebug.v(TAG, TAG + ".getSkuId.propKey = " + sPropValue.propKey + ".key = " + key
                                    + ", value = " + value);
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

    private SkuPriceNum resolvePriceNum(String skuid) {
        if (tbDetailResultV6 != null && tbDetailResultV6.getApiStack() != null && tbDetailResultV6.getApiStack().size() > 0) {
            try {
                String value = tbDetailResultV6.getApiStack().get(0).getValue();
                if (value != null) {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    if (sku2info.has(skuid)) {
                        JSONObject jsonObject1 = sku2info.getJSONObject(skuid);
                        SkuPriceNum skuPriceNum = JSON.parseObject(jsonObject1.toString(), SkuPriceNum.class);
                        return skuPriceNum;
                    }
                    return null;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取默认属性对
     *
     * @param skuId
     * @return
     */
    public Map<String, String> getDefaultPropFromSkuId(String skuId) {
        if (TextUtils.isEmpty(skuId) || tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null) {
            return null;
        }

        // 如果默认的skuid对应的数据不存在时 返回null
        if (tbDetailResultV6.getSkuBase().getProps() != null) {
            //SkuPriceAndQuanitiy skuPriceAndQuanity = mTBDetailResultVO.skuModel.skus.get(skuId);
            SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
            if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
                return null;
            }
        }

        if (mPPathIdmap == null) {
            return null;
        }

        String propPair = null;
        Iterator<Entry<String, String>> iter = mPPathIdmap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
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
                Map<String, String> propMap = new HashMap<String, String>();
                for (int i = 0; i < propArray.length; i++) {
                    if (!TextUtils.isEmpty(propArray[i])) {
                        String[] pair = propArray[i].split(":");
                        if (pair != null && pair.length == 2) {
                            propMap.put(pair[0], pair[1]);
                        }
                    }
                }
                return propMap;
            }
        }

        return null;
    }

    /**
     * 获取默认属性值
     *
     * @param propId
     * @return
     */
    public String getDefaultValueIdFromPropId(String propId, Map<String, String> defaultPropMap) {
        if (defaultPropMap == null || defaultPropMap.size() == 0 || TextUtils.isEmpty(propId)) {
            return null;
        }

        Iterator<Entry<String, String>> iter = defaultPropMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            if (entry != null) {
                if (propId.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    /**
     * 获取没有选择的属性
     *
     * @return
     */
    public TBDetailResultV6.SkuBaseBean.PropsBeanX getUnSelectProp() {
        if (mSelectedPropMap == null || tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || tbDetailResultV6.getSkuBase().getProps() == null) {
            return null;
        }

        int size = tbDetailResultV6.getSkuBase().getProps().size();
        if (mSelectedPropMap.size() != size) {
            for (int i = 0; i < size; i++) {
                TBDetailResultV6.SkuBaseBean.PropsBeanX propsBeanX = tbDetailResultV6.getSkuBase().getProps().get(i);

                long propId = Long.parseLong(propsBeanX.getPid());
                boolean exist = false;
                Iterator<Entry<Long, PropData>> iter = mSelectedPropMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Entry<Long, PropData> entry = iter.next();
                    if (propId == entry.getKey()) {// 表明该属性已经选择
                        exist = true;
                        break;
                    }
                }
                if (!exist) {//如果属性不存在，则表示没有选中
                    return propsBeanX;
                }
            }
        }

        return null;
    }

    private OnSkuPropLayoutListener mOnSkuPropLayoutListener;

    public void setOnSkuPropLayoutListener(OnSkuPropLayoutListener l) {
        mOnSkuPropLayoutListener = l;
    }

    public interface OnSkuPropLayoutListener {

        // 更新Sku属性
        void updateSkuProp(long propId, long valueId, SkuPropItemLayout.VALUE_VIEW_STATUS status);

        // 更新库存和价格
        void updateSkuKuCunAndrPrice(String skuId);

        // 更新主图
        void updateItemImage(PropData propData);
    }

    public static class PropData {

        public String propKey;// key值
        public long propId; // 属性ID
        public long valueId; // 值ID
        public String imageUrl; // 主图url
        public boolean selected;
        public boolean enable;

        @Override
        public String toString() {
            return "{ propKey = " + propKey + ", propId = " + propId + ", valueId = " + valueId + ", selected = "
                    + selected + ", enable = " + enable + ", imageUrl = " + imageUrl + "}";
        }
    }
}
