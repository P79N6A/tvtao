package com.yunos.voice.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.WriterException;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.tvtaobao.voicesdk.utils.QRCodeUtil;
import com.yunos.CloudUUIDWrapper;
import com.yunos.voice.R;
import com.yunos.voice.activity.CreateOrderActivity;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/5/15
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class OrderPromptView {
    private RelativeLayout ivPromptLayout;
    private ImageView ivErrorPrompt, ivQRCode, ivCellPhone;
    private WeakReference<CreateOrderActivity> mWeakReference;

    public OrderPromptView(WeakReference<CreateOrderActivity> weakReference) {
        this.mWeakReference = weakReference;
        initView();
    }

    private void initView() {
        if (mWeakReference != null && mWeakReference.get() != null) {
            CreateOrderActivity activity = mWeakReference.get();
            ivPromptLayout = activity.findViewById(R.id.order_info_error_prompt_layout);
            ivErrorPrompt = activity.findViewById(R.id.order_info_error_prompt);
            ivQRCode = activity.findViewById(R.id.order_info_error_qrcode);
            ivCellPhone = activity.findViewById(R.id.order_info_error_phone);
        }
    }

    /**
     * 展示二维码，去手淘下单购买
     *
     * @param itemId
     */
    public void showBuyToCellPhone(String itemId) {
        showQRCodeView();

        String itemUrl = "http://m.tb.cn/ZQzcgg?id=" + itemId + "&orderMarker=v:w-ostvuuid*"
                + CloudUUIDWrapper.getCloudUUID() + ",w-ostvclient*tvtaobao" + "&w-ostvuuid=" + CloudUUIDWrapper.getCloudUUID() + "&w-ostvclient=tvtaobao";

        Bitmap qrBitmap = getQRCode(itemUrl);
        ivQRCode.setImageBitmap(qrBitmap);
    }

    /**
     * 展示去手淘我的收获地址页面的二维码
     *
     * 引导用户手淘扫码，去手淘新增(修改)收获地址
     */
    public void showNotAddress() {
        showQRCodeView();

        String addressUrl = "http://my.m.taobao.com/deliver/wap_deliver_address_list.htm";

        Bitmap qrBitmap = getQRCode(addressUrl);
        ivQRCode.setImageBitmap(qrBitmap);
    }

    /**
     * 展示去手淘未付款订单页面的二维码
     *
     * 引导用户手淘扫码，去手淘支付未付款订单
     */
    public void showWaitPay() {
        showQRCodeView();

        String waitPayUrl = "http://h5.m.taobao.com/mlapp/olist.html?tabCode=waitPay";

        Bitmap qrBitmap = getQRCode(waitPayUrl);
        ivQRCode.setImageBitmap(qrBitmap);
    }

    private void showQRCodeView() {
        ivPromptLayout.setVisibility(View.VISIBLE);
        ivQRCode.setVisibility(View.VISIBLE);
        ivCellPhone.setVisibility(View.VISIBLE);
    }

    private Bitmap getQRCode(String url) {
        Bitmap qrBitmap = null;
        try {
//            Drawable drawable = mWeakReference.get().getResources().getDrawable(R.drawable.icon_taobao_qr_small);
            Bitmap icon = BitmapFactory.decodeResource(mWeakReference.get().getResources(), R.drawable.icon_taobao_qr_small);
//            Bitmap icon = null;
//            if (drawable != null) {
//                BitmapDrawable bd = (BitmapDrawable) drawable;
//                icon = bd.getBitmap();
//            }
            ViewGroup.LayoutParams para = ivQRCode.getLayoutParams();
            LogPrint.e("SSD", "layout width : " + para.width + "height : " + para.height);

            qrBitmap = QRCodeUtil.create2DCode(url, para.width, para.height, icon);


        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrBitmap;
    }
}
