/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;


import com.yunos.tv.core.util.PriceFormator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 淘宝商品详情，超级复杂的对象
 * @author tianxiang
 * @date 2012-10-18 下午4:35:38
 */
public class TbItemDetail extends BaseMO implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3295625841796722989L;

    private Item              item;

    private Seller seller;

    private Guarantee[]       guarantees;

    private Delivery delivery;

    private Trade             trade;

    private Promotion[]       promotions;

    private Prop[]            props;

    private Sku               sku;

    private PriceUnits[]      priceUnits;

    private JhsItemInfo       jhsItemInfo;

    public static TbItemDetail resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        TbItemDetail detail = new TbItemDetail();

        detail.setItem(Item.resolveFromMTOP(obj.getJSONObject("item")));

        if (!obj.isNull("trade")) {
            detail.setTrade(Trade.resolveFromMTOP(obj.getJSONObject("trade")));
        }

        if (!obj.isNull("guarantees")) {
            JSONArray array = obj.getJSONArray("guarantees");
            Guarantee[] temp = new Guarantee[array.length()];
            for (int i = 0; i < array.length(); i++) {
                temp[i] = Guarantee.resolveFromMTOP(array.getJSONObject(i));
            }
            detail.setGuarantees(temp);
        }

        if (detail.getItem() != null && detail.getItem().getSku()) {
            if (!obj.isNull("sku")) {
                detail.setSku(Sku.resolveFromMTOP(obj.getJSONObject("sku")));
            }
        }

        if (!obj.isNull("delivery")) {
            detail.setDelivery(Delivery.resolveFromMTOP(obj.getJSONObject("delivery")));
        }

        if (!obj.isNull("seller")) {
            detail.setSeller(Seller.resolveFromMTOP(obj.getJSONObject("seller")));
        }

        if (!obj.isNull("props")) {
            JSONArray array = obj.getJSONArray("props");
            Prop[] temp = new Prop[array.length()];
            for (int i = 0; i < array.length(); i++) {
                temp[i] = Prop.resolveFromMTOP(array.getJSONObject(i));
            }
            detail.setProps(temp);
        }

        if (!obj.isNull("priceUnits")) {
            JSONArray array = obj.getJSONArray("priceUnits");
            PriceUnits[] temp = new PriceUnits[array.length()];
            for (int i = 0; i < array.length(); i++) {
                temp[i] = PriceUnits.resolveFromMTOP(array.getJSONObject(i));
            }
            detail.setPriceUnits(temp);
        }

        if (!obj.isNull("jhsItemInfo")) {
            detail.setJhsItemInfo(JhsItemInfo.resolveFromMTOP(obj.getJSONObject("jhsItemInfo")));
        }
        return detail;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Guarantee[] getGuarantees() {
        return guarantees;
    }

    public void setGuarantees(Guarantee[] guarantees) {
        this.guarantees = guarantees;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Promotion[] getPromotions() {
        return promotions;
    }

    public void setPromotions(Promotion[] promotions) {
        this.promotions = promotions;
    }

    public Prop[] getProps() {
        return props;
    }

    public void setProps(Prop[] props) {
        this.props = props;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public PriceUnits[] getPriceUnits() {
        return priceUnits;
    }

    public void setPriceUnits(PriceUnits[] priceUnits) {
        this.priceUnits = priceUnits;
    }

    public JhsItemInfo getJhsItemInfo() {
        return jhsItemInfo;
    }

    public void setJhsItemInfo(JhsItemInfo jhsItemInfo) {
        this.jhsItemInfo = jhsItemInfo;
    }

    /**
     * 获取活动价
     * @return
     */
    public String getActivityPrice() {
        String activityPrice = PriceFormator.formatNoSymbolLong(item.getPrice());
        if (priceUnits != null && priceUnits.length > 0 && priceUnits[0] != null) {
            activityPrice = priceUnits[0].getPrice();
        }
        if (jhsItemInfo != null) {
            activityPrice = PriceFormator.formatNoSymbolLong(jhsItemInfo.getActivityPrice());
        }
        return activityPrice;
    }

    /**
     * 获取原价
     */
    public String getPrice() {
        String price = PriceFormator.formatNoSymbolLong(item.getPrice());
        if (priceUnits != null && priceUnits.length > 1 && priceUnits[1] != null) {
            price = priceUnits[1].getPrice();
        }
        return price;
    }

    /**
     * 活动标题
     * @return
     */
    public String getActivityTitle() {
        String activityTitle = "一口价";
        if (priceUnits != null) {
            for (int i = 0; i < priceUnits.length && priceUnits.length > 0; i++) {
                PriceUnits temp = priceUnits[i];
                if (temp.getValid()) {
                    activityTitle = temp.getName();
                    break;
                }
            }
        }
        if (jhsItemInfo != null) {
            activityTitle = "聚划算价";
        }
        return activityTitle;
    }

    /**
     * 是否活动中
     * @return
     */
    public boolean isActivity() {
        return (priceUnits == null && jhsItemInfo == null) ? false : true;
    }
}
