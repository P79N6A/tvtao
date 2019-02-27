package com.yunos.tvtaobao.detailbundle.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.TopicsEntity;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.bean.NewDetailPanelData;
import com.yunos.tvtaobao.detailbundle.flash.DensityUtils;
import com.yunos.tvtaobao.detailbundle.type.DetailModleType;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by dingbin on 2017/8/23.
 */


public class NewDetailPanelView implements ViewTreeObserver.OnGlobalLayoutListener, SnapshotUtil.OnFronstedGlassSreenDoneListener {

    private final String TAG = "DetailPanelView";
    private WeakReference<NewDetailActivity> mNewDetailActivityReference;
    //店铺名
    private TextView shopName;
    //商品名
    private TextView goodName;
    //聚划算标签
    private TextView newDetailJhsTag;
    //打标的ll,里面有3个iv
//    private LinearLayout double11_tag;
//
    private ImageView iv_tag1;

    //618运营标签
    private LinearLayout ll618Tag;

    private ImageView iv618Tag1;
    private TextView tv618Tag;

    // 模板类型
    private DetailModleType mModleType;
    //双十一运营panel标签
    private ImageView marketingIconPanel;

    //    private ImageView iv_tag2;
//
//    private ImageView iv_tag3;
    //商品现价Title
    private TextView nowPriceTitle;
    //商品现价
    private TextView nowPrice;
    //商品现价描述
    private TextView nowPriceDesc;


    private TextView presellPrice;

    //商品原价LinearLayout
    private LinearLayout oldPriceLayout;
    //商品原价title
    private TextView oldPriceTitle;
    //商品原价Title
    private TextView oldPrice;

    //销售量
    private TextView soldNum;
    //销售量
    private TextView superWeigh;
    //运费
    private TextView postage;
    //进口税
    private TextView tax;
    //发货
    private TextView deliverGoods;
    //优惠券的ll
    private RelativeLayout ll_coupon;
    //优惠券的图标
    private TextView iv_coupon;
    //优惠券的名字
    private TextView tv_coupon;
    //积分ll
    private RelativeLayout ll_jifen;
    //积分图标
    private TextView mPointRate;
    //积分名称
    private TextView tv_jifen;

    //飞猪里程ll
    private RelativeLayout llFeizhuMileage;
    //飞猪里程图标
    private TextView ivFeizhuMileage;
    //飞猪里程名称
    private TextView tvFeizhuMileage;


    //服务的包裹rl,如果获取不到服务就置为gone
    private RelativeLayout rl_server;
    private ImageView ivServiceLine;
    //第一个服务
    private LinearLayout ll_server1;
    private TextView tv_server1;
    //第一个服务
    private LinearLayout ll_server2;
    private TextView tv_server2;

    //飞猪商品出签率
    private TextView tvRightDesc;

    // 下载管理器
    private ImageLoaderManager mImageLoaderManager;

    private SnapshotUtil.OnFronstedGlassSreenDoneListener screenShotListener;

    private ImageView ivBackgroundBlur;

    private String marketingIconPanelAddress;
    private boolean marketingIconPanelIsShowAll = false;

    //购物津贴布局
    public RelativeLayout llCouponJintie;
    private ImageView ivCouponIcon;
    private TextView tvCoupnText;

    private boolean isShowAllPrice = false;

    private ProductTagBo productTagBo;
    private NewDetailPanelData detailPanelData;

    /**
     * 显示电视淘宝积分
     */
    private double pointRate = 1.0;
    private String pointPrice;

    public NewDetailPanelView(WeakReference<NewDetailActivity> weakReference) {
        mNewDetailActivityReference = weakReference;
        onInitPanelView();

    }

    public void setImageNull() {
        iv_tag1 = null;
        ivBackgroundBlur = null;
        marketingIconPanel = null;
        ivCouponIcon = null;
        screenShotListener = null;
    }

    private void onInitPanelView() {
        if (mNewDetailActivityReference != null && mNewDetailActivityReference.get() != null) {
            NewDetailActivity newDetailActivity = mNewDetailActivityReference.get();

            shopName = (TextView) newDetailActivity.findViewById(R.id.new_detail_shop_name);
            goodName = (TextView) newDetailActivity.findViewById(R.id.new_detail_good_name);
            newDetailJhsTag = (TextView) newDetailActivity.findViewById(R.id.new_detail_jhs_tag);
//            double11_tag = (LinearLayout) newDetailActivity.findViewById(R.id.new_detail_double11_tag);
            iv_tag1 = (ImageView) newDetailActivity.findViewById(R.id.new_detail_11tag_iv1);
            ll618Tag = (LinearLayout) newDetailActivity.findViewById(R.id.new_detail_618_tag);
            iv618Tag1 = (ImageView) newDetailActivity.findViewById(R.id.new_detail_618_iv1);
            tv618Tag = (TextView) newDetailActivity.findViewById(R.id.new_detail_618_tv1);
            marketingIconPanel = (ImageView) newDetailActivity.findViewById(R.id.iv_new_detail_marketing_icon_panel);
            nowPriceTitle = (TextView) newDetailActivity.findViewById(R.id.new_detail_now_price_title);
            nowPrice = (TextView) newDetailActivity.findViewById(R.id.new_detail_now_price);
            nowPriceDesc = (TextView) newDetailActivity.findViewById(R.id.new_detail_now_price_desc);
            oldPriceLayout = (LinearLayout) newDetailActivity.findViewById(R.id.ll_new_detail_old_price);
            oldPriceTitle = (TextView) newDetailActivity.findViewById(R.id.new_detail_old_price_title);
            oldPrice = (TextView) newDetailActivity.findViewById(R.id.new_detail_old_price);
            presellPrice = (TextView) newDetailActivity.findViewById(R.id.new_detail_presell_price);
            soldNum = (TextView) newDetailActivity.findViewById(R.id.new_detail_soldnum);
            superWeigh = (TextView) newDetailActivity.findViewById(R.id.new_detail_super_weigh);
            postage = (TextView) newDetailActivity.findViewById(R.id.new_detail_postage);
            tax = (TextView) newDetailActivity.findViewById(R.id.new_detail_tax);
            deliverGoods = (TextView) newDetailActivity.findViewById(R.id.new_detail_deliver_goods);

            tvRightDesc = (TextView) newDetailActivity.findViewById(R.id.new_detail_right_desc);
            ll_coupon = (RelativeLayout) newDetailActivity.findViewById(R.id.new_detail_coupon);
            //津贴
            llCouponJintie = (RelativeLayout) newDetailActivity.findViewById(R.id.ll_coupon);
            iv_coupon = (TextView) newDetailActivity.findViewById(R.id.new_detail_coupon_iv);
            tv_coupon = (TextView) newDetailActivity.findViewById(R.id.new_detail_coupon_tv);

            ll_jifen = (RelativeLayout) newDetailActivity.findViewById(R.id.new_detail_jifen);
            mPointRate = (TextView) newDetailActivity.findViewById(R.id.new_detail_jifen_iv);
            tv_jifen = (TextView) newDetailActivity.findViewById(R.id.new_detail_jifen_tv);

            llFeizhuMileage = (RelativeLayout) newDetailActivity.findViewById(R.id.new_detail_feizhu_mileage);
            ivFeizhuMileage = (TextView) newDetailActivity.findViewById(R.id.new_detail_mileage_iv);
            tvFeizhuMileage = (TextView) newDetailActivity.findViewById(R.id.new_detail_mileage_tv);

            ivCouponIcon = (ImageView) newDetailActivity.findViewById(R.id.iv_coupon_icon);

            if(ivCouponIcon!=null&&ivCouponIcon.getViewTreeObserver()!=null){

                ivCouponIcon.getViewTreeObserver().addOnGlobalLayoutListener(this);
            }



            tvCoupnText = (TextView) newDetailActivity.findViewById(R.id.tv_coupon_text);

            rl_server = (RelativeLayout) newDetailActivity.findViewById(R.id.layout_attrs);
            ivServiceLine = (ImageView) newDetailActivity.findViewById(R.id.iv_service_line);


            ll_server1 = (LinearLayout) newDetailActivity.findViewById(R.id.server1);
            tv_server1 = (TextView) newDetailActivity.findViewById(R.id.server1_tv);

            ll_server2 = (LinearLayout) newDetailActivity.findViewById(R.id.server2);
            tv_server2 = (TextView) newDetailActivity.findViewById(R.id.server2_tv);
            mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(newDetailActivity);

            ivBackgroundBlur = (ImageView) newDetailActivity.findViewById(R.id.iv_background_blur);
            screenShotListener = this;
        }
    }


    public void setbg(String itemImageUrl) {
        mImageLoaderManager.loadImage(itemImageUrl, new ImageLoadingListener() {

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//                        if (childView != null) {
//                            childView.setBackgroundDrawable(new BitmapDrawable(arg2));
//                            if ("1".equals(childView.getTag())) {
                SnapshotUtil.getFronstedBitmap(arg2, 5, screenShotListener);
//                            }
//                        }
            }

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }

        });
    }


    public void setGoodsInfo(NewDetailPanelData detailPanelData) {
        if (detailPanelData == null) {
            return;
        }
        this.detailPanelData = detailPanelData;

        setbg(detailPanelData.toutuUrl);
        //店铺名
        if (!TextUtils.isEmpty(detailPanelData.title) && shopName != null) {
            shopName.setText(detailPanelData.title);
        }
        //商品名
        if (!TextUtils.isEmpty(detailPanelData.goodTitle) && goodName != null) {
            goodName.setText(detailPanelData.goodTitle);
        }
        //聚划算标签和淘抢购标签
        if (!TextUtils.isEmpty(detailPanelData.slogo) && newDetailJhsTag != null) {
            newDetailJhsTag.setVisibility(View.VISIBLE);
            newDetailJhsTag.setText(detailPanelData.slogo);
            if (detailPanelData.detailModleType != null) {
                if (detailPanelData.detailModleType == DetailModleType.JUHUASUAN) {
                    newDetailJhsTag.setBackgroundResource(R.drawable.shop_tag_jhs_icon);
                } else if (detailPanelData.detailModleType == DetailModleType.QIANGOU) {
                    newDetailJhsTag.setBackgroundResource(R.drawable.shop_tag_taoqianggou_icon);
                }
            }
        } else {
            newDetailJhsTag.setVisibility(View.GONE);
        }

        //双十一运营标签图片地址
        if (!TextUtils.isEmpty(detailPanelData.marketingIconPanel)) {
            marketingIconPanelAddress = detailPanelData.marketingIconPanel;
            marketingIconPanelIsShowAll = detailPanelData.marketingIconPanelShowAll;

        }


        //现价title

        if (!TextUtils.isEmpty(detailPanelData.nowPriceTitle) && nowPriceTitle != null) {
            nowPriceTitle.setVisibility(View.VISIBLE);
            nowPriceTitle.setText(detailPanelData.nowPriceTitle + ":");
        } else {
            nowPriceTitle.setVisibility(View.GONE);
        }
        if (detailPanelData.detailModleType == DetailModleType.JUHUASUAN) {
            if (detailPanelData.status == 0) {
                nowPrice.setTextColor(Color.parseColor("#00cc33"));
                nowPriceTitle.setTextColor(Color.parseColor("#00cc33"));
                nowPriceDesc.setTextColor(Color.parseColor("#00cc33"));

            } else {
                nowPrice.setTextColor(Color.parseColor("#f61d4b"));
                nowPriceTitle.setTextColor(Color.parseColor("#00cc33"));
                nowPriceDesc.setTextColor(Color.parseColor("#00cc33"));

            }
        }
        //depositPriceDesc,定金膨胀
        if (!TextUtils.isEmpty(detailPanelData.depositPriceDesc) && nowPriceDesc != null) {
            nowPriceDesc.setVisibility(View.VISIBLE);
            nowPriceDesc.setText(detailPanelData.depositPriceDesc);
        } else {
            nowPriceDesc.setVisibility(View.GONE);
        }


        //现价
        if (!TextUtils.isEmpty(detailPanelData.nowPrice) && nowPrice != null) {
            nowPrice.setText("¥" + detailPanelData.nowPrice);
            if (!TextUtils.isEmpty(detailPanelData.nowPriceTitle)) {
                if (detailPanelData.nowPrice.length() <= 9) {
                    nowPrice.setTextSize(44.3f);
                } else if (detailPanelData.nowPrice.length() <= 10) {
                    nowPrice.setTextSize(40.5f);
                } else if (detailPanelData.nowPrice.length() <= 11) {
                    nowPrice.setTextSize(35.5f);
                } else if (detailPanelData.nowPrice.length() <= 13) {
                    nowPrice.setTextSize(30.4f);
                } else if (detailPanelData.nowPrice.length() <= 15) {
                    nowPrice.setTextSize(27.9f);
                } else if (detailPanelData.nowPrice.length() <= 17) {
                    nowPrice.setTextSize(24.1f);
                } else if (detailPanelData.nowPrice.length() <= 21) {
                    nowPrice.setTextSize(21.5f);
                } else {
                    nowPrice.setTextSize(20.3f);
                }
            } else {
                if (detailPanelData.nowPrice.length() <= 10) {
                    nowPrice.setTextSize(48.1f);
                } else if (detailPanelData.nowPrice.length() <= 14) {
                    nowPrice.setTextSize(40.5f);
                } else if (detailPanelData.nowPrice.length() <= 16) {
                    nowPrice.setTextSize(34.2f);
                } else if (detailPanelData.nowPrice.length() <= 18) {
                    nowPrice.setTextSize(30.4f);
                } else {
                    nowPrice.setTextSize(26.6f);
                }
            }
        }

        //预售价
        if (!TextUtils.isEmpty(detailPanelData.presellPrice) && presellPrice != null) {
            presellPrice.setVisibility(View.VISIBLE);
            presellPrice.setText(detailPanelData.presellPriceTitle + "：¥" + detailPanelData.presellPrice);
        } else {
            presellPrice.setVisibility(View.GONE);
        }
        //原价
        if (!TextUtils.isEmpty(detailPanelData.oldPrice) && oldPrice != null) {
            oldPriceLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(detailPanelData.oldPriceTitle) && oldPriceTitle != null) {
                oldPriceTitle.setText(detailPanelData.oldPriceTitle + "：¥");
            } else {
                oldPriceTitle.setText("¥");
            }
            if (!TextUtils.isEmpty(detailPanelData.oldPriceLineThrough) && detailPanelData.oldPriceLineThrough.equals("false")) {
                oldPrice.setText(detailPanelData.oldPrice);
            } else {
                SpannableString spannableString = new SpannableString(detailPanelData.oldPrice);
                StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
                spannableString.setSpan(strikethroughSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                oldPrice.setText(spannableString);
            }
        } else {
            oldPriceLayout.setVisibility(View.GONE);
        }

        //价格显示
        if (detailPanelData.detailModleType == DetailModleType.PRESALE || isShowAllPrice) {

        } else {
            if (!TextUtils.isEmpty(detailPanelData.nowPrice)
                    && !TextUtils.isEmpty(detailPanelData.presellPrice)
                    && !TextUtils.isEmpty(detailPanelData.oldPrice)) {
                presellPrice.setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(detailPanelData.presellPrice)) {
                    presellPrice.setVisibility(View.VISIBLE);
                } else {
                    presellPrice.setVisibility(View.GONE);

                }
            }
        }

        //运费
        if (!TextUtils.isEmpty(detailPanelData.postage) && postage != null) {
            postage.setVisibility(View.VISIBLE);
            postage.setText(detailPanelData.postage);
        } else {
            postage.setVisibility(View.GONE);
        }

        if (detailPanelData.orderedItemAmount != null) {//预售商品已预订
            soldNum.setText("已预订:" + detailPanelData.orderedItemAmount + "件");
        } else if (!TextUtils.isEmpty(detailPanelData.soldNum) && soldNum != null) { //销售量
            soldNum.setText(detailPanelData.soldNum);
        }
        //进口税
        if (!TextUtils.isEmpty(detailPanelData.tax) && tax != null) {
            tax.setVisibility(View.VISIBLE);
            tax.setText("进口税:" + detailPanelData.tax);
        } else {
            tax.setVisibility(View.GONE);
        }

        //重量
        if (!TextUtils.isEmpty(detailPanelData.weight) && superWeigh != null) {
            superWeigh.setVisibility(View.VISIBLE);
            superWeigh.setText("重量:" + detailPanelData.weight);
        } else {
            superWeigh.setVisibility(View.GONE);
        }

        //发货
        if (!TextUtils.isEmpty(detailPanelData.deliverGoods) && deliverGoods != null) {
            deliverGoods.setVisibility(View.VISIBLE);
            deliverGoods.setText("发货:" + detailPanelData.deliverGoods);
        } else {
            deliverGoods.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(detailPanelData.rightDesc) && tvRightDesc != null) {
            tvRightDesc.setVisibility(View.VISIBLE);
            tvRightDesc.setText(detailPanelData.rightDesc);
        } else {
            tvRightDesc.setVisibility(View.GONE);
        }

        //促销
        //优惠券
        if (!TextUtils.isEmpty(detailPanelData.salesPromotion) && tv_coupon != null
                && !detailPanelData.salesPromotionIconText.equals("积分")) {  //如果手淘也送积分，就不展示。
            ll_coupon.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(detailPanelData.salesPromotionIconText) && iv_coupon != null) {
                tv_coupon.setText(detailPanelData.salesPromotion);
                if (iv_coupon != null) {
                    if (detailPanelData.salesPromotionIconText != null) {
                        iv_coupon.setText(detailPanelData.salesPromotionIconText);
                    } else {
                        iv_coupon.setText("促销");
                    }
                }
            }
        } else {
            ll_coupon.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(detailPanelData.presellPrice)) {
//            showPoint(detailPanelData.presellPrice, 0);//电视淘宝积分=price
//        } else {
        showPoint(detailPanelData.nowPrice, 0);//电视淘宝积分=price
//        }

        if (detailPanelData.services.size() != 0 && rl_server != null) {
            AppDebug.e(TAG, TAG + "--设置服务信息----" + detailPanelData.services.size());
            rl_server.setVisibility(View.VISIBLE);
            ivServiceLine.setVisibility(View.VISIBLE);
            if (detailPanelData.services.size() == 1) {
                ll_server1.setVisibility(View.VISIBLE);
                ll_server2.setVisibility(View.GONE);
                tv_server1.setText(detailPanelData.services.get(0));
            } else if (detailPanelData.services.size() >= 2) {
                ll_server1.setVisibility(View.VISIBLE);
                tv_server1.setText(detailPanelData.services.get(0));
                ll_server2.setVisibility(View.VISIBLE);
                tv_server2.setText(detailPanelData.services.get(1));
            }
        } else {
            rl_server.setVisibility(View.GONE);
            ivServiceLine.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(detailPanelData.mileageTitle)) {
            llFeizhuMileage.setVisibility(View.VISIBLE);
            tvFeizhuMileage.setText(detailPanelData.mileageTitle);
            if (!TextUtils.isEmpty(detailPanelData.flayerTitle)) {
                ivFeizhuMileage.setText(detailPanelData.flayerTitle);
            } else {
                ivFeizhuMileage.setText("飞猪里程");

            }

        } else {
            llFeizhuMileage.setVisibility(View.GONE);

        }


        if (!TextUtils.isEmpty(detailPanelData.couponText) && tvCoupnText != null) {
            llCouponJintie.setVisibility(View.VISIBLE);
            tvCoupnText.setText(detailPanelData.couponText);
            if (!TextUtils.isEmpty(detailPanelData.couponIcon) && ivCouponIcon != null) {
                mImageLoaderManager.displayImage(detailPanelData.couponIcon, ivCouponIcon);
            }
        } else {
            llCouponJintie.setVisibility(View.GONE);

        }


    }


    public void setModleType(DetailModleType detailModleType) {
        try {
            if (detailModleType != null) {
                mModleType = detailModleType;
                if (marketingIconPanelIsShowAll) {
                    if (!TextUtils.isEmpty(marketingIconPanelAddress) && marketingIconPanel != null && mImageLoaderManager != null) {
                        mImageLoaderManager.displayImage(marketingIconPanelAddress, marketingIconPanel);
                        marketingIconPanel.setVisibility(View.VISIBLE);
                    } else {
                        marketingIconPanel.setVisibility(View.GONE);
                    }
                } else {

                    if (mModleType == DetailModleType.PRESALE) {
                        if (!TextUtils.isEmpty(marketingIconPanelAddress) && marketingIconPanel != null && mImageLoaderManager != null) {
                            mImageLoaderManager.displayImage(marketingIconPanelAddress, marketingIconPanel);
                            marketingIconPanel.setVisibility(View.VISIBLE);
                        } else {
                            marketingIconPanel.setVisibility(View.GONE);
                        }
                    } else {
                        marketingIconPanel.setVisibility(View.GONE);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTagInfo(ProductTagBo productTagBo, boolean isPre) {
        if (productTagBo != null) {
            this.productTagBo = productTagBo;
            try {
                AppDebug.e("打标信息", productTagBo.getOutPreferentialId() + "---" + productTagBo.getIcon());
            } catch (Exception e) {
                e.printStackTrace();
            }//            if (!TextUtils.isEmpty(productTagBo.getOutPreferentialId()) && !TextUtils.isEmpty(productTagBo.getIcon())) {
//                switch (productTagBo.getPosition()) {
//
//                    case 0:
//                    case 1:
//                        if (iv_tag1 != null) {
//                            double11_tag.setVisibility(View.VISIBLE);
//                            iv_tag1.setVisibility(View.VISIBLE);
//                            mImageLoaderManager.displayImage(productTagBo.getIcon(), iv_tag1);
//                        }
//                        break;
////                    case 2:
////                        if (mDetailSubCartTag != null) {
////                            mDetailSubCartTag.setVisibility(View.VISIBLE);
////                            mImageLoaderManager.displayImage(productTagBo.getIcon(), mDetailSubCartTag);
////                        }
////                        break;
//                }
//
//            }

            if (!TextUtils.isEmpty(productTagBo.getPointSchemeId()) && productTagBo.getPointRate() > 1.0) {
                AppDebug.e("打标----", productTagBo.getPosition() + "---" + productTagBo.getPointRate());
                showPoint(detailPanelData.nowPrice, productTagBo.getPointRate());
            } else {
                showPoint(detailPanelData.nowPrice, 0);//电视淘宝积分=price
            }

            if (!TextUtils.isEmpty(productTagBo.getCouponType())
                    && productTagBo.getCouponType().equals("1")) {
                if (!TextUtils.isEmpty(productTagBo.getCoupon())) {
                    int rebateMoney = Integer.parseInt(productTagBo.getCoupon());
                    if (rebateMoney > 0) {
                        ll618Tag.setVisibility(View.VISIBLE);
                        tv618Tag.setVisibility(View.VISIBLE);
                        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                        float num = (float) rebateMoney / 100;
                        String result = df.format(num);//返回的是String类型
                        if (!TextUtils.isEmpty(productTagBo.getCouponMessage())) {
                            tv618Tag.setText(productTagBo.getCouponMessage() + " ¥ " + result);
                        } else {
                            tv618Tag.setText("最高再返" + " ¥ " + result);
                        }

                        if (!TextUtils.isEmpty(productTagBo.getPicUrl())) {
                            iv618Tag1.setVisibility(View.VISIBLE);
                            mImageLoaderManager.displayImage(productTagBo.getPicUrl(), iv618Tag1);
                        } else {
                            iv618Tag1.setVisibility(View.GONE);
                        }
                    } else {
                        //预售商品显示返利信息
                        if (isPre) {
                            int rebateMoneyPre = Integer.parseInt(productTagBo.getCoupon());
                            if (rebateMoneyPre > 0) {
                                ll618Tag.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(productTagBo.getCouponMessage())) {
                                    tv618Tag.setVisibility(View.VISIBLE);
                                    tv618Tag.setText(productTagBo.getCouponMessage());
                                } else {
                                    tv618Tag.setVisibility(View.GONE);
                                }
                                if (!TextUtils.isEmpty(productTagBo.getPicUrl())) {
                                    iv618Tag1.setVisibility(View.VISIBLE);
                                    mImageLoaderManager.displayImage(productTagBo.getPicUrl(), iv618Tag1);
                                } else {
                                    iv618Tag1.setVisibility(View.GONE);

                                }
                            } else {
                                ll618Tag.setVisibility(View.GONE);
                                tv618Tag.setText("");
                            }
                        } else {
                            ll618Tag.setVisibility(View.GONE);
                            tv618Tag.setText("");
                        }
                    }
                } else {
                    //预售商品显示返利信息
                    if (isPre) {
                        if (!TextUtils.isEmpty(productTagBo.getCouponMessage())) {
                            tv618Tag.setVisibility(View.VISIBLE);
                            ll618Tag.setVisibility(View.VISIBLE);
                            tv618Tag.setText(productTagBo.getCouponMessage());
                        } else {
                            tv618Tag.setVisibility(View.GONE);
                        }
                        if (!TextUtils.isEmpty(productTagBo.getPicUrl())) {
                            iv618Tag1.setVisibility(View.VISIBLE);
                            ll618Tag.setVisibility(View.VISIBLE);
                            mImageLoaderManager.displayImage(productTagBo.getPicUrl(), iv618Tag1);

                        } else {
                            iv618Tag1.setVisibility(View.GONE);
                        }
                    } else {
                        ll618Tag.setVisibility(View.GONE);
                        tv618Tag.setText("");
                    }
                }
            } else {
                ll618Tag.setVisibility(View.GONE);
                tv618Tag.setText("");
            }
        }

    }

    public void setIsShowAllPrice(Boolean isShowAllPrice) {
        this.isShowAllPrice = isShowAllPrice;

    }

    //积分显示
    public void setTaobaoPointVisibility(int visibility) {
        if (productTagBo != null && productTagBo.getCouponType() != null && productTagBo.getCouponType().equals("0")) {
            if (ll_jifen != null && mNewDetailActivityReference.get().isShowPoint) {
                ll_jifen.setVisibility(visibility);
            }
        }
    }

    private void showPoint(String price, double rate) {
        AppDebug.e(TAG, "显示电视淘宝积分");
        if (rate > pointRate)
            pointRate = rate;

        if (price != null)
            pointPrice = price;
        //电视淘宝积分
        if (!TextUtils.isEmpty(pointPrice) && (tv_jifen != null) && !TextUtils.isEmpty(price)) {
            String activityPrice = "";
            if (price.contains("-")) {
                String[] prices = pointPrice.split("-");
                activityPrice = StringUtil.subZeroAndDot(prices[0]);
            } else {
                activityPrice = price;
            }

            try {
                double point = (Float.parseFloat(activityPrice) / 2) * pointRate;
                AppDebug.d(TAG, "-----" + point);
                NewDetailActivity detailActivity = mNewDetailActivityReference.get();
                if (point >= 1) {
                    detailActivity.isShowPoint = true;
                    if (pointRate > 1.0) {
                        NumberFormat nf = new DecimalFormat("#.#");
                        mPointRate.setText("积分" + nf.format(pointRate) + "倍");
                        tv_jifen.setText("送电视淘宝积分" + (int) point);
                    } else {
                        mPointRate.setText("积分");
                        tv_jifen.setText("送电视淘宝积分" + (int) point);
                    }
                } else {
                    AppDebug.e(TAG, "积分<1");
                    detailActivity.isShowPoint = false;
                    setTaobaoPointVisibility(View.GONE);
                }
            } catch (Exception e) {
                AppDebug.e(TAG, "积分异常");
                NewDetailActivity detailActivity = mNewDetailActivityReference.get();
                detailActivity.isShowPoint = false;
                setTaobaoPointVisibility(View.GONE);
            }
        }
        String appkey = SharePreferences.getString("device_appkey", "");
        String brandName = SharePreferences.getString("device_brandname", "");
        Log.d(TAG, TAG + ".isVisbilePoint appkey : " + appkey + " ,brandName : " + brandName);

        if (appkey.equals("10004416") && brandName.equals("海尔")) {
            AppDebug.e(TAG, "海尔商品积分!isShowPoint");
            setTaobaoPointVisibility(View.GONE);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (ivCouponIcon.getDrawable() == null || ivCouponIcon.getHeight() == 0 || ivCouponIcon.getWidth() == 0 || ivCouponIcon.getContext() == null) {
            return;
        }

        int mm = (int) ivCouponIcon.getContext().getResources().getDimensionPixelOffset(R.dimen.dp_25);

        if (ivCouponIcon.getHeight() != mm) {
            float radio = ivCouponIcon.getWidth() * 1f / ivCouponIcon.getHeight();
            int target = (int) (mm * radio);
            ViewGroup.LayoutParams layoutParams = ivCouponIcon.getLayoutParams();
            layoutParams.height = mm;
            layoutParams.width = target;
            ivCouponIcon.setLayoutParams(layoutParams);
            ivCouponIcon.setVisibility(View.VISIBLE);
        } else {
            ivCouponIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFronstedGlassSreenDone(Bitmap bmp) {
        if (ivBackgroundBlur != null) {
            if ((bmp != null) && (!bmp.isRecycled())) {
                ivBackgroundBlur.setBackgroundDrawable(new BitmapDrawable(bmp));
            }
        }
    }
}
