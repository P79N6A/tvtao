package com.tvtaobao.voicesdk;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.tvtaobao.voicesdk.control.GoodsSearchControl;
import com.tvtaobao.voicesdk.control.TakeOutSearchControl;
import com.tvtaobao.voicesdk.control.bo.ConfigVO;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.request.ASRUTRequest;
import com.tvtaobao.voicesdk.type.ShoppingType;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LocalUnderstand;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ASRInput extends NLPDeal {
    private static final String TAG = "ASRInput";
    private static ASRInput asrInput;

    private VoiceHandler mHandler;
    private ASRNotify mNotify;

    private final static int TYPE_MESSAGE_JSON = 0;
    private final static int TYPE_MESSAGE_ASR = 1;
    private final static int TYPE_MESSAGE_TVTAOSDK = 2;
    private final static int TYPE_MESSAGE_TVTAOMSG = 3;

    public static ASRInput getInstance() {
        if (asrInput == null) {
            synchronized (ASRInput.class) {
                if (asrInput == null) {
                    asrInput = new ASRInput();
                }
            }
        }

        return asrInput;
    }

    private ASRInput() {
        configVO = new ConfigVO();
        mHandler = new VoiceHandler();
        mNotify = ASRNotify.getInstance();
    }

    public void destroy() {

    }

    public void setContext(Service service) {
        mWeakService = new WeakReference<>(service);
    }

    /**
     * 暴风传过来的数据
     *
     * @param s
     * @param s1
     * @param listener
     */
    public void handleInput(String s, String s1, VoiceListener listener) {
        LogPrint.e(TAG, "handleInput s : " + s + " ,s1 : " + s1);
        Bundle bundle = new Bundle();
        bundle.putString("asr", s);
        bundle.putString("json", s1);
        Message msg = new Message();
        msg.setData(bundle);
        if (s1 != null && !s1.equals("null")) {
            msg.what = TYPE_MESSAGE_JSON;
        } else if (s != null && !s.equals("null")) {
            msg.what = TYPE_MESSAGE_ASR;
        }

        mHandler.sendMessage(msg);
        mWeakListener = new WeakReference<>(listener);
    }

    /**
     * tvtaosdk传过来的数据
     * 语音助手传过来的消息处理
     *
     * @param aString
     * @return
     */
    public void setMessage(String aString, VoiceListener listener) {
        LogPrint.e(TAG, "setMessage aString : " + aString);

        mHandler.sendMessage(mHandler.obtainMessage(TYPE_MESSAGE_TVTAOMSG, aString));
        mWeakListener = new WeakReference<>(listener);
    }

    /**
     * tvtaosdk传过来的数据
     * 语音助手传过来的消息处理
     *
     * @param asr_text
     * @param json
     * @return
     */
    public void setMessage(String asr_text, String searchConfig, String json, VoiceListener listener) {
        LogPrint.i(TAG, "sendMessage searchConfig : " + searchConfig);
        LogPrint.e(TAG, "sendMessage asr : " + asr_text + " ,json : " + json);

        Bundle bundle = new Bundle();
        bundle.putString("asr", asr_text);
        bundle.putString("json", json);
        bundle.putString("searchConfig", searchConfig);
        Message msg = new Message();
        msg.setData(bundle);
        msg.what = TYPE_MESSAGE_TVTAOSDK;

        mHandler.sendMessage(msg);
        mWeakListener = new WeakReference<>(listener);
    }

    private static class VoiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogPrint.e(TAG, "Handler msg : " + msg);
            String asr = msg.getData().getString("asr");
            switch (msg.what) {
                case TYPE_MESSAGE_ASR:
                    asrInput.dealASR(asr);
                    break;
                case TYPE_MESSAGE_JSON:
                    String json1 = msg.getData().getString("json");
                    asrInput.dealBaoFengJSON(asr, json1);
                    break;
                case TYPE_MESSAGE_TVTAOSDK:
                    Bundle bundle = msg.getData();
                    String json2 = bundle.getString("json");
                    String search = bundle.getString("searchConfig");
                    asrInput.dealTVTaoJson(asr, json2, search);
                    break;
                case TYPE_MESSAGE_TVTAOMSG:
                    String aString = (String) msg.obj;
                    LogPrint.i(TAG, "VoiceHandler aString : " + aString);
                default:
                    break;
            }
        }
    }

    /**
     * 处理TVTaoSDK传过来的json数据
     * 根据type类型，去进行不同的操作
     *
     * @param json
     * @return
     */
    private void dealTVTaoJson(String asr, String json, String search) {
        LogPrint.e(TAG, "dealTVTaoJson searchConfig : " + search);
        configVO.searchConfig = SearchObject.resolverData(search);
        if (TextUtils.isEmpty(json)) {
            dealASR(asr);
            return;
        }

        try {
            JSONObject object = new JSONObject(json);
            int type = object.getInt("type");
            LogPrint.e(TAG, "sendMessage type is " + type);
            switch (type) {
                case ShoppingType.TYPE_SDK_INIT:
//                    Config.setChannel(appkey);
                    SDKInitConfig.init(object);

                    SharePreferences.put("sdkInit_Str", object.toString());
                    break;
                case ShoppingType.TYPE_SHOPPING_SEARCH:
                    GoodsSearchControl control = new GoodsSearchControl();
                    control.init(mWeakService, mWeakListener);
                    control.setSearchConfig(configVO.searchConfig);
                    control.requestSearch();

                    recordVoiceToService("system", configVO.searchConfig.keyword);
                    break;
                case ShoppingType.TYPE_SHOPPING_TOBUY:
                    String quickCreateOrder = "tvtaobao://home?app=voice&module=createorder&itemId=" + object.getString("itemId") + "&from=voice_system&notshowloading=true";
                    gotoActivity(quickCreateOrder);

                    recordVoiceToService("system", "buy_index");
                    break;
                case ShoppingType.TYPE_SHOPPING_TODETAIL:
                    String toDetail = "tvtaobao://home?module=detail&itemId=" + object.getString("itemId") + "&from=voice_system&notshowloading=true";
                    gotoActivity(toDetail);

                    recordVoiceToService("system", "see_index");
                    break;
                case ShoppingType.TYPE_SHOPPING_ADDCART:
                    addCart(object.getString("itemId"));
                    break;
                case ShoppingType.TYPE_SHOPPING_COLLECT:
                    manageFav(object.getString("itemId"));
                    break;
                case ShoppingType.TYPE_SHOPPING_TAKEOUT:
                    TakeOutSearchControl takeOutSearchControl = new TakeOutSearchControl();
                    takeOutSearchControl.setConfig(configVO);
                    takeOutSearchControl.init(mWeakService, mWeakListener);
                    String keywords = object.getString("keywords");
                    int pageNo = object.getInt("pageNo");
                    int pageSize = object.getInt("pageSize");
                    String orderType = object.getString("orderType");
                    takeOutSearchControl.takeoutSearch(keywords, pageNo, pageSize, orderType);
                    break;
                case ShoppingType.TYPE_SHOPPING_TOTAKEOUTSHOPHOME:
                    String takeoutshophome = "tvtaobao://home?module=takeouthome&shopId=" + object.getString("shopId") + "&v_from=voice_system&notshowloading=true";
                    if (mWeakService != null && mWeakService.get() != null) {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(takeoutshophome));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mWeakService.get().startActivity(intent);

                        DialogManager.getManager().dismissAllDialog();
                    }
                    recordVoiceToService("system", "to_takeout_home");
                    break;
                case ShoppingType.TYPE_INIT_SHOW_TAKEOUT_TIPS:
                    boolean needTakeOutTips = object.getBoolean("needTakeOutTips");
                    SDKInitConfig.setNeedTakeOutTips(needTakeOutTips);
                    break;
                case ShoppingType.TYPE_INIT_SHOW_TVTAO_SEARCH:
                    boolean needTVTaobaoSearch = object.getBoolean("needTVTaobaoSearch");
                    SDKInitConfig.setNeedTVTaobaoSearch(needTVTaobaoSearch);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void dealBaoFengJSON(String asr, String json) {
        try {
            if(TextUtils.isEmpty(json)){
                json = "{}"; // 为了不影响后续逻辑，这里直接给个可转换成空JSON对象的字符串
            }
            JSONObject object = new JSONObject(json);
            LogPrint.e(TAG, "Handler object : " + object.toString());
//            if (object.has("help_cmd")) {
//                JSONObject help_cmd = object.getJSONObject("help_cmd");
//                String action = help_cmd.getString("action");
//                String _asr = null;
//                if (action.equals("buyNow") || action.equals("clickOk")) {
//                    _asr = "立即购买";
//                }
//                if (mNotify.isAction(_asr)) {
//                    return;
//                }
//            }

            if (object.has("PRICE") || object.has("SellNum")) {
                String good = object.getJSONArray("Good").getString(0);

                if (object.has("PRICE")) {
                    Utils.utCustomHit("Voice_search_screen_price", getProperties(asr));

                    configVO.priceScope = object.getJSONArray("PRICE").getString(0);
                }

                if (object.has("SellNum")) {
                    Utils.utCustomHit("Voice_search_screen_sales", getProperties(asr));

                    configVO.saleSorting = object.getJSONArray("SellNum").getString(0);
                }
                LogPrint.e(TAG, "asrJson: " + good);
                nlpRequest(good);
                return;
            }

            if (object.has("tips")) {
                Map<String, String> map = getProperties(asr);
                map.put("screencase", object.getString("tips"));
                Utils.utCustomHit("Voice_search_screen", map);

                SearchObject searchConfig = new SearchObject();
                searchConfig.keyword = object.getString("tips");
                GoodsSearchControl control = new GoodsSearchControl();
                control.init(mWeakService, mWeakListener);
                control.setConfig(configVO);
                control.setSearchConfig(searchConfig);
                control.requestSearch();

                recordVoiceToService("system", searchConfig.keyword);
                return;
            }

            if (object.has("uri")) {
                String uri = object.getString("uri");
                resolverUri(uri);
                return;
            }

            dealASR(asr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dealASR(String asr_text) {
        LogPrint.e(TAG, "dealASR asr : " + asr_text);
        if (TextUtils.isEmpty(asr_text)) {
            return;
        }

        Utils.utCustomHit("Voice_asr", getProperties(asr_text));

        try {
            if (LocalUnderstand.isContain(asr_text, ".*(退出)(电视淘宝|淘宝|应用)*")
                    || (ActivityUtil.isTopActivity(mWeakService.get(), Class.forName("com.yunos.tvtaobao.homebundle.activity.HomeActivity"))
                    && LocalUnderstand.isContain(asr_text, ".*([首|主]页)"))) {
                LogPrint.e(TAG, "dealASR Exit TVTaobao!");
                alreadyDeal("欢迎下次光临电视淘宝～");

                Intent intent = new Intent();
                intent.setAction("com.yunos.tvtaobao.exit.application");
                mWeakService.get().sendOrderedBroadcast(intent, null);
                return;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        nlpRequest(asr_text);
    }

    /**
     * 解析uri
     *
     * @param uri
     */
    private void resolverUri(String uri) {
        if (uri == null)
            return;

        //uri是主模块的话，直接去跳转
        if (uri.startsWith("tvtaobao://home")) {
            gotoActivity(uri);
        }

        //uri是slideshow模块,直接去跳转
        if (uri.startsWith("tvtaobao://slideshow")) {
            gotoActivity(uri);
        }

        //加入购物车
        if (uri.startsWith("tvtaobao://addcart")) {

            Utils.utCustomHit("Voice_search_cart_add", getProperties());

            Bundle bundle = decodeUri(Uri.parse(uri));
            addCart(bundle.getString("itemId"));
        }

        //收藏
        if (uri.startsWith("tvtaobao://collection")) {

            Utils.utCustomHit("Voice_search_collect", getProperties());

            Bundle bundle = decodeUri(Uri.parse(uri));
            manageFav(bundle.getString("itemId"));
        }

        if (uri.startsWith("tvtaobao://voice")) {
            gotoActivity(uri);
        }
    }

    /**
     * 解析Uri的数据
     *
     * @param uri
     * @return
     */
    private Bundle decodeUri(Uri uri) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        Set<String> params = uri.getQueryParameterNames();
        for (String key : params) {
            bundle.putString(key, uri.getQueryParameter(key));
        }

        return bundle;
    }

    /**
     * 记录语音数据到服务端
     * @param referrer
     * @param asr
     */
    private void recordVoiceToService(String referrer, String asr) {
        BusinessRequest.getBusinessRequest().baseRequest(new ASRUTRequest(asr, referrer), null, false);
    }
}
