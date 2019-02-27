package com.yunos.tvtaobao.zhuanti.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Created by chenjiajuan on 17/4/25.
 */

public class SharePreUtil {
    private static SharedPreferences sp;
    private static final String RECORDS_NAME = "recharge_records";
    private static final String RECORDS_KEY = "number";
    private static final String RECORDS_KEY_DEFAULT = "nothing";
    private static final String RECORDS_SYSTEM = "system";
    private static final String TAG = "SharePreUtil";
    public static final long RECORDS_SYSTEM_DEFAULT= -1;
    //设置该页面本地缓存的清除时间（毫秒）：10分钟
    private static long mClearTime=600000;

    private static  final String RECORDS_TAOKE="TvBuytaoke";
    private static  final String RECORDS_TAOKE_TIME="taoketime";
    private static  final  long RECORDS_TAOKE_TIME_DEFAULT=-1;


    public static void saveTvBuyTaoKe(Context context, long time){
        sp=context.getSharedPreferences(RECORDS_TAOKE,0);
        sp.edit().putLong(RECORDS_TAOKE_TIME,time).commit();

    }

    public static long getTaoKeLogin(Context context){
        sp=context.getSharedPreferences(RECORDS_TAOKE,0);
        long histotyTime=sp.getLong(RECORDS_TAOKE_TIME,RECORDS_TAOKE_TIME_DEFAULT);
        return  histotyTime;
    }

    /**
     * 保存领取过的积分
     * @param id  传入积分id
     */
    public static void saveHistoryIntegration(Context context, String id){
        sp = context.getSharedPreferences(RECORDS_NAME, 0);
        String history = sp.getString(RECORDS_KEY, RECORDS_KEY_DEFAULT);
        StringTokenizer st = new StringTokenizer(history, ",");
        int count = st.countTokens();
        for (int i = 0; i < count; i++) {
            String str = st.nextToken();
            if (id.equals(str)) {
                break;
            }
            StringBuilder sb = new StringBuilder(history);
            sb.insert(0, id + ",");
            sp.edit().putString(RECORDS_KEY, sb.toString()).commit();
            long system= System.currentTimeMillis();
            sp.edit().putLong(RECORDS_SYSTEM,system).commit();
        }
        AppDebug.e(TAG,"saveHistoryIntegration = "+getHistoryIntegrationRecord(context));
    }

    /**
     * 获取已领取过的积分列表
     * @return
     */
    public static List<String> getHistoryIntegrationRecord(Context context) {
        List<String> hisArrays = new ArrayList<String>();
        sp = context.getSharedPreferences(RECORDS_NAME, 0);
        //先判断有没有过期
        long  system=sp.getLong(RECORDS_SYSTEM,RECORDS_SYSTEM_DEFAULT);
        AppDebug.e(TAG,"system1 = "+system);
        if (system!=-1){
            system+=mClearTime;
            AppDebug.e(TAG,"system2 = "+system);
        }
        long nowTime= System.currentTimeMillis();
        AppDebug.e(TAG," nowTime = "+TimeUtil.timeStamp2Date(String.valueOf(nowTime/1000),null));
        if (system<=nowTime){
            AppDebug.e(TAG,"清除数据历史记录");
            clearHistoryRechargeRecords(context);
            return null;
        }
        String history = sp.getString(RECORDS_KEY, RECORDS_KEY_DEFAULT);
        StringTokenizer st = new StringTokenizer(history, ",");
        int count = st.countTokens();
        for (int i = 0; i < count; i++) {
            String str = st.nextToken();
            if (!str.equals(RECORDS_KEY_DEFAULT)) {
                hisArrays.add(str);
            }
        }
        AppDebug.e(TAG,"getHistoryIntegrationRecord  hisArrays = "+hisArrays.toString());
        return hisArrays;
    }

    /**
     * 清除历史积分记录
     */
    public static void clearHistoryRechargeRecords(Context context) {
        sp = context.getSharedPreferences(RECORDS_NAME, 0);
        sp.edit().clear().commit();
    }

}
