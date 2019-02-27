// ITVTaoCallBack.aidl
package com.tvtao.sdk.voice;

// Declare any non-default types here with import statements
interface ITVTaoCallBack {
    void callback(String command);
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void searchResult(String asr, String data);
}
