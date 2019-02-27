package com.yunos.tvtaobao.biz.request.ztc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yunos.tv.core.BuildConfig;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.StringUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/***
 * @author z
 */
public class RedirectRequest {
    private String userAgent;
    private Context context;
    private WebView zpWebView;
    private String id = null;
    private static boolean systemSupportWebView = true;

    private static final int STATUS_INITING = 1;
    private static final int STATUS_FAIL = 2;
    private static final int STATUS_NORMAL = 0;

    private static final String PREF_NAME = "ztc_init_status";

    public interface RedirectRequestListener {
        void onItemIdRetrieveResult(boolean success, String id);
    }

    private RedirectRequestListener mListener;

    private boolean shouldLoadWeb = true;

    public RedirectRequest(Context context, String agent, boolean isLoadUrl, RedirectRequestListener listener) {
        this.context = context.getApplicationContext();
        mListener = listener;
        userAgent = agent;
        shouldLoadWeb = isLoadUrl;
        initWebView(context);
    }

    private void initWebView(Context context) {
        systemSupportWebView = checkEnvironMent(context);
        if (systemSupportWebView) {
            systemSupportWebView = checkPreviousLauchCode();
        }
        if (shouldLoadWeb && systemSupportWebView)
            try {
                SharePreferences.put(PREF_NAME, STATUS_INITING);
                zpWebView = new WebView(context);
                systemSupportWebView = zpWebView != null;
                shouldLoadWeb = zpWebView != null;
                SharePreferences.put(PREF_NAME, STATUS_NORMAL);
            } catch (Throwable e) {
                e.printStackTrace();
                shouldLoadWeb = false;
                systemSupportWebView = false;
                SharePreferences.put(PREF_NAME, STATUS_FAIL);
                utSendFailInfo();
            }
        if (!systemSupportWebView)
            shouldLoadWeb = false;
        if (shouldLoadWeb) {
            WebSettings settings = zpWebView.getSettings();
            settings.setUserAgentString(userAgent);
            settings.setBlockNetworkImage(true);
            settings.setJavaScriptEnabled(true);
            settings.setLoadsImagesAutomatically(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                zpWebView.setWebContentsDebuggingEnabled(Config.isDebug());
            }
        }
    }

    private boolean checkEnvironMent(Context context) {
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (globalConfig == null) return true;
        GlobalConfig.ZhitongcheConfig ztcConfig = globalConfig.getZtcConfig();
        if (ztcConfig != null && ztcConfig.blackList != null) {
            for (String model : ztcConfig.blackList) {
                AppDebug.d("test", "blacklist:" + model);
                if (model.equals(Build.MODEL)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkPreviousLauchCode() {
        int launchStatus = SharePreferences.getInt(PREF_NAME, STATUS_NORMAL);
        if (launchStatus == STATUS_FAIL || launchStatus == STATUS_INITING)//initing
        {
            utSendFailInfo();
            SharePreferences.put(PREF_NAME, STATUS_FAIL);
            return false;
        }
        return true;
    }

    private void utSendFailInfo() {
        Map<String, String> params = new HashMap<>();
        params.put("model", Build.MODEL);
        params.put("ua", userAgent);
        params.put("sdk_int", "" + Build.VERSION.SDK_INT);
        params.put("version", Build.VERSION.RELEASE);
        for (String value : params.values())
            AppDebug.d("test", "sysinfo:" + value);
        Utils.utCustomHit(Utils.utGetCurrentPage(), "ztc_init_fail", params);
    }

    public void requestParams(String url) {
        //okhttp快速得到商品id
        //创建okHttpClient对象

        if (TextUtils.isEmpty(url) || "null".equalsIgnoreCase(url)) {
            if (mListener != null) mListener.onItemIdRetrieveResult(false, null);
            return;
        }
        AppDebug.d("test", "requestParams:" + url);
        if (GlobalConfig.instance != null && GlobalConfig.instance.getZtcConfig().isSingleRequest()) {
            if (shouldLoadWeb) {
                doWebRequest(url, true);
            } else {
                doOkhttpRequest(url);
            }
        } else {
            if (shouldLoadWeb) {
                doWebRequest(url, false);
            }
            doOkhttpRequest(url);
        }
    }

    private void doWebRequest(String url) {
        doWebRequest(url, false);
    }

    private void doWebRequest(String url, final boolean notifyCallback) {
        if (zpWebView == null) {
            initWebView(context);
        }
        zpWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Uri uri = Uri.parse(url);
                if (uri.isHierarchical()) {
                    String paramId = uri.getQueryParameter("id");
                    AppDebug.e("test", "RedirectRequest pageStarted: " + url + "  id:" + id);
                    if (!TextUtils.isEmpty(paramId) && !paramId.equals(id)) {//防止重复回调
                        id = paramId;
                        if (notifyCallback && mListener != null)
                            mListener.onItemIdRetrieveResult(true, id);
                        mListener = null;
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                AppDebug.e("test", "shouldintercept:" + url);
                if (TextUtils.isEmpty(url)) {
                    return super.shouldInterceptRequest(view, url);
                }
                Uri uri = Uri.parse(url);
                if (uri != null && uri.isHierarchical()) {
                    if (uri.getPath() != null && uri.getPath().endsWith(".mp4"))
                        return new WebResourceResponse(null, null, new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return 0;
                            }
                        });
                }
                return super.shouldInterceptRequest(view, url);
            }

            @TargetApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                AppDebug.e("test", "shouldintercept:" + request.getUrl());
                if (request.getUrl().getPath() != null && request.getUrl().getPath().endsWith(".mp4")) {
                    return new WebResourceResponse(null, null, new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return 0;
                        }
                    });
                }
                return super.shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url))
                    return false;
                Uri uri = Uri.parse(url);
                if (uri != null && uri.isHierarchical())
                    AppDebug.e("test", "RedirectRequest shouldOverRideUrlloading: " + url + "  id:" + uri.getQueryParameter("id"));
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (TextUtils.isEmpty(url))
                    return;

                view.loadUrl("javascript:(function(){var c=document.querySelectorAll(\"video\");for(var b=0,a=c.length;b<a;b++){var e=c[b];var d=e.parentNode;d.removeChild(e)}})();var tvbuy_block_video = document.createElement;document.createElement=function(tag){if(tag=='video'){console.log('block video');return undefined;}else return tvbuy_block_video.call(document,tag);}");
                Uri uri = Uri.parse(url);
                if (uri != null && uri.isHierarchical())
                    AppDebug.e("test", "RedirectRequest pageFinished: " + url + "  id:" + uri.getQueryParameter("id"));

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                AppDebug.e("test", "RedirectRequest page errorCode: " + errorCode);
                if (mListener == null)
                    return;
                if (TextUtils.isEmpty(id)) {
                    if (notifyCallback && mListener != null)
                        mListener.onItemIdRetrieveResult(false, null);
                    mListener = null;
                }
            }
        });
        zpWebView.loadUrl(url);
    }

    private void doOkhttpRequest(String url) {
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addNetworkInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {
                AppDebug.d("test", "intercept:" + chain.request().url());
                okhttp3.Request request = chain.request();
                Response respon = chain.proceed(request);
                if (respon.isRedirect()) {
                    Uri location = Uri.parse(respon.header("Location"));
                    AppDebug.d("test", "response redirect location:" + location);
                    if (location.isHierarchical() && location.getQueryParameter("id") != null && id == null) {
                        id = location.getQueryParameter("id");
                        if (mListener != null) {
                            mListener.onItemIdRetrieveResult(true, id);
                            mListener = null;
                        }
                    }
                } else {
                    //TODO
                    //阿里妈妈直通车应该至少有一次跳转，否则第一次拿不到可能会有问题
                    if (id == null) {
                        if (mListener != null) {
                            mListener.onItemIdRetrieveResult(false, id);
                            mListener = null;
                        }
                    }
                }
                return respon;
            }
        });
        OkHttpClient mOkHttpClient = okhttpBuilder.build();
        //创建一个Request
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("User-Agent", userAgent)
                .build();
        //new call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        id = null;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppDebug.d("test", "onFailure" + e.getMessage());
                if (mListener != null) {
                    mListener.onItemIdRetrieveResult(false, null);
                    mListener = null;
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String fid = response.request().url().queryParameter("id");
                //TODO: 这里和intercept中获取商品ID的处理好像重复了，需要再Review下逻辑
                //TODO: 另外可能要在finally中把response关闭下，严格模式下会检测出这个异常：
                //java.lang.Throwable: Explicit termination method 'end' not called.
                AppDebug.d("test", "onResponse ,url:" + response.request().url() + ", fid:" + fid + ",id:" + id);
                if (fid != null && !fid.equals(id)) {
                    if (mListener != null) {
                        mListener.onItemIdRetrieveResult(!TextUtils.isEmpty(fid), fid);
                        mListener = null;//只回调一次
                    }
                }
            }
        });
    }

    public void destroy() {
        if (zpWebView != null) {
            zpWebView.destroy();
            zpWebView = null;
        }
    }

}
