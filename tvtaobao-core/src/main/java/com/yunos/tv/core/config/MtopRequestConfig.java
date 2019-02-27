/**
 * 
 */
package com.yunos.tv.core.config;


/**
 * mtop请求配置
 * @author tianxiang
 * @date 2012-10-21 上午10:10:16
 */
public class MtopRequestConfig extends RequestConfig {

    private static final class HttpDomain {

        /**
         * securityApi.do 和 api3.do 的区别：
         * 1.安全登录接口一定要用securityApi.do? 这样才会做安全检查.
         * 2.其他Mtop API 可以使用securityApi.do? 或者api3.do? 。其他API 使用securityApi.do? 是
         * 不会做安全检查的。
         * 3 要支持安全检查，登录接口不能缺失security=校验串参数。
         * 4 其他Mtop API 使用就算加入security=校验串参数 ，目前是不会校验的。
         */
        public final static String DALIY = "http://api.waptest.taobao.com/rest/api3.do";
        //        public final static String DALIY = "http://10.232.127.67/rest/api3.do";
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
