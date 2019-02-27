package com.yunos.tvtaobao.newcart.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.newcart.R;


/**
 * 购物车列表商品控制按钮布局里面增加了上下两条分割线
 */
public class ShopCartItemControllerLayout extends LinearLayout {
    private Paint mLinePaint;
    public ShopCartItemControllerLayout(Context context) {
        super(context);
        init();
    }
    
    public ShopCartItemControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ShopCartItemControllerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 上下两条分割线
        canvas.drawLine(0, 0.5f, getWidth(), 0.5f, mLinePaint);
        canvas.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1, mLinePaint);
    }
    
    private void init(){
        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.newcart_controller_line));
    }
}
