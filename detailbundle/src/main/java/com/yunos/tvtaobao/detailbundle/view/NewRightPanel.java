package com.yunos.tvtaobao.detailbundle.view;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.SystemUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.DetailEvaluateActivity;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.bean.NewDetailPanelData;
import com.yunos.tvtaobao.detailbundle.evaluate.CouponActivity;
import com.yunos.tvtaobao.detailbundle.evaluate.HaierShareActivity;
import com.yunos.tvtaobao.detailbundle.evaluate.ShareActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dingbin on 2017/8/23.
 */

public class NewRightPanel implements View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "DetailPanelView";
    protected String mPageName;
    public TextView tvBackHome;
    public TextView tvMyTaobao;
    public TextView tvCart;
    public TextView tvJifen;
    public TextView tvJifenNum;
    public TextView tvShare;
    public TextView tvCoupon;
    public TextView tvEvaluation;
    public TextView tvCollection;

    public ImageView ivBackHome;
    public ImageView ivMyTaobao;
    public ImageView ivCart;
    public ImageView ivJifen;
    public ImageView ivShare;
    public ImageView ivShare11;
    public ImageView ivCoupon;
    public ImageView ivEvaluation;
    public ImageView ivCollection;

    private ImageView ivToolbarBaseTop;

    private String title;

    public RelativeLayout rlJifen;
    public RelativeLayout rlEvaluation;

    private String mItemID;
    private String mSellerId;
    private String ivShare11Address;
    private List<ShopCoupon> mShopCouponList = new ArrayList<ShopCoupon>();
    // 下载管理器
    private ImageLoaderManager mImageLoaderManager;

    private WeakReference<NewDetailActivity> newDetailActivityWeakReference;

    private NewDetailPanelData newDetailPanelData;

    private NewDetailActivity newDetailActivity;

    private boolean isCollection = false;

    // 网络请求
    private BusinessRequest mBusinessRequest;


    public NewRightPanel(WeakReference<NewDetailActivity> weakReference) {
        newDetailActivityWeakReference = weakReference;
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        this.mItemID = newDetailActivityWeakReference.get().mItemId;
        initNewRightPanelView();
    }

    public void onDestroy(){
        try {
            mImageLoaderManager.stop();
            mImageLoaderManager.clearMemoryCache();
            mImageLoaderManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIvShare11Address(String ivShare11Address) {
        this.ivShare11Address = ivShare11Address;
        if (!TextUtils.isEmpty(ivShare11Address) && ivShare11 != null) {
            mImageLoaderManager.displayImage(ivShare11Address, ivShare11);
            ivShare11.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivShare11.setVisibility(View.GONE);
                }
            }, 3000);

        } else {
            ivShare11.setVisibility(View.GONE);

        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void showMagicalMCart(boolean isActing) {
        if (isActing) {
            ivToolbarBaseTop.setBackgroundResource(R.drawable.new_detail_toolbar_base_top_magical);
        } else {
            ivToolbarBaseTop.setBackgroundResource(R.drawable.new_detail_toolbar_base_top);
        }

    }

    public void initNewRightPanelView() {
        if (newDetailActivityWeakReference != null && newDetailActivityWeakReference.get() != null) {
            newDetailActivity = newDetailActivityWeakReference.get();
            tvBackHome = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_home);
            tvMyTaobao = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_my_taobao);
            tvCart = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_cart);
            tvJifen = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_jifen);
            tvJifenNum = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_jifen_num);
            tvShare = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_share);
            tvCoupon = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_coupon);
            tvEvaluation = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_evaluation_num);
            tvCollection = (TextView) newDetailActivity.findViewById(R.id.tv_pierce_collection_info);

            ivToolbarBaseTop = (ImageView) newDetailActivity.findViewById(R.id.iv_new_detail_toolbar_base_top);
            ivBackHome = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_home_focusd);
            ivMyTaobao = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_my_taobao_focusd);
            ivCart = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_cart_focusd);
            ivJifen = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_jifen_focusd);
            ivShare = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_share_focusd);
            ivShare11 = (ImageView) newDetailActivity.findViewById(R.id.tv_pierce_share_11);
            ivCoupon = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_red_packet_focusd);
            ivEvaluation = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_evaluation_focusd);
            ivCollection = (ImageView) newDetailActivity.findViewById(R.id.fiv_pierce_collection_focusd);

            rlJifen = (RelativeLayout) newDetailActivity.findViewById(R.id.rl_pierce_jifen);
            rlEvaluation = (RelativeLayout) newDetailActivity.findViewById(R.id.rl_pierce_evaluation);
            mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(newDetailActivity);

            mPageName = newDetailActivity.getFullPageName();
            Utils.utPageAppear(mPageName, mPageName);
            setOnPierceItemFocusdListener();
            setOnItemViewCLickListener();


        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view.getId() == R.id.fiv_pierce_home_focusd) {
            if (hasFocus) {

                Utils.utCustomHit("Expore_TbDetail_Home", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_HOME));
                tvBackHome.setVisibility(View.VISIBLE);

            } else {
                tvBackHome.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.fiv_pierce_my_taobao_focusd) {
            if (hasFocus) {
                tvMyTaobao.setVisibility(View.VISIBLE);
                if (newDetailActivityWeakReference != null && newDetailActivityWeakReference.get() != null) {
                    NewDetailActivity newDetailActivity = newDetailActivityWeakReference.get();
                    Utils.utCustomHit("Expore_TbDetail_MyTb", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_MYTB));
                    if (CoreApplication.getLoginHelper(newDetailActivity).isLogin()) {
                        tvMyTaobao.setText(User.getNick());
                    } else {
                        String myTaobaoStr = newDetailActivity.getResources().getString(R.string.ytbv_pierce_my_taobao);
                        tvMyTaobao.setText(myTaobaoStr);
                    }
                }
            } else {
                tvMyTaobao.setVisibility(View.GONE);

            }
        } else if (view.getId() == R.id.fiv_pierce_cart_focusd) {
            if (hasFocus) {

                Utils.utCustomHit("Expore_TbDetail_Cart", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_CART));
                tvCart.setVisibility(View.VISIBLE);

            } else {
                tvCart.setVisibility(View.GONE);

            }
        } else if (view.getId() == R.id.fiv_pierce_jifen_focusd) {
            if (hasFocus) {

                Utils.utCustomHit("Expore_TbDetail_Point", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_POINT));
                rlJifen.setVisibility(View.VISIBLE);

            } else {
                rlJifen.setVisibility(View.GONE);

            }
        } else if (view.getId() == R.id.fiv_pierce_share_focusd) {
            if (hasFocus) {
                Utils.utCustomHit("Expore_TbDetail_Share", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_SHARE));
                if (!TextUtils.isEmpty(ivShare11Address)) {
                    ivShare11.setVisibility(View.VISIBLE);
                } else {
                    tvShare.setVisibility(View.VISIBLE);
                }
            } else {
                ivShare11.setVisibility(View.GONE);
                tvShare.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.fiv_pierce_red_packet_focusd) {
            if (hasFocus) {

                Utils.utCustomHit("Expore_TbDetail_Coupon", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_COUPON));
                tvCoupon.setVisibility(View.VISIBLE);
            } else {
                tvCoupon.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.fiv_pierce_evaluation_focusd) {
            if (hasFocus) {
                Utils.utCustomHit("Expore_TbDetail_Evaluate", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_EVALUATE));
                rlEvaluation.setVisibility(View.VISIBLE);
            } else {
                rlEvaluation.setVisibility(View.GONE);
            }

        } else if (view.getId() == R.id.fiv_pierce_collection_focusd) {
            if (hasFocus) {
                Utils.utCustomHit("Expore_TbDetail_Collection", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_COLLECTION));
                tvCollection.setVisibility(View.VISIBLE);
            } else {
                tvCollection.setVisibility(View.GONE);
            }

        }


    }

    private void setOnPierceItemFocusdListener() {
        ivBackHome.setOnFocusChangeListener(this);
        ivMyTaobao.setOnFocusChangeListener(this);
        ivCart.setOnFocusChangeListener(this);
        ivJifen.setOnFocusChangeListener(this);
        ivShare.setOnFocusChangeListener(this);
        ivCoupon.setOnFocusChangeListener(this);
        ivEvaluation.setOnFocusChangeListener(this);
        ivCollection.setOnFocusChangeListener(this);

    }


    @Override
    public void onClick(View view) {
        //返回首页
        if (view.getId() == R.id.fiv_pierce_home_focusd) {
            //埋点
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Home", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_HOME));

            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_HOME);
            Intent intent = new Intent();
            intent.setClassName(newDetailActivity, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //setFrom("");
            newDetailActivity.startActivity(intent);
        } else if (view.getId() == R.id.fiv_pierce_my_taobao_focusd) {//我的淘宝
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_MyTb", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_MYTB));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_MYTB);
            Intent intent = new Intent();
            intent.setClassName(newDetailActivity, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
            //setFrom("");
            newDetailActivity.startActivity(intent);
        } else if (view.getId() == R.id.fiv_pierce_cart_focusd) {//购物车
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Cart2", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_CART));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_CART);

            Intent intent = new Intent();
            intent.setClassName(newDetailActivity, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //setFrom("");
            newDetailActivity.startActivity(intent);
        } else if (view.getId() == R.id.fiv_pierce_jifen_focusd) {//积分
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Ponit", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_POINT));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_POINT);

            Intent intent = new Intent();
            intent.setClassName(newDetailActivity, BaseConfig.SWITCH_TO_POINT_ACTIVITY);
            newDetailActivity.startActivity(intent);
        } else if (view.getId() == R.id.fiv_pierce_share_focusd) {//分享
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Share", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_SHARE));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_SHARE);
            //                ShareActivity.launch(newDetailActivity, mItemID, title);
//                share2HaierApp(newDetailActivity);
//                HaierShareActivity.launch(newDetailActivity,mItemID, title,newDetailPanelData.toutuUrl,newDetailPanelData.nowPrice,newDetailPanelData.oldPrice,newDetailPanelData.postage);

            if (SystemUtil.isAppInstalled(newDetailActivity, "com.haier.haiertv.ims")) {
                if (!checkNetwork(newDetailActivity)) {
                    return;
                }
                HaierShareActivity.launch(newDetailActivity, mItemID, title, newDetailPanelData.toutuUrl, newDetailPanelData.nowPrice, newDetailPanelData.oldPrice, newDetailPanelData.postage);
            } else {
                ShareActivity.launch(newDetailActivity, mItemID, title);
            }
        } else if (view.getId() == R.id.fiv_pierce_red_packet_focusd) {//优惠券

            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Coupon", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_COUPON));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_COUPON);
            CouponActivity.launch(newDetailActivity, mSellerId, mItemID, title);
//                Intent intent = new Intent(newDetailActivity,CouponActivity.class);
//                newDetailActivity.startActivity(intent);

        } else if (view.getId() == R.id.fiv_pierce_evaluation_focusd) {
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Evaluate", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_EVALUATE));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_EVALUATE);
//            showEvaluate();

            Intent intent = new Intent(newDetailActivity, DetailEvaluateActivity.class);
            intent.putExtra("mTBDetailResultVO", newDetailActivity.tbDetailResultV6);
            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, mItemID);
            newDetailActivity.startActivity(intent);
        } else if (view.getId() == R.id.fiv_pierce_collection_focusd) {
            Utils.utControlHit(newDetailActivity.getPageName(), "Button_Collection", initTBSProperty(SPMConfig.NEW_DETAIL_SIDEBAR_COLLECTION));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_SIDEBAR_COLLECTION);
            if (!isCollection) {
                mBusinessRequest.addCollection(mItemID, new AddCollectionRequestListener(newDetailActivityWeakReference));
            } else {
                mBusinessRequest.cancelCollection(mItemID, new CancelCollectionRequestListener(newDetailActivityWeakReference));
            }
        }
    }

    /**
     * 管理右侧收藏文案
     * @param isFav
     */
    public void manFavText(boolean isFav) {
        if (isFav) {
            isCollection = true;
            tvCollection.setText("已收藏");
        } else {
            isCollection = false;
            tvCollection.setText("收藏宝贝");
        }
    }

//    // 打开评论界面
//    private void showEvaluate() {
//        Intent intent = new Intent(this, DetailEvaluateActivity.class);
//        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
//        intent.putExtra(SDKInitConfig.INTENT_KEY_ITEMID, mItemId);
//        startActivity(intent);
//    }

    private void setOnItemViewCLickListener() {
        ivBackHome.setOnClickListener(this);
        ivMyTaobao.setOnClickListener(this);
        ivCart.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivJifen.setOnClickListener(this);
        ivCoupon.setOnClickListener(this);
        ivEvaluation.setOnClickListener(this);
        ivCollection.setOnClickListener(this);
    }


    /**
     * 检查网络状态
     *
     * @return
     */
    private boolean checkNetwork(NewDetailActivity newDetailActivity) {
        boolean result = false;
        if (!NetWorkUtil.isNetWorkAvailable()) {
            result = false;
            newDetailActivity.showNetworkErrorDialog(false);
        } else {
            newDetailActivity.removeNetworkOkDoListener();
            result = true;
        }
        return result;
    }


    public void setDetailPanelData(NewDetailPanelData newDetailPanelData) {
        this.newDetailPanelData = newDetailPanelData;
    }

    /**
     * 处理淘宝积分显示
     */
    private void onHandleGetTaobaoPoint(String Mypoint, NewDetailActivity mNewDetailActivity) {
        if (tvJifen != null && tvJifenNum != null) {
            if (!Mypoint.isEmpty() && Integer.parseInt(Mypoint) > 0) {
                if (Integer.parseInt(Mypoint) > 9999) {
                    tvJifen.setText(mNewDetailActivity.getString(R.string.new_detail_taobao_point));
                    tvJifenNum.setText("9999+");

                } else {
                    tvJifen.setText(mNewDetailActivity.getString(R.string.new_detail_taobao_point));
                    tvJifenNum.setText(Mypoint);
                }
            }
        }
    }

    public void setTvCouponNum(List<ShopCoupon> mShopCouponList) {
        NewDetailActivity mNewDetailActivity = (NewDetailActivity) newDetailActivityWeakReference.get();
        if (mNewDetailActivity != null && mShopCouponList != null && mShopCouponList.size() > 0) {
            tvCoupon = (TextView) mNewDetailActivity.findViewById(R.id.tv_pierce_coupon);
            tvCoupon.setText(mShopCouponList.size() + mNewDetailActivity.getString(R.string.new_detail_taobao_coupon_num));
        } else {
            tvCoupon = (TextView) mNewDetailActivity.findViewById(R.id.tv_pierce_no_coupon);
            tvCoupon.setText(mNewDetailActivity.getString(R.string.new_detail_taobao_coupon_num_null));
        }
    }


    //判断是否收藏
    public void getIsCollection(BusinessRequest mBusinessRequest, String itemId) {
        if (mBusinessRequest == null || newDetailActivityWeakReference == null) {
            return;
        }
        mBusinessRequest.checkFav(itemId, new IsCollectionRequestListener(newDetailActivityWeakReference));

    }

    //判断是否收藏的监听
    public class IsCollectionRequestListener extends BizRequestListener<String> {

        public IsCollectionRequestListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }
        @Override
        public boolean onError(int resultCode, String msg) {
            isCollection = false;
            tvCollection.setText("收藏宝贝");
            return false;
        }
        @Override
        public void onSuccess(String data) {
            isCollection = false;
            AppDebug.d(TAG, "msg----------------:" + data);
            if (TextUtils.equals(data, "true")) {
                isCollection = true;
                tvCollection.setText("已收藏");
            } else {
                isCollection = false;
                tvCollection.setText("收藏宝贝");
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    //宝贝加入收藏的监听
    public class AddCollectionRequestListener extends BizRequestListener<String> {

        public AddCollectionRequestListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }
        @Override
        public boolean onError(int resultCode, String msg) {
            isCollection = false;
            tvCollection.setText("收藏宝贝");
            return false;
        }
        @Override
        public void onSuccess(String data) {
            isCollection = false;
            AppDebug.d(TAG, "msg----------------:" + data);
            if (TextUtils.equals(data, "true")) {
                isCollection = true;
                tvCollection.setText("已收藏");
            } else {
                isCollection = false;
                tvCollection.setText("收藏宝贝");
            }
        }
        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    //宝贝取消收藏的监听
    public class CancelCollectionRequestListener extends BizRequestListener<String> {
        public CancelCollectionRequestListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(String data) {
            isCollection = false;
            AppDebug.d(TAG, "msg----------------:" + data);
            if (TextUtils.equals(data, "true")) {
                isCollection = false;
                tvCollection.setText("收藏宝贝");
            } else {
                isCollection = true;
                tvCollection.setText("已收藏");
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 获取我的积分
     */
    public void getPoint(BusinessRequest mBusinessRequest) {
        if (mBusinessRequest == null || newDetailActivityWeakReference == null) {
            return;
        }
        mBusinessRequest.requestTaobaoPoint(new TaobaoPointRequestListener(newDetailActivityWeakReference));
    }

    /**
     * 淘宝积分回调
     */
    private static class TaobaoPointRequestListener extends BizRequestListener<String> {
        public TaobaoPointRequestListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (mNewDetailActivity != null) {
                mNewDetailActivity.mRightPanel.tvJifen.setText(mNewDetailActivity.getString(R.string.new_detail_taobao_point));
                mNewDetailActivity.mRightPanel.tvJifenNum.setText("0");
//             setJifen(mNewDetailActivity.getString(R.string.new_detail_taobao_point)+ "0");
            }
            return true;
        }

        @Override
        public void onSuccess(String point) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (mNewDetailActivity != null) {
                AppDebug.d(mNewDetailActivity.TAG, point);
            }

            mNewDetailActivity.mRightPanel.onHandleGetTaobaoPoint(point, mNewDetailActivity);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 获取店铺优惠券
     *
     * @param mBusinessRequest
     * @param mSellerId
     */
    public void getShopCoupon(BusinessRequest mBusinessRequest, String mSellerId) {
        this.mSellerId = mSellerId;
        if (newDetailActivityWeakReference != null)
            mBusinessRequest.getShopCoupon(mSellerId, new ShopCouponListBusinessRequestListener(new WeakReference<BaseActivity>(newDetailActivityWeakReference.get())));
    }


    /**
     * 获取店铺优惠券的回调
     */
    private static class ShopCouponListBusinessRequestListener extends BizRequestListener<List<ShopCoupon>> {
        public ShopCouponListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            mNewDetailActivity.mRightPanel.setTvCouponNum(null);
            return true;
        }

        @Override
        public void onSuccess(List<ShopCoupon> data) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (mNewDetailActivity != null) {
                mNewDetailActivity.mRightPanel.mShopCouponList = data;
                mNewDetailActivity.mRightPanel.setTvCouponNum(mNewDetailActivity.mRightPanel.mShopCouponList);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }


    /**
     * 请求评论的数据
     */
    public void getEvaluationNumData(BusinessRequest mBusinessRequest) {
        String rateType = null;
        AppDebug.i(TAG, "getRatesData -->  pageNo = 1" + "; pagesize = 2 ; rateType = " + rateType);
        mBusinessRequest.requestGetItemRates(mItemID, 1, 2, rateType,
                new GetItemRatesBusinessRequestListener(new WeakReference<BaseActivity>(newDetailActivityWeakReference.get())));

    }

    /**
     * 评论请求的监听类
     */
    private static class GetItemRatesBusinessRequestListener extends BizRequestListener<PaginationItemRates> {

        public GetItemRatesBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.i(NewDetailActivity.TAG,
                    "GetItemRatesBusinessRequestListener --> onError --> resultCode = " + resultCode + "; msg = "
                            + msg);

            return true;
        }

        @Override
        public void onSuccess(PaginationItemRates data) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            AppDebug.i(NewDetailActivity.TAG, "GetItemRatesBusinessRequestListener --> onSuccess --> data = "
                    + data);
            mNewDetailActivity.mRightPanel.handlerGetAllRatesData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    public void handlerGetAllRatesData(PaginationItemRates data) {
        if (newDetailActivityWeakReference != null && newDetailActivityWeakReference.get() != null) {
            if (data != null && data.getFeedAllCount() != null) {
                tvEvaluation.setText(data.getFeedAllCount());
            }
        }
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }

        if (!TextUtils.isEmpty(title)) {
            p.put("item_name", title);
        }
        try {
            if (!TextUtils.isEmpty(AppInfo.getPackageName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
                p.put("from_app", AppInfo.getPackageName() + AppInfo.getAppVersionName());
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }

        if (newDetailActivityWeakReference != null && newDetailActivityWeakReference.get() != null) {
            NewDetailActivity newDetailActivity = newDetailActivityWeakReference.get();
            if (CoreApplication.getLoginHelper(newDetailActivity.getApplicationContext()).isLogin()) {
                p.put("is_login", "1");
            } else {
                p.put("is_login", "0");
            }
        }

        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

//        p.put(SPMConfig.SPM_CNT, "a2o0j.7984570.newdetail.detailpoint");

        return p;

    }


}
