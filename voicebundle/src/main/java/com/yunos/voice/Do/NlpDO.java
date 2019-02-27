package com.yunos.voice.Do;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.tvtaobao.voicesdk.utils.LocalUnderstand;
import com.yunos.tv.core.common.AppDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pan on 2017/8/9.
 */

public class NlpDO {

    /**
     * score : 1
     * back_intent : search_goods
     * slots : {"product_type":[{"raw":"液晶电视","norm":"液晶电视"}],"product_brand":[{"raw":"小米","norm":"小米"}]}
     * back_domain : online_shopping
     * keywords : ["42","寸"]
     * domain : online_shopping
     * source : REMOTE
     * intent : search_goods
     */

    private String score;
    private String product_type = "";
    private String product_brand = "";
    private String domain;
    private String intent;
    private String timeText;
    private String beginTime;
    private String endTime;
    private List<String> keywords;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProduct_brand() {
        return product_brand;
    }

    public void setProduct_brand(String product_brand) {
        this.product_brand = product_brand;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public static NlpDO resolverDataFromMTOP(JSONObject object) throws JSONException {
        NlpDO nlpDO = new NlpDO();
        nlpDO.setScore(object.getString("score"));
        nlpDO.setDomain(object.getString("domain"));
        nlpDO.setIntent(object.getString("intent"));
        nlpDO.setKeywords(GsonUtil.parseJson(object.getJSONArray("keywords").toString(), new TypeToken<List<String>>(){}));
        JSONObject slotsObject = object.getJSONObject("slots");
        if (slotsObject.has("product_type")) {
            JSONArray product_types = slotsObject.getJSONArray("product_type");
            String product_type = "";
            for (int i = 0  ; i < product_types.length() ; i++) {
                product_type += product_types.getJSONObject(i).getString("norm");
            }
            nlpDO.setProduct_type(product_type);
        }

        if (slotsObject.has("time")) {
            JSONArray time = slotsObject.getJSONArray("time");
            nlpDO.setTimeText(time.getJSONObject(0).getString("raw"));

            if (time.getJSONObject(0).has("timex")) {
                JSONObject timeX = time.getJSONObject(0).getJSONObject("timex");
                String dateType = timeX.getString("type");
                AppDebug.e("TVTao_NlpDo", "dateType : " + dateType);
                if (dateType.equals("DATE")) {
                    String value = timeX.getString("value");
                    String[] times = value.split("-");
                    AppDebug.e("TVTao_NlpDo", "times.length : " + times.length);
                    if (times.length == 2) {
                        nlpDO.setBeginTime(nlpDO.timeXToDate(value));
                        nlpDO.setEndTime(nlpDO.timeXToDate(value));
                    } else if (times.length == 3) {
                        Calendar c = Calendar.getInstance();
                        AppDebug.e("TVTao_NlpDo", "times[0] : " + times[0] + " ,time[1] : " + times[1] + " ,time[2] : " + times[2]);
                        if (times[0].equals("XXXX")) {
                            times[0] = String.valueOf(c.get(Calendar.YEAR));
                        }

                        if (times[1].equals("XX") && times[2].equals("XX")) {   //2017-XX-XX

                            nlpDO.setBeginTime(times[0] + "-01" + "-01");
                            nlpDO.setEndTime(times[0] + "-12" + "-31");

                        } else if (times[1].equals("XX") && !times[2].equals("XX")) {  //XXXX-XX-23,2017-XX-23

                            nlpDO.setBeginTime(times[0] + "-" + (c.get(Calendar.MONTH) + 1) + "-" + times[2]);
                            nlpDO.setEndTime(times[0] + "-" + (c.get(Calendar.MONTH) + 1) + "-" + times[2]);

                        } else if (!times[1].equals("XX") && times[2].equals("XX")) { //XXXX-09-XX, 2017-09-XX
                            c.set(Calendar.MONTH, Integer.parseInt(times[1]) - 1);
                            nlpDO.setBeginTime(times[0] + "-" + times[1] + "-01");
                            nlpDO.setEndTime(times[0] + "-" + times[1] + "-" + c.getActualMaximum(Calendar.DAY_OF_MONTH));
                        } else { //2017-09-23
                            nlpDO.setBeginTime(times[0] + "-" + times[1] + "-" + times[2]);
                            nlpDO.setEndTime(times[0] + "-" + times[1] + "-" + times[2]);
                        }
                    }
                } else if (dateType.equals("DURATION")) {
                    String start = timeX.getString("start").replace("TXX:XX:XX", "");
                    String end = timeX.getString("end").replace("TXX:XX:XX", "");

                    nlpDO.setBeginTime(nlpDO.ToStrartDay(start));
                    nlpDO.setEndTime(nlpDO.ToEndDay(end));
                }
            }
        }
        AppDebug.e("TVTao_NlpDo", "1 startTime : " + nlpDO.getBeginTime() + " ,endTime : " + nlpDO.getEndTime());
        //模糊时间。前2天，前几周
        if (slotsObject.has("norm_time")) {
            JSONArray norm_time = slotsObject.getJSONArray("norm_time");
            nlpDO.setTimeText(norm_time.getJSONObject(0).getString("raw"));
            String timeType = norm_time.getJSONObject(0).getString("norm");

            int fewday = 0;   //时间范围
            if (slotsObject.has("index")) {
                JSONArray index = slotsObject.getJSONArray("index");
                fewday = Integer.parseInt(index.getJSONObject(0).getString("norm"));

                if (timeType.equals("DAY")) {

                    //减去几天
                    Calendar cal = Calendar.getInstance();
                    nlpDO.setEndTime(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                    cal.add(Calendar.DATE, -fewday);
                    nlpDO.setBeginTime(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));

                } else if (timeType.equals("WEEK")) {

                    //减去几周时间
                    Calendar cal = Calendar.getInstance();
                    int dayWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    cal.add(Calendar.DAY_OF_MONTH, -dayWeek);
                    nlpDO.setEndTime(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));

                    cal.add(Calendar.WEEK_OF_MONTH, -fewday);
                    nlpDO.setBeginTime(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + (cal.get(Calendar.DAY_OF_MONTH) + 1));

                } else if (timeType.equals("MONTH")) {

                    //减去几个月时间
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -fewday);
                    nlpDO.setBeginTime(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-01");

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, -1);
                    nlpDO.setEndTime(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
            }

        }
        AppDebug.e("TVTao_NlpDo", "2 startTime : " + nlpDO.getBeginTime() + " ,endTime : " + nlpDO.getEndTime());
        if (slotsObject.has("product_brand")) {
            JSONArray product_brands = slotsObject.getJSONArray("product_brand");
            String product_brand = "";
            for (int i = 0  ; i < product_brands.length() ; i++) {
                product_brand += product_brands.getJSONObject(i).getString("norm");
            }
            nlpDO.setProduct_brand(product_brand);
        }
        return nlpDO;
    }

    //2017-XX-XX, XXXX-09-XX, XXXX-09-23, XXXX-XX-23
    private String ToStrartDay(String date) {
        String[] times = date.split("-");
        Calendar c = Calendar.getInstance();

        if (times[0].equals("XXXX")) {
            times[0] = String.valueOf(c.get(Calendar.YEAR));
        }

        if (times[1].equals("XX") && !times[2].equals("XX")) {
            int month = c.get(Calendar.MONTH) + 1;
            times[1] = String.valueOf(month);
        } else if (!times[1].equals("XX") && times[2].equals("XX")) {
            times[2] = "01";
        } else if (times[1].equals("XX") && times[2].equals("XX")) {
            times[1] = "01";
            times[2] = "01";
        }

        return times[0] + "-" + times[1] + "-" + times[2];
    }

    private String ToEndDay(String date) {
        String[] times = date.split("-");
        Calendar c = Calendar.getInstance();

        if (times[0].equals("XXXX")) {
            times[0] = String.valueOf(c.get(Calendar.YEAR));
        }

        if (times[1].equals("XX") && !times[2].equals("XX")) {
            int month = c.get(Calendar.MONTH) + 1;
            times[1] = String.valueOf(month);
        } else if (!times[1].equals("XX") && times[2].equals("XX")) {
            c.add(Calendar.MONTH, -1);
            times[2] = String.valueOf(c.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (times[1].equals("XX") && times[2].equals("XX")) {
            times[1] = "12";
            times[2] = "31";
        }

        return times[0] + "-" + times[1] + "-" + times[2];
    }

    public String timeXToDate(String date) {
        AppDebug.e("TVTao_NlpDo", "timeXToDate date : " + date);
        Map<String, Integer> we_map = new HashMap<>();
        we_map.put("MO", Calendar.MONDAY); // 周一到周日
        we_map.put("TU", Calendar.TUESDAY);
        we_map.put("WE", Calendar.WEDNESDAY);
        we_map.put("TH", Calendar.THURSDAY);
        we_map.put("FR", Calendar.FRIDAY);
        we_map.put("SA", Calendar.SATURDAY);
        we_map.put("SU", Calendar.SUNDAY);
        we_map.put("E", Calendar.SUNDAY); // 这个是周末，我就当作周日处理了
        // 上周3 9月13号
        //2017-WWE37
        Pattern pattern = Pattern.compile("(XXXX|\\d{4})-W([A-Z]{2})(\\d{1,3})");
        Matcher matcher = pattern.matcher(date);
        matcher.matches();
        // 如果不是这个pattern那么就不是星期
        String year = matcher.group(1);
        String week = matcher.group(2);
        String num = matcher.group(3);
        AppDebug.e("TVTao_NlpDo", "timeXToDate year : " + year + " ,week : " + week + " ,num : " + num);
        Calendar calendar = new GregorianCalendar();
        if (LocalUnderstand.isNumeric(year)) { // 如果指定了年份就设置年份，否则就是XXXX，那就默认当前年份
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
        }
        if (!we_map.containsKey(week)) {
            // 错误处理
            return "";
        }
        // 设置星期
        calendar.set(Calendar.DAY_OF_WEEK, we_map.get(week));
        if (LocalUnderstand.isNumeric(num)) { // 如果指定了星期就设置星期，否则就是XX，那就默认本周
            calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(num));
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(calendar.getTime());
    }
}
