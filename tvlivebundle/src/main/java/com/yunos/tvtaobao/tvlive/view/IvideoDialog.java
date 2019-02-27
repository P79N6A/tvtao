package com.yunos.tvtaobao.tvlive.view;

import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;

import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 */

public interface IvideoDialog {

    void playVideo(int videoType, String url);

    void stopVideo();

    void show();

    void dismiss();

    void showError();

    void fullScreen(int liveType);

    void changeVideoSize(int width,int height, int marginTop, int marginLeft);

    void changeLiveType(int liveType);

    void addYGVideoData(List<YGVideoInfo> data);
}
