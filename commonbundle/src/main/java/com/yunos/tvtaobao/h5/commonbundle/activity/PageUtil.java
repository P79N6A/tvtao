package com.yunos.tvtaobao.h5.commonbundle.activity;

import android.text.TextUtils;

import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.lib.SystemProUtils;

/**
 * Created by rca on 26/03/2018.
 */

class PageUtil {

    private static final String TAG = PageUtil.class.getSimpleName();

    /**
     * 通过牌照管控，重置url
     * D模式目前不作处理，因为无法知道当前所处牌照环境
     */
    static String processPageUrl(String pageUrl) {
        if (!com.yunos.RunMode.isYunos()) {
            return pageUrl;
        }
        String host = Utils.parseHost(pageUrl);
        RetData retData = null;
        if (!TextUtils.isEmpty(host)) {
            try {
                retData = TVCompliance.getComplianceDomain(host);
            } catch (Exception e) {
                retData = null;
            }
        }
        if (retData == null) {
            return pageUrl;
        }
        AppDebug.d(TAG, "Converted domain host " + host);
        AppDebug.d(TAG, "Converted domain is " + retData.toString());
        if (retData.getCode() == RetCode.Success || retData.getCode() == RetCode.Default) {
            AppDebug.d(TAG, "Converted domain is " + retData.getResult());
            String domainName = retData.getResult();
            String replace = pageUrl.replace(host, domainName);
            AppDebug.d(TAG, "Original page is " + pageUrl);
            AppDebug.d(TAG, "replace page is " + replace);
            if (!domainName.equals(host))
                replace = replace.replaceFirst("http://", "https://");
            return replace;
        } else {
            AppDebug.d(TAG, "Original domain is " + retData.getResult());
            String domainName = extraComplianceDomain(host);
            if (!domainName.equals(host))
                return pageUrl.replace(host, domainName).replaceFirst("http://", "https://");
        }
        return pageUrl;
    }

    /**
     * 牌照管控补充，当sdk无法转换某些新域名时在此转换，目前只处理tvos.taobao.com
     *
     * @param originalDomain
     * @return
     */
    private static String extraComplianceDomain(String originalDomain) {
        String license = SystemProUtils.getLicense();
        if ("tvos.taobao.com".equals(originalDomain)) {
            if ("1".equals(license)) {//wasu
                return "tb.cp12.wasu.tv";
            }
            if ("7".equals(license)) {//icintv
                return "tb.cp12.ott.cibntv.net";
            }
        }
        return originalDomain;
    }
}
