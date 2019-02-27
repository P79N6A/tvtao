package com.yunos.tvtaobao.tradelink.listener;

import android.content.DialogInterface;
import android.view.KeyEvent;


public interface QRCodeKeyListener {
   boolean onQRCodeKey(DialogInterface dialog, int keyCode, KeyEvent event);
}
