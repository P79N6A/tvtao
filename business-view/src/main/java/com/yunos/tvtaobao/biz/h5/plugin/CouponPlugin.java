package com.yunos.tvtaobao.biz.h5.plugin;

import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by vincent on 1/17/17.
 * BLITZ调用阿里妈妈优惠券接口
 */

public class CouponPlugin {
    private static final String TAG = CouponPlugin.class.getSimpleName();
    private static final String DATA = "data";
    private static final String ITEMID = "itemId";
    private static final String PID = "pid";
    private static final String COUPONKEY = "couponKey";

    
    private WeakReference<TaoBaoBlitzActivity> mTaoBaoBlitzActivityWeakReference;
    private CouponJsCallback mCouponJsCallback;
    public CouponPlugin(WeakReference<TaoBaoBlitzActivity> taoBaoBlitzActivityWeakReference){
        mTaoBaoBlitzActivityWeakReference = taoBaoBlitzActivityWeakReference;
        onInitPlugin();
    }

    private void onInitPlugin() {
        mCouponJsCallback = new CouponJsCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs("applyCoupon", mCouponJsCallback);
        AppDebug.v(TAG, "CouponPlugin init finished.");
    }

    private void onHandleCallApply(String param, final long cbData) {
        AppDebug.v(TAG, "onHandleCallApply --> param = " + param + ";  cbData_final = " + cbData);
        TaoBaoBlitzActivity taoBaoBlitzActivity = null;
        JSONObject couponParams = null;
        try{
            couponParams = new JSONObject(param);
        }catch(JSONException ex){
            ex.printStackTrace();
        }
        if(couponParams == null){
            failResponseJs(BzResult.RET_PARAM_ERR, cbData);
            return;
        }
        final String itemId = couponParams.optString(ITEMID);
        final String couponKey = couponParams.optString(COUPONKEY);
        final String pid = couponParams.optString(PID);

        if(itemId != null && couponKey != null){
            if (mTaoBaoBlitzActivityWeakReference != null && mTaoBaoBlitzActivityWeakReference.get() != null) {
                taoBaoBlitzActivity = mTaoBaoBlitzActivityWeakReference.get();
            }
            //TODO: 优化if/else逻辑
            if (taoBaoBlitzActivity != null) {
                if(CoreApplication.getLoginHelper(taoBaoBlitzActivity).isLogin()){
                    //NOTE：getUserId会得不到数据，所以要调用SyncLoginListener
                    //TODO: 查找getUserId得不到数据的原因
                    final String userId = User.getUserId();
                    AppDebug.v(TAG, userId + itemId + couponKey + pid);
                    if(userId != null){
                        taoBaoBlitzActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doCouponApply(itemId, pid, couponKey, userId, cbData);
                            }
                        });
                    }else{
                        LoginHelper.SyncLoginListener synLoginListener = new LoginHelper.SyncLoginListener() {
                            @Override
                            public void onLogin(boolean isSuccess) {
                                CoreApplication.getLoginHelper(CoreApplication.getApplication()).removeSyncLoginListener(this);
                                if (isSuccess) {
                                    TaoBaoBlitzActivity taoBaoBlitzActivity = null;
                                    if (mTaoBaoBlitzActivityWeakReference != null && mTaoBaoBlitzActivityWeakReference.get() != null) {
                                        taoBaoBlitzActivity = mTaoBaoBlitzActivityWeakReference.get();
                                    }
                                    if(taoBaoBlitzActivity != null){
                                        taoBaoBlitzActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                doCouponApply(itemId, pid, couponKey, User.getUserId(), cbData);
                                            }
                                        });
                                    }else{
                                        failResponseJs(BzResult.RET_CLOSED, cbData);
                                    }
                                } else {
                                    //登录失败返回错误
                                    failResponseJs(BzResult.RET_NOT_LOGIN, cbData);
                                }
                            }
                        };
                        CoreApplication.getLoginHelper(CoreApplication.getApplication()).addSyncLoginListener(synLoginListener);
                        CoreApplication.getLoginHelper(CoreApplication.getApplication()).login(CoreApplication.getApplication());
                    }
                }else{
                    failResponseJs(BzResult.RET_NOT_LOGIN, cbData);
                }
            }else{
                failResponseJs(BzResult.RET_CLOSED, cbData);
            }
        }else{
            failResponseJs(BzResult.RET_PARAM_ERR, cbData);
        }
    }

    private static void failResponseJs(JSONObject data, final long cbData){
        BzResult result = new BzResult(BzResult.FAIL);
        if(data != null){
            result.addData(DATA, data);
        }
        String res = result.toJsonString();
        BlitzPlugin.responseJs(false, res, cbData);
    }

    private static void failResponseJs(BzResult result, final long cbData){
        String res = result.toJsonString();
        BlitzPlugin.responseJs(false, res, cbData);
    }

    private static void failResponseJs(String msg, final long cbData){
        BzResult result = new BzResult();
        if(msg != null){
            result.setResult(msg);
        }
        String res = result.toJsonString();
        BlitzPlugin.responseJs(false, res, cbData);
    }

    private static void successResponseJs(JSONObject data, final long cbData){
        BzResult result = new BzResult(BzResult.SUCCESS);
        if(data != null){
            result.addData(DATA, data);
        }
        String res = result.toJsonString();
        BlitzPlugin.responseJs(true, res, cbData);
    }

    private void doCouponApply(final String itemId, final String pid, final String couponKey, final String userId, final long cbData){
        BusinessRequest.getBusinessRequest().requestAlimamaApplyCoupon(itemId, pid, couponKey, userId, new couponRequestListener(cbData));
    }

    private static class couponRequestListener implements RequestListener<String> {

        private long cbData = 0L;
        private couponRequestListener(final long cbData){
            this.cbData = cbData;
        }

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            AppDebug.v(TAG, "onRequestDone ---> " + "data=" + data + ",resultCode=" + resultCode + ",msg=" + msg);
            if(resultCode == 200){
                JSONObject resultData = null;
                try{
                    resultData = new JSONObject(data);
                }catch (JSONException exception){
                    exception.printStackTrace();
                }finally {
                    /*
                    ```
    APPLY_COUPON_SUCCESS(0,"领券成功"),
    //买家领取单张券的限制
    APPLY_COUPON_SINGAL_COUNT_LIMIT(1,"买家领取单张券的限制"),
    //已达单日领取上限
    APPLY_COUPON_DYA_COUNT_LIMIT(2,"券已领完"),
    //其它领取失败
    APPLY_COUPON_OTHER_FAIL(3,"领取异常"),
    //领取安全异常
    APPLY_USER_NOT_SAFE(4,"领取用户不安全"),
    APPLY_COUPON_NOT_EXIST(5,"优惠券失效或过期");
                    ```
                    ```
                    startFee 满多少元可使用
                    amount 券面额
                    effectiveStartTime 使用开始时间
                    effectiveEndTime 使用结束时间
                    ```
                     */
                    if(resultData.optBoolean("success")){
                        resultData = resultData.optJSONObject("result");
                        //data中的success为true是真正的领取成功
                        if(resultData != null && resultData.optBoolean("success")){
                            CouponPlugin.successResponseJs(resultData, cbData);
                        }else{
                            CouponPlugin.failResponseJs(resultData, cbData);
                        }
                    }else{
                        //error request, output error msg
                        CouponPlugin.failResponseJs(resultData.optString("msg"), cbData);
                    }
                }
            }else{
                //http error
                CouponPlugin.failResponseJs("http status code is " + resultCode, cbData);
            }
        }
    }

    private static class CouponJsCallback implements BlitzPlugin.JsCallback {

        private WeakReference<CouponPlugin> mReference;

        private CouponJsCallback(WeakReference<CouponPlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.v(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                CouponPlugin plugin = mReference.get();
                plugin.onHandleCallApply(param, cbData);
            }
        }
    }
}
