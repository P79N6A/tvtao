package com.yunos.tvtaobao.juhuasuan.util;


import android.text.TextUtils;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.juhuasuan.request.config.RequestConfig;

import java.util.ArrayList;

public class ModelTranslator {

    public static final String KEY_TODAY_ALL = "-1";

    /**
     * option转化为CategoryMO
     * @param ops
     * @return
     */
    public static CountList<CategoryMO> translateCategory(CountList<Option> ops) {
        CountList<CategoryMO> list = null;
        if (ops != null) {
            list = new CountList<CategoryMO>();
            list.setCurrentPage(ops.getCurrentPage());
            list.setItemCount(ops.getItemCount());
            list.setPageSize(ops.getPageSize());
            list.setTotalPage(ops.getTotalPage());
            for (Option op : ops) {
                CategoryMO c = new CategoryMO();
                // 是Value？
                c.setCid(op.getValue());
                c.setOptStr(op.getOptStr());
                // 仅支持两级，暂不用递归。
                if (op.getChildren() != null) {
                    ArrayList<CategoryMO> chList = new ArrayList<CategoryMO>();
                    if (ops != null) {
                        for (Option opt : op.getChildren()) {
                            CategoryMO cate = new CategoryMO();
                            // 是Value？
                            cate.setCid(opt.getValue());
                            cate.setOptStr(opt.getOptStr());
                            cate.setName(opt.getDisplayName());
                            if (null != opt.getParams()) {
                                String type = opt.getParams().get("itemType");
                                if (!TextUtils.isEmpty(type)) {
                                    cate.setType(Integer.valueOf(type));
                                }
                            }
                            cate.setAll(KEY_TODAY_ALL.equals(opt.getValue()));
                            chList.add(cate);
                        }
                    }
                    c.setChildren(chList);
                }
                c.setName(op.getDisplayName());
                if (null != op.getParams()) {
                    String type = op.getParams().get("itemType");
                    if (!TextUtils.isEmpty(type)) {
                        c.setType(Integer.valueOf(type));
                    }
                }
                c.setAll(KEY_TODAY_ALL.equals(op.getValue()));
                list.add(c);
            }
        }

        return list;
    }

    /**
     * option转化为BrandModel
     * @param ops
     * @return
     */
    public static CountList<BrandMO> translateBrand(CountList<Option> ops) {
        CountList<BrandMO> list = null;
        if (ops != null) {
            list = new CountList<BrandMO>();
            list.setCurrentPage(ops.getCurrentPage());
            list.setItemCount(ops.getItemCount());
            list.setPageSize(ops.getPageSize());
            list.setTotalPage(ops.getTotalPage());
            for (Option op : ops) {
                BrandMO b = new BrandMO();
                b.setCode(op.getOptStr());
                if (op.getExtend() != null) {
                    b.setItemCount(op.getExtend().getSoldCount());
                    // b.juBrand_id =
                    b.setJuDiscount(String.valueOf(op.getExtend().getLowestDiscount() / 100));
                    // 此两项缺前缀
                    b.setJuBanner(RequestConfig.getBrandImageServer(op.getExtend().getWlBannerImgUrl()));
                    b.setJuLogo(RequestConfig.getBrandImageServer(op.getExtend().getBrandLogoUrl()));
                    b.setJuSlogo(op.getExtend().getWlBrandDesc());
                    b.setOnline(op.getExtend().getOnline());

                    if (op.getExtend().getOnlineStartTime() != null) {
                        b.setOnlineStartTimeLong(StringUtil.convertToLong(op.getExtend().getOnlineStartTime()));
                    }
                    if (op.getExtend().getOnlineEndTime() != null) {
                        b.setOnlineEndTime(op.getExtend().getOnlineEndTime());
                        b.setOnlineStartTimeLong(StringUtil.convertToLong(op.getExtend().getOnlineEndTime()));
                    }
                }
                b.setName(op.getDisplayName());
                list.add(b);
            }
        }
        return list;
    }
}
