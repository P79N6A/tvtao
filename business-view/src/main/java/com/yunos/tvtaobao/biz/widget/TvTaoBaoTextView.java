package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;


public class TvTaoBaoTextView extends TextView {

    public TvTaoBaoTextView(Context context) {
        super(context); 
    }

    public TvTaoBaoTextView(Context context, AttributeSet attrs) {
        super(context, attrs); 
    }

    public TvTaoBaoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); 
    }
     
    @Override
    protected void onDraw(Canvas canvas) { 
        CharSequence text = getText();
        setText(null); 
        super.onDraw(canvas);    
        TextPaint paint =  getPaint();
        FontMetrics  ft = paint.getFontMetrics();
        int view_h = getHeight();
        float descent_y = ft.descent;
        
        
        canvas.drawText("2345", 0, getHeight(), paint);
    }  
    
    
    public void setDrawText(String text){
        
    }

}
