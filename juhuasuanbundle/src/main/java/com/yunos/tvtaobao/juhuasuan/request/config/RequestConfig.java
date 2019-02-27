package com.yunos.tvtaobao.juhuasuan.request.config;


import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;

import dalvik.system.DexClassLoader;

/**
 * 请求的公共基本配置
 * @author tianxiang
 * @date 2012-10-21 上午10:04:17
 */
public class RequestConfig {

    static String dexPath = "//data//data//com.taobao.ju.android//test.jar";
    static String dexOutputPath = "//data//data//com.taobao.ju.android//";
    static DexClassLoader cl;

    public static final int DEFAULT_PAGE_SIZE = 30;

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

    //品牌团图片服务器
    private static final class BrandImageServer {

        public final static String[] DALIY = new String[] { "http://img01.taobaocdn.net/bao/uploaded/",
                "http://img02.taobaocdn.net/bao/uploaded/", "http://img03.taobaocdn.net/bao/uploaded/",
                "http://img04.taobaocdn.net/bao/uploaded/" };

        public final static String[] PREDEPLOY = new String[] { "http://gju1.alicdn.com/bao/uploaded/",
                "http://gju2.alicdn.com/bao/uploaded/", "http://gju3.alicdn.com/bao/uploaded/",
                "http://gju4.alicdn.com/bao/uploaded/" };

        public final static String[] PRODUCTION = new String[] { "http://gju1.alicdn.com/bao/uploaded/",
                "http://gju2.alicdn.com/bao/uploaded/", "http://gju3.alicdn.com/bao/uploaded/",
                "http://gju4.alicdn.com/bao/uploaded/" };
    }

    // 支付服务配置
    public static final String DOMAIN_PAY_DAILY = "http://mali.alipay.net";
    public static final String DOMAIN_PAY_PREDEPLOY = "http://mali.alipay.com";
    public static final String DOMAIN_PAY_PRODUCTION = "http://mali.alipay.com";

    // 版权说明
    public static final String URL_COPYRIGHT = "http://act.ju.taobao.com/go/rgn/mobile/jma_update.php";

    // 帮助中心
    public static final String URL_HELP = "http://act.ju.taobao.com/go/rgn/mobile/jmad_help.php";

    /**
     * 品牌团 wap页:已废弃wap模式
     * @deprecated
     */
    public static final String URL_BRAND = "http://m.ju.taobao.com/wap/ju_brand.htm";

    public static String getAuthType() {
        return AUTH_TYPE;
    }

    public static String getApiVersion() {
        return DEFAULT_API_VERSION;
    }

    /**
     * 根据img名称得到需要的 ImageServer Prefix
     * @param imageName 图片名
     * @return
     */
    public static String getBrandImageServer(String imageName) {
        if (imageName == null) {
            imageName = "";
        }
        String imageServer = "";
        int mod = 0;
        switch (Config.getRunMode()) {
            case DAILY:
                mod = Math.abs(imageName.hashCode()) % BrandImageServer.DALIY.length;
                imageServer = BrandImageServer.DALIY[mod];
                break;
            case PREDEPLOY:
                mod = Math.abs(imageName.hashCode()) % BrandImageServer.PREDEPLOY.length;
                imageServer = BrandImageServer.PREDEPLOY[mod];
                break;
            case PRODUCTION:
                mod = Math.abs(imageName.hashCode()) % BrandImageServer.PRODUCTION.length;
                imageServer = BrandImageServer.PRODUCTION[mod];
                break;
            default:
                mod = Math.abs(imageName.hashCode()) % BrandImageServer.PRODUCTION.length;
                imageServer = BrandImageServer.PRODUCTION[mod];
                break;
        }
        return imageServer + imageName;
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

    public interface IAppKeyConfig {

        public String getAppKey();

        public String getAppSecret();
    }
}
