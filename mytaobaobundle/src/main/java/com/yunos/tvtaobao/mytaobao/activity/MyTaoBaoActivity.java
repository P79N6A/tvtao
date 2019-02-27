package com.yunos.tvtaobao.mytaobao.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.ut.device.UTDevice;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusScrollerLinearLayout;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.util.DataEncoder;
import com.yunos.tv.core.util.StringUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.bo.CollectList;
import com.yunos.tvtaobao.biz.request.bo.FavModule;
import com.yunos.tvtaobao.biz.request.bo.FavoriteCnt;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.MyAlipayHongbaoList;
import com.yunos.tvtaobao.biz.request.bo.MyCoupon;
import com.yunos.tvtaobao.biz.request.bo.MyCouponsList;
import com.yunos.tvtaobao.biz.request.bo.OrderListData;
import com.yunos.tvtaobao.biz.request.bo.QueryBagRequestBo;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
//import com.yunos.tvtaobao.blitz.account.LoginHelper;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.mytaobao.BuildConfig;
import com.yunos.tvtaobao.mytaobao.R;
import com.yunos.tvtaobao.mytaobao.view.ItemLayoutForMyTaoBao;
import com.yunos.tvtaobao.payment.logout.LogoutActivity;

import java.lang.ref.WeakReference;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的淘宝主页面
 */
public class MyTaoBaoActivity extends BaseActivity {

    private final String TAG = "MyTaoBao";

    private final String SPM = "a2o0j.11516635";
    private Context mContext;
    private ItemLayoutForMyTaoBao order;     //订单
    private ItemLayoutForMyTaoBao cart;      //购物车
    private ItemLayoutForMyTaoBao collect;   //收藏
    private ItemLayoutForMyTaoBao point;     //积分
    private ItemLayoutForMyTaoBao coupon;    //卡券包
    private ItemLayoutForMyTaoBao address;   //地址
    private ItemLayoutForMyTaoBao account;   //账号

    private BusinessRequest mBusinessRequest;
    private FocusPositionManager layout;
    private Handler mHandler;
    private OnClickListener mOnClickListener;
    private OnFocusChangeListener mOnFocusChangeListener;
    private TextView mAppVersion;
    private TextView appBuildNo;

    // 优惠券列表
    private MyCouponsList mMyCouponsList;
    // 支付宝红包列表
    private MyAlipayHongbaoList mMyAlipayHongbaoList;
    // 重新调整后的优惠券数据列表
    private ArrayList<MyCoupon> mCouponDataList;
    // 是否需要显示Loading
    private boolean isNeedShowLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mHandler = new AppHandler<>(this);
        setContentView(R.layout.ytsdk_activity_mytaobao);
        registerLoginListener();

        // 初始化控件
        initViews();

        // 初始化请求类
        mBusinessRequest = BusinessRequest.getBusinessRequest();

        // 初始化焦点控件
        initWrappedLayout();

    }

    @Override
    protected void onDestroy() {
        unRegisterLoginListener();
        order = null;
        cart = null;
        collect = null;
        address = null;
        coupon = null;
        mBusinessRequest = null;
        layout = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean loginIsCanceled = loginIsCanceled();
        AppDebug.v(TAG, TAG + ".onResume.loginIsCanceled = " + loginIsCanceled);
        if (!loginIsCanceled) {
            refreshData();
        } else {
            finish();
        }

    }

    /**
     * 刷新数据
     */
    protected void refreshData() {
        // 获取订单数量
        getOrderCount();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        // 加入右边的间隔
        FocusScrollerLinearLayout scrollerLayout = (FocusScrollerLinearLayout) findViewById(R.id.mytaobao_scroller);
        int paddingRight = getResources().getDimensionPixelSize(R.dimen.dp_40);
        scrollerLayout.setManualPaddingRight(paddingRight);
        order = (ItemLayoutForMyTaoBao) findViewById(R.id.order);
        cart = (ItemLayoutForMyTaoBao) findViewById(R.id.cart);
        collect = (ItemLayoutForMyTaoBao) findViewById(R.id.collect);
        point = (ItemLayoutForMyTaoBao) findViewById(R.id.point);
        address = (ItemLayoutForMyTaoBao) findViewById(R.id.address);
        coupon = (ItemLayoutForMyTaoBao) findViewById(R.id.coupon);
        account = (ItemLayoutForMyTaoBao) findViewById(R.id.account);
        mAppVersion = (TextView) findViewById(R.id.loading_app_version);
        appBuildNo = (TextView) findViewById(R.id.loading_app_buildno);
        order.setFocus(true);
        if (!TextUtils.isEmpty(SystemConfig.APP_VERSION)) {
            mAppVersion.setText(SystemConfig.APP_VERSION + " - ");
            appBuildNo.setText(BuildConfig.BUILD_NO);
        }
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (order != null) {
                    order.setOnClickListener(mOnClickListener);
                    order.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (cart != null) {
                    cart.setOnClickListener(mOnClickListener);
                    cart.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (collect != null) {
                    collect.setOnClickListener(mOnClickListener);
                    collect.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (point != null) {
                    point.setOnClickListener(mOnClickListener);
                    point.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (address != null) {
                    address.setOnClickListener(mOnClickListener);
                    address.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (coupon != null) {
                    coupon.setOnClickListener(mOnClickListener);
                    coupon.setOnFocusChangeListener(mOnFocusChangeListener);
                }
                if (account != null) {
                    account.setOnClickListener(mOnClickListener);
                    account.setOnFocusChangeListener(mOnFocusChangeListener);
                }
            }
        }, 500);
    }

    /**
     * 账号初始化
     */
    private void initAccount() {
        account.setDetail(User.getNick());
        account.getImageFlow().setVisibility(View.VISIBLE);
        ImageLoaderManager.getImageLoaderManager(this)//TODO
                .loadImage(
                        "http://wwc.taobaocdn.com/wangwang/headshow.htm?longId="
                                + DataEncoder.urlEncode(User.getNick(), "GBK"), new ImageLoadingListener() {

                            @Override
                            public void onLoadingStarted(String arg0, View arg1) {
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                account.getImageIcon().setImageResource(R.drawable.ytsdk_ui2_mytaobao_item_default);
                                account.getImageIcon().setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
                                account.getImageIcon().setImageBitmap(bitmap);
                                account.getImageIcon().setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingCancelled(String arg0, View arg1) {
                                account.getImageIcon().setImageResource(R.drawable.ytsdk_ui2_mytaobao_item_default);
                                account.getImageIcon().setVisibility(View.VISIBLE);
                            }
                        });
    }

    /**
     * 获得订单数量
     */
    private void getOrderCount() {

        if (mBusinessRequest == null) {
            return;
        }

        // 只有第一次进入页面时才会显示Loading，避免每次onresume时都会显示loading
        if (isNeedShowLoading) {
            OnWaitProgressDialog(true);
        }
        mBusinessRequest.requestGetOrderCount(new GetOrderListBusinessRequestListener(new WeakReference<BaseActivity>(
                this)));
    }

    /**
     * 获得购物车数量
     */
    private void getCartCount() {

        if (mBusinessRequest == null) {
            return;
        }

        QueryBagRequestBo queryBagRequestBo = new QueryBagRequestBo();
        queryBagRequestBo.setPage(true);

        mBusinessRequest.queryBag(queryBagRequestBo, new QueryBagListener(new WeakReference<BaseActivity>(this)));

    }

    /**
     * 获取收藏数量
     */
    private void getCollectCount() {

        if (mBusinessRequest == null) {
            return;
        }

//        mBusinessRequest.getCollects(1, 1, new CollectsBusinessRequestListener(new WeakReference<BaseActivity>(this)));

        mBusinessRequest.getNewCollectsNum(new CollectsNumBusinessRequestListener(new WeakReference<BaseActivity>(this)));

    }

    //TODO 获取积分数量

    /**
     * 获取我的积分
     */
    private void getPoint() {
        if (mBusinessRequest == null) {
            return;
        }
        mBusinessRequest.requestTaobaoPoint(new TaobaoPointRequestListener(new WeakReference<BaseActivity>(this)));
    }

    /**
     * 获取收货地址数量
     */
    private void getAddressCount() {

        if (mBusinessRequest == null) {
            return;
        }

        mBusinessRequest.requestGetAddressList(new GetAddressListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));

    }

    /**
     * 获取优惠券数量
     */
    private void getCouponCount() {

        if (mBusinessRequest == null) {
            return;
        }

        mBusinessRequest.requestMyCouponsList("1", null, new CouponListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 获取支付宝数量
     */
    public void getAlipayCount() {
        mBusinessRequest.requestMyAlipayHongbaoList("1", new MyAlipayListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    @Override
    protected void onLogout() {
    }

    /**
     * 初始化包含item的外层Layout，可以重载后修改
     *
     * @return
     */
    protected void initWrappedLayout() {
        layout = (FocusPositionManager) findViewById(R.id.mytaobao_layout);
        layout.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.ytbv_common_focus)));
        layout.requestFocus();

        mOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = null;
                int id = v.getId();
                if (id == R.id.order) {

//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Orders", "name=订单", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Orders", initTBSProperty("订单",SPM+".Button.P_Orders"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Orders");

                    intent = new Intent();
                    intent.setClassName(mContext, BaseConfig.SWITCH_TO_ORDERLIST_ACTIVITY);
                } else if (id == R.id.cart) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Cart", "name=购物车", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Cart", initTBSProperty("购物车",SPM+".Button.P_Cart"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Cart");

                    setHuodong("my_taobao_cart");
                    intent = new Intent();
                    intent.setClassName(mContext, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
                } else if (id == R.id.collect) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Collects", "name=收藏", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Collects", initTBSProperty("收藏",SPM+".Button.P_Collects"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Collects");

                    setHuodong("my_taobao_collect");
                    intent = new Intent();
                    intent.setClassName(mContext, BaseConfig.SWITCH_TO_COLLECTS_ACTIVITY);
                } else if (id == R.id.point) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Point", "name=积分", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Point", initTBSProperty("积分",SPM+".Button.P_Point"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Point");

                    intent = new Intent();
                    intent.setClassName(mContext, BaseConfig.SWITCH_TO_POINT_ACTIVITY);
                } else if (id == R.id.address) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Address", "name=地址", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Address", initTBSProperty("地址",SPM+".Button.P_Address"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Address");

                    intent = new Intent();
                    intent.setClassName(mContext, BaseConfig.SWITCH_TO_ADDRESS_ACTIVITY);
                } else if (id == R.id.coupon) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Coupon", "name=优惠券", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Coupon", initTBSProperty("优惠券",SPM+".Button.P_Coupon"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Coupon");

                    setHuodong("my_taobao_youhuiquan");
                    intent = new Intent();
                    if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                        String page = PageMapConfig.getPageUrlMap().get("coupon");
                        if (!TextUtils.isEmpty(page)) {
                            intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                        } else {
                            intent.setClassName(mContext, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                        }
                    } else
                        intent.setClassName(mContext, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                } else if (id == R.id.account) {
//                    TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Account", "name=账号", "uuid=" + CloudUUIDWrapper.getCloudUUID(),
//                            "from_channel=" + getmFrom());
                    Utils.utControlHit("Page_TtMyTaoBao", "Button-"+TAG+"_P_Account", initTBSProperty("账号",SPM+".P_Account.P_Point"));
                    Utils.updateNextPageProperties(SPM+".Button.P_Account");
                    setHuodong("my_taobao_account");
                    if (Config.isAgreementPay()) {
                        boolean status = CoreApplication.getLoginHelper(mContext).isLogin();
                        if (status) {
//                                CoreApplication.getLoginHelper(getApplicationContext()).startYunosAccountActivity(
//                                        MyTaoBaoActivity.this, false);
//                        CoreApplication.getLoginHelper(getApplicationContext()).logout(getApplicationContext());

                            intent = new Intent(MyTaoBaoActivity.this, LogoutActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(MyTaoBaoActivity.this, getString(R.string.ytsdk_account_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        CoreApplication.getLoginHelper(getApplicationContext()).startYunosAccountActivity(
                                MyTaoBaoActivity.this, false);
                    }

                    return;
                }

                startNeedLoginActivity(intent);
            }
        };

        mOnFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof ItemLayoutForMyTaoBao) {
                    ItemLayoutForMyTaoBao view = (ItemLayoutForMyTaoBao) v;
                    if (view != null) {
                        view.setFocus(hasFocus);
                    }
                }
            }
        };
    }

    public Map<String, String> initTBSProperty(String name,String spm) {
        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(getmFrom())) {
            p.put("from_channel", getmFrom());
        }
        if (!TextUtils.isEmpty(name)) {
            p.put("name", name);
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }


        return p;

    }


    @Override
    public String getPageName() {
        return TAG;
    }
    @Override
    public Map<String, String> getPageProperties() {

        Map<String, String> p = super.getPageProperties();
        p.put("spm-cnt", SPM+".0.0");
        return p;
    }


    /**
     * 处理淘宝积分显示
     */
    public void onHandleGetTaobaoPoint(String Mypoint) {
        if (point != null) {
            point.setDetail("");
        }
        point.setDetail(getString(R.string.ytsdk_all_taobao_point, Mypoint));
    }

    public void onHandleGetOrderList(OrderListData orderList) {

        if (order != null) {
            order.setDetail("");
        }

        // 订单请求完成了,在去请求收藏
        getCartCount();
        getCollectCount();
        getAddressCount();
        getCouponCount();
        getPoint();
        initAccount();

        if (orderList != null) {
            int total = orderList.getTotal();
            if (order != null) {
                order.setDetail(getString(R.string.ytsdk_all_order, total + ""));
            }
        }
    }

    public void onHandleRequestCollectsNum(FavModule data) {

        if (collect != null) {
            collect.setDetail("");
            if(data==null){
                collect.setDetail(getString(R.string.ytsdk_all_collect, "0"));
                return;
            }
            FavoriteCnt favoriteCnt = data.getFavoriteCnt();
            if(favoriteCnt==null){
                collect.setDetail(getString(R.string.ytsdk_all_collect, "0"));
                return;
            }

            String totalResults = favoriteCnt.getNum();
            if(!TextUtils.isEmpty(totalResults)) {
                collect.setDetail(getString(R.string.ytsdk_all_collect, totalResults));
            }else {
                collect.setDetail(getString(R.string.ytsdk_all_collect, "0"));
            }
        }
    }

    public void onHandleRequestCollects(CollectList collectList) {

        if (collect != null) {
            collect.setDetail("");

            if (collectList == null) {
                collect.setDetail(getString(R.string.ytsdk_all_collect, "0"));
                return;
            }

            int totalResults = collectList.geTotalCount();
            collect.setDetail(getString(R.string.ytsdk_all_collect, totalResults + ""));
        }
    }

    public void onHandleRequestGetAddressList(List<Address> addressList) {

        if (address != null) {
            address.setDetail("");
        }
        if (addressList != null && address != null) {
            address.setDetail(getString(R.string.ytsdk_all_address, addressList.size() + ""));
        } else if (address != null) {
            address.setDetail(getString(R.string.ytsdk_all_address, "0"));
        }
    }

    private void onHandleRequestCouponList() {

        if (coupon != null) {
            coupon.setDetail("");
            if (mMyCouponsList == null && mMyAlipayHongbaoList == null) {
                coupon.setDetail(getString(R.string.ytsdk_all_coupon, "0"));
                return;
            }

            int couponsCount = 0;
            if (mMyCouponsList != null && !TextUtils.isEmpty(mMyCouponsList.getTotalNum())) {
                // 去除无效的优惠券
                mCouponDataList = reBuildCouponList(mMyCouponsList.getCouponList());
                if (mCouponDataList != null) {
                    couponsCount = Integer.valueOf(mCouponDataList.size());
                }
            }
            int alipayCount = 0;
            if (mMyAlipayHongbaoList != null && !TextUtils.isEmpty(mMyAlipayHongbaoList.getTotalCount())) {
                alipayCount = Integer.valueOf(mMyAlipayHongbaoList.getTotalCount());
            }

            AppDebug.v(TAG, TAG + ", onHandleRequestCouponList.couponsCount = " + couponsCount + ", alipayCount = "
                    + alipayCount);

            coupon.setDetail(getString(R.string.ytsdk_all_coupon, couponsCount + alipayCount + ""));
        }
    }

    /**
     * 重新调整优惠券列表
     *
     * @param couponList
     * @return
     */
    private ArrayList<MyCoupon> reBuildCouponList(ArrayList<MyCoupon> couponList) {
        if (couponList == null) {
            return null;
        }
        ArrayList<MyCoupon> newCouponList = new ArrayList<MyCoupon>();
        for (int i = 0; i < couponList.size(); i++) {
            MyCoupon myCoupon = couponList.get(i);
            // 过滤有效的的优惠券，以及去除包邮券
            if (myCoupon != null && "1".equals(myCoupon.getStatus()) && !"8".equals(myCoupon.getCouponType())) {
                newCouponList.add(myCoupon);
            }
        }
        return newCouponList;
    }

    /**
     * 获取订单列表请求结果
     *
     * @author yunzhong.qyz
     */
    private static class GetOrderListBusinessRequestListener extends BizRequestListener<OrderListData> {

        public GetOrderListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            final MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.order != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".GetOrderListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = "
                            + msg);
                }
                myTaoBaoActivity.OnWaitProgressDialog(false);
                myTaoBaoActivity.isNeedShowLoading = false;
                myTaoBaoActivity.order.setDetail("");
                if (resultCode == 1) {//网络未连接
                    myTaoBaoActivity.setNetworkOkDoListener(new NetworkOkDoListener() {

                        @Override
                        public void todo() {
                            // 数据更新后取消网络注册
                            if (myTaoBaoActivity != null) {
                                AppDebug.v(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                                        + ".GetOrderListBusinessRequestListener.onError.refreshData");
                                myTaoBaoActivity.isNeedShowLoading = true;
                                myTaoBaoActivity.refreshData();
                                myTaoBaoActivity.setNetworkOkDoListener(null);
                            }
                        }
                    });
                }
            }
            return false;
        }

        @Override
        public void onSuccess(OrderListData data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".GetOrderListBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.OnWaitProgressDialog(false);
                myTaoBaoActivity.isNeedShowLoading = false;
                myTaoBaoActivity.onHandleGetOrderList(data);
            }

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }

    /**
     * 购物车请求返回结果
     */
    public static class QueryBagListener extends BizRequestListener<String> {

        public QueryBagListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.order != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG + ".QueryBagListener.onError.resultCode = "
                            + resultCode + ".msg = " + msg);
                }
                myTaoBaoActivity.cart.setDetail("");
            }
            return true;
        }

        @Override
        public void onSuccess(String data) {
            int totalResults = 0;
            JSONObject jsonData = JSON.parseObject(data);
            if (jsonData != null) {
                JSONObject innerData = jsonData.getJSONObject("data");
                if (innerData != null) {
                    JSONObject allItem = innerData.getJSONObject("allItemv2_1");
                    if (allItem != null) {
                        JSONObject fields = allItem.getJSONObject("fields");
                        if (fields != null) {
                            totalResults = fields.getIntValue("value");
                        }
                    }
                }
            }
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.order != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG + ".QueryBagListener.onSuccess.data = "
                            + data);
                }
                myTaoBaoActivity.cart.setDetail(myTaoBaoActivity.getString(R.string.ytsdk_all_collect, totalResults + ""));
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }

    private static class CollectsNumBusinessRequestListener extends BizRequestListener<FavModule>{

        public CollectsNumBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.collect != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CollectsBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = " + msg);
                }
                myTaoBaoActivity.collect.setDetail(myTaoBaoActivity.getString(R.string.ytsdk_all_collect, "0"));
            }
            return true;
        }

        @Override
        public void onSuccess(FavModule data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CollectsBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.onHandleRequestCollectsNum(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    private static class CollectsBusinessRequestListener extends BizRequestListener<CollectList> {

        public CollectsBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.collect != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CollectsBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = " + msg);
                }
                myTaoBaoActivity.collect.setDetail(myTaoBaoActivity.getString(R.string.ytsdk_all_collect, "0"));
            }
            return true;
        }

        @Override
        public void onSuccess(CollectList data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CollectsBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.onHandleRequestCollects(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }

    private static class GetAddressListBusinessRequestListener extends BizRequestListener<List<Address>> {

        public GetAddressListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.address != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".GetAddressListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = "
                            + msg);
                }

                if (resultCode == ServiceCode.NO_ADDRESS.getCode()) {
                    myTaoBaoActivity.address.setDetail(myTaoBaoActivity.getString(R.string.ytsdk_all_address, "0"));
                } else {
                    myTaoBaoActivity.address.setDetail("");
                }
            }
            return true;
        }

        @Override
        public void onSuccess(List<Address> data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".GetAddressListBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.onHandleRequestGetAddressList(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

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
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.point != null) {
                myTaoBaoActivity.point.setDetail(myTaoBaoActivity.getString(R.string.ytsdk_all_taobao_point, "0"));
            }
            return true;
        }

        @Override
        public void onSuccess(String point) {

            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                AppDebug.d(myTaoBaoActivity.TAG, point);
            }

            myTaoBaoActivity.onHandleGetTaobaoPoint(point);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class CouponListBusinessRequestListener extends BizRequestListener<MyCouponsList> {

        public CouponListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.coupon != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CouponListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = " + msg);
                }
                myTaoBaoActivity.getAlipayCount();

            }
            return true;
        }

        @Override
        public void onSuccess(MyCouponsList data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".CouponListBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.mMyCouponsList = data;
                myTaoBaoActivity.getAlipayCount();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class MyAlipayListBusinessRequestListener extends BizRequestListener<MyAlipayHongbaoList> {

        public MyAlipayListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null && myTaoBaoActivity.coupon != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".MyAlipayListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = "
                            + msg);
                }
                myTaoBaoActivity.onHandleRequestCouponList();
            }
            return true;
        }

        @Override
        public void onSuccess(MyAlipayHongbaoList data) {
            MyTaoBaoActivity myTaoBaoActivity = (MyTaoBaoActivity) mBaseActivityRef.get();
            if (myTaoBaoActivity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(myTaoBaoActivity.TAG, myTaoBaoActivity.TAG
                            + ".MyAlipayListBusinessRequestListener.onSuccess.data = " + data);
                }
                myTaoBaoActivity.mMyAlipayHongbaoList = data;
                myTaoBaoActivity.onHandleRequestCouponList();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    protected void onLoginCancel() {
        super.onLoginCancel();
        finish();
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return (FocusPositionManager) findViewById(R.id.mytaobao_layout);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        String uuid="model:"+Build.PRODUCT+"|"+"utdid:"+UTDevice.getUtdid(this)+"|"+"uuid:"+ (StringUtil.isEmpty(CloudUUIDWrapper.getCloudUUID())?"uuid未取到":CloudUUIDWrapper.getCloudUUID());
//        if (keyCode == KeyEvent.KEYCODE_MENU) {
//           Toast.makeText(this,uuid,Toast.LENGTH_LONG).show();
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }
}
