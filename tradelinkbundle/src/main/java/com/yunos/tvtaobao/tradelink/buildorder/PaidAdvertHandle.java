package com.yunos.tvtaobao.tradelink.buildorder;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.tradelink.buildorder.bean.PaidAdvertBo;
import com.yunos.tvtaobao.tradelink.paidadvert.PaidAdvertActivity;
import com.yunos.tvtaobao.tradelink.request.TvTaobaoBusinessRequest;

import java.lang.ref.WeakReference;

public class PaidAdvertHandle {

    private static final String TAG = "PaidAdvertHandle";
    // 网络请求的接口
    private TvTaobaoBusinessRequest mTvTaobaoBusinessRequest;
    // 图片下载管理
    private ImageLoaderManager mImageLoaderManager;

    private String mUri;
    private String mPic_Url;
    private DisplayImageOptions mImageOptions;

    public PaidAdvertHandle(Context context) {
        mTvTaobaoBusinessRequest = TvTaobaoBusinessRequest.getBusinessRequest();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        
        // 配置显示选项
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(false).cacheInMemory(true).setDisplayShow(false)
                .build();

        mUri = null;
        mPic_Url = null;
    }

    /**
     * 获取运营配置的内容
     */
    public void getPaidAdvertContent(BaseActivity baseActivity, String mBizOrderId) {
        AppDebug.i(TAG, "getPaidAdvertContent... mBizOrderId = " + mBizOrderId);
        baseActivity.OnWaitProgressDialog(true);
        mTvTaobaoBusinessRequest.getPaidAdvertRequest(mBizOrderId, new GetPaidAdvertRequestListener(
                new WeakReference<PaidAdvertHandle>(this), new WeakReference<BaseActivity>(baseActivity)));
    }

    /**
     * 进入广告推荐页
     */
    public void enterPaidAdvertActivity(BaseActivity baseActivity) {
        if (!TextUtils.isEmpty(mPic_Url)) {
            Intent intent = new Intent();
            intent.setClass(baseActivity, PaidAdvertActivity.class);
            intent.putExtra("picurl", mPic_Url);
            intent.putExtra("uri", mUri);
            baseActivity.startActivity(intent);
        }
    } 

    /**
     * 处理网络请求的结果
     * @param data
     */
    private void onHandlerPaidAdvert(PaidAdvertBo data) {
        AppDebug.i(TAG, "onHandlerPaidAdvert... data = " + data);
        if (data != null) {
            mUri = data.getUri();
            mPic_Url = data.getImageUrl();
            AppDebug.i(TAG, "onHandlerPaidAdvert -->  mUri = " + mUri + "; mPic_Url = " + mPic_Url);
            mImageLoaderManager.loadImage(mPic_Url, mImageOptions, new ImageLoadingListener(){

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    AppDebug.i(TAG, "onHandlerPaidAdvert... imageUri = " + imageUri + ";  loadedImage = " + loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    
                }});
        }  
    }
    
    

    /**
     * 广告运营网络请求的监听类
     */
    private static class GetPaidAdvertRequestListener implements RequestListener<PaidAdvertBo> {

        private WeakReference<PaidAdvertHandle> ref;
        private WeakReference<BaseActivity> baseActivityref;

        public GetPaidAdvertRequestListener(WeakReference<PaidAdvertHandle> paidAdvertHandle,
                WeakReference<BaseActivity> baseActivity) {
            ref = paidAdvertHandle;
            baseActivityref = baseActivity;
        }

        @Override
        public void onRequestDone(PaidAdvertBo data, int resultCode, String msg) {
            BaseActivity baseActivity = baseActivityref.get();
            if (baseActivity != null) {
                baseActivity.OnWaitProgressDialog(false);
            }

            PaidAdvertHandle paidAdvertHandle = ref.get();
            if (paidAdvertHandle != null) {
                if (resultCode == 200) {
                    paidAdvertHandle.onHandlerPaidAdvert(data);
                }
            }
        }
    }
}
