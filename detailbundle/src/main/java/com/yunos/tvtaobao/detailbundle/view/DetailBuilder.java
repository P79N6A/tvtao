package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.FeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.Unit;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.resconfig.IResConfig;
import com.yunos.tvtaobao.detailbundle.resconfig.TaobaoResConfig;
import com.yunos.tvtaobao.detailbundle.resconfig.TmallResConfig;

public class DetailBuilder {

    // 评价TAB 的  pos 
    public static final int LAYOUT_INDEX_F1 = -1;
    public static final int LAYOUT_INDEX_0 = 0;
    public static final int LAYOUT_INDEX_1 = 1;
    public static final int LAYOUT_INDEX_2 = 2;
    public static final int LAYOUT_INDEX_3 = 3;
    public static final int LAYOUT_INDEX_4 = 4;

    private final String TAG = "DetailView";

    private Context mContext;

    //详情数据对象
    //private TBDetailResultVO mTBDetailResultVO;
    private TBDetailResultV6 tbDetailResultV6;

    // 资源配置[天猫，或者淘宝]
    private IResConfig resConfig;

    // 支持购物车
    private boolean mAddCart;

    // 扫描购买
    private boolean mScanQrCode;


    // 支持购买
    private boolean mBuySupport;

    // 是否有优惠券
    private boolean mHasCoupon;

    // 收藏数量
    private String mFavCount;

    public boolean isSuperMarket;

    public DetailBuilder(Context context) {
        mContext = context;
        mAddCart = true;
        mScanQrCode = true;
        mHasCoupon = true;
        mBuySupport = true;
    }

    private MockData getMockdata(TBDetailResultV6 tbDetailResultV6) {
        if (tbDetailResultV6 != null) {
            if (tbDetailResultV6.getMockData() != null) {
                String mockData = tbDetailResultV6.getMockData();
                MockData mockData1 = JSON.parseObject(mockData, MockData.class);
                return mockData1;
            }
        }
        return null;
    }

    /**
     * 检查TBDetailResultVO里面的信息
     *
     * @param tbDetailResultVO
     */
    public void onCheckResultVO(TBDetailResultV6 tbDetailResultVO, FeiZhuBean feiZhuBean) {
        AppDebug.i(TAG, "onInitConfig --> tbDetailResultVO = " + tbDetailResultVO);
        this.tbDetailResultV6 = tbDetailResultVO;
        if (tbDetailResultV6 != null) {

            TBDetailResultV6.SellerBean seller = tbDetailResultV6.getSeller();

            if (seller != null && !TextUtils.isEmpty(seller.getShopType())) {
                if (seller.getShopType().toUpperCase().equals("B")) {// tmall
                    AppDebug.e(TAG, "B类");
                    resConfig = new TmallResConfig(mContext);
                } else {
                    resConfig = new TaobaoResConfig(mContext);
                }
            } else {
                // 默认是淘宝商品
                AppDebug.e("默认淘宝商品", "店家为null，默认淘宝商品");
                resConfig = new TaobaoResConfig(mContext);
            }

            AppDebug.e(TAG, "onCheckResultVO --> resConfig =  " + resConfig.getClass());

            // 判断是否可以购买
            if (tbDetailResultVO.getApiStack() != null) {
                Unit unit = JSON.parseObject(tbDetailResultVO.getApiStack().get(0).getValue(), Unit.class);
                if (unit != null && unit.getTrade() != null) {
                    if (unit.getTrade().getCartEnable().equals("true")) mAddCart = true;
                    else mAddCart = false;
                    if (unit.getTrade().getBuyEnable().equals("true"))
                        mBuySupport = true;
                    else mBuySupport = false;
                    AppDebug.e("是否可加购物车可购买", "可以购买" + mAddCart + " " + mBuySupport);
                } else {
                    AppDebug.e("商品不能购买", "不能购买");
                    mAddCart = false;
                    mBuySupport = false;
                }
            } else {
                MockData mockdata = getMockdata(tbDetailResultVO);
                if (mockdata != null && mockdata.getTrade() != null) {
                    if (mockdata.getTrade().isCartEnable()) mAddCart = true;
                    else mAddCart = false;
                    if (mockdata.getTrade().isBuyEnable()) mBuySupport = true;
                    else mBuySupport = false;
                    AppDebug.e("飞猪是否可加购物车可购买", "可以购买" + mAddCart + " " + mBuySupport);

                } else {

                    AppDebug.e("飞猪商品不能购买", "不能购买");
                    mAddCart = false;
                    mBuySupport = false;
                }
            }
            if (tbDetailResultVO.getFeature() != null) {
                if (tbDetailResultVO.getFeature().getSecKill() != null) {
                    if (tbDetailResultVO.getFeature().getSecKill().equals("true")) {
                        //秒杀商品
                        if (tbDetailResultVO.getTrade().getCartEnable() != null && tbDetailResultVO.getTrade().getCartEnable().equals("true")) {
                            mAddCart = true;
                        } else {
                            mAddCart = false;
                        }

                        if (tbDetailResultVO.getTrade().getBuyEnable() != null && tbDetailResultVO.getTrade().getBuyEnable().equals("true")) {
                            mBuySupport = true;
                        } else {
                            mBuySupport = false;
                        }
                    }
                }
            }
            // 检查商品类型
//            String displayType[] = tbDetailResultV6.displayType;
//            if (displayType != null) {
//                int len = displayType.length;
//                for (int index = 0; index < len; index++) {
//                    String typeString = displayType[index];
//                    if (TextUtils.equals(typeString, "seckill")) {
//                        AppDebug.e("秒杀商品不能购买", "属于秒杀，不能购买");
//                        // 如果是秒杀商品，那么不支持购买
//                        mBuySupport = false;
//                        // 文案提示 <暂不支持购买>
//                        if (tbDetailResultV6.itemControl != null && tbDetailResultV6.itemControl.unitControl != null) {
//                            tbDetailResultV6.itemControl.unitControl.errorMessage = mContext
//                                    .getString(R.string.ytsdk_confirm_cannot_buy);
//                            tbDetailResultV6.itemControl.unitControl.buySupport = false;
//                        }
//                        continue;
//                    }
//
//                    if (TextUtils.equals(typeString, "supermarket")) {
//                        AppDebug.e("天猫超市商品", "天猫超市");
//
//                        isSuperMarket = true;
//                        continue;
//                    }
//                }
//            }

            Unit tbDetailResultV6Util = DetailV6Utils.getUnit(tbDetailResultV6);
            //天猫超市的商品是不能购买的，可以加购物车
            if (tbDetailResultV6 != null && tbDetailResultV6Util != null && tbDetailResultV6Util.getVertical() != null && tbDetailResultV6Util.getVertical().getSupermarket() != null) {
                isSuperMarket = true;
            }

            if (!mBuySupport) {
                AppDebug.e("binbinbin", "如果不支持购买");
                //resConfig.setCanBuy(false);
                //mScanQrCode = false;
                if (tbDetailResultV6 != null && tbDetailResultV6Util != null && tbDetailResultV6Util.getTrade() != null && tbDetailResultV6Util.getTrade().getHintBanner() != null) {
                    if (tbDetailResultV6Util.getTrade().getHintBanner().getText() != null)
                        resConfig.setGreenStatus(tbDetailResultV6Util.getTrade().getHintBanner().getText());
                } else {
                    resConfig.setGreenStatus(mContext.getString(R.string.ytsdk_confirm_cannot_buy));
                }
            } else {
                AppDebug.i("binbinbin", "支持购买");
                if (tbDetailResultV6 != null && tbDetailResultV6Util != null && tbDetailResultV6Util.getTrade() != null && tbDetailResultV6Util.getTrade().getBuyText() != null) {
                    resConfig.setGreenStatus(tbDetailResultV6Util.getTrade().getBuyText());
                } else {
                    resConfig.setGreenStatus(mContext.getString(R.string.ytsdk_option_desc_immediately));
                }
            }

//            if (tbDetailResultV6.itemControl != null && tbDetailResultV6.itemControl.unitControl != null) {
//                String src = tbDetailResultV6.itemControl.unitControl.errorMessage;
//                // 把“无线端”这个文案替换成TV端
//                AppDebug.i("binbinbin", "无线端替换成tv端");
//                tbDetailResultV6.itemControl.unitControl.errorMessage = DocumentUtil.replaceWireless(mContext, src);
//            }

            AppDebug.i("binbinbin", "检查是否有优惠券");
            if (tbDetailResultV6 != null && tbDetailResultV6Util != null && tbDetailResultV6Util.getFeature() != null) {
                // 检查是有优惠券
                if (tbDetailResultV6Util.getFeature().getHasCoupon() != null) {
                    if (tbDetailResultV6Util.getFeature().getHasCoupon().equals("true")) {
                        mHasCoupon = true;
                        AppDebug.i("binbinbin", "有券");
                    } else {
                        AppDebug.i("binbinbin", "没有券");
                        mHasCoupon = false;
                    }
                } else {
                    AppDebug.i("binbinbin", "没有券");
                    mHasCoupon = false;
                }
            } else {
                MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
                if (mockdata != null && mockdata.getFeature() != null && mockdata.getFeature().getHasCoupon() != null) {
                    if (mockdata.getFeature().getHasCoupon().equals("true")) {
                        mHasCoupon = true;
                        AppDebug.i("binbinbin", "有券");
                    } else {
                        AppDebug.i("binbinbin", "没有券");
                        mHasCoupon = false;
                    }
                } else {
                    AppDebug.i("binbinbin", "没有券");
                    if (feiZhuBean != null) {
                        if (feiZhuBean.isHasCoupon()) {
                            mHasCoupon = true;
                        } else {
                            mHasCoupon = false;
                        }
                    } else {
                        mHasCoupon = false;
                    }
                }
            }

            //收藏数量
            if (tbDetailResultV6.getItem() != null) {
                long favcount = Long.parseLong(tbDetailResultV6.getItem().getFavcount());
                AppDebug.e("收藏数量", "设置搜藏数量");
                setFavcount(favcount);
            }
        }
    }

    /**
     * 获取资源配置类
     *
     * @return
     */

    public IResConfig getResConfig() {
        return resConfig;
    }

    /**
     * 是否支持添加购物车
     *
     * @return
     */
    public boolean isSupportAddCart() {
        return mAddCart;
    }

    public void setSupportAddCart(boolean isSupport) {
        mAddCart = isSupport;
    }

    /**
     * 是否支持购买
     *
     * @return
     */
    public boolean isSupportBuy() {
        return mBuySupport;
    }

    /**
     * 是否支持扫码购买
     *
     * @return
     */
    public boolean isScanQrCode() {
        return mScanQrCode;
    }


    public void setCanScanQrcode(boolean canScan) {
        mScanQrCode = canScan;
    }

    /**
     * 是否有优惠券
     *
     * @return
     */
    public boolean isHasCoupon() {
        return mHasCoupon;
    }


    /**
     * 获取收藏数量
     */
    public String getFavcount() {
        return mFavCount;
    }

    /**
     * 设置收藏数
     *
     * @param count
     */
    public void setFavcount(long count) {
        if (count >= 10000) {// 大于10000显示1.3万
            float ffav = count / 10000f;
            ffav = (float) (Math.round(ffav * 10)) / 10;
            mFavCount = ffav + "万";
        } else {
            mFavCount = count + "";
        }
    }

    /**
     * 获得评价的类型种类数
     *
     * @return
     */
    public int getRateTypeCount() {
        int totalCount = 4;
        if (IResConfig.GoodsType.TMALL == resConfig.getGoodsType()) {
            totalCount = 2;
        } else {
            totalCount = 4;
        }
        return totalCount;
    }

    /**
     * 获取评价类型
     *
     * @return
     */
    public String getRateType(int mLayoutIndex) {
        String rateType = null;
        if (resConfig != null) {
            if (IResConfig.GoodsType.TMALL == resConfig.getGoodsType()) {
                if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_0) {
                    // 全部
                    rateType = null;
                } else if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_1) {
                    // 3 有图
                    rateType = "3";
                } else {
                    rateType = null;
                }
            } else {
                if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_0) {
                    // 1 好评
                    rateType = "1";
                } else if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_1) {
                    // 0 中评
                    rateType = "0";
                } else if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_2) {
                    // -1 差评
                    rateType = "-1";
                } else if (mLayoutIndex == DetailBuilder.LAYOUT_INDEX_3) {
                    // 3 有图
                    rateType = "3";
                } else {
                    rateType = null;
                }
            }
        }
        return rateType;
    }
}
