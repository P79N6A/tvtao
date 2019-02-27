package com.yunos.tvtaobao.answer.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tvtaobao.answer.dialog.HQVideoDialog;
import com.yunos.tvtaobao.answer.plugin.HQMessagePlugin;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.lang.ref.WeakReference;
import java.net.URLDecoder;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class HQLiveActivity extends TaoBaoBlitzActivity {
    private static String TAG = "HQLiveActivity";
    private String page;
    private HQMessagePlugin hqPlugin;
    private HQVideoDialog hqVideoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(HQLiveActivity.class.getName());

        hqPlugin = new HQMessagePlugin(new WeakReference<HQLiveActivity>(this));

        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.v(TAG, TAG + ".onCreate.page = " + page);
        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        page = URLDecoder.decode(page);

        if (!Config.isDebug() && !StringUtil.isInnerUrl(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_invalid_h5_url), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        onInitH5View(page);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hqVideoDialog != null) {
            hqVideoDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        AppDebug.i(TAG, TAG + ".onDestroy");
        super.onDestroy();
        onRemoveKeepedActivity(HQLiveActivity.class.getName());
    }

    @Override
    public void onWebviewPageDone(String url) {
        super.onWebviewPageDone(url);
        AppDebug.i(TAG, TAG + ".onWebviewPageDone");
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }

    /**
     * Blitz页面通信，初始化视频直播的位置。
     */
    public void initVideoArea(int width, int height, int marginTop, int marginLeft) {
        AppDebug.i(TAG, TAG + ".initVideoArea");
        if (hqVideoDialog == null) {
            hqVideoDialog = new HQVideoDialog(this);
        }
        hqVideoDialog.setVideoAreaSizeForJS(width, height, marginTop, marginLeft);

        if (!hqVideoDialog.isShowing()) {
            hqVideoDialog.show();
        }
    }

    /**
     * Blitz页面通信，控制视频播放
     * @param url
     */
    public void playVideo(String url) {
        AppDebug.i(TAG, TAG + ".playVideo");
        if (hqVideoDialog != null) {
            hqVideoDialog.play(url);
        }
    }
}
