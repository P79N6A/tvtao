package com.yunos.tvtaobao.detailbundle.view;

import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.bean.NewDetailPanelData;
import com.yunos.tvtaobao.detailbundle.type.DetailModleType;

import java.lang.ref.WeakReference;

/**
 * Created by dingbin on 2017/8/23.
 */

public class NewDetailView {
    private final int DISTANCE = 150;

    //详情
    private NewDetailPanelView mDetailPanelView;
    //购买加购扫码
    public NewDetailBuyView mDetailBuyView;
    //图文详情页
//    public NewDetailScrollInfoView mDetailPicView;

    private WeakReference<NewDetailActivity> mDetailActivityReference;

    public NewDetailView(WeakReference<NewDetailActivity> mBaseActivityRef) {
        mDetailActivityReference = mBaseActivityRef;

        // 初始化View值
        onInitViewHolder();

    }

    public void onDestroy(){
        try {
            mDetailPanelView.setImageNull();
            mDetailBuyView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onInitViewHolder() {
        // 创建商品信息按钮
        mDetailPanelView = new NewDetailPanelView(mDetailActivityReference);

        //创建购买按钮view
        mDetailBuyView = new NewDetailBuyView(mDetailActivityReference);

        //创建头图View
//        mDetailPicView = new NewDetailScrollInfoView(mDetailActivityReference);

    }

    /**
     * 设置商品信息
     */
    public void setGoodsInfo(NewDetailPanelData detailPanelData) {
        if (mDetailPanelView != null) {
            mDetailPanelView.setGoodsInfo(detailPanelData);
        }
    }

    //设置电淘积分的显示
    public void setTaobaoPointVisibilityState(int visibilityState) {
        mDetailPanelView.setTaobaoPointVisibility(visibilityState);


    }

    public void setItemFocusChangeListener() {
        if (mDetailBuyView != null) {
            mDetailBuyView.setItemFocusChangeListener();
        }

    }

    public void setIvTopButtonAddress(String ivTopButtonAddress) {
        mDetailBuyView.setIvTopButtonAddress(ivTopButtonAddress);
    }

    public void setIsShowAllPrice(Boolean isShowAllPrice){
       mDetailPanelView.setIsShowAllPrice(isShowAllPrice);

    }

    /**
     * 设置购物车和扫描按钮的显示状态
     *
     * @param addcarview
     * @param scanqrview
     */
    public void setButtonVisibilityState(int addcarview, int scanqrview) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setButtonVisibilityState(addcarview, scanqrview);
        }
    }
//    //购买按钮的文案
//    public void setGoodsBuyButton(String text,boolean canbuy){
//        if (mDetailBuyView != null) {
//            mDetailBuyView.setGoodsBuyButton(text, canbuy);
//        }
//    }

    //购买按钮文案
    public void setGoodsBuyButtonText(DetailModleType detailModleType, String status, String time, String buymessage, boolean buySupport) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setGoodsBuyButtonText(detailModleType, status, time, buymessage, buySupport);
        }
    }

//    //购买按钮的监听
//    public void setBuyButtonListener(View.OnClickListener listener) {
//        if (mDetailBuyView != null) {
//            mDetailBuyView.setBuyButtonListener(listener);
//        }
//
//    }
//
//    //购物车按钮的监听
//    public void setAddCartButtonListener(View.OnClickListener listener) {
//        if (mDetailBuyView != null) {
//            mDetailBuyView.setAddCartButtonListener(listener);
//        }
//
//
//    }
//
//    //去店铺按钮的监听
//    public void setScanQrCodetButtonListener(View.OnClickListener listener) {
//        if (mDetailBuyView != null) {
//            mDetailBuyView.setScanQrCodetButtonListener(listener);
//        }
//
//    }

    public void setTvPaymentTimeText(String paymentTime) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setTvPaymentTimeText(paymentTime);
        }

    }

    //设置时间倒计时结束的监听
    public void setTimeDoneListener(NewTimerTextView.TimeDoneListener timeDoneListener) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setTimeDoneListener(timeDoneListener);
        }
    }

    /**
     * 设置活动标签信息
     */
    public void setTagInfo(ProductTagBo productTagBo,boolean isPre) {
        if (mDetailPanelView != null) {
            mDetailPanelView.setTagInfo(productTagBo,isPre);
        }
    }

//    /**
//     * 更新时间
//     *
//     */
//    public void upDateTimer(List<String> timeList) {
//        if (mDetailPanelView != null) {
//            mDetailPanelView.upDateTimer(timeList);
//        }
//    }

    public void setModleType(DetailModleType detailModleType) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setModleType(detailModleType);
        }

        if (mDetailPanelView != null) {
            mDetailPanelView.setModleType(detailModleType);
        }
    }

    public void setStatus(String status) {
        if (mDetailBuyView != null) {
            mDetailBuyView.setStatus(status);
        }
    }
}
