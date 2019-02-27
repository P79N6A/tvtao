/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tv.core.util.PriceFormator;
import com.yunos.tvtaobao.biz.request.bo.PropPath.Pvid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @author tianxiang
 * @date 2012-10-18 下午4:27:51
 */
public class SkuItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 659841578861572201L;

	// sku唯一标识
	private Long skuId;

	// sku价格(单个sku宝贝的原价)
	private Long price;

	// sku库存
	private Long quantity = 0L;

	private String ppath;

	private PropPath path;

	private PriceUnits[] priceUnits;

	public Long getSkuId() {
		return skuId;
	}

	public void setSkuId(Long skuId) {
		this.skuId = skuId;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public boolean contains(Pvid pvid, Long quantity) {
		boolean flag = false;
		flag = path.contains(pvid);

		if (flag && quantity != null) {
			flag = flag && (quantity.longValue() == this.quantity.longValue());
		}

		return flag;
	}

	public boolean contains(List<Pvid> idList, Long quantity) {
		if (idList == null) {
			if (quantity == null) {
				return true;
			} else if (quantity.longValue() == this.quantity.longValue()) {
				return true;
			} else {
				return false;
			}
		}

		boolean flag = false;
		flag = path.contains(idList);

		if (flag && quantity != null) {
			flag = flag && (quantity.longValue() == this.quantity.longValue());
		}

		return flag;
	}

	public static SkuItem resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		SkuItem k = new SkuItem();
		if (!obj.isNull("skuId")) {
			k.setSkuId(obj.getLong("skuId"));
		}

		if (!obj.isNull("ppath")) {
			k.setPpath(obj.getString("ppath"));
		}

		if (!obj.isNull("price")) {
			k.setPrice(obj.getLong("price"));
		}

		if (!obj.isNull("quantity")) {
			k.setQuantity(obj.getLong("quantity"));
		}

		if (!obj.isNull("priceUnits")) {
		    JSONArray array = obj.getJSONArray("priceUnits");
		    PriceUnits temp[] = new PriceUnits[array.length()];
		    for (int i = 0; i < array.length(); i++) {
		        temp[i] = PriceUnits.resolveFromMTOP(array.getJSONObject(i));
		    }
		    k.setPriceUnits(temp);
		}

		return k;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getPpath() {
		return ppath;
	}

	public void setPpath(String ppath) {
		this.ppath = ppath;
		this.path = new PropPath(ppath);
	}

	public PropPath getPath() {
		return path;
	}

    public PriceUnits[] getPriceUnits() {
        return priceUnits;
    }

    public void setPriceUnits(PriceUnits[] priceUnits) {
        this.priceUnits = priceUnits;
    }
    
    /**
     * 活动价
     * @return
     */
    public String getActivityPrice() {
        String activityPrice = PriceFormator.formatNoSymbolLong(price);
        if (priceUnits != null && priceUnits.length > 0) {
            for (int i = 0; i < priceUnits.length; i++) {
                if (priceUnits[i].getValid()) {
                    activityPrice = priceUnits[i].getPrice();
                    break;
                }
            }
        }
        return activityPrice;
    }
}
