package com.yunos.tvtaobao.tradelink.paidadvert;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.tradelink.R;

import java.util.Map;

public class PaidAdvertActivity extends TradeBaseActivity {

    private static final String TAG = "PaidAdvertActivity";
    // 图片下载管理
    private ImageLoaderManager mImageLoaderManager;
    // 图片显示的View
    private ImageView mPaiadvertPicView;
    // 进入其他界面的URI
    private String mUri;
    private String mPic_Url;

    private DisplayImageOptions mImageOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDebug.i(TAG, "onCreate... ");
        setContentView(R.layout.ytm_activity_paidadvert);

        // 获取外部传入的数据
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mUri = intent.getStringExtra("uri");
        mPic_Url = intent.getStringExtra("picurl");
        AppDebug.i(TAG, "mUri = " + mUri + ";  mPic_Url = " + mPic_Url);
        initValue();
        if (TextUtils.isEmpty(mPic_Url)) {
            finish();
            return;
        }

        LoadPaidAdvertPic();
    }

    /**
     * 初始化变量值
     */
    private void initValue() {
        AppDebug.i(TAG, "initValue... ");
        mPaiadvertPicView = (ImageView) this.findViewById(R.id.paiadvert_pic_view);

        // 配置显示选项
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(false).cacheInMemory(true).setDisplayShow(true)
                .build();

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
    }

    private void LoadPaidAdvertPic() {
        AppDebug.i(TAG, "LoadPaidAdvertPic ... mPic_Url = " + mPic_Url);
        if (!TextUtils.isEmpty(mPic_Url)) {
            mPaiadvertPicView.setVisibility(View.VISIBLE);
            mImageLoaderManager.displayImage(mPic_Url, mPaiadvertPicView, mImageOptions);
        }
    }

    /**
     * 进入其他界面
     */
    private void enterOtherActivity() {
        AppDebug.i(TAG, "enterOtherActivity... mUri = " + mUri);
        String uri = mUri;
        if (!TextUtils.isEmpty(uri)) {
            try {
                Intent intent = new Intent();
                intent.setData(Uri.parse(uri));
                startActivity(intent);

                TBS.Adv.ctrlClicked(CT.Button, getPageName(), "URI=" + uri, "uuid=" + CloudUUIDWrapper.getCloudUUID(),
                        "from_channel=" + getmFrom());
                finish();
            } catch (Exception e) {
                String name = PaidAdvertActivity.this.getResources().getString(R.string.ytsdk_themeMarket_not_open);
                Toast.makeText(PaidAdvertActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                enterOtherActivity();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    public String getPageName() {
        return "alipay_promotion";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mUri)) {
            p.put("URI", mUri);
        }
        return p;
    }
}
