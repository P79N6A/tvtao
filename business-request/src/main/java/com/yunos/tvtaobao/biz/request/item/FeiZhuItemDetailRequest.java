package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.FeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.NewFeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dingbin on 2017/6/2.
 */

public class FeiZhuItemDetailRequest extends BaseMtopRequest {

    private static final String apiversion = "5.0";
    private static final String API = "mtop.trip.traveldetailskip.detail.get";

    public FeiZhuItemDetailRequest(String itemid) {
        if (!TextUtils.isEmpty(itemid)) {
            addParams("itemId", itemid);
        }
    }

    @Override
    protected FeiZhuBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        AppDebug.e("feizhu", obj.toString());
        Gson gson = new Gson();
        NewFeiZhuBean newFeiZhuBean = gson.fromJson(obj.toString(), NewFeiZhuBean.class);
        AppDebug.e("newFeiZhuBean1", newFeiZhuBean.toString());

        FeiZhuBean feiZhuBean = new FeiZhuBean();
        feiZhuBean.setNewFeiZhuBean(newFeiZhuBean);
        resolveFeiZhuService(obj, feiZhuBean);
        resolveFeiZhuSoldNum(obj, feiZhuBean);
        resolveFeiZhuText(obj, feiZhuBean);
        resolveFeiZhuRightDesc(obj, feiZhuBean);
        resolveFeiZhuMileage(obj, feiZhuBean);

        if (obj.has("coupon")) {
            feiZhuBean.setHasCoupon(true);
        } else {
            feiZhuBean.setHasCoupon(false);
        }

        if (TextUtils.isEmpty(feiZhuBean.getNewPrice())
                && newFeiZhuBean.getJhs() != null && newFeiZhuBean.getJhs().getData() != null
                && newFeiZhuBean.getJhs().getData().getStarted() != null
                && newFeiZhuBean.getJhs().getData().getStarted().getPrice() != null) {
            String price = newFeiZhuBean.getJhs().getData().getStarted().getPrice();
            AppDebug.e("feizhu price", price);
            if (price != null) {
                String priceResult = getPrice(price);
                feiZhuBean.setNewPrice(priceResult);
                AppDebug.e("feizhu price4", priceResult);
            }
        }

        if (TextUtils.isEmpty(feiZhuBean.getOldPrice())
                && newFeiZhuBean.getJhs() != null && newFeiZhuBean.getJhs().getData() != null
                && newFeiZhuBean.getJhs().getData().getStarted() != null
                && newFeiZhuBean.getJhs().getData().getStarted().getOriginalPrice() != null) {
            String price = newFeiZhuBean.getJhs().getData().getStarted().getOriginalPrice();
            AppDebug.e("feizhu price", price);
            if (price != null) {
                String priceResult = getPrice(price);
                feiZhuBean.setOldPrice(priceResult);
                AppDebug.e("feizhu price4", priceResult);
            }
        }


        AppDebug.e("feizhu.tostring", feiZhuBean.toString());

        return feiZhuBean;
    }

    private void resolveFeiZhuText(JSONObject obj, FeiZhuBean feiZhuBean) throws JSONException {
        if (obj.has("buyBanner") && obj.getString("buyBanner") != null) {
            JSONObject buyBanner = obj.getJSONObject("buyBanner");
            if (buyBanner != null) {
                JSONObject data = buyBanner.getJSONObject("data");
                String buyButtonDesc = data.getString("buyButtonDesc");
                String carDesc = data.getString("carDesc");
                if (!buyButtonDesc.equals(""))
                    feiZhuBean.setBuyText(buyButtonDesc);
                if (!carDesc.equals(""))
                    feiZhuBean.setCartText(carDesc);
            }
        }
    }


    private void resolveFeiZhuRightDesc(JSONObject obj, FeiZhuBean feiZhuBean) throws JSONException {
        if (obj.has("banner")) {
            JSONObject banner = obj.getJSONObject("banner");
            if (banner != null) {
                JSONObject data = banner.getJSONObject("data");
                if (data.has("rightDesc")) {
                    String rightDesc = data.getString("rightDesc");
                    if (!rightDesc.equals(""))
                        feiZhuBean.setRightDesc(rightDesc);
                }
            }
        }
    }

    private void resolveFeiZhuMileage(JSONObject obj, FeiZhuBean feiZhuBean) throws JSONException {
        if (obj.has("mileage")) {
            JSONObject mileage = obj.getJSONObject("mileage");
            if (mileage != null) {
                JSONObject data = mileage.getJSONObject("data");
                if (data.has("flayerTitle")) {
                    String flayerTitle = data.getString("flayerTitle");
                    if (!flayerTitle.equals(""))
                        feiZhuBean.setFlayerTitle(flayerTitle);
                }
                if (data.has("title")) {
                    String title = data.getString("title");
                    if (!title.equals(""))
                        feiZhuBean.setMileageTitle(title);
                }
            }
        }
    }

    private void resolveFeiZhuSoldNum(JSONObject obj, FeiZhuBean feiZhuBean) throws JSONException {
        if (obj.has("price") && obj.getString("price") != null) {
            JSONObject price = obj.getJSONObject("price");
            JSONObject data = price.getJSONObject("data");
            JSONObject jsonObject = data.getJSONObject("aidedPrice");
            if (jsonObject.has("price") && jsonObject.getString("price") != null) {
                String price1 = jsonObject.getString("price");
                AppDebug.e("feizhu price1", price1);
                if (price1 != null) {
                    String price2 = getPrice(price1);
                    feiZhuBean.setOldPrice(price2);
                    AppDebug.e("feizhu price2", price2);
                }
            } else {
                feiZhuBean.setOldPrice("");
            }
            if (data.has("mainPrice") && data.getJSONObject("mainPrice") != null) {
                JSONObject mainPrice = data.getJSONObject("mainPrice");
                if (mainPrice.has("price")) {
                    String price3 = mainPrice.getString("price");
                    AppDebug.e("feizhu price3", price3);
                    if (price3 != null) {
                        String price4 = getPrice(price3);
                        feiZhuBean.setNewPrice(price4);
                        AppDebug.e("feizhu price4", price4);
                    }
                } else {
                    feiZhuBean.setNewPrice("");
                }
            } else {
                feiZhuBean.setNewPrice("");
            }

            String soldCount = null;
            JSONArray jsonArray = jsonObject.getJSONArray("extra");
            if (jsonArray.length() != 0) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                        String content = jsonObject1.optString("content");
                        if (content.contains("月售")) {
                            soldCount = getSoldCount(content);
                            feiZhuBean.setSoldCount(soldCount);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (TextUtils.isEmpty(soldCount)) {
                if (obj.has("sold") && obj.get("sold") != null) {
                    JSONObject soldObject = obj.getJSONObject("sold");
                    if (soldObject.has("data") && soldObject.get("data") != null) {
                        JSONObject soldCountObject = soldObject.getJSONObject("data");
                        if (soldCountObject.has("soldCount") && soldCountObject.get("soldCount") != null) {
                            soldCount = soldCountObject.getString("soldCount");
                            feiZhuBean.setSoldCount(soldCount);
                        }
                    }
                }
            }


        }
    }

    /**
     * 格式化价格，飞猪接口返回的价格都是默认加2个0且没有逗号
     *
     * @param s
     * @return
     */
    private String getPrice(String s) {
        String s1;
        if (s.contains("-")) {
            String[] split = s.split("-");
            String substring = split[0].substring(0, split[0].length() - 2);
            String substring1 = split[1].substring(0, split[1].length() - 2);
            s1 = substring + "-" + substring1;
            return s1;
        } else {
            if (s.length() > 2 && s.endsWith("00")) {
                return s.substring(0, s.length() - 2);
            } else {
                int price = Integer.parseInt(s);
                float num = (float) price / 100;
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                String result = df.format(num);//返回的是String类型
                return result;
            }
        }
    }

    private String getSoldCount(String s) {
        //<span style=\"color:#A5A5A5;font-size:0.32rem;\">月售158笔</span>
        String[] split = s.split("月售");
        String[] sp = split[1].split("笔");
        String s1 = "";
        if (sp[0].contains(",")) {
            String[] split1 = sp[0].split(",");
            for (int i = 0; i < split1.length; i++) {
                s1 += split1[i];
            }
            return s1;
        }
        return sp[0];
    }

    private void resolveFeiZhuService(JSONObject jsonObject, FeiZhuBean feiZhuBean) throws JSONException {
        if (jsonObject.has("services") && jsonObject.getString("services") != null) {
            JSONObject services = jsonObject.getJSONObject("services");
            JSONObject jsonObject1 = services.getJSONObject("data");
            JSONArray jsonArray = jsonObject1.getJSONArray("cells");
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                    String title = jsonObject2.getString("title");
                    list.add(title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            feiZhuBean.setService(list);
        }
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
        return apiversion;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }


}
