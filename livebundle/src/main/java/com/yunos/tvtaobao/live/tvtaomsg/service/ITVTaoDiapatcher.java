package com.yunos.tvtaobao.live.tvtaomsg.service;

import com.yunos.tvtaobao.live.tvtaomsg.po.TVTaoMessage;

import java.util.Map;

/**
 * Created by pan on 16/12/22.
 */

public interface ITVTaoDiapatcher {
    void Diapatcher(TVTaoMessage taoMessage);
    void Error(Map<String, String> map, int Error);
}
