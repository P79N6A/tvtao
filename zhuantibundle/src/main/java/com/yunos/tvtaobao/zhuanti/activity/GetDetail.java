package com.yunos.tvtaobao.zhuanti.activity;

import android.app.Activity;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.taobao.detail.domain.base.Unit;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2017/12/01
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetDetail {
    //详情数据对象

    public static TBDetailResultV6 resolveResult(String result) throws Exception {
        if (result == null) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(result);
        JSONObject obj = jsonObject.getJSONObject("data");

        TBDetailResultV6 tbDetailResultV6 = JSON.parseObject(obj.toString(), TBDetailResultV6.class);
        List<Unit> units = resolveDomainUnit(obj);
        if (units != null)
            tbDetailResultV6.setDomainList(units);
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


    public static TBDetailResultV6.PriceBeanX resolveSeckKillPrice(JSONObject data) {
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

    public static String resolveSeckKillSkuCore(JSONObject data) {

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

    public static TBDetailResultV6.Feature resolveSeckKillFeature(JSONObject data) {
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

    public static TBDetailResultV6.Delivery rexolveSeckKillDelivery(JSONObject data) {

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

    public static List<com.taobao.detail.domain.base.Unit> resolveDomainUnit(JSONObject data) {

        try {
            JSONObject props = data.getJSONObject("props");
            JSONArray groupProps = props.getJSONArray("groupProps");
            JSONObject jsonObject1 = (JSONObject) groupProps.get(0);
            JSONArray jsonArray = jsonObject1.getJSONArray("基本信息");
            List<com.taobao.detail.domain.base.Unit> list = new ArrayList<Unit>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                Iterator keys = jsonObject2.keys();
                while (keys.hasNext()) {
                    com.taobao.detail.domain.base.Unit unit = new com.taobao.detail.domain.base.Unit();
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
