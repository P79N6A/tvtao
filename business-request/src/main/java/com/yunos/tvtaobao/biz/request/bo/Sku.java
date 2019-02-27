/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tvtaobao.biz.request.bo.PropPath.Pvid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 宝贝的SKU信息
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:26:19
 */
public class Sku implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4037393397654866350L;

	private SkuItem[] skuItems;

	private Prop[] props;

	public SkuItem[] getSkuItems() {
		return skuItems;
	}

	public void setSkuItems(SkuItem[] skuItems) {
		this.skuItems = skuItems;
	}

	public Prop[] getProps() {
		return props;
	}

	public void setProps(Prop[] props) {
		this.props = props;
	}

	public Prop getPropById(Long id) {
		if (props == null || props.length == 0) {
			return null;
		}

		for (Prop p : props) {
			if (p.getPropId().longValue() == id.longValue()) {
				return p;
			}
		}

		return null;
	}

	public Long getSkuId(List<Pvid> selectedPvids) {
		// 如果属性没有选择完整,根本就不能调用。如果调用了则要抛出异常终止，因为这是一个严重的错误行为
		if (selectedPvids.size() != props.length) {
			throw new RuntimeException("非法的调用,没有选择完SKU");
		}

		Long id = null;
		for (SkuItem item : skuItems) {
			if (item.contains(selectedPvids, null)) {
				id = item.getSkuId();
				break;
			}
		}

		return id;
	}

	public static Sku resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Sku s = new Sku();
		if (!obj.isNull("skus")) {
			JSONArray array = obj.getJSONArray("skus");
			SkuItem[] temp = new SkuItem[array.length()];
			for (int i = 0; i < array.length(); i++) {
				temp[i] = SkuItem.resolveFromMTOP(array.getJSONObject(i));
			}
			s.setSkuItems(temp);
		}

		if (!obj.isNull("props")) {
			JSONArray array = obj.getJSONArray("props");
			Prop[] temp = new Prop[array.length()];
			for (int i = 0; i < array.length(); i++) {
				temp[i] = Prop.resolveFromMTOP(array.getJSONObject(i));
			}
			s.setProps(temp);
		}

		return s;
	}
}
