package com.yunos.tv.core.config;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GuoLiDong on 2018/12/24.
 * 下单支付等流程要用到的tvOption字段
 * 多个页面都会设置相关值，
 * 最终在下班支付的接口中会发送出去
 */
public class TvOptionsConfig {
    //只用来记录最近一次商品的下单
    //对购物车中的商品列表无效，购物车商品列表中的每一个商品都有对应的tvOptions
    private static String[] tvOptions = {"1","0","0","0","0","0"};
    //备选的TvOptions路径
    private static Map<TvOptionsChannel, String> mTvOptionMap;
    private static void initTvOptionMap() {
        if (mTvOptionMap == null) {
            mTvOptionMap = new HashMap();
        }
        mTvOptionMap.put(TvOptionsChannel.OTHER, "00");
        mTvOptionMap.put(TvOptionsChannel.SEARCH, "01");
        mTvOptionMap.put(TvOptionsChannel.RETRIEVE, "02");
        mTvOptionMap.put(TvOptionsChannel.TTTJ, "03");
        mTvOptionMap.put(TvOptionsChannel.TMCS, "04");
        mTvOptionMap.put(TvOptionsChannel.JHS, "05");
        mTvOptionMap.put(TvOptionsChannel.SNYG, "06");
        mTvOptionMap.put(TvOptionsChannel.TAO_QG, "07");
        mTvOptionMap.put(TvOptionsChannel.TMGJ, "08");
        mTvOptionMap.put(TvOptionsChannel.CZ_YK, "09");
        mTvOptionMap.put(TvOptionsChannel.CZ_HF, "10");
        mTvOptionMap.put(TvOptionsChannel.SPP, "11");
        mTvOptionMap.put(TvOptionsChannel.SP_HC, "12");
        mTvOptionMap.put(TvOptionsChannel.COLLECT, "13");
        mTvOptionMap.put(TvOptionsChannel.ORDER, "14");
        mTvOptionMap.put(TvOptionsChannel.HC, "15");
        mTvOptionMap.put(TvOptionsChannel.FZ_TRIP, "16");
        mTvOptionMap.put(TvOptionsChannel.GUESSLIKE, "17");
        mTvOptionMap.put(TvOptionsChannel.FAST_ORDER, "20");
        mTvOptionMap.put(TvOptionsChannel.VOICE_SEARCH_ORDER, "21");
        mTvOptionMap.put(TvOptionsChannel.VOICE_VIEW_MORE, "23");
        mTvOptionMap.put(TvOptionsChannel.FIND_SAME,"24");
    }
    static {
        initTvOptionMap();
    }

    public static String getTvOptions(){
        StringBuilder tvOptionsBuilder = new StringBuilder();
        for (String s:tvOptions){
            tvOptionsBuilder.append(s);
        }
        return String.valueOf(tvOptionsBuilder);
    }

    public static void setTvOptionsChannel(TvOptionsChannel channel){
        if (mTvOptionMap == null) {
            initTvOptionMap();
        }
        String channelCode = mTvOptionMap.get(channel);
        if(channelCode!=null){
            tvOptions[1] = String.valueOf(channelCode.charAt(0));
            tvOptions[2] = String.valueOf(channelCode.charAt(1));
            AppDebug.i("[tvOptionsDebug] setTvOptionsChannel ----> ",tvOptions[1]+tvOptions[2]);
        }
    }

    //频道
    public static void setTvOptionsChannel(String channelCode){
        if(channelCode.length() == 2){
            tvOptions[1] = String.valueOf(channelCode.charAt(0));
            tvOptions[2] = String.valueOf(channelCode.charAt(1));
            AppDebug.i("[tvOptionsDebug] setTvOptionsChannel ----> ",tvOptions[1]+tvOptions[2]);
        }
    }

    /*
     * 根据from字段判断是否具有语音
     * voice_system 语音，应用外
     * voice_application 语音，应用内
     */
    public static void setTvOptionVoiceSystem(String from) {
        if (TextUtils.isEmpty(from)) {
            tvOptions[3] = "0";
            tvOptions[4] = "0";
            AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","0");
            AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","0");
            return;
        }

        switch (from) {
            case "voice_system":
                tvOptions[3] = "1";
                tvOptions[4] = "1";
                AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","1");
                AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","1");
                break;
            //魔盒点击语音搜索结果
            case "tvspeech":
                tvOptions[3] = "1";
                tvOptions[4] = "1";
                AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","1");
                AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","1");
                break;
            case "voice_application":
                tvOptions[3] = "1";
                tvOptions[4] = "0";
                AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","1");
                AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","0");
                break;
            default:
                tvOptions[3] = "0";
                tvOptions[4] = "0";
                AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","0");
                AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","0");
        }

    }
    //是否具有语音
    public static void setTvOptionsVoice(boolean voice){
        if(voice){
            tvOptions[3] = "1";
            AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","1");
        }else {
            tvOptions[3] = "0";
            AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","0");
        }
    }

    //是否应用外
    public static void setTvOptionsSystem(boolean system){
        if(system){
            tvOptions[4] = "1";
            // 应用外一定有语音业务
            tvOptions[3] = "1";
            AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","1");
            AppDebug.i("[tvOptionsDebug] setTvOptionsVoice ----> ","1");
        }else {
            tvOptions[4] = "0";
            AppDebug.i("[tvOptionsDebug] setTvOptionsSystem ----> ","0");
        }

    }

    //是否有购物车
    public static void setTvOptionsCart(boolean fromShopCart){
        if(fromShopCart){
            tvOptions[5] = "1";
            AppDebug.i("[tvOptionsDebug] setTvOptionsCart ----> ","1");
        }else {
            tvOptions[5] = "0";
            AppDebug.i("[tvOptionsDebug] setTvOptionsCart ----> ","0");
        }

    }
}
