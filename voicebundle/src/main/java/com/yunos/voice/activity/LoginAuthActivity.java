package com.yunos.voice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.google.zxing.WriterException;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.request.ASRUTRequest;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.tvtaobao.voicesdk.utils.QRCodeUtil;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.payment.alipay.AlipayAuthManager;
import com.yunos.voice.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/5/18
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class LoginAuthActivity extends BaseActivity {
    private String TAG = "LoginAuthActivity";

    private QRLoginListener qrLoginListener;
    private QRAuthListener qrAuthListener;
    private ImageView qrCode;
    private FrameLayout qrCodeStatus;
    private TextView reply, tvTips;
    private LinearLayout lytips;

    private String promptText = "";
    private String qrCodeType = "login";
    private String from = null;
    private String alipayId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(LoginAuthActivity.class.getName());

        qrCodeType = getIntent().getStringExtra("QRCodeType");
        alipayId = getIntent().getStringExtra("alipayId");
        from = getIntent().getStringExtra("from");
        if ("search".equals(from)) {
            promptText = "亲，请打开【支付宝】扫码登录，我们将为您安排下单。";
        } else {
            promptText = "亲，请打开【支付宝】扫码登录。";
        }

        setContentView(R.layout.activity_voice_login);
        reply = (TextView) findViewById(R.id.voice_login_reply);
        qrCode = (ImageView) findViewById(R.id.voice_login_qrcode);
        qrCodeStatus = (FrameLayout) findViewById(R.id.voice_login_status);
        lytips = (LinearLayout) findViewById(R.id.order_info_tip_layout);
        tvTips = (TextView) findViewById(R.id.order_info_tip);
        showPrompt();

        AlipayAuthManager.init(LoginAuthActivity.this);

        LogPrint.e(TAG, TAG + ".onCreate qrCodeType : " + qrCodeType + " ,alipayId : " + alipayId + " ,from : " + from);
        registerBroadcast();
    }

    /**
     * 设置一个退出页面的广播
     */
    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yunos.voice.LoginAuth.LOGOUT");
        registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if ("login".equals(qrCodeType)) {
            qrLoginListener = new QRLoginListener();
            AlipayAuthManager.doAuthLogin(qrLoginListener);
        } else {
            qrAuthListener = new QRAuthListener();
            AlipayAuthManager.doAuth(alipayId, qrAuthListener);
        }

    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        PageReturn pageReturn = new PageReturn();
        if (ActionType.BACK.equals(object.getIntent())) {
            pageReturn.isHandler = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }).start();
        }
        return pageReturn;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AlipayAuthManager.dispose();

        Intent intent = new Intent();
        intent.setAction("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH");
        intent.putExtra("status", "pause");
        sendOrderedBroadcast(intent, null);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(LoginAuthActivity.class.getName());
    }

    /**
     * 显示提示信息
     */
    private void showPrompt() {
        tvTips.setText("想退出扫码？可以说“返回”");
    }

    private class QRLoginListener implements AlipayAuthManager.AuthLoginListener {
        @Override
        public void onAuthQrGenerated(final String qrUrl) {
            LogPrint.i(TAG, TAG + ".QRLoginListener onQrCodeUrlGenerated");
            LoginAuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getQRCode(qrUrl);
                    qrCode.setImageBitmap(bitmap);
                    setReply(promptText);
                }
            });
        }

        @Override
        public void onAuthSuccess() {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthSuccess");
        }

        @Override
        public void onAuthFailure() {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthFailure");
            LoginAuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (qrAuthListener ==  null) {
                        qrAuthListener = new QRAuthListener();
                    }
                    AlipayAuthManager.doAuthLogin(qrLoginListener);
                    setReply("不好意思，登录失败。请重新扫码进行登录。");
                }
            });
        }

        @Override
        public void onAuthLoginResult(boolean success, Session session) {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthLoginResult success : " + success);
            if (success) {
                BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(LoginAuthActivity.this)));
                loginSuccessUT();

                Intent intent = new Intent();
                intent.setAction("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH");
                intent.putExtra("status", "success");
                sendOrderedBroadcast(intent, null);
                finish();
            } else {
                LoginAuthActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (qrAuthListener ==  null) {
                            qrAuthListener = new QRAuthListener();
                        }
                        AlipayAuthManager.doAuthLogin(qrLoginListener);
                        setReply("不好意思，登录失败。请重新扫码进行登录。");
                    }
                });
            }
        }
    }

    private class QRAuthListener implements AlipayAuthManager.AuthListener {

        @Override
        public void onAuthQrGenerated(final String qrUrl) {
            LoginAuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getQRCode(qrUrl);
                    qrCode.setImageBitmap(bitmap);
                    setReply("亲，请打开【支付宝】扫码付款，我们将为您安排下单。");
                }
            });
        }

        @Override
        public void onAuthSuccess() {
            LogPrint.i(TAG, TAG + ".QRAuthListener onAuthSuccess");
            BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(LoginAuthActivity.this)));
            loginSuccessUT();

            Intent intent = new Intent();
            intent.setAction("com.yunos.tvtaobao.VOICE.ALIPAY.AUTH");
            intent.putExtra("status", "success");
            sendOrderedBroadcast(intent, null);
            finish();
        }

        @Override
        public void onAuthFailure() {
            LogPrint.i(TAG, TAG + ".QRAuthListener onAuthFailure");
            LoginAuthActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (qrAuthListener ==  null) {
                        qrAuthListener = new QRAuthListener();
                    }
                    AlipayAuthManager.doAuth(alipayId, qrAuthListener);
                    setReply("不好意思，授权失败。请重新扫码进行授权。");
                }
            });
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            if ("login".equals(qrCodeType)) {
                intent.setAction("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH");
            } else {
                intent.setAction("com.yunos.tvtaobao.VOICE.ALIPAY.AUTH");
            }
            intent.putExtra("status", "cancel");
            sendOrderedBroadcast(intent, null);
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setReply(String statusMsg) {
        reply.setText(statusMsg);
        ASRNotify.getInstance().playTTS(statusMsg);
    }

    private Bitmap getQRCode(String url) {
        LogPrint.i(TAG, "getQRCode url : " + url);
        Bitmap qrBitmap = null;
        try {
//            Drawable drawable = getResources().getDrawable(R.drawable.icon_zhifubao);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_zhifubao);
//            if (drawable != null) {
//                BitmapDrawable bd = (BitmapDrawable) drawable;
//                icon = bd.getBitmap();
//            }
            ViewGroup.LayoutParams para = qrCode.getLayoutParams();
            LogPrint.e("SSD", "layout width : " + para.width + "height : " + para.height);

            qrBitmap = QRCodeUtil.create2DCode(url, para.width, para.height, icon);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrBitmap;
    }

    /**
     * 淘客打点监听
     */
    private  class TaokeBussinessRequestListener extends BizRequestListener<JSONObject> {

        public TaokeBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
            LogPrint.d(TAG, data.toString());
            long historyTime = System.currentTimeMillis() + 604800000;//7天
            SharedPreferencesUtils.saveTvBuyTaoKe(LoginAuthActivity.this, historyTime);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public String getPageName() {
        return "VoiceSystem_expore_zhifubao";
    }

    /**
     * 登录成功打点
     */
    private void loginSuccessUT() {
        Utils.utCustomHit(getPageName(), "login_success", getPageProperties());

        BusinessRequest.getBusinessRequest().baseRequest(new ASRUTRequest("login_success", LoginAuthActivity.class.getCanonicalName()), null, false);
    }
}
