package com.tvtaobao.voicesdk.interfaces;

import com.tvtaobao.voicesdk.bo.CommandReturn;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface VoiceListener {
    void callback(CommandReturn command);

    void searchResult(String data);
}
