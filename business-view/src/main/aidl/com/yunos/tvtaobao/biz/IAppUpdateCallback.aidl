// IAppUpdateCallback.aidl
package com.yunos.tvtaobao.biz;

// Declare any non-default types here with import statements

interface IAppUpdateCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onUpdateStatusChanged(in Bundle result);
}
