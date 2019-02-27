package com.yunos.tvtaobao.tvlive.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.tvlive.activity.TVBuyActivity;
import com.yunos.tvtaobao.tvlive.bo.model.YGVideoModel;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.bo.model.impl.YGVideoImpl;
import com.yunos.tvtaobao.tvlive.presenter.VideoDialogPresenter;
import com.yunos.tvtaobao.tvlive.utils.VideoType;
import com.yunos.tvtaobao.tvlive.view.IvideoDialog;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 */

public class VideoDialogPresenterImpl implements VideoDialogPresenter {

    private YGVideoModel mYGVideoModel;
    private IvideoDialog mIvideoDialog;
    private WeakReference<Context> mContext;
    private YGVideoInfo ygVideoInfo;

    private int currentVideoType = -1;

    public VideoDialogPresenterImpl(Context context, IvideoDialog ivideoDialog) {
        mYGVideoModel = new YGVideoImpl();
        this.mContext = new WeakReference<Context>(context);
        this.mIvideoDialog = ivideoDialog;
    }

    @Override
    public void loadVideo(int videoType, Context context) {
        currentVideoType = videoType;
        switch (videoType) {
            case VideoType.TMALL_VIDEO:

                break;
            case VideoType.TAOBAO_VIDEO:

                break;
            case VideoType.TV_BUY_VIDEO:
                mYGVideoModel.loadYGVideo(new OnYGVideoListener(new WeakReference((BaseActivity) context)));
                break;
        }
    }

    private void showVideo(String url) {
        if (currentVideoType != -1) {
            mIvideoDialog.playVideo(currentVideoType, url);
            mIvideoDialog.show();
        }
    }

    @Override
    public void videoStart(YGVideoInfo ygVideoInfo, int videoType) {
        currentVideoType = videoType;
        this.ygVideoInfo = ygVideoInfo;
        mIvideoDialog.playVideo(videoType, ygVideoInfo.getVideoUrl());
        mIvideoDialog.show();
    }

    @Override
    public void videoPause() {
        mIvideoDialog.stopVideo();
        mIvideoDialog.dismiss();
    }

    /**
     * 播放视频
     */
    private void playVideo() {
        if (ygVideoInfo == null) {
            ygVideoInfo = mYGVideoModel.getYGVideo(0);
            if (ygVideoInfo != null) {
                String currentVideoURL = ygVideoInfo.getVideoUrl();
                showVideo(currentVideoURL);
            }
        }
    }


    @Override
    public void fullscreenVideo() {
        if (mContext == null || mContext.get() == null) {
            return;
        }
        Activity activity = (Activity) mContext.get();
        if (activity.isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        switch (currentVideoType) {
            case VideoType.TMALL_VIDEO:
                intent.setData(Uri.parse("tvtaobao://home?module=video&livetype=tmalllive&notshowloading=true"));
                break;
            case VideoType.TAOBAO_VIDEO:
                intent.setData(Uri.parse("tvtaobao://home?module=video&livetype=taobaolive&notshowloading=true"));
                break;
            case VideoType.TV_BUY_VIDEO:
                intent.setClass(activity, TVBuyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoInfo", ygVideoInfo);
                intent.putExtras(bundle);
                intent.putExtra("fullscreen", true);
                break;
        }
        activity.startActivityForResult(intent, VideoType.YG_FULLSCREEN_REQUEST_CODE);
    }

    @Override
    public void dismiss() {
        mIvideoDialog.stopVideo();
        mIvideoDialog.dismiss();
    }

    @Override
    public void changeVideoDialogSize(int width, int height, int marginTop, int marginLeft) {
        mIvideoDialog.changeVideoSize(width, height, marginTop, marginLeft);
    }

    public class OnYGVideoListener extends BizRequestListener<List<YGVideoInfo>> {

        public OnYGVideoListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (mContext == null || mContext.get() == null) {
                return false;
            }
            Activity activity = (Activity) mContext.get();
            loadVideo(VideoType.TMALL_VIDEO, activity);
            return false;
        }

        @Override
        public void onSuccess(List<YGVideoInfo> data) {
            if (mContext == null || mContext.get() == null) {
                return;
            }
            Activity activity = (Activity) mContext.get();
            if (data == null || data.size() == 0) {
                loadVideo(1, activity);
                return;
            }
            if (data.get(0).getType() == 0) { //判断是否包含央广直播
                mYGVideoModel.addYGVideo(data);
                playVideo();
            } else {
                loadVideo(VideoType.TMALL_VIDEO, activity);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public void showYGVideo(YGVideoInfo videoInfo) {
        currentVideoType = VideoType.TV_BUY_VIDEO;
        mIvideoDialog.playVideo(currentVideoType, videoInfo.getVideoUrl());
        mIvideoDialog.show();
    }

    @Override
    public void showTMALLVideo() {

    }
}
