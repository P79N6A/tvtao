package com.yunos.tvtaobao.tvlive.presenter;

import android.content.Context;

import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;

/**
 * Created by huangdaju on 17/7/19.
 */

public interface VideoDialogPresenter {
    void loadVideo(int videoType, Context context);
    void fullscreenVideo();
    void changeVideoDialogSize(int width, int height, int marginTop, int marginLeft);
    void showYGVideo(YGVideoInfo videoInfo);
    void showTMALLVideo();
    void videoStart(YGVideoInfo ygVideoInfo, int videoType);
    void videoPause();
    void dismiss();

}
