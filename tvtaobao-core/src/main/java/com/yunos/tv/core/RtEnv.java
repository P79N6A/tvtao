package com.yunos.tv.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GuoLiDong on 2019/1/4.
 * 存放一些App运行时数据
 * 不方便写成成员变量的
 * 或者临时存放一些数据
 * 简而言之，一些无处存放的全局变量
 */
public class RtEnv {
    /*----------------------------------------*/
    /**
     * 更新的附带信息 的 取值键
     */
    public static final String KEY_UPDATE_BUNDLE = "KEY_UPDATE_BUNDLE";

    /**
     * 每次应用启动，只检查一次更新，（全局更新是否检查过的标识）
     */
    public static final String KEY_SHOULD_CHECK_UPDATE = "KEY_SHOULD_CHECK_UPDATE";

    /**
     * 每页面resume，检查一次更新，（标识是否应该开始更新）
     */
    public static final String KEY_SHOULD_START_UPDATE = "KEY_SHOULD_START_UPDATE";

    /**
     * 每页面onCreate，检查一次更新，（标识是否应该检查）
     */
    public static final String KEY_SHOULD_CHECK_UPDATE_ON_CREATE = "KEY_SHOULD_CHECK_UPDATE_ON_CREATE";

    /*----------------------------------------*/
    private static Map records = new HashMap();

    /*----------------------------------------*/
    public static String mkKey(Object obj){
        String objHas = (obj==null)?("null"):(""+obj.hashCode());
        return objHas+"#"+System.currentTimeMillis();
    }

    public static void clear(){
        records.clear();
    }

    public static void tmpSet(String key, Object object){
        records.put(key,object);
    }

    public static Object tmpGet(String key){
        return tmpGet(key,null);
    }

    public static Object tmpGet(String key, Object defaultV){
        Object rtn = records.remove(key);
        return (rtn!=null)?(rtn):(defaultV);
    }

    public static void set(String key, Object object){
        records.put(key,object);
    }

    public static Object get(String key){
        return get(key,null);
    }

    public static Object get(String key, Object defaultV){
        Object rtn = records.get(key);
        return (rtn!=null)?(rtn):(defaultV);
    }

    public static Object rmv(String key){
        return records.remove(key);
    }
}
