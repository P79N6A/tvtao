package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by xtt
 * on 2018/12/21
 * desc
 */
public class SearchRelationRecommendItemBean {

        private List<DataResult> result;
        public void setResult(List<DataResult> result) {
            this.result = result;
        }
        public List<DataResult> getResult() {
            return result;
        }




    public class DataResult {

        private String searchtext;
        private String showtext;

        public void setSearchtext(String searchtext) {
            this.searchtext = searchtext;
        }
        public String getSearchtext() {
            return searchtext;
        }

        public void setShowtext(String showtext) {
            this.showtext = showtext;
        }
        public String getShowtext() {
            return showtext;
        }

    }
}
