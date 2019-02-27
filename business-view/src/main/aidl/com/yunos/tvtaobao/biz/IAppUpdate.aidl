package com.yunos.tvtaobao.biz;
import com.yunos.tvtaobao.biz.IAppUpdateCallback;
interface IAppUpdate
{
	void startUpdate(String jsonParam, in IAppUpdateCallback callback);
	void stopUpdate(String jsonParam);
}