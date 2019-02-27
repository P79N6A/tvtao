package com.yunos.tvtaobao.biz.common;


import android.content.Context;
import android.text.TextUtils;

import com.yunos.tvtaobao.businessview.R;

/**
 * 文案处理工具
 */
public class DocumentUtil {

    /**
     * 把src中带有资源数组中R.array.ytbv_document_wireless的文字替换成TV端
     * @param context
     * @param src
     * @return
     */
    public static String replaceWireless(Context context, String src) {
        if (context != null && !TextUtils.isEmpty(src)) {
            // 读取需要被替换的文案
            String[] wirelesssstr = context.getResources().getStringArray(R.array.ytbv_document_wireless);
            // 读取替换后的文案
            String tv = context.getResources().getString(R.string.ytbv_document_tv);
            int length = wirelesssstr.length;
            String replaceText = src;
            for (int i = 0; i < length; i++) {
                if (replaceText.contains(wirelesssstr[i])) {
                    replaceText = replaceText.replace(wirelesssstr[i], tv);
                }
            }
            return replaceText;
        }
        return src;
    }
}
