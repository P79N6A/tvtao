package com.yunos.tvtaobao.tvlive.bo.model.bean;

import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 * 央广视频实体类
 */

public class YGVideo {
    private List<YGVideoInfo> mYGVideoInfos;

    public List<YGVideoInfo> getYGVideoInfos() {
        return mYGVideoInfos;
    }

    public void setYGVideoInfos(List<YGVideoInfo> YGVideoInfos) {
        mYGVideoInfos = YGVideoInfos;
    }
}
