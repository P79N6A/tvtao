package com.yunos.tvtaobao.tradelink.buildorder.bean;


import android.text.SpannableStringBuilder;
import android.util.SparseArray;

import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;

public class GoodsDisplayInfo {

    private String TAG = "GoodsDisplayInfo";
    // 小票的标题
    private String mTitle;
    // 
    private String mSku;
    // 小票显示当前使用的信息
    private ArrayList<GoodsItem> mInfoLinkedList;
    // 合计的总价
    private String mTotalPrice;
    // 店铺的名称
    private String mShopName;
    private String mItemId_tbs;
    private String mItemNames;
    // 保存无效的商品标题
    private SparseArray<String> mInvalidGoods;

    // 保存有效的商品标题
    private SparseArray<String> mValidGoods;


    private boolean installMentPay = false;

    public GoodsDisplayInfo() {
        mInfoLinkedList = new ArrayList<GoodsItem>();
        mInfoLinkedList.clear();

        mInvalidGoods = new SparseArray<String>();
        mInvalidGoods.clear();


        mValidGoods = new SparseArray<String>();
        mValidGoods.clear();

        mTitle = null;
        mSku = null;
        mTotalPrice = null;
        mShopName = null;
        mItemId_tbs = null;
    }

    public SparseArray<String> getInvalidGoods() {
        return mInvalidGoods;
    }

    public void setPutInvalidGoods(int postion, String value) {
        synchronized (mInvalidGoods) {
            mInvalidGoods.put(postion, value);
            AppDebug.i(TAG, "setPutInvalidGoods --> postion = " + postion + "; value = " + value + "size = "
                    + mInvalidGoods.size());
        }
    }

    public void setInstallMentPay(boolean installMentPay) {
        this.installMentPay = installMentPay;
    }

    public boolean isInstallMentPay() {
        return installMentPay;
    }

    public SparseArray<String> getValidGoods() {
        return mValidGoods;
    }


    public void setPutValidGoods(int postion, String value) {
        synchronized (mValidGoods) {
            mValidGoods.put(postion, value);
            AppDebug.i(TAG, "setPutValidGoods --> postion = " + postion + "; value = " + value + "size = "
                    + mValidGoods.size());
        }
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSku() {
        return mSku;
    }

    public void setSku(String mSku) {
        this.mSku = mSku;
    }

    public ArrayList<GoodsItem> getInfoList() {
        return mInfoLinkedList;
    }

    public void setInfoList(ArrayList<GoodsItem> mInfo) {
        this.mInfoLinkedList = mInfo;
    }

    public String getShopName() {
        return mShopName;
    }

    public void setShopName(String mShopName) {
        this.mShopName = mShopName;
    }

    public String getmItemIdTbs() {
        return mItemId_tbs;
    }

    public void setmItemIdTbs(String mItemIdtbs) {
        this.mItemId_tbs = mItemIdtbs;
    }

    public void setItemNames(String itemNames) {
        this.mItemNames = itemNames;
    }

    public String getItemNames() {
        return mItemNames;
    }

    public void setPutInfo(int postion, String key, SpannableStringBuilder value) {
        GoodsItem goodsItem = new GoodsItem();
        goodsItem.documents = key;
        goodsItem.value = value;
        synchronized (mInfoLinkedList) {
            mInfoLinkedList.add(goodsItem);
            AppDebug.i(TAG, "setPutInfo --> postion = " + postion + "; key = " + key + "; value = " + value + "size = "
                    + mInfoLinkedList.size());
        }

    }

    public String getTotalPrice() {
        return mTotalPrice;
    }

    public void setTotalPrice(String mTotalPrice) {
        this.mTotalPrice = mTotalPrice;
    }

    public static class GoodsItem {

        public String documents;
        public SpannableStringBuilder value;

        public GoodsItem() {

        }

        public GoodsItem(String key, SpannableStringBuilder val) {
            documents = key;
            value = val;
        }


        @Override
        public String toString() {
            return "documents = [" + documents + "] ; value = [" + value.toString() + "];";
        }
    }
}
