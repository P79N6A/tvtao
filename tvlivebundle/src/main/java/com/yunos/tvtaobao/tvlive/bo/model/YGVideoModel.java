package com.yunos.tvtaobao.tvlive.bo.model;


import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.presenter.impl.VideoDialogPresenterImpl;

import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 */

public interface YGVideoModel {

     void loadYGVideo(VideoDialogPresenterImpl.OnYGVideoListener listener);

     void addYGVideo(List<YGVideoInfo> datas);

     YGVideoInfo getYGVideo(int index);
}
