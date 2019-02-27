/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 宝贝优惠信息
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:18:03
 */
public class Promotion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3813035539734207655L;

	// 优惠的名称，定向优惠, 限时打折,店铺优惠 等
	private String name;

	// 值为LIMIT,TJB，MJS，PROMOTION分别表示限时打折、淘金币、店铺优惠、优惠，否则为空（PROMOTION指所有不能精确识别的优惠类别）
	private String type;

	// 优惠描述
	private String description;

	// 价格或价格范围(用于显示)，格式： *.元 或 **.*-*.*元 数值部分精确到小数点后两位
	private String price;

	// // 限时打折时出现，如：剩余1小时50分开始 或 剩余2小时50分结束
	// private String remaining;
	//
	// // 限购数量, 如限购5件
	// private String limitCount;
	//
	// // 淘金币优惠时出现，如5000淘金币
	// private String tjbNeed;
	//
	// // 店铺优惠出现(当送的是另一件宝贝时出现)， 宝贝的ID
	// private String itemId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public static Promotion resolveFromMTOP(JSONObject obj)
			throws JSONException {
		if (obj == null) {
			return null;
		}

		Promotion p = new Promotion();
		if (!obj.isNull("description")) {
			p.setDescription(obj.getString("description"));
		}

		if (!obj.isNull("name")) {
			p.setName(obj.getString("name"));
		}

		if (!obj.isNull("price")) {
			p.setPrice(obj.getString("price"));
		}

		if (!obj.isNull("type")) {
			p.setType(obj.getString("type"));
		}

		return p;
	}
}
