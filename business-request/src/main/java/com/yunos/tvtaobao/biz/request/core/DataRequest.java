/**
 * 
 */
package com.yunos.tvtaobao.biz.request.core;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Map;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * 定义的请求基类
 * @author tianxiang
 * @date 2012-10-20 下午1:54:16
 */
public abstract class DataRequest extends MtopRequest {

    private static final long serialVersionUID = -2737660281410004209L;
    protected String TAG = getClass().getSimpleName();

    /**
     * 得到请求类型,如mtop,top等
     * @date 2012-10-20下午3:59:17
     * @return
     */
    public abstract DataRequestType getRequestType();

    /**
     * 得到调用的api名称
     * @date 2012-10-20下午4:00:17
     * @return
     */
    protected abstract String getApi();

    /**
     * 得到调用的api的版本
     * @date 2012-10-20下午4:00:30
     * @return
     */
    protected abstract String getApiVersion();

    /**
     * 得到请求的http参数，如a=6&b=9&c=7
     * @date 2012-10-20下午4:01:24
     * @return
     */
    protected abstract String getHttpParams();

    /**
     * 获取参数数据
     * @return
     */
    protected abstract Map<String, String> getAppData();

    /**
     * 取得post的相关数据
     * @return
     */
    protected abstract List<? extends NameValuePair> getPostParameters();

    /**
     * 得到请求的http domain，如http://www.taobao.com
     * @date 2012-10-20下午4:01:52
     * @return
     */
    protected abstract String getHttpDomain();

    /**
     * 请求的初始化，这个方法非常重要
     * @date 2012-10-20下午4:52:10
     */
    protected void initialize() {
        throw new RuntimeException("request not initialize");
    }

    /**
     * 得到完整的http url
     * @date 2012-10-20下午4:02:47
     * @return
     */
    public String getUrl() {
        initialize();
        String url = null;
        try {
            url = getHttpDomain();
            String params = getHttpParams();
            if (!TextUtils.isEmpty(params)) {
                url += ("?" + getHttpParams());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppDebug.v(TAG, TAG + ".getUrl.url = " + url);
        return url;
    }

    /**
     * 取得http post相关的URL
     * @return
     */
    public String getPostUrl() {
        initialize();
        String url = null;
        try {
            url = getHttpDomain();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 解析结果
     * @date 2012-10-20下午4:02:58
     * @param response
     * @return
     * @throws Exception
     */
    public abstract <T> ServiceResponse<T> resolveResponse(String response) throws Exception;

    /**
     * 返回消息的编码
     * @date 2012-11-13上午10:42:17
     * @return
     */
    public String getResponseEncode() {
        return AppInfo.HTTP_PARAMS_ENCODING;
    }
}
