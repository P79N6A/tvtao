package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VerticalTextView extends LinearLayout {

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.context = context;
    }

    private String text;
    private Context context;
    private int     color;
    private int     size;

    public VerticalTextView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        this.context = context;
    }

    public void setText(String text) {
        this.text = text;
        addText();
    }

    private void addText() {
        removeAllViews();
        if (text != null) {
            char[] chara = text.toCharArray();
            for (int i = 0; i < chara.length; i++) {
                TextView oneText = new TextView(context);
                oneText.setTextColor(color);

                oneText.setText(text.substring(i, i + 1));
                if (size > 0) {
                    oneText.setTextSize(1, size);
                }
                addView(oneText);
            }
        }

    }

    public void setTextColor(int color) {
        this.color = color;
    }

    public void setTextSize(int size) {
        this.size = size;
    }

}
