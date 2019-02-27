package com.tvtaobao.voicesdk.interfaces;

import com.tvtaobao.voicesdk.bo.DomainResultVo;

import java.util.List;

/**
 * Created by pan on 2017/9/22.
 */

public interface ASRSearchHandler {
    boolean onSearch(String asr, String txt, String spoken, List<String> tips, DomainResultVo.OtherCase otherCase);
}
