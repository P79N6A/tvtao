package com.tvtaobao.voicesdk.interfaces;

import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;

/**
 * Created by pan on 2017/9/22.
 */

public interface ASRHandler {
    PageReturn onASRNotify(DomainResultVo object);
}
