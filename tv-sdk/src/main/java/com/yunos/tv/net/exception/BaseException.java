package com.yunos.tv.net.exception;

import android.content.Context;

public abstract class BaseException extends Exception {
	
	protected int code;
	protected String errorMessage;
	
	public BaseException() {
		super();
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

	public BaseException(int code, String errorMessage) {
		super();
		this.code = code;
		this.errorMessage = errorMessage;
	}
	
	public BaseException(String errorMessage, Throwable throwable) {
		super(throwable);
		this.errorMessage = errorMessage;
	}
	
	public BaseException(int code, String errorMessage, Throwable throwable) {
		super(throwable);
		this.code = code;
		this.errorMessage = errorMessage;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public String getErrorMessage() {
		if (this.errorMessage != null) {
			return this.errorMessage;
		}
		return super.getMessage();
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public abstract boolean handle(Context context);
}
