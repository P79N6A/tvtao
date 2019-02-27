package com.yunos.tv.core.config;



import dalvik.system.DexClassLoader;

/**
 * 请求的公共基本配置
 * @author tianxiang
 * @date 2012-10-21 上午10:04:17
 */
public class RequestConfig {

    static DexClassLoader cl;

    public static final int DEFAULT_PAGE_SIZE = 30;


    // 加密类型
    private static final String AUTH_TYPE = "md5";

    // 默认的API版本
    private static final String DEFAULT_API_VERSION = "1.0";

    // 图片服务器配置
    private static final class ImageServer {

        public final static String[] DALIY = new String[] { "http://img01.daily.taobaocdn.net/bao/uploaded/",
                "http://img02.daily.taobaocdn.net/bao/uploaded/", "http://img03.daily.taobaocdn.net/bao/uploaded/" };
        public final static String[] PREDEPLOY = new String[] { "http://img01.taobaocdn.com/bao/uploaded/",
                "http://img02.taobaocdn.com/bao/uploaded/", "http://img03.taobaocdn.com/bao/uploaded/",
                "http://img04.taobaocdn.com/bao/uploaded/" };
        public final static String[] PRODUCTION = new String[] { "http://img01.taobaocdn.com/bao/uploaded/",
                "http://img02.taobaocdn.com/bao/uploaded/", "http://img03.taobaocdn.com/bao/uploaded/",
                "http://img04.taobaocdn.com/bao/uploaded/" };
    }

    // 支付服务配置
    public static final String DOMAIN_PAY_DAILY = "http://mali.alipay.net";
    public static final String DOMAIN_PAY_PREDEPLOY = "http://mali.alipay.com";
    public static final String DOMAIN_PAY_PRODUCTION = "http://mali.alipay.com";



    public static String getApiVersion() {
        return DEFAULT_API_VERSION;
    }

    public static String[] getImageServer() {
        if (Config.getRunMode() == RunMode.PRODUCTION) {
            return ImageServer.PRODUCTION;
        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
            return ImageServer.PREDEPLOY;
        } else {
            return ImageServer.DALIY;
        }
    }

}
