package com.yunos.tv.core.security;

import android.content.Context;

import com.taobao.wireless.security.sdk.SecurityGuardManager;
import com.taobao.wireless.security.sdk.SecurityGuardParamContext;
import com.taobao.wireless.security.sdk.dynamicdataencrypt.IDynamicDataEncryptComponent;
import com.taobao.wireless.security.sdk.securesignature.ISecureSignatureComponent;
import com.taobao.wireless.security.sdk.securesignature.SecureSignatureDefine;
import com.yunos.tv.core.common.AppDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类是安全签名的Facade
 * @author Jiejing
  */
public class SecureSigner {

//    public class SecureUtilKey {
//        public static final String M_API = SecretUtil.M_API;
//        public static final String M_V = SecretUtil.M_V;
//        public static final String M_IMEI = SecretUtil.M_IMEI;
//        public static final String M_IMSI = SecretUtil.M_IMSI;
//        public static final String M_DATA = SecretUtil.M_DATA;
//        public static final String M_TIME = SecretUtil.M_TIME;
//        public static final String M_ECODE = SecretUtil.M_ECODE;
//        public static final String M_SSO = SecretUtil.M_SSO;
//        public static final String M_DEV = SecretUtil.M_DEV;
//    }

    private SecurityGuardManager securityGuardManager_;
    private static SecureSigner signerInstance_;
    private Context context_;
//    private SecretUtil util_;

    private SecureSigner(Context ctx) {
        context_ = ctx;
//        util_ = new SecretUtil((ContextWrapper) context_);
        securityGuardManager_ = SecurityGuardManager.getInstance(ctx);
    }

    /**
     * 得到签名器的实例， 建议使用先得到实例再调用签名的东西 ，这样可以避免每次SecurityGuard的初始化的开销。
     * @param ctx  ApplicationContext 和 ActivityContext都可以
     * @return  返回签名的实例
     */
    public static SecureSigner getInstance(Context ctx) {
        if (signerInstance_ == null) {
            signerInstance_ = new SecureSigner(ctx);
        }
        return signerInstance_;
    }

    /**
     * 得到Mtop的安全签名
     * @param appkey  传入需要签名的AppKey
     * @param api     需要调用的mtop的API
     * @param v       API的版本， 比如1.0
     * @param imei    设备识别号
     * @param imsi    客户端用户标识
     * @param data    Api请求参数的集合， *不要* Encode 这段数据
     * @param t       时间戳， UTC 1970-1-1日0时到现在的时间差，精度到秒，不能为空
     * @param ecode   用户调用登录接口获取的随机数(不需要在http上传递，只用于生成签名),从login/autologin接口取得
     * @return        返回安全签名， 如果出错，返回空字符串
     */
    public final String getMtopSign(String appkey, String api,
                                    String v, String imei, String imsi, 
                                    String data, String t, String ecode) {

        Map<String, String> argMap = getParamMap(api, v, imei, imsi, data, t, ecode);
        return getSignInner(securityGuardManager_, argMap, appkey, SecureSignatureDefine.SIGN_MTOP);
    }

    /**
     * 得到Top的签名
     * @param appkey
     * @param map
     * @return
     */

    public final String getTopSign(String appkey, Map<String, String> map) {
        return getSignInner(securityGuardManager_, map, appkey, SecureSignatureDefine.SIGN_TOP);
    }

    /**
     * 得到Mtop的签名， 传入参数的map版本
     * @param appkey
     * @param map
     * @return
     */
    public final String getMTopSign(String appkey, Map<String, String> map) {
        return getSignInner(securityGuardManager_, map, appkey, SecureSignatureDefine.SIGN_MTOP);
    }

    /**
     * 非Mtop的签名， 算法不同于Mtop
     * @param appkey
     * @param map
     * @return
     */
    public final String getHttpSign(String appkey, Map<String, String> map) {
        return getSignInner(securityGuardManager_, map, appkey, SecureSignatureDefine.SIGN_FLYSTREET);
    }

//    public final String getYunOSSign(String rawText) {
//    	AppDebug.i("Mtop", "getYunOSSign : " + rawText);
//        LinkedHashMap<String, String> linkedParams = new LinkedHashMap<String, String>();
//        DataContext dc = new DataContext(0,null);
//        dc.category = CategoryType.FleasStreet.FLEAS_STREET;
//        dc.type = CategoryType.FleasStreet.T.T_P;
//        dc.index = 3;
//        linkedParams.put("keys", rawText);  //str
//        String sign = util_.getExternalSign(linkedParams, dc);
//        AppDebug.i("Mtop", "getYunOSSign sign: " + sign);
//        return sign;
//    }

//    public final String getExternalSign(LinkedHashMap<String, String> linkedParams, DataContext dc) {
//    	return util_.getExternalSign(linkedParams, dc);
//    }
    
    /**
     * 得到Mtop的安全签名
     * @param ctx  ApplicationContext 和 ActivityContext都可以, 这个API因为每次都要初始化， 所以性能起见使用非static 的API
     * @param appkey  传入需要签名的AppKey
     * @param api     需要调用的mtop的API
     * @param v       API的版本， 比如1.0
     * @param imei    设备识别号
     * @param imsi    客户端用户标识
     * @param data    Api请求参数的集合， *不要* Encode 这段数据
     * @param t       时间戳， UTC 1970-1-1日0时到现在的时间差，精度到秒，不能为空
     * @param ecode   用户调用登录接口获取的随机数(不需要在http上传递，只用于生成签名),从login/autologin接口取得
     * @return        返回安全签名， 如果出错，返回空字符串
     */
    public static String getMtopSign(Context ctx, String appkey, String api,
                                           String v, String imei, String imsi, 
                                           String data, String t, String ecode) {

        Map<String, String> argMap = getParamMap(api, v, imei, imsi, data, t, ecode);

        SecurityGuardManager sgManager;
        sgManager = SecurityGuardManager.getInstance(ctx);

        return getSignInner(sgManager, argMap, appkey, SecureSignatureDefine.SIGN_MTOP);
    }


    /**
     * 动态数据加解密
     * @param plainText 明文
     * @return 返回加密后的文字， 返回空字符串表示失败， 不会返回null
     */
    public String dynamicEncrypt(String plainText) {
        IDynamicDataEncryptComponent dynamicDataEncryptComponent = securityGuardManager_.getDynamicDataEncryptComp();
        if (dynamicDataEncryptComponent != null) {
            String encStr = dynamicDataEncryptComponent.dynamicEncrypt(plainText);
            if (encStr == null)
                return "";
            else
                return encStr;
        }

        return "";
    }

    /**
     * 解密字符串
     * @param cipherText  密文
     * @return 返回解密的字符串， 返回空字符串表示失败， 不会返回null
     */
    public String dynamicDecrypt(String cipherText) {
        IDynamicDataEncryptComponent dynamicDataEncryptComponent = securityGuardManager_.getDynamicDataEncryptComp();
        if (dynamicDataEncryptComponent != null) {
            String origStr = dynamicDataEncryptComponent.dynamicDecrypt(cipherText);
            if (origStr == null)
                return "";
            else
                return origStr;
        }

        return "";
    }

    private static Map<String, String> getParamMap(String api, String v, String imei, String imsi, String data, String t, String ecode) {
        Map<String, String> argMap = new HashMap<String, String>();

        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_VERSION, v);
        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_IMEI, imei);
        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_IMSI, imsi);
        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_TIME, t);
        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_API, api);
        argMap.put(SecureSignatureDefine.SG_KEY_SIGN_DATA, data); // XXX: *Don't* Encode app Data when do the sign!!!

        if (ecode != null)
            argMap.put(SecureSignatureDefine.SG_KEY_SIGN_ECODE, ecode);
        return argMap;
    }

    private static String getSignInner(SecurityGuardManager sgManager, Map<String, String> map, String appkey, int requestType) {

        if (sgManager != null) {
            ISecureSignatureComponent ssComp = sgManager.getSecureSignatureComp();

            if (ssComp != null) {
                SecurityGuardParamContext sgc = new SecurityGuardParamContext();
                sgc.appKey = appkey;
                sgc.paramMap = map;
                sgc.requestType =  requestType;

                AppDebug.i("Mtop", "appkey: " + appkey  + "params: "  + map + "request " + sgc.requestType);

                String ssStr = ssComp.signRequest(sgc);

                AppDebug.i("Mtop", "get sign is : " + ssStr);

                if (ssStr != null)
                    return ssStr;
                else
                    return "";
            }
        }
        return "";
    }
}
