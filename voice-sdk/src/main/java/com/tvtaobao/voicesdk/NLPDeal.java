package com.tvtaobao.voicesdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.control.base.BizBaseBuilder;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.request.NlpNewRequest;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.tvtaobao.voicesdk.type.DomainType;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by panbeixing on 2018/4/9.
 */

public class NLPDeal extends BizBaseControl {

    protected final static String TAG = "NLPDeal";
    private String asr_text;
    private addressReceived addressReceiver;
    private IntentFilter intentFilter;
    private boolean mReceiverTag = false;
    private DomainResultVo mDomainResultVo;
    private boolean isLogin;

    /**
     * 请求idst的语义理解能力
     *
     * @param asr_text
     */
    public void nlpRequest(String asr_text) {
        if (TextUtils.isEmpty(asr_text)) {
            return;
        }


        BusinessRequest.getBusinessRequest().baseRequest(
                new NlpNewRequest(asr_text, SDKInitConfig.getLocation()), new NlpListener(asr_text), false);
    }

    /**
     * 加入购物车请求
     *
     * @param itemId 商品ID
     */
    public void addCart(String itemId) {
        BusinessRequest.getBusinessRequest().addBag(
                itemId, 1, null, "", new AddCartListener());
    }

    /**
     * 收藏
     *
     * @param itemId
     */
    public void manageFav(String itemId) {
        BusinessRequest.getBusinessRequest().manageFav(itemId, "addAuction", new ManageFavListener());
    }

    @Override
    public void execute(DomainResultVo domainResultVO) {

    }

    class NlpListener implements RequestListener<DomainResultVo> {


        public NlpListener(String asr) {
            asr_text = asr;
        }

        @Override
        public void onRequestDone(DomainResultVo domainResultVo, int resultCode, String msg) {
            if (domainResultVo == null) {
                notDeal();
                return;
            }
            mDomainResultVo = domainResultVo;
            TTSUtils.getInstance().setDomainResult(domainResultVo);
            if (domainResultVo.getTaobaoLogin() != null && domainResultVo.getTaobaoLogin() == 1) {
                LogPrint.i(TAG, "NlpListener startLoginActivity");
                //需要登录
                isLogin = false;
                startLoginActivity();
                return;
            }

            if (domainResultVo.getAddressSwitch() != null && domainResultVo.getAddressSwitch() == 1) {
                LogPrint.i(TAG, "NlpListener startAddressActivity");
                //TODO 需要地址
                if (!mReceiverTag) {
                    mReceiverTag = true;

                    addressReceiver = new addressReceived();
                    intentFilter = new IntentFilter();
                    intentFilter.addAction("com.yunos.tvtaobao.address");
                    mWeakService.get().registerReceiver(addressReceiver, intentFilter);
                }
                LogPrint.e(TAG, "需要地址");
                String url = "tvtaobao://home?module=address&page=https://tvos.taobao.com/wow/yunos/act/yuyinwaimaixuanzhe?wh_isJump=false";
                gotoActivity(url);
                return;
            }


            LogPrint.e(TAG, "NlpListener domain : " + domainResultVo.getDomain() + " ,intent : " + domainResultVo.getIntent());
            configVO.asr_text = asr_text;
            if (DomainType.TYPE_ONLINE_SHOPPING.equals(domainResultVo.getDomain())) {
                //TODO 语点注册需要修改
                //TODO 看看第三个，买第三个。要进入到页面内。
                BizBaseControl bizBaseControl = BizBaseBuilder.builder(domainResultVo.getIntent());
                if (bizBaseControl != null) {
                    if (!TextUtils.isEmpty(domainResultVo.getLoadingTxt())) {     //如果含有loadingTxt，就直接先反正loading状态。
                        alreadyDeal(domainResultVo.getLoadingTxt());
                    }
                    bizBaseControl.init(mWeakService, mWeakListener);
                    bizBaseControl.setConfig(configVO);
                    bizBaseControl.execute(domainResultVo);
                } else {
                    PageReturn pageReturn = ASRNotify.getInstance().isAction(domainResultVo);
                    if (pageReturn != null && pageReturn.isHandler) {
                        alreadyDeal(pageReturn.feedback);
                    } else {
                        //TODO 语点注册需要修改
                        //TODO nlp表示是购物领域，但是最终没有执行的
                        Toast.makeText(mWeakService.get(), "没有执行的购物领域", Toast.LENGTH_SHORT).show();
                        notDeal();
                    }
                }
            } else {
                notDeal();

                Utils.utCustomHit("Voice_asr_unknown", getProperties(asr_text));
            }
            asr_text = null;
            if (mReceiverTag && addressReceiver != null) {
                mReceiverTag = false;
                mWeakService.get().unregisterReceiver(addressReceiver);
            }
        }
    }

    public class addressReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogPrint.e(TAG, "addressReceived " + intent.getAction());
            if ("com.yunos.tvtaobao.address".equals(intent.getAction())) {
                nlpRequest(asr_text);
            }
        }
    }

    @Override
    public void loginSuccess() {
        LogPrint.e(TAG, TAG + "登录成功");
        if (!TextUtils.isEmpty(asr_text) && !isLogin) {
            LogPrint.e(TAG, asr_text);
            isLogin = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    nlpRequest(asr_text);
                    asr_text = null;
                }
            }, 1000);
        }
    }


    /**
     * 加入购物车
     *
     * @author pan
     * @data 2017年9月7日 下午10:33
     */
    class AddCartListener implements RequestListener<ArrayList<SearchResult>> {

        @Override
        public void onRequestDone(ArrayList<SearchResult> data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".AddCartListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                onTTS("加入购物车成功，您还想买什么？");
            } else {
                onTTS(msg);
            }
        }
    }

    /**
     * 加入收藏
     *
     * @author pan
     * @data 2017年9月7日 下午10:33
     */
    class ManageFavListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".ManageFavListener Code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                onTTS("加入收藏成功，您还想买什么？");
            } else {
                onTTS(msg);
            }
        }
    }
}
