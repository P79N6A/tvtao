package com.yunos.tvtaobao.payment.logout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.service.impl.CredentialManager;
import com.ali.auth.third.offline.login.LoginService;
import com.ali.auth.third.offline.login.callback.LogoutCallback;
import com.yunos.tvtaobao.payment.MemberSDKLoginHelper;
import com.yunos.tvtaobao.payment.MemberSDKLoginStatus;
import com.yunos.tvtaobao.payment.R;
import com.yunos.tvtaobao.payment.alipay.request.ReleaseContractRequest;
import com.yunos.tvtaobao.payment.analytics.Utils;
import com.yunos.tvtaobao.payment.broadcast.BroadcastLogin;

import mtopsdk.mtop.common.ApiID;
import mtopsdk.mtop.common.DefaultMtopCallback;
import mtopsdk.mtop.common.MtopFinishEvent;
import mtopsdk.mtop.intf.Mtop;

public class LogoutActivity extends Activity {
    private static String TAG = "LogoutActivity";
    private TextView tvLogout;
    private TextView tvNotLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvLogout.requestFocus();
        Utils.utPageAppear("page_logoff", "page_logoff");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.utPageDisAppear("page_logoff");
    }

    private void initView() {
        tvLogout = (TextView) findViewById(R.id.tv_logout);
        tvNotLogout = (TextView) findViewById(R.id.tv_not_logout);
        tvLogout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvLogout.setTextColor(getResources().getColor(R.color.logout_focused_color));
                } else {
                    tvLogout.setTextColor(getResources().getColor(R.color.logout_unfocused_color));
                }
            }
        });
        tvNotLogout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvNotLogout.setTextColor(getResources().getColor(R.color.logout_focused_color));
                } else {
                    tvNotLogout.setTextColor(getResources().getColor(R.color.logout_unfocused_color));
                }
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utCustomHit("sure_logoff", Utils.getProperties());
                LoginService service = MemberSDK.getService(LoginService.class);
                if (isLogin()) {
                    MemberSDKLoginStatus.setLoggingOut(true);
                    service.logout(new LogoutCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, " memberSDK loginOut succsss");
                            CredentialManager.INSTANCE.logout();
                            Toast.makeText(LogoutActivity.this, "成功登出", Toast.LENGTH_SHORT).show();
                            BroadcastLogin.sendBroadcastLogin(LogoutActivity.this, false);
                            finish();
                            MemberSDKLoginStatus.setLoggingOut(false);

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.d(TAG, " memberSDK loginOut failure");
                            MemberSDKLoginStatus.setLoggingOut(false);
                        }
                    });
                    ReleaseContractRequest request = new ReleaseContractRequest();
                    ApiID id = Mtop.instance(LogoutActivity.this).build(request, null).useWua().addListener(new DefaultMtopCallback() {
                        @Override
                        public void onFinished(MtopFinishEvent event, Object context) {
                            Log.d("test", "releaseContract response: " + event.getMtopResponse().getDataJsonObject());
                            super.onFinished(event, context);
                        }
                    }).asyncRequest();
                } else {
                    Toast.makeText(LogoutActivity.this, getString(R.string.ytsdk_account_error),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        tvNotLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utCustomHit("cancel_logoff", Utils.getProperties());
                finish();
            }
        });


    }


    public boolean isLogin() {
        LoginService service = MemberSDK.getService(LoginService.class);

        return service == null ? false : service.checkSessionValid();
    }
}
