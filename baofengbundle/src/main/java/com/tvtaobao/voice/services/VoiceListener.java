package com.tvtaobao.voice.services;

import com.bftv.fui.thirdparty.VoiceFeedback;

/**
 * Created by pan on 2017/8/2.
 */

public interface VoiceListener {
    void onVoiceResult(VoiceFeedback back);
}
