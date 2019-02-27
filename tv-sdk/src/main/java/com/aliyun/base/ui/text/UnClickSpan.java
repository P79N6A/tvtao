package com.aliyun.base.ui.text;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

public class UnClickSpan extends CharacterStyle implements UpdateAppearance {
	@Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor); // 蓝色
        ds.setUnderlineText(false); // 没有下划线
    }
}
