package com.yunos.tv.app.widget.adapter;

import java.util.ArrayList;

public class LocationData {

	private ArrayList<LocationItem> mLocationList = new ArrayList<LocationItem>();

	public LocationData() {

	}

	public int getDataCount() {
		return mLocationList == null ? 0 : mLocationList.size();
	}

	public LocationItem getItem(int position) {
		return mLocationList == null ? null : mLocationList.get(position);
	}

	public ArrayList<LocationItem> getLocationList() {
		return mLocationList;
	}

	public void setLocationList(ArrayList<LocationItem> mLocationList) {
		this.mLocationList.addAll(mLocationList);
	}

	public int getViewTypeCount() {
		return 1;
	}

}
