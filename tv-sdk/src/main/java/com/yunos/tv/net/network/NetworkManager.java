package com.yunos.tv.net.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.yunos.tv.net.exception.NoNetworkException;
import com.yunos.tv.net.network.NetworkMonitor.INetworkCtrListener;

import java.util.HashSet;

public class NetworkManager {
	public static final String TAG = "NetworkManager";

	private NetworkManager() {
	}

	private static NetworkManager networkManager = null;

	public static NetworkManager instance() {
		if (null == networkManager) {
			networkManager = new NetworkManager();
		}
		return networkManager;
	}

	private Context applicationContext;
	private boolean isConnected = true;
	private boolean mLastIsConnected = false;
	private HashSet<INetworkListener> listenerSet = new HashSet<INetworkListener>();
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, android.content.Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				mLastIsConnected = isConnected;
				isConnected = isNetworkAvailable(context);
				for (INetworkListener l : listenerSet) {
					l.onNetworkChanged(isConnected, mLastIsConnected);
				}
			}
		};
	};

	public void init(Context context) {
		init(context, null);
	}

	public void init(Context context, NoNetworkException.NoNetworkHanler noNetworkHanler) {
		this.applicationContext = context;
		if (context instanceof Activity) {
			Context applicationContext = context.getApplicationContext();
			if (applicationContext != null) {
				this.applicationContext = applicationContext;
			}
		}
		this.applicationContext.registerReceiver(mBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		NoNetworkException.setNoNetworkHanler(noNetworkHanler);

		isConnected = isNetworkAvailable(context);
		mLastIsConnected = isConnected;
	}

	public void release() {
		this.applicationContext.unregisterReceiver(mBroadcastReceiver);
	}

	public void registerStateChangedListener(INetworkListener l) {
		if (listenerSet.add(l)) {
		}
		Log.i(TAG, "registerStateChangedListener, size:" + listenerSet.size());
	}

	public void unregisterStateChangedListener(INetworkListener l) {
		listenerSet.remove(l);
		Log.i(TAG, "unregisterStateChangedListener, size:" + listenerSet.size());
	}

	public boolean isNetworkConnected() {
		return this.isConnected;
	}

	public Context getApplicationContext() {
		return applicationContext;
	}

	// ==========================below is old====
	public static final int UNCONNECTED = -9999;

	public static int getNetworkType(Context context) {
		if (context instanceof Activity) {
			Context applicationContext = context.getApplicationContext();
			if (applicationContext != null) {
				context = applicationContext;
			}
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected() && info.isAvailable()) {
			return info.getType();
		}

		return UNCONNECTED;
	}

	public static boolean isNetworkAvailable(Context context) {
		return UNCONNECTED != getNetworkType(context);
	}

	public interface INetworkListener {
		public void onNetworkChanged(boolean isConnected, boolean lastIsConnected);
	};

	// public static volatile boolean needToast = true;
	// private static final String NETWORK_UNCONNECTED = "未连接网络，请开启移动网络或WIFI";
	// public static void showNoNetwork(Context context) {
	// if (needToast) {
	// Toast.makeText(context, NETWORK_UNCONNECTED, Toast.LENGTH_SHORT).show();
	// }
	// }
}
