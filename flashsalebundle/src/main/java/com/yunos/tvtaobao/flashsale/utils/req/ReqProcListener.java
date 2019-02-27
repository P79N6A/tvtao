package com.yunos.tvtaobao.flashsale.utils.req;

public interface ReqProcListener {
	public void loadingData(Object userData);
	
	public boolean loadingDataError(Object userData);
	
	public void loadingDataSuccess(Object userData, Object reqData);
	
	public void excuteReq(Object userData);
	
	public boolean avaibleUpdate();
}
