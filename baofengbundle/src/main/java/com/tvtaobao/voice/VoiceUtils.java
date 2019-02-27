package com.tvtaobao.voice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.bftv.fui.thirdparty.VoiceFeedback;
import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voice.services.BftvAsrService;
import com.tvtaobao.voice.services.VoiceListener;
import com.tvtaobao.voice.utils.SearchControl;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.voice.ASRNotify;
import com.yunos.voice.Do.JinnangDo;
import com.yunos.voice.Do.NlpDO;
import com.yunos.voice.Do.PageReturn;
import com.yunos.voice.Do.ProductDo;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.yunos.voice.interfaces.VoiceCallBack;
import com.tvtaobao.voicesdk.request.CheckBillRequest;
import com.tvtaobao.voicesdk.request.CheckOrderRequest;
import com.yunos.voice.request.NlpRequest;
import com.tvtaobao.voicesdk.request.VoiceSearch;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.voice.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pan on 2017/8/2.
 */

public class VoiceUtils {
    private static final String TAG = "VoiceUtils";
    private static VoiceUtils mVoice;
    private WeakReference<VoiceListener> mWeakListener;
    private WeakReference<BftvAsrService> mWeakService;
    private VoiceHandler mHandler;

    private final static int TYPE_MESSAGE_ASR = 0;
    private final static int TYPE_MESSAGE_JSON = 1;
    private final static int TYPE_MESSAGE_OTHER = 2;

    private ASRNotify mNotify;

    public static VoiceUtils getInstance() {
        if (mVoice == null) {
            synchronized (VoiceUtils.class) {
                if (mVoice == null)
                    mVoice = new VoiceUtils();
            }
        }
        return mVoice;
    }

    private VoiceUtils() {
        mNotify = ASRNotify.getInstance();
        mNotify.setVoiceCallBack(new VoiceCallBack() {
            @Override
            public void onTTS(boolean mIsHandle, String msg, List<String> tips) {
                VoiceUtils.this.onTTS(msg, tips);
            }

            @Override
            public void onSearch(SearchObject search) {

            }
        });
        mHandler = new VoiceHandler();
    }

    /**
     * 设置一个service对象，用来进行intent
     * @param context
     */
    public void setContext(BftvAsrService context) {
        mWeakService = new WeakReference<BftvAsrService>(context);
    }

    /**
     * 与暴风AIDL通信之后，通过handler将线程放入主线程去做操作。
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
        } else {
            msg.what = TYPE_MESSAGE_OTHER;
        }
        mHandler.sendMessage(msg);
        mWeakListener = new WeakReference<>(listener);
    }

    private String asr;
    private static class VoiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogPrint.e(TAG, "Handler msg : " + msg);
            mVoice.asr = msg.getData().getString("asr");
            Utils.utCustomHit("Voice_asr", mVoice.getProperties());
            switch (msg.what) {
                case TYPE_MESSAGE_ASR:
                    mVoice.dealASR(mVoice.asr);
                    break;
                case TYPE_MESSAGE_JSON:
                    String json = msg.getData().getString("json");
                    mVoice.dealJSON(mVoice.asr, json);
                    break;
                default:
                    break;
            }
        }
    }

    //处理ASR
    private void dealASR(String content) {
        if (TextUtils.isEmpty(content))
            return;

        PageReturn pageReturn = mNotify.isAction(content);
        if (pageReturn != null && pageReturn.isHandler) {
            return;
        }

        if (content.contains("花了多少钱") && !content.contains("淘宝")) {
            content = content.replace("花了多少钱", "淘宝花了多少钱");
        }
        mVoice.onNlpRequest(content);
    }

    String priceScope;
    String saleSorting;
    private void dealJSON(String asr,String json) {
        try {
            JSONObject object = new JSONObject(json);
            LogPrint.e(TAG, "Handler object : " + object.toString());

            if (object.has("PRICE") || object.has("SellNum")) {
                String good = object.getJSONArray("Good").getString(0);

                if (object.has("PRICE")) {
                    Utils.utCustomHit("Voice_search_screen_price", getProperties());

                    priceScope = object.getJSONArray("PRICE").getString(0);
                }

                if (object.has("SellNum")) {
                    Utils.utCustomHit("Voice_search_screen_sales", getProperties());

                    saleSorting = object.getJSONArray("SellNum").getString(0);
                }

                BusinessRequest.getBusinessRequest().baseRequest(new NlpRequest(good), new NlpListener(asr, priceScope, saleSorting), false);
                return;
            }

            if (object.has("tips")) {
                Map<String, String> map = getProperties();
                map.put("screencase", object.getString("tips"));
                Utils.utCustomHit("Voice_search_screen", map);

                onSearchRequest(object.getString("tips"), null, null);
                return;
            }

            if (object.has("uri")) {
                String uri = object.getString("uri");
                resolverUri(uri);
                return;
            }

            dealASR(asr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求idst的语义理解能力
     * @param asr_text
     */
    private void onNlpRequest(String asr_text) {
        BusinessRequest.getBusinessRequest().baseRequest(new NlpRequest(asr_text), new NlpListener(asr_text, null, null), false);
    }

    /**
     * 去搜索请求
     * @param words
     */
    private void onSearchRequest(String words, String price, String sale) {
        LogPrint.e(TAG, "gotoSearchRequest words : " + words);
        if (TextUtils.isEmpty(words)) {
            onRequestFailure("不好意思，你想买什么呢");
            return;
        }
        BusinessRequest.getBusinessRequest().baseRequest(new VoiceSearch(words, 0, 30, true, false, price, sale), new SearchListener(price, sale), false);
    }

    /**
     * 解析uri
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
            dealVoiceIntent(uri);
        }
    }

    /**
     * 处理远场语音uri的一些跳转逻辑
     * @param uri
     */
    private void dealVoiceIntent(String uri) {
        LogPrint.e(TAG, TAG + ".dealVoiceIntent uri : " + uri);
        Bundle bundle = decodeUri(Uri.parse(uri));
        String skuSize = bundle.getString("skuSize");
        String itemId = bundle.getString("itemId");
        String presale = bundle.getString("presale");

        Map<String, String> map = getProperties();
        map.put("itemId", itemId);
        Utils.utCustomHit("Voice_search_buy", map);

        if ((skuSize != null && Integer.parseInt(skuSize) > 1) || (presale != null && presale.equals("true"))) {
            gotoSkuActivity(itemId);
            return;
        }

        gotoActivity(uri);
    }

    /**
     * 解析Uri的数据
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
     * 进行Activity
     * @param uri
     */
    private void gotoActivity(String uri) {
        if (mWeakService != null && mWeakService.get() != null) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(uri));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            mWeakService.get().startActivity(intent);
        }
    }

    /**
     * 当skuSize大于0时，直接跳转到SkuActivity
     * @param itemId
     */
    private void gotoSkuActivity(String itemId) {
        if (mWeakService != null && mWeakService.get() != null) {
            Intent intent = new Intent();
            intent.setData(Uri.parse("tvtaobao://home?module=sureJoin&itemId=" + itemId));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            mWeakService.get().startActivity(intent);
        }
    }

    /**
     * 加入购物车请求
     * @param itemId
     */
    private void addCart(String itemId) {
        BusinessRequest.getBusinessRequest().addBag(itemId, 1, null, "", new AddCartListener());
    }

    private void manageFav(String itemId) {
        BusinessRequest.getBusinessRequest().manageFav(itemId, "addAuction", new ManageFavListener());
    }

    /**
     * 搜索请求监听
     */
    class SearchListener implements RequestListener<JSONObject> {

        private String p;
        private String s;
        public SearchListener(String price, String sale) {
            this.p = price;
            this.s = sale;
        }

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".SearchResponse resultCode : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                LogPrint.e(TAG, TAG + ".SearchResponse SUCCESS");
                try {
                    String keyword = data.getString("keyword");
                    List<ProductDo> mProducts = new ArrayList<>();
                    if (data.has("model")) {
                        JSONArray model = data.getJSONArray("model");
                        LogPrint.e(TAG, TAG + ".SearchResponse size : " + model.length());
                        for (int i = 0; i < model.length(); i++) {
                            mProducts.add(GsonUtil.parseJson(model.getJSONObject(i).toString(), new TypeToken<ProductDo>(){}));
                        }
                    }

                    List<JinnangDo> mJinnangs = new ArrayList<>();
                    if (data.has("jinNangItems")) {
                        JSONArray jinnang = data.getJSONArray("jinNangItems");
                        LogPrint.w(TAG, TAG + ".SearchResponse jinnang.size : " + jinnang.length());
                        for (int i = 0 ; i < jinnang.length() ; i++) {
                            mJinnangs.add(JinnangDo.resolverData(jinnang.getJSONObject(i)));
                        }
                    }

                    if (mProducts.size() > 0) {
                        onSearchSuccess(keyword, mProducts, mJinnangs, p, s);
                    } else {
                        List<String> tips = new ArrayList<>();
                        tips.add("说出您想买的，试着说“我想买矿泉水”");
                        onTTS("没有找到与 " + keyword + " 相关的宝贝", tips);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                LogPrint.e(TAG, TAG + ".SearchResponse FAILURE    msg : " + msg);
                onRequestFailure(msg);
            }
        }
    }

    /**
     * 搜索成功，进行数据处理，让暴风中间层进行展示。
     * @param productDos
     */
    private void onSearchSuccess(String words, List<ProductDo> productDos, List<JinnangDo> jinnangDos, String price, String sale) {
        VoiceFeedback voiceFb = new VoiceFeedback();
        voiceFb.isHasResult = true;
        voiceFb.listMiddleData = new ArrayList<>();
        voiceFb.listTips = new ArrayList<>();

        if (!TextUtils.isEmpty(price) && TextUtils.isEmpty(sale)) {
            String[] pricescope = price.split("~");
            DecimalFormat df = new DecimalFormat("#.##");
            String startPrice = df.format(Double.parseDouble(pricescope[0]));
            String endPrice = df.format(Double.parseDouble(pricescope[1]));
            if (startPrice.startsWith("0")) {
                voiceFb.feedback = "为您找到商品价格小于" + endPrice + "元的" + words;
            } else if (!startPrice.startsWith("0") && !price.endsWith("9999999.00")) {
                voiceFb.feedback = "为您找到商品价格" + startPrice + "元到" + endPrice + "元之间的" + words;
            } else if (endPrice.endsWith("9999999")) {
                voiceFb.feedback = "为您找到商品价格大于" + startPrice + "元的" + words;
            }
        } else if (TextUtils.isEmpty(price) && !TextUtils.isEmpty(sale)) {
            if (sale.equals("low")) {
                voiceFb.feedback = "已为您按照销量从低到高进行排序";
            } else {
                voiceFb.feedback = "已为您按照销量从高到低进行排序";
            }
        } else if (!TextUtils.isEmpty(price) && !TextUtils.isEmpty(sale)) {
            voiceFb.feedback = "已根据您的搜索条件，搜索到以下几款" + words + ",您想买第几个？";
        } else {
            voiceFb.feedback = "为您推荐以下几款" + words + "，您想买第几个？";
        }
        
        voiceFb.listPrompts = new ArrayList<>();
        if (jinnangDos.size() > 0) {
            String jinnang = jinnangDos.get(5).getName();  //TODO 产品郭阳阳要求一定要是第6个，我解释过了，有问题我不管。目的是为了提示正好对应上面第6个，用户可以看到
            voiceFb.listPrompts.add("想要" + jinnang + "的？可以对我说“" + jinnang + "”");
        }
        voiceFb.listPrompts.add("想看第三个商品的详情，可以对我说“看看第三个”");
        voiceFb.listPrompts.add("没有喜欢的？可以对我说“换一批”");
        voiceFb.type = VoiceFeedback.TYPE_MIDDLE;
        for (int i = 0 ; i < productDos.size() ; i++) {
            voiceFb.listMiddleData.add(SearchControl.makeProduct(productDos.get(i)));
        }

        for (int j = 0; j < jinnangDos.size(); j++) {
            voiceFb.listTips.add(SearchControl.makeJinNang(jinnangDos.get(j)));
        }

        LogPrint.w(TAG, TAG + ".WeakListener : " + mWeakListener + " ,VoiceListener : " + mWeakListener.get());
        LogPrint.w(TAG, TAG + ".VoiceFeedback : " + voiceFb.toString());
        if (mWeakListener != null && mWeakListener.get() != null)
            mWeakListener.get().onVoiceResult(voiceFb);
    }

    /**
     * 请求失败，返回错误信息，让大耳朵提示错误原因。
     * @param msg
     */
    private void onRequestFailure(String msg) {
        VoiceFeedback voiceFb = new VoiceFeedback();
        voiceFb.isHasResult = false;
        voiceFb.feedback = msg;
        voiceFb.type = VoiceFeedback.TYPE_CMD;

        LogPrint.w(TAG, TAG + ".WeakListener : " + mWeakListener + " ,VoiceListener : " + mWeakListener.get());
        LogPrint.w(TAG, TAG + ".VoiceFeedback : " + voiceFb.toString());
        if (mWeakListener != null && mWeakListener.get() != null)
            mWeakListener.get().onVoiceResult(voiceFb);
    }

    /**
     * 返回错误信息，让大耳朵提示错误原因。
     * @param msg
     */
    private void onTTS(String msg, List<String> tips) {
        VoiceFeedback voiceFb = new VoiceFeedback();
        voiceFb.isHasResult = true;
        voiceFb.feedback = msg;
        voiceFb.type = VoiceFeedback.TYPE_CMD;
        voiceFb.listPrompts = tips;

        LogPrint.w(TAG, TAG + ".onTTS.WeakListener : " + mWeakListener + " ,VoiceListener : " + mWeakListener.get());
        if (mWeakListener != null && mWeakListener.get() != null)
            mWeakListener.get().onVoiceResult(voiceFb);
    }

    /**
     * 语义理解数据返回。
     */
    class NlpListener implements RequestListener<List<NlpDO>> {

        private String a;
        private String p;
        private String s;
        public NlpListener(String asr, String price, String sales) {
            this.a = asr;
            this.p = price;
            this.s = sales;
        }

        @Override
        public void onRequestDone(List<NlpDO> data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".NlpListener Code : " + resultCode + " ,msg : " + msg);

            if (resultCode == 200) {
                for (int i = 0; i < data.size(); i++) {
                    String domain = data.get(i).getDomain();
                    LogPrint.w(TAG, TAG + ".NlpListener domain : " + domain);

                    if (domain.equals("unknown"))
                        Utils.utCustomHit("Voice_asr_unknow", getProperties());

                    if (domain.equals("online_shopping")) {

                        NlpDO nlp = data.get(i);
                        if (nlp.getIntent().equals("search_goods")) {
                            String keyword = "";
                            for (int j = 0; j < nlp.getKeywords().size(); j++) {
                                keyword += nlp.getKeywords().get(j);
                            }
                            LogPrint.w(TAG, TAG + ".NlpListener brand " + nlp.getProduct_brand() + " ,keyword : " + keyword + " ,type : " + nlp.getProduct_type());
                            String key_search = nlp.getProduct_brand() + keyword + nlp.getProduct_type();
                            onSearchRequest(key_search, p, s);

                            Map<String, String> map = getProperties();
                            map.put("query", key_search);
                            Utils.utCustomHit("Voice_search", map);

                        } else if (nlp.getIntent().equals("check_bill")) { //消费查询

                            LogPrint.w(TAG, TAG + ".NlpListener check_bill start : " + nlp.getProduct_type());
                            LogPrint.w(TAG, TAG + ".NlpListener start : " + nlp.getTimeText());
                            LogPrint.w(TAG, TAG + ".NlpListener start : " + nlp.getBeginTime() + " ,end : " + nlp.getEndTime());

                            BusinessRequest.getBusinessRequest().baseRequest(new CheckBillRequest(nlp.getTimeText(), nlp.getBeginTime(), nlp.getEndTime(), "1.0"), new BillListener(nlp.getTimeText()), true);

                        } else if (nlp.getIntent().equals("check_order_state")) { //物流查询

                            LogPrint.w(TAG, TAG + ".NlpListener check_order_state start : " + nlp.getProduct_type());
                            LogPrint.w(TAG, TAG + ".NlpListener start : " + nlp.getTimeText());
                            LogPrint.w(TAG, TAG + ".NlpListener start : " + nlp.getBeginTime() + " ,end : " + nlp.getEndTime());

                            BusinessRequest.getBusinessRequest().baseRequest(new CheckOrderRequest(nlp.getTimeText(), nlp.getProduct_type(), nlp.getBeginTime(), nlp.getEndTime(), "1.0"), new LogisticsListener(nlp.getTimeText()), true);
                        }

                        return;
                    }
                }

                onRequestFailure("");
            } else {
                onTTS(msg, null);
            }
        }
    }

    /**
     * 登录监听
     */
    class LoginListener implements LoginHelper.SyncLoginListener {

        private String uri;
        public LoginListener(String uri) {
            this.uri = uri;
        }

        @Override
        public void onLogin(boolean isSuccess) {
            if (isSuccess) {
                resolverUri(uri);
            } else {
                onTTS("不好意思，登录失败了", null);
            }
            if (mWeakService != null && mWeakService.get() != null) {
                CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).addReceiveLoginListener(null);
            }
        }
    }

    /**
     * 加入购物车
     * @author pan
     * @data 2017年9月7日 下午10:33
     */
    class AddCartListener implements RequestListener<ArrayList<SearchResult>> {

        @Override
        public void onRequestDone(ArrayList<SearchResult> data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".AddCartListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                onTTS("加入购物车成功，您还想买什么？", null);
            } else {
                if (resultCode == 102 || resultCode == 104) {
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).startYunosAccountActivity(mWeakService.get(), false);
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).addReceiveLoginListener(new LoginListener(null));
                    msg = "请先打开手机淘宝扫码登陆，登陆后再试一下";
                }
                onTTS(msg, null);
            }
        }
    }

    /**
     * 加入收藏
     * @author pan
     * @data 2017年9月7日 下午10:33
     */
    class ManageFavListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".ManageFavListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                onTTS("加入收藏成功，您还想买什么？", null);
            } else {
                if (resultCode == 102 || resultCode == 104) {
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).startYunosAccountActivity(mWeakService.get(), false);
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).addReceiveLoginListener(new LoginListener(null));
                    msg = "请先打开手机淘宝扫码登陆，登陆后再试一下";
                }
                onTTS(msg, null);
            }
        }
    }

    class LogisticsListener implements RequestListener<JSONObject> {

        private String timeText;
        public LogisticsListener(String timeText) {
            this.timeText = timeText;
        }

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".LogisticsListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                try {
                    String tts = data.getString("tts");

                    Map<String, String> map = getProperties();
                    map.put("asr_time", timeText);
                    map.put("tts", tts);
                    Utils.utCustomHit("Voice_check_order_state", map);

                    if (data.has("tip")) {
                        String tip = data.getString("tip");
                        List<String> tips = new ArrayList<>();
                        tips.add(tip);
                        onTTS(tts, tips);
                    } else {
                        onTTS(tts, null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (resultCode == 102 || resultCode == 104) {
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).startYunosAccountActivity(mWeakService.get(), false);
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).addReceiveLoginListener(new LoginListener(null));
                    msg = "请先打开手机淘宝扫码登陆，登陆后再试一下";
                }
                onTTS(msg, null);
            }
        }
    }

    class BillListener implements RequestListener<JSONObject> {

        private String timeText;
        public BillListener(String timeText) {
            this.timeText = timeText;
        }

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".BillListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                try {
                    String tts = data.getString("tts");
                    if (data.has("tip")) {
                        String tip = data.getString("tip");
                        List<String> tips = new ArrayList<>();
                        tips.add(tip);
                        onTTS(tts, tips);
                    } else {
                        onTTS(tts, null);
                    }

                    Map<String, String> map = getProperties();
                    map.put("asr_time", timeText);
                    map.put("tts", tts);
                    Utils.utCustomHit("Voice_check_bill", map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (resultCode == 102 || resultCode == 104) {
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).startYunosAccountActivity(mWeakService.get(), false);
                    CoreApplication.getLoginHelper(mWeakService.get().getApplicationContext()).addReceiveLoginListener(new LoginListener(null));
                    msg = "请先打开手机淘宝扫码登陆，登陆后再试一下";
                }
                onTTS(msg, null);
            }
        }
    }

    /**
     * 获取统计最简的Properties
     * @return
     */
    private Map<String, String> getProperties() {
        Map<String, String> p = new HashMap<String, String>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }

        p.put("channel", Config.getChannel());

        if (!TextUtils.isEmpty(asr)) {
            p.put("asr", asr);
        }
        return p;
    }
}
