package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.content.Intent;
import android.net.Uri;

import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;


/**
 * 调用详情页
 * @author hanqi
 */
public class ToItemDetail {


    public static void detail(JuBaseActivity context, final Long juId, final Long itemId) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "tvtaobao://home?app=taobaosdk&module=detail&itemId="+itemId+"&juId="+juId+"&from_app=juhuasuan";
        intent.setData(Uri.parse(url));
        context.startActivity(intent);

    }

}
