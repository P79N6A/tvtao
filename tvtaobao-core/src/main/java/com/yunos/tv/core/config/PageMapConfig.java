package com.yunos.tv.core.config;

import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.tv.core.common.AppDebug;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GuoLiDong on 2018/12/24.
 */
public class PageMapConfig {
    private static final String TAG = PageMapConfig.class.getSimpleName();
    private static Map<String, String> mPageUrlMap;
    private static void initPageUrlMap() {
        if (mPageUrlMap == null) {
            mPageUrlMap = new HashMap<String, String>();
        }

        RetData retData = TVCompliance.getComplianceDomain("tvos.taobao.com");
        AppDebug.d(TAG, "Converted code is " + retData.toString());
        if (retData.getCode() == RetCode.Success || retData.getCode() == RetCode.Default) {
            AppDebug.d(TAG, "Converted domain is " + retData.getResult());
        } else {
            AppDebug.d(TAG, "Original domain is " + retData.getResult());
        }
        String domainName = retData.getResult();

        mPageUrlMap.put("home", "https://" + domainName + "/wow/yunos/act/home");//正式主页
        //mPageUrlMap.put("home", "http://pre-wormhole.tmall.com/wow/yunos/act/home?wh_showError=true&wh_appkey=2016092423");//正式主页
        //预发主页:http://pre-wormhole.tmall.com/wow/yunos/act/home?wh_showError=true&wh_appkey=2016092423
        mPageUrlMap.put("chaoshi", "https://" + domainName + "/wow/yunos/act/tvchaoshi");
        mPageUrlMap.put("chaoshi_searchresult", "https://" + domainName + "/wow/chaoshi/act/cssr");
        mPageUrlMap.put("orderlist", "https://" + domainName + "/wow/yunos/act/order");
        //TODO 域名需要更改
        mPageUrlMap.put("shopbliz", "https://" + domainName + "/wow/yunos/act/shop-home");
        mPageUrlMap.put("point", "https://" + domainName + "/wow/yunos/act/myjifen");
        mPageUrlMap.put("todaygoods", "https://" + domainName + "/wow/yunos/act/tvtb-ntoday");
        mPageUrlMap.put("coupon", "https://" + domainName + "/wow/yunos/act/ka-quan-bao");


        RetData retData1 = TVCompliance.getComplianceDomain("h5.m.taobao.com");
        AppDebug.d(TAG, "Converted code is " + retData1.toString());
        if (retData1.getCode() == RetCode.Success || retData1.getCode() == RetCode.Default) {
            AppDebug.d(TAG, "Converted domain is " + retData1.getResult());
        } else {
            AppDebug.d(TAG, "Original domain is " + retData1.getResult());
        }
        String domainName1 = retData1.getResult();
//        AppDebug.d(TAG, "Converted domain is "+ domainName1);
        mPageUrlMap.put("chongzhi", "https://" + domainName1 + "/yuntv/tvphonepay.html");
        mPageUrlMap.put("recommend", "https://" + domainName1 + "/yuntv/recommend.html");
        mPageUrlMap.put("relativerecommend", "https://" + domainName1 + "/yuntv/detailrecommend.html");
        mPageUrlMap.put("tiantian", "https://" + domainName1 + "/yuntv/saledetail.html");
        mPageUrlMap.put("fenlei", "https://" + domainName1 + "/yuntv/subsite.html");
        mPageUrlMap.put("zhuhuichang", "https://" + domainName1 + "/yuntv/mainsite.html");


//        AppDebug.d(TAG, "Converted relativerecommend is "+ mPageUrlMap.get("relativerecommend"));
    }
    static {
        initPageUrlMap();
    }
    public static Map<String, String> getPageUrlMap() {
        return mPageUrlMap;
    }
}
