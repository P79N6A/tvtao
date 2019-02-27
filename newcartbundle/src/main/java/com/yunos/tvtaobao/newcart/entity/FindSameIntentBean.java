package com.yunos.tvtaobao.newcart.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhoubo on 2018/7/18.
 * zhoubo on 2018/7/18 8：00
 * describition 为避免数据过多导致代码凌乱 传递的数据为Bean
 */

public class FindSameIntentBean implements Parcelable {
    private String catid;
    private String nid;
    private String skuId;
    private String title;
    private String url;
    private String price;

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.catid);
        dest.writeString(this.nid);
        dest.writeString(this.skuId);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.price);
    }

    public FindSameIntentBean() {
    }

    protected FindSameIntentBean(Parcel in) {
        this.catid = in.readString();
        this.nid = in.readString();
        this.skuId = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.price = in.readString();
    }

    public static final Parcelable.Creator<FindSameIntentBean> CREATOR = new Parcelable.Creator<FindSameIntentBean>() {
        @Override
        public FindSameIntentBean createFromParcel(Parcel source) {
            return new FindSameIntentBean(source);
        }

        @Override
        public FindSameIntentBean[] newArray(int size) {
            return new FindSameIntentBean[size];
        }
    };
}
