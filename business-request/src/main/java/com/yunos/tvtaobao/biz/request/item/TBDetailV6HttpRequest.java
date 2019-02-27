package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.taobao.detail.domain.base.Unit;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.DetailResultBo;
import com.yunos.tvtaobao.biz.request.bo.DetailResultBo_v6;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultVO_v6;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TBDetailV6HttpRequest extends BaseMtopRequest {
    private String itemId = "";
    //https://acs.m.taobao.com/gw/mtop.taobao.detail.getdetail/6.0/?data=%7B%22itemNumId%22%3A%22540389625693%22%7D
    private String host = "https://acs.m.taobao.com/gw/mtop.taobao.detail.getdetail/6.0/?data=";
    private String tag = "%7B%22itemNumId%22%3A%22";
    private String tag2 = "%22%2C%22detail_v%22%3A%223.1.0%22%7D";
    private String data = null;

    public TBDetailV6HttpRequest(String itemId, String areaId) {
        addParams("itemNumId", itemId);
        if (!TextUtils.isEmpty(areaId)) {
            addParams("areaId", areaId);
        }
        addParams("detail_v", "3.1.0");
        this.itemId = itemId;

    }

    @Override
    protected String getApi() {
        return "mtop.taobao.detail.getdetail";
    }

    @Override
    protected String getApiVersion() {
        return "6.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected TBDetailResultV6 resolveResponse(JSONObject obj) throws Exception {
        TBDetailResultV6 tbDetailResultV6 = JSON.parseObject(obj.toString(), TBDetailResultV6.class);
        List<Unit> units = resolveDomainUnit(obj);
        if (units != null)
            tbDetailResultV6.setDomainList(units);
        TBDetailResultV6.Feature feature = resolveSeckKillFeature(obj);
        TBDetailResultV6.Delivery delivery = rexolveSeckKillDelivery(obj);
        TBDetailResultV6.PriceBeanX priceBeanX = resolveSeckKillPrice(obj);
        String s = resolveSeckKillSkuCore(obj);

        //合约机
        if (tbDetailResultV6.getApiStack() != null && tbDetailResultV6.getApiStack().size() > 0) {
            tbDetailResultV6.setContractData(resolveContractData(tbDetailResultV6.getApiStack().get(0)));
        }

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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    public String getTTid() {
        return "142857@taobao_iphone_7.10.3";
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
