// ITVTaoMessage.aidl
package com.tvtao.sdk.voice;
import com.tvtao.sdk.voice.ITVTaoCallBack;

// Declare any non-default types here with import statements

interface ITVTaoMessage {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void message(String aString, ITVTaoCallBack callback);
}
