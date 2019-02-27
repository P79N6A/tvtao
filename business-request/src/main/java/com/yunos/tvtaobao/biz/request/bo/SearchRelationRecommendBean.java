package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by xtt
 * on 2018/12/21
 * desc
 */
public class SearchRelationRecommendBean {

        private List<Result> result;
        private String suggest_rn;
        private String pvid;
        private List<String> templates;
        private String scm;
        private int version;
        private String tpp_trace;
        public void setResult(List<Result> result) {
            this.result = result;
        }
        public List<Result> getResult() {
            return result;
        }

        public void setSuggest_rn(String suggest_rn) {
            this.suggest_rn = suggest_rn;
        }
        public String getSuggest_rn() {
            return suggest_rn;
        }

        public void setPvid(String pvid) {
            this.pvid = pvid;
        }
        public String getPvid() {
            return pvid;
        }

        public void setTemplates(List<String> templates) {
            this.templates = templates;
        }
        public List<String> getTemplates() {
            return templates;
        }

        public void setScm(String scm) {
            this.scm = scm;
        }
        public String getScm() {
            return scm;
        }

        public void setVersion(int version) {
            this.version = version;
        }
        public int getVersion() {
            return version;
        }

        public void setTpp_trace(String tpp_trace) {
            this.tpp_trace = tpp_trace;
        }
        public String getTpp_trace() {
            return tpp_trace;
        }

    public class Result {

        private SearchRelationRecommendItemBean data;
        private String traceBizType;
        private String tItemType;
        private String traceTmplType;
        public void setData(SearchRelationRecommendItemBean data) {
            this.data = data;
        }
        public SearchRelationRecommendItemBean getData() {
            return data;
        }

        public void setTraceBizType(String traceBizType) {
            this.traceBizType = traceBizType;
        }
        public String getTraceBizType() {
            return traceBizType;
        }

        public void setTItemType(String tItemType) {
            this.tItemType = tItemType;
        }
        public String getTItemType() {
            return tItemType;
        }

        public void setTraceTmplType(String traceTmplType) {
            this.traceTmplType = traceTmplType;
        }
        public String getTraceTmplType() {
            return traceTmplType;
        }

    }


}
