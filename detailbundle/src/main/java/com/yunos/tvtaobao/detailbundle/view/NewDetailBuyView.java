package com.yunos.tvtaobao.detailbundle.view;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.flash.DateUtils;
import com.yunos.tvtaobao.detailbundle.type.DetailModleType;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dingbin on 2017/8/23.
 */

public class NewDetailBuyView implements View.OnFocusChangeListener, View.OnClickListener {
    private final int DISTANCE = 150;

    private RelativeLayout newDetailBuyButtonFather;

    public TextView btn_cart;

    // 去店铺
    public TextView btn_qrd;

    //立即购买
    public TextView tv_buy;

    //button之前分割线
    public LinearLayout ivLine;
    public LinearLayout ivLine2;
    //背景框
    public ImageView ivBackground;

    //倒计时的监听
    private NewTimerTextView.TimeDoneListener timeDoneListener;


    //淘抢购和聚划算的已开团的购买按钮
    public RelativeLayout layoutBuyAtOnce;
    public NewTimerTextView tvTimeCurrentLeft;
    public TextView tvLeft;
    public TextView tvBuyAtOnce;

    public TextView tvPaymentTime;

    private final String TAG = "NewDetailBuyView";

    // 模板类型
    private DetailModleType mModleType;

    //状态类型
    private String mStatus;

    //是否显示加入购物车
    private int addcarviewStatus;
    //是否显示加入
    private int scanqrviewStatus;
    //运营双十一购物车标签
    private ImageView ivTopButton;
    // 下载管理器
    private ImageLoaderManager mImageLoaderManager;

    private WeakReference<NewDetailActivity> mBaseActivityRef;
    private String ivTopButtonAddress;

//    public ScrollView newDetailFulldescScrollView;

    public NewDetailBuyView(WeakReference<NewDetailActivity> mBaseActivityRef) {
        this.mBaseActivityRef = mBaseActivityRef;
        onInitView();
    }

    private void onInitView() {
        if (mBaseActivityRef != null && mBaseActivityRef.get() != null) {
            NewDetailActivity newDetailActivity = mBaseActivityRef.get();
//            newDetailFulldescScrollView = (ScrollView) newDetailActivity.findViewById(R.id.new_detail_fulldesc);
            newDetailBuyButtonFather = (RelativeLayout) newDetailActivity.findViewById(R.id.new_detail_buy_layout);
            ivTopButton = (ImageView) newDetailActivity.findViewById(R.id.iv_top_button);
            tv_buy = (TextView) newDetailActivity.findViewById(R.id.new_detail_buy_button);
            if(tv_buy!=null){
                tv_buy.requestFocus();
            }
            btn_cart = (TextView) newDetailActivity.findViewById(R.id.new_detail_add_cart);
            btn_qrd = (TextView) newDetailActivity.findViewById(R.id.new_detail_scan_qrcode);
            btn_qrd.setNextFocusRightId(R.id.fiv_pierce_evaluation_focusd);
            ivLine = (LinearLayout) newDetailActivity.findViewById(R.id.iv_line);
            ivLine2 = (LinearLayout) newDetailActivity.findViewById(R.id.iv_line2);

            layoutBuyAtOnce = (RelativeLayout) newDetailActivity.findViewById(R.id.layout_buy_at_once);
            tvTimeCurrentLeft = (NewTimerTextView) newDetailActivity.findViewById(R.id.tv_time_current_left);
            tvLeft = (TextView) newDetailActivity.findViewById(R.id.tv_left);
            tvBuyAtOnce = (TextView) newDetailActivity.findViewById(R.id.tv_buy_text_at_once);
            tvBuyAtOnce.setSelected(true);
            tvPaymentTime = (TextView) newDetailActivity.findViewById(R.id.tv_payment_time);

            ivBackground = (ImageView) newDetailActivity.findViewById(R.id.iv_background);
            mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(newDetailActivity);

            checkoutDetailModleType();
            setItemFocusChangeListener();
            tv_buy.setOnClickListener(this);
            btn_cart.setOnClickListener(this);
            btn_qrd.setOnClickListener(this);
        }
    }


    public void onDestroy(){
        try {
            tvTimeCurrentLeft.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIvTopButtonAddress(String ivTopButtonAddress) {
        this.ivTopButtonAddress = ivTopButtonAddress;
        if (!TextUtils.isEmpty(ivTopButtonAddress) && ivTopButton != null) {

            mImageLoaderManager.displayImage(ivTopButtonAddress, ivTopButton);
            ivTopButton.setVisibility(View.VISIBLE);

        } else {
            ivTopButton.setVisibility(View.GONE);

        }

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (view.getId() == R.id.new_detail_add_cart) {
            if (btn_cart.getVisibility() == View.VISIBLE) {
                if (hasFocus) {
                    btn_cart.setText("加入\n购物车");
                    if (getModleType() == DetailModleType.JUHUASUAN) {
                        btn_cart.setBackgroundResource(R.drawable.button_add_cart_jhs_focused);
                    } else {
                        btn_cart.setBackgroundResource(R.drawable.button_add_cart_focused);
                    }
                    ivLine.setVisibility(View.INVISIBLE);


                    ivBackground.setBackgroundResource(R.drawable.button_focused_bg);
                } else {
                    btn_cart.setText("");
                    btn_cart.setBackgroundResource(R.drawable.button_add_cart_unfocused);
                    ivLine.setVisibility(View.VISIBLE);
                    ivBackground.setBackgroundResource(R.drawable.detailbundle_button_unfocused_bg);
                }
            }
        } else if (view.getId() == R.id.new_detail_buy_button) {
            if (hasFocus) {
                if (addcarviewStatus == View.GONE && scanqrviewStatus == View.GONE) {
                    tv_buy.setBackgroundResource(R.drawable.button_buy_go_shop_bg);
                } else if (getModleType() == DetailModleType.JUHUASUAN) {
                    if (addcarviewStatus == View.GONE) {
                        tv_buy.setBackgroundResource(R.drawable.button_buy_jhs_no_car_focused);

                    } else {
                        tv_buy.setBackgroundResource(R.drawable.button_buy_jhs_focused);
                    }

                } else if (addcarviewStatus == View.GONE) {

                    tv_buy.setBackgroundResource(R.drawable.button_buy_presale_bg);
                } else {
                    tv_buy.setBackgroundResource(R.drawable.button_buy_focused);

                }
                if (mBaseActivityRef != null && mBaseActivityRef.get() != null) {
                    NewDetailActivity newDetailActivity = mBaseActivityRef.get();
                    tv_buy.setTextColor(newDetailActivity.getResources().getColor(R.color.bs_up_update_white));
                    tvTimeCurrentLeft.setTextColor(newDetailActivity.getResources().getColor(R.color.bs_up_update_white));
                    tvLeft.setTextColor(newDetailActivity.getResources().getColor(R.color.bs_up_update_white));
                    tvBuyAtOnce.setTextColor(newDetailActivity.getResources().getColor(R.color.bs_up_update_white));
                }

                ivLine.setVisibility(View.INVISIBLE);

                ivLine2.setVisibility(View.INVISIBLE);

                ivBackground.setBackgroundResource(R.drawable.button_focused_bg);

            } else {
                tv_buy.setBackgroundResource(0);
                if (btn_cart.getVisibility() == View.VISIBLE) {
                    ivLine.setVisibility(View.VISIBLE);
                }

                ivLine2.setVisibility(View.VISIBLE);

                ivBackground.setBackgroundResource(R.drawable.detailbundle_button_unfocused_bg);
                if (mBaseActivityRef != null && mBaseActivityRef.get() != null) {
                    NewDetailActivity newDetailActivity = mBaseActivityRef.get();
                    tv_buy.setTextColor(newDetailActivity.getResources().getColor(R.color.new_detail_line));
                    tvTimeCurrentLeft.setTextColor(newDetailActivity.getResources().getColor(R.color.new_detail_line));
                    tvLeft.setTextColor(newDetailActivity.getResources().getColor(R.color.new_detail_line));
                    tvBuyAtOnce.setTextColor(newDetailActivity.getResources().getColor(R.color.new_detail_line));
                }
            }
        } else if (view.getId() == R.id.new_detail_scan_qrcode) {
            if (btn_qrd.getVisibility() == View.VISIBLE) {
                if (hasFocus) {
                    btn_qrd.setText("去店铺");
                    if (getModleType() == DetailModleType.JUHUASUAN) {
                        btn_qrd.setBackgroundResource(R.drawable.button_go_shop_jhs_focesed);
                    } else {
                        btn_qrd.setBackgroundResource(R.drawable.button_go_shop_focused);
                    }
                        ivLine2.setVisibility(View.INVISIBLE);

                    ivBackground.setBackgroundResource(R.drawable.button_focused_bg);


                } else {
                    btn_qrd.setText("");
                    btn_qrd.setBackgroundResource(R.drawable.button_go_shop_unfocused);
                    ivLine2.setVisibility(View.VISIBLE);
                    ivBackground.setBackgroundResource(R.drawable.detailbundle_button_unfocused_bg);

                }
            }
        }
    }

    public void setItemFocusChangeListener() {
        btn_cart.setOnFocusChangeListener(this);
        tv_buy.setOnFocusChangeListener(this);
        btn_qrd.setOnFocusChangeListener(this);
    }


    /**
     * 检查当前详情界面是哪种类型
     */
    private void checkoutDetailModleType() {
        if (getModleType() == DetailModleType.QIANGOU) {

            if (btn_cart != null) {
                btn_cart.clearAnimation();
                btn_cart.setVisibility(View.GONE);
                ivTopButton.setVisibility(View.GONE);

            }
            if (btn_qrd != null) {
                btn_qrd.clearAnimation();
                btn_qrd.setVisibility(View.GONE);
            }
        }
    }


    public void setModleType(DetailModleType detailModleType) {
        mModleType = detailModleType;
        if (addcarviewStatus == View.GONE) {
            tv_buy.setBackgroundResource(R.drawable.button_buy_presale_bg);
        } else if (addcarviewStatus == View.GONE && scanqrviewStatus == View.GONE) {
            tv_buy.setBackgroundResource(R.drawable.button_buy_go_shop_bg);
        } else {
            if (mModleType == DetailModleType.JUHUASUAN) {
                tv_buy.setBackgroundResource(R.drawable.button_buy_jhs_focused);
            } else {
                tv_buy.setBackgroundResource(R.drawable.button_buy_focused);
            }

        }

    }

    /**
     * 获取界面展示的类型
     *
     * @return
     */
    public DetailModleType getModleType() {
        return mModleType;
    }

    public void setGoodsBuyButtonText(DetailModleType detailModleType, String status, String time, String buymessage, boolean buySupport) {
//        buymessage = "此商品不支持在当前地区销售";
//        detailModleType = DetailModleType.PRESALE;
//        status = "2";
        if (detailModleType == DetailModleType.QIANGOU || detailModleType == DetailModleType.JUHUASUAN) {
            //淘抢购未开团
            if (status.equals("0")) {
                if (time != null) {
                    String buytext = DateUtils.getStrTime(time);
                    String[] times = buytext.split("\\s+");
                    tv_buy.setText(times[0] + "\n" + times[1] + "开抢");
                }
                tv_buy.setTextSize(20);
            } else if (status.equals("1")) { //淘抢购已开团
                tv_buy.setText("");
                layoutBuyAtOnce.setVisibility(View.VISIBLE);
                //获得当前时间
                Date now = new Date();
                //获得时间差
                long diff = Long.valueOf(time) - now.getTime();
                //设置时间
                tvTimeCurrentLeft.setTimes(diff);
                //设置刷新监听
                tvTimeCurrentLeft.setTimeDoneListener(timeDoneListener);

                /**
                 * 开始倒计时
                 */
                if (!tvTimeCurrentLeft.isRun()) {
                    tvTimeCurrentLeft.start();
                }
            } else {
                tv_buy.setText(buymessage);
            }

        } else if (detailModleType == DetailModleType.PRESALE) {
            if (status.equals("2")) {
                tv_buy.setText("");
                layoutBuyAtOnce.setVisibility(View.VISIBLE);
                if (buymessage.equals("此商品不支持在当前地区销售")) {
                    tvBuyAtOnce.setText("不支持本地区下单");
                } else {
                    tvBuyAtOnce.setText(buymessage);
                }
                if (tvLeft != null) {
                    tvLeft.setText(time);
                }
                tvTimeCurrentLeft.setVisibility(View.GONE);
            } else {
                tv_buy.setText(buymessage);
            }
        } else {
            layoutBuyAtOnce.setVisibility(View.GONE);
            tv_buy.setText(buymessage);

            if (!buySupport && tv_buy != null) {
//                tv_buy.setTextColor(tv_buy.getResources().getColor(
//                        R.color.ytsdk_detail_not_buysupport_message_color));
                tv_buy.setTextColor(Color.WHITE);

            } else if (tv_buy != null) {
                tv_buy.setTextColor(Color.WHITE);
            }
        }

        if (tv_buy.getText().toString().equals("此商品不支持在当前地区销售")) {
            tv_buy.setText("不支持本地区下单");
        }

    }


    public void setTvPaymentTimeText(String paymentTime) {
        if (!TextUtils.isEmpty(paymentTime)) {
            tvPaymentTime.setVisibility(View.VISIBLE);
            tvPaymentTime.setBackgroundResource(R.drawable.tv_payment_time_bg);
            String reg = "(\\d{4}\\.\\d{2}\\.\\d{2} (\\d{2})\\:(\\d{2}))";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(paymentTime);
            while (matcher.find()) {
                String originalDate = matcher.group();
                AppDebug.e(TAG, "group : " + originalDate);//打印找到的日期
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                // 指定一个日期
                try {
                    Date date = dateFormat.parse(matcher.group());
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM.dd HH:mm");
                    String currentlDate = dateFormat2.format(date);
                    AppDebug.e(TAG, "date : " + currentlDate);//打印找到的日期
                    paymentTime = paymentTime.replace(originalDate, currentlDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            tvPaymentTime.setText(paymentTime);
        } else {
            tvPaymentTime.setVisibility(View.GONE);
        }

    }


    //设置时间倒计时结束的监听
    public void setTimeDoneListener(NewTimerTextView.TimeDoneListener timeDoneListener) {
        this.timeDoneListener = timeDoneListener;
    }

    /**
     * P
     * 设置提醒按钮的文案
     *
     * @param content
     */
    public void setRemindContent(String content) {
        if (!TextUtils.isEmpty(content) && tv_buy != null) {
            tv_buy.setText(content);
        }
    }

    /**
     * 设置购物车和扫描按钮的显示状态
     *
     * @param addcarview
     * @param scanqrview
     */
    public void setButtonVisibilityState(int addcarview, int scanqrview) {
        addcarviewStatus = addcarview;
        scanqrviewStatus = scanqrview;
        if (btn_cart != null) {
            btn_cart.clearAnimation();
            btn_cart.setVisibility(addcarview);
            if (!TextUtils.isEmpty(ivTopButtonAddress)) {
                ivTopButton.setVisibility(addcarview);
            }
//            ivLine.setVisibility(addcarview);
            if (addcarview == View.GONE && tv_buy != null) {
                if (getModleType() == DetailModleType.JUHUASUAN) {
                    tv_buy.setBackgroundResource(R.drawable.button_buy_jhs_no_car_focused);
                } else {
                    tv_buy.setBackgroundResource(R.drawable.button_buy_presale_bg);
                }
                if (tv_buy.getText().length() >= 8) {
                    tv_buy.setPadding(20, 0, 0, 0);
                }
            }
        }

        if (btn_qrd != null) {
            btn_qrd.clearAnimation();
            btn_qrd.setVisibility(scanqrview);
//            ivLine2.setVisibility(scanqrview);
        }

        if (addcarview == View.GONE && scanqrview == View.GONE) {
            tv_buy.setBackgroundResource(R.drawable.button_buy_go_shop_bg);
        }
        tv_buy.requestFocus();
        newDetailBuyButtonFather.setVisibility(View.VISIBLE);

    }

    public void setBuyButtonVisibilityState(int buyview) {
        if (tv_buy != null) {
            tv_buy.setVisibility(buyview);
        }
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

//
//    /**
//     * 设置购买按钮的监听
//     *
//     * @param l
//     */
//    public void setBuyButtonListener(View.OnClickListener l) {
//        if (tv_buy != null && l != null) {
//            tv_buy.setOnClickListener(l);
//        }
//
//    }
//
//    /**
//     * 设置添加购物车按钮
//     *
//     * @param l
//     */
//    public void setAddCartButtonListener(View.OnClickListener l) {
//        if (btn_cart != null && l != null) {
//            btn_cart.setOnClickListener(l);
//        }
//    }
//
//    /**
//     * 设置去店铺的按钮监听
//     *
//     * @param l
//     */
//    public void setScanQrCodetButtonListener(View.OnClickListener l) {
//        if (btn_qrd != null && l != null) {
//            btn_qrd.setOnClickListener(l);
//        }
//    }

    @Override
    public void onClick(View view) {
        NewDetailActivity newDetailActivity = mBaseActivityRef.get();
        if (view.getId() == R.id.new_detail_buy_button) {
            newDetailActivity.setBuyButton();
        } else if (view.getId() == R.id.new_detail_add_cart) {
            newDetailActivity.setAddCartButton();
        } else if (view.getId() == R.id.new_detail_scan_qrcode) {
            newDetailActivity.setScanQrCodetButton();
        }
    }
}
