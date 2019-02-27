package com.tvtaobao.voicesdk.bo;

/**
 * Created by xutingting on 2017/11/3.
 */

public class BillData {
    private String code;
    private String end;
    private BillModel model;
    private String tts;
    private String tip;
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setEnd(String end) {
        this.end = end;
    }
    public String getEnd() {
        return end;
    }

    public void setModel(BillModel model) {
        this.model = model;
    }
    public BillModel getModel() {
        return model;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }
    public String getTts() {
        return tts;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
