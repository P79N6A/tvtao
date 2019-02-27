package com.yunos.tvtaobao.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.yunos.tvtaobao.biz.request.bo.TMallLiveCommentBean;
import com.yunos.tvtaobao.live.R;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by libin on 16/9/21.
 */

public class Tools {

    public static int[] color = {0xff009999, 0xff009900, 0xff0066cc, 0xffcc00cc, 0xffcc3300};

    public static int compatiblePx(Context context, int defaultPx) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        if (widthPixels == 1280) {
            return defaultPx;
        } else {
            float scale = widthPixels / 1280f;
            return (int) (defaultPx * scale + 0.5f);
        }
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static int getRandomBySize(int size) {
        if (size <= 0)
            return 0;

        Random random = new Random();
        return random.nextInt(size);
    }

    public static String getTrueImageUrl(String url) {
        String s = "";
        if (url.startsWith("/")) {
            s = "http:" + url;
        } else {
            s = url;
        }

        return s;
    }

    private static List<TMallLiveCommentBean.ModelBean.DataBean> dataBeen = new ArrayList<>();
    public static List<TMallLiveCommentBean.ModelBean.DataBean> getNewData(List<TMallLiveCommentBean.ModelBean.DataBean> list) {
        dataBeen.clear();
        int size = list.size();
        for (int i = 30; i > 0 ; i--) {
            dataBeen.add(list.get(size - i));
        }

        return dataBeen;
    }

    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return re_time;
    }

    public static BitmapDrawable getBitmapDrawable(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        opt.inPurgeable = true;

        opt.inInputShareable = true;

        InputStream is = context.getResources().openRawResource(resId);

        Bitmap bm = BitmapFactory.decodeStream(is, null, opt);

        BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);

        return bd;
    }

    public static String getPrice(Context context, String price) {
        if (price.contains("元")) {
            return price;
        } else {
            return context.getString(R.string.ytsdk_dollar_sign) + " " + price;
        }
    }

    public static String getNum(long num) {
        if (num >= 1000000) {
//            float sd = (float) (num/10000);
//            DecimalFormat df  = new DecimalFormat(".0");
            return String.format("%.1f万", (float) (num/10000f));
        } else if (num >= 100000) {
            double sd = ((double) (num/10000.00));
            DecimalFormat df  = new DecimalFormat(".0#");
            return df.format(sd) + "万";
        } else {
            return num+"";
        }
    }

    public static String getNum(Double num) {
        if (num >= 1000000) {
//            float sd = (float) (num/10000);
//            DecimalFormat df  = new DecimalFormat(".0");
            return String.format("%.1f万", (float) (num/10000f));
        } else if (num >= 100000) {
            double sd = (num/10000.00);
            DecimalFormat df  = new DecimalFormat(".0#");
            return df.format(sd) + "万";
        } else {
            return num.intValue()+"";
        }
    }


    public static String exchangeUnit(String s) {
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        String ss = "";
        if (Integer.parseInt(s) > 10000) {
            DecimalFormat df = new DecimalFormat("0.00");
            ss = df.format((double) Integer.parseInt(s) / 10000);
            ss =  ss+ "万";

        } else {
            ss = Integer.parseInt(s) + "";
        }
        return ss;
    }

    public static String getUserHead(String userId, int size) {
        return "http://wwc.alicdn.com/avatar/getAvatar.do?userId=" + userId + "&width=" + size + "&height=" + size;
    }

    public static String fuzzyNick(String nick) {
        return nick.substring(0, 1) + "****" + nick.substring(nick.length() - 1, nick.length());
    }

    public static String simpleMapToJsonStr(Map<String ,String> map){
        if(map==null||map.isEmpty()){
            return "null";
        }
        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\""+key+"\":\""+map.get(key)+"\",";
        }
        jsonStr = jsonStr.substring(1,jsonStr.length()-2);
        jsonStr += "}";
        return jsonStr;
    }

    public static SpannableString setTextImg(Context context, String content, int startIndex, int endIndex, Drawable drawable){
        if(TextUtils.isEmpty(content) || startIndex < 0 || startIndex >= endIndex ){
            return null;
        }

        SpannableString spannableString = new SpannableString(content);
        drawable.setBounds(0, 0, compatiblePx(context, 42), compatiblePx(context, 22));
        ImageSpan imageSpan = new ImageSpan(drawable);
        spannableString.setSpan(imageSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public static void runGc() {
        System.gc();
        System.runFinalization();
    }
}
