// ITVTaoInterface.aidl
package com.tvtao.sdk.voice;
import com.tvtao.sdk.voice.ITVTaoCallBack;

interface ITVTaoInterface {

    /**
     * understanding
     */
    void nlpRequest(String asr, String searchConfig, String json, ITVTaoCallBack callback);

}
