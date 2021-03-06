package com.yunos.tvtaobao.juhuasuan.request;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.PaginationItemRate;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.JuOrderMO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json数据解析器
 * @author tianxiang
 * @date 2012-10-8 18:13:10
 */
public class JsonResolver {

    /**
     * 解析option
     * @param dataObj
     * @return
     * @throws JSONException
     */
    public static CountList<Option> resolveOptionList(JSONObject dataObj) throws JSONException {
        CountList<Option> list = new CountList<Option>();
        list.setCurrentPage(dataObj.optInt("currentPage"));
        list.setItemCount(dataObj.optInt("itemCount"));
        list.setPageSize(dataObj.optInt("pageSize"));
        list.setTotalPage(dataObj.optInt("totalPage"));
        if (dataObj.has("model")) {
            JSONArray array = dataObj.getJSONArray("model");
            if (array == null || array.length() == 0) {
                return null;
            }

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                Option item = Option.resolveFromMTOP(array.optJSONObject(i));
                if (item != null) {
                    list.add(item);
                } else {
                    AppDebug.v("", "null item:-----------------------------------" + i);
                }
            }
        }
        return list;
    }

    /**
     * 解析mtop.wdetail.getItemRates返回的评价信息
     * @param dataObj
     * @return
     * @throws JSONException
     * @author hanqi
     * @date 2014-6-5
     */
    public static PaginationItemRate resolveItemRate(JSONObject dataObj) throws JSONException {
        return PaginationItemRate.resolveFromMTOP(dataObj);
    }

    /**
     * 解析mtop.ju.item.search接口商品
     * @author tianxiang
     * @date 2012-10-8 19:12:38
     */
    public static CountList<ItemMO> resolveItemSearchItemMOList(JSONObject dataObj) throws JSONException {
        CountList<ItemMO> list = new CountList<ItemMO>();
        list.setCurrentPage(dataObj.optInt("currentPage"));
        list.setItemCount(dataObj.optInt("itemCount"));
        list.setPageSize(dataObj.optInt("pageSize"));
        list.setTotalPage(dataObj.optInt("totalPage"));
        JSONArray array = null;
        try {
            array = dataObj.getJSONArray("model");
        } catch (JSONException e) {
        }
        if (array == null || array.length() == 0) {
            return null;
        }
        for (int i = 0; i < array.length(); i++) {
            ItemMO item = ItemMO.fromMTOP(array.optJSONObject(i));
            if (item != null) {
                list.add(item);
            } else {
                AppDebug.v("", "null item:-----------------------------------" + i);
            }
        }

        return list;
    }

    /**
     * 解析普通分类商品
     * @author tianxiang
     * @date 2012-10-8 19:12:38
     */
    public static List<ItemMO> resolveItemMOList(JSONObject dataObj) throws JSONException {
        JSONArray array = getResultArray(dataObj);
        if (array == null || array.length() == 0) {
            return null;
        }

        List<ItemMO> list = new ArrayList<ItemMO>();
        for (int i = 0; i < array.length(); i++) {
            ItemMO item = ItemMO.fromMTOP(array.optJSONObject(i));
            if (item != null) {
                list.add(item);
            } else {
                AppDebug.v("", "null item:-----------------------------------" + i);
            }
        }

        return list;
    }

    /**
     * 解析 视频Item结构
     * @param dataObj
     * @return
     * @throws JSONException
     */
    public static CountList<ItemMO> resolveOptionItems(JSONObject dataObj) throws JSONException {
        CountList<ItemMO> list = CountList.fromMTOP(dataObj);
        if (null == list) {
            list = new CountList<ItemMO>();
        }

        if (dataObj.has("model")) {
            JSONArray array = dataObj.getJSONArray("model");
            if (array == null || array.length() == 0) {
                return null;
            }

            for (int i = 0; i < array.length(); i++) {
                @SuppressWarnings("unused")
                JSONObject obj = array.optJSONObject(i);
                //                if (!obj.has("videoURL") || StringUtil.isEmpty(obj.optString("videoURL"))) {
                //                    continue;
                //                }
                ItemMO item = ItemMO.fromMTOP(array.optJSONObject(i));
                if (item != null) {
                    list.add(item);
                } else {
                    AppDebug.v("", "null item:-----------------------------------" + i);
                }
            }
        }
        return list;
    }

    //    /**
    //     * 解析地址列表
    //     * @author tianxiang
    //     * @date 2012-10-8 19:12:38
    //     */
    //    public static List<Address> resolveAddressList(JSONObject dataObj) throws JSONException {
    //        JSONArray array = dataObj.getJSONArray("addressList");
    //        if (array == null || array.length() == 0) {
    //            return null;
    //        }
    //
    //        List<Address> list = new ArrayList<Address>();
    //
    //        // 是否有默认收货地址,没有的话则设置第一个为默认地址
    //        boolean hasDefault = false;
    //        for (int i = 0; i < array.length(); i++) {
    //            Address a = Address.fromMTOP(array.getJSONObject(i));
    //            if (a.getStatus() == 1 && !hasDefault) {
    //                hasDefault = true;
    //            }
    //            list.add(a);
    //        }
    //
    //        if (!hasDefault && list.size() > 0) {
    //            list.get(0).setStatus(1);
    //        }
    //
    //        return list;
    //    }

    /**
     * 解析运营标签
     * @author tianxiang
     * @date 2012-10-8 18:12:38
     */
    //	public static List<OperationTagMO> resolveOperationTagMOList(
    //			JSONObject dataObj) throws JSONException {
    //		JSONArray array = getResultArray(dataObj);
    //		if (array == null || array.length() == 0) {
    //			return null;
    //		}
    //
    //		List<OperationTagMO> list = new ArrayList<OperationTagMO>();
    //		for (int i = 0; i < array.length(); i++) {
    //			list.add(OperationTagMO.fromMTOP(array.getJSONObject(i)));
    //		}
    //
    //		return list;
    //	}

    /**
     * 解析运营标签
     * @author tianxiang
     * @date 2012-10-8 18:12:38
     */
    //	public static List<OperationTagMO> resolveOperationBanners(
    //			JSONObject dataObj) throws JSONException {
    //		JSONArray array = getResultArray(dataObj);
    //		if (array == null || array.length() == 0) {
    //			return null;
    //		}
    //
    //		List<OperationTagMO> list = new ArrayList<OperationTagMO>();
    //		for (int i = 0; i < array.length(); i++) {
    //			list.add(OperationTagMO.fromMTOP(array.getJSONObject(i)));
    //		}
    //
    //		return list;
    //	}

    /**
     * 解析云标签
     * @author tianxiang
     * @date 2012-10-8 18:12:38
     */
    public static List<String> resolveCloudTags(JSONObject dataObj) throws JSONException {
        JSONArray array = getResultArray(dataObj);

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }

        return list;
    }

    //    /**
    //     * 解析下单错误信息
    //     * @author hanqi
    //     * @date 2014-09-24
    //     */
    //    public static List<Address> resolveInvalidGroup(JSONObject dataObj) throws JSONException {
    //        JSONArray array = dataObj.getJSONArray("addressList");
    //        if (array == null || array.length() == 0) {
    //            return null;
    //        }
    //
    //        List<Address> list = new ArrayList<Address>();
    //
    //        // 是否有默认收货地址,没有的话则设置第一个为默认地址
    //        boolean hasDefault = false;
    //        for (int i = 0; i < array.length(); i++) {
    //            Address a = Address.fromMTOP(array.getJSONObject(i));
    //            if (a.getStatus() == 1 && !hasDefault) {
    //                hasDefault = true;
    //            }
    //            list.add(a);
    //        }
    //
    //        if (!hasDefault && list.size() > 0) {
    //            list.get(0).setStatus(1);
    //        }
    //
    //        return list;
    //    }

    /**
     * 解析字符串数组
     * @author tianxiang
     * @date 2012-10-8 18:12:38
     */
    public static List<String> resolveStringArray(JSONObject dataObj) throws JSONException {
        JSONArray array = getResultArray(dataObj);
        return resolveStringArray(array);
    }

    /**
     * 解析字符串数组
     * @param array
     * @return
     * @throws JSONException
     * @author hanqi
     * @date 2014-6-5
     */
    public static List<String> resolveStringArray(JSONArray array) throws JSONException {
        if (array == null || array.length() == 0) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }

        return list;
    }

    /**
     * 解析前台类目
     * @author tianxiang
     * @date 2012-10-8 18:12:38
     */
    public static List<CategoryMO> resolveFrontCategory(JSONObject dataObj) throws JSONException {
        JSONArray array = getResultArray(dataObj);
        if (array == null || array.length() == 0) {
            return null;
        }

        List<CategoryMO> list = new ArrayList<CategoryMO>();
        for (int i = 0; i < array.length(); i++) {
            list.add(CategoryMO.fromMTOP(array.getJSONObject(i)));
        }

        return list;
    }

    public static JSONObject getDataElement(String json) {

        if (json == null) {
            return null;
        }

        JSONObject data = null;
        try {
            data = new JSONObject(json).getJSONObject("data");
        } catch (JSONException e) {
        }

        return data;
    }

    public static String getElement(String json, String key) {
        if (json == null) {
            return null;
        }

        String data = null;
        try {
            data = new JSONObject(json).get(key).toString();
        } catch (JSONException e) {
        }

        return data;
    }

    private static JSONArray getResultArray(JSONObject dataObj) {
        JSONArray result = null;
        try {
            result = dataObj.getJSONArray("result");
        } catch (JSONException e) {
        }

        return result;
    }

    /**
     * 解析订单
     */
    public static List<JuOrderMO> resolveJuOrderList(JSONObject dataObj) throws JSONException {
        JSONArray array = getResultArray(dataObj);
        if (array == null || array.length() == 0) {
            return null;
        }

        List<JuOrderMO> list = new ArrayList<JuOrderMO>();
        for (int i = 0; i < array.length(); i++) {
            list.add(JuOrderMO.fromMTOP(array.getJSONObject(i)));
        }

        return list;
    }

    /**
     * 解析订单详情
     */
    //	public static JuScheduleMO resolveJuOrderDetail(JSONObject dataObj)
    //			throws JSONException {
    //		if (dataObj == null) {
    //			return null;
    //		}
    //		// "address":"文二路391号西湖国际科技大厦23F",
    //		// "area":"西湖区",
    //		// "attributesCC":"1",
    //		// "buyerId":"0",
    //		// "city":"杭州市",
    //		// "codStatus":"0",
    //		// "consignTime":"2012-08-01 12:40:38",
    //		// "divisionCode":"330106",
    //		// "endTime":"2012-08-11 12:42:22",
    //		// "fullName":"魏玉科",
    //		// "gmtCreate":"2012-07-31 08:51:53",
    //		// "gmtModified":"2012-08-11 12:42:22",
    //		// "logisticsOrderId":"200863840020467",
    //		// "logisticsStatus":"3",
    //		// "mobilePhone":"18072721160",
    //		// "outLogisticsId":"10252016442",
    //		// "post":"310000",
    //		// "postFee":"600",
    //		// "prov":"浙江省",
    //		// "shipping":"2"
    //		JuScheduleMO js = new JuScheduleMO();
    //		// 物流状态
    //		if (dataObj.has("logisticsStatus")) {
    //			js.setTransport_status(dataObj.getInt("logisticsStatus"));
    //		}
    //		// 地址
    //		String addr = "";
    //		if (dataObj.has("prov")) {
    //			addr = dataObj.getString("prov");
    //		}
    //		if (dataObj.has("city")) {
    //			addr += dataObj.getString("city");
    //		}
    //		if (dataObj.has("area")) {
    //			addr += dataObj.getString("area");
    //		}
    //		if (dataObj.has("address")) {
    //			addr += dataObj.getString("address");
    //		}
    //		js.setBuyer_address(addr);
    //		// 名字
    //		if (dataObj.has("fullName")) {
    //			js.setBuyer_nick(dataObj.getString("fullName"));
    //		}
    //		// 电话
    //		if (dataObj.has("mobilePhone")) {
    //			js.setBuyer_mobile(dataObj.getString("mobilePhone"));
    //		}
    //
    //		// sku
    //		if (dataObj.has("sku")) {
    //			js.setSku_str(dataObj.getString("sku"));
    //		}
    //		// 卖家
    //		if (dataObj.has("sellerNick")) {
    //			js.setSeller_nick(dataObj.getString("sellerNick"));
    //		}
    //
    //		return js;
    //	}

    /**
     * 转换JSONObject为Map
     * @date 2012-11-18下午6:12:22
     * @param obj
     * @return
     */
    public static Map<String, String> jsonobjToMap(JSONObject obj) {
        if (obj != null && obj.length() > 0) {
            Map<String, String> map = new HashMap<String, String>();
            JSONArray array = obj.names();
            for (int i = 0; i < array.length(); i++) {
                try {
                    map.put(array.getString(i), obj.optString(array.getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return map;
        } else {
            return null;
        }
    }
    /**
     * 解析push消息
     * @author yanlu
     * @date 2012-10-8 19:12:38
     */
    //   	public static List<PushMessageMO> resolvePushMsgList(JSONArray array)
    //   			throws JSONException {
    //   		if (array == null || array.length() == 0) {
    //   			return null;
    //   		}
    //
    //   		List<PushMessageMO> list = new ArrayList<PushMessageMO>();
    //   		for (int i = 0; i < array.length(); i++) {
    //               PushMessageMO pushMsg = PushMessageMO.fromMTOP(array.optJSONObject(i));
    //   			if (pushMsg != null) {
    //   				list.add(pushMsg);
    //   			} else {
    //   				Log.v("", "null push message:-----------------------------------" + i);
    //   			}
    //   		}
    //
    //   		return list;
    //   	}
}
