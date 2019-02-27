/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 投递信息
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:49:12
 */
public class Delivery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2803043364298675013L;

	// 0.虚拟商品，不需要运费，1:买家承担运费，2:卖家承担运费
	private int deliveryFeeType;

	/**
	 * 运费类型标题，对应不同deliveryFeeType为固定这符串，
	 * 
	 * deliveryFeeType=0 虚拟商品不需运费
	 * 
	 * deliveryFeeType=1 买家承担运费
	 * 
	 * deliveryFeeType=2 卖家承担运费
	 */
	private String title;

	// 投递目标地址, 全国或特定城市，如上海。计算规则先按用户收货地址，再按访问IP
	private String destination;
	
	private List<String> deliveryFees;

	public int getDeliveryFeeType() {
		return deliveryFeeType;
	}

	public void setDeliveryFeeType(int deliveryFeeType) {
		this.deliveryFeeType = deliveryFeeType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public static Delivery resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Delivery d = new Delivery();

		if (!obj.isNull("deliveryFeeType")) {
			d.setDeliveryFeeType(obj.optInt("deliveryFeeType"));
		}

		if (!obj.isNull("title")) {
			d.setTitle(obj.getString("title"));
		}

		if (!obj.isNull("destination")) {
			d.setDestination(obj.getString("destination"));
		}
		
		if (!obj.isNull("deliveryFees")) {
		    JSONArray jsonArray = obj.getJSONArray("deliveryFees");
		    if (jsonArray != null && jsonArray.length() > 0) {
		        List<String> deliveryFees = new ArrayList<String> ();
		        for (int i = 0; i < jsonArray.length(); i++) {
		            try {
    		            JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
    		            deliveryFees.add(jsonObject.getString("title"));
		            } catch (Exception e) {
		                
		            }
		        }
		        if (deliveryFees.size() >= 1) {
		            d.setDeliveryFees(deliveryFees);
		        }
		    }
		}

		return d;
	}

    public List<String> getDeliveryFees() {
        return deliveryFees;
    }

    public void setDeliveryFees(List<String> deliveryFees) {
        this.deliveryFees = deliveryFees;
    }
}
