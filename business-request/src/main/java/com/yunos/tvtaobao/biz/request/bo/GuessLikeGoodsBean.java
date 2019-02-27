package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by yuanqihui on 2018/6/28.
 */

public class GuessLikeGoodsBean {
    private String currentPage;
    private String currentTime;
    private String empty;
    private ResultVO result;

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public ResultVO getResult() {
        return result;
    }

    public void setResult(ResultVO result) {
        this.result = result;
    }

    public static class ResultVO {
        private BrandVO brand;
        private List<RecommendVO> recommedResult;

        public BrandVO getBrand() {
            return brand;
        }

        public void setBrand(BrandVO brand) {
            this.brand = brand;
        }

        public List<RecommendVO> getRecommedResult() {
            return recommedResult;
        }

        public void setRecommedResult(List<RecommendVO> recommedResult) {
            this.recommedResult = recommedResult;
        }

        private class BrandVO {
            private String title;
            private String tips;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTips() {
                return tips;
            }

            public void setTips(String tips) {
                this.tips = tips;
            }
        }

        public static class RecommendVO {
            private String bizName;
            private String type;
            private GuessLikeFieldsVO fields;
            private RebateBo rebateBo;
            private DynamicRecommend recommend;

            @Override
            public String toString() {
                return "RecommendVO{" +
                        "bizName='" + bizName + '\'' +
                        ", type='" + type + '\'' +
                        ", fields=" + fields +
                        '}';
            }

            public RebateBo getRebateBo() {
                return rebateBo;
            }

            public void setRebateBo(RebateBo rebateBo) {
                this.rebateBo = rebateBo;
            }

            public String getBizName() {
                return bizName;
            }

            public void setBizName(String bizName) {
                this.bizName = bizName;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public GuessLikeFieldsVO getFields() {
                return fields;
            }

            public void setFields(GuessLikeFieldsVO fields) {
                this.fields = fields;
            }

            public DynamicRecommend getRecommend() {
                return recommend;
            }

            public void setRecommend(DynamicRecommend recommend) {
                this.recommend = recommend;
            }
        }
    }

    @Override
    public String toString() {
        return "GuessLikeGoodsBean{" +
                "currentPage='" + currentPage + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", empty='" + empty + '\'' +
                ", result=" + result +
                '}';
    }
}
