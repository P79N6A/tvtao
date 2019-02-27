package com.yunos.tvtaobao.detailbundle.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.view.DetailScrollInfoView;

import java.lang.ref.WeakReference;
import java.util.Map;

public class DetailFullDescActivity extends TaoBaoBlitzActivity {

    private final String TAG = "DetailFullDesc";

    // 商品ID号
    private String mItemID = null;
    //private TBDetailResultVO mTBDetailResultVO;
    private TBDetailResultV6 tbDetailResultV6;
    //扩展参数,如淘抢购商品显示渠道专享价
    private String extParams;

    private BusinessRequest mBusinessRequest;
    private DetailScrollInfoView mDetailScrollInfoView;
    public boolean isSuperMarket;
    // 专项优惠价格
    private String uriPrice;
    private String btnText;
    //打标活动信息
    private ProductTagBo mProductTagBo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(DetailFullDescActivity.class.getName());

        setContentView(R.layout.ytm_activity_detail_fulldesc);

//        initBlitzContext();

        mItemID = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ITEMID);
        tbDetailResultV6 = (TBDetailResultV6) getIntent().getExtras().get("mTBDetailResultVO");
        extParams = getIntent().getStringExtra(BaseConfig.INTENT_KEY_EXTPARAMS);
        uriPrice = getIntent().getStringExtra(BaseConfig.INTENT_KEY_PRICE);
        btnText = getIntent().getStringExtra("buyText");
        mProductTagBo = (ProductTagBo) getIntent().getExtras().get("mProductTagBo");
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
        mDetailScrollInfoView = new DetailScrollInfoView(new WeakReference<Activity>(this));

        String buyText = btnText;
//        if (!isBuySupport()) {
////            if (mTBDetailResultVO.itemControl != null && mTBDetailResultVO.itemControl.unitControl != null
////                    && !TextUtils.isEmpty(mTBDetailResultVO.itemControl.unitControl.errorMessage)) {
////                buyText = mTBDetailResultVO.itemControl.unitControl.errorMessage;
////            } else {
//                buyText = getString(R.string.ytsdk_confirm_cannot_buy);
//            AppDebug.e("图文详情的按钮文字",buyText);
//            //}
//        } else {
////            if (mTBDetailResultVO.itemControl != null && mTBDetailResultVO.itemControl.unitControl != null
////                    && !TextUtils.isEmpty(mTBDetailResultVO.itemControl.unitControl.buyText)) {
////                buyText = mTBDetailResultVO.itemControl.unitControl.buyText;
////            } else {
//                buyText = getString(R.string.ytsdk_option_desc_immediately);
//            AppDebug.e("图文详情的按钮文字",buyText);
//           // }
//        }

        if (mDetailScrollInfoView != null && !TextUtils.isEmpty(buyText)) {
            mDetailScrollInfoView.setBuyText(buyText);
        }

        checkSuperMarket();

        if (isSuperMarket) {
            if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade() != null) {
                    if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getCartEnable() != null && DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getCartEnable().equals("true")) {
                        mDetailScrollInfoView.setBuyText("加入购物车");
                    } else {
                        mDetailScrollInfoView.setBuyText(btnText);
                    }
                } else {
                    mDetailScrollInfoView.setBuyText("加入购物车");
                }
            }
            // 设置添加购物车按钮的监听
            mDetailScrollInfoView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!checkNetwork()) {
                        return;
                    }
                    // 购物车统计
                    TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "Cart", null),
                            Utils.getKvs(initTBSProperty()));
                    if (isCartSupport()) {
                        addCart();
                    } else {
                        showNotbuyDialog();
                    }
                }
            });
        } else {
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
    }

    // 检测商品是否为天猫超市
    private void checkSuperMarket() {
        // 检查商品类型
        if (tbDetailResultV6 != null && DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical() != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical().getSupermarket() != null) {
                    isSuperMarket = true;
                } else {
                    isSuperMarket = false;
                }
            }
        }

    }

    // 加入购物车
    private void addCart() {
        Intent intent = new Intent();
        intent.setClassName(this.getBaseContext(), BaseConfig.SWITCH_TO_SKU_ACTIVITY);
        intent.putExtra(BaseConfig.INTENT_KEY_REQUEST_TYPE, TradeType.ADD_CART);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra("extParams", extParams);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }
        startActivity(intent);
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

        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        return p;
    }

    /**
     * 显示二维码扫描
     */
    private void showQRCode(String text, boolean isfeizhu) {
        Bitmap icon = null;
        Drawable drawable = getResources().getDrawable(R.drawable.ytm_qr_code_icon_taobao);
        ;
        if (drawable != null) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            icon = bd.getBitmap();
        }

        AppDebug.v(TAG, TAG + ".showQRCode.mItemId = " + mItemID + ", icon = " + icon);
        showItemQRCodeFromItemId(text, mItemID, icon, true, null,isfeizhu);
    }

    /**
     * 不能购买时，单击按钮，弹出对话框提示
     */
    private void showNotbuyDialog() {
        String message = null;
        if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            if (tbDetailResultV6 != null && DetailV6Utils.getUnit(tbDetailResultV6).getTrade() != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getHintBanner() != null) {
                    if (DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getHintBanner().getText() != null) {
                        message = DetailV6Utils.getUnit(tbDetailResultV6).getTrade().getHintBanner().getText();
                    }
                }
            }
        }
        if (TextUtils.isEmpty(message)) {
            message = btnText;
            //message = getString(R.string.ytsdk_confirm_cannot_buy);
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
     * 图文详情页购买流程：飞猪或者花费充值的商品直接显示二维码
     *
     * @return
     */
    private boolean buy() {

        //飞猪商品提示扫码购买
        if ((tbDetailResultV6.getTrade() != null && tbDetailResultV6.getTrade().getRedirectUrl() != null && tbDetailResultV6.getTrade().getRedirectUrl().contains("trip"))) {
            showQRCode(getString(R.string.ytbv_qr_buy_item), true);
            return true;
        }

        //话费充值商品提示扫码购买
        if ((DetailV6Utils.getUnit(tbDetailResultV6) != null && DetailV6Utils.getUnit(tbDetailResultV6).getItem() != null && DetailV6Utils.getUnit(tbDetailResultV6).getItem().getTitle() != null && (DetailV6Utils.getUnit(tbDetailResultV6).getItem().getTitle().contains("充值") || DetailV6Utils.getUnit(tbDetailResultV6).getItem().getTitle().contains("话费")))) {
            showQRCode(getString(R.string.ytbv_qr_buy_item), false);
            return true;
        }

        if (tbDetailResultV6.getFeature() != null) {
            if (tbDetailResultV6.getFeature().getSecKill() != null) {
                if (tbDetailResultV6.getFeature().getSecKill().equals("true")) {
                    if (isBuySupport()) {
                        showQRCode(getString(R.string.ytbv_qr_buy_item), false);
                    } else {
                        showNotbuyDialog();
                    }
                    return true;
                }
            }
        }

        //不支持购买直接返回
        if (!isBuySupport()) {
            showNotbuyDialog();
            return true;
        }


        // 统计购按钮
        TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "Buy", null),
                Utils.getKvs(initTBSProperty()));

        boolean isJuhuasuan = false;
        if (tbDetailResultV6 != null && DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical() != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical().getJhs() != null) {
                    isJuhuasuan = true;
                } else {
                    isJuhuasuan = false;
                }
            } else {
                isJuhuasuan = false;
            }
        } else {
            isJuhuasuan = false;
        }

        // 跳转到选择商品页面,如果是聚划算商品,先参团
        sureJoin(isJuhuasuan);

        return true;
    }

    /**
     * 根据商品的ID号请求数据
     *
     * @param itemId
     */
    private void requestLoadDetail(final String itemId) {
        OnWaitProgressDialog(true);

        // 请求详情页面的内容
        mBusinessRequest.requestGetFullItemDesc(itemId, new GetFullItemDescBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));

    }

    /**
     * 跳转到选择商品页面,如果是聚划算商品,先参团
     */
    private void sureJoin(boolean isJuhuasuan) {

        if (isFinishing()) {
            return;
        }

        Intent intent = new Intent();
        intent.setClassName(this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra("extParams", extParams);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }

        if (!isJuhuasuan) {
            startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
            OnWaitProgressDialog(false);
        } else {
            // 参团
            mBusinessRequest.requestJoinGroup(mItemID, new JoinGroupBusinessRequestListener(
                    new WeakReference<BaseActivity>(this), intent));
        }
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
                html = html.replace("</body>", mDetailScrollInfoView.getPropsHtml(tbDetailResultV6) + "</body>");
            }
        } else if (mDetailScrollInfoView != null) {
            html = html + mDetailScrollInfoView.getPropsHtml(tbDetailResultV6) + "</body>";
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
            DetailFullDescActivity mDetailFullDescActivity = (DetailFullDescActivity) mBaseActivityRef.get();
            if (mDetailFullDescActivity != null) {
                mDetailFullDescActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
            DetailFullDescActivity mDetailFullDescActivity = (DetailFullDescActivity) mBaseActivityRef.get();
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

    /**
     * 参团请求的监听类
     */
    private static class JoinGroupBusinessRequestListener extends BizRequestListener<JoinGroupResult> {
        private final Intent intent;

        public JoinGroupBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, Intent intent) {
            super(mBaseActivityRef);
            this.intent = intent;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            DetailFullDescActivity mDetailFullDescActivity = (DetailFullDescActivity) mBaseActivityRef.get();
            if (mDetailFullDescActivity != null) {
                mDetailFullDescActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(JoinGroupResult data) {
            DetailFullDescActivity mDetailFullDescActivity = (DetailFullDescActivity) mBaseActivityRef.get();
            if (mDetailFullDescActivity != null) {
                mDetailFullDescActivity.OnWaitProgressDialog(false);
                mDetailFullDescActivity
                        .startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

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
        onRemoveKeepedActivity(DetailFullDescActivity.class.getName());
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

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
