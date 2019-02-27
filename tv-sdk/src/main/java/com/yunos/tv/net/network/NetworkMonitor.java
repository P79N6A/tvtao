package com.yunos.tv.net.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetworkMonitor extends BroadcastReceiver {
	private static final String TAG = "NetworkMonitor";
	private final int ON_NETWORK_DISCONNECTED = 100;
	private Context mContext = null;
	private Runnable mRunnable = null;
	private Handler mHandler = null;
	private boolean ENABLE = true;
	private INetworkCtrListener iNetworkCtrListener;
	
	public NetworkMonitor(INetworkCtrListener l) {
		super();
		this.iNetworkCtrListener = l;
		Log.d(TAG, "NetworkMonitor.Constructor");
	}

	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "NetworkMonitor.onReceive");
		try {
			mContext = context;
			NetInfoAdapter nia = new NetInfoAdapter(mContext);
			if (nia.isConnected()) {
				String tickerText = "NetworkMonitor: connected " + nia.getInfo("type");

				if (nia.exists("netID")) {
					tickerText = tickerText + " " + nia.getInfo("netID");
				}

				if (nia.exists("speed")) {
					tickerText = tickerText + " " + nia.getInfo("speed");
				}

				Log.v(TAG, tickerText);
				if (ENABLE) {
					String[] lable = new LableMap().getLableList();
					int i = 2; // Lables start at 0 but we're only using 2
								// onwards..
				}
				nia = null;
			} else {
				Log.v(TAG, "NetworkMonitor: not connected");
				if (null == mHandler) {
					mHandler = new Handler() {
						public void handleMessage(Message msg) {
							switch (msg.what) {
							case ON_NETWORK_DISCONNECTED:
								
									Log.i(TAG, "Connection re-build.");
									removeCallbacks(mRunnable);
									iNetworkCtrListener.setNetworkConnectedStatus(true);
									mRunnable = null;
								
								break;
							}
						}
					};
				}

				if (null == mRunnable) {
					mRunnable = new Runnable() {
						public void run() {
							mHandler.obtainMessage(ON_NETWORK_DISCONNECTED).sendToTarget();
						}
					};
				}

				mHandler.postDelayed(mRunnable, 1000);
			}
		} catch (Exception e) {
			Log.v(TAG, "NetworkMonitor.onReceive");
			Log.e(TAG, "NetworkMonitor.onReceive, failed: " + e.getMessage());
		}
	}
	
	public interface INetworkCtrListener {
		public void setNetworkConnectedStatus(boolean connected);
	};
}
