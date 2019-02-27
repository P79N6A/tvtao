package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupPromotion;
import com.taobao.wireless.trade.mcart.sdk.co.biz.Icon;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemLogo;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.newcart.entity.ShopCartGoodsBean;
import com.yunos.tvtaobao.newcart.ui.adapter.NewShopCartListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by linmu on 2018/6/27.
 * <p>
 * layout file: layout_new_shop_cart_item_view.xml
 */
public class NewShopCartItemInfoView extends LinearLayout {

    private final static String DEFAULT = "DEFAULT";

    //    双11icons(b类大促标，一般都是规则中心配置)
    private final static String DOUBLE11 = "DOUBLE11_ICON";
    //    淘宝嘉年华(c类大促标，一般都是规则中心配置)
    private final static String CARNIVAL_ICON = "CARNIVAL_ICON";
    //    超级会员标
    private final static String SUPERMAMBER_ICON = "SUPERMAMBER_ICON";
    //    抢先订
    private final static String PRIORITY_BUY_ICON = "PRIORITY_BUY_ICON";
    //    A+/A营销标
    private final static String MARKETING_CAMPAIN_ICON = "MARKETING_CAMPAIN_ICON";
    //    聚划算预热标icon标记
    private final static String JHS_PREHEAT_ICON = "JHS_PREHEAT_ICON";
    //    聚划算预热标icon标记中的文字
    private final static String JHS_PREHEAT_ICON_TEXT = "JHS_PREHEAT_ICON_TEXT";
    //    电视淘宝返利标
    private final static String TVTAO_FANLI_ICON = "TVTAO_FANLI_ICON";

    private final static String SKU_EXT = "skuExt";
    //降价提醒
    private final static String PRICE_DOWN = "priceDown";
    //限购提示
    private final static String BUY_LIMIT = "buyLimit";
    //库存紧张
    private final static String INVENTORY_TENSION = "inventoryTension";
    //预售
    private final static String PRE_SELL = "preSell";
    //聚划算
    private final static String JHS = "jhs";
    //抢先订
    private final static String YOU_XIAN_GOUY = "yxg";
    //买就返后续可能是别的图标，依赖手淘配置
    private final static String MJFTAG = "tag";


    // 商品操作功能按钮的点击事件
    private OnShopItemClickListener onShopItemClickListener; // 商品操作功能按钮的点击事件

    private RoundImageView ivToutu;
    private ImageView ivTag1;
    private ImageView ivTag2;
    private ImageView ivTag3;
    private boolean tag1Show;
    private boolean tag2Show;
    private boolean tag3Show;

    private NewGoodsItemInfoView newGoodsItemInfoView;
    private NewShopCartButton tvModify;
    private NewShopCartButton tvDetails;
    private NewShopCartButton tvDelete;
    // 商品卡片位的宽度
    private int mShopItemCardWidth;
    private NewShopCartGoodsSelectView goodsSelectView;
    // 下载管理器
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions mImageOptions; // 图片加载的参数设置

    private ShopCartGoodsBean shopCartGoodsBean;
    private ItemComponent itemComponent;
    private Context context;

    public NewShopCartItemInfoView(Context context) {
        super(context);
    }

    public NewShopCartItemInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewShopCartItemInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView(getContext());
    }

    public void initView(Context context) {
        this.context = context;
        ivToutu = (RoundImageView) findViewById(R.id.iv_toutu);
        ivToutu.setRound(true, true, false, false);
        ivTag1 = (ImageView) findViewById(R.id.iv_tag1);
        ivTag2 = (ImageView) findViewById(R.id.iv_tag2);
        ivTag3 = (ImageView) findViewById(R.id.iv_tag3);
        newGoodsItemInfoView = (NewGoodsItemInfoView) findViewById(R.id.goods_view);
        tvModify = (NewShopCartButton) findViewById(R.id.tv_modify);
        tvDetails = (NewShopCartButton) findViewById(R.id.tv_details);
        tvDelete = (NewShopCartButton) findViewById(R.id.tv_delete);
        goodsSelectView = (NewShopCartGoodsSelectView) findViewById(R.id.new_shop_cart_select);
        goodsSelectView.setNextFocusLeftId((tvModify.getVisibility() == VISIBLE) ? (R.id.tv_modify) : (R.id.tv_details));
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheOnDisc(true).cacheInMemory(true).build();
        mShopItemCardWidth = context.getResources().getDimensionPixelSize(R.dimen.dp_240);
        setClick();

    }

    public void resetState() {
        tvDelete.setBackgroundResource(R.drawable.new_shop_cart_button_divider_shape);
        tvDelete.setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
        tvModify.setBackgroundResource(R.drawable.new_shop_cart_button_divider_shape);
        tvModify.setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
        tvDetails.setBackgroundResource(R.drawable.new_shop_cart_button_divider_shape);
        tvDetails.setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
        goodsSelectView.resetState();
    }

    public void setData(ShopCartGoodsBean shopCartGoodsBean) {
        if (shopCartGoodsBean == null) {
            return;
        }

        this.shopCartGoodsBean = shopCartGoodsBean;
        goodsSelectView.setData(shopCartGoodsBean);
        CartGoodsComponent cartGoodsComponent = shopCartGoodsBean.getCartGoodsComponent();
        if (cartGoodsComponent == null) {
            return;
        }
        ItemComponent itemComponent = cartGoodsComponent.getItemComponent();
        this.itemComponent = itemComponent;
        if (itemComponent == null) {
            return;
        }

        //设置数据
        newGoodsItemInfoView.setItemInfoData(shopCartGoodsBean);

        //失效宝贝不展示修改
        if (shopCartGoodsBean.isInvalid() || itemComponent.isPreSell()) {
            tvModify.setVisibility(GONE);
        } else {
            tvModify.setVisibility(VISIBLE);
        }
        goodsSelectView.setNextFocusLeftId((tvModify.getVisibility() == VISIBLE) ? (R.id.tv_modify) : (R.id.tv_details));
        //加载主图
        if (!TextUtils.isEmpty(itemComponent.getPic())) {
            String imageUrl = getShopItemImageUrl(itemComponent.getPic());
            ivToutu.setImageResource(R.drawable.new_shop_cart_img_default);
            mImageLoaderManager.displayImage(imageUrl, ivToutu, mImageOptions);
        }
        JSONObject jsonObjectFields = itemComponent.getFields();
        newGoodsItemInfoView.setLabelTagTvTb(null);
        if (jsonObjectFields.containsKey("bizIcon")) {
            JSONObject jsonObject = jsonObjectFields.getJSONObject("bizIcon");
//            if (jsonObject != null && jsonObject.containsKey("S")) {
//                JSONArray jsonArray = jsonObject.getJSONArray("S");
//                AppDebug.e("NewShopCart jsonArray", jsonArray.toString());
//            }
            if(jsonObject != null && jsonObject.containsKey("TV")){
                JSONArray jsonArray = jsonObject.getJSONArray("TV");
                for (int i = 0;i<jsonArray.size();i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    if(jsonObject1!=null&&jsonObject1.containsKey("iconIDEnum")
                            &&jsonObject1.getString("iconIDEnum").equals(TVTAO_FANLI_ICON)){
                        newGoodsItemInfoView.setLabelTagTvTb(jsonObject1);
                    }else {
                        newGoodsItemInfoView.setLabelTagTvTb(null);
                    }
                }

            }else {
                newGoodsItemInfoView.setLabelTagTvTb(null);

            }
        }



//        头图上的标签
        HashMap<String, List<Icon>> iconComponent = itemComponent.getBizIcon();

//        newGoodsItemInfoView.setLabelTagTvTb(null);
//        newGoodsItemInfoView.setLabelSubsidy(null);
//        newGoodsItemInfoView.setLabelJhs(null);
//        newGoodsItemInfoView.setTvTax(null);
//        newGoodsItemInfoView.setTvDepreciate(null);
//        List<Icon> iconTVRebate = iconComponent.get("TV");
//        if(iconTVRebate!=null&&iconTVRebate.size()>0){
//            //电视淘宝返利
//            for (Icon icon :iconTVRebate){
//                if(icon!=null&&icon.getIconIDEnum().equals(TVTAO_FANLI_ICON)){
//                    newGoodsItemInfoView.setLabelTagTvTb(icon);
//                    break;
//                }else {
//                    newGoodsItemInfoView.setLabelTagTvTb(null);
//                }
//            }
//        }else {
//            newGoodsItemInfoView.setLabelTagTvTb(null);
//        }


        if (iconComponent != null && iconComponent.size() > 0) {
            List<Icon> iconItem = iconComponent.get("S");
            AppDebug.e("NewShopCart iconItem", iconItem.toString());
            if (iconItem != null && iconItem.size() > 0) {
                for (int i = 0; i < iconItem.size(); i++) {
                    Icon icon = iconItem.get(i);
                    //电视淘宝返利
//                    newGoodsItemInfoView.setLabelTagTvTb(icon);
                    if (icon != null) {
                        if (i == 0) {
                            if (!TextUtils.isEmpty(icon.getPic())) {
                                ivTag1.setVisibility(VISIBLE);
                                mImageLoaderManager.displayImage(icon.getPic(), ivTag1, mImageOptions);
                            } else {
                                ivTag1.setVisibility(GONE);
                            }
                            ivTag2.setVisibility(GONE);
                            ivTag3.setVisibility(GONE);
                        } else if (i == 1) {
                            if (!TextUtils.isEmpty(icon.getPic())) {
                                ivTag2.setVisibility(VISIBLE);
                                mImageLoaderManager.displayImage(icon.getPic(), ivTag2, mImageOptions);
                            } else {
                                ivTag2.setVisibility(GONE);
                            }
                            ivTag3.setVisibility(GONE);
                        } else if (i == 2) {
                            if (!TextUtils.isEmpty(icon.getPic())) {
                                ivTag3.setVisibility(VISIBLE);
                                mImageLoaderManager.displayImage(icon.getPic(), ivTag3, mImageOptions);
                            } else {
                                ivTag3.setVisibility(GONE);
                            }
                        }

                    }
                }
            }
        } else {
//            newGoodsItemInfoView.setLabelTagTvTb(null);
            ivTag1.setVisibility(GONE);
            ivTag2.setVisibility(GONE);
            ivTag3.setVisibility(GONE);
        }


        List<ItemLogo> itemLogos = itemComponent.getLogos();
        //是否显示降价信息
        boolean hasShowDepreciate = false;
        //是否显示限购信息
        boolean hasShowByLimit = false;
        //是否显示预售信息
        boolean hasShowPreSell = false;
        //是否显示抢先订信息
        boolean hasShowQXD = false;
        newGoodsItemInfoView.setTvTax(null);
        newGoodsItemInfoView.setTvDepreciate(null);
        if (itemLogos != null && itemLogos.size() > 0) {
            for (int i = 0; i < itemLogos.size(); i++) {
                ItemLogo itemLogo = itemLogos.get(i);
                //降价提醒>限购提示>库存紧张
                if (itemLogo != null && itemLogo.fields != null && itemLogo.fields.type != null) {
                    String type = itemLogo.fields.type;
                    AppDebug.e("itemLogo.fields.type = ", type);
                    if (type.equals(SKU_EXT) && !TextUtils.isEmpty(itemLogo.fields.title)) {
                        newGoodsItemInfoView.setTvTax(itemLogo.fields.title);
                    } else if (type.equals(PRICE_DOWN)) {
                        newGoodsItemInfoView.setTvDepreciate(itemLogo.fields.title);
                        hasShowDepreciate = true;
                    } else if (type.equals(BUY_LIMIT) && !hasShowDepreciate) {
                        newGoodsItemInfoView.setTvDepreciate(itemLogo.fields.title);
                        hasShowByLimit = true;
                    } else if (type.equals(INVENTORY_TENSION) && !hasShowDepreciate && !hasShowByLimit) {
                        newGoodsItemInfoView.setTvDepreciate(itemLogo.fields.title);
                    } else if (type.equals(PRE_SELL)) {
                        newGoodsItemInfoView.setLabelJhs(itemLogo);
                        hasShowPreSell = true;
                    } else if (type.equals(YOU_XIAN_GOUY) && !hasShowPreSell) {
                        newGoodsItemInfoView.setLabelJhs(itemLogo);
                        hasShowQXD = true;
                    } else if (type.equals(JHS) && !hasShowQXD && !hasShowQXD) {
                        newGoodsItemInfoView.setLabelJhs(itemLogo);
                    } else if (type.equals(MJFTAG)) {
                        try {
                        newGoodsItemInfoView.setBuyRebateVisible(itemLogo);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            newGoodsItemInfoView.setTvTax(null);
            newGoodsItemInfoView.setTvDepreciate(null);
            newGoodsItemInfoView.setLabelInternationalCoupon(null);
            newGoodsItemInfoView.setLabelSubsidy(null);
            newGoodsItemInfoView.setLabelJhs(null);
            newGoodsItemInfoView.setBuyRebateVisible(null);
        }

        //购物津贴，营销券
        Component component = itemComponent.getParent();
        if (component != null) {
            AppDebug.e("TAG component = ", component.toString());
            if (component instanceof GroupComponent) {
                GroupPromotion groupPromotion = ((GroupComponent) component).getGroupPromotion();
                if (groupPromotion != null && (groupPromotion.getTitle() != null || groupPromotion.getPic() != null)) {
                    AppDebug.e("GroupPromotion", groupPromotion.toString());
                    newGoodsItemInfoView.setLabelSubsidy(groupPromotion);
                } else {
                    newGoodsItemInfoView.setLabelSubsidy(null);
                }
            }
        }

        newGoodsItemInfoView.maxVisibleFour();
    }

    /**
     * 将sum图片替换成指定的分辨率图片
     *
     * @param picUrl
     * @return
     */
    private String getShopItemImageUrl(String picUrl) {
        int lastStringCount = 10;
        if (picUrl.length() > lastStringCount) {
            String beforeString = picUrl.substring(0, (picUrl.length() - lastStringCount));
            String afterString = picUrl.substring((picUrl.length() - lastStringCount), picUrl.length());
            afterString = afterString.replace("_sum.", "_" + mShopItemCardWidth + "x" + mShopItemCardWidth + ".");
            return beforeString + afterString;
        }
        return picUrl;
    }

    public void setOnShopItemClickListener(OnShopItemClickListener listener) {
        onShopItemClickListener = listener;
    }


    public void setClick() {
        tvDetails.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShopItemClickListener != null) {
                    onShopItemClickListener.onShopItemClick(NewShopCartListAdapter.ShopItemAction.DETAIL);
                }

            }
        });
        tvModify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShopItemClickListener != null) {
                    onShopItemClickListener.onShopItemClick(NewShopCartListAdapter.ShopItemAction.EDIT);
                }
            }
        });
        tvDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onShopItemClickListener != null) {
                    onShopItemClickListener.onShopItemClick(NewShopCartListAdapter.ShopItemAction.DELETE);
                }

            }
        });

        goodsSelectView.setOperateClickListener(new NewShopCartGoodsSelectView.OperateClickListener() {
            @Override
            public void onItemClick(NewShopCartGoodsSelectView view) {
                if (onShopItemClickListener != null) {
                    onShopItemClickListener.onShopItemCheck(shopCartGoodsBean);
                }
            }
        });
    }

    public void updateCheck(boolean isCheck) {
        goodsSelectView.updateCheck(isCheck);
    }

    /**
     * 商品的操作点击事件的监听方法
     */
    public interface OnShopItemClickListener {

        public void onShopItemClick(NewShopCartListAdapter.ShopItemAction action);

        public void onShopItemCheck(ShopCartGoodsBean bean);
    }


}

