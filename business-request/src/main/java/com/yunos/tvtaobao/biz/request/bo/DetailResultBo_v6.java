package com.yunos.tvtaobao.biz.request.bo;


import android.text.TextUtils;
import android.util.Log;

import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.taobao.detail.clientDomain.TBDetailResultVO.SkuModel;
import com.taobao.detail.domain.DetailVO.DynamicItem.Delivery;
import com.taobao.detail.domain.DetailVO.DynamicItem.ItemControl;
import com.taobao.detail.domain.DetailVO.DynamicItem.ItemControl.UnitControl;
import com.taobao.detail.domain.DetailVO.DynamicItem.SkuPriceAndQuanitiy;
import com.taobao.detail.domain.DetailVO.StaticItem.DescInfo;
import com.taobao.detail.domain.DetailVO.StaticItem.GuaranteeInfo;
import com.taobao.detail.domain.DetailVO.StaticItem.GuaranteeInfo.Guarantee;
import com.taobao.detail.domain.DetailVO.StaticItem.RateInfo;
import com.taobao.detail.domain.DetailVO.StaticItem.SaleInfo.SkuProp;
import com.taobao.detail.domain.DetailVO.StaticItem.SaleInfo.SkuProp.SkuPropValue;
import com.taobao.detail.domain.DetailVO.StaticItem.Seller;
import com.taobao.detail.domain.DetailVO.StaticItem.Seller.EvaluateInfoVO;
import com.taobao.detail.domain.base.ActionUnit;
import com.taobao.detail.domain.base.PriceUnit;
import com.taobao.detail.domain.base.TipDO;
import com.taobao.detail.domain.base.Unit;
import com.taobao.detail.domain.meal.ComboInfo;
import com.taobao.detail.domain.rate.RateDetail;
import com.taobao.detail.domain.rate.RateTag;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.resource.entrances.Coupon;
import com.yunos.tvtaobao.biz.request.bo.resource.entrances.Entrances;
import com.yunos.tvtaobao.biz.request.bo.resource.entrances.ShopProm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetailResultBo_v6 implements Serializable {

    /**
     *
     */

    private static final String TAG = "DetailResultBo_v6";

    public static TBDetailResultVO_v6 resolve(JSONObject response) throws JSONException {

        if (response == null) {
            return null;
        }
        Log.i("worldbin", "v6_resolve: "+response.toString());
        AppDebug.i(TAG, "use DetailResultBo_v6 ... ");

        TBDetailResultVO_v6 tbDetailResultVO = new TBDetailResultVO_v6();

        JSONArray array_apiStack = response.optJSONArray("apiStack");
        if (array_apiStack != null && array_apiStack.length() > 0) {
            resolve_ApiStack(tbDetailResultVO, array_apiStack);
        }

//        JSONObject dataModel = response.optJSONObject("resource");
//        if (dataModel != null) {
//            resolve_resource(tbDetailResultVO, dataModel);
//        }


        return tbDetailResultVO;
    }


    /**
     * 解析ApiStack
     *
     * @param tbDetailResultVO
     * @param array
     */
    private static void resolve_ApiStack(TBDetailResultVO_v6 tbDetailResultVO, JSONArray array) {

        int length = array.length();
        List<Unit> apiStack = new ArrayList<Unit>();
        for (int i = 0; i < length; i++) {
            JSONObject obj = array.optJSONObject(i);
            if (obj != null) {
                Unit unit = new Unit();
                unit.name = obj.optString("name", null);
                unit.value = obj.optString("value", null);
                resolve_resource(tbDetailResultVO, unit.value);
//                resolve_resource(tbDetailResultVO, unit.value);
                apiStack.add(unit);
            }
        }
        tbDetailResultVO.setApiStack(apiStack);
    }


    private static void resolve_price(TBDetailResultVO_v6 tbDetailResultVO, JSONObject json) {
        if (json != null) {
            if (json.has("shopProm")) {
                JSONArray shopPromArray = json.optJSONArray("shopProm");
                resolve_shopProm(tbDetailResultVO, shopPromArray);
            }
        }
    }

    private static void resolve_shopProm(TBDetailResultVO_v6 tbDetailResultVO_v6, JSONArray jsonArray) {

        if (jsonArray != null && jsonArray.length() > 0) {
            JSONObject shopPromObject = jsonArray.optJSONObject(0);
            ShopProm shopProm = new ShopProm();
            shopProm.setIconText(shopPromObject.optString("iconText", ""));
            shopProm.setPeriod(shopPromObject.optString("period", ""));
            shopProm.setActionUrl(shopPromObject.optString("actionUrl",""));
            String content = "";
            if (shopPromObject.has("content")) {
                JSONArray contentArray = shopPromObject.optJSONArray("content");
                if (contentArray != null && contentArray.length() > 0)
                    content = contentArray.optString(0);
            }

            shopProm.setContent(content);
            tbDetailResultVO_v6.setShopProm(shopProm);
        }
    }

    /**
     * 解析feature
     *
     * @param tbDetailResultVO
     * @param jsonArray
     */
    private static void resolve_resource(TBDetailResultVO_v6 tbDetailResultVO, String jsonArray) {
        if (jsonArray == null)
            return;
        try {
            JSONObject obj = new JSONObject(jsonArray);
            if (obj.has("price")) {
                JSONObject priceObj = obj.optJSONObject("price");
                resolve_price(tbDetailResultVO, priceObj);
            }
            if (obj.has("resource")) {
                JSONObject resourceObj = obj.optJSONObject("resource");
                resolve_resource1(tbDetailResultVO, resourceObj);
//                String temp = obj.getString("resource");
//                TBDetailResultVO_v6.Resource resource = GsonUtil.parseJson(temp, new TypeToken<TBDetailResultVO_v6.Resource>() {
//                    obj.optJSONObject("coupon");
//                });
//                tbDetailResultVO.setResource(resource);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * ItemInfoModel
     * @param tbDetailResultVO
     * @param itemInfoModel
     *//*
    private static void resolve_ItemInfoModel(TBDetailResultVO_v6 tbDetailResultVO, JSONObject itemInfoModel) {

        tbDetailResultVO.itemInfoModel = new ItemInfoModel();

        tbDetailResultVO.itemInfoModel.itemId = itemInfoModel.optString("itemId", null);
        tbDetailResultVO.itemInfoModel.title = itemInfoModel.optString("title", null);
        tbDetailResultVO.itemInfoModel.favcount = itemInfoModel.optLong("favcount");
        tbDetailResultVO.itemInfoModel.stuffStatus = itemInfoModel.optString("stuffStatus", null);
        tbDetailResultVO.itemInfoModel.itemUrl = itemInfoModel.optString("itemUrl", null);
        tbDetailResultVO.itemInfoModel.sku = itemInfoModel.optBoolean("sku", false);
        tbDetailResultVO.itemInfoModel.location = itemInfoModel.optString("location", null);
        tbDetailResultVO.itemInfoModel.saleLine = itemInfoModel.optString("saleLine", null);
        tbDetailResultVO.itemInfoModel.categoryId = itemInfoModel.optString("categoryId", null);
        tbDetailResultVO.itemInfoModel.itemTypeName = itemInfoModel.optString("itemTypeName", null);
        tbDetailResultVO.itemInfoModel.itemTypeLogo = itemInfoModel.optString("itemTypeLogo", null);
        tbDetailResultVO.itemInfoModel.itemIcon = itemInfoModel.optString("itemIcon", null);
        tbDetailResultVO.itemInfoModel.isMakeup = itemInfoModel.optBoolean("isMakeup", false);
        tbDetailResultVO.itemInfoModel.itemUrl = itemInfoModel.optString("itemUrl", null);
        tbDetailResultVO.itemInfoModel.payedCount = itemInfoModel.optString("payedCount", null);
        tbDetailResultVO.itemInfoModel.points = itemInfoModel.optString("points", null);
        tbDetailResultVO.itemInfoModel.quantity = itemInfoModel.optLong("quantity");
        tbDetailResultVO.itemInfoModel.quantityText = itemInfoModel.optString("quantityText", null);

        tbDetailResultVO.itemInfoModel.soldQuantityText = itemInfoModel.optString("soldQuantityText", null);
        tbDetailResultVO.itemInfoModel.startTime = itemInfoModel.optString("startTime", null);
        tbDetailResultVO.itemInfoModel.totalSoldQuantity = itemInfoModel.optInt("totalSoldQuantity");
        tbDetailResultVO.itemInfoModel.weight = itemInfoModel.optString("weight", null);
        tbDetailResultVO.itemInfoModel.subTitle = itemInfoModel.optString("subTitle", null);

        JSONArray array = itemInfoModel.optJSONArray("picsPath");
        if (array != null && array.length() > 0) {
            tbDetailResultVO.itemInfoModel.picsPath = new ArrayList<String>();
            int length = array.length();
            for (int i = 0; i < length; i++) {
                String pic = array.optString(i);
                if (!StringUtils.isEmpty(pic)) {
                    tbDetailResultVO.itemInfoModel.picsPath.add(pic);
                }
            }
        }

        JSONArray priceUnits = itemInfoModel.optJSONArray("priceUnits");
        tbDetailResultVO.itemInfoModel.priceUnits = resolve_PriceUnit(priceUnits);

        JSONArray videosPath = itemInfoModel.optJSONArray("videosPath");
        if (videosPath != null && videosPath.length() > 0) {
            tbDetailResultVO.itemInfoModel.videosPath = new ArrayList<String>();
            int length = videosPath.length();
            for (int i = 0; i < length; i++) {
                String videoPath = videosPath.optString(i);
                if (!StringUtils.isEmpty(videoPath)) {
                    tbDetailResultVO.itemInfoModel.videosPath.add(videoPath);
                }
            }
        }
    }*/


    /**
     * 卖家信息节点
     *
     * @param tbDetailResultVO
     * @param seller
     */
    private static void resolve_Seller(TBDetailResultVO tbDetailResultVO, JSONObject seller) {

        tbDetailResultVO.seller = new Seller();
        tbDetailResultVO.seller.userNumId = seller.optLong("userNumId");
        tbDetailResultVO.seller.type = seller.optString("type", null);
        tbDetailResultVO.seller.nick = seller.optString("nick", null);
        tbDetailResultVO.seller.certificateLogo = seller.optString("certificateLogo", null);
        tbDetailResultVO.seller.certify = seller.optString("certify", null);
        tbDetailResultVO.seller.creditLevel = seller.optInt("creditLevel");
        tbDetailResultVO.seller.goodRatePercentage = seller.optString("goodRatePercentage", null);
        tbDetailResultVO.seller.shopTitle = seller.optString("shopId", null);
        tbDetailResultVO.seller.weitaoId = seller.optLong("weitaoId");
        tbDetailResultVO.seller.fansCount = seller.optString("fansCount", null);
        tbDetailResultVO.seller.fansCountText = seller.optString("fansCountText", null);
        tbDetailResultVO.seller.picUrl = seller.optString("picUrl", null);
        tbDetailResultVO.seller.starts = seller.optString("starts", null);
        tbDetailResultVO.seller.shopPromtionType = seller.optString("shopPromtionType", null);

        tbDetailResultVO.seller.bailAmount = seller.optString("bailAmount", null);
        tbDetailResultVO.seller.distance = seller.optString("distance", null);

        tbDetailResultVO.seller.distance = seller.optString("distance", null);
        tbDetailResultVO.seller.hide = seller.optString("hide", null);
        tbDetailResultVO.seller.hideDsr = seller.optString("hideDsr", null);
        tbDetailResultVO.seller.hideWangwang = seller.optString("hideWangwang", null);
        tbDetailResultVO.seller.html = seller.optString("html", null);
        tbDetailResultVO.seller.o2oMapUrl = seller.optString("o2oMapUrl", null);
        tbDetailResultVO.seller.shopBrand = seller.optString("shopBrand", null);
        tbDetailResultVO.seller.shopCollectorCount = seller.optInt("shopCollectorCount");
        tbDetailResultVO.seller.shopIcon = seller.optString("shopIcon", null);
        tbDetailResultVO.seller.shopId = seller.optInt("shopId");
        tbDetailResultVO.seller.shopLocation = seller.optString("shopLocation", null);
        tbDetailResultVO.seller.tollFreeNumber = seller.optString("tollFreeNumber", null);
        tbDetailResultVO.seller.tollFreeSubNumber = seller.optString("tollFreeSubNumber", null);

        JSONArray evaluateInfo = seller.optJSONArray("evaluateInfo");
        if (evaluateInfo != null && evaluateInfo.length() > 0) {
            tbDetailResultVO.seller.evaluateInfo = new ArrayList<EvaluateInfoVO>();
            int length = evaluateInfo.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = evaluateInfo.optJSONObject(i);
                if (obj != null) {
                    EvaluateInfoVO evaluateInfoVO = new EvaluateInfoVO();
                    evaluateInfoVO.highGap = obj.optString("highGap", null);
                    evaluateInfoVO.name = obj.optString("name", null);
                    evaluateInfoVO.score = obj.optString("score", null);
                    evaluateInfoVO.title = obj.optString("title", null);
                    tbDetailResultVO.seller.evaluateInfo.add(evaluateInfoVO);
                }
            }
        }

        JSONArray actionUnits = seller.optJSONArray("actionUnits");
        if (actionUnits != null && actionUnits.length() > 0) {
            tbDetailResultVO.seller.actionUnits = new ArrayList<ActionUnit>();
            int length = actionUnits.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = actionUnits.optJSONObject(i);
                if (obj != null) {
                    ActionUnit actionUnit = new ActionUnit();
                    actionUnit.name = obj.optString("name", null);
                    actionUnit.track = obj.optString("track", null);
                    actionUnit.url = obj.optString("url", null);
                    actionUnit.value = obj.optString("value", null);
                    tbDetailResultVO.seller.actionUnits.add(actionUnit);
                }
            }
        }

    }


    /**
     * 解析宝贝属性
     *
     * @param tbDetailResultVO
     * @param props
     */
    private static void resolve_Props(TBDetailResultVO tbDetailResultVO, JSONArray props) {
        int length = props.length();
        tbDetailResultVO.props = new ArrayList<Unit>();
        for (int i = 0; i < length; i++) {
            JSONObject obj = props.optJSONObject(i);
            if (obj != null) {
                Unit unit = new Unit();
                unit.name = obj.optString("name", null);
                unit.value = obj.optString("value", null);
                tbDetailResultVO.props.add(unit);
            }
        }
    }

    private static void resolve_DescInfo(TBDetailResultVO tbDetailResultVO, JSONObject descInfo) {

        tbDetailResultVO.descInfo = new DescInfo();
        tbDetailResultVO.descInfo.briefDescUrl = descInfo.optString("briefDescUrl", null);
        tbDetailResultVO.descInfo.fullDescUrl = descInfo.optString("fullDescUrl", null);
        tbDetailResultVO.descInfo.showFullDetailDesc = descInfo.optString("showFullDetailDesc", null);
        tbDetailResultVO.descInfo.pcDescUrl = descInfo.optString("showFullDetailDesc", null);
        tbDetailResultVO.descInfo.h5DescUrl = descInfo.optString("h5DescUrl", null);
        tbDetailResultVO.descInfo.h5DescUrl2 = descInfo.optString("h5DescUrl2", null);
        tbDetailResultVO.descInfo.moduleDescUrl2 = descInfo.optString("moduleDescUrl2", null);

    }

    private static void resolve_RateInfo(TBDetailResultVO tbDetailResultVO, JSONObject rateInfo) {
        tbDetailResultVO.rateInfo = new RateInfo();
        tbDetailResultVO.rateInfo.rateCounts = rateInfo.optInt("rateCounts");
        JSONArray rateDetailList = rateInfo.optJSONArray("rateDetailList");
        if (rateDetailList != null && rateDetailList.length() > 0) {
            int length = rateDetailList.length();
            tbDetailResultVO.rateInfo.rateDetailList = new ArrayList<RateDetail>();
            for (int i = 0; i < length; i++) {
                JSONObject obj = rateDetailList.optJSONObject(i);
                if (obj != null) {
                    RateDetail rateDetail = new RateDetail();
                    rateDetail.feedback = obj.optString("feedback", null);
                    rateDetail.nick = obj.optString("nick", null);
                    rateDetail.headPic = obj.optString("headPic", null);
                    rateDetail.star = obj.optInt("star");
                    rateDetail.subInfo = obj.optString("subInfo", null);
                    tbDetailResultVO.rateInfo.rateDetailList.add(rateDetail);
                }
            }
        }

        JSONArray tagList = rateInfo.optJSONArray("tagList");
        if (tagList != null && tagList.length() > 0) {
            int length = tagList.length();
            tbDetailResultVO.rateInfo.tagList = new ArrayList<RateTag>();
            for (int i = 0; i < length; i++) {
                JSONObject obj = tagList.optJSONObject(i);
                if (obj != null) {
                    RateTag rateTag = new RateTag();
                    rateTag.attribute = obj.optString("attribute", null);
                    rateTag.title = obj.optString("title", null);
                    rateTag.count = obj.optString("count", null);
                    rateTag.score = obj.optString("score", null);
                    tbDetailResultVO.rateInfo.tagList.add(rateTag);
                }
            }
        }
    }

    private static void resolve_ComboInfo(TBDetailResultVO tbDetailResultVO, JSONObject comboInfo) {
        tbDetailResultVO.comboInfo = new ComboInfo();


        JSONObject asynUrl = comboInfo.optJSONObject("asynUrl");
        if (asynUrl != null) {
            tbDetailResultVO.comboInfo.asynUrl = new Unit();
            tbDetailResultVO.comboInfo.asynUrl.name = asynUrl.optString("name", null);
            tbDetailResultVO.comboInfo.asynUrl.value = asynUrl.optString("value", null);
        }

        JSONObject h5Url = comboInfo.optJSONObject("h5Url");
        if (h5Url != null) {
            tbDetailResultVO.comboInfo.h5Url = new Unit();
            tbDetailResultVO.comboInfo.h5Url.name = h5Url.optString("name", null);
            tbDetailResultVO.comboInfo.h5Url.value = h5Url.optString("value", null);
        }
    }

    /**
     * 解析商品的SKU字段
     *
     * @param tbDetailResultVO
     * @param skuModel
     */
    private static void resolve_SkuModel(TBDetailResultVO tbDetailResultVO, JSONObject skuModel) {
        tbDetailResultVO.skuModel = new SkuModel();
        tbDetailResultVO.skuModel.installmentEnable = skuModel.optBoolean("installmentEnable");

        tbDetailResultVO.skuModel.skuTitle = skuModel.optString("skuTitle");

        JSONArray skuProps = skuModel.optJSONArray("skuProps");
        tbDetailResultVO.skuModel.skuProps = resolve_SkuModel_skuProps(skuProps);


        JSONObject ppathIdmap = skuModel.optJSONObject("ppathIdmap");
        tbDetailResultVO.skuModel.ppathIdmap = resolveToMap_String(ppathIdmap);


        JSONObject skus = skuModel.optJSONObject("skus");
        if (skus != null) {
            tbDetailResultVO.skuModel.skus = new HashMap<String, SkuPriceAndQuanitiy>();
            Iterator<?> it = skus.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                if (!TextUtils.isEmpty(key)) {
                    JSONObject sku_json = skus.optJSONObject(key);
                    if (sku_json != null) {
                        SkuPriceAndQuanitiy skuPriceAndQuanitiy = new SkuPriceAndQuanitiy();

                        skuPriceAndQuanitiy.quantity = sku_json.optInt("quantity");
                        skuPriceAndQuanitiy.hide = sku_json.optString("hide", null);
                        skuPriceAndQuanitiy.html = sku_json.optString("html", null);
                        skuPriceAndQuanitiy.quantityText = sku_json.optString("quantityText", null);
                        skuPriceAndQuanitiy.simplePrice = sku_json.optString("simplePrice", null);

                        JSONArray priceUnits = sku_json.optJSONArray("priceUnits");
                        skuPriceAndQuanitiy.priceUnits = resolve_PriceUnit(priceUnits);

                        tbDetailResultVO.skuModel.skus.put(key, skuPriceAndQuanitiy);
                    }
                }
            }
        }
    }


    /**
     * 解析 extras 字段(如果需要这个字段，请把Object进行序列化)
     *
     * @param tbDetailResultVO
     * @param extras
     */

    protected static void resolve_Extras(TBDetailResultVO tbDetailResultVO, JSONObject extras) {
        tbDetailResultVO.extras = new HashMap<String, Object>();
        Iterator<?> it = extras.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            if (!TextUtils.isEmpty(key)) {
                Object value = extras.opt(key);
                if (value != null) {
                    // TODO 看了合并的代码，没有具体看到extras的处理； 若后期需要用到，使用递归运算方式处理！
                    tbDetailResultVO.extras.put(key, value);
                }
            }
        }
        AppDebug.i(TAG, "tbDetailResultVO.extras = " + tbDetailResultVO.extras);
    }

    /**
     * @param tbDetailResultVO
     * @param displayType
     */
    private static void resolve_DisplayType(TBDetailResultVO tbDetailResultVO, JSONArray displayType) {
        int length = displayType.length();
        tbDetailResultVO.displayType = new String[length];
        for (int i = 0; i < length; i++) {
            tbDetailResultVO.displayType[i] = displayType.optString(i);
        }
    }


    /**
     * 运费相关的信息
     *
     * @param tbDetailResultVO
     * @param delivery
     */
    private static void resolve_Delivery(TBDetailResultVO tbDetailResultVO, JSONObject delivery) {

        tbDetailResultVO.delivery = new Delivery();

        tbDetailResultVO.delivery.destination = delivery.optString("destination", null);

        JSONArray deliveryFees = delivery.optJSONArray("deliveryFees");
        if (deliveryFees != null && deliveryFees.length() > 0) {
            tbDetailResultVO.delivery.deliveryFees = new ArrayList<String>();
            int length = deliveryFees.length();
            for (int i = 0; i < length; i++) {
                String deliveryFee = deliveryFees.optString(i);
                if (!TextUtils.isEmpty(deliveryFee)) {
                    tbDetailResultVO.delivery.deliveryFees.add(deliveryFee);
                }
            }
        }
    }


    /***
     * 消保相关的数据
     *
     * @param tbDetailResultVO
     * @param guaranteeInfo
     */
    private static void resolve_GuaranteeInfo(TBDetailResultVO tbDetailResultVO, JSONObject guaranteeInfo) {

        tbDetailResultVO.guaranteeInfo = new GuaranteeInfo();

        JSONArray guarantees = guaranteeInfo.optJSONArray("guarantees");
        if (guarantees != null && guarantees.length() > 0) {
            tbDetailResultVO.guaranteeInfo.guarantees = resolve_Guarantees(guarantees);
        }

        JSONArray beforeGuarantees = guaranteeInfo.optJSONArray("beforeGuarantees");
        if (beforeGuarantees != null && beforeGuarantees.length() > 0) {
            tbDetailResultVO.guaranteeInfo.beforeGuarantees = resolve_Guarantees(beforeGuarantees);
        }

        JSONArray afterGuarantees = guaranteeInfo.optJSONArray("afterGuarantees");
        if (afterGuarantees != null && afterGuarantees.length() > 0) {
            tbDetailResultVO.guaranteeInfo.afterGuarantees = resolve_Guarantees(afterGuarantees);
        }
    }


    /***
     * 商品
     *
     * @param tbDetailResultVO
     * @param itemControl
     */
    private static void resolve_ItemControl(TBDetailResultVO tbDetailResultVO, JSONObject itemControl) {

        tbDetailResultVO.itemControl = new ItemControl();

        tbDetailResultVO.itemControl.degradedItemUrl = itemControl.optString("degradedItemUrl", null);
        tbDetailResultVO.itemControl.buyUrl = itemControl.optString("buyUrl", null);
        tbDetailResultVO.itemControl.smartbanner = itemControl.optBoolean("smartbanner");

        JSONObject unitControl = itemControl.optJSONObject("unitControl");
        if (unitControl != null) {
            tbDetailResultVO.itemControl.unitControl = new UnitControl();
            tbDetailResultVO.itemControl.unitControl.baseTime = unitControl.optString("baseTime", null);
            tbDetailResultVO.itemControl.unitControl.buySupport = unitControl.optBoolean("buySupport");
            tbDetailResultVO.itemControl.unitControl.buyText = unitControl.optString("buyText", null);
            tbDetailResultVO.itemControl.unitControl.cartSupport = unitControl.optBoolean("cartSupport");
            tbDetailResultVO.itemControl.unitControl.cartText = unitControl.optString("cartText", null);
            tbDetailResultVO.itemControl.unitControl.errorCode = unitControl.optString("errorCode", null);
            tbDetailResultVO.itemControl.unitControl.errorLink = unitControl.optString("errorLink", null);
            tbDetailResultVO.itemControl.unitControl.errorMessage = unitControl.optString("errorMessage", null);
            tbDetailResultVO.itemControl.unitControl.limitCount = unitControl.optInt("limitCount");
            tbDetailResultVO.itemControl.unitControl.limitMultipleCount = unitControl.optInt("limitMultipleCount");
            tbDetailResultVO.itemControl.unitControl.limitMultipleText = unitControl.optString("limitMultipleText", null);
            tbDetailResultVO.itemControl.unitControl.offShelfUrl = unitControl.optString("offShelfUrl", null);
            tbDetailResultVO.itemControl.unitControl.submitText = unitControl.optString("submitText", null);
            tbDetailResultVO.itemControl.unitControl.unitTip = unitControl.optString("unitTip", null);

            JSONObject beforeBuyApi = unitControl.optJSONObject("beforeBuyApi");
            if (beforeBuyApi != null) {
                tbDetailResultVO.itemControl.unitControl.beforeBuyApi = new Unit();
                tbDetailResultVO.itemControl.unitControl.beforeBuyApi.name = beforeBuyApi.optString("name", null);
                tbDetailResultVO.itemControl.unitControl.beforeBuyApi.value = beforeBuyApi.optString("value", null);
            }

            JSONObject beforeCartApi = unitControl.optJSONObject("beforeCartApi");
            if (beforeCartApi != null) {
                tbDetailResultVO.itemControl.unitControl.beforeCartApi = new Unit();
                tbDetailResultVO.itemControl.unitControl.beforeCartApi.name = beforeCartApi.optString("name", null);
                tbDetailResultVO.itemControl.unitControl.beforeCartApi.value = beforeCartApi.optString("value", null);
            }

        }
    }


    /**
     * 运营定义的 文案 提示区域
     *
     * @param tbDetailResultVO
     * @param tips
     */
    private static void resolve_Tips(TBDetailResultVO tbDetailResultVO, JSONObject tips) {
        tbDetailResultVO.tips = new HashMap<String, List<TipDO>>();
        Iterator<?> it = tips.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            if (!TextUtils.isEmpty(key)) {
                JSONArray tip_list_json = tips.optJSONArray(key);
                if (tip_list_json != null && tip_list_json.length() > 0) {
                    List<TipDO> tipdo_list = new ArrayList<TipDO>();
                    int length = tip_list_json.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject tip_json = tip_list_json.optJSONObject(i);
                        if (tip_json != null) {
                            TipDO tipDO = new TipDO();
                            tipDO.html = tip_json.optString("html", null);
                            tipDO.logo = tip_json.optString("logo", null);
                            tipDO.txt = tip_json.optString("txt", null);
                            tipDO.url = tip_json.optString("url", null);
                            tipdo_list.add(tipDO);
                        }
                        tbDetailResultVO.tips.put(key, tipdo_list);
                    }
                }
            }
        }
    }


    /**
     * @param guarantees
     * @return
     */

    private static List<Guarantee> resolve_Guarantees(JSONArray guarantees) {
        List<Guarantee> guarantees_list = new ArrayList<Guarantee>();
        int length = guarantees.length();
        for (int i = 0; i < length; i++) {
            JSONObject guarantee_obj = guarantees.optJSONObject(i);
            if (guarantee_obj != null) {
                Guarantee guarantee = new Guarantee();
                guarantee.icon = guarantee_obj.optString("icon", null);
                guarantee.title = guarantee_obj.optString("title", null);

                JSONObject actionUrl = guarantee_obj.optJSONObject("actionUrl");
                if (actionUrl != null) {
                    guarantee.actionUrl = new Unit();
                    guarantee.actionUrl.name = actionUrl.optString("name", null);
                    guarantee.actionUrl.value = actionUrl.optString("value", null);
                }
                guarantees_list.add(guarantee);
            }
        }
        return guarantees_list;
    }

    /**
     * @param priceUnits
     * @return
     */
    private static List<PriceUnit> resolve_PriceUnit(JSONArray priceUnits) {
        if (priceUnits != null && priceUnits.length() > 0) {
            List<PriceUnit> priceUnits_list = new ArrayList<PriceUnit>();
            int length = priceUnits.length();
            for (int i = 0; i < length; i++) {
                JSONObject price_obj = priceUnits.optJSONObject(i);
                if (price_obj != null) {
                    PriceUnit priceUnit = new PriceUnit();
                    priceUnit.display = price_obj.optInt("display");
                    priceUnit.name = price_obj.optString("name", null);
                    priceUnit.preName = price_obj.optString("preName", null);
                    priceUnit.prePayName = price_obj.optString("prePayName", null);
                    priceUnit.prePayPrice = price_obj.optString("prePayPrice", null);
                    priceUnit.price = price_obj.optString("price", null);
                    priceUnit.priceTitle = price_obj.optString("priceTitle", null);

                    //                    priceUnit.priceCss;
                    //                    priceUnit.rangePriceCss;
                    //                    priceUnit.tips;
                    //                    priceUnit.tips2;

                    priceUnits_list.add(priceUnit);
                }
            }

            return priceUnits_list;
        }
        return null;
    }

    /**
     * 解析SKU中属性值
     *
     * @param skuProps
     * @return
     */
    private static List<SkuProp> resolve_SkuModel_skuProps(JSONArray skuProps) {

        if (skuProps != null && skuProps.length() > 0) {
            List<SkuProp> skuProps_List = new ArrayList<SkuProp>();
            int length = skuProps.length();
            for (int i = 0; i < length; i++) {
                JSONObject obj = skuProps.optJSONObject(i);
                if (obj != null) {
                    SkuProp skuProp = new SkuProp();
                    skuProp.propId = obj.optLong("propId");
                    skuProp.propName = obj.optString("propName", null);
                    JSONArray values = obj.optJSONArray("values");
                    if (values != null && values.length() > 0) {
                        skuProp.values = new ArrayList<SkuPropValue>();
                        int len = values.length();
                        for (int k = 0; k < len; k++) {
                            JSONObject obj_value = values.optJSONObject(k);
                            if (obj_value != null) {
                                SkuPropValue skuPropValue = new SkuPropValue();
                                skuPropValue.valueId = obj_value.optLong("valueId");
                                skuPropValue.name = obj_value.optString("name", null);
                                skuPropValue.imgUrl = obj_value.optString("imgUrl", null);
                                skuPropValue.propId = obj_value.optString("propId", null);
                                skuPropValue.valueAlias = obj_value.optString("valueAlias", null);
                                skuProp.values.add(skuPropValue);
                            }
                        }
                    }
                    skuProps_List.add(skuProp);
                }
            }
            return skuProps_List;
        }
        return null;
    }


    /**
     * 把JSON格式转换为MAP格式( string )
     *
     * @param obj_map
     * @return
     */
    private static Map<String, String> resolveToMap_String(JSONObject obj_map) {
        if (obj_map != null) {
            Map<String, String> json_map = new HashMap<String, String>();
            Iterator<?> it = obj_map.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value = (String) obj_map.opt(key);
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    json_map.put(key, value);
                }
            }
            return json_map;
        }
        return null;
    }

    private static void resolve_resource1(TBDetailResultVO_v6 tbDetailResultVO, JSONObject resources) {
        TBDetailResultVO_v6.Resource resource = new TBDetailResultVO_v6.Resource();
        JSONObject entrancesModel = resources.optJSONObject("entrances");
        if (entrancesModel != null) {
            resolve_entrances(resource, entrancesModel);
            tbDetailResultVO.setResource(resource);
        }
    }

    private static void resolve_entrances(TBDetailResultVO_v6.Resource resource, JSONObject entrancesObj) {
        Entrances entrances = new Entrances();
        JSONObject couponModel = entrancesObj.optJSONObject("coupon");
        if (couponModel != null) {
            resolve_double11Coupon(entrances, couponModel);
            resource.setEntrances(entrances);
        }
    }

    private static void resolve_double11Coupon(Entrances entrances, JSONObject double11CouponObj) {
        Coupon double11Coupon = new Coupon();
        double11Coupon.setIcon(double11CouponObj.optString("icon"));
        double11Coupon.setLink(double11CouponObj.optString("link"));
        double11Coupon.setText(double11CouponObj.optString("text"));
        double11Coupon.setLinkText(double11CouponObj.optString("linkText"));
        entrances.setCoupon(double11Coupon);
    }

}
