package com.yunos.tvtaobao.payment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.auth.third.core.callback.NQrCodeLoginCallback;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.offline.LoginFragment;
import com.ali.auth.third.offline.NQRView;
import com.ali.auth.third.offline.webview.AuthWebView;
import com.google.zxing.WriterException;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthLoginTask;
import com.yunos.tvtaobao.payment.analytics.Utils;
import com.yunos.tvtaobao.payment.broadcast.BroadcastLogin;
import com.yunos.tvtaobao.payment.config.DebugConfig;
import com.yunos.tvtaobao.payment.qrcode.QRCodeManager;
import com.yunos.tvtaobao.payment.request.ScanBindRequest;
import com.yunos.tvtaobao.payment.utils.SPMConfig;
import com.yunos.tvtaobao.payment.utils.UtilsDistance;
import com.yunos.tvtaobao.payment.view.AlphaDialog;

import java.util.ArrayList;
import java.util.Map;

import mtopsdk.mtop.domain.MtopRequest;
import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.Mtop;


/**
 *
 */
public class CustomLoginFragment extends LoginFragment implements ViewPager.OnPageChangeListener {

    private static String TAG = "memberSDK";
    private static String mPageName = "Page_login";


    private ImageView ivTaobaoLoginQR;
    private ImageView ivAlipayLoginQR;
    private Bitmap taoBaoLogo;
    private Bitmap alipayLogo;
    private ViewPager viewPagerQR;
    private ArrayList<View> viewList;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView ivQRScanSuccess;
    private AlphaDialog dialog;
    private Handler handler;

    private AlphaDialog loginFailureDialog;

    private AlipayAuthLoginTask alipayAuthTask;

    private NQrCodeLoginCallback.NQrCodeLoginController controller;

    private NQrCodeLoginCallback loginCallback = new NQrCodeLoginCallback() {
        public void onQrImageLoaded(String qrToken, Bitmap qrBitmap, NQrCodeLoginController nQrCodeLoginController) {
            controller = nQrCodeLoginController;
            taoBaoLogo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_taobao_qr_small);
            Bitmap bitmap = QRCodeManager.create2DCode(qrBitmap, UtilsDistance.dp2px(getContext(), 350),
                    UtilsDistance.dp2px(getContext(), 350), taoBaoLogo, UtilsDistance.dp2px(getContext(), 64), UtilsDistance.dp2px(getContext(), 64));
            ivTaobaoLoginQR.setImageBitmap(bitmap);
        }

        @Override
        public void onQrImageStatusChanged(String qrToken, int status) {
            if (status == 5) {
                onQRScanCodeSuccess(status, "扫码成功");
            } else if (status == 6) {
                onQROrdverdue(status, "超时");
            } else if (status != 4) {
                showLoginFailure("状态码：" + status);
            }
        }

        @Override
        public void onSuccess(Session session) {
            onQRLoginSuccess(session);
            if (getActivity() != null) {
                getActivity().finish();//fixme 仅此处可用，如在业务场景，则回调不能简单的finish activity，否则会影响业务
            }
            Log.d(TAG, " memberSDK login succsss" + session.openSid);
        }

        @Override
        public void onFailure(int code, String msg) {
            onQRLoginFailure(code, msg);
        }
    };

    MtopRequest mtopRequest;

    protected void initViews(View view) {

        handler = new Handler();
        super.initViews(view);
//        mWebView.loadUrl(mUrl);
//        mWebView.addBridgeObject(LoginContext.bindBridgeName,mLoginBridge);
//        if (mWebView != null) {
//            mWebView.setVisibility(View.GONE);
//        }
        startTaobaoQR();
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_qr_invalid, null);
        dialog = new AlphaDialog(getContext(), dialogView);
        initViewPager(view);

        if (DebugConfig.whetherIsMonkey()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (CustomLoginFragment.this != null && CustomLoginFragment.this.getActivity() != null) {
                        CustomLoginFragment.this.getActivity().finish();
                    }
                }
            }, 30000);
        }
    }

    @Override
    protected AuthWebView createTaeWebView() {//防止创建webView
        return null;
    }

    private void initViewPager(View view) {
        viewPagerQR = (ViewPager) view.findViewById(R.id.vp_qr);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View taobaoLoginQR = inflater.inflate(R.layout.user_taobao_login_qr, null);
        View alipayLoginQR = inflater.inflate(R.layout.user_alipay_login_qr, null);
        ivTaobaoLoginQR = (ImageView) taobaoLoginQR.findViewById(R.id.iv_qr);
        ivQRScanSuccess = (ImageView) taobaoLoginQR.findViewById(R.id.iv_qr_scan_success);
        ivAlipayLoginQR = (ImageView) alipayLoginQR.findViewById(R.id.iv_qr);
        viewList = new ArrayList<View>();
        viewList.add(taobaoLoginQR);
        viewList.add(alipayLoginQR);
        viewPagerAdapter = new ViewPagerAdapter(viewList);
        viewPagerQR.setAdapter(viewPagerAdapter);
        viewPagerQR.addOnPageChangeListener(this);

    }

    @Override
    protected int getLayoutContent() {
        return R.layout.user_login_fragment_test;
    }

    public void onQRLoginSuccess(final Session session) {
//        Log.d(TAG, "onQRLoginSuccess" + session.openSid + "--" + session.nick);
        if (controller != null) {
            controller.cancle();
            controller = null;
        }
        BroadcastLogin.sendBroadcastLogin(getActivity(), true);
//        if (getActivity() != null && !getActivity().isFinishing()) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getContext(), "欢迎回来，" + session.nick, Toast.LENGTH_LONG).show();
//                }
//            });
//        }

    }

    private void showLoginFailure(final String msg) {

        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View dialogView = View.inflate(getContext() == null ? (getActivity() == null ? getActivity().getApplicationContext() : getActivity()) : getContext(), R.layout.dialog_login_fail, null);
                    TextView tv = (TextView) dialogView.findViewById(R.id.tv);
                    TextView tv_msg = (TextView) dialogView.findViewById(R.id.msg);
                    if (!TextUtils.isEmpty(msg) && tv_msg != null) {
                        tv_msg.setText(msg);
                    }
                    if (loginFailureDialog != null)
                        loginFailureDialog.dismiss();
                    loginFailureDialog = new AlphaDialog(getContext() == null ? (getActivity() == null ? getActivity().getApplicationContext() : getActivity()) : getContext(), dialogView);
                    loginFailureDialog.setCancelable(false);
                    loginFailureDialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (loginFailureDialog != null)
                                loginFailureDialog.dismiss();
                            loginFailureDialog = null;
                        }
                    }, 3000);
                }
            });
        }


    }

    public void onQRLoginFailure(int code, String msg) {
        Log.d(TAG, "onQRLoginFailure" + code);
        showLoginFailure(msg);
    }

    public void onQRScanCodeSuccess(int code, String msg) {
        Log.d(TAG, "onQRScanCodeSuccess   " + code);
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivQRScanSuccess.setVisibility(View.VISIBLE);

                }
            });
        }

        mtopRequest = new ScanBindRequest(getActivity());
        MtopResponse response = Mtop.instance(getActivity()).build(mtopRequest, null).useWua().syncRequest();


    }


    public void onQROrdverdue(int code, String msg) {
        Log.d(TAG, "onQROrdverdue  code = " + code + "，msg = " + msg);
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //todo
                    if (viewPagerQR.getCurrentItem() == 0 && getActivity() != null && !getActivity().isFinishing()) {
                        dialog.show();
                    }
                    startTaobaoQR();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            ivQRScanSuccess.setVisibility(View.GONE);
                        }
                    }, 3000);
                    Utils.utCustomHit(mPageName, viewPagerQR.getCurrentItem() == 0 ? "Expore_taobao_login_Disuse" : "Expore_zhifubao_login_Disuse"
                            , viewPagerQR.getCurrentItem() == 0 ? initTBSProperty(SPMConfig.CUSTOM_LOGIN_TB_DISUSE) : initTBSProperty(SPMConfig.CUSTOM_LOGIN_ZHIFUBAO_DISUSE));

                }
            });
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
//            if (controller != null) {
//                controller.cancle();
//            }
//            controller = null;
            startAlipay();

            Utils.utCustomHit(mPageName, "Expore_zhifubao", initTBSProperty(SPMConfig.CUSTOM_LOGIN_ZHIFUBAO_EXPORE));

        } else {
            if (alipayAuthTask != null) {
                alipayAuthTask.setListener(null);
                alipayAuthTask.cancel(true);
                alipayAuthTask = null;
            }
//            startTaobaoQR();
            Utils.utCustomHit(mPageName, "Expore_taobao", initTBSProperty(SPMConfig.CUSTOM_LOGIN_TB_EXPORE));
        }
    }

    public Map<String, String> initTBSProperty(String spm) {
        Map<String, String> p = Utils.getProperties();
        p.put("spm", spm);
        return p;
    }


    @MainThread
    private void startTaobaoQR() {
        if (controller != null)
            controller.cancle();
        controller = null;
//        ivQRScanSuccess.setVisibility(View.GONE);
        NQRView.start(300, 300, loginCallback);
    }

    @MainThread
    private void startAlipay() {
        if (alipayAuthTask != null) {
            alipayAuthTask.cancel(true);
            alipayAuthTask.setListener(null);
        }
        alipayAuthTask = new AlipayAuthLoginTask(getContext());
        alipayAuthTask.setListener(new AlipayAuthLoginTask.AlipayAuthTaskListener() {
            @Override
            public void onReceivedAlipayAuthStateNotify(AlipayAuthLoginTask.AlipayAuthTaskResult result) {
                if (result.getStep() == AlipayAuthLoginTask.STEP_GEN && result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS) {
                    final String url = (String) result.getObject();
                    try {
                        if (alipayLogo == null)
                            alipayLogo = BitmapFactory.decodeResource(getResources(), R.drawable.payment_icon_alipay);
                        Bitmap bitmap = QRCodeManager.create2DCode(url, UtilsDistance.dp2px(getContext(), 350),
                                UtilsDistance.dp2px(getContext(), 350), alipayLogo, UtilsDistance.dp2px(getContext(), 64), UtilsDistance.dp2px(getContext(), 64));
                        ivAlipayLoginQR.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                } else if (result.getStep() == AlipayAuthLoginTask.STEP_LOGIN) {
                    if (result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS) {
                        getActivity().finish();
                    } else {
                        if (result.getObject() instanceof String) {
                            showLoginFailure((String) result.getObject());
                        }
                    }
                }
            }
        });
        alipayAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

//    private void onLoginSuccess(final Session session) {
//        if (getActivity() != null && !getActivity().isFinishing())
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getContext(), "欢迎回来，" + session.nick, Toast.LENGTH_LONG).show();
//                }
//            });
//    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alipayAuthTask != null) {
            alipayAuthTask.setListener(null);
            alipayAuthTask.cancel(true);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (controller != null) {
            controller.cancle();
        }
        controller = null;
        MemberSDKLoginStatus.compareAndSetLogin(true, false);
    }


    protected void enterUT() {
//        if (isTbs()) {
        //如果这应页面需要做代码统计，就不能不设置名称
        if (TextUtils.isEmpty(mPageName)) {
            throw new IllegalArgumentException("The PageName was null and TBS is open");
        }
        Utils.utPageAppear(mPageName, mPageName);
//        AppDebug.i(TAGS, TAGS + ".enterUT end mPageName=" + mPageName);
//        }
    }

    /**
     * 退出UT
     */
    protected void exitUT() {
        if (!TextUtils.isEmpty(mPageName)) {
            Map<String, String> p = Utils.getProperties();
            p.put("spm-cnt", SPMConfig.CUSTOM_LOGIN_FRAGMENT);
//            AppDebug.i(TAGS, TAGS + ".exitUI TBS=updatePageProperties(" + p + ")");
            Utils.utUpdatePageProperties(mPageName, p);
            Utils.utPageDisAppear(mPageName);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        enterUT();
    }

    @Override
    public void onPause() {
        super.onPause();
        exitUT();
    }
}
