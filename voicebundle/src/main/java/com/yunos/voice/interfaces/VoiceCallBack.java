package com.yunos.voice.interfaces;

import com.tvtaobao.voicesdk.bo.SearchObject;

import java.util.List;

/**
 * <pre>
 *     author : pan
 *     e-mail : panbeixing@zhiping.tech
 *     time   : 2017/11/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface VoiceCallBack {
    void onTTS(boolean mIsHandle, String msg, List<String> tips);

    void onSearch(SearchObject search);
}
