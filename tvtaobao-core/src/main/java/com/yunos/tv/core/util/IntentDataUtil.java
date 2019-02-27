package com.yunos.tv.core.util;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

/**
 * Activity Itent 辅助工具
 *
 * @author hanqi
 * @date 2014-4-10
 */
public class IntentDataUtil {

    private static String TAG = "IntentDataUtil";

    /**
     * 取得int型的数据值
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static Integer getInteger(Intent intent, String name, int defaultValue) {
        if (null == intent) {
            return null;
        }
        Integer value = defaultValue;
        if (intent.hasExtra(name)) {
            value = intent.getIntExtra(name, defaultValue);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(name)) {
                value = bundle.getInt(name, defaultValue);
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    String valueStr = uri.getQueryParameter(name);
                    if (null != valueStr && valueStr.length() > 0) {
                        try {
                            value = Integer.parseInt(valueStr);
                        } catch (Exception e) {
                            AppDebug.i(TAG, TAG + ".getInteger name=" + name + ", valueStr=" + valueStr + ", uri="
                                    + uri);
                        }
                    }
                }
                AppDebug.i(TAG, TAG + ".getString uri = " + uri + ", name=" + name + ", value=" + value
                        + ", defaultValue=" + defaultValue);
            }
        }
        return value;
    }

    /**
     * 取得Long型的数据值
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static Long getLong(Intent intent, String name, long defaultValue) {
        if (null == intent) {
            return null;
        }
        AppDebug.i(TAG, TAG + ".getLong name=" + name + ", defaultValue=" + defaultValue + ", intent=" + intent);
        Long value = defaultValue;
        if (intent.hasExtra(name)) {
            value = intent.getLongExtra(name, defaultValue);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(name)) {
                value = bundle.getLong(name, defaultValue);
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    String valueStr = uri.getQueryParameter(name);
                    if (null != valueStr && valueStr.length() > 0) {
                        try {
                            value = Long.parseLong(valueStr);
                        } catch (Exception e) {
                            AppDebug.i(TAG, TAG + ".getLong name=" + name + ", valueStr=" + valueStr + ", uri=" + uri);
                        }
                    }
                }
                AppDebug.i(TAG, TAG + ".getLong uri = " + uri + ", name=" + name + ", value=" + value
                        + ", defaultValue=" + defaultValue);
            }
        }
        return value;
    }

    /**
     * 取得String
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getString(Intent intent, String name, String defaultValue) {
        String value = getStringFromBundle(intent, name, defaultValue);
        if (TextUtils.isEmpty(value)) {
            value = getStringFromUri(intent, name, defaultValue);
        }

        return value;
    }

    /**
     * 只从bundle中取得数据值
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getStringFromBundle(Intent intent, String name, String defaultValue) {
        if (null == intent) {
            return null;
        }
        String value = defaultValue;
        if (intent.hasExtra(name)) {
            value = intent.getStringExtra(name);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(name)) {
                AppDebug.i(TAG, TAG + ".getString 1==> name=" + name + ", value=" + value + ", defaultValue="
                        + defaultValue + ", bundle=" + bundle);
                value = bundle.getString(name, defaultValue);
            }
        }

        return value;
    }

    public static Object getObjectFromBundle(Intent intent, String name, Object defaultValue) {
        if (null == intent) {
            return null;
        }
        Object value = defaultValue;
        Bundle bundle = intent.getExtras();
        if (null != bundle && bundle.containsKey(name)) {
            AppDebug.i(TAG, TAG + ".getObject 1==> name=" + name + ", value=" + value + ", defaultValue="
                    + defaultValue + ", bundle=" + bundle);
            value = bundle.getSerializable(name);
        }

        return value;
    }

    /**
     * 只从URI中提取数据值
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getStringFromUri(Intent intent, String name, String defaultValue) {
        if (null == intent) {
            return null;
        }
        String value = defaultValue;

        Uri uri = intent.getData();
        if (null != uri) {
            try {
                value = uri.getQueryParameter(name);
                if (TextUtils.isEmpty(value)) {
                    value = defaultValue;
                }
            } catch (Exception e) {
                value = defaultValue;
            }
        }
        AppDebug.i(TAG, TAG + ".getString 2==> name=" + name + ", value=" + value + ", defaultValue=" + defaultValue
                + ", uri = " + uri);

        return value;
    }

    /**
     * 取得布尔型的值
     *
     * @param intent
     * @param name
     * @param defaultValue
     * @return
     */
    public static Boolean getBoolean(Intent intent, String name, boolean defaultValue) {
        if (null == intent) {
            return null;
        }
        Boolean value = defaultValue;
        if (intent.hasExtra(name)) {
            value = intent.getBooleanExtra(name, defaultValue);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(name)) {
                value = bundle.getBoolean(name, defaultValue);
            } else {
                Uri uri = intent.getData();
                if (null != uri) {
                    value = uri.getBooleanQueryParameter(name, defaultValue);
                }
                AppDebug.i(TAG, TAG + ".getString uri = " + uri + ", name=" + name + ", value=" + value
                        + ", defaultValue=" + defaultValue);
            }
        }
        return value;
    }

    /**
     * 从URI中提取host信息
     *
     * @param intent
     * @return
     */
    public static String getHost(Intent intent) {
        if (null == intent) {
            return null;
        }
        String host = null;
        Uri uri = intent.getData();
        AppDebug.i(TAG, TAG + ".getHost uri = " + uri);
        if (null != uri) {
            host = uri.getHost();
        }
        return host;
    }
}
