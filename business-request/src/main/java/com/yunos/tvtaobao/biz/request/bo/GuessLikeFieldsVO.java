package com.yunos.tvtaobao.biz.request.bo;

import android.content.Context;

/**
 * Created by yuanqihui on 2018/6/28.
 */

public class GuessLikeFieldsVO {
    private BottomTipVO bottomTip;
    private String itemId;
    private MasterPicVO masterPic;
    /**
     * cent :
     * separator :
     * symbol : ¥
     * unit :
     * yuan : 134
     */

    private PriceVO price;

    private SimilarVO similar;
    private TitleVO title;


    public BottomTipVO getBottomTip() {
        return bottomTip;
    }

    public void setBottomTip(BottomTipVO bottomTip) {
        this.bottomTip = bottomTip;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public MasterPicVO getMasterPic() {
        return masterPic;
    }

    public void setMasterPic(MasterPicVO masterPic) {
        this.masterPic = masterPic;
    }

    public PriceVO getPrice() {
        return price;
    }

    public void setPrice(PriceVO price) {
        this.price = price;
    }

    public SimilarVO getSimilar() {
        return similar;
    }

    public void setSimilar(SimilarVO similar) {
        this.similar = similar;
    }

    public TitleVO getTitle() {
        return title;
    }

    public void setTitle(TitleVO title) {
        this.title = title;
    }

    public static class BottomTipVO {
        private TextVO text;

        @Override
        public String toString() {
            return "BottomTipVO{" +
                    "text=" + text +
                    '}';
        }

        public TextVO getText() {
            return text;
        }

        public void setText(TextVO text) {
            this.text = text;
        }

        public static class TextVO {
            private String content;

            @Override
            public String toString() {
                return "TextVO{" +
                        "content='" + content + '\'' +
                        '}';
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }

    public static class MasterPicVO {
        private String picUrl;

        @Override
        public String toString() {
            return "MasterPicVO{" +
                    "picUrl='" + picUrl + '\'' +
                    '}';
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }

    public static class PriceVO {
        private String cent;
        private String separator;
        private String symbol;
        private String unit;
        private String yuan;

        @Override
        public String toString() {
            return "PriceVO{" +
                    "cent='" + cent + '\'' +
                    ", separator='" + separator + '\'' +
                    ", symbol='" + symbol + '\'' +
                    ", unit='" + unit + '\'' +
                    ", yuan='" + yuan + '\'' +
                    '}';
        }

        public String getCent() {
            return cent;
        }

        public void setCent(String cent) {
            this.cent = cent;
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getYuan() {
            return yuan;
        }

        public void setYuan(String yuan) {
            this.yuan = yuan;
        }
    }

    public class SimilarVO {
        private String action;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    public static class TitleVO {
        private ContextVO context;
        private IconVo icon;

        @Override
        public String toString() {
            return "TitleVO{" +
                    "context=" + context +
                    ", icon=" + icon +
                    '}';
        }

        public static class ContextVO {
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        private class IconVo {
            private String picUrl;

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }
        }

        public ContextVO getContext() {
            return context;
        }

        public void setContext(ContextVO context) {
            this.context = context;
        }

        public IconVo getIcon() {
            return icon;
        }

        public void setIcon(IconVo icon) {
            this.icon = icon;
        }
    }

    @Override
    public String toString() {
        return "GuessLikeFieldsVO{" +
                "bottomTip=" + bottomTip +
                ", itemId='" + itemId + '\'' +
                ", masterPic=" + masterPic +
                ", price=" + price +
                ", similar=" + similar +
                ", title=" + title +
                '}';
    }
}
