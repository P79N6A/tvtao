package com.yunos.tvtaobao.biz;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.WriterException;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.QRCodeDialog;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.businessview.R;

/**
 * Created by huangdaju on 17/6/19.
 */

public class TradeBaseActivity extends BaseActivity {

    // 二维码
    protected QRCodeDialog mQRCodeDialog;
    protected TvTaoBaoDialog mSuccessAddCartDialog;

    //访问来源
    protected String mFROM = "tvhongbao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStartActivityNetWorkError() {
        showNetworkErrorDialog(false);
    }

    /**
     * 定义应用关键字，和pageName合并拼成一个页面名称
     *
     * @return
     */
    protected String getAppTag() {
        return "Tt";
    }

    /**
     * 获取二维码图片
     *
     * @param itemUrl
     * @param icon
     * @return
     */
    public Bitmap getQrCodeBitmap(String itemUrl, Bitmap icon) {
        Bitmap qrBitmap = null;
        int width = (int) getResources().getDimensionPixelSize(R.dimen.dp_422);
        int height = width;
        try {
            qrBitmap = QRCodeManager.create2DCode(itemUrl, width, height, icon);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return qrBitmap;
    }

    /**
     * 显示商品二维码
     *
     * @param itemUrl
     */
    public void showItemQRCodeFromUrl(String text, String itemUrl, Bitmap icon, boolean show,
                                      DialogInterface.OnKeyListener onKeyListener) {
        Bitmap qrBitmap = getQrCodeBitmap(itemUrl, icon);

        onQRCodeDialog(text, qrBitmap, show, onKeyListener);
    }

    /**
     * 显示商品二维码
     *
     * @param itemId
     */
    public void showItemQRCodeFromItemId(String text, String itemId, Bitmap icon, boolean show,
                                         DialogInterface.OnKeyListener onKeyListener) {
        String itemUrl = "http://m.tb.cn/ZvJs9c?id=" + itemId + "&orderMarker=v:w-ostvuuid*"
                + CloudUUIDWrapper.getCloudUUID() + ",w-ostvclient*tvtaobao";
        AppDebug.i(TAG, "ItemQRCode:" + itemUrl);
        showItemQRCodeFromUrl(text, itemUrl, icon, show, onKeyListener);
    }

    /**
     * 显示商品二维码
     *
     * @param itemId
     */
    public void showItemQRCodeFromItemId(String text, String itemId, Bitmap icon, boolean show,
                                         DialogInterface.OnKeyListener onKeyListener, boolean isFeizhu) {
        StringBuilder params = new StringBuilder();
        params.append(itemId);
        if (isFeizhu) {
            params.append("&hybrid=true");
        }
        params.append("&w-ostvuuid=");
        params.append(CloudUUIDWrapper.getCloudUUID());
        params.append("&w-ostvclient=tvtaobao&orderMarker=v:w-ostvuuid*");
        params.append(CloudUUIDWrapper.getCloudUUID());
        params.append(",w-ostvclient*tvtaobao");
        String itemUrl = "http://m.tb.cn/ZvCmA0?id=" + params.toString();
        AppDebug.i(TAG, "ItemQRCode:" + itemUrl);
        showItemQRCodeFromUrl(text, itemUrl, icon, show, onKeyListener);
    }

    /**
     * 显示 当加入购物车成功对话框
     *
     * @param show
     * @param positiveListener
     * @param negativeListener
     */
    public void showSuccessAddCartDialog(boolean show, DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnClickListener negativeListener, DialogInterface.OnKeyListener onKeyListener) {
        onSuccessAddCartDialog(show, positiveListener, negativeListener, onKeyListener);
    }

    /**
     * 显示二维码对话框
     *
     * @param show
     */
    private void onQRCodeDialog(String text, Bitmap bitmap, boolean show, DialogInterface.OnKeyListener onKeyListener) {
        if (mQRCodeDialog != null && mQRCodeDialog.isShowing()) {
            mQRCodeDialog.dismiss();
            mQRCodeDialog = null;
        }

        if (isFinishing()) {
            return;
        }

        mQRCodeDialog = new QRCodeDialog.Builder(this).setQRCodeText(text).setQrCodeBitmap(bitmap).create();
        if (onKeyListener != null) {
            mQRCodeDialog.setOnKeyListener(onKeyListener);
        }

        if (show) {
            mQRCodeDialog.show();
        } else {
            mQRCodeDialog.dismiss();
        }
    }

    /**
     * 当成功加入购物车时
     *
     * @param show
     * @param positiveListener
     * @param negativeListener
     */
    private void onSuccessAddCartDialog(boolean show, DialogInterface.OnClickListener positiveListener,
                                        DialogInterface.OnClickListener negativeListener, DialogInterface.OnKeyListener onKeyListener) {

        if (isFinishing()) {
            return;
        }

        mSuccessAddCartDialog = new TvTaoBaoDialog.Builder(this)
                .setMessage(getResources().getString(R.string.ytbv_success_add_cart))
                .setPositiveButton(getResources().getString(R.string.ytbv_goto_jiesuan), positiveListener)
                .setNegativeButton(getResources().getString(R.string.ytbv_zai_guangguang), negativeListener).create();

        if (onKeyListener != null) {
            mSuccessAddCartDialog.setOnKeyListener(onKeyListener);
        }

        if (show) {
            mSuccessAddCartDialog.show();
        } else {
            mSuccessAddCartDialog.dismiss();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQRCodeDialog != null) {
            mQRCodeDialog.dismiss();
        }
        if (mSuccessAddCartDialog != null) {
            mSuccessAddCartDialog.dismiss();
        }
        if (GlobalConfig.instance != null && GlobalConfig.instance.isBeta()) {
            System.gc();
            System.runFinalization();
        }
    }
}
