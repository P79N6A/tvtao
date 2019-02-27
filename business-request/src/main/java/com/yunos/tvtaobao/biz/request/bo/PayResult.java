/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;

/**
 * @author tianxiang
 * @date 2012-11-12 上午11:25:20
 */
public class PayResult implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 450541528396783649L;

    private String resultStatus;

	private String memo;

	private String tradeNo;

	private String externToken;

	private String partner;

	private String success;

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getExternToken() {
		return externToken;
	}

	public void setExternToken(String externToken) {
		this.externToken = externToken;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public static PayResult fromStr(String res) {
		PayResult payResult = new PayResult();
		try {
			String[] payResultItems = res.split(";");
			for (String payResultItemStr : payResultItems) {
				String[] payResultItemKv = payResultItemStr.split("=");
				if (payResultItemKv[0].equals("resultStatus")) {
					payResult.setResultStatus(payResultItemKv[1].substring(1,
							payResultItemKv[1].length() - 1));
				} else if (payResultItemKv[0].equals("memo")) {
					payResult.setMemo(payResultItemKv[1].substring(1,
							payResultItemKv[1].length() - 1));
				} else if (payResultItemKv[0].equals("result")) {
					if (payResultItemKv[1].equals("{}")) {
						continue;
					}

					String[] resultItems = payResultItemKv[1].split("&");
					for (String resultItemStr : resultItems) {
						String[] resultItemKv = resultItemStr.split("=");
						if (resultItemKv[0].equals("trade_no")) {
							payResult.setTradeNo(resultItemKv[1]);
						} else if (resultItemKv[0].equals("extern_token")) {
							payResult.setExternToken(resultItemKv[1]);
						} else if (resultItemKv[0].equals("partner")) {
							payResult.setPartner(resultItemKv[1]);
						} else if (resultItemKv[0].equals("success")) {
							payResult.setSuccess(resultItemKv[1]);
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e("PayResult", "数据解析错误", e);
		}

		return payResult;
	}

	public boolean isSuccess() {
		if (TextUtils.isEmpty(resultStatus) || TextUtils.isEmpty(success)) {
			return false;
		}

		return resultStatus.equalsIgnoreCase("9000")
				&& success.equalsIgnoreCase("true");
	}

	public boolean isCancel() {
		return resultStatus.equals("6001");
	}

	public String getErrorMsg() {
		if (resultStatus.equals("4003")) {
			return "支付宝账户被冻结或不允许支付";
		} else if (resultStatus.equals("6000")) {
			return "支付服务正在进行升级,请稍后再试";
		} else {
			return "支付未成功";
		}
	}
}
