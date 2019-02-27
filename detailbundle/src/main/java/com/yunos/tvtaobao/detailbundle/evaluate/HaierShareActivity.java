package com.yunos.tvtaobao.detailbundle.evaluate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.WriterException;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper;
import com.yunos.tvtaobao.detailbundle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HaierShareActivity extends BaseActivity {
    private final String TAG = "Page_TbDetail_Share";

    private RelativeLayout rlLogin;

    private RelativeLayout rlNotLogin;

    private RelativeLayout ivShareQRCodeBgLogin;

    private ImageView ivShareQrCode;
    private ImageView ivShareQrCodeUnlogin;
    private ImageView ivLogin;
    private ImageView ivShareHaier;
    private String mItemId;
    private String mItemName;
    private Bitmap taoBaoLogo;
    private String strQR;
    private BusinessRequest mBusinessRequest;

    private String toutuUrl;
    private String nowPrice;
    private String oldPrice;
    private String postage;

    private AccountActivityHelper.OnAccountStateChangedListener mOnAccountStateChangedListener;

    public static void launch(Activity activity, String itemid, String itemName, String toutuUrl, String nowPrice, String oldPrice, String postage) {
        Intent intent = new Intent(activity, HaierShareActivity.class);
        intent.putExtra("itemid", itemid);
        intent.putExtra("itemName", itemName);
        intent.putExtra("toutuUrl", toutuUrl);
        intent.putExtra("nowPrice", nowPrice);
        intent.putExtra("oldPrice", oldPrice);
        intent.putExtra("postage", postage);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_haier);
        registerLoginListener();
        CoreApplication.getLoginHelper(this).addSyncLoginListener(new LoginHelper.SyncLoginListener() {
            @Override
            public void onLogin(boolean isSuccess) {
                if (isSuccess) {
                    getQR();
                }
            }
        });
        mItemId = getIntent().getStringExtra("itemid");
        mItemName = getIntent().getStringExtra("itemName");
        toutuUrl = getIntent().getStringExtra("toutuUrl");
        oldPrice = getIntent().getStringExtra("oldPrice");
        nowPrice = getIntent().getStringExtra("nowPrice");
        postage = getIntent().getStringExtra("postage");

        // 初始化请求类
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        ivShareQrCode = (ImageView) findViewById(R.id.iv_share_qr_code);
        ivShareQrCodeUnlogin = (ImageView) findViewById(R.id.iv_share_qr_code_unlogin);
        ivShareHaier = (ImageView) findViewById(R.id.iv_share_haier);
        ivShareQRCodeBgLogin = (RelativeLayout) findViewById(R.id.iv_share_qr_code_bg_login);
        ivLogin = (ImageView) findViewById(R.id.iv_login);
        rlLogin = (RelativeLayout) findViewById(R.id.rl_login);
        rlNotLogin = (RelativeLayout) findViewById(R.id.rl_not_login);
        taoBaoLogo = BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_taobao_small);
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                    CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(HaierShareActivity.this, false);
                }
            }
        });

        ivShareHaier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkNetwork()) {
                    return;
                }
                share2HaierApp();
            }
        });

        getQR();
    }


    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mItemId)) {
            p.put("item_id", mItemId);
        }
        if (!TextUtils.isEmpty(getAppName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("from_app", getAppName() + AppInfo.getAppVersionName());
        }
        if (!TextUtils.isEmpty(mItemName)) {
            p.put("item_name", mItemName);
        }
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }
        p.put(SPMConfig.SPM_CNT, "a2o0j.7984570.0.0");
        return p;
    }

    /**
     * 检查网络状态
     *
     * @return
     */
    private boolean checkNetwork() {
        boolean result = false;
        if (!NetWorkUtil.isNetWorkAvailable()) {
            result = false;
            showNetworkErrorDialog(false);
        } else {
            removeNetworkOkDoListener();
            result = true;
        }
        return result;
    }

    //跳转到海尔分享应用
    private void share2HaierApp() {
        try {
            JSONObject data = new JSONObject();
            data.put("MSG_TITLE", mItemName);//商品标题
            data.put("PICURL_LIST", toutuUrl);//商品头图（头图地址）
            data.put("INTENT_NAME", "tvtaobao://home?app=taobaosdk&module=detail&itemId=" + mItemId
                    + "&from=com.haier.haiertv.ims.share");//商品详情页链接（URI）
            if (!TextUtils.isEmpty(nowPrice) && !TextUtils.isEmpty(oldPrice)) {
                data.put("MSG_TEXT", nowPrice + "," + oldPrice);//现价、原价
            } else {
                data.put("MSG_TEXT","￥" + nowPrice);//现价
            }
            data.put("MSG_SUBTYPE", postage);//邮费
            Intent intent = new Intent("com.haier.haiertv.ims.share");
            intent.putExtra(Intent.EXTRA_TEXT, data.toString());
            intent.putExtra("SHARETYPE", "SHOPPING");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getQR() {
        strQR = "http://tvos.taobao.com/wow/yunos/act/taokouling?a=";
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            ivLogin.setVisibility(View.GONE);
            String userId = User.getUserId();
            String userIdResult = "0";
            if (userId != null) {
                Long userIdLong = Long.parseLong(userId);
                userIdResult = String.format("%o", userIdLong);
            }
            strQR += userIdResult + "," + mItemId;
            strQR += "," + CloudUUIDWrapper.getCloudUUID();
            Log.e("strQR", strQR);
        } else {
            ivLogin.setVisibility(View.VISIBLE);
            ivLogin.requestFocus();
            strQR += "0," + mItemId;
            Log.e("strQR", strQR);
        }
        try {
            Bitmap bitmap = QRCodeManager.create2DCode(strQR, dp2px(400), dp2px(400), taoBaoLogo);
            ivShareQrCode.setImageBitmap(bitmap);
            ivShareQrCodeUnlogin.setImageBitmap(bitmap);
            ivShareQRCodeBgLogin.requestFocus();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            rlNotLogin.setVisibility(View.GONE);
            rlLogin.setVisibility(View.VISIBLE);

        } else {
            rlNotLogin.setVisibility(View.VISIBLE);
            rlLogin.setVisibility(View.GONE);
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
