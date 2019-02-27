/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.config;


import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;

/**
 * @author tianxiang
 * @date 2012-10-21 上午10:24:31
 */
public class TopRequestConfig extends RequestConfig {

    private static final class HttpDomain {

        public final static String DALIY = "http://api.daily.taobao.net/router/rest";
        public final static String PREDEPLOY = "http://110.75.14.63/top/router/rest";
        public final static String PRODUCTION = "http://gw.api.taobao.com/router/rest";
    }

    public static String getHttpDomain() {
        if (Config.getRunMode() == RunMode.PRODUCTION) {
            return HttpDomain.PRODUCTION;
        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
            return HttpDomain.PREDEPLOY;
        } else {
            return HttpDomain.DALIY;
        }
    }
}
