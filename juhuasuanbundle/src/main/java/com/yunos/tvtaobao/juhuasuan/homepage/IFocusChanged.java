package com.yunos.tvtaobao.juhuasuan.homepage;

import android.view.View;


public interface IFocusChanged {
    public void onFocusOut(int pageNum, int index);
    
    public void onFocusIn (View view, int pageNum);
}
