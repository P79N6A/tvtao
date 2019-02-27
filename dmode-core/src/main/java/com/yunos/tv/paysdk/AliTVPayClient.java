package com.yunos.tv.paysdk;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

public class AliTVPayClient {
    public void aliTVPay(Context context, String orderInfo, String signInfo, Bundle parameters, AliTVPayClient.IPayCallback payCallback) throws RemoteException {
        //do nothing
    }

    public interface IPayCallback {
        void onPayProcessEnd(AliTVPayResult var1);
    }
}
