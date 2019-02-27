package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by pan on 16/12/22.
 */

public class LiveBlackList {
    private List<String> taobaoLiveAccountIdList;

    public List<String> getTaobaoLiveAccountIdList() {
        return taobaoLiveAccountIdList;
    }

    public void setTaobaoLiveAccountIdList(List<String> taobaoLiveAccountIdList) {
        this.taobaoLiveAccountIdList = taobaoLiveAccountIdList;
    }
}
