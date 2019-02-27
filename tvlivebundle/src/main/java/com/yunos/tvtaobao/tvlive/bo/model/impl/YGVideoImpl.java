package com.yunos.tvtaobao.tvlive.bo.model.impl;

import com.yunos.tvtaobao.tvlive.bo.model.YGVideoModel;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideo;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.presenter.impl.VideoDialogPresenterImpl;
import com.yunos.tvtaobao.tvlive.request.TvLiveRequest;

import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 * 央广视频model层操作
 */

public class YGVideoImpl implements YGVideoModel {

    private YGVideo mYGVideo;

    public YGVideoImpl() {
        mYGVideo = new YGVideo();
    }

    @Override
    public void loadYGVideo(VideoDialogPresenterImpl.OnYGVideoListener listener) {
        TvLiveRequest.getBusinessRequest().getYGVideoList(listener);
    }

    public void addYGVideo(List<YGVideoInfo> datas) {
        mYGVideo.setYGVideoInfos(datas);
    }

    @Override
    public YGVideoInfo getYGVideo(int index) {
        if (mYGVideo == null || mYGVideo.getYGVideoInfos() == null) {
            return null;
        } else {
            return mYGVideo.getYGVideoInfos().get(index);
        }
    }
}
