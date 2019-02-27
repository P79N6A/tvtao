package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemQuantity;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.entity.ShopCartGoodsBean;
import com.yunos.tvtaobao.newcart.util.RebateManager;


/**
 * Created by linmu on 2018/6/13.
 * 选种未选中按钮
 */

public class NewShopCartGoodsSelectView extends LinearLayout {
    private RelativeLayout rlSelect;
    private TextView tvSelect;
    private ImageView ivSelect;
    private ItemComponent itemComponent;
    private ShopCartGoodsBean shopCartGoodsBean;
    private View view;
//    private CustomDialog commonDialog;

    private boolean isSelected = false;

    public interface OperateClickListener {
        void onItemClick(NewShopCartGoodsSelectView view);
    }

    private OperateClickListener operateClickListener;

    public void setOperateClickListener(OperateClickListener operateClickListener) {
        this.operateClickListener = operateClickListener;
    }

    public NewShopCartGoodsSelectView(Context context) {
        super(context);
        initView(context);
    }

    public NewShopCartGoodsSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public NewShopCartGoodsSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        view = inflate(getContext(), R.layout.layout_new_shop_cart_item_select, this);

        rlSelect = (RelativeLayout) view.findViewById(R.id.rl_select);
        tvSelect = (TextView) view.findViewById(R.id.tv_select);
        ivSelect = (ImageView) view.findViewById(R.id.iv_select);
        setClick(context);
    }

    public void updateCheck(boolean isCheck) {
        if (isCheck) {
            ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
        } else {
            ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_unselect);
        }
    }


    public void setData(ShopCartGoodsBean shopCartGoodsBean) {
        this.shopCartGoodsBean = shopCartGoodsBean;
        ItemComponent itemComponent = shopCartGoodsBean.getCartGoodsComponent().getItemComponent();
        if(itemComponent == null){
            return;
        }
        this.itemComponent = itemComponent;
//        失效商品
        if (shopCartGoodsBean.isInvalid()) {

            Log.e("itemComponent title =", itemComponent.getTitle() + shopCartGoodsBean.isInvalid());
            tvSelect.setVisibility(VISIBLE);
            ivSelect.setVisibility(GONE);
            boolean skuInvalid = false;
            if(itemComponent.getSku()!=null) {
                 skuInvalid = itemComponent.getSku().isSkuInvalid();
            }
            if (skuInvalid) {
                tvSelect.setText("重选");
            } else {
                // TODO: 2018/7/3 找相似
                tvSelect.setText("找相似");
            }
        } else {
            String shopId = itemComponent.getShopId();
            String cartId = itemComponent.getCartId();
            RebateManager rebateManager = RebateManager.getInstance();
            //预售
            tvSelect.setVisibility(VISIBLE);
            ivSelect.setVisibility(GONE);
            if (!itemComponent.isValid()&&itemComponent.isPreBuyItem()) {
                tvSelect.setText("未开团");
            } else if (!itemComponent.isValid()&&itemComponent.isPreSell()) {
                tvSelect.setText("未开始");
            } else if (itemComponent.isChecked()) {
                ItemQuantity itemQuantity = itemComponent.getItemQuantity();
                if(itemComponent!=null && itemComponent.isValid()){
                    rebateManager.add(shopId,cartId,itemQuantity.getQuantity());
                }
                tvSelect.setVisibility(GONE);
                ivSelect.setVisibility(VISIBLE);
                ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_select);
            } else {
                rebateManager.remove(cartId);
                tvSelect.setVisibility(GONE);
                ivSelect.setVisibility(VISIBLE);
                ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_unselect);
            }
        }
        setState();

    }

    public void resetState(){
        isSelected = false;
        setState();
    }

    private void setState() {
        if (isSelected) {
            if (itemComponent != null && itemComponent.isChecked()) {
                ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
            } else {
                ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_unselect);

            }
            tvSelect.setTextColor(getResources().getColor(R.color.new_shop_cart_white));
            setBackgroundResource(R.drawable.new_shop_cart_button_select_focuse_bg);
        } else {//失去焦点时文字和颜色状态
            if (itemComponent != null && itemComponent.isChecked()) {
                ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_select);
            } else {
                ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_unselect);
            }
            tvSelect.setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
            setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void setClick(final Context context) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operateClickListener != null)
                    operateClickListener.onItemClick(NewShopCartGoodsSelectView.this);
                //
                Log.e("selectView isChecked = ", itemComponent.isChecked() + "");
                if (itemComponent.isChecked()) {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
                } else {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_unselect);
                }
            }
        });

        rlSelect.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (itemComponent != null && itemComponent.isChecked()) {
                        ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
                    } else {
                        ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_unselect);

                    }
                    tvSelect.setTextColor(getResources().getColor(R.color.new_shop_cart_white));
                } else {//失去焦点时文字和颜色状态
                    if (itemComponent != null && itemComponent.isChecked()) {
                        ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_select);
                    } else {
                        ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_unselect);
                    }
                    tvSelect.setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
                }
            }
        });

    }

    public void setItemSelected(boolean selected) {
        isSelected = selected;
        setState();
    }

}
