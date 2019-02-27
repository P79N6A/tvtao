package com.yunos.tvtaobao.biz.request.bo;


import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：店铺详情页，列出超级会员代金券+店铺代金券信息
 */
public class VouchersList {

    /**
     * result : {"activityId":3889,"name":"正品NewTheBest","amount":"秒杀正品coswel可薇儿-补水啫喱黄瓜面膜（2片）H07315 H","description":"博纳屋豹纹桌面收纳盒收纳包两件套 S01340","status":9073,"conditionType":4998,"exchangeType":4553,"exchangeDescription":{"text":"爱丽小屋可爱动物小蜜蜂润唇膏13gH08342 J11005日本泰福高螺纹杯不锈钢真空保温杯保冷杯","highlight":"菱形提花麻花纹针织毛线半指长手套袖套J08819 MIKUER蜜酷儿玫瑰精油香氛保湿美白护手霜25g*2只装 冬季保暖情侣居家棉拖鞋软底地板拖鞋室内男女"},"exchangeTips":{"text":"伸缩双层架厨房置物架宜家水槽下","highlight":"NewTheBest纽比士玫瑰"},"exchangeLeftNum":6735,"exchangeConsumeAmount":9747.7,"popupType":4169,"successTips":{"text":"专业美甲剪/指甲剪工具铁匠手工铸造美甲指甲剪H04286 H71769保","highlight":"DJ美国康宁/VISIONS晶彩透明锅/超值装二件组/2件套 NewTheBest/纽比士金盏"},"hongbaoTotalNum":140,"popupTips":{"text":"女士纽比士植物干细胞面膜美白保湿补水面贴膜","highlight":"欧润哲【8折】医药箱隔盒装药盒创意家庭用药品收纳盒子药品"},"popupLink":"柠檬绿茶糖果色保暖棉拖鞋镶钻PU皮拖鞋居","isNewExclusive":false,"popupParam":"冬季棉鞋加厚保暖情侣款棉鞋包跟棉鞋保"}
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        //代金券活动ID
        private String activityId;
        //名称
        private String name;
        //金额描述
        private String amount;
        //使用限制描述
        private String description;
        //0: 未领取 1: 已领取 3: 不是超级会员 4: 奖励金不足 5:不是年终奖红包
        private String status;
        //0: 普通红包 1: 超级会员专享红包 2: 年终奖红包
        private String conditionType;
        //-1: 年终奖 0: 免费 1: 权益 2: 红包 3: 奖励金
        private String exchangeType;
        //兑换文案
        private ExchangeDescriptionBean exchangeDescription;
        //兑换提示
        private ExchangeTipsBean exchangeTips;
        //兑换剩余
        private String exchangeLeftNum;
        //兑换消耗
        private String exchangeConsumeAmount;
        //1: 方案A 2: 方案B
        private String popupType;
        //兑换完的文案
        private SuccessTipsBean successTips;
        //每月可领取的红包数
        private String hongbaoTotalNum;
        //弹窗文案
        private PopupTipsBean popupTips;
        //弹窗跳转链接
        private String popupLink;
        //是都新用户专享
        private String isNewExclusive;
        //弹窗里的一些数字信息
        private String popupParam;

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }

        public String getExchangeType() {
            return exchangeType;
        }

        public void setExchangeType(String exchangeType) {
            this.exchangeType = exchangeType;
        }

        public ExchangeDescriptionBean getExchangeDescription() {
            return exchangeDescription;
        }

        public void setExchangeDescription(ExchangeDescriptionBean exchangeDescription) {
            this.exchangeDescription = exchangeDescription;
        }

        public ExchangeTipsBean getExchangeTips() {
            return exchangeTips;
        }

        public void setExchangeTips(ExchangeTipsBean exchangeTips) {
            this.exchangeTips = exchangeTips;
        }

        public String getExchangeLeftNum() {
            return exchangeLeftNum;
        }

        public void setExchangeLeftNum(String exchangeLeftNum) {
            this.exchangeLeftNum = exchangeLeftNum;
        }

        public String getExchangeConsumeAmount() {
            return exchangeConsumeAmount;
        }

        public void setExchangeConsumeAmount(String exchangeConsumeAmount) {
            this.exchangeConsumeAmount = exchangeConsumeAmount;
        }

        public String getPopupType() {
            return popupType;
        }

        public void setPopupType(String popupType) {
            this.popupType = popupType;
        }

        public SuccessTipsBean getSuccessTips() {
            return successTips;
        }

        public void setSuccessTips(SuccessTipsBean successTips) {
            this.successTips = successTips;
        }

        public String getHongbaoTotalNum() {
            return hongbaoTotalNum;
        }

        public void setHongbaoTotalNum(String hongbaoTotalNum) {
            this.hongbaoTotalNum = hongbaoTotalNum;
        }

        public PopupTipsBean getPopupTips() {
            return popupTips;
        }

        public void setPopupTips(PopupTipsBean popupTips) {
            this.popupTips = popupTips;
        }

        public String getPopupLink() {
            return popupLink;
        }

        public void setPopupLink(String popupLink) {
            this.popupLink = popupLink;
        }

        public String getIsNewExclusive() {
            return isNewExclusive;
        }

        public void setIsNewExclusive(String isNewExclusive) {
            this.isNewExclusive = isNewExclusive;
        }

        public String getPopupParam() {
            return popupParam;
        }

        public void setPopupParam(String popupParam) {
            this.popupParam = popupParam;
        }

        public static class ExchangeDescriptionBean {

            private String text;
            private String highlight;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHighlight() {
                return highlight;
            }

            public void setHighlight(String highlight) {
                this.highlight = highlight;
            }
        }

        public static class ExchangeTipsBean {

            private String text;
            private String highlight;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHighlight() {
                return highlight;
            }

            public void setHighlight(String highlight) {
                this.highlight = highlight;
            }
        }

        public static class SuccessTipsBean {

            private String text;
            private String highlight;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHighlight() {
                return highlight;
            }

            public void setHighlight(String highlight) {
                this.highlight = highlight;
            }
        }

        public static class PopupTipsBean {

            private String text;
            private String highlight;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getHighlight() {
                return highlight;
            }

            public void setHighlight(String highlight) {
                this.highlight = highlight;
            }
        }
    }
}
