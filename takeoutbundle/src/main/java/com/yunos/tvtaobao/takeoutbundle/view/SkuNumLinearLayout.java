package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;

import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.takeoutbundle.R;

import java.util.Map;


/**
 * Created by chenjiajuan on 17/12/20.
 *
 * @describe 数量加减
 */

public class SkuNumLinearLayout extends LinearLayout {
    private static final String TAG="SkuNumLinearLayout";
    private ImageView ivNumberSub, ivNumberAdd;
    private TextView tvNumber;
    private int quantity=1;
    private int count=1;
    private String itemId=null;
    private String pageName=null;
    public SkuNumLinearLayout(Context context) {
        super(context);
    }
    public SkuNumLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_sku_number,this);
        this.setOrientation(HORIZONTAL);
        ivNumberAdd = (ImageView) findViewById(R.id.iv_number_add);
        ivNumberSub = (ImageView) findViewById(R.id.iv_number_sub);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        ivNumberSub.setEnabled(false);
        ivNumberSub.setFocusable(false);
        ivNumberAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG,"ivNumberAdd.onClick .count = "+count+",quantity = "+quantity);
                utSkuNumberAdd();
                if (count<quantity){
                    count++;
                    tvNumber.setText(count+"");
                }else {
                    ivNumberAdd.setEnabled(false);
                    ivNumberAdd.setFocusable(false);
                    ivNumberSub.setEnabled(true);
                    ivNumberSub.setFocusable(true);
                    ivNumberSub.requestFocus();
                }
                if (count>1){
                    ivNumberSub.setEnabled(true);
                    ivNumberSub.setFocusable(true);
                }else {
                    ivNumberSub.setFocusable(false);
                    ivNumberSub.setEnabled(false);
                }
            }
        });
        ivNumberSub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG,"ivNumberSub.onClick .count = "+count+",quantity = "+quantity);
                utSkuNumberSub();
                if (count>1){
                    count--;
                    AppDebug.e(TAG,"count = "+count);
                    tvNumber.setText(count+"");
                    if (count==1){
                        ivNumberSub.clearFocus();
                        ivNumberSub.setFocusable(false);
                        ivNumberSub.setEnabled(false);
                        ivNumberAdd.setEnabled(true);
                        ivNumberAdd.setFocusable(true);
                        ivNumberAdd.requestFocus();
                    }
                }else {
                    ivNumberSub.clearFocus();
                    ivNumberSub.setFocusable(false);
                    ivNumberSub.setEnabled(false);
                    ivNumberAdd.requestFocus();
                }
            }
        });

    }

    /**
     * 数量减少点击事件
     */
    private void utSkuNumberSub() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("item id",itemId);

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_NUM_DES);
        Utils.utControlHit(pageName,"Page_waimai_shop_grant_sku_button_increase",properties);
    }

    /**
     * 数量增加点击事件
     */
    private void utSkuNumberAdd() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("item id",itemId);

        properties.put("uuid",CloudUUIDWrapper.getCloudUUID());

        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_NUM_INC);

        Utils.utControlHit(pageName,"Page_waimai_shop_grant_sku_button_decrease",properties);
    }

    public SkuNumLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * 设置最大数量
     * @param quantity
     */
    public void setQuantity(int quantity){
        AppDebug.e(TAG,"setQuantity = "+quantity);
        this.quantity=quantity;
    }

    public  void setPageNameAddItemId(String pageName,String itemId){
        this.pageName=pageName;
        this.itemId=itemId;

    }

    /**
     * 判断焦点是否在数量层
     * @return
     */
    public boolean hasSkuNumLinearFocus(){
        if (ivNumberAdd.hasFocus()||ivNumberSub.hasFocus()){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 增加按钮获取焦点
     */
    public void requestSkuNumFocus(){
        ivNumberAdd.requestFocus();

    }


    /**
     * 初始化count
     * @param count
     */
    public void setCount(String count){
        if (tvNumber!=null){
            tvNumber.setText(count);
            this.count=1;
            ivNumberSub.setEnabled(false);
            ivNumberSub.setFocusable(false);
        }

    }

    public int getCount(){
      return count;
    }


}
