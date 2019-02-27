/**
 * 
 */
package com.yunos.tvtaobao.biz.request.core;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * @author tianxiang
 * @date 2012-11-3 下午1:50:21
 */
public class ApacheHttpClient {

    private static final String TAG = "ApacheHttpClient";
    private static final String CHARSET = HTTP.UTF_8;

    public static enum HTTP_REQUEST_ERROR {
        HTTP_NONE_ERROR, HTTP_SSL_ERROR, HTTP_CONNECT_ERROR, HTTP_SOCKET_ERROR, HTTP_SOCKET_TIMEOUT_ERROR, HTTP_CONNECT_TIMEOUT_ERROR, HTTP_UNKNOWN_HOST_ERROR, HTTP_UNSUPPORTED_ENCODING, HTTP_OTHER_ERROR;
    };

    /**
     * 每个路由最大连接数
     */
    public final static int MAX_ROUTE_CONNECTIONS = 400;

    /**
     * 最大连接数
     */
    public final static int MAX_TOTAL_CONNECTIONS = 800;
    private static HttpClient client = initHttpClient();

    private static synchronized HttpClient initHttpClient() {

        HttpParams params = new BasicHttpParams();
        // 设置一些基本参数
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        // 超时设置和最大连接数
        ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
        ConnManagerParams.setTimeout(params, 15000);// 从连接池中取连接的超时时间
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS); // 设置每个路由最大连接数
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        /************************
         * 一定要注意：
         * 此段代码的三处超时时间，绝对不能改小，
         * 因为之前把超时时间改小[5S]出现过网络请求超时的问题，
         * 原因是接口返回的时间可能会大于5S!
         ***********************/
        HttpConnectionParams.setConnectionTimeout(params, 15000);// 通过网络与服务器建立连接的超时时间
        HttpConnectionParams.setSoTimeout(params, 15000);// Socket读数据的超时时间

        //支持http
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        //支持https
        KeyStore trustStore;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", sf, 443);
            schReg.register(sch);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建线程安全的client
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

        return new DefaultHttpClient(conMgr, params);

    }

    public static void shutdown() {
        if (client != null && client.getConnectionManager() != null) {
            client.getConnectionManager().shutdown();
        }
    }

    public static void onStartHttpClient() {
        client = null;
        client = initHttpClient();
    }

    /**
     * http请求
     * @author tianxiang
     * @date 2012-10-9 18:41:46
     */
    public static String executeHttpGet(String host, String url, String encode) {
        return executeHttpGet(host, url, encode, true);
    }

    public static String executeHttpPost(String host, String url, String encode,
            List<? extends NameValuePair> parameters) {
        return executeHttpPost(host, url, encode, parameters, true);
    }

    private static String executeHttpPost(String host, String url, String encode,
            List<? extends NameValuePair> parameters, boolean isRetry) {
        if (url == null) {
            throw new RuntimeException("url is null");
        }
        AppDebug.i("HttpClient-request-post", url);
        InputStream in = null;
        BufferedReader reader = null;
        String result = null;
        HttpURLConnection conn = null;
        try {
            URL urlConn = new URL(url);
            conn = (HttpURLConnection) urlConn.openConnection();
            // 设置请求的超时时间
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            // 设置请求的方式 
            conn.setRequestMethod("POST");
            // 设置请求的头  
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setDoOutput(true); // 发送POST请求必须设置允许输出  
            conn.setDoInput(true); // 发送POST请求必须设置允许输入
            if (!TextUtils.isEmpty(host)) {
                conn.addRequestProperty("host", host);
            }

            // 组合输入流
            StringBuffer data = new StringBuffer();
            for (NameValuePair param : parameters) {
                data.append(param.getName()).append("=").append(param.getValue()).append("&");
            }
            AppDebug.i("HttpClient-request-post", "params=" + data);
            byte[] bypes = data.toString().getBytes();
            //获取输出流  
            OutputStream os = conn.getOutputStream();
            os.write(bypes);
            os.flush();
            // 获取请求流
            in = conn.getInputStream();
            String encoding = conn.getContentEncoding();
            if (encoding != null && encoding.contains("gzip")) {//首先判断服务器返回的数据是否支持gzip压缩，
                in = new GZIPInputStream(in); //如果支持则应该使用GZIPInputStream解压，否则会出现乱码无效数据
            }
            reader = new BufferedReader(new InputStreamReader(in, encode));
            if (conn.getResponseCode() == HttpStatus.SC_OK) {
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
                result = sb.toString();
            }
            if (result != null && !result.contains("SUCCESS") && url.contains("api.m.taobao.com")) {
                AppDebug.e(TAG, "executeHttpPost" + result);
            } else {
                AppDebug.i(TAG, "executeHttpPost" + result);
            }
        } catch (Exception e) {
            HTTP_REQUEST_ERROR error = getRequestException(e);
            AppDebug.e(TAG, "executeHttpPost error=" + error + e + " isRetry=" + isRetry);

            //            //异常并且使用了http-dns,需要清除ip缓存
            //            if (!StringUtils.isEmpty(host)) {
            //                HttpDns.getInstance().SetErrorByHost(host);
            //            }

            //异常时重试一次
            if (isRetry) {
                result = executeHttpPost(host, url, encode, parameters, false);
            }
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                AppDebug.e(TAG, "executeHttpPost IO error=" + e);
            }
        }

        return result;
    }

    private static String executeHttpGet(String host, String url, String encode, boolean isRetry) {
        if (url == null) {
            throw new RuntimeException("url is null");
        }
        AppDebug.i("HttpClient-request", url);
        InputStream in = null;
        BufferedReader reader = null;
        String result = null;
        HttpGet request = null;

        try {
            request = new HttpGet(url);
            if (!TextUtils.isEmpty(host)) {
                request.addHeader("host", host);
            }
            HttpResponse r = client.execute(request);
            if (r != null && r.getEntity() != null) {
                in = r.getEntity().getContent();
                Header contentEncoding = r.getFirstHeader("Content-Encoding");
                // gzip解压的支持
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    AppDebug.i(TAG, "gzip");
                    in = new GZIPInputStream(new BufferedInputStream(in));
                }
                reader = new BufferedReader(new InputStreamReader(in, encode));

                if (r.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    StringBuilder sb = new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                    }
                    result = sb.toString();
                }
                if (result != null && !result.contains("SUCCESS") && url.contains("api.m.taobao.com")) {
                    AppDebug.e(TAG, "executeHttpGet" + result);
                } else {
                    AppDebug.i(TAG, "executeHttpGet" + result);
                }
            } else {
                AppDebug.i(TAG, "response = NULL");
            }
        }

        catch (Exception e) {
            HTTP_REQUEST_ERROR error = getRequestException(e);
            if (request != null) {
                request.abort();
            }
            AppDebug.e(TAG, "executeHttpGet error=" + error + e + " isRetry=" + isRetry);

            //            //异常并且使用了http-dns,需要清除ip缓存
            //            if (!StringUtils.isEmpty(host)) {
            //                HttpDns.getInstance().SetErrorByHost(host);
            //            }

            //异常时重试一次
            if (isRetry) {
                result = executeHttpGet(host, url, encode, false);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                AppDebug.e(TAG, "executeHttpGet IO error=" + e);
            }
        }

        return result;
    }

    /**
     * 取得不同的错误类型
     * @param e
     * @return HTTP_REQUEST_ERROR
     */
    private static HTTP_REQUEST_ERROR getRequestException(Exception e) {
        HTTP_REQUEST_ERROR error;
        if (e instanceof UnsupportedEncodingException) {
            error = HTTP_REQUEST_ERROR.HTTP_UNSUPPORTED_ENCODING;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof SSLPeerUnverifiedException) {
            error = HTTP_REQUEST_ERROR.HTTP_SSL_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof ConnectException) {
            error = HTTP_REQUEST_ERROR.HTTP_CONNECT_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof SocketException) {
            error = HTTP_REQUEST_ERROR.HTTP_SOCKET_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof SocketTimeoutException) {
            error = HTTP_REQUEST_ERROR.HTTP_SOCKET_TIMEOUT_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof ConnectTimeoutException) {
            error = HTTP_REQUEST_ERROR.HTTP_CONNECT_TIMEOUT_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else if (e instanceof UnknownHostException) {
            error = HTTP_REQUEST_ERROR.HTTP_UNKNOWN_HOST_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        } else {
            error = HTTP_REQUEST_ERROR.HTTP_OTHER_ERROR;
            AppDebug.e(TAG, "sendRequest error mHttpRequestError" + error + "e=" + e);
        }
        return error;
    }
}
