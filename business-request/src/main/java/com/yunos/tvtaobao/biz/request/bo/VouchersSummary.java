package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：
 */
public class VouchersSummary {
    //是否启用新的样式
    private String newUI;
    //领取按钮
    private List<Button> buttons;

    public class Button {
        //描述
        private String description;
        //0: 未领取， 1:已领取
        private String status;
        //0: 普通专享红包 1: 超级会员专享红包 2: 年终奖红包
        private String type;
        //金额
        private String value;
        //描述
        private String title;
        //详细描述
        private String titleDetail;
        //第二行描述
        private String subTitle;
        //是否包含多个红包
        private String hasMultiHongbao;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleDetail() {
            return titleDetail;
        }

        public void setTitleDetail(String titleDetail) {
            this.titleDetail = titleDetail;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getHasMultiHongbao() {
            return hasMultiHongbao;
        }

        public void setHasMultiHongbao(String hasMultiHongbao) {
            this.hasMultiHongbao = hasMultiHongbao;
        }
    }


    public String getNewUI() {
        return newUI;
    }

    public void setNewUI(String newUI) {
        this.newUI = newUI;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }
}
