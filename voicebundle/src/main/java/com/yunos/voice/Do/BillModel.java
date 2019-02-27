package com.yunos.voice.Do;

import java.util.List;

/**
 * Created by xutingting on 2017/11/3.
 */

public class BillModel {
    private String amounts;
    private List<BillDo> detailDOList;
    public void setAmounts(String amounts) {
        this.amounts = amounts;
    }
    public String getAmounts() {
        return amounts;
    }

    public void setDetailDOList(List<BillDo> detailDOList) {
        this.detailDOList = detailDOList;
    }
    public List<BillDo> getDetailDOList() {
        return detailDOList;
    }
}
