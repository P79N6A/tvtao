package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupPromotion;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemLogo;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.entity.ShopCartGoodsBean;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by linmu on 2018/6/13.
 */

/**
 * layout file: layout_new_shop_cart_item_info.xml
 */
public class NewGoodsItemInfoView extends RelativeLayout {
    //    电视淘宝返利标
    private final static String TVTAO_FANLI_ICON = "TVTAO_FANLI_ICON";
    private TextView tvTitle;
    private TextView tvSku;
    //失效商品不显示
    private LinearLayout llInfo, ll_last_item;
    private TextView tvErrorInfo;
    private NewPriceNumWeightView rlPriceNumWeight;
    //电视淘宝营销表达
    private NewShopCartLabelView labelTagTvTb;
    //大促营销标签：购物津贴
    private NewShopCartLabelView labelSubsidy;
    // 兴业营销表达
    private NewShopCartLabelView labelInternationalCoupon;
    //    限时提醒类信息
    private NewShopCartLabelView labelJhs;

    private TextView tvDepreciate;
    private TextView tvTax;

    private TextViewBorder label_buy_rebate;


    public NewGoodsItemInfoView(Context context) {
        super(context);
    }

    public NewGoodsItemInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewGoodsItemInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSku = (TextView) findViewById(R.id.tv_sku);
        llInfo = (LinearLayout) findViewById(R.id.ll_info);
        ll_last_item = (LinearLayout) findViewById(R.id.ll_last_item);
        tvErrorInfo = (TextView) findViewById(R.id.tv_error_info);
        rlPriceNumWeight = (NewPriceNumWeightView) findViewById(R.id.rl_price_num_weight);
        labelTagTvTb = (NewShopCartLabelView) findViewById(R.id.label_tag_tv_tb);
        label_buy_rebate = (TextViewBorder) findViewById(R.id.label_buy_rebate);
        labelSubsidy = (NewShopCartLabelView) findViewById(R.id.label_subsidy);
        labelInternationalCoupon = (NewShopCartLabelView) findViewById(R.id.label_international_coupon);
        labelJhs = (NewShopCartLabelView) findViewById(R.id.label_jhs);
        tvDepreciate = (TextView) findViewById(R.id.tv_depreciate);
        tvTax = (TextView) findViewById(R.id.tv_tax);
    }


    public void setItemInfoData(ShopCartGoodsBean shopCartGoodsBean) {
        ItemComponent itemComponent = shopCartGoodsBean.getCartGoodsComponent().getItemComponent();
//        title和SKU一定存在不管是否是失效商品
        if (!TextUtils.isEmpty(itemComponent.getTitle())) {
            tvTitle.setVisibility(VISIBLE);
            tvTitle.setText(itemComponent.getTitle());
            if (shopCartGoodsBean.isInvalid() && !itemComponent.getSku().isSkuInvalid()) {
                tvTitle.setTextColor(getResources().getColor(R.color.new_shop_cart_title_invalid));
            } else {
                tvTitle.setTextColor(getResources().getColor(R.color.new_shop_cart_title_valid));
            }
        } else {
            tvTitle.setText(null);
            tvTitle.setVisibility(GONE);

        }
        if (itemComponent.getSku() != null && !TextUtils.isEmpty(itemComponent.getSku().getTitle())) {
            tvSku.setVisibility(VISIBLE);
            tvSku.setText(itemComponent.getSku().getTitle().replace(";", "; "));
        } else {
            tvSku.setText(null);
            tvSku.setVisibility(GONE);
        }
//        失效商品，只展示失效信息
        if (shopCartGoodsBean.isInvalid()) {
            llInfo.setVisibility(GONE);
            //sku失效
            if (itemComponent.getSku() != null && itemComponent.getSku().isSkuInvalid()) {
                tvErrorInfo.setVisibility(VISIBLE);
                tvErrorInfo.setText(getResources().getString(R.string.new_shop_choose_product_info_again));
            } else {
                tvSku.setText(null);
                tvSku.setVisibility(GONE);
                if (!TextUtils.isEmpty(itemComponent.getCodeMsg())) {
                    tvErrorInfo.setText(itemComponent.getCodeMsg());
                    tvErrorInfo.setVisibility(VISIBLE);
                } else {
                    tvErrorInfo.setText(null);
                    tvErrorInfo.setVisibility(GONE);
                }

            }


        } else {
            llInfo.setVisibility(VISIBLE);
            tvErrorInfo.setVisibility(GONE);
//            价格
            if (itemComponent.getItemPay() != null && !TextUtils.isEmpty(itemComponent.getItemPay().getNowTitle())) {
                String nowTitle = itemComponent.getItemPay().getNowTitle();
                rlPriceNumWeight.setTvPrice(transformPrice(nowTitle));
            }
//            数量
            if (itemComponent.getItemQuantity() != null) {
                rlPriceNumWeight.setTvNum("x" + itemComponent.getItemQuantity().getQuantity());
            }
//            重量
            if (itemComponent.getWeight() != null && !TextUtils.isEmpty(itemComponent.getWeight().getTitle())) {
                rlPriceNumWeight.setTvWeight(itemComponent.getWeight().getTitle());
            } else {
                rlPriceNumWeight.setTvWeight(null);
            }
//            预售修改字体和普通商品保持一致
            /*if (itemComponent.isPreBuyItem() || itemComponent.isPreSell()) {
                rlPriceNumWeight.setTextColor(true);
            } else {
                rlPriceNumWeight.setTextColor(false);
            }*/

            if (itemComponent.getSku() != null && !TextUtils.isEmpty(itemComponent.getSku().getTitle())) {
                tvSku.setVisibility(VISIBLE);
                tvSku.setText(itemComponent.getSku().getTitle().replace(";", "; "));
            } else {
                tvSku.setText(null);
                tvSku.setVisibility(GONE);

            }
        }


    }

    //电视淘宝营销表达：返现
    public void setLabelTagTvTb(JSONObject object) {
        if (object == null) {
            labelTagTvTb.setVisibility(GONE);
        } else {
            if (object.containsKey("text") && !TextUtils.isEmpty(object.getString("text"))) {
                String rebateCoupon = object.getString("text");
                if (rebateCoupon.equals("|null")) {
                    if (object.containsKey("pic") && !TextUtils.isEmpty(object.getString("pic"))) {
                        labelTagTvTb.setVisibility(VISIBLE);
                        labelTagTvTb.setDataTVTb(object.getString("pic"), null);
                    } else {
                        labelTagTvTb.setVisibility(GONE);
                    }
                } else {
                    //如果text为空，则看图片，图片不为空则展示图片
                    if (!TextUtils.isEmpty(rebateCoupon)) {
                        //后台(兴隆)为以后考虑：如果不返回|，则是预售
                        if (rebateCoupon.contains("|")) {
                            String[] rebate = rebateCoupon.split("\\|");
                            if (rebate.length >= 2 && !TextUtils.isEmpty(rebate[1])) {
                                String coupon = Utils.getRebateCoupon(rebate[1]);
                                //后台(兴隆)定义若是预售：则返回 message|null,则coupon是null表示预售
                                if (coupon != null && coupon.equals("null")) {
                                    labelTagTvTb.setVisibility(VISIBLE);
                                    labelTagTvTb.setDataTVTb(object.getString("pic"), rebate[0]);
                                } else if (coupon != null) {
                                    labelTagTvTb.setVisibility(VISIBLE);
                                    if (object.containsKey("pic") && object.getString("pic") != null) {
                                        labelTagTvTb.setDataTVTb(object.getString("pic"), rebate[0] + " ¥ " + coupon);
                                    } else {
                                        labelTagTvTb.setDataTVTb(null, rebate[0] + " ¥ " + coupon);
                                    }
                                } else {

                                    labelTagTvTb.setVisibility(GONE);
                                }
                            } else {
                                labelTagTvTb.setDataTVTb(null, null);

                                labelTagTvTb.setVisibility(GONE);
                            }
                        } else {
                            labelTagTvTb.setVisibility(VISIBLE);
                            labelTagTvTb.setDataTVTb(object.getString("pic"), rebateCoupon);
                        }
                    } else {
                        if (object.containsKey("pic") && !TextUtils.isEmpty(object.getString("pic"))) {
                            labelTagTvTb.setVisibility(VISIBLE);
                            labelTagTvTb.setDataTVTb(object.getString("pic"), null);
                        } else {
                            labelTagTvTb.setVisibility(GONE);
                        }

                    }
                }


            } else {
                labelTagTvTb.setVisibility(GONE);
            }
        }


    }


    public void setBuyRebateVisible(ItemLogo icon) {
        if (icon == null) {
            label_buy_rebate.setVisibility(GONE);
        } else {
            label_buy_rebate.setVisibility(VISIBLE);
            label_buy_rebate.setText(icon.fields.tags.get(0).text);
            String color = icon.fields.tags.get(0).bgColor;
            String borderColor = icon.fields.tags.get(0).borderColor;
            label_buy_rebate.setTextColor(color.charAt(0) == '#' ? Color.parseColor(color) : Color.parseColor('#' + color));
            label_buy_rebate.setBorderColor(borderColor.charAt(0) == '#' ? Color.parseColor(borderColor) : Color.parseColor('#' + borderColor));
        }
    }

    //营销券
    public void setLabelSubsidy(GroupPromotion groupPromotion) {
        if (groupPromotion == null) {
            labelSubsidy.setVisibility(GONE);
        } else {
            labelSubsidy.setVisibility(VISIBLE);
            labelSubsidy.setData(groupPromotion);
        }
    }


    //营销券
    public void setLabelInternationalCoupon(ItemLogo icon) {
        if (icon == null) {
            labelInternationalCoupon.setVisibility(GONE);
        } else {
            labelInternationalCoupon.setVisibility(VISIBLE);
            labelInternationalCoupon.setData(icon);
        }
    }

    //聚划算
    public void setLabelJhs(ItemLogo icon) {
        if (icon == null) {
            labelJhs.setVisibility(GONE);
        } else {
            labelJhs.setVisibility(VISIBLE);
            labelJhs.setData(icon);
        }
    }

    //降价提醒>限购提示>库存紧张
    public void setTvDepreciate(String depreciate) {

        if (!StringUtil.isEmpty(depreciate)) {
            if (depreciate.contains("￥")) {
                depreciate = depreciate.replaceAll("￥", "¥ ");
            }
            tvDepreciate.setText(depreciate);
            tvDepreciate.setVisibility(VISIBLE);
        } else {
            tvDepreciate.setText(null);
            tvDepreciate.setVisibility(GONE);
        }
    }
    //

    //预计关税>仅售区域
    public void setTvTax(String tax) {
        if (!TextUtils.isEmpty(tax)) {
            if (tax.contains("￥")) {
                tax = tax.replaceAll("￥", "¥ ");
            }
            tvTax.setText(tax);
            tvTax.setVisibility(VISIBLE);
        } else {
            tvTax.setText(null);
            tvTax.setVisibility(GONE);
        }
    }


    /**
     * （特殊处理）购物车内商品价格展示格式,默认保留小数点后两位，如果小数点后为0则舍去
     *
     * @param nowTitle 当前服务端返回的价格，格式如 ￥20.00
     */
    private String transformPrice(String nowTitle) {
        String result;
        String[] titleSpilt = nowTitle.split("￥");
        float floatTitle;
        try {
            floatTitle = Float.parseFloat(titleSpilt[1]);
        } catch (Exception e) {
            result = nowTitle;
            return result;
        }
        result = "￥" + NumberFormat.getInstance().format(floatTitle);
        return result;
    }

    public void resetState() {

    }

    public void maxVisibleFour() {//从上到下只展示四个，其他不展示
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(String.valueOf(label_buy_rebate), label_buy_rebate.getVisibility());
        map.put(String.valueOf(labelSubsidy), labelSubsidy.getVisibility());
        map.put(String.valueOf(labelInternationalCoupon), labelInternationalCoupon.getVisibility());

        if (labelTagTvTb.getVisibility() == VISIBLE) {
            if (!map.containsValue(GONE)) {
                labelJhs.setVisibility(GONE);
                ll_last_item.setVisibility(GONE);
            } else if (labelJhs.getVisibility() == VISIBLE) {
                int i = 0;
                for (Integer ir : map.values()) {
                    if (ir == VISIBLE) {
                        i++;
                    }
                }

                if (i == 2) {
                    ll_last_item.setVisibility(GONE);
                }
            }

        } else if (labelTagTvTb.getVisibility() == GONE) {
            if (!map.containsValue(GONE) && labelJhs.getVisibility() == VISIBLE) {
                ll_last_item.setVisibility(GONE);
            }
        }

//        labelTagTvTb
//        label_buy_rebate
//        labelSubsidy
//        labelInternationalCoupon
//        labelJhs
//        ll_last_item
    }

}
