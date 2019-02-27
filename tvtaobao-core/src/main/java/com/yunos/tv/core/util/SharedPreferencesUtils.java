package com.yunos.tv.core.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by linmu on 2018/6/7.
 */

public class SharedPreferencesUtils {
    private static SharedPreferences sp;
    private static  final String RECORDS_TAOKE="TvBuytaoke";
    private static  final String RECORDS_TAOKE_BTOC="TvBuytaokeBtoc";
    private static  final String RECORDS_TAOKE_TIME="taoketime";
    private static  final String RECORDS_TAOKE_TIME_BTOC="taoketimebtoc";
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

    //淘客打点 btoc接口，一天只打一次
    public static void saveTaoKeBtoc(Context context, long time){
        sp=context.getSharedPreferences(RECORDS_TAOKE_BTOC,0);
        sp.edit().putLong(RECORDS_TAOKE_TIME_BTOC,time).commit();

    }
    //淘客打点 btoc接口，一天只打一次
    public static long getTaoKeBtoc(Context context){
        sp=context.getSharedPreferences(RECORDS_TAOKE_BTOC,0);
        long histotyTime=sp.getLong(RECORDS_TAOKE_TIME_BTOC,RECORDS_TAOKE_TIME_DEFAULT);
        return  histotyTime;
    }
}
