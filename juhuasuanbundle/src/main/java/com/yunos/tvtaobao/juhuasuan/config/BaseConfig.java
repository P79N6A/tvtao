package com.yunos.tvtaobao.juhuasuan.config;


import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;

public class BaseConfig {

    // 应用类型,android为2
    public static final int APP_TYPE = 2;

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

    // 版权说明
    public static final String URL_COPYRIGHT = "http://act.ju.taobao.com/go/rgn/mobile/jma_update.php";

    // 帮助中心
    public static final String URL_HELP = "http://act.ju.taobao.com/go/rgn/mobile/jmad_help.php";

    public static String getAuthType() {
        return AUTH_TYPE;
    }

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

    public static String getPayDomain() {
        if (Config.getRunMode() == RunMode.PRODUCTION) {
            return DOMAIN_PAY_PRODUCTION;
        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
            return DOMAIN_PAY_PREDEPLOY;
        } else {
            return DOMAIN_PAY_DAILY;
        }
    }

    public static String getAppKey() {
        return Config.getAppKey();
    }

}
