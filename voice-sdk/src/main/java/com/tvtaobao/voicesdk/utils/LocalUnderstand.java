package com.tvtaobao.voicesdk.utils;

import com.tvtaobao.voicesdk.utils.LogPrint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pan on 2017/7/26.
 */

public class LocalUnderstand {
    private static String TAG = "LocalUnderstand";
    private static Map<String, Integer> intList;
    public static int ChineseToNumber(String str) {
        if (intList == null) {
            String[] chnNumChinese = {"零","一","二","三","四","五","六","七","八","九"};
            intList = new HashMap<>();
            for(int i=0;i<chnNumChinese.length;i++){
                intList.put(chnNumChinese[i], i);
            }

            intList.put("十",10);
            intList.put("两",2);
        }
        return intList.get(str);
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static int getNumeric(String str) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher num = pattern.matcher(str);
        return Integer.parseInt(num.replaceAll("").trim());
    }

    public static boolean containNumeric(String str) {
        return str.matches(".*?[\\d]+.*?");
    }

    public static int chooseNum(String str) {
        String matcher = ".*(第[一|二|三|四|五|六|七|八|九|1|2|3|4|5|6|7|8|9][个|件|款]).*";
        Pattern p = Pattern.compile(matcher);
        Matcher m = p.matcher(str);
        LogPrint.e(TAG, TAG + ".matches : " + m.matches());
        if (m.matches()) {
            int startIndex = str.indexOf("第") + 1;
            String num = str.substring(startIndex, startIndex + 1);
            int pos = -1;
            if (LocalUnderstand.isNumeric(num)) {
                pos = Integer.parseInt(num);
            } else {
                pos = LocalUnderstand.ChineseToNumber(num);
            }

            return pos;
        }

        return -1;
    }

    public static boolean isContain(String asr, String matcher) {
        Pattern pattern = Pattern.compile(matcher);
        Matcher m = pattern.matcher(asr);
        boolean b = m.matches();
        LogPrint.i(TAG, TAG + ".isContain matcher : " + b);
        return b;
    }
}
