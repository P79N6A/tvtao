package com.yunos.tvtaobao.detailbundle.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.view.SeckKillDetailScrollInfoView;

import java.lang.ref.WeakReference;
import java.util.Map;

public class SeckKillDetailFullDescActivity extends TaoBaoBlitzActivity {

    private final String TAG = "DetailFullDesc";

    // 商品ID号
    private String mItemID = null;
    private TBDetailResultV6 tbDetailResultV6;
    //扩展参数,如淘抢购商品显示渠道专享价
    private String extParams;

    private BusinessRequest mBusinessRequest;
    private SeckKillDetailScrollInfoView mDetailScrollInfoView;
    public boolean isSuperMarket;
    // 专项优惠价格
    private String uriPrice;

    private String modeFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(SeckKillDetailFullDescActivity.class.getName());

        setContentView(R.layout.ytm_activity_detail_fulldesc);

        Intent intent = getIntent();
        if (intent != null) {
            modeFrom = intent.getStringExtra("mode_from");
            mItemID = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ITEMID);
        }

//        mTBDetailResultVO = (TBDetailResultVO) getIntent().getExtras().get("mTBDetailResultVO");
//        extParams = getIntent().getStringExtra(BaseConfig.INTENT_KEY_EXTPARAMS);
//        uriPrice = getIntent().getStringExtra(BaseConfig.INTENT_KEY_PRICE);
        if (TextUtils.isEmpty(mItemID)) {
            finish();
            return;
        }
        onInitDetailValue();
        requestLoadDetail(mItemID);
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }
        p.put(SPMConfig.SPM_CNT, "a2o0j.7984570.0.0");
        return p;
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    protected void refreshData() {

    }

    /**
     * 初始化详情页面的变量值
     */
    private void onInitDetailValue() {
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mDetailScrollInfoView = new SeckKillDetailScrollInfoView(new WeakReference<Activity>(this));


        // 设置立即购买按钮的监听
        mDetailScrollInfoView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!checkNetwork()) {
                    return;
                } else {
                    buy();
                }
            }
        });
    }


    /**
     * 检查网络状态
     *
     * @return
     */
    private boolean checkNetwork() {
        boolean result = false;
        if (!NetWorkUtil.isNetWorkAvailable()) {
            result = false;
            showNetworkErrorDialog(false);
        } else {
            removeNetworkOkDoListener();
            result = true;
        }
        return result;
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    private Map<String, String> initTBSProperty() {
        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }

        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        return p;
    }

    /**
     * 不能购买时，单击按钮，弹出对话框提示
     */
    private void showNotbuyDialog() {
        String message = null;
//        if (isBuySupport() != null
//                && mTBDetailResultVO.itemControl.unitControl != null) {
//            if (!TextUtils.isEmpty(mTBDetailResultVO.itemControl.unitControl.errorMessage)) {
//                message = mTBDetailResultVO.itemControl.unitControl.errorMessage;
//            }
//        }
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.ytsdk_confirm_cannot_buy);
        }

        showErrorDialog(message, false);
    }

    /**
     * 是否支持购买
     *
     * @return
     */
    private boolean isBuySupport() {
        if (tbDetailResultV6 != null) {
            if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getBuyEnable() != null && DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getBuyEnable().equals("false")) {
                    return false;
                }
            } else {
                MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
                if (mockdata != null && !mockdata.getTrade().isBuyEnable()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否支持购买
     *
     * @return
     */
    private boolean isCartSupport() {
        if (tbDetailResultV6 != null) {
            if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getCartEnable() != null && DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getCartEnable().equals("false")) {
                return false;
            }
        }
        return true;
    }

    /**
     * 购买流程
     *
     * @return
     */
    private boolean buy() {
        AppDebug.v(TAG, TAG + ".buy.buy = ");
        //不支持购买直接返回
        if (!isBuySupport()) {
            showNotbuyDialog();
            return true;
        }
        AppDebug.v(TAG, TAG + ".buy.buy.buy = ");
        // 统计购按钮
        TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "Buy", null),
                Utils.getKvs(initTBSProperty()));

        // 跳转到选择商品页面,如果是聚划算商品,先参团
        sureJoin();

        return true;
    }

    /**
     * 根据商品的ID号请求数据
     *
     * @param itemId
     */
    private void requestLoadDetail(final String itemId) {
        OnWaitProgressDialog(true);

        String params = Utils.jsonString2HttpParam(extParams);
        AppDebug.v(TAG, TAG + ".requestLoadDetail.itemId = " + mItemID + ".extParams = " + extParams + ",params = "
                + params);
        mBusinessRequest.requestGetItemDetailV6(mItemID, params, new GetItemDetailBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
        // 请求详情页面的内容
        mBusinessRequest.requestGetFullItemDesc(itemId, new GetFullItemDescBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));


    }

    /**
     * 跳转到选择商品页面
     */
    private void sureJoin() {
        AppDebug.v(TAG, TAG + ".sureJoin.itemId = ");
        if (isFinishing()) {
            return;
        }
        AppDebug.v(TAG, TAG + ".requestLoadDetail.SkuActivity = ");
        Intent intent = new Intent();
        intent.setClassName(this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra("extParams", extParams);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);

        startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
        OnWaitProgressDialog(false);
    }

    /**
     * 清除html的body标签默认内外边距
     * <style>
     * body{
     * margin:0;
     * padding:0;
     * }
     * </style>
     *
     * @param html
     * @return
     */
    private String clearHtmlBodyMarginPadding(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        int bodyIndex = html.indexOf("<body>");
        if (bodyIndex >= 0) {
            String replaceStyleBody = "<body style='margin:0px;padding:0px;background-color:white;'>";
            html = html.replace("<body>", replaceStyleBody);
        } else {
            html = "<body style='margin:0px;padding:0px;background-color:white;'>" + html;
        }

        //插入商品属性
        int bodyLastIndex = html.indexOf("</body>");
        if (bodyLastIndex >= 0) {
            if (mDetailScrollInfoView != null) {
//                html = html.replace("</body>", mDetailScrollInfoView.getPropsHtml(mTBDetailResultVO) + "</body>");
            }
        } else if (mDetailScrollInfoView != null) {
//            html = html + mDetailScrollInfoView.getPropsHtml(mTBDetailResultVO) + "</body>";
        }

        //插入页面配置
        String replaceContent = "<meta name='viewport' content='width=790'></meta><meta charset='utf-8'></meta><style>img {vertical-align:bottom;}u {display:inline;}iframe {width:0px;height:0px}</style>";
        if (html.indexOf("<head>") != -1) {
            html = html.replace("<head>", "<head>" + replaceContent);
        } else if (html.indexOf("<html>") != -1) {
            html = html.replace("<html>", "<html>" + replaceContent);
        } else {
            html = replaceContent + html;
        }

        return html;
    }

    /**
     * 商品详情的内容
     */
    private static class GetFullItemDescBusinessRequestListener extends BizRequestListener<String> {

        public GetFullItemDescBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mBaseActivityRef.get();
            if (mDetailFullDescActivity != null) {
                mDetailFullDescActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
            SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mBaseActivityRef.get();
            if (mDetailFullDescActivity != null) {
                mDetailFullDescActivity.OnWaitProgressDialog(false);
                if (!TextUtils.isEmpty(data)) {
                    // 加载商品详情的内容
                    String detailFullHtml = mDetailFullDescActivity.clearHtmlBodyMarginPadding(data);
                    //FileUtil.writeFileSdcardFile("/sdcard/detail.txt", detailFullHtml);
                    if (mDetailFullDescActivity.mDetailScrollInfoView != null) {
                        mDetailFullDescActivity.mDetailScrollInfoView.loadDataWithBaseURL("about:blank",
                                detailFullHtml, "text/html", "UTF-8", "");

                    }
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.d(TAG,"event: " + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
//            AppDebug.d(TAG, TAG + "dispatchKeyEvent " + modeFrom);
//            if (modeFrom != null && modeFrom.equals("tvbuy")) {
//                Intent intent = new Intent();
//                intent.setAction("com.yunos.tv.tvtaobao.graphicdetail");
//                sendBroadcast(intent);
//            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDetailScrollInfoView.setFronstedGlassSreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDetailScrollInfoView.onCleanAndDestroy();
        onRemoveKeepedActivity(SeckKillDetailFullDescActivity.class.getName());
    }

    /**
     * tbsdk子应用页面开头，用于TBS统计
     */
    protected String getAppTag() {
        return "Tb";
    }


    @Override
    protected boolean isTbs() {
        return true;
    }


    /**
     * 处理商品详情返回的结果
     *
     * @param mTBDetailResultVO
     */
    private void onHandleRequestGetItemDetail(TBDetailResultV6 mTBDetailResultVO) {

        OnWaitProgressDialog(false);
        if (mTBDetailResultVO == null) {
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
        if (mTBDetailResultVO.getItem() == null) {
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
        this.tbDetailResultV6 = mTBDetailResultVO;

        String buyText = "";

//        if (tbDetailResultV6.getTrade() != null) {
//            if (tbDetailResultV6.getTrade().getBuyEnable() != null) {
//                if (tbDetailResultV6.getTrade().getBuyEnable().equals("true")) {
//                    buyText = "马上抢";
//                } else {
//                    buyText = "暂不支持购买";
//                }
//                AppDebug.e(TAG, "秒杀是否可购买= " + buyText);
//            }
//
//        }

        if (DetailV6Utils.getUnit(tbDetailResultV6)!=null){
            if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade()!=null){
                if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getBuyEnable()!=null&&DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getBuyEnable().equals("true")){
                    buyText = "马上抢";
                } else {
                    buyText = "暂不支持购买";
                }
            }
        }


        if (mDetailScrollInfoView != null && !TextUtils.isEmpty(buyText)) {
            mDetailScrollInfoView.setBuyText(buyText);
        }
    }


    /**
     * 详情页面数据请求的监听类
     */
    private static class GetItemDetailBusinessRequestListener extends BizRequestListener<TBDetailResultV6> {

        public GetItemDetailBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SeckKillDetailFullDescActivity detailActivity = (SeckKillDetailFullDescActivity) mBaseActivityRef.get();
            if (detailActivity != null) {
                detailActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(TBDetailResultV6 data) {
            SeckKillDetailFullDescActivity detailFullDescActivity = (SeckKillDetailFullDescActivity) mBaseActivityRef.get();
            if (detailFullDescActivity != null) {
                detailFullDescActivity.onHandleRequestGetItemDetail(data);
            }

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
