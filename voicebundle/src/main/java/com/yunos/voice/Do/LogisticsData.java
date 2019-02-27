package com.yunos.voice.Do;

import java.util.List;

/**
 * Created by xutingting on 2017/11/2.
 */

public class LogisticsData {
    private String code;
    private String end;
    private List<LogisticsDo> model;
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

    public void setModel(List<LogisticsDo> model) {
        this.model = model;
    }
    public List<LogisticsDo> getModel() {
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
