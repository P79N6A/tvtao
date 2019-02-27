/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 宝贝特性
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:37:44
 */
public class Tag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6736952240795493531L;

	/**
	 * 宝贝特性标识，通过数值的二进制位表示， 100 商城宝贝 10000 虚拟宝贝 100000 PC秒杀 1000000 Wap秒杀
	 * 10000000 口碑店铺宝贝(暂时无法判断该标识) 100000000 票务凭证宝贝 1000000000 商超 10000000000
	 * 外卖(暂时无法判断该标识) 100000000000 代购 1000000000000 淘金币 10000000000000
	 * 房产(暂时无法判断该标识) 100000000000000 聚划算
	 * 
	 * 1000000000000000 医药馆
	 * 
	 * 10000000000000000 数字产品
	 * 
	 * 
	 * 判断方式可以通过与运算： 如 (tag & 16) == 16 为true 表示虚拟宝贝，false为非虚拟宝贝
	 */
	private Long value;
	
	private Map<String, String> params;

	// 不支持宝贝特性标识属性映射表
	private List<Integer> unSupportList = Arrays.asList(5, 6, 9, 10, 18, 19, 29, 21, 23, 25, 26);
	
	private boolean supportBuy = true;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public static Tag resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Tag tag = new Tag();
		if (!obj.isNull("value")) {
			tag.setValue(obj.getLong("value"));
			tag.setSupportBuy(tag.resolveSupportBuy(obj.getLong("value")));
		}

		return tag;
	}
	
	private boolean resolveSupportBuy(Long value) {
	    
	    if (value != null && value > 0) {
	        for (int i = 0; i< unSupportList.size(); i++) {
	            AppDebug.i("Tag", "mItem.getTag().value:"+value+",unSupportList.get(i):"+unSupportList.get(i)+",1L<<get(i):"+(1L << unSupportList.get(i)));
	            if ((value & (1L << unSupportList.get(i))) > 0) {
	                AppDebug.i("Tag", "mItem.getTag().supportBuy:"+false);
	                return false;
	            }
	        }
	    }
	    
	    return true;
	}

    public boolean isSupportBuy() {
        return supportBuy;
    }

    public void setSupportBuy(boolean supportBuy) {
        this.supportBuy = supportBuy;
    }
}
