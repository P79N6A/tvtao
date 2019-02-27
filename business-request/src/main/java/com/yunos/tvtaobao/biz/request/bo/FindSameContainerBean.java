package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by zhoubo on 2018/7/12.
 * zhoubo on 2018/7/12 16:30
 * describition 找相似的
 */

public class FindSameContainerBean {

    private String pic;
    private List<FindSameBean> findSameBeanList;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<FindSameBean> getFindSameBeanList() {
        return findSameBeanList;
    }

    public void setFindSameBeanList(List<FindSameBean> findSameBeanList) {
        this.findSameBeanList = findSameBeanList;
    }
}
