package com.yunos.tvtaobao.biz.widget.newsku.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.businessview.R;
/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/11
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class NumChooseLayout extends LinearLayout {

    private TextView name, manytimes, canBuyQuantity;
    private NumChooseItem numChooseItem;
    //多倍购买，times 倍数。
    private int times = 1;
    private int kucuns = 0;
    private int limit = 0;
    private String tradeType = TradeType.ADD_CART;

    public NumChooseLayout(Context context) {
        super(context);
        initView(context);
    }

    public NumChooseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NumChooseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
        if(numChooseItem != null){
            numChooseItem.setTradeType(tradeType);
        }
    }

    private void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        name = new TextView(context);
        name.setTextSize(getResources().getDimension(R.dimen.sp_16));
        name.setTextColor(Color.parseColor("#202020"));
        name.setText("数量(左右键选择)");
        addView(name);
        View view = LayoutInflater.from(context).inflate(R.layout.item_numchoose_layout, null);
        numChooseItem = (NumChooseItem) view.findViewById(R.id.item_layout_numchooseitem);
        manytimes = (TextView) view.findViewById(R.id.item_layout_manytimes);
        canBuyQuantity = (TextView) view.findViewById(R.id.item_layout_canbuyquantity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, (int) getResources().getDimension(R.dimen.dp_6), 0, 0);
        view.setLayoutParams(lp);
        addView(view);
    }

    public NumChooseItem getNumChooseItem() {
        return numChooseItem;
    }

    /**
     * @return 获取当前购买的数量
     */
    public int getNum() {
        return numChooseItem.getNum() * times;
    }

    /**
     * 设置数量
     * 主要是从购物车过来，需要把数量传递过来。
     *
     * @param num
     */
    public void setBuyCount(long num) {
        if (num / times > 0) {
            numChooseItem.setBuyCount((int) (num / times));
        } else {
            numChooseItem.setBuyCount(1);
        }
    }

    /**
     * 显示多倍购买
     *
     * @param times 倍数
     */
    public void showUnitBuy(int times) {
        if (times > 1) {
            this.times = times;
            manytimes.setVisibility(VISIBLE);
            manytimes.setText("X " + times + "件");
            updateCanBuyQuantity();
        }
    }

    /**
     * 设置库存数量和限购的数量
     *
     * @param kucun
     */
    public void setKuCunNum(int kucun, int limit) {
        this.kucuns = kucun;
        this.limit = limit;
        updateCanBuyQuantity();
    }

    /**
     * 更新可购买数量文案
     */
    private void updateCanBuyQuantity() {
        //先初始化
        canBuyQuantity.setText("");
        if (limit > 0 && limit < kucuns) {
            canBuyQuantity.setText("( 限购" + limit + "件 )");
            numChooseItem.setMaxNum(1, limit / times);
        }

        if ((kucuns > 0 && kucuns < limit) || limit <= 0) {
            if(tradeType.equals(TradeType.TAKE_OUT_ADD_CART)){
                if(kucuns <= 10){
                    canBuyQuantity.setText("( 仅剩" + kucuns + "件 )");
                }
            }else {
                canBuyQuantity.setText("( 库存" + kucuns + "件 )");
            }
            numChooseItem.setMaxNum(0, kucuns / times);
        }
    }
}
