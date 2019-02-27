package com.ihome.android.market2.aidl;

interface AppOperateAidl{
	/**
	 *
	 * if occur error ,will be return 1
	 * esle return 0
     * 
     */
   String appOperate(String reqParam);
   
	/**
	 * 第三方应用检测应用是否需要更新
 	 * true 有更新， false 没有更新
     */
   boolean appUpdateCheck();
   
}