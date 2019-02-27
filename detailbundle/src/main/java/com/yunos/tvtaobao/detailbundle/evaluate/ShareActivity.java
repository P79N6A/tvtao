package com.yunos.tvtaobao.detailbundle.evaluate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.detailbundle.R;

import java.util.Map;
import java.util.Properties;

/**
 * 详情页分享
 */

public class ShareActivity extends BaseActivity {
    private final String TAG = "Page_TbDetail_Share";
    private ImageView ivShareQrCode;
    private ImageView ivLogin;
    private TextView tvLogin;
    private String itemId;
    private String itemName;
    private Bitmap taoBaoLogo;
    private String strQR = "http://tvos.taobao.com/wow/yunos/act/taokouling?a=";

    public static void launch(Activity activity, String itemid, String itemName) {
        Intent intent = new Intent(activity, ShareActivity.class);
        intent.putExtra("itemId", itemid);
        intent.putExtra("itemName", itemName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        registerLoginListener();
        CoreApplication.getLoginHelper(this).addSyncLoginListener(new LoginHelper.SyncLoginListener() {
            @Override
            public void onLogin(boolean isSuccess) {
                if (isSuccess) {
                    showQR();
                }
            }
        });
        itemId = getIntent().getStringExtra("itemId");
        itemName = getIntent().getStringExtra("itemName");
        ivShareQrCode = (ImageView) findViewById(R.id.iv_share_qr_code);
        ivLogin = (ImageView) findViewById(R.id.iv_login_1);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        taoBaoLogo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_taobao_small);
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                    CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(ShareActivity.this, false);
                }
            }
        });
        showQR();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(itemId)) {
            p.put("item_id", itemId);
        }
        if (!TextUtils.isEmpty(getAppName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("from_app", getAppName() + AppInfo.getAppVersionName());
        }
        if (!TextUtils.isEmpty(itemName)) {
            p.put("item_name", itemName);
        }
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        p.put(SPMConfig.SPM_CNT, "a2o0j.11292291.0.0");

        return p;
    }

    /**
     * 显示二维码
     */
    public void showQR() {
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            ivLogin.setVisibility(View.GONE);
            tvLogin.setVisibility(View.GONE);
            String userId = User.getUserId();
            String userIdResult = "0";
            if (!TextUtils.isEmpty(userId)) {
                Long userIdLong = Long.parseLong(userId);
                userIdResult = String.format("%o", userIdLong);
            }
            strQR += userIdResult + "," + itemId;
            strQR += "," + CloudUUIDWrapper.getCloudUUID();
            Log.e("strQR", strQR);
        } else {
            ivLogin.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.VISIBLE);
            ivLogin.requestFocus();
            strQR += "0," + itemId;
            Log.e("strQR", strQR);
        }
        try {
            Bitmap bitmap = QRCodeManager.create2DCode(strQR, Utils.dp2px(this, 384),
                    Utils.dp2px(this, 384), taoBaoLogo);
            ivShareQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            ivLogin.setVisibility(View.GONE);
            tvLogin.setVisibility(View.GONE);
        } else {
            ivLogin.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
