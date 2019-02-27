/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 宝贝属性
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:11:58
 */
public class Prop implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 137490692503179586L;

	// 宝贝属性id
	private Long propId;

	// 宝贝属性名称，如：机身颜色
	private String propName;

	// 属性值集合
	private PropValue[] values;

	public Long getPropId() {
		return propId;
	}

	public PropValue getValue(Long vid) {
		for (PropValue v : values) {
			if (v.getValueId().intValue() == vid.longValue()) {
				return v;
			}
		}

		return null;
	}

	public void setPropId(Long propId) {
		this.propId = propId;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public PropValue[] getValues() {
		return values;
	}

	public void setValues(PropValue[] values) {
		this.values = values;
	}

	public static Prop resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Prop p = new Prop();
		if (!obj.isNull("propId")) {
			p.setPropId(obj.getLong("propId"));
		}

		if (!obj.isNull("propName")) {
			p.setPropName(obj.getString("propName"));
		}

		if (!obj.isNull("values")) {
			JSONArray array = obj.getJSONArray("values");
			PropValue[] temp = new PropValue[array.length()];
			for (int i = 0; i < array.length(); i++) {
				temp[i] = PropValue.resolveFromMTOP(array.getJSONObject(i));
			}
			p.setValues(temp);
		}

		return p;
	}
}
