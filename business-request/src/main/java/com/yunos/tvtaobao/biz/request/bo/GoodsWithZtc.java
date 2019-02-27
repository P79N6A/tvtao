/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: GoodsTmail.java
 * CREATED TIME: 2015年11月13日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import android.text.Html;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import com.alibaba.fastjson.JSON;

/**
 * 搜索天猫的商品数据
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年11月13日 下午4:17:09
 */

public class GoodsWithZtc implements Serializable, SearchedGoods {

    private static final String TAG = GoodsWithZtc.class.getSimpleName();

    /**
     * 商品标题
     */
    private String title;
    /**
     * detail链接
     */
    private String uri;

    /**
     * sku最优价格
     */
    private String price;
    /**
     * 折扣价
     */
    private String discount;

    /**
     * 销量
     */
    private String sold;

    /**
     * 类型:"item" or "ztc"
     */
    private String type;

    private String ismall;

    /**
     * ztc url
     */
    private String eurl;

    /**
     * 位置
     */
    private String location;

    /**
     * ztc图片
     */
    private String tbgoodslink;

    private String s11;

    private String s11_pre;

    /**
     * 返利
     */
    private RebateBo rebateBo;
    /**
     * post for ztc
     */
    private String post;

    private String nick;

    private String tagPicUrl;

    private List<String> tagNames;

    private String soldText;

    public String getSoldText() {
        return soldText;
    }

    public void setSoldText(String soldText) {
        this.soldText = soldText;
    }

    @Override
    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }

    public void setBuyCashback(boolean buyCashback) {
        this.buyCashback = buyCashback;
    }

    private boolean pre;

    private boolean buyCashback;

    @Override
    public String getEurl() {
        return eurl;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getItemId() {
        return "";
    }

    /**
     * 设置商品标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 商品图片url
     *
     * @param img
     */
    public void setImgUrl(String img) {
        this.tbgoodslink = img;
    }

    /**
     * 商品sku最优价格
     *
     * @param price
     */
    public void setPrice(String price) {
        this.price = price;
    }


    /**
     * 商品月销量
     *
     * @param sold
     */
    public void setSold(String sold) {
        this.sold = sold;
    }

    /**
     * 获取商品标题
     *
     * @@return title
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String getWmPrice() {
        return discount;
    }

    public String getPostFee() {
        return post;
    }

    /**
     * 商品详情url
     *
     * @return url
     */
    public String getDetailUrl() {
        return uri;
    }

    /**
     * 商品图片url
     */
    public String getImgUrl() {
        return tbgoodslink;
    }

    /**
     * 商品sku最优价格
     */
    public String getPrice() {
        return price;
    }

    /**
     * 商品月销量
     */
    public String getSold() {
        return sold;
    }

    @Override
    public String toString() {
        String text = "[  title = " + title + ", uri = " + uri + ", img = " + tbgoodslink
                + ", price = " + price + ", sold = " + sold + ", discount = " + discount
                + ", eurl = " + eurl + ", ismall = " + ismall + ", tbgoodslink = " + tbgoodslink + " ]";
        return text;
    }

    public String getPost() {
        return null;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getS11() {
        return s11;
    }

    @Override
    public String getS11Pre() {
        return s11_pre;
    }

    @Override
    public Boolean isPre() {
        return pre;
    }

    @Override
    public Boolean isBuyCashback() {
        return buyCashback;
    }

    @Override
    public void setRebateBo(RebateBo rebateBo) {
        this.rebateBo = rebateBo;

    }

    @Override
    public RebateBo getRebateBo() {
        return rebateBo;
    }


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


    @Override
    public String getTagPicUrl() {
        return tagPicUrl;
    }

    public void setTagPicUrl(String tagPicUrl) {
        this.tagPicUrl = tagPicUrl;
    }

    /**
     * 解析商品数据
     *
     * @return
     */
    public static GoodsWithZtc resolveFromJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        GoodsWithZtc goods = new GoodsWithZtc();
        if (!obj.isNull("title")) {
            String title = obj.getString("title");
            //替换换行符
            title = title.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\r", " ");
            title = Html.fromHtml(title).toString();
            goods.setTitle(title);
        }

        if (!obj.isNull("type")) {
            goods.setType(obj.getString("type"));
        }

        if (!obj.isNull("discount")) {
            goods.discount = obj.getString("discount");
        }

        if (!obj.isNull("price")) {
            goods.setPrice(obj.getString("price"));
        }

        if (!obj.isNull("tbgoodslink")) {
            goods.setImgUrl(obj.getString("tbgoodslink"));
        }

        if (!obj.isNull("ismall")) {
            goods.ismall = obj.getString("ismall");
        }

        if (!obj.isNull("type")) {
            goods.type = obj.getString("type");
        }

        if (!obj.isNull("s11")) {
            goods.s11 = obj.getString("s11");
        }

        if (!obj.isNull("s11_pre")) {
            goods.s11_pre = obj.getString("s11_pre");
        }

        if (!obj.isNull("sold")) {// 如果销量为空，或者销量第一个字符不是数字，则销量置为0
            String sold = obj.getString("sold");
            sold = sold.trim();
            if (TextUtils.isEmpty(sold)) {
                sold = "0";
            } else {
                String temp = sold.substring(0, 1);
                if (!TextUtils.isDigitsOnly(temp)) {
                    sold = "0";
                }
            }
            goods.setSold(sold);
        }

        if (!obj.isNull("eurl")) {
            goods.eurl = obj.getString("eurl");
        }

        if (!obj.isNull("uri")) {
            goods.uri = (obj.getString("uri"));
        }

        if (!obj.isNull("pre")) {
            goods.pre = (obj.getBoolean("pre"));
        }

        if (!obj.isNull("buyCashback")) {
            goods.buyCashback = (obj.getBoolean("buyCashback"));
        }

        if (!obj.isNull("nick")) {
            goods.setNick(obj.getString("nick"));
        }

        if (!obj.isNull("tagPicUrl")) {
            goods.setTagPicUrl(obj.getString("tagPicUrl"));
        }

        if (!obj.isNull("tagNames")) {
            goods.setTagNames(JSON.parseArray(obj.optString("tagNames"),String.class));
        }
        if (!obj.isNull("soldText")) {
            goods.setSoldText(obj.getString("soldText"));
        }

        AppDebug.v(TAG, "GoodsWithZtc.resolveFromJson.goods = " + goods);
        return goods;
    }
}
