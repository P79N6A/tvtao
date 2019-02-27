package com.yunos.tvtaobao.detailbundle.listener;


import android.view.KeyEvent;

public interface InnerFocusListener {

    public boolean findFirstFocus(int keyCode); 
    public boolean findNextFocus(int keyCode, KeyEvent event);
    public void setNextFocusSelected();
    public void clearItemSelected(boolean fatherViewselected, boolean clearselectview);
    public void performItemSelect(boolean selected);
    
    
    public boolean onKeyUp(int keyCode, KeyEvent event);
    public boolean onKeyDown(int keyCode, KeyEvent event);

}
