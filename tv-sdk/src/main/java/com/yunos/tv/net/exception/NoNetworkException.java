package com.yunos.tv.net.exception;

import android.content.Context;
import android.widget.Toast;

public class NoNetworkException extends BaseException {

	public static final String NETWORK_UNCONNECTED = "未连接网络，请检查网络设置";
	
	public NoNetworkException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NoNetworkException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NoNetworkException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public NoNetworkException(int code, String detailMessage, Throwable throwable) {
		super(code, detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public NoNetworkException(int code, String errorMessage) {
		super(code, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public NoNetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean handle(Context context) {
		if (mNoNetworkHanler != null) {
			return mNoNetworkHanler.handle(context);
		} else {
			Toast.makeText(context, NETWORK_UNCONNECTED, Toast.LENGTH_SHORT).show();
			return true;
		}
	}
	
	public interface NoNetworkHanler {
		public boolean handle(Context context);
	}
	
	public static NoNetworkHanler mNoNetworkHanler;
	
	public static void setNoNetworkHanler(NoNetworkHanler handler) {
		mNoNetworkHanler = handler;
	}
}
