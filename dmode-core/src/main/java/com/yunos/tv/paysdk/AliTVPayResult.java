package com.yunos.tv.paysdk;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AliTVPayResult implements Parcelable {
    private boolean payResult;
    private String payFeedback;
    private Bundle payInformation;
    public static final Creator<AliTVPayResult> CREATOR = new Creator<AliTVPayResult>() {
        public AliTVPayResult createFromParcel(Parcel source) {
            AliTVPayResult mYunOSPayResult = new AliTVPayResult();
            mYunOSPayResult.payResult = source.readInt() != 0;
            mYunOSPayResult.payFeedback = source.readString();
            mYunOSPayResult.payInformation = source.readBundle();
            return mYunOSPayResult;
        }

        public AliTVPayResult[] newArray(int size) {
            return new AliTVPayResult[size];
        }
    };

    public AliTVPayResult() {
    }

    public boolean getPayResult() {
        return this.payResult;
    }

    public String getPayFeedback() {
        return this.payFeedback;
    }

    public Bundle getPayInformation() {
        return this.payInformation;
    }

    public void setPayResult(boolean result) {
        this.payResult = result;
    }

    public void setPayFeedback(String feedback) {
        this.payFeedback = feedback;
    }

    public void setPayInformation(Bundle bundle) {
        this.payInformation = bundle;
    }

    public String getPayInformation(String requestcode) {
        String payResultInfo = this.payInformation.getString(requestcode);
        return payResultInfo;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.payResult ? 1 : 0);
        dest.writeString(this.payFeedback);
        if (this.payInformation != null) {
            dest.writeBundle(this.payInformation);
        }

    }
}
