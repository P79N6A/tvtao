package com.yunos.tvtaobao.flashsale.listener;


import com.yunos.tvtaobao.flashsale.activity.FlashSaleBaseActivity;

public interface ContextListener {
	public void OnWaitProgressDialog(boolean show);
	public void showNetworkErrorDialog(final boolean isfinishActivity);
	public FlashSaleBaseActivity getFlashSaleBaseActivity();
	public void showErrorDialog(String msg, final boolean isFinishActivity);
}
