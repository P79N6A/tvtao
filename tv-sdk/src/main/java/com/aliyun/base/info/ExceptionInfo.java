package com.aliyun.base.info;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {
	public String exception;

	public String type;

	public String createTime;

	private ExceptionInfo() {}

	public ExceptionInfo(String exception, String type, long time) {
		this.exception = exception;
		this.type = type;
		createTime = String.valueOf(time);
	}
}
