package com.yunos.tv.net.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.yunos.tv.aliTvSdk.R;
import com.yunos.tv.app.widget.Dialog.TvAlertDialog;

public class NetworkAlertDialog {
	static TvAlertDialog mNetworkAlert;

	public static void showDialog(final Context context) {
		showDialog(context, false);//网络对话框是没有毛玻璃的
	}

	public static void showDialog(final Context context, boolean isFrost) {
		hideDialog();
		mNetworkAlert = new TvAlertDialog.Builder(context, TvAlertDialog.THEME_SET_NETWORK).setIcon(R.drawable.tui_ic_ethernetexception)
				.setFrostedGlass(false).setTitle(R.string.netdialog_title).setMessage(R.string.netdialog_msg)
				.setPositiveButton(R.string.netdialog_setting, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent("android.settings.NETWORK_SETTINGS");
						Bundle bnd = new Bundle();
						bnd.putString("FinishMode", "BACK");
						intent.putExtras(bnd);
						context.startActivity(intent);
						dialog.dismiss();
					}
				}).create();
		mNetworkAlert.show();
	}

	public static boolean isDialogShow() {
		if (mNetworkAlert != null) {
			return mNetworkAlert.isShowing();
		} else {
			return false;
		}
	}

	public static void hideDialog() {
		try {
			if (mNetworkAlert != null) {
				mNetworkAlert.dismiss();
				mNetworkAlert = null;
			}
		} catch (Exception e) {
		}
	}
}
