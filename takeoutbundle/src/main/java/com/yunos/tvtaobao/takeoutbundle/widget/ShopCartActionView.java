package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.focus_impl.ConsumerB;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.takeoutbundle.R;


/**
 * Created by wuhaoteng on 2018/10/11.
 * Desc:外卖购物车底部按钮
 */

public class ShopCartActionView extends FrameLayout {
    private String mTips;
    private String mActionStr;
    private OnActionListener mOnActionListener;

    private FocusArea mContainerView;
    private LinearLayout mActionLayout;
    private TextView mActionTxt;
    private TextView mTipsTxt;
    private View mDevideLine;

    public ShopCartActionView(@NonNull Context context) {
        super(context);
        init(context);

    }

    public ShopCartActionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ShopCartActionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    /**
     * 设置数据源
     *
     * @param actionStr 行为描述文字（如去结算、去选择）
     * @param tips      提示词
     * @param listener  按键行为回调
     */
    public void setData(String actionStr, @Nullable String tips, OnActionListener listener) {
        mTips = tips;
        mActionStr = actionStr;
        mOnActionListener = listener;
        boolean nodeHasFocus = mContainerView.getNode().isNodeHasFocus();
        refreshUI(nodeHasFocus);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_shop_cart_action_button, this, true);
        mContainerView = (FocusArea) findViewById(R.id.layout_shop_cart_action);
        mActionLayout = (LinearLayout) findViewById(R.id.layout_action);
        mActionTxt = (TextView) findViewById(R.id.txt_action);
        mTipsTxt = (TextView) findViewById(R.id.txt_tips);
        mDevideLine = findViewById(R.id.view_devide_line);

        mContainerView.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                refreshUI(true);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                refreshUI(false);
                return true;
            }

            @Override
            public boolean onFocusClick() {
                if (mOnActionListener != null) {
                    mOnActionListener.onActionCallBack();
                }
                return true;
            }
        });
    }

    /**
     * @param focus
     */
    private void refreshUI(boolean focus) {
        if (mActionStr == null) {
            mContainerView.setVisibility(View.GONE);
        } else {
            mContainerView.setVisibility(View.VISIBLE);
        }
        mActionLayout.setVisibility(View.VISIBLE);
        mActionTxt.setText(mActionStr);
        if (mTips != null && !mTips.equals("")) {
            mTipsTxt.setVisibility(View.VISIBLE);
            mDevideLine.setVisibility(View.VISIBLE);
            mTipsTxt.setText(mTips);
        } else {
            mTipsTxt.setVisibility(View.GONE);
            mDevideLine.setVisibility(View.GONE);
        }
        if (focus) {
            mActionTxt.setTextColor(getResources().getColor(R.color.white));
            mDevideLine.setBackgroundColor(getResources().getColor(R.color.white));
            mTipsTxt.setTextColor(getResources().getColor(R.color.white));
            mActionLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_shop_car_action_button_focus));
        } else {
            mActionTxt.setTextColor(getResources().getColor(R.color.white_8396ae));
            mDevideLine.setBackgroundColor(getResources().getColor(R.color.white_808396ae));
            mTipsTxt.setTextColor(getResources().getColor(R.color.white_8396ae));
            mActionLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_shop_car_action_button));
        }
    }

    public FocusArea getmContainerView() {
        return mContainerView;
    }

    public interface OnActionListener {
        void onActionCallBack();
    }

}
