package com.yunos.tvtaobao.juhuasuan.common;


import android.net.Uri;
import android.os.Bundle;


public class UrlParserFactory {
    private static final String TAG = "UrlParserFactory";
    private static final int INT_KEY    = 1;
    private static final int LONG_KEY   = 2;
    private static final int FLOAT_KEY  = 3;
    private static final int DOUBLE_KEY = 4;
    private static final int STRING_KEY = 5;
    /**
     * 供外部调用
     * @param query
     * @return
     */
    public static Bundle onGetParserResult(String query) {

        Bundle bundle = new Bundle();

//        // 获取 模块名称，注意：此段代码保持不变
//        String moduleKey = getToModuleKey(query);
//        
//        // 先查看是否有MODUL关键字
//        if (moduleKey != null)  //home方法
//        {
//                moduleKey = moduleKey.trim();
//                bundle.putString(UrlKeyBaseConfig.INTENT_KEY_MODULE, moduleKey);
//        
//                // 获取具体可用内容部分， 
//                // 若需要额外的解析，可在以下语句中添加分支
////                String content = getUrlConnect(query);
//                onGetParserDetail(query, bundle);
////        
////                 if (moduleKey.equals(UrlKeyBaseConfig.INTENT_KEY_MODULE_DETAIL)) {
////                    
////                 }
//        
//                
//        }
//        else//detail & list方法
//        {
////            String content = getUrlConnect(query);
////            AppDebug.i(TAG, "content : " + content);
//            // 查看是否进入商品详情和分类
//            onGetParser(query ,bundle);
//            //提取URL字符串   
//        }

        onGetParser(query ,bundle);
        // 获取FROM部分，注意：此段代码保持不变
        String form = getUrlFrom(query);
        if (form != null) {
            bundle.putString(UrlKeyBaseConfig.INTENT_KEY_FROM, form);
        }

        return bundle;
    }

    /**
     * 获取需要进入的模块名称
     * @param query
     * @return
     */
    public static String getToModuleKey(String query) {

        String key_Module = null;

        //查找 module 
        int start = query.indexOf(UrlKeyBaseConfig.INTENT_KEY_MODULE, 0);
        if (start < 0)
            return null;

        start = query.indexOf("=", start);

        // 定位到 “ = ” 后面的位置 
        start++;

        int nextAmpersand = query.indexOf('&', start);
        int end = nextAmpersand != -1 ? nextAmpersand : query.length();

        // 获取模块名称
        key_Module = Uri.decode(query.substring(start, end));

        return key_Module;
    }
 
    /***
     * 获取URL的From部分
     * @param query
     * @return
     */
    public static String getUrlFrom(String query) {

        String form = null;

        int start = query.indexOf("&from", 0);
        if (start < 0) {
            return null;
        }

        start = query.indexOf("=", start);
        start++;

        int end = query.length();
        form = Uri.decode(query.substring(start, end));
        return form;
    }

    /**
     * 获取URL的内容部分
     * @param query
     * @return
     */
    public static String getUrlConnect(String query) {
        String connect = null;

        int connectStart = query.indexOf("&", 0);
        if (connectStart < 0) {
            return null;
        }
        connectStart++;

        int connectEnd = query.indexOf("&from", 0);
        connectEnd = connectEnd != -1 ? connectEnd : query.length();

        if (connectStart >= connectEnd) {
            return null;
        }
        connect = Uri.decode(query.substring(connectStart, connectEnd));

        return connect;
    }

 

    /***
     * 对详情模块的解析
     * @param query
     * @return
     */
    public static int onGetParserDetail(String query, Bundle bundle) {

        if (query == null) {
            return -1;
        }
        
        String[] keyList = { UrlKeyBaseConfig.INTENT_KEY_ITEMID, UrlKeyBaseConfig.INTENT_KEY_FROM,UrlKeyBaseConfig.INTENT_KEY_MODULE};
        int[] keyType = { UrlParserFactory.LONG_KEY, UrlParserFactory.STRING_KEY, UrlParserFactory.STRING_KEY};
        //参数提取
        putKV(query, bundle, keyList ,keyType);
        return 0;
    }
    /***
     * 对列表模块的解析
     * @param query
     * @return
     */
    public static int onGetParser(String query, Bundle bundle) {

        if (query == null) {
            return -1;
        }
        String[] keyList = {UrlKeyBaseConfig.INTENT_KEY_OPT_ID,UrlKeyBaseConfig.INTENT_KEY_ITEMID, UrlKeyBaseConfig.INTENT_KEY_CATEID, UrlKeyBaseConfig.INTENT_KEY_EXITCLOSE,
                UrlKeyBaseConfig.INTENT_KEY_TYPE, UrlKeyBaseConfig.INTENT_KEY_FROM ,UrlKeyBaseConfig.INTENT_KEY_MODULE};
        int[] keyType = {UrlParserFactory.STRING_KEY,UrlParserFactory.LONG_KEY,UrlParserFactory.LONG_KEY, UrlParserFactory.STRING_KEY,
                UrlParserFactory.STRING_KEY, UrlParserFactory.STRING_KEY, UrlParserFactory.STRING_KEY};
        //参数提取
        putKV(query, bundle, keyList ,keyType);
        return 0;
    }
    

    /**
     * 提取参数加入到bundle中
     * @param query
     * @param bundle
     * @param key
     * @param end
     */
    private static void putKV(String query, Bundle bundle, String[] keyList) {
        for (int i = 0; i < keyList.length; i++) {
            int startPosition = query.indexOf(keyList[i] + "=", 0);
            if (startPosition == -1) {
                continue;
            }
            startPosition = startPosition + keyList[i].length() + 1;

            int endPosition = -1;

            for (int j = 0; j < keyList.length; j++) {
                if (j == i) {
                    continue;
                }

                endPosition = query.indexOf("&" + keyList[j], startPosition);
                if (endPosition > 0) {
                    break;
                }
            }
            if (endPosition == -1) {
                endPosition = query.length();
            }

            if (startPosition >= endPosition) {
                continue;
            }

            String value = Uri.decode(query.substring(startPosition, endPosition));
            bundle.putString(keyList[i], value.trim());
        }
    }
    /**
     * 提取参数加入到bundle中
     * @param query
     * @param bundle
     * @param key
     * @param end
     */
    private static void putKV(String query, Bundle bundle, String[] keyList, int[] keyType) {
        for (int i = 0; i < keyList.length; i++) {
            int startPosition = query.indexOf(keyList[i] + "=", 0);
            if (startPosition == -1) {
                continue;
            }
            startPosition = startPosition + keyList[i].length() + 1;

            int endPosition = -1;

            for (int j = 0; j < keyList.length; j++) {
                if (j == i) {
                    continue;
                }

                endPosition = query.indexOf("&" + keyList[j], startPosition);
                if (endPosition > 0) {
                    break;
                }
            }
            if (endPosition == -1) {
                endPosition = query.length();
            }

            if (startPosition >= endPosition) {
                continue;
            }

            String value = Uri.decode(query.substring(startPosition, endPosition));
            switch (keyType[i]) {
                case INT_KEY:
                    bundle.putInt(keyList[i], Integer.parseInt(value.trim()));
                    break;
                case FLOAT_KEY:
                    bundle.putFloat(keyList[i], Float.parseFloat(value.trim()));
                    break;
                case DOUBLE_KEY:
                    bundle.putDouble(keyList[i], Double.parseDouble(value.trim()));
                    break;
                case LONG_KEY:
                    bundle.putLong(keyList[i], Long.parseLong(value.trim()));
                    break;
                default:
                    bundle.putString(keyList[i], value.trim());
                    break;
            }

        }
    }
}
