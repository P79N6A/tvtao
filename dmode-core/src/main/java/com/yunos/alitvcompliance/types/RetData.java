package com.yunos.alitvcompliance.types;

public class RetData {
    private RetCode code;
    private String result;

    public RetData(RetCode c, String r) {
        this.code = c;
        this.result = r;
    }

    public RetCode getCode() {
        return this.code;
    }

    public void setCode(RetCode code) {
        this.code = code;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String toString() {
        return "RetData [code=" + this.code + ", result=" + this.result + "]";
    }
}