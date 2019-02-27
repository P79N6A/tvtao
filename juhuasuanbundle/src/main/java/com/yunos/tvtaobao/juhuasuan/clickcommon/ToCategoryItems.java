package com.yunos.tvtaobao.juhuasuan.clickcommon;



import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeItemsBo;

import java.io.Serializable;

import com.yunos.tvtaobao.juhuasuan.config.ConstValues.HomeItemTypes;

/**
 * 调用分类商品列表页
 * @author hanqi
 */
public class ToCategoryItems {

    /**
     * 进入商品列表页
     * @param homeCate
     * @param homeItem
     * @param category
     */
    public static void category(JuBaseActivity activity, HomeCatesBo homeCate, HomeItemsBo homeItem, CategoryMO category) {
        HomeBundle homeBundle = new HomeBundle(homeCate, homeItem, category);
        category(activity, homeBundle);
    }

    /**
     * 进入商品列表页
     * @param homeBundle
     */
    public static void category(JuBaseActivity activity, HomeBundle homeBundle) {
        String cateId = null;
        if (null != homeBundle.cid) {
            cateId = homeBundle.cid;
        }
        switch (homeBundle.type) {
            case CATE:
                ToOldCategoryItems.category(activity, cateId);
                break;
            case JMP:
                ToOldCategoryItems.category(activity, "1000090");
                break;
            default:
                break;
        }

        //        Intent intent = new Intent(context, ClassificationActivity.class);
        //        Bundle bundle = new Bundle();
        //        bundle.putSerializable(HomeBundle.INTENT_KEY_OBJECT, homeBundle);
        //        intent.putExtra(HomeBundle.INTENT_KEY_BUNDLE, bundle);
        //        context.startActivity(intent);
    }

    public static class HomeBundle implements Serializable {

        private static final long serialVersionUID = 8721746462565953383L;

        public final static String INTENT_KEY_BUNDLE = "IntentGoodList"; // BUNDLE 名称
        public final static String INTENT_KEY_OBJECT = "HomeBundle"; // 对象名称

        private String cid;
        private String name;
        private String eName;
        private HomeItemTypes type;
        private String optStr;
        private String icon;
        private String iconHl;
        private String bgcolor;
        private String desc; //广告语

        public HomeBundle() {
        }

        public HomeBundle(HomeCatesBo homeCate, HomeItemsBo homeItem, CategoryMO category) {
            setData(homeItem);
            setData(homeCate);
            setData(category);
        }

        public void setData(CategoryMO category) {
            if (null != category) {
                if (StringUtil.isEmpty(this.cid)) {
                    this.cid = String.valueOf(category.getCid());
                }
                if (StringUtil.isEmpty(this.name)) {
                    this.name = category.getName();
                }
                if (StringUtil.isEmpty(this.icon)) {
                    this.icon = category.getIcon();
                }
                if (StringUtil.isEmpty(this.iconHl)) {
                    this.iconHl = category.getIconHl();
                }
                this.optStr = category.getOptStr();
            }
        }

        public void setData(HomeCatesBo homeCate) {
            if (null != homeCate) {
                this.cid = homeCate.getCid();
                this.name = homeCate.getName();
                this.eName = homeCate.getE_name();
                this.type = HomeItemTypes.valueOf(homeCate.getType());
                this.icon = homeCate.getIcon();
                this.iconHl = homeCate.getIconHl();
                this.bgcolor = homeCate.getBgcolor();
            }
        }

        public void setData(HomeItemsBo homeItem) {
            if (null != homeItem) {

                this.name = homeItem.getTitle();
                this.eName = homeItem.getE_name();
                this.type = HomeItemTypes.valueOf(homeItem.getType());
                this.desc = homeItem.getDesc();
                if (null != homeItem.getContent()) {
                    this.cid = homeItem.getContent().get("cid");
                    this.icon = homeItem.getContent().get("icon");
                    this.iconHl = homeItem.getContent().get("iconHl");
                    this.bgcolor = homeItem.getContent().get("bgcolor");
                }

            }
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String geteName() {
            return eName;
        }

        public void seteName(String eName) {
            this.eName = eName;
        }

        public HomeItemTypes getType() {
            return type;
        }

        public void setType(HomeItemTypes type) {
            this.type = type;
        }

        public String getOptStr() {
            return optStr;
        }

        public void setOptStr(String optStr) {
            this.optStr = optStr;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIconHl() {
            return iconHl;
        }

        public void setIconHl(String iconHl) {
            this.iconHl = iconHl;
        }

        public String getBgcolor() {
            return bgcolor;
        }

        public void setBgcolor(String bgcolor) {
            this.bgcolor = bgcolor;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
