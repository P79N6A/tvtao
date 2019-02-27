/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.h5
 * FILE NAME: ChongzhiActivity.java
 * CREATED TIME: 2016年4月14日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.h5.commonbundle.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 充值界面
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2016年4月14日 下午1:59:38
 */
public class ChongzhiActivity extends TaoBaoBlitzActivity {

    private final static String TAG = "ChongzhiActivity";
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.i(TAG, TAG + ".onCreate --> page 1 = " + page);

        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap().get("chongzhi");
        } else {
            page = URLDecoder.decode(page);
        }

        page = PageUtil.processPageUrl(page);

        //构建语音路径
        page = buildVoiceUrl(page,getIntent());

        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        AppDebug.i(TAG, TAG + ".onCreate --> page 2 = " + page);

        if (!StringUtil.isInnerUrl(page)) {
            Toast.makeText(this.getApplicationContext(), getString(R.string.ytbv_invalid_h5_url), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        try {
            page = naiveAppendQueryParam(page, appkey, Config.getChannel());
        } catch (UnsupportedEncodingException e) {
        }

        onInitH5View(page);
        //测试
        //onInitH5View("http://tvos.taobao.com/wow/yunos/act/android-h5-static");
    }

    @Override
    protected void onResume() {
        super.onResume();

//        ASRUtils.getInstance().setHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        ASRUtils.getInstance().setHandler(null);
    }

    @Override
    public String getPageName() {
        return "Main";
    }

    @Override
    protected String getAppTag() {
        return "Cz";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void telephoneChar(String key) {
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(key);
        String phone = null;
        String price = null;
        while (m.find()) {
            String num = m.group(1).toString();
            if (isMobileNO(num)) {
                phone = num;
            }
            if (getTelephoneCharge(num, key)) {
                price = num;
            }
        }

        notifyPage(phone, price);
    }

    private void notifyPage(String phone, String price) {
        AppDebug.e(TAG,  "notifyPage   phone = " + phone + "    price = " + price);
        if (phone == null && price == null)
            return;

        Intent intent = new Intent();
        intent.setAction("android.intent.action.ACTION_SOUND");
        intent.putExtra("TELEPHONE_NUMBER", phone);
        intent.putExtra("PRIECE", price);
        sendBroadcast(intent);
    }

    private boolean getTelephoneCharge(String i, String key) {

        return key.contains("充" + i) || key.contains("充值" + i) || key.contains("冲" + i);
    }

    public boolean isMobileNO(String str){
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }

    //    @Override
//    public boolean handleAsrResult(String key) {
//        telephoneChar(key);
//        return true;
//    }
}
