package com.yunos.tvtaobao.payment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ali.auth.third.core.callback.NQrCodeLoginCallback;
import com.ali.auth.third.core.config.ConfigManager;
import com.ali.auth.third.core.context.KernelContext;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.offline.NQRView;
import com.ali.auth.third.offline.webview.AuthWebView;
import com.ali.auth.third.offline.webview.BridgeWebChromeClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaobaoQRCodeGenerator {

    private QRCodeListener listener;

    public void setListener(QRCodeListener listener) {
        this.listener = listener;
    }

    public void onQRLoginSuccess(Session session) {
        if (listener != null)
            listener.onQRLoginSuccess(session);
    }

    public void onQRLoginFailure(int code, String msg) {
        if (listener != null)
            listener.onQRLoginFailure(code, msg);
    }

    public void onQRScanCodeSuccess(int code, String msg) {
        if (listener != null) {
            listener.onQRScanCodeSuccess(code, msg);
        }
    }

    public void onQROrdverdue(int code, String msg) {
        if (listener != null) {
            listener.onQROverdue(code, msg);
        }
    }

    public void onQRImageLoaded(Bitmap qrBitmap) {
        if (listener != null) {
            listener.onQrCodeUrlGenerated(qrBitmap);
        }
    }

    public interface QRCodeListener {
        void onQrCodeUrlGenerated(Bitmap bitmap);

        void onQRLoginSuccess(Session session);

        void onQRScanCodeSuccess(int code, String message);

        void onQRLoginFailure(int code, String message);

        void onQROverdue(int code, String msg);
    }

    private NQrCodeLoginCallback.NQrCodeLoginController controller;

    private NQrCodeLoginCallback callback = new NQrCodeLoginCallback() {
        @Override
        public void onQrImageLoaded(String token, Bitmap bitmap, NQrCodeLoginController nQrCodeLoginController) {
            onQRImageLoaded(bitmap);
        }

        @Override
        public void onQrImageStatusChanged(String token, int status) {
            onQRScanCodeSuccess(status, null);
        }

        @Override
        public void onSuccess(Session session) {
            onQRLoginSuccess(session);
        }

        @Override
        public void onFailure(int code, String message) {
            onQRLoginFailure(code, message);
        }
    };

    public void stopQR() {
        if (controller != null) {
            controller.cancle();
            controller = null;
        }
    }

    public void startQR() {
        NQRView.start(200, 200, callback);
    }
}
