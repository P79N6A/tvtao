package com.tvtaobao.voicesdk.utils;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunos.tv.core.common.AppDebug;

/**
 * Gson的工具类
 * @author yunzhong.qyz
 *
 */
public class GsonUtil {

    private GsonUtil() {
    }

    private final static Gson G = new Gson();

    /**
     * 将json字符串解析成对象
     * @param json
     * @param token
     * @return T 各种对象
     */
    public static <T> T parseJson(String json, TypeToken<T> token) {
        if (json == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            T t = (T) G.fromJson(json, token.getType());
            return t;
        }catch (Exception e) {
            AppDebug.e("GsonUtil", "parseJson error:"+e);
            return null;
        }
    }


}
