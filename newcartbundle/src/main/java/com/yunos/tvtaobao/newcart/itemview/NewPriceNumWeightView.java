package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yunos.tvtaobao.newcart.R;



/**
 * Created by linmu on 2018/6/11.
 */

public class NewPriceNumWeightView extends RelativeLayout {
    private TextView tvPrice;
    private TextView tvNum;
    private TextView tvWeight;
    private View lineNum;
    private View lineWeight;

    public NewPriceNumWeightView(Context context) {
        super(context);
    }

    public NewPriceNumWeightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(getContext(), R.layout.layout_new_shop_cart_item_price, this);
        tvPrice = (TextView) view.findViewById(R.id.tv_price);
        tvNum = (TextView) view.findViewById(R.id.tv_num);
        tvWeight = (TextView) view.findViewById(R.id.tv_weight);
        lineNum = view.findViewById(R.id.line_num);
        lineWeight = view.findViewById(R.id.line_weight);
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.layout_new_shop_cart_item_price, this);
        tvPrice = (TextView) view.findViewById(R.id.tv_price);
        lineNum = view.findViewById(R.id.line_num);
        lineWeight = view.findViewById(R.id.line_weight);
    }

    public NewPriceNumWeightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTvPrice(String price) {
        if (!TextUtils.isEmpty(price)) {
            price = price.replaceAll("￥","¥ ");
            price = price.replaceAll(",","");
            if(price.indexOf(".") > 0){
                //正则表达
                price = price.replaceAll("0+?$", "");//去掉后面无用的零
                price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            }
            tvPrice.setText(price);
            tvPrice.setVisibility(VISIBLE);
        }
    }

    public void setTvNum(String num) {
        if (!TextUtils.isEmpty(num)) {
            tvNum.setText(num);
            tvNum.setVisibility(VISIBLE);
            lineNum.setVisibility(VISIBLE);
        }
    }

    public void setTvWeight(String weight) {
        if (!TextUtils.isEmpty(weight)) {
            tvWeight.setText(weight);
            tvWeight.setVisibility(VISIBLE);
            lineWeight.setVisibility(VISIBLE);
            lineNum.setVisibility(GONE);
        } else {
            tvWeight.setVisibility(GONE);
            lineWeight.setVisibility(GONE);
        }
    }

    public void setTextColor(boolean isPre){
        if(isPre){
            tvPrice.setTextColor(getResources().getColor(R.color.new_shop_cart_ispre));
            lineNum.setBackgroundColor(getResources().getColor(R.color.new_shop_cart_ispre));
            lineWeight.setBackgroundColor(getResources().getColor(R.color.new_shop_cart_ispre));
        }else {
            tvPrice.setTextColor(getResources().getColor(R.color.new_shop_cart_txt_price));
            lineNum.setBackgroundColor(getResources().getColor(R.color.new_shop_cart_txt_price_line));
            lineWeight.setBackgroundColor(getResources().getColor(R.color.new_shop_cart_txt_price_line));
        }


    }




}
