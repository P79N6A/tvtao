package com.yunos.tvtaobao.biz.request.header;


import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public class DetainMentHeader implements Header {

    private String mHeaderName = "";
    private String mHeaderValue = "";

    @Override
    public HeaderElement[] getElements() throws ParseException {
        return null;
    }

    @Override
    public String getName() {
        return mHeaderName;
    }

    @Override
    public String getValue() {
        return mHeaderValue;
    }

    public void setHeaderNameAndValue(String name, String value) {
        mHeaderName = name;
        mHeaderValue = value;
    }

    @Override
    public String toString() {
        return "DetainMentHeader [mHeaderName=" + mHeaderName + ", mHeaderValue=" + mHeaderValue + "]";
    } 

}
