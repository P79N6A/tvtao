package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonObject;
import com.taobao.detail.domain.base.Unit;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 目前只用于解析数据返回,没有走默认的接口请求逻辑
 *
 * @author huangdaju
 * @data
 */
public class GetItemDetailV6RequestNew extends BaseMtopRequest {


    private String TAG = "GetItemDetailV6Request";
    private String extParams;
    private String apiVersion = "6.0";

    private final static String API = "mtop.taobao.detail.getdetail";

    public GetItemDetailV6RequestNew(String itemNumId, String extParams) {
        this.extParams = extParams;

        if (!TextUtils.isEmpty(itemNumId)) {
            addParams("itemNumId", itemNumId);
        }
        addParams("extParams", extParams);
        addParams("detail_v", "3.1.0");
        addParams("ttid", "taobao_android_7.0.0");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TBDetailResultV6 resolveResponse(JSONObject obj) throws Exception {
//        if (obj.toString().length() > 2000) {
//            for (int i = 0; i < obj.toString().length(); i += 2000) {
//                if (i + 2000 < obj.toString().length())
//                    AppDebug.e("商品详情数据" + i, obj.toString().substring(i, i + 2000));
//                else
//                    AppDebug.e("商品详情数据" + i, obj.toString().substring(i, obj.toString().length()));
//            }
//        } else {
//            AppDebug.e("商品详情数据", obj.toString());
//
//        }
        if (obj == null) {
            return null;
        }

        TBDetailResultV6 tbDetailResultV6 = JSON.parseObject(obj.toString(), TBDetailResultV6.class);
        List<Unit> units = resolveDomainUnit(obj);
        if (units != null)
            tbDetailResultV6.setDomainList(units);
        //合约机
        if (tbDetailResultV6.getApiStack() != null && tbDetailResultV6.getApiStack().size() > 0) {
            tbDetailResultV6.setContractData(resolveContractData(tbDetailResultV6.getApiStack().get(0)));
        }
        TBDetailResultV6.Feature feature = resolveSeckKillFeature(obj);
        TBDetailResultV6.Delivery delivery = rexolveSeckKillDelivery(obj);
        TBDetailResultV6.PriceBeanX priceBeanX = resolveSeckKillPrice(obj);
        String s = resolveSeckKillSkuCore(obj);

        if (feature != null)
            tbDetailResultV6.setFeature(feature);
        if (delivery != null)
            tbDetailResultV6.setDelivery(delivery);

        if (priceBeanX != null) {
            tbDetailResultV6.setPrice(priceBeanX);
        }
        if (!s.equals("")) {
            tbDetailResultV6.setSkuKore(s);
        }
        return tbDetailResultV6;
    }


    @Override
    protected String getHttpParams() {
        String params = super.getHttpParams();
        if (!TextUtils.isEmpty(extParams)) {
            params += ("&" + extParams);
        }
        AppDebug.e(TAG, TAG + ".getHttpParams.params = " + params);
        return params;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return apiVersion;
    }

    private TBDetailResultV6.PriceBeanX resolveSeckKillPrice(JSONObject data) {
        try {
            if (data.has("price")) {
                TBDetailResultV6.PriceBeanX priceBeanX = new TBDetailResultV6.PriceBeanX();
                JSONObject price = data.getJSONObject("price");
                if (price.has("price")) {
                    JSONObject price1 = price.getJSONObject("price");
                    if (price1 != null) {
                        TBDetailResultV6.PriceBeanX.PriceBean priceBean = new TBDetailResultV6.PriceBeanX.PriceBean();
                        if (price1.has("priceText")) {
                            priceBean.setPriceText(price1.getString("priceText"));
                        }
                        if (price1.has("priceTitle")) {
                            priceBean.setPriceTitle(price1.getString("priceTitle"));
                        }
                        priceBeanX.setPrice(priceBean);
                        return priceBeanX;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    private String resolveSeckKillSkuCore(JSONObject data) {

        try {
            if (data.has("skuCore")) {
                JSONObject data1 = data.getJSONObject("skuCore");
                //if (data1.has("sku2info")){
                //JSONObject sku2info = data1.getJSONObject("sku2info");
                if (data1 != null) {
                    return data1.toString();
                }
                //}
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    private List<TBDetailResultV6.ContractData> resolveContractData(TBDetailResultV6.ApiStackBean apiStackBean) {
        try {
            JSONObject object = new JSONObject(apiStackBean.getValue());
            if (object.has("skuVertical")) {
                JSONObject skuVertical = object.getJSONObject("skuVertical");
                if (!skuVertical.has("contractData")) {
                    return null;
                }
                JSONArray contract = skuVertical.getJSONArray("contractData");
                List<TBDetailResultV6.ContractData> result = new ArrayList<>();
                for (int i = 0; i < contract.length(); i++) {
                    JSONObject contractJson = contract.getJSONObject(i);
                    TBDetailResultV6.ContractData contractData = new TBDetailResultV6.ContractData();
                    contractData.versionData = TBDetailResultV6.ContractData.VersionData.resolveVersionData(contractJson.getJSONObject("version"));
                    result.add(contractData);
                }
                return result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private TBDetailResultV6.Feature resolveSeckKillFeature(JSONObject data) {
        try {
            if (data.has("feature")) {
                TBDetailResultV6.Feature feature = new TBDetailResultV6.Feature();
                JSONObject featureBean = data.getJSONObject("feature");
                if (featureBean.has("secKill")) {
                    String secKill = featureBean.getString("secKill");
                    feature.setSecKill(secKill);

                }
                if (featureBean.has("hasSku")) {
                    String hasSku = featureBean.getString("hasSku");
                    feature.setHasSku(hasSku);
                }
                return feature;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private TBDetailResultV6.Delivery rexolveSeckKillDelivery(JSONObject data) {

        if (data.has("delivery")) {
            try {
                TBDetailResultV6.Delivery delivery = new TBDetailResultV6.Delivery();
                JSONObject deliveryBean = data.getJSONObject("delivery");
                if (deliveryBean.has("postage")) {
                    String postage = deliveryBean.getString("postage");
                    delivery.setPostage(postage);
                    return delivery;
                }
                return null;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;

    }

    private List<Unit> resolveDomainUnit(JSONObject data) {

        try {
            JSONObject props = data.getJSONObject("props");
            JSONArray groupProps = props.getJSONArray("groupProps");
            JSONObject jsonObject1 = (JSONObject) groupProps.get(0);
            JSONArray jsonArray = jsonObject1.getJSONArray("基本信息");
            List<Unit> list = new ArrayList<Unit>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                Iterator keys = jsonObject2.keys();
                while (keys.hasNext()) {
                    Unit unit = new Unit();
                    String next = (String) keys.next();
                    String value = jsonObject2.optString(next);
                    unit.name = next;
                    unit.value = value;
                    list.add(unit);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                AppDebug.e("props数据", list.get(i).name);

            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

}
