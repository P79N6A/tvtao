package com.aliyun.base.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ExArrayList<E> extends ArrayList<E> {
	
	private int total;
	
	private HashMap<String, Object> data;
	
	public void putData(String key, Object value) {
		if (data == null) {
			data = new HashMap<String, Object>();
		}
		data.put(key, value);
	}
	
	public Object getData(String key) {
		if (data != null) {
			return data.get(key);
		}
		return null;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
}
