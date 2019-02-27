package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * Created by chenjiajuan on 18/1/8.
 *
 * @describe
 */

public class SkuTitleLinearLayout extends LinearLayout {
    private String TAG="SkuTitleLinearLayout";
    private Context context;
    private int maxLines=0;
    private int children=0;
    private View textName;
    private View textPrice;
    private int  marginLeft= 17;
    public SkuTitleLinearLayout(Context context) {
        super(context);
        this.context=context;
    }

    public SkuTitleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkuTitleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        AppDebug.e(TAG,"onLayout..........");
        int maxLines=0;
        int children=0;
        if (context!=null){
            marginLeft= (int) getResources().getDimension(R.dimen.dp_17);
            float dm= DeviceUtil.getDensityFromDevice(context);
            AppDebug.e(TAG,"dm = "+dm);
            marginLeft= (int) (dm*marginLeft);
        }else {
            marginLeft= (int) (marginLeft*1.5);
        }
        maxLines=(getWidth()-marginLeft);
        for (int i=0;i<getChildCount();i++){
            View view=getChildAt(i);
            children+=view.getWidth();
            AppDebug.e(TAG,"i = "+i+" ,view.getWidth = "+view.getWidth());
            if (i==0){
                textName=view;
            }else {
                textPrice=view;
            }
        }
        AppDebug.e(TAG,"children = "+children+" ,maxLines = "+maxLines);
        if (children>maxLines){
            AppDebug.e(TAG,"children>maxLines");
            int width=maxLines-textPrice.getWidth();
            android.widget.LinearLayout.LayoutParams textNameParams=new android.widget.LinearLayout.LayoutParams(
                    width, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textName.setLayoutParams(textNameParams);
            android.widget.LinearLayout.LayoutParams textPriceParams=new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.
                    LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textPriceParams.leftMargin=marginLeft;
            textPrice.setLayoutParams(textPriceParams);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
