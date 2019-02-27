/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.config;


import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;

/**
 * mtop请求配置
 * @author tianxiang
 * @date 2012-10-21 上午10:10:16
 */
public class MtopRequestConfig extends RequestConfig {

    private static final class HttpDomain {

        // public final static String DALIY = "http://api.waptest.taobao.com/rest/api3.do";
        public final static String DALIY = "http://10.232.127.67/rest/api3.do";
        public final static String PREDEPLOY = "http://api.wapa.taobao.com/rest/api3.do";
        public final static String PRODUCTION = "https://m.yunos.wasu.tv/rest/api3.do?";//"https://api.m.taobao.com/rest/api3.do?";
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
