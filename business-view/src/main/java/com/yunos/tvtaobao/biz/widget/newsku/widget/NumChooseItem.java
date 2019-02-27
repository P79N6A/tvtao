package com.yunos.tvtaobao.biz.widget.newsku.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.biz.widget.newsku.SkuActivity;

import java.text.Format;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/8
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class NumChooseItem extends LinearLayout {
    private ImageView leftView, rightView = null;
    private TextView textView;
    private int num = 1;
    private int maxNum = 1;
    private int type = 0;
    private Context context;
    private String tradeType = TradeType.ADD_CART;
    private Bitmap leftInvaildUnfocus, leftVaildUnfocus, leftInvaildFocused, leftVaildFocused,
            rightInvaildUnfocus, rightVaildUnfocus, rightInvaildFocused, rightVaildFocused;

    public NumChooseItem(Context context) {
        super(context);
        initView(context);
    }

    public NumChooseItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NumChooseItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    private void initBitmap() {
        leftInvaildUnfocus = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_left_invaild_unfocus);
        leftVaildUnfocus = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_left_vaild_unfocus);
        leftInvaildFocused = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_left_invaild_focused);
        leftVaildFocused = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_left_vaild_focused);
        rightInvaildUnfocus = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_right_invaild_unfocus);
        rightVaildUnfocus = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_right_vaild_unfocus);
        rightInvaildFocused = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_right_invaild_focused);
        rightVaildFocused = BitmapFactory.decodeResource(getResources(), R.drawable.iv_numchoose_right_vaild_focused);
    }

    private void initView(Context context) {
        this.context = context;
        initBitmap();
        setFocusable(true);
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundResource(R.drawable.bg_sku_item_unfocused_enable);
        View view = LayoutInflater.from(context).inflate(R.layout.item_numchoose, null);
        leftView = (ImageView) view.findViewById(R.id.item_num_choose_left);
        rightView = (ImageView) view.findViewById(R.id.item_num_choose_right);
        textView = (TextView) view.findViewById(R.id.item_num_choose_num);
        textView.setTextColor(Color.parseColor("#202020"));

        addView(view);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        updateArrow(gainFocus);
        if (gainFocus) {
            setBackgroundResource(R.drawable.bg_sku_item_focused_color);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            setBackgroundResource(R.drawable.bg_sku_item_unfocused_enable);
            textView.setTextColor(Color.parseColor("#202020"));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isFocused()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (num > 1) {
                        num--;
                        updateNum(num);
                    } else {
                        if(tradeType.equals(TradeType.TAKE_OUT_ADD_CART)){
                            showError(context.getString(R.string.take_out_sku_num_exceed_minimum));
                        }else {
                            showError(context.getString(R.string.new_shop_sku_num_exceed_minimum));
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (checkNum()) {
                        num++;
                        updateNum(num);
                    } else {
                        if(tradeType.equals(TradeType.TAKE_OUT_ADD_CART)){
                            switch (type) {
                                case 0:
                                    showError(context.getString(R.string.take_out_sku_num_exceed_kucun));
                                    break;
                                case 1:
                                    showError(String.format(context.getString(R.string.take_out_sku_num_exceed_limit),String.valueOf(maxNum)));
                                    break;
                            }
                        }else {
                            switch (type) {
                                case 0:
                                    showError(context.getString(R.string.new_shop_sku_num_exceed_kucun));
                                    break;
                                case 1:
                                    showError(context.getString(R.string.new_shop_sku_num_exceed_limit));
                                    break;
                            }
                        }

                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showError(String prompt) {
        if (context instanceof SkuActivity) {
            ((SkuActivity) context).onShowError(prompt);
        }
    }

    /**
     * 设置可购买最大数量
     *
     * @param type   0 库存
     *               1 限购
     * @param maxNum 最大数量
     */
    public void setMaxNum(int type, int maxNum) {
        this.type = type;
        this.maxNum = maxNum;
        updateArrow(isFocused());
    }

    /**
     * 检查是否还可以添加
     *
     * @return
     */
    public boolean checkNum() {
        //TODO
        if (num >= maxNum) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 更新修改左右箭头状态
     *
     * @param gainFocus
     */
    private void updateArrow(boolean gainFocus) {
        if (gainFocus) {
            if (maxNum == 0) { //商品库存为0的情况下
                leftView.setImageBitmap(leftInvaildFocused);
                rightView.setImageBitmap(rightInvaildFocused);
            } else if (num == 1) {
                leftView.setImageBitmap(leftInvaildFocused);
                rightView.setImageBitmap(rightVaildFocused);
            } else if (!checkNum()) {
                leftView.setImageBitmap(leftVaildFocused);
                rightView.setImageBitmap(rightInvaildFocused);
            } else {
                leftView.setImageBitmap(leftVaildFocused);
                rightView.setImageBitmap(rightVaildFocused);
            }
        } else {
            if (maxNum == 0) {
                leftView.setImageBitmap(leftInvaildUnfocus);
                rightView.setImageBitmap(rightInvaildUnfocus);
            } else if (num == 1) {
                leftView.setImageBitmap(leftInvaildUnfocus);
                rightView.setImageBitmap(rightVaildUnfocus);
            } else if (!checkNum()) {
                leftView.setImageBitmap(leftVaildUnfocus);
                rightView.setImageBitmap(rightInvaildUnfocus);
            } else {
                leftView.setImageBitmap(leftVaildUnfocus);
                rightView.setImageBitmap(rightVaildUnfocus);
            }
        }
    }

    private void updateNum(int num) {
        this.num = num;
        textView.setText(String.valueOf(num));
        updateArrow(true);
    }

    public void setBuyCount(int num) {
        this.num = num;
        textView.setText(String.valueOf(num));
        updateArrow(isFocused());
    }

    public int getNum() {
        if (maxNum == 0) {
            return maxNum;
        }

        return num;
    }
}
