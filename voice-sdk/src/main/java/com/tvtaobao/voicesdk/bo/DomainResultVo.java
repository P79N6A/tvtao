package com.tvtaobao.voicesdk.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuanqihui on 2018/2/28.
 */

public class DomainResultVo implements Serializable {
//    domain 领域
//    intent  意图
//    toUri 跳转的uri
//    spoken  语音播报
//    spokenTxt 播报的显示词
//    List<String>tips 滚动提示词
//    Integer  openFarMic 是否开麦 1: 开麦 天宫项目使用
//    Integer  taobaoLogin  淘宝登录标识  1：需要登录
//    Integer  addressSwitch 地址选择标识 1：需要地址选择
//    dataType  ResultVO的数据类型
//    ResultVO
//    ext 扩展字段
//    page 分页信息
//    Integer   pageNo  页码 从1开始
//    Integer   pageSize  每页记录数   客户端先不处理
//    Integer   totalCount 总条数
//    List<DetailListVO> 明细列表数据 以下是通用字段
//    String id
//    String name
//    String pic
//    String uri  点击后跳转地址
//    nluInfo  nlu有关的内容
//    sessionId  多轮对话id
//    Map<String,Object> nluResult   nlu结果信息 idst提供

    private String domain;
    private String intent;
    private String toUri;
    private String spoken;
    private String spokenTxt;
    private List<String> tips;
    private Integer openFarMic;
    private Integer taobaoLogin;
    private Integer addressSwitch;
    private String loadingTxt;
    private OtherCase otherCaseSpokens;
    public ResultVO resultVO;

    @Override
    public String toString() {
        return "DomainResultVo{" +
                "domain='" + domain + '\'' +
                ", intent='" + intent + '\'' +
                ", toUri='" + toUri + '\'' +
                ", spoken='" + spoken + '\'' +
                ", spokenTxt='" + spokenTxt + '\'' +
                ", tips=" + tips +
                ", openFarMic=" + openFarMic +
                ", taobaoLogin=" + taobaoLogin +
                ", addressSwitch=" + addressSwitch +
                ", resultVO=" + resultVO +
                '}';
    }

    public String getLoadingTxt() {
        return loadingTxt;
    }

    public void setLoadingTxt(String loadingTxt) {
        this.loadingTxt = loadingTxt;
    }

    public OtherCase getOtherCaseSpokens() {
        return otherCaseSpokens;
    }

    public void setOtherCaseSpokens(OtherCase otherCaseSpokens) {
        this.otherCaseSpokens = otherCaseSpokens;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getToUri() {
        return toUri;
    }

    public void setToUri(String toUri) {
        this.toUri = toUri;
    }

    public String getSpoken() {
        return spoken;
    }

    public void setSpoken(String spoken) {
        this.spoken = spoken;
    }

    public String getSpokenTxt() {
        return spokenTxt;
    }

    public void setSpokenTxt(String spokenTxt) {
        this.spokenTxt = spokenTxt;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public Integer getOpenFarMic() {
        return openFarMic;
    }

    public void setOpenFarMic(Integer openFarMic) {
        this.openFarMic = openFarMic;
    }

    public Integer getTaobaoLogin() {
        return taobaoLogin;
    }

    public void setTaobaoLogin(Integer taobaoLogin) {
        this.taobaoLogin = taobaoLogin;
    }

    public Integer getAddressSwitch() {
        return addressSwitch;
    }

    public void setAddressSwitch(Integer addressSwitch) {
        this.addressSwitch = addressSwitch;
    }

    public ResultVO getResultVO() {
        return resultVO;
    }

    public void setResultVO(ResultVO resultVO) {
        this.resultVO = resultVO;
    }

    public class ResultVO implements Serializable{
        private String pageNo;
        private String pageSize;
        private String totalCount;

        //外卖订单id
        private String tbMainOrderId;
        //搜索关键字
        private String keywords;
        private String beginTime;
        private String endTime;
        private String timeText;
        private String norm;

        private String storeId;

        private List<DetailListVO> detailList;
        /**
         * firstCategoryId : 27
         * firstCategoryName : 小吃夜宵
         * secondCategoryId : 12
         * secondCategoryName : 烧烤
         * totalPage : 0
         */

        private String categoryName;
        private String firstCategoryId;
        private String firstCategoryName;
        private String secondCategoryId;
        private String secondCategoryName;
        private String totalPage;

        @Override
        public String toString() {
            return "ResultVO{" +
                    "pageNo='" + pageNo + '\'' +
                    ", pageSize='" + pageSize + '\'' +
                    ", totalCount='" + totalCount + '\'' +
                    ", tbMainOrderId='" + tbMainOrderId + '\'' +
                    ", keywords='" + keywords + '\'' +
                    ", storeId='" + storeId + '\'' +
                    ", detailList=" + detailList +
                    ", categoryName='" + categoryName + '\'' +
                    ", firstCategoryId='" + firstCategoryId + '\'' +
                    ", firstCategoryName='" + firstCategoryName + '\'' +
                    ", secondCategoryId='" + secondCategoryId + '\'' +
                    ", secondCategoryName='" + secondCategoryName + '\'' +
                    ", totalPage='" + totalPage + '\'' +
                    '}';
        }

        public String getPageNo() {
            return pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public String getTbMainOrderId() {
            return tbMainOrderId;
        }

        public void setTbMainOrderId(String tbMainOrderId) {
            this.tbMainOrderId = tbMainOrderId;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public List<DetailListVO> getDetailList() {
            return detailList;
        }

        public void setDetailList(List<DetailListVO> detailList) {
            this.detailList = detailList;
        }

        public String getFirstCategoryId() {
            return firstCategoryId;
        }

        public void setFirstCategoryId(String firstCategoryId) {
            this.firstCategoryId = firstCategoryId;
        }

        public String getFirstCategoryName() {
            return firstCategoryName;
        }

        public void setFirstCategoryName(String firstCategoryName) {
            this.firstCategoryName = firstCategoryName;
        }

        public String getSecondCategoryId() {
            return secondCategoryId;
        }

        public void setSecondCategoryId(String secondCategoryId) {
            this.secondCategoryId = secondCategoryId;
        }

        public String getSecondCategoryName() {
            return secondCategoryName;
        }

        public void setSecondCategoryName(String secondCategoryName) {
            this.secondCategoryName = secondCategoryName;
        }

        public String getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(String totalPage) {
            this.totalPage = totalPage;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getTimeText() {
            return timeText;
        }

        public void setTimeText(String timeText) {
            this.timeText = timeText;
        }

        public String getNorm() {
            return norm;
        }

        public void setNorm(String norm) {
            this.norm = norm;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }

    public class OtherCase implements Serializable{
        private NoSearchResult noSearchResult;

        public NoSearchResult getNoSearchResult() {
            return noSearchResult;
        }

        public void setNoSearchResult(NoSearchResult noSearchResult) {
            this.noSearchResult = noSearchResult;
        }

        public class NoSearchResult implements Serializable{
            private String spoken;
            private String spokenTxt;

            public String getSpoken() {
                return spoken;
            }

            public void setSpoken(String spoken) {
                this.spoken = spoken;
            }

            public String getSpokenTxt() {
                return spokenTxt;
            }

            public void setSpokenTxt(String spokenTxt) {
                this.spokenTxt = spokenTxt;
            }
        }
    }
}
